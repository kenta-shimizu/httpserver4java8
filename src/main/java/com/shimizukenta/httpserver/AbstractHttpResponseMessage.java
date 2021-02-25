package com.shimizukenta.httpserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public abstract class AbstractHttpResponseMessage extends AbstractHttpMessage implements HttpResponseMessage {
	
	private static final long serialVersionUID = -769739754437449724L;
	
	private final HttpResponseStatusLine statusLine;
	private final byte[] body;
	
	private byte[] cacheBytes;
	private String cacheToString;
	
	public AbstractHttpResponseMessage(
			HttpResponseStatusLine statusLine,
			HttpHeaderListParser headerList,
			byte[] body) {
		
		super(headerList);
		
		this.statusLine = statusLine;
		this.body = Arrays.copyOf(body, body.length);
		
		this.cacheBytes = null;
		this.cacheToString = null;
	}
	
	@Override
	public HttpVersion version() {
		return statusLine.version();
	}
	
	@Override
	public String versionString() {
		return this.statusLine.version().toString();
	}
	
	@Override
	public byte[] body() {
		return Arrays.copyOf(body, body.length);
	}
	
	@Override
	public byte[] getBytes() {
		
		synchronized ( this ) {
			
			if ( this.cacheBytes == null ) {
				
				try (
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						) {
					
					baos.write(statusLine().getBytes(StandardCharsets.US_ASCII));
					baos.write(CrLfBytes);
					
					for ( String line : headerLines() ) {
						baos.write(line.getBytes(StandardCharsets.US_ASCII));
						baos.write(CrLfBytes);
					}
					
					baos.write(CrLfBytes);
					
					baos.write(body);
					
					this.cacheBytes = baos.toByteArray();
				}
				catch ( IOException giveup ) {
				}
			}
			
			return this.cacheBytes == null ? new byte[0] : this.cacheBytes;
		}
	}
	
	@Override
	public String statusLine() {
		return statusLine.toLine();
	}
	
	@Override
	public String toString() {
		
		synchronized ( this ) {
			
			if ( this.cacheToString == null ) {
				
				StringBuilder sb = new StringBuilder();
				
				sb.append(this.statusLine()).append(CrLfString);
				
				for ( String line : headerLines() ) {
					sb.append(line).append(CrLfString);
				}
				
				this.cacheToString = sb.toString();
			}
			
			return this.cacheToString;
		}
	}
	
}
