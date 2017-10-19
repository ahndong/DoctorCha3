package com.infoline.doctorcha.presentation.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.infoline.doctorcha.R;
import com.infoline.doctorcha.presentation.MainCons;
import com.infoline.doctorcha.presentation.bean.BeanPost;
import com.infoline.doctorcha.presentation.bean.BeanPostSearch;
import com.infoline.doctorcha.presentation.bean.BeanShopSearch;
import com.infoline.doctorcha.presentation.fragment.CarInfoFragment;
import com.infoline.doctorcha.presentation.fragment.MemoListFragment;
import com.infoline.doctorcha.presentation.fragment.PushTargetListFragment;
import com.infoline.doctorcha.presentation.fragment.SendBirdCounsellorListFragment;
import com.infoline.doctorcha.presentation.fragment.EmptyFragment;
import com.infoline.doctorcha.presentation.fragment.MapViewFragment;
import com.infoline.doctorcha.presentation.fragment.PostEditOfChatForAccidentFragment;
import com.infoline.doctorcha.presentation.fragment.PostEditFragment;
import com.infoline.doctorcha.presentation.fragment.PostListFragment;
import com.infoline.doctorcha.presentation.fragment.PostReadFragment;
import com.infoline.doctorcha.presentation.fragment.PostReplFragment;
import com.infoline.doctorcha.presentation.fragment.SendBirdChannelListFragment;
import com.infoline.doctorcha.presentation.fragment.ShopListFragment;
import com.infoline.doctorcha.presentation.fragment.YoutubeVideoListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
1. 굳이 단독 Activity를 만들 필요가 없을 경우 Fragment로 작성 후 호출
2. 다른 Activity의 TabView의 일원으로 사용키 위하여 작성되었으나 독립적으로도 호출할 필요가 있는 Fragment를 위하여
3. 이 Activity에 impot하기 위해서는 가능한 모든 종속성 제거 후 사용
*/

public class FragmentContainerActivity extends AppCompatActivity {
    @BindView(R.id.tb) Toolbar tb;
    @BindView(R.id.fl_fragment)
    FrameLayout fl_fragment;

    String fragmentName;
    Fragment fragment;

    //복잡해서 간편한 편법으로
    private int replCount;

    public void replPlus(int x) {
        replCount += x;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragmentcontainer);
        ButterKnife.bind(this);

        final Intent intent = getIntent();
        fragmentName = intent.getStringExtra(MainCons.EnumExtraName.NAME1.name());
        final String title;

        final FragmentManager fm = getFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();

        if(fragmentName.equals(CarInfoFragment.class.getSimpleName())) {
            title = "차량정보";
            final MainCons.EnumFragmentOpenType enumFragmentOpenType = (MainCons.EnumFragmentOpenType)intent.getSerializableExtra(MainCons.EnumFragmentOpenType.class.getSimpleName());
            final int member_id = intent.getIntExtra(MainCons.EnumExtraName.ID.name(), 0);
            fragment = CarInfoFragment.newInstance(enumFragmentOpenType, member_id);
        } else if (fragmentName.equals(PostEditOfChatForAccidentFragment.class.getSimpleName())) {
            title = "사고수리 정보";
            final MainCons.EnumFragmentOpenType enumFragmentOpenType = (MainCons.EnumFragmentOpenType)intent.getSerializableExtra(MainCons.EnumFragmentOpenType.class.getSimpleName());
            final int post_id = intent.getIntExtra(MainCons.EnumExtraName.ID.name(), 0);
            fragment = PostEditOfChatForAccidentFragment.newInstance(enumFragmentOpenType, post_id); //내가 작성할 것이므로 특정 post_id 전달할 필요가 없다
        } else if (fragmentName.equals(PostEditFragment.class.getSimpleName())) {
            final int board_id = intent.getIntExtra("board_id", 9);
            final int owner_id = intent.getIntExtra("owner_id", 1);
            final String owner_cn = intent.getStringExtra("owner_cn");
            final BeanPost beanPost = (BeanPost)intent.getSerializableExtra("beanPost");

            title = beanPost == null ? "포스트 작성" : "포스트 수정";

            fragment = PostEditFragment.newInstance(board_id, owner_id, owner_cn, beanPost);
        }
        else if(fragmentName.equals(PostReplFragment.class.getSimpleName())) {
            title = "댓글";
            final BeanPost beanPost = (BeanPost)intent.getSerializableExtra("beanPost");
            fragment = PostReplFragment.newInstance(beanPost);
        }else if (fragmentName.equals(PostReadFragment.class.getSimpleName())) {
            title = "글 보기";
            final BeanPost beanPost = (BeanPost)intent.getSerializableExtra("beanPost");
            fragment = PostReadFragment.newInstance(beanPost);
        }  else if (fragmentName.equals(SendBirdChannelListFragment.class.getSimpleName())) {
            /* 필요할까?
            final MainCons.EnumSendBirdChannelListType enumSendBirdChannelListType;

            if(sr == 0) {
                enumSendBirdChannelListType = MainCons.EnumSendBirdChannelListType.COUNSEL;
            } else if(sr == 1) {
                enumSendBirdChannelListType = MainCons.EnumSendBirdChannelListType.COUNSELLOR;
            } else {
                enumSendBirdChannelListType = MainCons.EnumSendBirdChannelListType.PARTNER;
            }

            intent.putExtra(MainCons.EnumSendBirdChannelListType.class.getSimpleName(), enumSendBirdChannelListType);
            */
            title = "상담 채팅방 선택";
            final int sr = intent.getIntExtra(MainCons.EnumExtraName.NAME2.name(), 0);
            final String channelUrl = intent.getStringExtra("channelUrl");
            fragment = SendBirdChannelListFragment.newInstance(sr, channelUrl);
        } else if (fragmentName.equals(MapViewFragment.class.getSimpleName())) {
            title = "지도보기";
            final String addr = intent.getStringExtra(MainCons.EnumExtraName.NAME2.name());
            final String location = intent.getStringExtra(MainCons.EnumExtraName.NAME3.name());
            fragment = MapViewFragment.newInstance(addr, location);
        } else if (fragmentName.equals(SendBirdCounsellorListFragment.class.getSimpleName())) {
            final int sr = intent.getIntExtra("sr", 1);
            title = sr == 1 ? "상담사 초대" : "협력업체 초대";
            fragment = SendBirdCounsellorListFragment.newInstance(intent.getIntExtra("sr", 1), intent.getIntegerArrayListExtra(MainCons.EnumExtraName.ID_LIST.name()));
        } else if (fragmentName.equals(YoutubeVideoListFragment.class.getSimpleName())) {
            title = "자동차상식";
            fragment = YoutubeVideoListFragment.newInstance();
        } else if (fragmentName.equals(ShopListFragment.class.getSimpleName())) {
            title = "업체 검색 결과";
            final BeanShopSearch beanShopSearch = (BeanShopSearch)intent.getSerializableExtra(BeanShopSearch.class.getSimpleName());
            fragment = ShopListFragment.newInstance(beanShopSearch);
        } else if (fragmentName.equals(PushTargetListFragment.class.getSimpleName())) {
            title = "푸시 타겟 선택";
            final int member_id = intent.getIntExtra("member_id", 0);
            fragment = PushTargetListFragment.newInstance(member_id);
        } else if (fragmentName.equals(MemoListFragment.class.getSimpleName())) {
            title = "메모 리스트";
            final int target_id = intent.getIntExtra("target_id", 0);
            fragment = MemoListFragment.newInstance(target_id);
        } else if (fragmentName.equals(PostListFragment.class.getSimpleName())) {
            title = "포스트 검색 결과";
            final BeanPostSearch beanPostSearch = (BeanPostSearch)intent.getSerializableExtra(BeanPostSearch.class.getSimpleName());
            fragment = PostListFragment.newInstance(beanPostSearch);
        } else {
            title = "닥터차";
            fragment = EmptyFragment.newInstance();
        }

        tb.setTitle(title); //setSupportActionBar(tb) 보다 선행되어야 동작한다
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(fragment != null) {
            ft.replace(R.id.fl_fragment, fragment);
            ft.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //1. Home Key일 경우 Activity에서 일괄 처리
        //2. 나머지는 해당 Fragment에서 처리


        //1. return true; --> 모든 것이 종결된다. 즉 하우ㅏ Fragment의 onOptionsItemSelected()도 호출되지 않고 아무 행동동 안한다. 즉, 빠꾸도 안한다
        //2. return false; --> 모든 것이 종결된다. 즉 하우ㅏ Fragment의 onOptionsItemSelected()도 호출되지 않고 아무 행동동 안한다. 즉, 빠꾸도 안한다
        //3. return super.onOptionsItemSelected(item) --> 하위 Fragment에서 처리한 결과 값대로 동작한다

        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(fragmentName.equals(PostReplFragment.class.getSimpleName()) || fragmentName.equals(PostReadFragment.class.getSimpleName())) {
            final Intent intent = new Intent();
            intent.putExtra("replCount", replCount);
            setResult(Activity.RESULT_CANCELED, intent); //RESULT_CANCELED로 넘기는게 중요하다
        }

        finish();
    }

    @Override
    public void finish() {
        //1. 해당 Fragment에서 이미 intent를 주사하고 finish()를 호출한 경우
        //   그 intebt에 위 replCount를 추가하면 코딩이 논리정연해 지는데.....현재로서는 그 intent를 구할 수가 없슴
        //2. 따라서 PostEditFragment의 저장버튼 클릭 - PosteEadFragment의 onActivityResult()에서 자동으로 setResult()할 때
        //   해당 intent에 replCount를 추가하도록 한다.
        //   물론 beanPost의 coc에 값을 직접 지정할 수도 있지만 코드가 난해해 지므로 일관성을 유지하도록 한다

        //***아아아 안타깝게도

        //1. 중요 : 만약 finish() 내에서 onActivityResult()로 setResult로 intent를 전달할 경우 super.finish() 이전에 기술되어야 한다
        //2. 그렇지 않으면 intent는 null을 전달한다
        super.finish();

        overridePendingTransition(R.anim.activity_slide_in_left, R.anim.activity_slide_out_right);
    }
}