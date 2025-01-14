package io.github.ngspace.nnupref;

public class ValueProcessingException extends RuntimeException {

	public ValueProcessingException(String value, int line) {
		super("Failed to process line #" + line + ": " + value);
	}

	public ValueProcessingException(Exception e, String value, int line) {
		super("Failed to process line #" + line + ": " + value, e);
	}

	private static final long serialVersionUID = -8687211009622518203L;
	
}
