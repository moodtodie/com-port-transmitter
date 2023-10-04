package com.github.moodtodie.term5_fcn.bytestuffing;

import java.nio.charset.StandardCharsets;

public class ByteStuffing {
//    public static byte[] convert(String portName, String data) {
//        byte portNumber = (byte) portName.charAt(portName.length() - 1);
//
//        if (data.getBytes(StandardCharsets.UTF_8).length > 19) {
//            System.out.println("Error: Can't converting, data too long");
//            System.exit(1);
//        }
//
//        Packet packet = new Packet(flag, ZERO, portNumber, data.getBytes(StandardCharsets.UTF_8), ZERO);
//        // use COM-port to send packet.getBytes()
//        return packet.getBytes();
//    }

  public static String readString(byte[] packet) {
    return new Packet(packet).toString();
  }

  //  ================================================================================================================

  private static final StringBuilder data = new StringBuilder();

  public static void addData(String ch) {
    data.append(ch);
  }

  public static String getData() {
    return data.toString();
  }

  public static String unstaffing(byte[] data) {
    return null;
  }

  public static int getDataByteSize() {
    return data.toString().getBytes(StandardCharsets.UTF_8).length;
  }

  public static void clearData() {
    while (data.length() != 0) {
      data.deleteCharAt(0);
    }
  }
}
