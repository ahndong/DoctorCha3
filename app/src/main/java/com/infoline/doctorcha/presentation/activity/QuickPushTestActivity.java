package com.infoline.doctorcha.presentation.activity;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.httphelper.ServiceGenerator;
import com.infoline.doctorcha.core.util.MediaUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

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
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class QuickPushTestActivity extends AppCompatActivity {
    @BindView(R.id.bt_11)
    Button bt_11;
    @BindView(R.id.tv_11)
    TextView tv_11;
    @BindView(R.id.iv_11)
    ImageView iv_11;

    @OnClick({R.id.bt_11})
    protected void OnClick_trashcan(View v){
        MediaUtil mediaUtil = new MediaUtil();
        startActivityForResult(mediaUtil.getMediaChooser(QuickPushTestActivity.this), 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_quickpushtest);
        ButterKnife.bind(this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);하더라도 아래와 같다
                //Intent.ACTION_SEND_MULTIPLE.equals(data.getAction() == false
                //data.hasExtra(Intent.EXTRA_STREAM) == false
                //final ArrayList<Parcelable> list = data.getParcelableArrayListExtra(Intent.EXTRA_STREAM); //--> null

                final ClipData cd = data.getClipData();

                if (cd != null) {

                } else {
                    final Uri uri = data.getData();

                    tv_11.setText(Uri.decode(uri.toString()));
                    ImageLoader.getInstance().displayImage(Uri.decode(uri.toString()), (ImageView) iv_11);

                    uploadFile(uri);
                }
            }
        }
    }

    private void uploadFile(Uri uri) {
        // create upload service client
        FileUploadService service = ServiceGenerator.createService(FileUploadService.class);

        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        //File file = FileUtils.getFile(this, uri);
        String path = null;

        String [] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);

        if(cursor != null && cursor.moveToFirst()) {
            int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            path = cursor.getString(index);
            cursor.close();
        }



        File file = new File(path);

        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

        // add another part within the multipart request
        String descriptionString = "hello, this is description speaking";
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);

        // finally, execute the request
        Call<ResponseBody> call = service.upload(description, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

    public interface FileUploadService {
        @Multipart
        @POST("quickhelp/upload")
        Call<ResponseBody> upload(@Part("description") RequestBody description, @Part MultipartBody.Part file);
    }
}
