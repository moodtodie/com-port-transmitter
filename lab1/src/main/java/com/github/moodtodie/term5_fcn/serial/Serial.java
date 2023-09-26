package com.github.moodtodie.term5_fcn.serial;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows communication with a serial port
 */
public class Serial {
    private static final int BAUDRATE = SerialPort.BAUDRATE_9600;
    private static final int DATABITS = SerialPort.DATABITS_8;
    private static final int PARITY = SerialPort.PARITY_NONE;

    private SerialPort port;

    /**
     * Opens a serial port and provides the ability to read/write data using this port
     *
     * @param port name of the serial port that will be used
     * @throws SerialPortException
     */
    public Serial(String port) throws SerialPortException {
        setPort(port);
        open();
    }

    /**
     * Opens a serial port
     *
     * @throws SerialPortException
     */
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

    /**
     * Closes the serial port
     *
     * @throws SerialPortException
     */
    public void close() throws SerialPortException {
        // Закрываем последовательный порт
        port.closePort();

        System.out.println("info: " + port.getPortName() + " is close");
    }

    /**
     * Transmits data via serial port
     *
     * @param data data to be transmitted
     * @throws SerialPortException
     */
    public void write(String data) throws SerialPortException {
        port.setParams(BAUDRATE, DATABITS, PortManager.getStopBits(), PARITY);

        // Отправляем данные в последовательный порт
        port.writeBytes(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Transmits data via serial port
     *
     * @param data data to be transmitted
     * @throws SerialPortException
     */
    public void write(byte[] data) throws SerialPortException {
        port.setParams(BAUDRATE, DATABITS, PortManager.getStopBits(), PARITY);

        // Отправляем данные в последовательный порт
        port.writeBytes(data);
    }

    /**
     * Automatically selects the first free serial port
     */
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

    /**
     * Sets the new serial port to be used
     *
     * @param portName serial port name
     */
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

    /**
     * Get the baudrate value used
     *
     * @return baudrate value
     */
    public static String getBaudRate() {
        return String.valueOf(BAUDRATE);
    }

    /**
     * Get a list of unused serial ports
     *
     * @return list of unused serial ports
     */
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
            if (PortManager.getPort() == null)
                System.exit(1);
        }

        // Возвращаем новый массив
        return newStrings;
    }

    /**
     * Get the name of this serial port
     *
     * @return name of this serial port
     */
    public String getPortName() {
        return port.getPortName();
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
