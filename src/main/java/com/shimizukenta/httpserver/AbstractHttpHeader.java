package com.shimizukenta.httpserver;

import java.io.Serializable;

public abstract class AbstractHttpHeader implements HttpHeader, Serializable {
	
	private static final long serialVersionUID = 8192898004788111987L;
	
	private static final String SEPARATOR = ":";
	private static final String SP = " ";
	
	private final String name;
	private final String value;
	private String line;
	
	public AbstractHttpHeader(String name, String value) {
		this.name = name;
		this.value = value;
		this.line = null;
	}
	
	@Override
	public String fieldName() {
		return this.name;
	}

	@Override
	public String fieldValue() {
		return this.value;
	}

	@Override
	public String toLine() {
		synchronized ( this ) {
			if ( this.line == null ) {
				this.line = this.name + SEPARATOR + SP + this.value;
			}
			return this.line;
		}
	}
}
