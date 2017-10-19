package com.infoline.doctorcha.presentation.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.CoreCons;
import com.infoline.doctorcha.core.httphelper.ServiceGenerator;
import com.infoline.doctorcha.core.listener.MediaScanFileHelper;
import com.infoline.doctorcha.core.listener.ViewOnClickListener;
import com.infoline.doctorcha.core.util.CommonUtil;
import com.infoline.doctorcha.core.util.MediaUtil;
import com.infoline.doctorcha.core.util.SpinnerUtil;
import com.infoline.doctorcha.presentation.MainApp;
import com.infoline.doctorcha.presentation.MainCons;
import com.infoline.doctorcha.presentation.RetrofitInterface;
import com.infoline.doctorcha.presentation.bean.BeanBoarde;
import com.infoline.doctorcha.presentation.bean.BeanContent;
import com.infoline.doctorcha.presentation.bean.BeanDeleted;
import com.infoline.doctorcha.presentation.bean.BeanErrResponse;
import com.infoline.doctorcha.presentation.bean.BeanPost;
import com.infoline.doctorcha.presentation.bean.BeanPostAndContents_test;
import com.infoline.doctorcha.presentation.viewholder.VhEdit1;
import com.infoline.doctorcha.presentation.viewholder.VhImage1;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
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
import static com.infoline.doctorcha.presentation.MainApp.beanBoardeList;
import static com.infoline.doctorcha.presentation.MainApp.beanMember;
import static com.infoline.doctorcha.presentation.MainCons.CN_REQUEST_GALLARY;

public class PostEditFragment extends Fragment {
    //1. UsedCarListFragment에서 static으로 선언하면 UsedCarListFragment를 사용하는 전체 Activity에서 동일 ViewHolderType이 적용된다.
	private MyRecyclerAdapter rva;

	private Handler mHandler;
	private Runnable mRunnable;

	@BindView(R.id.sp_cn)
	Spinner sp_cn;
	@BindView(R.id.sp_bn)
	Spinner sp_bn;
	@BindView(R.id.et_tt)
	EditText et_tt;
    @BindView(R.id.rv)
    RecyclerView rv;
	@BindView(R.id.ll_bottom)
	LinearLayout ll_bottom;
	@BindView(R.id.tv_confirm)
	TextView tv_confirm;
	@BindView(R.id.tv_del)
	TextView tv_del;

	BeanPost beanPost;
	final MediaUtil mediaUtil;

	//1. 두가지 용도로 쓰인다.
	//	1) 상담사가 메세지창에서 '보기 아이콘'을 클릭할 경우 해당 post_id를 전달 받는 용도로 사용
	//	2) 회원이 작성 후 상담사에게 전송하는 용도로 사용
	int board_id;
	int owner_id;
	String owner_cn;

	@OnClick({R.id.tv_confirm, R.id.tv_media, R.id.tv_del})
	protected void onClick_1(final View v) {
		switch (v.getId()) {
			case R.id.tv_confirm:
				rva.saveData();
				break;
			case R.id.tv_del:
				deleteData();
				break;
			case R.id.tv_media:
				final String[] items = { "사진 겔러리", "사진 촬영", "동영상 겔러리", "동영상 촬영"};
				final AlertDialog ad;
				final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				//builder.setTitle(textView.getHint());

				builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int pos) {
						final Intent intent = mediaUtil.getGallaryIntent(getActivity(), pos);

						if(intent != null) {
							startActivityForResult(intent, CN_REQUEST_GALLARY);
						}
					}
				});

				ad = builder.create();
				ad.show();
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
										final BeanContent beanContent = new BeanContent();
										beanContent.realUrl = MediaUtil.getRealPathFromUri(getActivity(), uri);
										beanContent.cot = beanContent.realUrl.contains("mp4") ? MainCons.EnumContentType.VIDEO.getValue() : MainCons.EnumContentType.PHOTO.getValue();
										beanContent.cob = beanMember.id + "_" + CommonUtil.getFormattedDate(UNIQUE_FILENAME) + "." + CommonUtil.getExtName(beanContent.realUrl);

										rva.appendItem(beanContent);
										rva.appendItem(new BeanContent()); //마지막에는 항상 내용 입력 content
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
					final BeanContent beanContent = new BeanContent();
					beanContent.realUrl = MediaUtil.getRealPathFromUri(getActivity(), uri);
					beanContent.cot = beanContent.realUrl.contains("mp4") ? MainCons.EnumContentType.VIDEO.getValue() : MainCons.EnumContentType.PHOTO.getValue();
					beanContent.cob = beanMember.id + "_" + CommonUtil.getFormattedDate(UNIQUE_FILENAME) + "." + CommonUtil.getExtName(beanContent.realUrl);

					rva.applyNewFileItem(beanContent);
				}
			}
		}
	}

    public PostEditFragment() {
		mediaUtil = new MediaUtil();
    }

    public static PostEditFragment newInstance(final int board_id, final int owner_id, final String owner_cn, final BeanPost beanPost) {
		final PostEditFragment fragment = new PostEditFragment();

		final Bundle bundle = new Bundle();

		if(beanPost == null) {
			bundle.putInt("board_id", board_id);
			bundle.putInt("owner_id", owner_id);
			bundle.putString("owner_cn", owner_cn);
		} else {
			bundle.putSerializable("beanPost", beanPost);
		}

		fragment.setArguments(bundle);

		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_postedit, container, false);
        ButterKnife.bind(this, rootView);

		final Bundle bundle = getArguments();
		beanPost = (BeanPost)bundle.getSerializable("beanPost");

		if(beanPost == null) {
			board_id = bundle.getInt("board_id", 11); //자유게시판
			owner_id = bundle.getInt("owner_id", 1); //닥터차
			owner_cn = bundle.getString("owner_cn"); //소유자의 블로그명

			tv_del.setVisibility(View.GONE);
		} else {
			board_id = beanPost.board_id;
			owner_id = beanPost.owner_id;
			owner_cn = beanPost.cn;
		}

		rva = new MyRecyclerAdapter(getActivity());
		final LinearLayoutManager lm = new LinearLayoutManager(getActivity());
		lm.setOrientation(LinearLayoutManager.VERTICAL);

		loadData();

		rv.setHasFixedSize(true);
		rv.setLayoutManager(lm);
		rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(rva);

		fillSpinner();

		return rootView;
	}

	private void fillSpinner() {
        //1. 게시판 목록
        final SpinnerUtil spinnerUtil = new SpinnerUtil();
        spinnerUtil.attachBasicCodeAdapter( sp_bn, SpinnerUtil.CN_BASECODETYPE_BOARD, board_id, null);

		//---

        //1. 게시판을 소유하는 블로그명
        //2. 현재는 블로그명 표시 역할만 하며 선택값 참조는 없다
        //3. 차후 블로그 선택가능 기능을 확장할 경우 사용키 위해 미리 준비
		final List<String> ownerCnList = new ArrayList<>();
		ownerCnList.add(owner_cn);

		final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), R.layout.tmp_spinner_textview, ownerCnList);
		adapter2.setDropDownViewResource(R.layout.tmp_spinner_dropdownitem);
		sp_cn.setAdapter(adapter2);

		//sp_bn.setSelection(((ArrayAdapter<String>)sp_bn.getAdapter()).getPosition(BeanBoarde.getNmFromId(beanBoardeList, board_id)));
	}

	private void deleteData() {
		if(beanPost.coc != 0) {
			if(!(beanMember.id == 1 || beanMember.id == 2)) {
				Toast.makeText(getActivity(), "댓글이 있는 포스트는 삭제할 수 없습니다", Toast.LENGTH_LONG).show();
				return;
			}
		}

		final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if(which == DialogInterface.BUTTON_POSITIVE) {
					final RetrofitInterface.PostService service = ServiceGenerator.createService(RetrofitInterface.PostService.class);
					final Call<Void> call = service.delete(beanPost.id);

					call.enqueue(new Callback<Void>() {
						@Override
						public void onResponse(Call<Void> call, Response<Void> response) {
							final String msg;

							if(!response.isSuccessful()) {
								msg = "포스트 삭제에 실패하였습니다\n" + response.errorBody().source().toString();
							} else {
								msg = "포스트가 성공적으로 삭제되었습니다.";
							}

							Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();

							if(response.isSuccessful()) {
								rva.closeActivity(2);
							}
						}

						@Override
						public void onFailure(Call<Void> call, Throwable t) {
							Toast.makeText(getActivity(), ServiceGenerator.getExceptionMsgByCause(t), Toast.LENGTH_LONG).show();
						}
					});
				}
			}
		};

		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("이 포스트를 영구적으로 삭제하시겠습니까?").setPositiveButton("예", dialogClickListener).setNegativeButton("아니오", dialogClickListener).show();
	}

	//콘텐츠 목록 읽어 오기
	private void loadData() {
		if(beanPost == null) {
			//새 글
			et_tt.setText("");

			beanPost = new BeanPost();
			beanPost.board_id = board_id;
			beanPost.member_id = beanMember.id;

			/*
			TODO : 아래 오류 해갤하고 owner_cn 전달받는 거 모두 취소시키라
			final RetrofitInterface.MemberService service = ServiceGenerator.createService(RetrofitInterface.MemberService.class);
			final Call<String> call = service.selectCn(owner_id);
			call.enqueue(new Callback<String>() {
				@Override
				public void onResponse(Call<String> call, Response<String> response) {
					if(response.isSuccessful()) {
						final String cn = response.body();
						Toast.makeText(getActivity(), cn, Toast.LENGTH_LONG).show();
					}
					else {
						final String msg = "블로그 명칭을 구하지 못했습니다\n" + response.errorBody().source().toString();

						Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
					}
				}

				@Override
				public void onFailure(Call<String> call, Throwable t) {
					final String msg = ServiceGenerator.getExceptionMsgByCause(t);

					Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
				}
			});
			*/

			/*
			rva.beanContentList.clear();
			rva.notifyDataSetChanged();
			*/

			final BeanContent beanContent = new BeanContent();
			beanContent.cot = MainCons.EnumContentType.TEXT.getValue();

			//신규작성 화면에서 기본적으로 내용입력 content 제시
			rva.appendItem(beanContent);
			return;
		}

		//------------------------------------------------------------------------------------------

		et_tt.setText(beanPost.tt);

		final RetrofitInterface.ContentService service = ServiceGenerator.createService(RetrofitInterface.ContentService.class);

		final Call<List<BeanContent>> call = service.selectByPostId(beanPost.id);
		call.enqueue(new Callback<List<BeanContent>>() {
			@Override
			public void onResponse(Call<List<BeanContent>> call, Response<List<BeanContent>> response) {
				if(response.isSuccessful()) {
					retriveContentList(response.body());
				}
				else {
					Toast.makeText(getActivity(), response.errorBody().source().toString(), Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onFailure(Call<List<BeanContent>> call, Throwable t) {
				Toast.makeText(getActivity(), ServiceGenerator.getExceptionMsgByCause(t), Toast.LENGTH_LONG).show();
			}
		});
	}

	private void retriveContentList(List<BeanContent> beanContentList) {
		final int size = beanContentList.size();
		if(size != 0 && beanContentList.get(size - 1).cot != MainCons.EnumContentType.TEXT.getValue()) {
			final BeanContent beanContent = new BeanContent();
			beanContent.cot = MainCons.EnumContentType.TEXT.getValue();
			beanContentList.add(beanContent);
		}

		rva.appendItems(beanContentList);
	}

	public class MyRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
		private final ImageLoader imageLoader = ImageLoader.getInstance();

		private final List<BeanContent> beanContentList;
		private final List<BeanDeleted> beanDeletedList;
		private final Context ctx;

		private final ViewOnClickListener viewOnClickListener;

		public MyRecyclerAdapter(final Context ctx) {
			//onAttachedToRecyclerView()에서 rv를 이용하여 Context를 구할 수 있으나 Attached 이전에
			//Context는 참조할 수 없으므로 인자로 전달 받는다.

			this.ctx = ctx;
			this.beanContentList = new ArrayList<>();
			this.beanDeletedList = new ArrayList<>();

			viewOnClickListener = new ViewOnClickListener(ctx, new ViewOnClickListener.OnClickListener() {
				@Override
				public void onViewClick(final View v, Point touchPoint, final Point rawTouchPoint) {
					final List<String> menuItemList = new ArrayList<>();
					final int pos = CommonUtil.getAdapterPositionFromView(rv, v);

					if(pos == 0 || (beanContentList.get(pos - 1)).cot != MainCons.EnumContentType.TEXT.getValue()) {
						menuItemList.add("윗 행에 내용 삽입하기");
					}

					if(pos < beanContentList.size() -1 && (beanContentList.get(pos + 1)).cot != MainCons.EnumContentType.TEXT.getValue()) {
						menuItemList.add("아래 행에 내용 삽입하기");
					}

					menuItemList.add("삭제하기");

					final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

					builder.setItems(menuItemList.toArray(new String[menuItemList.size()]), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int clickPos) {
							final String menuItem = menuItemList.get(clickPos);

							if(menuItem.contains("삭제")) {
								removeItem(pos);
							} else {
								final int insertPos = menuItem.contains("윗") ? pos : pos + 1;
								final BeanContent beanContent = new BeanContent();
								beanContent.cot = MainCons.EnumContentType.TEXT.getValue();
								insertItem(insertPos, beanContent);
							}
						}
					});
					builder.setCancelable(true);
					final AlertDialog ad = builder.create();
					ad.show();
				}

				@Override
				public void onViewLongPress(View v, Point touchPoint, Point rawTouchPoint) {

				}
			});
		}

		private void saveData() {
			//1. 순서가 변경되는 곳곳에서 별도 갱신하지 않고 저장시에만 일괄 갱신한다
			//2. 이렇게 되면 문제는 아무런 변경이 없어도 모든 정보를 WAS에 전달해야 한다.
			//3. 차차 정리되면 update, insert한 행들만 저장하도록 변경할 때 함께 손 볼 것
			updateSn();

			final String tt = et_tt.getText().toString().trim();

			if(tt.isEmpty()) {
				Toast.makeText(getActivity(), "제목을 입력하셔야 됩니다.", Toast.LENGTH_LONG).show();
				return;
			}

			final int selectdBoard_id = sp_bn.getSelectedItemPosition() + 1;

			beanPost.board_id = selectdBoard_id;
			beanPost.owner_id = owner_id;
			beanPost.cn = owner_cn;
			beanPost.member_id = beanMember.id;
			beanPost.parent_id = 0;
			beanPost.tt = tt;


			/*
			beanBoardeList.add(new BeanBoarde(1, "공지사항"));
			beanBoardeList.add(new BeanBoarde(2, "수리의뢰"));
			beanBoardeList.add(new BeanBoarde(3, "서비스사례"));
			beanBoardeList.add(new BeanBoarde(4, "고객리뷰"));
			beanBoardeList.add(new BeanBoarde(5, "이벤트"));
			beanBoardeList.add(new BeanBoarde(6, "중고차매물"));
			beanBoardeList.add(new BeanBoarde(7, "벼룩시장"));
			beanBoardeList.add(new BeanBoarde(8, "경정비상식"));
			beanBoardeList.add(new BeanBoarde(9, "블로그소개"));
			beanBoardeList.add(new BeanBoarde(10, "Q/A"));
			beanBoardeList.add(new BeanBoarde(11, "자유게시판"));
			*/

			if(selectdBoard_id == 1 || selectdBoard_id == 3 || selectdBoard_id == 9) {
				if(!(beanMember.id == 1 || beanMember.id == 2 || owner_id == beanMember.id)) {
					Toast.makeText(getActivity(), "글 쓰기 권한이 없는 게시판입니다", Toast.LENGTH_LONG).show();
					return;
				}
			}

			if(selectdBoard_id == 4) {
				if(!beanMember.sr.equals("0")) {
					Toast.makeText(getActivity(), "글 쓰기 권한이 없는 게시판입니다", Toast.LENGTH_LONG).show();
					return;
				}
			}




			//1. 유효성 검증 및 빈 글 삭제
			//   단, 최소 1개의 텍스트가 있을 경우만 빈 글 삭제한 후 저장하고 그렇지 않은 경우는 빈 글을 삭제하면 안된다
			//2. 대표 텍스트/대표 이미지 추출
			boolean isValid = false;
			String fcb = "";
			String ffn = "";
			for(BeanContent beanContent : beanContentList) {
				final String cob = beanContent.cob.trim();

				if(beanContent.cot == MainCons.EnumContentType.TEXT.getValue()) {
					if(fcb.isEmpty() && !cob.isEmpty()) {
						fcb = cob;
						isValid = true;
					}
				} else {
					if(ffn.isEmpty()) {
						ffn = cob;
						isValid = true;
					}
				}
			}

			if(!isValid) {
				Toast.makeText(getActivity(), "저장할 내용이 없습니다.", Toast.LENGTH_LONG).show();
				return;
			}

			beanPost.fcb = fcb;
			beanPost.ffn = ffn;

			if(beanPost.board_id == 9 && ffn.isEmpty()) {
				Toast.makeText(getActivity(), "업체소개 포스팅은 사진이 필요합니다", Toast.LENGTH_LONG).show();
				return;
			}

			//빈글 삭제
			boolean isDeleted = false;
			Iterator<BeanContent> iterator = beanContentList.iterator();
			while (iterator.hasNext()) {
				final BeanContent beanContent = iterator.next();
				if(beanContent.cot == MainCons.EnumContentType.TEXT.getValue()) {
					if(beanContent.cob.trim().isEmpty()) {
						final int id = beanContent.id;

						isDeleted = true;
						iterator.remove();

						if(id != 0) {
							beanDeletedList.add(new BeanDeleted(id));
						}
					}
				}
			}

			if(isDeleted) {
				//1. 혹시 저장오류 발생할 경우 현재 상태 유지
				//2. 성공하면 loadData() 재호출 하므로 의미없슴
				notifyDataSetChanged();
			}

			final ProgressDialog pd = ProgressDialog.show(getActivity(), "", "데이타 및 사진을 저장하고 있습니다", true);
			final RetrofitInterface.PostService service = ServiceGenerator.createService(RetrofitInterface.PostService.class);
			final BeanPostAndContents_test beanPostAndContents_test = new BeanPostAndContents_test(beanPost, beanContentList, beanDeletedList);

			final Call<Integer> call = beanPost.id == 0 ? service.insert(beanPostAndContents_test) : service.update(beanPostAndContents_test);
			call.enqueue(new Callback<Integer>() {
				@Override
				public void onResponse(Call<Integer> call, Response<Integer> response) {
					if(response.isSuccessful()) {
						if(beanPost.id == 0) {
							beanPost.id = response.body();
							beanPost.nn = beanMember.nn;
							beanPost.ud = CommonUtil.getFormattedDate(CoreCons.EnumDateFormat.DATE_AND_TIME);
						}

						uploadFile(pd);
					}
					else {
						pd.dismiss();
						final String msg = "데이터 저장에 실패하였습니다\n" + response.errorBody().source().toString();

						Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
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

		private void uploadFile(final ProgressDialog pd) {
			final MultipartBody.Builder builder = new MultipartBody.Builder();
			builder.setType(MultipartBody.FORM);
			int count = 0;

			try {
				for(BeanContent bc : beanContentList) {
					if(bc.id == 0 && (bc.cot == 2 || bc.cot == 3)) {
						count++;

						final File file = new File(bc.realUrl);

						final RequestBody requestBody = RequestBody.create(MediaUtil.getMediaType(bc.realUrl), file);
						builder.addFormDataPart("fileList", bc.cob, requestBody);
						builder.addFormDataPart("pathIdList", bc.cot == 2 ? MainCons.EnumContentPath.CONTENT_I.getId()+"" : MainCons.EnumContentPath.CONTENT_V.getId()+"");

						if(bc.cot == 3) {
							final Bitmap bm = MediaUtil.createThumbnailFromVideFile(bc.realUrl);
							final File thumbnail_file = MediaUtil.saveBitmaptoJpeg(bm);

							final RequestBody requestBody_t = RequestBody.create(MediaType.parse("image/jpeg"), thumbnail_file);
							builder.addFormDataPart("fileList", bc.cob.replace("mp4", "jpg"), requestBody_t);
							builder.addFormDataPart("pathIdList", MainCons.EnumContentPath.CONTENT_V_T.getId()+"");
						}
					}
				}

				if(count == 0) {
					pd.dismiss();
					closeActivity(1);

					return;
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
							closeActivity(1);
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

		private void closeActivity(int type) {
			final Intent intent = new Intent();

			if(type == 1) {
				//1. 성공적인 저장처리 후 신규 또는 수정 post를 이전화면에 반영하기 위해 intnt전달과 함께 Activity를 종료한다

			} else {
				//삭제
				beanPost.id = -1;
			}

			intent.putExtra("beanPost", beanPost);
			getActivity().setResult(Activity.RESULT_OK, intent);
			getActivity().finish();
		}

		private void applyNewFileItem(BeanContent newBeanContent) {
			//1. 겔러리에서파일 선택 후의 ActivityResut()에 호출

			BeanContent lastBeanContent = beanContentList.get(beanContentList.size() - 1);

			if(lastBeanContent.cob.isEmpty()) {
				//마지막 내용입력 edittext가 비어있을 경우 그 앞으로 파일을 insert한다
				rva.insertItem(beanContentList.size() - 1, newBeanContent);
			} else {
				//파일 insert후 마지막에 새로운 입력 item 유지
				appendItem(newBeanContent);
				appendItem(new BeanContent()); //마지막에는 항상 내용 입력 content
			}
		}

		private void appendItem(BeanContent newBeanContent) {
			this.beanContentList.add(newBeanContent);
			notifyItemInserted(this.beanContentList.size() - 1);
			rv.scrollToPosition(this.beanContentList.size() - 1);
		}

		private void insertItem(int insertPos, BeanContent newBeanContent) {
			this.beanContentList.add(insertPos, newBeanContent);
			notifyItemInserted(insertPos);
			rv.scrollToPosition(this.beanContentList.size() - 1);
		}

		private void updateSn() {
			//1. UI와는 전혀 관계없는 단순 update이
			float i = 0;

			for(BeanContent bc : beanContentList) {
				i++;
				bc.sn = i;
			}
		}

		private void appendItems(List<BeanContent> beanContentList) {
			//저장 후 retieve
			if(this.beanContentList.size() != 0) {
				this.beanContentList.clear();
				notifyDataSetChanged();
			}

			if(this.beanDeletedList.size() != 0) this.beanDeletedList.clear();

			this.beanContentList.addAll(beanContentList);
			notifyDataSetChanged();
		}

		public void removeItem(int pos) {
			final int id = beanContentList.get(pos).id;

			if(id != 0) {
				beanDeletedList.add(new BeanDeleted(id));
			}

			beanContentList.remove(pos);
			notifyItemRemoved(pos);
		}

		@Override
		public int getItemCount() {
			return beanContentList.size();
		}

		@Override
		public long getItemId(int pos) {
			//getItemId() method는 ViewHolder로 상속된다. - 필요한가?
			return beanContentList.get(pos).id;
		}

		@Override
		public int getItemViewType(int pos) {
			return beanContentList.get(pos).cot; //content type = ViewType = ViewHolder type
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup vg, int viewHolderType) {
			final RecyclerView.ViewHolder vh;

			if(viewHolderType == MainCons.EnumContentType.TEXT.getValue()) {
				final VhEdit1 vhEdit1 = new VhEdit1(LayoutInflater.from(ctx).inflate(R.layout.vh_content_edittext, vg, false));

				vhEdit1.et_31.addTextChangedListener(new TextWatcher() {
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {}

					@Override
					public void afterTextChanged(Editable s) {
						final BeanContent beanContent = beanContentList.get(vhEdit1.getAdapterPosition());
						beanContent.cob = s.toString();
					}
				});

				/*
				vhEdit1.et_31.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View view, boolean bFocus) {
						if(!bFocus) {
							if(vhEdit1.et_31.getText().toString().isEmpty()) {
								removeItem(vhEdit1.getAdapterPosition());
							}
						}
					}
				});
				*/

				vh = vhEdit1;
			} else if(viewHolderType == MainCons.EnumContentType.PHOTO.getValue()) {
				final VhImage1 vhImage1 = new VhImage1(LayoutInflater.from(ctx).inflate(R.layout.vh_content_editphoto, vg, false));
				////////////vhImage1.iv_31.setOnTouchListener(viewOnClickListener);
				vhImage1.itemView.setOnTouchListener(viewOnClickListener);
				vh = vhImage1;
			}
			else if(viewHolderType == MainCons.EnumContentType.VIDEO.getValue()) {
				final VhImage1 vhImage1 = new VhImage1(LayoutInflater.from(ctx).inflate(R.layout.vh_content_editvideo, vg, false));
				////////////vhImage1.iv_31.setOnTouchListener(viewOnClickListener);
				vhImage1.itemView.setOnTouchListener(viewOnClickListener);
				vh = vhImage1;
			} else {
				//임시
				vh = null;
			}

			return vh;
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder vh, int pos) {
			final BeanContent beanContent = beanContentList.get(pos);

			final int viewHolderType = getItemViewType(pos);

			if(viewHolderType == MainCons.EnumContentType.TEXT.getValue()) {

				final VhEdit1 vhEdit1 = (VhEdit1)vh;
				vhEdit1.et_31.setText(beanContent.cob);
			} else if(viewHolderType == MainCons.EnumContentType.PHOTO.getValue()) {
				final VhImage1 vhImage1 = (VhImage1) vh;
				final String url;

				if (beanContent.id == 0) {
					final File file = new File(beanContent.realUrl);
					final Uri uri = Uri.fromFile(file);

					//파일명이 한글일 경우 uri는 이미 encoding되어 있다

					url = Uri.decode(uri.toString());
				} else {
					url = MainCons.EnumContentPath.CONTENT_I.getPath() + beanContent.cob;
				}

				imageLoader.displayImage(url, vhImage1.iv_31);
			} else if(viewHolderType == MainCons.EnumContentType.VIDEO.getValue()) {
				final VhImage1 vhImage1 = (VhImage1)vh;
				final String url;

				if (beanContent.id == 0) {
					final File file = new File(beanContent.realUrl);
					final Uri uri = Uri.fromFile(file);
					url = Uri.decode(uri.toString());
				} else {
					url = MainCons.EnumContentPath.CONTENT_V_T.getPath() + beanContent.cob.replace("mp4", "jpg");
				}

				imageLoader.displayImage(url, vhImage1.iv_31);
			} else {
				//임시

			}

		}
	}
}