package com.infoline.doctorcha.core.binding;

import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.LineHeightSpan;
import android.widget.ImageView;

import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.util.CommonUtil;
import com.infoline.doctorcha.core.util.MediaUtil;
import com.infoline.doctorcha.presentation.MainApp;
import com.infoline.doctorcha.presentation.MainCons;
import com.infoline.doctorcha.presentation.bean.BeanMemberAndShop;
import com.infoline.doctorcha.presentation.bean.BeanPost;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.io.File;

import static com.infoline.doctorcha.presentation.MainApp.beanMember;
import static com.infoline.doctorcha.presentation.MainCons.EnumContentPath.CONTENT_I;
import static com.infoline.doctorcha.presentation.MainCons.EnumContentPath.CONTENT_V_T;

/**
 * Created by Administrator on 2017-09-11.
 */

public class MyBindingAdapter {
    private static final ImageLoader imageLoader = ImageLoader.getInstance();
    private static final DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .showImageOnFail(R.mipmap.ic_launcher)
            .displayer(new RoundedBitmapDisplayer(15))
            .build();

    private static final DisplayImageOptions optionsForOvalThumb = new DisplayImageOptions.Builder()
            .considerExifParams(true)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .displayer(new RoundedBitmapDisplayer(100))
            .showImageOnFail(R.drawable.ic_profile_member)
            .build();

    //vh_category_favorite
    @BindingAdapter({"imageRes"})
    public static void loadImage(ImageView iv, int resid) {
        iv.setImageResource(resid);
    }

    //vh_shop_favorite
    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView iv, BeanMemberAndShop beanMemberAndShop) {
        if(beanMemberAndShop == null) return;

        final int owner_sr = Integer.parseInt(beanMemberAndShop.sr);
        String ifn = beanMemberAndShop.ifn;

        if(ifn.isEmpty()) {
            ifn = owner_sr == 0 ? "ifn_m.jpg" : (owner_sr == 1 || owner_sr == 9 ? "ifn_c.jpg" : "ifn_s.jpg");
        }
        ifn = CommonUtil.getFileName(ifn).concat("_200x200.jpg");

        imageLoader.displayImage(MainCons.EnumContentPath.MEMBER_I.getPath().concat(ifn), iv, options);
    }

    //vh_shop_favorite
    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView iv, BeanPost beanPost) {
        String witn = beanPost.witn;

        if(witn.isEmpty()) {
            int wsr = Integer.valueOf(beanPost.wsr);

            witn = wsr == 0 ? "itn_m.jpg" : (wsr == 1 || wsr == 9 ? "itn_c.jpg" : "itn_s.jpg");
        }

        imageLoader.displayImage(MainCons.EnumContentPath.MEMBER_I.getPath() + witn, iv, optionsForOvalThumb);
    }

    //vh_post(MainFavoriteFragment, PostListFragment), vh_content_editphoto(PostEditFragment), vh_content_readphoto(PostReadFragment)
    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView iv, String fileName) {
        if(fileName.isEmpty()) return;

        final String url;
        final boolean isVideo = MediaUtil.isVideo(fileName);

        if(fileName.contains("/")) {
            //1. beanContent.realUrl
            final File file = new File(fileName);
            final Uri uri = Uri.fromFile(file);

            //파일명이 한글일 경우 uri는 이미 encoding되어 있다

            url = Uri.decode(uri.toString());
        } else {
            if(isVideo) {
                url = CONTENT_V_T.getPath() + fileName.replace(CommonUtil.getExtName(fileName), "jpg");
            } else {
                url = CONTENT_I.getPath() + fileName;
            }
        }

        imageLoader.displayImage(url, iv);
    }

    //vh_shop_thumnbnail(ShopListFragment)
    @BindingAdapter({"imageUrl2"})
    public static void loadImage2(ImageView iv, BeanMemberAndShop beanMemberAndShop) {
        String ifn = beanMemberAndShop.ifn;

        if(ifn.isEmpty()) {
            ifn = "ifn_s.jpg";
        }

        final String fileName = CommonUtil.getFileName(ifn); //순수 파일명
        final String thumbFileNameWithExt = fileName.concat("_200x200.jpg");

        imageLoader.displayImage(MainCons.EnumContentPath.MEMBER_I.getPath() + thumbFileNameWithExt, iv, MainApp.optionsForRectThumb);
    }


}
