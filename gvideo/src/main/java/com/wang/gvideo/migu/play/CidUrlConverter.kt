package com.wang.gvideo.migu.play

import android.util.Base64
import com.wang.gvideo.common.net.ApiFactory
import com.wang.gvideo.migu.api.WapMiGuInter
import com.wang.gvideo.migu.model.VideoInfoModel
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.net.URLDecoder
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec

/**
 * Date:2018/6/5
 * Description:
 *
 * @author wangguang.
 */
object CidUrlConverter {
    fun getVideoObservibe(contId: String): Observable<VideoInfoModel> {
        return ApiFactory.INSTANCE()
                .createApi(WapMiGuInter::class.java)
//                        ApiFactory.createCookie("www.miguvideo.com", "UserInfo", "982324173|772E524A40FA31D9F78D"))
                .getVideoInfo(contId)
                .subscribeOn(Schedulers.io())
                .map {
                    it[0]
                }
                .map {
                    val newModel = it.copy()
                    val key = VideoInfoModel.getKey(it.func)
                    newModel.playList.play1 = getRealUrl(it.playList.play1, key)
                    newModel.playList.play2 = getRealUrl(it.playList.play2, key)
                    newModel.playList.play3 = getRealUrl(it.playList.play3, key)
                    newModel.playList.play4 = getRealUrl(it.playList.play4, key)
                    newModel.playList.play5 = getRealUrl(it.playList.play5, key)
                    newModel.pilotPlayList.play41 = getRealUrl(it.pilotPlayList.play41, key)
                    newModel.pilotPlayList.play42 = getRealUrl(it.pilotPlayList.play42, key)
                    newModel.pilotPlayList.play43 = getRealUrl(it.pilotPlayList.play43, key)
                    newModel.pilotPlayList.play44 = getRealUrl(it.pilotPlayList.play44, key)
                    newModel.pilotPlayList.play45 = getRealUrl(it.pilotPlayList.play45, key)
                    if(it.Variety.size == 1&& it.Variety[0].contId.isEmpty() ){
                        newModel.Variety.removeAt(0)
                    }
                    newModel
                }
                .observeOn(AndroidSchedulers.mainThread())
    }
    private fun getRealUrl(content: String, key: String): String {
        if (content.isNotEmpty()) {
            return URLDecoder.decode(decrypt(content, key))
        } else {
            return ""
        }

    }

    private fun decrypt(content: String, key: String): String {
        val cipher = Cipher.getInstance("DES/ECB/PKCS7Padding")
        val spec = DESKeySpec(key.toByteArray())
        val keys = SecretKeyFactory.getInstance("DES").generateSecret(spec)
        cipher.init(Cipher.DECRYPT_MODE, keys)
        return String(cipher.doFinal(Base64.decode(content, 0)))
    }
}