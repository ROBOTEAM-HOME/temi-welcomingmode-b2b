package com.robotemi.welcomingbtob.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.robotemi.sdk.Robot
import com.robotemi.welcomingbtob.R
import kotlinx.android.synthetic.main.activity_settings.*
import org.koin.android.ext.android.inject

class SettingsActivity : AppCompatActivity(), IActivityCallback {

    private val robot: Robot by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        startFragment(ConfigurationFragment.newInstance())
        imageButtonBack.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
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

    override fun onUserInteraction() {
        super.onUserInteraction()
        robot.stopMovement()
    }
}
