package com.infoline.doctorcha.presentation.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.util.CommonUtil;
import com.infoline.doctorcha.core.util.SpinnerUtil;
import com.infoline.doctorcha.presentation.MainCons;
import com.infoline.doctorcha.presentation.activity.FragmentContainerActivity;
import com.infoline.doctorcha.presentation.bean.BeanShopSearch;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.infoline.doctorcha.core.util.CommonUtil.writeLog;
import static com.infoline.doctorcha.presentation.MainApp.beanMember;

public class SearchPanelOfShopsFragment extends Fragment {
    @BindView(R.id.sp_bmc)
    Spinner sp_bmc;
    @BindView(R.id.et_ca)
    EditText et_ca;
    @BindView(R.id.et_cn)
    EditText et_cn;
    @BindView(R.id.et_words)
    EditText et_words;

    private static BeanShopSearch beanShopSearch;

    @OnClick({R.id.tv_confirm})
    protected void onClick_BottomMenu(final View v) {
        String errMsg = null;

        beanShopSearch.ca = et_ca.getText().toString().trim();
        beanShopSearch.cn = et_cn.getText().toString().trim();
        beanShopSearch.words = et_words.getText().toString().trim();

        /*
        if(beanShopSearch.ca.length() == 0 && beanShopSearch.cn.length() == 0 && beanShopSearch.words.length() == 0) {
            errMsg = "소재지, 업체명, 해시태그 중 최소 1개 항목의 검색어가 필요합니다";
        } else {
            if(beanShopSearch.ca.length() == 1 || beanShopSearch.cn.length() == 1 || beanShopSearch.words.length() == 1) {
                errMsg = "검색어는 최소 2자리가 필요합니다";
            }
        }

        if(errMsg != null) {
            Toast.makeText(getActivity(), errMsg, Toast.LENGTH_LONG).show();
            return;
        }
        */

        saveToSharedPrefernces();

        final SpinnerUtil spinnerUtil = new SpinnerUtil();

        beanShopSearch.bmCategory_id = spinnerUtil.getBasicCode(sp_bmc);

        getActivity().finish();

        final Intent intent = new Intent(getActivity(), FragmentContainerActivity.class);
        intent.putExtra(MainCons.EnumExtraName.NAME1.name(), ShopListFragment.class.getSimpleName());
        intent.putExtra(BeanShopSearch.class.getSimpleName(), beanShopSearch);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
    }

    public SearchPanelOfShopsFragment() {

    }

    public static SearchPanelOfShopsFragment newInstance(BeanShopSearch beanShopSearch) {
        final SearchPanelOfShopsFragment fragment = new SearchPanelOfShopsFragment();

        final Bundle bundle = new Bundle();
        bundle.putSerializable(BeanShopSearch.class.getSimpleName(), beanShopSearch);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle bundle = getArguments();
        beanShopSearch = (BeanShopSearch)bundle.getSerializable(BeanShopSearch.class.getSimpleName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_searchpanelofshops, container, false);
        ButterKnife.bind(this, rootView);

        SpinnerUtil spinnerUtil = new SpinnerUtil();
        spinnerUtil.attachBasicCodeAdapter( sp_bmc, SpinnerUtil.CN_BASECODETYPE_BMCATEGORY, beanShopSearch.bmCategory_id, "전체");

        restoreFromSharedPrefernces();

        return rootView;
    }

    private void saveToSharedPrefernces() {
        final SharedPreferences.Editor editor = getActivity().getSharedPreferences(this.getClass().getSimpleName(), MODE_PRIVATE).edit();

        editor.putString("ca", beanShopSearch.ca);
        editor.putString("cn", beanShopSearch.cn);
        editor.putString("words", beanShopSearch.words);
        editor.apply();
    }
    
    private void restoreFromSharedPrefernces() {
        final SharedPreferences sp = getActivity().getSharedPreferences(this.getClass().getSimpleName(), MODE_PRIVATE);

        et_ca.setText(sp.getString("ca", ""));
        et_cn.setText(sp.getString("cn", ""));
        et_words.setText(sp.getString("words", ""));
    }
}