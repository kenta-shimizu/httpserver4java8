package com.shimizukenta.httpserver;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * This interface is HttpServer-Log.
 * 
 * <p>
 * To get log-subject, {@link #subject()}.<br />
 * To get log-timestamp, {@link #timestamp()}.<br />
 * To get log-detail-information, {@link #value()}.<bt />
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public interface HttpServerLog {
	
	/**
	 * Returns Log-Subject.
	 * 
	 * @return Log-Subject
	 */
	public String subject();
	
	/**
	 * Returns Log-timestamp.
	 * 
	 * @return Log-Timestamp
	 */
	public LocalDateTime timestamp();
	
	/**
	 * Returns detail-information-object if exist.
	 * 
	 * @return detail-information-object if exist
	 */
	public Optional<Object> value();
	
	/**
	 * Returns detail-information-object-String if exist.
	 * 
	 * @return detail-information-object-String if exist
	 */
	public Optional<String> optionalValueString();
	
}
