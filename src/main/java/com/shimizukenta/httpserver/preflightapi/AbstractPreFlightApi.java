package com.shimizukenta.httpserver.preflightapi;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.shimizukenta.httpserver.AbstractHttpApi;
import com.shimizukenta.httpserver.AbstractHttpApiConfig;
import com.shimizukenta.httpserver.HttpHeader;
import com.shimizukenta.httpserver.HttpRequestMessage;
import com.shimizukenta.httpserver.HttpRequestMethod;
import com.shimizukenta.httpserver.HttpServerRequestMessageParseException;

public abstract class AbstractPreFlightApi extends AbstractHttpApi implements PreFlightApi {

	public AbstractPreFlightApi() {
		super();
	}
	
	public AbstractPreFlightApi(AbstractHttpApiConfig config) {
		super(config);
	}
	
	@Override
	public boolean accept(HttpRequestMessage request) {
		
		return request.method() == HttpRequestMethod.OPTIONS
				&& request.headerListParser().optionalValue("Origin").isPresent();
	}
	
	
	protected static HttpHeader accessControlAllowOrigin(HttpRequestMessage request) throws HttpServerRequestMessageParseException {
		return header(
				"Access-Control-Allow-Origin",
				request.headerListParser().optionalValue("Origin")
				.orElseThrow(() -> new HttpServerRequestMessageParseException())
				);
	}
	
	protected static HttpHeader accessControlAllowMethods(CharSequence cs) {
		return header("Access-Control-Allow-Methods", cs);
	}
	
	protected static HttpHeader accessControlAllowMethods(HttpRequestMethod... methods) {
		return accessControlAllowMethods(
				Stream.of(methods).map(m -> m.toString()).collect(Collectors.joining(","))
				);
	}
	
	protected static HttpHeader accessControlAllowCredentialsTrue() {
		return header("Access-Control-Allow-Credentials", "true");
	}
	
}
