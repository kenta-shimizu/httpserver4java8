package com.shimizukenta.httpserver;

public class HttpServerRequestChunkMessageParseException extends HttpServerRequestMessageParseException {
	
	private static final long serialVersionUID = 5546377585606249332L;
	
	public HttpServerRequestChunkMessageParseException() {
		super();
	}

	public HttpServerRequestChunkMessageParseException(String message) {
		super(message);
	}

	public HttpServerRequestChunkMessageParseException(Throwable cause) {
		super(cause);
	}

	public HttpServerRequestChunkMessageParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpServerRequestChunkMessageParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
