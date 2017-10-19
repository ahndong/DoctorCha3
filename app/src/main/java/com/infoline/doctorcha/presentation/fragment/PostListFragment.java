package com.infoline.doctorcha.presentation.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.httphelper.ServiceGenerator;
import com.infoline.doctorcha.core.util.AnimUtil;
import com.infoline.doctorcha.core.util.CommonUtil;
import com.infoline.doctorcha.core.util.MediaUtil;
import com.infoline.doctorcha.presentation.MainCons;
import com.infoline.doctorcha.presentation.RetrofitInterface;
import com.infoline.doctorcha.presentation.activity.FragmentContainerActivity;
import com.infoline.doctorcha.presentation.activity.SearchPanelActivity;
import com.infoline.doctorcha.presentation.activity.ShopActivity;
import com.infoline.doctorcha.presentation.bean.BeanErrResponse;
import com.infoline.doctorcha.presentation.bean.BeanPost;
import com.infoline.doctorcha.presentation.bean.BeanPostSearch;
import com.infoline.doctorcha.presentation.viewholder.VhPostStatus;
import com.infoline.doctorcha.presentation.viewholder.VhText1;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.infoline.doctorcha.presentation.MainApp.beanMember;

public class PostListFragment extends Fragment {
    private MyRecyclerAdapter rva;

	@BindView(R.id.rv)
    RecyclerView rv;

	int modifyPos;

	BeanPostSearch beanPostSearch;

	public void xxx() {
		//ShopActivity의 글작성 버튼 클릭시 호출
		final Intent intent = new Intent(getActivity(), FragmentContainerActivity.class);

		intent.putExtra(MainCons.EnumExtraName.NAME1.name(), PostEditFragment.class.getSimpleName());
		intent.putExtra("board_id", beanPostSearch.board_id == -1 ? 11 : beanPostSearch.board_id);
		intent.putExtra("owner_id", beanPostSearch.owner_id);

		//TODO : 정리할 것
		intent.putExtra("owner_cn", ((Toolbar)((ShopActivity)getActivity()).findViewById(R.id.tb)).getTitle().toString()); //편법.  급해서 넘어간다

		//신규이므로 BeanPost는 전달하지 않는다

		startActivityForResult(intent, 800);
		getActivity().overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//800 : PostEditFragment(신규 포스트 작성)
		//801 : PostReadFragment
		//803 : PostReplFragment
		if (requestCode == 801 || requestCode == 803) {
			//TODO : 졸라 찜찜하다
			//PostList -> PostRead 또는  PostReplFragment인 상태에서 push 터치하여 PostRead가 뜬 후 백 시킬 경우
			if(data == null) return;

			if (requestCode == 801) {
				rva.beanPostList.get(modifyPos).rec = rva.beanPostList.get(modifyPos).rec + 1;
				rva.notifyItemChanged(modifyPos);
			}

			//1. post의 댓글 아이콘을 클릭하여 댓글창으로 직접 진입했을 경우
			//   PostReplFragment의 FragmentContainerActivity에서 편법으로 전달된다
			//2. post 클릭하여 글보기 창으로 진입했을 경우
			//   PostReadFragment의 FragmentContainerActivity에서 편법으로 전달된다
			final int replCount = data.getIntExtra("replCount", 0);

			if(replCount != 0) {
				rva.beanPostList.get(modifyPos).coc += replCount;
				rva.notifyItemChanged(modifyPos);
			}
		}

		if (resultCode != RESULT_OK) return;

		if (requestCode == 800) {
			//새글
			final BeanPost newBeanPost = (BeanPost)data.getSerializableExtra("beanPost");
			rva.beanPostList.add(0, newBeanPost);
			rva.notifyItemInserted(0);
			rv.scrollToPosition(0);
		} else if (requestCode == 801) {
			//글보기 - 글수정 - 수정 또는 삭제후 이곳으로 바로 자동 점프
			final BeanPost modifiedBeanPost = (BeanPost)data.getSerializableExtra("beanPost");

			if(modifiedBeanPost.id == -1) {
				rva.removeItem(modifyPos);
			} else {
				final BeanPost xx = rva.beanPostList.get(modifyPos);
				xx.tt =   modifiedBeanPost.tt;
				xx.ud =   modifiedBeanPost.ud;
				xx.fcb =   modifiedBeanPost.fcb;
				xx.ffn =   modifiedBeanPost.ffn;

				rva.notifyItemChanged(modifyPos);
			}
		}
	}

    public PostListFragment() {

    }

    //public static PostListFragment newInstance(final int bmCategory_id, final int owner_id, final int board_id, final int creator_id, final String words) {
	public static PostListFragment newInstance(final BeanPostSearch beanPostSearch) {
		final PostListFragment fragment = new PostListFragment();

		final Bundle bundle = new Bundle();
		bundle.putSerializable("beanPostSearch", beanPostSearch);

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
		View rootView = inflater.inflate(R.layout.fragment_postlist, container, false);
        ButterKnife.bind(this, rootView);

		final Bundle bundle = getArguments();
		beanPostSearch = (BeanPostSearch)bundle.getSerializable("beanPostSearch");

		rva = new MyRecyclerAdapter(getActivity());
		final LinearLayoutManager lm = new LinearLayoutManager(getActivity());
		lm.setOrientation(LinearLayoutManager.VERTICAL);

		/*
		rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				//AnimUtil.playMenuAnim(tv_confirm, newState == RecyclerView.SCROLL_STATE_IDLE, getResources().getInteger(android.R.integer.config_longAnimTime), 0);

				if(fab.getVisibility() == View.VISIBLE) {
					AnimUtil.togglePopup(fab, false);
				}
			}
		});
		*/

		rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				final View fab = getActivity().findViewById(R.id.fab);

				if (fab != null) {
					AnimUtil.playMenuAnim(fab, newState == RecyclerView.SCROLL_STATE_IDLE, getResources().getInteger(android.R.integer.config_longAnimTime), 0);
				}
			}
		});

		loadData();

		rv.setHasFixedSize(true);
		rv.setLayoutManager(lm);
		rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(rva);

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
		//현재 카테고리 및 게시판 종류 전달
		intent.putExtra(BeanPostSearch.class.getSimpleName(), beanPostSearch);

		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.activity_zoomin, 0);

		return super.onOptionsItemSelected(item);
	}

	private void loadData() {
		final RetrofitInterface.PostService service = ServiceGenerator.createService(RetrofitInterface.PostService.class);
		final Call<List<BeanPost>> call = service.selectByFree(beanPostSearch.bmCategory_id, beanPostSearch.board_id, beanPostSearch.owner_id, beanPostSearch.creator_id, beanPostSearch.words, 0, 50);
		call.enqueue(new Callback<List<BeanPost>>() {
			@Override
			public void onResponse(Call<List<BeanPost>> call, Response<List<BeanPost>> response) {
				if(response.isSuccessful()) {
					rva.appendItems(response.body());
				}
				else {
					String msg = null;

					try {
						final BeanErrResponse beanErrResponse = new BeanErrResponse(response.errorBody().string()); //{ec:%s,em:'%s',sv:'%s'}

						if(beanErrResponse.em == null) {
							msg = String.format("[%s]%s", response.code(), response.raw().toString());
						} else {
							msg = beanErrResponse.em;
						}
					} catch (Exception e) {
						msg = e.getMessage();
					}

					if(msg != null) {
						Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
					}
				}
			}

			@Override
			public void onFailure(Call<List<BeanPost>> call, Throwable t) {
				Toast.makeText(getActivity(), ServiceGenerator.getExceptionMsgByCause(t), Toast.LENGTH_LONG).show();
			}
		});
	}

	public class MyRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
		private final ImageLoader imageLoader = ImageLoader.getInstance();
		private final List<BeanPost> beanPostList;
		private final Context ctx;

		private final View.OnClickListener ocl;

		public MyRecyclerAdapter(final Context ctx) {
			//onAttachedToRecyclerView()에서 rv를 이용하여 Context를 구할 수 있으나 Attached 이전에
			//Context는 참조할 수 없으므로 인자로 전달 받는다.

			this.ctx = ctx;
			this.beanPostList = new ArrayList<>();

			ocl = new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					final int pos = CommonUtil.getAdapterPositionFromView(rv, v);
					final BeanPost beanPost = beanPostList.get(pos);

					switch (v.getId()) {
						case R.id.tv_share:
							break;
						case R.id.tv_like:
							break;
						case R.id.tv_repl:
							modifyPos = pos;
							final Intent intent1 = new Intent(getActivity(), FragmentContainerActivity.class);
							intent1.putExtra(MainCons.EnumExtraName.NAME1.name(), PostReplFragment.class.getSimpleName());
							intent1.putExtra("beanPost", beanPost);
							startActivityForResult(intent1, 803);
							getActivity().overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
							break;
						default:
							modifyPos = pos;
							final Intent intent2 = new Intent(getActivity(), FragmentContainerActivity.class);
							intent2.putExtra(MainCons.EnumExtraName.NAME1.name(), PostReadFragment.class.getSimpleName());
							intent2.putExtra("beanPost", beanPost);
							startActivityForResult(intent2, 801);
							getActivity().overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
					}
				}
			};
		}

		private void removeItem(int pos) {
			if(beanPostList.size() == 1) {
				beanPostList.get(0).id = -1; //empty view로 전환
				notifyItemChanged(0);
			} else {
				beanPostList.remove(pos);
				notifyItemRemoved(pos);
			}
		}

		private void appendItems(List<BeanPost> beanPostList) {
			if(beanPostList.size() == 0) {
				if(this.beanPostList.size() == 0) {
					//1. dummy item 추가
					//2. ms : madia status - 0.saved 1.new 2.updated
					//3.id, su, lu, ms --> ms는 의미없다
					final BeanPost beanPost = new BeanPost();
					beanPost.id = -1;
					this.beanPostList.add(beanPost);
				}
			} else {
				if(this.beanPostList.size() == 1 && this.beanPostList.get(0).id == -1) {
					this.beanPostList.remove(0);
				}

				this.beanPostList.addAll(beanPostList);
			}

			notifyDataSetChanged();
		}

		@Override
		public int getItemCount() {
			return beanPostList.size();
		}

		@Override
		public long getItemId(int pos) {
			//getItemId() method는 ViewHolder로 상속된다. - 필요한가?
			return beanPostList.get(pos).id;
		}

		@Override
		public int getItemViewType(int pos) {
			//대표 텍스트만 있을 경우 , 대표 이미지만 있을 경우 , 둘 다 있을 경우

			final int viewType;
			final BeanPost beanPost = beanPostList.get(pos);

			if(beanPost.id == -1) {
				viewType = -1;
			} else {
				if(beanPost.fcb.isEmpty() || beanPost.ffn.isEmpty()) {
					viewType = beanPost.ffn.isEmpty() ? 1 : 2;
				} else {
					viewType = 3;
				}
			}

			return viewType;
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup vg, int itemViewType) {
			final RecyclerView.ViewHolder vh;


			if(itemViewType == -1) {
				//empty holde presentation
				vh = new VhText1(LayoutInflater.from(getActivity()).inflate(R.layout.vh_empty_text_image, vg, false));;
			} else {
				final VhPostStatus vhPostStatus = new VhPostStatus(LayoutInflater.from(ctx).inflate(R.layout.vh_post, vg, false));

				if(itemViewType == 1) {
					//1. text만
					//2. photo or video만
					//3. text and (photo or video)

					vhPostStatus.fl_image.setVisibility(View.GONE);
				}

				vhPostStatus.itemView.setOnClickListener(ocl);
				vhPostStatus.tv_repl.setOnClickListener(ocl);

				vh = vhPostStatus;
			}

			return vh;
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder vh, int pos) {
			final int itemViewType = getItemViewType(pos);

			if(itemViewType == -1) {
				final VhText1 vhText1 = (VhText1)vh;
				vhText1.tv_31.setText("작성된 포스트가 없습니다");
				return;
			}

			final VhPostStatus vhPostStatus = (VhPostStatus)vh;
			final BeanPost beanPost = beanPostList.get(pos);
			final SpannableStringBuilder ssb = new SpannableStringBuilder();

			SpannableString ss;
			String s;

			s = beanPost.tt;
			ss = new SpannableString(s);
			ss.setSpan(new ForegroundColorSpan(CommonUtil.getColor(ctx, R.color.tc_blue_highligh)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			ss.setSpan(new AbsoluteSizeSpan(15, true), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			ssb.append(ss);

			s = "\n" + beanPost.nn + "   " + beanPost.ud.substring(0, 16) + "   조회수 " + beanPost.rec + (beanMember.id == 2 ? " (" + Integer.toString(beanPost.id) + ")" : "");
			ss = new SpannableString(s);
			ss.setSpan(new AbsoluteSizeSpan(13, true), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

			ss.setSpan(new LineHeightSpan() {
				@Override
				public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
					//fm.top += 10;
					//fm.ascent += 10;
					fm.bottom += 20;
					fm.descent += 20;
				}
			}, 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			ssb.append(ss);

			final int viewType = getItemViewType(pos);

			if(viewType == 1 || viewType == 3) {
				s = "\n" + beanPost.fcb;

				ss = new SpannableString(s);
				ss.setSpan(new AbsoluteSizeSpan(14, true), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				ss.setSpan(new ForegroundColorSpan(Color.parseColor("#FF444444")), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

				ss.setSpan(new LineHeightSpan() {
					@Override
					public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
						fm.bottom += 5;
						fm.descent += 5;
					}
				}, 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

				ssb.append(ss);
			}

			if(viewType == 2 || viewType == 3) {
				final String fileName = beanPost.ffn;
				final String mimeType = MediaUtil.getMimeType(fileName);
				final MediaType mediaType = MediaType.parse(mimeType);

				final String url;

				if(mediaType.type().equals("image")) {
					url = MainCons.EnumContentPath.CONTENT_I.getPath() + fileName;
					vhPostStatus.iv_video_icon.setVisibility(View.GONE);
				} else {
					url = MainCons.EnumContentPath.CONTENT_V_T.getPath() + fileName.replace("mp4", "jpg");
					vhPostStatus.iv_video_icon.setVisibility(View.VISIBLE);
				}
				imageLoader.displayImage(url, vhPostStatus.iv_31);
			}

			vhPostStatus.tv_repl.setText(beanPost.coc+"");

			vhPostStatus.tv_31.setText(ssb);
		}
	}
}