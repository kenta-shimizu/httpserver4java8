package com.shimizukenta.httpserver;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class HttpContentEncoder {

	protected HttpContentEncoder() {
		/* Nothing */
	}
	
	private static class SingletonHolder {
		private static final HttpContentEncoder inst = new HttpContentEncoder();
	}
	
	public static HttpEncodingResult encode(
			HttpRequestMessage request,
			byte[] body) {
		
		return SingletonHolder.inst.protoEncode(request, body);
	}

	protected HttpEncodingResult protoEncode(
			HttpRequestMessage request,
			byte[] body) {
		
		try {
			List<HttpEncoding> encs = request.headerListParser().acceptEncodings();
			
			for ( HttpEncoding enc : encs ) {
				
				switch ( enc ) {
				case GZIP: {
					return encodeGzip(body);
					/* break */
				}
				case DEFLATE: {
					return encodeDeflate(body);
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
		
		return new HttpEncodingResult() {

			@Override
			public byte[] getBytes() {
				return body;
			}
			
			@Override
			public Optional<HttpEncoding> contentEncoding() {
				return Optional.empty();
			}
		};
	}
	
	protected HttpEncodingResult encodeGzip(byte[] body) throws IOException {
		
		//TODO
		
		return new HttpEncodingResult() {
			
			@Override
			public byte[] getBytes() {
				return body;
			}
			
			@Override
			public Optional<HttpEncoding> contentEncoding() {
				return Optional.empty();
			}
		};
	}
	
	protected HttpEncodingResult encodeDeflate(byte[] body) throws IOException {
		
		//TODO
		
		return new HttpEncodingResult() {
			
			@Override
			public byte[] getBytes() {
				return body;
			}
			
			@Override
			public Optional<HttpEncoding> contentEncoding() {
				return Optional.empty();
			}
		};
	}
	
}
