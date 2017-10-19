/*
//1. jinsmama@google.com tejin030745
//2. channel id = UCvPUsrGWocZR2QLloP_NW-A
//3. channel name = 인포라인

//1. 내 챈널로 요청 - 뭘 요청한거지
//2. 동영상 게시 후 요청해도 아무것도 안나옴 - 오류 아님
//https://www.googleapis.com/youtube/v3/search?order=date&part=snippet&channelId=UCvPUsrGWocZR2QLloP_NW&key=AIzaSyArvgZqAJkMMokOp0K6Vk5yTL_tJsyTuwM

//한개의 동영상을 지정하여 요청 - 성공
//https://www.googleapis.com/youtube/v3/videos?id=fOqDksO9i78&key=AIzaSyArvgZqAJkMMokOp0K6Vk5yTL_tJsyTuwM&part=snippet,contentDetails,statistics,status

//특정챈널에 있는 playlists(재생목록) - 성공
//https://www.googleapis.com/youtube/v3/playlists?part=contentDetails&key=AIzaSyArvgZqAJkMMokOp0K6Vk5yTL_tJsyTuwM&channelId=UCvPUsrGWocZR2QLloP_NW-A

//특정 playlist에 있는 vide목록 - 성공
//https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&key=AIzaSyArvgZqAJkMMokOp0K6Vk5yTL_tJsyTuwM&playlistId=PLgHlOY1RAfgh4s7Be2H_7EwpuL_gpjy8Z
 */

package com.infoline.doctorcha.presentation.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.LineHeightSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.httphelper.ServiceGenerator;
import com.infoline.doctorcha.core.listener.ViewOnClickListener;
import com.infoline.doctorcha.core.util.AnimUtil;
import com.infoline.doctorcha.core.util.CommonUtil;
import com.infoline.doctorcha.core.util.SoundSearcher;
import com.infoline.doctorcha.core.view.RevealForegroundView;
import com.infoline.doctorcha.presentation.MainApp;
import com.infoline.doctorcha.presentation.MainCons;
import com.infoline.doctorcha.presentation.RetrofitInterface;
import com.infoline.doctorcha.presentation.bean.BeanCarKind;
import com.infoline.doctorcha.presentation.bean.BeanCarOption;
import com.infoline.doctorcha.presentation.bean.BeanCollapsableItem;
import com.infoline.doctorcha.presentation.bean.BeanResponse;
import com.infoline.doctorcha.presentation.bean.BeanSimpleItem;
import com.infoline.doctorcha.presentation.bean.BeanYutubePlay;
import com.infoline.doctorcha.presentation.viewholder.VhText1;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.infoline.doctorcha.core.util.CommonUtil.writeLog;
import static com.infoline.doctorcha.presentation.util.AppUtil.getBeanResponseFromJson;

public class SimpleSelectActivity extends AppCompatActivity {
    @BindView(R.id.cl)
    CoordinatorLayout cl;
    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.et_search)
    EditText et_search;

    private MyRecyclerAdapter rva;
    private LinearLayoutManager lm;
    private List<BeanCarKind> beanCarKindList;
    private List<BeanCarKind> beanCarKindFilterList = new ArrayList<>();
    private List<BeanSimpleItem> beanSimpleItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CommonUtil.writeLog(null);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simpleselect);
        ButterKnife.bind(this);

        //final Intent intent = getIntent();
        //final int id = intent.getIntExtra(MainCons.EnumExtraName.class.getSimpleName(), 1);

        tb.setTitle("차종 선택"); //setSupportActionBar(tb) 보다 선행되어야 동작한다
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        //------------------------------------------------------------------------------------------

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                
            }

            @Override
            public void onScrolled(RecyclerView _rv, int dx, int dy) {
                CommonUtil.writeLog(null);

                /*
                int itemCount = lm.getItemCount();
                int lastVisiblePos = lm.findLastVisibleItemPosition();

                if (!isItemLoading && nextPageToken != null && lastVisiblePos == itemCount - 1) {
                    new LoadMoreTask(false).execute();
                }
                */
            }
        });

        beanCarKindList = new ArrayList<>();
        

        rva = new MyRecyclerAdapter();
        lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);

        rv.setHasFixedSize(true);
        rv.setLayoutManager(lm);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(rva);

        RetrofitInterface.CarKindService service = ServiceGenerator.createService(RetrofitInterface.CarKindService.class);

        Call<List<BeanCarKind>> call = service.select();
        call.enqueue(new Callback<List<BeanCarKind>>() {
            @Override
            public void onResponse(Call<List<BeanCarKind>> call, Response<List<BeanCarKind>> response) {
                String msg = null;

                if(response.isSuccessful()) {
                    beanCarKindList = response.body();
                    beanCarKindFilterList.addAll(beanCarKindList);
                    rva.notifyDataSetChanged();
                }
                else {
                    msg = response.errorBody().source().toString();
                }

                if(msg != null) {
                    CommonUtil.writeLog(msg);
                }
            }

            @Override
            public void onFailure(Call<List<BeanCarKind>> call, Throwable t) {
                CommonUtil.writeLog(ServiceGenerator.getExceptionMsgByCause(t));
            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                beanCarKindFilterList.clear();

                if(s.length() == 0) {//텍스트가 다 지워질때는 전체 목록을 보여준다
                    beanCarKindFilterList.addAll(beanCarKindList);
                }else {
                    final String keyWord= s.toString();

                    for(int i = 0; i < beanCarKindList.size(); i++){
                        final BeanCarKind beanCarKind = beanCarKindList.get(i);
                        final String searchData = beanCarKind.c1 + " " + beanCarKind.c2 + " " + beanCarKind.c3 + " " + beanCarKind.c4 + " " + beanCarKind.c5 + " " + beanCarKind.c6 + " " + beanCarKind.c7;

                        if(SoundSearcher.matchString(searchData, keyWord.toUpperCase())){
                            beanCarKindFilterList.add(beanCarKind);//검색대상에 있으면 새로운 리스트를 만들어서 이름을 애드해준다
                        }
                    }
                }
                rva.notifyDataSetChanged();//검색이 끝나면 새로운 리스트로 리스트뷰를 갱신해준다
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onResume() {
        CommonUtil.writeLog(null);
        super.onResume();
    }

    @Override
    public void onPause() {
        CommonUtil.writeLog(null);
        super.onPause();
    }

    @Override
    public void onStop() {
        CommonUtil.writeLog(null);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        CommonUtil.writeLog(null);
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_slide_in_left, R.anim.activity_slide_out_right);
    }

    private class MyRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        final private Context ctx = SimpleSelectActivity.this;

        public MyRecyclerAdapter() {

        }

        @Override
        public int getItemCount() {
            return beanCarKindFilterList.size();
        }

        @Override
        public long getItemId(int pos) {
            //ViewHolder로 상속된다.
            return pos;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup vg, int postType) {
            final VhText1 vh;
            vh = new VhText1(LayoutInflater.from(ctx).inflate(R.layout.vh_simpleselect_text, vg, false));

            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final BeanCarKind beanCarKind = beanCarKindFilterList.get(vh.getAdapterPosition());
                    final Intent intent = new Intent();
                    intent.putExtra(MainCons.EnumExtraName.NAME1.name(), beanCarKind.c1 + " " + beanCarKind.c4); //모델
                    intent.putExtra(MainCons.EnumExtraName.NAME2.name(), beanCarKind.c5); //등급
                    intent.putExtra(MainCons.EnumExtraName.NAME3.name(), beanCarKind.c6); //연료
                    intent.putExtra(MainCons.EnumExtraName.NAME4.name(), beanCarKind.c7); //배기량
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });

            return vh;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder vh, int pos) {
            final BeanCarKind beanCarKind = beanCarKindFilterList.get(pos);

            final VhText1 vhText1 = (VhText1)vh;

            vhText1.tv_31.setBackgroundResource(pos%2 == 0 ? R.color.select_even_backcolor : R.color.select_odd_backcolor );
            vhText1.tv_31.setText(beanCarKind.c1 + " " + beanCarKind.c4 + " " + beanCarKind.c5 + " " + beanCarKind.c6+ " " + beanCarKind.c7 + "cc");
        }
    }
}