package com.wang.gvideo.migu.cache

import android.os.Environment
import com.hdl.m3u8.bean.OnDownloadListener
import com.wang.gvideo.App
import com.wang.gvideo.common.dao.DataCenter
import com.wang.gvideo.common.net.OneSubScriber
import com.wang.gvideo.common.utils.has
import com.wang.gvideo.migu.dao.model.CacheTaskDao
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File


/**
 * Date:2018/4/15
 * Description:
 *
 * @author wangguang.
 */
class CacheManager {
    companion object {
        private val manager = CacheManager()
        fun intance(): CacheManager {
            return manager
        }
    }

    init {
        //从数据库中读取缓存数据
        DataCenter.instance().queryWithConditionSort(CacheTaskDao::class, CacheTask::class, "state", "", CacheAdapter())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(object : OneSubScriber<List<CacheTask>>() {
                    override fun onNext(t: List<CacheTask>) {
                        super.onNext(t)
                        taskList.putAll(t.map { Pair(it.taskId, it) }.toMap())
                        taskVideoId.putAll(t.map { Pair(it.contId(), it.taskId) }.toMap())
                        t.forEach {
                            taskCallBack[it.taskId] = mutableListOf()
                        }
                    }
                })
    }

    /** 所有提交过的任务数据结构 */
    private val taskList = mutableMapOf<String, ITask>()
    /** 下载回调 */
    private val taskCallBack = mutableMapOf<String, List<OnDownloadListener>>()
    /** contId 与 TaskId的对应关系 */
    private val taskVideoId = mutableMapOf<String, String>()

    private val taskQueue = TaskQueue()

    init {
        taskQueue.taskChangeListener = { taskId: String, state: ITask.STATE ->
            val t = taskList[taskId]
            if (t is CacheTask) {
                t.state = state
                //事实更新Task
                DataCenter.instance().insert(CacheTaskDao::class, t, CacheAdapter())
            }
        }
    }

    fun allTask(): List<ITask> {
        return taskList.values.toMutableList().sortedBy { it.state() }
    }

    fun submitNewTask(task: ITask) {
        if (checkHasSubmit(task.contId())) {
            taskQueue.resume(task.contId())
        } else {
            val id = task.taskId()
            if (task is CacheTask) {
                task.path = buildPath(task)
                DataCenter.instance().insert(CacheTaskDao::class, task, CacheAdapter())
            }
            taskList[id] = task
            taskCallBack[id] = mutableListOf()
            taskVideoId[task.contId()] = id
            taskQueue.submit(task)

        }
    }


    fun checkHasSubmit(contId: String): Boolean {
        return taskVideoId.containsKey(contId)
    }

    fun checkIsDownload(list: List<String>): Observable<List<Pair<String, Boolean>>> {
        return Observable
                .create<List<Pair<String, Boolean>>> {
                    val result = mutableListOf<Pair<String, Boolean>>()
                    list.forEach {
                        result.add(Pair(it, !taskVideoId.containsKey(it)))
                    }
                    it.onNext(result.toList())
                    it.onCompleted()
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun buildPath(task: ITask): String {
        if (task is CacheTask) {
            val rootPath = if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()) {
                App.app.getExternalFilesDir("videoCache").path
            } else {
                App.app.getFileStreamPath("videoCache").path
            }
            return rootPath + File.separator + task.nodeId() + File.separator + task.contId() + ".mp4"
        }
        return ""
    }

    //  test
    fun addITask(task: ITask) {
        if (task is CacheTask) {
            task.state = ITask.STATE.STATE_COMPLETE
            taskList[task.taskId] = task
            taskCallBack[task.taskId] = mutableListOf()
            taskVideoId[task.contId()] = task.taskId
        }
    }

    fun pause(contId: String) {
        taskVideoId.has(contId) {
            taskQueue.pause(it)
        }

    }

    fun pauseAll() {
        taskQueue.pauseAll()
    }

    fun resume(contId: String) {
        taskVideoId.has(contId) {
            taskQueue.resume(it)
        }
    }

    fun resumeAll() {
        taskQueue.resumeAll()
    }

    fun delete(contId: String) {
        taskVideoId.has(contId) {
            val task = taskList.remove(it)
            task?.let {
                File(it.path()).deleteOnExit()
            }
            taskCallBack.remove(it)
            taskQueue.delete(it)
        }
        taskVideoId.remove(contId)
        DataCenter.instance().delete(CacheTaskDao::class, contId)
    }

    fun desotry() {
        pauseAll()
        taskQueue.desotry()
    }

    fun checkVaild(task: ITask): Boolean {
        return if (task.path().isNotEmpty()) {
            File(task.path()).exists()
        } else {
            false
        }

    }
}