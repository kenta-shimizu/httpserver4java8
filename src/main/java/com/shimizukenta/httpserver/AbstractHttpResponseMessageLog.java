package com.shimizukenta.httpserver;

import java.time.LocalDateTime;
import java.util.Optional;

public abstract class AbstractHttpResponseMessageLog extends AbstractHttpServerLog implements HttpResponseMessageLog {
	
	private static final long serialVersionUID = 3913938215296946177L;
	
	private static final String commonSubject = "Send-Response";
	
	private final HttpResponseMessage response;
	
	public AbstractHttpResponseMessageLog(LocalDateTime timestamp, HttpResponseMessage response) {
		super(commonSubject, timestamp, response);
		this.response = response;
	}
	
	public AbstractHttpResponseMessageLog(HttpResponseMessage response) {
		super(commonSubject, response);
		this.response = response;
	}
	
	@Override
	public HttpResponseMessage responseMessage() {
		return this.response;
	}
	
	@Override
	public String subject() {
		return commonSubject;
	}
	
	@Override
	public Optional<String> optionalValueString() {
		return Optional.of(this.response.toString());
	}
	
}
