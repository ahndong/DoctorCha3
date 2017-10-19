package com.infoline.doctorcha.presentation.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.etc.MarketVersionChecker;
import com.infoline.doctorcha.core.util.CommonUtil;

import java.io.File;

import static com.infoline.doctorcha.presentation.MainCons.CN_PATH_DOCTORCHA_DOWNLOAD;
import static com.infoline.doctorcha.presentation.MainCons.CN_PATH_DOCTORCHA_PHOTO;
import static com.infoline.doctorcha.presentation.MainCons.CN_PATH_DOCTORCHA_TEMP;
import static com.infoline.doctorcha.presentation.MainCons.CN_PATH_DOCTORCHA_VIDEO;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        final int option = getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(option);

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
    }

    /*
    @Override
    protected void onResume() {
        super.onResume();

        final ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo ni = cm.getActiveNetworkInfo();

        if (ni == null) {
            (new AlertDialog.Builder(this)).setMessage("네트워크를 사용할 수 없습니다.\n확인 후 앱을 다시 실행해 주세요").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).show();
        } else {
            Toast.makeText(this, ni.getTypeName(), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    callMainActivity();
                };
            }, 1000);
        }
    }
    */

    @Override
    protected void onResume() {
        super.onResume();

        final ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo ni = cm.getActiveNetworkInfo();

        if (ni == null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("네트워크를 사용할 수 없습니다.\n확인 후 앱을 다시 실행해 주세요").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        finish();
                    }
                }
            }).show();
        } else {
            Toast.makeText(this, ni.getTypeName(), Toast.LENGTH_SHORT).show();

            new Thread() {
                public void run() {
                    final String storeVersion = MarketVersionChecker.getMarketVersion(getPackageName());
                    final Bundle bundle = new Bundle();
                    bundle.putString("storeVersion", storeVersion);

                    final Message message = handler.obtainMessage();
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }.start();
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message message) {
            final Bundle bundle = message.getData();
            final String storeVersion = bundle.getString("storeVersion");

            if(storeVersion != null) {
                try {
                    final String packageName = getApplicationContext().getPackageName();
                    final String deviceVversion = getPackageManager().getPackageInfo(packageName, 0).versionName;

                    if (storeVersion.compareTo(deviceVversion) > 0) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                        builder.setMessage("새로운 버전이 출시 되었습니다\n보다 나은 사용을 위해 업데이트 하시겠습니까?").setCancelable(false).setPositiveButton("업데이트", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == DialogInterface.BUTTON_POSITIVE) {
                                    try {
                                        startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)), 1000);
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)), 1000);
                                    }
                                }
                            }
                        }).setNegativeButton("다음에",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                callMainActivity();
                                            };
                                        }, 500);
                                    }
                                }).show();
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                callMainActivity();
                            };
                        }, 500);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        CommonUtil.writeLog(null);
    }

    private void callMainActivity() {
        //iv_light.clearAnimation();
        CommonUtil.writeLog(null);

        final Intent intent3 = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent3);
        overridePendingTransition(R.anim.activity_zoomin, R.anim.activity_zoomout);

        finish();
    }
}
