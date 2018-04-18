package com.wang.gvideo.common.net

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Date:2018/4/3
 * Description:
 *
 * @author wangguang.
 */
class StringResponseConverterFactory private constructor() : Converter.Factory() {
    companion object {
        private val INSTANCE = StringResponseConverterFactory()
        val converter = StringConverter()
        fun create(): StringResponseConverterFactory {
            return INSTANCE
        }
    }

    override fun responseBodyConverter(type: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, String>? {
        return if (type != null && type == String::class.java) {
            converter
        } else {
            null
        }

    }
}

class StringConverter : Converter<ResponseBody, String> {
    override fun convert(value: ResponseBody?): String {
        return value?.string() ?: ""
    }

}