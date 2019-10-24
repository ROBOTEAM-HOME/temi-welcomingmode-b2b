package com.robotemi.welcomingbtob.featurelist.play

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.robotemi.welcomingbtob.R
import com.robotemi.welcomingbtob.featurelist.FeatureListFragment
import com.robotemi.welcomingbtob.featurelist.adapter.FeatureListAdapter
import com.robotemi.welcomingbtob.featurelist.adapter.ViewHolder
import com.robotemi.welcomingbtob.utils.Constants
import kotlinx.android.synthetic.main.fragment_sub_feature_list.*
import java.lang.StringBuilder

class PlayFragment : FeatureListFragment() {
    companion object {
        private const val SKILL_PACKAGE_CAMERA = "com.roboteam.teamy.camera"

        private const val SKILL_PACKAGE_MUSIC = "com.roboteam.teamy.iheart"

        fun newInstance() = PlayFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sub_feature_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textViewTitle.text = getString(R.string.feature_play)
        textViewSubtitle.text = getString(R.string.sub_title_play)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator!!.changeDuration = 0
        val featureList: List<String> = arrayOf(
            getString(R.string.feature_take_photo),
            getString(R.string.feature_follow),
            getString(R.string.feature_play_music)
        ).asList()
        val adapter =
            object :
                FeatureListAdapter<String>(context!!, R.layout.item_sub_feature_card, featureList) {
                override fun convert(holder: ViewHolder, name: String) {
                    holder.setText(R.id.textViewName, name)
                    holder.setOnClickListener(
                        R.id.linearLayout,
                        View.OnClickListener { handleAction(name) })
                }
            }
        recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setCloseVisibility(false)
    }

    fun handleAction(name: String) {
        when (name) {
            getString(R.string.feature_take_photo) -> {
                startSkill(SKILL_PACKAGE_CAMERA)
            }
            getString(R.string.feature_follow) -> robot.beWithMe()
            getString(R.string.feature_play_music) -> {
//                if (Robot.getInstance().wakeupWord == Constants.WAKEUP_WORD_DING_DANG) {
//                    return
//                }
                startSkill(SKILL_PACKAGE_MUSIC)
            }
        }
    }

    private fun startSkill(packageNameWithoutSuffix: String) {
        val wakeupWord = robot.wakeupWord
        val packageName = StringBuilder(packageNameWithoutSuffix)
        if (wakeupWord == Constants.WAKEUP_WORD_ALEXA || wakeupWord == Constants.WAKEUP_WORD_HEY_TEMI) {
            packageName.append(Constants.SUFFIX_USA)
        } else {
            packageName.append(Constants.SUFFIX_CHINA)
        }
        val intent = context?.packageManager?.getLaunchIntentForPackage(packageName.toString())
        intent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
        if(intent == null) {
            return
        }
        startActivity(intent)
    }

}
