package com.infoline.doctorcha.presentation.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2015-07-01.
 */

public class BeanMainCategory implements Parcelable {
    public int id;
    public String nm;
    public int ct;
    public int res;
    public float sn;

    public BeanMainCategory() {

    }

    public BeanMainCategory(final int id, final String nm, final int ct, final int res, final float sn) {
        this.id = id;
        this.nm = nm;
        this.ct = ct;    //Category Type
        this.res = res;  //Drawable Resource Id
        this.sn = sn;    //Serial No - Category Type 별
    }

    public BeanMainCategory(Parcel src) {
        this.id = src.readInt();
        this.nm = src.readString();
        this.ct = src.readInt();
        this.res = src.readInt();
        this.sn = src.readFloat();
    }

    //@SuppressWarnings("unchecked") --> 다른 sample에서는 선언되지 않는다
    public static final Creator<BeanMainCategory> CREATOR = new Creator<BeanMainCategory>() {
        @Override
        public BeanMainCategory createFromParcel(final Parcel src) {
            return new BeanMainCategory(src);
        }

        @Override
        public BeanMainCategory[] newArray(final int size) {
            return new BeanMainCategory[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flag) {
        dest.writeInt(id);
        dest.writeString(nm);
        dest.writeInt(ct);
        dest.writeInt(res);
        dest.writeFloat(sn);
    }
}