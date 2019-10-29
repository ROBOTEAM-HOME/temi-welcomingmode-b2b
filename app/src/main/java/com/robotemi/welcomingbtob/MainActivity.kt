package com.robotemi.welcomingbtob

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.robotemi.sdk.Robot
import com.robotemi.sdk.listeners.OnRobotReadyListener
import com.robotemi.sdk.listeners.OnWelcomingModeStatusChangedListener
import com.robotemi.sdk.listeners.OnWelcomingModeStatusChangedListener.ACTIVE
import com.robotemi.sdk.listeners.OnWelcomingModeStatusChangedListener.IDLE
import com.robotemi.welcomingbtob.featurelist.FeatureListFragment
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), OnRobotReadyListener,
    OnWelcomingModeStatusChangedListener, IActivityCallback {

    private val robot: Robot by inject()

    private var disposableAction: Disposable = Disposables.disposed()

    private var disposableScreenSaver: Disposable = Disposables.disposed()

    override fun toggleActivityClickListener(enable: Boolean) {
        if (enable) {
            constraintLayoutParent.setOnClickListener {
                startFragment(FeatureListFragment.newInstance())
            }
        } else {
            constraintLayoutParent.setOnClickListener(null)
        }
    }

    override fun toggleWelcomingModeListener(enable: Boolean) {
        if (enable) {
            robot.addOnWelcomingModeStatusChangedListener(this)
        } else {
            robot.removeOnWelcomingModeStatusChangedListener(this)
        }
    }

    override fun onWelcomingModeStatusChanged(status: String) {
        Timber.d("onWelcomingModeStatusChanged(String) (status=$status)")
        robot.hideTopBar()
        when (status) {
            ACTIVE -> handleActive()
            IDLE -> handleIdle()
        }
    }

    private fun handleActive() {
        removeFragments()
        textViewGreeting.visibility = View.VISIBLE
        disposableAction.dispose()
        disposableAction = Completable.timer(2, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                startFragment(FeatureListFragment.newInstance())
                constraintLayoutParent.setBackgroundResource(R.drawable.bg_dark_overlay)
            }
    }

    private fun handleIdle() {
        resetUI()
        disposableAction.dispose()
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
            robot.hideTopBar()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnOpenHomeList.setOnLongClickListener {
            robot.showAppList()
            true
        }
        imageButtonClose.setOnClickListener {
            startFragment(FeatureListFragment.newInstance())
            imageButtonClose.visibility = View.GONE
            constraintLayoutParent.setBackgroundResource(R.drawable.bg_dark_overlay)
        }
    }

    private fun setCloseButtonVisibility(isVisible: Boolean) {
        imageButtonClose.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        robot.hideTopBar()
        robot.addOnRobotReadyListener(this)
        robot.addOnWelcomingModeStatusChangedListener(this)
        toggleActivityClickListener(true)
    }

    override fun onPause() {
        super.onPause()
        robot.removeOnRobotReadyListener(this)
        robot.removeOnWelcomingModeStatusChangedListener(this)

        if (!disposableScreenSaver.isDisposed) {
            disposableScreenSaver.dispose()
        }
        if (!disposableAction.isDisposed) {
            disposableAction.dispose()
        }
    }

    private fun startFragment(fragment: Fragment) {
        textViewGreeting.visibility = View.GONE
        frameLayout.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragment)
            .commitAllowingStateLoss()
        disposableAction.dispose()
    }

    private fun removeFragments() {
        val fragments = supportFragmentManager.fragments
        frameLayout.visibility = View.GONE
        for (fragment in fragments) {
            supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        }
        stopTimerForScreenSaver()
    }

    private fun resetUI() {
        textViewGreeting.visibility = View.GONE
        constraintLayoutParent.setBackgroundResource(0)
        removeFragments()
    }

    private fun stopTimerForScreenSaver() {
        if (!disposableScreenSaver.isDisposed) {
            disposableScreenSaver.dispose()
        }
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        robot.stopMovement()
    }
}