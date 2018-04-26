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
open class SeasonInfoDao(@PrimaryKey var contId: String, var name: String, var nodeid: String,var position:Int) : RealmObject() {

    constructor() : this("", "", "",0)

    companion object : IDao<SeasonInfoDao> {
        override fun getCollect(): ICollect<SeasonInfoDao> {
            return SeasonInfoCollect()
        }
    }

    class SeasonInfoCollect : ICollect<SeasonInfoDao> {
        val realm = Realm.getDefaultInstance()
        override fun deleteList(condition: String) {
            realm._deleteList(SeasonInfoDao::class.java,"nodeid",condition)
        }

        override fun insertList(models: Iterable<SeasonInfoDao>) {
            realm._insert(SeasonInfoDao::class.java,models)
        }

        override fun queryConditionSorted(sorted: String,condition: String): Observable<List<SeasonInfoDao>> {
            return realm._querySortedCondition(SeasonInfoDao::class.java,sorted,"nodeid",condition, Sort.ASCENDING)
        }

        override fun queryList(): Observable<List<SeasonInfoDao>> {
            return realm._queryList(SeasonInfoDao::class.java)
        }

        override fun insert(model: SeasonInfoDao) {
            realm._insert(SeasonInfoDao::class.java,model)
        }

        override fun delete(key: String) {
            realm._delete(SeasonInfoDao::class.java,"contId",key)
        }

        override fun query(key: String): SeasonInfoDao? {
            return realm._query(SeasonInfoDao::class.java,"contId",key)
        }

        override fun exist(key: String): Boolean {
            return realm._exist(SeasonInfoDao::class.java,"contId",key)
        }

    }

    override fun toString(): String {
        return "SeasonInfoDao(contId='$contId', name='$name', nodeid='$nodeid')"
    }

    override fun equals(other: Any?): Boolean {
        return other is SeasonInfoDao && contId == other.contId && name == name && nodeid == nodeid && position == position
    }

}