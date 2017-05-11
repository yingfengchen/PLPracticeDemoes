package pl.pltest.app.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import pl.pltest.app.R;
import pl.pltest.app.adapter.MyItemRecyclerViewAdapter;
import pl.pltest.app.model.DATAS;
import pl.pltest.app.model.DataBean;

import java.util.ArrayList;
import java.util.List;

public class ItemFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";

    private static final String ARG_ORIENTATION = "orientation";

    private static final String ARG_REVERSE_LAYOUT = "reverse_layout";

    private static final String ARG_STAGGERED = "staggered";

    // 列数
    private int mColumnCount = 1;

    // 滑动方向默认垂直
    private int mOrientation = LinearLayout.VERTICAL;

    // 展示方向默认升序(正序)
    private boolean mReverseLayout = false;

    // 是否是瀑布流
    private boolean mStaggered = false;

    private OnRecycleItemClickListener mListener;

    /**
     * 传入RecyclerView的LayoutManager参数
     * @param columnCount   列数
     * @param orientation   LinearLayout.VERTICAL垂直滑动 LinearLayout.HORIZONTAL水平滑动
     * @param reverseLayout false:升序 true:降序
     * @param staggered     false:非瀑布流 true:瀑布流
     */
    public static ItemFragment newInstance(int columnCount, int orientation, boolean reverseLayout, boolean staggered) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putInt(ARG_ORIENTATION, orientation);
        args.putBoolean(ARG_REVERSE_LAYOUT, reverseLayout);
        args.putBoolean(ARG_STAGGERED, staggered);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mOrientation = getArguments().getInt(ARG_ORIENTATION);
            mReverseLayout = getArguments().getBoolean(ARG_REVERSE_LAYOUT, false);
            mStaggered = getArguments().getBoolean(ARG_STAGGERED, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);

        List<DataBean> beanList = new ArrayList<>();

        if (mOrientation != LinearLayout.VERTICAL && mOrientation != LinearLayout.HORIZONTAL) {
            mOrientation = LinearLayout.VERTICAL;
        }

        if (mColumnCount <= 1) {
            mColumnCount = 1;
            recyclerView.setLayoutManager(new LinearLayoutManager(context, mOrientation, mReverseLayout));

            for (int i = 0; i < DATAS.ICONS.length; i++) {
                DataBean bean = new DataBean();
                bean.setIcon(DATAS.ICONS[i]);
                bean.setName("图片-" + i);
                beanList.add(bean);
            }

        } else {
            if (mStaggered) {
                StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(mColumnCount, mOrientation);
                layoutManager.setReverseLayout(mReverseLayout);
                recyclerView.setLayoutManager(layoutManager);

                for (int i = 0; i < DATAS.PICS.length; i++) {
                    DataBean bean = new DataBean();
                    bean.setIcon(DATAS.PICS[i]);
                    bean.setName("图片-" + i);
                    beanList.add(bean);
                }
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount, mOrientation, mReverseLayout));

                for (int i = 0; i < DATAS.ICONS.length; i++) {
                    DataBean bean = new DataBean();
                    bean.setIcon(DATAS.ICONS[i]);
                    bean.setName("图片-" + i);
                    beanList.add(bean);
                }
            }
        }

        recyclerView.setAdapter(new MyItemRecyclerViewAdapter(beanList, mColumnCount, mListener));
        return view;
    }

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttachToContext(context);
    }

    /*
     * Deprecated on API 23
     * Use onAttachToContext instead
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }
    }

    private void onAttachToContext(Context context) {
        if (context instanceof OnRecycleItemClickListener) {
            mListener = (OnRecycleItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRecycleItemClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnRecycleItemClickListener {
        void onListFragmentInteraction(DataBean item);
    }
}
