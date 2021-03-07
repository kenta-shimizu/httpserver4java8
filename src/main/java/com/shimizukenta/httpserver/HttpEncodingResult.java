package com.shimizukenta.httpserver;

import java.util.Optional;

public interface HttpEncodingResult {
	
	/**
	 * Returns HttpEncoding if exist.
	 * 
	 * @return HttpEncoding if exist
	 */
	public Optional<HttpEncoding> optionalEncoding();
	
	/**
	 * Returns encoded bytes.
	 * 
	 * @return encoded bytes
	 */
	public byte[] getBytes();
	
	/**
	 * Returns bytes length.
	 * 
	 * @return bytes length
	 */
	public int length();
	
}
