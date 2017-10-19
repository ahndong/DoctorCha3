package com.infoline.doctorcha.presentation.bean;

import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_MT_COMMAND;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_MT_CONFIRM_REQUEST;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_MT_CONFIRM_RESPONSE;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_MT_MAP;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_MT_VIEW;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_ST_ADMIN;

import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_MT_TEXT;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_MT_IMAGE;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_MT_VIDEO;

import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_COMMAND;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_CONFIRM_REQUEST;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_CONFIRM_RESPONSE;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_MAP_LEFT;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_MAP_RIGHT;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_TEXT;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_TEXT_LEFT;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_TEXT_RIGHT;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_IMAGE;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_IMAGE_LEFT;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_IMAGE_RIGHT;

import static com.infoline.doctorcha.core.util.CommonUtil.getDisplayTimeOrDate;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_VIDEO;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_VIDEO_LEFT;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_VIDEO_RIGHT;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_VIEW;

import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.SendBird;
import com.sendbird.android.User;
import com.sendbird.android.UserMessage;

import org.json.JSONObject;

//static import MainCons.EnumSendBirdSenderType;

/**
 * Created by Administrator on 2015-07-01.
 */

public class BeanChatMessage {
    public long chat_mid;       //chat message id
    public String chat_uid;       //chat user id
    public String chat_nn;      //chat nick name
    public String chat_pu;      //chat profile url
    public String chat_mb;      //message body - textmessage, image url, thumbnail url
    public String chat_vu;      //video url - CarsGramRest
    public String chat_ca;      //create at
    public int chat_st;      //chat sender type
    public int chat_mt;      //chat message type
    public int chat_ct;      //chat command type
    public int chat_rv;      //response value
    public String chat_sv;
    public String chat_hg;
    public String chat_itn;

    public BeanChatMessage(BaseMessage baseMessage) {
        final User user;
        final String data;
        final BeanChatMessageData beanChatMessageData;

        if(baseMessage instanceof UserMessage) {
            final UserMessage userMessage = (UserMessage)baseMessage;
            user = userMessage.getSender();
            data = userMessage.getData();
            chat_mb = userMessage.getMessage();
        } else {
            final FileMessage fileMessage = (FileMessage)baseMessage;
            user = fileMessage.getSender();
            data = fileMessage.getData();
            chat_mb = fileMessage.getUrl();
        }

        beanChatMessageData = new BeanChatMessageData(data);

        chat_mid = baseMessage.getMessageId();
        chat_uid = user.getUserId();
        chat_ca = getDisplayTimeOrDate(baseMessage.getCreatedAt());
        chat_st = beanChatMessageData.chat_st;
        chat_mt = beanChatMessageData.chat_mt;
        chat_ct = beanChatMessageData.chat_ct;
        chat_rv = beanChatMessageData.chat_rv;
        chat_vu = beanChatMessageData.chat_vu;
        chat_sv = beanChatMessageData.chat_sv;
        chat_hg = beanChatMessageData.chat_hg;

        chat_nn = user.getNickname();
        chat_pu = user.getProfileUrl();

        //-------------TODO : 성능에 너무 안좋다. 정리할 것
        String itn = "";
        try {
            final JSONObject jo = new JSONObject(user.getProfileUrl());
            itn = jo.getString("itn");
        } catch (Exception e) {
            //dummy
        }

        if(itn.isEmpty()) {
            itn = chat_st == 0 ? "itn_m.jpg" : (chat_st == 1 || chat_st == 9 ? "itn_c.jpg" : "itn_s.jpg");
        }
        chat_itn = itn;
        //-------------
    }

    public static int getChatMessageViewType(final int chat_st, final int chat_mt, final String chat_uid) {        ;
        final int chat_vt;      //chat viewholder type

        if(chat_st == CN_CHAT_ST_ADMIN) {
            switch (chat_mt) {
                case CN_CHAT_MT_TEXT:
                    chat_vt = CN_CHAT_VT_TEXT;
                    break;
                case CN_CHAT_MT_IMAGE:
                    chat_vt = CN_CHAT_VT_IMAGE;
                    break;
                case CN_CHAT_MT_VIDEO:
                    chat_vt = CN_CHAT_VT_VIDEO;
                    break;
                case CN_CHAT_MT_COMMAND:
                    chat_vt = CN_CHAT_VT_COMMAND;
                    break;
                case CN_CHAT_MT_CONFIRM_REQUEST:
                    chat_vt = CN_CHAT_VT_CONFIRM_REQUEST;
                    break;
                case CN_CHAT_MT_CONFIRM_RESPONSE:
                    chat_vt = CN_CHAT_VT_CONFIRM_RESPONSE;
                    break;
                case CN_CHAT_MT_VIEW:
                default:
                    chat_vt = CN_CHAT_VT_VIEW;
                    break;
            }
        }
        else {
            final boolean chat_isMine = chat_uid.equals(SendBird.getCurrentUser().getUserId());
            switch (chat_mt) {
                case CN_CHAT_MT_TEXT:
                    chat_vt = chat_isMine ? CN_CHAT_VT_TEXT_RIGHT : CN_CHAT_VT_TEXT_LEFT;
                    break;
                case CN_CHAT_MT_MAP:
                    chat_vt = chat_isMine ? CN_CHAT_VT_MAP_RIGHT : CN_CHAT_VT_MAP_LEFT;
                    break;
                case CN_CHAT_MT_IMAGE:
                    chat_vt = chat_isMine ? CN_CHAT_VT_IMAGE_RIGHT : CN_CHAT_VT_IMAGE_LEFT;;
                    break;
                case CN_CHAT_MT_VIDEO:
                default:
                    chat_vt = chat_isMine ? CN_CHAT_VT_VIDEO_RIGHT : CN_CHAT_VT_VIDEO_LEFT;
                    break;
            }
        }

        return chat_vt;
    }
}
