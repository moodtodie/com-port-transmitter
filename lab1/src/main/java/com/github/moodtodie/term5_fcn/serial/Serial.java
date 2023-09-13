package com.github.moodtodie.term5_fcn.serial;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.nio.charset.StandardCharsets;

public class Serial {
	private static final int BAUDRATE = SerialPort.BAUDRATE_9600;
	private static final int DATABITS = SerialPort.DATABITS_8;
	private static final int STOPBITS = SerialPort.STOPBITS_1;
	private static final int PARITY = SerialPort.PARITY_NONE;

	private final SerialPort port;

	public Serial(String port) {
		this.port = new SerialPort(port);
	}

	public static String[] getPortList(){
		return SerialPortList.getPortNames();
	}

//	public static String findPortPair(String portName){
////		for(String port : SerialPortList.getPortNames()) {
//		for (int i = 0; i < 2; i++) {
//			String port = [i];
//			System.out.println(port);
//			if (!portName.equals(port)){
//				try {
//					if (new Serial(portName, port).test())
//						return port;
//				} catch (SerialPortException e) {
//					throw new RuntimeException(e);
//				}
//			}
//		}
//		return null;
//	}

//	private boolean test() throws SerialPortException {
//		char c = 3;
//		String data = String.valueOf(c);
//		write(port1, data);
//		String receivedData = read(port2);
//		return data.equals(receivedData);
//	}

	public void write(String data) throws SerialPortException {
		// Открываем последовательный порт
		if (!port.isOpened())
			port.openPort();

		// Устанавливаем параметры последовательного порта
		port.setParams(BAUDRATE, DATABITS, STOPBITS, PARITY);

		// Отправляем данные в последовательный порт
		port.writeString(data);

		// Закрываем последовательный порт
		port.closePort();
	}

	public String read() throws SerialPortException {
		// Открываем последовательный порт
		if (!port.isOpened())
			port.openPort();

		// Устанавливаем параметры последовательного порта
		port.setParams(BAUDRATE, DATABITS, STOPBITS, PARITY);

		//	считываем данные из последовательного порта
		byte[] buffer = port.readBytes();

		// Закрываем последовательный порт
		port.closePort();

		if (buffer == null)
			return null;
		return new String(buffer, StandardCharsets.UTF_8);
	}
}
