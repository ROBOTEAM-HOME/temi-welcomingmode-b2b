package com.robotemi.welcomingbtob.featurelist.play

import android.content.Intent
import com.robotemi.welcomingbtob.R
import com.robotemi.welcomingbtob.featurelist.FeatureBaseFragment
import com.robotemi.welcomingbtob.featurelist.adapter.ViewHolder
import com.robotemi.welcomingbtob.utils.Constants
import kotlinx.android.synthetic.main.fragment_sub_feature_list.*

class PlayFragment : FeatureBaseFragment() {

    override fun getLayoutResId() = R.layout.fragment_sub_feature_list

    override fun configureTextViews() {
        textViewTitle.text = getString(R.string.feature_play)
        textViewSubtitle.text = getString(R.string.sub_title_play)
    }

    override fun getFeatureList() = resources.getStringArray(R.array.feature_play).asList()

    override fun handleAction(featureObj: Any) {
        when (featureObj as String) {
            getString(R.string.feature_take_photo) -> startSkill(SKILL_PACKAGE_CAMERA)
            getString(R.string.feature_follow) -> robot.beWithMe()
            getString(R.string.feature_play_music) -> startSkill(SKILL_PACKAGE_MUSIC)
        }
    }

    override fun getCardLayoutId() = R.layout.item_sub_feature_card

    override fun handleListMedia(featureObj: Any, holder: ViewHolder) {
        holder.setText(R.id.textViewName, featureObj as CharSequence)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setCloseVisibility(false)
    }

    private fun startSkill(packageNameWithoutSuffix: String) {
        val wakeupWord = robot.wakeupWord
        val packageName = StringBuilder(packageNameWithoutSuffix)
        if (wakeupWord.toLowerCase() == Constants.WAKEUP_WORD_ALEXA.toLowerCase()
            || wakeupWord.toLowerCase() == Constants.WAKEUP_WORD_HEY_TEMI.toLowerCase()
        ) {
            packageName.append(Constants.SUFFIX_USA)
        } else {
            packageName.append(Constants.SUFFIX_CHINA)
        }
        val intent = context?.packageManager?.getLaunchIntentForPackage(packageName.toString())
        intent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
        if (intent == null) {
            return
        }
        startActivity(intent)
    }

    companion object {
        private const val SKILL_PACKAGE_CAMERA = "com.roboteam.teamy.camera"

        private const val SKILL_PACKAGE_MUSIC = "com.roboteam.teamy.iheart"

        fun newInstance() = PlayFragment()
    }
}