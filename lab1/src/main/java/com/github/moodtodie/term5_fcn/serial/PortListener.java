package com.github.moodtodie.term5_fcn.serial;

import com.github.moodtodie.term5_fcn.GUI.Window;
import jssc.SerialPortException;

public class PortListener extends Thread {
	private static Serial port;
	private final Window window;
	private volatile boolean isActive = true;

	public PortListener(Window window, String serialPort){
		this.window = window;
		port = new Serial(serialPort);
	}

	public static void updatePort(String serialPort){
		port = new Serial(serialPort);
	}

	public void listen(){
		while (isActive){
			try {
				String data = port.read();
				window.appendOutputText(data);
			} catch (SerialPortException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void kill(){
		isActive = false;
	}
}
