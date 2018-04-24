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
open class ViewVideoDao(@PrimaryKey var contId: String,
                        var name: String,
                        var image: String,
                        var postion: Int,
                        var precent: Int,
                        var time: Long) : RealmObject() {
    constructor() : this("", "", "", 0, 0, 0)

    companion object : IDao<ViewVideoDao> {
        override fun getCollect(): ICollect<ViewVideoDao> {
            return SeasonInfoCollect()
        }
    }

    class SeasonInfoCollect : ICollect<ViewVideoDao> {
        val realm = Realm.getDefaultInstance()

        override fun deleteList(condition: String) {

        }

        override fun insertList(models: Iterable<ViewVideoDao>) {
            realm._insert(ViewVideoDao::class.java, models)
        }

        override fun queryConditionSorted(sorted: String, condition: String): Observable<List<ViewVideoDao>> {
            return realm._querySortedCondition(ViewVideoDao::class.java, sorted, "", condition, Sort.DESCENDING)

        }

        override fun queryList(): Observable<List<ViewVideoDao>> {
            return realm._queryList(ViewVideoDao::class.java)
        }

        override fun insert(model: ViewVideoDao) {
            realm._insert(ViewVideoDao::class.java, model)
        }

        override fun delete(key: String) {
            realm._delete(ViewVideoDao::class.java, "contId", key)
        }

        override fun query(key: String): ViewVideoDao? {
            return realm._query(ViewVideoDao::class.java, "contId", key)
        }

        override fun exist(key: String): Boolean {
            return realm._exist(ViewVideoDao::class.java, "contId", key)
        }

    }

    override fun toString(): String {
        return "ViewVideoDao(contId='$contId', name='$name', image='$image', postion=$postion, precent=$precent)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ViewVideoDao) return false

        if (contId != other.contId) return false
        if (name != other.name) return false
        if (image != other.image) return false
        if (postion != other.postion) return false
        if (precent != other.precent) return false
        if (time != other.time) return false

        return true
    }

}