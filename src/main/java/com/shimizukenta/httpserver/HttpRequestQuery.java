package com.shimizukenta.httpserver;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public final class HttpRequestQuery {
	
	private final Map<String, List<String>> map;
	
	private HttpRequestQuery( Map<String, List<String>> map ) {
		this.map = map;
	}
	
	public Set<String> keySet() {
		return map.keySet();
	}
	
	public String getValue(CharSequence key) {
		return getValues(key).stream().findFirst().orElse(null);
	}
	
	public List<String> getValues(CharSequence key) {
		if ( key != null ) {
			List<String> ll = map.get(key.toString());
			if ( ll != null ) {
				return Collections.unmodifiableList(ll);
			}
		}
		return Collections.emptyList();
	}
	
	public void forEach(BiConsumer<? super String, ? super List<String>> action) {
		map.forEach(action);
	}
	
	private static final  HttpRequestQuery emptyQuery = new HttpRequestQuery(Collections.emptyMap());
	
	public static HttpRequestQuery from(CharSequence query) throws HttpServerRequestMessageParseException {
		
		if ( query == null ) {
			throw new HttpServerRequestMessageParseException("require not null");
		}
		
		String queryStr = query.toString().trim();
		
		if ( queryStr.isEmpty() ) {
			return emptyQuery;
		}
		
		final Map<String, List<String>> map = new HashMap<>();
		
		String[] ss = queryStr.split("&");
		for ( String s : ss ) {
			String[] pp = s.split("=", 2);
			
			try {
				String key = URLDecoder.decode(pp[0], StandardCharsets.UTF_8.name());
				String v = (pp.length == 2 ? URLDecoder.decode(pp[1], StandardCharsets.UTF_8.name()) : "");
				map.computeIfAbsent(key, k -> new ArrayList<>()).add(v);
			}
			catch ( UnsupportedEncodingException e ) {
				throw new HttpServerRequestMessageParseException(e);
			}
		}
		
		return new HttpRequestQuery(map);
	}
	
}
