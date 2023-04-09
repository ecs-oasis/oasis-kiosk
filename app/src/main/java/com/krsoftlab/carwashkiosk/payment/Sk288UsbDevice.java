package com.krsoftlab.carwashkiosk.payment;

import android.content.Context;
import android.util.Log;

import com.krsoftlab.carwashkiosk.CommonUtil;
import com.syncotek.sk288ku01driver.sk288ku01driver;

public class Sk288UsbDevice extends sk288ku01driver {
    public Sk288UsbDevice(Context mContext, String sAppName) {
        super(mContext, sAppName);
    }

    public int ExeCution(int[] InBufLen, byte[] InBuf, int[] OutBufLen, byte[] OutBuf) {
        Log.d("idpark", "TX : " + CommonUtil.INSTANCE.byteArrayToHex(InBuf));
        int ret = super.ExeCution(InBufLen, InBuf, OutBufLen, OutBuf);
        Log.d("idpark", "RX : " + CommonUtil.INSTANCE.byteArrayToHex(OutBuf));
        return ret;
    }

}