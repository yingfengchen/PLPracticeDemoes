package pl.pltest.app.utils;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.List;

/**
 * 描述：FloatingActionButton点击旋转90度
 * 创建时间：2016/12/19 14:29
 */

public class RotateBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {

    /**
     * 自定义RotateBehavior，要在xml中使用，则必须有该构造方法，
     */
    public RotateBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     *
     * @param parent  child 和 dependency的父view，实现了NestedScrollingParent的view
     * @param child  监听对象状态改变，需要作出相应的view
     * @param dependency  被监听对象
     * @return
     */
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        float translationY = getFabTranslationYForSnackbar(parent, child);
        Log.i("PL", "translationY === " + translationY);
        float percentComplete = -translationY / dependency.getHeight();
        child.setRotation(-90 * percentComplete);
        child.setTranslationY(translationY);
        return false;
    }

    private float getFabTranslationYForSnackbar(CoordinatorLayout parent, FloatingActionButton fab) {
        float minOffset = 0;
        //获取到fab依赖的view
        final List<View> dependencies = parent.getDependencies(fab);
        Log.i("PL", "size === " + dependencies.size());
        for (int i = 0, z = dependencies.size(); i < z; i++) {
            final View view = dependencies.get(i);
            if (view instanceof Snackbar.SnackbarLayout && parent.doViewsOverlap(fab, view)) {
                minOffset = Math.min(minOffset, ViewCompat.getTranslationY(view) - view.getHeight());
                Log.i("PL", "changeY === " + ViewCompat.getTranslationY(view));
            }
        }

        return minOffset;
    }
}
