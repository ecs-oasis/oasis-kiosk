package com.krsoftlab.carwashkiosk.device;

import android.app.Activity;

import com.krsoftlab.carwashkiosk.serial.SerialPortInterface;
import com.krsoftlab.carwashkiosk.serial.SerialPortManager;

public class WashMachineManager {
  SerialPortManager serialPortManager; //rs232
  final int BAUD_RATE = 9600;
  final String DEVICE = "/dev/ttyS3";
  final int BUFFER_SIZE = 5;

  public WashMachineManager(Activity activity){
    serialPortManager = new SerialPortManager(activity, BAUD_RATE, DEVICE, BUFFER_SIZE, new SerialPortInterface() {
      @Override
      public void onReceiveData(byte[] buffer, int size) {

      }
    });
  }

  public void sendData(byte[] data){
    serialPortManager.sendData(data);
  }

  public byte getRs232Checksum(byte cmd0, byte cmd1, byte cmd2) {
    return (byte)(cmd0 + cmd1 + cmd2);
  }
}
