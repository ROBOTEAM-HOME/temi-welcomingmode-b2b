package com.robotemi.welcomingbtob.settings

import android.content.Context
import com.google.gson.Gson
import com.robotemi.welcomingbtob.R
import com.robotemi.welcomingbtob.utils.Constants
import timber.log.Timber

data class SettingsModel(
    var isUsingGreeterUser: Boolean = true,
    var isUsingDefaultMessage: Boolean = true,
    var customMessage: String = "",
    var isUsingDisplayMessage: Boolean = true,
    var displayMessage: String = "",
    var isUsingVoiceGreeter: Boolean = true,
    var voiceGreetingMessage: String = "",
    var isUsingLocationAnnouncements: Boolean = true,
    var greeterMessageForVideoCall: String = "",
    var isUsingCallPageInterface: Boolean = true,
    var isUsingAutoCall: Boolean = true
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
                Constants.PREF_KEY_SETTINGS, gson.toJson(SettingsModel())
            )

            Timber.d("Settings-get, $settings")
            return gson.fromJson<SettingsModel>(settings, SettingsModel::class.java)
                .apply {
                    // If robot installed and ran this skill without new configuration data before,
                    // We should set the default data for it here.
                    if (displayMessage.isBlank()) {
                        displayMessage = context.getString(R.string.greeting)
                    }
                    if (voiceGreetingMessage.isBlank()) {
                        voiceGreetingMessage = context.getString(R.string.greeting)
                    }
                    if (greeterMessageForVideoCall.isBlank()) {
                        greeterMessageForVideoCall =
                            context.getString(R.string.greeter_message_for_video_call)
                    }
                }
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