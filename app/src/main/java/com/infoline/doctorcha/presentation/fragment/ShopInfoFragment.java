package com.infoline.doctorcha.presentation.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Selection;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.httphelper.ServiceGenerator;
import com.infoline.doctorcha.core.util.CommonUtil;
import com.infoline.doctorcha.core.util.MediaUtil;
import com.infoline.doctorcha.core.util.PermissionUtil;
import com.infoline.doctorcha.presentation.MainApp;
import com.infoline.doctorcha.presentation.MainCons;
import com.infoline.doctorcha.presentation.RetrofitInterface;
import com.infoline.doctorcha.presentation.activity.FragmentContainerActivity;
import com.infoline.doctorcha.presentation.activity.MapsActivity;
import com.infoline.doctorcha.presentation.activity.ShopActivity;
import com.infoline.doctorcha.presentation.bean.BeanErrResponse;
import com.infoline.doctorcha.presentation.bean.BeanMemberAndShop;
import com.infoline.doctorcha.presentation.bean.BeanPost;
import com.infoline.doctorcha.presentation.viewholder.VhPostCi;
import com.infoline.doctorcha.presentation.viewholder.VhText1;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.R.attr.dial;
import static android.R.attr.fragment;
import static android.app.Activity.RESULT_OK;
import static com.infoline.doctorcha.presentation.MainApp.beanMember;
import static com.infoline.doctorcha.presentation.MainCons.CN_REQUEST_ADDRESS_BY_GPS;

public class ShopInfoFragment extends Fragment implements OnMapReadyCallback {
	@BindView(R.id.tv_header_standard)
	TextView tv_header_standard;
	@BindView(R.id.et_cn)
	EditText et_cn;
	@BindView(R.id.tv_bmc)
	TextView tv_bmc;
	@BindView(R.id.et_ht)
	EditText et_ht;
	@BindView(R.id.et_ca)
	EditText et_ca;
	@BindView(R.id.et_tn)
	EditText et_tn;

	@BindView(R.id.tv_header_bi)
	TextView tv_header_ci;
	@BindView(R.id.et_bi)
	EditText et_bi;

	@BindView(R.id.tv_header_post)
	TextView tv_header_post;

    @BindView(R.id.rv_post)
    RecyclerView rv_post;

	private MyRecyclerAdapter rva;
	private BeanMemberAndShop beanMemberAndShop;
	final int categoryCount = MainCons.EnumBmCategory.values().length;
	final boolean[] isCheckedStatusArray = new boolean[16];

	int modifyPos;
	boolean editable;

	public void xxx() {
		final Intent intent = new Intent(getActivity(), FragmentContainerActivity.class);

		intent.putExtra(MainCons.EnumExtraName.NAME1.name(), PostEditFragment.class.getSimpleName());
		intent.putExtra("board_id", 11);
		intent.putExtra("owner_id", beanMemberAndShop.id);
		intent.putExtra("owner_cn", ((Toolbar)((ShopActivity)getActivity()).findViewById(R.id.tb)).getTitle().toString()); //편법.  급해서 넘어간다
		//신규이므로 BeanPost는 전달하지 않는다

		//TODO : 추가 후 어떻게 처리할 지 확정할 것
		/////////////////////startActivityForResult(intent, 800);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
	}

	@OnClick({R.id.tv_header_standard, R.id.tv_bmc, R.id.et_ca, R.id.et_tn, R.id.tv_header_bi, R.id.tv_header_post})
	protected void onClick_BottomMenu(final View v) {
		switch (v.getId()) {
			case R.id.tv_bmc:
				final CharSequence[] itemArray = new CharSequence[categoryCount];
				final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

				for(int i = 0; i < categoryCount; i++) {
					final MainCons.EnumBmCategory enumBmCategory = MainCons.EnumBmCategory.values()[i];
					itemArray[i] = enumBmCategory.getNm();
				}

				builder.setTitle("카테고리 선택").setMultiChoiceItems(itemArray, isCheckedStatusArray, new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
						/*
						if(isChecked) {
							selectedItemIndexList.add(which);
						} else if(selectedItemIndexList.contains(which)) {
							//1. int which를 전달하면 위치 기준으로 remove 시키고 Integer.valueOf(which)를 전달하면 값 기준으로 검색 후 rempve 시킨다 --> 어어어...괜찮은 걸
							selectedItemIndexList.remove(Integer.valueOf(which));
						}
						*/
					}
				}).setPositiveButton("확인", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					bbb();
				}
				}).setNegativeButton("취소", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				});

				final AlertDialog dialog = builder.create();

				dialog.setOnShowListener(new DialogInterface.OnShowListener() {
					@Override
					public void onShow(DialogInterface dialogInterface) {
						ListView listView = ((AlertDialog) dialogInterface).getListView();
						final ListAdapter originalAdapter = listView.getAdapter();

						listView.setAdapter(new ListAdapter() {
							@Override
							public int getCount() {
								return originalAdapter.getCount();
							}

							@Override
							public Object getItem(int id) {
								return originalAdapter.getItem(id);
							}

							@Override
							public long getItemId(int id) {
								return originalAdapter.getItemId(id);
							}

							@Override
							public int getItemViewType(int id) {
								return originalAdapter.getItemViewType(id);
							}

							@Override
							public View getView(int position, View convertView, ViewGroup parent) {
								View view = originalAdapter.getView(position, convertView, parent);
								TextView textView = (TextView) view;
								//textView.setTextSize(16); set text size programmatically if needed
								textView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 72 /* this is item height */));
								return view;
							}

							@Override
							public int getViewTypeCount() {
								return originalAdapter.getViewTypeCount();
							}

							@Override
							public boolean hasStableIds() {
								return originalAdapter.hasStableIds();
							}

							@Override
							public boolean isEmpty() {
								return originalAdapter.isEmpty();
							}

							@Override
							public void registerDataSetObserver(DataSetObserver observer) {
								originalAdapter.registerDataSetObserver(observer);

							}

							@Override
							public void unregisterDataSetObserver(DataSetObserver observer) {
								originalAdapter.unregisterDataSetObserver(observer);

							}

							@Override
							public boolean areAllItemsEnabled() {
								return originalAdapter.areAllItemsEnabled();
							}

							@Override
							public boolean isEnabled(int position) {
								return originalAdapter.isEnabled(position);
							}

						});
					}
				});

				dialog.show();

				break;
			case R.id.et_ca:
				if(tv_header_standard.getTag() != null) {
					if(PermissionUtil.requestGps(getActivity())) {
						final Intent intent = new Intent(getActivity(), MapsActivity.class);
						startActivityForResult(intent, CN_REQUEST_ADDRESS_BY_GPS);
					}
				}
				break;
			case R.id.et_tn:
				if(tv_header_standard.getTag() == null) {
					if(PermissionUtil.requestReadPhoneState(getActivity())) {
						final String tn = ((TextView)v).getText().toString();

						if(!tn.isEmpty()) {
							startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:".concat(tn))));
						}
					}
				}

				break;
			case R.id.tv_header_standard:
				if(tv_header_standard.getTag() == null) {
					//편집 모드로 전환
					CommonUtil.setTextViewImage(getActivity(), tv_header_standard, R.drawable.ic_save_red_24dp, 24, 0, 3);
					tv_header_standard.setTag("edit");
					////////////et_ca.setFocusableInTouchMode(true); //키보드 open할 필요까지는 업다
					et_cn.setFocusableInTouchMode(true); //키보드 open할 필요까지는 업다
					et_tn.setFocusableInTouchMode(true);
					et_ht.setFocusableInTouchMode(true);

					CommonUtil.setTextViewImage(getActivity(), tv_bmc, R.drawable.ic_keyboard_arrow_right_red_24dp, 24, 0, 3);
					/////CommonUtil.setTextViewImage(getActivity(), et_ht, R.drawable.ic_keyboard_arrow_right_red_24dp, 24, 0, 3);
					CommonUtil.setTextViewImage(getActivity(), et_ca, R.drawable.ic_keyboard_arrow_right_red_24dp, 24, 0, 3);
					et_tn.setCompoundDrawables(null, null, null, null);
				} else {
					//저장 후 보기 모드로 전환

					final String cn = et_cn.getText().toString().trim();

					if(cn.isEmpty()) {
						Toast.makeText(getActivity(), "상호 또는 블로그 명칭을 입력하세요", Toast.LENGTH_LONG).show();
						return;
					}

					beanMemberAndShop.cn = et_cn.getText().toString().trim();
					beanMemberAndShop.bmc = getBmcIdString();
					beanMemberAndShop.ca = et_ca.getText().toString().trim();
					beanMemberAndShop.ht = et_ht.getText().toString().trim();
					beanMemberAndShop.tn = et_tn.getText().toString().trim();
					beanMemberAndShop.bi = et_bi.getText().toString().trim();

					final RetrofitInterface.ShopService service = ServiceGenerator.createService(RetrofitInterface.ShopService.class);
					final Call<Void> call = service.update(beanMemberAndShop);

					call.enqueue(new Callback<Void>() {
						@Override
						public void onResponse(Call<Void> call, Response<Void> response) {
							final String msg;

							if(response.isSuccessful()) {
								msg = "기본 정보가 성공적으로 저장되었습니다";

								tv_header_standard.setTag(null);

								CommonUtil.hideSoftKeyboardInput(getActivity(), et_bi);
								CommonUtil.setTextViewImage(getActivity(), tv_header_standard, R.drawable.ic_create_red_24dp, 24, 0, 3);

								CommonUtil.hideSoftKeyboardInput(getActivity(), et_tn);
								et_cn.setFocusable(false);
								et_tn.setFocusable(false);
								et_ht.setFocusable(false);
								///////////et_ca.setFocusable(false);

								tv_bmc.setCompoundDrawables(null, null, null, null);
								/////et_ht.setCompoundDrawables(null, null, null, null);
								et_ca.setCompoundDrawables(null, null, null, null);
								CommonUtil.setTextViewImage(getActivity(), et_tn, R.drawable.ic_phone_blue_24dp, 24, 0, 3);

								tv_header_ci.setTag(null);
							} else {
								msg = "기본 정보 저장에 실패하였습니다\n" + response.errorBody().source().toString();
							}

							Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
						}

						@Override
						public void onFailure(Call<Void> call, Throwable t) {
							Toast.makeText(getActivity(), ServiceGenerator.getExceptionMsgByCause(t), Toast.LENGTH_LONG).show();
						}
					});
				}
				break;
			case R.id.tv_header_bi:
				if(tv_header_ci.getTag() == null) {
					//편집 모드로 전환
					CommonUtil.setTextViewImage(getActivity(), tv_header_ci, R.drawable.ic_save_red_24dp, 24, 0, 3);
					tv_header_ci.setTag("edit");
					et_bi.setFocusableInTouchMode(true);
					et_bi.requestFocus();
					CommonUtil.showSoftKeyboard(getActivity(), et_bi);
				} else {
					//저장 후 보기 모드로 전환

					beanMemberAndShop.cn = et_cn.getText().toString().trim();
					beanMemberAndShop.bmc = getBmcIdString();
					beanMemberAndShop.ca = et_ca.getText().toString().trim();
					beanMemberAndShop.ht = et_ht.getText().toString().trim();
					beanMemberAndShop.tn = et_tn.getText().toString().trim();
					beanMemberAndShop.bi = et_bi.getText().toString().trim();

					final RetrofitInterface.ShopService service = ServiceGenerator.createService(RetrofitInterface.ShopService.class);
					final Call<Void> call = service.update(beanMemberAndShop);

					call.enqueue(new Callback<Void>() {
						@Override
						public void onResponse(Call<Void> call, Response<Void> response) {
							final String msg;

							if(response.isSuccessful()) {
								msg = "인사말이 성공적으로 저장되었습니다";

								CommonUtil.hideSoftKeyboardInput(getActivity(), et_bi);
								et_bi.setFocusable(false);
								CommonUtil.setTextViewImage(getActivity(), tv_header_ci, R.drawable.ic_create_red_24dp, 24, 0, 3);
								tv_header_ci.setTag(null);
							} else {
								msg = "인사말 정보 저장에 실패하였습니다\n" + response.errorBody().source().toString();
							}

							Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
						}

						@Override
						public void onFailure(Call<Void> call, Throwable t) {
							Toast.makeText(getActivity(), ServiceGenerator.getExceptionMsgByCause(t), Toast.LENGTH_LONG).show();
						}
					});
				}
				break;
			case R.id.tv_header_post:
				final int owner_id = beanMemberAndShop.id;

				final Intent intent = new Intent(getActivity(), FragmentContainerActivity.class);

				intent.putExtra(MainCons.EnumExtraName.NAME1.name(), PostEditFragment.class.getSimpleName());
				intent.putExtra("board_id", 9); //
				intent.putExtra("owner_id", owner_id);
				intent.putExtra("owner_cn", beanMemberAndShop.cn);
				//신규이므로 BeanPost는 전달하지 않는다

				startActivityForResult(intent, 800);
				getActivity().overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
				break;
			default:
		}
	}

	public boolean isModified() {
		if(isModifiedStandardInfo()) {
			Toast.makeText(getActivity(), "기본 정보가 변경되었습니다\n먼저 저장 후 종료 하세요", Toast.LENGTH_LONG).show();
			return true;
		}

		if(!et_bi.getText().toString().equals(beanMemberAndShop.bi)) {
			Toast.makeText(getActivity(), "인사말이 변경되었습니다\n먼저 저장 후 종료 하세요", Toast.LENGTH_LONG).show();
			return true;
		}

		return  false;
	}

	private boolean isModifiedStandardInfo() {
		boolean isModified = false;

		if(!(et_ht.getText().toString().trim().equals(beanMemberAndShop.ht) && et_ca.getText().toString().trim().equals(beanMemberAndShop.ca) && et_tn.getText().toString().trim().equals(beanMemberAndShop.tn))) {
			isModified = true;
		} else {
			if(!getBmcIdString().equals(beanMemberAndShop.bmc)) {
				isModified = true;
			}
		}

		return isModified;
	}

	private String getBmcIdString() {
		String bmcIdString = "";
		int i = -1;

		for(boolean cheked : isCheckedStatusArray) {
			i++;
			if(cheked) {
				final String qqq = String.valueOf(i + 1);
				bmcIdString = bmcIdString.concat(bmcIdString.length() == 0 ? qqq : "," + qqq);
			}
		}

		return bmcIdString.isEmpty() ? "0" : bmcIdString;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) return;

		if (requestCode == 800) {
			//새글
			final BeanPost newBeanPost = (BeanPost)data.getSerializableExtra("beanPost");
			rva.beanPostList.add(0, newBeanPost);
			rva.notifyItemInserted(0);
			rv_post.scrollToPosition(0);
		} else if (requestCode == 801) {
			//글보기 - 글수정 - 수정 또는 삭제후 이곳으로 바로 자동 점프
			final BeanPost modifiedBeanPost = (BeanPost)data.getSerializableExtra("beanPost");

			if(modifiedBeanPost.id == -1) {
				rva.removeItem(modifyPos);
			} else {
				final BeanPost xx = rva.beanPostList.get(modifyPos);
				//xx.tt =   modifiedBeanPost.tt;
				//xx.ud =   modifiedBeanPost.ud;
				//xx.fcb =   modifiedBeanPost.fcb;
				xx.ffn =   modifiedBeanPost.ffn;

				rva.notifyItemChanged(modifyPos);
			}
		} else if (requestCode == CN_REQUEST_ADDRESS_BY_GPS) {
			/*
			intent.putExtra(MainCons.EnumExtraName.NAME1.name(), marker.getSnippet());
        	intent.putExtra(MainCons.EnumExtraName.NAME2.name(), location);
			*/

			et_ca.setText(data.getStringExtra(MainCons.EnumExtraName.NAME1.name()));
			//et_ca.requestFocus();
			//Selection.setSelection(et_ca.getText(), et_ca.getText().length());
			//et_ca.requestFocus();
		}
	}

	public ShopInfoFragment() {

	}

	public static ShopInfoFragment newInstance(BeanMemberAndShop beanMemberAndShop, int owner_id) {
		final ShopInfoFragment fragment = new ShopInfoFragment();
		final Bundle bundle = new Bundle();

		if(beanMemberAndShop == null) {
			bundle.putInt("owner_id", owner_id);
		} else {
			bundle.putSerializable(BeanMemberAndShop.class.getSimpleName(), beanMemberAndShop);
		}
		
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
		View rootView = inflater.inflate(R.layout.fragment_shopinfo, container, false);

		ButterKnife.bind(this, rootView);

		rva = new MyRecyclerAdapter();

		final LinearLayoutManager lm = new LinearLayoutManager(getActivity());
		lm.setOrientation(GridLayoutManager.HORIZONTAL);

		//rv_post.setHasFixedSize(true);
		rv_post.setNestedScrollingEnabled(false); //이거 안하먼 rv를 스크롤할 때 CollapsingToolbarLayout와 연동되지 않고 NestedScrollView내에서 따로 논다
		rv_post.setLayoutManager(lm);
		rv_post.setItemAnimator(new DefaultItemAnimator());
		rv_post.setAdapter(rva);

		final Bundle bundle = getArguments();
		beanMemberAndShop = (BeanMemberAndShop)bundle.getSerializable(BeanMemberAndShop.class.getSimpleName());

		final int owner_id;

		if(beanMemberAndShop == null) {
			owner_id = bundle.getInt("owner_id", -1);
			loadShopInfo(owner_id);
		} else {
			owner_id = beanMemberAndShop.id;
			retrieveShopInfo();
		}

		loadPostList(owner_id);

		if(beanMember.id == 1 || beanMember.id == 2 || beanMember.id == owner_id) {
			editable = true;

			CommonUtil.setTextViewImage(getActivity(), tv_header_standard, R.drawable.ic_create_red_24dp, 24, 0, 3);
			CommonUtil.setTextViewImage(getActivity(), tv_header_ci, R.drawable.ic_create_red_24dp, 24, 0, 3);
			CommonUtil.setTextViewImage(getActivity(), tv_header_post, R.drawable.ic_create_red_24dp, 24, 0, 3);
		}

		return rootView;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home) {
			if(!isModified()) {
				getActivity().finish();;
			}
		}

		return super.onOptionsItemSelected(item);
	}

	private void retrieveShopInfo() {
		/*
		final MapFragment mf = (MapFragment)getChildFragmentManager().findFragmentById(R.id.mf);
		mf.getMapAsync(this);
		*/

		/*
		final GoogleMapOptions options = new GoogleMapOptions();
		options.liteMode(true).zoomControlsEnabled(true).zoomGesturesEnabled(false).compassEnabled(true).mapToolbarEnabled(true);

		final LatLng latLng = new LatLng(37.517180, 127.041268);

		CameraPosition camera = new CameraPosition(latLng, 13, 0, 0);
		options.camera(camera);

		final MapFragment mapFragment = MapFragment.newInstance(options);
		*/

		final MapFragment mapFragment = MapFragment.newInstance();
		mapFragment.getMapAsync(this);

		final FragmentManager fm = getChildFragmentManager();
		final FragmentTransaction ft = fm.beginTransaction();

		ft.add(R.id.fl_map, mapFragment).commit();

		final String bmc = beanMemberAndShop.bmc;
		String bmcString;

		if(bmc.equals("0")) {
			bmcString = "카테고리 미지정";
			tv_bmc.setText(bmcString);
		} else {
			final String[] bmcArray = bmc.split(",");
			int j = -1;

			for(String s : bmcArray) {
				j++;
				final int bmCategory_id = Integer.parseInt(s);
				isCheckedStatusArray[bmCategory_id - 1] = true; //위치기준
			}

			bbb();
		}

		et_cn.setText(beanMemberAndShop.cn);
		et_ht.setText(beanMemberAndShop.ht);
		et_ca.setText(beanMemberAndShop.ca);
		et_tn.setText(beanMemberAndShop.tn);

		et_bi.setText(beanMemberAndShop.bi);
	}

	private void bbb() {
		String bmcString = "";
		int i = -1;

		for(boolean cheked : isCheckedStatusArray) {
			i++;
			if(cheked) {
				final MainCons.EnumBmCategory enumBmCategory = MainCons.EnumBmCategory.values()[i];
				bmcString = bmcString.concat(enumBmCategory.getNm() + " ");
			}
		}

		tv_bmc.setText(bmcString.isEmpty() ? "카테고리 미지정" : bmcString);
	}

	private void loadShopInfo(final int owner_id) {
		final RetrofitInterface.ShopService service = ServiceGenerator.createService(RetrofitInterface.ShopService.class);
		final Call<List<BeanMemberAndShop>> call = service.select(owner_id);
		call.enqueue(new Callback<List<BeanMemberAndShop>>() {
			@Override
			public void onResponse(Call<List<BeanMemberAndShop>> call, Response<List<BeanMemberAndShop>> response) {
				if(response.isSuccessful()) {
					if(response.body().size() == 0) {
						//논리적으로 이럴 경우는 원천 봉쇄되어야 한다.
						Toast.makeText(getActivity(), "업체정보가 존재하지 않습니다", Toast.LENGTH_LONG).show();
						return;
					}

					beanMemberAndShop = response.body().get(0);
					retrieveShopInfo();
				}
				else {
					Toast.makeText(getActivity(), response.errorBody().source().toString(), Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onFailure(Call<List<BeanMemberAndShop>> call, Throwable t) {
				Toast.makeText(getActivity(), ServiceGenerator.getExceptionMsgByCause(t), Toast.LENGTH_LONG).show();
			}
		});
	}

	//TODO : loadShopInfo, loadPostList 통합할 것
	private void loadPostList(final int owner_id) {
		//1. 회사소개 post list
		final RetrofitInterface.PostService service = ServiceGenerator.createService(RetrofitInterface.PostService.class);
		final Call<List<BeanPost>> call = service.selectByFree(-1, 9, owner_id, -1, "", 0, 20); //--> infinite scroll은 지원할 필요가 없고 최근 20개로 하자
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

	@Override
	public void onMapReady(GoogleMap map) {
		try {
			final Geocoder geocoder = new Geocoder(getActivity());
			final List<Address> addressList = geocoder.getFromLocationName(beanMemberAndShop.ca, 1); // 얻어올 값의 개수

			if (addressList.size() == 0) {
				Toast.makeText(getActivity(), "해당되는 주소 정보는 없습니다", Toast.LENGTH_LONG).show();
			} else {
				final Address address = addressList.get(0);
				final LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

				//map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

				final MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.draggable(false).position(latLng).title(beanMemberAndShop.cn).snippet(beanMemberAndShop.ca);
				map.addMarker(markerOptions).showInfoWindow();
				////////////////////////////map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
				//map.addMarker(marker);
			}

		} catch (IOException e) {
			Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onDestroyView() {

		super.onDestroyView();

		/*
		//중요 : XML 에 android:name="com.google.android.gms.maps.MapFragment"를 사용하여 fragment를 직접 inject할 결루
		//1. Tab의 한 요소로 존재하는 fragment내에 MapFragment를 사용할 경우 onCreateView가 다시 시도될 때 fragment 중복오류 발생
		//   Duplicate id 0x7f0e011e, tag null, or parent id 0xffffffff 오류 발생

		final FragmentManager fm = getChildFragmentManager();
		final MapFragment mf = (MapFragment)getChildFragmentManager().findFragmentById(R.id.fl_map);
		final FragmentTransaction ft = fm.beginTransaction();
		ft.remove(mf);
		ft.commit();
		*/
	}

    private class MyRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
		private final ImageLoader imageLoader = ImageLoader.getInstance();
		private final List<BeanPost> beanPostList;

		private final View.OnClickListener ocl;

        private MyRecyclerAdapter() {
            this.beanPostList = new ArrayList<>();

			ocl = new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					final int pos = CommonUtil.getAdapterPositionFromView(rv_post, v);
					final BeanPost beanPost = beanPostList.get(pos);

					modifyPos = pos;
					final Intent intent2 = new Intent(getActivity(), FragmentContainerActivity.class);
					intent2.putExtra(MainCons.EnumExtraName.NAME1.name(), PostReadFragment.class.getSimpleName());
					intent2.putExtra("beanPost", beanPost);
					startActivityForResult(intent2, 801);
					getActivity().overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
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
        public int getItemViewType(int pos) {
			//1. 대표 이미지가 있는 회사소개만 가져온다.
			//2. 대표 텍스트는 사용하지 않는다

			return beanPostList.get(pos).id == -1 ? -1 : 1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup vg, int itemViewType) {
            final RecyclerView.ViewHolder vh;

            if(itemViewType == -1) {
				//empty holde presentation
                vh = new VhText1(LayoutInflater.from(getActivity()).inflate(R.layout.vh_empty_text_image, vg, false));;
            } else {
                vh = new VhPostCi(LayoutInflater.from(getActivity()).inflate(R.layout.vh_post_ci, vg, false));
				vh.itemView.setOnClickListener(ocl);
            }

            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder vh, int pos) {
            final int itemViewType = getItemViewType(pos);

            if(itemViewType == -1) {
                final VhText1 vhText1 = (VhText1)vh;

                vhText1.tv_31.setText("작성된 소개 포스트가 없습니다");
            } else {
                final VhPostCi vhPostCi = (VhPostCi)vh;
                final BeanPost beanPost = beanPostList.get(pos);
                final String url;

				/*
				//로컬 이미비를 사용할려고 하니 video의 경우 일이 복잡하여 일단 ReLoading하는걸로 처리한다
                if (beanPost.ffn.contains(":")) {
					final File file = new File(beanPost.ffn);
					final Uri uri = Uri.fromFile(file);
					url = uri.toString();
                } else {
					url = MainCons.EnumContentPath.CONTENT_I.getPath() + beanTempMedia.su;
                }
                */

				final String fileName = beanPost.ffn;
				final String mimeType = MediaUtil.getMimeType(fileName);
				final MediaType mediaType = MediaType.parse(mimeType);
				final int videoIconVisivility;

				//vhPostCi.tv_31.setText(beanPost.tt); --> 제목이 들어가니깐 없어 보인다

				if(mediaType.type().equals("image")) {
					url = MainCons.EnumContentPath.CONTENT_I.getPath() + fileName;
					videoIconVisivility = View.GONE;
				} else {
					url = MainCons.EnumContentPath.CONTENT_V_T.getPath() + fileName.replace("mp4", "jpg");
					videoIconVisivility = View.VISIBLE;
				}

				vhPostCi.iv_video_icon.setVisibility(videoIconVisivility);

				imageLoader.displayImage(url, vhPostCi.iv_31);
            }
        }
    }
}