package com.wang.gvideo.common.utils

import com.google.gson.Gson
import org.json.JSONObject

/**
 * Date:2018/4/4
 * Description:
 *
 * @author wangguang.
 * kotlin 内联工具类
 */

/******************************************* 通用内联函数**********************************************/
/**
 * 为空则执行函数并返回 ,不为空返回当前对象
 * 说明：内联的含义 是替换执行以下函数,
 * ？在声明中表示 T 可以为空 跟使用是不同 注意
 */
inline infix fun <T : Any> T?.nil(block: (T?) -> T): T {
    if (this == null) {
        return block(this)
    }
    return this
}

/**
 * 选择性替换某些List的元素
 */
inline  infix fun <T> Iterable<T>.toList(block: (T) -> T): List<T> {
    if (this is Collection) {
        return when (size) {
            0 -> emptyList()
            1 -> listOf(if (this is List) block(get(0)) else block(iterator().next()))
            else -> {
                val result = mutableListOf<T>()
                this.forEach {
                    result.add(block(it))
                }
                result.toList()
            }
        }
    }
    return this.toList()
}

/**
 * 判断是否为空
 */
inline fun <T : Any> Collection<T>?.empty(): Boolean {
    return this == null || this.isEmpty()
}


/**
 * 容器异常处理 （执行）
 */
inline infix fun <T : Any> Collection<T>?.notEmptyRun(block: (Collection<T>) -> Unit) {
    if (this != null && this.isNotEmpty()) {
        block(this)
    }
}


/**
 * 容器异常处理 （执行）
 */
inline infix fun <T : Any> Collection<T>?.emptyRun(block: (Collection<T>?) -> Unit) {
    if (this == null || this.isEmpty()) {
        block(this)
    }
}

/******************************************* String 内联函数**********************************************/
/**
 * 字符串(空字符串)异常处理 （返回）
 */
inline infix fun String?.empty(block: (String?) -> String): String {
    if (this == null || this.isEmpty()) {
        return block(this)
    }
    return this
}

/**
 * 字符串(空字符串)异常处理 （执行）
 */
inline infix fun String?.emptyRun(block: (String?) -> Unit) {
    if (this == null || this.isEmpty()) {
        block(this)
    }
}

/******************************************* List 内联函数**********************************************/

/**
 * List安全获取元素（返回）
 */
inline fun <T : Any> List<T>?.safeGet(index: Int, block: (List<T>?) -> T): T {
    return if (this == null || this.isEmpty() || index >= this.size) {
        block(this)
    } else {
        this[index]
    }
}

/**
 * List安全获取元素（执行）
 */
inline fun <T : Any> List<T>?.safeGetRun(index: Int, block: (T) -> Unit) {
    if (this != null && this.isNotEmpty() && index < this.size) {
        block(this[index])
    }
}

/**
 * List转换为字符串
 */
fun <T : Any> List<T>?.string(): String {
    if (this != null && this.isNotEmpty()) {
        val result = StringBuilder()
        result.append("[")
        this.forEach { value ->
            if (value is Int
                    || value is Long
                    || value is Short
                    || value is Byte
                    || value is Float
                    || value is Double
                    || value is Boolean
                    || value is CharSequence) {
                result.append(value)
                result.append(",")
            }else{
                result.append(value.toString())
                result.append(",")
            }
        }
        if (result.last() == ',') {
            result.deleteCharAt(result.lastIndex)
        }
        result.append("]")
        return result.toString()
    } else {
        return "[]"
    }
}


/******************************************* Array 内联函数**********************************************/

/**
 * Array安全获取元素（返回）
 */
inline fun <T : Any> Array<T>?.safeGet(index: Int, block: (Array<T>?) -> T): T {
    return if (this == null || this.isEmpty() || index >= this.size) {
        block(this)
    } else {
        this[index]
    }
}

/**
 * Array安全获取元素（执行）
 */
inline fun <T : Any> Array<T>?.safeGetRun(index: Int, block: (T) -> Unit) {
    if (this != null && !this.isEmpty() && index < this.size) {
        block(this[index])
    }
}
/******************************************* Map 内联函数**********************************************/

/**
 * Map转换为JSONObject
 */
fun <T : Any> Map<String, T>?.toJson(): JSONObject {
    val obj = JSONObject()
    if (this != null && this.isNotEmpty()) {
        this.forEach { entry ->
            val value = entry.value
            if (value is Int
                    || value is Long
                    || value is Short
                    || value is Byte
                    || value is Float
                    || value is Double
                    || value is Boolean
                    || value is CharSequence) {
                obj.put(entry.key, value)
            } else if (value is Map<*, *>) {
                obj.put(entry.key, toJson())
            } else {
                obj.put(entry.key, Gson().toJson(value))
            }
        }
    }
    return obj
}

/**
 * Map转换为JSONObject字符串
 */
fun <T : Any> Map<String, T>?.string(): String {
    return toJson().toString(0)
}