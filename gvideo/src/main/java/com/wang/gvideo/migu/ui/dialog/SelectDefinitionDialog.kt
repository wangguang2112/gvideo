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
class SelectDefinitionDialog(ctx: Context) : BaseDialog(ctx, R.style.TranslucentDialog) {

    var model: VideoInfoModel? = null
    private var listener: ((Int,String) ->Unit)? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_select_definition_layout)
        select_de_root_view.setOnClickListener {
            dismiss()
        }
        model?.apply {
            getLow().emptyRun {
                definition1_contianer.visibility = View.GONE
            }
            getHigh().emptyRun {
                definition2_contianer.visibility = View.GONE
            }
            get720().emptyRun {
                definition3_contianer.visibility = View.GONE
            }
            get1080().emptyRun {
                definition5_contianer.visibility = View.GONE
            }
            updateSelectedState(definPos?:0)
            definition1.setOnClickListener {
                dismiss()
                if(definPos != 0){
                    definPos = 0
                    updateSelectedState(definPos)
                    listener?.invoke(definPos,getLow())
                }

            }
            definition2.setOnClickListener {
                dismiss()
                if(definPos != 1) {
                    definPos = 1
                    updateSelectedState(definPos)
                    listener?.invoke(definPos, getHigh())
                }
            }
            definition3.setOnClickListener {
                dismiss()
                if(definPos != 2) {
                    definPos = 2
                    updateSelectedState(definPos)
                    listener?.invoke(definPos, get720())
                }
            }
            definition5.setOnClickListener {
                dismiss()
                if(definPos != 3) {
                    definPos = 3
                    updateSelectedState(definPos)
                    listener?.invoke(definPos, get1080())
                }
            }
        }
    }

    private fun updateSelectedState(pos:Int){
        when (pos) {
            0 -> definition1.isSelected = true
            1 -> definition2.isSelected = true
            2 -> definition3.isSelected = true
            3 -> definition5.isSelected = true
            else -> definition1.isSelected = true
        }
    }
    fun setSelectListener(listener:(Int,String)->Unit): SelectDefinitionDialog {
        this.listener = listener
        return this
    }

    fun setModel(model: VideoInfoModel?): SelectDefinitionDialog {
        this.model = model
        return this
    }

}