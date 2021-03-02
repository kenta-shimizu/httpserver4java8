package com.shimizukenta.httpserver;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

public abstract class AbstractHttpEncodingResult implements HttpEncodingResult, Serializable {
	
	private static final long serialVersionUID = 5193261535071119103L;
	
	private final byte[] originalBytes;
	private final byte[] encBytes;
	private final HttpEncoding enc;
	
	public AbstractHttpEncodingResult(byte[] originalBytes, byte[] encBytes, HttpEncoding enc) {
		this.originalBytes = Arrays.copyOf(originalBytes, originalBytes.length);
		this.encBytes = Arrays.copyOf(encBytes, encBytes.length);
		this.enc = enc;
	}
	
	@Override
	public byte[] originalBytes() {
		return Arrays.copyOf(originalBytes, originalBytes.length);
	}
	
	@Override
	public byte[] compressedBytes() {
		return Arrays.copyOf(encBytes, encBytes.length);
	}
	
	@Override
	public Optional<HttpEncoding> contentEncoding() {
		return this.enc == null ? Optional.empty() : Optional.of(this.enc);
	}
	
	@Override
	public boolean encoded() {
		return this.enc != null;
	}
	
	@Override
	public byte[] getBytes() {
		return encoded() ? compressedBytes() : originalBytes();
	}
	
	@Override
	public int length() {
		return getBytes().length;
	}
	
}
