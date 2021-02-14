package com.shimizukenta.httpserver;

/**
 * This interface is HTTP-Message-Header.
 * 
 * <p>
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public interface HttpHeader {
	
	/**
	 * Returns Field-Name.
	 * 
	 * @return Field-Name
	 */
	public String fieldName();
	
	/**
	 * Returns Field-Value.
	 * 
	 * @return Field-Value
	 */
	public String fieldValue();
	
	/**
	 * Returns Header-Line.
	 * 
	 * <p>
	 * <strong>Not</strong> include CRLF.<br />
	 * </p>
	 * 
	 * @return Header-Line
	 */
	public String toLine();
	
}
