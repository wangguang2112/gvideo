package com.wang.gvideo.migu.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.wang.gvideo.R
import com.wang.gvideo.common.base.BaseDialog
import com.wang.gvideo.common.utils.emptyRun
import com.wang.gvideo.migu.model.VideoInfoModel
import kotlinx.android.synthetic.main.dialog_select_definition_layout.*

/**
 * Date:2018/4/13
 * Description:
 *
 * @author wangguang.
 */
class SelectMovieDefiDialog(ctx: Context) : BaseDialog(ctx, R.style.TranslucentDialog) {

    private var listener: ((Int) -> Unit)? = null
    private var rates: List<Int>? = null
    private var selectRate: Int = 50

    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_select_definition_layout)
        select_de_root_view.setOnClickListener {
            dismiss()
        }
        definition1_contianer.visibility = View.GONE
        definition2_contianer.visibility = View.GONE
        definition3_contianer.visibility = View.GONE
        definition5_contianer.visibility = View.GONE
        rates?.sorted()
        updateSelectedState(selectRate)
        rates?.forEach { pair ->
            if (pair == 50) {
                definition1_contianer.visibility = View.VISIBLE
            }
            if (pair == 75) {
                definition2_contianer.visibility = View.VISIBLE
            }
            if (pair == 150) {
                definition3_contianer.visibility = View.VISIBLE
            }
            if (pair == 200) {
                definition5_contianer.visibility = View.VISIBLE
            }
        }
        definition1.setOnClickListener {
            dismiss()
            if (selectRate != 50) {
                selectRate = 50
                updateSelectedState(selectRate)
                listener?.invoke(selectRate)
            }

        }
        definition2.setOnClickListener {
            dismiss()
            if (selectRate != 75) {
                selectRate = 75
                updateSelectedState(selectRate)
                listener?.invoke(selectRate)
            }
        }
        definition3.setOnClickListener {
            dismiss()
            if (selectRate != 150) {
                selectRate = 150
                updateSelectedState(selectRate)
                listener?.invoke(selectRate)
            }
        }
        definition5.setOnClickListener {
            dismiss()
            if (selectRate != 200) {
                selectRate = 200
                updateSelectedState(selectRate)
                listener?.invoke(selectRate)
            }
        }

    }

    private fun updateSelectedState(rate: Int) {
        when (rate) {
            50 -> definition1.isSelected = true
            75 -> definition2.isSelected = true
            150 -> definition3.isSelected = true
            200 -> definition5.isSelected = true
            else -> definition1.isSelected = true
        }
    }

    fun setSelectListener(listener: (Int) -> Unit): SelectMovieDefiDialog {
        this.listener = listener
        return this
    }

    fun setModel(rate: List<Int>): SelectMovieDefiDialog {
        this.rates = rate
        return this
    }

    fun selectRate(rate: Int): SelectMovieDefiDialog {
        this.selectRate = rate
        return this
    }

}