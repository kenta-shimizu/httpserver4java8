package com.shimizukenta.httpserver;

import java.util.Optional;

public interface HttpEncodingResult {
	
	/**
	 * Returns Original-bytes.
	 * 
	 * @return Original-bytes
	 */
	public byte[] originalBytes();
	
	/**
	 * Returns Encoding-bytes.
	 * 
	 * @return Encoding-bytes
	 */
	public byte[] compressedBytes();
	
	/**
	 * Returns Encoding if encoded.
	 * 
	 * @return Encoding if encoded
	 */
	public Optional<HttpEncoding> contentEncoding();
	
	/**
	 * Returns {@code true} if encoded.
	 * 
	 * @return {@code true} if encoded
	 */
	public boolean encoded();
	
	/**
	 * Returns Encoding-Bytes if encoded, Original-bytes otherwise.
	 * 
	 * @return Encoding-Bytes if encoded, Original-bytes otherwise
	 */
	public byte[] getBytes();
	
	/**
	 * Returns Encoding-Bytes-length if encoded, Original-bytes-length otherwise.
	 * 
	 * @return Encoding-Bytes-length if encoded, Original-bytes-length otherwise
	 */
	public int length();
	
}
