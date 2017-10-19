package com.infoline.doctorcha.presentation.activity;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.CoreCons;
import com.infoline.doctorcha.core.view.RevealFrameLayout;
import com.infoline.doctorcha.presentation.MainCons;
import com.infoline.doctorcha.presentation.bean.BeanName_Res;
import com.infoline.doctorcha.presentation.fragment.HomeOfShopsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainOfShopsActivity extends AppCompatActivity  implements RevealFrameLayout.OnRevealStateChangeListener {
    @BindView(R.id.rfl)
    RevealFrameLayout rfl;
    @BindView(R.id.cl)
    CoordinatorLayout cl;
    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.fl_fragment)
    FrameLayout fl_fragment;

    private PointF revealStartPoint;

    /*   이거 머지 - 필요없는 것 같은데....
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (resultCode == RESULT_OK) {
            if (imageReturnedIntent.getData() != null) {
                //If uploaded with Android Gallery (max 1 image)
                Uri selectedImage = imageReturnedIntent.getData();
                InputStream imageStream;
                try {
                    imageStream = getContentResolver().openInputStream(selectedImage);
                    Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                    //////////////////////////////////////////////photos.add(yourSelectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                //If uploaded with the new Android Photos gallery
                ClipData clipData = imageReturnedIntent.getClipData();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    clipData.getItemAt(i);
                    // more code logic
                }
            }
        }
    }
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mainofshops);
        ButterKnife.bind(this);

        final BeanName_Res beanFavoriteCategory = (BeanName_Res) getIntent().getSerializableExtra(BeanName_Res.class.getSimpleName());

        tb.setTitle(beanFavoriteCategory.nm); //setSupportActionBar(tb) 보다 선행되어야 동작한다
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle(beanFavoriteCategory.getNm());

        ////////////////////////////////////ctl.setTitle("오토그램");
        /////////////////////////////////////ctl.setCollapsedTitleTextColor(getResources().getColor(android.R.color.transparent));
        /*
        //ctl.setStatusBarScrim();
        ////////ctl.setScrimsShown(true); 반응없다
        ////////ctl.setContentScrimColor(getResources().getColor(android.R.color.transparent));

        /*
        NavigationView navigationView = (NavigationView) findViewById(R.id.nv_left);
        navigationView.setNavigationItemSelectedListener(this);
        */

        //NavigationView navigationView2 = (NavigationView) findViewById(R.id.nv_right);
        //navigationView2.setNavigationItemSelectedListener(this);

        //---------------------------------------------------------------------------------------------------

        final Intent intent = getIntent();
        final Point startPoint = intent.getParcelableExtra(MainCons.EnumExtraName.ANIM_START_POINT.name());
        revealStartPoint = new PointF((float)startPoint.x, (float)startPoint.y);

        setupRevealBackground(savedInstanceState);

        /*
        ll_bottom.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                ll_bottom.getViewTreeObserver().removeOnPreDrawListener(this);

                ll_bottom.setTranslationY(ll_bottom.getHeight());
                return true;
            }
        });
        */

        final FragmentManager fm = getFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();

        //범용의 경우는 HomeOfShopsFragment, 특정 카테고리(중고차매매 등)은 개별 Fragment를 사용할 예정인데 굳이 필요한지는 미확정이다
        final Fragment fragment = HomeOfShopsFragment.newInstance(beanFavoriteCategory.id);

        ft.replace(R.id.fl_fragment, fragment);
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(revealStartPoint == null) {
            super.onBackPressed();
        }
        else {
            rfl.startRevealAnimation(null, 500, 2000, 200, null, null, true);
        }
    }

    @Override
    public void finish() {
        super.finish();

        if(revealStartPoint == null) {
            overridePendingTransition(R.anim.activity_zoomin, R.anim.activity_zoomout);
        }
    }

    @Override
    public void onRevealStateChange(RevealFrameLayout.RevealState revealState) {
        /*
        if (RevealBackgroundView.STATE_FINISHED == state) {
            vTakePhotoRoot.setVisibility(View.VISIBLE);
            if (pendingIntro) {
                startIntroAnimation();
            }
        } else {
            vTakePhotoRoot.setVisibility(View.INVISIBLE);
        }
        */

        if(revealState == RevealFrameLayout.RevealState.FINISHED) {
            if(rfl.isReverse) {
                finish();
                overridePendingTransition(0, 0);
            }
            else {
                /*
                ll_bottom.animate()
                        .translationY(0)
                        .setInterpolator(new OvershootInterpolator(1.f))
                        //.setStartDelay(300)
                        .setDuration(1000)
                        .start();
                        */
            }
        }
    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        //rfl.setFillPaintColor(Color.parseColor("#00000000"));

        if (savedInstanceState == null) {
            rfl.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    rfl.getViewTreeObserver().removeOnPreDrawListener(this);
                    //ll_bottom.setTranslationY(getResources().getDimensionPixelOffset(R.dimen.fab_size) + getResources().getDimensionPixelOffset(R.dimen.fab_margin_bottom));

                    //ll_bottom.setTranslationY(ll_bottom.getHeight());

                    //public void startRevealAnimation(int[] startLocation, int revealDuration, int alphaDuration, int alphaStartDelay, Interpolator revealInterpolator, Interpolator alphaInterpolator, boolean isReverse) {
                    rfl.startRevealAnimation(revealStartPoint, 500, 500, 0, CoreCons.ACCELERATE, null, false);
                    return true;
                }
            });
        } else {
            rfl.setToFinishedFrame();
        }
    }

    /*
    private void showMenu() {
        //ll_bottom.setVisibility(View.INVISIBLE);
        iv_bm1.setVisibility(View.INVISIBLE);
        iv_bm2.setVisibility(View.INVISIBLE);

        menu_layout.setVisibility(View.VISIBLE);

        List<Animator> animList = new ArrayList<>();

        ViewGroup vg = (ViewGroup) menu_layout.getChildAt(0);

        for (int i = 0, len = vg.getChildCount(); i < len; i++) {
            animList.add(createShowItemAnimator(vg.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(600);
        animSet.setInterpolator(new OvershootInterpolator());
        animSet.playTogether(animList);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);


            }
        });
        animSet.start();
        iv_bm3.setImageResource(android.R.drawable.ic_menu_send);
    }

    private void hideMenu() {
        List<Animator> animList = new ArrayList<>();

        ViewGroup vg = (ViewGroup) menu_layout.getChildAt(0);

        for (int i = vg.getChildCount() - 1; i >= 0; i--) {
            animList.add(createHideItemAnimator(vg.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();

        animSet.setDuration(500);
        animSet.setInterpolator(new AnticipateInterpolator());
        animSet.playTogether(animList);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                menu_layout.setVisibility(View.INVISIBLE);
                //ll_bottom.setVisibility(View.VISIBLE);
                iv_bm1.setVisibility(View.VISIBLE);
                iv_bm2.setVisibility(View.VISIBLE);

                iv_bm3.setImageResource(viewHolderType == MainCons.ViewHolderType.BIGIMAGE ? R.drawable.ic_view_stream_white_24dp : (viewHolderType == MainCons.ViewHolderType.THUMBNAIL ? R.drawable.ic_view_list_white_24dp : R.drawable.ic_view_quilt_white_24dp));

                if (isMenuClick) {
                    isMenuClick = false;
                    ////////////////////////////////////curShopsFragment.setViewHolderType(viewHolderType);
                }
            }
        });
        animSet.start();

    }

    private Animator createShowItemAnimator(View v) {
        //animation이 비정상적으로 Cancel된 경우를 위하여 아래 초기화 필요
        v.setRotation(0f);
        v.setTranslationX(0f);

        final float distance;
        final int width = v.getWidth();
        final int gap = width / 2; //View간 이격거리

        if (v.getId() == R.id.iv_bigimage) {
            distance = 3;
        } else if (v.getId() == R.id.iv_thumbnail) {
            distance = 2;
        } else {
            distance = 1;
        }

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(v, PropertyValuesHolder.ofFloat(View.ROTATION, 0f, 720f), PropertyValuesHolder.ofFloat(View.SCALE_X, 0f, 1.2f), PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f, 1.2f), PropertyValuesHolder.ofFloat(View.TRANSLATION_X, -(distance * width * 1.7f)));

        return anim;
    }

    private Animator createHideItemAnimator(final View v) {
        final float distance;
        final int width = v.getWidth();

        if (v.getId() == R.id.iv_bigimage) {
            distance = 3;
        } else if (v.getId() == R.id.iv_bigimage) {
            distance = 2;
        } else {
            distance = 1;
        }
        Animator anim = ObjectAnimator.ofPropertyValuesHolder(v, PropertyValuesHolder.ofFloat(View.ROTATION, 0f, 720f), PropertyValuesHolder.ofFloat(View.SCALE_X, 1.2f, 0f), PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.2f, 0f), PropertyValuesHolder.ofFloat(View.TRANSLATION_X, (distance * width * 1.7f)));


        //필요없을 것 같다.
        //anim.addListener(new AnimatorListenerAdapter() {
            //@Override
            //public void onAnimationEnd(Animator animation) {
                //super.onAnimationEnd(animation);
                //v.setTranslationX(0f);
            //}
        //});

        //return anim;
    }
    */

    private void showSubCategory() {
        final String[] items = {"경정비", "오토미션전문", "커먼레일전문", "기능장샵"};

        Dialog dialog = new Dialog(this, R.style.PauseDialog);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LinearLayout root = (LinearLayout)getLayoutInflater().inflate(R.layout.tmp_dialog_list_tv, null);

        for(String item : items) {
            TextView tv = new TextView(this);
            tv.setText(item);
            tv.setPadding(6, 6, 6, 6);

            LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            tvParams.setMargins(0,3,0,3);
            tv.setLayoutParams(tvParams);

            //tv.setPadding(8,8,8,8);
            tv.setTextSize(17);

            root.addView(tv);
        }


        dialog.setContentView(root);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setTitle("카테고리 선택");

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        dialogWindow.setAttributes(lp);

        lp.x = (int)tb.getWidth() - lp.width; // The new position of the X coordinates
        lp.y = (int)(tb.getBottom()); // The new position of the Y coordinates
        //lp.x = 50; // The new position of the X coordinates
        //lp.y = 112; // The new position of the Y coordinates

        dialog.show();
    }
}