package pl.pltest.app.itemDecoration;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2017/4/19.
 */
public class GridAndStaggeredLayoutItemDecoration extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    private Drawable mDivider;

    public GridAndStaggeredLayoutItemDecoration(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawHorizontal(c, parent);
        drawVerticalLeft(c, parent);
        drawVerticalRight(c, parent);
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getLeft() - params.leftMargin - mDivider.getIntrinsicHeight();
            final int right = child.getRight() + params.rightMargin + mDivider.getIntrinsicHeight();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    public void drawVerticalLeft(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getLeft() + params.leftMargin - mDivider.getIntrinsicHeight();
            final int right = child.getLeft();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    public void drawVerticalRight(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        //设置view的偏移量：就好像是给它的周围留出空间，便于之后在这些空间上“涂色”，即draw
        outRect.set(mDivider.getIntrinsicHeight(), 0, mDivider.getIntrinsicHeight(), mDivider.getIntrinsicHeight());
        
//        int spanCount = getSpanCount(parent);
//        int allChildCount = parent.getAdapter().getItemCount();
//
//        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
//        int itemPositionInLayout = 0;
//        if (layoutManager instanceof GridLayoutManager) {
//            itemPositionInLayout = ((GridLayoutManager.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
//        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
//            itemPositionInLayout = ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
//        }
    }

//    private int getSpanCount(RecyclerView parent) { // 列数
//        int spanCount = -1;
//        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
//        if (layoutManager instanceof GridLayoutManager) {
//
//            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
//        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
//            spanCount = ((StaggeredGridLayoutManager) layoutManager)
//                    .getSpanCount();
//        }
//        return spanCount;
//    }
}
