package com.shimizukenta.httpserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

public class HttpContentEncoder {

	protected HttpContentEncoder() {
		/* Nothing */
	}
	
	private static class SingletonHolder {
		
		private static final HttpContentEncoder inst = new HttpContentEncoder();
		
		private static final HttpEncodingResult empty = new AbstractHttpEncodingResult(
				new byte[0],
				new byte[0],
				null) {
			
			private static final long serialVersionUID = 3148020411528616296L;
		};
		
	}
	
	public static HttpEncodingResult encode(
			HttpRequestMessage request,
			byte[] body) {
		
		return SingletonHolder.inst.protoEncode(request, body);
	}
	
	public static HttpEncodingResult empty() {
		return SingletonHolder.empty;
	}
	
	protected HttpEncodingResult protoEncode(
			HttpRequestMessage request,
			byte[] body) {
		
		try {
			List<HttpEncoding> encs = request.headerListParser().acceptEncodings();
			
			for ( HttpEncoding enc : encs ) {
				
				switch ( enc ) {
				case GZIP: {
					
					return createEncodingResult(
							body,
							createGZIPBytes(body),
							HttpEncoding.GZIP
							);
					/* break */
				}
				case DEFLATE: {
					
					return createEncodingResult(
							body,
							createDeflateBytes(body),
							HttpEncoding.DEFLATE
							);
					/* break; */
				}
				case X_GZIP: {
					
					return createEncodingResult(
							body,
							createGZIPBytes(body),
							HttpEncoding.X_GZIP
							);
					/* break; */
				}
				case X_COMPRESS: {
					
					return createEncodingResult(
							body,
							createDeflateBytes(body),
							HttpEncoding.X_COMPRESS
							);
					/* break; */
				}
				default: {
					
					/* Nothing */
				}
				}
				
			}
		}
		catch ( IOException giveup ) {
		}
		catch ( HttpServerMessageHeaderParseException giveup ) {
		}
		
		return empty();
	}
	
	private static HttpEncodingResult createEncodingResult(
			byte[] originalBytes,
			byte[] encodeBytes,
			HttpEncoding enc) throws IOException {
		
		return new AbstractHttpEncodingResult(
				originalBytes,
				encodeBytes,
				HttpEncoding.GZIP
				) {
			
					private static final long serialVersionUID = 1L;
		};
	}
	
	protected HttpEncodingResult encodeGzip(byte[] body) throws IOException {
		
		return new AbstractHttpEncodingResult(
				body,
				createGZIPBytes(body),
				HttpEncoding.GZIP
				) {
			
					private static final long serialVersionUID = -432653517316209997L;
		};
	}
	
	private static final int ByteArrayOutputStreamSize = 256 * 64;
	
	private static byte[] createGZIPBytes(byte[] bs) throws IOException {
		
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
	
}
