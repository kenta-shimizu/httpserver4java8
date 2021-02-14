/**
 * 
 */
package com.shimizukenta.httpserver;

/**
 * This interface is HttpServer-Throwable-Log
 * 
 * <p>
 * To get cause, {@link #getCause()}.<br />
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public interface HttpServerThrowableLog extends HttpServerLog {
	
	/**
	 * Cause getter.
	 * 
	 * @return cause
	 */
	public Throwable getCause();
	
}
