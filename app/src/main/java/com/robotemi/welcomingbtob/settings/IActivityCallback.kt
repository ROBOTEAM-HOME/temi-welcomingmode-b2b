package com.robotemi.welcomingbtob.settings

import android.view.View
import androidx.fragment.app.Fragment
import com.robotemi.welcomingbtob.widgets.CustomToggle

interface IActivityCallback {
    fun setTitle(title: String)

    fun setVisibilityOfDone(isVisible: Boolean)

    fun setEnableOfDone(enable: Boolean)

    fun setDoneClickListener(listener: View.OnClickListener)

    fun setBackClickListener(listener: View.OnClickListener)

    fun setVisibilityOfCustomToggle(isVisible: Boolean)

    fun setCustomToggleStatus(isOn: Boolean)

    fun setCustomToggleListener(listener: CustomToggle.ToggleListener)

    fun startFragment(fragment: Fragment)

    fun getSettings(): SettingsModel

    fun saveSettings(settingsModel: SettingsModel, callback: (settings: SettingsModel) -> Unit)
}