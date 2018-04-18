package com.wang.gvideo

import com.wang.gvideo.common.Common

import android.app.Application
import android.content.Context
import com.facebook.drawee.backends.pipeline.Fresco
import io.realm.Realm
import io.realm.RealmConfiguration



/**
 * Date:2018/4/11
 * Description:
 *
 * @author wangguang.
 */

class App : Application() {

    companion object {
        lateinit var app: Context
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        Fresco.initialize(this)
        Common.initCommon(this)
        Realm.init(this)
        val config = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(config)
    }

}
