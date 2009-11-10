package org.yestech.multimedia.video.mpeg4.video;

import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;

import org.yestech.multimedia.video.mpeg4.FramePumpMPEG4;

/**
 * The <code>MPEG4Decoder</code>
 * 
 * @author Konstantin Belous
 */
public final class MPEG4Decoder implements Runnable {
	
	/** The visual object sequence start code. */
	public final static int VISUAL_OBJECT_SEQUENCE_START_CODE = 0xB0;
	/** The visual object start code. */
	public final static int VISUAL_OBJECT_START_CODE = 0xB5;

	/** The video object start code. */
	public final static int VIDEO_OBJECT_START_CODE = 0x1F;
	/** The video object layer start code. */
	public final static int VIDEO_OBJECT_LAYER_START_CODE = 0x2F;
	
	/** The group of vop start code value. */
	public final static int GROUP_VOP_START_CODE = 0xB3;
	/** The vop start code value. */
	public final static int VOP_START_CODE = 0xB6;
	
	/** The resync marker value. */
	public final static int RESYNC_MARKER = 1;
	
	
	/** 'Intra coded' VOP coding type. */
	public final static int I_VOP = 0;
	/** 'Predictive coded' VOP coding type. */
	public final static int P_VOP = 1;
	/** 'Bidirectionally-predictive coded' VOP coding type. */
	public final static int B_VOP = 3;
	/** 'Sprite' VOP coding type. */
	public final static int S_VOP = 4;
	
	/** The extended PAR pixel aspect ratio code. */
	public final static int EXTENDED_ASPECT_RATIO = 15;
	
	/** The 'rectangular' shape type of a video object layer. */
	public final static byte RECTANGULAR_SHAPE = 0;
	/** The 'binary' shape type of a video object layer. */
	public final static byte BINARY_SHAPE = 1;
	/** The 'binary only' shape type of a video object layer. */
	public final static byte BINARY_ONLY_SHAPE = 2;
	/** The 'grayscale' shape type of a video object layer. */
	public final static byte GRAYSCALE_SHAPE = 3;
	
	/** The 'sprite not used' sprite coding mode. */
	public final static byte NOT_USED_SPRITE = 0;
	/** The 'static (Basic/Low Latency)' sprite coding mode. */
	public final static byte STATIC_SPRITE = 1;
	/** The 'GMC (Global Motion Compensation)' sprite coding mode. */
	public final static byte GMC_SPRITE = 2;

	/** The 'Stop' transmit mode of the sprite object. */
	public final static byte STOP_TRANSMIT_MODE = 0;
	/** The 'Piece' transmit mode of the sprite object. */
	public final static byte PIECE_TRANSMIT_MODE = 1;
	/** The 'Update' transmit mode of the sprite object. */
	public final static byte UPDATE_TRANSMIT_MODE = 2;
	/** The 'Pause' transmit mode of the sprite object. */
	public final static byte PAUSE_TRANSMIT_MODE = 3;
	
	/** The 'Direct' motion mode. */
	public final static byte DIRECT_MOTION_MODE = 2;
	/** The 'Interpolate' motion mode. */
	public final static byte INTERPOLATE_MOTION_MODE = 2;
	/** The 'Backward' motion mode. */
	public final static byte BACKWARD_MOTION_MODE = 3;
	/** The 'Forward' motion mode. */
	public final static byte FORWARD_MOTION_MODE = 4;
	
	/** The default matrix for intra blocks. */
	public final static int[] DEFAULT_INTRA_QUANT_MAT = {
		8, 17, 18, 19, 21, 23, 25, 27,
		17, 18, 19, 21, 23, 25, 27, 28,
		20, 21, 22, 23, 24, 26, 28, 30,
		21, 22, 23, 24, 26, 28, 30, 32,
		22, 23, 24, 26, 28, 30, 32, 35,
		23, 24, 26, 28, 30, 32, 35, 38,
		25, 26, 28, 30, 32, 35, 38, 41,
		27, 28, 30, 32, 35, 38, 41, 45
	};

	/** The default matrix for non-intra blocks. */
	public final static int[] DEFAULT_NON_INTRA_QUANT_MAT = {
		16, 17, 18, 19, 20, 21, 22, 23,
		17, 18, 19, 20, 21, 22, 23, 24,
		18, 19, 20, 21, 22, 23, 24, 25,
		19, 20, 21, 22, 23, 24, 26, 27,
		20, 21, 22, 23, 25, 26, 27, 28,
		21, 22, 23, 24, 26, 27, 28, 30,
		22, 23, 24, 26, 27, 28, 30, 31,
		23, 24, 25, 27, 28, 30, 31, 33
	};

	/** The Alternate-Horizontal scan pattern. */
	public static int[] ALTERNATE_HORIZONTAL_SCAN_TABLE = {
		0, 1, 2, 3, 8, 9, 16, 17, 
		10, 11, 4, 5, 6, 7, 15, 14, 
		13, 12, 19, 18, 24, 25, 32, 33, 
		26, 27, 20, 21, 22, 23, 28, 29, 
		30, 31, 34, 35, 40, 41, 48, 49, 
		42, 43, 36, 37, 38, 39, 44, 45, 
		46, 47, 50, 51, 56, 57, 58, 59, 
		52, 53, 54, 55, 60, 61, 62, 63, 
	};

	/** The Alternate-Vertical scan pattern. */
	public static int[] ALTERNATE_VERTICAL_SCAN_TABLE = {
		0, 8, 16, 24, 1, 9, 2, 10, 
		17, 25, 32, 40, 48, 56, 57, 49, 
		41, 33, 26, 18, 3, 11, 4, 12, 
		19, 27, 34, 42, 50, 58, 35, 43, 
		51, 59, 20, 28, 5, 13, 6, 14, 
		21, 29, 36, 44, 52, 60, 37, 45, 
		53, 61, 22, 30, 7, 15, 23, 31, 
		38, 46, 54, 62, 39, 47, 55, 63, 
	};
	
	/** The Zigzag scan pattern. */
	public static int[] ZIGZAG_SCAN_TABLE = {
		0, 1, 8, 16, 9, 2, 3, 10, 
		17, 24, 32, 25, 18, 11, 4, 5, 
		12, 19, 26, 33, 40, 48, 41, 34, 
		27, 20, 13, 6, 7, 14, 21, 28, 
		35, 42, 49, 56, 57, 50, 43, 36, 
		29, 22, 15, 23, 30, 37, 44, 51, 
		58, 59, 52, 45, 38, 31, 39, 46, 
		53, 60, 61, 54, 47, 55, 62, 63, 
	};	
		
	/** The LMAX table of intra macroblocks. */
	public final static int[][][] INTRA_LMAX_TAB = {
		{
			{0, 27},
			{1, 10},
			{2, 5},
			{3, 4},
			{7, 3},
			{9, 2},
			{14, 1}
		},
		{
			{0, 8},
			{1, 3},
			{6, 2},
			{20, 1}
		}
	};

	/** The LMAX table of inter macroblocks. */
	public final static int[][][] INTER_LMAX_TAB = {
		{
			{0 ,12 },
			{1 ,6 },
			{2 ,4 },
			{6 ,3 },
			{10 ,2 },
			{26 ,1 }
		},
		{
			{0 ,3 },
			{1 ,2 },
			{40 ,1 }
		}
	};

	/** The RMAX table of intra macroblocks. */
	public final static int[][][] INTRA_RMAX_TAB = {
		{
			{1, 14},
			{2, 9},
			{3, 7},
			{4, 3},
			{5, 2},
			{10, 1},
			{27, 0}
		},
		{
			{1, 20},
			{2, 6},
			{3, 1},
			{8, 0}
		}
	};

	/** The RMAX table of inter macroblocks. */
	public final static int[][][] INTER_RMAX_TAB = {
		{
			{1 ,26 },
			{2 ,10 },
			{3 ,6 },
			{4 ,2 },
			{6 ,1 },
			{12 ,0 }
		},
		{
			{1 ,40 },
			{2 ,1 },
			{3 ,0 }
		}
	};
	
	public static final int[] aux_comp_count = {1, 1, 2, 2, 3, 1, 2, 1, 1, 2, 3, 2, 3, 1, 1, 1};
	
	/** The input MPEG4 video bitstream. */
	private BitStream videoStream = null;
	/** The VLC (Huffman) decoder. */
	private Huffman huffman = null; 
	/** The thread of the video decoder. */
	private volatile Thread videoThread = null;
	/** <tt>True</tt>, if the decoder starts to decode the video stream. */
	private boolean firstLoop = true;
	/** The fps value of the video stream. */
	private double video_rate;
	/** The size in bytes of the video stream. */
	private int video_size;
	
	/** The reference to the applet's object. */
	private FramePumpMPEG4 applet;
	
	/**
	 * Constructs an <code>MPEG4Decoder</code> object.
	 * @param applet the reference to the applet's object.
	 * @param bitstream the input MPEG4 video bitstream.
	 */
	public MPEG4Decoder(FramePumpMPEG4 applet, BitStream videoStream, int width, int height, double video_rate, int video_size) {
		super();
		this.videoStream = videoStream;
		this.huffman = new Huffman(videoStream);
		this.applet = applet;
		this.video_rate = video_rate;
		this.video_size = video_size;
		this.video_object_layer_width = (short)width;
		this.video_object_layer_height = (short)height;
		
		this.vop_time_increment_resolution = (int)video_rate;
		while(((double)vop_time_increment_resolution / video_rate) - (int)((double)vop_time_increment_resolution / video_rate) > 0.000000001d) {
			vop_time_increment_resolution ++;
		}
		vop_time_increment_length = 31;
		while(((vop_time_increment_resolution >>> vop_time_increment_length) == 0) && (vop_time_increment_length > 0)) {
			vop_time_increment_length --;
		}
		vop_time_increment_length ++;
		vop_id_length = vop_time_increment_length + 3;
		vop_id_length = vop_id_length > 15 ? 15 : vop_id_length;
		
		videoThread = new Thread(this, "Video Thread");
		videoThread.start(); 
	}
	
	/**
	 * Stops the playing of the video.
	 */	
	public synchronized void stop() {
		if(videoThread != null) {
			Thread workThread = videoThread; 
			videoThread = null;
			workThread.interrupt();
			try {
				workThread.join(2000);
			} catch (Exception ex) {
			}
		}
	}
	
	public void run() {
		try {
			while((videoThread != null) && (decodeStream() == true)) {
			}
		} catch (Exception ex ){
		} finally {
			videoThread = null; 
		}
	}

	private boolean printed_video_info = false;
	
	private boolean decodeStream() {
		try {
			int start_code = videoStream.get_next_start_code();
			switch (start_code) {
				case VISUAL_OBJECT_SEQUENCE_START_CODE:
					decode_VisualObjectSequence();
					break;
				case VISUAL_OBJECT_START_CODE:
					decode_VisualObject();
					break;
				case GROUP_VOP_START_CODE:
					decode_Group_of_VideoObjectPlane();
					break;
				case VOP_START_CODE:
					if(!printed_video_info) {
						video_rate += 0.005d; // try to round the video_rate to the nearest double value 
						int iVideo_rate = (int)video_rate;
						int duration = (int)Math.round(applet.get_video_length() / 1000d);
						System.out.println("Video: MPEG 4 " + ((profile_and_level_indication & 0xf0) == 0 ? "Simple " : "") + "@ L" + (profile_and_level_indication & 0x0f) + ", " + duration + " secs, " + (int)(8 * video_size / (duration * 1000)) + " kbps, " + video_object_layer_width + "x" + video_object_layer_height + " @ " + iVideo_rate + (((video_rate - iVideo_rate) == 0) ? "" : "." + Integer.toString((int)(100 * (video_rate - iVideo_rate)))) + " fps");
						printed_video_info = true;
					}
					decode_VideoObjectPlane();
					break;
				default:
					if(start_code <= VIDEO_OBJECT_START_CODE) {
						break;
					}
					if(start_code <= VIDEO_OBJECT_LAYER_START_CODE) {
						video_object_layer_id = (byte)(start_code & 0x0f); 
						decode_VideoObjectLayer();
						break;
					}
//					System.out.println("Unknown start code: " + Integer.toHexString(start_code));
			}
			return true;
		} catch (EOFException eofex) {
			applet.playerend();
			return false;
		} catch (InterruptedIOException ioe) {
			return false;
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return true;
		}
	}
	/** The profile and level indication of the visual stream. */
	private short profile_and_level_indication = 1;
	
	private void decode_VisualObjectSequence() throws IOException {
		profile_and_level_indication = (short)videoStream.next_bits(8);
/*		
		System.out.println("VisualObjectSequence()");
		System.out.println("Profile And Level Indication = " + profile_and_level_indication);
*/		
	}
	
	private boolean is_visual_object_identifier;
	private byte visual_object_verid;
	private byte visual_object_priority;
	private byte visual_object_type;
	
	
	private boolean video_signal_type;
	private byte video_format;
	private boolean video_range;
	private boolean colour_description;
	private short colour_primaries;
	private short transfer_characteristics;
	private short matrix_coefficients;

	private void decode_VisualObject() throws IOException {
		is_visual_object_identifier = videoStream.next_bit();
		if(is_visual_object_identifier) {
			visual_object_verid = (byte)videoStream.next_bits(4);
			visual_object_priority = (byte)videoStream.next_bits(3);
		}
		visual_object_type = (byte)videoStream.next_bits(4);
		if(visual_object_type == 1) {
			video_signal_type = videoStream.next_bit();
			if(video_signal_type) {
				video_format = (byte)videoStream.next_bits(3);
				video_range = videoStream.next_bit();
				colour_description = videoStream.next_bit();
				if(colour_description) {
					colour_primaries = (short)videoStream.next_bits(8);
					transfer_characteristics = (short)videoStream.next_bits(8);
					matrix_coefficients = (short)videoStream.next_bits(8);
				}
			}
		}
/*		
		System.out.println("VisualObject()");
		System.out.println("is_visual_object_identifier = " + is_visual_object_identifier);
		if(is_visual_object_identifier) {
			System.out.println("visual_object_verid = " + visual_object_verid);
			System.out.println("visual_object_priority = " + visual_object_priority);
		}
		System.out.println("visual_object_type = " + visual_object_type);
		System.out.println("video_signal_type = " + video_signal_type);
		if(video_signal_type) {
			System.out.println("video_format = " + video_format);
			System.out.println("video_range = " + video_range);
			System.out.println("colour_description = " + colour_description);
			if(colour_description) {
				System.out.println("colour_primaries = " + colour_primaries);
				System.out.println("transfer_characteristics = " + transfer_characteristics);
				System.out.println("matrix_coefficients = " + matrix_coefficients);
			}
		}
*/		
	}
	
	private byte video_object_layer_id;
	
	private boolean short_video_header = false;
	private boolean random_accessible_vol;
	private short video_object_type_indication;
	private boolean is_object_layer_identifier;
	private byte video_object_layer_verid = 1;
	private byte video_object_layer_priority;
	/** The value of pixel aspect ratio. */
	private byte aspect_ratio_info;
	private short par_width;
	private short par_height;
	private boolean vol_control_parameters;
	private byte chroma_format;
	private boolean low_delay;
	private boolean vbv_parameters;
	private short first_half_bit_rate;
	private short latter_half_bit_rate;
	private short first_half_vbv_buffer_size;
	private byte latter_half_vbv_buffer_size;
	private short first_half_vbv_occupancy;
	private short latter_half_vbv_occupancy;
	
	private byte video_object_layer_shape = RECTANGULAR_SHAPE;
	private byte video_object_layer_shape_extension;
	private int vop_time_increment_resolution;
	private int vop_time_increment_length;
	private int vop_id_length = 8;
	private boolean fixed_vop_rate = false;
	private int fixed_vop_time_increment;
	private short video_object_layer_width;
	private short video_object_layer_height;
	private boolean interlaced = false;
	private boolean obmc_disable = true;	
	private byte sprite_enable = 0;
	private byte no_of_sprite_warping_points;
	private boolean sadct_disable = true;
	private boolean low_latency_sprite_enable;
	private boolean not_8_bit = false;
	/** The number of bits used to represent quantiser parameters. */
	private byte quant_precision = 5;
	private byte bits_per_pixel = 8;
	
	private byte quant_type = 0;
	private boolean load_intra_quant_mat;
	private boolean load_nonintra_quant_mat;
	
	private int[] intra_quant_mat = DEFAULT_INTRA_QUANT_MAT;
	private int[] nonintra_quant_mat = DEFAULT_NON_INTRA_QUANT_MAT;
	
	private boolean load_intra_quant_mat_grayscale;
	private boolean load_nonintra_quant_mat_grayscale;
	
	private boolean quarter_sample = false;
	
	private boolean complexity_estimation_disable = true;
	private boolean resync_marker_disable = true;
	private boolean data_partitioned = false;
	private boolean reversible_vlc = false;
	private boolean newpred_enable = false;
	private byte requested_upstream_message_type;
	private boolean newpred_segment_type;
	private boolean reduced_resolution_vop_enable;
	private boolean scalability = false;
	private boolean hierarchy_type;
	private byte ref_layer_id;
	private boolean ref_layer_sampling_direc;
	private byte hor_sampling_factor_n;
	private byte hor_sampling_factor_m;
	private byte vert_sampling_factor_n;
	private byte vert_sampling_factor_m;
	private boolean enhancement_type;
	private boolean use_ref_shape;
	private boolean use_ref_texture;
	private byte shape_hor_sampling_factor_n;
	private byte shape_hor_sampling_factor_m;
	private byte shape_vert_sampling_factor_n;
	private byte shape_vert_sampling_factor_m;
	
	private byte estimation_method;
	private boolean shape_complexity_estimation_disable;
	private boolean opaque;
	private boolean transparent;
	private boolean intra_cae;
	private boolean inter_cae;
	private boolean no_update;
	private boolean upsampling;
	private boolean texture_complexity_estimation_set_1_disable;
	private boolean intra_blocks;
	private boolean inter_blocks;
	private boolean inter4v_blocks;
	private boolean not_coded_blocks;
	private boolean texture_complexity_estimation_set_2_disable;
	private boolean dct_coefs;
	private boolean dct_lines;
	private boolean vlc_symbols;
	private boolean vlc_bits;
	private boolean motion_compensation_complexity_disable;
	private boolean apm;
	private boolean npm;
	private boolean interpolate_mc_q;
	private boolean forw_back_mc_q;
	private boolean halfpel2;
	private boolean halfpel4;
	private boolean version2_complexity_estimation_disable;
	private boolean sadct;
	private boolean quarterpel;
	
	private byte sprite_transmit_mode;
	
	private void decode_VideoObjectLayer() throws IOException {
		// init predefined values
		bits_per_pixel = 8;
		quant_precision = 5;
		intra_quant_mat = DEFAULT_INTRA_QUANT_MAT;
		nonintra_quant_mat = DEFAULT_NON_INTRA_QUANT_MAT;
		quarter_sample = false;
		newpred_enable = false;
		sprite_transmit_mode = PIECE_TRANSMIT_MODE;
		transparent = false;
		data_partitioned = false;
		not_8_bit = false;
		
		short_video_header = false;
		random_accessible_vol = videoStream.next_bit();
		video_object_type_indication = (short)videoStream.next_bits(8);
		is_object_layer_identifier = videoStream.next_bit();
		if(is_object_layer_identifier) {
			video_object_layer_verid = (byte)videoStream.next_bits(4);
			video_object_layer_priority = (byte)videoStream.next_bits(3);
		}
		aspect_ratio_info = (byte)videoStream.next_bits(4);
		if(aspect_ratio_info == EXTENDED_ASPECT_RATIO) {
			par_width = (short)videoStream.next_bits(8);
			par_height = (short)videoStream.next_bits(8);
		}
		vol_control_parameters = videoStream.next_bit();
		if(vol_control_parameters) {
			chroma_format = (byte)videoStream.next_bits(2);
			low_delay = videoStream.next_bit();
			vbv_parameters = videoStream.next_bit();
			if(vbv_parameters) {
				first_half_bit_rate = (short)videoStream.next_bits(15);
				videoStream.marker_bit(); 
				latter_half_bit_rate = (short)videoStream.next_bits(15);
				videoStream.marker_bit(); 
				first_half_vbv_buffer_size = (short)videoStream.next_bits(15);
				videoStream.marker_bit(); 
				latter_half_vbv_buffer_size = (byte)videoStream.next_bits(3);
				first_half_vbv_occupancy = (short)videoStream.next_bits(11);
				videoStream.marker_bit(); 
				latter_half_vbv_occupancy = (short)videoStream.next_bits(15);
				videoStream.marker_bit(); 
			}			
		}
		video_object_layer_shape = (byte)videoStream.next_bits(2);
		if((video_object_layer_shape == GRAYSCALE_SHAPE) && (video_object_layer_verid != 1)) {
			video_object_layer_shape_extension = (byte)videoStream.next_bits(4);
		}
		videoStream.marker_bit(); 
		vop_time_increment_resolution = (int)videoStream.next_bits(16);
		// calculates the vop_time_increment_length (number of bits required to store vop_time_increment_resolution) 
		vop_time_increment_length = 31;
		while(((vop_time_increment_resolution >>> vop_time_increment_length) == 0) && (vop_time_increment_length > 0)) {
			vop_time_increment_length --;
		}
		vop_time_increment_length ++;
		if(prev_vop_time_increment == -1) {
			prev_vop_time_increment = 0;
		}
		vop_id_length = vop_time_increment_length + 3;
		vop_id_length = vop_id_length > 15 ? 15 : vop_id_length;
		videoStream.marker_bit(); 
		fixed_vop_rate = videoStream.next_bit();
		if(fixed_vop_rate) {
			fixed_vop_time_increment = (int)videoStream.next_bits(vop_time_increment_length);
		}
		if(video_object_layer_shape != BINARY_ONLY_SHAPE) {
			if(video_object_layer_shape == RECTANGULAR_SHAPE) {
				videoStream.marker_bit(); 
				video_object_layer_width = (short)videoStream.next_bits(13);
				videoStream.marker_bit(); 
				video_object_layer_height = (short)videoStream.next_bits(13);
				videoStream.marker_bit(); 
			}
			interlaced = videoStream.next_bit();
			obmc_disable = videoStream.next_bit();
			if(video_object_layer_verid == 1) {
				sprite_enable = (byte)videoStream.next_bits(1);
			} else {
				sprite_enable = (byte)videoStream.next_bits(2);
			}
			if((sprite_enable == STATIC_SPRITE) || (sprite_enable == GMC_SPRITE)) {
				// skip sprite information
				if(sprite_enable != GMC_SPRITE) {
					videoStream.skip_bits(13 + 1 + 13 + 1 + 13 + 1 + 13 + 1);
				}
				no_of_sprite_warping_points = (byte)videoStream.next_bits(6);
				videoStream.skip_bits(2 + 1);
				if(sprite_enable != GMC_SPRITE) {
					low_latency_sprite_enable = videoStream.next_bit();
				}
			}
			if((video_object_layer_verid != 1) && (video_object_layer_shape != RECTANGULAR_SHAPE)) {
				sadct_disable = videoStream.next_bit();
			}
			not_8_bit = videoStream.next_bit();
			if(not_8_bit) {
				quant_precision = (byte)videoStream.next_bits(4);
				bits_per_pixel = (byte)videoStream.next_bits(4);
			}
			if(video_object_layer_shape == GRAYSCALE_SHAPE) {
				// skips the data of an 'grayscale' object
				videoStream.skip_bits(3);
			}
			quant_type = (byte)videoStream.next_bits(1);
			if(quant_type == 1) {
				load_intra_quant_mat = videoStream.next_bit();
				if(load_intra_quant_mat) {
					intra_quant_mat = read_quant_matrix();
				}
				load_nonintra_quant_mat = videoStream.next_bit();
				if(load_nonintra_quant_mat) {
					nonintra_quant_mat = read_quant_matrix();
				}
				if(video_object_layer_shape == GRAYSCALE_SHAPE) {
					// skip the matrix information for the grayscale object layer
					for(int i = 0; i < aux_comp_count[video_object_layer_shape_extension]; i++) {
						load_intra_quant_mat_grayscale = videoStream.next_bit();
						if(load_intra_quant_mat_grayscale) {
							read_quant_matrix();
						}
						load_nonintra_quant_mat_grayscale = videoStream.next_bit();
						if(load_nonintra_quant_mat_grayscale) {
							read_quant_matrix();
						}
					}
				}
			}
			if(video_object_layer_verid != 1) {
				quarter_sample = videoStream.next_bit();
			}
			complexity_estimation_disable = videoStream.next_bit();
			if(!complexity_estimation_disable) {
				estimation_method = (byte)videoStream.next_bits(2);
				if ((estimation_method == 0) || (estimation_method == 1)) {
					shape_complexity_estimation_disable = videoStream.next_bit();
					if(! shape_complexity_estimation_disable) {
						opaque = videoStream.next_bit();
						transparent = videoStream.next_bit();
						intra_cae = videoStream.next_bit();
						inter_cae = videoStream.next_bit();
						no_update = videoStream.next_bit();
						upsampling = videoStream.next_bit();
					}
					texture_complexity_estimation_set_1_disable = videoStream.next_bit();
					if(! texture_complexity_estimation_set_1_disable) {
						intra_blocks = videoStream.next_bit();
						inter_blocks = videoStream.next_bit();
						inter4v_blocks = videoStream.next_bit();
						not_coded_blocks = videoStream.next_bit();
					}
					videoStream.marker_bit();
					texture_complexity_estimation_set_2_disable = videoStream.next_bit();
					if(! texture_complexity_estimation_set_2_disable) {
						dct_coefs = videoStream.next_bit();
						dct_lines = videoStream.next_bit();
						vlc_symbols = videoStream.next_bit();
						vlc_bits = videoStream.next_bit();
					}
					motion_compensation_complexity_disable = videoStream.next_bit();
					if(! motion_compensation_complexity_disable) {
						apm = videoStream.next_bit();
						npm = videoStream.next_bit();
						interpolate_mc_q = videoStream.next_bit();
						forw_back_mc_q = videoStream.next_bit();
						halfpel2 = videoStream.next_bit();
						halfpel4 = videoStream.next_bit();
					}
					videoStream.marker_bit();
					if(estimation_method == 1) {
						version2_complexity_estimation_disable = videoStream.next_bit();
						if(! version2_complexity_estimation_disable) {
							sadct = videoStream.next_bit();
							quarterpel = videoStream.next_bit();
						}
					}
				}
			}
			resync_marker_disable = videoStream.next_bit();
			data_partitioned = videoStream.next_bit();
			
			if(data_partitioned) {
				reversible_vlc = videoStream.next_bit();
			}
			if(video_object_layer_verid != 1) {
				newpred_enable = videoStream.next_bit();
				if(newpred_enable) {
					requested_upstream_message_type = (byte)videoStream.next_bits(2);
					newpred_segment_type = videoStream.next_bit();
				}
				reduced_resolution_vop_enable = videoStream.next_bit();
			}
			scalability = videoStream.next_bit();
			if(scalability) {
				hierarchy_type = videoStream.next_bit();
				ref_layer_id = (byte)videoStream.next_bits(4);
				ref_layer_sampling_direc = videoStream.next_bit();
				hor_sampling_factor_n = (byte)videoStream.next_bits(5);
				hor_sampling_factor_m = (byte)videoStream.next_bits(5);
				vert_sampling_factor_n = (byte)videoStream.next_bits(5);
				vert_sampling_factor_m = (byte)videoStream.next_bits(5);
				enhancement_type = videoStream.next_bit();
				if((video_object_layer_shape == BINARY_SHAPE) && (hierarchy_type == false)) {
					use_ref_shape = videoStream.next_bit();
					use_ref_texture = videoStream.next_bit();
					shape_hor_sampling_factor_n = (byte)videoStream.next_bits(5);
					shape_hor_sampling_factor_m = (byte)videoStream.next_bits(5);
					shape_vert_sampling_factor_n = (byte)videoStream.next_bits(5);
					shape_vert_sampling_factor_m = (byte)videoStream.next_bits(5);
				}
			}
		} else {
			if(video_object_layer_verid != 1) {
				scalability = videoStream.next_bit();
				if(scalability) {
					ref_layer_id = (byte)videoStream.next_bits(4);
					shape_hor_sampling_factor_n = (byte)videoStream.next_bits(5);
					shape_hor_sampling_factor_m = (byte)videoStream.next_bits(5);
					shape_vert_sampling_factor_n = (byte)videoStream.next_bits(5);
					shape_vert_sampling_factor_m = (byte)videoStream.next_bits(5);
				}
			}
			resync_marker_disable = videoStream.next_bit();
		}
/*		
		System.out.println("VisualObjectLayer()");
		System.out.println("random_accessible_vol = " + random_accessible_vol);
		System.out.println("video_object_type_indication = " + video_object_type_indication);
		System.out.println("is_object_layer_identifier = " + is_object_layer_identifier);
		if(is_object_layer_identifier) {
			System.out.println("video_object_layer_verid = " + video_object_layer_verid);
			System.out.println("video_object_layer_priority = " + video_object_layer_priority);
		}
		System.out.println("aspect_ratio_info = " + aspect_ratio_info);
		if(aspect_ratio_info == EXTENDED_ASPECT_RATIO) {
			System.out.println("par_width = " + par_width);
			System.out.println("par_height = " + par_height);
		}
		System.out.println("vol_control_parameters = " + vol_control_parameters);
		if(vol_control_parameters) {
			System.out.println("chroma_format = " + chroma_format);
			System.out.println("low_delay = " + low_delay);
			System.out.println("vbv_parameters = " + vbv_parameters);
			if(vbv_parameters) {
				System.out.println("first_half_bit_rate = " + first_half_bit_rate);
				System.out.println("latter_half_bit_rate = " + latter_half_bit_rate);
				System.out.println("first_half_vbv_buffer_size = " + first_half_vbv_buffer_size);
				System.out.println("latter_half_vbv_buffer_size = " + latter_half_vbv_buffer_size);
				System.out.println("first_half_vbv_occupancy = " + first_half_vbv_occupancy);
				System.out.println("latter_half_vbv_occupancy = " + latter_half_vbv_occupancy);
			}
		}
		System.out.println("video_object_layer_shape = " + video_object_layer_shape);
		if((video_object_layer_shape == GRAYSCALE_SHAPE) && (video_object_layer_verid != 1)) {
			System.out.println("video_object_layer_shape_extension = " + video_object_layer_shape_extension);
		}
		System.out.println("vop_time_increment_resolution = " + vop_time_increment_resolution);
		System.out.println("vop_time_increment_length = " + vop_time_increment_length);
		System.out.println("vop_id_length = " + vop_id_length);
		System.out.println("fixed_vop_rate = " + fixed_vop_rate);
		if(fixed_vop_rate) {
			System.out.println("fixed_vop_time_increment = " + fixed_vop_time_increment);
		}
		if(video_object_layer_shape != BINARY_ONLY_SHAPE) {
			if(video_object_layer_shape == RECTANGULAR_SHAPE) {
				System.out.println("video_object_layer_width = " + video_object_layer_width);
				System.out.println("video_object_layer_height = " + video_object_layer_height);
			}
			System.out.println("interlaced = " + interlaced);
			System.out.println("obmc_disable = " + obmc_disable);
			System.out.println("sprite_enable = " + sprite_enable);
			if((video_object_layer_verid != 1) && (video_object_layer_shape != RECTANGULAR_SHAPE)) {
				System.out.println("sadct_disable = " + sprite_enable);
			}
			System.out.println("not_8_bit = " + not_8_bit);
			if(not_8_bit) {
				System.out.println("quant_precision = " + quant_precision);
				System.out.println("bits_per_pixel = " + bits_per_pixel);
			}
			System.out.println("quant_type = " + quant_type);
			if(quant_type == 1) {
				System.out.println("load_intra_quant_mat = " + load_intra_quant_mat);
			}
			System.out.println("scalability = " + scalability);
		}
		System.out.println("data_partitioned = " + data_partitioned);
		System.out.println("resync_marker_disable = " + resync_marker_disable);
		if(data_partitioned) {
			System.out.println("reversible_vlc = " + reversible_vlc);
		}
		System.out.println("complexity_estimation_disable = " + complexity_estimation_disable);
*/		
	}
	
	/** The time (hours) of the group of video object planes. */
	private int time_code_hours = 0;
	/** The time (minutes) of the group of video object planes. */
	private int time_code_minutes = 0;
	/** The time (seconds) of the group of video object planes. */
	private int time_code_seconds = 0;
	/** The Closed Gov flag of the group of video object planes. */
	private boolean closed_gov = false;
	/** The Broken Link flag of the group of video object planes. */
	private boolean broken_link = false;
	
	private void decode_Group_of_VideoObjectPlane() throws IOException {
		vop_number_in_gop = 0;
		time_code_hours = (int)videoStream.next_bits(5);
		time_code_minutes = (int)videoStream.next_bits(6);
		videoStream.marker_bit(); 
		time_code_seconds = (int)videoStream.next_bits(6);
		closed_gov = videoStream.next_bit();
		broken_link = videoStream.next_bit();
		if(prev_vop_time_increment > 0) {
			prev_vop_time_increment = 0; 
		}
/*		
		System.out.println("Group_of_VideoObjectPlane");
		System.out.println("Time Code Hours = " + time_code_hours);
		System.out.println("Time Code Minutes = " + time_code_minutes);
		System.out.println("Time Code Seconds = " + time_code_seconds);
		System.out.println("Closed Gov = " + closed_gov);
		System.out.println("Broken Link = " + broken_link);
*/		
		return;
	}
	
	/** The coding type of the VOP. */
	private int vop_coding_type;
	/** The number of seconds since the synchronization point. */
	private int modulo_time_base;
	
	private int vop_time_increment;
	
	
	private boolean vop_coded;
	/** The id of VOP. */
	private int vop_number_in_gop = 0;
	private short vop_id = 0;
	private boolean vop_id_for_prediction_indication;
	private short vop_id_for_prediction;
	private byte vop_rounding_type;
	private boolean vop_reduced_resolution;
	private byte intra_dc_vlc_thr;
	private boolean top_field_first;
	private boolean alternate_vertical_scan_flag;
	private short vop_width;
	private short vop_height;
	private short vop_horizontal_mc_spatial_ref;
	private short vop_vertical_mc_spatial_ref;
	private boolean background_composition;
	private boolean change_conv_ratio_disable;
	private boolean vop_constant_alpha;
	private short vop_constant_alpha_value;
	
	private short vop_quant;
	private byte vop_fcode_forward;
	private byte vop_fcode_backward;
	private boolean vop_shape_coding_type;
	
	private boolean load_backward_shape;
	private short backward_shape_width;
	private short backward_shape_height;
	private short backward_shape_horizontal_mc_spatial_ref;
	private short backward_shape_vertical_mc_spatial_ref;
	private boolean load_forward_shape;
	private short forward_shape_width;
	private short forward_shape_height;
	private short forward_shape_horizontal_mc_spatial_ref;
	private short forward_shape_vertical_mc_spatial_ref;
	private byte ref_select_code;
	
	private VideoFrame last_I_P_Frame = null;
	private VideoFrame currentFrame = null;
	
	private int prev_vop_time_increment = -1;
		
	private void decode_VideoObjectPlane() throws IOException {
		// init predefined values
		vop_rounding_type = 0;
		macroblock_number = 0;
		prevQp = -1;
		vop_width = video_object_layer_width;
		vop_height = video_object_layer_height;
		max_macroblock_number = ((vop_width + 15)/16) * ((vop_height + 15)/16) - 1;		
		vop_coding_type = (int)videoStream.next_bits(2);
		
		if((vop_coding_type != I_VOP) && (vop_coding_type != P_VOP)) {
			System.out.println("Unsupported Frame (Type = " + vop_coding_type + ")");
			// TODO Add support of B_VOP frames
			return;
		}
		
		vop_id ++;
		
		modulo_time_base = 0;
		while(videoStream.next_bits(1) == 1) {
			modulo_time_base++; 
		}
		videoStream.marker_bit();
		vop_time_increment = (int)videoStream.next_bits(vop_time_increment_length);
		videoStream.marker_bit(); 

		if((vop_time_increment < prev_vop_time_increment) && ((prev_vop_time_increment - vop_time_increment) > (vop_time_increment_resolution / 2))) {
			time_code_seconds ++;
			while(time_code_seconds >= 60) {
				time_code_seconds -= 60;
				time_code_minutes++;
			}
			while(time_code_minutes >= 60) {
				time_code_minutes -= 60;
				time_code_hours++;
			}
		}
		prev_vop_time_increment = vop_time_increment;

		// calculates the current playing time
		int base_time = time_code_hours * 3600 + time_code_minutes * 60 + time_code_seconds;
		
		vop_coded = videoStream.next_bit();
		if(vop_coded) {
			// finds out the last I or P type frame for the forward prediction
			if((currentFrame != null) && ((currentFrame.getType() == I_VOP) || (currentFrame.getType() == P_VOP))) {
				VideoFrame unusedFrame = last_I_P_Frame;
				last_I_P_Frame = currentFrame;
				currentFrame = unusedFrame;
			} else {
				currentFrame = null;
			}
			// creates a VideoFrame object.
			if(currentFrame == null) {
				currentFrame = new VideoFrame(vop_coding_type, video_object_layer_width, video_object_layer_height, bits_per_pixel);
			} else {
				currentFrame.setType(vop_coding_type);
			}
			currentFrame.setPlaying_time(base_time * 1000 + 
				((1000 * vop_time_increment) / vop_time_increment_resolution));
			
			if(newpred_enable) {
				vop_id_for_prediction = vop_id = (short)videoStream.next_bits(vop_id_length);
				vop_id_for_prediction_indication = videoStream.next_bit();
				if(vop_id_for_prediction_indication) {
					vop_id_for_prediction = (short)videoStream.next_bits(vop_id_length);
				} else {
					vop_id_for_prediction--;
				}
				videoStream.marker_bit();
			}
			if ((video_object_layer_shape != BINARY_ONLY_SHAPE) &&
			((vop_coding_type == P_VOP) || ((vop_coding_type == S_VOP) && (sprite_enable == GMC_SPRITE)))) {
				vop_rounding_type = (byte)videoStream.next_bits(1);
				currentFrame.setRounding_control(vop_rounding_type);
			}
			if ((reduced_resolution_vop_enable) &&
			(video_object_layer_shape == RECTANGULAR_SHAPE) &&
			((vop_coding_type == P_VOP) || (vop_coding_type == I_VOP))) {
				vop_reduced_resolution = videoStream.next_bit();
			}
			if (video_object_layer_shape != RECTANGULAR_SHAPE) {
				if(!((sprite_enable == STATIC_SPRITE) && (vop_coding_type == I_VOP))) {
					vop_width = (short)videoStream.next_bits(13);
					videoStream.marker_bit();
					vop_height = (short)videoStream.next_bits(13);
					videoStream.marker_bit();
					vop_horizontal_mc_spatial_ref = (short)videoStream.next_bits(13);
					videoStream.marker_bit();
					vop_vertical_mc_spatial_ref = (short)videoStream.next_bits(13);
					videoStream.marker_bit();
				}				
				if ((video_object_layer_shape != BINARY_ONLY_SHAPE) &&
				scalability && enhancement_type) {
					background_composition = videoStream.next_bit();			
				}
				change_conv_ratio_disable = videoStream.next_bit();
				vop_constant_alpha = videoStream.next_bit();
				if(vop_constant_alpha) {
					vop_constant_alpha_value = (short)videoStream.next_bits(8);
				}
			}
			if (video_object_layer_shape != BINARY_ONLY_SHAPE) {
				if (!complexity_estimation_disable) {
					read_vop_complexity_estimation_header();
				}
			}
			if (video_object_layer_shape != BINARY_ONLY_SHAPE) {
				intra_dc_vlc_thr = (byte)videoStream.next_bits(3);
//				System.out.println("intra_dc_vlc_thr = " + intra_dc_vlc_thr);
				if (interlaced) {
					top_field_first = videoStream.next_bit();
					alternate_vertical_scan_flag = videoStream.next_bit();
				}
			}
			if (((sprite_enable == STATIC_SPRITE) || (sprite_enable== GMC_SPRITE)) &&
			(vop_coding_type == S_VOP)) {
				// TODO add sprite functionality
				if (no_of_sprite_warping_points > 0) {
									
				}
				return;
			}			
			if (video_object_layer_shape != BINARY_ONLY_SHAPE) {
				quantiser_scale = vop_quant =  (short)videoStream.next_bits(quant_precision);
				if(video_object_layer_shape == GRAYSCALE_SHAPE) {
					// skip the data for the grayscale shape
					for(int i=0; i < aux_comp_count[video_object_layer_shape_extension]; i++) {
						videoStream.skip_bits(6);					
					}					
				}
				if (vop_coding_type != I_VOP) {
					vop_fcode_forward = (byte)videoStream.next_bits(3);
				}
				if (vop_coding_type == B_VOP) {
					vop_fcode_backward = (byte)videoStream.next_bits(3);
				}
				if (reduced_resolution_vop_enable) {
					max_macroblock_number = ((video_object_layer_width + 15)/16) * ((video_object_layer_height + 15)/16);		
				} else {
					max_macroblock_number = ((vop_width + 15)/16) * ((vop_height + 15)/16);		
				}
				max_macroblock_number--;
				printVideoObjectPlane();
		
				if (!scalability) {
					if ((video_object_layer_shape != RECTANGULAR_SHAPE) && (vop_coding_type != I_VOP)) {
						vop_shape_coding_type = videoStream.next_bit();
					}
					motion_shape_texture();
					while (videoStream.nextbits_byteAligned(17) == RESYNC_MARKER) {
						video_packet_header();
						motion_shape_texture();
					}
				} else {
					if (enhancement_type) {
						load_backward_shape = videoStream.next_bit();
						if (load_backward_shape) {
							backward_shape_width = (short)videoStream.next_bits(13);
							videoStream.marker_bit();
							backward_shape_height = (short)videoStream.next_bits(13);
							videoStream.marker_bit();
							backward_shape_horizontal_mc_spatial_ref = (short)videoStream.next_bits(13);
							videoStream.marker_bit();
							backward_shape_vertical_mc_spatial_ref = (short)videoStream.next_bits(13);
							videoStream.marker_bit();
							backward_shape();
							load_forward_shape = videoStream.next_bit();
							if (load_forward_shape) {
								forward_shape_width = (short)videoStream.next_bits(13);
								videoStream.marker_bit();
								forward_shape_height = (short)videoStream.next_bits(13);
								videoStream.marker_bit();
								forward_shape_horizontal_mc_spatial_ref = (short)videoStream.next_bits(13);
								videoStream.marker_bit();
								forward_shape_vertical_mc_spatial_ref = (short)videoStream.next_bits(13);
								videoStream.marker_bit();
								forward_shape();
							}
						}
					}
					ref_select_code = (byte)videoStream.next_bits(2);
					combined_motion_shape_texture();					
				}
				
			} else {
				combined_motion_shape_texture();
				while (videoStream.nextbits_byteAligned(17) == RESYNC_MARKER) {
					video_packet_header();
					combined_motion_shape_texture();
				}				
			}
			applet.nextFrame(currentFrame);
		} else {
			printVideoObjectPlane();
			if(vop_coding_type == I_VOP) {
				// creates a VideoFrame object.
				// finds out the last I or P type frame for the forward prediction
				if((currentFrame != null) && ((currentFrame.getType() == I_VOP) || (currentFrame.getType() == P_VOP))) {
					VideoFrame unusedFrame = last_I_P_Frame;
					last_I_P_Frame = currentFrame;
					currentFrame = unusedFrame;
				} else {
					currentFrame = null;
				}
				if(currentFrame == null) {
					currentFrame = new VideoFrame(vop_coding_type, video_object_layer_width, video_object_layer_height, bits_per_pixel);
				} else {
					currentFrame.setType(I_VOP);
				}
				currentFrame.clearFrame();
				applet.nextFrame(currentFrame);
			}
		}
	}
	
	private void printVideoObjectPlane() {
	/*		
		System.out.println("VideoObjectPlane()");
		System.out.println("Vop Id = " + vop_id);
		System.out.println("Vop Coding Type = " + vop_coding_type);
		System.out.println("Modulo Time Base = " + modulo_time_base);
		System.out.println("Vop Time Increment = " + vop_time_increment);
		System.out.println("Vop Coded = " + vop_coded);
		System.out.println("vop_rounding_type = " + vop_rounding_type);
//		System.out.println("max_macroblock_number = " + max_macroblock_number);
		System.out.println("Current Time:" + currentFrame.getPlaying_time());
		System.out.println("Time Code Hours = " + time_code_hours);
		System.out.println("Time Code Minutes = " + time_code_minutes);
		System.out.println("Time Code Seconds = " + time_code_seconds);
		
		if(vop_coded) {
			if(newpred_enable) {
				System.out.println("vop_id_for_prediction_indication = " + vop_id_for_prediction_indication);
				System.out.println("vop_id_for_prediction = " + vop_id_for_prediction);
			}
		}
*/		
	} 
	
	private short dcecs_opaque;
	private short dcecs_transparent;
	private short dcecs_intra_cae;
	private short dcecs_inter_cae;
	private short dcecs_no_update;
	private short dcecs_upsampling;
	private short dcecs_intra_blocks;
	private short dcecs_not_coded_blocks;
	private short dcecs_dct_coefs;
	private short dcecs_dct_lines;
	private short dcecs_vlc_symbols;
	private byte dcecs_vlc_bits;
	private short dcecs_sadct;
	private short dcecs_inter_blocks;
	private short dcecs_inter4v_blocks;
	private short dcecs_apm;
	private short dcecs_npm;
	private short dcecs_forw_back_mc_q;
	private short dcecs_halfpel2;
	private short dcecs_halfpel4;
	private short dcecs_quarterpel;
	private short dcecs_interpolate_mc_q;
		
	
	private void read_vop_complexity_estimation_header() throws IOException {
		if(estimation_method == 0) {
			if(vop_coding_type == I_VOP) {
				if (opaque) dcecs_opaque = (short)videoStream.next_bits(8);
				if (transparent) dcecs_transparent = (short)videoStream.next_bits(8);
				if (intra_cae) dcecs_intra_cae = (short)videoStream.next_bits(8);
				if (inter_cae) dcecs_inter_cae = (short)videoStream.next_bits(8);
				if (no_update) dcecs_no_update = (short)videoStream.next_bits(8);
				if (upsampling) dcecs_upsampling = (short)videoStream.next_bits(8);
				if (intra_blocks) dcecs_intra_blocks = (short)videoStream.next_bits(8);
				if (not_coded_blocks) dcecs_not_coded_blocks = (short)videoStream.next_bits(8);
				if (dct_coefs) dcecs_dct_coefs = (short)videoStream.next_bits(8);
				if (dct_lines) dcecs_dct_lines = (short)videoStream.next_bits(8);
				if (vlc_symbols) dcecs_vlc_symbols = (short)videoStream.next_bits(8);
				if (vlc_bits) dcecs_vlc_bits = (byte)videoStream.next_bits(4);
				if (sadct) dcecs_sadct = (short)videoStream.next_bits(8);
			}
			if(vop_coding_type == P_VOP) {
				if (opaque) dcecs_opaque = (short)videoStream.next_bits(8);
				if (transparent) dcecs_transparent = (short)videoStream.next_bits(8);
				if (intra_cae) dcecs_intra_cae = (short)videoStream.next_bits(8);
				if (inter_cae) dcecs_inter_cae = (short)videoStream.next_bits(8);
				if (no_update) dcecs_no_update = (short)videoStream.next_bits(8);
				if (upsampling) dcecs_upsampling = (short)videoStream.next_bits(8);
				if (intra_blocks) dcecs_intra_blocks = (short)videoStream.next_bits(8); // TODO
				if (not_coded_blocks) dcecs_not_coded_blocks = (short)videoStream.next_bits(8); // TODO
				if (dct_coefs) dcecs_dct_coefs = (short)videoStream.next_bits(8);
				if (dct_lines) dcecs_dct_lines = (short)videoStream.next_bits(8);
				if (vlc_symbols) dcecs_vlc_symbols = (short)videoStream.next_bits(8);
				if (vlc_bits) dcecs_vlc_bits = (byte)videoStream.next_bits(4);
				if (inter_blocks) dcecs_inter_blocks = (short)videoStream.next_bits(8);
				if (inter4v_blocks) dcecs_inter4v_blocks = (short)videoStream.next_bits(8);
				if (apm) dcecs_apm = (short)videoStream.next_bits(8);
				if (npm) dcecs_npm = (short)videoStream.next_bits(8);
				if (forw_back_mc_q) dcecs_forw_back_mc_q = (short)videoStream.next_bits(8);
				if (halfpel2) dcecs_halfpel2 = (short)videoStream.next_bits(8);
				if (halfpel4) dcecs_halfpel4 = (short)videoStream.next_bits(8);
				if (sadct) dcecs_sadct = (short)videoStream.next_bits(8);
				if (quarterpel) dcecs_quarterpel = (short)videoStream.next_bits(8);
			}
			if(vop_coding_type == B_VOP) {
				if (opaque) dcecs_opaque = (short)videoStream.next_bits(8);
				if (transparent) dcecs_transparent = (short)videoStream.next_bits(8);
				if (intra_cae) dcecs_intra_cae = (short)videoStream.next_bits(8);
				if (inter_cae) dcecs_inter_cae = (short)videoStream.next_bits(8);
				if (no_update) dcecs_no_update = (short)videoStream.next_bits(8);
				if (upsampling) dcecs_upsampling = (short)videoStream.next_bits(8);
				if (intra_blocks) dcecs_intra_blocks = (short)videoStream.next_bits(8);
				if (not_coded_blocks) dcecs_not_coded_blocks = (short)videoStream.next_bits(8);
				if (dct_coefs) dcecs_dct_coefs = (short)videoStream.next_bits(8);
				if (dct_lines) dcecs_dct_lines = (short)videoStream.next_bits(8);
				if (vlc_symbols) dcecs_vlc_symbols = (short)videoStream.next_bits(8);
				if (vlc_bits) dcecs_vlc_bits = (byte)videoStream.next_bits(4);
				if (inter_blocks) dcecs_inter_blocks = (short)videoStream.next_bits(8);
				if (inter4v_blocks) dcecs_inter4v_blocks = (short)videoStream.next_bits(8);
				if (apm) dcecs_apm = (short)videoStream.next_bits(8);
				if (npm) dcecs_npm = (short)videoStream.next_bits(8);
				if (forw_back_mc_q) dcecs_forw_back_mc_q = (short)videoStream.next_bits(8);
				if (halfpel2) dcecs_halfpel2 = (short)videoStream.next_bits(8);
				if (halfpel4) dcecs_halfpel4 = (short)videoStream.next_bits(8);
				if (interpolate_mc_q) dcecs_interpolate_mc_q = (short)videoStream.next_bits(8);
				if (sadct) dcecs_sadct = (short)videoStream.next_bits(8);
				if (quarterpel) dcecs_quarterpel = (short)videoStream.next_bits(8);
			}
			if ((vop_coding_type == S_VOP) && (sprite_enable == STATIC_SPRITE)) {
				if (intra_blocks) dcecs_intra_blocks = (short)videoStream.next_bits(8);
				if (not_coded_blocks) dcecs_not_coded_blocks = (short)videoStream.next_bits(8);
				if (dct_coefs) dcecs_dct_coefs = (short)videoStream.next_bits(8);
				if (dct_lines) dcecs_dct_lines = (short)videoStream.next_bits(8);
				if (vlc_symbols) dcecs_vlc_symbols = (short)videoStream.next_bits(8);
				if (vlc_bits) dcecs_vlc_bits = (byte)videoStream.next_bits(4);
				if (inter_blocks) dcecs_inter_blocks = (short)videoStream.next_bits(8);
				if (inter4v_blocks) dcecs_inter4v_blocks = (short)videoStream.next_bits(8);
				if (apm) dcecs_apm = (short)videoStream.next_bits(8);
				if (npm) dcecs_npm = (short)videoStream.next_bits(8);
				if (forw_back_mc_q) dcecs_forw_back_mc_q = (short)videoStream.next_bits(8);
				if (halfpel2) dcecs_halfpel2 = (short)videoStream.next_bits(8);
				if (halfpel4) dcecs_halfpel4 = (short)videoStream.next_bits(8);
				if (interpolate_mc_q) dcecs_interpolate_mc_q = (short)videoStream.next_bits(8);
			}
		}
//		System.out.println("read_vop_complexity_estimation_header()");
	}
	
	/**
	 * Loads the quantization matrix from the video stream.
	 * @return the quantization matrix. 
	 * @throws IOException raises if an error occurs. 
	 */
	private int[] read_quant_matrix() throws IOException {
		int i; 
		int[] quant_matrix = new int[64];
		for(i = 0 ; i < 64; i++) {
			int quant_value = (int)videoStream.next_bits(8);  
			quant_matrix[i] = quant_value;
			if((i > 0) && (quant_value == 0)) {
				break;
			}
		}
		for( ; i < 64; i++) {
			quant_matrix[i] = 0;
		}
		return quant_matrix;
	}
	
	private boolean header_extension_code;
	private short macroblock_number;
	private int macroblock_number_length;
	private int max_macroblock_number;
	private short quant_scale;
	
	private void video_packet_header() throws IOException {
//		System.out.println("video_packet_header()");		
		videoStream.next_resyncmarker();
		if (video_object_layer_shape != RECTANGULAR_SHAPE) {
			header_extension_code = videoStream.next_bit();
			if (header_extension_code
			&& !((sprite_enable == STATIC_SPRITE) && (vop_coding_type == I_VOP))) {
				vop_width = (short)videoStream.next_bits(13);
				videoStream.marker_bit();
				vop_height = (short)videoStream.next_bits(13);
				videoStream.marker_bit();
				vop_horizontal_mc_spatial_ref = (short)videoStream.next_bits(13);
				videoStream.marker_bit();
				vop_vertical_mc_spatial_ref = (short)videoStream.next_bits(13);
				videoStream.marker_bit();
			}
		}
		macroblock_number_length = 13;
		while((((max_macroblock_number + 1) >>> macroblock_number_length) == 0) && (macroblock_number_length > 0)) {
			macroblock_number_length --;
		}
		macroblock_number_length ++;
		macroblock_number = (short)videoStream.next_bits(macroblock_number_length);
		currentFrame.clearMacroblockInfo(macroblock_number);
		if (video_object_layer_shape != BINARY_ONLY_SHAPE) {
			quantiser_scale = quant_scale = (short)videoStream.next_bits(quant_precision);
		}
		if (video_object_layer_shape == RECTANGULAR_SHAPE) {
			header_extension_code = videoStream.next_bit();		
		}
		if (header_extension_code) {
			modulo_time_base = 0;
			while(videoStream.next_bits(1) == 1) {
				modulo_time_base++; 
			}
			videoStream.marker_bit(); 
			vop_time_increment = (int)videoStream.next_bits(vop_time_increment_length);
			videoStream.marker_bit(); 
			vop_coding_type = (byte)videoStream.next_bits(2);
			if (video_object_layer_shape != RECTANGULAR_SHAPE) {
				change_conv_ratio_disable = videoStream.next_bit();
				if (vop_coding_type != I_VOP) {
					vop_shape_coding_type = videoStream.next_bit();
				}
			}	
			if (video_object_layer_shape != BINARY_ONLY_SHAPE) {
				intra_dc_vlc_thr = (byte)videoStream.next_bits(3);
				if ((sprite_enable == GMC_SPRITE) && (vop_coding_type == S_VOP)
				&& (no_of_sprite_warping_points > 0)) {
					sprite_trajectory();					
				}
				if ((reduced_resolution_vop_enable)
				&& (video_object_layer_shape == RECTANGULAR_SHAPE)
				&& ((vop_coding_type == P_VOP) || (vop_coding_type == I_VOP))) {
					vop_reduced_resolution = videoStream.next_bit();				
				}
				if (vop_coding_type != I_VOP) {
					vop_fcode_forward = (byte)videoStream.next_bits(3);
				}
				if (vop_coding_type == B_VOP) {
					vop_fcode_backward = (byte)videoStream.next_bits(3);
				}
			}					
		}
		if (newpred_enable) {
			vop_id_for_prediction = vop_id = (short)videoStream.next_bits(vop_id_length);
			vop_id_for_prediction_indication = videoStream.next_bit();
			if(vop_id_for_prediction_indication) {
				vop_id_for_prediction = (short)videoStream.next_bits(vop_id_length);
			} else {
				vop_id_for_prediction--;
			}
			videoStream.marker_bit();
		}				
	}
	
	private void motion_shape_texture() throws IOException {
//		System.out.println("motion_shape_texture()");
		if (data_partitioned ) {
			data_partitioned_motion_shape_texture();
		} else {
			combined_motion_shape_texture();
		}
	}
	
	
	private void combined_motion_shape_texture() throws IOException {
//		System.out.println("combined_motion_shape_texture()");
		do{
			macroblock();
		} while ((macroblock_number <= max_macroblock_number) && (videoStream.is_data_in_next_byte() || ((videoStream.nextbits_byteAligned(17) != RESYNC_MARKER) 
				  && (videoStream.nextbits_byteAligned(24) != 1))));
	}
	
	private void data_partitioned_motion_shape_texture() throws IOException {
		// TODO complete data_partitioned_motion_shape_texture() method
//		System.out.println("data_partitioned_motion_shape_texture()");
	}
	
	private boolean not_coded;
	
	private int mb_type;
	private int derived_mb_type;
	private int cbpc;
	private boolean mcsel;
	private boolean ac_pred_flag;
	private byte cbpy;
	private byte dquant;	
	private int block_count = 6;
	private boolean use_intra_dc_vlc;
	
	private short Qp;
	private short prevQp;
	private short quantiser_scale;
	
	private void macroblock() throws IOException {
//		System.out.println("macroblock(" + macroblock_number + ")");

		// set default values
		if((ref_select_code == 0) && scalability) {
			mb_type = 4 /* 'forward mc + Q' */;
		} else {
			mb_type = 1 /* 'direct' */;
		}
		
		not_coded = false;
		mcsel = false;
		ac_pred_flag = false;
/*		
		if((sprite_enable == GMC_SPRITE) && (vop_coding_type == S_VOP) && not_coded) {
			mcsel = true;
		}
*/		
		cbpy = 15;
		cbpc = 3;
		
/*
  	Qp is defined as the DCT quantisation parameter for luminance and chrominance used for
	immediately previous coded macroblock, except for the first coded macroblock in a VOP or a video packet. At the
	first coded macroblock in a VOP or a video packet, the running Qp is defined as the quantisation parameter value
	for the current macroblock.
 */		
		if (vop_coding_type != B_VOP) {
			int[][] mcbpc_table = null;
			if((vop_coding_type == I_VOP) || ((vop_coding_type == S_VOP) &&  
				low_latency_sprite_enable && (sprite_transmit_mode == PIECE_TRANSMIT_MODE))) {
				mcbpc_table = Huffman.MCBPC_1_TAB;
			} else {
				mcbpc_table = Huffman.MCBPC_2_TAB;
			}
			if ((video_object_layer_shape != RECTANGULAR_SHAPE)
			&& !((sprite_enable == STATIC_SPRITE) && low_latency_sprite_enable
			&& (sprite_transmit_mode == UPDATE_TRANSMIT_MODE))) {
				mb_binary_shape_coding();
			}
			if (video_object_layer_shape != BINARY_ONLY_SHAPE) {
				if (!transparent_mb()) {
					if ((video_object_layer_shape != RECTANGULAR_SHAPE)
					&& !((sprite_enable == STATIC_SPRITE) && low_latency_sprite_enable
					&& (sprite_transmit_mode == UPDATE_TRANSMIT_MODE))) {
						do{
							if ((vop_coding_type != I_VOP) && !((sprite_enable == STATIC_SPRITE)
							&& (sprite_transmit_mode == PIECE_TRANSMIT_MODE))) {
								not_coded = videoStream.next_bit();
							}
							if (!not_coded || (vop_coding_type == I_VOP)
							|| ((vop_coding_type == S_VOP)
							&& low_latency_sprite_enable
							&& (sprite_transmit_mode == PIECE_TRANSMIT_MODE))) {
								// decode mcbpc
								int[] mcbpcValues = huffman.decode(9, mcbpc_table); 
								derived_mb_type = mcbpcValues[2];
								cbpc = mcbpcValues[3];
							}
																					
						} while(!(not_coded || (derived_mb_type != Huffman.MCBPC_STUFFING)));
					} else {
						if ((vop_coding_type != I_VOP) && !((sprite_enable == STATIC_SPRITE)
						&& (sprite_transmit_mode == PIECE_TRANSMIT_MODE))) {
							not_coded = videoStream.next_bit();
							if(not_coded && (vop_coding_type == P_VOP)) {
								currentFrame.copyMacroblock(last_I_P_Frame, macroblock_number);
							}
						}
						if (!not_coded || (vop_coding_type == I_VOP)
						|| ((vop_coding_type == S_VOP)
						&& low_latency_sprite_enable
						&& (sprite_transmit_mode == PIECE_TRANSMIT_MODE))) {
							// decode mcbpc
							int[] mcbpcValues = huffman.decode(9, mcbpc_table); 
							derived_mb_type = mcbpcValues[2];
							cbpc = mcbpcValues[3];
						}
					}
					// set the default value for the mcsel
					if((sprite_enable == GMC_SPRITE) && (vop_coding_type == S_VOP) && not_coded) {
						mcsel = true;
					}
					if (!not_coded || (vop_coding_type == I_VOP)
					|| ((vop_coding_type == S_VOP) && low_latency_sprite_enable
					&& (sprite_transmit_mode == PIECE_TRANSMIT_MODE))) {
						if ((vop_coding_type == S_VOP) && (sprite_enable == GMC_SPRITE)
						&& ((derived_mb_type == 0) || (derived_mb_type == 1))) {
							mcsel = videoStream.next_bit(); 						
						}
						if (!short_video_header && ((derived_mb_type == 3) || (derived_mb_type == 4))) {
							ac_pred_flag = videoStream.next_bit();							
						}
						if (derived_mb_type == Huffman.MCBPC_STUFFING) {
//							System.out.println("MCBPC_STUFFING");
							return;						
						}
						// select the VLC table for cbpy in the case of one - four non-transparent blocks 						
						int[][] cbpy_table = Huffman.CBPY_4_TAB;		
/*															
						if(transparent_mb()) {
							switch(non_transparent_blocks) {
								case 1:
									cbpy_table = Huffman.CBPY_1_TAB;
									break;
								case 2: 
									cbpy_table = Huffman.CBPY_2_TAB;
									break;
								case 3:
									cbpy_table = Huffman.CBPY_3_TAB;
									break;
								default:
									cbpy_table = Huffman.CBPY_4_TAB;
								break;
							}
						}
*/						
						// select the value for cbpy in the case of the intra or the inter macroblock
						int cbpy_index = (derived_mb_type >= 3) ? 2 : 3;
						cbpy = (byte)huffman.decode(6, cbpy_table)[cbpy_index];
						if ((derived_mb_type == 1) || (derived_mb_type == 4)) {
							dquant = (byte)videoStream.next_bits(2);
							// apply dquant value
							switch(dquant) {
								case 0:
									quantiser_scale--; 
									break;
								case 1: 
									quantiser_scale -= 2; 
									break;
								case 2:
									quantiser_scale ++; 
									break;
								case 3:
									quantiser_scale += 2; 
									break;
							}
							if(quantiser_scale < 1) {
								quantiser_scale = 1;
							}
							if(quantiser_scale > ((1 << quant_precision) - 1)) {
								quantiser_scale = (short)((1 << quant_precision) - 1);
							}
						}
						if (interlaced) {
							interlaced_information();
						}
						if ( !((ref_select_code == 3) && scalability)
						&& (sprite_enable != STATIC_SPRITE)) {
							if (((derived_mb_type == 0) || (derived_mb_type == 1))
							&& ((vop_coding_type == P_VOP)
							|| ((vop_coding_type == S_VOP) && !mcsel))) {
								motion_vector(FORWARD_MOTION_MODE);
								currentFrame.setForwardMotionVector(
									macroblock_number, -1, 
									quarter_sample, vop_fcode_forward, 
									horizontal_mv_data, horizontal_mv_residual, 
									vertical_mv_data, vertical_mv_residual
								);
								if (interlaced && field_prediction) {
									motion_vector(FORWARD_MOTION_MODE);
								}
							}
							if (derived_mb_type == 2) {
								for (int j = 0; j < 4; j++) {
									if (!transparent_block(j)) {
										motion_vector(FORWARD_MOTION_MODE);
										currentFrame.setForwardMotionVector(
											macroblock_number, j, 
											quarter_sample, vop_fcode_forward, 
											horizontal_mv_data, horizontal_mv_residual, 
											vertical_mv_data, vertical_mv_residual
										);
									}
								}
							}														
						}
						set_use_intra_dc_vlc();
						
						currentFrame.setMacroblockInfo(macroblock_number, derived_mb_type, quantiser_scale);
/*						
						if((vop_id == 9) && ((macroblock_number == 21) || (macroblock_number == 20) || (macroblock_number == 11) || (macroblock_number == 10))) {
							System.out.println("macroblock_number = " + macroblock_number);
							System.out.println("derived_mb_type = " + derived_mb_type);
							if ((derived_mb_type == 1) || (derived_mb_type == 4)) {
								System.out.println("dquant = " + dquant);
							}
							System.out.println("not_coded = " + not_coded);
							System.out.println("cbpy = " + cbpy);
							System.out.println("cbpc = " + cbpc);
							System.out.println("quantiser_scale = " + quantiser_scale);
							if((derived_mb_type == 3) || (derived_mb_type == 4)) {
								System.out.println("ac_pred_flag = " + ac_pred_flag);
								System.out.println("use_intra_dc_vlc = " + use_intra_dc_vlc);
							}
						}
*/						
						for (int i = 0; i < block_count; i++) {
							if(!transparent_block(i)) {
								block(i);
							}
						}

						if ( !((ref_select_code == 3) && scalability)
						&& (sprite_enable != STATIC_SPRITE)) {
							if (((derived_mb_type == 0) || (derived_mb_type == 1) || (derived_mb_type == 2))
							&& ((vop_coding_type == P_VOP)
							|| ((vop_coding_type == S_VOP) && !mcsel))) {
								currentFrame.applyForwardMotionVector(last_I_P_Frame, macroblock_number);
							}
						}

					}					
				}				
			}					
		} else {
			// TODO complete macroblock()
//			System.out.println("Unsupported macroblock()...");
		}
		macroblock_number ++;
	}

	/**
	 * Sets the use_intra_dc_vlc flag for the intra coded macroblocks.
	 */	
	private void set_use_intra_dc_vlc() {
		// finds running Qp value
		if(prevQp == -1) {
			prevQp = Qp = quantiser_scale;
		} else {
			Qp = prevQp;
			prevQp = quantiser_scale;
		}
		if ((derived_mb_type == 3) || (derived_mb_type == 4)) {
			switch(intra_dc_vlc_thr) {
				case 0:
					use_intra_dc_vlc = true;
					break;
				case 1:
					use_intra_dc_vlc = Qp >= 13 ? false : true;  
					break;
				case 2:
					use_intra_dc_vlc = Qp >= 15 ? false : true;  
					break;
				case 3:
					use_intra_dc_vlc = Qp >= 17 ? false : true;  
					break;
				case 4:
					use_intra_dc_vlc = Qp >= 19 ? false : true;  
					break;
				case 5:
					use_intra_dc_vlc = Qp >= 21 ? false : true;  
					break;
				case 6:
					use_intra_dc_vlc = Qp >= 23 ? false : true;  
					break;
				case 7:
					use_intra_dc_vlc = false;
					break;
			}
		} 
	}
	
	/** The 1-bit flag indicating whether the macroblock is frame (false) DCT coded or field (true) DCT coded. */
	private boolean dct_type;
	private boolean field_prediction;
	private boolean forward_top_field_reference;
	private boolean forward_bottom_field_reference;
	private boolean backward_top_field_reference;
	private boolean backward_bottom_field_reference;
	
	private void interlaced_information() throws IOException {
		dct_type = false;
		
		if ((derived_mb_type == 3) || (derived_mb_type == 4) 
			|| ((cbpy << 2 + cbpc) != 0) ) { 					// TODO check this code (cbp (cbpy << 2 + cbpc))  
			dct_type = videoStream.next_bit();
		}
		if ( ((vop_coding_type == P_VOP) &&
		((derived_mb_type == 0) || (derived_mb_type == 1)) ) ||
		((sprite_enable == GMC_SPRITE) && (vop_coding_type == S_VOP) &&
		(derived_mb_type < 2) && (!mcsel)) ||
		((vop_coding_type == B_VOP) && (mb_type != 1 /* '1' */)) ) {
			field_prediction = videoStream.next_bit();
			if (field_prediction) {
				if ((vop_coding_type == P_VOP) ||
				((vop_coding_type == B_VOP) && (mb_type != 3 /* '001' */)) ) {
					forward_top_field_reference = videoStream.next_bit(); 
					forward_bottom_field_reference = videoStream.next_bit(); 
				}
				if ((vop_coding_type == B_VOP) &&
				(mb_type != 4/* '0001' */) ) {
					backward_top_field_reference = videoStream.next_bit();
					backward_bottom_field_reference = videoStream.next_bit();
				}				
			}		
		}				
//		System.out.println("interlaced_information()");
//		System.out.println("dct_type = " + dct_type);
		
	}
	
	private boolean transparent_mb() {
		// TODO complete transparent_mb()
		/*		
		if(!transparent)
			return false;
		*/		
		return false;
	}
	
	/**
	 * Returns <tt>true</tt> if the 8x8 block with index <code>n</code> consists only transparent pixels.
	 * @param n the number of the block to test.
	 * @return <tt>true</tt> if the 8x8 block with index <code>n</code> consists only transparent pixels.
	 */
	private boolean transparent_block(int n) {
		// TODO complete transparent_block()
		return false;
	}
	
	private void mb_binary_shape_coding() throws IOException {
		// TODO complete mb_binary_shape_coding()
//		System.out.println("mb_binary_shape_coding()");
	}
	
	private void backward_shape() throws IOException {
		// TODO complete backward_shape()
//		System.out.println("backward_shape()");
	}
	private void forward_shape() throws IOException {
		// TODO complete forward_shape()
//		System.out.println("forward_shape()");
	}

	private void sprite_trajectory() throws IOException {
		// TODO complete sprite_trajectory()
//		System.out.println("sprite_trajectory()");
	}
	
	private int horizontal_mv_data;
	private byte horizontal_mv_residual;
	private int vertical_mv_data;
	private byte vertical_mv_residual;
	
	private void motion_vector(int mode) throws IOException {
//		System.out.println("motion_vector(" + mode + ")");
//		videoStream.print_next_bits(13);
		
		horizontal_mv_residual = 0;
		vertical_mv_residual = 0;
		if ( mode == DIRECT_MOTION_MODE) {
			horizontal_mv_data = huffman.decode(13, Huffman.MVD_TAB)[2];
			vertical_mv_data = huffman.decode(13, Huffman.MVD_TAB)[2];
		} else if ( mode == FORWARD_MOTION_MODE) {
			horizontal_mv_data = huffman.decode(13, Huffman.MVD_TAB)[2]; 
			if ((vop_fcode_forward != 1) && (horizontal_mv_data != 0)) {
				horizontal_mv_residual = (byte)videoStream.next_bits(vop_fcode_forward - 1);
			}
			vertical_mv_data = huffman.decode(13, Huffman.MVD_TAB)[2]; 
			if ((vop_fcode_forward != 1) && (vertical_mv_data != 0)) {
				vertical_mv_residual = (byte)videoStream.next_bits(vop_fcode_forward - 1);
			}
		} else if ( mode == BACKWARD_MOTION_MODE ) {
			horizontal_mv_data = huffman.decode(13, Huffman.MVD_TAB)[2]; 
			if ((vop_fcode_backward != 1) && (horizontal_mv_data != 0))
				horizontal_mv_residual = (byte)videoStream.next_bits(vop_fcode_backward - 1);
			vertical_mv_data = huffman.decode(13, Huffman.MVD_TAB)[2];  
			if ((vop_fcode_backward != 1) && (vertical_mv_data != 0))
				vertical_mv_residual = (byte)videoStream.next_bits(vop_fcode_backward - 1);
		}
/*		
		System.out.println("vop_fcode_forward = " + vop_fcode_forward);
		System.out.println("horizontal_mv_data = " + horizontal_mv_data);
		System.out.println("horizontal_mv_residual = " + horizontal_mv_residual);
		System.out.println("vertical_mv_data = " + vertical_mv_data);
		System.out.println("vertical_mv_residual = " + vertical_mv_residual);
*/		
	}
	
	private int intra_dc_coefficient;
	private int dct_dc_size_luminance;
	private int dct_dc_size_chrominance;
	private int dct_dc_differential;
	private int dc_scaler;
	private int [] dct_coeff = new int[100];
	private boolean vertical_prediction;
	private boolean macroblock_intra;
		
	/**
	 * Reads the 8x8 block with the index <code>n</code> from the video stream.
	 * @param n the index of the block.
	 * @throws IOException raises if an error occurs. 
	 */
	private void block(int n) throws IOException {
//		System.out.println("block(" + n + ")");
		int coeff_pointer = 0;
		int [][] t_coeff = currentFrame.getBlock(macroblock_number, n);
		// resets the coeff block with zero values
		for(int i = 0; i < 64; i += 4) {
			dct_coeff[i] = dct_coeff[i + 1] = dct_coeff[i + 2] = dct_coeff[i + 3] = 0;
		}
		for(int i = 0; i < 8; i ++) {
			t_coeff[i][0] = t_coeff[i][1] = t_coeff[i][2] = t_coeff[i][3] = 0;		
			t_coeff[i][4] = t_coeff[i][5] = t_coeff[i][6] = t_coeff[i][7] = 0;		
		}
		macroblock_intra = (derived_mb_type == 3) || (derived_mb_type == 4);
		// calculate dc_scaler for intra macroblock
		if(macroblock_intra) {
			if(short_video_header == true) {
				dc_scaler = 8;
			} else {
				if (n < 4) {
					if(quantiser_scale <= 4) {
						dc_scaler = 8;
					} else if(quantiser_scale <= 8) { 
						dc_scaler = quantiser_scale * 2;
					} else if(quantiser_scale <= 24) { 
						dc_scaler = quantiser_scale + 8;
					} else {
						dc_scaler = quantiser_scale * 2 - 16;
					}
				} else {
					if(quantiser_scale <= 4) {
						dc_scaler = 8;
					} else if(quantiser_scale <= 24) { 
						dc_scaler = (quantiser_scale + 13)/2;
					} else {
						dc_scaler = quantiser_scale - 6;
					}
				}
			}
		}
		boolean last = false;
		if(!data_partitioned && macroblock_intra) {
			if(short_video_header == true) {
				intra_dc_coefficient = (int)videoStream.next_bits(8);
				dct_coeff[0] = intra_dc_coefficient;
				coeff_pointer ++;	
			} else if (use_intra_dc_vlc) {
				if (n < 4) {
					dct_dc_size_luminance = huffman.decode(11, Huffman.DCT_DC_SIZE_LUMINANCE_TAB)[2];
					if(dct_dc_size_luminance != 0) {
						dct_dc_differential = (int)videoStream.next_bits(dct_dc_size_luminance);
						if((dct_dc_differential & (1 << (dct_dc_size_luminance - 1))) == 0) {
							dct_dc_differential = (dct_dc_differential | (Integer.MIN_VALUE >> (31 - dct_dc_size_luminance))) + 1;
						}
					} else {
						dct_dc_differential = 0;
					}
					if (dct_dc_size_luminance > 8) {
						videoStream.marker_bit();				
					}
				} else {
					dct_dc_size_chrominance = huffman.decode(12, Huffman.DCT_DC_SIZE_CHROMINANCE_TAB)[2];
					if(dct_dc_size_chrominance != 0) {
						dct_dc_differential = (int)videoStream.next_bits(dct_dc_size_chrominance);						
						if((dct_dc_differential & (1 << (dct_dc_size_chrominance - 1))) == 0) {
							dct_dc_differential = (dct_dc_differential | (Integer.MIN_VALUE >> (31 - dct_dc_size_chrominance))) + 1;
						}
					} else {
						dct_dc_differential = 0;						
					}
					if (dct_dc_size_chrominance > 8) {
						videoStream.marker_bit();				
					}
				}
				dct_coeff[0] = dct_dc_differential;
				coeff_pointer ++;	
			}			
		}
		int[][] tcoeff_tab = null;
		int[][][] lmax_tab = null;
		int[][][] rmax_tab = null;
		if((short_video_header == false) && macroblock_intra) {
			tcoeff_tab = Huffman.INTRA_TCOEF_TAB;
			lmax_tab = INTRA_LMAX_TAB;
			rmax_tab = INTRA_RMAX_TAB;
		} else {
			tcoeff_tab = Huffman.INTER_TCOEF_TAB;
			lmax_tab = INTER_LMAX_TAB;
			rmax_tab = INTER_RMAX_TAB;
		}
		if ( pattern_code(n) ) {
			while ( ! last ) {
				int run = 0;
				int level = 0;
				int type = 0;
				try {
				// read DCT coefficient from the stream
				int[] values = huffman.decode(12, tcoeff_tab);
				if(values[2] != Huffman.TCOEF_ESCAPE) {
					last = values[2] == 1;
					run = values[3];
					// test the sign of the level
					if(videoStream.next_bit()) {
						level =  - values[4];
					} else {
						level =  values[4];
					}
					for(int i = 0; i < run; i ++) {
						dct_coeff[coeff_pointer++] = 0;
					}					
					dct_coeff[coeff_pointer++] = level;
				} else {
					// escape sequence
					if(short_video_header == true) {
						// read the ESCAPE sequence
						type = 1;
						last = videoStream.next_bit();
						run = (int)videoStream.next_bits(6);
						level = 0;
						if(videoStream.next_bit()) {
							level = (int)videoStream.next_bits(7) - 128;
						} else {
							level = (int)videoStream.next_bits(7);
						}
						for(int i = 0; i < run; i ++) {
							dct_coeff[coeff_pointer++] = 0;
						}
						dct_coeff[coeff_pointer++] = level;
					} else if(! videoStream.next_bit()) {
						// type 1 of the escape sequence
						type = 2;
						values = huffman.decode(12, tcoeff_tab);
						last = values[2] == 1;
						int lastValue = values[2]; 
						run = values[3];
						level = values[4];
						// finds lmax value for the combination of run and last						
						for(int i = 0; i < lmax_tab[lastValue].length; i ++) {
							if(run <= lmax_tab[lastValue][i][0]) {
								level = level + lmax_tab[lastValue][i][1];
								break;
							}
						}
						// test the sign of the level
						if(videoStream.next_bit()) {
							level = -level;
						}
						for(int i = 0; i < run; i ++) {
							dct_coeff[coeff_pointer++] = 0;
						}
						dct_coeff[coeff_pointer++] = level;
					} else if(! videoStream.next_bit()) {
						type = 3;
						values = huffman.decode(12, tcoeff_tab);
						last = values[2] == 1;
						int lastValue = values[2]; 
						run = values[3];
						level = values[4];
						// finds rmax value as a function of the decoded values of level and last
						run++;						
						for(int i = 0; i < rmax_tab[lastValue].length; i ++) {
							if(level <= rmax_tab[lastValue][i][0]) {
								run = run + rmax_tab[lastValue][i][1];
								break;
							}
						}
						// test the sign of the level
						if(videoStream.next_bit()) {
							level = -level;
						}
						for(int i = 0; i < run; i ++) {
							dct_coeff[coeff_pointer++] = 0;
						}
						dct_coeff[coeff_pointer++] = level;
					} else {
						type = 4;
						last = videoStream.next_bit();
						run = (int)videoStream.next_bits(6);
						videoStream.marker_bit();
						if(videoStream.next_bit()) {
							level = (int)videoStream.next_bits(11) - 2048;
						} else {
							level = (int)videoStream.next_bits(11);
						}
						videoStream.marker_bit();
						for(int i = 0; i < run; i ++) {
							dct_coeff[coeff_pointer++] = 0;
						}
						dct_coeff[coeff_pointer++] = level;
					}
				}
				} catch (RuntimeException ex) {
					System.out.println("vop_id = " + vop_id);
					System.out.println("macroblock_number = " + macroblock_number);
					System.out.println("n = " + n);
					System.out.println("type = " + type);
					System.out.println("last = " + last);
					System.out.println("run = " + run);
					System.out.println("level = " + level);
					throw ex; 
				}
			}
		}

		int[] scan_table = ZIGZAG_SCAN_TABLE;
		
		boolean use_intra_prediction = 
			(short_video_header == false) && macroblock_intra;
		if(use_intra_prediction) {
			// find prediction direction
			vertical_prediction = currentFrame.getPredictionDirection(macroblock_number, n);
			
			if(ac_pred_flag) {
				// select alternate scan table
				if(vertical_prediction) {
					scan_table = ALTERNATE_HORIZONTAL_SCAN_TABLE;
				} else {
					scan_table = ALTERNATE_VERTICAL_SCAN_TABLE;
				}
			}
		}
		
		// re-order of coefficients into a two-dimension array 
		for (int i = 0; i < coeff_pointer; i++) {
			int index = scan_table[i];
			t_coeff[index >> 3][index & 7] = dct_coeff[i];		
		}
		if(use_intra_prediction) {
			int[][] previous_block = currentFrame.getPreviousBlock();
			// DC coefficient prediction
			t_coeff[0][0] = saturate_coefficient(
				t_coeff[0][0] + integer_round_div(previous_block[0][0], dc_scaler)
			);
			if(ac_pred_flag) {
				// AC coefficients prediction
				int previous_quantiser_scale = currentFrame.getPreviousQuantiserScale();
				if(previous_quantiser_scale > 0) {
					if(vertical_prediction) {
						for(int i = 1; i < 8; i ++) {
							t_coeff[0][i] = saturate_coefficient(
								t_coeff[0][i] + integer_round_div(
									previous_block[8][i] * previous_quantiser_scale, quantiser_scale
								)
							);
						}
					} else {
						for(int i = 1; i < 8; i ++) {
							t_coeff[i][0] = saturate_coefficient(
								t_coeff[i][0] + integer_round_div(
									previous_block[i][8] * previous_quantiser_scale, quantiser_scale
								)
							);
						}
					}
					// fixs the pointer of last coeff in the block 
					if(coeff_pointer < 14) {
						coeff_pointer = 14;
					}
				}
			}
		}
		if(macroblock_intra) {
			for(int i = 1; i < 8; i ++) {
				t_coeff[i][8] = t_coeff[i][0]; 
				t_coeff[8][i] = t_coeff[0][i]; 
			}
		}
/*
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				t_coeff[i][j] = saturate_coefficient(t_coeff[i][j]);
			}
		}
*/
		// inverse quantization
		int max_value = 1 << (bits_per_pixel + 3);
		if(quant_type == 1) {
			// first quantization method
			int sum = 0;
			int index, i, j;
			for (int coeff_number = 0; coeff_number < coeff_pointer; coeff_number++) {
				index = scan_table[coeff_number];
				i = index >> 3;
				j = index & 7;
				if(t_coeff[i][j] != 0) {
					if((i == 0) && (j == 0) && macroblock_intra) {
						t_coeff[0][0] = t_coeff[0][0] * dc_scaler;
					} else {
						if(macroblock_intra) {
							t_coeff[i][j] = ( t_coeff[i][j] * intra_quant_mat[i << 3 + j] * quantiser_scale * 2 ) / 16;
						} else {
							t_coeff[i][j] = ( ( t_coeff[i][j] * 2  + (t_coeff[i][j] >= 0 ? 1 : -1) ) * nonintra_quant_mat[i << 3 + j] * quantiser_scale ) / 16;
						}
					}
					// saturate coefficient
					if(t_coeff[i][j] > max_value) {
						t_coeff[i][j] = max_value;
					} else if(t_coeff[i][j] < -max_value) {
						t_coeff[i][j] = -max_value; 
					}
					sum = sum + t_coeff[i][j];
				}
			}
			if ((sum & 1) == 0) {
				if ((t_coeff[7][7] & 1) != 0) {
					t_coeff[7][7]--;
				} else {
					t_coeff[7][7]++;
				}
			}
		} else {
			// second quantization method
			int event_addition = (quantiser_scale & 1) ^ 1;
			int index, i, j;
			for (int coeff_number = 0; coeff_number < coeff_pointer; coeff_number++) {
				index = scan_table[coeff_number];
				i = index >> 3;
				j = index & 7;
				if(t_coeff[i][j] != 0) {
					if((i == 0) && (j == 0) && macroblock_intra) {
						t_coeff[0][0] = t_coeff[0][0] * dc_scaler;
					} else {
						t_coeff[i][j] = ((2 * Math.abs(t_coeff[i][j]) + 1) * quantiser_scale - event_addition) * (t_coeff[i][j] >= 0 ? 1 : -1);
					}
					// saturate coefficient
					if(t_coeff[i][j] >= max_value) {
						t_coeff[i][j] = max_value - 1;
					} else if(t_coeff[i][j] < -max_value) {
						t_coeff[i][j] = -max_value; 
					}
				}
			}
		}
		
		currentFrame.transformBlock(macroblock_number, n);
	}

	/**
 	* Returns <tt>true</tt> if the 8x8 block with index <code>n</code> is coded (present in the bitstream).
 	* @param n the number of the block to test.
 	* @return <tt>true</tt> if the 8x8 block with index <code>n</code> is coded (present in the bitstream).
 	*/
	private boolean pattern_code(int n) {
		if(n < 4) {
			// luminance block
			return (cbpy & (1 << (3 - n))) > 0;		
		}
		// chrominance block
		return (cbpc & (1 << (5 - n))) > 0;		
	}

	private int integer_round_div(int a, int b) {
		int tmp = (a << 1) / b;
		return tmp >= 0 ? (tmp + 1) >> 1 : - ((-tmp + 1) >> 1); 
	}
	
	private int saturate_coefficient(int value) {
		if(value > 2047) {
			value = 2047;
		} else if(value < -2048) {
			value = -2048; 
		}
		return value;
	}
}
