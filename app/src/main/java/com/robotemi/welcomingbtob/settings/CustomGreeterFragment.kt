package com.robotemi.welcomingbtob.settings


import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.robotemi.welcomingbtob.BaseFragment
import com.robotemi.welcomingbtob.R
import kotlinx.android.synthetic.main.fragment_custom.*
import timber.log.Timber

class CustomGreeterFragment private constructor() : BaseFragment() {

    private lateinit var customizeType: String

    private val activityCallback by lazy { context as IActivityCallback }

    override fun getLayoutResId() = R.layout.fragment_custom

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        customizeType = arguments?.getString(CUSTOMIZE_TYPE)!!
        val customizeGreeter = getCustomizeMessage(customizeType)
        activityCallback.apply {
            if (customizeType == CUSTOMIZE_DISPLAY_GREETER) {
                setTitle(getString(R.string.fragment_custom_greeter))
            } else if (customizeType == CUSTOMIZE_VOICE_GREETER) {
                setTitle(getString(R.string.fragment_custom_voice_greeter))
            }
            setVisibilityOfDone(true)
            setBackClickListener(View.OnClickListener { close() })
            setEnableOfDone(customizeGreeter.isNotEmpty())
            setDoneClickListener(View.OnClickListener {
                saveCustomizeMessage(editTextGreeterMessage.text.toString(), customizeType)
                close()
            })
        }
        editTextGreeterMessage.apply {
            if (customizeType == CUSTOMIZE_DISPLAY_GREETER) {
                // Set max length for displayed message.
                filters = arrayOf(InputFilter.LengthFilter(MAX_LENGTH))
                textViewMessageCounter.visibility = View.VISIBLE
            } else {
                textViewMessageCounter.visibility = View.GONE
            }
            setText(customizeGreeter)
            requestFocus()

            (activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
                this, InputMethodManager.SHOW_IMPLICIT
            )

            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    Timber.d("Settings-custom: $s")
                    if (customizeType == CUSTOMIZE_DISPLAY_GREETER) {
                        textViewMessageCounter.visibility = View.VISIBLE
                        textViewMessageCounter.text = (MAX_LENGTH - s!!.length).toString()
                    } else {
                        textViewMessageCounter.visibility = View.GONE
                    }
                    if (s!!.isNotBlank()) {
                        activityCallback.setEnableOfDone(true)
                        textViewAlert.visibility = View.GONE
                    } else {
                        activityCallback.setEnableOfDone(false)
                        textViewAlert.visibility = View.VISIBLE
                    }
                }
            })
        }
        textViewMessageCounter.text = (MAX_LENGTH - customizeGreeter.length).toString()
    }

    private fun getCustomizeMessage(customizeType: String): String {
        return when (customizeType) {
            CUSTOMIZE_DISPLAY_GREETER -> getSettings().displayMessage
            CUSTOMIZE_VOICE_GREETER -> getSettings().voiceGreetingMessage
            else -> ""
        }
    }

    private fun saveCustomizeMessage(text: String, customizeType: String) {
        val settings = getSettings()
        when (customizeType) {
            CUSTOMIZE_DISPLAY_GREETER -> settings.displayMessage = text
            CUSTOMIZE_VOICE_GREETER -> settings.voiceGreetingMessage = text
        }
        saveSettings(settings)
    }

    private fun getSettings() = activityCallback.getSettings()

    private fun saveSettings(settingsModel: SettingsModel) {
        activityCallback.saveSettings(settingsModel) {}
    }

    private fun close() {
        activity?.supportFragmentManager?.popBackStack()
    }

    companion object {
        private const val MAX_LENGTH = 24

        private const val CUSTOMIZE_TYPE = "customize_type"

        const val CUSTOMIZE_DISPLAY_GREETER = "customize_display_greeter"

        const val CUSTOMIZE_VOICE_GREETER = "customize_voice_greeter"

        fun newInstance(customizeType: String): CustomGreeterFragment {
            val fragment = CustomGreeterFragment()
            val bundle = Bundle(1)
            bundle.putString(CUSTOMIZE_TYPE, customizeType)
            fragment.arguments = bundle
            return fragment
        }
    }
}
