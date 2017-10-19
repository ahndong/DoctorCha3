package com.infoline.doctorcha.presentation.bean;

import android.support.annotation.DrawableRes;

/**
 * Created by Administrator on 2016-09-26.
 */
public class BeanSimpleItem {
    private int drawableRes;
    private String title;

    public BeanSimpleItem(@DrawableRes int drawableRes, String title) {
        this.drawableRes = drawableRes;
        this.title = title;
    }

    public @DrawableRes int getDrawableRes() {
        return drawableRes;
    }

    public String getTitle() {
        return title;
    }
}
