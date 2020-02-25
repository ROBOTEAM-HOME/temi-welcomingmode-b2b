package com.robotemi.welcomingbtob.call

import android.animation.Keyframe
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import com.robotemi.sdk.listeners.OnDetectionStateChangedListener
import com.robotemi.sdk.listeners.OnUserInteractionChangedListener
import com.robotemi.welcomingbtob.MainActivity.Companion.DELAY_FOR_ACTIVE
import com.robotemi.welcomingbtob.R
import com.robotemi.welcomingbtob.settings.SettingsModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import kotlinx.android.synthetic.main.activity_call.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.concurrent.TimeUnit


class CallActivity : AppCompatActivity(), OnDetectionStateChangedListener, Robot.TtsListener,
    OnUserInteractionChangedListener {

    companion object {
        private const val DELAY_CALL = 5L

        internal const val EXTRA_TTS_STATUS = "extra_tts_status"
        internal const val EXTRA_DETECTION_STATE = "extra_detection_state"
        internal const val RESULT_CODE_FOR_FINISH_BY_CLOSE_BUTTON = 1
        internal const val RESULT_CODE_FOR_FINISH_BY_DETECTION_LOST = 2

    }

    private var disposableAutoCall: Disposable = Disposables.disposed()

    private var ttsStatus = TtsRequest.Status.COMPLETED

    private var detectionState = OnDetectionStateChangedListener.DETECTED

    private val robot: Robot by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("Call Activity - onCreate()")
        setContentView(R.layout.activity_call)
        ttsStatus = intent?.getSerializableExtra(EXTRA_TTS_STATUS) as TtsRequest.Status
        startAnimationOfIcon()
        startAnimationOfCircles(imageViewCircleSmall, 1f, 2.2f, 1000)
        startAnimationOfCircles(imageViewCircleBig, 1f, 1.7f, 1000)
        imageViewVideoCall.setOnClickListener { startVideoCall() }
        imageViewVideoCallBg.setOnClickListener { startVideoCall() }
        imageButtonClose.setOnClickListener { close() }
        val delay = if (DELAY_CALL - DELAY_FOR_ACTIVE < 0) {
            0L
        } else {
            DELAY_CALL - DELAY_FOR_ACTIVE
        }
        startAutoCall(delay)
    }

    override fun onResume() {
        super.onResume()
        Timber.d("Call Activity - onResume()")
        robot.addOnDetectionStateChangedListener(this)
        robot.addTtsListener(this)
        robot.addOnUserInteractionChangedListener(this)
        robot.hideTopBar()
    }

    override fun onPause() {
        super.onPause()
        Timber.d("Call Activity - onPause()")
        robot.removeDetectionStateChangedListener(this)
        robot.removeTtsListener(this)
        robot.removeOnUserInteractionChangedListener(this)
        cancelAutoCall()
    }

    override fun onBackPressed() {
        close()
        super.onBackPressed()
    }

    /**
     * Finish CallActivity by user's finger.
     */
    private fun close() {
        val intent = Intent()
        intent.putExtra(EXTRA_DETECTION_STATE, detectionState)
        setResult(RESULT_CODE_FOR_FINISH_BY_CLOSE_BUTTON, intent)
        finish()
    }

    private fun closeByDetectionLost() {
        val intent = Intent()
        intent.putExtra(EXTRA_DETECTION_STATE, OnDetectionStateChangedListener.IDLE)
        setResult(RESULT_CODE_FOR_FINISH_BY_DETECTION_LOST, intent)
        finish()
    }

    /**
     * Scale & Alpha animator for circles.
     */
    private fun startAnimationOfCircles(view: ImageView, from: Float, to: Float, duration: Long) {
        val scaleAnim = ScaleAnimation(
            from,
            to,
            from,
            to,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        scaleAnim.duration = duration
        scaleAnim.repeatMode = AnimationSet.RESTART
        scaleAnim.repeatCount = AnimationSet.INFINITE
        val alphaAnimation = AlphaAnimation(1f, 0f)
        alphaAnimation.duration = duration
        alphaAnimation.repeatMode = AnimationSet.RESTART
        alphaAnimation.repeatCount = AnimationSet.INFINITE
        val animationSet = AnimationSet(false)
        animationSet.addAnimation(scaleAnim)
        animationSet.addAnimation(alphaAnimation)
        view.startAnimation(animationSet)
    }

    /**
     * Shaking animator for video call icon.
     */
    private fun startAnimationOfIcon() {
        val shakeAngle = 5f
        val staticDuration = 1000f  // Static duration
        val shakeDuration = 600f  // Shaking duration
        val duration = staticDuration + shakeDuration  // Total duration
        val startFraction = staticDuration / duration
        val durationFraction =
            shakeDuration / 4f / duration  // Shaking will be divided into 4 steps.
        val propertyValuesHolder = PropertyValuesHolder.ofKeyframe(
            View.ROTATION,
            // Stay still
            Keyframe.ofFloat(0f, 0f),
            Keyframe.ofFloat(startFraction, 0f),
            // Start shaking (4 steps with the same duration)
            Keyframe.ofFloat(startFraction + durationFraction, -shakeAngle),
            Keyframe.ofFloat(startFraction + durationFraction * 2, shakeAngle),
            Keyframe.ofFloat(startFraction + durationFraction * 3, -shakeAngle),
            Keyframe.ofFloat(startFraction + durationFraction * 4, shakeAngle),
            Keyframe.ofFloat(1.0f, 0f)
        )
        val objectAnimator =
            ObjectAnimator.ofPropertyValuesHolder(imageViewVideoCall, propertyValuesHolder)
        objectAnimator.duration = duration.toLong()
        objectAnimator.repeatMode = ObjectAnimator.RESTART
        objectAnimator.interpolator = LinearInterpolator()
        objectAnimator.repeatCount = ObjectAnimator.INFINITE
        objectAnimator.start()

    }

    private fun startVideoCall() {
        val adminInfo = robot.adminInfo
        if (adminInfo == null) {
            Timber.e("Admin info is null.")
            return
        }
        robot.startTelepresence(adminInfo.name, adminInfo.userId)
    }

    override fun onDetectionStateChanged(state: Int) {
        Timber.i("Call Activity - onDetectionStateChanged: $state")
        detectionState = state
        // CallActivity don't use the 'windowIsTranslucent=true' theme
        // so that this activity can't restart welcoming if welcoming ended by a video call.
        // So we need start constraintBeWith here manually.
        if (state == OnDetectionStateChangedListener.DETECTED) {
            robot.constraintBeWith()
        } else if (state == OnDetectionStateChangedListener.IDLE) {
            // ConstraintBeWith action should be stop manually also.
            robot.stopMovement()
        }
    }

    override fun onUserInteraction(isInteracting: Boolean) {
        Timber.i("onUserInteraction, isInteracting=$isInteracting")
        if (!isInteracting) {
            closeByDetectionLost()
        }
    }

    override fun onUserInteraction() {
        Timber.d("onUserInteraction - From Activity.java of Android SDK.")
        super.onUserInteraction()
        robot.stopMovement()
        cancelAutoCall()
    }

    override fun onTtsStatusChanged(ttsRequest: TtsRequest) {
        Timber.d("Call Activity - ttsRequest.status = ${ttsRequest.status}")
        ttsStatus = ttsRequest.status
        if (ttsRequest.status == TtsRequest.Status.COMPLETED
            || ttsRequest.status == TtsRequest.Status.ERROR
            || ttsRequest.status == TtsRequest.Status.NOT_ALLOWED
        ) {
            startAutoCall(DELAY_CALL)
        }
    }

    private fun startAutoCall(delay: Long) {
        cancelAutoCall()
        if (!SettingsModel.getSettings(this).isUsingAutoCall
            || ttsStatus == TtsRequest.Status.STARTED
            || ttsStatus == TtsRequest.Status.PENDING
        ) {
            return
        }
        disposableAutoCall = Completable.timer(delay, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { Timber.d("Start timer for auto call, ${delay}s left.") }
            .doOnError { Timber.e(it) }
            .subscribe {
                Timber.d("Timer of auto call ends, start video call..")
                startVideoCall()
            }
    }

    private fun cancelAutoCall() {
        Timber.d("Cancel auto call.")
        if (!disposableAutoCall.isDisposed) {
            disposableAutoCall.dispose()
        }
    }

}
