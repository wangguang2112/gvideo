package com.wang.gvideo.migu.ui.view;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;

/**
 * Created by wangguang.
 * Date:2017/2/13
 * Description:使用LinearLayout代替ListView，数据量少的时候推荐使用。
 */

public class ListItemView extends LinearLayout {

    static final String TAG = "ListItemView";

    ViewAdapter adapter;

    OnItemClickListener mOnItemClickListener;

    boolean isHasDivider = false;

    LinearLayout.LayoutParams mDividerParams;

    Drawable dividerDrawable;

    View.OnClickListener mCommListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                ViewGroup group = (ViewGroup) v.getParent();
                if (group != null) {
                    int pos = group.indexOfChild(v);
                    if (pos != -1) {
                        mOnItemClickListener.onItemClick(v, isHasDivider ? pos / 2 : pos);
                    }
                }
            }
        }
    };

    public ListItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public ListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ListItemView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
    }

    public void setAdapter(ViewAdapter adapter) {
        this.adapter = adapter;
        adapter.setListItemView(this);
        processAddViews(adapter);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    private void processAddViews(ViewAdapter adapter) {
        this.removeAllViews();
        for (int i = 0; i < adapter.getCount(); i++) {
            View view = adapter.getView(i, this);
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            LinearLayout.LayoutParams params;
            if (view.getLayoutParams() != null && view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
                if (view.getLayoutParams().height == ViewGroup.LayoutParams.MATCH_PARENT) {
                    params = (LinearLayout.LayoutParams) view.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                } else {
                    params = (LinearLayout.LayoutParams) view.getLayoutParams();
                }
            } else {
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            addView(view, params);
            view.setOnClickListener(mCommListener);
            if (isHasDivider && i != adapter.getCount() - 1) {
                View dividerView = new View(getContext());
                if (dividerDrawable != null) {
                    dividerView.setBackgroundDrawable(dividerDrawable);
                }
                if (mDividerParams != null) {
                    addView(dividerView, mDividerParams);
                } else {
                    addView(dividerView);
                }
            }
        }
    }

    /**
     * 创建间隔
     */
    public void setDivider(@Nullable Drawable divider, LinearLayout.LayoutParams params) {
        isHasDivider = true;
        dividerDrawable = divider;
        mDividerParams = params;
        if (adapter != null) {
            processAddViews(adapter);
        }
    }

    /**
     * 创建间隔
     */
    public void setDivider(@Nullable Drawable divider, int height) {
        mDividerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        setDivider(divider, mDividerParams);
    }

    private void addOneView(int position) {
        View view = adapter.getView(position, this);
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        LinearLayout.LayoutParams params;
        if (view.getLayoutParams() != null && view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            if (view.getLayoutParams().height == ViewGroup.LayoutParams.MATCH_PARENT) {
                params = (LinearLayout.LayoutParams) view.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            } else {
                params = (LinearLayout.LayoutParams) view.getLayoutParams();
            }
        } else {
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        addView(view, position, params);
        view.setOnClickListener(mCommListener);
    }

    /**
     * 获得已经创建出来的view
     */
    public View getExistView(int position) {
        if (position < 0 || position >= adapter.getCount()) {
            Log.e(TAG, "getExistView: position =" + position + ",count=" + adapter.getCount());
            return null;
        }
        if (isHasDivider) {
            return this.getChildAt(position * 2);
        } else {
            return this.getChildAt(position);
        }

    }

    /**
     * adapter
     */
    public static abstract class ViewAdapter<T> {

        private int count = 0;

        private WeakReference<ListItemView> mListItemView;

        public abstract int getCount();

        public abstract T getItem(int position);

        public abstract View getView(int position, ViewGroup parent);

        public void addPosView(int position) {
            if (count != getCount()) {
                mListItemView.get()
                             .addOneView(position);
            }
        }

        /**
         * 获取已经创建出来的view
         */
        public View getUpdateView(int position) {
            if (mListItemView == null) {
                throw new IllegalStateException("please after setAdapter");
            }
            return mListItemView.get()
                                .getExistView(position);
        }

        private void setListItemView(ListItemView view) {
            this.mListItemView = new WeakReference<>(view);
            count = getCount();
        }

        /**
         * 局部更新（不涉及添加）
         */
        public abstract void updateView(int position);

        /**
         * 完全更新（添加删除条目）
         */
        public void updateAll() {
            mListItemView.get()
                         .processAddViews(this);
        }

    }

    public interface OnItemClickListener {

        void onItemClick(View view, int position);
    }
}
