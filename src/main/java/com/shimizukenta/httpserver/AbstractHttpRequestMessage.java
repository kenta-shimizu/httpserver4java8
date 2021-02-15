package com.shimizukenta.httpserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AbstractHttpRequestMessage extends AbstractHttpMessage implements HttpRequestMessage {
	
	private final String requestLine;
	private final List<String> headerLines;
	
	private HttpRequestMethod cacheMethod;
	private String cacheMethodString;
	private String cacheUri;
	private HttpVersion cacheVersion;
	private String cacheVersionString;
	private List<HttpHeader> cacheHeaders;
	
	public AbstractHttpRequestMessage(
			CharSequence requestLine,
			List<? extends CharSequence> headerLines
			) {
		
		super();
		
		this.requestLine = Objects.requireNonNull(requestLine).toString().trim();
		this.headerLines = headerLines.stream()
				.map(CharSequence::toString)
				.collect(Collectors.toList());
		
		this.cacheMethod = null;
		this.cacheMethodString = null;
		this.cacheUri = null;
		this.cacheVersion = null;
		this.cacheVersionString = null;
		this.cacheHeaders = null;
	}
	
	@Override
	public String requestLine() {
		return requestLine;
	}
	
	private void parseRequestLine() {
		
		String[] ss = requestLine.split(SP, 3);
		int len = ss.length;
		
		if ( len > 0 ) {
			this.cacheMethodString = ss[0];
		} else {
			this.cacheMethodString = "";
		}
		
		if ( len > 1 ) {
			this.cacheUri = ss[1];
		} else {
			this.cacheUri = "";
		}
		
		if ( len > 2 ) {
			this.cacheVersionString = ss[2];
		} else {
			this.cacheVersionString = "";
		}
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
		synchronized ( this ) {
			if ( this.cacheMethodString == null ) {
				this.parseRequestLine();
			}
			return this.cacheMethodString;
		}
	}
	
	@Override
	public String uri() {
		synchronized ( this ) {
			if ( this.cacheUri == null ) {
				this.parseRequestLine();
			}
			return this.cacheUri;
		}
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
		synchronized ( this ) {
			if ( this.cacheVersionString == null ) {
				this.parseRequestLine();
			}
			return this.cacheVersionString;
		}
	}
	
	@Override
	public List<String> headerLines() {
		return Collections.unmodifiableList(this.headerLines);
	}

	@Override
	public List<HttpHeader> headers() {
		
		synchronized ( this ) {
			
			if ( this.cacheHeaders == null ) {
				
				final LinkedList<String> ll = new LinkedList<>();
				final LinkedList<String> aa = new LinkedList<>();
				
				for ( int i = this.headerLines.size(); i > 0; ) {
					--i;
					
					String s = this.headerLines.get(i);
					
					if ( ! s.isEmpty() ) {
						
						char c = s.charAt(0);
						
						if ( c > 0x0020 ) {
							
							aa.addFirst(s);
							
						} else {
							
							ll.addFirst(s + aa.stream().collect(Collectors.joining()));
							
							aa.clear();
						}
					}
				}
				
				//TOOD
				//to HttpHeader s
			}
			
			return Collections.unmodifiableList(this.cacheHeaders);
		}
	}

	@Override
	public byte[] body() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public byte[] getBytes() {
		
		// TODO Auto-generated method stub
		return null;
	}

}
