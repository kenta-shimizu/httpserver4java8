package com.shimizukenta.httpserver.generalfileapi;

import com.shimizukenta.httpserver.AbstractHttpApi;
import com.shimizukenta.httpserver.AbstractHttpApiConfig;
import com.shimizukenta.httpserver.HttpConnectionValue;
import com.shimizukenta.httpserver.HttpRequestMessage;
import com.shimizukenta.httpserver.HttpResponseMessage;
import com.shimizukenta.httpserver.HttpServerException;

public abstract class AbstractGeneralFileHttpApi extends AbstractHttpApi implements GeneralFileHttpApi {
	
	private final AbstractGeneralFileHttpApiConfig config;
	
	public AbstractGeneralFileHttpApi(AbstractGeneralFileHttpApiConfig config) {
		super(config);
		this.config = config;
	}

	@Override
	public boolean accept(HttpRequestMessage request) {
		return true;
	}

	@Override
	public HttpResponseMessage receiveRequest(
			HttpRequestMessage request,
			HttpConnectionValue connectionValue)
					throws InterruptedException, HttpServerException {
		
		// TODO Auto-generated method stub
		
		return null;
	}

}
