package com.robotemi.welcomingbtob.settings

import android.content.Context
import com.google.gson.Gson
import com.robotemi.welcomingbtob.R
import com.robotemi.welcomingbtob.utils.Constants
import timber.log.Timber

data class SettingsModel(
    var isUsingGreeterUser: Boolean = true,
    var isUsingDefaultMessage: Boolean = true,
    var defaultMessage: String = "",
    var customMessage: String = "",
    var isUsingVoiceGreeter: Boolean = true,
    var isUsingLocationAnnouncements: Boolean = true
) {

    companion object {

        private val gson = Gson()

        fun getSettings(context: Context): SettingsModel {
            val sharedPreferences =
                context.getSharedPreferences(
                    context.getString(R.string.app_name),
                    Context.MODE_PRIVATE
                )
            val settings = sharedPreferences?.getString(
                Constants.PREF_KEY_SETTINGS,
                gson.toJson(
                    SettingsModel(
                        isUsingGreeterUser = true,
                        isUsingDefaultMessage = true,
                        defaultMessage = context.getString(R.string.greeting),
                        customMessage = "",
                        isUsingVoiceGreeter = true,
                        isUsingLocationAnnouncements = true
                    )
                )
            )
            Timber.d("Settings-get, $settings")
            return gson.fromJson<SettingsModel>(settings, SettingsModel::class.java)
        }

        fun saveSettings(
            context: Context,
            settingsModel: SettingsModel,
            callback: ISaveSettingsCallback?
        ) {
            if (settingsModel.customMessage.isEmpty()) {
                settingsModel.isUsingDefaultMessage = true
            }
            val sharedPreferences =
                context.getSharedPreferences(
                    context.getString(R.string.app_name),
                    Context.MODE_PRIVATE
                )
            val editor = sharedPreferences?.edit()
            editor?.putString(Constants.PREF_KEY_SETTINGS, gson.toJson(settingsModel))?.apply()
            Timber.d("Settings-save, $settingsModel")
            callback?.onComplete()
        }

        interface ISaveSettingsCallback {
            fun onComplete()
        }
    }
}