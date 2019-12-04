package com.robotemi.welcomingbtob.featurelist.walk

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.robotemi.sdk.listeners.OnLocationsUpdatedListener
import com.robotemi.welcomingbtob.R
import com.robotemi.welcomingbtob.featurelist.FeatureBaseFragment
import com.robotemi.welcomingbtob.featurelist.adapter.ViewHolder
import kotlinx.android.synthetic.main.fragment_sub_feature_list.*

class WalkFragment : FeatureBaseFragment(), OnLocationsUpdatedListener {
    override fun getLayoutResId() = R.layout.fragment_sub_feature_list
    override fun configureTextViews() {
        textViewTitle.text = getString(R.string.feature_walk)
        textViewSubtitle.text = getString(R.string.sub_title_walk)
    }

    override fun getFeatureList(): List<String> {
        featureList = displayLocationList(robot.locations)
        return featureList
    }

    override fun getCardLayoutId() = R.layout.item_sub_feature_card

    override fun handleAction(featureObj: Any) {
        if (featureObj == getString(R.string.location_home_base)) {
            robot.goTo(HOME_BASE_FROM_ROBOX)
        }
        robot.goTo(featureObj as String)
    }

    override fun handleListMedia(featureObj: Any, holder: ViewHolder) {
        holder.setText(R.id.textViewName, featureObj as CharSequence)
    }

    private var featureList = mutableListOf<String>()

    override fun onResume() {
        super.onResume()
        robot.addOnLocationsUpdatedListener(this)
    }

    override fun onPause() {
        super.onPause()
        robot.removeOnLocationsUpdateListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setCloseVisibility(false)
    }

    @SuppressLint("DefaultLocale")
    private fun displayLocationList(locationList: List<String>): MutableList<String> {
        val displayLocationList = mutableListOf<String>()
        for ((index, value) in locationList.withIndex()) {
            val locationName = if (HOME_BASE_FROM_ROBOX.toLowerCase().trim() == value) {
                getString(R.string.location_home_base)
            } else {
                value
            }
            displayLocationList.add(index, locationName)
        }
        return displayLocationList
    }

    companion object {
        const val HOME_BASE_FROM_ROBOX = "home base"

        fun newInstance() = WalkFragment()
    }

    override fun onLocationsUpdated(locations: List<String>) {
        featureList.clear()
        featureList.addAll(displayLocationList(locations))
        adapter.notifyDataSetChanged()
    }
}
