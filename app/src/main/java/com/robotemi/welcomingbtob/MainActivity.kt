package com.robotemi.welcomingbtob

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.robotemi.sdk.Robot
import com.robotemi.sdk.listeners.OnRobotReadyListener
import com.robotemi.sdk.listeners.OnWelcomingModeStatusChangedListener
import com.robotemi.welcomingbtob.featurelist.FeatureListFragment
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), OnRobotReadyListener,
    OnWelcomingModeStatusChangedListener, IActivityCallback {

    private lateinit var robot: Robot

    private var mIsDetected = false

    private var disposableTapRightCorner: Disposable = Disposables.disposed()

    private var disposableAsr: Disposable = Disposables.disposed()

    private var disposableScreenSaver: Disposable = Disposables.disposed()

    override fun toggleActivityClickListener(enable: Boolean) {
        if (enable) {
            constraintLayoutParent.setOnClickListener {
                startFragment(
                    FeatureListFragment.newInstance()
                )
            }
        } else {
            constraintLayoutParent.setOnClickListener(null)
        }
    }

    override fun toggleWelcomingModeListener(enable: Boolean) {
        if (enable) {
            robot.addOnWelcomingModeStatusChangedListener(this)
        } else {
            robot.removeWelcomingModeStatusChangedListener(this)
        }
    }

    override fun onWelcomingModeStatusChanged(
        angle: Double,
        distance: Double,
        isDetected: Boolean
    ) {
        Timber.d("onWelcomingModeStatusChanged(Double, Double, Boolean) (angle=$angle, distance=$distance, isDetected=$isDetected)")
        robot.hideTopBar()
//        robot.stopMovement()
//        robot.toggleWelcomingMode(true)
        if (isDetected) {
            removeFragment()
            textViewGreeting.visibility = View.VISIBLE
            if (isDetected == mIsDetected) {
                return
            }
            mIsDetected = true
            disposableAsr.dispose()
            disposableAsr = Observable.timer(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { robot.triggerWakeup() }
        } else {
            mIsDetected = false
            textViewGreeting.visibility = View.GONE
            removeFragment()
            disposableAsr.dispose()
        }
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
            robot.toggleWelcomingMode(true)
            robot.hideTopBar()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        robot = Robot.getInstance()
        btnOpenHomeList.setOnTouchListener { _, event ->
            if (event?.action == MotionEvent.ACTION_DOWN) {
                disposableTapRightCorner.dispose()
                disposableTapRightCorner = Observable.timer(5, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { robot.showAppList() }
            } else if (event?.action == MotionEvent.ACTION_UP) {
                disposableTapRightCorner.dispose()
            }
            false
        }
        imageButtonClose.setOnClickListener {
            startFragment(FeatureListFragment.newInstance())
            imageButtonClose.visibility = View.GONE
            robot.toggleWelcomingMode(true)
        }
    }

    private fun setCloseButtonVisibility(isVisible: Boolean) {
        imageButtonClose.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        robot.hideTopBar()
        robot.toggleWelcomingMode(true)
        robot.addOnRobotReadyListener(this)
        robot.addOnWelcomingModeStatusChangedListener(this)
        toggleActivityClickListener(true)
        startTimerForScreenSaver()
    }

    override fun onPause() {
        super.onPause()
        robot.toggleWelcomingMode(false)
        robot.removeOnRobotReadyListener(this)
        robot.removeWelcomingModeStatusChangedListener(this)
        mIsDetected = false
        if (!disposableScreenSaver.isDisposed) {
            disposableScreenSaver.dispose()
        }
        if (!disposableAsr.isDisposed) {
            disposableAsr.dispose()
        }
        if (!disposableTapRightCorner.isDisposed) {
            disposableTapRightCorner.dispose()
        }
    }

    private fun startFragment(fragment: Fragment) {
        toggleWelcomingModeListener(false)
        robot.toggleWelcomingMode(false)
        textViewGreeting.visibility = View.GONE
        frameLayout.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit()
    }

    private fun removeFragment() {
        val fragments = supportFragmentManager.fragments
        frameLayout.visibility = View.GONE
        for (fragment in fragments) {
            supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        }
    }

    private fun startTimerForScreenSaver() {
        stopTimerForScreenSaver()
        disposableScreenSaver = Completable.timer(10, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { removeFragment() }
    }

    private fun stopTimerForScreenSaver() {
        if (!disposableScreenSaver.isDisposed) {
            disposableScreenSaver.dispose()
        }
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        startTimerForScreenSaver()
    }

}
