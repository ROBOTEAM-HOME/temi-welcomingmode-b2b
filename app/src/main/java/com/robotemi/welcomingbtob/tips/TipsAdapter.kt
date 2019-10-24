package com.robotemi.welcomingbtob.tips

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.widget.AppCompatTextView
import com.robotemi.welcomingbtob.R
import java.security.SecureRandom
import java.util.*

class TipsAdapter constructor(private val context: Context) : BaseAdapter() {
    private val random = SecureRandom()

    private var tipsList: List<String>? = ArrayList()

    fun updateList(tipsList: List<String>) {
        this.tipsList = tipsList
        notifyDataSetChanged()
    }

    override fun getCount() = if (tipsList == null) 0 else tipsList!!.size

    override fun getItem(position: Int) = tipsList!![position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertViewVar = convertView
        if (convertViewVar == null) {
            convertViewVar = LayoutInflater.from(context).inflate(R.layout.tips_item, null)
        }

        val tip = tipsList!![random.nextInt(tipsList!!.size)]

        (convertViewVar as AppCompatTextView).text = tip
        return convertViewVar
    }
}