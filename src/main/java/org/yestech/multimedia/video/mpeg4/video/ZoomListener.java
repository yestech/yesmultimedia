/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.multimedia.video.mpeg4.video;

import org.yestech.multimedia.video.mpeg4.ViewPort;

public class ZoomListener extends ZoomThread {
	public ZoomListener(ViewPort viewPort) {
		super(viewPort, 0);
	}
	
	public void run() {
		try {
			while(true) {
				synchronized(this) {
					this.wait();
					synchronized( viewPort) {
						wasZoomed= viewPort.zoom(ZOOM_FUNC);
						//System.out.println("Zoom "+viewPort.toString());
					}
				}
			}
		} catch (InterruptedException ex) {
			isZooming = false;
			return;
		}
	}
}
