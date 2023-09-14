package com.github.moodtodie.term5_fcn.serial;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Serial {
    private static final int BAUDRATE = SerialPort.BAUDRATE_9600;
    private static final int DATABITS = SerialPort.DATABITS_8;
    private static int STOPBITS = SerialPort.STOPBITS_1;
    private static final int PARITY = SerialPort.PARITY_NONE;

    private SerialPort port;

    public Serial(String port, int stopBits) throws SerialPortException {
        setPort(port);
        Serial.STOPBITS = stopBits;
        open();
    }

    public Serial(int stopBits) throws SerialPortException {
        autoSetPort();
        Serial.STOPBITS = stopBits;
        open();
    }

    private void open() throws SerialPortException {
        if (port == null) {
            autoSetPort();
        }

        if (port.isOpened()) {
            System.out.println("Error: serial port \"" + port.getPortName() + "\" is busy");
            return;
        }

        // Открываем последовательный порт
        port.openPort();

        port.addEventListener(new PortListener(port), SerialPort.MASK_RXCHAR);

        System.out.println("info: " + port.getPortName() + " is open");
    }

    public void close() throws SerialPortException {
        // Закрываем последовательный порт
        port.closePort();

        System.out.println("info: " + port.getPortName() + " is close");
    }

    public void write(String data) throws SerialPortException {
        port.setParams(BAUDRATE, DATABITS, STOPBITS, PARITY);

        // Отправляем данные в последовательный порт
        port.writeBytes(data.getBytes(StandardCharsets.UTF_8));
    }

    private void autoSetPort() {
        for (String portName : getPortList()) {
            SerialPort serialPort = new SerialPort(portName);
            if (!serialPort.isOpened()) {
                setPort(serialPort.getPortName());
                return;
            }
        }
        System.exit(1);
    }

    public void setPort(String portName) {
        SerialPort serialPort = new SerialPort(portName);
        if (!serialPort.isOpened()) {
            this.port = serialPort;
            System.out.println("info: set serial port " + this.port.getPortName());
            return;
        }
        System.out.println("Error: serial port \"" + portName + "\" is busy");
        System.exit(1);
    }

    public static String getBaudRate() {
        return String.valueOf(BAUDRATE);
    }

    public static void test(String name) {
        for (String s : SerialPortList.getPortNames()) {
            if (s.equals(name)) {
                try {
                    System.out.println(name + " is opened: " + new SerialPort(name).openPort());
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String[] getPortList() {
        List<String> nonNullStrings = new ArrayList<>();

        // Перебираем массив строк
        for (String string : SerialPortList.getPortNames()) {
            // Если строка не равна null, добавляем ее в новый массив

            if (!Serial.isOpened(string)) {
                nonNullStrings.add(string);
            }
        }

        // Создаем новый массив из строк, которые не равны null
        String[] newStrings = new String[nonNullStrings.size()];
        nonNullStrings.toArray(newStrings);

        if (newStrings.length < 1) {
            System.out.println("Error: The serial ports cannot be found.");
            System.exit(1);
        }

        // Возвращаем новый массив
        return newStrings;
    }

    private static boolean isOpened(String port) {
        try {
            SerialPort sp = new SerialPort(port);
            sp.openPort();
            sp.closePort();
            return false;
        } catch (SerialPortException e) {
            return true;
        }
    }
}
