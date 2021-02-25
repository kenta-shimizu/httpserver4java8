package com.shimizukenta.httpserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractHttpApiServer extends AbstractHttpServer implements HttpApiServer {
	
	private final AbstractHttpApiServerConfig config;
	
	public AbstractHttpApiServer(AbstractHttpApiServerConfig config) {
		super(config);
		this.config = config;
	}
	
	private static final byte CR = (byte)0xD;
	private static final byte LF = (byte)0xA;
	
	@Override
	public void connectionWorker(AsynchronousSocketChannel channel) throws InterruptedException, IOException {
		
		final SocketAddress client = channel.getRemoteAddress();
		final SocketAddress server = channel.getLocalAddress();
		
		final BlockingQueue<Byte> recvByteQueue = new LinkedBlockingQueue<>();
		final BlockingQueue<HttpRequestMessage> reqMsgQueue = new LinkedBlockingQueue<>();
		
		final Collection<Callable<Void>> tasks = Arrays.asList(
				() -> {
					
					try {
						final ByteBuffer buffer = ByteBuffer.allocate(1024);
						
						for ( ;; ) {
							
							((Buffer)buffer).clear();
							
							final Future<Integer> f = channel.read(buffer);
							
							try {
								int r = f.get().intValue();
								
								if ( r < 0 ) {
									break;
								}
								
								((Buffer)buffer).flip();
								while( buffer.hasRemaining() ) {
									recvByteQueue.put(buffer.get());
								}
							}
							catch ( InterruptedException e ) {
								f.cancel(true);
								throw e;
							}
						}
					}
					catch ( InterruptedException ignore ) {
					}
					catch ( ExecutionException e ) {
						putLog(e.getCause());
					}
					
					return null;
				},
				() -> {
					
					try (
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							) {
						
						for ( ;; ) {
							
							baos.reset();
							
							{
								Byte b = recvByteQueue.poll(config.keepAliveTimeout(), TimeUnit.SECONDS);
								
								if ( b == null ) {
									break;
								}
								
								baos.write(b.byteValue());
							}
							
							for( ;; ) {
								
								byte b = recvByteQueue.take().byteValue();
								
								if ( b == LF ) {
									
									break;
									
								} else if ( b != CR ) {
									
									baos.write(b);
								}
							}
							
							final HttpRequestLineParser requestLine = HttpRequestLineParser.fromLine(
									new String(baos.toByteArray(), StandardCharsets.US_ASCII));
							
							baos.reset();
							
							final List<String> headerLines = new ArrayList<>();
							
							for ( ;; ) {
								
								for ( ;; ) {
									
									byte b = recvByteQueue.take().byteValue();
									
									if ( b == LF ) {
										
										break;
										
									} else if ( b != CR ) {
										
										baos.write(b);
									}
								}
								
								byte[] bs = baos.toByteArray();
								baos.reset();
								
								if ( bs.length == 0 ) {
									break;
								}
								
								headerLines.add(new String(bs, StandardCharsets.US_ASCII));
							}
							
							final HttpHeaderListParser headersParser = HttpHeaderListParser.fromLines(headerLines);
							
							reqMsgQueue.put(createHttpRequestMessage(
									requestLine,
									headersParser,
									recvByteQueue));
						}
					}
					catch ( InterruptedException ignore ) {
					}
					catch ( HttpServerException | IOException e ) {
						putLog(e);
					}
					
					return null;
				},
				() -> {
					
					try {
						final HttpConnectionValue connectionValue = new HttpConnectionValue(
								config.keepAliveTimeout(),
								config.keepAliveMax());
						
						for ( ;; ) {
							
							HttpRequestMessage req = reqMsgQueue.take();
							
							putLog(req);
							
							HttpResponseMessage res = receiveRequest(req, connectionValue, config);
							
							if ( res == null ) {
								break;
							}
							
							boolean f = sendResponse(channel, req, res);
							
							putLog(res);
							
							if ( ! f ) {
								break;
							}
						}
					}
					catch ( InterruptedException ignore ) {
					}
					catch ( IOException | HttpServerException e ) {
						putLog(e);
					}
					
					return null;
				}
				);
		
		try {
			executorService().invokeAny(tasks, this.config.connectionTimeout(), TimeUnit.SECONDS);
		}
		catch ( TimeoutException e ) {
			this.putLog(e);
		}
		catch ( ExecutionException e ) {
			
			Throwable t = e.getCause();
			
			if ( t instanceof RuntimeException ) {
				throw (RuntimeException)t;
			}
			
			this.putLog(t);
		}
	}
	
	private static HttpRequestMessage createHttpRequestMessage(
			HttpRequestLineParser requestLine,
			HttpHeaderListParser headers,
			BlockingQueue<Byte> recvQueue
			) throws InterruptedException, HttpServerException {
		
		if ( headers.isTransferEncodingChunked() ) {
			
			return createHttpRequestChunkMessage(requestLine, headers, recvQueue);
			
		} else {
			
			int len = (int)(headers.contentLength());
			
			byte[] body = new byte[len];
			
			for ( int i = 0; i < len; ++i ) {
				body[i] = recvQueue.take();
			}
			
			return new AbstractHttpRequestBytesMessage(requestLine, headers, body) {
				
				private static final long serialVersionUID = -7571083631584881313L;
			};
		}
	}
	
	private static HttpRequestMessage createHttpRequestChunkMessage(
			HttpRequestLineParser requestLine,
			HttpHeaderListParser headers,
			BlockingQueue<Byte> recvQueue
			) throws InterruptedException, HttpServerException {
		
		try (
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				) {
			
			try (
					ByteArrayOutputStream sizest = new ByteArrayOutputStream();
					) {
				
				for ( ;; ) {
					
					/* read chunk-size */
					for ( ;; ) {
						
						byte b = recvQueue.take().byteValue();
						
						baos.write(b);
						
						if ( b == LF ) {
							break;
						}
						
						if ( b != CR ) {
							sizest.write(b);
						}
					}
					
					String[] ss = new String(sizest.toByteArray(), StandardCharsets.US_ASCII).split(";");
					
					int size = Integer.parseInt(ss[0], 16);
					
					if ( size == 0 ) {
						break;
					}
					
					sizest.reset();
					
					/* read chunk-data */
					for ( int i = 0; i < size; ++i ) {
						baos.write(recvQueue.take().byteValue());
					}
					
					if ( recvQueue.take().byteValue() != CR ) {
						throw new HttpServerRequestChunkMessageParseException();
					}
					
					if ( recvQueue.take().byteValue() != LF ) {
						throw new HttpServerRequestChunkMessageParseException();
					}
				}
			}
			
			/* read trailer */
			{
				int size = 0;
				
				for ( ;; ) {
					
					byte b = recvQueue.take().byteValue();
					
					baos.write(b);
					
					if ( b == LF ) {
						
						if ( size == 0 ) {
							break;
						}
						
						size = 0;
						
					} else if ( b != CR ) {
						
						++ size;
					}
				}
			}
			
			byte[] chunks = baos.toByteArray();
			
			return AbstractHttpRequestChunkMessage.build(requestLine, headers, chunks);
		}
		catch ( NumberFormatException e ) {
			throw new HttpServerRequestChunkMessageParseException(e);
		}
		catch ( IOException e ) {
			throw new HttpServerRequestChunkMessageParseException(e);
		}
	}
	
	private final Collection<HttpApi> apis = new CopyOnWriteArrayList<>();
	
	@Override
	public boolean addApi(HttpApi api) {
		return apis.add(api);
	}
	
	@Override
	public boolean removeApi(HttpApi api) {
		return apis.remove(api);
	}
	
	@Override
	protected HttpResponseMessage receiveRequest(
			HttpRequestMessage request,
			HttpConnectionValue connectionValue,
			HttpServerConfig serverConfig)
					throws InterruptedException, HttpServerException {
		
		for ( HttpApi api : apis ) {
			if ( api.accept(request) ) {
				return api.receiveRequest(request, connectionValue, serverConfig);
			}
		}
		
		return HttpResponseMessage.build(request, HttpResponseCode.BadRequest);
	}
	
	@Override
	protected boolean sendResponse(
			AsynchronousSocketChannel channel,
			HttpRequestMessage request,
			HttpResponseMessage response)
					throws InterruptedException, IOException, HttpServerException {
		
		try {
			
			byte[] bs = response.getBytes();
			ByteBuffer buffer = ByteBuffer.allocate(bs.length);
			buffer.put(bs);
			((Buffer)buffer).flip();
			
			while ( buffer.hasRemaining() ) {
				
				final Future<Integer> f = channel.write(buffer);
				
				try {
					int w = f.get().intValue();
					
					if ( w <= 0 ) {
						throw new HttpServerResponseMessageException();
					}
				}
				catch ( InterruptedException e ) {
					f.cancel(true);
					throw e;
				}
			}
		}
		catch ( ExecutionException e ) {
			
			Throwable t = e.getCause();
			
			if ( t instanceof RuntimeException ) {
				throw (RuntimeException)t;
			}
			
			if ( t instanceof IOException ) {
				throw (IOException)t;
			}
			
			throw new HttpServerResponseMessageException(t);
		}
		
		switch ( request.version() ) {
		case HTTP_2_0:
		case HTTP_1_1: {
			
			return ! response.headerListParser().isConnectionClose();
			/* break; */
		}
		case HTTP_1_0:
		default : {
			
			return false;
		}
		}
	}

}
