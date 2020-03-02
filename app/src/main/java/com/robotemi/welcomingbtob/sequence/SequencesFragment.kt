package com.robotemi.welcomingbtob.sequence

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.robotemi.sdk.Robot
import com.robotemi.sdk.sequence.SequenceCallback
import com.robotemi.sdk.sequence.SequenceModel
import com.robotemi.welcomingbtob.BaseFragment
import com.robotemi.welcomingbtob.IActivityCallback
import com.robotemi.welcomingbtob.R
import com.robotemi.welcomingbtob.adapter.CommonRvAdapter
import com.robotemi.welcomingbtob.adapter.ViewHolder
import kotlinx.android.synthetic.main.fragment_sequences.*
import org.koin.android.ext.android.inject
import timber.log.Timber

class SequencesFragment : BaseFragment() {

    private val robot: Robot by inject()

    private val activityCallback by lazy { context as IActivityCallback }

    private val mSequenceList = mutableListOf<SequenceModel>()

    override fun getLayoutResId() = R.layout.fragment_sequences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityCallback.toggleActivityClickListener(false)
        initUI()
        initData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activityCallback.toggleActivityClickListener(true)
    }

    private fun initData() {
        fetchSequences()
    }

    private fun initUI() {
        val layoutManager = GridLayoutManager(context, 2)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        recyclerViewSequences.layoutManager = layoutManager
        recyclerViewSequences.setHasFixedSize(true)
        recyclerViewSequences.itemAnimator!!.changeDuration = 0
        recyclerViewSequences.adapter = getAdapter()
    }

    private fun getAdapter(): CommonRvAdapter<SequenceModel> {
        return object :
            CommonRvAdapter<SequenceModel>(
                requireContext(),
                R.layout.item_sequence,
                mSequenceList
            ) {
            override fun convert(holder: ViewHolder, t: SequenceModel) {
                holder.setText(R.id.tvContent, t.name)
                holder.setOnClickListener(R.id.tvContent, View.OnClickListener {
                    Toast.makeText(requireContext(), "Start Sequence..", Toast.LENGTH_SHORT).show()
                    robot.startSequence(t.id)
                })
            }

        }
    }

    private fun fetchSequences() {
        robot.fetchSequences(object : SequenceCallback {
            override fun onFailure(e: Exception) {
                Timber.e("fetchSequences - onFailure, ${e.message}")
            }

            override fun onSuccess(sequenceList: MutableList<SequenceModel>) {
                Handler(Looper.getMainLooper()).post {
                    Timber.d("fetchSequences - onSuccess, size = ${sequenceList.size}")
                    mSequenceList.clear()
                    mSequenceList.addAll(sequenceList)
                    recyclerViewSequences?.adapter?.notifyDataSetChanged()
                }
            }
        })
    }

    companion object {
        fun newInstance() = SequencesFragment()
    }
}