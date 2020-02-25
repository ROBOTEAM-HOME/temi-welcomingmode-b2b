package com.robotemi.welcomingbtob.settings

import android.os.Bundle
import android.view.View
import com.robotemi.sdk.Robot
import com.robotemi.welcomingbtob.BaseFragment
import com.robotemi.welcomingbtob.R
import com.robotemi.welcomingbtob.widgets.CustomToggle
import kotlinx.android.synthetic.main.fragment_configuration.*
import org.koin.android.ext.android.inject

class ConfigurationFragment : BaseFragment() {

    private val robot: Robot by inject()

    private val activityCallback by lazy { context as IActivityCallback }

    override fun getLayoutResId() = R.layout.fragment_configuration

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityCallback.apply {
            setTitle(getString(R.string.fragment_config))
            setVisibilityOfDone(false)
            setBackClickListener(View.OnClickListener { activity?.finish() })
        }
        refreshUI(SettingsModel.getSettings(activity!!))
        initListener()
    }

    private fun refreshUI(settingsModel: SettingsModel) {
        settingsModel.apply {
            if (isUsingDisplayMessage || isUsingVoiceGreeter) {
                // User greeter
                textViewGreetUserToggle.text = getString(R.string.custom_toggle_on)
                textViewGreetUserToggle.isEnabled = true
            } else {
                // No greeter
                textViewGreetUserToggle.text = getString(R.string.custom_toggle_off)
                textViewGreetUserToggle.isEnabled = false
            }
            customToggleLocationAnnouncements.setToggle(isUsingLocationAnnouncements)
            customToggleCallPage.setToggle(isUsingCallPageInterface)
            customToggleAutoCall.setToggle(isUsingCallPageInterface && isUsingAutoCall)
        }
    }

    private fun initListener() {
        // Greet user configuration
        linearLayoutForGreetUserConfig.setOnClickListener {
            activityCallback.startFragment(GreetUserConfigFragment.newInstance())
        }

        customToggleLocationAnnouncements.setToggleListener(object : CustomToggle.ToggleListener {
            override fun onToggle(on: Boolean) {
                saveSettings(getSettings().apply { isUsingLocationAnnouncements = on })
            }
        })

        customToggleCallPage.setToggleListener(object : CustomToggle.ToggleListener {
            override fun onToggle(on: Boolean) {
                saveSettings(getSettings().apply {
                    isUsingCallPageInterface = on
                    if (!on) {
                        isUsingAutoCall = false
                    }
                })
            }
        })

        customToggleAutoCall.setToggleListener(object : CustomToggle.ToggleListener {
            override fun onToggle(on: Boolean) {
                saveSettings(getSettings().apply { isUsingAutoCall = on })
            }
        })

        textViewOpenAppList.setOnClickListener {
            robot.showAppList()
            robot.showTopBar()
        }
    }

    private fun getSettings() = activityCallback.getSettings()

    private fun saveSettings(settings: SettingsModel) {
        activityCallback.saveSettings(settings) {
            refreshUI(settings)
        }
    }

    companion object {
        fun newInstance() = ConfigurationFragment()
    }
}