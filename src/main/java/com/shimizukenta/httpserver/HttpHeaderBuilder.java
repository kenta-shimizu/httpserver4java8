package com.shimizukenta.httpserver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HttpHeaderBuilder {

	private HttpHeaderBuilder() {
		/* Nothing */
	}
	
	private static class SingletonHolder {
		private static final HttpHeaderBuilder inst = new HttpHeaderBuilder();
	}
	
	public static HttpHeaderBuilder getInstance() {
		return SingletonHolder.inst;
	}
	
	
	public HttpHeader header(CharSequence name, CharSequence value) {
		return new AbstractHttpHeader(name, value) {
			private static final long serialVersionUID = 1L;
		};
	}
	
	private final ZoneId gmtZoneId = ZoneId.of("GMT");
	
	public String nowZonedDateTime() {
		return ZonedDateTime.now(gmtZoneId).format(DateTimeFormatter.RFC_1123_DATE_TIME);
	}
	
	public String filePathZonedDateTime(Path filePath) throws IOException {
		FileTime ft = Files.getLastModifiedTime(filePath);
		ZonedDateTime z = ZonedDateTime.ofInstant(ft.toInstant(), gmtZoneId);
		return z.format(DateTimeFormatter.RFC_1123_DATE_TIME);
	}
	
	public HttpHeader date() {
		return header("Date", nowZonedDateTime());
	}
	
	public HttpHeader server(HttpServerConfig config) {
		return header("Server", config.serverName());
	}
	
	public HttpHeader lastModified(String zdtStr) {
		return header("Last-Modified", zdtStr);
	}
	
	private final HttpHeader pragmeNoCacheHeader = header("Pragma", "no-cache");
	private final HttpHeader noCacheControlHeader = header("Cache-Control", "no-cache");
	
	public List<HttpHeader> noCache(HttpRequestMessage request) {
		
		switch ( request.version() ) {
		case HTTP_1_0: {
			
			return Collections.singletonList(pragmeNoCacheHeader);
			/* break */
		}
		case HTTP_1_1:
		case HTTP_2_0: {
			
			return Collections.singletonList(noCacheControlHeader);
			/* break */
		}
		default: {
			
			return Collections.emptyList();
		}
		}
	}
	
	private final HttpHeader acceptRangesHeader = header("Accept-Ranges", "bytes");
	
	public HttpHeader acceptRanges() {
		return acceptRangesHeader;
	}
	
	public HttpHeader contentType(HttpContentType type) {
		return header("Content-Type", type.type());
	}
	
	public HttpHeader contentLength(int length) {
		return header("Content-Length", String.valueOf(length));
	}
	
	public HttpHeader contentLength(byte[] bs) {
		return contentLength(bs.length);
	}
	
	public HttpHeader contentEncoding(HttpEncoding enc) {
		return header("Content-Encoding", enc.toString());
	}
	
	private final HttpHeader connectionCloseHeader = header("Connection", "close");
	private final HttpHeader connectionKeepAliveHeader = header("Connection", "Keep-Alive");
	
	public List<HttpHeader> connectionKeeyAlive(
			HttpRequestMessage request,
			HttpConnectionValue connectionValue) {
		
		switch ( request.version() ) {
		case HTTP_1_1: {
			
			if ( ! request.headerListParser().isConnectionClose() ) {
				int max = connectionValue.decreaseKeepAliveMax();
				if ( max > 0 ) {
					return Arrays.asList(
							connectionKeepAliveHeader,
							header(
									"Keep-Alive",
									"timeout="
									+ connectionValue.keepAliveTimeout()
									+ ", max="
									+ max
									)
							);
				}
			}
			
			return Collections.singletonList(connectionCloseHeader);
			/* break; */
		}
		case HTTP_2_0:
		case HTTP_1_0:
		default: {
			return Collections.emptyList();
		}
		}
	}


}
