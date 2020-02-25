package com.robotemi.welcomingbtob

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import com.robotemi.sdk.listeners.OnDetectionStateChangedListener
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener.Companion.COMPLETE
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener.Companion.START
import com.robotemi.sdk.listeners.OnRobotReadyListener
import com.robotemi.sdk.listeners.OnUserInteractionChangedListener
import com.robotemi.welcomingbtob.call.CallActivity
import com.robotemi.welcomingbtob.featurelist.FeatureListFragment
import com.robotemi.welcomingbtob.settings.SettingsActivity
import com.robotemi.welcomingbtob.settings.SettingsModel
import com.robotemi.welcomingbtob.utils.Constants.Companion.HOME_BASE_FROM_ROBOX
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
    OnGoToLocationStatusChangedListener, Robot.TtsListener {

    companion object {
        internal const val DELAY_FOR_ACTIVE = 2L
        internal const val REQUEST_CODE_FOR_CALL_ACTIVITY = 1
    }

    private var ttsStatus: TtsRequest.Status = TtsRequest.Status.COMPLETED

    private val robot: Robot by inject()

    private var disposableAction: Disposable = Disposables.disposed()

    private var detectionState = OnDetectionStateChangedListener.IDLE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("Main Activity - onCreate()")

        setContentView(R.layout.activity_main)
        btnOpenHomeList.setOnLongClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            activeDefault()
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
        robot.addTtsListener(this)
        toggleActivityClickListener(true)
        if (textViewGreeting.isVisible) {
            textViewGreeting.visibility = View.GONE
        }
    }

    override fun onPause() {
        super.onPause()
        robot.removeOnRobotReadyListener(this)
        robot.removeOnUserInteractionChangedListener(this)
        robot.removeDetectionStateChangedListener(this)
        robot.removeOnGoToLocationStatusChangedListener(this)
        robot.removeTtsListener(this)
        if (!disposableAction.isDisposed) {
            disposableAction.dispose()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.d("onActivityResult - requestCode=$requestCode, resultCode=$resultCode, data=$data")
        if (data == null) {
            activeDefault()
            return
        }
        when (requestCode) {
            REQUEST_CODE_FOR_CALL_ACTIVITY -> {
                detectionState =
                    data.getIntExtra(CallActivity.EXTRA_DETECTION_STATE, detectionState)
                if (resultCode == CallActivity.RESULT_CODE_FOR_FINISH_BY_DETECTION_LOST) {
                    handleIdle()
                } else {
                    activeDefault()
                }
            }
            else -> {
                activeDefault()
            }
        }
    }

    override fun toggleActivityClickListener(enable: Boolean) {
        constraintLayoutParent.setOnClickListener {
            if (enable) {
                constraintLayoutParent.setBackgroundResource(R.drawable.bg_dark_overlay)
                startFragment(FeatureListFragment.newInstance())
            }
            robot.stopMovement()
        }
    }

    override fun onUserInteraction(isInteracting: Boolean) {
        Timber.i("onUserInteraction, isInteracting=$isInteracting")
        // Wakeup will cause user interaction
        if (isInteracting) {
            if (!textViewGreeting.isVisible && detectionState != OnDetectionStateChangedListener.DETECTED) {
                Timber.d("onUserInteraction, interaction=true. active default")
                activeDefault()
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
            val userInfo = robot.adminInfo
            if (userInfo != null && !userInfo.picUrl.isNullOrEmpty()) {
                Glide.with(this).load(userInfo.picUrl).preload()
            }
        }
    }

    override fun onTtsStatusChanged(ttsRequest: TtsRequest) {
        Timber.d("onTtsStatusChanged, ttsRequest=$ttsRequest")
        ttsStatus = ttsRequest.status
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
        val settingsModel = getSettings()
        var delay = 0L
        if (settingsModel.isUsingDisplayMessage) {
            textViewGreeting.text = settingsModel.displayMessage
            textViewGreeting.visibility = View.VISIBLE
            delay = DELAY_FOR_ACTIVE
        }
        if (settingsModel.isUsingVoiceGreeter) {
            robot.cancelAllTtsRequests()
            robot.speak(TtsRequest.create(settingsModel.voiceGreetingMessage, false))
        }
        constraintLayoutParent.setBackgroundResource(R.drawable.bg_dark_overlay)
        if (!disposableAction.isDisposed) {
            disposableAction.dispose()
        }
        disposableAction = Completable.timer(delay, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { Timber.d("Timer for active starts, ${delay}s left.") }
            .subscribe {
                Timber.d("Timer for active ends, active something..")
                if (settingsModel.isUsingCallPageInterface) {
                    activeCall()
                } else {
                    activeDefault()
                }
            }
    }

    private fun activeCall() {
        val intent = Intent(this, CallActivity::class.java)
        intent.putExtra(CallActivity.EXTRA_TTS_STATUS, ttsStatus)
        startActivityForResult(intent, REQUEST_CODE_FOR_CALL_ACTIVITY)
    }

    private fun activeDefault() {
        startFragment(FeatureListFragment.newInstance())
    }

    private fun handleIdle() {
        resetUI()
        if (!disposableAction.isDisposed) {
            disposableAction.dispose()
        }
    }

    private fun startFragment(fragment: Fragment) {
        textViewGreeting.visibility = View.GONE
        frameLayout.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment, fragment.javaClass.name)
            .commitAllowingStateLoss()
        if (!disposableAction.isDisposed) {
            disposableAction.dispose()
        }
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

    private fun getSettings() = SettingsModel.getSettings(this)

    private fun speak(speech: String) {
        if (!getSettings().isUsingLocationAnnouncements) {
            return
        }
        robot.cancelAllTtsRequests()
        robot.speak(TtsRequest.create(speech, false))
    }

    override fun onGoToLocationStatusChanged(
        location: String,
        status: String,
        descriptionId: Int,
        description: String
    ) {
        val locationName = if (HOME_BASE_FROM_ROBOX.toLowerCase().trim() == location) {
            getString(R.string.location_home_base)
        } else {
            location
        }
        when (status) {
            START -> speak(String.format(getString(R.string.go_to_start_tts), locationName))
            COMPLETE -> speak(String.format(getString(R.string.go_to_complete_tts), locationName))
        }
    }
}