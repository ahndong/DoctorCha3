package com.infoline.doctorcha.core.listener;

import android.content.Context;
import android.graphics.Point;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Administrator on 2016-01-10.
 */
public class ViewOnClickListener implements View.OnTouchListener {
    private OnClickListener onClickListener;
    private GestureDetector gestureDetector;
    private View targetView;

    public interface OnClickListener {
        void onViewClick(View v, Point point, Point rawPoint);
        void onViewLongPress(View v, Point point, Point rawPoint);
    }

    public ViewOnClickListener(Context context, OnClickListener onClickListener) {
        this.onClickListener = onClickListener;

        //getApplicationContext 잊자에 null을 전달해도 실행상에는 이상이 없네. 왜 그렇지.
        gestureDetector = new GestureDetector(context.getApplicationContext(), new GestureListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        targetView = v;

        //1. return true;
        //2. return false;
        //위 1, 2 둘 다 반응없다
        return gestureDetector.onTouchEvent(e);
    }

    //----------------------------------------------------------------------------------------------

    protected class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            onClickListener.onViewClick(targetView, touchPoint(e, 1), touchPoint(e, 2));

            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            onClickListener.onViewLongPress(targetView, touchPoint(e, 1), touchPoint(e, 2));
        }

        @Override
        public boolean onDown(MotionEvent e) {
            // Best practice to always return true here.
            // http://developer.android.com/training/gestures/detector.html#detect
            return true;
        }

        protected Point touchPoint(MotionEvent e, int type) {
            return new Point((int)(type == 1 ? e.getX() : e.getRawX()), (int)((int)(type == 1 ? e.getY() : e.getRawY())));
        }
    }
}
