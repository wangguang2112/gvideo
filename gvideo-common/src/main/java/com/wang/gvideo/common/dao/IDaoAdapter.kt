package com.wang.gvideo.common.dao

/**
 * Date:2018/4/15
 * Description:
 *
 * @author wangguang.
 */
interface IDaoAdapter<T, S> {
    fun adapt(t: T): S
    fun reAdapt(t: S?): T?
}