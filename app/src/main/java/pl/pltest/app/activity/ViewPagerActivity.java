package pl.pltest.app.activity;

import android.app.Activity;
import android.os.Bundle;
import pl.pltest.app.R;
import pl.pltest.app.utils.imageurl.HttpImageUrl;
import pl.pltest.app.utils.pageindicator.CirclePageIndicator;
import pl.pltest.app.adapter.MyPagerAdapter;
import pl.pltest.app.utils.viewpage.MyViewPager;

public class ViewPagerActivity extends Activity {

	private MyViewPager mViewPager;
	private MyPagerAdapter myPagerAdapter;
	private int index = 1;
	private CirclePageIndicator mPageIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpager);
		initViewPage();
	}

	public void initViewPage() {
		mViewPager=(MyViewPager)findViewById(R.id.viewPager);		
		myPagerAdapter = new MyPagerAdapter(mViewPager, this, HttpImageUrl.IMAGES);
		mViewPager.setAdapter(myPagerAdapter);
		mViewPager.setCurrentItem(index);
		
		//添加指示圆点，使用第三方开源项目中的CirclePageIndicator
		mPageIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
		mPageIndicator.setViewPager(mViewPager);
	}
}
