/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.multimedia.video.mpeg4.panel;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

/**
 * Button
 * 
 */
public class Button extends Component {
	
	private Image on_1_image;
	private Image on_2_image;
	private Image off_image;
	
	private int x_coord;
	private int y_coord;
	
	private boolean enabled = true;
	private boolean firstState = true;

	public Button(Image on_1_image, Image on_2_image, Image off_image, int x_coord, int y_coord, int top_x_coord, int top_y_coord) {
		super();
		this.setBounds(x_coord + top_x_coord, y_coord + top_y_coord, 17, 16);
		this.x_coord = x_coord;
		this.y_coord = y_coord;
		this.on_1_image = on_1_image;
		this.on_2_image = on_2_image;
		this.off_image = off_image;
		this.enableEvents(AWTEvent.MOUSE_EVENT_MASK);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
	
/*	
	public final static String BASE_PATH = "/mediaframe/control_set/";
	
	public Button(String on_1_image_name, String on_2_image_name, String off_image_name, int x_coord, int y_coord, int top_x_coord, int top_y_coord) {
		super();
		this.setBounds(x_coord + top_x_coord, y_coord + top_y_coord, 17, 16);
		this.x_coord = x_coord;
		this.y_coord = y_coord;
		this.on_1_image = getImage(on_1_image_name);
		this.on_2_image = getImage(on_2_image_name);
		this.off_image = getImage(off_image_name);
		this.enableEvents(AWTEvent.MOUSE_EVENT_MASK);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
	
	private Image getImage(String image_name) {
		if(image_name == null) {
			return null;
		}
		InputStream is = getClass().getResourceAsStream(BASE_PATH + image_name);
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
		if(image == null) {
			image = createImage(17, 16);
		}

		MediaTracker mediaTracker = new MediaTracker(this);
		mediaTracker.addImage(image, 0);
		try {
			mediaTracker.waitForAll();
		} catch (Exception ex) {
		}
		return image;
	}
*/	
	public void paint(Graphics g) {
		if(isEnabled()) {
			if(firstState) {
				if(on_1_image != null) {
					g.drawImage(on_1_image, x_coord, y_coord, this);
				}
			} else {
				if(on_2_image != null) {
					g.drawImage(on_2_image, x_coord, y_coord, this);
				}
			}
		} else {
			if(off_image != null) {
				g.drawImage(off_image, x_coord, y_coord, this);
			}
		}
	}
	
	public void setState(boolean firstState) {
		this.firstState = firstState;	
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(17, 16);
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}
