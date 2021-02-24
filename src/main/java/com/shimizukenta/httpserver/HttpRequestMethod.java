package com.shimizukenta.httpserver;

public enum HttpRequestMethod {
	
	UNKNOWN("UNKNOWN"),
	
	GET("GET"),
	HEAD("HEAD"),
	POST("POST"),
	OPTIONS("OPTIONS"),
	PUT("PUT"),
	DELETE("DELETE"),
	TRACE("TRACE"),
	CONNECT("CONNECT"),
	
	LINK("LINK"),
	UNLINK("UNLINK"),
	
	;
	
	private String methodStr;
	
	private HttpRequestMethod(String method) {
		this.methodStr = method;
	}
	
	@Override
	public String toString() {
		return this.methodStr;
	}
	
	public static HttpRequestMethod from(CharSequence cs) {
		
		if ( cs != null ) {
			
			String s = cs.toString().trim();
			
			for ( HttpRequestMethod v : values() ) {
				if ( v.methodStr.equals(s) ) {
					return v;
				}
			}
		}
		
		return UNKNOWN;
	}
}
