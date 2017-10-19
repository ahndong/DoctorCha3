package com.infoline.doctorcha.presentation.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.infoline.doctorcha.presentation.bean.BeanResponse;

/**
 * Created by Administrator on 2016-09-08.
 */
public class AppUtil {
    public static BeanResponse getBeanResponseFromJson(JsonObject jo) {
        //1. BeanResponse beanResponse = (BeanResponse)LoganSquare.parse(jo.toString(), BeanResponse.class);
        //2. 위 구문 실행하면 dat = null 로 parsing되므로 별도 함수 작성

        final BeanResponse beanResponse = new BeanResponse();

        try {
            beanResponse.code = jo.get("code").getAsString();
            beanResponse.subCode = jo.get("subCode").getAsInt();
            beanResponse.msg = jo.get("msg").getAsString();
            beanResponse.scalar = jo.get("scalar").getAsString();

            beanResponse.data = jo.get("data").toString();

            final JsonObject dataArray = jo.getAsJsonObject("dataArray");

            beanResponse.d1 = dataArray.getAsJsonArray("d1").toString();
            beanResponse.d2 = dataArray.getAsJsonArray("d2").toString();
            beanResponse.d3 = dataArray.getAsJsonArray("d3").toString();        }
        catch (Exception e) {
            //의미없다
        }

        return beanResponse;
    }
}
