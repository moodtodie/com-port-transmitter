package com.github.moodtodie.term5_fcn.service;

import com.github.moodtodie.term5_fcn.serial.PortManager;
import jssc.SerialPortException;

import java.util.Random;

public class CsmaCd {
  private static boolean jamWasReceived = false;
  private static final byte jam = '*';
  private static final int MAX_ATTEMPTS = 16;

  //  Предусмотреть возможность эмуляции занятости канала.
  //  Вероятность занятости канала должна составлять 30 %.
  public static boolean isBusy() {
    return new Random().nextInt(10) < 3;  //  Range from 0 to 9;
  }

  //  Предусмотреть возможность эмуляции коллизии.
  //  Вероятность коллизии должна составлять 70 %.
  public static boolean isCollision() {
    return new Random().nextInt(10) < 7;  //  Range from 0 to 9;
  }

  //  Для расчета случайной задержки использовать стандартную формулу.
  public static void collisionDelay(int attemptNumber) {
    try {
      Thread.sleep(Math.min(attemptNumber, 10) * 100L);
    } catch (InterruptedException e) {
      System.out.println("Error: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public static byte getJam(){
    return jam;
  }

  public static boolean jamWasReceived() {
    return jamWasReceived;
  }

  public static void jamReceived(boolean wasReceived) {
    CsmaCd.jamWasReceived = wasReceived;
  }

  public static void sendPacket(byte[] data) throws SerialPortException {
    int attemptCounter = 0;

    while (true){
      while (CsmaCd.isBusy()){  //  Chance 30%
        System.out.println("info: channel is busy");
        try {
          Thread.sleep(150);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }

      PortManager.getPort().write(data);

      //  Окно колизий

      if (CsmaCd.isCollision()){ //  Chance 70%
        attemptCounter++;

        System.out.println("info: collision [" + attemptCounter + "/" + MAX_ATTEMPTS + "]");
        //  Send JAM
        PortManager.getPort().write(new byte[]{CsmaCd.getJam()});

        if (attemptCounter > MAX_ATTEMPTS){
          System.out.println("warn: Too many attempts");
          break;
        }

        CsmaCd.collisionDelay(attemptCounter);
      } else {
        PortManager.getPort().write(new byte[]{' '}); //  Debug
        break;
      }
    }
  }

/*
реализовать три ключевых шага алгоритма:
  прослушивание канала,
  обнаружение коллизии,
  розыгрыш случайной задержки (в соответствующей последовательности).
 */

  //  Коллизию рассматривать применительно к кадру целиком (не к байту)

  //  Из «довесков» к алгоритму, реализовать поддержку jam-сигнала
  //  (дополнительно и правильно; как на стороне передатчика, так и на стороне приемника).
}
