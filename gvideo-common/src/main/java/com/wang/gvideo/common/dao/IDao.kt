package com.wang.gvideo.common.dao

/**
 * Date:2018/4/15
 * Description:
 *
 * @author wangguang.
 */
interface IDao<T> {
    fun getCollect(): ICollect<T>
}