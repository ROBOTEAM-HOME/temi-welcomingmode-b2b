package com.robotemi.welcomingbtob.settings

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.robotemi.welcomingbtob.BaseFragment
import com.robotemi.welcomingbtob.R
import com.robotemi.welcomingbtob.adapter.CommonRvAdapter
import com.robotemi.welcomingbtob.adapter.ViewHolder
import kotlinx.android.synthetic.main.fragment_sub_feature_list.*

class StartingScreenConfigFragment : BaseFragment() {

    private val startingScreenList = mutableListOf<SingleSelectionModel>()

    private val activityCallback by lazy { context as IActivityCallback }

    override fun getLayoutResId() = R.layout.fragment_starting_screen_config

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshUI()
    }

    private fun refreshUI() {
        activityCallback.apply {
            setTitle(getString(R.string.settings_starting_screen))
            setVisibilityOfDone(false)
            setBackClickListener(View.OnClickListener { close() })
        }
        loadStartingScreenList()
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator!!.changeDuration = 0
        recyclerView.adapter = object : CommonRvAdapter<SingleSelectionModel>(
            requireContext(),
            R.layout.item_single_selection,
            startingScreenList
        ) {
            private var selectedPosition: Int = -1

            override fun convert(holder: ViewHolder, t: SingleSelectionModel) {
                t.selected = getSettings().startingScreenSelected == t.name
                if (t.selected) {
                    selectedPosition = holder.adapterPosition
                }
                holder.setText(R.id.tvName, t.name)
                holder.setText(R.id.tvDescription, t.description)
                holder.getView<ImageView>(R.id.ivIsSelected).apply {
                    if (t.selected) {
                        setImageResource(R.drawable.ic_radio_button_on)
                    } else {
                        setImageResource(R.drawable.ic_radio_button_off)
                    }
                }
                holder.setOnClickListener(R.id.itemParentLayout, View.OnClickListener {
                    if (!t.selected) {
                        updateChange(holder.adapterPosition)
                    }
                })
                // Don't show the divider if this is the last item.
                holder.getView<View>(R.id.viewDivider).visibility =
                    if (holder.adapterPosition == startingScreenList.size - 1) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
            }
        }
    }

    /**
     * Add item here if there are new features would like to be the starting screen.
     */
    private fun loadStartingScreenList() {
        startingScreenList.clear()
        val default = SingleSelectionModel(
            getString(R.string.starting_screen_default),
            getString(R.string.starting_screen_default_description),
            false
        )
        val sequence = SingleSelectionModel(
            getString(R.string.starting_screen_sequence),
            getString(R.string.starting_screen_sequence_description),
            false
        )
        startingScreenList.add(default)
        startingScreenList.add(sequence)
    }

    private fun updateChange(position: Int) {
        startingScreenList[position].selected = false
        getSettings().apply {
            startingScreenSelected = startingScreenList[position].name
        }.saveSettings()
    }

    private fun SettingsModel.saveSettings() {
        activityCallback.saveSettings(this) {
            recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    private fun getSettings() = activityCallback.getSettings()

    private fun close() {
        activity?.supportFragmentManager?.popBackStack()
    }

    companion object {
        fun newInstance() = StartingScreenConfigFragment()
    }
}