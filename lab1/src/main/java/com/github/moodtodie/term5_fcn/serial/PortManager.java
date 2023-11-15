package com.github.moodtodie.term5_fcn.serial;

import com.github.moodtodie.term5_fcn.GUI.Window;

import com.github.moodtodie.term5_fcn.bytestuffing.Fcs;
import com.github.moodtodie.term5_fcn.bytestuffing.Packet;
import jssc.SerialPortException;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class PortManager {
    private static Serial port = null;
    private static String stopBits = "1";
    private static int byteReceived = 0;
    private static Packet packet;
    private static boolean dataIsCorrect = false;

    public static void setPacket(Packet packet){
        if (new Random().nextInt(10) < 3){  //  Range from 0 to 9
            byte[] data = packet.getData();
            Fcs.corruptByte(data);
            packet.setData(data);
            System.out.println("info: INCORRECT VALUE!\ndata: " + new String(packet.getData(), StandardCharsets.UTF_8));
        }
        dataIsCorrect = new Fcs(packet.getData()).checkFcs(packet.getFcs());
        PortManager.packet = packet;
    }

    public static boolean dataIsCorrect(){
        return dataIsCorrect;
    }

    public static byte[] fixData(){
        Fcs fcs = new Fcs(packet.getData());
        if (dataIsCorrect() || fcs.correctError(packet.getFcs())){
            return fcs.getData();
        }
        return packet.getData();
    }

    public static void setPort(String portName) throws SerialPortException {
        if (port != null) {
            port.close();
        }
        port = new Serial(portName);
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
