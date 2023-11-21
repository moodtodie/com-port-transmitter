package com.github.moodtodie.term5_fcn.service;

public class Fcs {
  private final int POLY = 0xE7;
  private final byte[] data;
  private byte fcs;

  public Fcs(byte[] data) {
    this.data = data;
    this.fcs = calculateFcs(data);
  }

  private byte calculateFcs(byte[] data) {
    return (byte) crc8(data, POLY, 0);
  }

  public boolean correctError(byte receivedFcs) {
    int rem = (receivedFcs ^ fcs) & 0xFF;

    int errorBit = crc8ErrorFinder(data.length, POLY, rem);
    data[errorBit / 8] ^= (byte) (1 << (7 - errorBit % 8));

    fcs = calculateFcs(data);

    return checkFcs(receivedFcs);
  }

  public static void corruptByte(byte[] data) {
    // Выберем случайный индекс бита
    int bitIndex = (int) (Math.random() * data.length * 8);

    // Установим бит в противоположное состояние
    data[bitIndex / 8] ^= (byte) (1 << (7 - bitIndex % 8));
  }

  private int crc8(byte[] data, int poly, int rem) {
    for (byte b : data) {
      // Add the data and remainder from the last division operation
      rem ^= b;

      // Perform long division over this byte
      for (int i = 0; i < 8; i++) {
        rem <<= 1;

        // Should we subtract the divisor/polynomial?
        if ((rem & 0x100) != 0) {
          rem ^= poly | 0x100;
        }
      }
    }
    return rem & 0xFF;
  }

  private int crc8ErrorFinder(int num_bytes, int poly, int rem) {
    for (int bit = num_bytes * 8 - 1; bit >= 0; bit--) {
      if (rem == poly) {
        return bit;
      }
      if ((rem & 1) != 0) {
        rem ^= poly | 0x100;
      }
      rem >>= 1;
    }
    return -1;
  }

  public boolean checkFcs(byte receivedFcs) {
    return receivedFcs == fcs;
  }

  public byte[] getData() {
    return data;
  }

  public byte getFcs() {
    return fcs;
  }
}
