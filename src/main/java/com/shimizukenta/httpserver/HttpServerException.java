package com.shimizukenta.httpserver;

public class HttpServerException extends Exception {
	
	private static final long serialVersionUID = -4278331778638677007L;

	public HttpServerException() {
		super();
	}

	public HttpServerException(String message) {
		super(message);
	}

	public HttpServerException(Throwable cause) {
		super(cause);
	}

	public HttpServerException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
