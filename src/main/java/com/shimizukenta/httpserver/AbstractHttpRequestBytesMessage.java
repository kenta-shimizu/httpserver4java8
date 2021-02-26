package com.shimizukenta.httpserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractHttpRequestBytesMessage extends AbstractHttpRequestMessage {
	
	private static final long serialVersionUID = -4666937793555047905L;
	
	private final List<byte[]> body;
	
	private List<byte[]> cacheBytes;
	private String cacheToString;
	
	public AbstractHttpRequestBytesMessage(
			HttpRequestLineParser requestLine,
			HttpHeaderListParser headerList,
			List<byte[]> body) {
		
		super(requestLine, headerList);
		
		this.body = body.stream()
				.map(bs -> Arrays.copyOf(bs, bs.length))
				.collect(Collectors.toList());
		
		this.cacheToString = null;
		this.cacheBytes = null;
	}
	
	public AbstractHttpRequestBytesMessage(
			HttpRequestLineParser requestLine,
			HttpHeaderListParser headerList,
			byte[] body) {
		
		this(requestLine, headerList, Collections.singletonList(body));
	}
	
	@Override
	public List<byte[]> body() {
		return Collections.unmodifiableList(this.body);
	}
	
	@Override
	public List<byte[]> getBytes() {
		
		synchronized ( this ) {
			
			if ( this.cacheBytes == null ) {
				
				List<byte[]> ll = new ArrayList<>();
				
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
					
					ll.add(baos.toByteArray());
				}
				catch ( IOException giveup ) {
				}
				
				ll.addAll(body);
				
				this.cacheBytes = Collections.unmodifiableList(ll);
			}
			
			return this.cacheBytes;
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
