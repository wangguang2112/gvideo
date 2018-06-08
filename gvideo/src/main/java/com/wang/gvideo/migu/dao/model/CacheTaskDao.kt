package com.wang.gvideo.migu.dao.model

import com.wang.gvideo.common.dao.ICollect
import com.wang.gvideo.common.dao.IDao
import com.wang.gvideo.migu.dao.realm.*
import io.realm.Realm
import io.realm.RealmObject
import io.realm.Sort
import io.realm.annotations.PrimaryKey
import rx.Observable

/**
 * Date:2018/5/3
 * Description:
 *
 * @author wangguang.
 */
open class CacheTaskDao(@PrimaryKey var contId: String,
                        var url: String,
                        var img: String,
                        var name: String,
                        var nodeId: String,
                        var state: Int,
                        var size: Long,
                        var percent: Int,
                        var ts: Int,
                        var path: String,
                        var taskId: String,
                        var tempFile:String) : RealmObject() {
    constructor() : this("", "", "", "", "", 0, 0, 0, 0, "", "","")

    companion object : IDao<CacheTaskDao> {
        override fun getCollect(): ICollect<CacheTaskDao> {
            return CacheCollect()
        }


    }

    class CacheCollect : ICollect<CacheTaskDao> {
        val realm = Realm.getDefaultInstance()

        override fun deleteList(condition: String) {

        }

        override fun insertList(models: Iterable<CacheTaskDao>) {
            realm._insert(CacheTaskDao::class.java, models)
        }

        override fun queryConditionSorted(sorted: String, condition: String): Observable<List<CacheTaskDao>> {
            return realm._querySortedCondition(CacheTaskDao::class.java, sorted, "", condition, Sort.DESCENDING)

        }

        override fun queryList(): Observable<List<CacheTaskDao>> {
            return realm._queryList(CacheTaskDao::class.java)
        }

        override fun insert(model: CacheTaskDao) {
            realm._insert(CacheTaskDao::class.java, model)
        }

        override fun delete(key: String) {
            realm._delete(CacheTaskDao::class.java, "contId", key)
        }

        override fun query(key: String): CacheTaskDao? {
            return realm._query(CacheTaskDao::class.java, "contId", key)
        }

        override fun exist(key: String): Boolean {
            return realm._exist(CacheTaskDao::class.java, "contId", key)
        }

    }

}