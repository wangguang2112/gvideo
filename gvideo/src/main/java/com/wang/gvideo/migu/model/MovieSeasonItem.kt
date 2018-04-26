package com.wang.gvideo.migu.model

/**
 * Date:2018/4/3
 * Description:
 *
 * @author wangguang.
 */
data class MovieSeasonItem(val name: String, val param: String){

    companion object {
        fun getContId(contParam: String): String {
            return contParam.split(";")[0].split("=")[1]
        }
    }

    fun changePair():Pair<String,String>{
        return Pair(getContId(param),name)
    }
}