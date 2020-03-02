package com.robotemi.welcomingbtob.settings

import android.os.Bundle
import android.view.View
import android.widget.TextView
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
            setVisibilityOfCustomToggle(false)
            setBackClickListener(View.OnClickListener { activity?.finish() })
        }
        refreshUI(SettingsModel.getSettings(activity!!))
        initListener()
    }

    private fun refreshUI(settingsModel: SettingsModel) {
        settingsModel.apply {
            tvStartingScreenVal.text = startingScreenSelected
            tvGreetUserToggle.setChecked(isUsingDisplayMessage || isUsingVoiceGreeter)
            customToggleLocationAnnouncements.setToggle(isUsingLocationAnnouncements)
            tvCallButtonToggle.setChecked(isUsingCallPageInterface)
        }
    }

    private fun initListener() {
        llForStartingScreenConfig.setOnClickListener {
            activityCallback.startFragment(StartingScreenConfigFragment.newInstance())
        }

        // Greet user configuration
        llForGreetUserConfig.setOnClickListener {
            activityCallback.startFragment(GreetUserConfigFragment.newInstance())
        }

        llForCallButtonConfig.setOnClickListener {
            activityCallback.startFragment(CallButtonFragment.newInstance())
        }

        customToggleLocationAnnouncements.setToggleListener(object : CustomToggle.ToggleListener {
            override fun onToggle(on: Boolean) {
                saveSettings(getSettings().apply { isUsingLocationAnnouncements = on })
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

    /**
     * For text view with toggle indicator
     */
    private fun TextView.setChecked(checked: Boolean) {
        text = if (checked) {
            getString(R.string.custom_toggle_on)
        } else {
            getString(R.string.custom_toggle_off)
        }
        isEnabled = checked
    }

    companion object {
        fun newInstance() = ConfigurationFragment()
    }
}