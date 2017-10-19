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
public class VhImage1 extends RecyclerView.ViewHolder {
    @BindView(R.id.iv_31) public ImageView iv_31;

    public VhImage1(View v) {
        super(v);
        ButterKnife.bind(this, v);
    }
}
