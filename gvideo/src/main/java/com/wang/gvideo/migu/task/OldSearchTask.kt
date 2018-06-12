package com.wang.gvideo.migu.task

import com.wang.gvideo.App
import com.wang.gvideo.common.net.ApiFactory
import com.wang.gvideo.common.utils.DensityUtil
import com.wang.gvideo.common.utils.notEmptyRun
import com.wang.gvideo.migu.api.MiGuCmInter
import com.wang.gvideo.migu.constant.Config
import com.wang.gvideo.migu.dao.CollectManager
import com.wang.gvideo.migu.model.AppSearchListItem
import com.wang.gvideo.migu.model.AppVideoListInfo
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
class OldSearchTask(val value: String) : BaseTask<MiGuCmInter, AppVideoListInfo>() {
    override fun getApi(): Class<MiGuCmInter> {
        return MiGuCmInter::class.java
    }

    override fun execApiWithObaserable(api: MiGuCmInter): Observable<AppVideoListInfo> {
        return api.getSearchList(4, DensityUtil.getDensityStr(App.app), Config.CLIENT_ID, 0, value, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    val result = it.replace(Regex(",[\\s]*\\}|,[\\s]*\\]")) {
                        if (it.value.takeLast(1) == "}") {
                            "}"
                        } else {
                            "]"
                        }
                    }
                    ApiFactory.INSTANCE().getGson().fromJson(result, AppVideoListInfo::class.java)
                }
                .map {
                    it.apply {
                        it.searchresult2.forEach {
                            it.isCollect = CollectManager.manager.checkExit(AppSearchListItem.getContId(it.contParam))
                            it.subList.notEmptyRun { it.forEachIndexed { index, appSeasonItem -> appSeasonItem.position = index } }
                        }
                    }
                }
    }


}

fun VideoSearchActivity.getSearchList(value: String): Observable<AppVideoListInfo> {
    return OldSearchTask(value).exec()

}

