package com.infoline.doctorcha.core.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.infoline.doctorcha.R;

/**
 * Created by DELL on 2016-01-08.
 */

/*
•translationX
•translationY
•rotation
•rotationX
•rotationY
•scaleX
•scaleY
•pivotX
•pivotY
•x
•y
•alpha
*/

/**
 * Created by Administrator on 2015-11-18.
 */

public class AnimUtil {
    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    private static final String ROTATION = "rotation";
    private static final String SCALE_X = "scaleX";
    private static final String SCALE_Y = "scaleY";
    private static final String TRANSLATION_X = "translationX";
    private static final String TRANSLATION_Y = "translationY";

    //Fixed Library----------------------------------------------------------------------------------------------



    //UnFixed Library----------------------------------------------------------------------------------------------



    public static PropertyValuesHolder rotation(float... values) {
        return PropertyValuesHolder.ofFloat(ROTATION, values);
    }

    public static PropertyValuesHolder translationX(float... values) {
        return PropertyValuesHolder.ofFloat(View.TRANSLATION_X, values);
    }

    public static PropertyValuesHolder translationY(float... values) {
        return PropertyValuesHolder.ofFloat(TRANSLATION_Y, values);
    }

    public static PropertyValuesHolder scaleX(float... values) {
        return PropertyValuesHolder.ofFloat(SCALE_X, values);
    }

    public static PropertyValuesHolder scaleY(float... values) {
        return PropertyValuesHolder.ofFloat(SCALE_Y, values);
    }

    //----------------------------------------------------------------------------------------------

    public static void togglePopup(final View popup, boolean show) {
        if(show) {
            popup.setTranslationY(-popup.getHeight());
            popup.setVisibility(View.VISIBLE);

            popup.animate().translationY(0).setDuration(1000).setInterpolator(new BounceInterpolator()).setListener(new AnimatorListenerAdapter() {
                //1. empty AnimatorListenerAdapter()를 설정하지 않을 경우 hideTrashcan()에서 설정된 AnimatorListenerAdapter()가 적용된다.
                //2. setInterpolator등 다른 것도 마찬가지이다.
            }).start();
        }
        else {
            popup.animate().translationY(-popup.getHeight()).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    popup.setVisibility(View.GONE);
                }
            }).start();
        }
    }

    //public static void playMenuAnim(Activity activity, @IdRes int containerId, int visibleState, int delay) {
    public static void playMenuAnim(View container, boolean visible, int duration, int delay) {
        final Interpolator interpolator;
        final int toY;

        if (container != null) {
            if(visible) {
                interpolator = new OvershootInterpolator();
                toY = 0;
            }
            else {
                interpolator = null;
                toY = container.getHeight();
            }

            container.animate().translationY(toY).setInterpolator(interpolator).setStartDelay(delay).setDuration(duration).start();
        }
    }

    public static void playRevealAnimateTest(final ViewGroup targetParent) {

        int ccc = targetParent.getChildCount();

        AnimatorSet as = new AnimatorSet();

        final int radius = Math.min(targetParent.getWidth(), targetParent.getHeight());
        final View revealView = new View(targetParent.getContext());

        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(radius,  radius);
        lp.gravity = Gravity.CENTER;

        revealView.setLayoutParams(lp);

        //revealView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //revealView.setBackgroundColor(0x77ffffff);
        revealView.setBackgroundResource(R.drawable.x_circle_reveal);
        targetParent.addView(revealView);

        ObjectAnimator bgScaleYAnim = ObjectAnimator.ofFloat(revealView, "scaleY", 0.1f, 1f);
        bgScaleYAnim.setDuration(200);
        bgScaleYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator bgScaleXAnim = ObjectAnimator.ofFloat(revealView, "scaleX", 0.1f, 1f);
        bgScaleXAnim.setDuration(200);
        bgScaleXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator bgAlphaAnim = ObjectAnimator.ofFloat(revealView, "alpha", 1f, 0f);
        bgAlphaAnim.setDuration(200);
        bgAlphaAnim.setStartDelay(150);
        bgAlphaAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

        as.playTogether(bgScaleYAnim, bgScaleXAnim, bgAlphaAnim);

        as.addListener(new AnimatorListenerAdapter() {
            void xxx() {
                targetParent.removeView(revealView);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                xxx();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                xxx();
            }
        });

        as.start();
    }

    public static AnimatorSet alphaAnim_down(View v, float startAlpha, float endAlpha, int duration) {
        AnimatorSet as = new AnimatorSet();

        ObjectAnimator bgAlphaAnim = ObjectAnimator.ofFloat(v, "alpha", startAlpha, endAlpha);
        bgAlphaAnim.setDuration(duration);
        //bgAlphaAnim.setStartDelay(150);
        bgAlphaAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

        bgAlphaAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

            }
        });

        as.play(bgAlphaAnim);

        //as.start();
        return as;
    }

    public static void lotateView(final View v, final int resId) {
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(v, "rotation", 0f, 360f);
        rotationAnim.setDuration(300);
        rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(v, "scaleX", 0.2f, 1f);
        bounceAnimX.setDuration(300);
        bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

        ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(v, "scaleY", 0.2f, 1f);
        bounceAnimY.setDuration(300);
        bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
        bounceAnimY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                //((FloatingActionButton)v).setImageResource(resId);
            }
        });

        animatorSet.play(rotationAnim);
        animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {                //likeAnimations.remove(holder);
                //holder.vBgLike.setVisibility(View.GONE);
                //holder.ivLike.setVisibility(View.GONE);
                ((FloatingActionButton) v).setImageResource(resId);
            }
        });

        animatorSet.start();
    }

    public static void sb_ViewExpand(View v) {
        // 1. MeasureSpec.AT_MOST : wrap_content 에 매핑되며 뷰 내부의 크기에 따라 크기가 달라진다.
        // 2. MeasureSpec.EXACTLY : fill_parent, match_parent 로 외부에서 미리 크기가 지정되었다.
        // 3. MeasureSpec.UNSPECIFIED : Mode 가 설정되지 않았을 경우. 소스상에서 직접 넣었을 때 주로 불립니다.
        // 4. 정확하지 않는 경우가 있다. AT_MOST는 아예 안 먹는다
        v.setVisibility(View.VISIBLE);

        /*
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.AT_MOST);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.AT_MOST);

        v.measure(widthSpec, heightSpec);
        */

        v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        ValueAnimator mAnimator = slideAnimator(v, 0, v.getMeasuredHeight());
        mAnimator.start();
    }

    public static void sb_ViewCollapse(final View v) {
        final int finalHeight = v.getHeight();

        ValueAnimator mAnimator = slideAnimator(v, finalHeight, 0);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                v.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        mAnimator.start();
    }

    private static ValueAnimator slideAnimator(final View v, int start, int end) {
        final ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(500);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                final int value = (Integer) valueAnimator.getAnimatedValue();

                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = value;
                v.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    public static AnimatorSet ScaleAfterLotateAnim2(final ImageView v, final int nextResId) {
        final AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(v, "rotation", 0f, 360f);
        rotationAnim.setDuration(700);
        //rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);


        rotationAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //v.clearAnimation();  --> 안먹는다
                ((ImageView) v).setImageResource(nextResId);
                /*


                final AnimatorSet animatorSet2 = new AnimatorSet();

                ObjectAnimator zoomInAnimX = ObjectAnimator.ofFloat(v, "scaleX", 0.2f, 1f);
                zoomInAnimX.setDuration(300);
                zoomInAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

                ObjectAnimator zoomInAnimY = ObjectAnimator.ofFloat(v, "scaleY", 0.2f, 1f);
                zoomInAnimY.setDuration(300);
                zoomInAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
                animatorSet2.play(zoomInAnimX).with(zoomInAnimY);
                animatorSet2.start();
                */
            }
        });
        //animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);



        //animatorSet.play(zoomInAnimX).with(zoomInAnimY).after(bounceAnimX).with(bounceAnimY);
        animatorSet.play(rotationAnim);


        return animatorSet;
    }

    public static AnimatorSet ScaleAfterLotateAnim(final ImageView v, final int nextResId) {
        final AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(v, "rotation", 0f, 180f);
        rotationAnim.setDuration(500);
        //rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);


        rotationAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ((ImageView) v).setImageResource(nextResId);

                final AnimatorSet animatorSet2 = new AnimatorSet();

                ObjectAnimator zoomInAnimX = ObjectAnimator.ofFloat(v, "scaleX", 0.2f, 1f);
                zoomInAnimX.setDuration(300);
                zoomInAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

                ObjectAnimator zoomInAnimY = ObjectAnimator.ofFloat(v, "scaleY", 0.2f, 1f);
                zoomInAnimY.setDuration(300);
                zoomInAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
                animatorSet2.play(zoomInAnimX).with(zoomInAnimY);
                animatorSet2.start();
            }
        });
        //animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);



        //animatorSet.play(zoomInAnimX).with(zoomInAnimY).after(bounceAnimX).with(bounceAnimY);
        animatorSet.play(rotationAnim);



        return animatorSet;
    }



    public static AnimatorSet SlideAnim(final View v, int x) {
        final AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(v, "scaleY", x == 1 ? 0f : 1f, x == 1 ? 1f : 0f);
        bounceAnimY.setDuration(1000);

        v.setPivotY(0f);

        bounceAnimY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

            }
        });

        //animatorSet.play(zoomInAnimX).with(zoomInAnimY).after(bounceAnimX).with(bounceAnimY);
        v.setVisibility(View.VISIBLE);
        animatorSet.play(bounceAnimY);

        return animatorSet;
    }

    public static void playAnim(final View v) {
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(v, "rotationY", -180f, 0f);
        //v.setPivotX(0f);
        //v.setPivotY(0f);
        rotationAnim.setDuration(500);
        rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        animatorSet.play(rotationAnim);



    }

    public static AnimatorSet flipAnim(final View v, final int nextResId) {
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(v, "rotationY", 180f, 0f);
        //v.setPivotX(0f);
        //v.setPivotY(0f);
        rotationAnim.setDuration(500);
        rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        rotationAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ((ImageView)v).setImageResource(nextResId);
            }
        });

        /*
        ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(v, "scaleX", 0.2f, 1f);
        bounceAnimX.setDuration(200);
        //bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

        ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(v, "scaleY", 0.2f, 1f);
        bounceAnimY.setDuration(200);
        //bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);


        bounceAnimX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                ((ImageView)v).setImageResource(nextResId);
            }
        });
        //animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);
        */

        animatorSet.play(rotationAnim);


        return animatorSet;
    }

    public static AnimatorSet lotateView4(final View v) {
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(v, "rotation", 0f, 360f);
        rotationAnim.setDuration(500);
        rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(v, "scaleX", 0.2f, 1f);
        bounceAnimX.setDuration(200);
        bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

        ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(v, "scaleY", 0.2f, 1f);
        bounceAnimY.setDuration(200);
        bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);

        /*
        1. 호출측에 addListener 구현
        2. 단, 에니메이션 직후의 Action이 동일할 경우는 공통 함수 별도 구현
        bounceAnimY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                ((FloatingActionButton)v).setImageResource(resId);
            }
        });
        */

        //animatorSet.play(rotationAnim);
        animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);

        return animatorSet;
    }

    public static AnimatorSet lotateView3(final View v) {
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(v, "rotation", 0f, 360f);
        rotationAnim.setDuration(500);
        rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(v, "scaleX", 0.2f, 1f);
        bounceAnimX.setDuration(200);
        bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

        ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(v, "scaleY", 0.2f, 1f);
        bounceAnimY.setDuration(200);
        bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);

        /*
        1. 호출측에 addListener 구현
        2. 단, 에니메이션 직후의 Action이 동일할 경우는 공통 함수 별도 구현
        bounceAnimY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                ((FloatingActionButton)v).setImageResource(resId);
            }
        });
        */

        //animatorSet.play(rotationAnim);
        animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);

        return animatorSet;
    }

    public static void lotateView2(final View v) {
        // Create an animation instance
        Animation an = new RotateAnimation(0.0f, 360.0f, 100, 100);

        // Set the animation's parameters
        an.setDuration(10000);               // duration in ms
        an.setRepeatCount(0);                // -1 = infinite repeated
        an.setRepeatMode(Animation.REVERSE); // reverses each repeat
        an.setFillAfter(true);               // keep rotation after animation

        // Aply animation to image view
        v.setAnimation(an);
    }

    public static AnimatorSet playRevealAnimate(final ViewGroup targetParent) {
        AnimatorSet as = new AnimatorSet();

        final int radius = Math.min(targetParent.getWidth(), targetParent.getHeight());
        final View revealView = new View(targetParent.getContext());

        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(radius,  radius);
        lp.gravity = Gravity.CENTER;

        revealView.setLayoutParams(lp);

        //revealView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //revealView.setBackgroundColor(0x77ffffff);
        revealView.setBackgroundResource(R.drawable.x_circle_reveal);
        targetParent.addView(revealView);

        ObjectAnimator bgScaleYAnim = ObjectAnimator.ofFloat(revealView, "scaleY", 0.1f, 1f);
        bgScaleYAnim.setDuration(200);
        bgScaleYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator bgScaleXAnim = ObjectAnimator.ofFloat(revealView, "scaleX", 0.1f, 1f);
        bgScaleXAnim.setDuration(200);
        bgScaleXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator bgAlphaAnim = ObjectAnimator.ofFloat(revealView, "alpha", 1f, 0f);
        bgAlphaAnim.setDuration(200);
        bgAlphaAnim.setStartDelay(150);
        bgAlphaAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

        as.playTogether(bgScaleYAnim, bgScaleXAnim, bgAlphaAnim);

        as.addListener(new AnimatorListenerAdapter() {
            void xxx() {
                targetParent.removeView(revealView);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                xxx();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                xxx();
            }
        });

        return as;
    }

    //실패
    public static AnimatorSet RevealAnimate(Context ctx) {
        //AnimatorSet as = new AnimatorSet();

        final ViewGroup rootView = (ViewGroup)(((Activity)ctx).getWindow().getDecorView().getRootView());

        //final int radius = Math.min(targetParent.getWidth(), targetParent.getHeight());
        final int radius =100;
        final View revealView = new View(ctx);

        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        revealView.setLayoutParams(lp);
        revealView.setBackgroundColor(Color.RED);

        revealView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                revealView.getViewTreeObserver().removeOnPreDrawListener(this);
                ///////////////////////////rbv.startFromLocation(xy);

                final int startLocationX = 200;
                final int startLocationY = 200;

                final ObjectAnimator revealAnimator = ObjectAnimator.ofInt(revealView, "currentRadius", 0, 100).setDuration(1000);
                /////////revealAnimator.setInterpolator(INTERPOLATOR);

                revealAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //changeState(STATE_FINISHED);
                        // ((Activity) (((View) vvv).getContext())).finish();
                        int iii = 0;
                    }
                });

                revealAnimator.start();

                return true;
            }
        });

        ViewTreeObserver.OnDrawListener odl = new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {
                revealView.getViewTreeObserver().removeOnDrawListener(this);
                revealView.invalidate();
            }
        };

        revealView.getViewTreeObserver().addOnDrawListener(odl);
        rootView.addView(revealView);




        /*
        ObjectAnimator bgScaleYAnim = ObjectAnimator.ofFloat(revealView, "scaleY", 0.1f, 1f);
        bgScaleYAnim.setDuration(200);
        bgScaleYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator bgScaleXAnim = ObjectAnimator.ofFloat(revealView, "scaleX", 0.1f, 1f);
        bgScaleXAnim.setDuration(200);
        bgScaleXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator bgAlphaAnim = ObjectAnimator.ofFloat(revealView, "alpha", 1f, 0f);
        bgAlphaAnim.setDuration(200);
        bgAlphaAnim.setStartDelay(150);
        bgAlphaAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

        as.playTogether(bgScaleYAnim, bgScaleXAnim, bgAlphaAnim);

        as.addListener(new AnimatorListenerAdapter() {
            void xxx() {
                targetParent.removeView(revealView);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                xxx();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                xxx();
            }
        });
        */

        //as.start();


        return null;
    }

    public static void playFlipAnim(final View v) {
        v.setTranslationY(v.getHeight());
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(v, "translationY", 0);
        //v.setPivotX(.5f);
        //v.setPivotY(.5f);
        rotationAnim.setDuration(500);
        //rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        /*
        ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(v, "scaleX", 0.2f, 1f);
        bounceAnimX.setDuration(200);
        //bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

        ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(v, "scaleY", 0.2f, 1f);
        bounceAnimY.setDuration(200);
        //bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);


        bounceAnimX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                ((ImageView)v).setImageResource(nextResId);
            }
        });
        //animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);
        */

        animatorSet.play(rotationAnim);
        animatorSet.start();

    }
}
