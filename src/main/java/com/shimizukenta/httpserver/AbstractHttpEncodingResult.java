package com.shimizukenta.httpserver;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

public abstract class AbstractHttpEncodingResult implements HttpEncodingResult, Serializable {
	
	private static final long serialVersionUID = -689560339327132875L;
	
	private final Optional<HttpEncoding> enc;
	private final byte[] body;
	private final int length;
	
	public AbstractHttpEncodingResult(HttpEncoding enc, byte[] body) {
		this.enc = (enc == null ? Optional.empty() : Optional.of(enc));
		this.length = body.length;
		this.body = Arrays.copyOf(body, this.length);
	}
	
	@Override
	public Optional<HttpEncoding> optionalEncoding() {
		return this.enc;
	}
	
	@Override
	public byte[] getBytes() {
		return Arrays.copyOf(this.body, this.length);
	}
	
	@Override
	public int length() {
		return this.length;
	}
	
}
