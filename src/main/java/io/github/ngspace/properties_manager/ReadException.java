package io.github.ngspace.properties_manager;

import java.io.IOException;

public class ReadException extends IOException {
	private static final long serialVersionUID = 6607957682950815858L;
	public final int line;
	protected ReadException(int line) {super();this.line=line;}
}
