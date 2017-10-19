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
public class VhText1 extends RecyclerView.ViewHolder {
    @BindView(R.id.tv_31) public TextView tv_31;

    public VhText1(View v) {
        super(v);
        ButterKnife.bind(this, v);
    }
}
