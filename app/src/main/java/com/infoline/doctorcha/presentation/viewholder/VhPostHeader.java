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
public class VhPostHeader extends RecyclerView.ViewHolder {
    @BindView(R.id.tv_board) public TextView tv_board;
    @BindView(R.id.tv_nn) public TextView tv_nn;
    @BindView(R.id.tv_tt) public TextView tv_tt;

    @BindView(R.id.iv_profile) public ImageView iv_profile;

    public VhPostHeader(View v) {
        super(v);
        ButterKnife.bind(this, v);
    }
}
