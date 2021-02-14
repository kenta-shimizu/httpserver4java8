package com.shimizukenta.httpserver;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractHttpServerLog implements HttpServerLog, Serializable {
	
	private static final long serialVersionUID = 1362365294426354919L;
	
	private final String subject;
	private final LocalDateTime timestamp;
	private final Object value;
	
	private String cacheToString;
	
	public AbstractHttpServerLog(CharSequence subject, LocalDateTime timestamp, Object value) {
		this.subject = Objects.requireNonNull(subject).toString();
		this.timestamp = Objects.requireNonNull(timestamp);
		this.value = value;
		this.cacheToString = null;
	}
	
	public AbstractHttpServerLog(CharSequence subject, Object value) {
		this(subject, LocalDateTime.now(), value);
	}
	
	public AbstractHttpServerLog(CharSequence subject, LocalDateTime timestamp) {
		this(subject, timestamp, null);
	}
	
	public AbstractHttpServerLog(CharSequence subject) {
		this(subject, LocalDateTime.now(), null);
	}
	
	@Override
	public String subject() {
		return this.subject;
	}

	@Override
	public LocalDateTime timestamp() {
		return timestamp;
	}

	@Override
	public Optional<Object> value() {
		return value == null ? Optional.empty() : Optional.of(value);
	}

	@Override
	public Optional<String> optionalValueString() {
		return value().map(Object::toString);
	}
	
	private static final String BR = System.lineSeparator();
	private static final String SPACE = "  ";
	private static DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
	
	@Override
	public String toString() {
		
		synchronized ( this ) {
			
			if ( this.cacheToString == null ) {
				
				final StringBuilder sb = new StringBuilder()
						.append(timestamp().format(DATETIME))
						.append(SPACE)
						.append(subject());
				
				optionalValueString().ifPresent(v -> {
					sb.append(BR).append(v);
				});
				
				this.cacheToString = sb.toString();
			}
			
			return this.cacheToString;
		}
	}

}
