package com.infoline.doctorcha.presentation.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2015-07-01.
 */

public class BeanFavoriteShop {
    public int member_id;
    public int blog_id;

    public BeanFavoriteShop(final int member_id, final int blog_id) {
        this.member_id = member_id;
        this.blog_id = blog_id;
    }
}