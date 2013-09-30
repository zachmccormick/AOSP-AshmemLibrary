package edu.vanderbilt.mccormick.ashmemlibrary;

import java.io.IOException;
import java.util.Random;

import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;

public class AshmemBuffer implements Parcelable {

	public static AshmemBuffer createAshmemBuffer(final int pagesDesired) {
		return new AshmemBuffer(pagesDesired);
	}

	private final int mPages;
	private int mPosition;

	private final int mFileDescriptor;
	private final String mUniqueName;

	public static final int PAGE_SIZE = AshmemTool.getPageSize();

	public static Parcelable.Creator<AshmemBuffer> CREATOR = new Parcelable.Creator<AshmemBuffer>() {

		@Override
		public AshmemBuffer createFromParcel(final Parcel source) {
			final ParcelFileDescriptor pfd = source.readFileDescriptor();
			final int nfd = pfd.getFd();
			final String name = source.readString();
			final int pages = source.readInt();
			final int position = source.readInt();
			if (nfd == -1)
				return null;
			return new AshmemBuffer(name, nfd, pages, position);
		}

		@Override
		public AshmemBuffer[] newArray(final int size) {
			return null;
		}

	};

	private AshmemBuffer(final int pages) {
		final Random r = new Random();
		final char[] chars = new char[8];
		for (int i = 0; i < 8; ++i)
			chars[i] = (char) (r.nextInt(25) + 97);
		mUniqueName = new String(chars);
		mFileDescriptor = AshmemTool.allocateMemory(mUniqueName, pages);
		mPages = pages;
	}

	private AshmemBuffer(final String name, final int fd, final int pages,
			final int position) {
		mUniqueName = name;
		mFileDescriptor = fd;
		mPages = pages;
		mPosition = position;
	}

	public int capacity() {
		return mPages * PAGE_SIZE;
	}

	@Override
	public int describeContents() {
		return Parcelable.CONTENTS_FILE_DESCRIPTOR;
	}

	public int position() {
		return mPosition;
	}

	public byte readByte() throws Exception {
		if (mPosition < mPages * PAGE_SIZE)
			return AshmemTool.readByte(mFileDescriptor, mPages, mPosition++);
		else
			throw new Exception("Tried to read past end of buffer: (size):["
					+ mPages * PAGE_SIZE + "] (position):[" + mPosition + "]");
	}

	public byte[] readBytes(final int length) throws Exception {
		if (mPosition + length - 1 < mPages * PAGE_SIZE) {
			final byte[] ret = AshmemTool.readBytes(mFileDescriptor, mPages,
					mPosition, length);
			mPosition += length;
			return ret;
		} else
			throw new Exception("Tried to read past end of buffer: (size):["
					+ mPages * PAGE_SIZE + "] (position):[" + mPosition + "]");
	}

	public void setPosition(final int position) {
		mPosition = position;
	}

	public void writeByte(final byte b) throws Exception {
		if (mPosition < mPages * PAGE_SIZE)
			AshmemTool.writeByte(mFileDescriptor, mPages, mPosition++, b);
		else
			throw new Exception("Tried to write past end of buffer: (size):["
					+ mPages * PAGE_SIZE + "] (position):[" + mPosition + "]");
	}

	public void writeBytes(final byte[] b) throws Exception {
		if (mPosition + b.length - 1 < mPages * PAGE_SIZE) {
			AshmemTool.writeBytes(mFileDescriptor, mPages, mPosition, b);
			mPosition += b.length;
		} else
			throw new Exception("Tried to write past end of buffer: (size):["
					+ mPages * PAGE_SIZE + "] (position):[" + mPosition + "]");
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		try {
			dest.writeFileDescriptor(ParcelFileDescriptor.fromFd(
					mFileDescriptor).getFileDescriptor());
		} catch (final IOException e) {
			e.printStackTrace();
		}
		dest.writeString(mUniqueName);
		dest.writeInt(mPages);
		dest.writeInt(mPosition);
	}
}
