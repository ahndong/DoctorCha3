package com.infoline.doctorcha.gcm;

import android.content.Context;
import android.widget.Toast;

import com.google.common.util.concurrent.ExecutionError;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.infoline.doctorcha.core.httphelper.ServiceGenerator;
import com.infoline.doctorcha.core.util.CommonUtil;
import com.infoline.doctorcha.presentation.MainApp;
import com.infoline.doctorcha.presentation.RetrofitInterface;
import com.infoline.doctorcha.presentation.bean.BeanErrResponse;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyInstanceIDListenerService extends FirebaseInstanceIdService {
    private static final String TAG = "MyInstanceIDLS";

    @Override
    public void onTokenRefresh() {
        final String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //sendTokenToSendbirdServer(MyInstanceIDListenerService.this, refreshedToken);
        //sendTokenToDoctorChaServer(MyInstanceIDListenerService.this, refreshedToken);
        subscribeBasicTopic();
    }

    public static void sendTokenToSendbirdServer(final Context ctx, String token) {
        if(SendBird.getConnectionState() != SendBird.ConnectionState.OPEN) return;

        SendBird.registerPushTokenForCurrentUser(token, new SendBird.RegisterPushTokenWithStatusHandler() {
            @Override
            public void onRegistered(SendBird.PushTokenRegistrationStatus pushTokenRegistrationStatus, SendBirdException e) {
                if (e != null) {
                    Toast.makeText(ctx, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pushTokenRegistrationStatus == SendBird.PushTokenRegistrationStatus.PENDING) {
                    //1. connect가 안되어 있을 경우
                    //2. 근데 여기는 정상적으로 들어 오는데 앱이 죽는다. -- bug인듯

                    //앱 실행때마다 update되므로 메세지 필요없다
                }
            }
        });
    }

    public static void sendTokenToDoctorChaServer(final Context ctx, String token) {
        final RetrofitInterface.MemberService service = ServiceGenerator.createService(RetrofitInterface.MemberService.class);
        final Call<Void> call = service.update_gt( MainApp.beanMember.id, token);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(!response.isSuccessful()) {
                    final BeanErrResponse beanErrResponse = new BeanErrResponse(response.errorBody().source().toString()); //{ec:%s,em:'%s',sv:'%s'}

                    //1. 잘못된 nn 또는 upw 이외의 원인으로 인한 오류 메세지
                    //2. 404 같은 경우에는 beanErrResponse 자체가 null이다
                    CommonUtil.writeLog(beanErrResponse.em == null ? String.format("[%s]%s", response.code(), response.message()) : beanErrResponse.em);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                CommonUtil.writeLog(ServiceGenerator.getExceptionMsgByCause(t));
            }
        });
    }

    private void subscribeBasicTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("global");
    }

    public static void subscribeSrTopic(final int sr) {
        final FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();

        if(sr == 0) {
            firebaseMessaging.subscribeToTopic("person");
            firebaseMessaging.unsubscribeFromTopic("shop");
            firebaseMessaging.unsubscribeFromTopic("counsellor");
        } else if(sr == 1 || sr == 9) {
            firebaseMessaging.subscribeToTopic("counsellor");
            firebaseMessaging.unsubscribeFromTopic("person");
            firebaseMessaging.unsubscribeFromTopic("shop");
        } else {
            firebaseMessaging.subscribeToTopic("shop");
            firebaseMessaging.unsubscribeFromTopic("person");
            firebaseMessaging.unsubscribeFromTopic("counsellor");
        }
    }
}
