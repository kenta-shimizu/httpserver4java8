package com.shimizukenta.httpserver;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public enum HttpEncoding {
	
	UNKNOWN("unknown"),
	
	GZIP("gzip"),
	COMPRESS("compress"),
	DEFLATE("deflate"),
	
	IDENTITY("identity"),
	
	X_COMPRESS("x-compress"),
	X_GZIP("x-gzip"),
	
	;
	
	private final String enc;
	
	private HttpEncoding(String enc) {
		this.enc = enc;
	}
	
	@Override
	public String toString() {
		return this.enc;
	}
	
	/**
	 * Returns HttpEncoding.
	 * 
	 * @param cs
	 * @return HttpENcoding
	 */
	public static HttpEncoding get(CharSequence cs) {
		
		if ( cs != null ) {
			String s = cs.toString();
			for ( HttpEncoding v : values() ) {
				if ( v.enc.equalsIgnoreCase(s) ) {
					return v;
				}
			}
		}
		
		return UNKNOWN;
	}
	
	
	/**
	 * Returns HttpEncoding q-Sorted-List.
	 * 
	 * @param fieldValue
	 * @return HttpEncoding q-Sorted-List
	 */
	public static List<HttpEncoding> fromFieldValue(CharSequence fieldValue) throws HttpServerMessageHeaderParseException {
		
		final List<AbstractQValueComparable<HttpEncoding>> inners = new ArrayList<>();
		
		if ( fieldValue != null ) {
			
			String[] pp = fieldValue.toString().split(",");
			
			QQ_LOOP:
			for ( String p : pp ) {
				
				String[] vv = p.trim().split(";");
				int vvlength = vv.length;
				
				HttpEncoding enc = HttpEncoding.get(vv[0].trim());
				
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
								
								inners.add(getQInner(enc, q));
								
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
				
				inners.add(getQInner(enc));
			}
		}
		
		return inners.stream()
				.filter(i -> i.value() != HttpEncoding.UNKNOWN)
				.sorted()
				.map(i -> i.value())
				.collect(Collectors.toList());
	}
	
	private static AbstractQValueComparable<HttpEncoding> getQInner(HttpEncoding enc, float q) {
		return new AbstractQValueComparable<HttpEncoding>(enc, q) {};
	}
	
	private static AbstractQValueComparable<HttpEncoding> getQInner(HttpEncoding enc) {
		return new AbstractQValueComparable<HttpEncoding>(enc) {};
	}

}

