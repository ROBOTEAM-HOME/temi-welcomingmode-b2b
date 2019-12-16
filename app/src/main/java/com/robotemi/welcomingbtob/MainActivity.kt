package com.robotemi.welcomingbtob

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import com.robotemi.sdk.listeners.OnDetectionStateChangedListener
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener.Companion.COMPLETE
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener.Companion.START
import com.robotemi.sdk.listeners.OnRobotReadyListener
import com.robotemi.sdk.listeners.OnUserInteractionChangedListener
import com.robotemi.welcomingbtob.featurelist.FeatureListFragment
import com.robotemi.welcomingbtob.featurelist.walk.WalkFragment
import com.robotemi.welcomingbtob.settings.SettingsActivity
import com.robotemi.welcomingbtob.settings.SettingsModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), OnRobotReadyListener, IActivityCallback,
    OnUserInteractionChangedListener, OnDetectionStateChangedListener,
    OnGoToLocationStatusChangedListener {

    private val robot: Robot by inject()

    private var disposableAction: Disposable = Disposables.disposed()

    private var disposableTopUpdating: Disposable = Disposables.disposed()

    private var detectionState = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnOpenHomeList.setOnLongClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            true
        }
        imageButtonClose.setOnClickListener {
            startFragment(FeatureListFragment.newInstance())
            imageButtonClose.visibility = View.GONE
            constraintLayoutParent.setBackgroundResource(R.drawable.bg_dark_overlay)
        }
    }

    override fun onResume() {
        super.onResume()
        robot.addOnRobotReadyListener(this)
        robot.addOnUserInteractionChangedListener(this)
        robot.addOnDetectionStateChangedListener(this)
        robot.addOnGoToLocationStatusChangedListener(this)
        toggleActivityClickListener(true)
    }

    override fun onPause() {
        super.onPause()
        robot.removeOnRobotReadyListener(this)
        robot.removeOnUserInteractionChangedListener(this)
        robot.removeDetectionStateChangedListener(this)
        robot.removeOnGoToLocationStatusChangedListener(this)
        if (!disposableAction.isDisposed) {
            disposableAction.dispose()
        }
        if (!disposableTopUpdating.isDisposed) {
            disposableTopUpdating.dispose()
        }
    }

    override fun toggleActivityClickListener(enable: Boolean) {
        if (enable) {
            constraintLayoutParent.setOnClickListener {
                constraintLayoutParent.setBackgroundResource(R.drawable.bg_dark_overlay)
                startFragment(FeatureListFragment.newInstance())
            }
        } else {
            constraintLayoutParent.setOnClickListener(null)
        }
    }

    override fun onUserInteraction(isInteracting: Boolean) {
        Timber.i("onUserInteraction, isInteracting=$isInteracting")
        if (isInteracting) {
            if (!textViewGreeting.isVisible && supportFragmentManager.fragments.count() == 0) {
                startFragment(FeatureListFragment.newInstance())
            }
        } else {
            handleIdle()
        }
    }

    override fun onDetectionStateChanged(state: Int) {
        Timber.d("onDetectionStateChanged, state = %d", state)
        if (state == OnDetectionStateChangedListener.DETECTED && detectionState == OnDetectionStateChangedListener.IDLE) {
            handleActive()
        }
        detectionState = state
    }

    override fun setCloseVisibility(isVisible: Boolean) {
        setCloseButtonVisibility(isVisible)
    }

    override fun onRobotReady(isReady: Boolean) {
        Timber.d("onRobotReady(Boolean) (isReady=%b)", isReady)
        if (isReady) {
            val activityInfo =
                packageManager.getActivityInfo(componentName, PackageManager.GET_META_DATA)
            robot.onStart(activityInfo)
        }
    }

    override fun onUserInteraction() {
        Timber.d("onUserInteraction - tap")
        super.onUserInteraction()
        robot.stopMovement()
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return if (event?.keyCode == KeyEvent.KEYCODE_BACK) {
            true
        } else {
            super.dispatchKeyEvent(event)
        }
    }

    private fun setCloseButtonVisibility(isVisible: Boolean) {
        imageButtonClose.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun handleActive() {
        removeFragments()
        val settingsModel = SettingsModel.getSettings(this)
        var delay = 0L
        val greetMessage =
            if (settingsModel.isUsingDefaultMessage || settingsModel.customMessage.isEmpty()) {
                settingsModel.defaultMessage
            } else {
                settingsModel.customMessage
            }
        if (settingsModel.isUsingGreeterUser) {
            textViewGreeting.text = greetMessage
            textViewGreeting.visibility = View.VISIBLE
            delay = 2L
        }
        if (settingsModel.isUsingVoiceGreeter) {
            robot.cancelAllTtsRequests()
            robot.speak(TtsRequest.create(greetMessage, false))
        }
        constraintLayoutParent.setBackgroundResource(R.drawable.bg_dark_overlay)
        if (!disposableAction.isDisposed) {
            disposableAction.dispose()
        }
        disposableAction.dispose()
        disposableAction = Completable.timer(delay, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                startFragment(FeatureListFragment.newInstance())
            }
    }

    private fun handleIdle() {
        resetUI()
        if (!disposableAction.isDisposed) {
            disposableAction.dispose()
        }
        disposableAction.dispose()
    }

    private fun startFragment(fragment: Fragment) {
        textViewGreeting.visibility = View.GONE
        frameLayout.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment, fragment.javaClass.name)
            .commitAllowingStateLoss()
        disposableAction.dispose()
    }

    private fun removeFragments() {
        val fragments = supportFragmentManager.fragments
        frameLayout.visibility = View.GONE
        for (fragment in fragments) {
            supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        }
    }

    private fun resetUI() {
        textViewGreeting.visibility = View.GONE
        constraintLayoutParent.setBackgroundResource(0)
        removeFragments()
    }

    override fun onGoToLocationStatusChanged(
        location: String,
        status: String,
        descriptionId: Int,
        description: String
    ) {
        val locationName = if (WalkFragment.HOME_BASE_FROM_ROBOX.toLowerCase().trim() == location) {
            getString(R.string.location_home_base)
        } else {
            location
        }
        when (status) {
            START -> {
                robot.speak(
                    TtsRequest.create(
                        String.format(
                            getString(R.string.go_to_start_tts),
                            locationName
                        ), false
                    )
                )
            }
            COMPLETE -> {
                robot.speak(
                    TtsRequest.create(
                        String.format(
                            getString(R.string.go_to_complete_tts),
                            locationName
                        ), false
                    )
                )
            }
        }
    }
}