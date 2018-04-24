package com.wang.gvideo.migu.dao.model

import com.wang.gvideo.common.dao.ICollect
import com.wang.gvideo.common.dao.IDao
import com.wang.gvideo.migu.dao.realm.*
import io.realm.Realm
import io.realm.RealmObject
import io.realm.Sort
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import rx.Observable

/**
 * Date:2018/4/14
 * Description:
 *
 * @author wangguang.
 */
open class VideoInfoDao(@PrimaryKey var contId: String,
                        var name: String,
                        var image: String,
                        var time: Long,
                        //存储类型
                        var extend1:String,
                        //存储级数
                        var extend2:String,
                        //存储时长
                        var extend3:Long) : RealmObject() {
    constructor() : this("", "", "", 0,"","",0)
    companion object : IDao<VideoInfoDao> {
        override fun getCollect(): ICollect<VideoInfoDao> {
            return SeasonInfoCollect()
        }
    }

    class SeasonInfoCollect : ICollect<VideoInfoDao> {
        val realm = Realm.getDefaultInstance()
        override fun deleteList(condition: String) {

        }
        override fun insertList(models: Iterable<VideoInfoDao>) {
            return realm._insert(VideoInfoDao::class.java,models)
        }
        override fun queryConditionSorted(sorted: String,condition: String): Observable<List<VideoInfoDao>> {
            return realm._querySortedCondition(VideoInfoDao::class.java,sorted,"",condition, Sort.DESCENDING)
        }
        override fun queryList(): Observable<List<VideoInfoDao>> {
            return realm._queryList(VideoInfoDao::class.java)
        }

        override fun insert(model: VideoInfoDao) {
            realm._insert(VideoInfoDao::class.java,model)
        }

        override fun delete(key: String) {
            realm._delete(VideoInfoDao::class.java,"contId",key)
        }

        override fun query(key: String): VideoInfoDao? {
            return realm._query(VideoInfoDao::class.java,"contId",key)
        }

        override fun exist(key: String): Boolean {
            return realm._exist(VideoInfoDao::class.java,"contId",key)
        }

    }

    override fun toString(): String {
        return "VideoInfoDao(contId='$contId', name='$name', image='$image', time='$time')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VideoInfoDao) return false

        if (contId != other.contId) return false
        if (name != other.name) return false
        if (image != other.image) return false
        if (time != other.time) return false
        if (extend1 != other.extend1) return false
        if (extend2 != other.extend2) return false
        if (extend3 != other.extend3) return false

        return true
    }


}