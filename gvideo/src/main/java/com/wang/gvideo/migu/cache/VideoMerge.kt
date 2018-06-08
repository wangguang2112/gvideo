package com.wang.gvideo.migu.cache

import android.util.Log
import java.io.File
import org.apache.commons.io.IOUtils

/**
 * Date:2018/6/7
 * Description:
 *
 * @author wangguang.
 */
class VideoMerge {
    val TAG = "VideoMerge"

    /**
     * m3u8 files的合并
     */
    fun merge(tempDir: String, outFile: String): Boolean {
        Log.d(TAG,"merge:tempDir = $tempDir")
        val tempFile = File(tempDir)
        val outFiles = File(outFile)
        outFiles.parentFile.mkdirs()
        outFiles.delete()
        if(!outFiles.createNewFile()){
            Log.d(TAG,"create file faild")
            return false
        }
        if (tempFile.exists() && tempFile.isDirectory) {
            val childs = tempFile.listFiles()
            childs.sortBy {
                val fileName = it.name
                val index = it.name.indexOf("-")
                val dotIndex = it.name.lastIndexOf(".")
                if (index >= 0) {
                    if (dotIndex > index) {
                        fileName.substring(index + 1, dotIndex).toInt()
                    } else {
                        fileName.substring(index + 1, fileName.lastIndex).toInt()
                    }
                } else {
                    10000
                }
            }
            var output = outFiles.outputStream()
            childs.forEach {
                Log.d(TAG,"copy $it")
                val input =  it.inputStream()
                IOUtils.copyLarge(input,output)
                input.close()
            }
            return true
        } else {
            return false
        }
    }
}