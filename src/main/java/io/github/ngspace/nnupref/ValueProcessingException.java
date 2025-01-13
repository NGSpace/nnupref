package io.github.ngspace.nnupref;

public class ValueProcessingException extends RuntimeException {

	public ValueProcessingException(String value, int line) {
		super("Failed to process line #" + line + ": " + value);
	}

	private static final long serialVersionUID = -8687211009622518203L;
	
}
