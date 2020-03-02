package com.robotemi.welcomingbtob.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.robotemi.sdk.Robot
import com.robotemi.welcomingbtob.R
import com.robotemi.welcomingbtob.widgets.CustomToggle
import kotlinx.android.synthetic.main.activity_settings.*
import org.koin.android.ext.android.inject

class SettingsActivity : AppCompatActivity(), IActivityCallback {

    companion object {
        internal const val RESULT_CODE_FOR_UPDATED_STARTING_SCREEN = 1
    }

    private val robot: Robot by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        startFragment(ConfigurationFragment.newInstance())
        imageButtonBack.setOnClickListener { close() }
    }

    override fun onResume() {
        super.onResume()
        robot.hideTopBar()
        robot.stopMovement()
    }

    override fun startFragment(fragment: Fragment) {
        if (fragment is ConfigurationFragment) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commitAllowingStateLoss()
        } else {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
    }

    override fun getSettings() = SettingsModel.getSettings(this)

    override fun saveSettings(
        settingsModel: SettingsModel,
        callback: (settings: SettingsModel) -> Unit
    ) {
        SettingsModel.saveSettings(
            this,
            settingsModel,
            object : SettingsModel.Companion.ISaveSettingsCallback {
                override fun onComplete() {
                    callback(settingsModel)
                }
            })
    }

    override fun setTitle(title: String) {
        textViewTitle.text = title
    }

    override fun setVisibilityOfDone(isVisible: Boolean) {
        textViewDone.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun setEnableOfDone(enable: Boolean) {
        textViewDone.isEnabled = enable
    }

    override fun setDoneClickListener(listener: View.OnClickListener) {
        textViewDone.setOnClickListener(listener)
    }

    override fun setBackClickListener(listener: View.OnClickListener) {
        imageButtonBack.setOnClickListener(listener)
    }

    override fun setVisibilityOfCustomToggle(isVisible: Boolean) {
        customToggleTop.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun setCustomToggleStatus(isOn: Boolean) {
        customToggleTop.setToggle(isOn)
    }

    override fun setCustomToggleListener(listener: CustomToggle.ToggleListener) {
        customToggleTop.setToggleListener(listener)
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        robot.stopMovement()
    }

    private fun close() {
        setResult(RESULT_CODE_FOR_UPDATED_STARTING_SCREEN)
        finish()
    }
}
