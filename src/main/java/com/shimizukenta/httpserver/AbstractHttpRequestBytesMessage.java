package com.shimizukenta.httpserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public abstract class AbstractHttpRequestBytesMessage extends AbstractHttpRequestMessage {
	
	private final byte[] body;
	private byte[] cacheBytes;
	
	private String cacheToString;
	
	public AbstractHttpRequestBytesMessage(
			HttpRequestLineParser requestLine,
			HttpHeaderListParser headerList,
			byte[] body) {
		
		super(requestLine, headerList);
		
		this.body = Arrays.copyOf(body, body.length);
		
		this.cacheToString = null;
		this.cacheBytes = null;
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
					
					baos.write(requestLine().getBytes(StandardCharsets.US_ASCII));
					baos.write(CrLfBytes);
					
					for (String line : this.headerLines() ) {
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
			
			if ( this.cacheBytes == null ) {
				
				return new byte[0];
			
			} else {
				
				return Arrays.copyOf(this.cacheBytes, this.cacheBytes.length);
			}
		}
	}
	
	private static final String BR = System.lineSeparator();
	
	@Override
	public String toString() {
		
		synchronized ( this ) {
			
			if ( this.cacheToString == null ) {
				
				StringBuilder sb = new StringBuilder();
				
				sb.append(this.requestLine());
				this.headerLines().forEach(line -> {
					sb.append(BR).append(line);
				});
				
				this.cacheToString = sb.toString();
			}
			
			return this.cacheToString;
		}
	}

}
