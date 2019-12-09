package com.robotemi.welcomingbtob.featurelist.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class FeatureListAdapter<T> constructor(
    private val context: Context,
    private val layoutId: Int,
    private val data: List<T>
) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.get(context, layoutId, parent)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        convert(holder, data[position])
    }

    abstract fun convert(holder: ViewHolder, t: T)
}