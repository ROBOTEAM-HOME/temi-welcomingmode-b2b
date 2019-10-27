package com.robotemi.welcomingbtob.featurelist.walk

import android.os.Bundle
import android.view.View
import com.robotemi.welcomingbtob.R
import com.robotemi.welcomingbtob.featurelist.FeatureBaseFragment
import com.robotemi.welcomingbtob.featurelist.adapter.ViewHolder
import kotlinx.android.synthetic.main.fragment_sub_feature_list.*

class WalkFragment : FeatureBaseFragment() {
    override fun getLayoutResId() = R.layout.fragment_sub_feature_list
    override fun configureTextViews() {
        textViewTitle.text = getString(R.string.feature_walk)
        textViewSubtitle.text = getString(R.string.sub_title_walk)
    }

    override fun getFeatureList(): List<String> {
        featureList = robot.locations
        return featureList
    }

    override fun getCardLayoutId() = R.layout.item_sub_feature_card

    override fun handleAction(featureObj: Any) {
        robot.goTo(featureObj as String)
    }

    override fun handleListMedia(featureObj: Any, holder: ViewHolder) {
        holder.setText(R.id.textViewName, featureObj as CharSequence)
    }

    private var featureList = mutableListOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        robot.addOnLocationsUpdatedListener { locations ->
            featureList.clear()
            featureList.addAll(locations!!.toList())
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setCloseVisibility(false)
    }

    companion object {
        fun newInstance() = WalkFragment()
    }
}
