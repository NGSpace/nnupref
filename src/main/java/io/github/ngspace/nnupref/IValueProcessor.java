package io.github.ngspace.nnupref;

import java.io.IOException;

public interface IValueProcessor {
	public Object readValue(byte[] value, int line) throws ValueProcessingException;
	public byte[] writeValue(Object value) throws ValueProcessingException, IOException;
}
