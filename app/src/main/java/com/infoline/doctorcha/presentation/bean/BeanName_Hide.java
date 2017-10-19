package com.infoline.doctorcha.presentation.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2015-07-01.
 */

public class BeanName_Hide implements Parcelable {
    private int id;
    private String nm;
    private int st; //SectionType
    private int hide;

    public BeanName_Hide() {

    }

    public BeanName_Hide(int id, String nm, int st, boolean hide) {
        this.id = id;
        this.nm = nm;
        this.st = st;
        this.hide = hide ? 1 : 0;
    }

    public BeanName_Hide(Parcel src) {
        this.id = src.readInt();
        this.nm = src.readString();
        this.st = src.readInt();
        this.hide = src.readInt();
    }

    public int getId() {
        return id;
    }

    public String getNm() {
        return nm;
    }

    public int getSt() {
        return st;
    }

    public boolean getHide() {
        return hide == 1;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNm(String nm) {
        this.nm = nm;
    }

    public void setSt(int st) {
        this.st = st;
    }

    public void setHide(boolean hide) {
        this.hide = hide ? 1 : 0;
    }

    @SuppressWarnings("unchecked")
    public static final Creator<BeanName_Hide> CREATOR = new Creator<BeanName_Hide>() {
        @Override
        public BeanName_Hide createFromParcel(Parcel src) {
            BeanName_Hide item = new BeanName_Hide();

            item.setId(src.readInt());
            item.setNm(src.readString());
            item.setSt(src.readInt());
            item.setHide(src.readInt() == 1);

            return item;
        }

        @Override
        public BeanName_Hide[] newArray(int size) {
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
        dest.writeInt(st);
        dest.writeInt(hide);
    }
}