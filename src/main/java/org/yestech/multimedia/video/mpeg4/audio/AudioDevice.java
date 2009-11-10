/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.multimedia.video.mpeg4.audio;

import java.io.InterruptedIOException;

/**
 * The <code>AudioDevice</code> interface defines methods which allows to 
 * start, pause/continue, stop the playback of the audio stream. 
 * They also allows to control the mute state and the volume of the playback and to check  
 * the current status of the Audio Device (if it is ready for playback and if it is opened). 
 */
public interface AudioDevice {

	/**
	 * Starts to play the audio stream. 
	 */
	public void play();

	/**
	 * Opens an output audio device and initializes it with the specified sample frequency 
	 * and the number of channels of the input audio stream.
	 * @param sampleFrequency the sample frequence of the audio stream.
	 * @param channelCount the number of channels of the audio stream.
	 */
	public void open(int sampleFrequency, int channelCount);
	
	/**
	 * Writes the next portion of audio samples into the audio device.
	 * @param buffer the array with the audio samples' data.
	 * @param size the size of the audio samples' data.
	 * @throws InterruptedIOException raises if the current thread has been interrupted.
	 */
	public void write(byte[] buffer, int size) throws InterruptedIOException;

	/**
	 * Pauses the playback of the audio stream.
	 */
	public void pause();

	/**
	 * Closes the audio device.
	 */
	public void close();

	/**
	 * Returns <tt>true</tt>, if the audio device is opened, <tt>false</tt> otherwise.
	 */
	public boolean isOpened();

	/**
	 * Returns <tt>true</tt>, if the audio device is ready to play the audio stream, <tt>false</tt> otherwise.
	 */
	public boolean isReady();

	/**
	 * Sets the mute state of the audio device.
	 * @param mute the mute state to set.
	 */	
	public void setMute(boolean mute);

	/**
	 * Sets the volume of the audio stream.
	 * @param volume the volume to set.
	 */	
	public void setVolume(int volume);
}
