package com.shimizukenta.httpserver;

import java.util.Objects;

public class HttpResponseStatusLine {
	
	private static final String SP = " ";
	
	private final HttpVersion version;
	private final String versionStr;
	private final String statusCode;
	private final String reasonPhrase;
	private String cacheLine;
	
	public HttpResponseStatusLine(CharSequence version, CharSequence code, CharSequence reasonPhrase) {
		this.version = HttpVersion.from(version);
		this.versionStr = Objects.requireNonNull(version).toString();
		this.statusCode = Objects.requireNonNull(code).toString();
		this.reasonPhrase = Objects.requireNonNull(reasonPhrase).toString();
		this.cacheLine = null;
	}
	
	public HttpResponseStatusLine(HttpVersion version, HttpResponseCode responseCode) {
		this.version = version;
		this.versionStr = version.toString();
		this.statusCode = responseCode.codeString();
		this.reasonPhrase = responseCode.defaultReasonPhrase();
		this.cacheLine = null;
	}
	
	public HttpVersion version() {
		return this.version;
	}
	
	public String toLine() {
		synchronized ( this ) {
			if ( this.cacheLine == null ) {
				this.cacheLine = versionStr + SP
						+ statusCode + SP
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
