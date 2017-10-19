package com.infoline.doctorcha.presentation.bean;

import org.json.JSONObject;

//static import MainCons.EnumSendBirdSenderType;

/**
 * Created by Administrator on 2015-07-01.
 */

public class BeanChatMessageData {
    public int chat_st = -1;      //chat sender type
    public int chat_mt = -1;      //chat message type
    public int chat_ct = -1;      //chat coomand type
    public int chat_rv = 0;      //confirm에 대한 response value : 승낙(1), 거절(0) --> 사용할 필요성이 없을 경우 폐기할 것
    public String chat_vu = "";   //video url - CarsGramRest

    //1. 특수목적의 문자열 정보
    //2. 차량정보, 사고수리내역 등을 전송할 경우 필요한 post_id등 특정목적을 위한 단일 값
    //3. 2의 경우 이 값에 대하여 Integer.parsㄷ 연산을 하므로 runtime 오류 주의할 것 : 위도,경도도 sv를 활용한다
    public String chat_sv = "0";

    //1. 이 콘텐츠를 숨겨야 할 sr list
    //2.
    public String chat_hg = "";

    //아래는 2016.12.19 17:09 메세지부터 적용되는 속성
    //public

    public BeanChatMessageData() {

    }

    public BeanChatMessageData(String data) {
        try {
            final JSONObject jo = new JSONObject(data);

            if(jo.has("st")) chat_st = jo.getInt("st");
            if(jo.has("mt")) chat_mt = jo.getInt("mt");
            if(jo.has("ct")) chat_ct = jo.getInt("ct");
            if(jo.has("rv")) chat_rv = jo.getInt("rv");
            if(jo.has("vu")) chat_vu = jo.getString("vu");
            if(jo.has("sv")) chat_sv = jo.getString("sv");
            if(jo.has("hg")) chat_hg = jo.getString("hg");
        }
        catch (Exception e) {
            final String msg = e.getMessage();
        }
    }

    public String toString() {
        return String.format("{'st':%s,'mt':%s,'ct':%s,'rv':%s,'vu':'%s','sv':'%s','hg':'%s'}", chat_st, chat_mt, chat_ct, chat_rv, chat_vu, chat_sv, chat_hg);
    }
}
