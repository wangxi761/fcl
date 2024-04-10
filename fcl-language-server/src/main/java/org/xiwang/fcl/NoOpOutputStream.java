package org.xiwang.fcl;

import java.io.IOException;
import java.io.OutputStream;

public class NoOpOutputStream extends OutputStream {
	
	@Override
	public void write(int b) throws IOException {
	}
	
	@Override
	public void write(byte[] b, int off, int len) {
	}
	
	@Override
	public void write(byte[] b) throws IOException {
	}
	
}