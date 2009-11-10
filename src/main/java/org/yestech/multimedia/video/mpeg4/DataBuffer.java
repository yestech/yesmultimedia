/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.multimedia.video.mpeg4;

import java.io.InputStream;
import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * DataBuffer
 */
public final class DataBuffer extends InputStream implements Runnable {
	
	private InputStream is;
	
	private Thread bufferThread = null;
	private byte[] movieData = null;
	private int readed;
	private int writed;
	private int movieLength;
	private int wait_for_data;
	private FramePumpMPEG4 applet;
	private int initMaxBufferSize;
	private int minBufferSize;
	private int maxBufferSize;
	private int lastPrintedBufferPercent;
	private int startBufferPosition;
	private boolean buffering;
	private boolean reBuffering;
	private boolean encryptedStream;
	private int encryption_index;
	private byte[] encryption_key;
	
	public DataBuffer(FramePumpMPEG4 applet, InputStream is, int movieLength, int maxBufferSize, boolean encryptedStream) {
		this.applet = applet;
		this.is = is;
		this.encryption_index = this.readed = this.writed = this.wait_for_data =  
								this.lastPrintedBufferPercent = this.startBufferPosition = 0;  
		this.movieLength = movieLength;
		this.initMaxBufferSize = this.maxBufferSize = maxBufferSize;
		this.minBufferSize = maxBufferSize / 5;
		this.encryptedStream = encryptedStream;
		buffering = true;
		reBuffering = false;
		movieData = new byte[movieLength];
		if(encryptedStream) {
			try {
				encryption_key = (byte[])Class.forName("framepump.mpeg4.LicenseManager").getMethod(
					"getEncryptionKey", new Class[0]).invoke(null, new Object[0]);
			} catch (Throwable ex) {
				this.encryptedStream = false;
			}
		}
		bufferThread = new Thread(this, "Buffer Thread");
		bufferThread.start();
	}
	
	/**
	 * Stops the buffering of the movie stream.
	 */	
	public synchronized void stop() {
		if(bufferThread != null) {
			Thread workThread = bufferThread; 
			bufferThread = null;
			workThread.interrupt();
		}
		try {
			super.close();
		} catch (Exception ex) {
		}
	}
	
	public void run() {
		try {
			int c = 0;
			while((bufferThread != null) && ((c = is.read()) != -1)) {
				if(encryptedStream) {
					c ^= encryption_key[encryption_index ++];
					if(encryption_index == encryption_key.length) {
						encryption_index = 0;					
					}
				}
				synchronized(this) {
					movieData[writed++] = (byte)(c - 128);
					if(wait_for_data > 0) {
						notifyAll();
					}
				}
				if(buffering || reBuffering) {
					if(writed >= maxBufferSize) {
//						System.out.println("Stop buffering"); 
//						System.out.println("buffer size:" + writed); 
						applet.printBufferPercent(100);
						if(reBuffering) {
							reBuffering = false;
							applet.stopReBuffering();
						} else {
							buffering = false;
							applet.stopBuffering();
						}
					} else {
						int bufferPercent = (100 * (writed - startBufferPosition)) / initMaxBufferSize;
						if(lastPrintedBufferPercent < bufferPercent) { 
							applet.printBufferPercent(lastPrintedBufferPercent = bufferPercent);
						}
					}
				}				
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			movieLength = writed;
			try {
				if(is != null) {
					is.close();
					is = null;
				}
			} catch (Exception ex) {
			}
			bufferThread = null;
			try {
				if(buffering || reBuffering) {
					if(reBuffering) {
						reBuffering = false;
						applet.stopReBuffering();
					} else {
						buffering = false;
						applet.stopBuffering();
					}
				}
			} catch (Exception ex) {
			}
			synchronized(this) {
				if(wait_for_data > 0) {
					notifyAll();
				}
			}
		}
	}

	public long skip(long n) throws IOException {
		long skipped = 0;
		for(int i = 0 ; (i < n) && (read() != -1); i++) {
		}
		return skipped;
	}
	
	public synchronized int read() throws IOException {
		if(readed >= movieLength) {
			return -1;
		}
		while(readed >= writed) {
			wait_for_writer();
		}
		return movieData[readed++] + 128;		
	}

	public synchronized int read(int position, int channelType) throws IOException {
		if(position >= movieLength) {
			return -1;
		}
		while((position >= writed) && (position < movieLength)) {
			wait_for_writer();
		}
		if((channelType == DataChannel.VIDEO_CHANNEL) && (buffering == false) && (reBuffering == false)) {
			if((writed != movieLength) && ((writed - position) <= minBufferSize)) {
				reBuffering = true;
				applet.startReBuffering();
				applet.printBufferPercent(lastPrintedBufferPercent = 0);
				startBufferPosition = writed;
				maxBufferSize = initMaxBufferSize + writed;
//				System.out.println("Start re-buffering"); 
//				System.out.println("buffer size:" + writed); 
			}			
		}
		return movieData[position] + 128;		
	}
	
	private final synchronized void wait_for_writer() throws IOException {
		wait_for_data ++;
		try { 
			wait();
		} catch (InterruptedException ie) {
			throw new InterruptedIOException(ie.getMessage());
		} finally {
			wait_for_data --;
		}
	}	
}
