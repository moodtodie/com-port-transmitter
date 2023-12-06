package com.github.moodtodie.term5_fcn.serial;

import com.github.moodtodie.term5_fcn.GUI.Window;
import com.github.moodtodie.term5_fcn.service.ByteStuffing;
import com.github.moodtodie.term5_fcn.service.CsmaCd;
import com.github.moodtodie.term5_fcn.service.Packet;
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

        //  Check JAM signal
        Thread jamThread = new Thread(() -> {
          try {
            waitJam();
          } catch (SerialPortException e) {
            throw new RuntimeException(e);
          }
        });

        jamThread.start();

        //  Delay for get JAM
        try {
          jamThread.join(1000); // Ожидаем завершения потока не более 1 секунд
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        if (CsmaCd.jamWasReceived()) {
          CsmaCd.jamReceived(false);
          return;
        }

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

  private void waitJam() throws SerialPortException {
    byte[] buffer = port.readBytes(1);

    //  Check JAM signal
    if (buffer != null && buffer[0] == CsmaCd.getJam()) {
      System.out.println("warn: Get JAM signal");
      CsmaCd.jamReceived(true);
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
