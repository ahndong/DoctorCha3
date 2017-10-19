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
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.listener.ViewOnClickListener;
import com.infoline.doctorcha.core.util.AnimUtil;
import com.infoline.doctorcha.core.util.CommonUtil;
import com.infoline.doctorcha.core.view.RevealForegroundView;
import com.infoline.doctorcha.presentation.MainApp;
import com.infoline.doctorcha.presentation.MainCons;
import com.infoline.doctorcha.presentation.bean.BeanMemberAndShop;
import com.infoline.doctorcha.presentation.bean.BeanYutubePlay;
import com.infoline.doctorcha.presentation.viewholder.VhImage1Text1;
import com.infoline.doctorcha.presentation.viewholder.VhImage1Text1;
import com.infoline.doctorcha.presentation.viewholder.VhImage2Text2_stats;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.infoline.doctorcha.core.util.CommonUtil.writeLog;
import static com.infoline.doctorcha.presentation.util.AppUtil.getBeanResponseFromJson;

public class YoutubePlayListActivity extends AppCompatActivity {
    @BindView(R.id.cl)
    CoordinatorLayout cl;
    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.rv)
    RecyclerView rv;

    private MyRecyclerAdapter rva;
    private LinearLayoutManager lm;
    private List<BeanYutubePlay> beanYutubePlayList;
    private String nextPageToken;
    private boolean isItemLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CommonUtil.writeLog(null);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_youtubeplaylist);
        ButterKnife.bind(this);

        tb.setTitle("동영상 카테고리"); //setSupportActionBar(tb) 보다 선행되어야 동작한다
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

                int itemCount = lm.getItemCount();
                int lastVisiblePos = lm.findLastVisibleItemPosition();

                if (!isItemLoading && nextPageToken != null && lastVisiblePos == itemCount - 1) {
                    new LoadMoreTask(false).execute();
                }
            }
        });

        beanYutubePlayList = new ArrayList<>();

        rva = new MyRecyclerAdapter();
        lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);

        rv.setHasFixedSize(true);
        rv.setLayoutManager(lm);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rv.setAdapter(rva);

        new LoadMoreTask(false).execute();
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
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_slide_in_left, R.anim.activity_slide_out_right);
    }

    /*
    private void loadNextPlayList() {
        final RetrofitInterface.YoutubeService youtubeService = ServiceGenerator.createYoutubeService(RetrofitInterface.YoutubeService.class);
        final Call<JSONObject> call = youtubeService.videos("AIzaSyArvgZqAJkMMokOp0K6Vk5yTL_tJsyTuwM", "PLgHlOY1RAfgh4s7Be2H_7EwpuL_gpjy8Z", "snippet");

        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                writeLog(null);
                String msg = null;

                if(response.code() == 200) {
                    JSONObject jo = response.body();

                    if(jo == null) {
                        writeLog("fefe");
                    }
                }
                else {

                }

                if(msg != null) {
                    writeLog(msg);
                    Toast.makeText(YoutubeVideoPlayListActivity.this, msg, Toast.LENGTH_LONG).show();
                }

                //pd.dismiss();
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                final String msg = ServiceGenerator.getExceptionMsgByCause(t);
                writeLog(msg);
                Toast.makeText(YoutubeVideoPlayListActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
    */

    private class LoadMoreTask extends AsyncTask<Void, Void, Boolean> {
        final boolean refresh;

        LoadMoreTask(boolean refresh) {
            this.refresh = refresh;
            isItemLoading = true;
            CommonUtil.writeLog(null);

            if(refresh) {
                beanYutubePlayList.clear();
                nextPageToken = null;
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                //1. 인포라인 UCvPUsrGWocZR2QLloP_NW-A
                //2. 정감독   UCHb6HELbsSS2cacTDVf2qgA
                //String url = String.format("https://www.googleapis.com/youtube/v3/playlists?part=snippet&maxResults=50&key=AIzaSyArvgZqAJkMMokOp0K6Vk5yTL_tJsyTuwM&channelId=UCvPUsrGWocZR2QLloP_NW-A");
                String url = String.format("https://www.googleapis.com/youtube/v3/playlists?part=snippet&maxResults=50&key=AIzaSyArvgZqAJkMMokOp0K6Vk5yTL_tJsyTuwM&channelId=UCHb6HELbsSS2cacTDVf2qgA");

                if(!(refresh || nextPageToken == null)) url += "&pageToken=" + nextPageToken;

                //sync
                final JSONObject jo = new JSONObject(IOUtils.toString(new URL(url), "UTF-8"));

                if(jo.has("nextPageToken")) {
                    nextPageToken = jo.getString("nextPageToken");
                }
                else {
                    nextPageToken = null;
                }

                final JSONArray items = jo.getJSONArray("items");
                final int itemCount = items.length();

                for(int i = 0; i < itemCount; i++) {
                    try {
                        final JSONObject item = items.getJSONObject(i);
                        final JSONObject snippet = item.getJSONObject("snippet");

                        /////final String thumbnails = snippet.getJSONObject("thumbnails").getJSONObject("maxres").getString("url");
                        final String id = item.getString("id");
                        final String publishedAt = snippet.getString("publishedAt").substring(0, 19).replace("T", " ");
                        final BeanYutubePlay beanYutubePlay = new BeanYutubePlay(id, publishedAt, snippet.getString("title"), snippet.getString("description"), "");

                        final JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                        beanYutubePlay.thumbnail = thumbnails.getJSONObject("medium").getString("url");

                        beanYutubePlayList.add(beanYutubePlay);
                    } catch (Exception e2) {

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(success) rva.notifyDataSetChanged();
            isItemLoading = false;
        }

        @Override
        protected void onCancelled() {
            isItemLoading = false;
        }
    }

    private class MyRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final ImageLoader imageLoader = ImageLoader.getInstance();

        final private Context ctx = YoutubePlayListActivity.this;

        public MyRecyclerAdapter() {

        }

        @Override
        public int getItemCount() {
            return beanYutubePlayList.size();
        }

        @Override
        public long getItemId(int pos) {
            //ViewHolder로 상속된다.
            return pos;
        }

        public void animate(RecyclerView.ViewHolder viewHolder) {
            final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(ctx, R.anim.rv_scroll_anim_overshoot);
            viewHolder.itemView.setAnimation(animAnticipateOvershoot);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup vg, int postType) {
            final View.OnClickListener ocl = new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final int pos = CommonUtil.getAdapterPositionFromView(rv, v);
                    final Intent intent = new Intent();
                    intent.putExtra("playId", beanYutubePlayList.get(pos).id);
                    intent.putExtra("playTitle", beanYutubePlayList.get(pos).title);
                    YoutubePlayListActivity.this.setResult(Activity.RESULT_OK, intent);
                    YoutubePlayListActivity.this.finish();


                }
            };

            final RecyclerView.ViewHolder vh = new VhImage1Text1(LayoutInflater.from(ctx).inflate(R.layout.vh_youtube_play, vg, false));
            vh.itemView.setOnClickListener(ocl);

            return vh;
        }

        @Override public void onViewAttachedToWindow (RecyclerView.ViewHolder vh) {

        }

        @Override public void onViewDetachedFromWindow (RecyclerView.ViewHolder vh) {
            //super.onViewDetachedFromWindow(vh);
            vh.itemView.clearAnimation();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder vh, int pos) {
            //final Animation anim = AnimationUtils.loadAnimation(ctx, R.anim.rv_scroll_anim_overshoot);

            final BeanYutubePlay beanYutubePlay = beanYutubePlayList.get(pos);

            final VhImage1Text1 vhImage1Text1 = (VhImage1Text1)vh;

            imageLoader.displayImage(beanYutubePlay.thumbnail, vhImage1Text1.iv_31, MainApp.optionsForRectThumb);

            final SpannableStringBuilder ssb = new SpannableStringBuilder();

            SpannableString ss;
            String s;

            //제목
            //Color.parseColor("#000000")
            s =  beanYutubePlay.title;
            ss = new SpannableString(s);
            ss.setSpan(new ForegroundColorSpan(CommonUtil.getColor(ctx, android.R.color.black)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new AbsoluteSizeSpan(15, true), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //ss.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append(ss);

            //주소 = default Color = tc_bg_cv
            s ="\nDoctorCha";
            ss = new SpannableString(s);
            ss.setSpan(new LineHeightSpan() {
                @Override
                public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
                    fm.top += 10;
                    fm.ascent -= 10;
                }
            }, 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append(ss);

            //게시일시
            //Color.parseColor("#000000")
            s =" " + beanYutubePlay.publishedAt;
            ss = new SpannableString(s);
            //ss.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append(ss);

            vhImage1Text1.tv_31.setText(ssb);
        }
    }
}