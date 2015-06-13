package mrfu.swiperefreshboth;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import mrfu.swiperefreshboth.lib.PullRefreshListener;
import mrfu.swiperefreshboth.lib.SwipeRefreshBothPull;
import mrfu.swiperefreshlayoutpulltopbottom.R;


public class MainActivity extends Activity implements PullRefreshListener {
    private SwipeRefreshBothPull mSwipeLayout;
    private ListView mListView;
    private ArrayList<String> list = new ArrayList<String>();
    private BaseAdapter arrayAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeLayout = (SwipeRefreshBothPull) findViewById(R.id.swipe_container);
        mSwipeLayout.setFooterViewBackground(android.R.color.white);
        listViweAboutMethod();

        mSwipeLayout.setOnPullRefreshListener(this);

    }

    private void listViweAboutMethod() {
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getData());
        mListView = (ListView) findViewById(R.id.listview);
        mListView.setAdapter(arrayAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mSwipeLayout.setRefreshing(true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Hide the refresh after 2sec
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mSwipeLayout.refreshReset();
                                }
                            });
                        }
                    }, 2000);
                }
            }
        });
    }

    private ArrayList<String> getData() {
        list.add("Yuan Fu");
        list.add("http://mrfufufu.github.io/");
        list.add("GitHub: MrFuFuFu");
        for (int i = 0; i < 10; i++) {
            list.add("Hello " + i);
        }
        return list;
    }

    @Override
    public void onPullDownRefresh() {
        Log.i("MrFu", "onPullDownRefresh Time = " + System.currentTimeMillis());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeLayout.refreshReset();
            }
        }, 1500);
    }

    @Override
    public void onPullUpRefresh() {
        Log.i("MrFu", "onPullUpRefresh Time = " + System.currentTimeMillis());
        mSwipeLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                list.addAll(getData());
                arrayAdapter.notifyDataSetChanged();
                // 加载完后调用该方法
                mSwipeLayout.refreshReset();
            }
        }, 1500);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent it = new Intent(Intent.ACTION_VIEW);
            it.setData(Uri.parse("http://mrfufufu.github.io/")); //这里面是需要调转的rul
            it = Intent.createChooser(it, null);
            startActivity(it);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
