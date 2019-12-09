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
        activityCallback.apply {
            setTitle(getString(R.string.fragment_custom_greeter))
            setVisibilityOfDone(true)
            setBackClickListener(View.OnClickListener { close() })
            setEnableOfDone(customizeGreeter.isNotEmpty())
            setDoneClickListener(View.OnClickListener {
                saveCustomizeGreeterMessage(editTextGreeterMessage.text.toString())
                close()
            })
        }
        editTextGreeterMessage.setText(customizeGreeter)
        textViewMessageCounter.text = (MAX_LENGTH - customizeGreeter.length).toString()
        editTextGreeterMessage.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Timber.d("Settings-custom: $s")
                textViewMessageCounter.text = (MAX_LENGTH - s!!.length).toString()
                if (s.isNotEmpty()) {
                    activityCallback.setEnableOfDone(true)
                    textViewAlert.visibility = View.GONE
                } else {
                    activityCallback.setEnableOfDone(false)
                    textViewAlert.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun saveCustomizeGreeterMessage(text: String) {
        val settings = getSettings()
        settings.customMessage = text
        saveSettings(settings)
    }

    private fun getSettings() = SettingsModel.getSettings(context!!)

    private fun saveSettings(settingsModel: SettingsModel) {
        SettingsModel.saveSettings(context!!, settingsModel, null)
    }

    private fun close() {
        activity?.supportFragmentManager?.popBackStack()
    }

    companion object {
        private const val MAX_LENGTH = 12
        fun newInstance() = CustomGreeterFragment()
    }

}
