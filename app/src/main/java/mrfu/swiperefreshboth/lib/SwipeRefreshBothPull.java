package mrfu.swiperefreshboth.lib;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import mrfu.swiperefreshlayoutpulltopbottom.R;

/**
 * Created by MrFu on 15/6/12.
 */
public class SwipeRefreshBothPull extends SwipeRefreshLayout implements OnScrollListener {

    /**
     * 滑动到最下面时的上拉操作
     */

    private int mTouchSlop;
    /**
     * listview实例
     */
    private ListView mListView;

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

    private PullRefreshListener mPullRefreshListener;

    /**
     * @param context
     */
    public SwipeRefreshBothPull(Context context) {
        this(context, null);
    }

    public SwipeRefreshBothPull(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mListViewFooter = LayoutInflater.from(context).inflate(R.layout.listview_footer, null, false);

        initColor();
    }

    /**
     * 初始化各项颜色，直接在这里配置完成，后续不需要修改
     */
    private void initColor() {
        //设置下拉刷新时圈圈的主题色
//        setColorSchemeResources(R.color.theme_color_primary,
//                android.R.color.holo_green_light, android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);
        //设置上拉加载时 footer 的背景色
        setFooterViewBackground(R.color.color_background);
    }

    public void setOnPullRefreshListener(PullRefreshListener listener){
        this.mPullRefreshListener = listener;
        setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullRefreshListener.onPullDownRefresh();
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // 初始化ListView对象
        if (mListView == null) {
            getListView();
        }
    }

    /**
     * 获取ListView对象
     */
    private void getListView() {
        int childs = getChildCount();
        if (childs > 0) {
            View childView = getChildAt(0);
            if (childView instanceof ListView) {
                mListView = (ListView) childView;
                // 设置滚动监听器给ListView, 使得滚动的情况下也可以自动加载
                mListView.setOnScrollListener(this);
                Log.d(VIEW_LOG_TAG, "### 找到listview");
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see android.view.ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
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

            case MotionEvent.ACTION_UP:
                // 抬起
                if (canLoad()) {
                    loadData();
                }
                break;
            default:
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    /**
     * 是否可以加载更多, 条件是到了最底部, listview不在加载中, 且为上拉操作.
     *
     * @return
     */
    private boolean canLoad() {
        return isBottom() && !isLoading && isPullUp() && !super.isRefreshing();
    }

    /**
     * 判断是否到了最底部
     */
    private boolean isBottom() {
        if (mListView != null && mListView.getAdapter() != null) {
            return mListView.getLastVisiblePosition() == (mListView.getAdapter().getCount() - 1);
        }
        return false;
    }

    /**
     * 是否是上拉操作
     *
     * @return
     */
    private boolean isPullUp() {
        return (mYDown - mLastY) >= mTouchSlop;
    }

    /**
     * 如果到了最底部,而且是上拉操作.那么执行onPullUpRefresh方法
     */
    private void loadData() {
        if (mPullRefreshListener != null) {
            // 设置状态
            setLoading(true);
            mPullRefreshListener.onPullUpRefresh();
        }
    }

    public void setFooterViewBackground(int colorRes) {
        if (mListViewFooter != null) {
            mListViewFooter.setBackgroundColor(getResources().getColor(colorRes));
        }
    }

    /**
     * @param loading
     */
    public void setLoading(boolean loading) {
        isLoading = loading;
        if (isLoading) {
            if (mListView != null) {
                mListView.addFooterView(mListViewFooter);
            }
        } else {
            if (mListView != null) {
                mListView.removeFooterView(mListViewFooter);
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
        // 滚动时到了最底部也可以加载更多
        if (canLoad()) {
            loadData();
        }
        if (onListViewScrollListener != null){
            onListViewScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    /**
     * 结束刷新
     */
    public void refreshReset() {
        setRefreshing(false);
        setLoading(false);
    }

    public interface OnListViewScrollListener{
        void onScrollStateChanged(AbsListView view, int scrollState);
        void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
    }

    OnListViewScrollListener onListViewScrollListener;

    public void setOnListViewScrollListener(OnListViewScrollListener listener){
        onListViewScrollListener = listener;
    }
}
