package com.krsoftlab.carwashkiosk.serial;

public interface SerialPortInterface {
  void onReceiveData(byte[] buffer, int size);
}
