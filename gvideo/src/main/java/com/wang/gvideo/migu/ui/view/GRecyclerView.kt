package com.wang.gvideo.migu.ui.view

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewParent

import com.github.jdsjlzx.interfaces.ILoadMoreFooter
import com.github.jdsjlzx.interfaces.IRefreshHeader
import com.github.jdsjlzx.interfaces.OnLoadMoreListener
import com.github.jdsjlzx.interfaces.OnNetWorkErrorListener
import com.github.jdsjlzx.interfaces.OnRefreshListener
import com.github.jdsjlzx.recyclerview.AppBarStateChangeListener
import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.github.jdsjlzx.view.ArrowRefreshHeader
import com.github.jdsjlzx.view.LoadingFooter

/**
 *
 * @author lizhixian
 * @created 2016/8/29 11:21
 */
class GRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RecyclerView(context, attrs, defStyle) {
    private var mPullRefreshEnabled = true
    private var mLoadMoreEnabled = true
    private var mRefreshing = false//是否正在下拉刷新
    private var mLoadingData = false//是否正在加载数据
    private var mRefreshListener: OnRefreshListener? = null
    private var mLoadMoreListener: OnLoadMoreListener? = null
    private var mLScrollListener: LScrollListener? = null
    private var mRefreshHeader: IRefreshHeader? = null
    private var mLoadMoreFooter: ILoadMoreFooter? = null
    private var mEmptyView: View? = null
    private var mFootView: View? = null

    private val mDataObserver = DataObserver()
    private var mLastY = -1f
    private var sumOffSet: Float = 0.toFloat()
    private var mPageSize = 10 //一次网络请求默认数量

    private var mWrapAdapter: LRecyclerViewAdapter? = null
    private var isNoMore = false
    private var mIsVpDragger: Boolean = false
    private var mTouchSlop: Int = 0
    private var startY: Float = 0.toFloat()
    private var startX: Float = 0.toFloat()
    private var isRegisterDataObserver: Boolean = false
    //scroll variables begin
    /**
     * 当前RecyclerView类型
     */
    protected var layoutManagerType: LayoutManagerType? = null

    /**
     * 最后一个的位置
     */
    private var lastPositions: IntArray? = null

    /**
     * 最后一个可见的item的位置
     */
    private var lastVisibleItemPosition: Int = 0

    /**
     * 当前滑动的状态
     */
    private var currentScrollState = 0

    /**
     * 滑动的距离
     */
    private var mDistance = 0

    /**
     * 是否需要监听控制
     */
    private var mIsScrollDown = true

    /**
     * Y轴移动的实际距离（最顶部为0）
     */
    private var mScrolledYDistance = 0

    /**
     * X轴移动的实际距离（最左侧为0）
     */
    private var mScrolledXDistance = 0
    //scroll variables end


    private var appbarState: AppBarStateChangeListener.State = AppBarStateChangeListener.State.EXPANDED

    val isOnTop: Boolean
        get() = mPullRefreshEnabled && mRefreshHeader!!.headerView.parent != null

    init {
        init()
    }

    private fun init() {
        mTouchSlop = ViewConfiguration.get(context.applicationContext).scaledTouchSlop
        if (mPullRefreshEnabled) {
            setRefreshHeader(ArrowRefreshHeader(context.applicationContext))
        }

        if (mLoadMoreEnabled) {
            setLoadMoreFooter(LoadingFooter(context.applicationContext))
        }


    }

    override fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        if (mWrapAdapter != null && mDataObserver != null && isRegisterDataObserver) {
            mWrapAdapter!!.innerAdapter.unregisterAdapterDataObserver(mDataObserver)
        }

        mWrapAdapter = adapter as LRecyclerViewAdapter
        super.setAdapter(mWrapAdapter)

        mWrapAdapter!!.innerAdapter.registerAdapterDataObserver(mDataObserver)
        mDataObserver.onChanged()
        isRegisterDataObserver = true

        mWrapAdapter!!.setRefreshHeader(mRefreshHeader)

        //fix bug: https://github.com/jdsjlzx/LRecyclerView/issues/115
        if (mLoadMoreEnabled && mWrapAdapter!!.footerViewsCount == 0) {
            mWrapAdapter!!.addFooterView(mFootView!!)
        }

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        if (mWrapAdapter != null && mDataObserver != null && isRegisterDataObserver) {
            mWrapAdapter!!.innerAdapter.unregisterAdapterDataObserver(mDataObserver)
            isRegisterDataObserver = false
        }

    }

    private inner class DataObserver : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            val adapter = adapter
            if (adapter is LRecyclerViewAdapter) {
                if (adapter.innerAdapter != null && mEmptyView != null) {
                    val count = adapter.innerAdapter.itemCount
                    if (count == 0) {
                        mEmptyView!!.visibility = View.VISIBLE
                        this@GRecyclerView.visibility = View.GONE
                    } else {
                        mEmptyView!!.visibility = View.GONE
                        this@GRecyclerView.visibility = View.VISIBLE
                    }
                }
            } else {
                if (adapter != null && mEmptyView != null) {
                    if (adapter.itemCount == 0) {
                        mEmptyView!!.visibility = View.VISIBLE
                        this@GRecyclerView.visibility = View.GONE
                    } else {
                        mEmptyView!!.visibility = View.GONE
                        this@GRecyclerView.visibility = View.VISIBLE
                    }
                }
            }

            if (mWrapAdapter != null) {
                mWrapAdapter!!.notifyDataSetChanged()
                if (mWrapAdapter!!.innerAdapter.itemCount < mPageSize) {
                    mFootView!!.visibility = View.GONE
                }
            }

        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            mWrapAdapter!!.notifyItemRangeChanged(positionStart + mWrapAdapter!!.headerViewsCount + 1, itemCount)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            mWrapAdapter!!.notifyItemRangeInserted(positionStart + mWrapAdapter!!.headerViewsCount + 1, itemCount)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            mWrapAdapter!!.notifyItemRangeRemoved(positionStart + mWrapAdapter!!.headerViewsCount + 1, itemCount)
            if (mWrapAdapter!!.innerAdapter.itemCount < mPageSize) {
                mFootView!!.visibility = View.GONE
            }

        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            val headerViewsCountCount = mWrapAdapter!!.headerViewsCount
            mWrapAdapter!!.notifyItemRangeChanged(fromPosition + headerViewsCountCount + 1, toPosition + headerViewsCountCount + 1 + itemCount)
        }

    }

    /**
     * 解决嵌套RecyclerView滑动冲突问题
     * @param ev
     * @return
     */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                // 记录手指按下的位置
                startY = ev.y
                startX = ev.x
                // 初始化标记
                mIsVpDragger = false
            }
            MotionEvent.ACTION_MOVE -> {
                // 如果viewpager正在拖拽中，那么不拦截它的事件，直接return false；
                if (mIsVpDragger) {
                    return false
                }

                // 获取当前手指位置
                val endY = ev.y
                val endX = ev.x
                val distanceX = Math.abs(endX - startX)
                val distanceY = Math.abs(endY - startY)
                // 如果X轴位移大于Y轴位移，那么将事件交给viewPager处理。
                if (distanceX > mTouchSlop && distanceX > distanceY) {
                    mIsVpDragger = true
                    return false
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                // 初始化标记
                mIsVpDragger = false
        }
        // 如果是Y轴位移大于X轴，事件交给swipeRefreshLayout处理。
        return super.onInterceptTouchEvent(ev)
    }

    var isRefreshInDrag = false;
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (mLastY == -1f) {
            mLastY = ev.rawY
        }
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastY = ev.rawY
                sumOffSet = 0f
                isRefreshInDrag = false
            }
            MotionEvent.ACTION_MOVE -> {
                var result = false
                if (!isRefreshInDrag){
                    result = super.onTouchEvent(ev)
                }
                val isNested =  getIsNestedScrolled()
                val deltaY = (ev.rawY - mLastY) / DRAG_RATE
                mLastY = ev.rawY
                sumOffSet += deltaY
                if (isOnTop && mPullRefreshEnabled && !mRefreshing && appbarState == AppBarStateChangeListener.State.EXPANDED
                        && !isNested) {
                    mRefreshHeader!!.onMove(deltaY, sumOffSet)
                    isRefreshInDrag = true
                    if (mRefreshHeader!!.visibleHeight > 0) {
                        return false
                    }
                }
                if(!isRefreshInDrag) {
                    return result
                }
            }
            else -> {
                isRefreshInDrag  = false
                mLastY = -1f // reset
                if (isOnTop && mPullRefreshEnabled && !mRefreshing/*&& appbarState == AppBarStateChangeListener.State.EXPANDED*/) {
                    if (mRefreshHeader != null && mRefreshHeader!!.onRelease()) {
                        if (mRefreshListener != null) {
                            mRefreshing = true
                            mFootView!!.visibility = View.GONE
                            mRefreshListener!!.onRefresh()

                        }
                    }
                }
            }
        }
        return super.onTouchEvent(ev)
    }

    private fun findMax(lastPositions: IntArray): Int {
        var max = lastPositions[0]
        for (value in lastPositions) {
            if (value > max) {
                max = value
            }
        }
        return max
    }

    /**
     * set view when no content item
     *
     * @param emptyView visiable view when items is empty
     */
    fun setEmptyView(emptyView: View) {
        this.mEmptyView = emptyView
        mDataObserver.onChanged()
    }

    /**
     *
     * @param pageSize 一页加载的数量
     */
    fun refreshComplete(pageSize: Int) {
        this.mPageSize = pageSize
        if (mRefreshing) {
            isNoMore = false
            mRefreshing = false
            mRefreshHeader!!.refreshComplete()

            if (mWrapAdapter!!.innerAdapter.itemCount < pageSize) {
                mFootView!!.visibility = View.GONE
            }
        } else if (mLoadingData) {
            mLoadingData = false
            mLoadMoreFooter!!.onComplete()
        }

    }

    /**
     * 设置是否已加载全部
     * @param noMore
     */
    fun setNoMore(noMore: Boolean) {
        mLoadingData = false
        isNoMore = noMore
        if (isNoMore) {
            mLoadMoreFooter!!.onNoMore()
        } else {
            mLoadMoreFooter!!.onComplete()
        }
    }

    /**
     * 设置自定义的RefreshHeader
     * 注意：setRefreshHeader方法必须在setAdapter方法之前调用才能生效
     */
    fun setRefreshHeader(refreshHeader: IRefreshHeader) {
        if (isRegisterDataObserver) {
            throw RuntimeException("setRefreshHeader must been invoked before setting the adapter.")
        }
        this.mRefreshHeader = refreshHeader
    }

    /**
     * 设置自定义的footerview
     */
    fun setLoadMoreFooter(loadMoreFooter: ILoadMoreFooter) {
        this.mLoadMoreFooter = loadMoreFooter
        mFootView = loadMoreFooter.footView
        mFootView!!.visibility = View.GONE

        //wxm:mFootView inflate的时候没有以RecyclerView为parent，所以要设置LayoutParams
        val layoutParams = mFootView!!.layoutParams
        if (layoutParams != null) {
            mFootView!!.layoutParams = RecyclerView.LayoutParams(layoutParams)
        } else {
            mFootView!!.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
        }
    }

    fun setPullRefreshEnabled(enabled: Boolean) {
        mPullRefreshEnabled = enabled
    }

    /**
     * 到底加载是否可用
     */
    fun setLoadMoreEnabled(enabled: Boolean) {
        if (mWrapAdapter == null) {
            throw NullPointerException("LRecyclerViewAdapter cannot be null, please make sure the variable mWrapAdapter have been initialized.")
        }
        mLoadMoreEnabled = enabled
        if (!enabled) {
            if (null != mWrapAdapter) {
                mWrapAdapter!!.removeFooterView()
            } else {
                mLoadMoreFooter!!.onReset()
            }
        }
    }

    fun setRefreshProgressStyle(style: Int) {
        if (mRefreshHeader != null && mRefreshHeader is ArrowRefreshHeader) {
            (mRefreshHeader as ArrowRefreshHeader).setProgressStyle(style)
        }
    }

    fun setArrowImageView(resId: Int) {
        if (mRefreshHeader != null && mRefreshHeader is ArrowRefreshHeader) {
            (mRefreshHeader as ArrowRefreshHeader).setArrowImageView(resId)
        }
    }

    fun setLoadingMoreProgressStyle(style: Int) {
        if (mLoadMoreFooter != null && mLoadMoreFooter is LoadingFooter) {
            (mLoadMoreFooter as LoadingFooter).setProgressStyle(style)
        }

    }

    fun setOnRefreshListener(listener: OnRefreshListener) {
        mRefreshListener = listener
    }

    fun setOnLoadMoreListener(listener: OnLoadMoreListener) {
        mLoadMoreListener = listener
    }

    fun setOnNetWorkErrorListener(listener: OnNetWorkErrorListener) {
        val loadingFooter = mFootView as LoadingFooter
        loadingFooter.state = LoadingFooter.State.NetWorkError
        loadingFooter.setOnClickListener({
            mLoadMoreFooter!!.onLoading()
            listener.reload()
        })
    }

    fun setFooterViewHint(loading: String, noMore: String, noNetWork: String) {
        if (mLoadMoreFooter != null && mLoadMoreFooter is LoadingFooter) {
            val loadingFooter = mLoadMoreFooter as LoadingFooter
            loadingFooter.setLoadingHint(loading)
            loadingFooter.setNoMoreHint(noMore)
            loadingFooter.setNoNetWorkHint(noNetWork)
        }
    }

    /**
     * 设置Footer文字颜色
     * @param indicatorColor
     * @param hintColor
     * @param backgroundColor
     */
    fun setFooterViewColor(indicatorColor: Int, hintColor: Int, backgroundColor: Int) {
        if (mLoadMoreFooter != null && mLoadMoreFooter is LoadingFooter) {
            val loadingFooter = mLoadMoreFooter as LoadingFooter
            loadingFooter.setIndicatorColor(ContextCompat.getColor(context, indicatorColor))
            loadingFooter.setHintTextColor(hintColor)
            loadingFooter.setViewBackgroundColor(backgroundColor)
        }
    }

    /**
     * 设置颜色
     * @param indicatorColor Only call the method setRefreshProgressStyle(int style) to take effect
     * @param hintColor
     * @param backgroundColor
     */
    fun setHeaderViewColor(indicatorColor: Int, hintColor: Int, backgroundColor: Int) {
        if (mRefreshHeader != null && mRefreshHeader is ArrowRefreshHeader) {
            val arrowRefreshHeader = mRefreshHeader as ArrowRefreshHeader
            arrowRefreshHeader.setIndicatorColor(ContextCompat.getColor(context, indicatorColor))
            arrowRefreshHeader.setHintTextColor(hintColor)
            arrowRefreshHeader.setViewBackgroundColor(backgroundColor)
        }

    }

    fun setLScrollListener(listener: LScrollListener) {
        mLScrollListener = listener
    }

    interface LScrollListener {

        fun onScrollUp() //scroll down to up

        fun onScrollDown() //scroll from up to down

        fun onScrolled(distanceX: Int, distanceY: Int) // moving state,you can get the move distance

        fun onScrollStateChanged(state: Int)
    }

    fun refresh() {
        if (mRefreshHeader!!.visibleHeight > 0 || mRefreshing) {// if RefreshHeader is Refreshing, return
            return
        }
        if (mPullRefreshEnabled && mRefreshListener != null) {
            mRefreshHeader!!.onRefreshing()
            val offSet = mRefreshHeader!!.headerView.measuredHeight
            mRefreshHeader!!.onMove(offSet.toFloat(), offSet.toFloat())
            mRefreshing = true

            mFootView!!.visibility = View.GONE
            mRefreshListener!!.onRefresh()
        }
    }

    fun forceToRefresh() {
        if (mLoadingData) {
            return
        }
        refresh()
    }


    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)

        var firstVisibleItemPosition = 0
        val layoutManager = layoutManager

        if (layoutManagerType == null) {
            if (layoutManager is LinearLayoutManager) {
                layoutManagerType = LayoutManagerType.LinearLayout
            } else if (layoutManager is GridLayoutManager) {
                layoutManagerType = LayoutManagerType.GridLayout
            } else if (layoutManager is StaggeredGridLayoutManager) {
                layoutManagerType = LayoutManagerType.StaggeredGridLayout
            } else {
                throw RuntimeException(
                        "Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager")
            }
        }

        when (layoutManagerType) {
            LRecyclerView.LayoutManagerType.LinearLayout -> {
                firstVisibleItemPosition = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            }
            LRecyclerView.LayoutManagerType.GridLayout -> {
                firstVisibleItemPosition = (layoutManager as GridLayoutManager).findFirstVisibleItemPosition()
                lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            }
            LRecyclerView.LayoutManagerType.StaggeredGridLayout -> {
                val staggeredGridLayoutManager = layoutManager as StaggeredGridLayoutManager
                if (lastPositions == null) {
                    lastPositions = IntArray(staggeredGridLayoutManager.spanCount)
                }
                staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions)
                lastVisibleItemPosition = findMax(lastPositions!!)
                staggeredGridLayoutManager.findFirstCompletelyVisibleItemPositions(lastPositions)
                firstVisibleItemPosition = findMax(lastPositions!!)
            }
        }

        // 根据类型来计算出第一个可见的item的位置，由此判断是否触发到底部的监听器
        // 计算并判断当前是向上滑动还是向下滑动
        calculateScrollUpOrDown(firstVisibleItemPosition, dy)
        // 移动距离超过一定的范围，我们监听就没有啥实际的意义了
        mScrolledXDistance += dx
        mScrolledYDistance += dy
        mScrolledXDistance = if (mScrolledXDistance < 0) 0 else mScrolledXDistance
        mScrolledYDistance = if (mScrolledYDistance < 0) 0 else mScrolledYDistance
        if (mIsScrollDown && dy == 0) {
            mScrolledYDistance = 0
        }
        //Be careful in here
        if (null != mLScrollListener) {
            mLScrollListener!!.onScrolled(mScrolledXDistance, mScrolledYDistance)
        }

        if (mLoadMoreListener != null && mLoadMoreEnabled) {
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            if (visibleItemCount > 0
                    && lastVisibleItemPosition >= totalItemCount - 1
                    && totalItemCount > visibleItemCount
                    && !isNoMore
                    && !mRefreshing) {

                mFootView!!.visibility = View.VISIBLE
                if (!mLoadingData) {
                    mLoadingData = true
                    mLoadMoreFooter!!.onLoading()
                    mLoadMoreListener!!.onLoadMore()
                }

            }

        }

    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        currentScrollState = state

        if (mLScrollListener != null) {
            mLScrollListener!!.onScrollStateChanged(state)
        }


    }

    /**
     * 计算当前是向上滑动还是向下滑动
     */
    private fun calculateScrollUpOrDown(firstVisibleItemPosition: Int, dy: Int) {
        if (null != mLScrollListener) {
            if (firstVisibleItemPosition == 0) {
                if (!mIsScrollDown) {
                    mIsScrollDown = true
                    mLScrollListener!!.onScrollDown()
                }
            } else {
                if (mDistance > HIDE_THRESHOLD && mIsScrollDown) {
                    mIsScrollDown = false
                    mLScrollListener!!.onScrollUp()
                    mDistance = 0
                } else if (mDistance < -HIDE_THRESHOLD && !mIsScrollDown) {
                    mIsScrollDown = true
                    mLScrollListener!!.onScrollDown()
                    mDistance = 0
                }
            }
        }

        if (mIsScrollDown && dy > 0 || !mIsScrollDown && dy < 0) {
            mDistance += dy
        }
    }

    enum class LayoutManagerType {
        LinearLayout,
        StaggeredGridLayout,
        GridLayout
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //解决LRecyclerView与CollapsingToolbarLayout滑动冲突的问题
        var appBarLayout: AppBarLayout? = null
        var p: ViewParent? = parent
        while (p != null) {
            if (p is CoordinatorLayout) {
                break
            }
            p = p.parent
        }
        if (null != p && p is CoordinatorLayout) {
            val coordinatorLayout = p as CoordinatorLayout?
            val childCount = coordinatorLayout!!.childCount
            for (i in childCount - 1 downTo 0) {
                val child = coordinatorLayout.getChildAt(i)
                if (child is AppBarLayout) {
                    appBarLayout = child
                    break
                }
            }
            if (appBarLayout != null) {
                appBarLayout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
                    override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                        appbarState = state
                    }
                })
            }
        }
    }

    companion object {
        private val DRAG_RATE = 2.0f

        /**
         * 触发在上下滑动监听器的容差距离
         */
        private val HIDE_THRESHOLD = 20
    }

    private fun getIsNestedScrolled():Boolean{
        val property = this::class.java.superclass.getDeclaredField( "mNestedOffsets")
        property?.apply {
            isAccessible =true
            var array = get(this@GRecyclerView)
            if(array is IntArray){
                val num = array[1]
                val result = num != 0
                isAccessible =false
                return result
            }
        }
        return false
    }
}
