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
 * Date:2018/4/14
 * Description:
 *
 * @author wangguang.
 */
open class RecommondDao(@PrimaryKey var contentId: String,
                        var contentName: String,
                        var img: String,
                        var contentScore: String,
                        var movieType:String,
                        var time: Long) : RealmObject() {
    constructor() : this("", "", "", "", "", 0)

    companion object : IDao<RecommondDao> {
        override fun getCollect(): ICollect<RecommondDao> {
            return RecommondCollect()
        }
    }

    class RecommondCollect : ICollect<RecommondDao> {
        val realm = Realm.getDefaultInstance()

        override fun deleteList(condition: String) {

        }

        override fun insertList(models: Iterable<RecommondDao>) {
            realm._insert(RecommondDao::class.java, models)
        }

        override fun queryConditionSorted(sorted: String, condition: String): Observable<List<RecommondDao>> {
            return realm._querySortedCondition(RecommondDao::class.java, sorted, "", condition, Sort.DESCENDING)

        }

        override fun queryList(): Observable<List<RecommondDao>> {
            return realm._queryList(RecommondDao::class.java)
        }

        override fun insert(model: RecommondDao) {
            realm._insert(RecommondDao::class.java, model)
        }

        override fun delete(key: String) {
            realm._delete(RecommondDao::class.java, "contentId", key)
        }

        override fun query(key: String): RecommondDao? {
            return realm._query(RecommondDao::class.java, "contentId", key)
        }

        override fun exist(key: String): Boolean {
            return realm._exist(RecommondDao::class.java, "contentId", key)
        }

    }



}