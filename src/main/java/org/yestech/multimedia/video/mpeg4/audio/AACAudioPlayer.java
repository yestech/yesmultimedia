/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.multimedia.video.mpeg4.audio;

import java.io.EOFException;
import java.io.InputStream;
import java.io.InterruptedIOException;

import org.yestech.multimedia.video.mpeg4.audio.AAC.BitStream;
import org.yestech.multimedia.video.mpeg4.audio.AAC.AACDecoder;

/**
 * The <code>AACAudioPlayer</code> class realizes an audio player that plays the AAC audio binary stream.
 * It uses the external AAC library to decode the audio binary stream into the array of audio samples, 
 * which plays through the available audio device (Java2 Sound API or Java1 compatible audio device).
 */
public final class AACAudioPlayer extends AudioPlayer implements Runnable {
	
	/** Constant, the size of the buffer for audio samples. */	
	private final static int BUFFER_SIZE = 15000;
	
	/** The input audio binary stream. */	
	private BitStream bitstream;

	/**
	 * Constructs an <code>AACAudioPlayer</code> object using specified audio data input stream
	 * and the size of the audio header. 
	 * @param is audio data input stream.
	 * @param audioHeaderSize the size of the audio header.
	 * @throws Exception raises if there is an error occurs 
	 * (in most cases if no output audio devices have been found).
	 */
	public AACAudioPlayer(InputStream is, int audioHeaderSize) throws Exception {
		super();
		bitstream = new BitStream(is, audioHeaderSize);
		audioPlayerThread = new Thread(this, "Audio Player Thread");
		audioPlayerThread.start();
	}
	
	/**
	 * Decodes the audio binary stream using the external AAC library into the array of audio samples,
	 * which plays through the available audio device.
	 */
	public void run() { 
		try {
			byte[] buf = new byte[BUFFER_SIZE];
			AACDecoder decoder = null;
			while(audioPlayerThread != null) {
				if(decoder == null) {
					decoder = new AACDecoder(bitstream);
					System.out.println("Audio: MPEG AAC " + decoder.getAudioProfile() + ' ' + 
						decoder.getSampleFrequency() + " kHz " + 
						(decoder.getChannelCount() == 1 ? "Mono" :  (decoder.getChannelCount() == 2 ? "Stereo" : decoder.getChannelCount() + " Channels")));
				}
				if(! audioDevice.isOpened()) {
					audioDevice.open(decoder.getSampleFrequency(), decoder.getChannelCount());
				}
				if(!readyToPlay && audioDevice.isReady()) {
					synchronized(this) {
						readyToPlay = true;
						notifyAll();
					}
				}
				int bufSize = decoder.decodeFrame(buf);
				if(bufSize > 0) {
					audioDevice.write(buf, bufSize);
				}
			}
		} catch (InterruptedIOException ioex) {
		} catch (EOFException ex) {
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			decoding = false; 
			readyToPlay = true;
			audioPlayerThread = null; 
		}
		System.out.println("Audio Stream terminated.");
	}
	
}
