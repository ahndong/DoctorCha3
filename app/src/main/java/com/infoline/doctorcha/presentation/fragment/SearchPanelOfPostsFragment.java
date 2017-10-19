package com.infoline.doctorcha.presentation.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.infoline.doctorcha.presentation.bean.BeanPostSearch;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class SearchPanelOfPostsFragment extends Fragment {
    @BindView(R.id.sp_bmc)
    Spinner sp_bmc;
    @BindView(R.id.sp_board)
    Spinner sp_board;
    @BindView(R.id.et_words)
    EditText et_words;

    private static BeanPostSearch beanPostSearch;

    @OnClick({R.id.tv_confirm})
    protected void onClick_BottomMenu(final View v) {
        String errMsg = null;

        beanPostSearch.words = et_words.getText().toString().trim();

        /*
        if(beanPostSearch.words.length() == 0) {
            errMsg = "제목 또는 본문 검색어를 입력하세요";
        } else {
            if(beanPostSearch.words.length() == 1) {
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

        beanPostSearch.bmCategory_id = spinnerUtil.getBasicCode(sp_bmc);
        beanPostSearch.board_id = spinnerUtil.getBasicCode(sp_board);

        getActivity().finish();
        final Intent intent = new Intent(getActivity(), FragmentContainerActivity.class);
        intent.putExtra(MainCons.EnumExtraName.NAME1.name(), PostListFragment.class.getSimpleName());
        intent.putExtra(BeanPostSearch.class.getSimpleName(), beanPostSearch);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
    }

    public SearchPanelOfPostsFragment() {

    }

    public static SearchPanelOfPostsFragment newInstance(BeanPostSearch beanPostSearch) {
        final SearchPanelOfPostsFragment fragment = new SearchPanelOfPostsFragment();

        final Bundle bundle = new Bundle();
        bundle.putSerializable(BeanPostSearch.class.getSimpleName(), beanPostSearch);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle bundle = getArguments();
        beanPostSearch = (BeanPostSearch)bundle.getSerializable(BeanPostSearch.class.getSimpleName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_searchpanelofposts, container, false);
        ButterKnife.bind(this, rootView);

        SpinnerUtil spinnerUtil = new SpinnerUtil();
        spinnerUtil.attachBasicCodeAdapter( sp_bmc, SpinnerUtil.CN_BASECODETYPE_BMCATEGORY, beanPostSearch.bmCategory_id, "전체");
        spinnerUtil.attachBasicCodeAdapter( sp_board, SpinnerUtil.CN_BASECODETYPE_BOARD, beanPostSearch.board_id, "전체");

        restoreFromSharedPrefernces();

        return rootView;
    }

    private void saveToSharedPrefernces() {
        final SharedPreferences.Editor editor = getActivity().getSharedPreferences(this.getClass().getSimpleName(), MODE_PRIVATE).edit();

        editor.putString("words", beanPostSearch.words);
        editor.apply();
    }

    private void restoreFromSharedPrefernces() {
        final SharedPreferences sp = getActivity().getSharedPreferences(this.getClass().getSimpleName(), MODE_PRIVATE);

        et_words.setText(sp.getString("words", ""));
    }
}