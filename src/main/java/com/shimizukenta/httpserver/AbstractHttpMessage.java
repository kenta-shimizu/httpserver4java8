package com.shimizukenta.httpserver;

public abstract class AbstractHttpMessage implements HttpMessage {
	
	protected static final String CrLfString = "\r\n";
	protected static final byte[] CrLfBytes = new byte[] {(byte)0xD, (byte)0xA};
	
	public AbstractHttpMessage() {
		/* Nothing */
	}
	
}
