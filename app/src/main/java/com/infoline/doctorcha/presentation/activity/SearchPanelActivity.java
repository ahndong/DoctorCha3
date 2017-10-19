package com.infoline.doctorcha.presentation.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.util.CommonUtil;
import com.infoline.doctorcha.presentation.bean.BeanPostSearch;
import com.infoline.doctorcha.presentation.bean.BeanShopSearch;
import com.infoline.doctorcha.presentation.fragment.SearchPanelOfPostsFragment;
import com.infoline.doctorcha.presentation.fragment.SearchPanelOfShopsFragment;

import butterknife.ButterKnife;

public class SearchPanelActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_searchpanel);
        ButterKnife.bind(this);

        final FragmentManager fm = getFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        final Fragment fragment;

        final BeanShopSearch beanShopSearch = (BeanShopSearch)getIntent().getSerializableExtra(BeanShopSearch.class.getSimpleName());

        if(beanShopSearch != null) {
            fragment = SearchPanelOfShopsFragment.newInstance(beanShopSearch);
        } else {
            final BeanPostSearch beanPostSearch = (BeanPostSearch)getIntent().getSerializableExtra(BeanPostSearch.class.getSimpleName());
            fragment = SearchPanelOfPostsFragment.newInstance(beanPostSearch);
        }

        ft.replace(R.id.fl_fragment, fragment);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        //아래 사용 이유는 Reference Project GeneralActivity 참조
        CommonUtil.hideSoftInput(this);

        super.finish();
        //overridePendingTransition(0, R.anim.activity_slide_out_left);
        overridePendingTransition(0, R.anim.activity_zoomout_alone);
    }
}