package com.robotemi.welcomingbtob.featurelist.walk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.robotemi.sdk.Robot
import com.robotemi.welcomingbtob.R
import com.robotemi.welcomingbtob.featurelist.FeatureListFragment
import com.robotemi.welcomingbtob.featurelist.adapter.FeatureListAdapter
import com.robotemi.welcomingbtob.featurelist.adapter.ViewHolder
import kotlinx.android.synthetic.main.fragment_sub_feature_list.*

class WalkFragment : FeatureListFragment() {

    private var featureList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sub_feature_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textViewTitle.text = getString(R.string.feature_walk)
        textViewSubtitle.text = getString(R.string.sub_title_walk)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator!!.changeDuration = 0
        featureList.clear()
        featureList.addAll(Robot.getInstance().locations)
        featureList = Robot.getInstance().locations
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

    fun handleAction(name: String) {
        Robot.getInstance().goTo(name)
    }

    companion object {
        fun newInstance() = WalkFragment()
    }
}
