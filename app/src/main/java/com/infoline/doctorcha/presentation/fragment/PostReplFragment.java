package com.infoline.doctorcha.presentation.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.LineHeightSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.CoreCons;
import com.infoline.doctorcha.core.httphelper.ServiceGenerator;
import com.infoline.doctorcha.core.listener.MediaScanFileHelper;
import com.infoline.doctorcha.core.util.CommonUtil;
import com.infoline.doctorcha.core.util.MediaUtil;
import com.infoline.doctorcha.core.view.RevealForegroundView;
import com.infoline.doctorcha.presentation.MainCons;
import com.infoline.doctorcha.presentation.RetrofitInterface;
import com.infoline.doctorcha.presentation.activity.FragmentContainerActivity;
import com.infoline.doctorcha.presentation.bean.BeanContent;
import com.infoline.doctorcha.presentation.bean.BeanDeleted;
import com.infoline.doctorcha.presentation.bean.BeanErrResponse;
import com.infoline.doctorcha.presentation.bean.BeanPost;
import com.infoline.doctorcha.presentation.bean.BeanPostAndContents_test;
import com.infoline.doctorcha.presentation.viewholder.VhPostRepl;
import com.infoline.doctorcha.presentation.viewholder.VhText1;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.infoline.doctorcha.core.CoreCons.EnumDateFormat.UNIQUE_FILENAME;
import static com.infoline.doctorcha.presentation.MainApp.beanMember;
import static com.infoline.doctorcha.presentation.MainCons.CN_REQUEST_GALLARY;

public class PostReplFragment extends Fragment {
	private MyRecyclerAdapter rva;

	@BindView(R.id.rv)
    RecyclerView rv;
	@BindView(R.id.et)
	EditText et;
	@BindView(R.id.iv_upload)
	ImageView iv_upload;
	@BindView(R.id.tv_send)
	TextView tv_send;

	BeanPost beanPost;
	final MediaUtil mediaUtil = new MediaUtil();
	String fileName = "";
	String realUrl = "";

	@OnClick({R.id.iv_upload, R.id.tv_send})
	protected void imageViewClickListener(final View v) {
		final RevealForegroundView rfv = RevealForegroundView.createRevealForegroundView(getActivity(), v, Color.parseColor("#35000000"));

		rfv.setListener(new RevealForegroundView.AnimationEndListener() {
			public void AnimationEnd() {
				if(v.getId() == R.id.tv_send) {
					fileName = "";
					realUrl = "";
					saveData();
				}
				else {
					////////////startActivityForResult(mediaUtil.getMediaChooser(), CN_REQUEST_PICK_FILE);

					final String[] items = { "사진 겔러리", "사진 촬영", "동영상 겔러리", "동영상 촬영"};
					final AlertDialog ad;
					final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

					builder.setItems(items, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int pos) {
							final Intent intent = mediaUtil.getGallaryIntent_(getActivity(), pos);

							if(intent != null) {
								startActivityForResult(intent, CN_REQUEST_GALLARY);
							}
						}
					});

					ad = builder.create();
					ad.show();
				}
			}
		});
	}

	private void saveData() {
		final String cb = et.getText().toString().trim();

		if(fileName.isEmpty() && cb.isEmpty()) {
			Toast.makeText(getActivity(), "댓글 내용을 입력하셔야 됩니다.", Toast.LENGTH_LONG).show();
			return;
		}

		final BeanPost replBeanPost = new BeanPost();

		replBeanPost.owner_id = beanPost.owner_id;
		replBeanPost.board_id = beanPost.board_id;
		replBeanPost.member_id = beanMember.id;
		replBeanPost.parent_id = beanPost.id;
		replBeanPost.tt = "";
		replBeanPost.fcb = fileName.isEmpty() ? cb : "";
		replBeanPost.ffn = fileName.isEmpty() ? "" : fileName;

		final ProgressDialog pd = ProgressDialog.show(getActivity(), "", !fileName.isEmpty() ? "파일을 저장하고 있습니다" : "댓글을 저장하고 있습니다", true);
		final RetrofitInterface.PostService service = ServiceGenerator.createService(RetrofitInterface.PostService.class);

		final BeanPostAndContents_test beanPostAndContents_test = new BeanPostAndContents_test(replBeanPost,  new ArrayList<BeanContent>(), new ArrayList<BeanDeleted>());

		final Call<Integer> call = service.insert(beanPostAndContents_test);
		call.enqueue(new Callback<Integer>() {
			@Override
			public void onResponse(Call<Integer> call, Response<Integer> response) {
				if(response.isSuccessful()) {
					((FragmentContainerActivity)getActivity()).replPlus(1);

					replBeanPost.id = response.body();
					replBeanPost.nn = beanMember.nn; //머지?
					replBeanPost.ud = CommonUtil.getFormattedDate(CoreCons.EnumDateFormat.DATE_AND_TIME); //화면표시를 위한 편법

					et.setText("");

					if(!fileName.isEmpty()) {
						uploadFile(pd, replBeanPost);
					} else {
						if(rva.beanPostList.size() == 1 && rva.beanPostList.get(0).id == -1) {
							//empty item 제거
							rva.beanPostList.remove(0);
							rva.notifyItemRemoved(0);
						}

						rva.beanPostList.add(0, replBeanPost);
						rva.notifyItemInserted(0);

						rv.scrollToPosition(0);

						pd.dismiss();
					}
				}
				else {
					final String msg = (fileName.isEmpty() ? "댓글" : "파일") + " 저장에 실패하였습니다\n" + response.errorBody().source().toString();

					Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
					pd.dismiss();
				}
			}

			@Override
			public void onFailure(Call<Integer> call, Throwable t) {
				pd.dismiss();
				final String msg = ServiceGenerator.getExceptionMsgByCause(t);

				Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
			}
		});
	}

	private void uploadFile(final ProgressDialog pd, final BeanPost replBeanPost) {
		final MultipartBody.Builder builder = new MultipartBody.Builder();
		builder.setType(MultipartBody.FORM);

		try {
			final boolean isImage = MediaUtil.isImage(realUrl);
			final File file = new File(realUrl);

			final RequestBody requestBody = RequestBody.create(MediaUtil.getMediaType(realUrl), file);
			builder.addFormDataPart("fileList", fileName, requestBody);
			builder.addFormDataPart("pathIdList", isImage ? MainCons.EnumContentPath.CONTENT_I.getId()+"" : MainCons.EnumContentPath.CONTENT_V.getId()+"");

			if(!isImage) {
				final Bitmap bm = MediaUtil.createThumbnailFromVideFile(realUrl);
				final File thumbnail_file = MediaUtil.saveBitmaptoJpeg(bm);

				final RequestBody requestBody_t = RequestBody.create(MediaType.parse("image/jpeg"), thumbnail_file);
				builder.addFormDataPart("fileList", fileName.replace("mp4", "jpg"), requestBody_t);
				builder.addFormDataPart("pathIdList", MainCons.EnumContentPath.CONTENT_V_T.getId()+"");
			}

			final RequestBody finalRequestBody = builder.build();

			//------------------------------------------------

			final RetrofitInterface.UploadService service = ServiceGenerator.createService(RetrofitInterface.UploadService.class);
			final Call<Void> call = service.files2(finalRequestBody);
			call.enqueue(new Callback<Void>() {
				@Override
				public void onResponse(Call<Void> call, Response<Void> response) {
					pd.dismiss();

					if(response.isSuccessful()) {
						if(rva.beanPostList.size() == 1 && rva.beanPostList.get(0).id == -1) {
							//empty item 제거
							rva.beanPostList.remove(0);
							rva.notifyItemRemoved(0);
						}

						rva.beanPostList.add(0, replBeanPost);
						rva.notifyItemInserted(0);
						rv.scrollToPosition(0);
					} else {
						final String msg = "파일 저장에 실패하였습니다\n" + response.errorBody().source().toString();
						Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
					}
				}

				@Override
				public void onFailure(Call<Void> call, Throwable t) {
					pd.dismiss();
					final String msg = ServiceGenerator.getExceptionMsgByCause(t);
					Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
				}
			});
		} catch (Exception e) {
			pd.dismiss();
			Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) return;

		if (requestCode == CN_REQUEST_GALLARY) {
			if(data == null) {
				MediaScanFileHelper.OnMediaScanListener onMediaScanListener = new MediaScanFileHelper.OnMediaScanListener() {
					public void onError() {

					}

					public void onSuccess(final Uri uri) {
						new Thread(new Runnable()
						{
							@Override
							public void run()
							{
								getActivity().runOnUiThread(new Runnable()
								{
									@Override
									public void run()
									{
										realUrl = MediaUtil.getRealPathFromUri(getActivity(), uri);
										fileName = beanMember.id + "_" + CommonUtil.getFormattedDate(UNIQUE_FILENAME) + "." + CommonUtil.getExtName(realUrl);
										saveData();
									}
								});
							}
						}).start();
					}
				};

				final MediaScanFileHelper mediaScanFileHelper = new MediaScanFileHelper(getActivity());
				mediaScanFileHelper.scanFile(new File(mediaUtil.realPath), onMediaScanListener);
			}
			else {
				final ClipData cd = data.getClipData();

				if (cd != null) {
					//google photo intent --> 다른 Intent는 무엇이 있는지 확인 못함
					final int selectedCount = cd.getItemCount() > 10 ? 10 : cd.getItemCount();

					for (int i = 0; i < selectedCount; i++) {
						mediaUtil.selectedUriList.add(cd.getItemAt(i).getUri());
					}

				} else {
					mediaUtil.selectedUriList.add(data.getData());
				}

				for(Uri uri : mediaUtil.selectedUriList) {
					realUrl = MediaUtil.getRealPathFromUri(getActivity(), uri);
					fileName = beanMember.id + "_" + CommonUtil.getFormattedDate(UNIQUE_FILENAME) + "." + CommonUtil.getExtName(realUrl);
				}

				saveData();
			}
		}
	}

	public PostReplFragment() {

	}

	public static PostReplFragment newInstance(final BeanPost beanPost) {
		//final String[] subMenuList = {"공지사항", "질문답변", "정보교류", "자유게시판"};
		final PostReplFragment fragment = new PostReplFragment();

		final Bundle bundle = new Bundle();

		bundle.putSerializable("beanPost", beanPost);
		fragment.setArguments(bundle);

		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_postrepl, container, false);
		ButterKnife.bind(this, rootView);

		final Bundle bundle = getArguments();
		beanPost = (BeanPost)bundle.getSerializable("beanPost");

		rva = new MyRecyclerAdapter(getActivity());
		final LinearLayoutManager lm = new LinearLayoutManager(getActivity());
		lm.setOrientation(LinearLayoutManager.VERTICAL);

		loadData();

		rv.setHasFixedSize(true);
		rv.setLayoutManager(lm);
		rv.setItemAnimator(new DefaultItemAnimator());
		rv.setAdapter(rva);

		return rootView;
	}

	private void loadData() {
		final RetrofitInterface.PostService service = ServiceGenerator.createService(RetrofitInterface.PostService.class);
		final Call<List<BeanPost>> call = service.selectByParentId(beanPost.id);
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

					final String fileName = beanPost.ffn;
					final boolean isImage = MediaUtil.isImage(fileName);

					final MainCons.EnumContentPath enumContentPath = isImage ? MainCons.EnumContentPath.CONTENT_I : MainCons.EnumContentPath.CONTENT_V;
					final String url = enumContentPath.getPath() + fileName;

					final Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.parse(url), isImage ? "image/*" : "video/*");
					startActivityForResult(intent, CN_REQUEST_GALLARY);
				}
			};
		}

		private void appendItems(List<BeanPost> beanPostList) {
			if(beanPostList.size() == 0) {
				final BeanPost emptyBeanPost = new BeanPost();
				emptyBeanPost.id = -1;

				beanPostList.add(emptyBeanPost);
			}

			this.beanPostList.addAll(beanPostList);
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
			//-1. vh_empty
			//1.대표 텍스트만 있을 경우 , 2.대표 이미지만 있을 경우 , 3.둘 다 있을 경우

			final int viewType;
			final BeanPost beanPost = beanPostList.get(pos);

			if(pos == 0 && beanPost.id == -1) {
				viewType = -1;
			} else {
				if(beanPost.fcb.isEmpty() || beanPost.ffn.isEmpty()) {
					viewType = beanPost.ffn.isEmpty() ? 1 : 2;
				} else {
					//지금은 사진 아니면 글이지만 차후 확장할 경우 필요
					viewType = 3;
				}
			}

			return viewType;
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup vg, int viewHolderType) {

			if(viewHolderType == -1) {
				return new VhText1(LayoutInflater.from(ctx).inflate(R.layout.vh_empty, vg, false));
			} else {
				final VhPostRepl vhPostRepl = new VhPostRepl(LayoutInflater.from(ctx).inflate(R.layout.vh_post_repl, vg, false));

				if(viewHolderType == 1) {
					//1. text만
					//2. photo or video만
					//3. text and (photo or video)

					vhPostRepl.fl_image.setVisibility(View.GONE);
				} else {
					vhPostRepl.itemView.setOnClickListener(ocl);
				}

				return vhPostRepl;
			}
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder vh, int pos) {
			final int viewHolderType = getItemViewType(pos);

			if(viewHolderType == -1) {
				return;
			}

			final VhPostRepl vhPostRepl = (VhPostRepl)vh;
			final BeanPost beanPost = beanPostList.get(pos);
			final SpannableStringBuilder ssb = new SpannableStringBuilder();

			SpannableString ss;
			String s;

			s = beanPost.nn + "   " + beanPost.ud.substring(0, 16);
			ss = new SpannableString(s);
			ss.setSpan(new AbsoluteSizeSpan(13, true), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			ssb.append(ss);

			if(viewHolderType == 1 || viewHolderType == 3) {
				s = "\n" + beanPost.fcb;
				ss = new SpannableString(s);
				ss.setSpan(new ForegroundColorSpan(CommonUtil.getColor(ctx, android.R.color.black)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

				//아 정말 왕짜증이네 - 윗 부분이 일부 짤린다 - 일단 통과
				ss.setSpan(new LineHeightSpan() {
					@Override
					public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
						fm.top += 8;
						fm.ascent -= 8;
					}
				}, 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

				ssb.append(ss);
			}

			if(viewHolderType == 2 || viewHolderType == 3) {
				final boolean isImage = MediaUtil.isImage(realUrl);
				final String url;

				if(isImage) {
					url = MainCons.EnumContentPath.CONTENT_I.getPath() + fileName;
					vhPostRepl.iv_video_icon.setVisibility(View.GONE);
				} else {
					url = MainCons.EnumContentPath.CONTENT_V_T.getPath() + fileName.replace(CommonUtil.getExtName(fileName), "jpg");
					vhPostRepl.iv_video_icon.setVisibility(View.VISIBLE);
				}
				imageLoader.displayImage(url, vhPostRepl.iv_31);
			}

			vhPostRepl.tv_31.setText(ssb);
		}
	}
}