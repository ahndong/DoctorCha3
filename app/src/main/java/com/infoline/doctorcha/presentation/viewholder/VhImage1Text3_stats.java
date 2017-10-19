package com.infoline.doctorcha.presentation.viewholder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.util.CommonUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by DELL on 2016-02-28.
 */
public class VhImage1Text3_stats extends RecyclerView.ViewHolder {
    @BindView(R.id.iv_31) public ImageView iv_31;
    @BindView(R.id.tv_31) public TextView tv_31;
    @BindView(R.id.tv_32) public TextView tv_32;
    @BindView(R.id.tv_33) public TextView tv_33;

    //inc_stats_post
    @BindView(R.id.tv_41) public TextView tv_41;
    @BindView(R.id.tv_42) public TextView tv_42;
    @BindView(R.id.tv_43) public TextView tv_43;
    @BindView(R.id.tv_44) public TextView tv_44;

    @OnClick({R.id.tv_41, R.id.tv_42, R.id.tv_43, R.id.tv_44})
    protected void onClick(View statsView){
        Log.d("ChildView===>", "onClick");
        //Recycler.OnItemTouchListener가 걸려 있을 경우 sigleTap이후에 trigger된다
        statsView.setSelected(!statsView.isSelected());
    }

    public VhImage1Text3_stats(View v) {
        super(v);
        ButterKnife.bind(this, v);

        CommonUtil.setCompoundDrawablesWithTintList(tv_41, R.drawable.ic_menu_view_16_20, R.color.tint_stats_icon);
        CommonUtil.setCompoundDrawablesWithTintList(tv_42, R.drawable.ic_menu_start_conversation_16_20, R.color.tint_stats_icon);
        CommonUtil.setCompoundDrawablesWithTintList(tv_43, R.drawable.ic_menu_star_16_20, R.color.tint_stats_icon);
        CommonUtil.setCompoundDrawablesWithTintList(tv_44, R.drawable.ic_menu_share_16_20, R.color.tint_stats_icon);

        tv_41.setSoundEffectsEnabled(false);
        tv_42.setSoundEffectsEnabled(false);
        tv_43.setSoundEffectsEnabled(false);
        tv_44.setSoundEffectsEnabled(false);
    }
}
