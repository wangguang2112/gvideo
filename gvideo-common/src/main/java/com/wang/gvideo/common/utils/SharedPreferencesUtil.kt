package com.wang.gvideo.common.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.wang.gvideo.common.Common

import java.util.HashMap
import java.util.HashSet

/**
 * @author 黄宏宇
 * @date: 2014年9月17日 上午10:26:00
 */
class SharedPreferencesUtil private constructor() {

    /**
     * 获得所有保存对象
     *
     * @return
     */
    val all: HashMap<String, *>?
        get() = if (mPreferences.all is HashMap<*, *>) {
            mPreferences.all as HashMap<String, *>
        } else null

    /**
     * 是否有键
     *
     * @param key
     * @return
     */
    fun isContainKey(key: String): Boolean {
        return mPreferences.contains(key)
    }

    /**
     * 删除指定键的值item
     *
     * @param key
     * @return
     */
    fun clearItem(key: String): Boolean {
        mEditor.remove(key)
        return mEditor.commit()
    }

    /**
     * 给指定键设置String值
     *
     * @param key
     * @param value
     * @return
     */
    fun setString(key: String, value: String): Boolean {
        if (mPreferences.contains(key)) {
            mEditor.remove(key)
        }
        mEditor.putString(key, value)
        return mEditor.commit()
    }

    /**
     * 获得指定键的String类型值
     *
     * @param key
     * 键
     * @return
     */
    fun getString(key: String): String {
        return mPreferences.getString(key, "")
    }

    /**
     * 获得指定键的String类型值，带有默认值的
     *
     * @param key
     * 键
     * @param defValue
     * 默认值
     * @return
     */
    fun getString(key: String, defValue: String): String? {
        return mPreferences.getString(key, defValue)
    }

    /**
     * 给指定键设置int值
     *
     * @param key
     * @param value
     * @return
     */
    fun setInt(key: String, value: Int): Boolean {
        if (mPreferences.contains(key)) {
            mEditor.remove(key)
        }
        mEditor.putInt(key, value)
        return mEditor.commit()
    }

    /**
     * 获得int类型数据
     *
     * @param key
     * @return
     */
    fun getInt(key: String): Int {
        return mPreferences.getInt(key, 0)
    }

    /**
     * 获得int类型数据，带有默认值的
     *
     * @param key
     * @param defValue
     * @return
     */
    fun getInt(key: String, defValue: Int): Int {
        return mPreferences.getInt(key, defValue)
    }

    /**
     * 设置float类型数据
     *
     * @param key
     * @param value
     * @return
     */
    fun setFloat(key: String, value: Float): Boolean {
        if (mPreferences.contains(key)) {
            mEditor.remove(key)
        }
        mEditor.putFloat(key, value)
        return mEditor.commit()
    }

    /**
     * 获得float类型数据
     *
     * @param key
     * @return
     */
    fun getFloat(key: String): Float {
        return mPreferences.getFloat(key, 0f)
    }

    /**
     * 获得float类型数据，带有默认值的
     *
     * @param key
     * @param defValue
     * @return
     */
    fun getFloat(key: String, defValue: Float): Float {
        return mPreferences.getFloat(key, defValue)
    }

    /**
     * 设置long类型数据，带有默认值的
     *
     * @param key
     * @param value
     * @return
     */
    fun setBoolean(key: String, value: Boolean): Boolean {
        if (mPreferences.contains(key)) {
            mEditor.remove(key)
        }
        mEditor.putBoolean(key, value)
        return mEditor.commit()
    }

    /**
     * 获得boolean类型数据
     *
     * @param key
     * @param defValue
     * @return
     */
    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return mPreferences.getBoolean(key, defValue)
    }

    /**
     * 设置long类型数据
     *
     * @param key
     * @param value
     * @return
     */
    fun setLong(key: String, value: Long): Boolean {
        if (mPreferences.contains(key)) {
            mEditor.remove(key)
        }
        mEditor.putLong(key, value)
        return mEditor.commit()
    }

    /**
     * 获得long类型数据
     *
     * @param key
     * @return
     */
    fun getLong(key: String): Long {
        return mPreferences.getLong(key, 0)
    }

    /**
     * 获得long类型数据，带有默认值的
     *
     * @param key
     * @param defValue
     * @return
     */
    fun getLong(key: String, defValue: Long): Long {
        return mPreferences.getLong(key, defValue)
    }


    fun setStringSet(key: String, set: Set<String>): Boolean {
        if (mPreferences.contains(key)) {
            mEditor.remove(key)
        }
        mEditor.putStringSet(key, set)
        return mEditor.commit()
    }

    fun getStringSet(key: String): Set<String> {
        return mPreferences.getStringSet(key, HashSet())
    }

    companion object {

        private val sharedPreferencesInfo = "guangInfo"

        private lateinit var mPreferences: SharedPreferences
        private lateinit var mEditor: Editor
        private val mSharedInstance = SharedPreferencesUtil()

        /**
         * 单例模式获得对象实例
         *
         * @param context
         * @return
         */
        fun getInstance(context: Context): SharedPreferencesUtil {
            context.let {
                mPreferences = it.applicationContext.getSharedPreferences(
                        sharedPreferencesInfo, Context.MODE_PRIVATE)
                mEditor = mPreferences.edit()
            }
            return mSharedInstance
        }

        /**
         * 单例模式获得对象实例
         *
         * @return
         */
        val instance: SharedPreferencesUtil
            get() = getInstance(Common.appContext)


        /**
         * 程序第一次运行之后设置
         *
         * @param context
         * 上下文
         * @param uid
         * uid
         * @author 黄宏宇
         */
        fun setAppIsFirstInit(context: Context, uid: Long) {
            val preferences = context.getSharedPreferences("user",
                    Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putBoolean("FIRST_INIT" + uid, false)
            editor.commit()
        }

        /**
         * 程序是否第一次运行
         *
         * @param context
         * 上下文
         * @param uid
         * uid
         * @return boolean
         * @author 黄宏宇
         */
        fun appIsFirstInit(context: Context, uid: Long): Boolean {
            val preferences = context.getSharedPreferences("user",
                    Context.MODE_PRIVATE)
            return preferences.getBoolean("FIRST_INIT" + uid, true)
        }
    }

}