package com.github.moodtodie.term5_fcn.serial;

import com.github.moodtodie.term5_fcn.GUI.Window;
import com.github.moodtodie.term5_fcn.bytestuffing.ByteStuffing;
import com.github.moodtodie.term5_fcn.bytestuffing.Packet;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.nio.charset.StandardCharsets;

public class PortListener implements SerialPortEventListener {
  SerialPort port;

  public PortListener(SerialPort port) {
    this.port = port;
  }

  @Override
  public void serialEvent(SerialPortEvent event) {
    if (event.isRXCHAR() && event.getEventValue() > 0) { // data is available
      try {
        //  Get data
        byte[] buffer = port.readBytes(event.getEventValue());

        //  Unpacketing
        Packet packet = new Packet(buffer);
        PortManager.setPacket(packet);

        System.out.print("data before fix: ");
        System.out.println(new String(packet.getData(), StandardCharsets.UTF_8));

        //  Check FCS
        byte[] data = PortManager.fixData();

        System.out.print("data after fix: ");
        System.out.println(new String(packet.getData(), StandardCharsets.UTF_8));

        //  Unstuffing
        String massage = ByteStuffing.unstaffing(data);

        //  Send text
        PortManager.addByteReceived(massage.getBytes(StandardCharsets.UTF_8).length);
        appendTextArea(massage);
      } catch (SerialPortException ex) {
        System.out.println("Type: " + ex.getExceptionType() +
            ", Method: " + ex.getMethodName() +
            ", Port: " + ex.getPortName() +
            ", Message: " + ex.getMessage());
        ex.printStackTrace();
      }
    }
  }

  private void appendTextArea(char ch) {
    if (ch == 13) {
      Window.appendOutputText("\n");
    } else {
      Window.appendOutputText(String.valueOf(ch));
    }
  }

  private void appendTextArea(String msg) {
    for (int i = 0; i < msg.length(); i++)
      appendTextArea(msg.charAt(i));
  }
}
