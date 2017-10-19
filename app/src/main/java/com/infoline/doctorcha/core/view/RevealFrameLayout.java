package com.infoline.doctorcha.core.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import com.infoline.doctorcha.core.util.CommonUtil;

public class RevealFrameLayout extends FrameLayout{

    private Path revealPath;
    //private RevealInfo mRevealInfo;

    /////////////////////////

    public enum RevealState {
        NONE(0),
        STARTED(1),
        FINISHED(2);

        private int value;

        RevealState(int value) {
            this.value = value;
        }
        public int getValue() { return this.value; }
    }

    private RevealState revealState = RevealState.NONE;

    private Paint fillPaint;
    private int currentRadius;

    private PointF revealStartPoint;

    private OnRevealStateChangeListener onRevealStateChangeListener;

    ////

    public boolean isReverse = false;

    /*
    public RevealFrameLayout(Context context) {
        this(context, null);
    }

    public RevealFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RevealFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        revealPath = new Path();
    }
    */

    public RevealFrameLayout(Context context) {
        super(context);
        init();
    }

    public RevealFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RevealFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.onRevealStateChangeListener = (OnRevealStateChangeListener)this.getContext();

        revealPath = new Path();

        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.WHITE);

        //int ggg = this.getLayerType();      ; 0
        //boolean bbb = this.getClipChildren(); //true
        //this.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        //this.setClipChildren(false);
    }

    public void setFillPaintColor(int color) {
        fillPaint.setColor(color);
    }

    public void startRevealAnimation(PointF revealStartPoint, int revealDuration, int alphaDuration, int alphaStartDelay, Interpolator revealInterpolator, Interpolator alphaInterpolator, boolean isReverse) {
        changeRevealState(RevealState.STARTED);

        this.isReverse = isReverse;

        if (!isReverse) {
            this.revealStartPoint = revealStartPoint;
        }

        final AnimatorSet as = new AnimatorSet();
        final int sqrt = CommonUtil.getSqrt(this, this.revealStartPoint);
        final ObjectAnimator revealAnim = ObjectAnimator.ofInt(this, "currentRadius", isReverse ? sqrt : 0, isReverse ? 0 : sqrt).setDuration(revealDuration);

        if (revealInterpolator != null) {
            revealAnim.setInterpolator(revealInterpolator);
        }

        /*
        final ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(this, "alpha", isReverse ? 1f : 0f, isReverse ? 0f : 1f);
        alphaAnim.setDuration(alphaDuration);
        alphaAnim.setStartDelay(alphaStartDelay);

        if (alphaInterpolator != null) {
            alphaAnim.setInterpolator(alphaInterpolator);
        }
        */

        as.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                changeRevealState(RevealState.FINISHED);
            }
        });

        //as.play(revealAnim).with(alphaAnim);
        //as.playTogether(revealAnim, alphaAnim);
        as.playTogether(revealAnim);
        as.start();
    }

    public void setToFinishedFrame() {
        changeRevealState(RevealState.FINISHED);
        invalidate();
    }

    private void changeRevealState(RevealState revealState) {
        if (this.revealState == revealState) {
            return;
        }

        this.revealState = revealState;

        if (onRevealStateChangeListener != null) {
            onRevealStateChangeListener.onRevealStateChange(revealState);
        }
    }

    public void setCurrentRadius(int radius) {
        this.currentRadius = radius;
        invalidate();
    }

    public interface OnRevealStateChangeListener {
        void onRevealStateChange(RevealState revealState);
    }

    /*
    private class RevealRadius extends Property<RevealFrameLayout, Float> {

        public RevealRadius() {
            super(Float.class, "currentRadius");
        }

        @Override
        public Float get(RevealFrameLayout object) {
            return 1f;
        }
    }
    */

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (revealState == RevealState.FINISHED) {
            if (!isReverse) {
                //canvas.drawRect(0, 0, getWidth(), getHeight(), fillPaint);
            }

        } else {
            //canvas.drawCircle(startLocationX, startLocationY, currentRadius, fillPaint);
        }

        //if(mRunning && (child.getId() == R.id.ncv || child.getId() == R.id.abl ) && state != STATE_FINISHED){
        if(revealStartPoint == null) {
            canvas.drawRect(0, 0, getWidth(), getHeight(), fillPaint);
        }
        else {
            final int state = canvas.save();

            revealPath.reset();
            revealPath.addCircle(revealStartPoint.x, revealStartPoint.y, currentRadius, Path.Direction.CW); //CCW

            canvas.clipPath(revealPath);

            boolean isInvalided = super.drawChild(canvas, child, drawingTime);

            canvas.restoreToCount(state);

            return isInvalided;
        }

        //return isReverse ? false : super.drawChild(canvas, child, drawingTime); --> simplicate
        return !isReverse && super.drawChild(canvas, child, drawingTime);
    }

    /*
    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (revealState == RevealState.FINISHED) {
            if (!isReverse) {
                //canvas.drawRect(0, 0, getWidth(), getHeight(), fillPaint);
            }

        } else {
            //canvas.drawCircle(startLocationX, startLocationY, currentRadius, fillPaint);
        }

        //if(mRunning && (child.getId() == R.id.ncv || child.getId() == R.id.abl ) && state != STATE_FINISHED){

        if(true){
            final int state = canvas.save();

            revealPath.reset();
            revealPath.addCircle(revealStartPoint.x, revealStartPoint.y, currentRadius, Path.Direction.CW); //CCW

            canvas.clipPath(revealPath);

            boolean isInvalided = super.drawChild(canvas, child, drawingTime);

            canvas.restoreToCount(state);

            return isInvalided;
        }

        return isReverse ? false : super.drawChild(canvas, child, drawingTime);
    }
    */

}
