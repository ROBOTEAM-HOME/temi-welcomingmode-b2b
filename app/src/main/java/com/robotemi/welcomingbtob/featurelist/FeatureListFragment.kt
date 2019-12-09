package com.robotemi.welcomingbtob.featurelist

import androidx.fragment.app.Fragment
import com.robotemi.welcomingbtob.R
import com.robotemi.welcomingbtob.featurelist.adapter.ViewHolder
import com.robotemi.welcomingbtob.featurelist.call.CallFragment
import com.robotemi.welcomingbtob.featurelist.play.PlayFragment
import com.robotemi.welcomingbtob.featurelist.walk.WalkFragment
import kotlinx.android.synthetic.main.fragment_sub_feature_list.*

open class FeatureListFragment : FeatureBaseFragment() {
    override fun handleListMedia(featureObj: Any, holder: ViewHolder) {
        holder.setText(R.id.textViewName, featureObj as CharSequence)
    }

    override fun getLayoutResId() = R.layout.fragment_feature_list

    override fun configureTextViews() {
        textViewSubtitle.text = getString(R.string.welcome)
    }

    override fun getFeatureList() = resources.getStringArray(R.array.feature).asList()

    override fun getCardLayoutId() = R.layout.item_feature_card

    override fun handleAction(name: Any) {
        when (name as String) {
            getString(R.string.feature_walk) -> goToFragment(WalkFragment.newInstance())
            getString(R.string.feature_call) -> goToFragment(CallFragment.newInstance())
            getString(R.string.feature_play) -> goToFragment(PlayFragment.newInstance())
        }
    }

    private fun goToFragment(fragment: Fragment) {
        setCloseVisibility(true)
        val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }

    companion object {
        fun newInstance() = FeatureListFragment()
    }
}