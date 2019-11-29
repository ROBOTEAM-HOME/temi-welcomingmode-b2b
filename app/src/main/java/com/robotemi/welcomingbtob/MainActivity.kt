package com.robotemi.welcomingbtob

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.robotemi.sdk.Robot
import com.robotemi.sdk.listeners.OnBeWithMeStatusChangedListener
import com.robotemi.sdk.listeners.OnConstraintBeWithStatusChangedListener
import com.robotemi.sdk.listeners.OnRobotReadyListener
import com.robotemi.sdk.listeners.OnUserInteractionChangedListener
import com.robotemi.welcomingbtob.featurelist.FeatureListFragment
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), OnRobotReadyListener, OnBeWithMeStatusChangedListener,
    IActivityCallback, OnConstraintBeWithStatusChangedListener, OnUserInteractionChangedListener {

    private val robot: Robot by inject()

    private var disposableAction: Disposable = Disposables.disposed()

    private var disposableTopUpdating: Disposable = Disposables.disposed()

    override fun onBeWithMeStatusChanged(status: String) {
        Timber.d("onBeWithMeStatusChanged(String) (status=$status)")
        if (!disposableTopUpdating.isDisposed) {
            disposableTopUpdating.dispose()
        }
        disposableTopUpdating = Completable.complete()
            .delay(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete { relativeLayoutTop.visibility = View.VISIBLE }
            .subscribe {
                when (status) {
                    OnBeWithMeStatusChangedListener.ABORT -> relativeLayoutTop.visibility =
                        View.GONE
                    OnBeWithMeStatusChangedListener.SEARCH,
                    OnBeWithMeStatusChangedListener.START -> textViewTop.text =
                        getString(R.string.top_bar_searching_text)
                    OnBeWithMeStatusChangedListener.TRACK,
                    OnBeWithMeStatusChangedListener.CALCULATING -> textViewTop.text =
                        getString(R.string.top_bar_following_text)
                    OnBeWithMeStatusChangedListener.OBSTACLE_DETECTED -> textViewTop.text =
                        getString(R.string.top_bar_obstacle_detected_text)
                }
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
        robot.hideTopBar()
        if (isInteracting) handleActive() else handleIdle()
    }

    override fun toggleWelcomingModeListener(enable: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConstraintBeWithStatusChanged(isConstraint: Boolean) {
        Timber.i("onConstraintBeWithStatusChanged, isConstraint=${isConstraint}")
        robot.hideTopBar()
        if (isConstraint) showConstraintLabel() else hideConstraintLabel()
    }

    private fun handleActive() {
        removeFragments()
        constraintLayoutParent.setBackgroundResource(R.drawable.bg_dark_overlay)
        textViewGreeting.visibility = View.VISIBLE
        if (!disposableAction.isDisposed) {
            disposableAction.dispose()
        }
        disposableAction.dispose()
        disposableAction = Completable.timer(2, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                startFragment(FeatureListFragment.newInstance())
            }
    }

    private fun showConstraintLabel() {
        relativeLayoutTop.visibility = View.VISIBLE
        textViewTop.text = getString(R.string.top_bar_hello_text)
    }

    private fun hideConstraintLabel() {
        relativeLayoutTop.visibility = View.GONE
    }

    private fun handleIdle() {
        resetUI()
        if (!disposableAction.isDisposed) {
            disposableAction.dispose()
        }
        disposableAction.dispose()
    }

    override fun setCloseVisibility(isVisible: Boolean) {
        setCloseButtonVisibility(isVisible)
    }

    override fun onRobotReady(isReady: Boolean) {
        Timber.d("onRobotReady(Boolean) (isReady=%b)", isReady)
        robot.hideTopBar()
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
        robot.addOnUserInteractionChangedListener(this)
        robot.addOnConstraintBeWithStatusChangedListener(this)
        robot.addOnBeWithMeStatusChangedListener(this)
        toggleActivityClickListener(true)
        pollingForHidingTopBar()
        startFragment(FeatureListFragment.newInstance())
    }

    override fun onPause() {
        super.onPause()
        robot.removeOnRobotReadyListener(this)
        robot.removeOnUserInteractionChangedListener(this)
        robot.removeOnConstraintBeWithStatusChangedListener(this)
        robot.removeOnBeWithMeStatusChangedListener(this)
        if (!disposableAction.isDisposed) {
            disposableAction.dispose()
        }
        if (!disposableTopUpdating.isDisposed) {
            disposableTopUpdating.dispose()
        }
        if (!disposableHideTopBar.isDisposed) {
            disposableHideTopBar.dispose()
        }
        robot.showTopBar()
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
    }

    private fun resetUI() {
        textViewGreeting.visibility = View.GONE
        constraintLayoutParent.setBackgroundResource(0)
        removeFragments()
    }

    private var disposableHideTopBar: Disposable = Disposables.disposed()

    private fun pollingForHidingTopBar() {
        if (!disposableHideTopBar.isDisposed) {
            disposableHideTopBar.dispose()
        }
        Timber.d("Start polling for hideTopBar..")
        disposableHideTopBar = Completable.timer(5, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Timber.d("hideTopBar under polling..")
                robot.hideTopBar()
                pollingForHidingTopBar()
            }
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        robot.hideTopBar()
        robot.stopMovement()
        relativeLayoutTop.visibility = View.GONE
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return if (event?.keyCode == KeyEvent.KEYCODE_BACK) {
            true
        } else {
            super.dispatchKeyEvent(event)
        }
    }
}