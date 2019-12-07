package com.robotemi.welcomingbtob.settings


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.robotemi.welcomingbtob.R
import kotlinx.android.synthetic.main.fragment_custom.*
import timber.log.Timber

class CustomGreeterFragment : Fragment() {

    private val activityCallback by lazy { context as IActivityCallback }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_custom, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val customizeGreeter = getSettings().customMessage
        activityCallback.setTitle(getString(R.string.fragment_custom_greeter))
        activityCallback.setVisibilityOfDone(true)
        activityCallback.setBackClickListener(View.OnClickListener { close() })
        activityCallback.setEnableOfDone(customizeGreeter.isNotEmpty())
        activityCallback.setDoneClickListener(View.OnClickListener {
            saveCustomizeGreeterMessage(editTextGreeterMessage.text.toString())
            close()
        })
        editTextGreeterMessage.setText(customizeGreeter)
        textViewMessageCounter.text = (25 - customizeGreeter.length).toString()
        editTextGreeterMessage.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Timber.d("Settings-custom: $s")
                textViewMessageCounter.text = (25 - s!!.length).toString()
                if (s.isNotEmpty()) {
                    activityCallback.setEnableOfDone(true)
                } else {
                    activityCallback.setEnableOfDone(false)
                }
            }
        })
    }

    private fun saveCustomizeGreeterMessage(text: String) {
        val settings = getSettings()
        settings.customMessage = text
        saveSettings(settings)
    }

    private fun getSettings(): SettingsModel {
        return SettingsModel.getSettings(context!!)
    }

    private fun saveSettings(settingsModel: SettingsModel) {
        SettingsModel.saveSettings(context!!, settingsModel, null)
    }

    private fun close() {
        activity?.supportFragmentManager?.popBackStack()
    }

    companion object {
        fun newInstance() = CustomGreeterFragment()
    }

}
