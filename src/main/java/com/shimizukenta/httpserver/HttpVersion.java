package com.shimizukenta.httpserver;

public enum HttpVersion {
	
	UNKNOWN("HTTP/UNKNOWN"),
	
	HTTP_1_0("HTTP/1.0"),
	HTTP_1_1("HTTP/1.1"),
	HTTP_2_0("HTTP/2.0"),
	
	;
	
	private String versionStr;
	
	private HttpVersion(String verStr) {
		this.versionStr = verStr;
	}
	
	@Override
	public String toString() {
		return this.versionStr;
	}
	
	public static HttpVersion from(CharSequence cs) {
		
		if ( cs != null ) {
			
			String s = cs.toString().trim();
			
			for ( HttpVersion v : values() ) {
				if ( v.versionStr.equals(s) ) {
					return v;
				}
			}
		}
		
		return UNKNOWN;
	}
	
}
