package com.shimizukenta.httpserver;

public class HttpServerResponseMessageException extends HttpServerException {
	
	private static final long serialVersionUID = 8660057437886107450L;
	
	public HttpServerResponseMessageException() {
		super();
	}

	public HttpServerResponseMessageException(String message) {
		super(message);
	}

	public HttpServerResponseMessageException(Throwable cause) {
		super(cause);
	}

	public HttpServerResponseMessageException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpServerResponseMessageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
