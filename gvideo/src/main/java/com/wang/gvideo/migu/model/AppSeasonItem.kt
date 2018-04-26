package com.wang.gvideo.migu.model

import com.wang.gvideo.common.dao.IDaoAdapter
import com.wang.gvideo.migu.dao.model.SeasonInfoDao
import com.wang.gvideo.migu.dao.model.VideoInfoDao

/**
 * Date:2018/4/3
 * Description:
 *
 * @author wangguang.
 */
data class AppSeasonItem(val param: String, val name: String, val img: String){

    var position = 0
    companion object {
        fun getDaoAdapter(): IDaoAdapter<AppSeasonItem, SeasonInfoDao> {
            return object : IDaoAdapter<AppSeasonItem, SeasonInfoDao> {
                override fun adapt(t: AppSeasonItem): SeasonInfoDao {
                    return SeasonInfoDao(AppSearchListItem.getContId(t.param),t.name,t.getNodeId(),t.position)
                }

                override fun reAdapt(t: SeasonInfoDao?): AppSeasonItem? {
                    return null
                }

            }
        }
    }
    fun changePair():Pair<String,String>{
        return Pair(AppSearchListItem.getContId(param),name)
    }

    fun getNodeId():String {
        return param.split(";")[1].split("=")[1]
    }
}