package com.shimizukenta.httpserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractHttpRequestChunkMessage extends AbstractHttpRequestMessage {
	
	private static final long serialVersionUID = -5824443783422568323L;
	
	private final List<byte[]> chunkBytes;
	private final byte[] body;
	private final List<HttpHeader> trailers;
	
	private List<byte[]> cacheBytes;
	private HttpHeaderListParser joinHeaderList;
	private String cacheToString;
	
	private AbstractHttpRequestChunkMessage(
			HttpRequestLineParser requestLine,
			HttpHeaderListParser headerList,
			List<byte[]> chunkBytes,
			byte[] body,
			List<HttpHeader> trailers
			) {
		
		super(requestLine, headerList);
		
		this.chunkBytes = chunkBytes.stream()
				.map(bs -> Arrays.copyOf(bs, bs.length))
				.collect(Collectors.toList());
		
		this.body = Arrays.copyOf(body, body.length);
		
		this.trailers = new ArrayList<>(trailers);
		
		this.cacheBytes = null;
		this.joinHeaderList = null;
		this.cacheToString = null;
	}
	
	@Override
	public byte[] body() {
		return Arrays.copyOf(body, body.length);
	}
	
	@Override
	public List<byte[]> getBytes() {
		
		synchronized ( this ) {
			
			if ( this.cacheBytes == null ) {
				
				final List<byte[]> ll = new ArrayList<>();
				
				try (
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						) {
					
					baos.write(requestLine().getBytes(StandardCharsets.US_ASCII));
					baos.write(CrLfBytes);
					
					for (String line : super.headerLines() ) {
						baos.write(line.getBytes(StandardCharsets.US_ASCII));
						baos.write(CrLfBytes);
					}
					
					baos.write(CrLfBytes);
					
					ll.add(baos.toByteArray());
				}
				catch ( IOException giveup ) {
				}
				
				ll.addAll(this.chunkBytes);
				
				this.cacheBytes = Collections.unmodifiableList(ll);
			}
			
			return this.cacheBytes;
		}
	}
	
	@Override
	public HttpHeaderListParser headerListParser() {
		return joinHeaderList();
	}
	
	@Override
	public List<String> headerLines() {
		return joinHeaderList().lines();
	}
	
	@Override
	public List<HttpHeader> headers() {
		return joinHeaderList().headers();
	}
	
	private HttpHeaderListParser joinHeaderList() {
		
		synchronized ( this ) {
			
			if ( this.joinHeaderList == null ) {
				
				List<HttpHeader> ll = new ArrayList<>();
				
				ll.addAll(super.headers());
				ll.addAll(this.trailers);
				
				this.joinHeaderList = HttpHeaderListParser.of(ll);
			}
			
			return this.joinHeaderList;
		}
	}
	
	
	private static final String BR = System.lineSeparator();
	
	@Override
	public String toString() {
		
		synchronized ( this ) {
			
			if ( this.cacheToString == null ) {
				
				StringBuilder sb = new StringBuilder();
				
				sb.append(requestLine());
				super.headerLines().forEach(line -> {
					sb.append(BR).append(line);
				});
				
				this.cacheToString = sb.toString();
			}
			
			return this.cacheToString;
		}
	}
	
	private static final byte CR = (byte)0xD;
	private static final byte LF = (byte)0xA;
	
	public static AbstractHttpRequestChunkMessage build(
			HttpRequestLineParser requestLine,
			HttpHeaderListParser headerList,
			List<byte[]> chunks
			) throws HttpServerRequestMessageParseException {
		
		final InnerQueue q = new InnerQueue(chunks);
		
		try (
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				) {
			
			try (
					ByteArrayOutputStream sizest = new ByteArrayOutputStream();
					) {
				
				for ( ;; ) {
					
					/* read chunk-size */
					for ( ;; ) {
						
						byte b = q.take();
						
						if ( b == LF ) {
							break;
						}
						
						if ( b != CR ) {
							sizest.write(b);
						}
					}
					
					String[] ss = new String(sizest.toByteArray(), StandardCharsets.US_ASCII).split(";", 2);
					
					int size = Integer.parseInt(ss[0], 16);
					
					if ( size == 0 ) {
						break;
					}
					
					sizest.reset();
					
					/* read chunk-data */
					for ( int i = 0; i < size; ++i ) {
						baos.write(q.take());
					}
					
					if ( q.take() != CR ) {
						throw new HttpServerRequestChunkMessageParseException();
					}
					
					if ( q.take() != LF ) {
						throw new HttpServerRequestChunkMessageParseException();
					}
					
				}
			}
			
			final byte[] body = baos.toByteArray();
			baos.reset();
			
			final List<String> trailers = new ArrayList<>();
			
			/* read trailer */
			for ( ;; ) {
				
				byte b = q.take();
				
				if ( b == LF ) {
					
					byte[] bs = baos.toByteArray();
					baos.reset();
					
					if ( bs.length == 0 ) {
						break;
					}
					
					trailers.add(new String(bs, StandardCharsets.US_ASCII));
					
				} else if ( b != CR ) {
					
					baos.write(b);
				}
			}
			
			final HttpHeaderListParser trailerParser = HttpHeaderListParser.fromLines(trailers);
			
			return new AbstractHttpRequestChunkMessage(
					requestLine,
					headerList,
					chunks,
					body,
					trailerParser.headers()) {
				
						private static final long serialVersionUID = 6295457468522351391L;
			};
		}
		catch ( IOException e ) {
			throw new HttpServerRequestChunkMessageParseException(e);
		}
	}
	
	private static class InnerQueue {
		
		private final List<byte[]> ll;
		private int i;
		private int m;
		private int ix;
		private final int mx;
		
		private InnerQueue(List<byte[]> ll) {
			
			this.ll = ll.stream()
					.filter(bs -> bs.length > 0)
					.collect(Collectors.toList());
			
			this.i = -1;
			this.ix = 0;
			this.mx = ll.size();
			
			if ( mx > 0 ) {
				m = ll.get(ix).length;
			} else {
				m = 0;
			}
		}
		
		public byte take() throws HttpServerRequestChunkMessageParseException {
			
			i += 1;
			
			if ( i < m ) {
				
				return (ll.get(ix))[i];
				
			} else {
				
				ix += 1;
				
				if ( ix < mx ) {
					
					i = 0;
					m = ll.get(ix).length;
					
					return (ll.get(ix))[i];
					
				} else {
					
					throw new HttpServerRequestChunkMessageParseException();
				}
			}
		}
		
	}
}
