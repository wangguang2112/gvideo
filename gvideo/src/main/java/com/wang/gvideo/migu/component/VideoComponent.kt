package com.wang.gvideo.migu.component

import android.content.Context
import com.wang.gvideo.migu.ui.VideoPlayActivity
import com.wang.gvideo.migu.ui.VideoSearchActivity
import dagger.BindsInstance
import dagger.Component

/**
 * Date:2018/4/2
 * Description:
 *
 * @author wangguang.
 */

@Component
interface VideoComponent {

    fun inject(activity: VideoPlayActivity)

    @Component.Builder
    interface Builder {
        fun build(): VideoComponent
        @BindsInstance
        fun setContext(ctx: VideoPlayActivity): Builder
    }
}
