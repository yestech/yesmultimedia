/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */

package org.yestech.multimedia.video.mpeg4;

import java.awt.*;

/**
 * A viewport to an image.  This rectangle is the portion of
 * the image that is drawn on the screen
 * 
 * @version 1.0
 */

public class ViewPort extends Rectangle {
  private static final long serialVersionUID = 1L;
  public final static int ZOOM_IN=1;
  public final static int ZOOM_OUT=0;
  private Image img=null;
  private Dimension imgDim=null;
  private int zoomDirection=ZOOM_IN; // start at zoomIn
  private Point center=new Point();
  private boolean moved=false;
  private int zoomStep=5;  // shrink/grow in all directions
  private Component imgObs=null;
  private Rectangle monitorRect=null;
  private Rectangle monitorVPort=null;
  
  private boolean getShowMonitor = false;

  public ViewPort() { super(); }

  public ViewPort(Component imgObs,Image img, Dimension imgDim, int zoomStep) {
    super();
    this.imgObs=imgObs;
    this.img=img;
    this.imgDim=imgDim;
    this.zoomStep=zoomStep;
    reset();
    getCenter();
  }
  /**
   *
   * @param g
   */
  public void draw (int topx, int topy, Graphics2D g) {
    g.drawImage(this.img,
                topx,topy,
                this.imgDim.width,
                this.imgDim.height,
                x,y,
                x+width,
                y+height,
                imgObs);
    if (this.isGetShowMonitor()) drawMonitor(g);
  }
  public void dumpImage() {
	 System.out.println("img width=" + this.imgDim.width+ " img height=" + this.imgDim.height + " x="+ x + " y="+ y + " width=" + width + " height="+height);
  }
  public void dumpMonitor() {
    System.out.print("mRect:["+
                       this.monitorRect.x+","+
                       this.monitorRect.y+","+
                       this.monitorRect.width+","+
                       this.monitorRect.height+"]"
                       );
    System.out.println(" mVPRect:["+
                      this.monitorVPort.x+","+
                      this.monitorVPort.y+","+
                      this.monitorVPort.width+","+
                      this.monitorVPort.height+"]"
                      );

  }
  /**
   *
   * @param g
   */
  public void drawMonitor (Graphics2D g) {
    if (monitorRect==null) {
      monitorRect = new Rectangle();
      monitorVPort = new Rectangle();
    }
    monitorRect.width=this.imgDim.width/6;
    monitorRect.height=(int)((double)monitorRect.width/getRatio());
    monitorRect.x=this.imgDim.width-monitorRect.width;
    monitorRect.y=this.imgDim.height+5;

    monitorVPort.width=width/6;
    monitorVPort.height=(int)((double)monitorVPort.width/getRatio());
    int dX=x/6;
    int dY=y/6;
    monitorVPort.x=monitorRect.x+dX;
    monitorVPort.y=monitorRect.y+dY;
    g.drawImage(this.img,
                monitorRect.x,
                monitorRect.y,
                monitorRect.x+monitorRect.width,
                monitorRect.y+monitorRect.height,
                0,0,
                this.imgDim.width,
                this.imgDim.height,
                imgObs);
    Color c= g.getColor();
    g.setColor(Color.cyan);
    g.drawRect(monitorVPort.x,
               monitorVPort.y,
               monitorVPort.width-1,
               monitorVPort.height-1);
    g.setColor(Color.white);
    int yPos=imgDim.height+22;
//    g.drawString("Image: "+applet.getImgURLStr(),4,yPos);
    yPos+=15;
    g.drawString("dimension: "+this.imgDim.width+", "+this.imgDim.height,4,yPos);
    yPos+=15;
    g.drawString("zoom: "+(double)this.imgDim.width/(double)width,4,yPos);
    yPos+=15;
    g.drawString("viewport: "+this.toString(), 4,yPos);
    yPos+=15;
    g.setColor(Color.yellow);
    g.drawString("Double click to reset", 4,yPos);
    g.setColor(c);
  }

  public String toString () {
    //return "vPort:["+x+","+y+","+width+","+height+"] img:["+imgDim.width+","+imgDim.height+"]"+
    //     " ratio: "+getRatio();
    return x+", "+y+", "+width+", "+height;
  }
  public void reset () {
    x=0;
    y=0;
    width=this.imgDim.width;
    height=this.imgDim.height;
  }
  private double getRatio () {
    return (double)this.imgDim.width/(double)this.imgDim.height;
  }
  /**
   * Grow the viewport, i.e. zoom out
   * @return
   */
  private boolean growView () {
    if ( ((this.width + zoomStep*2) > this.imgDim.width) ||
         ((this.height + zoomStep*2) > this.imgDim.height) ) return false;

    width += zoomStep*2;
    x -= zoomStep;
    //height += zoomStep*2;
    //y -= zoomStep;
    int oldH=height;
    height = (int)((double)width/getRatio());
    y -= (height-oldH)/2;
    // may have to reset X and Y for full image frame
    if( (x + this.width) > this.imgDim.width || x < 0) x = 0;
    if( (y + this.height) > this.imgDim.height || y < 0) y = 0;
    return true;
  }
  /**
   *Shrink the viewPort, i.e. zoom in
   * @return
   */
  private boolean shrinkView () {
    if (  ((this.width - zoomStep*2) < 1) ||
          ((this.height - zoomStep*2) < 1) ) return false;
    width -= zoomStep*2;
    x += zoomStep;
    //height -= zoomStep*2;
    //y += zoomStep;
    int oldH=height;
    height = (int)((double)width/getRatio());
    y += (oldH-height)/2;
    return true;
  }
  /**
   * set centroid from current viewport
   */
  private void getCenter() {
    center.x=x+(x+width-x)/2;
    center.y=y+(y+height-y)/2;
  }
  /**
   *
   * @return
   */
  public boolean wasMoved () {
    return this.moved;
  }
  /**
   *
   * @param p
   * @return
   */
  public boolean move(Point p) {
    moved=false;
    if (x+p.x >= 0 && (x+width)+p.x <=imgDim.width) {
      x+=p.x;
      moved=true;
    }
    if (y+p.y >= 0 && (y+height)+p.y <=imgDim.height) {
      y+=p.y;
      moved=true;
    }
    return wasMoved();
  }
  /**
   *
   * @param zoomDirection
   * @return
   */
  public boolean zoom(int zoomDirection) {
    this.zoomDirection=zoomDirection;
    switch (this.zoomDirection) {
      case ZOOM_IN:
        return shrinkView();
      case ZOOM_OUT:
        return growView();
    }
    return false;
  }

private boolean isGetShowMonitor() {
	return getShowMonitor;
}


}