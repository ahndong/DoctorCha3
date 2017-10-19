package com.infoline.doctorcha.presentation.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Administrator on 2015-07-01.
 */

public class BeanYutubePlay implements Serializable {
    public String id;
    public String publishedAt;
    public String title;
    public String description;
    public String thumbnail;

    public BeanYutubePlay() {

    }

    public BeanYutubePlay(final String id, final String publishedAt, final String title, final String description, final String thumbnail) {
        this.id = id;
        this.publishedAt = publishedAt;
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
    }


}