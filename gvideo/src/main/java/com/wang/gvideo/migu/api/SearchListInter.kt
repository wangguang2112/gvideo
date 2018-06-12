package com.wang.gvideo.migu.api

import com.wang.gvideo.common.net.HOST
import org.json.JSONObject
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url
import rx.Observable
import java.net.URLEncoder

/**
 * Date:2018/4/3
 * Description:
 *
 * @author wangguang.
 */

@HOST("http://search.miguvideo.com")
interface SearchListInter {

    companion object {
        private const val BASE_URL = "http://search.miguvideo.com/search/v1/open-search/"
        fun getSearchURL(key:String):String{
            return "$BASE_URL${URLEncoder.encode(key,"utf-8")}/1"
        }
    }

    /**
     * packId 1002581,1002601,1003861,1003862,1003863,1003864,1003865,1003866,1004041,1004121,1004261,1004262,1004281,1002781
     * pageSize 30
     * contDisplayType 1001
     */
    @GET
    fun getSearchList(@Url url:String,
                      @Query("packId") packId: String
                      = "1002581,1002601,1003861,1003862,1003863,1003864,1003865,1003866,1004041,1004121,1004261,1004262,1004281,1002781",
                      @Query("pageSize") pageSize: Int = 30): Observable<JSONObject>
}