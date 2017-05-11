package pl.pltest.app.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import pl.pltest.app.R;
import pl.pltest.app.adapter.MyItemFragmentPagerAdapter;
import pl.pltest.app.fragment.ItemFragment;
import pl.pltest.app.model.DataBean;

import java.util.ArrayList;
import java.util.List;

public class ToolBarWithTabActivity extends AppCompatActivity  implements ItemFragment.OnRecycleItemClickListener {

    private String msg;
    private TabLayout tab;
    private ViewPager viewpager;
    private List<String> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_bar_with_tab);

        initToolbar();
        initTabLayout();
    }

    private void initTabLayout() {
        tab = (TabLayout) findViewById(R.id.tab);
        viewpager = (ViewPager) findViewById(R.id.viewpager);

        initData();
    }

    //初始化TabLayout的title
    private void initData() {
        if (null != mList && mList.size() > 0) {
            mList.clear();
        }
        mList.add("List垂直");
        mList.add("List垂直反向");
        mList.add("List水平");
        mList.add("List水平反向");

        mList.add("Grid垂直");
        mList.add("Grid垂直反向");
        mList.add("Grid水平");
        mList.add("Grid水平反向");

        mList.add("瀑布流垂直");
        mList.add("瀑布流垂直反向");
        mList.add("瀑布流水平");
        mList.add("瀑布流水平反向");

        // 设置ViewPager的Adapter,item为fragment，使用的是FragmentPagerAdapter
        viewpager.setAdapter(new MyItemFragmentPagerAdapter(getSupportFragmentManager(), mList));
        // 将TabLayout与ViewPager关联
        tab.setupWithViewPager(viewpager);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //如下代码为了显示actionBar中的"返回键"
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //toolBar中的"返回"键的点击响应
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //menu中item的点击事件
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_list:// List样式
                        msg = "List样式_Toolbar";
                        break;

                    case R.id.action_grid:// Grid样式
                        msg = "Grid样式_Toolbar";
                        break;

                    case R.id.action_staggered:// 瀑布流样式
                        msg = "瀑布流样式_Toolbar";

                    default:
                        break;
                }
                if (msg != null) {
                    Toast.makeText(ToolBarWithTabActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_abl, menu);
        return true;
    }

    @Override
    public void onListFragmentInteraction(DataBean item) {
        Toast.makeText(this, item.getName(), Toast.LENGTH_SHORT).show();
    }
}
