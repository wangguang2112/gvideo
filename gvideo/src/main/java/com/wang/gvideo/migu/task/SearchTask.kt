package com.wang.gvideo.migu.task

import android.util.Log
import com.wang.gvideo.common.net.ApiFactory
import com.wang.gvideo.common.utils.foreach
import com.wang.gvideo.migu.api.SearchListInter
import com.wang.gvideo.migu.model.AppSearchListItem
import com.wang.gvideo.migu.model.AppVideoListInfo
import com.wang.gvideo.migu.model.NewSearchListItem
import com.wang.gvideo.migu.ui.VideoSearchActivity
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Date:2018/6/11
 * Description:
 *
 * @author wangguang.
 */
class SearchTask(val key: String) : BaseTask<SearchListInter, AppVideoListInfo>() {
    override fun execApiWithObaserable(api: SearchListInter): Observable<AppVideoListInfo> {
        Log.d("wangguang", SearchListInter.getSearchURL(key))
        return api.getSearchList(SearchListInter.getSearchURL(key))
                .map {
                    val obj = it.getJSONObject("body")
                    val result = obj.getJSONArray("searchResult")
                    val resultList = mutableListOf<AppSearchListItem>()
                    result.foreach { _, any ->
                        val item = ApiFactory.INSTANCE().getGson()
                                .fromJson(any.toString(), NewSearchListItem::class.java)
                        if(item != null) {
                            val changeItem = NewSearchListItem.changeToOld(item)
                            resultList.add(changeItem)
                        }
                    }
                    AppVideoListInfo(resultList)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }


    override fun getApi(): Class<SearchListInter> {
        return SearchListInter::class.java
    }

}

fun VideoSearchActivity.getNewSearchList(value: String): Observable<AppVideoListInfo> {
    return SearchTask(value).exec()
}