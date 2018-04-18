package com.wang.gvideo.common.dao

import rx.Observable

/**
 * Date:2018/4/12
 * Description:数据收集接口
 *
 * @author wangguang.
 */

interface ICollect<S> {
    fun queryList(): Observable<List<S>>
    fun queryConditionSorted(sorted:String,condition:String): Observable<List<S>>
    fun insert(model: S)
    fun insertList(models: Iterable<S>)
    fun deleteList(condition: String)
    fun delete(key: String)
    fun query(key: String): S?
    fun exist(key: String): Boolean
}
