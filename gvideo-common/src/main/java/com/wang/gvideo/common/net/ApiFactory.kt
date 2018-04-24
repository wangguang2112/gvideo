package com.wang.gvideo.common.net

import android.os.Environment
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.wang.gvideo.common.Common
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import okhttp3.Cookie


/**
 * Date:2018/4/3
 * Description:
 *
 * @author wangguang.
 */
class ApiFactory private constructor() {

    companion object {
        fun INSTANCE(): ApiFactory {
            return Holder.factoryInstance
        }

        fun createCookie(host: String, name: String, value: String): Cookie {
            return Cookie.Builder()
                    .domain(host)
                    .path("/")
                    .name(name)
                    .value(value)
                    .httpOnly()
                    .secure()
                    .build()
        }
    }

    private object Holder {
        val factoryInstance = ApiFactory()
        val okHttpClient = newOkHttpClient()
        val cacheMap = ConcurrentHashMap<Any, Any>()
        fun newOkHttpClient(cookieJar: CookieJar = CookieJar.NO_COOKIES): OkHttpClient {
            return OkHttpClient.Builder()
                    .cache(Cache(if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                        Common.appContext.externalCacheDir
                    } else {
                        Common.appContext.cacheDir
                    }, 100 * 1024 * 1024))
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor {
                        val reqBuider = it.request().newBuilder().addHeader("X_UP_CLIENT_CHANNEL_ID", "64000014-99000-800000200000002")
                        it.proceed(reqBuider.build())
                    }
                    .cookieJar(cookieJar)
                    .build()!!

        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> createApi(cls: Class<T>): T = Holder.cacheMap[cls]?.let {
        return it as T
    } ?: with(Retrofit.Builder()) {
        var baseUrl = ""
        cls.annotations.forEach {
            if (it is HOST) {
                baseUrl = it.value
                return@forEach
            }
        }
        client(Holder.okHttpClient)
        baseUrl(baseUrl)
        addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        addConverterFactory(StringResponseConverterFactory.create())
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setLenient()
        gsonBuilder.registerTypeAdapterFactory(ListAdapterFactory())
        addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
        var temp = build().create(cls)
        Holder.cacheMap[cls] = temp as Any
        return temp
    }

    fun getGson(): Gson {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setLenient()
        gsonBuilder.registerTypeAdapterFactory(ListAdapterFactory())
        return gsonBuilder.create()
    }

    fun <T> createApiWithCookie(cls: Class<T>, cookie: Cookie): T {
        val builder = Retrofit.Builder().apply {
            var baseUrl = ""
            cls.annotations.forEach {
                if (it is HOST) {
                    baseUrl = it.value
                    return@forEach
                }
            }

            client(Holder.newOkHttpClient(object : CookieJar {
                override fun saveFromResponse(url: HttpUrl?, cookies: MutableList<Cookie>?) {

                }

                override fun loadForRequest(url: HttpUrl?): MutableList<Cookie> {
                    return mutableListOf(cookie)
                }
            }))
            baseUrl(baseUrl)
            addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            addConverterFactory(StringResponseConverterFactory.create())
            val gsonBuilder = GsonBuilder()
            gsonBuilder.setLenient()
            gsonBuilder.registerTypeAdapterFactory(ListAdapterFactory())
            addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
        }
        return builder.build().create(cls)
    }


}