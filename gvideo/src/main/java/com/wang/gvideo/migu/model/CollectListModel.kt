package com.wang.gvideo.migu.model

import com.wang.gvideo.migu.dao.model.SeasonInfoDao
import com.wang.gvideo.migu.dao.model.VideoInfoDao

/**
 * Date:2018/4/17
 * Description:
 *
 * @author wangguang.
 */
data class CollectListModel(var contId: String,
                            var name: String,
                            var imageH: String,
                            var subList: List<SeasonItem>,
                            var seasonNum: String,
                            var contType: String,
                            var durtion: Long,
                            var time: Long) {
    constructor(parent: VideoInfoDao, seasonList: List<SeasonInfoDao>) :
            this(parent.contId, parent.name, parent.image,
                    seasonList.map { dao -> SeasonItem(dao.contId, dao.name) },
                    parent.extend2, parent.extend1, parent.extend3, parent.time)
}