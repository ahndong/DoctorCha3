package com.infoline.doctorcha.gcm;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.text.TextUtilsCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.util.CommonUtil;
import com.infoline.doctorcha.presentation.MainApp;
import com.infoline.doctorcha.presentation.MainCons;
import com.infoline.doctorcha.presentation.activity.FragmentContainerActivity;
import com.infoline.doctorcha.presentation.activity.MainActivity;
import com.infoline.doctorcha.presentation.bean.BeanPost;
import com.infoline.doctorcha.presentation.fragment.PostReadFragment;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016-06-09.
 */
public class MyGcmListenerService extends FirebaseMessagingService {

    private static final String TAG = FirebaseMessagingService.class.getSimpleName();
    private static int notiId = 0;

    /**
     *
     * from SenderID 값을 받아온다.
     * bundle Set형태로 GCM으로 받은 데이터 payload이다.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //remoteMessage.getFrom()
        //1. SendBird Message from : 1048233077547 -> 닥터차 GCM Sender ID
        //2. fcm message : 1) topics : topic 이름
        //                 2) token  : 닥터차 GCM Sender ID

        /*
        {"data":"{'st':9,'mt':1,'ct':-1,'rv':0,'vu':'','sv':'0','hg':''}",
        "custom_type":"",
        "channel":{"name":"산적님 상담전용","channel_url":"sendbird_group_channel_19179253_453b4888fd5f89b5c2cfd1470879b4625facda78"},
        "created_at":1497893309476,
        "message_id":872024427,
        "push_alert":"닥터차: 어라이",
        "message":"어라이",
        "type":"MESG",
        "unread_message_count":2,
        "audience_type":"only",
        "sender":{"profile_url":"https:\/\/sendbird.com\/main\/img\/profiles\/profile_25_512px.png","name":"닥터차","id":"1"},
        "translations":{},
        "recipient":{"name":"산적","id":"2"},
        "files":[],
        "category":"messaging:offline_notification",
        "channel_type":"group_messaging",
        "mentioned":[],
        "app_id":"C966BA6E-143D-4153-9249-7ADBA37C93FC"}

        {"data":"{'st':9,'mt':2,'ct':-1,'rv':0,'vu':'','sv':'0','hg':''}",
        "custom_type":"",
        "channel":{"name":"산적님 상담전용","channel_url":"sendbird_group_channel_19179253_453b4888fd5f89b5c2cfd1470879b4625facda78"},
        "created_at":1497894196412,
        "message_id":872042103,
        "push_alert":"temp343660162.jpg",
        "message":"temp343660162.jpg",
        "type":"FILE",
        "unread_message_count":1,
        "audience_type":"only",
        "sender":{"profile_url":"https:\/\/sendbird.com\/main\/img\/profiles\/profile_25_512px.png","name":"닥터차","id":"1"},
        "translations":{},
        "recipient":{"name":"산적","id":"2"},
        "files":[{"req_id":"1497891847661","size":201822,
        "edge_ts":1497894196412,
        "custom":"{'st':9,'mt':2,'ct':-1,'rv':0,'vu':'','sv':'0','hg':''}",
        "name":"temp343660162.jpg",
        "type":"image\/jpeg",
        "channel_id":75162814,
        "url":"https:\/\/sendbird-upload.s3.amazonaws.com\/C966BA6E-143D-4153-9249-7ADBA37C93FC\/upload\/n\/9e6b6fcaf86c4ebdaa783c0609ceb0a1.jpg",
        "channel_url":"sendbird_group_channel_19179253_453b4888fd5f89b5c2cfd1470879b4625facda78"}],
        "category":"messaging:offline_notification",
        "channel_type":"group_messaging",
        "mentioned":[],
        "app_id":"C966BA6E-143D-4153-9249-7ADBA37C93FC"}
        */

        //1. 나중에 전달받은 channel이 바로 open될 수 있도록 작업할 것

        final String from = remoteMessage.getFrom();
        final Map map = remoteMessage.getData();



        //if(from.equals("1048233077547")) {
        if(map.get("sendbird") != null) {
            try {
                final JSONObject sendbirdJo = new JSONObject(map.get("sendbird").toString());
                final JSONObject channelJo = (JSONObject)sendbirdJo.get("channel");
                final String channelUrl = channelJo.get("channel_url").toString();
                final JSONObject dataJo = new JSONObject(sendbirdJo.getString("data"));

                int st = dataJo.getInt("st");
                final int mt = dataJo.getInt("mt");

                String tt;
                String message = "";

                if(st == MainCons.CN_CHAT_ST_ADMIN) {
                    tt = "닥터차 AI";
                } else if(st == MainCons.CN_CHAT_ST_COUNSELLOR || st == MainCons.CN_CHAT_ST_TOPCOUNSELLOR){
                    tt = "닥터차";
                } else {
                    final JSONObject senderJo = sendbirdJo.getJSONObject("sender");
                    tt = senderJo.getString("name");
                }

                if(mt == 1) {
                    message = sendbirdJo.getString("message");
                } else {
                    tt = tt.concat("님이");

                    if(mt == 2) {
                        message = "사진을";
                    }else if(mt == 3) {
                        message = "동영상을";
                    } else {
                        message = "메세지를";
                    }

                    message = "채팅 ".concat(message).concat(" 보내셨습니다");
                }

                sendNotificationForSendbird(channelUrl, mt, tt, message);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {


            try {
                final BeanPost beanPost = new BeanPost();
                //beanPost.tt = URLDecoder.decode(dataJo.getString("tt"), "euc-kr");

                beanPost.board_id = Integer.parseInt(map.get("board_id").toString());
                beanPost.owner_id = Integer.parseInt(map.get("owner_id").toString());
                beanPost.member_id = Integer.parseInt(map.get("member_id").toString());
                beanPost.parent_id = Integer.parseInt(map.get("parent_id").toString());
                beanPost.id = Integer.parseInt(map.get("id").toString());
                beanPost.ud = map.get("ud").toString();
                beanPost.tt = map.get("tt").toString();
                beanPost.fcb = map.get("fcb").toString();
                beanPost.ffn = map.get("ffn").toString();
                beanPost.rec = Integer.parseInt(map.get("rec").toString());
                beanPost.coc = Integer.parseInt(map.get("coc").toString());
                beanPost.lic = Integer.parseInt(map.get("lic").toString());
                beanPost.fac = Integer.parseInt(map.get("fac").toString());
                beanPost.cn = map.get("cn").toString();
                beanPost.nn = map.get("nn").toString();
                beanPost.witn = map.get("witn").toString();
                beanPost.wsr = map.get("wsr").toString();

                sendNotificationForDoctorCha(beanPost);

            } catch (Exception e) {
                //Toast 안 먹고 뻗어 버린다
                e.printStackTrace();
            }
        }
    }

    /*
    //1. setPriority(Notification.PRIORITY_MAX)
    //   1) statusbar 상단에 sticky popup이 뜬다.
    //   2) setTicker()는 작동하지 않는다.
    //   3) PRIORITY_HIGH도 동일하다
    */

    private void sendNotificationForSendbird(String channelUrl, final int mt, String tt, String message) {
        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("channelUrl", channelUrl);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        /*
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder tsb = TaskStackBuilder.create(this);
        tsb.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        tsb.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = tsb.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        */

        final PendingIntent pendingIntent = PendingIntent.getActivity(this, -1, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        final Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        final @DrawableRes int smallIcon = R.mipmap.ic_launcher;

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(smallIcon)
                .setLargeIcon(largeIcon)
                .setAutoCancel(true)
                .setVibrate(new long[] { 1000, 1000 }) //노티가 등록될 때 진동 패턴 1초씩 두번.
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE) //노티가 등록될 때 사운드와 진동이 오도록, 기본 사운드가 울리도록.
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(tt)
                .setContentText(message)
                .setAutoCancel(true);

        builder.setContentIntent(pendingIntent);

        //builder.setDefaults(Notification.DEFAULT_VIBRATE);
        //builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.setVibrate(new long[] { 1000, 1000 }); //노티가 등록될 때 진동 패턴 1초씩 두번.
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE); //노티가 등록될 때 사운드와 진동이 오도록, 기본 사운드가 울리도록.

        final NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

    private void sendNotificationForDoctorCha(final BeanPost beanPost) {
        final Intent intent;

        /*
        final Context context = getApplicationContext();
        boolean isAppRunning = false;

        final ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = activityManager.getRunningAppProcesses();

        for(ActivityManager.RunningAppProcessInfo runningAppProcessInfo :  runningAppProcessInfoList){
            if(runningAppProcessInfo.processName.equals(context.getPackageName())){
                isAppRunning = true;
                break;
            }
        }
        */

        if(MainApp.beanMember == null) {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("beanPost", beanPost);
        } else {
            intent = new Intent(this, FragmentContainerActivity.class);
            intent.putExtra(MainCons.EnumExtraName.NAME1.name(), PostReadFragment.class.getSimpleName());
            intent.putExtra("beanPost", beanPost);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        /*
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder tsb = TaskStackBuilder.create(this);
        tsb.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        tsb.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = tsb.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        */

        final PendingIntent pendingIntent = PendingIntent.getActivity(this, -1, intent, PendingIntent.FLAG_UPDATE_CURRENT );

        final String tt = beanPost.tt;
        final String fcb = beanPost.fcb;
        final String ffn = beanPost.ffn;

        final String ticker = "닥터차 새소식이 도착하였습니다";
        final String subText = "setSubText 입니다";

        final ImageLoader imageLoader = ImageLoader.getInstance();
        //final Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.shop_400x400);
        final Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_gcm);
        final @DrawableRes int smallIcon = R.mipmap.ic_launcher;

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(smallIcon)
                .setLargeIcon(largeIcon)
                .setTicker(ticker)
                .setContentTitle(tt)
                //.setSubText(subText)
                .setAutoCancel(true);

        if(!(TextUtils.isEmpty(fcb) || TextUtils.isEmpty(ffn))) {
            builder.setContentText(fcb);

            final NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
            final Bitmap bmp = imageLoader.loadImageSync(MainCons.EnumContentPath.CONTENT_I.getPath() + ffn);
            style.setBigContentTitle(tt);
            style.setSummaryText(fcb);
            style.bigPicture(bmp);

            builder.setStyle(style);
        } else if(!TextUtils.isEmpty(fcb)) {
            /*
            final NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
            style.setSummaryText("setSummaryText 입니다");
            style.setBigContentTitle(tt);
            style.bigText(fcb);

            builder.setStyle(style);
            */
            builder.setContentText(fcb);
        } else {
            final NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
            final Bitmap bmp = imageLoader.loadImageSync(MainCons.EnumContentPath.CONTENT_I.getPath() + ffn);
            style.setBigContentTitle(tt);
            style.bigPicture(bmp);

            builder.setStyle(style);
        }

        builder.setContentIntent(pendingIntent);

        //builder.setDefaults(Notification.DEFAULT_VIBRATE);
        //builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.setVibrate(new long[] { 1000, 1000 }); //노티가 등록될 때 진동 패턴 1초씩 두번.
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE); //노티가 등록될 때 사운드와 진동이 오도록, 기본 사운드가 울리도록.

        final NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }


}
