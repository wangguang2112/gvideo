package com.leo.player.media.weiget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Date:2018/6/12
 * Description:
 *
 * @author wangguang.
 */

public class LockLayout extends FrameLayout {

    private boolean isLock = false;

    public LockLayout(@NonNull Context context) {
        super(context);
    }

    public LockLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LockLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LockLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void onLock(boolean isLock) {
        this.isLock = isLock;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return isLock || super.onTouchEvent(event);
    }
}
