package org.xiwang.fcl;

import java.io.PrintStream;

public class NoOpPrintStream extends PrintStream {
	
	public NoOpPrintStream() {
		super(new NoOpOutputStream());
	}
	
}