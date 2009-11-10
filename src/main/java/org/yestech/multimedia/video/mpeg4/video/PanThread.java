/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.multimedia.video.mpeg4.video;

import java.awt.Point;

import org.yestech.multimedia.video.mpeg4.ViewPort;

public class PanThread extends Thread {
	ViewPort viewPort;
	boolean wasPanned;
	boolean isPanning = false;
	Point PAN_FUNC;
	int PAN_TIME;
	
	public PanThread(ViewPort viewPort, int panTime) {
		this.viewPort = viewPort;
		PAN_TIME = panTime;
	}
	
	public boolean getIsPanning() {
		return isPanning;
	}
	
	public void stopPanning() {
		isPanning = false;
	}

	public void setIsPanningLeft() {
		isPanning = true;
		PAN_FUNC = new Point(-5,0);
	}
	public void setIsPanningRight() {
		isPanning = true;
		PAN_FUNC = new Point(5,0);
	}
	public void setIsPanningUp() {
		isPanning = true;
		PAN_FUNC = new Point(0,-5);
	}
	public void setIsPanningDown() {
		isPanning = true;
		PAN_FUNC = new Point(0,5);
	}
	
	public void run() {
		try {
			while (isPanning) {
				synchronized( viewPort) {
					wasPanned= viewPort.move(PAN_FUNC);
					//System.out.println("Pan "+viewPort.toString());
				}
				Thread.sleep(PAN_TIME);
			}
		} catch (InterruptedException ex) {
			isPanning = false;
			return;
		}
	}

}
