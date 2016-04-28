package mrfu.swiperefreshboth.lib;

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
import android.view.ViewConfiguration;
import android.widget.AbsListView;

import mrfu.swiperefreshboth.R;
import mrfu.swiperefreshboth.lib.recycler.EndlessRecyclerOnScrollListener;
import mrfu.swiperefreshboth.lib.utils.PullRefreshListener;
import mrfu.swiperefreshboth.lib.utils.ViewUtils;

/**
 * Created by MrFu on 16/3/18.
 */
public class LxRefresh extends SwipeRefreshLayout implements AbsListView.OnScrollListener {
    private PullRefreshListener mPullRefreshListener;
    private LxListView mLxListView;
    private LxRecyclerView mLxRecyclerView;
    /**
     * 0: ListView
     * 1: RecyclerView
     */
    private int mViewType = 0;


    /**
     * 滑动到最下面时的上拉操作
     */

    private int mTouchSlop;

    /**
     * ListView的加载中footer
     */
    public View mListViewFooter;

    /**
     * 按下时的y坐标
     */
    private int mYDown;
    /**
     * 抬起时的y坐标, 与mYDown一起用于滑动到底部时判断是上拉还是下拉
     */
    private int mLastY;
    /**
     * 是否在加载中 ( 上拉加载更多 )
     */
    private boolean isLoading = false;

    /**
     * 只有下拉刷新, 无加载更多
     */
    private boolean mNoLoadMore = false;
    private EndlessRecyclerOnScrollListener mEndlessRecyclerOnScrollListener;


    public LxRefresh(Context context) {
        this(context, null);
    }

    public LxRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (null != attrs) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LxRefresh);
            mLoarMoreEnable = ta.getBoolean(R.styleable.LxRefresh_loadmoreable, false);
            ta.recycle();
        }

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
//        mListViewFooter = LayoutInflater.from(context).inflate(R.layout.listview_footer, null, false);
        mListViewFooter = ViewUtils.initFooterView(context);
        initColor();

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

    /**
     * All
     */
    private void initColor() {
        setColorSchemeResources(R.color.circular_progress_color,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        setLvFooterViewBackground(R.color.color_background);
    }

    /**
     * All
     */
    private void initRefreshChildView() {

        if (mLxListView != null || mLxRecyclerView != null){
            return;
        }
        final int childs = getChildCount();

        for (int i = childs-1; i >= 0; i--) {
            final View childView = getChildAt(i);
            if (childView instanceof LxListView) {
                mViewType = 0;
                initLxListView(childView);
                break;
            }else if (childView instanceof LxRecyclerView){
                mViewType = 1;
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
        if (mLxListView != null) return;
        mLxListView = (LxListView) childView;
        // 设置滚动监听器给ListView, 使得滚动的情况下也可以自动加载
        mLxListView.setOnScrollListener(this);
    }

    /**
     * RecyclerView
     * @param childView
     */
    private void initLxRecyclerView(View childView){
        if (mLxRecyclerView != null) return;
        mLxRecyclerView = (LxRecyclerView) childView;
        RecyclerView.LayoutManager layoutManager = mLxRecyclerView.getLayoutManager();
        if (layoutManager == null) {
            Log.e("LxRefresh", "layoutManager == null");
            return;
        }

        if(layoutManager instanceof GridLayoutManager){//GridLayoutManager extends LinearLayoutManager
            final GridLayoutManager gridLayoutManager = (GridLayoutManager)layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return ((mLxRecyclerView.getHeaderViewRecyclerAdapter().getItemCount() - 1) == position) ? gridLayoutManager.getSpanCount() : 1;
                }
            });
        }
        mEndlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener((LinearLayoutManager)layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                if (mNoLoadMore){//没有加载更多
                    if (mViewType == 1 && mLxRecyclerView != null){//RecyclerView
                        mLxRecyclerView.setVisibilityFooterView(View.GONE);
                    }
                    return;
                }
                if (mIsRvLastPage) return;//最后一页
                mPullRefreshListener.onPullUpRefresh();
            }
        };
        mLxRecyclerView.addOnScrollListener(mEndlessRecyclerOnScrollListener);
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
            mIsRvLastPage = false;
            if (mViewType == 1 && mEndlessRecyclerOnScrollListener != null){
                mEndlessRecyclerOnScrollListener.setNewRefresh();
            }
            //update by yifeiyuan
            if (mViewType == 1 && mLxRecyclerView != null && !mNoLoadMore&&mLoarMoreEnable){
                mLxRecyclerView.setVisibilityFooterView(View.VISIBLE);
            }
            mPullRefreshListener.onPullDownRefresh();
        }
    };



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
        if (mViewType == 0){//only listview need dispatchTouchEvent
            final int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    // 按下
                    mYDown = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    // 移动
                    mLastY = (int) event.getRawY();
                    break;
//                case MotionEvent.ACTION_UP:
//                    // 抬起
//                    if (canLvLoad()) {
//                        loadLvData();
//                    }
//                    break;
                default:
                    break;
            }
        }

        return super.dispatchTouchEvent(event);
    }

    private float mLastMotionX;
    private float mLastMotionY;

    private float ly;
    private float lx;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        int action = event.getAction();

        if (mViewType == 0){

            if (isReadyForPullUp() && !isLoading && !super.isRefreshing()){// read to refresh
//                int action = event.getAction();
                float x = event.getX();
                float y = event.getY();
                switch (action) {
                    case MotionEvent.ACTION_MOVE:{
                        if (y < mLastMotionY) {
                            loadLvData();
                        }
                        break;
                    }
                    case MotionEvent.ACTION_DOWN: {
                        mLastMotionY = event.getY();
                        mLastMotionX = event.getX();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        break;
                }

            }

            //处理 viewpager 的时间冲突
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    lx = event.getRawX();
                    ly = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!canChildScrollUp()) {
                        float dy = event.getRawY() - ly;
                        float dx = event.getRawX() - lx;
                        if (dy > 0 && Math.abs(dy) > Math.abs(dx)) {
                            return super.onInterceptTouchEvent(event);
                        } else {
                            return false;
                        }
                    }
                    break;
            }

        }

        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mViewType == 0){
            switch (ev.getAction()){
                case MotionEvent.ACTION_MOVE:
                    mLastMotionY = ev.getY();
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }

//    /**
//     * ListView
//     * 是否可以加载更多, 条件是到了最底部, listview不在加载中, 且为上拉操作.
//     *
//     * @return
//     */
//    private boolean canLvLoad() {
//        return isLvBottom() && !isLoading && isLvPullUp() && !super.isRefreshing();
//    }

    /**
     * ListView
     * 判断是否到了最底部
     */
    private boolean isLvBottom() {
        if (mLxListView != null && mLxListView.getAdapter() != null) {
            boolean isBottom = mLxListView.getLastVisiblePosition() == (mLxListView.getAdapter().getCount() - 1);
            return isBottom;
        }
        return false;
    }

    /**
     * ListView
     * 是否是上拉操作
     *
     * @return
     */
    private boolean isLvPullUp() {
        return (mYDown - mLastY) >= mTouchSlop;
    }

    private boolean isReadyForPullUp(){
        if(mLxListView != null){
            int count = mLxListView.getCount();
            if(mLxListView.getLastVisiblePosition() >= count - 2){
                final int childIndex = mLxListView.getLastVisiblePosition() - mLxListView.getFirstVisiblePosition();
                final View lastVisibleChild = mLxListView.getChildAt(childIndex);
                if (lastVisibleChild != null) {
                    return lastVisibleChild.getBottom() - PRE_HEIGHT <= mLxListView.getBottom()-mLxListView.getTop();
                }
            }
        }
        return false;
    }

    /**
     * 预定义高度
     */
    private static final int PRE_HEIGHT = 250;

    /**
     * ListView
     * 如果到了最底部,而且是上拉操作.那么执行onPullUpRefresh方法
     */
    private void loadLvData() {
        if (mPullRefreshListener != null) {
            // 设置状态
            if (!mNoLoadMore){
                setLvLoading(true);
                mPullRefreshListener.onPullUpRefresh();
            }
        }
    }

    /**
     * All
     * 设置不加载更多, 只有下拉刷新
     */
    public void setNoLoadMore(){
        mNoLoadMore = true;
    }

    /**
     * ListView
     * currently, only support setting of listview background
     * @param colorRes
     */
    public void setLvFooterViewBackground(int colorRes) {
        if (mViewType == 0 && mListViewFooter != null) {
            mListViewFooter.setBackgroundColor(getResources().getColor(colorRes));
        }
    }

    /**
     * ListView
     * @param loading
     */
    public void setLvLoading(boolean loading) {
        isLoading = loading;
        if (isLoading) {
            if (mLxListView != null) {
                mLxListView.addFooterView(mListViewFooter);
            }
        } else {
            if (mLxListView != null) {
                mLxListView.removeFooterView(mListViewFooter);
                mYDown = 0;
                mLastY = 0;
            }
        }
    }

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

        if (mViewType == 0  && !isLoading && isLvPullUp() && isReadyForPullUp() && !super.isRefreshing() && !mNoLoadMore){
            loadLvData();
        }
//        // 滚动时到了最底部也可以加载更多
//        if (canLvLoad() && mViewType == 0) {
//            loadLvData();
//        }
    }

    /**
     * All
     * 结束刷新
     */
    public void refreshReset() {
        setRefreshing(false);
        setLvLoading(false);
    }

    private boolean mLoarMoreEnable = true;
    public void setLoadMoreEnable(boolean loadMoreEnable) {
        mLoarMoreEnable = loadMoreEnable;
        if (mLxRecyclerView != null){
            mLxRecyclerView.setVisibilityFooterView(loadMoreEnable?View.VISIBLE:View.GONE);
        }
        mIsRvLastPage = !loadMoreEnable;
    }

    public void setLoadMoreComplete() {
        if (null != mEndlessRecyclerOnScrollListener) {
            mEndlessRecyclerOnScrollListener.setLoadMoreComplete();
        }
    }

    /**
     * RecyclerView
     * 是否是最后一页
     */
    private boolean mIsRvLastPage = false;

    public interface OnListViewScrollListener{
        void onScrollStateChanged(AbsListView view, int scrollState);
        void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
    }

    OnListViewScrollListener onListViewScrollListener;

    public void setOnListViewScrollListener(OnListViewScrollListener listener){
        onListViewScrollListener = listener;
    }
}
