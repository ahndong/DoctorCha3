package com.infoline.doctorcha.presentation.bean;

import org.json.JSONObject;

//static import MainCons.EnumSendBirdSenderType;

/**
 * Created by Administrator on 2015-07-01.
 */

public class BeanChatChannelData {
    public int channel_ci = -1;      //channel create id -- 이전에는 복수개의 업체를 선택하여 새대화창을 만들 경우에는 필요했으나 지금은 channel_ci와 channel_mi는 동일할 것이다 - 일단은 그냥두고 관망하자
    public int channel_mi = -1;       //member id
    public int channel_ck = -1;       //channel kind - 0.일반 그룹채팅 챈널 1.멤버 vs 닥터차 상담전용 2.멤버 vs 닥터차, 파트너 상담 챈널(닥터차가 연결시킨) 3.멤버 vs 파트너(멤버가 직접 생성한 챈널)
    //public int channel_sr = -1;

    public BeanChatChannelData() {

    }

    public BeanChatChannelData(String data) {
        try {
            final JSONObject jo = new JSONObject(data);
            channel_ci = jo.getInt("ci");
            channel_ck = jo.getInt("ck");
            channel_mi = jo.getInt("mi"); //누가 create하였던가에 관계없이 상담을 받는 멤버 id
            //channel_sr = jo.getInt("sr"); //용도 고민하자
        }
        catch (Exception e) {
            final String msg = e.getMessage();
        }
    }

    public String toString() {
        //return String.format("{'ci':%s,'mi':%s,'ck':%s,'sr':%s}", channel_ci, channel_ck, channel_mi, channel_sr);
        return String.format("{'ci':%s,'mi':%s,'ck':%s}", channel_ci, channel_mi, channel_ck);
    }
}
