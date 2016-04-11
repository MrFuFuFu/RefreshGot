package mrfu.swiperefreshboth.lib;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by MrFu on 16/3/18.
 */
public class LxListView extends ListView {
    public LxListView(Context context) {
        super(context);
    }

    public LxListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LxListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LxListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setAdapter(LxRefresh lxRefresh, ListAdapter adapter) {
        if (lxRefresh.mListViewFooter != null){
            addFooterView(lxRefresh.mListViewFooter);//一定要先添加一次，不然会报错，setAdapter 之后 remove 掉   看这里：http://blog.csdn.net/Take_all/article/details/7635116
        }
        super.setAdapter(adapter);
        if (lxRefresh.mListViewFooter != null){
            removeFooterView(lxRefresh.mListViewFooter);
        }
    }

    @Override
    public void setAdapter(ListAdapter adapter){
        throw new IllegalArgumentException("Please call setAdapter(LxRefresh lxRefresh, ListAdapter adapter) method");
//        super.setAdapter(adapter);
    }
}
