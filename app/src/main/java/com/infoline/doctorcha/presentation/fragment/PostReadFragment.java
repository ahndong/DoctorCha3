package com.infoline.doctorcha.presentation.fragment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Point;
import android.graphics.PointF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.CoreCons;
import com.infoline.doctorcha.core.httphelper.ServiceGenerator;
import com.infoline.doctorcha.core.listener.ViewOnClickListener;
import com.infoline.doctorcha.core.util.CommonUtil;
import com.infoline.doctorcha.core.util.MediaUtil;
import com.infoline.doctorcha.core.util.PermissionUtil;
import com.infoline.doctorcha.presentation.MainApp;
import com.infoline.doctorcha.presentation.MainCons;
import com.infoline.doctorcha.presentation.RetrofitInterface;
import com.infoline.doctorcha.presentation.activity.FragmentContainerActivity;
import com.infoline.doctorcha.presentation.activity.ShopActivity;
import com.infoline.doctorcha.presentation.bean.BeanBoarde;
import com.infoline.doctorcha.presentation.bean.BeanContent;
import com.infoline.doctorcha.presentation.bean.BeanErrResponse;
import com.infoline.doctorcha.presentation.bean.BeanMemberAndShop;
import com.infoline.doctorcha.presentation.bean.BeanPost;
import com.infoline.doctorcha.presentation.viewholder.VhImage1;
import com.infoline.doctorcha.presentation.viewholder.VhPostHeader;
import com.infoline.doctorcha.presentation.viewholder.VhText1;
import com.kakao.KakaoLink;
import com.kakao.KakaoParameterException;
import com.kakao.KakaoTalkLinkMessageBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.sendbird.android.SendBird;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.infoline.doctorcha.presentation.MainApp.beanBoardeList;
import static com.infoline.doctorcha.presentation.MainApp.beanMember;
import static com.infoline.doctorcha.presentation.MainApp.beanMember;
import static com.infoline.doctorcha.presentation.MainCons.CN_PATH_DOCTORCHA_DOWNLOAD;
import static com.infoline.doctorcha.presentation.MainCons.CN_REQUEST_ADDRESS_BY_GPS;
import static com.infoline.doctorcha.presentation.MainCons.CN_REQUEST_GALLARY;

public class PostReadFragment extends Fragment {
	private MyRecyclerAdapter rva;

	@BindView(R.id.rv)
	RecyclerView rv;

	@BindView(R.id.tv_modi)
	TextView tv_modi;

	@BindView(R.id.tv_go)
	TextView tv_go;

	@BindView(R.id.fl_progress)
	FrameLayout fl_progress;

	@BindView(R.id.tv_progress)
	TextView tv_progress;

	BeanPost beanPost;
	File downLoadedFile;

	int sr;
	int replCount;

	ImageView imageView;

	@OnClick({R.id.iv_share_kakaotalk, R.id.iv_send_fcm, R.id.tv_go, R.id.tv_repl, R.id.tv_modi})
	protected void click1(View v) {
		switch (v.getId()) {
			case R.id.iv_share_kakaotalk:
				try {
					/*
					try {
						final PackageInfo pi = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), PackageManager.GET_SIGNATURES);

						for(Signature s : pi.signatures) {
							MessageDigest md = MessageDigest.getInstance("SHA");
							md.update(s.toByteArray());

							final String xxx = Base64.encodeToString(md.digest(), Base64.DEFAULT);
							Toast.makeText(getActivity(), xxx, Toast.LENGTH_SHORT);
						}
					} catch (PackageManager.NameNotFoundException e1) {


					} catch (NoSuchAlgorithmException e2) {

					}
					*/

					final KakaoLink kakaoLink = KakaoLink.getKakaoLink(getActivity());
					final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();

					//String text = "안희정의 친구들\n국민선거인단 등록  1811-1000";

					final String tt = beanPost.tt;
					final String fcb = beanPost.fcb;
					final String ffn = beanPost.ffn;

					if(tt.isEmpty() && fcb.isEmpty() && ffn.isEmpty()) {
						Toast.makeText(getActivity(), "전송할 내용이 없습니다", Toast.LENGTH_SHORT).show();
						return;
					}

					if(!(tt.isEmpty() && fcb.isEmpty())) {
						kakaoTalkLinkMessageBuilder.addText(tt + (tt.isEmpty() || fcb.isEmpty() ? "" : "\n\n") + beanPost.fcb);
					}

					if(!beanPost.ffn.isEmpty()) {
						//1. 2Mb 이상 전송은 되나 카카오톡에서 image 액박으로 나옴
						//2. 2000, 2000 전송은 되나 카카오톡에서 image 액박으로 나옴
						//3. 1920, 1080 정상 처리
						//kakaoTalkLinkMessageBuilder.addImage(imageName, resultImageView.getDrawable().getIntrinsicWidth(), resultImageView.getDrawable().getIntrinsicHeight())
						int w = imageView.getWidth();   //getDrawable().getIntrinsicWidth()
						int h = imageView.getHeight();  //getDrawable().getIntrinsicHeight()
						kakaoTalkLinkMessageBuilder.addImage(MainCons.EnumContentPath.CONTENT_I.getPath() + beanPost.ffn, w, h);
					}

					kakaoTalkLinkMessageBuilder.addWebLink("닥터차 홈페이지로 이동");
					kakaoTalkLinkMessageBuilder.addAppButton("닥터차 앱으로 이동");
					final String linkContents = kakaoTalkLinkMessageBuilder.build();

					kakaoLink.sendMessage(linkContents, getActivity());

				} catch (KakaoParameterException e) {
					Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.iv_send_fcm:
				final String[] items = { "전체 회원", "개인 회원", "업체 회원", "닥터차 상담사", "회원 선택"};
				final String[] topics = { "global", "person", "shop", "counsellor", "ids"};
				final AlertDialog ad;
				final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

				builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int pos) {
						if(pos == 4) {
							final Intent intent = new Intent(getActivity(), FragmentContainerActivity.class);
							intent.putExtra(MainCons.EnumExtraName.NAME1.name(), PushTargetListFragment.class.getSimpleName());
							intent.putExtra("member_id", sr == 1 || sr == 9 ? 0 : beanMember.id);
							startActivityForResult(intent, 777);
							getActivity().overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
						} else {
							beanPost.topic = topics[pos];
							sendPush();
						}
					}
				});

				ad = builder.create();
				ad.show();

				break;
			case R.id.tv_go:
				//업체 브로그 홈으로 가기
				final Intent intent = new Intent(getActivity(), ShopActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				final MainCons.EnumActivityAnimType enumActivityAnimType;

				enumActivityAnimType = MainCons.EnumActivityAnimType.LEFTINRIGHTOUT;

				intent.putExtra(MainCons.EnumActivityAnimType.class.getSimpleName(), enumActivityAnimType);
				intent.putExtra("owner_id", beanPost.owner_id);
				//intent.putExtra(BeanMemberAndShop.class.getSimpleName(), null);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);

				break;
			case R.id.tv_modi:
				final Intent intent2 = new Intent(getActivity(), FragmentContainerActivity.class);

				intent2.putExtra(MainCons.EnumExtraName.NAME1.name(), PostEditFragment.class.getSimpleName());
				intent2.putExtra("board_id", beanPost.board_id);
				intent2.putExtra("beanPost", beanPost);

				startActivityForResult(intent2, 800);
				getActivity().overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);

				break;
			case R.id.tv_repl:
				final Intent intent1 = new Intent(getActivity(), FragmentContainerActivity.class);
				intent1.putExtra(MainCons.EnumExtraName.NAME1.name(), PostReplFragment.class.getSimpleName());
				intent1.putExtra("beanPost", beanPost);
				startActivityForResult(intent1, 803);
				getActivity().overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);

				break;
		}
	}

	private void sendPush() {
		final RetrofitInterface.FcmService service = ServiceGenerator.createService(RetrofitInterface.FcmService.class);
		final String xxx = beanPost.topic.substring(0, beanPost.topic.length());
		//final Call<Void> call = service.sendToTopics(beanPost);
		final Call<Void> call = beanPost.topic.substring(0, 1).equals("[") ? service.sendtoMultiIds(beanPost) : service.sendToTopics(beanPost);

		call.enqueue(new Callback<Void>() {
			@Override
			public void onResponse(Call<Void> call, Response<Void> response) {
				final String msg;

				if(response.isSuccessful()) {
					msg = "푸시 알림이 성공적으로 전송되었습니다";
				} else {
					final BeanErrResponse beanErrResponse = new BeanErrResponse(response.errorBody().source().toString()); //{ec:%s,em:'%s',sv:'%s'}

					//1. 잘못된 nn 또는 upw 이외의 원인으로 인한 오류 메세지
					//2. 404 같은 경우에는 beanErrResponse 자체가 null이다
					msg = beanErrResponse.em == null ? String.format("[%s]%s", response.code(), response.message()) : beanErrResponse.em;

				}

				Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
			}

			@Override
			public void onFailure(Call<Void> call, Throwable t) {
				Toast.makeText(getActivity(), ServiceGenerator.getExceptionMsgByCause(t), Toast.LENGTH_LONG).show();
			}
		});
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 803) {
			//댓글화면에서 돌아오는 경우
			//PostReplFragment의 FragmentContainerActivity에서 편법으로 전달된다
			replCount += data.getIntExtra("replCount", 0); //댓글작성 화면에 여러번 들어갈 수도 있다

			if(replCount != 0) {
				beanPost.coc += replCount;
				rva.notifyItemChanged(0);

				//백키를 누를 경우 PostListFragment의 FragmentContainerActivity에 다시 전달해야 PostListFragment의 onActivityResult()에서
				//해당 post의 coc를 변경할 수 있다
				((FragmentContainerActivity)getActivity()).replPlus(replCount);
			}
		}

		if (resultCode != RESULT_OK) return;

		if (requestCode == 800) {
			//수정 화면에서 돌아오는 경우
			final BeanPost modifiedBeanPost = (BeanPost)data.getSerializableExtra("beanPost");
			final Intent intent = new Intent();

			if(modifiedBeanPost.id == -1) {
				//삭제후 돌아옴
				beanPost.id = -1;
			} else {
				//변경하고 저장 후 돌아옴
				beanPost.tt = modifiedBeanPost.tt;
				beanPost.fcb = modifiedBeanPost.fcb;
				beanPost.ffn = modifiedBeanPost.ffn;

				beanPost.rec += 1;
			}

			//1. replCount 이놈은 BackKey가 아닌 저장버튼에 의해 프로그램적으로 finish()될 경우를 위해 추가
			//2. FragmentContainerActivity의 finish()에 자세히 설명되어 있슴
			intent.putExtra("replCount", replCount);
			intent.putExtra("beanPost", beanPost);
			getActivity().setResult(Activity.RESULT_OK, intent);
			getActivity().finish();
		} else if (requestCode == 777) {
			final ArrayList<String> gtList = data.getStringArrayListExtra("gtList");
			beanPost.topic = gtList.toString();
			sendPush();
		}
	}

	public PostReadFragment() {

	}

	public static PostReadFragment newInstance(final BeanPost beanPost) {
		final PostReadFragment fragment = new PostReadFragment();

		final Bundle bundle = new Bundle();

		bundle.putSerializable("beanPost", beanPost);
		fragment.setArguments(bundle);

		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_postread, container, false);
		ButterKnife.bind(this, rootView);

		final Bundle bundle = getArguments();
		beanPost = (BeanPost)bundle.getSerializable("beanPost");

		sr = Integer.valueOf(beanMember.sr);

		if(beanMember.id == 2 || beanMember.id == beanPost.member_id || sr == 9) {
			tv_modi.setVisibility(View.VISIBLE);
		}

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
		final RetrofitInterface.ContentService service = ServiceGenerator.createService(RetrofitInterface.ContentService.class);
		final Call<List<BeanContent>> call = service.selectByPostId(beanPost.id);
		call.enqueue(new Callback<List<BeanContent>>() {
			@Override
			public void onResponse(Call<List<BeanContent>> call, Response<List<BeanContent>> response) {
				if(response.isSuccessful()) {
					retrieveData(response.body());
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
			public void onFailure(Call<List<BeanContent>> call, Throwable t) {
				Toast.makeText(getActivity(), ServiceGenerator.getExceptionMsgByCause(t), Toast.LENGTH_LONG).show();
			}
		});
	}

	private void retrieveData(List<BeanContent> beanContentList) {
		rva.appendItems(beanContentList);
	}

	public class MyRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
		private final ImageLoader imageLoader = ImageLoader.getInstance();
		private final DisplayImageOptions optionsForOvalThumb;

		private final List<BeanContent> beanContentList;
		private final Context ctx;

		private final ViewOnClickListener viewOnClickListener;

		public MyRecyclerAdapter(final Context ctx) {
			//onAttachedToRecyclerView()에서 rv를 이용하여 Context를 구할 수 있으나 Attached 이전에
			//Context는 참조할 수 없으므로 인자로 전달 받는다.

			this.ctx = ctx;
			this.beanContentList = new ArrayList<>();

			optionsForOvalThumb = new DisplayImageOptions.Builder()
					.considerExifParams(true)
					.cacheInMemory(true)
					.cacheOnDisk(true)
					.displayer(new RoundedBitmapDisplayer(100))
					.showImageOnFail(R.drawable.ic_profile_member)
					.build();

			viewOnClickListener = new ViewOnClickListener(ctx, new ViewOnClickListener.OnClickListener() {
				@Override
				public void onViewClick(final View v, Point touchPoint, final Point rawTouchPoint) {
					final int pos = CommonUtil.getAdapterPositionFromView(rv, v);
					final BeanContent beanContent = beanContentList.get(pos);

					if(v.getId() == R.id.iv_profile) {
						//글쓴이 브로그 홈으로 가기
						final Intent intent = new Intent(getActivity(), ShopActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						final MainCons.EnumActivityAnimType enumActivityAnimType;

						enumActivityAnimType = MainCons.EnumActivityAnimType.ANIM_THUMBNAIL_SCALE_UP;

						intent.putExtra(MainCons.EnumActivityAnimType.class.getSimpleName(), enumActivityAnimType);
						intent.putExtra("owner_id", beanPost.member_id);
						//intent.putExtra(BeanMemberAndShop.class.getSimpleName(), null);
						startActivity(intent);
						getActivity().overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
					} else {
						//image or video

						final MainCons.EnumContentPath enumContentPath = beanContent.cot == 2 ? MainCons.EnumContentPath.CONTENT_I : MainCons.EnumContentPath.CONTENT_V;
						final Uri uri = Uri.parse(enumContentPath.getPath() + beanContent.cob);

						final Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setDataAndType(uri, beanContent.cot == 3 ? "video/*" : "image/*");
						startActivityForResult(intent, CN_REQUEST_GALLARY);
					}
				}

				@Override
				public void onViewLongPress(final View v, Point touchPoint, Point rawTouchPoint) {
					/* 일단 clipboard에 넣기까지는 성공
                    final ClipboardManager ClipMan = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData;
                    final ContentValues values = new ContentValues(2);
                    final File file = new File("/storage/emulated/0/DCIM/Camera/20161230_012238.jpg");
                    values.put(MediaStore.Images.Media.MIME_TYPE, "Image/jpg");
                    values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                    final ContentResolver theContent = getActivity().getContentResolver();
                    final Uri imageUri = theContent.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Uri cc = Uri.fromFile(file);
                    /////////clipData = ClipData.newUri(getActivity().getContentResolver(), "Image", imageUri); //오류발생
                    clipData = ClipData.newUri(getActivity().getContentResolver(), "Image", cc);
                    ClipMan.setPrimaryClip(clipData);
                    */

					final Vibrator vibrator = (Vibrator)ctx.getSystemService(Context.VIBRATOR_SERVICE);
					vibrator.vibrate(50);

					final int pos = CommonUtil.getAdapterPositionFromView(rv, v);
					final BeanContent beanContent = rva.beanContentList.get(pos);

					final boolean isText = v instanceof TextView;

					//TODO : 전달 기능 고민할 것
					//final String[] itemsText = { "복사", "공유", "전달"};
					//final String[] itemsFile = { "다운로드", "공유", "전달"};
					final String[] itemsText = { "복사", "공유"};
					final String[] itemsFile = { "다운로드", "공유"};

					final AlertDialog ad;
					final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

					builder.setItems(isText ? itemsText : itemsFile, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int pos) {
							switch (pos) {
								case 0:
									if(isText) {
										final ClipboardManager ClipMan = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
										final ClipData clipData = ClipData.newPlainText("GoutCare", ((TextView)v).getText());
										ClipMan.setPrimaryClip(clipData);

										//복사완료 메세지가 자동으로 안나오네. 위 이미지 복사 sample에서는 '클립보드에 복사되었습니다' 나온다. 일단 수동으로
										Toast.makeText(getActivity(), "클립보드에 복사 되었습니다", Toast.LENGTH_SHORT).show();
									} else {
										downloadFile(beanContent, 1);
									}
									break;
								case 1:
									if(isText) {
										final Intent intent = new Intent(Intent.ACTION_SEND);
										intent.setType("text/plain");
										//intent.putExtra(Intent.EXTRA_SUBJECT, "제목);
										intent.putExtra(Intent.EXTRA_TEXT, ((TextView)v).getText());
										//
										Intent chooser = Intent.createChooser(intent, null);
										startActivity(chooser);
									} else {
										downloadFile(beanContent, 2);
									}
							}
						}
					});

					ad = builder.create();
					ad.show();
				}
			});
		}

		private void downloadFile(final BeanContent beanContent, final int downloadType) {
			if(!PermissionUtil.requestWriteStoragePermissions(getActivity())) {
				return;
			}

			fl_progress.setVisibility(View.VISIBLE);

			final int cot = beanContent.cot; //1.text 2.image 3.video

			final String serverFilePath = (cot == 3 ? MainCons.EnumContentPath.CONTENT_V.getPath() : MainCons.EnumContentPath.CONTENT_I.getPath()) + beanContent.cob;
			final RetrofitInterface.DownloadService service = ServiceGenerator.createService(RetrofitInterface.DownloadService.class);
			final Call<ResponseBody> call = service.files(serverFilePath);

			call.enqueue(new Callback<ResponseBody>() {
				@Override
				public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
					String msg;

					if(response.isSuccessful()) {
						new AsyncTask<Void, Void, Boolean>() {
							@Override
							protected void onPreExecute() {
								super.onPreExecute();
							}

							@Override
							protected Boolean doInBackground(Void... params) {
								return writeResponseBodyToDisk(response.body(), serverFilePath, downloadType);
							}

							@Override
							protected void onPostExecute(Boolean result) {
								super.onPostExecute(result);
								fl_progress.setVisibility(View.GONE);

								if(result && downloadType == 2) {
									//2 = sharing
									final Intent intent = new Intent(Intent.ACTION_SEND);
									intent.setType(cot == MainCons.CN_CHAT_MT_VIDEO ? "video/*" : "image/*");
									intent.putExtra(Intent.EXTRA_STREAM, cot == MainCons.CN_CHAT_MT_VIDEO ? MediaUtil.getContentUriFromVideoFile(getActivity(), downLoadedFile) : MediaUtil.getContentUriFromImageFile(getActivity(), downLoadedFile));
									startActivity(intent);
								}
							}

							@Override
							protected void onCancelled() {
								super.onCancelled();
							}
						}.execute();
					} else {
						fl_progress.setVisibility(View.GONE);
						msg = String.format("[%s]%s", response.code(), response.raw().toString());
						Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
					}
				}

				@Override
				public void onFailure(Call<ResponseBody> call, Throwable t) {
					final String msg;

					if (call.isCanceled()) {
						msg = "다운로드가 취소 되었습니다";
					}
					else {
						msg = ServiceGenerator.getExceptionMsgByCause(t);
					}

					fl_progress.setVisibility(View.GONE);
					Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
				}
			});
		}

		private boolean writeResponseBodyToDisk(ResponseBody body, final String serverFilePath, final int downloadType) {
			//1.downloadType ---> 1.download  2.shareing
            /*
            File.createTempFile(prefix, suffix)
            - 환경 변수로 지정된 tmp 디렉토리에 자동으로 생성된다.

            File.createTempFile(prefix, suffix, directory)
            - directory로 지정된 폴더로 임시파일이 생성된다.

            deleteOnExit()
            - 해당 메소드를 호출 하면 jvm이 종료 될때 자동으로 임시파일이 삭제된다.
             */
			try {
				final String saveFileName = CommonUtil.getFormattedDate(CoreCons.EnumDateFormat.UNIQUE_FILENAME);
				final String extName = CommonUtil.getExtName(serverFilePath);
				final File file;

				if(downloadType == 1) {
					file = new File(CN_PATH_DOCTORCHA_DOWNLOAD, saveFileName + "." + extName);
				} else {
					file = File.createTempFile(saveFileName, "." + extName, new File(CN_PATH_DOCTORCHA_DOWNLOAD));
				}

				InputStream inputStream = null;
				OutputStream outputStream = null;

				try {
					byte[] fileReader = new byte[8192];

					final long fileSize = body.contentLength();
					long fileSizeDownloaded = 0;

					inputStream = body.byteStream();

					//1.WRITE_EXTERNAL_STORAGE Permission : 기기, 사진, 미디어, 파일 엑세스
					outputStream = new FileOutputStream(file);

					while (true) {
						//android.os.NetworkOnMainThreadException
						int read = inputStream.read(fileReader);

						if (read == -1) {
							break;
						}

						outputStream.write(fileReader, 0, read);

						fileSizeDownloaded += read;

						//Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
						final long xx = fileSizeDownloaded;

                        /*
                        tv_progress.post(new Runnable() {
                            @Override
                            public void run() {
                                tv_progress.setText(((xx * 100) / fileSize)  +  "%");
                                tv_progress.invalidate();
                            }
                        });
                        */

						getActivity().runOnUiThread(new Runnable(){
							@Override
							public void run(){
								tv_progress.setText(((xx * 100) / fileSize)  +  "%");
								//tv_progress.invalidate();
							}
						});
					}

					outputStream.flush();
					downLoadedFile = file;
					//createdTempUri = Uri.fromFile(file);

					return true;
				} catch (Exception e) {
					return false;
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}

					if (outputStream != null) {
						outputStream.close();
					}
				}
			} catch (Exception e) {
				return false;
			}
		}

		private void appendItems(List<BeanContent> beanContentList) {
			if(beanContentList.size() != 0) {
				this.beanContentList.clear();
				notifyDataSetChanged();
			}

			//Post Header를 위한 dummy BeanContent
			beanContentList.add(0, new BeanContent());

			this.beanContentList.addAll(beanContentList);
			notifyDataSetChanged();
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
			//1. post = 0 : post Header
			//2. 1. text 2.photo 3.video
			return pos == 0 ? 0 : beanContentList.get(pos).cot;
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup vg, int viewHolderType) {
			final RecyclerView.ViewHolder vh;

			if(viewHolderType == 0) {
				final VhPostHeader vhPostHeader = new VhPostHeader(LayoutInflater.from(ctx).inflate(R.layout.vh_content_readheader, vg, false));
				vhPostHeader.iv_profile.setOnTouchListener(viewOnClickListener);
				vh = vhPostHeader;
			} else {
				if(viewHolderType == MainCons.EnumContentType.TEXT.getValue()) {
					final VhText1 vhText1 = new VhText1(LayoutInflater.from(ctx).inflate(R.layout.vh_content_readtext, vg, false));
					vhText1.tv_31.setOnTouchListener(viewOnClickListener);
					vh = vhText1;
				} else if(viewHolderType == MainCons.EnumContentType.PHOTO.getValue()) {
					final VhImage1 vhImage1 = new VhImage1(LayoutInflater.from(ctx).inflate(R.layout.vh_content_readphoto, vg, false));
					vhImage1.iv_31.setOnTouchListener(viewOnClickListener);
					vh = vhImage1;
				}
				else if(viewHolderType == MainCons.EnumContentType.VIDEO.getValue()) {
					final VhImage1 vhImage1 = new VhImage1(LayoutInflater.from(ctx).inflate(R.layout.vh_content_readvideo, vg, false));
					vhImage1.iv_31.setOnTouchListener(viewOnClickListener);
					vh = vhImage1;
				} else {
					//임시
					vh = null;
				}
			}

			return vh;
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder vh, int pos) {
			final BeanContent beanContent = beanContentList.get(pos);

			final int viewHolderType = getItemViewType(pos);

			if(viewHolderType == 0) {
				//1.post header
				final VhPostHeader vhPostHeader = (VhPostHeader)vh;
				String witn = beanPost.witn;

				if(witn.isEmpty()) {
					int wsr = Integer.valueOf(beanPost.wsr);

					witn = wsr == 0 ? "itn_m.jpg" : (wsr == 1 || wsr == 9 ? "itn_c.jpg" : "itn_s.jpg");
				}

				imageLoader.displayImage(MainCons.EnumContentPath.MEMBER_I.getPath() + witn, vhPostHeader.iv_profile, optionsForOvalThumb);

				SpannableString ss;
				String s;

				//TODO : 개인 불로그일 경우 닉네임 + 게시판명
				s = beanPost.cn.concat(" - ").concat(BeanBoarde.getNmFromId(beanBoardeList, beanPost.board_id)); //post owner + board name
				vhPostHeader.tv_board.setText(s);

				final SpannableStringBuilder ssb = new SpannableStringBuilder();

				s = beanPost.nn; //post 작성자의 닉네임
				ss = new SpannableString(s);
				ss.setSpan(new ForegroundColorSpan(CommonUtil.getColor(getActivity(), android.R.color.black)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				ssb.append(ss);

				s = "\n" + beanPost.ud.substring(0, 16) + "   조회 " + beanPost.rec + "   댓글 " + beanPost.coc;
				ss = new SpannableString(s);
				//ss.setSpan(new ForegroundColorSpan(CommonUtil.getColor(getActivity(), android.R.color.black)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				ss.setSpan(new AbsoluteSizeSpan(13, true), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				ssb.append(ss);

				vhPostHeader.tv_nn.setText(ssb);

				vhPostHeader.tv_tt.setText(beanPost.tt);
			} else	if(viewHolderType == MainCons.EnumContentType.TEXT.getValue()) {
				final VhText1 vhText1 = (VhText1)vh;
				vhText1.tv_31.setText(beanContent.cob);
			} else if(viewHolderType == MainCons.EnumContentType.PHOTO.getValue()) {
				final VhImage1 vhImage1 = (VhImage1) vh;
				imageLoader.displayImage(MainCons.EnumContentPath.CONTENT_I.getPath() + beanContent.cob, vhImage1.iv_31);

				if(beanContent.cob.equals(beanPost.ffn)) {
					imageView = vhImage1.iv_31;
				}
			} else if(viewHolderType == MainCons.EnumContentType.VIDEO.getValue()) {
				final VhImage1 vhImage1 = (VhImage1)vh;
				imageLoader.displayImage(MainCons.EnumContentPath.CONTENT_V_T.getPath() + beanContent.cob.replace("mp4", "jpg"), vhImage1.iv_31);
			} else {
				//임시

			}

		}
	}
}