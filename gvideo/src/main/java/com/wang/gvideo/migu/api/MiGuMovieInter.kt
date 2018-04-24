package com.wang.gvideo.migu.api

import com.wang.gvideo.common.net.HOST
import com.wang.gvideo.migu.model.AppVideoListInfo
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
                     @Query("nt") nt: Int = 4): Observable<String>
}