package com.infoline.doctorcha.presentation.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Selection;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.httphelper.ServiceGenerator;
import com.infoline.doctorcha.core.listener.MediaScanFileHelper;
import com.infoline.doctorcha.core.util.CommonUtil;
import com.infoline.doctorcha.core.util.MediaUtil;
import com.infoline.doctorcha.core.util.PermissionUtil;
import com.infoline.doctorcha.presentation.MainCons;
import com.infoline.doctorcha.presentation.RetrofitInterface;
import com.infoline.doctorcha.presentation.activity.MapsActivity;
import com.infoline.doctorcha.presentation.activity.SimpleSelectActivity;
import com.infoline.doctorcha.presentation.bean.BeanCarInfo;
import com.infoline.doctorcha.presentation.bean.BeanCarAreaPhoto;
import com.infoline.doctorcha.presentation.viewholder.VhImage1Text1;
import com.infoline.doctorcha.presentation.viewholder.VhImage2Text1;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
import static com.infoline.doctorcha.core.util.CommonUtil.writeLog;
import static com.infoline.doctorcha.presentation.MainApp.beanMember;
import static com.infoline.doctorcha.presentation.MainCons.CN_REQUEST_ADDRESS_BY_GPS;
import static com.infoline.doctorcha.presentation.MainCons.CN_REQUEST_CARKIND;
import static com.infoline.doctorcha.presentation.MainCons.CN_REQUEST_GALLARY;

public class CarInfoFragment extends Fragment {
	@BindView(R.id.rv_caroptions) RecyclerView rv_caroptions;
	@BindView(R.id.rv_carphotos) RecyclerView rv_carphotos;

	@BindView(R.id.et_cdk)	EditText et_cdk;
	@BindView(R.id.et_ccc)	EditText et_ccc;
	@BindView(R.id.et_cdg)	EditText et_cdg;
	@BindView(R.id.et_crn)	EditText et_crn;
	@BindView(R.id.et_crd)	EditText et_crd;
	@BindView(R.id.et_coy)	EditText et_coy;
	@BindView(R.id.et_ckm)	EditText et_ckm;
	@BindView(R.id.et_car)	EditText et_car;
	@BindView(R.id.et_cem)	EditText et_cem;

	@BindView(R.id.sp_ctk)	Spinner sp_ctk;
	@BindView(R.id.sp_cpk)	Spinner sp_cpk;
	@BindView(R.id.sp_cak)	Spinner sp_cak;
	@BindView(R.id.sp_cwk)	Spinner sp_cwk;

	@BindView(R.id.tv_confirm)
	TextView tv_confirm;

	BeanCarInfo beanCarInfo;
	MainCons.EnumFragmentOpenType enumFragmentOpenType;

	//1. 두가지 용도로 쓰인다.
	//	1) 상담사가 메세지창에서 '보기 아이콘'을 클릭할 경우 해당 post_id를 전달 받는 용도로 사용
	//	2) 회원이 작성 후 상담사에게 전송하는 용도로 사용
	//  3) 현재는 member_id를 돌려주지만(member_id는 회원 자신이므로 사실 현재는 필요 없다) 복수개의 차량으로 확장하게 되면 차량 고유 id를 전달해야 하므로 꼭 필요
	//3. 자기자신(회원, 상담사, 협력업체 관계없이 모두)의 차량 정보를 수정하기 위하여 진입할 경우 즉 0이면 자기자신의 member_id 또는 복수개 차량으로 확장할 경우는 차량 id
	int delveryId;

	@OnClick({R.id.iv_cdk_search, R.id.iv_car_search, R.id.tv_confirm})
	protected void onClick(final View v) {
		if(v.getId() == R.id.iv_cdk_search) {
			final Intent intent = new Intent(getActivity(), SimpleSelectActivity.class);
			startActivityForResult(intent, CN_REQUEST_CARKIND);
		} else if(v.getId() == R.id.iv_car_search){
			if(PermissionUtil.requestGps(getActivity())) {
				final Intent intent = new Intent(getActivity(), MapsActivity.class);
				startActivityForResult(intent, CN_REQUEST_ADDRESS_BY_GPS);
			}
		} else {
			saveData();
		}
	}

	MyRecyclerAdapter_CarOptions rva_caroptions;
	MyRecyclerAdapter_CarPhotos rva_carphotos;
	final MediaUtil mediaUtil;
	
    public CarInfoFragment() {
		writeLog(null);
		mediaUtil = new MediaUtil();
    }

	public static CarInfoFragment newInstance(final MainCons.EnumFragmentOpenType enumFragmentOpenType, int member_id) {
		final CarInfoFragment fragment = new CarInfoFragment();

		final Bundle bundle = new Bundle();

        bundle.putSerializable(MainCons.EnumFragmentOpenType.class.getSimpleName(), enumFragmentOpenType);
		bundle.putInt(MainCons.EnumExtraName.ID.name(), member_id);
        fragment.setArguments(bundle);

		return fragment;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_carinfo, container, false);
        ButterKnife.bind(this, rootView);

		final Bundle bundle = getArguments();
		enumFragmentOpenType = (MainCons.EnumFragmentOpenType)bundle.getSerializable(MainCons.EnumFragmentOpenType.class.getSimpleName());
		delveryId = bundle.getInt(MainCons.EnumExtraName.ID.name(), beanMember.id);

		if(enumFragmentOpenType == MainCons.EnumFragmentOpenType.SEND_READY) {
			tv_confirm.setText("전송");
		} else if(enumFragmentOpenType == MainCons.EnumFragmentOpenType.READ_ONLY) {
			tv_confirm.setVisibility(View.GONE);
		}

		rva_caroptions = new MyRecyclerAdapter_CarOptions(getActivity());
		final GridLayoutManager lm_caroptions = new GridLayoutManager(getActivity(), 4);
		//lm_caroptions.setOrientation(LinearLayoutManager.VERTICAL);

		rva_carphotos = new MyRecyclerAdapter_CarPhotos(getActivity());
		final GridLayoutManager lm_carphotos = new GridLayoutManager(getActivity(), 3);
		//lm_carphotos.setOrientation(LinearLayoutManager.VERTICAL);

		loadData();

		rv_caroptions.setNestedScrollingEnabled(false);
		rv_caroptions.setHasFixedSize(false);
		rv_caroptions.setLayoutManager(lm_caroptions);
		rv_caroptions.setItemAnimator(new DefaultItemAnimator());
		rv_caroptions.setAdapter(rva_caroptions);

		//------------------------------------------------------------------------------------------

		rv_carphotos.setHasFixedSize(true);
		rv_carphotos.setLayoutManager(lm_carphotos);
		rv_carphotos.setItemAnimator(new DefaultItemAnimator());
		rv_carphotos.setAdapter(rva_carphotos);
		

		return rootView;
	}

	private void loadData() {
		final RetrofitInterface.CarInfoService service = ServiceGenerator.createService(RetrofitInterface.CarInfoService.class);
		final Call<List<BeanCarInfo>> call = service.select(delveryId);
		call.enqueue(new Callback<List<BeanCarInfo>>() {
			@Override
			public void onResponse(Call<List<BeanCarInfo>> call, Response<List<BeanCarInfo>> response) {
				String msg = null;

				if(response.isSuccessful()) {
					retrieveData(response.body());
				}
				else {
					msg = response.errorBody().source().toString();
				}

				if(msg != null) {
					Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onFailure(Call<List<BeanCarInfo>> call, Throwable t) {
				CommonUtil.writeLog(ServiceGenerator.getExceptionMsgByCause(t));
			}
		});

		Spinner[] spinnerArray = {sp_ctk, sp_cpk, sp_cak, sp_cwk};

		for(Spinner sp : spinnerArray) {
			final String[] stringArray = getResources().getStringArray(sp.getId() == R.id.sp_ctk ? R.array.ctk_list : (sp.getId() == R.id.sp_cpk ? R.array.cpk_list : (sp.getId() == R.id.sp_cak ? R.array.cak_list : R.array.cwk_list)));
			final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.tmp_spinner_textview, stringArray);
			adapter.setDropDownViewResource(R.layout.tmp_spinner_dropdownitem);
			sp.setAdapter(adapter);
		}
	}

	@Override
	public void onResume() {
		CommonUtil.writeLog(null);
		super.onResume();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) return;

		if (requestCode == CN_REQUEST_GALLARY) {
			//intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);하더라도 아래와 같다
			//Intent.ACTION_SEND_MULTIPLE.equals(data.getAction() == false
			//data.hasExtra(Intent.EXTRA_STREAM) == false
			//final ArrayList<Parcelable> list = data.getParcelableArrayListExtra(Intent.EXTRA_STREAM); //--> null

			if(data == null) {
                    /*
                    Uri imageUri = mediaUtil.getLastCaptureImageUri(this);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    */

				MediaScanFileHelper.OnMediaScanListener onMediaScanListener = new MediaScanFileHelper.OnMediaScanListener() {
					public void onError() {
						CommonUtil.writeLog("onError");
					}

					public void onSuccess(Uri uri) {
						CommonUtil.writeLog(Uri.decode(uri.toString()));
						mediaUtil.selectedUriList.add(uri);

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
										rva_carphotos.applySelectedImages();
									}
								});
							}
						}).start();

					}
				};

				MediaScanFileHelper mediaScanFileHelper = new MediaScanFileHelper(getActivity());
				mediaScanFileHelper.scanFile(new File(mediaUtil.realPath), onMediaScanListener);
			}
			else {
				final ClipData cd = data.getClipData();

				if (cd != null) {
					//google photo intent --> 다른 Intent는 무엇이 있는지 확인 못함
					final int selectedCount = cd.getItemCount() > 3 ? 3 : cd.getItemCount();

					for (int i = 0; i < selectedCount; i++) {
						mediaUtil.selectedUriList.add(cd.getItemAt(i).getUri());
					}

				} else {
					mediaUtil.selectedUriList.add(data.getData());
				}

				rva_carphotos.applySelectedImages();
			}
		}
		else {
			final EditText et = requestCode == CN_REQUEST_CARKIND ? et_cdk : et_car; //CN_REQUEST_ADDRESS_BY_GPS

			et.setText(data.getStringExtra(MainCons.EnumExtraName.NAME1.name()));
			et.requestFocus();
			Selection.setSelection(et.getText(), et.getText().length());
			et.requestFocus();

			if(requestCode == CN_REQUEST_CARKIND) {
				et_cdg.setText(data.getStringExtra(MainCons.EnumExtraName.NAME2.name())); //등급
				CommonUtil.setSpinnerPosFromText(sp_cpk, data.getStringExtra(MainCons.EnumExtraName.NAME3.name())); //연료
				et_ccc.setText(data.getStringExtra(MainCons.EnumExtraName.NAME4.name())); //배기량
			}
		}
	}
	
	private void retrieveData(List<BeanCarInfo> beanCarInfoList) {
		if(beanCarInfoList.size() == 0) {
			//현재 등록된 차량정보가 없을 경우
			beanCarInfo = new BeanCarInfo();
		}
		else {
			beanCarInfo = beanCarInfoList.get(0);

			et_cdk.setText(beanCarInfo.cdk);
			et_ccc.setText(CommonUtil.getStringFromNumeric(beanCarInfo.ccc));
			et_cdg.setText(beanCarInfo.cdg);
			et_crn.setText(beanCarInfo.crn);
			et_crd.setText(beanCarInfo.crd);
			et_coy.setText(beanCarInfo.coy);
			et_ckm.setText(CommonUtil.getStringFromNumeric(beanCarInfo.ckm));
			sp_ctk.setSelection(beanCarInfo.ctk);
			sp_cpk.setSelection(beanCarInfo.cpk);
			et_car.setText(beanCarInfo.car);
			sp_cak.setSelection(beanCarInfo.cak);
			sp_cwk.setSelection(beanCarInfo.cwk);
			et_cem.setText(beanCarInfo.cem);
		}

		rva_carphotos.yyy();
		rva_caroptions.xxx();
	}

	public void saveData() {
		final ProgressDialog pd = ProgressDialog.show(getActivity(), "", "데이타 및 사진을 저장하고 있습니다", true);
		final int saveType = beanCarInfo.member_id;

		beanCarInfo.member_id = beanMember.id;

		beanCarInfo.cdk = et_cdk.getText().toString();
		beanCarInfo.ccc =  CommonUtil.getNumberFromString(et_ccc.getText().toString());
		beanCarInfo.cdg = et_cdg.getText().toString();
		beanCarInfo.crn = et_crn.getText().toString();
		beanCarInfo.crd = et_crd.getText().toString();
		beanCarInfo.coy = et_coy.getText().toString();
		beanCarInfo.ckm =  CommonUtil.getNumberFromString(et_ckm.getText().toString());
		beanCarInfo.ctk = sp_ctk.getSelectedItemPosition();
		beanCarInfo.cpk = sp_cpk.getSelectedItemPosition();
		beanCarInfo.car = et_car.getText().toString();
		beanCarInfo.cak = sp_cak.getSelectedItemPosition();
		beanCarInfo.cwk = sp_cwk.getSelectedItemPosition();
		beanCarInfo.cem = et_cem.getText().toString();

		beanCarInfo.cos = rva_caroptions.getCarOptionCheckList(); //1.jpg,5.png
		beanCarInfo.cps = rva_carphotos.getCarPhotoList(); //1,4,11

		final RetrofitInterface.CarInfoService service = ServiceGenerator.createService(RetrofitInterface.CarInfoService.class);
		final Call<Integer> call = saveType == 0 ? service.insert(beanCarInfo) : service.update(beanCarInfo);
		call.enqueue(new Callback<Integer>() {
			@Override
			public void onResponse(Call<Integer> call, Response<Integer> response) {
				if(response.isSuccessful()) {
					if(enumFragmentOpenType == MainCons.EnumFragmentOpenType.SEND_READY) {
						delveryId = beanCarInfo.member_id == 0 ? response.body() : beanCarInfo.member_id;
					}

					final SparseArray<String> photoUrlSparseArray = rva_carphotos.getPhotoFileList();

					if(photoUrlSparseArray.size() == 0) {
						pd.dismiss();

						if(enumFragmentOpenType == MainCons.EnumFragmentOpenType.SEND_READY) {
							final Intent intent = new Intent();
							intent.putExtra(MainCons.EnumExtraName.ID.name(), delveryId);
							getActivity().setResult(Activity.RESULT_OK, intent);
							getActivity().finish();
						} else {
							Toast.makeText(getActivity(), "데이타 저장이 완료되었습니다", Toast.LENGTH_LONG).show();
						}
					} else {
						uploadFile(pd, photoUrlSparseArray);
					}
				}
				else {
					pd.dismiss();
					final String msg = "데이터 저장이 실패로 끝났습니다\n" + response.errorBody().source().toString();
					if(saveType == 0) {
						beanCarInfo.member_id = 0;
					}

					Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onFailure(Call<Integer> call, Throwable t) {
				pd.dismiss();
				final String msg = ServiceGenerator.getExceptionMsgByCause(t);

				if(saveType == 0) {
					beanCarInfo.member_id = 0;
				}

				Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
			}
		});
	}

	private void uploadFile(final ProgressDialog pd, SparseArray<String> photoUrlSparseArray) {
		final MultipartBody.Builder builder = new MultipartBody.Builder();
		builder.setType(MultipartBody.FORM);

		try {
			for(int i = 0; i < photoUrlSparseArray.size(); i++) {
				final File file = new File(photoUrlSparseArray.valueAt(i));
				final RequestBody requestBody = RequestBody.create(MediaUtil.getMediaType(photoUrlSparseArray.valueAt(i)), file);
				builder.addFormDataPart("fileList", beanCarInfo.member_id + "_" + photoUrlSparseArray.keyAt(i) + "." + CommonUtil.getExtName(file.getName()), requestBody);
			}

			builder.addFormDataPart("pathId", MainCons.EnumContentPath.CARINFO_I.getId()+"");

			final RequestBody finalRequestBody = builder.build();

			//------------------------------------------------

			final RetrofitInterface.UploadService service = ServiceGenerator.createService(RetrofitInterface.UploadService.class);
			final Call<Void> call = service.files(finalRequestBody);
			call.enqueue(new Callback<Void>() {
				@Override
				public void onResponse(Call<Void> call, Response<Void> response) {
					pd.dismiss();

					if(response.isSuccessful()) {
						if(enumFragmentOpenType == MainCons.EnumFragmentOpenType.SEND_READY) {
							//final Intent intent = new Intent();
							//intent.putExtra(MainCons.EnumExtraName.ID.name(), delveryId);


							final Intent intent = new Intent();
							intent.putExtra(MainCons.EnumExtraName.ID.name(), delveryId);
							getActivity().setResult(RESULT_OK, intent);

						} else {
							Toast.makeText(getActivity(), "데이타 저장이 완료되었습니다", Toast.LENGTH_LONG).show();
						}
					} else {
						final String msg = "사진 업로드가 실패로 끝났습니다\n" + response.errorBody().source().toString();
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
		}catch (Exception e) {
			pd.dismiss();
			Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	private class MyRecyclerAdapter_CarPhotos extends RecyclerView.Adapter<VhImage2Text1> {
		private final ImageLoader imageLoader;
		private final Context ctx;
		private final List<BeanCarAreaPhoto> beanCarAreaPhotoList;
		DisplayImageOptions test = new DisplayImageOptions.Builder()
				.cacheInMemory(false)
				.cacheOnDisk(false)
				.considerExifParams(true)
				.build();

		private MyRecyclerAdapter_CarPhotos(final Context ctx) {
			writeLog(null);

			this.ctx = ctx;
			this.imageLoader = ImageLoader.getInstance();
			this.beanCarAreaPhotoList = new ArrayList<>();

			this.beanCarAreaPhotoList.add(new BeanCarAreaPhoto(1, "정면(번호판포함)", null));
			this.beanCarAreaPhotoList.add(new BeanCarAreaPhoto(2, "후면(번호판포함)", null));
			this.beanCarAreaPhotoList.add(new BeanCarAreaPhoto(3, "좌측면", null));
			this.beanCarAreaPhotoList.add(new BeanCarAreaPhoto(4, "우측면", null));
			this.beanCarAreaPhotoList.add(new BeanCarAreaPhoto(5, "실내", null));
			this.beanCarAreaPhotoList.add(new BeanCarAreaPhoto(6, "계기판", null));
			this.beanCarAreaPhotoList.add(new BeanCarAreaPhoto(7, "기타", null));
			this.beanCarAreaPhotoList.add(new BeanCarAreaPhoto(8, "기타", null));
			this.beanCarAreaPhotoList.add(new BeanCarAreaPhoto(9, "기타", null));
		}

		@Override
		public int getItemCount() {
			return beanCarAreaPhotoList.size();
		}

		@Override
		public VhImage2Text1 onCreateViewHolder(ViewGroup vg, int itemViewType) {
			final VhImage2Text1 vh = new VhImage2Text1(LayoutInflater.from(ctx).inflate(R.layout.vh_pickphoto_mycarinfo, vg, false));

			final View.OnClickListener ocl = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					final int pos = vh.getAdapterPosition();
					final BeanCarAreaPhoto beanCarAreaPhoto = beanCarAreaPhotoList.get(pos);

					if(v.getId() == R.id.tv_31) {
						//gallary, camera intent 호출
						final Intent intent = mediaUtil.getPhotoGallaryOrPhotoCamera(getActivity(), false);

						if(intent != null) {
							mediaUtil.selectedPos = pos;
							startActivityForResult(intent, CN_REQUEST_GALLARY);
						}
					}
					else if(v.getId() == R.id.iv_31) {
						if(beanCarAreaPhoto.url != null) {
							//사진 크게보기
							final Intent intent = new Intent();
							intent.setAction(Intent.ACTION_VIEW);

							final Uri uri;

							if(beanCarAreaPhoto.url.contains("/")) {
								//서버에 저장되지 않은 사진
								uri = Uri.fromFile(new File(beanCarAreaPhoto.url));
							} else {
								uri = Uri.parse(MainCons.EnumContentPath.CARINFO_I.getPath() + delveryId + "_" + beanCarAreaPhoto.url);
							}

							intent.setDataAndType(uri, "image/*");
							startActivity(intent);
						}
					}
					else {
						//사진삭제
						beanCarAreaPhoto.url = null;
						rva_carphotos.notifyItemChanged(pos);
					}
				}
			};

			CommonUtil.assignOnClickListener(new View[] {vh.tv_31, vh.iv_31, vh.iv_32}, ocl);

			return vh;
		}

		@Override
		public void onBindViewHolder(VhImage2Text1 vh, int pos) {
			final BeanCarAreaPhoto beanCarAreaPhoto = beanCarAreaPhotoList.get(pos);
			final String url = beanCarAreaPhoto.url;

			if(url == null) {
				vh.iv_31.setImageDrawable(null);
				vh.iv_31.setVisibility(View.GONE);
				vh.iv_32.setVisibility(View.GONE);
				vh.tv_31.setVisibility(View.VISIBLE);
				vh.tv_31.setText(beanCarAreaPhoto.nm);
			} else {
				vh.iv_31.setVisibility(View.VISIBLE);
				vh.iv_32.setVisibility(View.VISIBLE);
				vh.tv_31.setVisibility(View.GONE);

				final String realUrl;

				if(url.contains("/")) {
					final File file = new File(url);
					final Uri uri = Uri.fromFile(file);
					realUrl = uri.toString();
				} else {
					realUrl = MainCons.EnumContentPath.CARINFO_I.getPath() +  delveryId + "_" + url;
				}

				//optionsForBasicNoCache
				imageLoader.displayImage(realUrl, vh.iv_31, test); //사진을 삭제 후 재지정하더라도 filename x_y.jpg로 변하지 않으므로 cache사용하면 안된다
			}
		}

		private void applySelectedImages() {
			final int pos = mediaUtil.selectedPos;
			final BeanCarAreaPhoto beanCarAreaPhoto = beanCarAreaPhotoList.get(pos);
			beanCarAreaPhoto.url = MediaUtil.getRealPathFromUri(getActivity(), mediaUtil.selectedUriList.get(0));
			rva_carphotos.notifyItemChanged(pos);
		}

		private SparseArray<String> getPhotoFileList() {
			final SparseArray<String> photoFileList = new SparseArray<>();

			for(BeanCarAreaPhoto bs : beanCarAreaPhotoList) {
				//1. 서버에 있을 경우 xxx.yyy --> 뭔가 불안하다. 확인할 것
				if(bs.url != null && bs.url.contains("/")) {
					photoFileList.put(bs.id, bs.url);
				}
			}

			return photoFileList;
		}

		private String getCarPhotoList() {
			//1.jpg,5.png,7.gif
			String list = "";

			for(BeanCarAreaPhoto bs : beanCarAreaPhotoList) {
				if(bs.url != null) {
					final String extName = CommonUtil.getExtName(bs.url);
					final String briefFileName = bs.id + "." + extName;
					list += list.isEmpty() ? briefFileName : "," + briefFileName;
				}
			}

			return list;
		}

		private void yyy() {
			final String cps = beanCarInfo.cps;
			if(!cps.isEmpty()) {
				final String[] cpsList = cps.split(","); //1.jpg,4.png,7.bmp --> 순서가 아닌 id값이다

				for(String breifFileName : cpsList) {
					//급해서 임시방편으로 pos로 응급처치 - id검색으로 전환할 것 -- id값이나 순서가 변경되면 큰일난다
					// .(dot)는 정규식 예약어
					final int id = Integer.parseInt(breifFileName.split("\\.")[0]);
					this.beanCarAreaPhotoList.get(id - 1).url= breifFileName;
				}
			}

			notifyDataSetChanged();
		}
	}

	private class MyRecyclerAdapter_CarOptions extends RecyclerView.Adapter<VhImage1Text1> {
		private final Context ctx;
		private boolean[] checkedState = new boolean[] {false, false, false, false, false, false, false, false, false, false, false, false};

		private MyRecyclerAdapter_CarOptions(final Context ctx) {
			writeLog(null);
			this.ctx = ctx;
		}

		@Override
		public int getItemCount() {
			return MainCons.EnumCarOption.values().length;
		}

		@Override
		public VhImage1Text1 onCreateViewHolder(ViewGroup vg, int itemViewType) {
			final VhImage1Text1 vh = new VhImage1Text1(LayoutInflater.from(ctx).inflate(R.layout.vh_caroption, vg, false));

			vh.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//final int pos = CommonUtil.getAdapterPositionFromView(rv_caroptions, v);
					final int pos = rv_caroptions.getChildAdapterPosition(v);
					checkedState[pos] = !checkedState[pos];

					rva_caroptions.notifyItemChanged(pos);
				}
			});

			return vh;
		}

		@Override
		public void onBindViewHolder(VhImage1Text1 vh, int pos) {
			MainCons.EnumCarOption enumCarOption = MainCons.EnumCarOption.values()[pos];
			CommonUtil.setTextViewImage(ctx, vh.tv_31, enumCarOption.getResId(), 32, 2, 2);
			vh.tv_31.setText(enumCarOption.getNm());
			vh.iv_31.setVisibility(checkedState[pos] ? View.VISIBLE : View.INVISIBLE);
		}

		private void xxx() {
			try {
				if(!beanCarInfo.cos.isEmpty()) {
					//selected car options :1,3,7,12 --> 순서가 아닌 id값이다
					final List<String> cosList = Arrays.asList(beanCarInfo.cos.split(","));

					for(int i = 0; i < 12; i++) {
						int findIndex = cosList.indexOf(String.valueOf(MainCons.EnumCarOption.values()[i].getId()));
						if(findIndex != -1) {
							checkedState[i] = true;
						}
					}
				}

				notifyDataSetChanged();
			}
			catch (Exception e) {
				writeLog(e.getMessage());
			}
		}

		private String getCarOptionCheckList() {
			String checkList = "";

			for(int i = 0; i < 12; i++) {
				if(checkedState[i]) {
					final int id = MainCons.EnumCarOption.values()[i].getId();
					checkList += checkList.isEmpty() ? id : "," + id;
				}
			}

			return checkList;
		}

		private SparseArray<String> optionCategoryList = new SparseArray<>();

	}
}