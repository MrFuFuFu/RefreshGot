package mrfu.refreshgot;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;

import mrfu.refreshgot.recycler.EndlessRecyclerOnScrollListener;
import mrfu.refreshgot.utils.PullRefreshListener;

/**
 * An pull down refresh and pull up refresh for `ListView` and `RecyclerView`.
 * Created by MrFu on 16/3/18.
 */
public class GotRefresh extends SwipeRefreshLayout {
    enum ViewType{
        List, Recycler
    }

    ViewType mViewType;

    private PullRefreshListener mPullRefreshListener;
    private GotListView mGotListView;
    private GotRecyclerView mGotRecyclerView;
    private AttributeSet mAttributeSet;

    /**
     * 只有下拉刷新, 无加载更多
     */
    private boolean mNoLoadMore = false;

    private EndlessRecyclerOnScrollListener mEndlessRecyclerOnScrollListener;

    public GotRefresh(Context context) {
        this(context, null);
    }

    public GotRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (null != attrs) {
            mAttributeSet = attrs;
            TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.GotRefresh);
            mLoarMoreEnable = ta.getBoolean(R.styleable.GotRefresh_loadmoreable, true);
            ta.recycle();
        }
        setColorSchemeResources(R.color.footer_loading_color,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    /**
     * All
     */
    public void setRefreshing(){
        if (mPullRefreshListener != null){
            mPullRefreshListener.onPullDownRefresh();
        }
        setRefreshing(true);
    }

    private void initRefreshChildView() {
        if (mGotListView != null || mGotRecyclerView != null){
            return;
        }
        final int childs = getChildCount();

        for (int i = childs-1; i >= 0; i--) {
            final View childView = getChildAt(i);
            if (childView instanceof GotListView) {
                mViewType = ViewType.List;
                initLxListView(childView);
                break;
            }else if (childView instanceof GotRecyclerView){
                mViewType = ViewType.Recycler;
                initLxRecyclerView(childView);
                break;
            }
        }
    }

    /**
     * ListView
     * @param childView
     */
    private void initLxListView(View childView){
        if (mGotListView != null) return;
        mGotListView = (GotListView) childView;
        mGotListView.setAttributeSet(mAttributeSet);
        // 设置滚动监听器给ListView, 使得滚动的情况下也可以自动加载
        mGotListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (onListViewScrollListener != null){
                    onListViewScrollListener.onScrollStateChanged(view, scrollState);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (onListViewScrollListener != null){
                    onListViewScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }

                if (mViewType == ViewType.List  && mGotListView.canLoad() && !isRefreshing() && !mNoLoadMore){
                    doOnPullUpRefresh();
                }
            }
        });
    }

    /**
     * RecyclerView
     * @param childView
     */
    private void initLxRecyclerView(View childView){
        if (mGotRecyclerView != null) return;
        mGotRecyclerView = (GotRecyclerView) childView;
        mGotRecyclerView.setAttributeSet(mAttributeSet);
        RecyclerView.LayoutManager layoutManager = mGotRecyclerView.getLayoutManager();
        if (layoutManager == null) {
            Log.e("LxRefresh", "layoutManager == null");
            return;
        }

        if(layoutManager instanceof GridLayoutManager){
            final GridLayoutManager gridLayoutManager = (GridLayoutManager)layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return ((mGotRecyclerView.getHeaderViewRecyclerAdapter().getItemCount() - 1) == position) ? gridLayoutManager.getSpanCount() : 1;
                }
            });
        }
        mEndlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener((LinearLayoutManager)layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                if (mNoLoadMore){//没有加载更多
                    if (mViewType == ViewType.Recycler && mGotRecyclerView != null){//RecyclerView
                        mGotRecyclerView.theEnd(true);
                    }
                    return;
                }
                if (mLoarMoreEnable){
                    doOnPullUpRefresh();
                }
            }
        };
        mGotRecyclerView.addOnScrollListener(mEndlessRecyclerOnScrollListener);
        // onlayout里才初始化mLxRecyclerView 导致Loadmore提前开启
        setLoadMoreEnable(mLoarMoreEnable);
    }

    public void setOnPullRefreshListener(PullRefreshListener listener){
        mPullRefreshListener = listener;
        setOnRefreshListener(onRefreshListener);
    }

    private final OnRefreshListener onRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh() {//下拉刷新
            if (mViewType == ViewType.Recycler && mEndlessRecyclerOnScrollListener != null){
                mEndlessRecyclerOnScrollListener.setNewRefresh();
            }
            //update by yifeiyuan
            if (mViewType == ViewType.Recycler && mGotRecyclerView != null && !mNoLoadMore&&mLoarMoreEnable){
                mGotRecyclerView.theEnd(false);
            }
            mPullRefreshListener.onPullDownRefresh();
        }
    };

    /**
     * 做下拉刷新操作, 并返回成功与否
     */
    public void doOnPullUpRefresh() {
        if (mPullRefreshListener != null && !mNoLoadMore) {
            if (mViewType == ViewType.List){
                mGotListView.setLvLoading(true);
            }
            mPullRefreshListener.onPullUpRefresh();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        //init
        initRefreshChildView();
    }

    /**
     * (non-Javadoc)
     * @see android.view.ViewGroup#dispatchTouchEvent(MotionEvent)
     * @param event
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (isListViewAndTouch()){
            OnEventType onEventType = mSomeTouchListener.dispatchTouchEventLxRefresh(event);
            switch (onEventType){
                case TURE:
                    return true;
                case FALSE:
                    return false;
                case SUPER:
                    return super.dispatchTouchEvent(event);
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (isListViewAndTouch()){
            OnEventType onEventType = mSomeTouchListener.onInterceptTouchEventLxRefresh(event);
            switch (onEventType){
                case TURE:
                    return true;
                case FALSE:
                    return false;
                case SUPER:
                    return super.onInterceptTouchEvent(event);
            }
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isListViewAndTouch()){
            OnEventType onEventType = mSomeTouchListener.onTouchEventLxRefresh(ev);
            switch (onEventType){
                case TURE:
                    return true;
                case FALSE:
                    return false;
                case SUPER:
                    return super.onTouchEvent(ev);
            }
        }
        return super.onTouchEvent(ev);
    }



    private boolean isListViewAndTouch(){
        return mViewType == ViewType.List && mSomeTouchListener != null;
    }

    /**
     * All
     * 设置不加载更多, 只有下拉刷新
     */
    public void setNoLoadMore(){
        mNoLoadMore = true;
    }

    /**
     * All
     * 结束刷新
     */
    public void refreshReset() {
        if (mViewType == ViewType.List){
            mGotListView.setLvLoading(false);
        }
        setRefreshing(false);
    }

    private boolean mLoarMoreEnable = true;

    /**
     * Only RecyclerView needs to call this method, ListView doesn't needs it.
     *
     * Close pull up refresh
     * @param loadMoreEnable
     */
    public void setLoadMoreEnable(boolean loadMoreEnable) {
        mLoarMoreEnable = loadMoreEnable;
        if (mGotRecyclerView != null){
            mGotRecyclerView.theEnd(!loadMoreEnable);
        }
        if (!loadMoreEnable){
            if (null != mEndlessRecyclerOnScrollListener) {
                mEndlessRecyclerOnScrollListener.setLoadMoreComplete();
            }
        }
    }

    OnListViewScrollListener onListViewScrollListener;

    public void setOnListViewScrollListener(OnListViewScrollListener listener){
        onListViewScrollListener = listener;
    }

    SomeTouchListener mSomeTouchListener;

    public void setSomeTouchListener(SomeTouchListener listener){
        mSomeTouchListener = listener;
    }


    public interface SomeTouchListener{
        OnEventType onInterceptTouchEventLxRefresh(MotionEvent event);
        OnEventType dispatchTouchEventLxRefresh(MotionEvent event);
        OnEventType onTouchEventLxRefresh(MotionEvent ev);
    }

    public interface OnListViewScrollListener{
        void onScrollStateChanged(AbsListView view, int scrollState);
        void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
    }

    public enum OnEventType{
        TURE, FALSE, SUPER
    }

}
