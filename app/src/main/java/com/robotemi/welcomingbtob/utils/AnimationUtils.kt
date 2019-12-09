package com.robotemi.welcomingbtob.utils

import android.animation.*
import android.view.View
import android.widget.LinearLayout

class AnimationUtils {

    companion object {

        fun animate(
            view: View,
            animationRes: Int,
            listenerAdapter: AnimatorListenerAdapter
        ): Animator {
            val animator = loadItemSlideAnimation(view, animationRes, 0)
            animator.addListener(listenerAdapter)
            animator.start()
            return animator
        }

        private fun loadItemSlideAnimation(view: View, animationRes: Int, delay: Int): Animator {
            val animation = AnimatorInflater.loadAnimator(view.context, animationRes)
            view.setLayerType(LinearLayout.LAYER_TYPE_HARDWARE, null)
            animation.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {

                    val animationSet = animation as AnimatorSet
                    val itemView =
                        (animationSet.childAnimations[0] as ObjectAnimator).target as View
                    itemView.setLayerType(LinearLayout.LAYER_TYPE_NONE, null)
                }
            })

            animation.startDelay = delay.toLong()
            animation.setTarget(view)
            return animation
        }
    }
}