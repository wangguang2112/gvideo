package com.wang.gvideo.migu.task

import com.wang.gvideo.common.net.ApiFactory
import rx.Observable


/**
 * Date:2018/6/11
 * Description:
 *
 * @author wangguang.
 */
abstract class BaseTask<T, R> {

    internal fun exec():Observable<R> {

        return execApiWithObaserable(ApiFactory.INSTANCE().createApi(getApi()))
    }

    abstract fun execApiWithObaserable(api: T): Observable<R>

    abstract fun getApi(): Class<T>
}