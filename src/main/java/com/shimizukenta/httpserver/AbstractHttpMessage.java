package com.shimizukenta.httpserver;

import java.util.List;

public abstract class AbstractHttpMessage implements HttpMessage {
	
	private final HttpHeaderListParser headerList;
	
	protected static final String SP = " ";
	protected static final String CrLfString = "\r\n";
	protected static final byte[] CrLfBytes = new byte[] {(byte)0xD, (byte)0xA};
	
	public AbstractHttpMessage(HttpHeaderListParser headerList) {
		this.headerList = headerList;
	}
	
	@Override
	public HttpHeaderListParser headerListParser() {
		return this.headerList;
	}
	
	@Override
	public List<String> headerLines() {
		return this.headerList.lines();
	}
	
	@Override
	public List<HttpHeader> headers() {
		return this.headerList.headers();
	}
	
}
