package com.wang.gvideo.common.net

import android.util.Log
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.util.*

/**
 * Date:2018/4/18
 * Description:
 *
 * @author wangguang.
 */


class ListAdapterFactory : TypeAdapterFactory {

    override fun <T> create(gson: Gson, typeToken: TypeToken<T>): TypeAdapter<T>? {
        Log.d("ListAdapterFactory",typeToken.type.toString())
        Log.d("ListAdapterFactory",typeToken.rawType.toString())
        val type = typeToken.type
        if (typeToken.rawType != List::class.java || type !is ParameterizedType) {
            return null
        }
        Log.d("ListAdapterFactory","new")
        val elementType = type.actualTypeArguments[0]
        val elementAdapter = gson.getAdapter(TypeToken.get(elementType))
        return newListAdapter(elementAdapter) as TypeAdapter<T>
    }

    private fun <E> newListAdapter(elementAdapter: TypeAdapter<E>): TypeAdapter<List<E>> {
        return object : TypeAdapter<List<E>>() {
            @Throws(IOException::class)
            override fun write(out: JsonWriter, value: List<E>?) {
                if (value == null) {
                    out.nullValue()
                    return
                }

                out.beginArray()
                for (entry in value) {
                    elementAdapter.write(out, entry)
                }
                out.endArray()
            }

            @Throws(IOException::class)
            override fun read(`in`: JsonReader): List<E> {
                if (`in`.peek() == JsonToken.NULL) {
                    `in`.nextNull()
                    return ArrayList()
                }

                val result = ArrayList<E>()
                `in`.beginArray()
                while (`in`.hasNext()) {
                    val element = elementAdapter.read(`in`)
                    result.add(element)
                }
                `in`.endArray()
                return result
            }

            override fun toString(): String {
                return "ListAdapterFactory"
            }
        }
    }
}