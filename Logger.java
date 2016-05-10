package com.custommods.ai;

import org.lwjgl.Sys;

public class Logger {

	private static boolean debug = true;
	public static final int ERROR = -1;
	public static final int LOG = 1;
	
	public static void debug(String text){
		if (debug){
			System.out.println("DEBUG: " + text);
		}
	}
	
	///
	public static void debug(String text, int type){
		if (debug){
			switch (type) {
			case ERROR:
				System.out.println("ERROR: " + text);
				break;
			case LOG:
				System.out.println("ERROR: " + text);
				break;
			default:
				debug("could not find the debug type", ERROR);
				break;
			}
		}
	}
}
