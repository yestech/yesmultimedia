/**
 *
 */
/************************** MPEG-2 NBC Audio Decoder **************************
 *                                                                           
 * "This software module was originally developed by 
 * AT&T, Dolby Laboratories, Fraunhofer Gesellschaft IIS in the course of 
 * development of the MPEG-2 NBC/MPEG-4 Audio standard ISO/IEC 13818-7, 
 * 14496-1,2 and 3. This software module is an implementation of a part of one or more 
 * MPEG-2 NBC/MPEG-4 Audio tools as specified by the MPEG-2 NBC/MPEG-4 
 * Audio standard. ISO/IEC  gives users of the MPEG-2 NBC/MPEG-4 Audio 
 * standards free license to this software module or modifications thereof for use in 
 * hardware or software products claiming conformance to the MPEG-2 NBC/MPEG-4
 * Audio  standards. Those intending to use this software module in hardware or 
 * software products are advised that this use may infringe existing patents. 
 * The original developer of this software module and his/her company, the subsequent 
 * editors and their companies, and ISO/IEC have no liability for use of this software 
 * module or modifications thereof in an implementation. Copyright is not released for 
 * non MPEG-2 NBC/MPEG-4 Audio conforming products.The original developer
 * retains full right to use the code for his/her  own purpose, assign or donate the 
 * code to a third party and to inhibit third party from using the code for non 
 * MPEG-2 NBC/MPEG-4 Audio conforming products. This copyright notice must
 * be included in all copies or derivative works." 
 * Copyright(c)1996.
 * 
 ******************************************************************************/                                                                          
package org.yestech.multimedia.video.mpeg4.audio.AAC;

/**
 * Ch_Info
 * 
 */
final class Ch_Info {
	/** channel present */
	boolean present;	
	/** element tag */
	int tag;		
	/** false if single channel, true if channel pair */
	boolean cpe;		
	/** true if common window for cpe */
	boolean	common_window;
	/** true if left channel of cpe */	
	boolean	ch_is_left;	
	/** index of paired channel in cpe */
	int	paired_ch;	
	/** window element index for this channel */
	int widx;		
	/** IS information */
	IS_Info is_info = new IS_Info();
	/** number of coupling channels for this ch */	
	int ncch;		
	/** coupling channel idx */
	int[] cch = new int[Constants.CChans];	
	/** coupling channel domain */
	int[] cc_dom = new int[Constants.CChans];	
	/** independently switched coupling channel flag */
	int[] cc_ind = new int[Constants.CChans];
	/** filename extension */	
	String fext;		
}
