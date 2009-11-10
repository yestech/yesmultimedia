/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.multimedia.video.mpeg4;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

/**
 * DataChannel
 */
public final class DataChannel extends InputStream {
	
	public final static int VIDEO_CHANNEL = 0;
	public final static int AUDIO_CHANNEL = 1;
	
	private DataBuffer dataBuffer = null;
	private Vector channelSamples = null;
	private int channelSize = 0;
	private int currentSample = 0;
	private int currentSamplePos = 0;
	private DataSample currentDataSample = null;
	
	private int channelType;
	
	public DataChannel (int channelType, DataBuffer dataBuffer, Vector channelSamples) throws Exception {
		super();
		if((channelSamples == null) || (channelSamples.size() == 0)) {
			throw new Exception ("The data channel is empty");
		}
		this.dataBuffer = dataBuffer;
		this.channelSamples = channelSamples;
		this.channelType = channelType;
		for(int i = 0; i < channelSamples.size(); i++) {
			DataSample dataSample = (DataSample)channelSamples.elementAt(i);
			channelSize+= dataSample.getSize(); 
		}
		currentDataSample = (DataSample)channelSamples.elementAt(0);
	}
	
	public synchronized int read() throws IOException {
		if((currentDataSample == null) || (dataBuffer == null)) {
			throw new EOFException();
		}
		while(currentSamplePos == currentDataSample.getSize()) {
			if(currentSample == (channelSamples.size() - 1)) {
				currentDataSample = null;
				return -1;
			}
			currentSamplePos = 0;
			currentDataSample = (DataSample)channelSamples.elementAt(++currentSample);
		}
		return dataBuffer.read((int)(currentDataSample.getOffset() + currentSamplePos++), channelType);
	}	

	public int read(byte[] buf, int off, int len) throws IOException {
		int c, i = 0;
		for(; (i < len) && ((c = read()) != -1); i++) {
			buf[off + i] = (byte)c;
		}
		if((i == 0) && (len > 0)) {
			return -1;
		}
		return i;
	}
	
	public long skip(long n) throws IOException {
		long i = 0;
		for(; (i < n) && (read() != -1); i++) {
		}
		return i;
	}
}
