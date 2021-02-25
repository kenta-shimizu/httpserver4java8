package com.shimizukenta.httpserver;

import java.util.Optional;

public interface HttpEncodingResult {
	
	/**
	 * Returns Encoding-bytes.
	 * 
	 * @return Encoding-bytes
	 */
	public byte[] getBytes();
	
	/**
	 * Returns Encoding if encoded.
	 * 
	 * @return Encoding if encoded
	 */
	public Optional<HttpEncoding> contentEncoding();
	
}
