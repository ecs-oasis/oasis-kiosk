package com.krsoftlab.carwashkiosk;

import android.app.Activity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.krsoftlab.carwashkiosk.device.WashMachineManager;
import com.krsoftlab.carwashkiosk.payment.Sk288PaymentUtil;
import com.krsoftlab.carwashkiosk.serial.SerialPortManager;

import org.json.JSONObject;

import java.lang.annotation.Annotation;

public class KrJavascriptInterface implements JavascriptInterface {
    Activity activity;
    WebView wv;
    WashMachineManager washMachineManager;

    public KrJavascriptInterface(MainActivity activity, WebView wv, WashMachineManager washMachineManager) {
        this.activity = activity;
        this.wv = wv;
        this.washMachineManager = washMachineManager;
    }

    @JavascriptInterface
    public String readMagnet() {
        JSONObject jo;
        try {
            jo = Sk288PaymentUtil.Companion.readMagnet();
        } catch (Exception e) {
            Log.e("magnet", e.getMessage());
            return null;
        }
        return jo.toString();
    }

    @JavascriptInterface
    public void startPremiumCourse() {
        byte cksum = washMachineManager.getRs232Checksum((byte)0x53, (byte)0x43, (byte)0x01);
        washMachineManager.sendData(new byte[]{0x24, 0x53, 0x43, 0x01, cksum});
    }

    @JavascriptInterface
    public void startStandardCourse() {
        byte cksum = washMachineManager.getRs232Checksum((byte)0x53, (byte)0x43, (byte)0x02);
        washMachineManager.sendData(new byte[]{0x24, 0x53, 0x43, 0x02, cksum});
    }

    @JavascriptInterface
    public String getHost() {
        if(BuildConfig.BUILD_TYPE.equals("debug")){
            return "http://13.124.85.244:3001";
        } else {
            return "https://api.oasiscar.link";
        }

    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}