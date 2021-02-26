package com.shimizukenta.httpserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractHttpResponseMessage extends AbstractHttpMessage implements HttpResponseMessage {
	
	private static final long serialVersionUID = -769739754437449724L;
	
	private final HttpResponseStatusLine statusLine;
	private final List<byte[]> body;
	
	private byte[] cacheHeadBytes;
	private List<byte[]> cacheBytes;
	private String cacheToString;
	
	public AbstractHttpResponseMessage(
			HttpResponseStatusLine statusLine,
			HttpHeaderListParser headerList,
			List<byte[]> body) {
		
		super(headerList);
		
		this.statusLine = statusLine;
		
		this.body = body.stream()
				.map(bs -> Arrays.copyOf(bs, bs.length))
				.collect(Collectors.toList());
		
		this.cacheHeadBytes = null;
		this.cacheBytes = null;
		this.cacheToString = null;
	}
	
	public AbstractHttpResponseMessage(
			HttpResponseStatusLine statusLine,
			HttpHeaderListParser headerList,
			byte[] body) {
		
		this(statusLine, headerList, Collections.singletonList(body));
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
	public List<byte[]> body() {
		return Collections.unmodifiableList(this.body);
	}
	
	@Override
	public List<byte[]> getBytes() {
		
		synchronized ( this ) {
			
			if ( this.cacheBytes == null ) {
				
				final List<byte[]> ll = new ArrayList<>();
				ll.add(getHeadBytes());
				ll.addAll(body);
				
				this.cacheBytes = Collections.unmodifiableList(ll);
			}
			
			return this.cacheBytes;
		}
	}
	
	public byte[] getHeadBytes() {
		
		synchronized ( this ) {
			
			if ( this.cacheHeadBytes == null ) {
				
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
					
					this.cacheHeadBytes = baos.toByteArray();
				}
				catch ( IOException giveup ) {
				}
			}
			
			return this.cacheHeadBytes == null ? new byte[0] : this.cacheHeadBytes;
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
				
				sb.append(this.statusLine());
				
				for ( String line : headerLines() ) {
					sb.append(CrLfString).append(line);
				}
				
				this.cacheToString = sb.toString();
			}
			
			return this.cacheToString;
		}
	}
	
}
