package com.robotemi.welcomingbtob.settings

import android.os.Bundle
import android.view.View
import com.robotemi.welcomingbtob.BaseFragment
import com.robotemi.welcomingbtob.R
import com.robotemi.welcomingbtob.settings.CustomGreeterFragment.Companion.CUSTOMIZE_DISPLAY_GREETER
import com.robotemi.welcomingbtob.settings.CustomGreeterFragment.Companion.CUSTOMIZE_VOICE_GREETER
import com.robotemi.welcomingbtob.widgets.CustomToggle
import kotlinx.android.synthetic.main.fragment_greet_user_config.*

class GreetUserConfigFragment : BaseFragment() {

    private val activityCallback by lazy { context as IActivityCallback }

    override fun getLayoutResId() = R.layout.fragment_greet_user_config

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshUI(getSettings())
        initListener()
    }

    private fun refreshUI(settings: SettingsModel) {
        activityCallback.setTitle(getString(R.string.fragment_greet_user))
        activityCallback.setVisibilityOfDone(false)
        settings.apply {
            customToggleDisplayMessage.setToggle(isUsingDisplayMessage)
            textViewDisplayedMessageContent.text = displayMessage
            customToggleVoiceGreeter.setToggle(isUsingVoiceGreeter)
            textViewVoiceGreetingContent.text = voiceGreetingMessage
        }
    }

    private fun initListener() {
        activityCallback.setBackClickListener(View.OnClickListener { close() })
        customToggleDisplayMessage.setToggleListener(object : CustomToggle.ToggleListener {
            override fun onToggle(on: Boolean) {
                saveSettings(getSettings().apply { isUsingDisplayMessage = on })
            }
        })
        linearLayoutForSettingDisplayMessage.setOnClickListener {
            activityCallback.startFragment(
                CustomGreeterFragment.newInstance(CUSTOMIZE_DISPLAY_GREETER)
            )
        }
        customToggleVoiceGreeter.setToggleListener(object : CustomToggle.ToggleListener {
            override fun onToggle(on: Boolean) {
                saveSettings(getSettings().apply { isUsingVoiceGreeter = on })
            }
        })
        linearLayoutForSettingVoiceGreetingMessage.setOnClickListener {
            activityCallback.startFragment(
                CustomGreeterFragment.newInstance(CUSTOMIZE_VOICE_GREETER)
            )
        }
    }

    private fun close() {
        activity?.supportFragmentManager?.popBackStack()
    }

    private fun getSettings() = activityCallback.getSettings()

    private fun saveSettings(settings: SettingsModel) {
        activityCallback.saveSettings(settings) {
            refreshUI(settings)
        }
    }

    companion object {
        fun newInstance() = GreetUserConfigFragment()
    }

}