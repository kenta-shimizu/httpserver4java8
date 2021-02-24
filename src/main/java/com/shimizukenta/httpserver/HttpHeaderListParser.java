package com.shimizukenta.httpserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class HttpHeaderListParser {
	
	private final List<HttpHeader> headers;
	private List<String> lines;
	
	private HttpHeaderListParser(List<HttpHeader> headers, List<String> lines) {
		this.headers = new ArrayList<>(headers);
		this.lines = lines;
	}
	
	/**
	 * Returns HttpHeader-List.
	 * 
	 * @return HttpHeader-List
	 */
	public List<HttpHeader> headers() {
		return Collections.unmodifiableList(this.headers);
	}
	
	/**
	 * Returns Header-String-List.
	 * 
	 * @return Header-String-List
	 */
	public List<String> lines() {
		
		synchronized ( this ) {
			
			if ( this.lines == null ) {
				
				this.lines = headers.stream()
						.map(HttpHeader::toLine)
						.collect(Collectors.toList());
			}
			
			return Collections.unmodifiableList(this.lines);
		}
	}
	
	/**
	 * Returns value if exist.
	 * 
	 * @param fieldName
	 * @return value if exist
	 */
	public Optional<String> optionalValue(CharSequence fieldName) {
		return getValues(fieldName).stream().findFirst();
	}
	
	/**
	 * Returns field-Value.
	 * 
	 * @param fieldName
	 * @return value if exist, otherwise null
	 */
	public String getValue(CharSequence fieldName) {
		return optionalValue(fieldName).orElse(null);
	}
	
	/**
	 * Returns field-Value-List, Empty-List if not exist.
	 * 
	 * @param fieldName
	 * @return values
	 */
	public List<String> getValues(CharSequence fieldName) {
		
		if ( fieldName != null ) {
			
			final String s = fieldName.toString();
			
			return this.headers.stream()
					.filter(x -> x.fieldName().equalsIgnoreCase(s))
					.map(HttpHeader::fieldValue)
					.collect(Collectors.toList());
		}
		
		return Collections.emptyList();
	}
	
	/**
	 * Returns {@code true} if has "Connection: close" header.
	 * 
	 * @return {@code true} if has "Connection: close" header
	 */
	public boolean isConnectionClose() {
		return getValues("Connection").stream().anyMatch(v -> v.equalsIgnoreCase("close"));
	}
	
	/**
	 * Returns Accept q-Sorted-List.
	 * 
	 * @return Accept q-Sorted-List
	 * @throws HttpServerMessageHeaderParseException
	 */
	public List<String> accept() throws HttpServerMessageHeaderParseException {
		return parseQSortStringList(getValue("Accept"));
	}
	
	/**
	 * Returns Accept-Language q-Sorted-List.
	 * 
	 * @return Accept-Language q-Sorted-List
	 * @throws HttpServerMessageHeaderParseException
	 */
	public List<String> acceptLanguage() throws HttpServerMessageHeaderParseException {
		return parseQSortStringList(getValue("Accept-Language"));
	}
	
	/**
	 * Returns Accept-Encoding q-Sorted-List.
	 * 
	 * @return Accept-Encoding q-Sorted-List
	 */
	public List<HttpEncoding> acceptEncodings() throws HttpServerMessageHeaderParseException {
		return HttpEncoding.fromFieldValue(getValue("Accept-Encoding"));
	}
	
	/**
	 * Returns Host if exist.
	 * 
	 * @return Host if exist
	 */
	public Optional<String> host() {
		return optionalValue("Host");
	}
	
	/**
	 * Returns {@code true} if Transfer-Encoding chunked.
	 * 
	 * @return {@code true} if Transfer-Encoding chunked
	 */
	public boolean isTransferEncodingChunked() {
		return getValues("Transfer-Encoding").stream().anyMatch(v -> v.equalsIgnoreCase("chunked"));
	}
	
	/**
	 * Return Content-Length, 0 if not exist.
	 * 
	 * @return Content-Length, 0 if not exist
	 * @throws HttpServerMessageHeaderParseException
	 */
	public long contentLength() throws HttpServerMessageHeaderParseException {
		
		String s = optionalValue("Content-Length").orElse("0");
		
		try {
			return Long.valueOf(s);
		}
		catch ( NumberFormatException e ) {
			throw new HttpServerMessageHeaderParseException("Content-Length \"" + s + "\" parse failed", e);
		}
	}
	
	/**
	 * Instance static-factory-method of HttpHeaders-List.
	 * 
	 * @param headers
	 * @return parser
	 */
	public static HttpHeaderListParser of(List<HttpHeader> headers) {
		return new HttpHeaderListParser(headers, null);
	}
	
	private static final String SEPARATOR = ":";
	
	/**
	 * Instance static-factory-method from Header-String-Line-List.
	 * 
	 * @param lines
	 * @return parser
	 * @throws HttpServerRequestMessageParseException
	 */
	public static HttpHeaderListParser fromLines(List<? extends CharSequence> lines) throws HttpServerRequestMessageParseException {
		
		final List<String> ll = new ArrayList<>();
		
		for ( CharSequence c : lines ) {
			if ( c == null ) {
				throw new HttpServerRequestMessageParseException("line require not null");
			}
			ll.add(c.toString());
		}
		
		final LinkedList<HttpHeader> hh = new LinkedList<>();
		
		{
			final LinkedList<String> aa = new LinkedList<>();
			
			for ( int i = ll.size(); i > 0; ) {
				--i;
				
				String l = ll.get(i);
				
				if ( ! l.isEmpty() ) {
					
					aa.addFirst(l);
					
					char c = l.charAt(0);
					
					if ( c > 0x0020 ) {
						
						String s = aa.stream().collect(Collectors.joining());
						
						String[] ss = s.split(SEPARATOR, 2);
						
						if ( ss.length != 2 ) {
							throw new HttpServerRequestMessageParseException("Header parse failed \"" + s + "\"");
						}
						
						hh.addFirst(new AbstractHttpHeader(ss[0], ss[1].trim()) {
							
							private static final long serialVersionUID = 6247686805721708870L;
						});
						
						aa.clear();
					}
				}
			}
		}
		
		return new HttpHeaderListParser(hh, ll);
	}
	
	private static List<String> parseQSortStringList(CharSequence fieldValue) throws HttpServerMessageHeaderParseException {
		
		final List<AbstractQValueComparable<String>> ll = new ArrayList<>();
		
		if ( fieldValue != null ) {
			
			String[] pp = fieldValue.toString().trim().split(",");
			
			QQ_LOOP:
			for ( String p : pp ) {
				
				String[] vv = p.trim().split(";");
				int vvlength = vv.length;
				
				String type = vv[0].trim();
				
				if ( vvlength >= 2 ) {
					
					for ( int i = 1; i < vvlength; ++i ) {
						
						String[] qq = vv[i].trim().split("=");
						
						if ( qq.length != 2 ) {
							continue;
						}
						
						if ( ! qq[0].trim().equalsIgnoreCase("q") ) {
							continue;
						}
						
						try {
							float q = Float.valueOf(qq[1].trim());
							
							if ( q <= 1.0F && q >= 0.0F ) {
								
								ll.add(getQInner(type, q));
								
								continue QQ_LOOP;
								
							} else {
								
								throw new HttpServerMessageHeaderParseException("q= require between 0.0 and 1.0");
							}
						}
						catch ( NumberFormatException e ) {
							throw new HttpServerMessageHeaderParseException(e);
						}
					}
				}
				
				ll.add(getQInner(type));
			}
		}
		
		return ll.stream()
				.sorted()
				.map(l -> l.value())
				.collect(Collectors.toList());
	}
	
	private static <T> AbstractQValueComparable<T> getQInner(T v, float q) {
		return new AbstractQValueComparable<T>(v, q) {};
	}
	
	private static <T> AbstractQValueComparable<T> getQInner(T v) {
		return new AbstractQValueComparable<T>(v) {};
	}
	
}
