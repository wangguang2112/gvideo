package com.wang.gvideo.migu.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wang.gvideo.R
import com.wang.gvideo.common.utils.safeGet

/**
 * Date:2018/4/3
 * Description:
 *
 * @author wangguang.
 */
class SingleAdapter(val context: Context, var data: MutableList<String>,
                    val listener: ((Int, String) -> Unit) = { _, _ -> }) : RecyclerView.Adapter<SingleAdapter.SingleTextHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SingleTextHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.single_text_item, parent, false)
        val holder = SingleTextHolder(view)
        holder.titleView = view.findViewById(R.id.single_text)
        return holder
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: SingleTextHolder?, position: Int) {
        holder?.titleView?.text = data.safeGet(position) { "" }
        holder?.titleView?.setOnClickListener {
            listener?.invoke(position, data[position])
        }
    }


    class SingleTextHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        var titleView: TextView? = null
    }
}

