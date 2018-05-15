package com.wang.gvideo.migu.cache

import android.os.Handler
import android.util.Log
import com.hdl.m3u8.M3U8DownloadTask
import com.hdl.m3u8.bean.OnDownloadListener
import java.io.File
import java.util.*

/**
 * Date:2018/5/3
 * Description:
 *
 * @author wangguang.
 */
class TaskQueue {
    private val STATE_IDLE = 0
    private val STATE_RUNNING = 1

    private val runningTask = mutableMapOf<String, Pair<ITask, M3U8DownloadTask>>()
    private val pauseTask = LinkedList<ITask>()
    private val waitTask = LinkedList<ITask>()
    private val handler = Handler()
    var maxTaskNum = 3
    private var queueState = STATE_IDLE
    var taskChangeListener: ((String, ITask.STATE) -> Unit)? = null

    fun submit(task: ITask) {
        waitTask.add(task)
        taskChangeListener?.invoke(task.taskId(), ITask.STATE.STATE_WAITING)
        resumeQueue()
    }

    private fun resumeQueue() {
        if (runningTask.size >= maxTaskNum) {

        } else {
            runTask(maxTaskNum - runningTask.size)
        }
        if (runningTask.isNotEmpty()) {
            queueState = STATE_RUNNING
        } else {
            queueState = STATE_IDLE
        }
    }

    /**
     * 将新的TASK添加入队列
     */
    private fun runTask(size: Int) {
        if (size <= 0) {
            return
        }
        val task = if (waitTask.isNotEmpty()) {
            waitTask.poll()
        } else return
        createAndRun(task)
        runTask(size - 1)
    }

    private fun createAndRun(task:ITask){
        val realTask = M3U8DownloadTask(task.taskId())
        realTask.saveFilePath = task.path()
        realTask.isClearTempDir = true
        realTask.download(task.getUrl(), object : OnDownloadListener {
            override fun onSuccess() {
                runningTask.remove(task.taskId())
                taskChangeListener?.invoke(task.taskId(), ITask.STATE.STATE_COMPLETE)
                val file = File(task.path())
                if(file.exists()){
                    task.size(file.length())
                }
                resumeQueue()
            }

            override fun onDownloading(itemFileSize: Long, totalTs: Int, curTs: Int) {
                Log.d("wangguang","itemFileSize:$itemFileSize,totalTs:$totalTs")
                handler.post {
                    task.size(totalTs.toLong())
                }
            }

            override fun onProgress(curLength: Long,curTs:Int) {
                 handler.post {
                     task.percent(curTs.toLong())
                 }
            }

            override fun onError(errorMsg: Throwable?) {
                errorMsg?.printStackTrace()
                taskChangeListener?.invoke(task.taskId(), ITask.STATE.STATE_ERROR)
            }

            override fun onStart() {
                runningTask[task.taskId()] = Pair(task, realTask)
                taskChangeListener?.invoke(task.taskId(), ITask.STATE.STATE_RUNNING)
            }

        })
    }

    fun pause(taskId: String) {
        val task = pauseTask.find { it.taskId() == taskId }
        val runT = runningTask[taskId]
        if(task == null && runT != null){
            runT.second.stop()
            pauseTask.add(runT.first)
            runningTask.remove(taskId)
            taskChangeListener?.invoke(taskId, ITask.STATE.STATE_PAUSE)
            resumeQueue()
        }else if(task == null){
            val waitT = waitTask.find { it.taskId() == taskId }
            if(waitT != null){
                waitTask.remove(waitT)
                pauseTask.add(waitT)
                taskChangeListener?.invoke(taskId, ITask.STATE.STATE_PAUSE)
            }

        }
    }

    fun pauseAll() {
        runningTask.forEach { it ->
            it.value.second.stop()
            pauseTask.add(it.value.first)
            taskChangeListener?.invoke(it.key, ITask.STATE.STATE_PAUSE)
        }
        waitTask.forEach { it ->
            pauseTask.add(it)
            taskChangeListener?.invoke(it.taskId(), ITask.STATE.STATE_PAUSE)
        }
        pauseTask.forEach {
            runningTask.remove(it.contId())
            waitTask.remove(it)
        }

    }

    fun recovery(task:List<ITask>){
        task.forEach {
            if(it.state() == ITask.STATE.STATE_PAUSE){
                pauseTask.add(it)
            }else if(it.state() == ITask.STATE.STATE_WAITING){
                waitTask.add(it)
            }else if(it.state() == ITask.STATE.STATE_RUNNING){
                waitTask.add(it)
                taskChangeListener?.invoke(it.taskId(), ITask.STATE.STATE_WAITING)
                resumeQueue()
            }
        }
    }
    fun resume(taskId:String){
        val task = pauseTask.find { it.taskId() == taskId }
        if(task != null){
            pauseTask.remove(task)
            waitTask.addFirst(task)
            taskChangeListener?.invoke(taskId,ITask.STATE.STATE_WAITING)
            resumeQueue()
        }
    }

    fun resumeAll(){
        pauseTask.forEach {
            waitTask.add(it)
            taskChangeListener?.invoke(it.taskId(),ITask.STATE.STATE_WAITING)
        }
        resumeQueue()
    }

    fun delete(taskId: String){
        if(runningTask.containsKey(taskId)){
            val (t,rt) = runningTask[taskId]!!
            rt.stop()
            runningTask.remove(taskId)
        }else {
            var task = pauseTask.find { it.taskId() == taskId }
            if (task != null) {
                pauseTask.remove(task)
            } else {
                task = waitTask.find { it.taskId() == taskId }
                waitTask.remove(task)
            }
        }
    }

    fun desotry(){
        handler.removeCallbacksAndMessages(null)
    }
}