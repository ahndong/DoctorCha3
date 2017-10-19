package com.infoline.doctorcha.presentation.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Administrator on 2015-07-01.
 */

public class BeanName implements Serializable {
    public int id;
    public String nm;
    public String etc;

    public BeanName() {

    }

    public BeanName(final int id, final String nm, final String etc) {
        this.id = id;
        this.nm = nm;
        this.etc = etc;
    }
}
