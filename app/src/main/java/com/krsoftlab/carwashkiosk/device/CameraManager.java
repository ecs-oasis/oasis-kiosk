package com.krsoftlab.carwashkiosk.device;

import android.app.Activity;
import android.util.Log;
import android.webkit.WebView;

import com.krsoftlab.carwashkiosk.CommonUtil;
import com.krsoftlab.carwashkiosk.serial.SerialPortInterface;
import com.krsoftlab.carwashkiosk.serial.SerialPortManager;

public class CameraManager {
  WebView webView;
  SerialPortManager serialPortManager; //rs485
  final int BAUD_RATE = 115200;
  final String DEVICE = "/dev/ttyS1";
  final int BUFFER_SIZE = 50;
  byte[] historyBuffer = new byte[200];
  int pointer = 0;

  public CameraManager(Activity activity, WebView webView){
    this.webView = webView;
    serialPortManager = new SerialPortManager(activity, BAUD_RATE, DEVICE, BUFFER_SIZE, new SerialPortInterface() {
      @Override
      public void onReceiveData(byte[] buffer, int size) {
        pushHistoryBuffer(buffer, size);
        trimStartByte();
        byte[] message = getCompleteMessage();
        if (message != null) {
          String carNum = getCarNum(message);
          if(carNum != null && !carNum.equals("")) {
            sendCarNumToWebView(carNum);
          }
        }
      }
    });
  }

  public void sendData(byte[] data){
    serialPortManager.sendData(data);
  }

  private void pushHistoryBuffer(byte[] buffer, int size){
    if((pointer + size) > historyBuffer.length){
      historyBuffer = new byte[200]; pointer = 0;
    }
    System.arraycopy (buffer, 0, historyBuffer, pointer, size);
    pointer = pointer + size;
  }

  private String getCarNum(byte[] message) {
    byte[] plateNumber = new byte[16];
    System.arraycopy (message, 12, plateNumber, 0, 16);
    return replaceHangul(new String(plateNumber)).trim();
  }
  
  private void sendCarNumToWebView(String carNum) {
    webView.evaluateJavascript("onCarNumDetected('"+carNum+"');", null);
  }

  private byte[] getCompleteMessage() {
    if(pointer < 12){
      return null;
    }

    int dataLength = getDataByte();
    int messageLength = (8 + dataLength + 4);
    if(pointer >= messageLength){
      byte[] message = new byte[messageLength];
      System.arraycopy(historyBuffer, 0, message, 0, messageLength);

      byte[] newHistoryBuffer = new byte[200];
      if(pointer >= messageLength) {
        System.arraycopy(historyBuffer, messageLength, newHistoryBuffer, 0, pointer - messageLength);
      }
      pointer = pointer - messageLength;
      historyBuffer = newHistoryBuffer;
      return message;
    }
    return null;
  }

  private int getDataByte(){
    byte highByte = historyBuffer[6];
    byte lowByte = historyBuffer[7];

    return (short) ((highByte << 8) | lowByte);
  }

  private void trimStartByte() {
    int startPos = getStartBytePos();
    if(startPos == -1){
      historyBuffer = new byte[200]; pointer = 0;
    } else if(startPos != 0){
      byte[] newHistoryBuffer = new byte[200];
      System.arraycopy (historyBuffer, startPos, newHistoryBuffer, 0, pointer-startPos);
      pointer = pointer-startPos;
      historyBuffer = newHistoryBuffer;
    }
  }

  private int getStartBytePos() {
    byte[] startBytes = new byte[]{(byte)0xCC, (byte)0x99, 0x01, 0x00, 0x00, 0x00};
    for (int i=0; i<historyBuffer.length; i++) {
      if((historyBuffer.length - i) > startBytes.length){
        if(
                startBytes[0] == historyBuffer[i]
                && startBytes[1] == historyBuffer[i+1]
                && startBytes[2] == historyBuffer[i+2]
                && startBytes[3] == historyBuffer[i+3]
                && startBytes[4] == historyBuffer[i+4]
                && startBytes[5] == historyBuffer[i+5]
        ){
          return i;
        }
      }
    }
    return -1;
  }

  private String replaceHangul(String str) {
    StringBuilder sb = new StringBuilder();
    for(int i=0; i<str.length(); i++){
      char c = str.charAt(i);
      switch (c) {
        case 'A':
          sb.append('부');
          break;
        case 'B':
          sb.append('가');
          break;
        case 'C':
          sb.append('거');
          break;
        case 'D':
          sb.append('고');
          break;
        case 'E':
          sb.append('구');
          break;
        case 'F':
          sb.append('나');
          break;
        case 'G':
          sb.append('너');
          break;
        case 'H':
          sb.append('노');
          break;
        case 'I':
          sb.append('누');
          break;
        case 'J':
          sb.append('다');
          break;
        case 'K':
          sb.append('더');
          break;
        case 'L':
          sb.append('도');
          break;
        case 'M':
          sb.append('두');
          break;
        case 'N':
          sb.append('라');
          break;
        case 'O':
          sb.append('러');
          break;
        case 'P':
          sb.append('로');
          break;
        case 'Q':
          sb.append('루');
          break;
        case 'R':
          sb.append('마');
          break;
        case 'S':
          sb.append('머');
          break;
        case 'T':
          sb.append('모');
          break;
        case 'U':
          sb.append('무');
          break;
        case 'V':
          sb.append('버');
          break;
        case 'W':
          sb.append('보');
          break;
        case 'X':
          sb.append('서');
          break;
        case 'Y':
          sb.append('소');
          break;
        case 'Z':
          sb.append('수');
          break;
        case '!':
          sb.append('어');
          break;
        case '#':
          sb.append('주');
          break;
        case '$':
          sb.append('우');
          break;
        case '%':
          sb.append('오');
          break;
        case 'k':
          sb.append('허');
          break;
        case '(':
          sb.append('조');
          break;
        case ')':
          sb.append('자');
          break;
        case 'm':
          sb.append('바');
          break;
        case ';':
          sb.append('이');
          break;
        case '@':
          sb.append('하');
          break;
        case '[':
          sb.append('저');
          break;
        case ']':
          sb.append('사');
          break;
        case '^':
          sb.append('육');
          break;
        case '{':
          sb.append('호');
          break;
        case '}':
          sb.append('하');
          break;
        case '~':
          sb.append('아');
          break;
        case '+':
          sb.append('배');
          break;
        case '-':
          sb.append('경');
          break;
        case '=':
          sb.append('울');
          break;
        case 'a':
          sb.append('기');
          break;
        case 'b':
          sb.append('인');
          break;
        case 'c':
          sb.append('천');
          break;
        case 'd':
          sb.append('충');
          break;
        case 'e':
          sb.append('남');
          break;
        case 'f':
          sb.append('전');
          break;
        case 'g':
          sb.append('북');
          break;
        case 'h':
          sb.append('광');
          break;
        case 'i':
          sb.append('강');
          break;
        case 'j':
          sb.append('원');
          break;
        default:sb.append(c);
      }
    }
    return sb.toString();
  }
}
