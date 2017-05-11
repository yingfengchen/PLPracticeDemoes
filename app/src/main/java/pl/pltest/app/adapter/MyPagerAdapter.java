package pl.pltest.app.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import pl.pltest.app.R;
import pl.pltest.app.utils.myimageview.PLMatrixImageView;
import pl.pltest.app.utils.viewpage.MyViewPager;

public class MyPagerAdapter extends PagerAdapter {

    private Context mContext;
    private String pathStrings[];
    private LayoutInflater mLayoutInflater;
    private DisplayImageOptions options;
    private MyViewPager mViewPager;
    private PLMatrixImageView.OnSingleTapListener onSingleTapListener;
    private String correctPath;

    public MyPagerAdapter(MyViewPager mViewPager, Context mContext, String pathStrings2[]) {
        this.mViewPager = mViewPager;
        this.mContext = mContext;
        this.pathStrings = pathStrings2;

        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder = builder
                .showImageOnLoading(R.drawable.ic_stub)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true);
        options = builder.build();
    }

    @Override
    public int getCount() {

        return pathStrings.length;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {

        return arg0 == arg1;
    }

    /*
     *  PagerAdapter只缓存三张要显示的图片，如果滑动的图片超出了缓存的范围，就会调用这个方法，将图片销毁
     *  假如有三个View：1、2、3,当向左滑动到第三个View时,此时继续向左滑(假如有第四个View)，第一个View将会被销毁。
     *  此时的position即为0，表示第一个View
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ((ViewPager) container).removeView((View) object);
    }

    /*
     * 当要显示的图片可以进行缓存的时候，会调用这个方法进行显示图片的初始化，
     * 我们将要显示的ImageView加入到ViewGroup中，然后作为返回值返回即可
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        mLayoutInflater = LayoutInflater.from(mContext);
        View viewPagerLayout = mLayoutInflater.inflate(R.layout.chviewpage, null);
        container.addView(viewPagerLayout);

        PLMatrixImageView myImageView = (PLMatrixImageView) viewPagerLayout.findViewById(R.id.imageview);
        myImageView.setOnMovingListener(mViewPager);
        myImageView.setOnSingleTapListener(onSingleTapListener);

        String path = pathStrings[position];

        correctPath = correctPath(path);

        ImageLoader.getInstance().displayImage(correctPath, myImageView, options);
//				new ImageLoadingListener() {
//			
//			@Override
//			public void onLoadingStarted(String imageUri, View view) {
//				LoadingAnimationManager.createLoadingWindow(mContext);
//				Log.i("开始下载", "loading start");
//			}
//			
//			@Override
//			public void onLoadingFailed(String imageUri, View view,
//					FailReason failReason) {
////				LoadingAnimationManager.removeLoadingWindow(mContext);
//				Log.i("下载失败", "loading failed");
//			}
//			
//			@Override
//			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//				LoadingAnimationManager.removeLoadingWindow(mContext);
//				Log.i("完成下载", "loading completed");
//			}
//			
//			@Override
//			public void onLoadingCancelled(String imageUri, View view) {
////				LoadingAnimationManager.removeLoadingWindow(mContext);
//				Log.i("取消下载", "loading canceled");
//			}
//		});		

        return viewPagerLayout;
    }

    private String correctPath(String path) {
        String httpHead = "http://";
        if (path.startsWith(httpHead)) {
            return path;
        } else {
            return "file://" + path;
        }
    }
}
