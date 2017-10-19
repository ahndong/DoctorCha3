package com.infoline.doctorcha.presentation.activity;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.httphelper.ServiceGenerator;
import com.infoline.doctorcha.core.transformer.DepthPageTransformer;
import com.infoline.doctorcha.core.util.AnimUtil;
import com.infoline.doctorcha.core.util.CommonUtil;
import com.infoline.doctorcha.core.util.MediaUtil;
import com.infoline.doctorcha.presentation.MainApp;
import com.infoline.doctorcha.presentation.MainCons;
import com.infoline.doctorcha.presentation.RetrofitInterface;
import com.infoline.doctorcha.presentation.bean.BeanErrResponse;
import com.infoline.doctorcha.presentation.bean.BeanMemberAndShop;
import com.infoline.doctorcha.presentation.bean.BeanPostSearch;
import com.infoline.doctorcha.presentation.fragment.PostListFragment;
import com.infoline.doctorcha.presentation.fragment.ShopInfoFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.infoline.doctorcha.core.CoreCons.EnumDateFormat.UNIQUE_FILENAME;
import static com.infoline.doctorcha.core.util.MediaUtil.getMediaType;
import static com.infoline.doctorcha.core.util.MediaUtil.getMimeType;
import static com.infoline.doctorcha.presentation.MainApp.beanMember;

public class ShopActivity extends AppCompatActivity {
    @BindView(R.id.cl)
    CoordinatorLayout cl;
    @BindView(R.id.abl)
    AppBarLayout abl;
    @BindView(R.id.ctl)
    CollapsingToolbarLayout ctl;
    @BindView(R.id.iv_ifn)
    ImageView iv_ifn;
    @BindView(R.id.iv_itn)
    ImageView iv_itn;
    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.stl)
    SmartTabLayout stl;
    @BindView(R.id.vp)
    ViewPager vp;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private MainCons.EnumActivityAnimType enumActivityAnimType;
    private BeanMemberAndShop beanMemberAndShop;
    private int owner_id = -1;
    private int owner_sr = -1;

    private Fragment curFragment;
    private final MediaUtil mediaUtil;

    private final int CN_REQUEST_GALLARY_IFN = 1001;
    private final int CN_REQUEST_GALLARY_ITN = 1002;
    private final int CN_REQUEST_GALLARY_CROP = 1003;

    final ImageLoader imageLoader = ImageLoader.getInstance();

    public ShopActivity() {
        mediaUtil = new MediaUtil();
    }

    @OnClick({R.id.fab, R.id.iv_ifn, R.id.iv_itn})
    protected void onClick1(final View v) {
        final @IdRes int viewId = v.getId();

        if(viewId == R.id.fab) {
            //새글
            if(curFragment instanceof ShopInfoFragment) {
                ((ShopInfoFragment)curFragment).xxx();
            } else {
                ((PostListFragment)curFragment).xxx();
            }
        } else {
            if(beanMember.id == 1 || beanMember.id == 2 || beanMember.id == owner_id) {
                final Intent intent = mediaUtil.getGallaryIntentForPhotoOnly(ShopActivity.this, false);

                if(intent != null) {
                    startActivityForResult(intent, viewId == R.id.iv_ifn ? CN_REQUEST_GALLARY_IFN : CN_REQUEST_GALLARY_ITN);
                }
            }
        }
    }

    @OnLongClick({R.id.iv_ifn, R.id.iv_itn})
    protected boolean onLongClick(final View v) {
        if(beanMember.sr.equals("9") || beanMember.id == 2) {
            final String[] items = {"일반 회원으로 권한 변경", "상담사로 권한 변경", "업체 회원으로 권한 변경"};
            final AlertDialog ad;
            final AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this);

            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, final int menuPos) {
                    final RetrofitInterface.MemberService service = ServiceGenerator.createService(RetrofitInterface.MemberService.class);
                    final Call<Void> call = service.update_sr(owner_id, String.valueOf(menuPos));

                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if(response.isSuccessful()) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this);
                                builder.setMessage(beanMemberAndShop.nn + "님의 권한이 " + (menuPos == 0 ? "일반 회원으로" : (menuPos == 1 ? "상담사로" : "기업 회원으로")) + " 변경되었습니다\n새로 로그인 하시도록 메세지를 보내 주세요").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();
                            } else {
                                ServiceGenerator.displayErrMessageOnResponse(ShopActivity.this, response);
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            ServiceGenerator.displayErrMessageOnFailure(ShopActivity.this, t);
                        }
                    });


                }
            });

            ad = builder.create();
            ad.show();
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode != RESULT_OK) return;

        if(requestCode == CN_REQUEST_GALLARY_CROP) {
            //1. Loolipop Google+ : hasExtra = false, intent.getClipData() = null, intent.getData() = null

            final Bundle o = intent.getExtras();
            final ClipData cd = intent.getClipData();
            final Uri uri = intent.getData();

            if(uri != null) {
                try {
                    //final File destFile = new File(uri.getPath());
                    final File destFile = new File(MediaUtil.getRealPathFromUri(this,uri));

                    final String fileName = owner_id + "_m_" + CommonUtil.getFormattedDate(UNIQUE_FILENAME) + ".jpg";

                    final ProgressDialog pd = ProgressDialog.show(this, "", "사진을 서버로 전송하고 있습니다", true);

                    final MultipartBody.Builder builder = new MultipartBody.Builder();
                    builder.setType(MultipartBody.FORM);

                    final RequestBody requestBody = RequestBody.create(getMediaType(fileName), destFile);
                    builder.addFormDataPart("file", fileName, requestBody);
                    builder.addFormDataPart("pathId", MainCons.EnumContentPath.MEMBER_I.getId()+"");

                    final RequestBody finalRequestBody = builder.build();

                    final RetrofitInterface.UploadService service = ServiceGenerator.createService(RetrofitInterface.UploadService.class);
                    final Call<Void> call = service.file(finalRequestBody);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            pd.dismiss();
                            final String msg;

                            if(response.isSuccessful()) {
                                msg = "사진 전송이 완료되었습니다";

                                imageLoader.displayImage(MainCons.EnumContentPath.MEMBER_I.getPath().concat(fileName), iv_itn, MainApp.optionsForCircleThumb);

                            } else {
                                msg = "사진 전송이 실패했습니다\n" + response.errorBody().source().toString();
                            }

                            Toast.makeText(ShopActivity.this, msg, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            pd.dismiss();
                            Toast.makeText(ShopActivity.this, ServiceGenerator.getExceptionMsgByCause(t), Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {

                }
            }
        } else {
            final ClipData cd = intent.getClipData();

            final Uri uri;
            if (cd != null) {
                //google photo intent --> 다른 Intent는 무엇이 있는지 확인 못함
                uri = cd.getItemAt(0).getUri();

            } else {
                uri = intent.getData();
            }

            final String realUrl = MediaUtil.getRealPathFromUri(this, uri);

            if(TextUtils.isEmpty(realUrl)) {
                return;
            }

            if(requestCode == CN_REQUEST_GALLARY_ITN) {
                cropImage(uri);
            } else {
                final ProgressDialog pd = ProgressDialog.show(this, "", "사진을 서버로 전송하고 있습니다", true);

                final MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);

                final File file = new File(realUrl);
                final String fileName = owner_id + "_ifn_" + CommonUtil.getFormattedDate(UNIQUE_FILENAME) + "." + CommonUtil.getExtName(realUrl);

                final RequestBody requestBody = RequestBody.create(getMediaType(fileName), file);
                builder.addFormDataPart("file", fileName, requestBody);
                builder.addFormDataPart("pathId", MainCons.EnumContentPath.MEMBER_I.getId()+"");

                final RequestBody finalRequestBody = builder.build();

                final RetrofitInterface.UploadService service = ServiceGenerator.createService(RetrofitInterface.UploadService.class);
                final Call<Void> call = service.file(finalRequestBody);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        pd.dismiss();
                        final String msg;

                        if(response.isSuccessful()) {
                            msg = "사진 전송이 완료되었습니다";

                            final String fileName2 = CommonUtil.getFileName(fileName);
                            final String thumbFileNameWithExt1 = fileName2.concat("_720x360.jpg");
                            final String thumbFileNameWithExt2 = fileName2.concat("_200x200.jpg");

                            imageLoader.displayImage(MainCons.EnumContentPath.MEMBER_I.getPath().concat(thumbFileNameWithExt1), iv_ifn);
                            //imageLoader.displayImage(MainCons.EnumContentPath.MEMBER_I.getPath().concat(thumbFileNameWithExt2), iv_itn, MainApp.optionsForCircleThumb);

                        } else {
                            msg = "사진 전송이 실패했습니다\n" + response.errorBody().source().toString();
                        }

                        Toast.makeText(ShopActivity.this, msg, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        pd.dismiss();
                        Toast.makeText(ShopActivity.this, ServiceGenerator.getExceptionMsgByCause(t), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    //private void cropImage(final String sourceUrl) {
    private void cropImage(Uri sourceUri) {
        //final File file = new File(sourceUrl);
        final String fileName = owner_id + "_itn_" + CommonUtil.getFormattedDate(UNIQUE_FILENAME); // + ".jpg";

        //final File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DoctorCha");
        //final String realPath = mediaStorageDir.getPath() + File.separator + fileName + "jpg";

        final File storageDir = new File(Environment.getExternalStorageDirectory() + "/DoctorCha/CameraTemp/");

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        //final String realPath = Environment.getExternalStorageDirectory() + "/test2/" + fileName + ".jpg";
        final String realPath = Environment.getExternalStorageDirectory() + "/DoctorCha/CameraTemp/" + fileName + ".jpg";

        try {
            //destFile = File.createTempFile(fileName, "jpg");
            final File destFile = new File(realPath);
            final Uri destUri = FileProvider.getUriForFile(ShopActivity.this, "com.infoline.doctorcha.provider", destFile);

            //grantUriPermission("com.android.camera", uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //grantUriPermission("com.android.camera", destUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            final String sourceRealPath = MediaUtil.getRealPathFromUri(this, sourceUri);
            final File yyy = new File(sourceRealPath);
            final Uri contentUri = MediaUtil.getContentUriFromImageFile(this, yyy);

            //cropIntent.setDataAndType(uri, "image/*");
            cropIntent.setDataAndType(contentUri, "image/*");

            List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(cropIntent, 0);
            for (ResolveInfo resolveInfo : resInfoList) {
                //grantUriPermission(resolveInfo.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                grantUriPermission(resolveInfo.activityInfo.packageName, destUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            //indicate image type and Uri of image

            //final Uri testUri = FileProvider.getUriForFile(ShopActivity.this, "com.infoline.doctorcha.provider", file);

            //cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            //cropIntent.setDataAndType(Uri.fromFile(file), "image/*");
            //cropIntent.setDataAndType(testUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 200);
            cropIntent.putExtra("outputY", 200);
            //retrieve data on return
            //cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destFile));
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, destUri);
            //cropIntent.putExtra("return-data", true);

            final Intent chooserIntent = Intent.createChooser(cropIntent, "Crop 선택");

            //startActivityForResult(cropIntent, CN_REQUEST_GALLARY_CROP);
            startActivityForResult(chooserIntent, CN_REQUEST_GALLARY_CROP);
        } catch (Exception e) {
            Toast.makeText(ShopActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shop);
        ButterKnife.bind(this);

        //overridePendingTransition(0, 0);

        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Intent intent = getIntent();

        enumActivityAnimType = (MainCons.EnumActivityAnimType)intent.getSerializableExtra(MainCons.EnumActivityAnimType.class.getSimpleName());

        if(enumActivityAnimType == MainCons.EnumActivityAnimType.ANIM_SCENE_TRANSITION) {
            final String transitionName = intent.getStringExtra(MainCons.EnumTransitionName.class.getSimpleName());

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iv_ifn.setTransitionName(transitionName);
            } else {
                ViewCompat.setTransitionName(iv_ifn, transitionName);
            }
        }

        fab.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                fab.getViewTreeObserver().removeOnPreDrawListener(this);

                fab.setTranslationY(fab.getHeight() + CommonUtil.getPxFromDip(ShopActivity.this, 16));
                return true;
            }
        });

        beanMemberAndShop = (BeanMemberAndShop)intent.getSerializableExtra(BeanMemberAndShop.class.getSimpleName());

        if(beanMemberAndShop == null) {
            //owner_id는 -1일수가 없다
            owner_id = intent.getIntExtra("owner_id", -1);
            loadShopInfo();
        } else {
            owner_id = beanMemberAndShop.id;
            initActivity();
        }
    }

    private void initActivity() {
        //final String xxx = tb.getTitle().toString();
        //tb.setTitle(beanMemberAndShop.cn); //setSupportActionBar(tb) 보다 선행되어야 동작한다
        //setSupportActionBar(tb);

        getSupportActionBar().setTitle(beanMemberAndShop.cn);

        owner_sr = Integer.parseInt(beanMemberAndShop.sr);
        String ifn = beanMemberAndShop.ifn;

        if(ifn.isEmpty()) {
            ifn = owner_sr == 0 ? "ifn_m.jpg" : (owner_sr == 1 || owner_sr == 9 ? "ifn_c.jpg" : "ifn_s.jpg");
        }
        ifn = CommonUtil.getFileName(ifn).concat("_720x360.jpg");
        imageLoader.displayImage(MainCons.EnumContentPath.MEMBER_I.getPath().concat(ifn), iv_ifn);

        String itn = beanMemberAndShop.itn;

        if(itn.isEmpty()) {
            //itn은 thumbnail 전용이므로 사이즈별이 따로 없다
            itn = owner_sr == 0 ? "itn_m.jpg" : (owner_sr == 1 || owner_sr == 9 ? "itn_c.jpg" : "itn_s.jpg");
        }
        imageLoader.displayImage(MainCons.EnumContentPath.MEMBER_I.getPath().concat(itn), iv_itn, MainApp.optionsForCircleThumb);

        vp.setAdapter(getFpa());
        vp.setPageTransformer(true, new DepthPageTransformer());

        stl.setViewPager(vp);
    }

    private void loadShopInfo() {
        final RetrofitInterface.ShopService service = ServiceGenerator.createService(RetrofitInterface.ShopService.class);
        final Call<List<BeanMemberAndShop>> call = service.select(owner_id);
        call.enqueue(new Callback<List<BeanMemberAndShop>>() {
            @Override
            public void onResponse(Call<List<BeanMemberAndShop>> call, Response<List<BeanMemberAndShop>> response) {
                if(response.isSuccessful()) {
                    if(response.body().size() == 0) {
                        //논리적으로 이럴 경우는 원천 봉쇄되어야 한다.
                        Toast.makeText(ShopActivity.this, "업체정보가 존재하지 않습니다", Toast.LENGTH_LONG).show();
                        return;
                    }

                    beanMemberAndShop = response.body().get(0);
                    initActivity();
                }
                else {
                    Toast.makeText(ShopActivity.this, response.errorBody().source().toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<BeanMemberAndShop>> call, Throwable t) {
                Toast.makeText(ShopActivity.this, ServiceGenerator.getExceptionMsgByCause(t), Toast.LENGTH_LONG).show();
            }
        });
    }

    private FragmentPagerAdapter getFpa() {
        return new FragmentPagerAdapter(getFragmentManager()) {
            @Override
            public Fragment getItem(int pos) {
                final Fragment fragment;

                switch (MainCons.ShopActivityTabMenu.values()[pos]) {
                    case MENU1: //업체정보
                        //beanMemberAndShop != null일 경우 owner_id가 중복되어 넘아가지만 호출되는 ShopInfoFragment측에서 beanMemberAndShop를 가지고 먼저 판별하므로 문제 어뵤다.
                        //ShopInfoFragment측에서 owner_id를 가지고 먼저 판단하지 않도록 주의한다
                        fragment = ShopInfoFragment.newInstance(beanMemberAndShop, owner_id); break;
                    default:
                        final BeanPostSearch beanPostSearch = new BeanPostSearch();
                        beanPostSearch.owner_id = owner_id;

                        switch (MainCons.ShopActivityTabMenu.values()[pos]) {
                            case MENU2: //서비스사례 Post List
                                beanPostSearch.board_id = 3; break;
                            case MENU3: //고객리뷰 Post List
                                beanPostSearch.board_id = 4; break;
                            case MENU4: //이벤트 Post List
                                beanPostSearch.board_id = 5; break;
                            default: //새소식 Post List
                                beanPostSearch.board_id = -1; break;  //전체 게시판
                        }

                        fragment = PostListFragment.newInstance(beanPostSearch);
                }

                return fragment;
            }

            @Override
            public int getCount() {
                return MainCons.ShopActivityTabMenu.values().length;
            }

            @Override
            public CharSequence getPageTitle(int pos) {
                return MainCons.ShopActivityTabMenu.values()[pos].getText();
            }

            @Override
            public void setPrimaryItem(ViewGroup container, int pos, Object object) {
				//if (curFragment != object) {
                    curFragment = (Fragment)object;
				//}

                super.setPrimaryItem(container, pos, object);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_search, menu);

        AnimUtil.playMenuAnim(fab, true, getResources().getInteger(android.R.integer.config_longAnimTime), 500);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            if(curFragment instanceof ShopInfoFragment) {
                return false;
            }

            finish();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        if(curFragment instanceof ShopInfoFragment) {
            if(!((ShopInfoFragment)curFragment).isModified()) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        super.finish();

        if(enumActivityAnimType != MainCons.EnumActivityAnimType.ANIM_SCENE_TRANSITION) {
            overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
        }
    }
}