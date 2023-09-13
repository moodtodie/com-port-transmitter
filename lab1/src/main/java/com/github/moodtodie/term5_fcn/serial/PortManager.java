package com.github.moodtodie.term5_fcn.serial;

public class PortManager {
	private static String port = null;

	public static void init(String portName){
		port = portName;
	}

	public static String getPort(){
		return port;
	}
}
