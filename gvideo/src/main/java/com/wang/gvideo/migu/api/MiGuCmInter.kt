package com.wang.gvideo.migu.api

import com.wang.gvideo.common.net.HOST
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query
import rx.Observable

/**
 * Date:2018/4/3
 * Description:
 *
 * @author wangguang.
 */
@HOST("http://migu.cmvideo.cn")
interface MiGuCmInter {

    /**
     * playerType 4
     * res HDPI
     * clientId 3bbbe95333920f0a094f5ce052b3d168
     * filterType 0 1 2 3
     *
     */
    @FormUrlEncoded
    @POST("/clt50/publish/clt/resource/miguvideo4/search/characterData3.jsp")
    fun getSearchList(@Query("playerType") playerType: Int,
                      @Query("res") res: String,
                      @Query("clientId") clientId: String,
                      @Query("filterType") filterType: Int,
                      @Field("k") k: String,
                      @Field("pageIdx") pageIdx: Int): Observable<String>
}