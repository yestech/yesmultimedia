/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.multimedia.video.mpeg4.video;

import org.yestech.multimedia.video.mpeg4.ViewPort;

public class ZoomThread extends Thread {
	ViewPort viewPort;
	boolean wasZoomed;
	boolean isZooming = false;
	int ZOOM_FUNC;
	int ZOOM_TIME;
	
	public ZoomThread(ViewPort viewPort, int zoomTime) {
		this.viewPort = viewPort;
		ZOOM_TIME = zoomTime;
	}
	
	public boolean getIsZooming() {
		return isZooming;
	}
	
	public void stopZooming() {
		isZooming = false;
	}

	public void setIsZoomingIn() {
		isZooming = true;
		ZOOM_FUNC = ViewPort.ZOOM_IN;
	}
	public void setIsZoomingOut() {
		isZooming = true;
		ZOOM_FUNC = ViewPort.ZOOM_OUT;
	}
	
	public void run() {
		try {
			while (isZooming) {
				synchronized( viewPort) {
					wasZoomed= viewPort.zoom(ZOOM_FUNC);
					//System.out.println("Zoom "+viewPort.toString());
				}
				Thread.sleep(ZOOM_TIME);
			}
		} catch (InterruptedException ex) {
			isZooming = false;
			return;
		}
	}

}
