package com.infoline.doctorcha.presentation.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2015-07-01.
 */

public class BeanYutubeVideo implements Parcelable {
    public String videoId;
    public String publishedAt;
    public String title;
    public String description;
    public String thumbnail;

    public BeanYutubeVideo() {

    }

    public BeanYutubeVideo(final String videoId, final String publishedAt, final String title, final String description, final String thumbnail) {
        this.videoId = videoId;
        this.publishedAt = publishedAt;
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public BeanYutubeVideo(Parcel src) {
        this.videoId = src.readString();
        this.publishedAt = src.readString();
        this.title = src.readString();
        this.description = src.readString();
        this.thumbnail = src.readString();
    }

    public static final Creator<BeanYutubeVideo> CREATOR = new Creator<BeanYutubeVideo>() {
        @Override
        public BeanYutubeVideo createFromParcel(final Parcel src) {
            return new BeanYutubeVideo(src);
        }

        @Override
        public BeanYutubeVideo[] newArray(final int size) {
            return new BeanYutubeVideo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flag) {
        dest.writeString(videoId);
        dest.writeString(publishedAt);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(thumbnail);
    }
}

/*
public class BeanYutubeVideo implements Parcelable {
    public String videoId;
    public String publishedAt;
    public String title;
    public String description;
    public String thumbnail;
    public String viewCount;
    public String likeCount;
    public String dislikeCount;
    public String favoriteCount;
    public String commentCount;

    public BeanYutubeVideo() {

    }

    public BeanYutubeVideo(final String videoId, final String publishedAt, final String title, final String description, final String thumbnail, final String viewCount, final String likeCount, final String dislikeCount, final String favoriteCount, final String commentCount) {
        this.videoId = videoId;
        this.publishedAt = publishedAt;
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.favoriteCount = favoriteCount;
        this.commentCount = commentCount;
    }

    public BeanYutubeVideo(Parcel src) {
        this.videoId = src.readString();
        this.publishedAt = src.readString();
        this.title = src.readString();
        this.description = src.readString();
        this.thumbnail = src.readString();
        this.viewCount = src.readString();
        this.likeCount = src.readString();
        this.dislikeCount = src.readString();
        this.favoriteCount = src.readString();
        this.commentCount = src.readString();
    }

    public static final Creator<BeanYutubeVideo> CREATOR = new Creator<BeanYutubeVideo>() {
        @Override
        public BeanYutubeVideo createFromParcel(final Parcel src) {
            return new BeanYutubeVideo(src);
        }

        @Override
        public BeanYutubeVideo[] newArray(final int size) {
            return new BeanYutubeVideo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flag) {
        dest.writeString(videoId);
        dest.writeString(publishedAt);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(thumbnail);
        dest.writeString(viewCount);
        dest.writeString(likeCount);
        dest.writeString(dislikeCount);
        dest.writeString(favoriteCount);
        dest.writeString(commentCount);
    }
}
 */