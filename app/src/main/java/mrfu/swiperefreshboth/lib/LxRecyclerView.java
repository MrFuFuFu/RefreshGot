package mrfu.swiperefreshboth.lib;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import mrfu.swiperefreshboth.lib.recycler.HeaderViewRecyclerAdapter;
import mrfu.swiperefreshboth.lib.utils.ViewUtils;

/**
 * Created by MrFu on 16/3/18.
 */
public class LxRecyclerView extends RecyclerView {

    private HeaderViewRecyclerAdapter mHeaderViewRecyclerAdapter;

    public LxRecyclerView(Context context) {
        super(context);
    }

    public LxRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LxRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mHeaderViewRecyclerAdapter = new HeaderViewRecyclerAdapter(adapter);
        super.setAdapter(mHeaderViewRecyclerAdapter);
        createLoadMoreView();
    }

    private void createLoadMoreView() {
        mHeaderViewRecyclerAdapter.addFooterView(ViewUtils.initFooterView(getContext()));
    }

    public HeaderViewRecyclerAdapter getHeaderViewRecyclerAdapter(){
        return mHeaderViewRecyclerAdapter;
    }

    public void setVisibilityFooterView(int visibility){
        mHeaderViewRecyclerAdapter.setVisibilityFooterView(visibility);
    }
}
