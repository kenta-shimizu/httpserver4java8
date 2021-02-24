package com.shimizukenta.httpserver.generalfileapi;

import java.nio.file.Path;
import java.util.Optional;

import com.shimizukenta.httpserver.AbstractHttpApiConfig;

public abstract class AbstractGeneralFileHttpApiConfig extends AbstractHttpApiConfig {
	
	private Path rootPath;
	
	public AbstractGeneralFileHttpApiConfig() {
		super();
		
		this.rootPath = null;
	}
	
	public Optional<Path> rootPath() {
		synchronized ( this ) {
			return this.rootPath == null ? Optional.empty() : Optional.of(this.rootPath);
		}
	}
	
	public void rootPath(Path path) {
		synchronized ( this ) {
			this.rootPath = path;
		}
	}
	
}
