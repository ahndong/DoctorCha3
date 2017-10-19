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
public class VhImage3Text1 extends RecyclerView.ViewHolder {
    @BindView(R.id.iv_31) public ImageView iv_31;
    @BindView(R.id.iv_32) public ImageView iv_32;
    //@BindView(R.id.iv_33) public ImageView iv_33;
    @BindView(R.id.tv_31) public TextView tv_31;

    public VhImage3Text1(View v) {
        super(v);
        ButterKnife.bind(this, v);
    }
}
