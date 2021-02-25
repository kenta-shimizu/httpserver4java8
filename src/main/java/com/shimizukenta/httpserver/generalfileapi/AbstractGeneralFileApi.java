package com.shimizukenta.httpserver.generalfileapi;

import com.shimizukenta.httpserver.AbstractHttpApi;
import com.shimizukenta.httpserver.HttpConnectionValue;
import com.shimizukenta.httpserver.HttpRequestMessage;
import com.shimizukenta.httpserver.HttpResponseMessage;
import com.shimizukenta.httpserver.HttpServerConfig;
import com.shimizukenta.httpserver.HttpServerException;

public abstract class AbstractGeneralFileApi extends AbstractHttpApi implements GeneralFileApi {
	
	private final AbstractGeneralFileApiConfig config;
	
	public AbstractGeneralFileApi(AbstractGeneralFileApiConfig config) {
		super(config);
		this.config = config;
	}

	@Override
	public boolean accept(HttpRequestMessage request) {
		return request.uri().startsWith("/");
	}

	@Override
	public HttpResponseMessage receiveRequest(
			HttpRequestMessage request,
			HttpConnectionValue connectionValue,
			HttpServerConfig serverConfig)
					throws InterruptedException, HttpServerException {
		
		// TODO Auto-generated method stub
		
		return null;
	}

}
