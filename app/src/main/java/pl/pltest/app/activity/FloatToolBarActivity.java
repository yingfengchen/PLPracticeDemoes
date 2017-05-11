package pl.pltest.app.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import pl.pltest.app.R;
import pl.pltest.app.utils.BackgroundSnackBar;

public class FloatToolBarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float_tool_bar);

        initToolbar();
        initFloatActionBotton();
    }

    private void initFloatActionBotton() {
        // FloatingActionButton默认使用FloatingActionButton.Behavior，点击后没啥动作
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 外层CoordinatorLayout才能使FloatingActionButton跟随Snackbar上下移动
                Snackbar snackbar = Snackbar.make(v, "it is Snackbar", Snackbar.LENGTH_LONG);

                // 或者使用工具类，按照不同等级，修改背景颜色
                BackgroundSnackBar.confirm(snackbar)
                        .setActionTextColor(getResources().getColor(R.color.colorPrimary))  // 设置右侧取消文字的颜色
                        .setAction(getString(R.string.show_toast), new View.OnClickListener() {  // 右侧取消文字的响应事件
                            @Override
                            public void onClick(View v) {
                                // 这里的单击事件代表点击消除Action后的响应事件
                                Toast.makeText(FloatToolBarActivity.this, "it is Toast", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });
    }

    private void initToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // APP Logo
        toolbar.setLogo(R.mipmap.ic_logo);

        // Title
        toolbar.setTitle(getString(R.string.tool_bar_title));

        // Sub Title
        toolbar.setSubtitle(getString(R.string.tool_bar_sub_title));

        // 上面这些设置均可在布局文件中设置
        setSupportActionBar(toolbar);

        /**
         * 如何设置toolBar左侧的返回图片：三种方式
         *  1.toolbar.setNavigationIcon(R.mipmap.ic_return);
         *  2.getSupportActionBar().setDisplayHomeAsUpEnabled(true); 默认图片是左箭头
         *  3.在布局中设置图片：app:logo="@mipmap/ic_logo"
         */
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // toolbar的点击事件监听都要放在setSupportActionBar之后才会生效
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String msg = null;
                switch (item.getItemId()) {
                    case R.id.action_edit:
                        msg = "Click edit";
                        break;
                    case R.id.action_share:
                        msg = "Click share";
                        break;
                    case R.id.action_settings:
                        msg = "Click setting";
                        break;
                }

                if (msg != null) {
                    Toast.makeText(FloatToolBarActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        // 返回键的点击事件还可以onOptionsItemSelected中写，参照AppBarLayoutActivity
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fab, menu);
        return true;
    }
}
