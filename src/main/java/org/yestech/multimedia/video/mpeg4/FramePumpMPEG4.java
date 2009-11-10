/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.multimedia.video.mpeg4;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.yestech.multimedia.video.mpeg4.audio.AudioPlayer;
import org.yestech.multimedia.video.mpeg4.isofile.*;
import org.yestech.multimedia.video.mpeg4.video.MPEG4Decoder;
import org.yestech.multimedia.video.mpeg4.video.PanThread;
import org.yestech.multimedia.video.mpeg4.video.VideoFrame;
import org.yestech.multimedia.video.mpeg4.video.BitStream;
import org.yestech.multimedia.video.mpeg4.video.J2Utils;
import org.yestech.multimedia.video.mpeg4.video.ZoomListener;
import org.yestech.multimedia.video.mpeg4.video.ZoomThread;

/**
 * MPEG4 Viewer applet
 */
public class FramePumpMPEG4 extends Applet implements Runnable, MouseListener, ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Constant, the unknown connection speed. */ 
	public final int CONNECTION_SPEED_UNKNOWN = 0;
	/** Constant, the 24 Kbps connection speed. */ 
	public final int CONNECTION_SPEED_24K = 1;  // 1333 ms
	/** Constant, the 44 Kbps connection speed. */ 
	public final int CONNECTION_SPEED_44K = 2;  // 727 ms
	/** Constant, the 128 Kbps connection speed. */ 
	public final int CONNECTION_SPEED_128K = 3; // 250 ms
	/** Constant, the 256 Kbps connection speed. */ 
	public final int CONNECTION_SPEED_256K = 4; // 125 ms
	/** Constant, the 350 Kbps connection speed. */ 
	public final int CONNECTION_SPEED_350K = 5; // 91 ms
	/** Constant, the 500 Kbps connection speed. */ 
	public final int CONNECTION_SPEED_500K = 6; // 64 ms
	/** Constant, the 800 Kbps connection speed. */ 
	public final int CONNECTION_SPEED_800K = 7; // 40 ms
	/** Constant, the internal buffer size for the movie file (8k). */ 
	public final int MOVIE_BUFFER_SIZE = 8192;

	/** The array of movie file name parameters (specified by the connection speed). */  	
	public final String[] CONNECTION_PARAMETERS = {
			"default_media", "24k_media", "44k_media", "128k_media", 
			"256k_media", "350k_media", "500k_media", "800+_media"
	};

	/** The array of connection speed text values (specified by the connection speed). */  	
	public final String[] CONNECTION_TEXT = {
			"unknown", "24", "44", "128", "256", "350", "500", "800" 
	};

	/** The array of connection speed int values (specified by the connection speed). */  	
	public final int[] CONNECTION_VALUES = {
			-1, 24, 44, 128, 256, 350, 500, 800 
	};
	
	/** The array of error messages (specified by the error code). */  	
	public final String[] ERROR_MESSAGES = {
		null,
		"Unable to load the movie file",		
		"The parameter \"default_media\" isn't specified",		
		"Unsupported movie file format", "The license does not exist in the movie file", null, null, null, null, null,
		"FramePump Locked."
	};

	/**
	 * The <code>MovieFrame</code> class implements a custom frame that shows the movie.  
	 */	
	private class MovieFrame extends Frame {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		/** The main applet object. */
		FramePumpMPEG4 applet;

		/** 
		 * Constructs a <code>MovieFrame</code> object.
		 * @param applet the main applet object. 
		 */		
		public MovieFrame(FramePumpMPEG4 applet) {
			super();
			this.applet = applet;
		}

		public void paint(Graphics g) {
			applet.myPaint(g);
		}
		
		public void update(Graphics g) {
			applet.myPaint(g);
		}
		
		public void myPaint() {
			Graphics g = getGraphics();
			applet.myPaint(g);
		}
		
	}
	
	public final static int MANUAL_PLAYBACK = 0;
	public final static int ROLLOWER_TO_PLAY = 1;
	public final static int CLICK_TO_PLAY = 2;
	
	
	public final static int START_STATE   = 0;
	public final static int READY_STATE   = 1;
	public final static int PLAY_STATE    = 2;
	public final static int PAUSE_STATE   = 3;
	public final static int STOP_STATE    = 4;
	public final static int REWIND_STATE  = 5;
	public final static int ERROR_STATE   = 6;
	
	public final static String[] STATE_TEXT = {
		"start", "ready", "playing", "paused", "finished", "finished", "error" 
	};	 
	   

	private volatile int player_state = START_STATE;
	
	private boolean buffering = true;
	private boolean runInApplet = true;
	private boolean firstLoop = true;
		
	private String fileName = null;
	private Vector audioSamples = new Vector();
	private Vector videoSamples = new Vector();
	private AudioPlayer audioPlayer = null;
	private MPEG4Decoder videoDecoder = null;
	private DataBuffer dataBuffer = null;
	private InputStream audioStream = null;
	private BitStream videoStream = null;
	
	private MovieFrame frame = null;
	
	private volatile int connection_speed = -1;
	
	private int movieTime = -1;
	private double video_rate = -1;
	private int movieLength = -1;
	private int movieDuration = -1;
	private int video_size = -1;
	
	private int playback_mode = CLICK_TO_PLAY;
	
	private boolean autoplay = false;
	
	private boolean audio_enabled = true;
	private boolean video_license = false;
	private boolean controls_enabled = true;
	private boolean controls_float = false;
	private String control_location = "control_set/";
	private String detect_location = "detect_speed/";
	private boolean cache = false;
	private boolean feedback_agent = true;
	private boolean playback_loop = false;
	private String sBufferSize = "20%";
	private String post_screen = "/pre_post/post.gif";
	private String post_screen_range = "";
	private int post_screen_delay = 500;
	private boolean post_screen_loop = false;
	private String pre_screen = "/pre_post/pre.gif";
	private String pre_screen_range = "";
	private int pre_screen_delay = 500;
	private boolean pre_screen_loop = false;	
	private boolean smooth_video = true;
	private String address = null;
	private String target = "_blank";
	private String id = "ID: Unknown";
	private boolean licenseIsCorrect = true;

	private boolean allow_save = true; 
	
	private int error_code = 0;    
	private int volume = 100;
	private boolean mute = false; 
	
	private Image[] preScreenImgs;
	private Image[] postScreenImgs;
	
	
	private Image fp_logo = null;
	private volatile boolean show_fp_logo = false;
	
	private MenuItem idMenuItem = new MenuItem(id);
	private MenuItem playMenuItem = new MenuItem("Play");
	private MenuItem muteMenuItem = new MenuItem("mute");

	private MenuItem aboutMenuItem = new MenuItem("About FramePump");
	private MenuItem copyrightMenuItem = new MenuItem("Copyright information");
	
	private String aboutFramePump = "http://www.neocoretechs.com";
	private String copyrightInfo = "http://www.neocoretechs.com";
	/** The url of a web page with the description of the LOCKED error. */
	private String lockedInfo = "http://www.neocoretechs.com/locked.html";

	private PopupMenu popupMenu = new PopupMenu();
	
	private volatile Thread movieParseThread = null;
	
	/*
	public boolean imageUpdate(Image img,
            int infoflags,
            int x,
            int y,
            int w,
            int h) {
		System.out.println("imageUpdateApplet mww="+movie_window_width+" mwh="+movie_window_height+" info="+infoflags+ " x="+x+" y="+y+" w="+w+" h="+h);
		return ( w != movie_window_width || h != movie_window_height);
	}
	*/
	/**
	 * The init applet's routime.
	 * Loads parameter values from the applet's parameters.
	 */
	public void init() {
		movie_window_width = getSize().width;
		movie_window_height = getSize().height;
		audio_enabled = getBooleanParameter("audio", true);
		video_license = getBooleanParameter("video_license", false);
		copyrightInfo = getParameter("copyright_link", copyrightInfo);
		controls_enabled = getBooleanParameter("controls", true);
		if("float".equals(getParameter("controls"))) {
			controls_enabled = controls_float = true;
		} 
		/*
		control_location = getParameter("control_location", control_location);
		if(control_location.length() > 0) {
			char lastChar = control_location.charAt(control_location.length() - 1);
			if(lastChar != '/') {
				control_location = control_location + '/';
			}
		}
		*/
		control_location = getCodeBase().getPath()+"control_set/";
		//System.out.println(control_location);
		detect_location = getParameter("detect_location", detect_location);
		if(detect_location.length() > 0) {
			char lastChar = detect_location.charAt(detect_location.length() - 1);
			if(lastChar != '/') {
				detect_location = detect_location + '/';
			}
		}		
		feedback_agent = getBooleanParameter("feedback_agent", true);
		smooth_video = getBooleanParameter("smooth_video", true);
		playback_loop = getBooleanParameter("loop", false);
		sBufferSize = getParameter("pre_buffer", sBufferSize);
		String value = getParameter("playback");
		playback_mode = MANUAL_PLAYBACK;
		if("click_to_play".equalsIgnoreCase(value)) {
			playback_mode = CLICK_TO_PLAY;
		} else if ("rollower_to_play".equalsIgnoreCase(value)) {
			playback_mode = ROLLOWER_TO_PLAY;
		} else if ("auto_start".equalsIgnoreCase(value) || "autoplay".equalsIgnoreCase(value)) {
			autoplay = true;
		}
		id = getParameter("id", id);
		address = getParameter("address", address);
		target = getParameter("target", target);

		if(address != null) {
			// small bugfix of urls that don't contain the protocol in the address 
			if(address.startsWith("www.")) {
				address = "http://" + address;
			} else if(address.startsWith("ftp.")) {
				address = "ftp://" + address;
			}
		}

		pre_screen = getParameter("pre_screen", pre_screen);
		pre_screen_range = getParameter("pre_screen_range", pre_screen_range);
		pre_screen_loop = getBooleanParameter("pre_screen_loop", false);
		try {
			int iValue = Integer.parseInt(getParameter("pre_screen_delay", Integer.toString(pre_screen_delay)));
			if(iValue > 20) {
				pre_screen_delay = iValue;
			}
		} catch (Exception ex) {}

		post_screen = getParameter("post_screen", post_screen);
		post_screen_range = getParameter("post_screen_range", post_screen_range);
		post_screen_loop = getBooleanParameter("post_screen_loop", false);
		try {
			int iValue = Integer.parseInt(getParameter("post_screen_delay", Integer.toString(post_screen_delay)));
			if(iValue > 20) {
				post_screen_delay = iValue;
			}
		} catch (Exception ex) {}

		cache = getBooleanParameter("cache", false);
		playersetvolume(getParameter("startup_volume", "100%"));
		

		preScreenImgs = loadScreenImages(pre_screen, pre_screen_range);
		postScreenImgs = loadScreenImages(post_screen, post_screen_range);

		if(audio_enabled && (!AudioPlayer.isSoundEnabled())) {
			audio_enabled = false;
			mute = true;
		}
		addMouseListener(this);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		createPopupMenu();
		add(popupMenu);
		try {
			Class.forName("java.awt.Graphics2D");
			java2_platform = true;
		} catch (Exception ex) {
		}
		windows_platform = "\\".equals(System.getProperty("file.separator"));
		linux_platform = "Linux".equalsIgnoreCase(System.getProperty("os.name"));

		if(video_license) {
			licenseIsCorrect = false;
		}
		try {
			scaleMediaTracker.waitForAll();
		} catch (Exception ex) {
		}

		Image mf_image = getEmbeddedImage("/framepump/mpeg4/fp_logo.gif");
		if(mf_image != null) {
			fp_logo = createImage(movie_window_width, movie_window_height);
			Graphics imageGr = fp_logo.getGraphics();
			imageGr.setColor(new Color(51, 51, 51));
			imageGr.fillRect(0, 0, movie_window_width, movie_window_height);
			imageGr.drawImage(mf_image, (movie_window_width - mf_image.getWidth(null))/ 2, (movie_window_height - mf_image.getHeight(null)) / 2, null);
			show_fp_logo = true;
		}

	}
	
	private void createPopupMenu() {
		popupMenu.add(idMenuItem);
		popupMenu.addSeparator();
		popupMenu.add(playMenuItem);
		popupMenu.add(muteMenuItem);
		popupMenu.addSeparator();
	
		popupMenu.addSeparator();
		popupMenu.add(aboutMenuItem);
		popupMenu.add(copyrightMenuItem);
		setMenuState();
		playMenuItem.addActionListener(this);
		muteMenuItem.addActionListener(this);

		aboutMenuItem.addActionListener(this);
		copyrightMenuItem.addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == playMenuItem) {
			if(player_state == PLAY_STATE) {
				playerpause();
			} else if((player_state == READY_STATE) || (player_state == PAUSE_STATE)) {
				playerplay();
			} else if(player_state == STOP_STATE) {
				playerrewind();
				playerplay();
			}			
		} else if(e.getSource() == muteMenuItem) {
			playersetmute(!mute);
		} else if(e.getSource() == aboutMenuItem) {
			showDocument(aboutFramePump, "_blank");
		} else if(e.getSource() == copyrightMenuItem) {
			showDocument(copyrightInfo, "_blank");
		}
		setMenuState();
	}
	
	private void setMenuState() {
		idMenuItem.setLabel(id);
		if(!audio_enabled) {
			mute = true;
		}
		if(mute) {
			muteMenuItem.setLabel("un-mute");
		} else {
			muteMenuItem.setLabel("mute");
		}

		muteMenuItem.setEnabled(audio_enabled);		

		if(player_state == PLAY_STATE) {
			playMenuItem.setLabel("Pause");
		} else {
			playMenuItem.setLabel("Play");
		}
		playMenuItem.setEnabled((buffering == false) && ((player_state == STOP_STATE) || ((player_state == READY_STATE) && (autoplay == false)) || (player_state == PLAY_STATE) || (player_state == PAUSE_STATE)));

	}
	
	private String getParameter(String parameterName, String defaultValue) {
		String value = getParameter(parameterName);
		if(value == null) {
			return defaultValue;
		}
		return value;
	}
	
	/**
	 * Parses applet's parameter with the specified name as a boolean value and returns <tt>true</tt> 
	 * if it equals <tt>yes</tt> or <tt>true</tt>, and <tt>false</tt> if it 
	 * equals <tt>no</tt> or <tt>false</tt>.
	 * If parameter has been set to another value or hasn't been specified (null value), 
	 * returns <code>defaultValue</code>.
	 * @param parameter the name of applet's parameter to parse.
	 * @param defaultValue the default value of the parameter.
	 * @return the boolean value of the specified parameter. 
	 */
	private boolean getBooleanParameter(String parameter, boolean defaultValue) {
		String value = getParameter(parameter);
		if(value == null) {
			return defaultValue;
		}
		if(value.equalsIgnoreCase("true") || (value.equalsIgnoreCase("yes"))) {
			return true;
		}
		if(value.equalsIgnoreCase("false") || (value.equalsIgnoreCase("no"))) {
			return false;
		}
		return defaultValue;
	}
	
	/**
	 * The start applet's routime.
	 */
	public void start() {
		Thread appletThread = new Thread(this, "Applet_Thread");
		appletThread.start();

	}
	
	public void run() {
		String thread_name = Thread.currentThread().getName(); 
		System.out.println("Thread "+thread_name);
		if("Applet_Thread".equals(thread_name)) {
			JSCallback.startCallBack(this);
			if(connection_speed == -1) {
				connection_speed = detectBandwidth();
			}
			fileName = getParameter(CONNECTION_PARAMETERS[connection_speed]);
			if(fileName == null) {
				fileName = getParameter("default_media");
				if(fileName == null) {
					error_code = 2;
					player_state = ERROR_STATE;
					myPaint();
					throw new RuntimeException(ERROR_MESSAGES[2]);
				}
			}
			loadFile(fileName);

			if(dataBuffer != null) {
				movieParseThread = Thread.currentThread(); 
				parseFile();
				if(movieParseThread == Thread.currentThread()) {
					if(error_code == 0) {
						playerstart();
						// start playing auto JG
						playerplay();
						System.out.println("State: "+player_state);
					}
					movieParseThread = null;
				}
			}
		} else if ("Pause_Thread".equals(thread_name)){
			try {				
				long sleep_time = 1050 - (System.currentTimeMillis() - feedback_start_showing_time);
				if(sleep_time > 0) { 
					Thread.sleep(sleep_time);
				}
				myPaint();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if("Bandwidth Detecting Thread".equals(thread_name)) {
			myPaint();
			try {
				long sleep_time = 0;
				while(bandwidth_detecting) {
					if(detect_start_showing_time > 0) {
						long start_pause_time = System.currentTimeMillis();
						Thread.sleep(100);
						detect_start_showing_time += System.currentTimeMillis() - start_pause_time;
						sleep_time += System.currentTimeMillis() - start_pause_time;
					} else {
						Thread.sleep(100);
					}
				}
				sleep_time = 1010 - (sleep_time + System.currentTimeMillis() - this.detect_start_showing_time);
				detect_start_showing_time = System.currentTimeMillis();
				if(sleep_time > 0) { 
					Thread.sleep(sleep_time);
				}
				detect_start_showing_time = System.currentTimeMillis() - 1010;
				myPaint();
				sleep_time = 2010 - (System.currentTimeMillis() - this.detect_start_showing_time);
				if(sleep_time > 0) { 
					Thread.sleep(sleep_time);
				}
				myPaint();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			try {
				long paintTime = System.currentTimeMillis() - start_screen_paint_time;
				if(Thread.currentThread() == pre_screen_loop_thread) {
					while(Thread.currentThread() == pre_screen_loop_thread) {
						if(show_fp_logo && (pre_screen_image_index == -1)) {
							Thread.sleep(3000 - paintTime);
							show_fp_logo = false;
						} else {
							Thread.sleep(pre_screen_delay - paintTime);
						}
						long startPaintTime = System.currentTimeMillis();
						myPaint();
						paintTime = System.currentTimeMillis() - startPaintTime;
					}
				} else if(Thread.currentThread() == post_screen_loop_thread) {
					while(Thread.currentThread() == post_screen_loop_thread) {
						Thread.sleep(post_screen_delay - paintTime);
						long startPaintTime = System.currentTimeMillis();
						myPaint();
						paintTime = System.currentTimeMillis() - startPaintTime;
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}
	
	public void stop() {
		playerstop();
		Thread workThread = movieParseThread; 
		movieParseThread = null;
		if(workThread != null) {
			workThread.interrupt();
			try {
				workThread.join(2000);
			} catch (Exception ex) {
			}
			workThread = null;
		}
		try {
			if(dataBuffer != null) {
				dataBuffer.stop();
			}
		} catch (Exception ex) {
		} finally {
			dataBuffer = null;			
		}
	}
	
	private void loadFile(String fileName) {
		movieTime = -1;

		try {
			URL url = null;	
			URLConnection uc = null; 
			if(runInApplet) {
				URL codebase = getCodeBase();
				url = new URL(codebase, fileName);
			} else {
				url = new URL("file://" + new File(fileName).getAbsolutePath());
			}
			System.out.println("Loading "+url);
			uc = url.openConnection();
			uc.setUseCaches(cache);
			InputStream is = null;
			boolean encryptedStream = false;
			if(fileName.endsWith(".zip")) {
				// movie file stored in the zip file - try to load it
				try {
					is = new ZipInputStream(new BufferedInputStream(uc.getInputStream(), MOVIE_BUFFER_SIZE));
					ZipEntry ze = ((ZipInputStream)is).getNextEntry();  
					movieLength = (int)ze.getSize();
				} catch (Exception ex) {
					is = null;
				}
			}
			if(is == null) {
				movieLength = uc.getContentLength();
				is = new BufferedInputStream(uc.getInputStream(), MOVIE_BUFFER_SIZE);
			}
			if(video_license) {
				encryptedStream = licenseIsCorrect = false;
				try {
					Class[] param_classes = new Class[1];
					param_classes[0] = InputStream.class;
					Object[] params = new Object[1];
					params[0] = is;
					String license_key = (String)Class.forName("framepump.mpeg4.LicenseManager").getMethod(
						"loadVideoLicense", param_classes).invoke(null, params);
					movieLength -= license_key.length();
					if(license_key.length() > 2) {
						movieLength -= 2;
						encryptedStream = true;
						param_classes = new Class[2];
						param_classes[0] = URL.class;
						param_classes[1] = String.class;
						params = new Object[2];
						params[0] = getCodeBase();
						params[1] = license_key.substring(2);
						licenseIsCorrect = ((Boolean)Class.forName("framepump.mpeg4.LicenseManager").getMethod(
							"isCorrectLicenseKey", param_classes).invoke(null, params)).booleanValue();
					}
					if(! encryptedStream) {
						error_code = 4;
						player_state = ERROR_STATE;
						myPaint();
						try {
							if(is != null) {
								is.close();
								is = null;
							}
						} catch (Exception ex) {
						}
						return;
					}
				} catch (InvocationTargetException ex) {
					throw ex.getTargetException();
				} catch (ClassNotFoundException clEx) {
					licenseIsCorrect = true;
					video_license = false;
				}
			}
			int bufferSize = parsePercentValue(movieLength, sBufferSize, movieLength / 5);
			if(bufferSize == 0) {
				bufferSize = movieLength / 5;
			}
			if(bufferSize > movieLength) {
				bufferSize = movieLength;
			}
			buffering = true;
			dataBuffer = new DataBuffer(this, is, movieLength, bufferSize, encryptedStream); 
		} catch (Throwable ex) {
			error_code = 1;
			player_state = ERROR_STATE;
			ex.printStackTrace();
			myPaint();
		}

	}

	private int audioHeaderSize = 0;
	private long audioHeaderOffset = 0;
	
	private void parseFile() {
		int timeScale = -1;
		long duration = -1;
		long videoHeaderOffset = 0;
		int videoHeaderSize = 0;
		
		try {
			audioSamples.removeAllElements();
			videoSamples.removeAllElements();
			DataStream stream = new DataStream(dataBuffer);
			for(;((audioSamples.size() == 0) && (videoSamples.size() == 0));) {
				MP4Atom atom = MP4Atom.createAtom(stream);
				if(atom.getType() == MP4Atom.MP4MovieAtomType) {
					MP4Atom movieAtom = atom;
					int j = 0;
					MP4Atom trackAtom = movieAtom.lookup(
							MP4Atom.MP4TrackAtomType, j++);
					while(trackAtom != null){
						MP4Atom mediaAtomType = trackAtom.lookup(
								MP4Atom.MP4MediaAtomType, 0);
						if(mediaAtomType == null)
							throw new Exception("The MP4MediaAtom does not exist");

						MP4Atom handlerAtom = mediaAtomType.lookup(
								MP4Atom.MP4HandlerAtomType, 0);
						if(handlerAtom == null)
							throw new Exception("The MP4HandlerAtom does not exist");
							
						if(handlerAtom.getHandlerType() == MP4Atom.typeToInt("soun")
							|| handlerAtom.getHandlerType() == MP4Atom.typeToInt("vide")) {

							MP4Atom mediaInformationAtom = mediaAtomType.lookup( 												
									MP4Atom.MP4MediaInformationAtomType, 0);
							if(mediaInformationAtom == null)
								throw new Exception("The MP4MediaInformationAtom does not exist");

							MP4Atom sampleTableAtom = mediaInformationAtom.lookup( 												
									MP4Atom.MP4SampleTableAtomType, 0);
							if(sampleTableAtom == null)
								throw new Exception("The MP4SampleTableAtom does not exist");

							MP4Atom sampleDescriptionAtom = sampleTableAtom.lookup(
									MP4Atom.MP4SampleDescriptionAtomType, 0);
							if(sampleDescriptionAtom == null)
									throw new Exception("The MP4SampleDescriptionAtom does not exist");
						
							if(handlerAtom.getHandlerType() == MP4Atom.typeToInt("vide")) {

								MP4Atom mediaHeaderAtom = mediaAtomType.lookup(
										MP4Atom.MP4MediaHeaderAtomType, 0);
								if(mediaHeaderAtom == null)
									throw new Exception("The MP4MediaHeaderAtom does not exist");
									
								duration = mediaHeaderAtom.getDuration();
								timeScale = mediaHeaderAtom.getTimeScale();
								movieDuration = (int)(duration * 1000 / timeScale);

								MP4Atom visualSampleEntry = sampleDescriptionAtom.lookup(
										MP4Atom.MP4VisualSampleEntryAtomType, 0);
								if(visualSampleEntry == null)
									throw new Exception("The MP4VisualSampleEntryAtom does not exist");
								video_width = visualSampleEntry.getWidth();
								video_height =  visualSampleEntry.getHeight();
								video_size = 0;
								videoHeaderOffset = videoHeaderSize = 0;
								
								MP4Atom esdAtom = visualSampleEntry.lookup(MP4Atom.MP4ESDAtomType, 0);
								if(esdAtom != null) {
									MP4Descriptor esd_descriptor = esdAtom.getEsd_descriptor();
									if(esd_descriptor != null) {
										MP4Descriptor decoderConfigDescriptor = esd_descriptor.lookup(MP4Descriptor.MP4DecoderConfigDescriptorTag, 0);
										if(decoderConfigDescriptor != null) {
											MP4Descriptor decSpecificInfoDescriptor = decoderConfigDescriptor.lookup(MP4Descriptor.MP4DecSpecificInfoDescriptorTag, 0);
											if(decSpecificInfoDescriptor != null) {
												videoHeaderOffset = decSpecificInfoDescriptor.getDecSpecificDataOffset();
												videoHeaderSize = decSpecificInfoDescriptor.getDecSpecificDataSize();
											}
										}
									}
								}								
							} else {

								MP4Atom audioSampleEntry = sampleDescriptionAtom.lookup(
										MP4Atom.MP4AudioSampleEntryAtomType, 0);
								if(audioSampleEntry == null)
									throw new Exception("The MP4AudioSampleEntryAtom does not exist");

								audioHeaderOffset = audioHeaderSize = 0;
								
								MP4Atom esdAtom = audioSampleEntry.lookup(MP4Atom.MP4ESDAtomType, 0);
								if(esdAtom != null) {
									MP4Descriptor esd_descriptor = esdAtom.getEsd_descriptor();
									if(esd_descriptor != null) {
										MP4Descriptor decoderConfigDescriptor = esd_descriptor.lookup(MP4Descriptor.MP4DecoderConfigDescriptorTag, 0);
										if(decoderConfigDescriptor != null) {
											MP4Descriptor decSpecificInfoDescriptor = decoderConfigDescriptor.lookup(MP4Descriptor.MP4DecSpecificInfoDescriptorTag, 0);
											if(decSpecificInfoDescriptor != null) {
												audioHeaderOffset = decSpecificInfoDescriptor.getDecSpecificDataOffset();
												audioHeaderSize = decSpecificInfoDescriptor.getDecSpecificDataSize();
											}
										}
									}
								}
							}


							MP4Atom sampleToChunkAtom = sampleTableAtom.lookup(
									MP4Atom.MP4SampleToChunkAtomType, 0);
							if(sampleToChunkAtom == null)
								throw new Exception("The MP4SampleToChunkAtom does not exist");

							MP4Atom sampleSizeAtom = sampleTableAtom.lookup(
									MP4Atom.MP4SampleSizeAtomType, 0);
							if(sampleSizeAtom == null) {
								sampleSizeAtom = sampleTableAtom.lookup(
								MP4Atom.MP4CompactSampleSizeAtomType, 0);
							}
							if(sampleSizeAtom == null)
								throw new Exception("The MP4SampleSizeAtom does not exist");

							MP4Atom chunkOffsetAtomType = sampleTableAtom.lookup(
								MP4Atom.MP4ChunkOffsetAtomType, 0);
							if(chunkOffsetAtomType == null) {
								chunkOffsetAtomType = sampleTableAtom.lookup(
									MP4Atom.MP4ChunkLargeOffsetAtomType, 0);
							}
							if(chunkOffsetAtomType == null)
								throw new Exception("The MP4ChunkOffsetAtom does not exist");
								
							Vector records = sampleToChunkAtom.getRecords();
							int currentSample = 0;
							for(int i = 0; i < records.size(); i++) {
								MP4Atom.Record record = (MP4Atom.Record)records.elementAt(i);
								int maxChunkNumber = 0; 
								if(i == records.size() - 1) {
									maxChunkNumber = chunkOffsetAtomType.getChunks().size();
								} else {
									MP4Atom.Record nextRecord = (MP4Atom.Record)records.elementAt(i+1);
									maxChunkNumber = nextRecord.getFirstChunk() - 1;
								}
								for(int chunkNumber = record.getFirstChunk() - 1; chunkNumber < maxChunkNumber; chunkNumber ++){
									Long offset = (Long)chunkOffsetAtomType.getChunks().elementAt(chunkNumber);
									if(offset == null)
										throw new Exception("Unable to find the chunk with the id = " + record.getFirstChunk());
									long sampleOffset = offset.longValue();
									for(int num = 0; num < record.getSamplesPerChunk(); num++) {
										int size = sampleSizeAtom.getSampleSize();
										if(size == 0) {
											Integer iSize = (Integer)sampleSizeAtom.getSamples().elementAt(currentSample);
											if(iSize == null)
												throw new Exception("Unable to find the sample with the id = " + currentSample);
											size = iSize.intValue();
										}
										DataSample sample = new DataSample(sampleOffset, size);

										if(handlerAtom.getHandlerType() == MP4Atom.typeToInt("soun")) {
											audioSamples.addElement(sample);
										} else if (handlerAtom.getHandlerType() == MP4Atom.typeToInt("vide")) {
											videoSamples.addElement(sample);
											video_size += size;
										}
										sampleOffset+= size;
										currentSample++;
									}
								}
							}
						}
						trackAtom = movieAtom.lookup(
								MP4Atom.MP4TrackAtomType, j++);
					}
				}
			}
		} catch (InterruptedException iex) {
			return;
		} catch (InterruptedIOException iox) {
			return;
 		} catch (Exception ex) {
			error_code = 3;
			player_state = ERROR_STATE;
			myPaint();
			ex.printStackTrace();
		}

		video_rate = (((double)videoSamples.size()) * ((double)timeScale) / (double)(duration));
		if((videoHeaderSize > 0) && (videoHeaderOffset > 0)) {
			videoSamples.insertElementAt(new DataSample(videoHeaderOffset, videoHeaderSize), 0);
			video_size += videoHeaderSize;
		}
		if((audioHeaderSize > 0) && (audioHeaderOffset > 0)) {
			audioSamples.insertElementAt(new DataSample(audioHeaderOffset, audioHeaderSize), 0);
		}
	}
	
	private volatile boolean bandwidth_detecting = false;
	
	private int detectBandwidth() {
		
		try {
			int detect_size = 8;
			int fileLength = 0;
			long download_time = 0;
			byte[] buf = new byte[100]; 

			for(int downloads = 0; downloads < 2; downloads ++) {
				URLConnection uc = new URL(getCodeBase(), detect_location + detect_size + ".detect.speed?detect=true").openConnection();
				uc.setUseCaches(false);
				uc.connect();
				long start_download_time = System.currentTimeMillis();
				fileLength = uc.getContentLength() + 332;  // calculates the length of the file + headers
				if(fileLength < 4428) {
					return CONNECTION_SPEED_UNKNOWN;
				}
				if(downloads == 0) {
				    bandwidth_detecting = true;
    				connection_speed = CONNECTION_SPEED_24K;
					detect_start_showing_time = System.currentTimeMillis();
					new Thread(this, "Bandwidth Detecting Thread").start();
				}
				long is_open_time = System.currentTimeMillis() - start_download_time;
				start_download_time += is_open_time;
				InputStream is = uc.getInputStream();
				while(is.read(buf) != -1) {
				}
				download_time = System.currentTimeMillis() - start_download_time;
//				System.out.println("Input Stream open time: " + is_open_time);
//				System.out.println("Download time: " + download_time);
				download_time += is_open_time;
				is.close();
				if((downloads == 0) && (download_time < 2000)) {
					while((download_time < 2000) && (detect_size < 256)) {				
						detect_size *= 2;
						download_time *= 2;
					}
				} else {
					break;
				}
			}
			download_time = (download_time * 4096) / fileLength;			
//			System.out.println("Fixed download time: " + download_time);

			if(download_time >= 1100) {		  // 1333 - 121		//  fixed to expirimental 1100 value (~28000)
				return CONNECTION_SPEED_24K;
			} else if(download_time >= 632) { // 727 - 95
				return CONNECTION_SPEED_44K;
			} else if(download_time >= 225) { // 250 - 25
				return CONNECTION_SPEED_128K;
			} else if(download_time >= 115) { // 125 - 10		// fixed to experimental 115
				return CONNECTION_SPEED_256K;
			} else if(download_time >= 77) {  // 91 - 6 = 85	// fixed to experimental 77  
				return CONNECTION_SPEED_350K;
			} else if(download_time >= 57) {  // 64 - 7
				return CONNECTION_SPEED_500K;
			}
			return CONNECTION_SPEED_800K;	  // 40
		} catch (Exception ex) {
		} finally {
			bandwidth_detecting = false;
		}
		return CONNECTION_SPEED_UNKNOWN;		
	}

	private void playerstart() {
		try {
			if(audio_enabled) {
				try {
					audioStream = new DataChannel(DataChannel.AUDIO_CHANNEL, dataBuffer, audioSamples);
					audioPlayer = AudioPlayer.createAudioPlayer(audioStream, audioHeaderSize);
					audioPlayer.setVolume(volume);
					audioPlayer.setMute(mute);
				} catch (Exception ex) {
					mute = true;
					audioPlayer = null;
					audio_enabled = false;
				}						
			}
			videoStream = new BitStream(new DataChannel(DataChannel.VIDEO_CHANNEL, dataBuffer, videoSamples));
			videoDecoder = new MPEG4Decoder(this, videoStream, video_width, video_height, video_rate, video_size);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void playerplay() {
		synchronized (this) {
			if(firstLoop) {
				autoplay = true;
				return;
			}
		}
		if((buffering == true) || ((player_state != READY_STATE) && (player_state != PAUSE_STATE))) {
			return;
		}
		if(playback_mode == ROLLOWER_TO_PLAY) {
			playback_mode = MANUAL_PLAYBACK;
		}
		System.gc();
		try {
			if(audioPlayer != null) {						
				audioPlayer.play();
			}
			feedback_start_showing_time = 0;
			player_state = PLAY_STATE;
			setMenuState();			
			synchronized(this) {
				notifyAll();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void playerpause() {
		if((buffering == true) || (player_state != PLAY_STATE)) {
			return;
		}
		try {
			feedback_start_showing_time = 0;
			player_state = PAUSE_STATE;
			setMenuState();			
			if(audioPlayer != null) {
				audioPlayer.pause();
			}
			myPaint();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void playerstop() {
		if(audioPlayer != null) {
			try {
				audioPlayer.stop();
			} catch (Exception ex) {}
			finally {
				audioPlayer = null;
			}
		} 
		if(videoDecoder != null) {
			try {
				videoDecoder.stop();
			} catch (Exception ex) {}
			finally {
				videoDecoder = null;
			}
		} 
		movieTime = -1;
	}
	
	public void playerend() {
		movieTime = -1;
		if(!playback_loop) {
			player_state = STOP_STATE;
			setMenuState();			
			myPaint();
		}
		JSCallback.stopPlayCallBack(this);
		if(playback_loop) {
			autoplay = true;
			playerrewind();
		}
	}

	public void playerrewind() {
		if((player_state == ERROR_STATE) || (player_state == REWIND_STATE) || (player_state == START_STATE) || (player_state == READY_STATE)) {
			return;
		}
   		if(buffering && (player_state != START_STATE)) {
		    buffering = false;
		}
		player_state = REWIND_STATE;
		setMenuState();			
		myPaint();
		playerstop();
		System.gc();
		firstLoop = true;
		playerstart();
	}
	
	void startReBuffering() {		
		buffering = true;
		setMenuState();			
		if(player_state == PLAY_STATE) {
			if(audioPlayer != null) {
				audioPlayer.pause();
			}
		}
	}
	void stopBuffering() throws InterruptedIOException {
		try {
			if(player_state == PLAY_STATE) {
				if(audioPlayer != null) {
					audioPlayer.play();
				}
			}
			buffering = false;
			setMenuState();			
			synchronized(this) {
				notifyAll();
			}
			if((player_state == READY_STATE) || (player_state == START_STATE)) {
				Thread.sleep(300);
				myPaint();
			}
		} catch (InterruptedException iex){
			throw new InterruptedIOException(iex.getMessage());
		}
	}

	void stopReBuffering() throws InterruptedIOException {
		this.stopBuffering();
	}
	
	private int buffering_percent = 0;
	
	void printBufferPercent(int percent) {
		this.buffering_percent = percent;
		if(feedback_agent) {
			myPaint();
		}
	}
	
	public void playersetmute(boolean mute) {
		if(audio_enabled) {
			this.mute = mute;
			setMenuState();			
			if(audioPlayer != null) {
				audioPlayer.setMute(mute);
			}
			myPaint();
		}
	}
	
	public void playersetvolume(String sValue) {
		if(audio_enabled) {
			try {
				volume = parsePercentValue(100, sValue, volume);
				if((sValue != null) && (sValue.length() >= 1) && (sValue.charAt(sValue.length() - 1) != '%')) {
					if(volume == Integer.parseInt(sValue)) {
						volume = (volume - 1) * 100 / 9;
					}
				}
				if(volume < 0) {
					volume = 0;
				} else if (volume > 100) {
					volume = 100;
				}
				if(audioPlayer != null) {
					audioPlayer.setVolume(volume);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public boolean get_audio_state() {
		return mute;
	}
	
	public String get_filename() {
		return fileName == null ? "" : fileName;
	}
	
	public int get_volume() {
		return (int)((float)volume * 9.f / 100.f + 1.5f);
	}

	public int get_volume(String type) {
		if("%".equals(type)) {
			return volume;
		}
		return get_volume();
	}
	
	public int get_video_size() {
		return movieLength;
	}

	public int get_video_length() {
		return movieDuration;
	}
	
	public int get_video_time() {
		return movieTime;			
	} 	
	
	public int get_connection_speed() {
		return CONNECTION_VALUES[connection_speed >= 0 ? connection_speed : 0];
	}
	
	public String get_mf_state() {
		if(buffering && (player_state != ERROR_STATE)) {
			if((player_state == START_STATE) || (player_state == READY_STATE) || firstLoop) {
				return "buffering";
			} else {
				return "re-buffering";
			}
		}
		return STATE_TEXT[player_state];
	}
	
	public int get_errorcode() {
		return error_code;
	}

	public String get_errortext() {
		return ERROR_MESSAGES[error_code];
	}
	
	public void mediaframe_load(String fileName) {
		if(player_state == REWIND_STATE) {
			return;
		}
		error_code = 0;
		player_state = REWIND_STATE;
		setMenuState();			
		myPaint();
		stop();		
		System.gc();
		if(! audio_enabled) {
			audio_enabled = getBooleanParameter("audio", true);
			if(audio_enabled && (!AudioPlayer.isSoundEnabled())) {
				audio_enabled = false;
				mute = true;
			} else {
				mute = false;
			}
		}
		setMenuState();			
		firstLoop = true;
		autoplay = false;
		JSCallback.startCallBack(this);
		loadFile(this.fileName = fileName);
		if(dataBuffer != null) {
			movieScreen = null;
			pixels = null;
			movieParseThread = Thread.currentThread(); 
			parseFile();
			if(movieParseThread == Thread.currentThread()) {
				if(error_code == 0) {
					playerstart();
				}
				movieParseThread = null;
			}
		}

	}

	private MemoryImageSource mis = null;
	private Image movieScreen = null;
	private int[] pixels = null;
	
	private int top_x_coord = 0;
	private int top_y_coord = 0;

	public final static int ALPHA_MASK = (255 << 24);
	public final static int SHIFT_BITS = 16;
	public final static int R_CR_COEFF = (int)(1.402 * (1 << SHIFT_BITS));
	public final static int G_CR_COEFF = (int)(0.714 * (1 << SHIFT_BITS));
	public final static int G_CB_COEFF = (int)(0.344 * (1 << SHIFT_BITS));
	public final static int B_CB_COEFF = (int)(1.772 * (1 << SHIFT_BITS));
	
	private long start_playing_time = 0;
	
	private int movie_window_width = 300;//408;
	private int movie_window_height = -1;
	
	private int video_width;
	private int video_height;
	
	private Image bufferImage = null;
	private Graphics2D bufferGraphics = null;

	private boolean linux_platform = false;
	private boolean windows_platform = true;	
	private boolean java2_platform = false; 
	
	private Container sourceComponent = this;
	
	private int pixel_frame_width;
	private int pixel_frame_height;

	private int memory_image_width;
	private int memory_image_height;
	
	private int current_frame = 0;
	private int last_showed_frame = 0;
	
	private long last_sleep_time = 0;
	
	public void nextFrame(VideoFrame videoFrame) throws InterruptedIOException {
		int width = videoFrame.getFrameWidth();
		int height = videoFrame.getFrameHeight();
		int buffer_width = videoFrame.getBufferWidth();
		int[][] y_cb_cr_pixels = videoFrame.getPixelData();
		if(pixels == null) {
			if((movie_window_width == -1) && (movie_window_height == -1)) {
				movie_window_width = width;
				movie_window_height = height;
			} else if (movie_window_width == -1) {
				movie_window_width = (width * movie_window_height) / height;
			} else if (movie_window_height == -1) {
				movie_window_height = (height * movie_window_width) / width;
			}
			if(! runInApplet) {
				top_x_coord = 4;
				top_y_coord = 23;
				sourceComponent = frame = new MovieFrame(this);
			
				createPopupMenu();
				frame.setSize(movie_window_width + 8, movie_window_height + 27);
				frame.setLocation(
					(Toolkit.getDefaultToolkit().getScreenSize().width - movie_window_width) >> 1, 
					(Toolkit.getDefaultToolkit().getScreenSize().height - movie_window_height) >> 1
				);
				frame.setVisible(true);
				frame.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent ev) {
						stop();
						System.exit(0);
					}	
				});
				frame.addMouseListener(this);
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				frame.add(popupMenu);
			}
			if(java2_platform && smooth_video) {
				memory_image_width = pixel_frame_width = width;
				memory_image_height = pixel_frame_height = height;
			} else {
				memory_image_width = movie_window_width;
				memory_image_height = movie_window_height;
				pixel_frame_width = width > movie_window_width ? width : movie_window_width;
				pixel_frame_height = height > movie_window_height ? height : movie_window_height;
			}
			pixels = new int[pixel_frame_width * pixel_frame_height];
			mis = new MemoryImageSource(memory_image_width, memory_image_height, pixels, 0, pixel_frame_width);
			mis.setAnimated(true);
			movieScreen = sourceComponent.createImage(mis);
		}
		long playingTime = System.currentTimeMillis() - start_playing_time;
		int i, j, y, cb, cr, r_cr, g_cr_cb, b_cb, r, g, b;
		int lumminance_index = 0;
		int chrominance_index = 0;
		int pixel_index = 0;
		// Y CR CB -> R G B transformation
		for(j = 0; j < height; j+=2) {
			for(i = 0; i < width; i++) {
				y = y_cb_cr_pixels[0][lumminance_index + i] << SHIFT_BITS;
				cb = y_cb_cr_pixels[1][chrominance_index + (i >> 1)] - 128;
				cr = y_cb_cr_pixels[2][chrominance_index + (i >> 1)] - 128;
				r_cr = R_CR_COEFF * cr;
				g_cr_cb = - G_CB_COEFF * cb - G_CR_COEFF * cr;
				b_cb = B_CB_COEFF * cb;
				r = - (y + r_cr) >> SHIFT_BITS;
				g = - (y + g_cr_cb) >> SHIFT_BITS;
				b = - (y + b_cb) >> SHIFT_BITS;
//				r = g = b = - ((y >> SHIFT_BITS) + 128);
/*
				if(g > 255)
					g = 255;
				if(b > 255)
					b = 255;
				if(g < 0)
					g = 0;
				if(b < 0)
					b = 0;
				if(r < 0)
					r = 0;
				if(r < 0)
					r = 0;
				pixels[pixel_index + i++] = ALPHA_MASK + b + (g << 8) + (r << 16);
*/					
				r = - (r & (r >> 63)) - 255;
				g = - (g & (g >> 63)) - 255;
				b = - (b & (b >> 63)) - 255;
				pixels[pixel_index + i++] = ALPHA_MASK + ((b & (b >> 63)) + 255) + (((g & (g >> 63)) + 255) << 8) + (((r & (r >> 63)) + 255) << 16);
				y = y_cb_cr_pixels[0][lumminance_index + i] << SHIFT_BITS;
				r = - (y + r_cr) >> SHIFT_BITS;
				g = - (y + g_cr_cb) >> SHIFT_BITS;
				b = - (y + b_cb) >> SHIFT_BITS;
//				r = g = b = - ((y >> SHIFT_BITS) + 128);
				r = - (r & (r >> 63)) - 255;
				g = - (g & (g >> 63)) - 255;
				b = - (b & (b >> 63)) - 255;
				pixels[pixel_index + i--] = ALPHA_MASK + ((b & (b >> 63)) + 255) + (((g & (g >> 63)) + 255) << 8) + (((r & (r >> 63)) + 255) << 16);
				y = y_cb_cr_pixels[0][lumminance_index + buffer_width + i] << SHIFT_BITS;
				r = - (y + r_cr) >> SHIFT_BITS;
				g = - (y + g_cr_cb) >> SHIFT_BITS;
				b = - (y + b_cb) >> SHIFT_BITS;
//				r = g = b = - ((y >> SHIFT_BITS) + 128);
				r = - (r & (r >> 63)) - 255;
				g = - (g & (g >> 63)) - 255;
				b = - (b & (b >> 63)) - 255;
				pixels[pixel_index + pixel_frame_width + i++] = ALPHA_MASK + ((b & (b >> 63)) + 255) + (((g & (g >> 63)) + 255) << 8) + (((r & (r >> 63)) + 255) << 16);
				y = y_cb_cr_pixels[0][lumminance_index + buffer_width + i] << SHIFT_BITS;
				r = - (y + r_cr) >> SHIFT_BITS;
				g = - (y + g_cr_cb) >> SHIFT_BITS;
				b = - (y + b_cb) >> SHIFT_BITS;
//				r = g = b = - ((y >> SHIFT_BITS) + 128);
				r = - (r & (r >> 63)) - 255;
				g = - (g & (g >> 63)) - 255;
				b = - (b & (b >> 63)) - 255;
				pixels[pixel_index + pixel_frame_width + i] = ALPHA_MASK + ((b & (b >> 63)) + 255) + (((g & (g >> 63)) + 255) << 8) + (((r & (r >> 63)) + 255) << 16);
			}
			lumminance_index += buffer_width << 1;
			pixel_index += pixel_frame_width << 1;
			chrominance_index += buffer_width;			
		}
		if(!(java2_platform && smooth_video)) {
			if(movie_window_width > width) {
				int x_increment = (width << 8)/ movie_window_width;
				for(j = 0; j < height; j++) {
					int source_index = ((j * movie_window_width + width - 1) << 8) + (1 << (8 - 1)) + x_increment;
					int destination_index = (j + 1) * movie_window_width - 1;
					int eight_pixels_loops = movie_window_width >> 3;
					for(i = 0; i < eight_pixels_loops; i++) {
						pixels[destination_index--] = pixels[(source_index-=x_increment) >> 8];
						pixels[destination_index--] = pixels[(source_index-=x_increment) >> 8];
						pixels[destination_index--] = pixels[(source_index-=x_increment) >> 8];
						pixels[destination_index--] = pixels[(source_index-=x_increment) >> 8];
						pixels[destination_index--] = pixels[(source_index-=x_increment) >> 8];
						pixels[destination_index--] = pixels[(source_index-=x_increment) >> 8];
						pixels[destination_index--] = pixels[(source_index-=x_increment) >> 8];
						pixels[destination_index--] = pixels[(source_index-=x_increment) >> 8];
					}
					for(i = 0; i < (movie_window_width & 7); i++) {
						pixels[destination_index--] = pixels[(source_index-=x_increment) >> 8];
					}
				}
			}
			if(movie_window_height != height) {
				int y_increment = (height << 8)/ movie_window_height;
				int source_y, d_y_increment, destination_index = 0;
				if(movie_window_height > height) {
					source_y = ((height - 1) << 8) + (1 << (8 - 1));
					destination_index = (movie_window_height - 1) * movie_window_width;
					d_y_increment = - movie_window_width;
					y_increment = - y_increment;
				} else {
					y_increment = (height << 8)/ movie_window_height;
					source_y = 0;
					d_y_increment = + pixel_frame_width;
				}
				for(j = 0; j < (movie_window_height - 1); j++) {
					System.arraycopy(pixels, (source_y >> 8) * pixel_frame_width, pixels, destination_index, pixel_frame_width);
					destination_index += d_y_increment;
					source_y += y_increment;
				}
			}
			if(movie_window_width < width) {
				int x_increment = (width << 8)/ movie_window_width;
				for(j = 0; j < movie_window_height; j++) {
					int source_index = ((j * pixel_frame_width) << 8) + (1 << (8 - 1)) - x_increment;
					int destination_index = j * pixel_frame_width;
					int eight_pixels_loops = movie_window_width >> 3;
					for(i = 0; i < eight_pixels_loops; i++) {
						pixels[destination_index++] = pixels[(source_index+=x_increment) >> 8];
						pixels[destination_index++] = pixels[(source_index+=x_increment) >> 8];
						pixels[destination_index++] = pixels[(source_index+=x_increment) >> 8];
						pixels[destination_index++] = pixels[(source_index+=x_increment) >> 8];
						pixels[destination_index++] = pixels[(source_index+=x_increment) >> 8];
						pixels[destination_index++] = pixels[(source_index+=x_increment) >> 8];
						pixels[destination_index++] = pixels[(source_index+=x_increment) >> 8];
						pixels[destination_index++] = pixels[(source_index+=x_increment) >> 8];
					}
					for(i = 0; i < (movie_window_width & 7); i++) {
						pixels[destination_index++] = pixels[(source_index+=x_increment) >> 8];
					}
				}
			}
		}
		if(firstLoop) {
			last_showed_frame = current_frame = 0;
		}
		movieTime = (int)videoFrame.getPlaying_time();
		mis.newPixels();
				
		try {
			synchronized(this) {
				if(firstLoop) {
					while(buffering) {
						wait();
					}
					player_state = READY_STATE;
					JSCallback.readyToPlayCallBack(this);
					setMenuState();
					myPaint();										
					firstLoop = false;
					if(autoplay) {
						autoplay = false;
						playerplay(); 
					}
					last_sleep_time = start_playing_time = System.currentTimeMillis();
				}
				current_frame ++;
				while((player_state != PLAY_STATE) || buffering) {
					long start_pause_time = System.currentTimeMillis();
					wait();
					start_playing_time += System.currentTimeMillis() - start_pause_time;
					last_sleep_time = playingTime;
				}
			}
			long pauseTime = movieTime - playingTime;
			if(pauseTime > 15) {
//				System.out.println("Frame: " + current_frame + " Pause: " + pauseTime);
				last_sleep_time = playingTime;
				Thread.sleep(pauseTime - 10);
			} else if((playingTime - last_sleep_time) > 250) {
				// sleeps 30 ms if the audio player is decoding the audio stream 
				// and the video decoder thread hasn't slept last 250 milliseconds 
				last_sleep_time = playingTime;
				if((audioPlayer != null) && (audioPlayer.isDecoding())) {
					Thread.sleep(30);
				}
			}
		} catch (InterruptedException iex){
			throw new InterruptedIOException(iex.getMessage());
		}
		if(last_showed_frame < (current_frame - 3)) {
			myPaint();
		}
	}
	
	public void paint(Graphics g) {
		myPaint(g);
	}
	
	public void update(Graphics g) {
		myPaint(g);
	}

	public void myPaint() {
		if(runInApplet == false) {
			if(frame != null) {
				frame.myPaint();
			}
			return;
		}
		Graphics g = getGraphics();
		if(g != null) {
			myPaint(g);
		}
	}
	
	private Font ERROR_FONT = new Font("Arial", 0, 12);
	private Font TEXT_FONT = new Font("Arial", Font.ITALIC, 10);
	private Font TEXT_BI_FONT = new Font("Arial", Font.ITALIC | Font.BOLD, 10);
	
	private volatile Thread pre_screen_loop_thread = null;
	private volatile Thread post_screen_loop_thread = null;
	private volatile int pre_screen_image_index = 0;
	private volatile int post_screen_image_index = 0;
	private volatile long start_screen_paint_time = 0;
	
	private volatile long feedback_start_showing_time = 0;
	private volatile long detect_start_showing_time = 0;
	
	private ViewPort viewPort;
	private ZoomThread zoomThread;
	private PanThread panThread;
	
	public void myPaint(Graphics g) {
		try {
			if(g == null) {
				return;
			}
			if(bufferGraphics == null) {
				bufferImage = sourceComponent.createImage(movie_window_width, movie_window_height);
				bufferGraphics = (Graphics2D) bufferImage.getGraphics();
			}
			if( viewPort == null ) {
				viewPort = new ViewPort(sourceComponent, bufferImage, new Dimension(movie_window_width, movie_window_height), 5);
	
			}
			Graphics bg = bufferGraphics;
			if(player_state == ERROR_STATE) {
				if(java2_platform) {
						J2Utils.setRenderingHints(bg, smooth_video, false);
				}
				bg.setColor(Color.black);
				bg.fillRect(0, 0, movie_window_width, movie_window_height);
				bg.setColor(Color.white);
				bg.setFont(ERROR_FONT);
				if(error_code == 10) {
					bg.drawString(ERROR_MESSAGES[error_code], movie_window_width / 2 - 70, movie_window_height / 2 - 3);
				} else {
					bg.drawString("ERROR: " + ERROR_MESSAGES[error_code], 10, 20);
				}
			} else 
			if((player_state == PLAY_STATE) || (player_state == PAUSE_STATE)) {
				if(movieScreen != null) {
					last_showed_frame = current_frame;
					if(java2_platform) {
						J2Utils.setRenderingHints(bg, smooth_video, true);
						
						if(smooth_video) {
							bg.drawImage(movieScreen, 0, 0, movie_window_width, movie_window_height, sourceComponent);
						} else {
							bg.drawImage(movieScreen, 0, 0, sourceComponent);
						}
					} else {
						bg.drawImage(movieScreen, 0, 0, sourceComponent);
						
					}
					//viewPort.draw(top_x_coord, top_y_coord,(Graphics2D) bg);
				}

				if(! java2_platform) {
					bg.setFont(TEXT_FONT);
				} else {
					bg.setFont(TEXT_BI_FONT);
				}
				bg.setColor(Color.white);
				if(feedback_agent) {
					if(buffering) {
						bg.drawString("re-buffering... " + buffering_percent + "%", movie_window_width - (java2_platform ? 120 : 100), movie_window_height - 12);
					} else { 
						if(feedback_start_showing_time == 0) {
							feedback_start_showing_time = System.currentTimeMillis();
							if(player_state == PAUSE_STATE) {
								new Thread(this, "Pause_Thread").start();
							}
						}
						if ((System.currentTimeMillis() - feedback_start_showing_time) < 1000){
							if(player_state == PLAY_STATE) {
								bg.drawString("playing...", movie_window_width - 76, movie_window_height - 12);
							} else if(player_state == PAUSE_STATE){
								bg.drawString("paused", movie_window_width - 76, movie_window_height - 12);
							}
						}
					}
				}
				/*
				 * Seems like this is the place to put some limiting on client viewing 
				 * options open as to what comes from server side
				 */
				if(! licenseIsCorrect && (movieTime > 5000)) {
					error_code = 10;
					player_state = ERROR_STATE;
					playerstop();
					myPaint(g);
				}
			} else
			if((preScreenImgs != null) && (player_state == START_STATE) || (player_state == READY_STATE) || (player_state == REWIND_STATE)) {
				post_screen_loop_thread = null;
				if((preScreenImgs.length > 0) && (pre_screen_loop_thread == null)) {
					pre_screen_image_index = -1;
					start_screen_paint_time = System.currentTimeMillis();
					(pre_screen_loop_thread = new Thread(this, "Pre Screen Thread")).start(); 
				}
				if((Thread.currentThread() == pre_screen_loop_thread) && !show_fp_logo) {
					if(++ pre_screen_image_index == preScreenImgs.length) {
						if(pre_screen_loop) {
							pre_screen_image_index = 0;
						} else {
							pre_screen_image_index--;
						}
					}
				}
				Image preScreenImg = preScreenImgs[pre_screen_image_index < 0 ? 0 : pre_screen_image_index];
				bg.drawImage(show_fp_logo ? fp_logo : preScreenImg, 0, 0, this);

				if(feedback_agent) {
					if(java2_platform) {
						J2Utils.setRenderingHints(bg, smooth_video, false);
					}
					if(windows_platform && java2_platform) {
						bg.setFont(TEXT_BI_FONT);
					} else {
						bg.setFont(TEXT_FONT);
					}
					bg.setColor(Color.white);
					long current_time = System.currentTimeMillis();
					if (bandwidth_detecting || (current_time - detect_start_showing_time) < 1000){
						bg.drawString("detecting bandwidth...", movie_window_width - ((windows_platform && java2_platform) ? 135 : 130), movie_window_height - 12);
					} else if (! bandwidth_detecting && ((current_time - detect_start_showing_time) < 2000)){
						String text = null;
						if(connection_speed > 0) {
							text = CONNECTION_TEXT[connection_speed] + " kbps version chosen";
						} else {
							text = "Unable to detect bandwidth";
						}
						bg.drawString(text, movie_window_width - (((windows_platform && java2_platform) || (connection_speed <= 0)) ? 135 : 130), movie_window_height - 12);
					} else if(buffering && (connection_speed != -1)) {
						bg.drawString("buffering... " + buffering_percent + "%", movie_window_width - 85, movie_window_height - 12);
					}
				}
				g.drawImage(bufferImage, top_x_coord, top_y_coord, sourceComponent);
				return;
			} else 
			if((postScreenImgs != null) && (player_state == STOP_STATE)) {
				pre_screen_loop_thread = null;
				if((postScreenImgs.length > 0) && (post_screen_loop_thread == null)) {
					post_screen_image_index = -1;
					start_screen_paint_time = System.currentTimeMillis();
					(post_screen_loop_thread = new Thread(this, "Post Screen Thread")).start(); 
				}
				if(Thread.currentThread() == post_screen_loop_thread) {
					if(++ post_screen_image_index == postScreenImgs.length) {
						if(post_screen_loop) {
							post_screen_image_index = 0;
						} else {
							post_screen_image_index--;
						}
					}
				}
				Image postScreenImg = postScreenImgs[post_screen_image_index < 0 ? 0 : post_screen_image_index];
				bg.drawImage(postScreenImg, 0, 0, this);
				//paintControlPanel(bg);
				g.drawImage(bufferImage, top_x_coord, top_y_coord, sourceComponent);

				return;
			}
			
			pre_screen_loop_thread = post_screen_loop_thread = null;
			synchronized(viewPort) {
				viewPort.draw(top_x_coord, top_y_coord,(Graphics2D) bg);
			}

			g.drawImage(bufferImage, top_x_coord, top_y_coord, sourceComponent);
			//viewPort.dumpImage();
		
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public boolean isDoubleBuffered() {
		return true;
	}
	
	public void mouseClicked(MouseEvent me) {

		if ((me.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
			if(playback_mode == CLICK_TO_PLAY) {
				if(firstLoop) {
					autoplay = true;
					return;
				} else if(player_state == READY_STATE) {
					playerplay();
					return;
				}
			}
			if(error_code == 10) {
				showDocument(lockedInfo, target);
			} else {
				if(address != null) {
					showDocument(address, target);
				}
			}
		}
	}

	public synchronized void mouseEntered(MouseEvent me) {
		
		if(controls_float ) {
			if(player_state != PLAY_STATE) {
				myPaint();
			}
		}
		if(playback_mode == ROLLOWER_TO_PLAY) {
			if(firstLoop) {
				autoplay = true;
				return;
			} else if(player_state == READY_STATE) {
				playerplay();
			}
		}
	
	}

	public void mouseExited(MouseEvent me) {
		if(controls_float && (me.getSource() == this)) {

			if(player_state != PLAY_STATE) {
				myPaint();
			}
		}
	}

	public synchronized void playPause() {
				if(player_state == PLAY_STATE) {
					playerpause();
				} else if((player_state == READY_STATE) || (player_state == PAUSE_STATE)) {
					playerplay();
				}
	}
	public synchronized void stopPlay() {
		if( zoomThread != null )
			zoomThread.stopZooming();
		if( panThread != null )
			panThread.stopPanning();
		//if(stopButton.isEnabled()) {
		//	playerrewind();
		//}
	}
	
	public synchronized void mutePlay() {
		if(audio_enabled) {
			playersetmute(!mute);
		}
	}
	public synchronized void zoomInPlay() {
		zoomIn();
	}
	public synchronized void zoomOutPlay() {
		zoomOut();
	}
	
	public synchronized void zoomInMouseWheel() {
		zoomInWheel();
	}
	
	public synchronized void zoomOutMouseWheel() {
		zoomOutWheel();
	}
	
	public synchronized void panLeftPlay() {
		panLeft();
	}
	public synchronized void panRightPlay() {
		panRight();
	}
	public synchronized void panUpPlay() {
		panUp();
	}
	public synchronized void panDownPlay() {
		panDown();
	}
	public synchronized void mousePressed(MouseEvent me) {
		if(! linux_platform && me.isPopupTrigger()) {
			setMenuState();
			if(runInApplet) {
				popupMenu.show(this, me.getX(), me.getY());
			} else {
				popupMenu.show(frame, me.getX(), me.getY());
			}
			return;
		}
	}
	
	private void zoomIn() {
		if( zoomThread == null || !zoomThread.isAlive()) {
			zoomThread = new ZoomThread(viewPort, 100);
			zoomThread.start();
		}
		zoomThread.setIsZoomingIn();
	}
	private void zoomOut() {
		if( zoomThread == null || !zoomThread.isAlive()) {
			zoomThread = new ZoomThread(viewPort, 100);
			zoomThread.start();
		} 
		zoomThread.setIsZoomingOut();
	}
	
	private void zoomInWheel() {
		if( zoomThread == null || !zoomThread.isAlive()) {
			zoomThread = new ZoomListener(viewPort);
			zoomThread.start();
		}
		synchronized(zoomThread) {
			zoomThread.setIsZoomingIn();
			zoomThread.notify();
		}
	}
	private void zoomOutWheel() {
		if( zoomThread == null || !zoomThread.isAlive()) {
			zoomThread = new ZoomListener(viewPort);
			zoomThread.start();
		}
		synchronized(zoomThread) {
			zoomThread.setIsZoomingOut();
			zoomThread.notify();				
		}
	}
	
	private void panLeft() {
		if( panThread == null || !panThread.isAlive()) {
			panThread = new PanThread(viewPort, 100);
			panThread.setIsPanningLeft();
			panThread.start();
		} else {
			panThread.setIsPanningLeft();
		}
	}
	private void panRight() {
		if( panThread == null || !panThread.isAlive()) {
			panThread = new PanThread(viewPort, 100);
			panThread.setIsPanningRight();
			panThread.start();
		} else {
			panThread.setIsPanningRight();
		}
	}
	private void panUp() {
		if( panThread == null || !panThread.isAlive()) {
			panThread = new PanThread(viewPort, 100);
			panThread.setIsPanningUp();
			panThread.start();
		} else {
			panThread.setIsPanningUp();
		}
	}
	private void panDown() {
		if( panThread == null || !panThread.isAlive()) {
			panThread = new PanThread(viewPort, 100);
			panThread.setIsPanningDown();
			panThread.start();
		} else {
			panThread.setIsPanningDown();
		}
	}
	
	private void showDocument(String address, String target) {
		if(runInApplet) {
			try {
				URL url = null;
				try {
					url = new URL(address);
				} catch (MalformedURLException ex) {
					url = new URL(getCodeBase(), address);
				}
				getAppletContext().showDocument(url, target);
			} catch (Exception ex) {
				System.out.println(ex);
			}
		}
	}

	public void mouseReleased(MouseEvent me) {
		if (me.isPopupTrigger() || ((me.getModifiers() & MouseEvent.BUTTON3_MASK) != 0)) {
			setMenuState();
			if(runInApplet) {
				popupMenu.show(this, me.getX(), me.getY());
			} else {
				popupMenu.show(frame, me.getX(), me.getY());
			}
		}
	}

	private MediaTracker scaleMediaTracker = new MediaTracker(this);
	private int scaleImageIndex = 0;
	
	private Image[] loadScreenImages(String fileName, String range) {
		try {
			int start_range = -1;
			int end_range = -1;
			int images_count = 1;
			int minusIndex = range.indexOf('-');
			if((minusIndex > 0) && (minusIndex < (range.length() - 1))) {
				try {
					start_range = Integer.parseInt(range.substring(0, minusIndex));
					end_range = Integer.parseInt(range.substring(minusIndex + 1));
					if(start_range < end_range) {
						images_count = end_range - start_range + 1;  
					}
				} catch (Exception ex) {
				}
			}
			Image[] images = new Image[images_count];
			for(int i = 0; i < images_count; i++) {
				String imageName = fileName; 
				if(images_count > 1) {
					int dotIndex = fileName.lastIndexOf('.'); 
					if(dotIndex >= 0) {
						imageName = fileName.substring(0, dotIndex) + (start_range + i) + fileName.substring(dotIndex);  
					}
				}
				images[i] = getImage(imageName, false);
			}
			if(runInApplet) {
				for(int i = 0; i < images_count; i++) {
					if((images[i].getWidth(this) != movie_window_width) || (images[i].getHeight(this) != movie_window_height))  {
						images[i] = images[i].getScaledInstance(movie_window_width, movie_window_height, Image.SCALE_AREA_AVERAGING);
						scaleMediaTracker.addImage(images[i], scaleImageIndex++);
					}
				}
			}
			return images;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	private Image getImage(String imageName, boolean button) {
		MediaTracker mediaTracker = new MediaTracker(this);
		Image image = null;
		URL url = null;	
		try {
			if(runInApplet) {
				URL codebase = getCodeBase();
				url = new URL(codebase, imageName);
			} else {
				url = new URL("file://" + new File(imageName).getAbsolutePath());
			}
			image = Toolkit.getDefaultToolkit().getImage(url);
		} catch (Exception ex) {
		}
		if(image != null) {
			mediaTracker.addImage(image, 0);
			try {
				mediaTracker.waitForAll();
			} catch (Exception ex) {
			}
		}
		if((image == null) || (checkImage(image, this) == ImageObserver.ERROR)) {
			if(!runInApplet || button) {
				image = createImage(17, 16);
			} else {
				image = createImage(movie_window_width, movie_window_height);
			}
			if(image != null) {
				Graphics imageGr = image.getGraphics();				
				imageGr.setColor(Color.white);
				if(button) {
					imageGr.clearRect(0, 0, 17, 16);
					imageGr.setColor(Color.black);
					imageGr.drawRect(0, 0, 16, 15);
				} else {
					if(runInApplet) {
						imageGr.clearRect(0, 0, movie_window_width, movie_window_height);
					} else {
						imageGr.clearRect(0, 0, 17, 16);
					}
				}
			}
		}
		return image;
	}
	
	/**
	 * Gets the image that embedded into applet's jar file by it's path. 
	 * @param image_path the path to the resource image. 
	 * @return the image which was loaded.
	 */
	private Image getEmbeddedImage(String image_path) {
		if(image_path == null) {
			return null;
		}
		InputStream is = getClass().getResourceAsStream(image_path);
		int c = 0;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Image image = null;
		try { 
			while((c = is.read()) != -1) {
				baos.write(c);
			}
			is.close();
			baos.close();
			image = Toolkit.getDefaultToolkit().createImage(baos.toByteArray());
		} catch (Exception ex) {
		}
		if(image != null) {
			MediaTracker mediaTracker = new MediaTracker(this);
			mediaTracker.addImage(image, 0);
			try {
				mediaTracker.waitForAll();
			} catch (Exception ex) {
			}
		}
		return image;
	}
	
	
	private int parsePercentValue(int base, String sValue, int defaultValue) {
		int value = defaultValue;
		if(sValue != null) {
			try {
				if(sValue.charAt(sValue.length() - 1) == '%') {
					value = base * Integer.parseInt(sValue.substring(0, sValue.length() - 1)) / 100;
				} else {
					value = Integer.parseInt(sValue);
				}
			} catch (Exception ex) {
			}
			if(value < 0) {
				value = - value;
			}
		}
		return value;
	}
	
	private void writeStream(String fileName, Vector dataSamples) {
		FileOutputStream fos = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataChannel dataStream = new DataChannel(-1, dataBuffer, dataSamples);
			try {
				int c = 0;
				while((c = dataStream.read()) != -1) {
					baos.write(c);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} 
			fos = new FileOutputStream(fileName);
			fos.write(baos.toByteArray());
			fos.close(); 		
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if(fos != null) {
				try {
					fos.close();
				} catch (Exception ex) {
				}
			}
		}
	}
	
	public static void main(String[] args) {
		if(args.length < 1) {
			System.out.println("Usage: MPEG4 filename");
			return;
		}
		
		FramePumpMPEG4 player = new FramePumpMPEG4();		
		player.runInApplet = false;
		player.allow_save = false;
		player.licenseIsCorrect = true;
		player.smooth_video = true;
		player.control_location = "control_set/";
		player.preScreenImgs = player.loadScreenImages("pre_post/pre.gif", "1-4");
		player.pre_screen_loop = true;
		player.pre_screen_delay = 500;
		player.postScreenImgs = player.loadScreenImages("pre_post/post.gif", "");
		if(!AudioPlayer.isSoundEnabled()) {
			player.audio_enabled = false;
			player.mute = true;
		}
		try {
			Class.forName("java.awt.Graphics2D");
			player.java2_platform = true;
		} catch (Exception ex) {
		}
		player.myPaint();
		player.loadFile(args[0]);		 
		player.parseFile();
//		player.writeStream(args[1], player.videoSamples);
		player.writeStream(args[2], player.audioSamples);
		player.playerstart();
	}
}
