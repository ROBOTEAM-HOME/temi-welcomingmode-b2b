package com.robotemi.welcomingbtob.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.robotemi.sdk.Robot
import com.robotemi.welcomingbtob.R
import com.robotemi.welcomingbtob.widgets.CustomToggle
import kotlinx.android.synthetic.main.fragment_configuration.*
import org.koin.android.ext.android.inject

class ConfigurationFragment : Fragment() {

    private val robot: Robot by inject()

    private val activityCallback by lazy { context as IActivityCallback }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_configuration, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityCallback.apply {
            setTitle(getString(R.string.fragment_config))
            setVisibilityOfDone(false)
            setBackClickListener(View.OnClickListener { activity?.finish() })
        }
        updateUi(SettingsModel.getSettings(activity!!))
        customToggleGreetUser.setToggleListener(object : CustomToggle.ToggleListener {
            override fun onToggle(on: Boolean) {
                val settings = getSettings()
                settings.isUsingGreeterUser = on
                if (!on) {
                    settings.isUsingVoiceGreeter = false
                    settings.isUsingLocationAnnouncements = false
                }
                saveSettings(settings)
            }
        })
        customToggleVoiceGreeter.setToggleListener(object : CustomToggle.ToggleListener {
            override fun onToggle(on: Boolean) {
                saveSettings(getSettings().apply { isUsingVoiceGreeter = on })
            }
        })
        customToggleLocationAnnouncements.setToggleListener(object : CustomToggle.ToggleListener {
            override fun onToggle(on: Boolean) {
                saveSettings(getSettings().apply { isUsingLocationAnnouncements = on })
            }
        })
        relativeLayoutDefaultMessage.setOnClickListener {
            saveSettings(getSettings().apply { isUsingDefaultMessage = true })
        }
        relativeLayoutCustom.setOnClickListener {
            saveSettings(getSettings().apply {
                if (customMessage.isNotEmpty()) {
                    isUsingDefaultMessage = false
                }
            })
            activityCallback.startFragment(CustomGreeterFragment.newInstance())
        }
        textViewOpenAppList.setOnClickListener {
            robot.showAppList()
            robot.showTopBar()
        }
    }

    private fun updateUi(settingsModel: SettingsModel) {
        settingsModel.let {
            customToggleGreetUser.setToggle(it.isUsingGreeterUser)
            textViewDefaultMessage.isEnabled = it.isUsingGreeterUser
            textViewDefaultMessageDescription.text = descriptionFormat(it.defaultMessage)
            textViewDefaultMessageDescription.isEnabled = it.isUsingGreeterUser
            setRadioButtonSrc(
                radioButtonDefaultMessage,
                it.isUsingDefaultMessage,
                it.isUsingGreeterUser
            )
            textViewCustom.isEnabled = it.isUsingGreeterUser
            textViewCustomDescription.isEnabled = it.isUsingGreeterUser
            setRadioButtonSrc(radioButtonCustom, !it.isUsingDefaultMessage, it.isUsingGreeterUser)
            customToggleVoiceGreeter.setToggle(it.isUsingVoiceGreeter)
            customToggleLocationAnnouncements.setToggle(it.isUsingLocationAnnouncements)
            relativeLayoutDefaultMessage.isEnabled = it.isUsingGreeterUser
            relativeLayoutCustom.isEnabled = it.isUsingGreeterUser
        }
    }

    private fun getSettings() = SettingsModel.getSettings(context!!)

    private fun saveSettings(settingsModel: SettingsModel) {
        SettingsModel.saveSettings(
            context!!,
            settingsModel,
            object : SettingsModel.Companion.ISaveSettingsCallback {
                override fun onComplete() {
                    updateUi(settingsModel)
                }
            })
    }

    private fun descriptionFormat(description: String) = "($description)"

    private fun setRadioButtonSrc(radioButton: ImageView?, selected: Boolean, enabled: Boolean) {
        if (radioButton == null) {
            return
        }
        if (enabled && selected) {
            radioButton.setImageResource(R.drawable.ic_radio_button_on)
        } else if (enabled && !selected) {
            radioButton.setImageResource(R.drawable.ic_radio_button_off)
        } else if (!enabled && selected) {
            radioButton.setImageResource(R.drawable.ic_radio_button_on_disable)
        } else {
            radioButton.setImageResource(R.drawable.ic_radio_button_off_disable)
        }
    }

    companion object {
        fun newInstance() = ConfigurationFragment()
    }
}