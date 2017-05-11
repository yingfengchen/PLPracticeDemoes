package pl.pltest.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.LinearLayout;
import pl.pltest.app.fragment.ItemFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/27.
 */
public class MyItemFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<String> mList = new ArrayList<>();

    public MyItemFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public MyItemFragmentPagerAdapter(FragmentManager fm, List<String> mList) {
        super(fm);
        this.mList = mList;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ItemFragment.newInstance(1, LinearLayout.VERTICAL, false, false);
            case 1:
                return ItemFragment.newInstance(1, LinearLayout.VERTICAL, true, false);
            case 2:
                return ItemFragment.newInstance(1, LinearLayout.HORIZONTAL, false, false);
            case 3:
                return ItemFragment.newInstance(1, LinearLayout.HORIZONTAL, true, false);
            case 4:
                return ItemFragment.newInstance(3, LinearLayout.VERTICAL, false, false);
            case 5:
                return ItemFragment.newInstance(3, LinearLayout.VERTICAL, true, false);
            case 6:
                return ItemFragment.newInstance(3, LinearLayout.HORIZONTAL, false, false);
            case 7:
                return ItemFragment.newInstance(3, LinearLayout.HORIZONTAL, true, false);
            case 8:
                return ItemFragment.newInstance(3, LinearLayout.VERTICAL, false, true);
            case 9:
                return ItemFragment.newInstance(3, LinearLayout.VERTICAL, true, true);
            case 10:
                return ItemFragment.newInstance(3, LinearLayout.HORIZONTAL, false, true);
            case 11:
                return ItemFragment.newInstance(3, LinearLayout.HORIZONTAL, true, true);
            default:
                break;
        }
        // 默认返回
        return ItemFragment.newInstance(1, LinearLayout.VERTICAL, false, false);
    }

    @Override
    public int getCount() {
        return null == mList ? 0 : mList.size();
    }

    //设置Fragment对应的TabLayout的title
    @Override
    public CharSequence getPageTitle(int position) {
        return mList.get(position);
    }
}
