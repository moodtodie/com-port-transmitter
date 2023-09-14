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

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.isRXCHAR() && event.getEventValue() > 0) { // data is available
            try {
                byte[] dataByteFormat = port.readBytes(event.getEventValue());

                PortManager.addByteReceived(event.getEventValue());

                String data = new String(dataByteFormat, StandardCharsets.UTF_8);

                Window.appendOutputText(data);
            } catch (SerialPortException ex) {
                ex.printStackTrace();
            }
        }
    }
}
