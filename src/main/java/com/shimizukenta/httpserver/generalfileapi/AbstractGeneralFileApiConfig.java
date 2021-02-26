package com.shimizukenta.httpserver.generalfileapi;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import com.shimizukenta.httpserver.AbstractHttpApiConfig;

public abstract class AbstractGeneralFileApiConfig extends AbstractHttpApiConfig {
	
	private static final long serialVersionUID = 8721613546884682431L;
	
	private Path rootPath;
	
	public AbstractGeneralFileApiConfig() {
		super();
		
		this.rootPath = null;
	}
	
	/**
	 * Returns Root-Path if exist.
	 * 
	 * @return Root-Path if exist
	 */
	public Optional<Path> rootPath() {
		synchronized ( this ) {
			return this.rootPath == null ? Optional.empty() : Optional.of(this.rootPath);
		}
	}
	
	/**
	 * Root-Path setter.
	 * 
	 * @param path
	 */
	public void rootPath(Path path) {
		synchronized ( this ) {
			this.rootPath = path;
		}
	}
	
	private final List<String> directoryIndexes = new CopyOnWriteArrayList<>();
	
	/**
	 * Returns Directory-Indexes.
	 * 
	 * @return Directory-Indexes
	 */
	public List<String> directoryIndexes() {
		return Collections.unmodifiableList(this.directoryIndexes);
	}
	
	/**
	 * Add Directory-Index-File.
	 * 
	 * @param file
	 * @return {@code true} if add success
	 */
	public boolean addDirectoryIndex(CharSequence file) {
		return directoryIndexes.add(Objects.requireNonNull(file).toString());
	}
	
	/**
	 * Remove Directory-Index-File.
	 * 
	 * @param file
	 * @return {@code true} if remove success
	 */
	public boolean removeDirectoryIndex(CharSequence file) {
		return directoryIndexes.remove(Objects.requireNonNull(file).toString());
	}
	
}
