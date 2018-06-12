package com.wang.gvideo.migu.api

import com.wang.gvideo.common.net.HOST
import com.wang.gvideo.migu.model.VideoInfoModel
import org.json.JSONObject
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

/**
 * Date:2018/4/3
 * Description:
 *
 * @author wangguang.
 */
@HOST("http://wangguang2112.github.io")
interface GithubIoInter {


    @GET("/vipinfo.json")
    fun getCookie(): Observable<JSONObject>
}