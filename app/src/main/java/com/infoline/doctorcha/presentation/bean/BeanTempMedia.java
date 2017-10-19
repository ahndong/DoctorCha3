package com.infoline.doctorcha.presentation.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2015-07-01.
 */

public class BeanTempMedia {
    public int id;
    public String su; //server url
    public String lu; //local url - content real path

    //0.saved 1.new 2.updated
    public int ms; //madia status

    public BeanTempMedia() {

    }

    public BeanTempMedia(int id, String su, String lu, int ms) {
        this.id = id;
        this.su = su;
        this.lu = lu;
        this.ms = ms;
    }
}
