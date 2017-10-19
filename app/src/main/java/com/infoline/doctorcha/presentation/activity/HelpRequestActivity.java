package com.infoline.doctorcha.presentation.activity;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.httphelper.ServiceGenerator;
import com.infoline.doctorcha.core.listener.MediaScanFileHelper;
import com.infoline.doctorcha.core.util.CommonUtil;
import com.infoline.doctorcha.core.util.MediaUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

/*
1. Standsard Material -->
*/
public class HelpRequestActivity extends AppCompatActivity {
    @BindView(R.id.vf) public ViewFlipper vf;
    @BindView(R.id.iv_11) public ImageView iv_11;
    @BindView(R.id.iv_12) public ImageView iv_12;
    @BindView(R.id.iv_13) public ImageView iv_13;
    @BindView(R.id.iv_14) public ImageView iv_14;
    @BindView(R.id.iv_photo) public ImageView iv_photo;
    @BindView(R.id.picker1) public View picker1;
    @BindView(R.id.picker2) public View picker2;
    @BindView(R.id.picker3) public View picker3;
    @BindView(R.id.bt_send) public Button bt_send;
    @BindView(R.id.bt_start) public Button bt_start;
    @BindView(R.id.et_tt) public EditText et_tt;
    @BindView(R.id.et_bd) public EditText et_bd;

    private final ImageLoader imageLoader = ImageLoader.getInstance();
    private int curPickerIndex;
    private View[] pickerList;
    private Uri[] uriList = new Uri[] {null, null, null};

    final MediaUtil mediaUtil = new MediaUtil();

    private float initialX;

    private void initPickerButton(boolean empty) {
        pickerList[curPickerIndex].findViewById(R.id.iv_camera).setVisibility(empty ? View.VISIBLE : View.GONE);
        pickerList[curPickerIndex].findViewById(R.id.iv_clear).setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private class Login {
        String id;
        String pw;

        Login(String id, String pw) {
            this.id = id;
            this.pw = pw;
        }
    }

    private interface RequestHelpService {
        @POST("help/send_help")
        Call<ResponseBody> sendHelp(@Body RequestBody body);
    }
    
    @OnClick({R.id.bt_send, R.id.bt_start})
    protected void OnClick_send(View v){
        if(v.getId() == R.id.bt_send) {
            RequestHelpService service = ServiceGenerator.createService(RequestHelpService.class);

            MediaType MEDIA_TYPE_IMG = MediaType.parse("image/jpeg");
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);

            try {
                for(Uri uri : uriList) {
                    if(uri != null) {
                        final File file = new File(mediaUtil.getRealPathFromUri(this, uri));
                        final RequestBody requestBody = RequestBody.create(MEDIA_TYPE_IMG, file);
                        builder.addFormDataPart("imageList", file.getName(), requestBody);
                    }
                }

                TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);

                builder.addFormDataPart("hn",  tm.getLine1Number());
                builder.addFormDataPart("tt",  et_tt.getText().toString());
                builder.addFormDataPart("bd",  et_bd.getText().toString());

                RequestBody finalRequestBody = builder.build();

                //------------------------------------------------

                Call<ResponseBody> call = service.sendHelp(finalRequestBody);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        CommonUtil.writeLog("response == " + response.body().toString());
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        CommonUtil.writeLog(t.getMessage());
                        t.printStackTrace();
                        t.getCause();
                    }
                });
            }catch (Exception e) {
                CommonUtil.writeLog(e.getMessage());
                e.printStackTrace();
            }
        }
        else {
            finish();
            overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
        }
    }

/*
    @OnClick({R.id.bt_send, R.id.bt_start})
    protected void OnClick_send(View v){
        if(v.getId() == R.id.bt_send) {
            RequestHelpService service = ServiceGenerator.createService(RequestHelpService.class);

            MediaType MEDIA_TYPE_IMG = MediaType.parse("image/jpeg");
            RequestBody requestBody;

            Map<String, Login> loginMap = new HashMap<>();
            Map<String, RequestBody> imageMap = new HashMap<>();

            loginMap.put("loginMap", new Login("imakhan", "iampassword"));

            int i = 0;

            try {
                for(Uri uri : uriList) {
                    if(uri != null) {
                        File file = new File(mediaUtil.getRealPathFromUri(this, uri));
                        requestBody = RequestBody.create(MEDIA_TYPE_IMG, file);
                        imageMap.put("imageMap", requestBody);
                    }
                }

                //Call<ResponseBody> call = service.requestHelp(loginMap, imageMap);
                Call<ResponseBody> call = service.requestHelp(loginMap);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        CommonUtil.writeLog("response == " + response.body().toString());
                        bt_send.setText("response == " + response.body().toString());
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                        t.getCause();
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            finish();
            overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
        }
    }
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_helprequest);
        ButterKnife.bind(this);

        final int option = getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(option);

        vf.setInAnimation(this, android.R.anim.fade_in);
        vf.setOutAnimation(this, android.R.anim.fade_out);

        pickerList = new View[] {picker1, picker2, picker3};

        imageLoader.displayImage("drawable://" + R.drawable.quick_01, iv_11);
        imageLoader.displayImage("drawable://" + R.drawable.quick_02, iv_12);
        imageLoader.displayImage("drawable://" + R.drawable.quick_03, iv_13);
        imageLoader.displayImage("drawable://" + R.drawable.quick_04, iv_14);

        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View curPicker = (View)v.getParent();
                curPickerIndex = curPicker.getId() == R.id.picker1 ? 0 : (curPicker.getId() == R.id.picker2 ? 1 : 2);

                if(v.getId() == R.id.iv_camera) {
                    startActivityForResult(mediaUtil.getMediaChooser(HelpRequestActivity.this), 999);
                }
                else {
                    ((ImageView)curPicker.findViewById(R.id.iv_photo)).setImageBitmap(null);
                    uriList[curPickerIndex] = null;
                    initPickerButton(true);
                }
            }
        };

        CommonUtil.writeLog("Build.VERSION.SDK_INT == " + Build.VERSION.SDK_INT);

        //ButterKnife로 listener를 걸면 picker1의 child view만 동작한다
        CommonUtil.assignOnClickListener(new View[]{picker1.findViewById(R.id.iv_camera), picker1.findViewById(R.id.iv_clear),
                picker2.findViewById(R.id.iv_camera), picker2.findViewById(R.id.iv_clear),
                picker3.findViewById(R.id.iv_camera), picker3.findViewById(R.id.iv_clear)}, ocl);

        delayedShowSlider();
        sliderHandler.postDelayed(sliderRunnable, 1000);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 999) {
                //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);하더라도 아래와 같다
                //Intent.ACTION_SEND_MULTIPLE.equals(data.getAction() == false
                //data.hasExtra(Intent.EXTRA_STREAM) == false
                //final ArrayList<Parcelable> list = data.getParcelableArrayListExtra(Intent.EXTRA_STREAM); //--> null

                final List<Uri> tmpUriList = new ArrayList<>();

                if(data == null) {
                    /*
                    Uri imageUri = mediaUtil.getLastCaptureImageUri(this);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    */

                    MediaScanFileHelper.OnMediaScanListener onMediaScanListener = new MediaScanFileHelper.OnMediaScanListener() {
                        public void onError() {
                            CommonUtil.writeLog("onError");
                        }

                        public void onSuccess(Uri uri) {
                            CommonUtil.writeLog(Uri.decode(uri.toString()));
                            tmpUriList.add(uri);

                            new Thread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            applySelectedImages(tmpUriList);
                                        }
                                    });
                                }
                            }).start();

                        }
                    };

                    MediaScanFileHelper mediaScanFileHelper = new MediaScanFileHelper(this);
                    mediaScanFileHelper.scanFile(new File(mediaUtil.realPath), onMediaScanListener);
                }
                else {
                    final ClipData cd = data.getClipData();

                    if (cd != null) {
                        //google photo intent --> 다른 Intent는 무엇이 있는지 확인 못함
                        final int selectedCount = cd.getItemCount() > 3 ? 3 : cd.getItemCount();

                        for (int i = 0; i < selectedCount; i++) {
                            tmpUriList.add(cd.getItemAt(i).getUri());
                        }

                    } else {
                        tmpUriList.add( data.getData());
                    }

                    applySelectedImages(tmpUriList);
                }
            }
        }
    }

    private void applySelectedImages(List<Uri> tmpUriList) {
        final int applyCount = tmpUriList.size();

        if(applyCount == 1) {
            curPickerIndex = pickerList[0].findViewById(R.id.iv_clear).getVisibility() == View.GONE ? 0 : (pickerList[1].findViewById(R.id.iv_clear).getVisibility() == View.GONE ? 1 : 2);
        }
        else if(applyCount == 2) {
            curPickerIndex = pickerList[0].findViewById(R.id.iv_clear).getVisibility() == View.GONE ? 0 : 1;
        }
        else {
            curPickerIndex = 0;
        }

        for(Uri uri : tmpUriList) {
            CommonUtil.writeLog(Uri.decode(uri.toString()));

            ImageLoader.getInstance().displayImage(Uri.decode(uri.toString()), (ImageView) pickerList[curPickerIndex].findViewById(R.id.iv_photo));
            initPickerButton(false);

            uriList[curPickerIndex] = uri;

            curPickerIndex++;
        }
    }

    /*
    @Override
    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = touchevent.getX();
                break;
            case MotionEvent.ACTION_UP:
                float finalX = touchevent.getX();
                if (initialX > finalX) {
                    vf.showNext();
                }
                else {
                    vf.showPrevious();
                }
                break;
        }
        return false;
    }
    */

    Handler sliderHandler = new Handler();
    Runnable sliderRunnable;
    boolean stopSliding = false;

    public void delayedShowSlider() {
        sliderHandler = new Handler();
        sliderRunnable = new Runnable() {
            public void run() {
                if (!stopSliding) {
                    vf.showNext();
                    sliderHandler.postDelayed(sliderRunnable, 2000);
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        /*
        final DrawerLayout dl = (DrawerLayout) findViewById(R.id.dl);
        if (dl.isDrawerOpen(GravityCompat.START)) {
            dl.closeDrawer(GravityCompat.START);
        }
        else if (dl.isDrawerOpen(GravityCompat.END)) {
            dl.closeDrawer(GravityCompat.END);
        }
        else {
            //super.onBackPressed();
            moveTaskToBack(true);
        }
        */

        finish();
        overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
    }

    //----------------------------------------------------------------------------------------------


}
