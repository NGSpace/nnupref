package io.github.ngspace.nnupref;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * A helper class to help create and preconfigure NNUPref
 */
public class NNUPrefBuilder {
	
	private InputStream inputStream = null;
	private File file = null;
	private IValueProcessor valueProcessor = new DefaultValueProcessor();
	
	private boolean autosave = false;
	private boolean autocreatemissingfile = false;
	private boolean safesave = false;
	private boolean typechecking = true;

	/**
	 * Creates a new NNUPrefBuilder instance
	 * @param file - the default File
	 */
	public NNUPrefBuilder(File file) {
		this.file = file;
	}
	
	/**
	 * Creates a new NNUPrefBuilder instance
	 * @param is - the default InputStream
	 */
	public NNUPrefBuilder(InputStream is) {
		this.inputStream = is;
	}
	
	/**
	 * The InputStream to be read automatically when constructing the NNUPref object using {@link #build()}
	 * defaults to null
	 * @return ^
	 */
	public InputStream getInputStream() {
		return inputStream;
	}
	
	/**
	 * The File to be read automatically when constructing the NNUPref object using {@link #build()}
	 * defaults to null
	 * @return ^
	 */
	public File getFile() {
		return file;
	}
	
	/**
	 * Should automatically save to file when a value is changed
	 * defaults to false
	 * @return ^
	 */
	public boolean shouldAutoSave() {
		return autosave;
	}
	
	/**
	 * Should auto-create a missing npref file when constructing the NNUPref Object
	 * defaults to false
	 * @return ^
	 */
	public boolean shouldAutoCreateMissingFile() {
		return autocreatemissingfile;
	}
	
	/**
	 * The IValueProcessor used to construct and deconstruct objects to be read from and written to memory
	 * defaults to DefaultValueProcessor
	 * @return ^
	 */
	public IValueProcessor getValueProcessor() {
		return valueProcessor;
	}

	/**
	 * Should use a save method that does not overwrites the npref file without first writting all the objects to disk
	 * defaults to false
	 * @return ^
	 */
	public boolean shouldSafeSave() {
		return safesave;
	}
	
	/**
	 * Should check if the IValueProcessor instance is compatible with the objects set in the NNUPref object
	 * defaults to true
	 * @return ^
	 */
	public boolean doesTypeChecking() {
		return typechecking;
	}
	/**
	 * Should check if the IValueProcessor instance is compatible with the objects set in the NNUPref object
	 * defaults to true
	 * @param typechecking ^
	 */
	public void setTypeChecking(boolean typechecking) {
		this.typechecking = typechecking;
	}
	
	/**
	 * The InputStream to be read automatically when constructing the NNUPref object using {@link #build()}
	 * defaults to null
	 * @param inputStream ^
	 */
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	/**
	 * The File to be read automatically when constructing the NNUPref object using {@link #build()}
	 * defaults to null
	 * @param file ^
	 */
	public void setFile(File file) {
		this.file = file;
	}
	
	/**
	 * Should automatically save to file when a value is changed
	 * defaults to false
	 * @param autosave ^
	 */
	public void setAutoSave(boolean autosave) {
		this.autosave = autosave;
	}
	
	/**
	 * Should auto-create a missing npref file when constructing the NNUPref Object
	 * defaults to false
	 * @param autocreatemissingfile ^
	 */
	public void setAutoCreateMissingFile(boolean autocreatemissingfile) {
		this.autocreatemissingfile = autocreatemissingfile;
	}
	
	/**
	 * The IValueProcessor used to construct and deconstruct objects to be read from and written to memory
	 * defaults to DefaultValueProcessor
	 * @param valueProcessor ^
	 */
	public void setValueProcessor(IValueProcessor valueProcessor) {
		this.valueProcessor = valueProcessor;
	}
	
	/**
	 * Should use a save method that does not overwrites the npref file without first writting all the objects to disk
	 * defaults to false
	 * @param safesave ^
	 */
	public void setSafeSave(boolean safesave) {
		this.safesave = safesave;
	}
	
	/**
	 * Constructs the NNUPref object with the given config
	 * @return a new NNUPref object
	 * @throws IOException if fails to construct object
	 */
	public NNUPref build() throws IOException {
		return new NNUPref(file, inputStream, autosave, autocreatemissingfile, valueProcessor, safesave, typechecking);
	}
}
