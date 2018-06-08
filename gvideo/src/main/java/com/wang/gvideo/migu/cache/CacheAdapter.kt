package com.wang.gvideo.migu.cache

import com.wang.gvideo.common.dao.IDaoAdapter
import com.wang.gvideo.migu.dao.model.CacheTaskDao

/**
 * Date:2018/5/8
 * Description:
 *
 * @author wangguang.
 */
class CacheAdapter : IDaoAdapter<CacheTask, CacheTaskDao> {
    override fun adapt(t: CacheTask): CacheTaskDao {
        return CacheTaskDao(t.contId(), t.getUrl(), t.getImg(), t.getName(), t.nodeId(), getTaskState(t.state),
                t.size, t.percent, t.ts, t.path, t.taskId,t.tempFile?:"")
    }

    override fun reAdapt(t: CacheTaskDao?): CacheTask? {
        t?.let {
            val task = CacheTask(it.url, it.img, it.name, it.contId, it.nodeId)
            task.percent = it.percent
            task.state = getState(it.state)
            task.size = it.size
            task.ts = it.ts
            task.path = it.path
            task.taskId = it.taskId
            task.tempFile = it.tempFile
            return task
        }
        return null
    }

    fun getState(state: Int): ITask.STATE {
        return when (state) {
            0 -> ITask.STATE.STATE_RUNNING
            1 -> ITask.STATE.STATE_PARPERA
            2 -> ITask.STATE.STATE_PAUSE
            3 -> ITask.STATE.STATE_WAITING
            4 -> ITask.STATE.STATE_ERROR
            5 -> ITask.STATE.STATE_COMPLETE
            else -> ITask.STATE.STATE_ERROR
        }
    }

    fun getTaskState(state: ITask.STATE): Int {
        return when (state) {
            ITask.STATE.STATE_RUNNING -> 0
            ITask.STATE.STATE_PARPERA -> 1
            ITask.STATE.STATE_PAUSE -> 2
            ITask.STATE.STATE_WAITING -> 3
            ITask.STATE.STATE_ERROR -> 4
            ITask.STATE.STATE_COMPLETE -> 5
            else -> 4
        }
    }

}