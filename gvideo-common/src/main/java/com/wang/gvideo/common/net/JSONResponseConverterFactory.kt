package com.wang.gvideo.common.net

import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Date:2018/4/3
 * Description:
 *
 * @author wangguang.
 */
class JSONResponseConverterFactory private constructor() : Converter.Factory() {
    companion object {
        private val INSTANCE = JSONResponseConverterFactory()
        val converter = JSONObjectConverter()
        fun create(): JSONResponseConverterFactory {
            return INSTANCE
        }
    }

    override fun responseBodyConverter(type: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, JSONObject>? {
        return if (type != null && type == JSONObject::class.java) {
            converter
        } else {
            null
        }

    }
}

class JSONObjectConverter : Converter<ResponseBody, JSONObject> {
    override fun convert(value: ResponseBody?): JSONObject {
        value ?: return JSONObject()
        return JSONObject(value.string())
    }

}