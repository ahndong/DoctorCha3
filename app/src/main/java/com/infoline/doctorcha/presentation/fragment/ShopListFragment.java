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
import com.infoline.doctorcha.presentation.activity.FragmentContainerActivity;
import com.infoline.doctorcha.presentation.activity.SearchPanelActivity;
import com.infoline.doctorcha.presentation.activity.ShopActivity;
import com.infoline.doctorcha.presentation.bean.BeanErrResponse;
import com.infoline.doctorcha.presentation.bean.BeanShopSearch;
import com.infoline.doctorcha.presentation.viewholder.VhImage1Text1;
import com.infoline.doctorcha.presentation.bean.BeanMemberAndShop;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.infoline.doctorcha.presentation.MainApp.beanMember;

public class ShopListFragment extends Fragment {
	private MyRecyclerAdapter rva;
	private List<BeanMemberAndShop> beanMemberAndShopList;
	private EndlessScrollListener endlessScrollListener;
	private final int limitCount = 50;
	private boolean loadMore = true;
	private int sr;

	@BindView(R.id.rv)
	RecyclerView rv;

	BeanShopSearch beanShopSearch;

    public ShopListFragment() {

    }

    //public static ShopsForRepairFragment newInstance(MainCons.ViewHolderType viewHolderType) {
	public static ShopListFragment newInstance(final BeanShopSearch beanShopSearch) {
		final ShopListFragment fragment = new ShopListFragment();

		final Bundle bundle = new Bundle();

		bundle.putSerializable(BeanShopSearch.class.getSimpleName(), beanShopSearch);
		fragment.setArguments(bundle);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_shoplist, container, false);
        ButterKnife.bind(this, rootView);

		sr = Integer.parseInt(beanMember.sr);
		beanMemberAndShopList = new ArrayList<>();

		final Bundle bundle = getArguments();
		beanShopSearch = (BeanShopSearch)bundle.getSerializable(BeanShopSearch.class.getSimpleName());

		rva = new MyRecyclerAdapter();

		final LinearLayoutManager lm = new LinearLayoutManager(getActivity());

		rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				final View ll_bottom = getActivity().findViewById(R.id.ll_bottom);

				if (ll_bottom != null) {
					AnimUtil.playMenuAnim(ll_bottom, newState == RecyclerView.SCROLL_STATE_IDLE, getResources().getInteger(android.R.integer.config_longAnimTime), 0);
				}
			}
		});

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
		rv.setAdapter(rva);

		loadData(1);

		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_search, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final Intent intent = new Intent(getActivity(), SearchPanelActivity.class);
		intent.putExtra(BeanShopSearch.class.getSimpleName(), beanShopSearch);

		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.activity_zoomin, 0);
		
		return super.onOptionsItemSelected(item);
	}

	private void loadData(int pageNo) {
		/*
		// 1. First, clear the array of data
		listOfItems.clear();
		// 2. Notify the adapter of the update
		recyclerAdapterOfItems.notifyDataSetChanged(); // or notifyItemRangeRemoved
		// 3. Reset endless scroll listener when performing a new search
		scrollListener.resetState();

		 */

		final RetrofitInterface.ShopService service = ServiceGenerator.createService(RetrofitInterface.ShopService.class);
		final Call<List<BeanMemberAndShop>> call = service.selectByFree(beanShopSearch.bmCategory_id, beanShopSearch.ca, beanShopSearch.cn, beanShopSearch.words, (pageNo - 1) * limitCount, limitCount);
		call.enqueue(new Callback<List<BeanMemberAndShop>>() {
			@Override
			public void onResponse(Call<List<BeanMemberAndShop>> call, Response<List<BeanMemberAndShop>> response) {
				String msg = null;

				if(response.isSuccessful()) {
					beanMemberAndShopList.addAll(response.body());
					rva.notifyDataSetChanged();

					if(response.body().size() < limitCount) loadMore = false;
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
			public void onFailure(Call<List<BeanMemberAndShop>> call, Throwable t) {
				CommonUtil.writeLog(ServiceGenerator.getExceptionMsgByCause(t));
			}
		});
	}

	public class MyRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {
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
			final int viewId = v.getId();
			final int pos = CommonUtil.getAdapterPositionFromView(rv, v);
			final Intent intent = new Intent(ctx, ShopActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			final MainCons.EnumActivityAnimType enumActivityAnimType;

			if(viewId == R.id.iv_31 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				intent.putExtra(MainCons.EnumExtraName.class.getSimpleName(), beanMemberAndShopList.get(pos).id);

				final String transitionName = MainCons.EnumTransitionName.THUMB.name();

				v.setTransitionName(transitionName);
				enumActivityAnimType = MainCons.EnumActivityAnimType.ANIM_SCENE_TRANSITION;

				intent.putExtra(MainCons.EnumTransitionName.class.getSimpleName(), transitionName);
				//makeClipRevealAnimation --> added v23
				//makeTaskLaunchBehind --> added v21

				final ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity)ctx, v, transitionName);
				intent.putExtra(MainCons.EnumActivityAnimType.class.getSimpleName(), enumActivityAnimType);
				intent.putExtra(BeanMemberAndShop.class.getSimpleName(), beanMemberAndShopList.get(pos));
				startActivity(intent, options.toBundle());
			}
			else {
				enumActivityAnimType = MainCons.EnumActivityAnimType.ANIM_THUMBNAIL_SCALE_UP;

				intent.putExtra(MainCons.EnumActivityAnimType.class.getSimpleName(), enumActivityAnimType);
				intent.putExtra(BeanMemberAndShop.class.getSimpleName(), beanMemberAndShopList.get(pos));
				startActivity(intent);
				getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
			}
		}

		public boolean onLongClick(View v) {
			/*
			if(board_id == 1 && beanMember.mt != 1) {
				Toast.makeText(getActivity(), "관리자만 글을 등록할 수 있습니다", Toast.LENGTH_LONG).show();
				return;
			}
			*/

			final int pos = CommonUtil.getAdapterPositionFromView(rv, v);

			//if(sr == 1 || sr == 9) {
			//1. 회사소개 새글 작성은 임시 조치였으므로 제거한다
			//2. TODO : 혹시 관련 코드가 있으면 clear한다
			/////final String[] items = { "새글 작성", "즐겨찾기 등록"};
			final String[] items = {"즐겨찾기 등록", "푸시 타겟 등록"};
			final AlertDialog ad;
			final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int menuPos) {
					final BeanMemberAndShop beanMemberAndShop = beanMemberAndShopList.get(pos);

					if(menuPos == 0) {
						/*
						final Intent intent = new Intent(getActivity(), FragmentContainerActivity.class);

						intent.putExtra(MainCons.EnumExtraName.NAME1.name(), PostEditFragment.class.getSimpleName());
						intent.putExtra("board_id", 9); //회사소개
						intent.putExtra("owner_id", beanMemberAndShop.id);
						intent.putExtra("owner_cn", beanMemberAndShop.cn);
						//신규이므로 BeanPost는 전달하지 않는다

						startActivityForResult(intent, 800);
						getActivity().overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
						*/

						final RetrofitInterface.FavoriteShopService service = ServiceGenerator.createService(RetrofitInterface.FavoriteShopService.class);
						final Call<Void> call = service.insert(beanMember.id, beanMemberAndShop.id);

						call.enqueue(new Callback<Void>() {
							@Override
							public void onResponse(Call<Void> call, Response<Void> response) {
								final String msg;

								if(response.isSuccessful()) {
									msg = "즐겨찾기 목록에 성공적으로 등록되었습니다";
								} else {
									msg = "즐겨찾기 등록에 실패하였습니다\n" + response.errorBody().source().toString();
								}

								Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
							}

							@Override
							public void onFailure(Call<Void> call, Throwable t) {
								Toast.makeText(getActivity(), ServiceGenerator.getExceptionMsgByCause(t), Toast.LENGTH_LONG).show();
							}
						});
					} else {
						if(beanMemberAndShop.sr.equals("-1")) {
							Toast.makeText(getActivity(), "정식으로 가입된 회원이 아닙니다", Toast.LENGTH_LONG).show();
						} else {
							final RetrofitInterface.PushTargetService service = ServiceGenerator.createService(RetrofitInterface.PushTargetService.class);
							final Call<Void> call = service.insert(sr == 1 || sr == 9 ? 0 : beanMember.id, beanMemberAndShop.id);

							call.enqueue(new Callback<Void>() {
								@Override
								public void onResponse(Call<Void> call, Response<Void> response) {
									final String msg;

									if(response.isSuccessful()) {
										msg = "푸시 타겟에 성공적으로 등록되었습니다";
									} else {
										msg = "푸시 타겟 등록에 실패하였습니다\n" + response.errorBody().source().toString();
									}

									Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
								}

								@Override
								public void onFailure(Call<Void> call, Throwable t) {
									Toast.makeText(getActivity(), ServiceGenerator.getExceptionMsgByCause(t), Toast.LENGTH_LONG).show();
								}
							});
						}
					}
				}
			});

			ad = builder.create();
			ad.show();

			return true;
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

		/*
		@Override
		public int getItemViewType(int pos) {
			return viewHolderType.ordinal();
		}
		*/

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
			vhImage1Text1.iv_31.setOnClickListener(this);
			vhImage1Text1.itemView.setOnClickListener(this);
			vhImage1Text1.itemView.setOnLongClickListener(this);

			return vhImage1Text1;
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder vh, int pos) {
			//final Animation anim = AnimationUtils.loadAnimation(ctx, R.anim.rv_scroll_anim_overshoot);

			final BeanMemberAndShop beanMemberAndShop = beanMemberAndShopList.get(pos);
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
		}
	}
}