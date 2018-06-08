package com.wang.gvideo.migu.dao.realm

import io.realm.DynamicRealm
import io.realm.RealmMigration

/**
 * Date:2018/6/7
 * Description:
 *
 * @author wangguang.
 */
class Migration0T1 :RealmMigration{

    override fun migrate(realm: DynamicRealm?, oldVersion: Long, newVersion: Long) {
        realm?:return
        val schema = realm.schema
        if (oldVersion == 0L && newVersion == 1L) {
            schema.get("CacheTaskDao")
                    .addField("tempFile",String::class.java)
                    .transform { obj ->
                        obj.set("tempFile","")
                    }
        }
    }


}