package com.shimizukenta.httpserver;

public enum HttpResponseCode {
	
	UNKNOWN(0, "UNKNOWN"),
	
	Continue(100, "Continue"),
    SwitchingProtocols(101, "Switching Protocols"),
    
	OK(200, "OK"),
	Created(201, "Created"),
	Accepted(202, "Accepted"),
	NonAuthoritativeInformation(203, "Non-Authoritative Information"),
	NoContent(204, "No Content"),
	ResetContent(205, "Reset Content"),
	PartialContent(206, "Partial Content"),
	
	MultipleChoices(300, "Multiple Choices"),
	MovedPermanently(301, "Moved Permanently"),
	Found(302, "Found"),
	SeeOther(303, "See Other"),
	NotModified(304, "Not Modified"),
	UseProxy(305, "Use Proxy"),
	TemporaryRedirect(307, "Temporary Redirect"),
	
	BadRequest(400, "Bad Request"),
	Unauthorized(401, "Unauthorized"),
	PaymentRequired(402, "Payment Required"),
	Forbidden(403, "Forbidden"),
	NotFound(404, "Not Found"),
	NotAllowed(405, "Not Allowed"),
	NotAcceptable(406, "Not Acceptable"),
	ProxyAuthenticationRequired(407, "Proxy Authentication Required"),
	RequestTimeOut(408, "Request Time-out"),
	Conflict(409, "Conflict"),
	Gone(410, "Gone"),
	LengthRequired(411, "Length Required"),
	PreconditionFailed(412, "Precondition Failed"),
	RequestEntityFooLarge(413, "Request Entity Too Large"),
	RequestUriTooLarge(414, "Request-URI Too Large"),
	UnsupportedMediaType(415, "Unsupported Media Type"),
	RequestedRangeNotSatisfiable(416, "Requested range not satisfiable"),
	ExpectationFailed(417, "Expectation Failed"),
	
	InternalServerError(500, "Internal Server Error"),
	NotImplemented(501, "Not Implemented"),
	BadGateway(502, "Bad Gateway"),
	ServiceUnavailable(503, "Service Unavailable"),
	GatewayTimeOut(504, "Gateway Time-out"),
	HTTPVersionNotSupported(505, "HTTP Version not supported"),
	
	;
	
	private final int code;
	private final String codeStr;
	private final String defaultReasonPhrase;
	
	private HttpResponseCode(int code, String defaultReasonPhrase) {
		this.code = code;
		this.codeStr = String.format("%03d", code);
		this.defaultReasonPhrase = defaultReasonPhrase;
	}
	
	public int code() {
		return this.code;
	}
	
	public String codeString() {
		return this.codeStr;
	}
	
	public String defaultReasonPhrase() {
		return this.defaultReasonPhrase;
	}
	
	public static HttpResponseCode from(int code) {
		
		for ( HttpResponseCode v : values() ) {
			if ( v.code() == code ) {
				return v;
			}
		}
		
		return HttpResponseCode.UNKNOWN;
	}
	
	public static HttpResponseCode from(CharSequence codeString) {
		
		if ( codeString != null ) {
			
			try {
				return from(Integer.valueOf(codeString.toString().trim()));
			}
			catch ( NumberFormatException ignore ) {
			}
		}
		
		return HttpResponseCode.UNKNOWN;
	}
	
}
