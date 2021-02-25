package com.shimizukenta.httpserver;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractHttpApi implements HttpApi {
	
//	private AbstractHttpApiConfig config;
	
	public AbstractHttpApi(AbstractHttpApiConfig config) {
//		this.config = config;
	}
	
	protected static HttpHeader header(CharSequence name, CharSequence value) {
		return new AbstractHttpHeader(name, value) {
			
			private static final long serialVersionUID = -4563258496785621860L;
		};
	}
	
	private static final ZoneId gmtZoneId = ZoneId.of("GMT");
	
	protected static final String nowZonedDateTime() {
		return ZonedDateTime.now(gmtZoneId).format(DateTimeFormatter.RFC_1123_DATE_TIME);
	}
	
	protected static HttpHeader date() {
		return header("Date", nowZonedDateTime());
	}
	
	protected static HttpHeader acceptRanges() {
		return header("Accept-Ranges", "bytes");
	}
	
	protected static HttpHeader contentLength(byte[] bs) {
		return header("Content-Length", String.valueOf(bs.length));
	}
	
	protected static HttpHeader contentEncoding(HttpEncoding enc) {
		return header("Content-encoding", enc.toString());
	}
	
	private static final HttpHeader connectionCloseHeader = header("Connection", "close");
	private static final HttpHeader connectionKeepAliveHeader = header("Connection", "Keep-Alive");
	
	protected static List<HttpHeader> connectionKeeyAlive(
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
