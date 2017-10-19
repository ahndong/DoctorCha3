package com.infoline.doctorcha.presentation.bean;

import android.content.SharedPreferences;

import com.infoline.doctorcha.presentation.MainCons;

/**
 * Created by Administrator on 2015-07-01.
 */
//1. 하 번 호출되면 끝이므로 MainCons.EnumPreferAuth을 static으로 import하지 않는다
public class BeanPreferAuth {
    public int id;      //meber primary key
    public String sr;   //security role
    public String nn;   //neick name
    public String upw;   //neick name
    public String ccu;   //counsel channel url
    public boolean SAVED_GCMTOKEN_SERVER;
    public boolean SAVED_GCMTOKEN_SENDBIRD;

    public BeanPreferAuth(final SharedPreferences sp) {
        this.id = sp.getInt(MainCons.EnumPreferAuth.id.name(), 0);
        this.sr = sp.getString(MainCons.EnumPreferAuth.sr.name(), "");
        this.nn = sp.getString(MainCons.EnumPreferAuth.nn.name(), "");
        this.upw = sp.getString(MainCons.EnumPreferAuth.upw.name(), "");
        this.ccu = sp.getString(MainCons.EnumPreferAuth.ccu.name(), "");
        this.SAVED_GCMTOKEN_SERVER = sp.getBoolean(MainCons.EnumPreferAuth.SAVED_GCMTOKEN_SERVER.name(), false);
        this.SAVED_GCMTOKEN_SENDBIRD = sp.getBoolean(MainCons.EnumPreferAuth.SAVED_GCMTOKEN_SENDBIRD.name(), false);
    }
}