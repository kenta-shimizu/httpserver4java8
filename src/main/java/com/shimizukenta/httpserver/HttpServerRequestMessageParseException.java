package com.shimizukenta.httpserver;

public class HttpServerRequestMessageParseException extends HttpServerException {
	
	private static final long serialVersionUID = -7778664401217199884L;
	
	public HttpServerRequestMessageParseException() {
		super();
	}
	
	public HttpServerRequestMessageParseException(String message) {
		super(message);
	}
	
	public HttpServerRequestMessageParseException(Throwable cause) {
		super(cause);
	}
	
	public HttpServerRequestMessageParseException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public HttpServerRequestMessageParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
}
