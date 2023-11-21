package com.github.moodtodie.term5_fcn.service;

import java.nio.charset.StandardCharsets;

public class Packet {
  private static final byte[] FLAG = "#s".getBytes(StandardCharsets.UTF_8);
  private static final byte ZERO = (byte) 0;  //  Destination Address

  private final byte[] flag = new byte[2];
  private byte dst;
  private byte src;

  private final byte[] data = new byte[19];
  private byte fcs;

  public Packet(byte src, byte[] data, byte fcs) {
    if (data.length < 19) {
      System.out.println("Error: Can't converting, data too long");
      return;
    }

    System.arraycopy(FLAG, 0, this.flag, 0, 2);
    this.dst = ZERO;
    this.src = src;
    System.arraycopy(data, 0, this.data, 0, 19);
    this.fcs = fcs;
  }

  public Packet(byte[] flag, byte dst, byte src, byte[] data, byte fcs) {
    if (flag.length < 2 || data.length < 19) {
      System.out.println("Error: Can't create Package");
      return;
    }

    System.arraycopy(flag, 0, this.flag, 0, 2);
    this.dst = dst;
    this.src = src;
    System.arraycopy(data, 0, this.data, 0, 19);
    this.fcs = fcs;
  }

  public Packet(byte[] _package) {
    if (_package.length < 24) {
      System.out.println("Error: Can't create Package, byte array too short");
      return;
    }
    System.arraycopy(_package, 0, this.flag, 0, 2);
    this.dst = _package[2];
    this.src = _package[3];
    System.arraycopy(_package, 4, this.data, 0, 19);
    this.fcs = _package[23];
  }

  public void setData(byte[] data) {
    System.arraycopy(data, 0, this.data, 0, 19);
  }

  public byte[] getFlag() {
    return flag;
  }

  public byte getDst() {
    return dst;
  }

  public byte getSrc() {
    return src;
  }

  public byte[] getData() {
    return data;
  }

  public byte getFcs() {
    return fcs;
  }



  public byte[] getBytes() {
    byte[] bytes = new byte[24];

    //  Flag
    System.arraycopy(flag, 0, bytes, 0, flag.length);

    //  Destination Address
    bytes[2] = dst;

    //  Source Address
    bytes[3] = src;

    //  Data
    System.arraycopy(data, 0, bytes, 4, data.length);

    //  FCS
    bytes[23] = fcs;

    return bytes;
  }
}
