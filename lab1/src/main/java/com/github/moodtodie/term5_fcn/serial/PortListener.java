package com.github.moodtodie.term5_fcn.serial;

import com.github.moodtodie.term5_fcn.GUI.Window;
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

    private int bytesCounter = 0;
    private final StringBuilder buffer = new StringBuilder();

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.isRXCHAR() && event.getEventValue() > 0) { // data is available
            try {
                byte[] dataByteFormat = port.readBytes(event.getEventValue());

                bytesCounter += event.getEventValue();

//                PortManager.addByteReceived(event.getEventValue());

                this.buffer.append(new String(dataByteFormat, StandardCharsets.UTF_8));
//                String data = new String(dataByteFormat, StandardCharsets.UTF_8);

                if (bytesCounter >= 24) {
                    PortManager.addByteReceived(bytesCounter);
                    String data = this.buffer.toString();
                    for (int i = 0; i < data.length(); i++) {
                        appendTextArea(data.charAt(i));
                    }
                    bytesCounter = 0;
                }
            } catch (SerialPortException ex) {
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
}
