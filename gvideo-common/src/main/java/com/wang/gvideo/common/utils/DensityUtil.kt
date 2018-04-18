/**
 * @project: 58bangbang
 * @file: DensityUtil.java
 * @date: 2014年10月8日 下午4:52:53
 * @copyright: 2014  58.com Inc.  All rights reserved.
 */
package com.wang.gvideo.common.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowManager

import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * TODO 添加类的功能描述.
 * @author 赵彦辉
 * @date: 2014年10月8日 下午4:52:53
 */
object DensityUtil {
    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    fun gettDisplayWidth(ctx: Context): Int {
        val metric = DisplayMetrics()
        val winManager = ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        winManager.defaultDisplay.getMetrics(metric)
        return metric.widthPixels
    }

    fun gettDisplayHeight(ctx: Context): Int {
        val metric = DisplayMetrics()
        val winManager = ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        winManager.defaultDisplay.getMetrics(metric)
        return metric.heightPixels
    }

    fun printDisplayMetrics(ctx: Context) {
        val metric = DisplayMetrics()
        val winManager = ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        winManager.defaultDisplay.getMetrics(metric)
//        val width = metric.widthPixels // 屏幕宽度（像素）
//        val height = metric.heightPixels // 屏幕高度（像素）
//        val density = metric.density // 屏幕密度（0.75 / 1.0 / 1.5）
//        val densityDpi = metric.densityDpi // 屏幕密度DPI（120 / 160 / 240 / 320 / 480 ）
    }

    fun getDeviceDensity(ctx: Context): Float {
        val metric = DisplayMetrics()
        val winManager = ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        winManager.defaultDisplay.getMetrics(metric)
        return metric.density
    }

    fun getStatusBarHeight(ctx: Context): Int {
        var statusBarHeight = 0
        try {
            /**
             * 通过反射机制获取StatusBar高度
             */
            val clazz = Class.forName("com.android.internal.R\$dimen")
            val `object` = clazz.newInstance()
            val field = clazz.getField("status_bar_height")
            val height = Integer.parseInt(field.get(`object`).toString())
            /**
             * 设置StatusBar高度
             */
            statusBarHeight = ctx.resources.getDimensionPixelSize(height)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return statusBarHeight
    }

    /**
     * 是否显示虚拟按键
     */

    fun getHasVirtualKey(context: Context): Boolean {
        var dpi = 0
        val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val dm = DisplayMetrics()
        val c: Class<*>
        try {
            c = Class.forName("android.view.Display")
            val method = c.getMethod("getRealMetrics", DisplayMetrics::class.java)
            method.invoke(display, dm)
            dpi = dm.heightPixels
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return dpi != 0 && dpi != gettDisplayHeight(context)
    }

    fun getDensityStr(ctx: Context): String {
        val metric = DisplayMetrics()
        val winManager = ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        winManager.defaultDisplay.getMetrics(metric)
        return when (metric.densityDpi) {
            120 -> "MDPI"
            160 -> "HDPI"
            320 -> "XHDPI"
            480 -> "XXHDPI"
            640 -> "XXXHDPI"
            else -> "HDPI"
        }
    }
}
