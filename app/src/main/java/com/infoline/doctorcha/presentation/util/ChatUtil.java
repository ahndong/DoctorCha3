package com.infoline.doctorcha.presentation.util;

import com.infoline.doctorcha.presentation.bean.BeanChatMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-01-02.
 */

public class ChatUtil {
    private String getDisplayMemberNames(final GroupChannel groupChannel, final int sr) {
        //0.member 1.consellor 2.partener

        final StringBuilder names = new StringBuilder();
        for (User user : groupChannel.getMembers()) {
            if (user.getUserId().equals(SendBird.getCurrentUser().getUserId())) {
                continue;
            }

            names.append(", ");
            names.append(user.getNickname());
        }
        return names.delete(0, 2).toString();
    }

    public static List<Integer> getChatMemberIdList(final GroupChannel groupChannel) {
        //지정된 groupChannel의 대화 참여 멤버의 id list

        final List<Integer> list = new ArrayList<>();
        for (User user : groupChannel.getMembers()) {
            list.add(Integer.parseInt(user.getUserId()));
        }

        return list;
    }

    public static String getNickNameFromId(final GroupChannel groupChannel, int id) {
        //1. groupChannel.getMembers()에서 전달받은 id의 nickname 산출
        String id_s = String.valueOf(id);
        String nickName = "";

        final List<Integer> list = new ArrayList<>();
        for (User user : groupChannel.getMembers()) {
            if(user.getUserId().equals(id_s)) {
                nickName = user.getNickname();
            }
        }

        return nickName;
    }

    public static String getSenderName(final BeanChatMessage beanChatMessage) {
        String senderName;

        switch (beanChatMessage.chat_st) {
            case 8:
                senderName = "닥터차 AI";
                break;
            case 1:
            case 9:
                senderName = "닥터차";
                break;
            default:
                senderName = beanChatMessage.chat_nn;
        }

        return senderName;
    }

    public static String getDisplayMessage(final BeanChatMessage beanChatMessage) {
        String message;

        if(beanChatMessage.chat_mt == 1) {
            message = beanChatMessage.chat_mb;
        } else if(beanChatMessage.chat_mt == 2) {
            message = "사진이 도착하였습니다";
        } else if(beanChatMessage.chat_mt == 3) {
            message = "동영상이 도착하였습니다";
        } else {
            //message = "메세지가 도착하였습니다";
            message = beanChatMessage.chat_mb;
        }

        return message;
    }
}
