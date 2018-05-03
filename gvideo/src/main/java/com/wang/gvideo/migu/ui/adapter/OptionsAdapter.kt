package com.wang.gvideo.migu.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.wang.gvideo.R
import com.wang.gvideo.migu.ui.view.ListItemView

/**
 * Date:2018/5/3
 * Description:
 *
 * @author wangguang.
 */
class OptionsAdapter(val context: Context,val list:List<Pair<String,Int>>) : ListItemView.ViewAdapter<Pair<String,Int>>(){
    private val inflater = LayoutInflater.from(context)
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Pair<String, Int> {
        return list[position]
    }

    override fun getView(position: Int, parent: ViewGroup?): View {
        var (n,i) = list[position]
        val view =inflater.inflate(R.layout.options_list_item,parent)
        val image = view.findViewById<ImageView>(R.id.options_list_icon)
        val name = view.findViewById<TextView>(R.id.options_list_name)
        image.setImageResource(i)
        name.text = n
        return view
    }

    override fun updateView(position: Int) {

    }
}