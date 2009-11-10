package mediaframe.aac_decoder;

import java.io.IOException;

/* original source coupling.c */ 	
/**
 * Coupling
 */
final class Coupling {

	private AACDecoder decoder;
	private BitStream audio_stream = null;
	
	float[]   		cc_gain_scale = new float[4];
	int[][]	    	cc_lpflag = new int[Constants.CChans][Constants.MAXBANDS];
	int[][]	    	cc_prstflag = new int[Constants.CChans][Constants.LEN_PRED_RSTGRP];
	PRED_STATUS[][] cc_sp_status = new PRED_STATUS[Constants.CChans][Constants.LN2];
	float[][]	    cc_prev_quant = new float[Constants.CChans][Constants.LN2];
	byte[][]	    cc_group = new byte[Constants.CChans][Constants.MAXBANDS];

	Coupling(AACDecoder decoder) throws IOException {
		super();
		this.decoder = decoder;
		this.audio_stream = decoder.audio_stream;
		
		int i, ch;
		cc_gain_scale[3] = 2;

		/* initialize gain scale */
		for (i=2; i>=0; i--) {
			cc_gain_scale[i] = (float)Math.sqrt(cc_gain_scale[i+1]);
		}

		/* coupling channel predictors */
		for (ch = 0; ch < Constants.CChans; ch++) {
			for (i = 0; i < Constants.LN2; i++) {
				Monopred.init_pred_stat(cc_sp_status[ch][i],
					Constants.PRED_ORDER,Constants.PRED_ALPHA,Constants.PRED_A,Constants.PRED_B);
				cc_prev_quant[ch][i] = 0f;
			}
		}
	}

	int getcc(MC_Info mip, byte[] cc_wnd, Wnd_Shape[] cc_wnd_shape, float[] cc_coef, float[][][] cc_gain) throws IOException {
		int i, j, k, m, n, cpe, tag, cidx, ch, nele, nch;
		int[] target = new int[Constants.Chans];
		boolean[] shared = new boolean[Constants.Chans];
		int cc_l, cc_r, cc_dom, cc_gain_ele_sign, ind_sw_cc, scl_idx,
		sign, nsect, fac;
		byte[] sect = new byte[2*Constants.MAXBANDS+1];
		byte[] cc_max_sfb = new byte[Constants.CChans];
		short global_gain;
		short[] factors = new short[Constants.MAXBANDS];
		float scale;
		Info info;
		TNS_frame_info tns;

		tag = (int)audio_stream.next_bits(Constants.LEN_TAG);

		if (decoder.default_config) {
			cidx = mip.ncch;
		} else {
			cidx = Constants.XCChans;	    /* default is scratch index */
			for (i=0; i<mip.ncch; i++) {
				if (mip.cch_tag[i] == tag) {
					cidx = i;
				}
			}
		}
    
		if (cidx >= Constants.CChans) {
			throw new IOException("Unanticipated coupling channel");
		}

		/* coupled (target) elements */
		nele = (int)audio_stream.next_bits(Constants.LEN_NCC);
		nch = 0;
		for (i=0; i<nele; i++) {
			cpe = (int)audio_stream.next_bits(Constants.LEN_IS_CPE);
			tag = (int)audio_stream.next_bits(Constants.LEN_TAG);
			ch = decoder.config.ch_index(mip, cpe, tag);
	
			if (cpe == 0) {
				target[nch] = ch;
				shared[nch++] = false;
			} else {
				cc_l = (int)audio_stream.next_bits(Constants.LEN_CC_LR);
				cc_r = (int)audio_stream.next_bits(Constants.LEN_CC_LR);
				j = (cc_l<<1) | cc_r;
				switch(j) {
					case 0:	    /* shared gain list */
						target[nch] = ch;
						target[nch+1] = mip.ch_info[ch].paired_ch;
						shared[nch] = true;
						shared[nch+1] = true;
						nch += 2;
						break;
					case 1:	    /* left channel gain list */
						target[nch] = ch;
						shared[nch] = false;
						nch += 1;
						break;
					case 2:	    /* right channel gain list */
						target[nch+1] = mip.ch_info[ch].paired_ch;
						shared[nch] = false;
						nch += 1;
						break;
					case 3:	    /* two gain lists */
						target[nch] = ch;
						target[nch+1] = mip.ch_info[ch].paired_ch;
						shared[nch] = false;
						shared[nch+1] = false;
						nch += 2;
						break;
				}
			}
		}
    
		cc_com = (audio_stream.next_bits(Constants.LEN_CC_DOM) != Constants.CC_AFTER_TNS);
		cc_gain_ele_sign = (int)audio_stream.next_bits(Constants.LEN_CC_SGN);
		scl_idx = (int)audio_stream.next_bits(Constants.LEN_CCH_GES);

		/*
		 * coupling channel bitstream
		 * (equivalent to SCE)
		 */
		for(i = 0; i < Constants.LN2; i ++) {
			cc_coef[cidx] = 0;
		}
		info = decoder.winmap[cc_wnd[cidx]];

		if (decoder.getics(cidx, info, 0, cc_wnd, cc_wnd_shape,
			cc_group[cidx], cc_max_sfb, cc_lpflag[cidx], cc_prstflag[cidx],
			sect, cc_coef, factors, tns) == 0)
		return -1;

		/* coupling for first target channel(s) is already at
		 * correct scale
		 */
		ch = shared[0] ? 2 : 1;
		for (j=0; j<ch; j++) {
			for (i=0; i<Constants.MAXBANDS; i++) {
				cc_gain[cidx][target[j]][i] = 1.0f;
			}
		}

		/*
		 * bitstreams for target channel scale factors
		 */
		for (; ch<nch; ) {
			if (audio_stream.next_bits(Constants.LEN_CCH_CGP) > 0) {
				/* common gain */
				int t;
				Hcb hcb;
				int[][] hcw;

				/*  get ind_sw_cce flag */
				ind_sw_cc = (int)audio_stream.next_bits(Constants.LEN_IND_SW_CCE); // @TODO Проверить этот код

				/*  get just one scale factor */
				hcb = decoder.huffman.book[Constants.BOOKSCL];
				hcw = hcb.hcw;
				fac = 0;    /* dpcm relative to 0 */
				t = decoder.huffman.decode_huff_cw(hcw);
				fac += t - Constants.MIDFAC;    /* 1.5 dB */

				/* recover stepsize */
				scale = (float)Math.pow(cc_gain_scale[scl_idx], fac);

				/* copy to gain array */
				n = shared[ch] ? 2 : 1;
				for (m=0; m<n; m++) {
					k=0;
					for (i=0; i<info.nsbk; i++) {
						for (j=0; j<info.sfb_per_sbk[i]; j++) {
							cc_gain[cidx][target[ch]][k++] = scale;
						}
						ch++;
					}
				}
			} else {
				/* must be dependently switched cce */
				ind_sw_cc = 0;
	    
				/* get scale factors
				 * use sectioning of coupling channel
				 */
				decoder.hufffac(info, cidx, cc_group, sect, factors);
//				decoder.hufffac(info, cidx, cc_group[cidx], sect, factors);

				/* recover sign and stepsize */
				scale = cc_gain_scale[scl_idx];
				k=0;
				for (i=0; i<info.nsbk; i++) {
					for (j=0; j<info.sfb_per_sbk[i]; j++) {
						fac = factors[k];
						if (cc_gain_ele_sign > 0) {
							sign = fac & 1;
							fac >>= 1;
						} else {
							sign = 1;
						}
						scale = (float)Math.pow(scale, fac);
						scale *= (sign==0) ? 1 : -1;
						cc_gain[cidx][target[ch]][k++] = scale;
					}
				}
				/* shared gain lists */
				if ( shared[ch] ) {
					for (i=0; i<Constants.MAXBANDS; i++) {
						cc_gain[cidx][target[ch+1]][k] = 
							cc_gain[cidx][target[ch]][k];
					}
					ch++;
				}
			}
		}

		/* process coupling channel the same as other channels, 
		 * except that it can only be a SCE
		 */
		Monopred.predict(info, mip.profile,
		cc_lpflag[cidx], cc_sp_status[cidx],
			cc_prev_quant[cidx], cc_coef[cidx]);
		Monopred.predict_reset(info, cc_prstflag[cidx], cc_sp_status, cc_prev_quant, 
			cidx, cidx);

		for (i=j=0; i<tns.n_subblocks; i++) {
			tns_decode_subblock( cc_coef[cidx] + j,
				info.sfb_per_sbk[i],
				info.sbk_sfb_top[i],
				(tns.info[i]) );
			j += info.bins_per_sbk[i];
		}

		/* invert the cch to ch mapping */
		for (i=0; i<nch; i++) {
			mip.ch_info[target[i]].ncch = cidx+1;
			mip.ch_info[target[i]].cch[cidx] = cidx;
			mip.ch_info[target[i]].cc_dom = cc_dom;
			mip.ch_info[target[i]].cc_ind[cidx] = ind_sw_cc;
		}

		if (decoder.default_config)
			mip.ncch++;
	
		return 1;
	}

	/* transform independently switched coupling channels into
	 * time domain
	 */
	void ind_coupling(MC_Info mip, byte[] wnd, Wnd_Shape[] wnd_shape,
		byte[] cc_wnd, Wnd_Shape[] cc_wnd_shape, float[] cc_coef, float[] cc_state)
	{
		int i, j, cidx, dep, ch, widx;
		float[] data = new float[Constants.LN2];
		for (cidx=0; cidx<mip.ncch; cidx++) {
			if (mip.cc_ind[cidx]) {
				freq2time_adapt(cc_wnd[wn], cc_wnd_shape[wn], cc_coef[cidx],
				cc_state[cidx], data);
				for(i = 0; i < Constants.LN; i++) {
					cc_coef[cidx + i] = data[i]; 
				}
			}
		}
	}
	

	void mix_cc(float[] coef, float[] cc_coef, float[] cc_gain, int ind) {
		int nsbk, sbk, sfb, nsfb, k, top;
		float scale;
		Info info = winmap[win];
		int cc_gain_index = 0;

		if (ind == 0) {    
			/* frequency-domain coupling */
			k = 0;
			nsbk = info.nsbk;
			for (sbk=0; sbk<nsbk; sbk++) {
				nsfb = info.sfb_per_sbk[sbk];
				for (sfb=0; sfb<nsfb; sfb++) {
					top = info.sbk_sfb_top[sbk][sfb];
					scale = cc_gain[cc_gain_index++];
					if (scale == 0) {
						/* no coupling */
						k = top;
					} else {
						/* mix in coupling channel */
						while (k<top) {
							coef[k] += cc_coef[k] * scale;
							k++;
						}
					}
				}
			}
		} else {
			/* time-domain coupling (coef[] is actually data[]!) */
			scale = cc_gain[cc_gain_index];
			for (k=0; k<Constants.LN2; k++) 
				coef[k] += data[k] * scale;
			}	
	}
   
	void coupling(MC_Info mip, float[][] coef, float[][] cc_coef, float[][][] cc_gain,  
		int ch, int cc_dom, int cc_ind)
	{
		int i, j, wn, cch, ind;    
		Ch_Info cip = mip.ch_info[ch];
       
		j=cip.ncch;
		for (i=0; i<j; i++) {
			if ((cc_ind == 0) && (cc_dom != cip.cc_dom[i]) )
				continue;
			wn = cip.widx;
			cch = cip.cch[i];
			ind = cip.cc_ind[i];
			if (AACDecoder.debug[Constants.DEBUG_C])
				System.out.println("mixing cch " + cch + " onto ch " + ch + ", mode " + cc_dom + " " + dep);
			mix_cc(coef[ch], cc_coef[cch], cc_gain[cch][ch], ind);
		}
	}

}
