package com.robotemi.welcomingbtob.featurelist.call

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.robotemi.sdk.Robot
import com.robotemi.sdk.UserInfo
import com.robotemi.welcomingbtob.R
import com.robotemi.welcomingbtob.featurelist.FeatureListFragment
import com.robotemi.welcomingbtob.featurelist.adapter.FeatureListAdapter
import com.robotemi.welcomingbtob.featurelist.adapter.ViewHolder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_sub_feature_list.*

class CallFragment : FeatureListFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_call, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textViewTitle.text = getString(R.string.feature_call)
        textViewSubtitle.text = getString(R.string.sub_title_call)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator!!.changeDuration = 0
        val featureList: List<UserInfo> = arrayOf(Robot.getInstance().adminInfo!!).asList()
        val adapter =
            object : FeatureListAdapter<UserInfo>(context!!, R.layout.include_call_card, featureList) {
                override fun convert(holder: ViewHolder, userInfo: UserInfo) {
                    holder.setText(R.id.textViewName, userInfo.name)
                    Picasso.get()
                        .load(userInfo.picUrl)
                        .placeholder(R.drawable.ic_video_call_avatar_placeholder)
                        .into(holder.getView(R.id.imageViewAvatar) as ImageView)
                    holder.setOnClickListener(
                        R.id.linearLayout,
                        View.OnClickListener { handleAction(userInfo) })
                }
            }
        recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setCloseVisibility(false)
    }

    fun handleAction(userInfo: UserInfo) {
        Robot.getInstance().startTelepresence(userInfo.name, userInfo.userId)
    }

    companion object {
        fun newInstance() = CallFragment()
    }
}
