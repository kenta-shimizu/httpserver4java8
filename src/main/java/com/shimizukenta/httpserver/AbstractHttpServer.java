package com.shimizukenta.httpserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractHttpServer implements HttpServer {
	
	private final ExecutorService execServ = Executors.newCachedThreadPool(r -> {
		Thread th = new Thread(r);
		th.setDaemon(true);
		return th;
	});
	
	private final AbstractHttpServerConfig config;
	
	private boolean opened;
	private boolean closed;
	
	public AbstractHttpServer(AbstractHttpServerConfig config) {
		this.config = config;
		
		this.opened = false;
		this.closed = false;
	}
	
	@Override
	public void open() throws IOException {
		
		synchronized ( this ) {
			
			if ( this.closed ) {
				throw new IOException("Already closed");
			}
			
			if ( this.opened ) {
				throw new IOException("Already opened");
			}
			
			this.opened = true;
		}
		
		config.serverAddresses().forEach(this::openServer);
	}
	
	private void openServer(SocketAddress addr) {
		
		execServ.execute(() -> {
			
			try {
				for ( ;; ) {
					
					try (
							AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
							) {
						
						putLog(SimpleHttpServerBindLog.tryBind(addr));
						
						server.bind(addr);
						
						putLog(SimpleHttpServerBindLog.binded(addr));
						
						server.accept(null, new CompletionHandler<AsynchronousSocketChannel,Void>() {

							@Override
							public void completed(AsynchronousSocketChannel channel, Void attachment) {
								
								server.accept(null, this);
								
								SocketAddress server = null;
								SocketAddress client = null;
								
								try {
									server = channel.getLocalAddress();
									client = channel.getRemoteAddress();
									
									putLog(SimpleHttpServerConnectionLog.accept(server, client));
									
									Collection<Callable<Void>> tasks = Arrays.asList(() -> {
										
										try {
											connectionWorker(channel);
										}
										catch ( IOException e ) {
											putLog(e);
										}
										catch ( InterruptedException ignore ) {
										}
										return null;
									});
									
									execServ.invokeAny(tasks);
								}
								catch ( InterruptedException igonore ) {
								}
								catch ( ExecutionException e ) {
									
									Throwable t = e.getCause();
									
									if ( t instanceof RuntimeException ) {
										throw (RuntimeException)t;
									}
									
									putLog(t);
								}
								catch ( IOException e ) {
									putLog(e);
								}
								finally {
									
									try {
										channel.shutdownOutput();
									}
									catch ( IOException giveup ) {
									}
									
									try {
										channel.close();
									}
									catch ( IOException giveup ) {
									}
									
									putLog(SimpleHttpServerConnectionLog.closed(server, client));
								}
							}
							
							@Override
							public void failed(Throwable t, Void attachment) {
								
								if ( ! (t instanceof ClosedChannelException ) ) {
									putLog(t);
								}
								
								synchronized ( server ) {
									server.notifyAll();
								}
							}
						});
						
						synchronized ( server ) {
							server.wait();
						}
						
					}
					catch ( IOException e ) {
						putLog(e);
					}
					
					putLog(SimpleHttpServerBindLog.closed(addr));
					
					TimeUnit.SECONDS.sleep(10L);
				}
			}
			catch ( InterruptedException ignore ) {
			}
		});
	}
	
	
	protected static final byte CR = (byte)0xD;
	protected static final byte LF = (byte)0xA;
	
	protected void connectionWorker(AsynchronousSocketChannel channel) throws InterruptedException, IOException {
		
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
							
							HttpServerRequestMessageLog reqLog = putLog(req);
							
							HttpResponseMessage res = receiveRequest(req, connectionValue, config);
							
							if ( res == null ) {
								break;
							}
							
							boolean f = sendResponse(channel, req, res);
							
							HttpServerResponseMessageLog resLog = putLog(res);
							
							putLog(reqLog, resLog, client, server);
							
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
			
			if ( t instanceof IOException ) {
				throw (IOException)t;
			}
			
			this.putLog(t);
		}
	}
	
	protected static HttpRequestMessage createHttpRequestMessage(
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
			
			final List<byte[]> chunks = new ArrayList<>();
			
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
					
					{
						byte b = recvQueue.take().byteValue();
						if ( b != CR ) {
							throw new HttpServerRequestChunkMessageParseException();
						}
						baos.write(b);
					}
					{
						byte b = recvQueue.take().byteValue();
						if ( b != LF ) {
							throw new HttpServerRequestChunkMessageParseException();
						}
						baos.write(b);
					}
					
					chunks.add(baos.toByteArray());
					
					baos.reset();
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
				
				chunks.add(baos.toByteArray());
			}
			
			return AbstractHttpRequestChunkMessage.build(requestLine, headers, chunks);
		}
		catch ( NumberFormatException e ) {
			throw new HttpServerRequestChunkMessageParseException(e);
		}
		catch ( IOException e ) {
			throw new HttpServerRequestChunkMessageParseException(e);
		}
	}
	
	
	/**
	 * Send Response-Message proto-type.
	 * 
	 * @param channel
	 * @param request
	 * @param response
	 * @return {@code true} if channel close
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws HttpServerException
	 */
	protected boolean sendResponse(
			AsynchronousSocketChannel channel,
			HttpRequestMessage request,
			HttpResponseMessage response)
					throws InterruptedException, IOException, HttpServerException {
		
		try {
			
			final List<byte[]> ll;
			
			if ( request.method() == HttpRequestMethod.HEAD ) {
				ll = Collections.singletonList(response.getHeadBytes());
			} else {
				ll = response.getBytes();
			}
			
			for ( byte[] bs : ll ) {
				
				final ByteBuffer buffer = ByteBuffer.allocate(bs.length);
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

	
	@Override
	public void close() throws IOException {
		
		synchronized ( this ) {
			
			if ( this.closed ) {
				return;
			}
			
			this.closed = true;
			
			IOException ioExcept = null;
			
			try {
				execServ.shutdownNow();
				if ( ! execServ.awaitTermination(5L, TimeUnit.SECONDS) ) {
					ioExcept = new IOException("ExecutorService#shutdown failed");
				}
			}
			catch ( InterruptedException giveup ) {
			}
			
			if ( ioExcept != null ) {
				throw ioExcept;
			}
		}
	}
	
	@Override
	public boolean isOpen() {
		synchronized ( this ) {
			return this.opened && (! this.closed);
		}
	}
	
	@Override
	public boolean isClosed() {
		synchronized ( this ) {
			return this.closed;
		}
	}
	
	private final Collection<HttpServerLogListener> logListeners = new CopyOnWriteArrayList<>();
	
	@Override
	public boolean addLogListener(HttpServerLogListener l) {
		return logListeners.add(l);
	}

	@Override
	public boolean removeLogListener(HttpServerLogListener l) {
		return logListeners.remove(l);
	}
	
	protected void putLog(HttpServerLog log) {
		logListeners.forEach(l -> {
			l.received(log);
		});
	}
	
	protected AbstractHttpServerRequestMessageLog putLog(HttpRequestMessage request) {
		
		AbstractHttpServerRequestMessageLog log = new AbstractHttpServerRequestMessageLog(request) {
			
			private static final long serialVersionUID = -5745877813910058368L;
		};
		
		putLog(log);
		
		return log;
	}
	
	protected AbstractHttpServerResponseMessageLog putLog(HttpResponseMessage response) {
		
		AbstractHttpServerResponseMessageLog log =new AbstractHttpServerResponseMessageLog(response) {
			
			private static final long serialVersionUID = 6478174154016942721L;
		};
		
		putLog(log);
		
		return log;
	}
	
	protected AbstractHttpServerAccessLog putLog(
			HttpServerRequestMessageLog request,
			HttpServerResponseMessageLog response,
			SocketAddress client,
			SocketAddress server) {
		
		AbstractHttpServerAccessLog log = new AbstractHttpServerAccessLog(
				request,
				response,
				client,
				server) {
			
					private static final long serialVersionUID = 3751112139339892597L;
		};
		
		putLog(log);
		
		return log;
	}
	
	protected AbstractHttpServerThrowableLog putLog(Throwable t) {
		
		AbstractHttpServerThrowableLog log =new AbstractHttpServerThrowableLog(t) {

			private static final long serialVersionUID = -7233644881222347640L;
		};
		
		putLog(log);
		
		return log;
	}
	
	protected ExecutorService executorService() {
		return this.execServ;
	}
	
}
