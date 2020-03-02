package com.robotemi.welcomingbtob.featurelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.robotemi.sdk.Robot
import com.robotemi.welcomingbtob.BaseFragment
import com.robotemi.welcomingbtob.IActivityCallback
import com.robotemi.welcomingbtob.R
import com.robotemi.welcomingbtob.adapter.CommonRvAdapter
import com.robotemi.welcomingbtob.adapter.ViewHolder
import kotlinx.android.synthetic.main.fragment_sub_feature_list.*
import org.koin.android.ext.android.inject

abstract class FeatureBaseFragment : BaseFragment() {

    protected lateinit var adapter: CommonRvAdapter<Any>

    private val activityCallback by lazy { context as IActivityCallback }

    protected val robot: Robot by inject()

    abstract fun configureTextViews()

    abstract fun getFeatureList(): List<Any>

    abstract fun handleAction(featureObj: Any)

    abstract fun getCardLayoutId(): Int

    abstract fun handleListMedia(
        featureObj: Any,
        holder: ViewHolder
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(getLayoutResId(), container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureTextViews()
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator!!.changeDuration = 0
        adapter = getFeatureAdapter()
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

    open fun getFeatureAdapter() =
        object : CommonRvAdapter<Any>(context!!, getCardLayoutId(), getFeatureList()) {
            override fun convert(holder: ViewHolder, featureObj: Any) {
                handleListMedia(featureObj, holder)
                holder.setOnClickListener(
                    R.id.linearLayout,
                    View.OnClickListener { handleAction(featureObj) })
            }
        }

    protected fun setCloseVisibility(isVisible: Boolean) {
        activityCallback.setCloseVisibility(isVisible)
    }

    private fun toggleActivityClickListener(enable: Boolean) {
        activityCallback.toggleActivityClickListener(enable)
    }
}