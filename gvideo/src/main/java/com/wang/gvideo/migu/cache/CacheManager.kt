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