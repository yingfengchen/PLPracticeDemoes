package pl.pltest.app.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import pl.pltest.app.R;
import pl.pltest.app.adapter.LineAdapter;
import pl.pltest.app.itemDecoration.GridAndStaggeredLayoutItemDecoration;

import java.util.ArrayList;

public class RecycleViewActivity extends AppCompatActivity implements LineAdapter.RecycleItemClickListener {

    private RecyclerView recycleView;
    private ArrayList<String> mData;
    private LineAdapter lineAdapter;
    RecyclerView.LayoutManager manager = null;
    String TAG = "PL";
    private boolean loading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_view);
        initData();
        recycleView = (RecyclerView) findViewById(R.id.recyleView);
//        RecyclerView.LayoutManager line = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        RecyclerView.LayoutManager grid = new GridLayoutManager(this, 2);
//        RecyclerView.LayoutManager stagger = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recycleView.setLayoutManager(grid);
        lineAdapter = new LineAdapter(this, mData);
        lineAdapter.setOnItemClickListener(this);
        recycleView.setAdapter(lineAdapter);
//        recycleView.addItemDecoration(new LinearLayoutItemDecoration(this,
//                LinearLayoutItemDecoration.VERTICAL_LIST));
        recycleView.addItemDecoration(new GridAndStaggeredLayoutItemDecoration(this));
//        recycleView.addOnScrollListener(this);
        recycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                Log.i(TAG, "dx ===" + dx + "\n" + "dy === " + dy);
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                int totalItemCount = recyclerView.getAdapter().getItemCount();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastVisibleItemPos = 0;
                    if (null == manager) {
                        manager = recyclerView.getLayoutManager();
                    }

                    if (manager instanceof GridLayoutManager) {
                        lastVisibleItemPos = ((GridLayoutManager) manager).findLastCompletelyVisibleItemPosition();
                    } else if (manager instanceof LinearLayoutManager) {
                        lastVisibleItemPos = ((LinearLayoutManager) manager).findLastCompletelyVisibleItemPosition();
                    }

                    Log.i(TAG, "lastVisibleItemPos ==== " + lastVisibleItemPos + " totalItemCount ==== " + totalItemCount);

                    if (!loading && lastVisibleItemPos == totalItemCount - 1) {
                        Log.i(TAG, "滑到最后了 ===");
                        //可以写加载更多的代码逻辑
                        loading = true;
                        loadMoreData();
                    }
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void loadMoreData() {
        //加载逻辑
        for (int i = 0; i <= 2; i++) {
            mData.add(i + "");
        }
        lineAdapter.notifyDataSetChanged();
        loading = false;
    }

    private void initData() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mData = new ArrayList<String>();
        for (int i = 'A'; i <= 'Z'; i++) {
            mData.add((char) i + "");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recycle_view, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view, int position) {
        Toast.makeText(this, "你点击了：" + position, Toast.LENGTH_SHORT).show();
    }
}
