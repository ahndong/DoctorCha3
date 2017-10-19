package com.infoline.doctorcha.presentation.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.transformer.DepthPageTransformer;
import com.infoline.doctorcha.presentation.MainCons;
import com.infoline.doctorcha.presentation.bean.BeanPostSearch;
import com.infoline.doctorcha.presentation.bean.BeanShopSearch;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeOfShopsFragment extends Fragment {
	@BindView(R.id.stl)
	SmartTabLayout stl;
	@BindView(R.id.vp)
	ViewPager vp;

	int bmCategory_id;

	public HomeOfShopsFragment() {

	}

    public static HomeOfShopsFragment newInstance(int bmCategory_id) {
		final HomeOfShopsFragment fragment = new HomeOfShopsFragment();

		final Bundle bundle = new Bundle();

        bundle.putInt("bmCategory_id", bmCategory_id);
        fragment.setArguments(bundle);

		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_homeofshops, container, false);
        ButterKnife.bind(this, rootView);

		final Bundle bundle = getArguments();
		bmCategory_id = bundle.getInt("bmCategory_id", -1);

		vp.setAdapter(getFpa());
		vp.setPageTransformer(true, new DepthPageTransformer());

		stl.setViewPager(vp);

		return rootView;
	}

	private FragmentPagerAdapter getFpa() {
		//1. 아래 2 현재로써는 아무 차이가 없다
		//return new FragmentPagerAdapter(getdFragmentManager()) {
		return new FragmentPagerAdapter(getChildFragmentManager()) {
			@Override
			public Fragment getItem(int pos) {
				final Fragment fragment;

                switch (MainCons.ShopActivityTabMenu.values()[pos]) {
                    case MENU1: //업체정보
						final BeanShopSearch beanShopSearch = new BeanShopSearch();
						beanShopSearch.bmCategory_id = bmCategory_id;
                        fragment = ShopListFragment.newInstance(beanShopSearch); break;
                    default:
                        final BeanPostSearch beanPostSearch = new BeanPostSearch();
                        beanPostSearch.bmCategory_id = bmCategory_id;
                        beanPostSearch.owner_id = -1;

                        switch (MainCons.ShopActivityTabMenu.values()[pos]) {
                            case MENU2: //서비스사례 Post List
                                beanPostSearch.board_id = 3; break;
                            case MENU3: //고객리뷰 Post List
                                beanPostSearch.board_id = 4; break;
                            case MENU4: //이벤트 Post List
                                beanPostSearch.board_id = 5; break;
                            default: //새소식 Post List
                                beanPostSearch.board_id = -1; break;  //전체 게시판
                        }

                        fragment = PostListFragment.newInstance(beanPostSearch);
                }

				return fragment;
			}

			@Override
			public int getCount() {
				return MainCons.ShopActivityTabMenu.values().length;
			}

			@Override
			public CharSequence getPageTitle(int pos) {
				return MainCons.ShopActivityTabMenu.values()[pos].getText();
			}

			/*
			@Override
			public void setPrimaryItem(ViewGroup container, int pos, Object object) {
				super.setPrimaryItem(container, pos, object);
			}
			*/
		};
	}
}