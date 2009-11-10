/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.multimedia.video.mpeg4;

/**
 * DataSample
 */
public final class DataSample {
	protected long offset;
	protected int size;
	
	public DataSample(long offset, int size) {
		super();
		this.offset = offset;
		this.size = size;
	}

	public long getOffset() {
		return offset;
	}

	public int getSize() {
		return size;
	}

}
