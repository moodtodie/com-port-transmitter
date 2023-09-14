package com.github.moodtodie.term5_fcn.serial;

import com.github.moodtodie.term5_fcn.GUI.Window;

import jssc.SerialPortException;

public class PortManager {
    private static Serial port = null;
    private static String stopBits = "1";
    private static int byteReceived = 0;

    public static void setPort(String portName) throws SerialPortException {
        if (port != null) {
            port.close();
        }
        port = new Serial(portName, getStopBits());
    }

    public static Serial getPort() {
        return port;
    }

    public static void addByteReceived(int bytes) {
        byteReceived += bytes;
        Window.setLabelByteReceived(byteReceived);

    }

    public static void setStopBits(String stopBit) {
        stopBits = stopBit;
        System.out.println("info: stopbit set " + stopBits);
    }

    public static int getStopBits() {
        switch (stopBits) {
            case "1":
                return 1;
            case "1.5":
                return 3;
            case "2":
                return 2;
        }
        return 1;
    }
}
