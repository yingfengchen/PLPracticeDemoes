package pl.pltest.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import pl.pltest.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/18.
 */
public class LineAdapter extends RecyclerView.Adapter<LineAdapter.LineHolder> {
    private ArrayList<String> mData;
    private Context context;
    private RecycleItemClickListener recycleItemClickListener;
    private List<Integer> mHeights;

    public LineAdapter(Context context, ArrayList<String> mData) {
        this.context = context;
        this.mData = mData;
        //生成随机数
        mHeights = new ArrayList<Integer>();
        for (int i = 0; i < mData.size(); i++) {
            mHeights.add((int) (300 + Math.random() * 1000));
        }
    }

    @Override
    public LineHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_layout, parent, false);
        LineHolder lineHolder = new LineHolder(view);
        return lineHolder;
    }

    public interface RecycleItemClickListener {
        void onClick(View view, int position);
    }

    public void setOnItemClickListener(RecycleItemClickListener recycleItemClickListener) {
        this.recycleItemClickListener = recycleItemClickListener;
    }

    @Override
    public void onBindViewHolder(final LineHolder holder, final int position) {
        holder.text.setText(mData.get(position));

        //瀑布流的随机高度
//        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
//        layoutParams.height = mHeights.get(position);
//        holder.itemView.setLayoutParams(layoutParams);
        if (null != recycleItemClickListener) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recycleItemClickListener.onClick(holder.itemView, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return null == mData ? 0 : mData.size();
    }

    public class LineHolder extends RecyclerView.ViewHolder {

        public TextView text;
        public LinearLayout itemView;

        public LineHolder(View view) {
            super(view);
            text = (TextView) view.findViewById(R.id.textView);
            itemView = (LinearLayout) view.findViewById(R.id.item_view);
        }
    }
}
