package com.krsoftlab.carwashkiosk.serial;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.krsoftlab.carwashkiosk.serial.SerialPort;
import com.krsoftlab.carwashkiosk.serial.SerialPortInterface;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialPortManager {
  private static final String TAG = "SerialPortManager";

  private int bufferSize = 7;
  private int baudRate = 9600;
  private String device = "/dev/ttyS1";
  SerialPortInterface serialPortInterface;

  SerialPort serialPort;
  InputStream inputStream;
  OutputStream outputStream;

  SerialThread serialThread;
  Activity activity;

  public SerialPortManager(Activity activity, int baudRate, String device, int bufferSize, SerialPortInterface serialPortInterface){
    this.activity = activity;
    this.bufferSize = bufferSize;
    this.baudRate = baudRate;
    this.device = device;
    this.serialPortInterface = serialPortInterface;
    openSerialPort(this.device);
  }

  void openSerialPort(String path) {
    try {
      serialPort = new SerialPort(activity, new File(path), baudRate, 0);
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (serialPort != null) {
      inputStream = serialPort.getInputStream();
      outputStream = serialPort.getOutputStream();
    }
    startRxThread();
  }

  void startRxThread() {
    if (inputStream == null) {
      logTxt("Can't open inputstream");
      return;
    }

    serialThread = new SerialThread();
    serialThread.start();
  }

  public void sendData(byte[] data) {
    if (outputStream == null) {
      logTxt("Can't open outputstream");
      return ;
    }

    try {
      outputStream.write(data);
      logTxt("-----------");
      logBuff(data, data.length);
      logTxt("[SEND]");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  class SerialThread extends Thread {
    @Override
    public void run() {
      while (true) {
        try {
          byte[] buffer = new byte[bufferSize];
          int size = inputStream.read(buffer);
          if (size > 0) {
            activity.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                logTxt("-----------");
                logBuff(buffer, size);
                logTxt("[RECEIVE]");
                serialPortInterface.onReceiveData(buffer, size);
              }
            });
          };
        } catch (IOException e) {
          logTxt(e.toString());
        }
      }
    }
  }

  public void logBuff(byte[] buffer, int size){
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < size; i++) {
      stringBuilder.append(String.format("%02x ", buffer[i]));
    }

    logTxt(stringBuilder.toString());
  }

  void logTxt(String text){
    Log.d(TAG, text);
//        if(StringUtils.isEmpty(text))
//            return;
//        Log.d(TAG, text);
//        TextView tvLog = findViewById(R.id.tvLog);
//        if(tvLog == null){
//            return;
//        }
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                String totalText =  text + "\n" + tvLog.getText().toString();
//                tvLog.setText(totalText);
//            }
//        });
  }
}
