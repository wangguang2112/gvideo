package com.wang.gvideo.migu.model

import java.util.*
import kotlin.Comparator

/**
 * Date:2018/4/3
 * Description:
 *
 * @author wangguang.
 */
data class MovieInfoModel(val titleName: String,
                          val nodeName: String,
                          val srcH: String,
                          val DisplayName: String,
                          val contentId: String,
                          val supportRatePlay: String,
                          var playUrl: String,
                          val subList:List<MovieSeasonItem>,
                          var rate: Int) {
    companion object {
        fun definitionName(rate: Int): String {
            return when (rate) {
                50 -> "标清"
                75 -> "高清"
                150 -> "720P"
                200 -> "1080P"
                else -> "标清"
            }
        }
    }

    fun selectSupportRate(requestRate:Int):Int{
        val rateStr = supportRatePlay.split(";")
        val supportRate = mutableListOf<Int>()
        rateStr.forEach {
            supportRate.add(it.split("_")[0].toInt())
        }
        //倒叙排列
        supportRate.sortWith(Comparator { p0, p1 ->
            return@Comparator p1 - p0})
        supportRate.forEach {
            if(requestRate == it){
                return@selectSupportRate requestRate
            }else if(requestRate > it){
                return@selectSupportRate it
            }
        }
        return 50
    }

    fun getSupportRate():List<Int>{
        val rateStr = supportRatePlay.split(";")
        val supportRate = mutableListOf<Int>()
        rateStr.forEach {
            supportRate.add(it.split("_")[0].toInt())
        }
        return supportRate
    }

    fun getSeasonPairs():List<Pair<String,String>>{
        val result = mutableListOf<Pair<String,String>>()
        subList.forEach {
            result.add(it.changePair())
        }
        return result
    }
}