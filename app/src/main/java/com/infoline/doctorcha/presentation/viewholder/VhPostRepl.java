package com.infoline.doctorcha.presentation.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.infoline.doctorcha.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by DELL on 2016-02-28.
 */
public class VhPostRepl extends RecyclerView.ViewHolder {
    @BindView(R.id.tv_31) public TextView tv_31;
    @BindView(R.id.iv_31) public ImageView iv_31;

    @BindView(R.id.fl_image) public FrameLayout fl_image;
    @BindView(R.id.iv_video_icon) public ImageView iv_video_icon;

    public VhPostRepl(View v) {
        super(v);
        ButterKnife.bind(this, v);
    }
}
