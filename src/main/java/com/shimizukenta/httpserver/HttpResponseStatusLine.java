package com.shimizukenta.httpserver;

import java.util.Objects;

public class HttpResponseStatusLine {
	
	private static final String SP = " ";
	
	private final HttpVersion version;
	private final String versionStr;
	private HttpResponseCode statusCode;
	private final String statusCodeStr;
	private final String reasonPhrase;
	private String cacheLine;
	
	public HttpResponseStatusLine(CharSequence version, CharSequence code, CharSequence reasonPhrase) {
		this.version = HttpVersion.from(version);
		this.versionStr = Objects.requireNonNull(version).toString();
		this.statusCode = null;
		this.statusCodeStr = Objects.requireNonNull(code).toString();
		this.reasonPhrase = Objects.requireNonNull(reasonPhrase).toString();
		this.cacheLine = null;
	}
	
	public HttpResponseStatusLine(HttpVersion version, HttpResponseCode responseCode) {
		this.version = version;
		this.versionStr = version.toString();
		this.statusCode = responseCode;
		this.statusCodeStr = responseCode.codeString();
		this.reasonPhrase = responseCode.defaultReasonPhrase();
		this.cacheLine = null;
	}
	
	public HttpVersion version() {
		return this.version;
	}
	
	public HttpResponseCode statusCode() {
		synchronized ( this ) {
			if ( this.statusCode == null ) {
				this.statusCode = HttpResponseCode.from(statusCodeStr);
			}
			return this.statusCode;
		}
	}
	
	public String toLine() {
		synchronized ( this ) {
			if ( this.cacheLine == null ) {
				this.cacheLine = versionStr + SP
						+ statusCodeStr + SP
						+ reasonPhrase;
			}
			return this.cacheLine;
		}
	}
	
	@Override
	public String toString() {
		return toLine();
	}
	
}
