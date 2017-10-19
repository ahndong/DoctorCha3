package com.infoline.doctorcha.presentation.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.infoline.doctorcha.R;
import com.infoline.doctorcha.presentation.MainApp;
import com.infoline.doctorcha.presentation.bean.BeanSimpleItem;
import com.infoline.doctorcha.presentation.viewholder.VhImage1Text1;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-09-26.
 */
public class SimpleItemAdapter extends RecyclerView.Adapter<SimpleItemAdapter.SimpleViewHolder> {
    private final SimpleItemClickListener simpleItemClickListener;
    private final List<BeanSimpleItem> beanSimpleItemList;
    private RecyclerView rv;

    public SimpleItemAdapter(List<BeanSimpleItem> beanSimpleItemList, SimpleItemClickListener simpleItemClickListener) {
        this.beanSimpleItemList = beanSimpleItemList;
        this.simpleItemClickListener = simpleItemClickListener;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup vg, int viewType) {
        return new SimpleViewHolder(LayoutInflater.from(vg.getContext()).inflate(R.layout.vh_simple_item_text_below_circleimage, vg, false));
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder vh, int pos) {
        final BeanSimpleItem beanSimpleItem = beanSimpleItemList.get(pos);

        //////////imageLoader.displayImage("drawable://" + beanSimpleItem.getDrawableRes(), vh.iv_31, MainApp.optionsForCircleThumb);
        vh.iv_31.setImageResource(beanSimpleItem.getDrawableRes());
        //vh.tv_31.setText(beanSimpleItem.getTitle());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView rv) {
        this.rv = rv;
    }

    @Override
    public int getItemCount() {
        return beanSimpleItemList.size();
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        //@BindView(R.id.tv_31) public TextView tv_31;
        @BindView(R.id.iv_31) public ImageView iv_31;

        public SimpleViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            simpleItemClickListener.onSimpleItemClick(rv.getChildAdapterPosition(v));
        }
    }


    public interface SimpleItemClickListener {
        void onSimpleItemClick(int pos);
    }
}
