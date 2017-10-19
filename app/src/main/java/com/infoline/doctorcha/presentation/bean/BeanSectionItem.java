package com.infoline.doctorcha.presentation.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2015-07-01.
 */

public class BeanSectionItem {
    public int st;     //section type
    public int id;     //section header 일 경우는 0, chlid list일 경우는 category, shop, pos등 고유 id --> -1은 empty viewholder용으로 사용
    public int headerOp;
    public int op;     //original position

    public BeanSectionItem(final int st, final int id, final int headerOp, final int op) {
        this.st = st;
        this.id = id;
        this.headerOp = headerOp;
        this.op = op;
    }
}