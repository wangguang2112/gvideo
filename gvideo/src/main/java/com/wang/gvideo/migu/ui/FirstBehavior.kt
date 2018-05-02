package com.wang.gvideo.migu.ui

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Scroller
import com.wang.gvideo.R
import com.wang.gvideo.common.utils.getValue
import com.wang.gvideo.common.utils.value
import java.lang.ref.WeakReference


/**
 * Date:2018/4/28
 * Description:
 *
 * @author wangguang.
 */
class FirstBehavior(val context: Context, attrs: AttributeSet?) : CoordinatorLayout.Behavior<View>(context, attrs) {

    private val DEBUG = false
    private var scroller = Scroller(context)
    private var dependentView: WeakReference<View>? = null
    private var isScrolling = false


    /**
     * nestedScrollAxes表示滑动方向
     */

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, nestedScrollAxes: Int): Boolean {
        return (nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL) != 0
    }

    override fun onNestedScrollAccepted(coordinatorLayout: CoordinatorLayout?, child: View?, directTargetChild: View?, target: View?, nestedScrollAxes: Int) {
        scroller.abortAnimation()
        isScrolling = false
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes)
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout?, child: View?, target: View?) {
        super.onStopNestedScroll(coordinatorLayout, child, target)
        dependentView.value {
            if (!isScrolling && it.translationY > 0) {
                onUserStopDragging(800f)
            }
        }

    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout?, child: View, target: View, dx: Int, dy: Int, consumed: IntArray) {
        if(DEBUG) {
            Log.d("FirstBehavior", "onNestedPreScroll ${child.javaClass.simpleName}:${target.javaClass.simpleName}:$dx:$dy ")
        }
        dependentView.value { depence ->
            val middleY = depence.translationY
            if(DEBUG) {
                Log.d("FirstBehavior", "onNestedPreScroll $middleY :${-depence.height} ")
            }
            if (dy > 0 || (dy < 0 && middleY > -depence.height +3)) {
                val newTranslate = middleY - dy
                if (newTranslate > -depence.height && newTranslate < 0) {
                    depence.translationY = newTranslate
                    consumed[1] = dy
                }
            }
        }
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout?, child: View, target: View?, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
        if(DEBUG) {
            Log.d("FirstBehavior", "onNestedScroll $dyUnconsumed ")
        }
        //向下
        dependentView.value { depence ->
            val newTranslate = depence.translationY - dyUnconsumed
            if(DEBUG) {
                Log.d("FirstBehavior", "onNestedScroll tran :: $newTranslate ")
            }
            if (newTranslate >- child.height && newTranslate < 0) {
                depence.translationY = newTranslate
            }
        }
    }

    override fun onNestedFling(coordinatorLayout: CoordinatorLayout?, child: View, target: View?, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        if(DEBUG) {
            Log.d("FirstBehavior", "onNestedScroll $consumed $velocityY  ")
        }
        if(!consumed){
           return onUserStopDragging(velocityY)
        }else if(consumed && child.scrollY == 0){
            return onUserStopDragging(velocityY/3)
        }
        return false
    }

    override fun onNestedPreFling(coordinatorLayout: CoordinatorLayout?, child: View?, target: View?, velocityX: Float, velocityY: Float): Boolean {
        dependentView.value { depence ->
            val middleY = depence.translationY
            if(middleY < -3 && middleY > -depence.height + 3){
                return onUserStopDragging(velocityY)
            }else if (velocityY > 0 && middleY >-3 ) {
                return onUserStopDragging(velocityY)
            }
        }
        return false
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        if(DEBUG) {
            Log.d("FirstBehavior", "onDependentViewChanged ${child.javaClass.simpleName} ${dependency.javaClass.simpleName}  ")
        }
        child.translationY = dependency.height + dependency.translationY
        return true
    }

    override fun layoutDependsOn(parent: CoordinatorLayout?, child: View?, dependency: View?): Boolean {
        if(DEBUG) {
            Log.d("FirstBehavior", "layoutDependsOn ")
        }
        if (dependency != null && dependency.id === R.id.video_first_header) {
            dependentView = WeakReference(dependency)
            return true
        }
        return false
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: View, layoutDirection: Int): Boolean {
        val lp = child.layoutParams as CoordinatorLayout.LayoutParams
        if (lp.height == CoordinatorLayout.LayoutParams.MATCH_PARENT) {
            child.layout(0, getDependentViewCollapsedHeight(), parent.width, parent.height + getDependentViewCollapsedHeight())
            return true
        }
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    private fun getDependentViewCollapsedHeight(): Int {
        dependentView.getValue()?.let { view ->
            return view.height
        }
        return 0
    }

    private fun onUserStopDragging(velocity: Float): Boolean {
        dependentView.value { dependence ->
            val translateY = dependence.translationY

            if (translateY == 0f) {
                return false
            }
            scroller.fling(0, translateY.toInt(),0, -velocity.toInt(),0,0,-dependence.height,0)
            ViewCompat.postOnAnimation(dependence,flingRunnable)
            isScrolling = true
        }
        return true
    }

    private val flingRunnable = object : Runnable {
        override fun run() {
            dependentView.value { dependence ->
                if (scroller.computeScrollOffset()) {
                    dependence.translationY = scroller.currY.toFloat()
                    ViewCompat.postOnAnimation(dependence,this)
                } else {
                    isScrolling = false
                }
            }
        }
    }
}

