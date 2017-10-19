package com.infoline.doctorcha.presentation.viewholder;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.infoline.doctorcha.BR;
import com.infoline.doctorcha.core.binding.MyBindClickListener;

/**
 * Created by DELL on 2016-02-28.
 */
public class BindingHolder extends RecyclerView.ViewHolder {
    private final ViewDataBinding binding;

    public BindingHolder(View v) {
        super(v);

        //1. java.lang.IllegalArgumentException: View is not a binding layout
        //2. View root가 layout으로 감싸져 있지 않을 경우, 즉 binding이 명시적으로 선언되어 있지 않으면 오류발생한다
        //3. 코딩을 명확히 하기 위해서 try문은 걸지 않는다
        binding = DataBindingUtil.bind(v);;
    }

    public BindingHolder(View v, MyBindClickListener myBindClickListener) {
        super(v);
        binding = DataBindingUtil.bind(v);
        binding.setVariable(BR.myBindClickListener, myBindClickListener);
    }

    public ViewDataBinding getBinding() {
        return binding;
    }
}
