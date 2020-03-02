package com.robotemi.welcomingbtob.settings

import android.os.Bundle
import android.view.View
import com.robotemi.welcomingbtob.BaseFragment
import com.robotemi.welcomingbtob.R
import com.robotemi.welcomingbtob.widgets.CustomToggle
import kotlinx.android.synthetic.main.fragment_call_button.*

class CallButtonFragment : BaseFragment() {

    private val activityCallback by lazy { context as IActivityCallback }

    override fun getLayoutResId() = R.layout.fragment_call_button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityCallback.apply {
            setTitle(getString(R.string.settings_call_button))
            setVisibilityOfDone(false)
            setVisibilityOfCustomToggle(true)
            setCustomToggleStatus(getSettings().isUsingCallPageInterface)
            setCustomToggleListener(object : CustomToggle.ToggleListener {
                override fun onToggle(on: Boolean) {
                    getSettings().apply {
                        isUsingCallPageInterface = on
                        if (!on) {
                            isUsingAutoCall = false
                        }
                    }.saveSettings()
                }
            })
            setBackClickListener(View.OnClickListener { close() })
        }
        refreshUI(getSettings())
    }

    private fun refreshUI(settings: SettingsModel) {
        activityCallback.setCustomToggleStatus(settings.isUsingCallPageInterface)
        customToggleAutoCall.setToggle(settings.isUsingAutoCall && settings.isUsingCallPageInterface)
        customToggleAutoCall.setToggleListener(object : CustomToggle.ToggleListener {
            override fun onToggle(on: Boolean) {
                settings.apply {
                    isUsingAutoCall = on
                    if (on) {
                        isUsingCallPageInterface = on
                    }
                }.saveSettings()
            }
        })
    }

    private fun getSettings() = activityCallback.getSettings()

    private fun SettingsModel.saveSettings() {
        activityCallback.saveSettings(this) {
            refreshUI(this)
        }
    }

    private fun close() {
        activity?.supportFragmentManager?.popBackStack()
    }

    companion object {
        fun newInstance() = CallButtonFragment()
    }
}