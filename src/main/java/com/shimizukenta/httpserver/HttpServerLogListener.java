package com.shimizukenta.httpserver;

import java.util.EventListener;

public interface HttpServerLogListener extends EventListener {
	
	public void received(HttpServerLog log);
}
