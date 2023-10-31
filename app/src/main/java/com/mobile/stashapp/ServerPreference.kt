package com.mobile.stashapp

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.lang.Exception
import javax.inject.Inject

class ServerPreference @Inject constructor(
    private val context: Context
) {

    companion object {
        private const val FILE_NAME = "ServerInfo"
        private const val HOST = "hostname"
        private const val PORT = "port"
        private const val APIKEY = "apiKey"
    }

    private val mainKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val sharedPreferences by lazy {
        try {
            EncryptedSharedPreferences.create(
                "$FILE_NAME-enc",
                mainKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        }
    }


    var host: String?
        set(value) {
            with(sharedPreferences.edit()) {
                putString(HOST, value)
                apply()
            }
        }
        get() {
            return sharedPreferences.getString(HOST, null)
        }

    var port: String?
        set(value) {
            with(sharedPreferences.edit()) {
                putString(PORT, value)
                apply()
            }
        }
        get() {
            return sharedPreferences.getString(PORT, null)
        }

    var apiKey: String?
        set(value) {
            with(sharedPreferences.edit()) {
                putString(APIKEY, value)
                apply()
            }
        }
        get() {
            return sharedPreferences.getString(APIKEY, null)
        }


    fun getBaseUrl(): String? {
        if (host == null || port == null) {
            return null
        }
        return "$host:$port"
    }


}