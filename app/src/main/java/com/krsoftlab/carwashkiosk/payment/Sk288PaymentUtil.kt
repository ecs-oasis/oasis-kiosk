package com.krsoftlab.carwashkiosk.payment

import android.content.Context
import android.util.Log
import com.krsoftlab.carwashkiosk.CommonUtil
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import kotlin.experimental.and

abstract class Sk288PaymentUtil {
    companion object {
        var context: Context? = null;
        var usbDevice: Sk288UsbDevice? = null;
        const val ACTION_USB_PERMISSION = "com.syncotek.sk288ku01driver_test.sk288ku01driver_test"
        const val EXECUTE_READ:String = "EXECUTE_READ"
        const val EXECUTE_WRITE:String = "EXECUTE_WRITE"
        const val TAG = "Sk288PaymentUtil"

        fun init(context: Context) {
            this.context = context;
            usbDevice = Sk288UsbDevice(context, ACTION_USB_PERMISSION);
        }

        fun Byte.toHexString():String { return "%02x".format(this)}

        fun executeRf(exeCmd: String, store:String, money:String) :JSONArray?{
            val joStore: JSONObject = JSONObject(store)
            val joMoney: JSONObject = JSONObject(money)
            val sectorNumberStore: Int = joStore.getInt("sectorNumber")
            val sectorKeyStore: String = joStore.getString("sectorKey")
            val blockNumberStore: Int = joStore.getInt("blockNumber")
            val sectorNumberMoney: Int = joMoney.getInt("sectorNumber")
            val sectorKeyMoney: String = joMoney.getString("sectorKey")
            val blockNumberMoney: Int = joMoney.getInt("blockNumber")
            activeRf()
            rfCheckPasswordTwice(sectorKeyStore, sectorNumberStore)

            val readStoreResData:ByteArray = rfReadBlock(sectorNumberStore, blockNumberStore)

            val lCardStoreInfo :MutableList<String> = mutableListOf()
            val joStoreIndices : JSONArray = joStore.getJSONArray("indices")
            for(i in 0 until joStoreIndices.length()){
                lCardStoreInfo.add(readStoreResData[joStoreIndices.getInt(i)].toHexString())
            }
            val cardStoreInfo = lCardStoreInfo.joinToString("").toUpperCase()
            val kioskStoreInfo = joStore.getString("values").toUpperCase()
            if(cardStoreInfo != kioskStoreInfo){
                throw Exception("wrong store info kiosk=[$kioskStoreInfo] card=[$cardStoreInfo]")
            }

            rfCheckPasswordTwice(sectorKeyMoney, sectorNumberMoney)

            when(exeCmd){
                EXECUTE_READ -> {
                    val readMoneyResData = rfReadBlock(sectorNumberMoney, blockNumberMoney)
                    val lCardMoneyInfo :MutableList<String> = mutableListOf()
                    val jMoneyIndices :JSONArray = joMoney.getJSONArray("indices")
                    for(i in 0 until jMoneyIndices.length()){
                        lCardMoneyInfo.add("\""+readMoneyResData[jMoneyIndices.getInt(i)].toHexString()+"\"")
                    }
                    val jMoney = JSONArray("["+lCardMoneyInfo.joinToString(",")+"]")
                    return jMoney
                }
                EXECUTE_WRITE -> {
                    val readMoneyResData = rfReadBlock(sectorNumberMoney, blockNumberMoney)
                    val jMoneyIndices :JSONArray = joMoney.getJSONArray("indices")
                    val sMoneyValues :String = joMoney.getString("values")
                    val aMoneyValues = CommonUtil.hexStringToByteArray(sMoneyValues)
                    if(aMoneyValues.size != jMoneyIndices.length()){
                        throw Exception("indices and values not matched")
                    }
                    for(i in 0 until jMoneyIndices.length()){
                        readMoneyResData[jMoneyIndices.getInt(i)] = aMoneyValues[i]
                    }
                    rfWriteBlock(sectorNumberMoney, blockNumberMoney, readMoneyResData)
                    return null
                }
                else -> {
                    return null
                }
            }
        }

        fun rfReadBlock(sectorNumber:Int, blockNumber:Int) :ByteArray {
            var rc :Int? = -1
            val OutBuf = ByteArray(300)
            val OutBufLen = IntArray(2)
            val InBuf: ByteArray = ByteArray(8)
            val InBufLen: IntArray = IntArray(2)
            InBufLen[0] = InBuf.size
            InBufLen[1] = 0

            OutBufLen[0] = 0x00
            OutBufLen[1] = 0x00
            InBuf[0] = 0x43
            InBuf[1] = 0x60
            InBuf[2] = 0x33
            InBuf[3] = 0x00
            InBuf[4] = 0xB0.toByte()
            InBuf[5] = sectorNumber.toByte()
            InBuf[6] = blockNumber.toByte()
            InBuf[7] = 0x01

            rc = usbDevice?.ExeCution(InBufLen, InBuf, OutBufLen, OutBuf)
            if (rc == 0) {
                if(OutBufLen[0] == 23 &&  OutBuf[3] == 0x31.toByte() && OutBuf[4] == 0x32.toByte() && OutBuf[21] == 0x90.toByte() && OutBuf[22] == 0x00.toByte()){
                    return OutBuf.copyOfRange(5, 21)
                } else {
                    throw Exception("read Error")
                }
            } else {
                throw Exception("read communication error")
            }
        }

        fun rfWriteBlock(sectorNumber:Int, blockNumber:Int, data:ByteArray) {
            var rc :Int? = -1
            val OutBuf = ByteArray(300)
            val OutBufLen = IntArray(2)
            val InBuf: ByteArray = ByteArray(24)
            val InBufLen: IntArray = IntArray(2)
            InBufLen[0] = InBuf.size
            InBufLen[1] = 0

            OutBufLen[0] = 0x00
            OutBufLen[1] = 0x00
            InBuf[0] = 0x43
            InBuf[1] = 0x60
            InBuf[2] = 0x33
            InBuf[3] = 0x00
            InBuf[4] = 0xD1.toByte()
            InBuf[5] = sectorNumber.toByte()
            InBuf[6] = blockNumber.toByte()
            InBuf[7] = 0x01
            InBuf[8] = data[0]
            InBuf[9] = data[1]
            InBuf[10] = data[2]
            InBuf[11] = data[3]
            InBuf[12] = data[4]
            InBuf[13] = data[5]
            InBuf[14] = data[6]
            InBuf[15] = data[7]
            InBuf[16] = data[8]
            InBuf[17] = data[9]
            InBuf[18] = data[10]
            InBuf[19] = data[11]
            InBuf[20] = data[12]
            InBuf[21] = data[13]
            InBuf[22] = data[14]
            InBuf[23] = data[15]

            rc = usbDevice?.ExeCution(InBufLen, InBuf, OutBufLen, OutBuf)
            if (rc == 0) {
                if(OutBuf[3] == 0x31.toByte() && OutBuf[4] == 0x32.toByte() && OutBuf[5] == 0x90.toByte() && OutBuf[6] == 0x00.toByte()){
                    Log.d(TAG, "write sector/block=["+ sectorNumber+"/"+blockNumber+"] data=["+CommonUtil.byteArrayToHex(data)+"]")
                } else {
                    throw Exception("write Error")
                }
            } else {
                throw Exception("write communication error")
            }
        }

        fun rfCheckPasswordTwice(sectorKey:String, sectorNumber:Int) {
            try {
                rfCheckPassword(sectorKey, sectorNumber, 0x00)
            } catch (e:Exception) {
                rfCheckPassword(sectorKey, sectorNumber, 0x01)
            }
        }

        fun rfCheckPassword(sectorKey:String, sectorNumber:Int, keyAB: Byte) {
            var rc :Int? = -1
            val OutBuf = ByteArray(300)
            val OutBufLen = IntArray(2)
            var InBuf: ByteArray = ByteArray(14)
            var InBufLen: IntArray = IntArray(2)
            InBufLen[0] = InBuf.size
            InBufLen[1] = 0

            val Lenth = 6
            OutBufLen[0] = 0x00
            OutBufLen[1] = 0x00
            InBufLen = IntArray(2)
            InBufLen[0] = Lenth + 8
            InBufLen[1] = 0
            InBuf = ByteArray(Lenth + 8)
            InBuf[0] = 0x43
            InBuf[1] = 0x60
            InBuf[2] = 0x33
            InBuf[3] = 0x00
            InBuf[4] = 0x20
            InBuf[5] = keyAB //(Key A=00H，Key B=01H)
            InBuf[6] = sectorNumber.toByte()
            InBuf[7] = Lenth.toByte()

            for (i:Int in 1..Lenth) {
                InBuf[7 + i] = (sectorKey.substring((i-1) * 2, (i-1) * 2 + 2).toInt(16) and 0xff).toByte()
            }
            rc = usbDevice?.ExeCution(InBufLen, InBuf, OutBufLen, OutBuf)
            if (rc == 0) {
                if(OutBuf[5] != 0x90.toByte()){
                    throw Exception("password error")
                }
            } else {
                throw Exception("communication error")
            }
        }

        fun activeRf() {
            var rc:Int? = -1
            val OutBuf = ByteArray(300)
            val OutBufLen = IntArray(2)
            var InBuf = ByteArray(5)
            val InBufLen = IntArray(2)
            OutBufLen[0] = 0x00
            OutBufLen[1] = 0x00
            InBufLen[0] = 5
            InBufLen[1] = 0
            InBuf[0] = 0x43
            InBuf[1] = 0x60
            InBuf[2] = 0x30
            InBuf[3] = 0x41 //TYPE A
            InBuf[4] = 0x42 //TYPE B
            rc = usbDevice?.ExeCution(InBufLen, InBuf, OutBufLen, OutBuf)
            if (rc == 0) {
                if (OutBuf[0] == 0x50.toByte()) {
                    when (OutBuf[5]) {
                        0x41.toByte() -> {
                            throw Exception("not compatible card type (Type A)")
                        }
                        0x42.toByte() -> {
                            throw Exception("not compatible card type (Type B)")
                        }
                        0x4D.toByte() -> {
                            //success
                        }
                        else -> {
                            throw Exception("not compatible card type")
                        }
                    }
                } else {
                    throw Exception("Failed (" + OutBuf.get(3) as Char + OutBuf.get(4) as Char + ")")
                }
            } else {
                throw Exception("communication error")
            }
        }

        fun readMagnet() :JSONObject{
            var rc:Int? = -1
            val OutBuf = ByteArray(300)
            val OutBufLen = IntArray(2)
            var InBuf: ByteArray? = null
            val InBufLen = IntArray(2)

            var Tra1Buf: String
            val Tra2Buf: String
            val Tra3Buf: String
            Tra1Buf = ""
            Tra2Buf = ""
            Tra3Buf = ""

            val HexStr = ""
            //Read track 1
            //Read track 1
            OutBufLen[0] = 0x00
            OutBufLen[1] = 0x00

            InBufLen[0] = 5
            InBufLen[1] = 0

            InBuf = ByteArray(5)
            InBuf[0] = 0x43
            InBuf[1] = 0x36
            InBuf[2] = 0x31
            InBuf[3] = 0x30
            InBuf[4] = 0x31
            rc = usbDevice?.ExeCution(InBufLen, InBuf, OutBufLen, OutBuf)
            if (rc == 0) {
                if (OutBuf[0] == 0x50.toByte()) {
                    when (OutBuf[5]) {
                        0x50.toByte() -> {
                            var M = 6
                            while (M < OutBufLen[0]) {
                                Tra1Buf = Tra1Buf + OutBuf[M].toChar()
                                M++
                            }
                            //EditReturnATR.setText(Tra1Buf)
                        }
                        0x4E.toByte() -> {
                            when (OutBuf[7] and 0xFF.toByte()) {
                                0x30.toByte() -> throw Exception("Bytes Parity Error(Parity)")
                                0x33.toByte() -> throw Exception("Only SS+ES+LRC")
                                0x34.toByte() -> throw Exception("Card Track Data is Blank")
                                0x36.toByte() -> throw Exception("No start bits(SS)")
                                0x27.toByte() -> throw Exception("No stop bits(ES)")
                                0x28.toByte() -> throw Exception("Parity Bit Error(LRC)")
                            }
                        }
                    }
                } else {
                    throw Exception("Failed (" + OutBuf[3].toChar() + OutBuf[4].toChar() + ")")
                }
            } else {
                throw Exception("communication error")
            }
            val cardNum = Tra1Buf.substring(1).split("^")[0]
            val yymm = Tra1Buf.split("^")[2].substring(0,4)
            val data = JSONObject()
            data.put("cardNo", cardNum)
            data.put("vldDtYear", yymm.substring(0,2))
            data.put("vldDtMon", yymm.substring(2,4))
            clearBuf()
            return data
        }

        fun clearBuf() {
            var rc:Int? = -1
            val OutBuf = ByteArray(300)
            val OutBufLen = IntArray(2)
            var InBuf: ByteArray? = null
            val InBufLen = IntArray(2)

            OutBufLen[0] = 0x00
            OutBufLen[1] = 0x00

            InBufLen[0] = 5
            InBufLen[1] = 0

            InBuf = ByteArray(5)
            InBuf[0] = 0x43
            InBuf[1] = 0x36
            InBuf[2] = 0x39
            InBuf[3] = 0x0
            InBuf[4] = 0x0
            rc = usbDevice?.ExeCution(InBufLen, InBuf, OutBufLen, OutBuf)
            if (rc == 0) {
                if(OutBuf[0] != 0x50.toByte() || OutBuf[1] != 0x36.toByte() || OutBuf[2] != 0x39.toByte() || OutBuf[3] != 0x31.toByte() || OutBuf[4] != 0x30.toByte()) {
                    throw Exception("clear magnet communication error")
                }
            } else {
                throw Exception("clear magnet communication error")
            }
        }
    }
}