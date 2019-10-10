package com.robotemi.welcomingbtob.featurelist.adapter

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val mViews: SparseArray<View> = SparseArray()

    companion object {
        fun get(context: Context, layoutId: Int, parent: ViewGroup): ViewHolder {
            val itemView = LayoutInflater.from(context).inflate(layoutId, parent, false)
            return ViewHolder(itemView)
        }
    }

    fun <T : View> getView(viewId: Int): T {
        var view: View? = mViews.get(viewId)
        if (view == null) {
            view = itemView.findViewById(viewId)
            mViews.put(viewId, view)
        }
        return (view as T?)!!
    }

    fun setText(viewId: Int, text: CharSequence): ViewHolder {
        getView<TextView>(viewId).text = text
        return this
    }

    fun setOnClickListener(viewId: Int, listener: View.OnClickListener): ViewHolder {
        getView<View>(viewId).setOnClickListener(listener)
        return this
    }
}