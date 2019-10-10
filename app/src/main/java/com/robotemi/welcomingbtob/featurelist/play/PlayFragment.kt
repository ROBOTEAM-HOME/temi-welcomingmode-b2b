package com.robotemi.welcomingbtob.featurelist.play

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.robotemi.sdk.Robot
import com.robotemi.welcomingbtob.R
import com.robotemi.welcomingbtob.featurelist.FeatureListFragment
import com.robotemi.welcomingbtob.featurelist.adapter.FeatureListAdapter
import com.robotemi.welcomingbtob.featurelist.adapter.ViewHolder
import kotlinx.android.synthetic.main.fragment_feature_list.*

class PlayFragment : FeatureListFragment() {

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
                // TODO Add judgement for judging the language version of the Launcher
                val intent =
                    context?.packageManager?.getLaunchIntentForPackage("com.roboteam.teamy.camera.china")
                startActivity(intent)
            }
            getString(R.string.feature_follow) -> Robot.getInstance().beWithMe()
            getString(R.string.feature_play_music) -> {
//                val intent =
//                    context?.packageManager?.getLaunchIntentForPackage("com.roboteam.teamy.iheart.usa")
//                startActivity(intent)
            }
        }
    }

    companion object {
        fun newInstance() = PlayFragment()
    }
}
