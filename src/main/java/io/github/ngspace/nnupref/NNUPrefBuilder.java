package io.github.ngspace.nnupref;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * A helper class to help create NNUPref objects
 */
public class NNUPrefBuilder {
	
	private InputStream inputStream = null;
	private File file = null;
	private Map<String, Object> defaults = null;
	private IValueProcessor valueProcessor = new DefaultValueProcessor();
	
	private boolean autosave = false;
	private boolean autocreatemissingfile = false;
	private boolean safesave = false;

	public NNUPrefBuilder(File file) {
		this.file = file;
	}
	
	public NNUPrefBuilder(InputStream is) {
		this.inputStream = is;
	}
	
	public InputStream getInputStream() {
		return inputStream;
	}

	public File getFile() {
		return file;
	}

	public Map<String, Object> getDefaults() {
		return defaults;
	}

	public boolean isAutosave() {
		return autosave;
	}

	public boolean isAutocreatemissingfile() {
		return autocreatemissingfile;
	}

	public IValueProcessor getValueProcessor() {
		return valueProcessor;
	}

	public boolean isSafesave() {
		return safesave;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setDefaults(Map<String, Object> defaults) {
		this.defaults = defaults;
	}

	public void setAutosave(boolean autosave) {
		this.autosave = autosave;
	}

	public void setAutocreatemissingfile(boolean autocreatemissingfile) {
		this.autocreatemissingfile = autocreatemissingfile;
	}

	public void setValueProcessor(IValueProcessor valueProcessor) {
		this.valueProcessor = valueProcessor;
	}

	public void setSafesave(boolean safesave) {
		this.safesave = safesave;
	}

	public NNUPref build() throws IOException {
		return new NNUPref(file, inputStream, defaults, autosave, autocreatemissingfile, valueProcessor, safesave);
	}
}
