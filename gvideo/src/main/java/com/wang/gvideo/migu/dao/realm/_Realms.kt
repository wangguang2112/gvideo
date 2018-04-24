package com.wang.gvideo.migu.dao.realm

import com.wang.gvideo.common.utils.nil
import io.realm.Realm
import io.realm.RealmObject
import io.realm.Sort
import rx.Observable

/**
 * Date:2018/4/15
 * Description: Realm扩展工具类
 *
 * 方便实体类书写的扩展方法
 * @author wangguang.
 */

/**
 * 查询全部
 * @param cls Dao实体类
 */

fun <T : RealmObject> Realm._queryList(cls: Class<T>): Observable<List<T>> {
    return this.where(cls)
            .findAllAsync()
            .asObservable()
            .filter { results -> results.isLoaded }
            .first()
            .map { element ->
                element?.let {
                    this.copyFromRealm(element).toList()
                }.nil {
                            listOf<T>()
                        }

            }
}


/**
 * 查询全部递减
 * @param cls Dao实体类
 */

fun <T : RealmObject> Realm._querySortedCondition(cls: Class<T>, sort: String, conditionKey: String, condition: String, sortOrder: Sort): Observable<List<T>> {
    val middle = if (condition.isNotEmpty() && conditionKey.isNotEmpty()) {
        this.where(cls).equalTo(conditionKey, condition)
    } else {
        this.where(cls)
    }
    val last = if (sort.isNotEmpty()) {
        middle.findAllSorted(sort, sortOrder)
    } else {
        middle.findAll()
    }
    return last.asObservable()
            .filter { results -> results.isLoaded }
            .first()
            .map { element ->
                element?.let {
                    this.copyFromRealm(element).toList()
                }.nil {
                            listOf<T>()
                        }

            }
}


/**
 * 插入
 * @param cls Dao实体类
 */
fun <T : RealmObject> Realm._insert(cls: Class<T>, model: T) {
    executeTransaction { realm ->
        realm.copyToRealmOrUpdate(model)
    }
}

/**
 * 插入列表
 * @param cls Dao实体类
 */
fun <T : RealmObject> Realm._insert(cls: Class<T>, model: Iterable<T>) {
    executeTransaction { realm ->
        realm.copyToRealmOrUpdate(model)
    }
}

/**
 * 删除
 * @param cls Dao实体类
 */
fun <T : RealmObject> Realm._delete(cls: Class<T>, keyName: String, key: String) {
    executeTransaction { realm ->
        realm.where(cls).equalTo(keyName, key).findFirst().deleteFromRealm()
    }
}

/**
 * 删除
 * @param cls Dao实体类
 */
fun <T : RealmObject> Realm._deleteList(cls: Class<T>,conditionName:String,condition:String) {
    executeTransaction { realm ->
        realm.where(cls).equalTo(conditionName, condition).findAll().deleteAllFromRealm()
    }
}


/**
 * 同步查询
 * @param cls Dao实体类
 */
fun <T : RealmObject> Realm._query(cls: Class<T>, keyName: String, key: String): T? {
    val result = this.where(cls).equalTo(keyName, key).findFirst()
    return result?.let {
        this.copyFromRealm(it)
    }
}

/**
 * 同步查询是否已有
 * @param cls Dao实体类
 */
fun <T : RealmObject> Realm._exist(cls: Class<T>, keyName: String, key: String): Boolean {
    return this.where(cls).contains(keyName, key).findFirst() != null
}


/**
 * 异步查询
 * @param cls Dao实体类
 */
fun <T : RealmObject> Realm._queryAsyc(cls: Class<T>, keyName: String, key: String): Observable<T> {
    var result = this.where(cls).equalTo(keyName, key)
            .findFirstAsync()
    return if (result is RealmObject) {
        result.asObservable<T>()
                .filter { results -> results.isLoaded }
                .first()
                .map { mapIt ->
                    mapIt?.let {
                        this.copyFromRealm(it)
                    }
                }
    } else {
        Observable.empty<T>()
    }
}