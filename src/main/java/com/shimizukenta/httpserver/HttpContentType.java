package com.shimizukenta.httpserver;

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum HttpContentType {
	
	UNKNOWN("application/octet-stream"),
	
	PLAIN("text/plain", "txt"),
	CSV("text/csv", "csv"),
	HTML("text/html", "html", "htm"),
	CSS("text/css", "css"),
	JAVASCRIPT("text/javascript", "js"),
	XML("application/xml; charset=\"UTF-8\"", "xml"),
	JSON("application/json; charset=\"UTF-8\"", "json"),
	JSONP("application/javascript", "jsonp"),
	PDF("application/pdf", "pdf"),
	XHTML("application/xhtml+xml", "xhtml"),
	XLS("application/vnd.ms-excel", "xls"),
	PPT("application/vnd.ms-powerpoint", "ppt"),
	DOC("application/msword", "doc"),
	XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx"),
	DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx"),
	PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation", "pptx"),
	JPEG("image/jpeg", "jpg", "jpeg"),
	PNG("image/png", "png"),
	GIF("image/gif", "gif"),
	BMP("image/bmp", "bmp"),
	TIFF("image/tiff", "tiff", "tif"),
	ICO("image/vnd.microsoft.icon", "ico"),
	ZIP("application/zip", "zip"),
	LZH("application/x-lzh", "lzh"),
	TAR("application/x-tar", "tar"),
	MP3("audio/mpeg", "mp3"),
	MP4("video/mp4", "mp4"),
	MPEG("video/mpeg", "mpeg"),
	AVI("video/x-msvideo", "avi"),
	
	;
	
	private final String typeStr;
	private final Set<String> exts;
	
	private HttpContentType(String typeStr, String... extensions) {
		this.typeStr = typeStr;
		this.exts = Stream.of(extensions).map(String::toLowerCase).collect(Collectors.toSet());
	}
	
	public String type() {
		return typeStr;
	}
	
	@Override
	public String toString() {
		return type();
	}
	
	public static HttpContentType fromFileName(CharSequence cs) {
		if ( cs != null ) {
			final String s = cs.toString().toLowerCase().trim();
			for ( HttpContentType v : values() ) {
				for ( String ext : v.exts ) {
					if ( s.endsWith("." + ext) ) {
						return v;
					}
				}
			}
		}
		return UNKNOWN;
	}
	
	public static HttpContentType fromPath(Path path) {
		if ( path != null ) {
			return fromFileName(path.toString());
		}
		return UNKNOWN;
	}
	
}
