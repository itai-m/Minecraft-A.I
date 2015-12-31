package com.custommods.ai;

import org.lwjgl.Sys;

public class Logger {

	private static boolean debug = true;
	
	public static void debug(String text){
		if (debug){
			System.out.println("DEBUG: " + text);
		}
	}
}
