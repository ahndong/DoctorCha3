package com.infoline.doctorcha.presentation.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.CoreCons;
import com.infoline.doctorcha.core.binding.MyBindClickListener;
import com.infoline.doctorcha.core.binding.MyBindTextChangedListener;
import com.infoline.doctorcha.core.httphelper.ServiceGenerator;
import com.infoline.doctorcha.core.util.CommonUtil;
import com.infoline.doctorcha.databinding.FragmentMemolistBinding;
import com.infoline.doctorcha.databinding.VhMemoBinding;
import com.infoline.doctorcha.presentation.RetrofitInterface;
import com.infoline.doctorcha.presentation.bean.BeanMemo;
import com.infoline.doctorcha.presentation.bean.BeanErrResponse;
import com.infoline.doctorcha.presentation.viewholder.BindingHolder;
import com.infoline.doctorcha.presentation.viewholder.VhEdit1;
import com.infoline.doctorcha.presentation.viewholder.VhEdit1Text1;
import com.infoline.doctorcha.presentation.viewholder.VhText1;
import com.infoline.doctorcha.presentation.viewholder.VhText2;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.infoline.doctorcha.presentation.MainApp.beanMember;

public class MemoListFragment extends Fragment {
	private FragmentMemolistBinding binding;

	private MyRecyclerAdapter rva;
	private List<BeanMemo> beanMemoList;
	int target_id;

    public MemoListFragment() {
    }

    public static MemoListFragment newInstance(int target_id) {
		final MemoListFragment fragment = new MemoListFragment();
		final Bundle bundle = new Bundle();

		bundle.putInt("target_id", target_id);
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
		return inflater.inflate(R.layout.fragment_memolist, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		binding = FragmentMemolistBinding.bind(getView());

		binding.setMyBindEventHandlers(new MyBindEventHandlers());

		final Bundle bundle = getArguments();
		target_id = bundle.getInt("target_id");

		beanMemoList = new ArrayList<>();

		rva = new MyRecyclerAdapter();

		final LinearLayoutManager lm = new LinearLayoutManager(getActivity());

		binding.rv.setHasFixedSize(true);
		binding.rv.setLayoutManager(lm);
		binding.rv.setItemAnimator(new DefaultItemAnimator());
		binding.rv.setAdapter(rva);

		loadData();
	}

	private void loadData() {
		final RetrofitInterface.MemoService service = ServiceGenerator.createService(RetrofitInterface.MemoService.class);
		final Call<List<BeanMemo>> call = service.selectByWriterIdAndTargetId(beanMember.id, target_id);
		call.enqueue(new Callback<List<BeanMemo>>() {
			@Override
			public void onResponse(Call<List<BeanMemo>> call, Response<List<BeanMemo>> response) {
				String msg = null;

				if(response.isSuccessful()) {
					beanMemoList.addAll(response.body());
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
			public void onFailure(Call<List<BeanMemo>> call, Throwable t) {
				CommonUtil.writeLog(ServiceGenerator.getExceptionMsgByCause(t));
			}
		});
	}

	public class MyRecyclerAdapter extends RecyclerView.Adapter<BindingHolder> implements MyBindClickListener {
		private final Context ctx;

		public MyRecyclerAdapter() {
			this.ctx = getActivity();
		}

		@Override
		public int getItemCount() {
			return beanMemoList.size();
		}

		@Override
		public int getItemViewType(int pos) {
			return beanMemoList.get(pos).vhm; //view holder mode
		}

		@Override
		public BindingHolder onCreateViewHolder(ViewGroup vg, int itemViewType) {
			final RecyclerView.ViewHolder vh;

			final int resId;

			if(itemViewType == -1) {
				//시간상 add 구현 안했슴
				resId = R.layout.vh_empty_text_image;
			} else {
				//1. 정산, 수정상태, 신규상태 모두 같은 layout을 사용한다
				resId = R.layout.vh_memo;
			}

			final BindingHolder bh = new BindingHolder(LayoutInflater.from(getActivity()).inflate(resId, vg, false), this);

			if(itemViewType == -1) {
				((TextView)bh.itemView.findViewById(R.id.tv_31)).setText("선택된 회원관련 작성된 메모가 없습니다");
			} else {
				final VhMemoBinding vhMemoBinding = (VhMemoBinding)bh.getBinding();

				vhMemoBinding.et31.addTextChangedListener(new TextWatcher() {
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {}

					@Override
					public void afterTextChanged(Editable s) {
						final BeanMemo beanMemo = beanMemoList.get(bh.getAdapterPosition());
						beanMemo.mm = s.toString();
					}
				});
			}

			return bh;
		}

		@Override
		public void onBindViewHolder(BindingHolder bh, int pos) {
			//bh.getBinding().setVariable(BR., beanMemoList.get(pos));
			final BeanMemo beanMemo = beanMemoList.get(pos);

			final int itemViewType = getItemViewType(pos);

			if(itemViewType != -1) {
				//0.정상 1. 수정  2.신규
				((VhMemoBinding)bh.getBinding()).setBeanMemo(beanMemo);
			}
		}

		@Override
		public void onBindClick(View v) {
			final int pos = CommonUtil.getAdapterPositionFromView(binding.rv, v);
			final BeanMemo beanMemo = beanMemoList.get(pos);
			final int itemViewType = beanMemo.vhm;
			final int viewId = v.getId();

			if(viewId == R.id.iv_del_restore) {
				if(itemViewType == 0) { //삭제
					final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if(which == DialogInterface.BUTTON_POSITIVE) {
								final RetrofitInterface.MemoService service = ServiceGenerator.createService(RetrofitInterface.MemoService.class);
								final Call<Void> call = service.delete(beanMemo.id);

								call.enqueue(new Callback<Void>() {
									@Override
									public void onResponse(Call<Void> call, Response<Void> response) {
										final String msg;

										if(!response.isSuccessful()) {
											msg = "케이지 삭제에 실패하였습니다\n" + response.errorBody().source().toString();
										} else {
											beanMemoList.remove(pos);
											rva.notifyItemRemoved(pos);
											msg = "케이지가 성공적으로 삭제되었습니다.";
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
					};

					final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setMessage("이 케이지를 영구적으로 삭제하시겠습니까?").setPositiveButton("예", dialogClickListener).setNegativeButton("아니오", dialogClickListener).show();
				} else {
					//1. vh_device_edit : 수정 취소 - 읽기 모드로 전환
					//2. 수정사항 있는지의 체크는 시간상 보류한다

					if(beanMemo.vhm == 1) {
						//변경사항 취소하고 읽기 모드로 전환
						beanMemo.vhm = 0;
						notifyItemChanged(pos);
					} else {
						beanMemoList.remove(pos);
						notifyItemRemoved(pos);
					}
				}

			} else if(viewId == R.id.iv_modi_save) {
				if(itemViewType == 0) {
					//수정상태로 전환
					beanMemo.vhm = 1; //수정 모드
					notifyItemChanged(pos);
				} else {
					//수정, 추가상태에서 변경내용 저장
					final RetrofitInterface.MemoService service = ServiceGenerator.createService(RetrofitInterface.MemoService.class);

					if(beanMemo.vhm == 1) {
						final Call<Void> call = service.update(beanMemo);
						call.enqueue(new Callback<Void>() {
							@Override
							public void onResponse(Call<Void> call, Response<Void> response) {
								final String msg;

								if(!response.isSuccessful()) {
									msg = "메모 저장에 실패하였습니다\n" + response.errorBody().source().toString();
								} else {
									beanMemo.vhm = 0;
									rva.notifyItemChanged(pos);
									msg = "메모가 성공적으로 저장되었습니다.";
								}

								Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
							}

							@Override
							public void onFailure(Call<Void> call, Throwable t) {
								Toast.makeText(getActivity(), ServiceGenerator.getExceptionMsgByCause(t), Toast.LENGTH_LONG).show();
							}
						});
					} else {
						final Call<Integer> call = service.insert(beanMemo);
						call.enqueue(new Callback<Integer>() {
							@Override
							public void onResponse(Call<Integer> call, Response<Integer> response) {
								final String msg;

								if(!response.isSuccessful()) {
									msg = "메모 저장에 실패하였습니다\n" + response.errorBody().source().toString();
								} else {
									beanMemo.vhm = 0;
									beanMemo.id = response.body();
									rva.notifyItemChanged(pos);
									msg = "메모가 성공적으로 저장되었습니다.";
								}

								Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
							}

							@Override
							public void onFailure(Call<Integer> call, Throwable t) {
								Toast.makeText(getActivity(), ServiceGenerator.getExceptionMsgByCause(t), Toast.LENGTH_LONG).show();
							}
						});
					}
				}
			}
		}

		@Override
		public boolean onBindLongClick(View v) {


			return true;
		}
	}

	//--------Fragment level Event handler-------------------------

	public class MyBindEventHandlers {
		//R.id.fab
		public void onBindClick(final View v) {
			final BeanMemo beanMemo =new BeanMemo();
			beanMemo.writer_id = beanMember.id;
			beanMemo.target_id = target_id;
			beanMemo.ud = CommonUtil.getFormattedDate(CoreCons.EnumDateFormat.DATE_AND_TIME);
			beanMemo.vhm = 2;

			beanMemoList.add(beanMemo);
			rva.notifyDataSetChanged();
		}
	}
}