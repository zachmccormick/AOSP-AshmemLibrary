package edu.vanderbilt.mccormick.ashmemlibrary;

import java.io.FileDescriptor;

public class AshmemTool {

	static {
		System.loadLibrary("AshmemLibrary");
	}

	// returns file descriptor
	protected static native int allocateMemory(String name, int pages);

	protected static native FileDescriptor getFileDescriptor(int fd);

	protected static native int getPageSize();

	protected static native byte readByte(int fd, int pages, int offset);

	protected static native byte[] readBytes(int fd, int pages, int offset,
			int length);

	protected static native void writeByte(int fd, int pages, int offset, byte b);

	protected static native void writeBytes(int fd, int pages, int offset,
			byte[] b);
}
