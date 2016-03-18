package mrfu.swiperefreshboth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import mrfu.swiperefreshboth.lib.listview.SwipeRefreshListView;
import mrfu.swiperefreshboth.lib.utils.PullRefreshListener;


public class ListViewDemoActivity extends AppCompatActivity implements PullRefreshListener {
    private SwipeRefreshListView mSwipeLayout;
    private ListView mListView;
    private ArrayList<String> list = new ArrayList<>();
    private BaseAdapter arrayAdapter;
    private View progress_layout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTitleBar();

        progress_layout = findViewById(R.id.progress_layout);
        progress_layout.setVisibility(View.VISIBLE);
        mSwipeLayout = (SwipeRefreshListView) findViewById(R.id.swipe_container);
        mListView = (ListView)findViewById(R.id.listview);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);//getData()
        mListView.addFooterView(mSwipeLayout.mListViewFooter);//TODO 一定要先添加一次，不然会报错，setAdapter 之后 remove 掉   看这里：http://blog.csdn.net/Take_all/article/details/7635116
        mListView.setAdapter(arrayAdapter);
        mListView.removeFooterView(mSwipeLayout.mListViewFooter);
        mSwipeLayout.setOnPullRefreshListener(this);

        firstLoadTestData();
    }

    private void firstLoadTestData(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //load finish do it
                list.addAll(getData());
                arrayAdapter.notifyDataSetChanged();
                progress_layout.setVisibility(View.GONE);
            }
        }, 3000);
    }

    private void loadTestData(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //load finish do it
                list.addAll(getData());
                arrayAdapter.notifyDataSetChanged();
                mSwipeLayout.refreshReset();
            }
        }, 1500);
    }

    private void initTitleBar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("SwipeRefreshBoth");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setLogo(R.mipmap.logo);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
               @Override
               public boolean onMenuItemClick(MenuItem item) {
                   Uri uri = Uri.parse("https://github.com/MrFuFuFu/SwipeRefreshBoth");
                   Intent i = new Intent(Intent.ACTION_VIEW, uri);
                   i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   startActivity(i);
                   return false;
               }
           }
        );
    }


    private ArrayList<String> getData() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Yuan Fu");
        list.add("http://mrfufufu.github.io/");
        list.add("GitHub: MrFuFuFu");
        return list;
    }

    @Override
    public void onPullDownRefresh() {
        loadTestData();
    }

    @Override
    public void onPullUpRefresh() {
        loadTestData();
    }
}
