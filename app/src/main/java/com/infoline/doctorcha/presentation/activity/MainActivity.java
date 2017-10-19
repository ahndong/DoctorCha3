package com.infoline.doctorcha.presentation.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.httphelper.ServiceGenerator;
import com.infoline.doctorcha.core.util.CommonUtil;
import com.infoline.doctorcha.core.util.PermissionUtil;
import com.infoline.doctorcha.gcm.MyInstanceIDListenerService;
import com.infoline.doctorcha.presentation.MainCons;
import com.infoline.doctorcha.presentation.RetrofitInterface;
import com.infoline.doctorcha.presentation.bean.BeanErrResponse;
import com.infoline.doctorcha.presentation.bean.BeanMember;
import com.infoline.doctorcha.presentation.bean.BeanPost;
import com.infoline.doctorcha.presentation.fragment.CarInfoFragment;
import com.infoline.doctorcha.presentation.fragment.MainFavoriteFragment;
import com.infoline.doctorcha.presentation.fragment.PostReadFragment;
import com.infoline.doctorcha.presentation.fragment.SendBirdChatForCounselFragment;
import com.infoline.doctorcha.presentation.fragment.YoutubeVideoListFragment;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.infoline.doctorcha.core.util.CommonUtil.writeLog;
import static com.infoline.doctorcha.presentation.MainApp.beanMember;

/*
1. Standsard Material -->
*/
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.dl)
    DrawerLayout dl;
    @BindView(R.id.nv)
    NavigationView nv;
    @BindView(R.id.fl_fragment)
    FrameLayout fl_fragment;

    private static final String sai = "C966BA6E-143D-4153-9249-7ADBA37C93FC"; //senderbirdr app id
    private boolean exitApp = false;

    //SendBirdChatForCounselFragment fragment;
    private Fragment fragment;

    public MainActivity() {
        //Log.d("fverge", "d2d12");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //1. sr 등은 mobile client에서 변경되는 것이 아니고 관리자에 의해 외부에서 변경되므로
        //   app 실행시마다 가져올 수 있도록 작성해야만 한다.

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //sendbird push notification을 click할 경우
        final Intent intent = getIntent();
        final String channelUrl = intent.getStringExtra("channelUrl");

        if(channelUrl != null) {
            final SharedPreferences.Editor editor = getSharedPreferences(SendBirdChatForCounselFragment.class.getSimpleName(), MODE_PRIVATE).edit();
            editor.putString("channelUrl", channelUrl).apply();
        }

        //1. init시 network는 사용하지 않는다
        SendBird.init(sai, this);

        setSupportActionBar(tb);

        final ActionBarDrawerToggle dt = new ActionBarDrawerToggle(this, dl, tb, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dl.addDrawerListener(dt);
        //dl.setScrimColor(Color.TRANSPARENT);
        dt.syncState();

        nv.setNavigationItemSelectedListener(this);

        //----------------------------------------------------

        //1. 네트워크 체크
        if (!CommonUtil.checkNetworkState(this)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("네트워크를 사용할 수 없습니다.\n확인 후 앱을 다시 실행해 주세요").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        finish();
                    }
                }
            }).show();

            return;
        }

        //2. 로그인 쿠키가 존재하는지 확인
        final SharedPreferences sp = getSharedPreferences(LoginActivity.class.getSimpleName(), MODE_PRIVATE);

        final int id = sp.getInt("id", 0);
        if(id == 0) {
            loadLoginActivity();
        }
        else {
            getAuthInfo(id);
        }
    }

    private void loadLoginActivity() {
        //1. 회원미가입 또는 로그아웃된 상태
        startActivityForResult(new Intent(this, LoginActivity.class), 999);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SendBird.disconnect(null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtil.MY_PERMISSION_REQUEST:
                for(int result : grantResults) {
                    if(result != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "요청된 권한이 거부되어 작업을 중단합니다.", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 999) {
            if (resultCode == RESULT_OK) {
                connectSendbird();
            } else {
                finish();
            }
        } else if (requestCode == 666) {
            //구글서비스 업데이트 다이알로그
            if (resultCode != RESULT_OK) {
                xxx();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (dl.isDrawerOpen(GravityCompat.START)) {
            dl.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();

            if (exitApp) {
                //moveTaskToBack(true); -- Destroy 되지 않으므로  endBird.disconnect(null)를 수행할 수 없다
                //super.onBackPressed();
                finish();
                //System.exit(0);
                return;

            }

            this.exitApp = true;
            Toast.makeText(this, "백버튼을 한번 더 누르시면 종료됩니다", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exitApp = false;
                }
            }, 2000);
        }
        //finish();
        //overridePendingTransition(R.anim.activity_zoomin, R.anim.activity_zoomout);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //1. return true; --> 모든 것이 종결된다. 즉 하위 Fragment의 onOptionsItemSelected() 호출없이 끝난다
        //2. return false; --> setHasOptionsMenu(true)이 설정된 하위 Fragment의 onOptionsItemSelected(MenuItem item)를 호출한다
        //3. return super.onOptionsItemSelected(item) --> 2와 동일하다

        final int itemId = item.getItemId();

        if(itemId == R.id.action_channellist) {
            return false;
        } else {
            //아래 두개의 메뉴도 하위 Fragemt에서 inflate된 것이지만 해당 action에 대한 코드가 여기에 이미 있기 때문에 여기서 통합 처리함
            onNavigationItemSelected(nv.getMenu().getItem(item.getItemId() == R.id.action_counsell ? 0 : 1).setChecked(true));
            return true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        dl.closeDrawer(GravityCompat.START, true);
        final int itemId = item.getItemId();
        final String title;

        if(item.isCheckable()) {
            final int mainId = item.getItemId();
            final FragmentManager fm = getFragmentManager();
            final FragmentTransaction ft = fm.beginTransaction();

            if(mainId == R.id.nav_main1) {
                title = "";
                fragment = SendBirdChatForCounselFragment.newInstance();

            } else {
                title = "";
                fragment = MainFavoriteFragment.newInstance();
            }

            ft.replace(R.id.fl_fragment, fragment);
            ft.commit();
        } else {
            if(itemId == R.id.nav_logout) {
                final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == DialogInterface.BUTTON_POSITIVE) {
                            //disconnectSendbird();
                            final FragmentManager fm = getFragmentManager();
                            final FragmentTransaction ft = fm.beginTransaction();
                            ft.remove(fragment).commit();

                            SendBird.unregisterPushTokenAllForCurrentUser(new SendBird.UnregisterPushTokenHandler() {
                                @Override
                                public void onUnregistered(SendBirdException e) {
                                    if (e != null) {
                                        e.printStackTrace();
                                        // Don't return because we still need to disconnect.
                                    } else {

                                    }

                                    SendBird.disconnect(new SendBird.DisconnectHandler() {
                                        @Override
                                        public void onDisconnected() {
                                            //PreferenceUtils.setConnected(MainActivity.this, false);
                                            LoginActivity.logOut(MainActivity.this);
                                            loadLoginActivity();
                                        }
                                    });
                                }
                            });
                        }
                    }
                };

                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("지금 로그아웃 하시겠습니까?").setPositiveButton("예", dialogClickListener).setNegativeButton("아니오", dialogClickListener).show();
            } else {
                switch (itemId) {
                    case R.id.nav_myblog:
                        //내 블로그 홈으로 가기
                        final Intent intent1 = new Intent(this, ShopActivity.class);
                        final MainCons.EnumActivityAnimType enumActivityAnimType;

                        enumActivityAnimType = MainCons.EnumActivityAnimType.LEFTINRIGHTOUT;

                        intent1.putExtra(MainCons.EnumActivityAnimType.class.getSimpleName(), enumActivityAnimType);
                        intent1.putExtra("owner_id", beanMember.id);
                        //intent.putExtra(BeanMemberAndShop.class.getSimpleName(), null);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);

                        break;
                    case R.id.nav_carinfo:
                        final Intent intent2 = new Intent(this, FragmentContainerActivity.class);
                        final String fragmentName;
                        fragmentName = CarInfoFragment.class.getSimpleName();
                        intent2.putExtra(MainCons.EnumExtraName.NAME1.name(), fragmentName);
                        intent2.putExtra(MainCons.EnumFragmentOpenType.class.getSimpleName(), MainCons.EnumFragmentOpenType.READ_WRITE);
                        intent2.putExtra(MainCons.EnumExtraName.ID.name(), beanMember.id);
                        startActivity(intent2);
                        overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);

                        break;
                    case R.id.nav_youtube:
                    default:
                        final Intent intent3 = new Intent(this, FragmentContainerActivity.class);
                        intent3.putExtra(MainCons.EnumExtraName.NAME1.name(), YoutubeVideoListFragment.class.getSimpleName());
                        startActivity(intent3);
                        overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
                }


            }
        }

        //dl.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_zoomin, R.anim.activity_zoomout);
    }

    //----------------------------------------------------------------------------------------------

    private void getAuthInfo(final int id) {
        final RetrofitInterface.MemberService service = ServiceGenerator.createService(RetrofitInterface.MemberService.class);
        final Call<List<BeanMember>> call = service.select(id);

        call.enqueue(new Callback<List<BeanMember>>() {
            @Override
            public void onResponse(Call<List<BeanMember>> call, Response<List<BeanMember>> response) {
                String msg = null;
                if(response.isSuccessful() ) {
                    if(response.body().size() == 1) {
                        beanMember = response.body().get(0);
                        connectSendbird();
                        MyInstanceIDListenerService.subscribeSrTopic(Integer.parseInt(beanMember.sr));
                        return;
                    } else {
                        //1. client에는 정상적인 로그인 쿠키가 존재한다
                        //1. 관리자에 의해 서버 DB의 member row가 강제 삭제되었을 경우
                        //2. 기타 사유로 회원정보 유실
                        loadLoginActivity();
                        return;
                    }
                }
                else {
                    final BeanErrResponse beanErrResponse = new BeanErrResponse(response.errorBody().source().toString()); //{ec:%s,em:'%s',sv:'%s'}

                    //1. 잘못된 nn 또는 upw 이외의 원인으로 인한 오류 메세지
                    //2. 404 같은 경우에는 beanErrResponse 자체가 null이다
                    msg = beanErrResponse.em == null ? String.format("[%s]%s", response.code(), response.message()) : beanErrResponse.em;
                }

                if(msg != null) {
                    Toast.makeText(MainActivity.this,	msg, Toast.LENGTH_LONG).show();

                    //1. 정성적인 member 정보를 획득하지 모솬 경우이므로 이유여하 막론하고 LoginActivity로 redirect한다
                    loadLoginActivity();
                }
            }

            @Override
            public void onFailure(Call<List<BeanMember>> call, Throwable t) {
                Toast.makeText(MainActivity.this,	ServiceGenerator.getExceptionMsgByCause(t), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void connectSendbird() {
        //1. 성공적으로 connect된 network 꺼면 disconnect 상태가 되었다가 다시 켜면
        //   약간의 시간이 경과 후 자동으로 connect상태로 전환된다.
        SendBird.connect(String.valueOf(beanMember.id), new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                if (e != null) {
                    Toast.makeText(MainActivity.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                //TODO 정리할 것
                //1. ShopActivity에서 itn읋 변경할 경우 실시간으로 반영하는 부분
                //2. 무조건 아래를 실행해야만 하는가 부분
                //필요한가? 회원정보 변경시 닉네임 수정할 경우만 수행할 것 - sr변경건 때문에 일단 두도록 한다
                final int sr = Integer.valueOf(beanMember.sr);
                String itn = beanMember.itn;

                //최초가입시 itn = null : 1.4 즉시 재출시
                if(TextUtils.isEmpty(itn)) {
                    itn = sr == 0 ? "itn_m.jpg" : (sr == 1 || sr == 9 ? "itn_c.jpg" : "inn_s.jpg");
                }

                //usertag Version 1
                final String userTag = String.format("{sr:'%s',itn:'%s'}", sr, itn);

                SendBird.updateCurrentUserInfo(beanMember.nn, userTag, new SendBird.UserInfoUpdateHandler() {
                    @Override
                    public void onUpdated(SendBirdException e) {
                        if (e != null) {
                            Toast.makeText(MainActivity.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                /////////////////////////Toast.makeText(MainActivity.this, "상담서버에 성공적으로 접속되었습니다.", Toast.LENGTH_SHORT).show();

                final MenuItem mi = nv.getMenu().getItem(0).setChecked(true);
                onNavigationItemSelected(mi);

                //fc을 위한 임시조치 시작
                //1. 상담사일 경우 상담창 선택 화면이 상위로 올라오고 빠꾸하면 PostReadFragment가 보인다 - 별 문제 없다
                //2. 이전 상담창이 자동으로 열릴 경우에는 바로 PostReadFragment가 보인다
                final Intent data = getIntent();
                final BeanPost beanPost = (BeanPost)data.getSerializableExtra("beanPost");

                if(beanPost != null) {
                    final Intent intent = new Intent(MainActivity.this, FragmentContainerActivity.class);
                    intent.putExtra(MainCons.EnumExtraName.NAME1.name(), PostReadFragment.class.getSimpleName());
                    intent.putExtra("beanPost", beanPost);
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
                }
                //fc을 위한 임시조치 끝

                //1. 회원가입이 성공하였더라도 gcm token을 DB에 저장하는데 실패하였을 수도 있다.
                //2.  gcm token을 DB에 저장하는데 성공하였더라도 gcm token을 SendBird에 등록실패하였을 수도 있다.
                getGcmToken();
            }
        });
    }

    public void getGcmToken() {
        final GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode == ConnectionResult.SUCCESS) {
            final String token = FirebaseInstanceId.getInstance().getToken();
            MyInstanceIDListenerService.sendTokenToDoctorChaServer(this, token);
            MyInstanceIDListenerService.sendTokenToSendbirdServer(this, token);
        }
        else {
            //GoogleApiAvailability.makeGooglePlayServicesAvailable()을 호출하여 호출하여 사용자가 Play 스토어에서 Google Play 서비스를 다운로드하도록 허용할 수 있습니다. ??
            if (apiAvailability.isUserResolvableError(resultCode)) {
                //apiAvailability.getErrorDialog(this, resultCode, 666).show();
                apiAvailability.getErrorDialog(this, resultCode, 666, new DialogInterface.OnCancelListener() {
                    //1. //TODO : resultCode가 update 말고 머가 있을까? 낸중에 공부할 것
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        xxx();
                    }
                }).show();
                //1. update 또는 install dialo가 노온다.
                //2. back key를 탭하면 onActivityResult()에 안가고 기냥 끝나 버린다
                //3. 업트이트 또는 설치 버튼을 click한 후 playstore 화면으로 간 상태에서는 back key를 탭하면 onActivityResult()에 간다
                //4. 어..근데 설치 완료 후에도 RESULT_CANCELED로 진입한다. - 다시 확인해보자
            }
            else {
                Toast.makeText(MainActivity.this, "구글 플레이 서비스가 지원되지 않는 기기입니다\n푸쉬, 지도 등 일부 기능이 제한될 수 있습니다", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void xxx() {
        Toast.makeText(MainActivity.this, "구글 플레이 서비스가 업데이트 되지 않았습니다\n푸쉬, 지도 등 일부 기능이 제한될 수 있습니다", Toast.LENGTH_SHORT).show();
    }
}
