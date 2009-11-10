/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.multimedia.video.mpeg4.video;

import java.awt.Graphics;

public class J2Utils {
	
	public static void setRenderingHints(Graphics g, boolean smooth_video, boolean antialiasing) {
		if(smooth_video) {
			((java.awt.Graphics2D)g).setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		} else {
			((java.awt.Graphics2D)g).setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		}
		if(antialiasing) {
		    ((java.awt.Graphics2D)g).setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		} else {
		    ((java.awt.Graphics2D)g).setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		}
	}
	
}
