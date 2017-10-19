package com.infoline.doctorcha.presentation.fragment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.httphelper.ServiceGenerator;
import com.infoline.doctorcha.core.listener.EndlessScrollListener;
import com.infoline.doctorcha.core.util.AnimUtil;
import com.infoline.doctorcha.core.util.CommonUtil;
import com.infoline.doctorcha.core.view.RevealForegroundView;
import com.infoline.doctorcha.presentation.MainApp;
import com.infoline.doctorcha.presentation.MainCons;
import com.infoline.doctorcha.presentation.RetrofitInterface;
import com.infoline.doctorcha.presentation.activity.ShopActivity;
import com.infoline.doctorcha.presentation.bean.BeanMemberAndShop;
import com.infoline.doctorcha.presentation.viewholder.VhImage1Text1;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendBirdCounsellorListFragment extends Fragment {
    //1. UsedCarListFragment에서 static으로 선언하면 UsedCarListFragment를 사용하는 전체 Activity에서 동일 ViewHolderType이 적용된다.
	private MyRecyclerAdapter rva;
	private ArrayList<String> selectedIdList = new ArrayList<>();
	private EndlessScrollListener endlessScrollListener;
	private final int limitCount = 50;
	private boolean loadMore = true;
	int sr;
	List<Integer> idList;

	private Handler mHandler;
	private Runnable mRunnable;

    @BindView(R.id.rv)
	RecyclerView rv;

	/*
	현재 다중선택은 못하도록 한다. 차후 업무 변화에 따라서 조정될 수 있다
	@OnClick({R.id.tv_confirm})
	protected void onClick_1(final View v) {
		final Intent intent = new Intent();
		intent.putStringArrayListExtra(MainCons.EnumExtraName.NAME1.name(), selectedIdList);
		getActivity().setResult(Activity.RESULT_OK, intent);
		getActivity().finish();
	}
	*/

    public SendBirdCounsellorListFragment() {

    }

    public static SendBirdCounsellorListFragment newInstance(final int sr, final ArrayList<Integer> idList) {
		//1. idList : 현재 채팅방 멤버 id list ==> ,1,2,3,4,

		final SendBirdCounsellorListFragment fragment = new SendBirdCounsellorListFragment();

		final Bundle bundle = new Bundle();

		bundle.putInt("sr", sr);
        bundle.putIntegerArrayList(MainCons.EnumExtraName.ID_LIST.name(), idList);
        fragment.setArguments(bundle);

		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_sendbirdcounsellorlist, container, false);
        ButterKnife.bind(this, rootView);

		final Bundle bundle = getArguments();
		sr = bundle.getInt("sr");
		idList = bundle.getIntegerArrayList(MainCons.EnumExtraName.ID_LIST.name());

		rva = new MyRecyclerAdapter(getActivity());
		final LinearLayoutManager lm = new LinearLayoutManager(getActivity());
		lm.setOrientation(LinearLayoutManager.VERTICAL);

		/*
		rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				//AnimUtil.playMenuAnim(tv_confirm, newState == RecyclerView.SCROLL_STATE_IDLE, getResources().getInteger(android.R.integer.config_longAnimTime), 0);

				if(ll_help_popup.getVisibility() == View.VISIBLE) {
					AnimUtil.togglePopup(ll_help_popup, false);
				}
			}
		});

		ll_help_popup.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				//1. resume보다 늦게 trigger된다.
				//2. 일단, fragment load시의 animation을 여기서 처리하도록 한다 - 별 이상 없다
				ll_help_popup.getViewTreeObserver().removeOnPreDrawListener(this);

				//int test = ll_help_popup.getHeight();
				//ll_help_popup.setTranslationY(-ll_help_popup.getHeight());
				//ll_help_popup.setVisibility(View.GONE);
				AnimUtil.togglePopup(ll_help_popup, true);
				return true;
			}
		});
		*/

		endlessScrollListener = new EndlessScrollListener(lm) {
			@Override
			public void onLoadMore(int pageNo, int totalItemsCount, RecyclerView view) {
				// Triggered only when new data needs to be appended to the list
				// Add whatever code is needed to append new items to the bottom of the list
				if(loadMore) {
					loadData(pageNo + 1);
				}
			}
		};
		// Adds the scroll listener to RecyclerView
		rv.addOnScrollListener(endlessScrollListener);

		rv.setHasFixedSize(true);
		rv.setLayoutManager(lm);
		rv.setItemAnimator(new DefaultItemAnimator());
		rv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        rv.setAdapter(rva);

		loadData(1);

		return rootView;
	}

	private void loadData(int pageNo) {
		final RetrofitInterface.MemberService service = ServiceGenerator.createService(RetrofitInterface.MemberService.class);
		final Call<List<BeanMemberAndShop>> call = service.selectBySr(String.valueOf(sr), (pageNo - 1) * limitCount, limitCount);
		call.enqueue(new Callback<List<BeanMemberAndShop>>() {
			@Override
			public void onResponse(Call<List<BeanMemberAndShop>> call, Response<List<BeanMemberAndShop>> response) {
				String msg = null;

				if(response.isSuccessful()) {
					rva.beanMemberAndShopList.addAll(response.body());
					rva.notifyDataSetChanged();

					if(response.body().size() < limitCount) loadMore = false;
				}
				else {
					msg = response.errorBody().source().toString();
				}

				if(msg != null) {
					Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onFailure(Call<List<BeanMemberAndShop>> call, Throwable t) {
				CommonUtil.writeLog(ServiceGenerator.getExceptionMsgByCause(t));
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();

		/*
		mRunnable = new Runnable() {
			@Override
			public void run() {
				AnimUtil.togglePopup(ll_help_popup, false);
			}
		};

		mHandler = new Handler();
		mHandler.postDelayed(mRunnable, 5000);
		*/
	}

	public class MyRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
		private final ImageLoader imageLoader = ImageLoader.getInstance();

		final private List<BeanMemberAndShop> beanMemberAndShopList;
		final private Context ctx;

		public MyRecyclerAdapter(final Context ctx) {
			//onAttachedToRecyclerView()에서 rv를 이용하여 Context를 구할 수 있으나 Attached 이전에
			//Context는 참조할 수 없으므로 인자로 전달 받는다.

			this.ctx =ctx;
			this.beanMemberAndShopList = new ArrayList<>();
		}

		@Override
		public int getItemCount() {
			return beanMemberAndShopList.size();
		}

		@Override
		public long getItemId(int pos) {
			//ViewHolder로 상속된다.
			return beanMemberAndShopList.get(pos).id;
		}

		@Override
		public int getItemViewType(int pos) {
			return 0;
		}

		public void animate(RecyclerView.ViewHolder viewHolder) {
			final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(ctx, R.anim.rv_scroll_anim_overshoot);
			viewHolder.itemView.setAnimation(animAnticipateOvershoot);
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup vg, int viewHolderType) {
			final VhImage1Text1 vh = new VhImage1Text1(LayoutInflater.from(ctx).inflate(R.layout.vh_counsellor_thumbnail, vg, false));

			final View.OnClickListener ocl = new View.OnClickListener() {
				@Override
				public void onClick(final View v) {
					final int pos = vh.getAdapterPosition();
					final BeanMemberAndShop beanMemberAndShop = beanMemberAndShopList.get(pos);

					if(v.getId() == R.id.iv_31) {
						final RevealForegroundView rfv = RevealForegroundView.createRevealForegroundView(ctx, v, Color.parseColor("#35000000"));
						final Intent intent = new Intent(getActivity(), ShopActivity.class);

						intent.putExtra(MainCons.EnumExtraName.ANIM_START_POINT.name(), CommonUtil.getCenterPointFromView(v));

						rfv.setListener(new RevealForegroundView.AnimationEndListener() {
							public void AnimationEnd() {
								intent.putExtra(MainCons.EnumExtraName.class.getSimpleName(), beanMemberAndShopList.get(pos).id);

								MainCons.EnumActivityAnimType enumActivityAnimType;

								if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
									final String transitionName = MainCons.EnumTransitionName.THUMB.name();

									v.setTransitionName(transitionName);
									enumActivityAnimType = MainCons.EnumActivityAnimType.ANIM_SCENE_TRANSITION;

									intent.putExtra(MainCons.EnumTransitionName.class.getSimpleName(), transitionName);
									//makeClipRevealAnimation --> added v23
									//makeTaskLaunchBehind --> added v21

									ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity)ctx, v, transitionName); //이 경우 import android.util.Pair 사용
									intent.putExtra(MainCons.EnumActivityAnimType.class.getSimpleName(), enumActivityAnimType);
									ctx.startActivity(intent, options.toBundle());
								}
								else {
									enumActivityAnimType = MainCons.EnumActivityAnimType.ANIM_THUMBNAIL_SCALE_UP;
								}
							}
						});
					} else {
						/*
						final String id_s = String.valueOf(beanMemberAndShop.id);
						if(selectedIdList.contains(id_s)) {
							selectedIdList.remove(selectedIdList.indexOf(id_s));
						} else {
							selectedIdList.add(id_s);
						}

						notifyItemChanged(pos);
						*/

						if(idList.contains(beanMemberAndShop.id)) {
							Toast.makeText(getActivity(), "이미 상담에 참여중입니다", Toast.LENGTH_LONG).show();
							notifyItemChanged(pos);
						} else {
							final Intent intent = new Intent();
							intent.putExtra(MainCons.EnumExtraName.ID.name(), String.valueOf(beanMemberAndShop.id));
							intent.putExtra(MainCons.EnumExtraName.NN.name(), beanMemberAndShop.nn);
							getActivity().setResult(Activity.RESULT_OK, intent);
							getActivity().finish();
						}
					}
				}
			};

			CommonUtil.assignOnClickListener(new View[] {vh.iv_31, vh.itemView}, ocl);

			return vh;
		}

		@Override public void onViewAttachedToWindow (RecyclerView.ViewHolder vh) {
			// vh.itemView.clearAnimation();

        /*
        Log.d("======>", "onViewAttachedToWindow : " + vh.getAdapterPosition() + "");
        final View animView = vh.itemView;

        //animView.setTranslationY(vh.itemView.getHeight() * (pos >= mLastPos ? 1 : -1));
        //animView.setScaleX(0.7f);
        animView.setScaleY(0.0f);
        animView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Log.d("getMeasuredHeight===>", animView.getMeasuredHeight() + "");
        Log.d("getHeight===>", animView.getHeight() + "");
        animView.setPivotY(vh.getAdapterPosition() > mLastPos ? animView.getMeasuredHeight() : 0);

        //animView.animate().setDuration(1000).translationY(0).scaleX(1f).scaleY(1f).start();
        //animView.animate().setDuration(1000).scaleX(1f).scaleY(1f).start();
        animView.animate().setDuration(300).scaleY(1f).start();

        mLastPos = vh.getAdapterPosition();
        */
		}

		@Override public void onViewDetachedFromWindow (RecyclerView.ViewHolder vh) {
			//super.onViewDetachedFromWindow(vh);
			vh.itemView.clearAnimation();
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder vh, int pos) {
			//final Animation anim = AnimationUtils.loadAnimation(ctx, R.anim.rv_scroll_anim_overshoot);

			final BeanMemberAndShop beanMemberAndShop = beanMemberAndShopList.get(pos);
			final VhImage1Text1 vhImage1Text1 = (VhImage1Text1)vh;

			vhImage1Text1.itemView.setPressed(idList.contains(beanMemberAndShop.id)); //이미 대화에 참여중인 상담사 또는 업체

			final SpannableStringBuilder ssb = new SpannableStringBuilder();

			SpannableString ss;
			String s;

			String itn = beanMemberAndShop.itn;
			int owner_sr = Integer.valueOf(beanMemberAndShop.sr);
			if(itn.isEmpty()) {
				itn = owner_sr == 0 ? "itn_m.jpg" : (owner_sr == 1 || owner_sr == 9 ? "itn_c.jpg" : "itn_s.jpg");
			}
			imageLoader.displayImage(MainCons.EnumContentPath.MEMBER_I.getPath() + itn, vhImage1Text1.iv_31, MainApp.optionsForCircleThumb);


			s = String.format("%s(%s)", beanMemberAndShop.nm, beanMemberAndShop.nn);
			ss = new SpannableString(s);
			ss.setSpan(new ForegroundColorSpan(CommonUtil.getColor(ctx, android.R.color.black)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			ss.setSpan(new AbsoluteSizeSpan(15, true), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			//ss.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			ssb.append(ss);


			s = "\n핸드폰 : ".concat(beanMemberAndShop.hn).concat(beanMemberAndShop.tn.isEmpty() ? "" : "   사무실 : ".concat(beanMemberAndShop.tn)) ;
			ss = new SpannableString(s);
			//ss.setSpan(new ForegroundColorSpan(CommonUtil.getColor(ctx, R.color.tc_ar_cv)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			ssb.append(ss);

			//슬로건
			s = "\n" + "전문분야 : 자동차에 관련된 모든 것";
			ss = new SpannableString(s);
			ss.setSpan(new ForegroundColorSpan(CommonUtil.getColor(ctx, R.color.tc_ab_cv)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

			ssb.append(ss);

			vhImage1Text1.tv_31.setText(ssb);
		}
	}
}