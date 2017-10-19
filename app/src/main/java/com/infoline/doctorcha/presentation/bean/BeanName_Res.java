package com.infoline.doctorcha.presentation.bean;

import android.support.annotation.DrawableRes;

import java.io.Serializable;

/**
 * Created by Administrator on 2015-07-01.
 */

public class BeanName_Res implements Serializable {
    public int id;
    public String nm;
    public @DrawableRes int resId;


    public BeanName_Res() {

    }

    public BeanName_Res(int id, String nm, @DrawableRes int resId) {
        this.id = id;
        this.nm = nm;
        this.resId = resId;
    }
}