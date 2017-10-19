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
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import com.infoline.doctorcha.core.util.CommonUtil;

/**
 * Created by Miroslaw Stanek on 18.01.15.
 /**
 * Created by Miroslaw Stanek on 18.01.15.
 *
 *   1. view 대신에 SurfaceView를 사용하고,
 *   2. SurfaceView로부터 핸들러를 얻고,
 *   3. invalidate 대신에 repaint()를 사용할 것을 권장합니다.
 http://stackoverflow.com/questions/1458047/why-isnt-view-invalidate-immediately-redrawing-the-screen-in-my-android-game
 */

public class RevealForegroundView extends View {
    public static final int STATE_NOT_STARTED = 0;
    public static final int STATE_FILL_STARTED = 1;
    public static final int STATE_FINISHED = 2;

    private static final Interpolator DECELERATE = new DecelerateInterpolator();
    private static final int FILL_TIME = 400;

    private int state = STATE_NOT_STARTED;

    private Paint fillPaint;
    private int currentRadius;
    ObjectAnimator revealAnimator;

    private Point startRevealPoint;
    private AnimationEndListener animationEndListener;

    public interface AnimationEndListener {
        void AnimationEnd();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RevealForegroundView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public RevealForegroundView(Context context) {
        super(context);
        init();
    }

    public RevealForegroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RevealForegroundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.WHITE);
    }

    public void setListener(AnimationEndListener animationEndListener) {
        this.animationEndListener = animationEndListener;
    }

    private void setFillPaintColor(int color) {
        this.fillPaint.setColor(color);
    }

    public static RevealForegroundView createRevealForegroundView(Context ctx, final View clickView, int color) {
        final RevealForegroundView rfv = new RevealForegroundView(ctx);
        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        rfv.setLayoutParams(lp);
        rfv.setFillPaintColor(color);

        rfv.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                rfv.getViewTreeObserver().removeOnPreDrawListener(this);
                rfv.startFromLocation(clickView, 50, 300, 100);

                return true;
            }
        });

        ((ViewGroup)(clickView.getRootView())).addView(rfv);

        return rfv;
    }

    private void startFromLocation(final View clickView, int overRadus, int duration, int alphaSratDelay) {
        AnimatorSet as = new AnimatorSet();

        startRevealPoint = CommonUtil.getCenterPointFromView(clickView);

        revealAnimator = ObjectAnimator.ofInt(this, "currentRadius", 0, (Math.min(clickView.getWidth(), clickView.getHeight()) / 2) + overRadus).setDuration(duration);
        revealAnimator.setInterpolator(DECELERATE);

        ObjectAnimator bgAlphaAnim = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f);
        bgAlphaAnim.setDuration(alphaSratDelay);
        bgAlphaAnim.setStartDelay(alphaSratDelay);
        bgAlphaAnim.setInterpolator(DECELERATE);

        final View thisView = this;

        revealAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

            }
        });

        as.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //1. 문제 없을까?
                //2. 실행에는 이상없지만 한번씩 로그캣에 이상한 오류가 보인다.
                //3. 자신을 자신의 내부 메소드에서 제거하는 것이 논리적 모순이 있으나 일단 통과.
                //4. 혹, 문제가 있다면 호출한 측의 AnimationEnd Listener로 removeView code를 올기면 된다.
                //5. 코드를 간략히 하기 위해 이곳에 통합하였다.
                ((ViewGroup)(clickView.getRootView())).removeView(thisView);

                animationEndListener.AnimationEnd();
            }
        });

        as.play(revealAnimator).with(bgAlphaAnim);
        as.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (state == STATE_FINISHED) {
            //canvas.drawRect(0, 0, getWidth(), getHeight(), fillPaint);
        } else {
            //Log.d("onDraw", currentRadius+"");
            canvas.drawCircle(startRevealPoint.x, startRevealPoint.y, currentRadius, fillPaint);
        }
    }

    public void setCurrentRadius(int radius) {
        this.currentRadius = radius;
        invalidate();
    }
}