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

/* original source intensity.c */ 	
/**
 * Intensity
 * 
 */
public class Intensity {

	/*
	 * if (chan==RIGHT) { 
	 *     do IS decoding for this channel (scale left ch. values with
	 * 	factor(SFr-SFl) )
	 *     reset all lpflags for which IS is on
	 *     pass decoded IS values to predict
	 *     }
	 */
	static void intensity(MC_Info mip, Info info, int widx, int[] lpflag, int ch, float[][] coef)
	{
		int left, right, i, k, nsect, sign, bot, top, sfb, ktop;
		float scale;
		Ch_Info cip = mip.ch_info[ch];
		IS_Info iip = mip.ch_info[ch].is_info;

		if (!(cip.cpe && iip.is_present && !cip.ch_is_left)) {
			return;
		}
	
		left = cip.paired_ch;
		right = ch;

		nsect = iip.n_is_sect;
		for (i=0; i<nsect; i++) {
			sign = iip.sign[i];
			top = iip.top[i];
			bot = iip.bot[i];
			for (sfb=bot; sfb<top; sfb++) {
				/* disable prediction */
				lpflag[1+sfb] = 0;

				scale = (float)(sign * Math.pow( 0.5,  0.25*(iip.fac[sfb]) ));

				/* reconstruct right intensity values */
//				if (AACDecoder.debug[Constants.DEBUG_I])
//					System.out.println("applying IS coding of " + scale + " on ch " + ch + " at sfb " + sfb);
				k = (sfb==0) ? 0 : info.bk_sfb_top[sfb-1];
				ktop = info.bk_sfb_top[sfb];
				for ( ; k<ktop; k++) {
					coef[right][k] = coef[left][k] * scale;
				}
			}
		}
	}
}
