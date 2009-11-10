/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.multimedia.video.mpeg4.audio;

import javazoom.jl.decoder.*;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

//import javazoom.jlme.decoder.BitStream;
//import javazoom.jlme.decoder.Decoder;
//import javazoom.jlme.decoder.Header;
//import javazoom.jlme.decoder.SampleBuffer;

/**
 * The <code>MP3AudioPlayer</code> class realizes an audio player that plays the MP3 audio binary stream.
 * It uses the external MP3 library to decode the audio binary stream into the array of audio samples, 
 * which plays through the available audio device (Java2 Sound API or Java1 compatible audio device).
 */
public final class MP3AudioPlayer extends AudioPlayer implements Runnable {
	
	/** The input audio binary stream. */	
	private Bitstream bitstream;

	/**
	 * Constructs an <code>MP3AudioPlayer</code> object using specified audio data input stream. 
	 * @param is audio data input stream.
	 * @throws Exception raises if there is an error occurs 
	 * (in most cases if no output audio devices have been found).
	 */
	public MP3AudioPlayer(InputStream is) throws Exception {
		super();
		bitstream = new Bitstream(is);
		audioPlayerThread = new Thread(this, "Audio Player Thread");
		audioPlayerThread.start();
	}
	
	/**
	 * Decodes the audio binary stream using the external MP3 library into the array of audio samples,
	 * which plays through the available audio device.
	 */
	public void run() {
        throw new UnsupportedOperationException("Not Yet Ported...");
//		try {
//			Header header = null;
//			Decoder decoder = null;
//			while((audioPlayerThread != null) && ((header = bitstream.readFrame()) != null)) {
//				if(decoder == null) {
//					decoder = new Decoder(header, bitstream);
//					System.out.println("Audio: MPEG " + ((header.version() == Header.MPEG1) ? "1" : "2") + " LAYER " + header.layer() + ' ' + header.frequency() + " kHz " + Header.bitrate_str[header.version()][header.layer() - 1][header.bitrate_index()] + " " + header.mode_string());
//				}
//				SampleBuffer sampleBuffer = decoder.decodeFrame();
//				if(! audioDevice.isOpened()) {
//					audioDevice.open(sampleBuffer.getSampleFrequency(), sampleBuffer.getChannelCount());
//				}
//				if(!readyToPlay && audioDevice.isReady()) {
//					synchronized(this) {
//						readyToPlay = true;
//						notifyAll();
//					}
//				}
//				if(sampleBuffer.getBufferLength() > 0) {
//					audioDevice.write(sampleBuffer.getBuffer(), sampleBuffer.getBufferLength());
//				}
//				bitstream.closeFrame();
//			}
//		} catch (InterruptedIOException ioex) {
//		} catch (EOFException ex) {
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		} catch (DecoderException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (BitstreamException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } finally {
//			decoding = false;
//			readyToPlay = true;
//			audioPlayerThread = null;
//		}
//		System.out.println("Audio Stream has terminated");
    }
}
