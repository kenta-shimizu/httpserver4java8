package com.shimizukenta.httpserver;

public abstract class AbstractHttpRequestMessage extends AbstractHttpMessage implements HttpRequestMessage {
	
	private static final long serialVersionUID = -4808104067492284482L;
	
	private final HttpRequestLineParser requestLine;
	
	private HttpRequestMethod cacheMethod;
	private HttpVersion cacheVersion;
	
	public AbstractHttpRequestMessage(
			HttpRequestLineParser requestLine,
			HttpHeaderListParser headerList
			) {
		
		super(headerList);
		
		this.requestLine = requestLine;
		
		this.cacheMethod = null;
		this.cacheVersion = null;
	}
	
	@Override
	public String requestLine() {
		return requestLine.line();
	}
	
	@Override
	public HttpRequestMethod method() {
		synchronized ( this ) {
			if ( this.cacheMethod == null ) {
				this.cacheMethod = HttpRequestMethod.from(this.methodString());
			}
			return this.cacheMethod;
		}
	}
	
	@Override
	public String methodString() {
		return requestLine.method();
	}
	
	@Override
	public String uri() {
		return requestLine.uri();
	}
	
	@Override
	public HttpVersion version() {
		synchronized ( this ) {
			if ( this.cacheVersion == null ) {
				this.cacheVersion = HttpVersion.from(this.versionString());
			}
			return this.cacheVersion;
		}
	}
	
	@Override
	public String versionString() {
		return requestLine.version();
	}
}
