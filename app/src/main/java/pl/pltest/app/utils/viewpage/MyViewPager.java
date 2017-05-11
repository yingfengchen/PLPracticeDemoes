package pl.pltest.app.utils.viewpage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import pl.pltest.app.utils.myimageview.PLMatrixImageView;

public class MyViewPager extends CycleViewPager implements PLMatrixImageView.OnMovingListener {
    public static boolean mChildIsBeingDragged = false;

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (mChildIsBeingDragged)
            return false;
        try {
            return super.onInterceptTouchEvent(arg0);
        } catch (IllegalArgumentException arg01) {

        }
        return false;
    }

    @Override
    public void startDrag() {
        mChildIsBeingDragged = true;
    }

    @Override
    public void stopDrag() {
        mChildIsBeingDragged = false;
    }
}
