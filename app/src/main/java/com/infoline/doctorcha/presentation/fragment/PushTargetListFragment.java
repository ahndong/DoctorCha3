package com.infoline.doctorcha.presentation.fragment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.LineHeightSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.httphelper.ServiceGenerator;
import com.infoline.doctorcha.core.listener.EndlessScrollListener;
import com.infoline.doctorcha.core.util.AnimUtil;
import com.infoline.doctorcha.core.util.CommonUtil;
import com.infoline.doctorcha.presentation.MainApp;
import com.infoline.doctorcha.presentation.MainCons;
import com.infoline.doctorcha.presentation.RetrofitInterface;
import com.infoline.doctorcha.presentation.activity.SearchPanelActivity;
import com.infoline.doctorcha.presentation.activity.ShopActivity;
import com.infoline.doctorcha.presentation.bean.BeanErrResponse;
import com.infoline.doctorcha.presentation.bean.BeanPushTarget;
import com.infoline.doctorcha.presentation.bean.BeanShopSearch;
import com.infoline.doctorcha.presentation.viewholder.VhImage1Text1;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.infoline.doctorcha.presentation.MainApp.beanMember;

public class PushTargetListFragment extends Fragment {
	private MyRecyclerAdapter rva;
	private List<BeanPushTarget> beanPushTargetList;
	private int sr;
	private int member_id;
	List<Integer> posList = new ArrayList<>();

	@BindView(R.id.rv)
	RecyclerView rv;

	@OnClick({R.id.tv_confirm})
	protected void click1(View v) {
		final ArrayList<String> gtList = new ArrayList<>();

		for(int pos : posList) {
			final String gt = beanPushTargetList.get(pos).gt;

			gtList.add(gt);
		}

		if(gtList.size() == 0) {
			Toast.makeText(getActivity(), "전송할 타겟이 없습니다", Toast.LENGTH_SHORT).show();
		} else {
			final Intent intent = new Intent();
			intent.putStringArrayListExtra("gtList", gtList);
			getActivity().setResult(Activity.RESULT_OK, intent);
			getActivity().finish();
		}
	}

    public PushTargetListFragment() {

    }

    //public static ShopsForRepairFragment newInstance(MainCons.ViewHolderType viewHolderType) {
	public static PushTargetListFragment newInstance(final int member_id) {
		final PushTargetListFragment fragment = new PushTargetListFragment();

		final Bundle bundle = new Bundle();

		bundle.putSerializable("member_id", member_id);
		fragment.setArguments(bundle);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_pushtaargetlist, container, false);
        ButterKnife.bind(this, rootView);

		sr = Integer.parseInt(beanMember.sr);
		beanPushTargetList = new ArrayList<>();

		final Bundle bundle = getArguments();
		member_id = bundle.getInt("member_id");

		rva = new MyRecyclerAdapter();

		final LinearLayoutManager lm = new LinearLayoutManager(getActivity());

		rv.setHasFixedSize(true);
		rv.setLayoutManager(lm);
		rv.setItemAnimator(new DefaultItemAnimator());
		rv.setAdapter(rva);

		loadData();

		return rootView;
	}

	private void loadData() {
		final RetrofitInterface.PushTargetService service = ServiceGenerator.createService(RetrofitInterface.PushTargetService.class);
		final Call<List<BeanPushTarget>> call = service.selectByMemberId(member_id);
		call.enqueue(new Callback<List<BeanPushTarget>>() {
			@Override
			public void onResponse(Call<List<BeanPushTarget>> call, Response<List<BeanPushTarget>> response) {
				String msg = null;

				if(response.isSuccessful()) {
					beanPushTargetList.addAll(response.body());
					rva.notifyDataSetChanged();
				}
				else {
					final BeanErrResponse beanErrResponse = new BeanErrResponse(response.errorBody().source().toString()); //{ec:%s,em:'%s',sv:'%s'}

					//1. 잘못된 nn 또는 upw 이외의 원인으로 인한 오류 메세지
					//2. 404 같은 경우에는 beanErrResponse 자체가 null이다
					msg = beanErrResponse.em == null ? String.format("[%s]%s", response.code(), response.message()) : beanErrResponse.em;
				}

				if(msg != null) {
					Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onFailure(Call<List<BeanPushTarget>> call, Throwable t) {
				CommonUtil.writeLog(ServiceGenerator.getExceptionMsgByCause(t));
			}
		});
	}

	public class MyRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
		private final ImageLoader imageLoader = ImageLoader.getInstance();
		private final DisplayImageOptions options;
		private final Context ctx;

		public MyRecyclerAdapter() {
			this.ctx = getActivity();
			options = new DisplayImageOptions.Builder()
					.cacheInMemory(true)
					.cacheOnDisk(true)
					.considerExifParams(true)
					.showImageOnFail(R.drawable.shop)
					.build();
		}

		public void onClick(View v) {
			final int pos = CommonUtil.getAdapterPositionFromView(rv, v);
			final BeanPushTarget beanMemberAndShop = beanPushTargetList.get(pos);

			if(posList.contains(pos)) {
				//1. posList.remove(pos); - 위치로 인식
				//2. posList.remove(posList.indexOf(beanMemberAndShop.id)); --> 아래와 동일하게 정상적으로 처리된다 : 값으로 인식
				posList.remove((Integer)pos);
			} else {
				if(TextUtils.isEmpty(beanMemberAndShop.gt)) {
					Toast.makeText(getActivity(), "메세지를 전송할 디바이스 토큰이 없습니다", Toast.LENGTH_LONG).show();
				} else {
					posList.add(pos);
				}
			}

			notifyItemChanged(pos);
		}

		@Override
		public int getItemCount() {
			return beanPushTargetList.size();
		}

		@Override
		public long getItemId(int pos) {
			//ViewHolder로 상속된다.
			return beanPushTargetList.get(pos).id;
		}

		public void animate(RecyclerView.ViewHolder viewHolder) {
			final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(ctx, R.anim.rv_scroll_anim_overshoot);
			viewHolder.itemView.setAnimation(animAnticipateOvershoot);
		}

		@Override
        public void onViewDetachedFromWindow (RecyclerView.ViewHolder vh) {
			//super.onViewDetachedFromWindow(vh);
			vh.itemView.clearAnimation();
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup vg, int viewHolderType) {
			final VhImage1Text1 vhImage1Text1 = new VhImage1Text1(LayoutInflater.from(ctx).inflate(R.layout.vh_shop_thumbnail, vg, false));
			vhImage1Text1.itemView.setOnClickListener(this);

			return vhImage1Text1;
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder vh, int pos) {
			//final Animation anim = AnimationUtils.loadAnimation(ctx, R.anim.rv_scroll_anim_overshoot);

			final BeanPushTarget beanMemberAndShop = beanPushTargetList.get(pos);
			final SpannableStringBuilder ssb = new SpannableStringBuilder();

			SpannableString ss;
			String s;

			final VhImage1Text1 vhImage1Text1 = (VhImage1Text1)vh;

			//imageLoader.displayImage(MainCons.EnumPath.SHOP_IMAGE.getText() + "pic_" + String.format("%03d", post.getId()) + ".jpg", vhImage1Text1.iv_31 , MainApp.optionsForRectThumb);
			final String ifn = beanMemberAndShop.ifn.isEmpty() ? "ifn_s.jpg" : beanMemberAndShop.ifn;
			final String fileName = CommonUtil.getFileName(ifn); //순수 파일명
			final String thumbFileNameWithExt = fileName.concat("_200x200.jpg");

			imageLoader.displayImage(MainCons.EnumContentPath.MEMBER_I.getPath() + thumbFileNameWithExt, vhImage1Text1.iv_31, MainApp.optionsForRectThumb);

			//tx1 기본 style textSize:13sp textColor:#666666

			//제목
			//Color.parseColor("#000000")
			s = beanMemberAndShop.cn + (beanMember.id == 2 ? " (" + Integer.toString(beanMemberAndShop.id) + ")" : "");
			ss = new SpannableString(s);
			ss.setSpan(new ForegroundColorSpan(CommonUtil.getColor(ctx, android.R.color.black)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			ss.setSpan(new AbsoluteSizeSpan(15, true), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			//ss.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			ssb.append(ss);

			//주소 = default Color = tc_bg_cv
			s ="\n" + beanMemberAndShop.ca + " ";
			ss = new SpannableString(s);
			ss.setSpan(new LineHeightSpan() {
				@Override
				public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
					fm.top += 10;
					fm.ascent -= 10;
				}
			}, 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			ssb.append(ss);

			/*
			//거리
			s = "  0.6km";
			ss = new SpannableString(s);
			ss.setSpan(new ForegroundColorSpan(CommonUtil.getColor(ctx, R.color.tc_ar_cv)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			ssb.append(ss);
			*/

			//해쉬태그
			s = "\n" + beanMemberAndShop.ht;
			ss = new SpannableString(s);
			ss.setSpan(new ForegroundColorSpan(CommonUtil.getColor(ctx, R.color.tc_ab_cv)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

			ssb.append(ss);

			vhImage1Text1.tv_31.setText(ssb);

			vhImage1Text1.itemView.setPressed(posList.contains(pos)); //이미 대화에 참여중인 상담사 또는 업체
		}
	}
}