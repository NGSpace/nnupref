package io.github.ngspace.nnupref;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * A helper class to help create NNUPref objects
 */
public class NNUPrefBuilder {
	
	private InputStream inputStream = null;
	private File file = null;
	private IValueProcessor valueProcessor = new DefaultValueProcessor();
	
	private boolean autosave = false;
	private boolean autocreatemissingfile = false;
	private boolean safesave = false;
	private boolean typechecking = true;

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
	
	public boolean doesTypeChecking() {
		return typechecking;
	}
	
	public void setTypeChecking(boolean typechecking) {
		this.typechecking = typechecking;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public void setFile(File file) {
		this.file = file;
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
		return new NNUPref(file, inputStream, autosave, autocreatemissingfile, valueProcessor, safesave, typechecking);
	}
}
