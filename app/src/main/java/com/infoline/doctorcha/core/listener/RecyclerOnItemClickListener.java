package com.infoline.doctorcha.core.listener;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;

/**
 * Created by Administrator on 2016-01-10.
 */
public class RecyclerOnItemClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener onItemClickListener;
    private GestureDetector gestureDetector;

    @Nullable
    private View itemView;
    private int childViewPosition;

    public interface OnItemClickListener {
        //void onItemClick(View itemView, int position, Point point, Point rawPoint);
        //void onItemLongPress(View itemView, int position, Point point, Point rawPoint);
        void onItemPress(View itemView, int position, boolean isLongPress, MotionEvent e);
    }

    public RecyclerOnItemClickListener(Context context, OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        this.gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        itemView = rv.findChildViewUnder(e.getX(), e.getY());  //rootView of ViewHolder
        childViewPosition = rv.getChildAdapterPosition(itemView);

        //return itemView != null && gestureDetector.onTouchEvent(e);

        gestureDetector.onTouchEvent(e);

        //gestureDetector.onTouchEvent(e)의 return 값을 넘기면 itemView의 자식뷰들에 대한 click, touch, longPress 모두 안먹는다
        //반드시 false로 던질 것
        return false;
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent (boolean x) {

    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent event) {
        // Not needed.
    }

    //----------------------------------------------------------------------------------------------

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (itemView != null) {
                itemView.playSoundEffect(SoundEffectConstants.CLICK);
                onItemClickListener.onItemPress(itemView, childViewPosition, false, e);
            }

            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (itemView != null) {
                onItemClickListener.onItemPress(itemView, childViewPosition, true, e);
            }
        }

        @Override
        public boolean onDown(MotionEvent e) {
            // Best practice to always return true here.
            // http://developer.android.com/training/gestures/detector.html#detect
            return true;
        }
    }
}
