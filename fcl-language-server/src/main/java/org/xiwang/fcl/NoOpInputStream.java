package org.xiwang.fcl;

import java.io.IOException;
import java.io.InputStream;

public class NoOpInputStream extends InputStream {
	
	@Override
	public int read() throws IOException {
		return -1;
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		return -1;
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return -1;
	}
	
	@Override
	public int available() throws IOException {
		return 0;
	}
	
	@Override
	public synchronized void mark(int readlimit) {
	}
	
	@Override
	public boolean markSupported() {
		return true;
	}
	
	@Override
	public synchronized void reset() throws IOException {
	}
	
	@Override
	public long skip(long n) throws IOException {
		return 0;
	}
	
	@Override
	public void close() throws IOException {
	}
	
}