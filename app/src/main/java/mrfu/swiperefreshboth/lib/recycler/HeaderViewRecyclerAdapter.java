package mrfu.swiperefreshboth.lib.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mrfu.swiperefreshboth.lib.utils.FooterView;

/**
 * http://www.easydone.cn/2015/10/26/
 * Created by Android Studio
 * User: Ailurus(ailurus@foxmail.com)
 * Date: 2015-10-26
 * Time: 18:23
 */
public class HeaderViewRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int HEADERS_START = Integer.MIN_VALUE;
    private static final int FOOTERS_START = Integer.MIN_VALUE + 10;
    private static final int ITEMS_START = Integer.MIN_VALUE + 20;
    private static final int ADAPTER_MAX_TYPES = 100;

    private RecyclerView.Adapter mWrappedAdapter;
    private List<View> mHeaderViews, mFooterViews;
    private FooterView mFooterView;
    private Map<Class, Integer> mItemTypesOffset;

    public HeaderViewRecyclerAdapter(RecyclerView.Adapter adapter) {
        mHeaderViews = new ArrayList<>();
//        mFooterViews = new ArrayList<>();
        mItemTypesOffset = new HashMap<>();
        setWrappedAdapter(adapter);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        if (mWrappedAdapter != null && mWrappedAdapter.getItemCount() > 0) {
            notifyItemRangeRemoved(getHeaderCount(), mWrappedAdapter.getItemCount());
        }
        setWrappedAdapter(adapter);
        notifyItemRangeInserted(getHeaderCount(), mWrappedAdapter.getItemCount());
    }

    @Override
    public int getItemViewType(int position) {
        int hCount = getHeaderCount();
        if (position < hCount) return HEADERS_START + position;
        else {
            int itemCount = mWrappedAdapter.getItemCount();
            if (position < hCount + itemCount) {
                return getAdapterTypeOffset() + mWrappedAdapter.getItemViewType(position - hCount);
            } else return FOOTERS_START + position - hCount - itemCount;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType < HEADERS_START + getHeaderCount())
            return new StaticViewHolder(mHeaderViews.get(0));
        else if (viewType < FOOTERS_START + getFooterCount())
            return new StaticViewHolder(mFooterView);
        else {
            return mWrappedAdapter.onCreateViewHolder(viewGroup, viewType - getAdapterTypeOffset());
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int hCount = getHeaderCount();
        if (position >= hCount && position < hCount + mWrappedAdapter.getItemCount())
            mWrappedAdapter.onBindViewHolder(viewHolder, position - hCount);
    }

    public void addHeaderView(View view) {
        mHeaderViews.add(view);
    }
//
//    public void addFooterView(View view) {
//        mFooterViews.add(view);
//    }

    public void setFooterView(FooterView footerView){
        mFooterView = footerView;
    }

    public void setVisibilityFooterView(int visibility) {
        mFooterView.setVisibility(visibility);
    }

    public void theEnd(boolean isEnd){
        if (mFooterView != null){
            mFooterView.theEnd(isEnd);
        }
    }

    /**
     *
     * @param color R.color.color_xxx
     */
    public void modifyFooterViewBackgroundColor(int color){
        if (mFooterView != null){
            mFooterView.modifyFooterViewBackgroundColor(color);
        }
    }

    public void modifyFooterViewText(String text){
        if (mFooterView != null){
            mFooterView.modifyFooterViewText(text);
        }
    }

    @Override
    public int getItemCount() {
        return getHeaderCount() + getFooterCount() + getWrappedItemCount();
    }

    public int getWrappedItemCount() {
        return mWrappedAdapter.getItemCount();
    }

    public int getHeaderCount() {
        return mHeaderViews.size();
    }

    public int getFooterCount() {
        if (mFooterView != null){
            return 1;
        }else {
            return 0;
        }
    }


    private void setWrappedAdapter(RecyclerView.Adapter adapter) {
        if (mWrappedAdapter != null) mWrappedAdapter.unregisterAdapterDataObserver(mDataObserver);
        mWrappedAdapter = adapter;
        Class adapterClass = mWrappedAdapter.getClass();
        if (!mItemTypesOffset.containsKey(adapterClass)) putAdapterTypeOffset(adapterClass);
        mWrappedAdapter.registerAdapterDataObserver(mDataObserver);
    }


    private void putAdapterTypeOffset(Class adapterClass) {
        mItemTypesOffset.put(adapterClass, ITEMS_START + mItemTypesOffset.size() * ADAPTER_MAX_TYPES);
    }


    private int getAdapterTypeOffset() {
        return mItemTypesOffset.get(mWrappedAdapter.getClass());
    }


    private RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            notifyItemRangeChanged(positionStart + getHeaderCount(), itemCount);
        }


        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            notifyItemRangeInserted(positionStart + getHeaderCount(), itemCount);
        }


        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            notifyItemRangeRemoved(positionStart + getHeaderCount(), itemCount);
        }


        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            int hCount = getHeaderCount();
            notifyItemRangeChanged(fromPosition + hCount, toPosition + hCount + itemCount);
        }
    };

    private static class StaticViewHolder extends RecyclerView.ViewHolder {
        public StaticViewHolder(View itemView) {
            super(itemView);
        }
    }



//    private static final int FOOTERS_START = Integer.MIN_VALUE + 10;
//    private static final int ITEMS_START = Integer.MIN_VALUE + 20;
//    private static final int ADAPTER_MAX_TYPES = 100;
//
//    private RecyclerView.Adapter mWrappedAdapter;
//    private FooterView mFooterView;
//    private Map<Class, Integer> mItemTypesOffset;
//
//    public HeaderViewRecyclerAdapter(RecyclerView.Adapter adapter) {
//        mItemTypesOffset = new HashMap<>();
//        setWrappedAdapter(adapter);
//    }
//
//    public void setAdapter(RecyclerView.Adapter adapter) {
//        if (mWrappedAdapter != null && mWrappedAdapter.getItemCount() > 0) {
//            notifyItemRangeRemoved(0, mWrappedAdapter.getItemCount());
//        }
//        setWrappedAdapter(adapter);
//        notifyItemRangeInserted(0, mWrappedAdapter.getItemCount());
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        int itemCount = mWrappedAdapter.getItemCount();
//        if (position < itemCount) {
//            return getAdapterTypeOffset() + mWrappedAdapter.getItemViewType(position);
//        } else {
//            return FOOTERS_START + position - itemCount;
//        }
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
//        if (viewType < FOOTERS_START + getFooterCount())
//            return new StaticViewHolder(mFooterView);
//        else {
//            return mWrappedAdapter.onCreateViewHolder(viewGroup, viewType - getAdapterTypeOffset());
//        }
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
//        if (position < mWrappedAdapter.getItemCount())
//            mWrappedAdapter.onBindViewHolder(viewHolder, position);
//    }
//
//    public void setFooterView(FooterView footerView) {
//        mFooterView = footerView;
//    }
//
//    public void setVisibilityFooterView(int visibility) {
//        if (mFooterView != null){
//            mFooterView.setVisibility(visibility);
//        }
//    }
//
//    public void theEnd(boolean isEnd){
//        if (mFooterView != null){
//            mFooterView.theEnd(isEnd);
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return getFooterCount() + getWrappedItemCount();
//    }
//
//    public int getWrappedItemCount() {
//        return mWrappedAdapter.getItemCount();
//    }
//
//    public int getFooterCount() {
//        if (mFooterView != null){
//            return 1;
//        }else {
//            return 0;
//        }
//    }
//
//
//    private void setWrappedAdapter(RecyclerView.Adapter adapter) {
//        if (mWrappedAdapter != null) mWrappedAdapter.unregisterAdapterDataObserver(mDataObserver);
//        mWrappedAdapter = adapter;
//        Class adapterClass = mWrappedAdapter.getClass();
//        if (!mItemTypesOffset.containsKey(adapterClass)) putAdapterTypeOffset(adapterClass);
//        mWrappedAdapter.registerAdapterDataObserver(mDataObserver);
//    }
//
//
//    private void putAdapterTypeOffset(Class adapterClass) {
//        mItemTypesOffset.put(adapterClass, ITEMS_START + mItemTypesOffset.size() * ADAPTER_MAX_TYPES);
//    }
//
//
//    private int getAdapterTypeOffset() {
//        return mItemTypesOffset.get(mWrappedAdapter.getClass());
//    }
//
//
//    private RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
//        @Override
//        public void onChanged() {
//            super.onChanged();
//            notifyDataSetChanged();
//        }
//
//        @Override
//        public void onItemRangeChanged(int positionStart, int itemCount) {
//            super.onItemRangeChanged(positionStart, itemCount);
//            notifyItemRangeChanged(positionStart, itemCount);
//        }
//
//
//        @Override
//        public void onItemRangeInserted(int positionStart, int itemCount) {
//            super.onItemRangeInserted(positionStart, itemCount);
//            notifyItemRangeInserted(positionStart, itemCount);
//        }
//
//
//        @Override
//        public void onItemRangeRemoved(int positionStart, int itemCount) {
//            super.onItemRangeRemoved(positionStart, itemCount);
//            notifyItemRangeRemoved(positionStart, itemCount);
//        }
//
//
//        @Override
//        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
//            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
//            notifyItemRangeChanged(fromPosition, toPosition + itemCount);
//        }
//    };
//
//    private static class StaticViewHolder extends RecyclerView.ViewHolder {
//        public StaticViewHolder(View itemView) {
//            super(itemView);
//        }
//    }
}
