package com.krsoftlab.carwashkiosk

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.net.NetworkInterface
import java.util.*
import kotlin.experimental.and


object CommonUtil {

    fun getMACAddress(interfaceName: String?): String? {
        try {
            val interfaces: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                if (interfaceName != null) {
                    if (!intf.name.equals(interfaceName, ignoreCase = true)) continue
                }
                val mac = intf.hardwareAddress ?: return ""
                val buf = java.lang.StringBuilder()
                for (idx in mac.indices) buf.append(String.format("%02X:", mac[idx]))
                if (buf.isNotEmpty()) buf.deleteCharAt(buf.length - 1)
                return buf.toString()
            }
        } catch (ex: java.lang.Exception) {
        } // for now eat exceptions
        return ""
    }

    fun rotateImage(file: File, degree: Float) : File?{
        val sourceBitmap : Bitmap = BitmapFactory.decodeFile(file.absolutePath);
        val m = Matrix()
        m.setRotate(degree, sourceBitmap.width.toFloat(), sourceBitmap.height.toFloat())
        val rotatedBitmap = Bitmap.createBitmap(
            sourceBitmap,
            0,
            0,
            sourceBitmap.width,
            sourceBitmap.height,
            m,
            true
        )

        val dest = File(file.absolutePath)

        return try {
            val out = FileOutputStream(dest)
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            dest
        } catch (e: Exception) {
            Log.d("CommonUtil.rotateImage", "ERROR : " + e.message)
            null
        }
    }

    fun byteArrayToHex(a: ByteArray?): String? {
        if(a == null){
            return ""
        }
        val sb = StringBuilder()
        for (b in a) sb.append(String.format("%02x ", b))
        return sb.toString()
    }

    fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4)
                    + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

}