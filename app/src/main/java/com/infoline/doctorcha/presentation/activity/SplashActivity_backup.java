package com.infoline.doctorcha.presentation.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.util.AnimUtil;
import com.infoline.doctorcha.core.util.CommonUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

import static com.infoline.doctorcha.presentation.MainCons.CN_PATH_DOCTORCHA_DOWNLOAD;
import static com.infoline.doctorcha.presentation.MainCons.CN_PATH_DOCTORCHA_PHOTO;
import static com.infoline.doctorcha.presentation.MainCons.CN_PATH_DOCTORCHA_TEMP;
import static com.infoline.doctorcha.presentation.MainCons.CN_PATH_DOCTORCHA_VIDEO;

public class SplashActivity_backup extends AppCompatActivity {
    @BindView(R.id.iv_robot1) ImageView iv_robot1;
    @BindView(R.id.iv_robot2) ImageView iv_robot2;
    @BindView(R.id.iv_robot3) ImageView iv_robot3;

    ObjectAnimator alphaAnim1;
    ObjectAnimator alphaAnim2;
    ObjectAnimator alphaAnim3;

    //ObjectAnimator alphaAnim;

    @OnClick({R.id.fab})
    protected void OnClick_1(View v){
        //CommonUtil.writeLog(getResources().getDisplayMetrics().density + " : " + iv_car.getWidth() + " - " + iv_car.getHeight());
        //alphaAnim.end();
        callMainActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        iv_robot1 = (ImageView)findViewById(R.id.iv_robot1);
        iv_robot2 = (ImageView)findViewById(R.id.iv_robot2);
        iv_robot3 = (ImageView)findViewById(R.id.iv_robot3);

        final int option = getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(option);


        if(savedInstanceState == null) {
            iv_robot1.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    iv_robot1.getViewTreeObserver().removeOnPreDrawListener(this);
                    iv_robot1.setAlpha(0f);
                    return true;
                }
            });
        }

        AnimatorSet as = new AnimatorSet();

        alphaAnim1 = ObjectAnimator.ofFloat(iv_robot1, "alpha", 0f, 1f);
        alphaAnim2 = ObjectAnimator.ofFloat(iv_robot2, "alpha", 0f, 1f);
        alphaAnim3 = ObjectAnimator.ofFloat(iv_robot3, "alpha", 0f, 1f);

        alphaAnim1.setDuration(800);
        alphaAnim1.setStartDelay(0);
        alphaAnim1.setRepeatCount(1);
        alphaAnim1.setRepeatMode(ValueAnimator.REVERSE);

        alphaAnim2.setDuration(800);
        alphaAnim2.setStartDelay(0);
        alphaAnim2.setRepeatCount(1);
        alphaAnim2.setRepeatMode(ValueAnimator.REVERSE);

        alphaAnim3.setDuration(800);
        alphaAnim3.setStartDelay(0);
        alphaAnim3.setRepeatCount(1);
        alphaAnim3.setRepeatMode(ValueAnimator.REVERSE);

        /*
        alphaAnim1.addListener(new AnimatorListenerAdapter() {
            int i = 0;

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                i++;

                if (i == 1) {
                    animation.end();
                    alphaAnim2.start();
                    //callMainActivity();
                }
            }
        });

        alphaAnim2.addListener(new AnimatorListenerAdapter() {
            int i = 0;

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                i++;

                if (i == 1) {
                    animation.end();
                    alphaAnim3.start();
                    //callMainActivity();
                }
            }
        });

        alphaAnim3.addListener(new AnimatorListenerAdapter() {
            int i = 0;

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                i++;

                if (i == 1) {
                    animation.end();
                    //alphaAnim1.start();
                    callMainActivity();
                }
            }
        });

        //as.play(alphaAnim);
        //as.start();

        alphaAnim1.start();
        */

        //---------------------------------------------------------------------------------------------------------------------
        /*
        final File dir1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DoctorCha/help");
        if(!dir1.exists()) {
            boolean xx = dir1.mkdirs();
        }

        final File dir2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DoctorCha/CameraTemp");
        if(!dir2.exists()) {
            boolean xx = dir2.mkdirs();
        }
        */

        final File doctorChaPhoto = new File(CN_PATH_DOCTORCHA_PHOTO);
        if(!doctorChaPhoto.exists()) {
            boolean xx = doctorChaPhoto.mkdirs();
        }

        final File doctorChaVideo = new File(CN_PATH_DOCTORCHA_VIDEO);
        if(!doctorChaVideo.exists()) {
            boolean xx = doctorChaVideo.mkdirs();
        }

        final File doctorChaDownload = new File(CN_PATH_DOCTORCHA_DOWNLOAD);
        if(!doctorChaDownload.exists()) {
            boolean xx = doctorChaDownload.mkdirs();
        }

        final File doctorChaTempr = new File(CN_PATH_DOCTORCHA_TEMP);
        if(!doctorChaTempr.exists()) {
            boolean xx = doctorChaTempr.mkdirs();
        }

        final File dir1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DoctorCha/VideoThumbnail");
        if(!dir1.exists()) {
            boolean xx = dir1.mkdirs();
        }

        callMainActivity();
        //---------------------------------------------------------------------------------------------------------------------

        /*
        Animation animation = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.splash_light_anim);
        animation.setRepeatCount(20);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setAnimationListener(new Animation.AnimationListener() {
            int i = 0;

            @Override
            public void onAnimationStart(Animation animation) {
                CommonUtil.writeLog(null);
                getPojo();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                CommonUtil.writeLog(null);
                callMainActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                i++;
                CommonUtil.writeLog("repeat count == " + i);

                if(i > 10) {

                }
            }
        });

        iv_light.startAnimation(animation);
        */
    }

    @Override
    protected void onResume() {
        super.onResume();

        final ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo ni = cm.getActiveNetworkInfo();

        if (ni != null) { // connected to the internet
            if (ni.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                Toast.makeText(this, ni.getTypeName(), Toast.LENGTH_SHORT).show();
            } else if (ni.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                Toast.makeText(this, ni.getTypeName(), Toast.LENGTH_SHORT).show();
            }
        } else {
            // not connected to the internet
        }
    }

    private Animator createLightAnim(View v, boolean isZoomIn) {
        //v.setScaleX(0f);
        //v.setScaleY(0f);

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                v,
                AnimUtil.rotation(0f, 1080f),
                AnimUtil.scaleX(isZoomIn ? 0f : 1f, isZoomIn ? 1f : 0f),
                AnimUtil.scaleY(isZoomIn ? 0f : 1f, isZoomIn ? 1f : 0f)
        );

        return anim;
    }

    private void callMainActivity() {
        //iv_light.clearAnimation();
        CommonUtil.writeLog(null);

        final Intent intent3 = new Intent(SplashActivity_backup.this, MainActivity.class);
        startActivity(intent3);
        overridePendingTransition(R.anim.activity_zoomin, R.anim.activity_zoomout);

        finish();
    }
}
