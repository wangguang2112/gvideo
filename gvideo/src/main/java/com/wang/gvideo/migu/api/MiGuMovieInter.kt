package com.wang.gvideo.migu.api

import com.wang.gvideo.common.net.HOST
import retrofit2.http.*
import rx.Observable

/**
 * Date:2018/4/3
 * Description:
 *
 * @author wangguang.
 */
@HOST("http://migumovie.lovev.com")
interface MiGuMovieInter {

    /**
     * contentId
     * rate 200
     * playerType 4
     * res EXDPI
     * filterType 3
     * nt 4
     * cid 50142
     * jid 08a6634ab91e72ef85021cfe314a2fde
     * clientId  3db191e8cfe3acd0c4abb76fcb3b9486
     */
    @GET("/iworld/publish/clt/resource/migumovie4/player/playerData.jsp")
    fun getMovieData(@Query("contentId") contentId: String,
                     @Query("rate") rate: Int = 50,
                     @Query("playerType") playerType: Int = 4,
                     @Query("res") res: String = "EXDPI",
                     @Query("clientId") clientId: String = "3db191e8cfe3acd0c4abb76fcb3b9486",
                     @Query("filterType") filterType: Int = 3,
                     @Query("cid") cid: String = "50142",
                     @Query("jid") jid: String = "08a6634ab91e72ef85021cfe314a2fde",
                     @Query("nt") nt: Int = 4): Observable<String>


    @POST("/iworld/publish/clt/resource/migumovie4/search/searchData.jsp")
    @FormUrlEncoded
    fun getMovieSearchData(
            @Field("keyWord") keyWord: String = "",
            @Field("sdkVersion") sdkVersion: String = "64.00.00.14",
            @Query("playerType") playerType: Int = 4,
            @Query("res") res: String = "EXDPI",
            @Query("clientId") clientId: String = "3db191e8cfe3acd0c4abb76fcb3b9486",
            @Query("filterType") filterType: Int = 3,
            @Query("cid") cid: String = "50142",
            @Query("nt") nt: Int = 4): Observable<String>


    @POST("/iworld/publish/clt/resource/migumovie4/search/searchData.jsp")
    @FormUrlEncoded
    fun getMovieFilteData(
            @Field("currentPage") currentPage: Int = 1,
            @Field("mediaType") mediaType: String = "",
            @Field("order") order: String = "最热",
            @Field("mediaYear") mediaYear: String = "",
            @Field("mediaArea") mediaArea: String = "",
            @Field("pageSize") pageSize: String = "30",
            @Field("from") from: String = "videoBank",
            @Query("playerType") playerType: Int = 4,
            @Query("sdkVersion") sdkVersion: String = "64.00.00.14",
            @Query("res") res: String = "EXDPI",
            @Query("clientId") clientId: String = "3db191e8cfe3acd0c4abb76fcb3b9486",
            @Query("filterType") filterType: Int = 3,
            @Query("cid") cid: String = "50142",
            @Query("nt") nt: Int = 4): Observable<String>
}