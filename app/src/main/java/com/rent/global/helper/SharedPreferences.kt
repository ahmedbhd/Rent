package com.rent.global.helper

import android.content.Context
import android.content.SharedPreferences
import com.rent.global.utils.*
import com.securepreferences.SecurePreferences
import com.squareup.moshi.Moshi
import org.jetbrains.annotations.TestOnly


class SharedPreferences(val moshi: Moshi) {

    private lateinit var sharedPreferences: SharedPreferences

    constructor(context: Context, moshi: Moshi) : this(moshi) {
        sharedPreferences = SecurePreferences(context, getPassKeyStore(context), FILE_NAME_FLAG)
    }

    @TestOnly
    constructor(sharedPreferences: SharedPreferences, moshi: Moshi) : this(moshi) {
        this.sharedPreferences = sharedPreferences
    }

    private fun getPassKeyStore(context: Context): String {
        val alias = context.applicationContext.packageName

        var pass: String? = null
        try {
            KeyStoreHelper.createKeys(alias)
            pass = KeyStoreHelper.getSigningKey(alias)
        } catch (e: Exception) {
            DebugLog.e(TAG, "getPassKeyStore error : ${e.message}")
        }
        if (pass == null) {
            pass = context.getDeviceSerialNumber()
            pass = pass.bitShiftEntireString()
        }
        return pass
    }

}


private const val FILE_NAME_FLAG = "rent_file_flag"
private const val TOKEN_FLAG = "1"
private const val USER_FLAG = "2"
private const val CHOICE_FLAG = "3"
