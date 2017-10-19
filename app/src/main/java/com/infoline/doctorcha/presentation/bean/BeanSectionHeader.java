package com.infoline.doctorcha.presentation.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2015-07-01.
 */

public class BeanSectionHeader {
    public int id;
    public String nm;
    public int itemCount;
    public boolean collapsed;

    public BeanSectionHeader() {

    }

    public BeanSectionHeader(final int id, final String nm, final int itemCount, final boolean collapsed) {
        this.id = id;
        this.nm = nm;
        this.itemCount = itemCount;
        this.collapsed = collapsed;
    }
}