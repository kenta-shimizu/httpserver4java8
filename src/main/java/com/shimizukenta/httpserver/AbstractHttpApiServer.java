package com.shimizukenta.httpserver;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractHttpApiServer extends AbstractHttpServer implements HttpApiServer {
	
	public AbstractHttpApiServer(AbstractHttpApiServerConfig config) {
		super(config);
	}
	
	
	private final Collection<HttpApi> apis = new CopyOnWriteArrayList<>();
	
	@Override
	public boolean addApi(HttpApi api) {
		return apis.add(api);
	}
	
	@Override
	public boolean removeApi(HttpApi api) {
		return apis.remove(api);
	}
	
	@Override
	public HttpResponseMessage receiveRequest(
			HttpRequestMessage request,
			HttpConnectionValue connectionValue,
			HttpServerConfig serverConfig)
					throws InterruptedException, HttpServerException {
		
		for ( HttpApi api : apis ) {
			if ( api.accept(request) ) {
				return api.receiveRequest(request, connectionValue, serverConfig);
			}
		}
		
		return HttpResponseMessage.build(request, HttpResponseCode.InternalServerError);
	}
	

}
