package com.robotemi.welcomingbtob.featurelist.walk

import android.annotation.SuppressLint
import com.robotemi.sdk.listeners.OnLocationsUpdatedListener
import com.robotemi.welcomingbtob.R
import com.robotemi.welcomingbtob.featurelist.FeatureBaseFragment
import com.robotemi.welcomingbtob.featurelist.adapter.ViewHolder
import com.robotemi.welcomingbtob.utils.Constants.Companion.HOME_BASE_FROM_ROBOX
import kotlinx.android.synthetic.main.fragment_sub_feature_list.*

class WalkFragment : FeatureBaseFragment(), OnLocationsUpdatedListener {

    private var featureList = mutableListOf<String>()

    override fun getLayoutResId() = R.layout.fragment_sub_feature_list

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
        } else {
            robot.goTo(featureObj as String)
        }
    }

    override fun handleListMedia(featureObj: Any, holder: ViewHolder) {
        holder.setText(R.id.textViewName, featureObj as CharSequence)
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

    override fun onLocationsUpdated(locations: List<String>) {
        featureList.clear()
        featureList.addAll(displayLocationList(locations))
        adapter.notifyDataSetChanged()
    }

    companion object {
        fun newInstance() = WalkFragment()
    }
}
