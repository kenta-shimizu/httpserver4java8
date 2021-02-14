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
						
						//TODO
						//putLog -TryBind
						
						server.bind(addr);
						
						//TODO
						//putLog-binded.
						
						server.accept(null, new CompletionHandler<AsynchronousSocketChannel,Void>() {

							@Override
							public void completed(AsynchronousSocketChannel channel, Void attachment) {
								
								server.accept(null, this);
								
								SocketAddress client = null;
								SocketAddress server = null;
								
								try {
									client = channel.getRemoteAddress();
									server = channel.getLocalAddress();
									
									//TODO
									//putLog-accepted
									
									Collection<Callable<Void>> tasks = Arrays.asList(() -> {
										try {
											connectionWorker(channel);
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
									
									//TODO
									//putLog-closed
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
						
						//TODO
						//server closed
					}
					catch ( IOException e ) {
						putLog(e);
					}
					
					TimeUnit.SECONDS.sleep(10L);
				}
			}
			catch ( InterruptedException ignore ) {
			}
		});
	}
	
	abstract public void connectionWorker(AsynchronousSocketChannel channel) throws InterruptedException;
	
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
	
	protected void putLog(Throwable t) {
		putLog(new AbstractHttpServerThrowableLog(t) {

			private static final long serialVersionUID = -7233644881222347640L;
		});
	}
	
	protected ExecutorService executorService() {
		return this.execServ;
	}
	
	protected AbstractHttpServerConfig config() {
		return config;
	}
	
}
