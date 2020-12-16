package com.test.task.mvvm.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi


@SuppressLint("MissingPermission", "HardwareIds")
fun Context.getIMEI(): String {

    var imei = ""


    try {
        val telephonyManager =
            getSystemService(Context.TELEPHONY_SERVICE) as android.telephony.TelephonyManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            imei = telephonyManager.imei ?: ""
        } else {
            imei =  telephonyManager.deviceId ?: ""
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return imei
}

@SuppressLint("MissingPermission", "HardwareIds")
@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
fun Context.getIMSI(): String {

    var imsi = ""

    try {
        val telephonyManager =
            getSystemService(Context.TELEPHONY_SERVICE) as android.telephony.TelephonyManager

        imsi = telephonyManager.subscriberId
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return imsi
}

fun Activity.hideSoftKeyboard() {
    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = this.currentFocus
    if (view == null)
        view = View(this)
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}