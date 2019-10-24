package com.robotemi.welcomingbtob.featurelist.call

import android.widget.ImageView
import com.robotemi.sdk.UserInfo
import com.robotemi.welcomingbtob.R
import com.robotemi.welcomingbtob.featurelist.FeatureBaseFragment
import com.robotemi.welcomingbtob.featurelist.adapter.ViewHolder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_sub_feature_list.*

class CallFragment : FeatureBaseFragment() {
    override fun getLayoutResId() = R.layout.fragment_call

    override fun configureTextViews() {
        textViewTitle.text = getString(R.string.feature_call)
        textViewSubtitle.text = getString(R.string.sub_title_call)
    }

    override fun getFeatureList() = arrayOf(robot.adminInfo!!).asList()

    override fun handleAction(featureObj: Any) {
        val userInfo = featureObj as UserInfo
        robot.startTelepresence(userInfo.name, userInfo.userId)
    }

    override fun getCardLayoutId() = R.layout.include_call_card

    override fun handleListMedia(featureObj: Any, holder: ViewHolder) {
        val userInfo = featureObj as UserInfo
        holder.setText(R.id.textViewName, userInfo.name)
        Picasso.get()
            .load(userInfo.picUrl)
            .placeholder(R.drawable.ic_user)
            .into(holder.getView(R.id.imageViewAvatar) as ImageView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setCloseVisibility(false)
    }

    companion object {
        fun newInstance() = CallFragment()
    }
}