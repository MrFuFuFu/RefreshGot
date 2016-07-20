package mrfu.refreshgot;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import mrfu.refreshgot.recycler.HeaderViewRecyclerAdapter;
import mrfu.refreshgot.utils.FooterView;


/**
 * Created by MrFu on 16/3/18.
 */
public class GotRecyclerView extends RecyclerView {

    private HeaderViewRecyclerAdapter mHeaderViewRecyclerAdapter;

    public GotRecyclerView(Context context) {
        super(context);
    }

    public GotRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GotRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mHeaderViewRecyclerAdapter = new HeaderViewRecyclerAdapter(adapter);
        super.setAdapter(mHeaderViewRecyclerAdapter);
        createLoadMoreView();
    }

    private void createLoadMoreView() {
        mHeaderViewRecyclerAdapter.setFooterView(new FooterView(getContext()));
    }

    public HeaderViewRecyclerAdapter getHeaderViewRecyclerAdapter(){
        return mHeaderViewRecyclerAdapter;
    }

    public void setVisibilityFooterView(int visibility){
        mHeaderViewRecyclerAdapter.setVisibilityFooterView(visibility);
    }

    public void theEnd(boolean isEnd){
        mHeaderViewRecyclerAdapter.theEnd(isEnd);
    }

    public void setAttributeSet(AttributeSet attributeSet){
        mHeaderViewRecyclerAdapter.setAttributeSet(attributeSet);
    }

}
