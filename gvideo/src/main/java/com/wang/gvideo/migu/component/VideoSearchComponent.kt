package com.wang.gvideo.migu.component

import android.content.Context
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
interface VideoSearchComponent {

    fun inject(activity: VideoSearchActivity)

    @Component.Builder
    interface Builder {
        fun build(): VideoSearchComponent
        @BindsInstance
        fun setContext(ctx: Context): Builder
    }
}
