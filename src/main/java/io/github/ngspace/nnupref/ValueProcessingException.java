package io.github.ngspace.nnupref;

/**
 * Occurs when an IValueProcessor fails to process an object or read a byte array.
 */
public class ValueProcessingException extends RuntimeException {
	
	/**
	 * constructs the Exception
	 * @param value - the value
	 * @param line - the line
	 */
	public ValueProcessingException(String value, int line) {
		super("Failed to process line #" + line + ": " + value);
	}
	/**
	 * constructs the Exception
	 * @param e - the original exception
	 * @param value - the value
	 * @param line - the line
	 */
	public ValueProcessingException(Exception e, String value, int line) {
		super("Failed to process line #" + line + ": " + value, e);
	}

	private static final long serialVersionUID = -8687211009622518203L;
	
}
