package com.infoline.doctorcha.presentation.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.LineHeightSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.listener.ViewOnClickListener;
import com.infoline.doctorcha.core.util.CommonUtil;
import com.infoline.doctorcha.presentation.MainApp;
import com.infoline.doctorcha.presentation.activity.YoutubePlayListActivity;
import com.infoline.doctorcha.presentation.bean.BeanYutubeVideo;
import com.infoline.doctorcha.presentation.viewholder.VhImage2Text2;
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

public class YoutubeVideoListFragment extends Fragment {
	@BindView(R.id.rv)
	RecyclerView rv;

	final String[] thumbnaiSizeArray = {"maxres", "standard", "high", "medium", "default"};

	@OnClick(R.id.iv_playlist)
	protected void onClick_01(final View v) {
		final Intent intent = new Intent(getActivity(), YoutubePlayListActivity.class);
		startActivityForResult(intent, 901);
		getActivity().overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
	}

	private MyRecyclerAdapter rva;
	private LinearLayoutManager lm;
	private List<BeanYutubeVideo> beanYutubeVideoList;
	private String playId = "PLH5C39LJH3GVKTuTE4dS49x5Xnmt7R18N"; //자동차상식 //"PLgHlOY1RAfgh4s7Be2H_7EwpuL_gpjy8Z" --> 인포라인 대표 Player
	private String nextPageToken;
	private boolean isItemLoading = false;

	public YoutubeVideoListFragment() {
		CommonUtil.writeLog(null);
	}

	public static YoutubeVideoListFragment newInstance() {
		CommonUtil.writeLog(null);
		final YoutubeVideoListFragment fragment = new YoutubeVideoListFragment();

		final Bundle bundle = new Bundle();

		//bundle.putSerializable(MainCons.EnumPostType.class.getSimpleName(), enumPostType);
		//fragment.setArguments(bundle);

		return fragment;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		CommonUtil.writeLog(null);
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		//viewpager fragment visibility check 방법1
		//1. onCreateView 이전에 Trigger된다???

		super.setUserVisibleHint(isVisibleToUser);

		CommonUtil.writeLog("isVisibleToUser = " + isVisibleToUser);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		CommonUtil.writeLog(null);
		View rootView = inflater.inflate(R.layout.fragment_youtubevideolist, container, false);
		ButterKnife.bind(this, rootView);

		rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				CommonUtil.writeLog(null);
				/*
				final View ll_bottom = getActivity().findViewById(R.id.ll_bottom);

				if (ll_bottom != null) {
					AnimUtil.playMenuAnim(ll_bottom, newState == RecyclerView.SCROLL_STATE_IDLE, getResources().getInteger(android.R.integer.config_longAnimTime), 0);
				}
				*/
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

		beanYutubeVideoList = new ArrayList<>();

		rva = new MyRecyclerAdapter(getActivity());
		lm = new LinearLayoutManager(getActivity());
		lm.setOrientation(LinearLayoutManager.VERTICAL);

		rv.setHasFixedSize(true);
		rv.setLayoutManager(lm);
		rv.setItemAnimator(new DefaultItemAnimator());
		rv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
		rv.setAdapter(rva);

		new LoadMoreTask(false).execute();

		return rootView;
	}

	@Override
	public void onResume() {
		//1. setOffscreenPageLimit에 의해 숨겨진 상태에서도
		//   this.isVisible() = true
		//   this.isHidden() = false
		//즉, Fragment가 FragmentStatePagerAdapter에서 사용될 경우 자신의 activate 상태를 체크하기위해 위 속성을 사용할 수 없다. - 도데체 머지
		//이해할 수 없다
		super.onResume();
		writeLog(null);
	}

	@Override
	public void onPause() {
		super.onPause();
		writeLog(null);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		writeLog(null);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 901) {
			if (resultCode == Activity.RESULT_OK) {
				((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(data.getStringExtra("playTitle"));

				playId = data.getStringExtra("playId");
				new LoadMoreTask(true).execute();
			}
		}
	}

	private class LoadMoreTask extends AsyncTask<Void, Void, Boolean> {
		final boolean refresh;

		LoadMoreTask(boolean refresh) {
			this.refresh = refresh;
			isItemLoading = true;

			if(refresh) {
				beanYutubeVideoList.clear();
				rva.notifyDataSetChanged();
				nextPageToken = null;
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				String url = String.format("https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&key=AIzaSyArvgZqAJkMMokOp0K6Vk5yTL_tJsyTuwM&playlistId=%s", playId);

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
					final JSONObject snippet = items.getJSONObject(i).getJSONObject("snippet");
					final JSONObject thumbnails = snippet.getJSONObject("thumbnails");
					String thumbnailUrl = "";

					for(String s : thumbnaiSizeArray) {
						if(thumbnails.has(s)) {
							thumbnailUrl = thumbnails.getJSONObject(s).getString("url");
							break;
						}
					}

					final String videoId = snippet.getJSONObject("resourceId").getString("videoId");
					//final JSONObject statistics = snippet.getJSONObject("statistics");
					//final BeanYutubeVideo beanYutubeVideo = new BeanYutubeVideo(item.getString("id"), snippet.getString("publishedAt"), snippet.getString("title"), snippet.getString("description"), thumbnails.getString("default"),
					//        statistics.getString("viewCount"), statistics.getString("likeCount"), statistics.getString("dislikeCount"), statistics.getString("favoriteCount"), statistics.getString("commentCount"));

					final String publishedAt = snippet.getString("publishedAt").substring(0, 19).replace("T", " ");
					final BeanYutubeVideo beanYutubeVideo = new BeanYutubeVideo(videoId, publishedAt, snippet.getString("title"), snippet.getString("description"), thumbnailUrl);

					beanYutubeVideoList.add(beanYutubeVideo);
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

	public class MyRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
		private final ImageLoader imageLoader = ImageLoader.getInstance();
		private final ViewOnClickListener viewOnClickListener;

		final private Context ctx;

		public MyRecyclerAdapter(final Context ctx) {
			//onAttachedToRecyclerView()에서 rv를 이용하여 Context를 구할 수 있으나 Attached 이전에
			//Context는 참조할 수 없으므로 인자로 전달 받는다.

			this.ctx =ctx;

			viewOnClickListener = new ViewOnClickListener(ctx, new ViewOnClickListener.OnClickListener() {
				@Override
				public void onViewClick(final View v, Point touchPoint, final Point rawTouchPoint) {
					final int pos = CommonUtil.getAdapterPositionFromView(rv, v);
					String url = "https://www.youtube.com/watch?v=" + beanYutubeVideoList.get(pos).videoId;
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					startActivity(intent);
				}

				@Override
				public void onViewLongPress(View v, Point touchPoint, Point rawTouchPoint) {

				}
			});
		}

		@Override
		public int getItemCount() {
			return beanYutubeVideoList.size();
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
			return new VhImage2Text2(LayoutInflater.from(ctx).inflate(R.layout.vh_youtube_video, vg, false));
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

			final BeanYutubeVideo beanYutubeVideo = beanYutubeVideoList.get(pos);

			final VhImage2Text2 vhImage2Text2 = (VhImage2Text2)vh;
			writeLog(beanYutubeVideo.thumbnail);

			imageLoader.displayImage("drawable://" + R.drawable.ic_launcher_red, vhImage2Text2.iv_31, MainApp.optionsForCircleThumb);
			imageLoader.displayImage(beanYutubeVideo.thumbnail, vhImage2Text2.iv_32, MainApp.optionsForBasic);

			final SpannableStringBuilder ssb = new SpannableStringBuilder();
			final SpannableStringBuilder ssb2 = new SpannableStringBuilder();

			SpannableString ss;
			String s;

			//제목
			//Color.parseColor("#000000")
			s = beanYutubeVideo.title;
			ss = new SpannableString(s);
			ss.setSpan(new AbsoluteSizeSpan(15, true), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			ss.setSpan(new ForegroundColorSpan(CommonUtil.getColor(ctx, android.R.color.black)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			ss.setSpan(new LineHeightSpan() {
				@Override
				public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
					fm.bottom += 10;
					fm.descent += 10;
				}
			}, 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			ssb.append(ss);

			//게시자 ID
			//Color.parseColor("#000000")
			s ="\nDoctorCha";
			ss = new SpannableString(s);
			ss.setSpan(new ForegroundColorSpan(CommonUtil.getColor(ctx, R.color.tc_ab_cv)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			ssb.append(ss);

			//게시일시
			//Color.parseColor("#000000")
			s =" " + beanYutubeVideo.publishedAt;
			ss = new SpannableString(s);
			//ss.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			ssb.append(ss);

			vhImage2Text2.tv_31.setText(ssb);
			vhImage2Text2.tv_32.setText(beanYutubeVideo.description);

			vhImage2Text2.itemView.setOnTouchListener(viewOnClickListener);

		}
	}
}