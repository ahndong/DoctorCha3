package com.infoline.doctorcha.presentation.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.infoline.doctorcha.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by DELL on 2016-02-28.
 */
public class VhText2 extends RecyclerView.ViewHolder {
    @BindView(R.id.tv_31) public TextView tv_31;
    @BindView(R.id.tv_32) public TextView tv_32;

    public VhText2(View v) {
        super(v);
        ButterKnife.bind(this, v);
    }
}
