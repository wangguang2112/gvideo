package com.wang.gvideo.migu.model

import com.wang.gvideo.common.dao.IDaoAdapter
import com.wang.gvideo.migu.dao.model.VideoInfoDao


/**
 * Date:2018/4/3
 * Description:
 *
 * @author wangguang.
 *
 */
data class AppSearchListItem(val contName: String,
                             val actor: String,
                             val img: String,
                             val hImg: String,
                             val isVip:Int,
                             val subList: List<AppSeasonItem>,
        // 4 表示电影
        // 0 表示一般视频
        // 1 表示电视剧
        // 2 表示合集
                             val contentType: String,
        //contentId=624310186;nodeId=;objType=video;
                             val contParam: String,
                             val prop: String,
                             val contType: Int,
                             var isCollect: Boolean = false) {
    companion object {
        fun getTypeName(type: Int): String = when (type) {
            0 -> "视频"
            1 -> "电视剧"
            2 -> "合集"
            4 -> "电影"
            else -> "未知"
        }

        fun getContId(contParam: String): String {
            return contParam.split(";")[0].split("=")[1]
        }

        fun getDaoAdapter(): IDaoAdapter<AppSearchListItem, VideoInfoDao> {
            return object : IDaoAdapter<AppSearchListItem, VideoInfoDao> {
                override fun adapt(t: AppSearchListItem): VideoInfoDao {
                    return VideoInfoDao(AppSearchListItem.getContId(t.contParam), t.contName, t.hImg,
                            System.currentTimeMillis(),
                            getTypeName(t.contType),
                            (if (t.subList != null) {
                                t.subList.size
                            } else {
                                0
                            }).toString(),
                            0)
                }

                override fun reAdapt(t: VideoInfoDao?): AppSearchListItem? {
                    return null
                }

            }
        }
    }

    fun getSeasonPairs(): List<Pair<String, String>> {
        val result = mutableListOf<Pair<String, String>>()
        subList.forEach {
            result.add(it.changePair())
        }
        return result
    }

}