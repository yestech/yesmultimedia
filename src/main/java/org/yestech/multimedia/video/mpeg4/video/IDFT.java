/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.multimedia.video.mpeg4.video;

/**
 * IDFT
 */
public class IDFT {
	
	public final static int SHIFT_BITS = 12;
	
	private int pixels_per_line;
	
	private long[][] coeff = new long[8][8];
	private long[][] tmp = new long[8][8];
	
	public final long C2 = (long)((1 << SHIFT_BITS) * 2d * Math.cos(Math.PI / 8d));
	public final long C4 = (long)((1 << SHIFT_BITS) * Math.sqrt(2d));
	public final long C6 = (long)((1 << SHIFT_BITS) * 2d * Math.sin(Math.PI / 8d));
	public final long Q = C2 - C6;
	public final long R = C2 + C6;
	
	public IDFT(int pixels_per_line) {
		super();
		this.pixels_per_line = pixels_per_line;
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				// calculates the (4 / Cu) * cos(PI * u / 16) values
				double k = 4d; 
				if((i == 0) && (j == 0)) {
					k = 8d;
				} else if ((i == 0) || (j == 0)){
					k = 8d / Math.sqrt(2d); 
				}
				coeff[i][j] = (long)((1 << SHIFT_BITS) * ((Math.cos((Math.PI * (double)i) / 16d)) * Math.cos((Math.PI * (double)j) / 16d) / k));
			}
		}
	}
	
	private long a0, a1, a2, a3, a4, a5, a6, a7, tmp1, tmp2;
	private long b0, b1, b2, b3, b4, b5, b6, b7, tmp4;
	private long tmp3, n0, n1, n2, n3, n4, n5, n6, n7;
	private long m0, m1, m2, m3, m4, m5, m6, m7;
	
	public void idft_line(long[][] block, int index, boolean horizontal_direction) {
		if(horizontal_direction) {
			b0 = a0 = block[index][0] >> 4;
			b1 = a1 = block[index][4] >> 3;
			a2 = (block[index][2] >> 3) - (block[index][6] >> 3);
			n6 = b3 = a3 = (block[index][2] >> 3) + (block[index][6] >> 3);
			a4 = (block[index][5] >> 3) - (block[index][3] >> 3);
			tmp1 = (block[index][1] >> 3) + (block[index][7] >> 3);
			tmp2 = (block[index][3] >> 3) + (block[index][5] >> 3);
			a6 = (block[index][1] >> 3) - (block[index][7] >> 3);
		} else {
			b0 = a0 = block[0][index] >> 4;
			b1 = a1 = block[4][index] >> 3;
			a2 = (block[2][index] >> 3) - (block[6][index] >> 3);
			n6 = b3 = a3 = (block[2][index] >> 3) + (block[6][index] >> 3);
			a4 = (block[5][index] >> 3) - (block[3][index] >> 3);
			tmp1 = (block[1][index] >> 3) + (block[7][index] >> 3);
			tmp2 = (block[3][index] >> 3) + (block[5][index] >> 3);
			a6 = (block[1][index] >> 3) - (block[7][index] >> 3);
		}

		a5 = tmp1 - tmp2;
		m0 = n7 = b7 = a7 = tmp1 + tmp2;
			
		b2 = (C4 * a2) >> SHIFT_BITS;
		tmp4 = (C6 * (a4 + a6)) >> SHIFT_BITS; 
		n5 = b4 = ((-Q * a4) >> SHIFT_BITS) - tmp4;
		b5 = (C4 * a5) >> SHIFT_BITS;
		b6 = ((R * a6) >> SHIFT_BITS) - tmp4;
		
		tmp3 = b6 - b7;
		m1 = n0 = tmp3 - b5;
		n1 = b0 - b1;
		n2 = b2 - b3;
		n3 = b0 + b1;
		m2 = n4 = tmp3;
		
		m3 = n1 + n2;
		m4 = n3 + n6;
		m5 = n1 - n2;
		m6 = n3 - n6;
		m7 = n5 - n0;
		if(horizontal_direction) {
			block[index][0] = m4 + m0;
			block[index][1] = m3 + m2;
			block[index][2] = m5 - m1;
			block[index][3] = m6 - m7;
			block[index][4] = m6 + m7;
			block[index][5] = m5 + m1;
			block[index][6] = m3 - m2;
			block[index][7] = m4 - m0;
		} else {
			block[0][index] = m4 + m0;
			block[1][index] = m3 + m2;
			block[2][index] = m5 - m1;
			block[3][index] = m6 - m7;
			block[4][index] = m6 + m7;
			block[5][index] = m5 + m1;
			block[6][index] = m3 - m2;
			block[7][index] = m4 - m0;
		}
	}
		
	
	public void idft(int[][] block, int[] pixel_data, int block_pointer) {
		int i, j;
		for(i = 0; i < 8; i++) {
			for(j = 0; j < 8; j++) {
				tmp[i][j] = block[i][j] * coeff[i][j];
			}
		}

		for(i = 0; i < 8; i++) {
			idft_line(tmp, i, true);
		}
		for(i = 0; i < 8; i++) {
			idft_line(tmp, i, false);
		}

		for(i = 0; i < 8; i++) {
			for(j = 0; j < 8; j++) {
				pixel_data[block_pointer + j] = (int)((tmp[i][j]) >> (SHIFT_BITS));
			}
			block_pointer += pixels_per_line;
		}
	}

}
