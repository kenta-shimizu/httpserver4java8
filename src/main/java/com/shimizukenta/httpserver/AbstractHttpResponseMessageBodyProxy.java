package com.shimizukenta.httpserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

public abstract class AbstractHttpResponseMessageBodyProxy implements HttpResponseMessageBodyProxy, Serializable {
	
	private static final long serialVersionUID = 3671363655248842453L;
	
	private final byte[] body;
	
	private byte[] cacheGZip;
	private byte[] cacheDeflate;
	
	public AbstractHttpResponseMessageBodyProxy(byte[] body) {
		this.body = Arrays.copyOf(body, body.length);
		this.cacheGZip = null;
		this.cacheDeflate = null;
	}
	
	@Override
	public HttpEncodingResult get(List<HttpEncoding> acceptEncodings) throws IOException {
		
		if ( this.body.length > 0 ) {
			
			for ( HttpEncoding enc : acceptEncodings ) {
				
				switch ( enc ) {
				case GZIP: {
					
					return createEncodingResult(HttpEncoding.GZIP, getGZipBytes());
					/* break; */
				}
				case DEFLATE: {
					
					return createEncodingResult(HttpEncoding.DEFLATE, getDeflateBytes());
					/* break; */
				}
				case X_COMPRESS: {
					
					return createEncodingResult(HttpEncoding.X_COMPRESS, getDeflateBytes());
								/* break; */
				}
				case X_GZIP: {
					
					return createEncodingResult(HttpEncoding.X_GZIP, getGZipBytes());
					/* break; */
				}
				case IDENTITY: {
					
					return getIdentity();
					/* break; */
				}
				case COMPRESS:
				case UNKNOWN:
				default: {
					
					/* Nothing */
				}
				}
			}
		}
		
		return getIdentity();
	}
	
	@Override
	public HttpEncodingResult get(HttpEncoding encoding) throws IOException {
		return get(Collections.singletonList(encoding));
	}
	
	private HttpEncodingResult getIdentity() {
		return createEncodingResult(null, this.body);
	}
	
	private byte[] getGZipBytes() throws IOException {
		
		synchronized ( this ) {
			
			if ( this.cacheGZip == null ) {
				
				this.cacheGZip = createGZipBytes(this.body);
			}
			
			return this.cacheGZip;
		}
	}
	
	private byte[] getDeflateBytes() throws IOException {
		
		synchronized ( this ) {
			
			if ( this.cacheDeflate == null ) {
				
				this.cacheDeflate = createDeflateBytes(this.body);
			}
			
			return this.cacheDeflate;
		}
	}
	
	private static final int ByteArrayOutputStreamSize = 256 * 64;
	
	private static byte[] createGZipBytes(byte[] bs) throws IOException {
		
		try (
				ByteArrayOutputStream baos = new ByteArrayOutputStream(ByteArrayOutputStreamSize);
				) {
			
			try (
					GZIPOutputStream gzipos = new GZIPOutputStream(baos);
					) {
				
				gzipos.write(bs);
			}
			
			return baos.toByteArray();
		}

	}
	
	private static byte[] createDeflateBytes(byte[] bs) throws IOException {
		
		final Deflater comp = new Deflater();
		
		try {
			
			comp.setInput(bs);
			comp.finish();
			
			byte[] buffer = new byte[4096];
			
			try (
					ByteArrayOutputStream baos = new ByteArrayOutputStream(ByteArrayOutputStreamSize);
					) {
				
				for ( ;; ) {
					
					int len = comp.deflate(buffer);
					
					if ( len > 0 ) {
						
						baos.write(buffer, 0, len);
						
					} else {
						
						return baos.toByteArray();
					}
				}
			}
		}
		finally {
			comp.end();
		}
	}
	
	private static HttpEncodingResult createEncodingResult(HttpEncoding enc, byte[] bs) {
		
		return new AbstractHttpEncodingResult(enc, bs) {
			
			private static final long serialVersionUID = 2216585808657140161L;
		};
	}
	
}
