package pl.pltest.app.utils.viewpage;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 需求：定制一个可以循环滑动的ViewPager,主要有两种实现方式：
 * <p/>
 * 1. 网上流行的实现方式：重写adapter的getCount方法，返回一个很大的数（值为max），
 * adapter中的getView方法中的position重新根据实际数量取模，把ViewPager设置在1/2max的位置。
 * 因为这个值很大所以基本不可能滑动到position=0或者position=max的位置，不过确切来说这并不是无限循环。
 * <p/>
 * 2. CycleViewPager继承自ViewPager，重写了instantiateItem()里的逻辑，实现了无限循环的功能。
 *
 * @author PangLei
 */

public class CycleViewPager extends ViewPager {

    private InnerPagerAdapter mAdapter;

    public CycleViewPager(Context context) {
        super(context);
        addOnPageChangeListener(null);
    }

    public CycleViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        addOnPageChangeListener(null);
    }

    @Override
    public void setAdapter(PagerAdapter arg0) {
        mAdapter = new InnerPagerAdapter(arg0);
        super.setAdapter(mAdapter);
        setCurrentItem(1);  //因为最前面和最后面各加了一个item，所以显示的初始位置为0+1=1
    }

    @Override
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        super.addOnPageChangeListener(new InnerOnPageChangeListener(listener));
    }

    private class InnerOnPageChangeListener implements OnPageChangeListener {

        private OnPageChangeListener listener;
        private int position;

        public InnerOnPageChangeListener(OnPageChangeListener listener) {
            this.listener = listener;
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            if (null != listener) {
                listener.onPageScrollStateChanged(arg0);
            }
            if (arg0 == ViewPager.SCROLL_STATE_IDLE) {
                if (position == mAdapter.getCount() - 1) {
                    setCurrentItem(1, false);
                } else if (position == 0) {
                    setCurrentItem(mAdapter.getCount() - 2, false);
                }
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            if (null != listener) {
                listener.onPageScrolled(arg0, arg1, arg2);
            }
        }

        @Override
        public void onPageSelected(int arg0) {
            position = arg0;
            if (null != listener) {
                listener.onPageSelected(arg0);
            }
        }
    }

    private class InnerPagerAdapter extends PagerAdapter {

        private PagerAdapter adapter;

        public InnerPagerAdapter(PagerAdapter adapter) {
            this.adapter = adapter;
            adapter.registerDataSetObserver(new DataSetObserver() {

                @Override
                public void onChanged() {
                    notifyDataSetChanged();
                }

                @Override
                public void onInvalidated() {
                    notifyDataSetChanged();
                }

            });
        }

        @Override
        public int getCount() {
            return adapter.getCount() + 2;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return adapter.isViewFromObject(arg0, arg1);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (position == 0) {
                position = adapter.getCount() - 1;
            } else if (position == adapter.getCount() + 1) {
                position = 0;
            } else {
                position -= 1;
            }
            return adapter.instantiateItem(container, position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            adapter.destroyItem(container, position, object);
        }

    }
}
