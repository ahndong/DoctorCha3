package com.infoline.doctorcha.presentation.activity;

import android.annotation.TargetApi; ////annotation
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.infoline.doctorcha.R;

import static com.infoline.doctorcha.core.util.CommonUtil.writeLog;
import static com.infoline.doctorcha.presentation.MainApp.beanMember;
import static com.infoline.doctorcha.presentation.MainCons.EnumPreferAuth;
import static com.infoline.doctorcha.presentation.MainCons.EnumResponseCode;
import static com.infoline.doctorcha.presentation.MainCons.EnumGeneralMessage;
import static com.infoline.doctorcha.presentation.util.AppUtil.getBeanResponseFromJson;

//1.모두 사용하므로 static으로 선언할 필요없슴
import com.infoline.doctorcha.core.httphelper.ServiceGenerator;

import static android.Manifest.permission.READ_CONTACTS;
import static com.infoline.doctorcha.presentation.MainCons.CN_SQLEXCEPTION_DUPLICATE;

import com.infoline.doctorcha.core.util.PermissionUtil;
import com.infoline.doctorcha.gcm.MyInstanceIDListenerService;
import com.infoline.doctorcha.presentation.MainApp;
import com.infoline.doctorcha.presentation.MainCons;
import com.infoline.doctorcha.presentation.RetrofitInterface;
import com.infoline.doctorcha.presentation.bean.BeanErrResponse;
import com.infoline.doctorcha.presentation.bean.BeanMember;
import com.infoline.doctorcha.presentation.bean.BeanPreferAuth;
import com.infoline.doctorcha.presentation.bean.BeanResponse;
import com.infoline.doctorcha.presentation.fragment.SendBirdChatForCounselFragment;

import butterknife.BindView; //annotation
import butterknife.ButterKnife;

import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.IOException;
import java.util.List;

/*--------------------------------------------------------------------------------------------------
//1. 핸드폰 중고를 사고 팔 경우
        1) 폰을 새로 구입(기존 전화번호 유지, 새 전화번호)
                1) 새 폰 주인이 닥터차를 사용했을 경우
                    a)
                2) 안 했을 경우
                    a)
//-------------------------------------------------------------------------------------------------*/

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.til_hn)
    TextInputLayout til_hn;
    @BindView(R.id.til_ea) TextInputLayout til_ea;
    @BindView(R.id.til_nm) TextInputLayout til_nm;
    @BindView(R.id.til_nn) TextInputLayout til_nn;
    @BindView(R.id.til_upw) TextInputLayout til_upw;
    @BindView(R.id.et_hn) EditText et_hn;
    @BindView(R.id.et_ea) EditText et_ea;
    @BindView(R.id.et_nm) EditText et_nm;
    @BindView(R.id.et_nn) EditText et_nn;
    @BindView(R.id.et_upw) EditText et_upw;
    @BindView(R.id.bt_confirm) Button bt_confirm;
    @BindView(R.id.rb_login) RadioButton rb_login;
    @BindView(R.id.rb_signin) RadioButton rb_signin;

    ProgressDialog pd;

    private final String CN_SIGNIN_SUCCESS_MSG = "감사합니다. 회원가입이 완료되었습니다";
    private final String CN_LOGIN_SUCCESS_MSG = "님 반갑습니다. 좋은 하루 되세요";

    private final int CN_MODE_LOGIN = 1;
    private final int CN_MODE_SIGNIN = 2;
    private final int CN_MODE_SEARCH_NN = 3;
    private final int CN_MODE_SEARCH_UPW = 4;

    private int mode = CN_MODE_LOGIN;

    @OnClick({R.id.rb_login, R.id.rb_signin, R.id.rb_search_nn, R.id.rb_search_upw})
    protected void radioButtonClickListener(RadioButton v){
        int eaVisible = View.GONE;
        int hnVisible = View.GONE;
        int nnVisible = View.GONE;
        int nmVisible = View.GONE;
        int upwVisible = View.GONE;

        View focusView;

        switch (v.getId()) {
            case R.id.rb_signin:
                String hn= "";

                //android.permission.READ_PHONE_STATE
                if(PermissionUtil.requestReadPhoneState(this)) {
                    hn = ((TelephonyManager)getSystemService(TELEPHONY_SERVICE)).getLine1Number();

                    if(TextUtils.isEmpty(hn)) {
                        //1. 전화 기능이 없는 tablet
                        //2. 중복가입을 방지를 위하여 필요하다.
                        hn = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                    }
                } else {
                    rb_login.performClick();
                    return;
                }

                mode = CN_MODE_SIGNIN;
                eaVisible = View.VISIBLE;
                hnVisible = View.VISIBLE;
                nnVisible = View.VISIBLE;
                nmVisible = View.VISIBLE;
                upwVisible = View.VISIBLE;

                et_hn.setText(hn);
                focusView = et_ea;
                break;
            case R.id.rb_search_nn:
                mode = CN_MODE_SEARCH_NN;
                eaVisible = View.VISIBLE;
                hnVisible = View.VISIBLE;
                focusView = et_ea;
                break;
            case R.id.rb_search_upw:
                mode = CN_MODE_SEARCH_UPW;
                eaVisible = View.VISIBLE;
                hnVisible = View.VISIBLE;
                nnVisible = View.VISIBLE;
                focusView = et_ea;
                break;

            case R.id.rb_login:
            default:
                mode = CN_MODE_LOGIN;
                nnVisible = View.VISIBLE;
                upwVisible = View.VISIBLE;
                focusView = et_nn;
        }

        til_ea.setVisibility(eaVisible);
        til_hn.setVisibility(hnVisible);
        til_nm.setVisibility(nmVisible);
        til_nn.setVisibility(nnVisible);
        til_upw.setVisibility(upwVisible);

        focusView.requestFocus();
    }

    @OnClick({R.id.bt_confirm, R.id.bt_exit_app})
    protected void buttonClickListener(Button v){
        if(v.getId() == R.id.bt_confirm) {
            if(mode == CN_MODE_LOGIN || mode == CN_MODE_SIGNIN) {
                submitAfterValidate();
            }
            else {
                Toast.makeText(LoginActivity.this,	"현재 준비중인 기능입니다", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            finish();
        }
    }

    public LoginActivity() {
        //1. 정상적으로 LogOut : id, upw만 삭제하고 nn은 재로그인시 사용키 위하여 살려 놓는다
        //2. GoutCare에서는 나의 다짐을 노출시키기 위하여 app 재실행시 무조건 로그인 과정을 거치도록 한다

        beanMember = beanMember;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //1. 로그인 쿠키가 존재하는지 확인
        final SharedPreferences sp = getSharedPreferences(this.getClass().getSimpleName(), MODE_PRIVATE);

        et_nn.setText(sp.getString("nn", ""));
        et_upw.setText(sp.getString("upw", ""));

        et_upw.setImeActionLabel(getString(R.string.action_login_short), EditorInfo.IME_ACTION_GO);
        et_upw.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == 6 || id == EditorInfo.IME_NULL) {
                    submitAfterValidate();

                    return true;
                }
                return false;
            }
        });

        rb_login.setChecked(true);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void submitAfterValidate() {
        //1. 유효성검사후 서버에 로그인 요청

        boolean isValidate = true;

        et_nn.setError(null);
        et_upw.setError(null);

        //이메일, 전화번호, 이름, 닉네임, 비밀번호 순 - validation 순서 바꾸면 안됨
        if(mode == CN_MODE_SIGNIN) {
            et_ea.setError(null);
            et_hn.setError(null);
            et_nm.setError(null);

            isValidate = MainApp.validateInputText(this, et_ea, true, 0);
            if(isValidate) {
                //결국은 개인정보보호 차원에서 전화번호를 수동입력해야 되지 않을까?
                isValidate = MainApp.validateInputText(this, et_hn, true, 10);
                if(isValidate) {
                    isValidate = MainApp.validateInputText(this, et_nm, true, 2);
                }
            }
        }

        if(isValidate) {
            isValidate = MainApp.validateInputText(this, et_nn, true, 2);
            if(isValidate) {
                isValidate = MainApp.validateInputText(this, et_upw, true, 4);
            }
        }

        if(isValidate) {
            final String nn = et_nn.getText().toString().trim();
            final String upw = et_upw.getText().toString().trim();

            if(mode == CN_MODE_LOGIN) {
                //1. hn전달 이유는 전화번호가 변경되었을 경우에 대처하기 위해서
                //2. 현재는 준비만 해둔 상태이다
                login(nn, upw);
            } else {
                if(beanMember == null) beanMember = new BeanMember();

                beanMember.ea = et_ea.getText().toString().trim();
                beanMember.hn = et_hn.getText().toString().trim();
                beanMember.nm = et_nm.getText().toString().trim();
                beanMember.nn = nn;
                beanMember.upw = upw;

                signIn();
            }
        }
    }

    private void signIn() {
        pd = ProgressDialog.show(LoginActivity.this, "회원등록 중", "잠시만 기다려 주세요", true);

        final RetrofitInterface.MemberService service = ServiceGenerator.createService(RetrofitInterface.MemberService.class);
        final Call<List<BeanMember>> call = service.signin(beanMember);

        call.enqueue(new Callback<List<BeanMember>>() {
            @Override
            public void onResponse(Call<List<BeanMember>> call, Response<List<BeanMember>> response) {
                String msg = null;
                boolean success = false;

                if(response.isSuccessful()) {
                    //1. ok이면 1행의 cursor가 반드시 return된다
                    final BeanMember beanMemberReturn = response.body().get(0);

                    beanMember.id = beanMemberReturn.id;
                    beanMember.sr = beanMemberReturn.sr;

                    MyInstanceIDListenerService.subscribeSrTopic(Integer.parseInt(beanMember.sr));
                    applyToSharedPrefernces();

                    success = true;
                    msg = CN_SIGNIN_SUCCESS_MSG;
                }
                else {
                    try {
                        final BeanErrResponse beanErrResponse = new BeanErrResponse(response.errorBody().string()); //{ec:%s,em:'%s',sv:'%s'}

                        if(beanErrResponse.em == null) {
                            //1. response.raw().message()
                            //   404 : Not Found , 405 : Method not Aloowed,
                            msg = String.format("[%s]%s", response.code(), response.raw().toString());
                        } else {
                            if(beanErrResponse.ec == CN_SQLEXCEPTION_DUPLICATE) {
                                final String scalar = beanErrResponse.sv; //nn, ea 등등
                                final EditText focusView;

                                if(scalar.equals("ea")) {
                                    focusView = et_ea;
                                }
                                else {
                                    //nn 또는 hn
                                    focusView = et_nn;
                                }

                                //1. id, upw중 어느 것이 잘못되었는지 알려주고 싶은 경우에는 storedprocedure에서 별도의 추가 작업이 필요하다
                                focusView.setError(getString(R.string.error_duplicate_memberinfo));
                                focusView.requestFocus();
                            }
                            else {
                                msg = beanErrResponse.em;
                            }
                        }
                    } catch (Exception e) {
                        msg = e.getMessage();
                    }
                }

                pd.dismiss();

                if(msg != null) {
                    Toast.makeText(LoginActivity.this,	msg, Toast.LENGTH_LONG).show();
                }

                if(success) {
                    setResult(RESULT_OK);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<BeanMember>> call, Throwable t) {
                pd.dismiss();

                final String msg = ServiceGenerator.getExceptionMsgByCause(t);
                Toast.makeText(LoginActivity.this,	msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void login(final String nn, final String upw) {
        //1. 로그인 수행시간이 너무 짧아 의미가 없네
        pd = ProgressDialog.show(LoginActivity.this, "로그인 중", "잠시만 기다려 주세요", true);

        final RetrofitInterface.MemberService service = ServiceGenerator.createService(RetrofitInterface.MemberService.class);
        final Call<List<BeanMember>> call = service.login(nn, upw);

        call.enqueue(new Callback<List<BeanMember>>() {
            @Override
            public void onResponse(Call<List<BeanMember>> call, Response<List<BeanMember>> response) {
                String msg = null;
                boolean success = false;

                if(response.isSuccessful() ) {
                    if(response.body().size() == 1) {
                        beanMember = response.body().get(0);
                        beanMember.nn = nn;
                        beanMember.upw = upw;

                        MyInstanceIDListenerService.subscribeSrTopic(Integer.parseInt(beanMember.sr));
                        applyToSharedPrefernces();

                        success = true;
                        msg = nn + CN_LOGIN_SUCCESS_MSG;
                    } else {
                        //잘못된 nn 또는 upw일 경우 ok로 응답하지만 판단가능한 cursor가 없다
                        et_nn.setError(getString(R.string.error_incorrect_login));
                        et_nn.requestFocus();
                    }
                }
                else {
                    try {
                        final BeanErrResponse beanErrResponse = new BeanErrResponse(response.errorBody().string()); //{ec:%s,em:'%s',sv:'%s'}

                        if(beanErrResponse.em == null) {
                            msg = String.format("[%s]%s", response.code(), response.raw().toString());
                        } else {
                            msg = beanErrResponse.em;
                        }
                    } catch (Exception e) {
                        msg = e.getMessage();
                    }
                }

                pd.dismiss();

                if(msg != null) {
                    Toast.makeText(LoginActivity.this,	msg, Toast.LENGTH_LONG).show();
                }

                if(success) {
                    setResult(RESULT_OK);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<BeanMember>> call, Throwable t) {
                pd.dismiss();

                final String msg = ServiceGenerator.getExceptionMsgByCause(t);
                Toast.makeText(LoginActivity.this,	msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void applyToSharedPrefernces() {
        final SharedPreferences.Editor editor = getSharedPreferences(this.getClass().getSimpleName(), MODE_PRIVATE).edit();

        editor.putInt("id", beanMember.id);
        editor.putString("nn", beanMember.nn);
        editor.putString("upw", beanMember.upw);
        editor.apply();
    }

    public static void logOut(Context ctx) {
        ctx.getSharedPreferences(LoginActivity.class.getSimpleName(), MODE_PRIVATE).edit().putInt("id", 0).apply();
        ctx.getSharedPreferences(SendBirdChatForCounselFragment.class.getSimpleName(), MODE_PRIVATE).edit().remove("channelUrl").apply();
    }
}