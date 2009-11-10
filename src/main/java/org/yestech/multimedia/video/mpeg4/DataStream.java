/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.multimedia.video.mpeg4;

import java.io.IOException;
import java.io.EOFException;
import java.io.InputStream;

/**
 * <code>DataStream</code>
 */
public final class DataStream {
	
	/** The MPEG4 input stream. */
	private InputStream is;
	/** The current offset (position) in the stream. */
	private long offset = 0;
	
	/**
	 * Constructs an <code>DataStream</code> object using the specified MPEG4 input stream.
	 * @param is the MPEG4 input stream.
	 */
	public DataStream(InputStream is) {
		super();
		this.is = is;
	}

	public long readBytes(int n) throws IOException {
		int c = -1;
		long result = 0;
		while((n-- > 0) && ((c = is.read()) != -1)) {
			result <<= 8;
			result += c & 0xff;
			offset ++;
		}
		if(c == -1) 
			throw new EOFException();
		return result;
	}

	public String readString(int n) throws IOException {
		char c = (char)-1;
		StringBuffer sb = new StringBuffer();
		while((n-- > 0) && ((c = (char)is.read()) != -1)) {
			sb.append(c);
			offset ++;
		}
		if(c == -1) {
			throw new EOFException();
		}
		return sb.toString();
	}
	
	public void skipBytes(long n) throws IOException {
		offset += n;
		is.skip(n);
	}

	public long getOffset() {
		return offset;
	}

}
