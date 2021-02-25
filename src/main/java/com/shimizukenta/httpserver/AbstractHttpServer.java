package com.shimizukenta.httpserver;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
	
	
	abstract protected void connectionWorker(AsynchronousSocketChannel channel)
			throws InterruptedException, IOException;
	
	/**
	 * Returns Response-Message from Request-Message.
	 * 
	 * @param message
	 * @param connectionValue
	 * @param serverConfig
	 * @return HttpResponseMessage
	 * @throws InterruptedException
	 * @throws HttpServerException
	 */
	abstract protected HttpResponseMessage receiveRequest(
			HttpRequestMessage message,
			HttpConnectionValue connectionValue,
			HttpServerConfig serverConfig)
					throws  InterruptedException, HttpServerException;
	
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
	abstract protected boolean sendResponse(
			AsynchronousSocketChannel channel,
			HttpRequestMessage request,
			HttpResponseMessage response)
					throws InterruptedException, IOException, HttpServerException;
	
	
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
	
	protected void putLog(HttpRequestMessage request) {
		
		putLog(new AbstractHttpRequestMessageLog(request) {
			
			private static final long serialVersionUID = -5745877813910058368L;
		});
	}
	
	protected void putLog(HttpResponseMessage response) {
		
		putLog(new AbstractHttpResponseMessageLog(response) {
			
			private static final long serialVersionUID = 6478174154016942721L;
		});
	}
	
	protected void putLog(Throwable t) {
		putLog(new AbstractHttpServerThrowableLog(t) {

			private static final long serialVersionUID = -7233644881222347640L;
		});
	}
	
	protected ExecutorService executorService() {
		return this.execServ;
	}
	
	protected HttpServerConfig config() {
		return config;
	}
	
}
