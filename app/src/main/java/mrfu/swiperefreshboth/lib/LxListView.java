package mrfu.swiperefreshboth.lib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ListAdapter;
import android.widget.ListView;

import mrfu.swiperefreshboth.lib.utils.ViewUtils;

/**
 * Created by MrFu on 16/3/18.
 */
public class LxListView extends ListView implements LxRefresh.SomeTouchListener {
    /**
     * 预定义高度
     */
    private static final int PRE_HEIGHT = 250;
    private final View mListViewFooter;

    private LxRefresh mLxRefresh;
    /**
     * 是否在加载中 ( 上拉加载更多 )
     */
    private boolean isLoading = false;

    private float mLastMotionX;
    private float mLastMotionY;

    private float ly;
    private float lx;
    /**
     * 滑动到最下面时的上拉操作
     */
    private int mTouchSlop;
    /**
     * 按下时的y坐标
     */
    private int mYDown;
    /**
     * 抬起时的y坐标, 与mYDown一起用于滑动到底部时判断是上拉还是下拉
     */
    private int mLastY;

    public LxListView(Context context) {
        this(context, null);
    }

    public LxListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LxListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mListViewFooter = ViewUtils.initFooterView(context);
    }

    public void setAdapter(LxRefresh lxRefresh, ListAdapter adapter) {
        if (lxRefresh == null) return;
        mLxRefresh = lxRefresh;
        if (mListViewFooter != null){
            addFooterView(mListViewFooter);//一定要先添加一次，不然会报错，setAdapter 之后 remove 掉   看这里：http://blog.csdn.net/Take_all/article/details/7635116
        }
        super.setAdapter(adapter);
        if (mListViewFooter != null){
            removeFooterView(mListViewFooter);
        }

        mLxRefresh.setSomeTouchListener(this);
    }

    @Override
    public void setAdapter(ListAdapter adapter){
        throw new IllegalArgumentException("Please call setAdapter(LxRefresh lxRefresh, ListAdapter adapter) method");
//        super.setAdapter(adapter);
    }



    @Override
    public boolean onInterceptTouchEventLxRefresh(MotionEvent event) {
        int action = event.getAction();
        if (isReadyForPullUp() && !isLoading && !mLxRefresh.isRefreshing()){// read to refresh
            float x = event.getX();
            float y = event.getY();
            switch (action) {
                case MotionEvent.ACTION_MOVE:{
                    if (y < mLastMotionY) {
                        if(mLxRefresh.doOnPullUpRefresh()){
                            setLvLoading(true);
                        }
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

        //处理 viewpager 的事件冲突
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lx = event.getRawX();
                ly = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mLxRefresh != null && !mLxRefresh.canChildScrollUp()) {
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
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEventLxRefresh(MotionEvent event) {
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
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEventLxRefresh(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_MOVE:
                mLastMotionY = ev.getY();
                break;
        }
        return super.onTouchEvent(ev);
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

    public boolean isReadyForPullUp(){
        int count = getCount();
        if(getLastVisiblePosition() >= count - 2){
            final int childIndex = getLastVisiblePosition() - getFirstVisiblePosition();
            final View lastVisibleChild = getChildAt(childIndex);
            if (lastVisibleChild != null) {
                return lastVisibleChild.getBottom() - PRE_HEIGHT <= getBottom()-getTop();
            }
        }
        return false;
    }

    /**
     * ListView
     * @param loading
     */
    public void setLvLoading(boolean loading) {
        isLoading = loading;
        if (isLoading) {
            addFooterView(mListViewFooter);
        } else {
            removeFooterView(mListViewFooter);
            mYDown = 0;
            mLastY = 0;
        }
    }

    /**
     * ListView
     * 判断是否到了最底部
     */
    private boolean isLvBottom() {
        if (getAdapter() != null) {
            boolean isBottom = getLastVisiblePosition() == (getAdapter().getCount() - 1);
            return isBottom;
        }
        return false;
    }

    public boolean canLoad(){
        return !isLoading && isLvPullUp() && isReadyForPullUp();
    }

    /**
     * ListView
     * currently, only support setting of listview background
     * @param colorRes
     */
    public void setLvFooterViewBackground(int colorRes) {
        if (mListViewFooter != null) {
            mListViewFooter.setBackgroundColor(getResources().getColor(colorRes));
        }
    }
}
