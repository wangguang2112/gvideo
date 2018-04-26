package com.wang.gvideo.migu.cache

import com.hdl.m3u8.M3U8DownloadTask
import com.hdl.m3u8.bean.OnDownloadListener
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


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

    private val taskList = mutableMapOf<String,ITask>()
    private val taskCallBack = mutableMapOf<String,List<OnDownloadListener>>()
    private val taskVideoId = mutableMapOf<String,String>()
    private val runningTask = mutableMapOf<String,M3U8DownloadTask>()
    private val waitTask = mutableMapOf<String,M3U8DownloadTask>()

    fun submitNewTask(task:ITask){
        if(checkHasSubmit(task.contId())){

        }else{
            val id = generateTaskId()
            taskList[id] = task
            taskCallBack[id] = mutableListOf()
            taskVideoId[task.contId()] = id
            if(runningTask.size >= 3) {
                start(id)
            }
        }
    }

    fun generateTaskId():String{
        return System.currentTimeMillis().toString()
    }

    fun checkHasSubmit(contId:String):Boolean{
        return taskVideoId.containsKey(contId)
    }

    fun resumeTask(contId: String){
        val id = taskVideoId[contId]
        if(runningTask[id] == null){
            id?.let{
                start(it)
            }
        }
    }

    private fun start(id:String){
        val task = taskList[id]
        val callbacks = taskCallBack[id]
        task?.let {taskModel ->
            val realTask = M3U8DownloadTask(id)
            runningTask[id] = realTask
            realTask.download(taskModel.getUrl(), object : OnDownloadListener {
                override fun onSuccess() {
                    callbacks?.forEach {
                        it.onSuccess()
                    }
                }

                override fun onDownloading(itemFileSize: Long, totalTs: Int, curTs: Int) {
                    callbacks?.forEach {
                        it.onDownloading(itemFileSize,totalTs,curTs)
                    }
                }

                override fun onProgress(curLength: Long) {
                    callbacks?.forEach {
                        it.onProgress(curLength)
                    }
                }

                override fun onError(errorMsg: Throwable?) {
                    callbacks?.forEach {
                        it.onError(errorMsg)
                    }
                }

                override fun onStart() {
                   callbacks?.forEach {
                       it.onStart()
                   }
                }

            })
        }

    }

    fun checkIsDownload(list: List<Pair<String, String>>): Observable<List<Pair<String, Boolean>>> {
        return Observable
                .create<List<Pair<String, Boolean>>> {
                    val result = mutableListOf<Pair<String, Boolean>>()
                    list.forEach {
                        result.add(Pair(it.first, true))
                    }
                    result.removeAt(0)
                    result.add(0, Pair(list.first().first, false))
                    it.onNext(result.toList())
                    it.onCompleted()
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun downloadNew(list: List<Pair<String, Boolean>>) {
        val url = ""
        val task = M3U8DownloadTask("1001")
        task.download(url, object : OnDownloadListener {
            override fun onSuccess() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDownloading(itemFileSize: Long, totalTs: Int, curTs: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onProgress(curLength: Long) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onError(errorMsg: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onStart() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }
}