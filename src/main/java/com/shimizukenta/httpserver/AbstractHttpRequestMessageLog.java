package com.shimizukenta.httpserver;

import java.time.LocalDateTime;
import java.util.Optional;

public abstract class AbstractHttpRequestMessageLog extends AbstractHttpServerLog implements HttpRequestMessageLog {
	
	private static final long serialVersionUID = 8267980988164063088L;
	
	private static final String commonSubject = "Receive-Request";
	
	private final HttpRequestMessage request;
	
	public AbstractHttpRequestMessageLog(LocalDateTime timestamp, HttpRequestMessage request) {
		super(commonSubject, timestamp, request);
		this.request = request;
	}
	
	public AbstractHttpRequestMessageLog(HttpRequestMessage request) {
		super(commonSubject, request);
		this.request = request;
	}
	
	@Override
	public String subject() {
		return commonSubject;
	}
	
	@Override
	public Optional<String> optionalValueString() {
		return Optional.of(this.request.toString());
	}
	
	@Override
	public HttpRequestMessage requestMessage() {
		return this.request;
	}
	
}
