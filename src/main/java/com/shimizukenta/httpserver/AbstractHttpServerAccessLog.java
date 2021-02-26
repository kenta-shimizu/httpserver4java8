package com.shimizukenta.httpserver;

import java.net.SocketAddress;
import java.util.Optional;

public abstract class AbstractHttpServerAccessLog extends AbstractHttpServerLog implements HttpServerLog {
	
	private static final long serialVersionUID = -7081881685152677188L;
	
	private static final String commonSubject = "Http-Server-Access-Log";
	
	private final HttpServerRequestMessageLog request;
	private final HttpServerResponseMessageLog response;
	private final SocketAddress client;
	private final SocketAddress server;
	
	public AbstractHttpServerAccessLog(
			HttpServerRequestMessageLog request,
			HttpServerResponseMessageLog response,
			SocketAddress client,
			SocketAddress server) {
		
		super(commonSubject, request.timestamp());
		
		this.request = request;
		this.response = response;
		this.client = client;
		this.server = server;
	}
	
	public HttpServerRequestMessageLog requestLog() {
		return this.request;
	}
	
	public HttpServerResponseMessageLog responseLog() {
		return this.response;
	}
	
	public SocketAddress client() {
		return this.client;
	}
	
	public SocketAddress server() {
		return this.server;
	}
	
	private static final String TAB = "  ";
	
	@Override
	public Optional<String> optionalValueString() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("{").append(BR);
		
		if ( this.client != null ) {
			sb.append(TAB)
			.append("\"client\": \"")
			.append(this.client.toString())
			.append("\",")
			.append(BR);
		}
		
		if ( this.server != null ) {
			sb.append(TAB)
			.append("\"server\": \"")
			.append(this.server.toString())
			.append("\",")
			.append(BR);
		}
		
		{
			sb.append(TAB).append("\"request\": {").append(BR);
			
			sb.append(TAB).append(TAB)
			.append("\"timestamp\": \"")
			.append(request.timestamp().format(DATETIME))
			.append("\",").append(BR);
			
			sb.append(TAB).append(TAB)
			.append("\"startLine\": \"")
			.append(request.requestMessage().requestLine())
			.append("\"").append(BR);
			
			sb.append(TAB).append("},").append(BR);
		}
		
		{
			sb.append(TAB).append("\"response\": {").append(BR);
			
			sb.append(TAB).append(TAB)
			.append("\"timestamp\": \"")
			.append(response.timestamp().format(DATETIME))
			.append("\",").append(BR);
			
			sb.append(TAB).append(TAB)
			.append("\"startLine\": \"")
			.append(response.responseMessage().statusLine())
			.append("\"").append(BR);
			
			sb.append(TAB).append("}").append(BR);
		}
		
		sb.append("}");
		
		return Optional.of(sb.toString());
	}
	
}
