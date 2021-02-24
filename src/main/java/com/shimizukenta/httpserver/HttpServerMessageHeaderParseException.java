package com.shimizukenta.httpserver;

public class HttpServerMessageHeaderParseException extends HttpServerException {
	
	private static final long serialVersionUID = -583160559199562372L;
	
	public HttpServerMessageHeaderParseException() {
		super();
	}
	
	public HttpServerMessageHeaderParseException(String message) {
		super(message);
	}
	
	public HttpServerMessageHeaderParseException(Throwable cause) {
		super(cause);
	}
	
	public HttpServerMessageHeaderParseException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public HttpServerMessageHeaderParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
}
