package com.qurba.android.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Context.ACTIVITY_SERVICE
import android.app.ActivityManager
import android.util.Log
import java.lang.Exception

class YourUpgradeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            val packageName = intent?.data;
            if (packageName.toString() == "package:" + context?.packageName) {
                (context!!.getSystemService(ACTIVITY_SERVICE) as ActivityManager)
                    .clearApplicationUserData()
            }
        }catch (e:Exception)
        {
            Log.e("TAG", e.localizedMessage)
        }
    }
}