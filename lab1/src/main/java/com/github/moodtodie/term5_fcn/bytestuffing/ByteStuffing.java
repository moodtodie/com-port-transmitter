package com.github.moodtodie.term5_fcn.bytestuffing;

import java.nio.charset.StandardCharsets;

/**
 * Problems:
 * Ban the splitting of the combination "#r$"
 */

public class ByteStuffing {
  private static final StringBuilder data = new StringBuilder();

  public static void addData(String ch) {
    data.append(ch);
    if (getDataByteSize() >= 19)
      staffingData();
  }

  public static String getData() {
    if (getDataByteSize() < 19)
      return "";

    if (getDataByteSize() == 19) {
      String string = data.toString();
      data.delete(0, data.length());
      return string;
    }

    System.out.println("info: data bytes length=" + getDataByteSize());
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < 19;) {

      //  String.valueOf(ch).getBytes(StandardCharsets.UTF_8).length
      if (String.valueOf(data.charAt(0)).getBytes(StandardCharsets.UTF_8).length + i >= 19) {
        System.out.println("info: Character can not be write");
        for (int j = i; j < 19; j++) {
          builder.append((char) 0);
        }
        break;
      }
      i += String.valueOf(data.charAt(0)).getBytes(StandardCharsets.UTF_8).length;
      builder.append(data.charAt(0));
      data.deleteCharAt(0);
    }
    return builder.toString();
  }

  private static void staffingData() {
    System.out.println("info: data before staffing: " + data);

    boolean flag = false;
    for (int i = 0; i < data.length(); i++) {
      if (data.charAt(i) == '#') {
        flag = true;
      } else if (flag) {
        data.insert(i, "r$");  //  add "r$" between '#' and next character
        flag = false;
      }
    }

    System.out.println("info: data after staffing: " + data);
  }

  public static String unstaffing(byte[] data) {
    StringBuilder builder = new StringBuilder(new String(data, StandardCharsets.UTF_8));
    System.out.println("info: data before unstaffing: " + builder);

    boolean flag = false;
    for (int i = 0; i < builder.length(); i++) {
      if (builder.charAt(i) == '#' && builder.charAt(i + 1) == 'r' && builder.charAt(i + 2) == '$') {
        flag = true;
      } else if (flag) {
        builder.deleteCharAt(i);  //  rm 'r'
        builder.deleteCharAt(i);  //  rm '$'
        flag = false;
      }

      if (builder.charAt(i) == (char) 0)
        builder.deleteCharAt(i);  //  rm NULL
    }

    System.out.println("info: data after unstaffing: " + builder);
    return builder.toString();
  }

  public static int getDataByteSize() {
    return data.toString().getBytes(StandardCharsets.UTF_8).length;
  }
}
