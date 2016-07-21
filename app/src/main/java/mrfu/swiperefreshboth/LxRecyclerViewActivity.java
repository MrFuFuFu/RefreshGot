package mrfu.swiperefreshboth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import mrfu.refreshgot.GotRecyclerView;
import mrfu.refreshgot.GotRefresh;
import mrfu.refreshgot.utils.PullRefreshListener;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by MrFu on 16/2/19.
 */
public class LxRecyclerViewActivity extends AppCompatActivity implements PullRefreshListener {
    private List<String> mList;

    @Bind(R.id.swipe_refresh_layout)
    GotRefresh mGotRefresh;
    @Bind(R.id.recycler_view)
    GotRecyclerView mGotRecyclerView;
    private RefreshAdapter mRefreshAdapter;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lxrecyclerview);
        ButterKnife.bind(this);

        initTitleBar();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mGotRecyclerView.setHasFixedSize(true);
        mGotRecyclerView.setLayoutManager(linearLayoutManager);

        setData();
        mRefreshAdapter = new RefreshAdapter(mList, this);
        mGotRecyclerView.setAdapter(mRefreshAdapter);
        mGotRefresh.setOnPullRefreshListener(this);
        //If you only need pull down refresh, don't need pull up refresh, just call this method to close it:
//        mGotRefresh.setNoLoadMore();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initTitleBar() {
        toolbar.setTitle("LxRecyclerView");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setLogo(R.mipmap.logo);
        toolbar.inflateMenu(R.menu.menu_recycler);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                                               @Override
                                               public boolean onMenuItemClick(MenuItem item) {
                                                   startActivity(new Intent(LxRecyclerViewActivity.this, LxListViewActivity.class));
                                                   return false;
                                               }
                                           }
        );
    }

    private void setData() {
        mList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mList.add("第" + i + "个");
        }
    }

    @Override
    public void onPullDownRefresh() {
        aaaa= 3;
        Observable
                .timer(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .map(new Func1<Long, Object>() {
                    @Override
                    public Object call(Long aLong) {
                        fetchingNewData();
                        mRefreshAdapter.setList(mList);
                        mGotRefresh.refreshReset();
                        mGotRefresh.setLoadMoreEnable(aaaa > 0);
                        return null;
                    }
                }).subscribe();
    }

    @Override
    public void onPullUpRefresh() {
        if (aaaa > 0){
            mGotRefresh.setLoadMoreEnable(aaaa > 0);
            simulateLoadMoreData();
        }else {
            mGotRefresh.setLoadMoreEnable(false);
            mGotRefresh.refreshReset();
        }
    }

    private void fetchingNewData() {
        List<String> strings = new ArrayList<>(1);
        strings.add("下拉刷新出来的数据");
        mList = strings;
        setData();
    }

    int aaaa= 3;

    private void simulateLoadMoreData() {
        Observable
                .timer(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .map(new Func1<Long, Object>() {
                    @Override
                    public Object call(Long aLong) {
                        loadMoreData();
                        mRefreshAdapter.setList(mList);
                        aaaa --;
                        //Only RecyclerView needs to call this method, ListView doesn't needs it.
                        mGotRefresh.setLoadMoreEnable(aaaa > 0);//close pull up refresh
                        mGotRefresh.refreshReset();
                        return null;
                    }
                }).subscribe();
    }

    private void loadMoreData() {
        List<String> moreList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            moreList.add("加载更多的数据");
        }
        mList.addAll(moreList);
    }
}
