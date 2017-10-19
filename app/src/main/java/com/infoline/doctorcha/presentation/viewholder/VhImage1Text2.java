package com.infoline.doctorcha.presentation.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.infoline.doctorcha.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by DELL on 2016-02-28.
 */
public class VhImage1Text2 extends RecyclerView.ViewHolder {
    @BindView(R.id.iv_31) public ImageView iv_31;
    @BindView(R.id.tv_31) public TextView tv_31;
    @BindView(R.id.tv_32) public TextView tv_32;

    public VhImage1Text2(View v) {
        super(v);
        ButterKnife.bind(this, v);
    }
}
