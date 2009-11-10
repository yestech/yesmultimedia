/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.multimedia.video.mpeg4;

import java.applet.Applet;

import sun.plugin.javascript.JSObject;

/**
 * JSCallback
 */
public final class JSCallback {
	
	private static boolean js_error = false;
	
	public static void startCallBack(Applet applet) {
		if(!js_error) {
			try {
				JSObject win = (JSObject) JSObject.getWindow(applet);
				win.call("framepump_start", null);
			} catch (Throwable tr) {
				js_error = true;
			}
		}
	}

	public static void readyToPlayCallBack(Applet applet) {
		if(!js_error) {
			try {
				JSObject win = (JSObject) JSObject.getWindow(applet);
				win.call("framepump_ready", null);
			} catch (Throwable tr) {
				js_error = true;
			}
		}
	}

	public static void stopPlayCallBack(Applet applet) {
		if(!js_error) {
			try {
				JSObject win = (JSObject) JSObject.getWindow(applet);
				win.call("framepump_stop", null);
			} catch (Throwable tr) {
				js_error = true;
			}
		}
	}

}
