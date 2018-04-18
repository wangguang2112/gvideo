package com.wang.gvideo.migu.model

/**
 * Date:2018/4/3
 * Description:
 *
 * @author wangguang.
 */
data class SeasonItem(val contId: String, val newName: String){
    fun changePair():Pair<String,String>{
        return Pair(contId,newName)
    }
}