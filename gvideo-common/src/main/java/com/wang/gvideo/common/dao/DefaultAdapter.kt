package com.wang.gvideo.common.dao

/**
 * Date:2018/4/15
 * Description:
 *
 * @author wangguang.
 */
class DefaultAdapter<S> : IDaoAdapter<S, S> {
    override fun reAdapt(s: S?): S? {
        return s
    }

    override fun adapt(t: S): S {
        return t
    }
}