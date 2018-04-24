package com.wang.gvideo.migu.dao

import android.content.Context
import com.wang.gvideo.App
import com.wang.gvideo.common.dao.DataCenter
import com.wang.gvideo.common.dao.IDaoAdapter
import com.wang.gvideo.common.utils.empty
import com.wang.gvideo.common.utils.notEmptyRun
import com.wang.gvideo.migu.dao.model.SeasonInfoDao
import com.wang.gvideo.migu.dao.model.VideoInfoDao
import com.wang.gvideo.migu.dao.model.ViewVideoDao
import com.wang.gvideo.migu.model.AppSearchListItem
import com.wang.gvideo.migu.model.CollectListModel
import com.wang.gvideo.migu.model.VideoInfoModel
import com.wang.gvideo.migu.ui.adapter.SeasonAdapter
import io.realm.Realm
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * Date:2018/4/12
 * Description:
 *
 * @author wangguang.
 */
class CollectManager private constructor() {

    companion object {
        private val context: Context = App.app
        var manager: CollectManager = CollectManager()

    }

    fun <T : Any, S : Any> collectSeasonVideo(data: T, adapter: IDaoAdapter<T, VideoInfoDao>, season: List<S>?, seasonAdapter: IDaoAdapter<S, SeasonInfoDao>?) {
        DataCenter.instance().insert(VideoInfoDao::class, data, adapter)
        season.notEmptyRun {sea->
            seasonAdapter?.let { adapter->
                DataCenter.instance().insertList(SeasonInfoDao::class, sea, adapter)
            }
        }

    }

    fun unCollectSeasonVideo(key:String) {
        DataCenter.instance().delete(VideoInfoDao::class, key)
        DataCenter.instance().deleteList(SeasonInfoDao::class,key)
    }

    fun <T : Any> collectSingleVideo(data: T, adapter: IDaoAdapter<T, VideoInfoDao>) {
        DataCenter.instance().insert(VideoInfoDao::class, data, adapter)
    }

    fun checkExit(contId:String):Boolean{
       return DataCenter.instance().exist(VideoInfoDao::class,contId)
    }

    /**
     * 此处只会取前30条数据 和 前10秒数据
     */
    fun getCollectData(): Observable<List<CollectListModel>> {
        return DataCenter.instance().queryListWithSort(VideoInfoDao::class, "time")
                .flatMap {
                    Observable.from(it)
                }
                .flatMap { node ->
                    DataCenter.instance()
                            .queryWithConditionSort(SeasonInfoDao::class, "", node.contId)
                            .map { seasonList ->
                                CollectListModel(node, seasonList)
                            }

                }
                .buffer(10,TimeUnit.SECONDS,30)
                .observeOn(AndroidSchedulers.mainThread())
    }

}