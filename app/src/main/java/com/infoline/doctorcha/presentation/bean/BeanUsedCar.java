package com.infoline.doctorcha.presentation.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2015-07-01.
 */

public class BeanUsedCar implements Parcelable {
    private int id;
    private String nm;
    private int resId;
    private int hide;
    private int sn;

    public BeanUsedCar() {

    }

    public BeanUsedCar(int id, String nm, int resId, boolean hide, int sn) {
        this.id = id;
        this.nm = nm;
        this.resId = resId;
        this.hide = hide ? 1 : 0;
        this.sn = sn;
    }

    public BeanUsedCar(Parcel src) {
        this.id = src.readInt();
        this.nm = src.readString();
        this.resId = src.readInt();
        this.hide = src.readInt();
        this.sn = src.readInt();
    }

    public int getId() {
        return id;
    }

    public String getNm() {
        return nm;
    }

    public int getResId() {
        return resId;
    }

    public boolean getHide() {
        return hide == 1;
    }

    public int getSn() {
        return sn;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNm(String nm) {
        this.nm = nm;
    }

    public void setResId(int id) {
        this.resId = resId;
    }

    public void setHide(boolean hide) {
        this.hide = hide ? 1 : 0;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    @SuppressWarnings("unchecked")
    public static final Creator<BeanUsedCar> CREATOR = new Creator<BeanUsedCar>() {
        @Override
        public BeanUsedCar createFromParcel(Parcel src) {
            BeanUsedCar headerItem = new BeanUsedCar();

            headerItem.setId(src.readInt());
            headerItem.setNm(src.readString());

            return headerItem;
        }

        @Override
        public BeanUsedCar[] newArray(int size) {
            return null;
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag) {
        dest.writeInt(id);
        dest.writeString(nm);
    }
}