package com.shimizukenta.httpserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Optional;

public abstract class AbstractHttpServerThrowableLog extends AbstractHttpServerLog implements HttpServerThrowableLog {
	
	private static final long serialVersionUID = 4834873090609885026L;
	
	private static final String commonSubject = "Exception";
	
	private final Throwable cause;
	
	private String cacheToValueString;
	
	public AbstractHttpServerThrowableLog(Throwable cause, LocalDateTime timestamp) {
		super(commonSubject, timestamp, cause);
		this.cause = cause;
		this.cacheToValueString = null;
	}
	
	public AbstractHttpServerThrowableLog(Throwable cause) {
		super(commonSubject, cause);
		this.cause = cause;
		this.cacheToValueString = null;
	}
	
	@Override
	public Optional<String> optionalValueString() {
		
		synchronized ( this ) {
			
			if ( this.cacheToValueString == null ) {
				
				try (
						StringWriter sw = new StringWriter();
						) {
					
					try (
							PrintWriter pw = new PrintWriter(sw);
							) {
						
						this.cause.printStackTrace(pw);
						pw.flush();
						
						this.cacheToValueString = sw.toString();
					}
				}
				catch ( IOException giveup ) {
					this.cacheToValueString = null;
				}
			}
			
			return this.cacheToValueString == null ? Optional.empty() : Optional.of(this.cacheToValueString);
		}
	}
	
	@Override
	public Throwable getCause() {
		return cause;
	}

}
