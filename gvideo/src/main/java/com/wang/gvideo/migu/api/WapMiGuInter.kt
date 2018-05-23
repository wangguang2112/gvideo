package com.wang.gvideo.migu.api

import com.wang.gvideo.common.net.HOST
import com.wang.gvideo.migu.model.VideoInfoModel
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

/**
 * Date:2018/4/3
 * Description:
 *
 * @author wangguang.
 */
@HOST("http://www.miguvideo.com")
interface WapMiGuInter {


//    @GET("/wap/resource/pc/data/detailData.jsp")
    //接口更换
    @GET("/wap/resource/pc/data/miguData.jsp")
    fun getVideoInfo(@Query("cid") cid: String): Observable<List<VideoInfoModel>>

    /*      getSearchList("", value, "1", "1002601,1002581")
            val content: List<SearchListModel>
            data class SearchListModel(val title: String,
                             val zy: String,
                             val imgsrcV: String,
                             val childCont: List<SeasonItem>,
                             val DisplayName: String,
                             val lx: String,
                             val dq: String,
                             val cententId: String,
                             val recordPostion: Int)*/
    /* @FormUrlEncoded
     @POST("/wap/resource/pc/data/searchResultData.jsp")
     fun getSearchList(@Query("order") order: String,
                       @Field("searchValue") searchValue: String,
                       @Field("currentPage") currentPage: String,
                       @Field("searchLimit") searchLimit: String): Observable<VideoListModel>*/
}