package pl.pltest.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.pltest.app.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String TAG = "PL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindClick();
//        printJSON();
    }

    private void printJSON() {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("1");
        jsonArray.put("2");

        try {
            jsonObject.put("expiredTime", "00");
            jsonObject.put("scope", "USER");
            jsonObject.put("apis", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "打印出来 === " + jsonObject.toString());
    }

    private void bindClick() {
        findViewById(R.id.file).setOnClickListener(this);
        findViewById(R.id.popupWindow).setOnClickListener(this);
        findViewById(R.id.recycleView).setOnClickListener(this);
        findViewById(R.id.floatToolBar).setOnClickListener(this);
        findViewById(R.id.toolBarWithTab).setOnClickListener(this);
        findViewById(R.id.collapsingToolbar).setOnClickListener(this);
        findViewById(R.id.viewpage_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.file:
                startActivity(new Intent(MainActivity.this,FileActivity.class));
                break;
            case R.id.popupWindow:
                startActivity(new Intent(MainActivity.this,PopupWindowActivity.class));
                break;
            case R.id.recycleView:
                startActivity(new Intent(MainActivity.this,RecycleViewActivity.class));
                break;
            case R.id.floatToolBar:
                startActivity(new Intent(MainActivity.this,FloatToolBarActivity.class));
                break;
            case R.id.toolBarWithTab:
                startActivity(new Intent(MainActivity.this,ToolBarWithTabActivity.class));
                break;
            case R.id.collapsingToolbar:
                startActivity(new Intent(MainActivity.this,CollapsingToolBarActivity.class));
                break;
            case R.id.viewpage_btn:
                startActivity(new Intent(MainActivity.this,ViewPagerActivity.class));
                break;
            default:
                break;
        }
    }
}
