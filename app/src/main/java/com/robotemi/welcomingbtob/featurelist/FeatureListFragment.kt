package com.robotemi.welcomingbtob.featurelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.robotemi.sdk.Robot
import com.robotemi.welcomingbtob.IActivityCallback
import com.robotemi.welcomingbtob.R
import com.robotemi.welcomingbtob.featurelist.adapter.FeatureListAdapter
import com.robotemi.welcomingbtob.featurelist.adapter.ViewHolder
import com.robotemi.welcomingbtob.featurelist.call.CallFragment
import com.robotemi.welcomingbtob.featurelist.play.PlayFragment
import com.robotemi.welcomingbtob.featurelist.walk.WalkFragment
import kotlinx.android.synthetic.main.fragment_feature_list.*

open class FeatureListFragment : Fragment() {

    private val activityCallback by lazy { context as IActivityCallback }

    private lateinit var robot: Robot

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feature_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        robot = Robot.getInstance()
        textViewSubtitle.text = getString(R.string.welcome)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator!!.changeDuration = 0
        val featureList: List<String> = arrayOf(
            getString(R.string.feature_walk),
            getString(R.string.feature_call),
            getString(R.string.feature_play)
        ).asList()
        val adapter =
            object :
                FeatureListAdapter<String>(context!!, R.layout.item_feature_card, featureList) {
                override fun convert(holder: ViewHolder, name: String) {
                    holder.setText(R.id.textViewName, name)
                    holder.setOnClickListener(
                        R.id.linearLayout,
                        View.OnClickListener { handleAction(name) })
                }
            }
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        tipsViewFlipper.start()
        robot.hideTopBar()
        toggleActivityClickListener(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        toggleActivityClickListener(true)
    }

    private fun handleAction(name: String) {
        when (name) {
            getString(R.string.feature_walk) -> {
                goToFragment(WalkFragment.newInstance())
            }
            getString(R.string.feature_call) -> {
                goToFragment(CallFragment.newInstance())
            }
            getString(R.string.feature_play) -> {
                goToFragment(PlayFragment.newInstance())
            }
        }
    }

    private fun goToFragment(fragment: Fragment) {
        setCloseVisibility(true)
        val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.addToBackStack("feature_list")
        fragmentTransaction.commit()
    }

    protected fun setCloseVisibility(isVisible: Boolean) {
        activityCallback.setCloseVisibility(isVisible)
    }

    private fun toggleActivityClickListener(enable: Boolean) {
        activityCallback.toggleActivityClickListener(enable)
    }

    companion object {
        fun newInstance() = FeatureListFragment()
    }
}
