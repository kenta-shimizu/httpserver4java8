package com.shimizukenta.httpserver.generalfileapi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.shimizukenta.httpserver.AbstractHttpApi;
import com.shimizukenta.httpserver.AbstractHttpResponseMessage;
import com.shimizukenta.httpserver.AbstractHttpResponseMessageBodyProxy;
import com.shimizukenta.httpserver.HttpConnectionValue;
import com.shimizukenta.httpserver.HttpContentType;
import com.shimizukenta.httpserver.HttpEncodingResult;
import com.shimizukenta.httpserver.HttpHeader;
import com.shimizukenta.httpserver.HttpHeaderBuilder;
import com.shimizukenta.httpserver.HttpHeaderListParser;
import com.shimizukenta.httpserver.HttpRequestMessage;
import com.shimizukenta.httpserver.HttpResponseCode;
import com.shimizukenta.httpserver.HttpResponseMessage;
import com.shimizukenta.httpserver.HttpResponseMessageBodyProxy;
import com.shimizukenta.httpserver.HttpResponseStatusLine;
import com.shimizukenta.httpserver.HttpServerConfig;
import com.shimizukenta.httpserver.HttpServerException;

public abstract class AbstractGeneralFileApi extends AbstractHttpApi implements GeneralFileApi {
	
	private final AbstractGeneralFileApiConfig config;
	
	public AbstractGeneralFileApi(AbstractGeneralFileApiConfig config) {
		super(config);
		this.config = config;
	}

	@Override
	public boolean accept(HttpRequestMessage request) {
		switch (request.method()) {
		case HEAD:
		case GET: {
			return request.uri().startsWith("/");
			/* break; */
		}
		default: {
			return false;
		}
		}
	}

	@Override
	public HttpResponseMessage receiveRequest(
			HttpRequestMessage request,
			HttpConnectionValue connectionValue,
			HttpServerConfig serverConfig)
					throws InterruptedException, HttpServerException {
		
		if ( ! config.rootPath().isPresent() ) {
			return HttpResponseMessage.build(request, HttpResponseCode.InternalServerError);
		}
		
		String absPath = request.absPath();
		
		if ( absPath.contains("..") ) {
			return HttpResponseMessage.build(request, HttpResponseCode.BadRequest);
		}
		
		Path filepath = getFilePath(absPath);
		
		if ( filepath == null ) {
			return HttpResponseMessage.build(request, HttpResponseCode.NotFound);
		}
		
		final HttpResponseMessageBodyProxy bodyProxy;
			
		try {
			byte[] body = Files.readAllBytes(filepath);
			bodyProxy = new AbstractHttpResponseMessageBodyProxy(body) {
				
				private static final long serialVersionUID = -7881961954878933449L;
			};
		}
		catch ( IOException giveup ) {
			return HttpResponseMessage.build(request, HttpResponseCode.NotAllowed);
		}
		
		final HttpEncodingResult encResult;
		
		try {
			encResult = bodyProxy.get(request.headerListParser().acceptEncodings());
		}
		catch ( IOException giveup ) {
			return HttpResponseMessage.build(request, HttpResponseCode.InternalServerError);
		}
		
		final HttpResponseStatusLine statusLine = new HttpResponseStatusLine(
				request.version(),
				HttpResponseCode.OK);
		
		final HttpHeaderBuilder hb = HttpHeaderBuilder.getInstance();
		
		final List<HttpHeader> headers = new ArrayList<>();
		
		headers.add(hb.date());
		headers.add(hb.server(serverConfig));
		
		try {
			headers.add(hb.lastModified(hb.filePathZonedDateTime(filepath)));
		}
		catch ( IOException giveup ) {
			return HttpResponseMessage.build(request, HttpResponseCode.InternalServerError);
		}
		
		encResult.optionalEncoding()
		.map(x -> hb.contentEncoding(x))
		.ifPresent(headers::add);
		
		headers.add(hb.acceptRanges());
		headers.add(hb.contentLength(encResult.length()));
		headers.add(hb.contentType(HttpContentType.fromPath(filepath)));
		headers.addAll(hb.connectionKeeyAlive(request, connectionValue));
		
		return new AbstractHttpResponseMessage(
				statusLine,
				HttpHeaderListParser.of(headers),
				bodyProxy) {
			
					private static final long serialVersionUID = -7641998153414521198L;
		};
		
	}
	
	private Path getFilePath(String absPath) {
		
		Path p = config.rootPath().map(x -> x.resolve("." + absPath)).get().normalize();
		
		if ( Files.isRegularFile(p) ) {
			
			return p;
			
		} else if ( Files.isDirectory(p) ) {
			
			for ( String s : config.directoryIndexes() ) {
				
				Path pp = p.resolve(s);
				
				if ( Files.isRegularFile(pp) ) {
					return pp.normalize();
				}
			}
		}
		
		return null;
	}
	
}
