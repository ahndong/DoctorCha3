package com.infoline.doctorcha.presentation.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;


/**
 * Created by Administrator on 2015-07-01.
 */

public class BeanMember implements Serializable {
    public int id;
    public String sr;  //security role
    public String nm;  //nick name
    public String nn;  //nick name
    public String upw;
    public String hn;
    public String ea;  //e-mail
    public String ccu;  //sendbird counsel channel url
    public String ifn;
    public String itn;

    public BeanMember() {

    }
}