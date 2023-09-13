package com.github.moodtodie.term5_fcn;

import com.github.moodtodie.term5_fcn.serial.PortManager;

public class App {
	public static void main(String[] args) {
		PortManager.init("/dev/pts/1");
		com.github.moodtodie.term5_fcn.GUI.Window.main(args);

//		Serial com1 = new Serial("/dev/pts/1");
//		Serial com2 = new Serial("/dev/pts/2");
//		String[] data = {"Hello, world!", "Привет мир"};
//
//		try {
//			com1.write(data[0]);
//			System.out.println("\nin: " + data[0] + "\nout: " + com2.read());
//		} catch (SerialPortException e) {
//			throw new RuntimeException(e);
//		}
//
//
//		try {
//			com2.write(data[1]);
//			System.out.println("\nin: " + data[1] + "\nout: " + com1.read());
//		} catch (SerialPortException e) {
//			throw new RuntimeException(e);
//		}
	}
}
