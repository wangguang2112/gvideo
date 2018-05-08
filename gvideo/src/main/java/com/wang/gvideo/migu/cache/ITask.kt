package com.wang.gvideo.migu.cache

/**
 * Date:2018/4/26
 * Description:
 *
 * @author wangguang.
 */
interface ITask {

    enum class STATE{
        STATE_RUNNING,
        STATE_PARPERA,
        STATE_PAUSE,
        STATE_WAITING,
        STATE_ERROR,
        STATE_COMPLETE
    }

    fun getUrl(): String
    fun getImg(): String
    fun getName(): String
    fun state():STATE
    fun contId(): String
    fun nodeId(): String
    fun percent(progress:Long)
    fun size(size:Long)
    fun size():Long
    fun taskId():String
    fun path():String
}