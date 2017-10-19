package com.infoline.doctorcha.presentation.fragment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.LineHeightSpan;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.httphelper.ServiceGenerator;
import com.infoline.doctorcha.core.util.CommonUtil;
import com.infoline.doctorcha.core.util.MediaUtil;
import com.infoline.doctorcha.presentation.MainApp;
import com.infoline.doctorcha.presentation.MainCons;
import com.infoline.doctorcha.presentation.RetrofitInterface;
import com.infoline.doctorcha.presentation.activity.FragmentContainerActivity;
import com.infoline.doctorcha.presentation.activity.LoginActivity;
import com.infoline.doctorcha.presentation.activity.MainActivity;
import com.infoline.doctorcha.presentation.activity.MainOfShopsActivity;
import com.infoline.doctorcha.presentation.activity.ShopActivity;
import com.infoline.doctorcha.presentation.bean.BeanErrResponse;
import com.infoline.doctorcha.presentation.bean.BeanMemberAndShop;
import com.infoline.doctorcha.presentation.bean.BeanMultiFavorite;
import com.infoline.doctorcha.presentation.bean.BeanName;
import com.infoline.doctorcha.presentation.bean.BeanPost;
import com.infoline.doctorcha.presentation.bean.BeanSectionItem;
import com.infoline.doctorcha.presentation.bean.BeanSectionHeader;
import com.infoline.doctorcha.presentation.bean.BeanName_Res;
import com.infoline.doctorcha.presentation.viewholder.VhImage1Text1;
import com.infoline.doctorcha.presentation.viewholder.VhImage1Text3_stats;
import com.infoline.doctorcha.presentation.viewholder.VhPostStatus;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.infoline.doctorcha.presentation.MainApp.beanMember;

public class MainFavoriteFragment extends Fragment {
    @BindView(R.id.rv)
    RecyclerView rv;

    private Context ctx;
    private MyRecyclerAdapter rva;
    private SparseArray<List<BeanSectionItem>> dletedBeanSectionItemSpaseArray;

    private static final int CN_SECTION_FAVORITE_CATEGORY = 1;
    private static final int CN_SECTION_MOU_CATEGORY = 2;
    private static final int CN_SECTION_BEST_SHOP = 3;
    private static final int CN_SECTION_FAVORITE_SHOP = 4;
    private static final int CN_SECTION_NEW_EVENT = 5;
    private static final int CN_SECTION_NEW_USEDCAR = 6;
    private static final int CN_SECTION_NEW_POST = 7;

    private static final int CN_IVT_FAVORITE_CATEGORY = 11;
    private static final int CN_IVT_MOU_GROUP = 21;
    private static final int CN_IVT_FAVORITE_SHOP = 31;
    private static final int CN_IVT_BEST_SHOP = 41;
    private static final int CN_IVT_NEW_EVENT_TEXT = 51;    //only text
    private static final int CN_IVT_NEW_EVENT_MEDIA = 52;   //photo or video만
    private static final int CN_IVT_NEW_EVENT_BOTH = 53;    //text and (photo or video)
    private static final int CN_IVT_NEW_USEDCAR = 61;
    private static final int CN_IVT_NEW_POST_TEXT = 71;
    private static final int CN_IVT_NEW_POST_MEDIA = 72;
    private static final int CN_IVT_NEW_POST_BOTH = 73;

    private List<BeanSectionHeader> beanSectionHeaderList;
    private List<BeanSectionItem> beanSectionItemList;
    
    private List<BeanName_Res> beanFavoriteCategoryList; //1

    private List<BeanName> beanMouCategoryList; //2
    private List<BeanMemberAndShop> beanBestShopList; //3
    private List<BeanMemberAndShop> beanFavoriteShopList; //4

    private List<BeanPost> beanNewEventList; //5
    private List<BeanPost> beanNewUsedCarList; //6
    private List<BeanPost> beanNewPostList; //7

    int modifyPos;

    public MainFavoriteFragment() {

    }

    public static MainFavoriteFragment newInstance() {
        final MainFavoriteFragment fragment = new MainFavoriteFragment();
        final Bundle bundle = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        ctx = getActivity(); //.getApplicationContext();

        beanSectionHeaderList = new ArrayList<>();
        beanSectionItemList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mainfavorite, container, false);
        ButterKnife.bind(this, rootView);

        rva = new MyRecyclerAdapter();

        //onDetachView된 경우라도 재사용하면 오류발생한다
        final GridLayoutManager lm = new GridLayoutManager(getActivity(), 4);

        lm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int pos) {
                final BeanSectionItem beanSectionItem = beanSectionItemList.get(pos);
                final int st = beanSectionItem.st;
                final int id = beanSectionItem.id;
                final int headerOp = beanSectionItem.headerOp;
                final int spanCount;

                if(id == 0) {
                    //section header
                    spanCount = 4;
                } else if(st == CN_SECTION_NEW_EVENT || st == CN_SECTION_NEW_POST) {
                    spanCount = 4;
                } else if(st == CN_IVT_NEW_USEDCAR) {
                    spanCount = 2;
                } else {
                    if((st == CN_SECTION_FAVORITE_SHOP || st == CN_SECTION_BEST_SHOP)) {
                        final int itemCount = beanSectionHeaderList.get(headerOp).itemCount;
                        if(itemCount >= 3) {
                            //1. 회색 나와도 어쩔 수 없다
                            //2. empty View 넣는 것도 시도 했으나 보기도 않좋고 복잡해서 포기
                            spanCount = 1;
                        } else {
                            spanCount = itemCount == 1 ? 4 : 2;
                        }
                    } else {
                        spanCount = 1;
                    }
                }

                return spanCount;
            }
        });

        rv.setHasFixedSize(true);
        rv.setLayoutManager(lm);
        rv.setItemAnimator(new DefaultItemAnimator()); //하나 안하나 떠거럴
        rv.setAdapter(rva);

        loadData();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_mainfavorite, menu);
    }

    private void xxx(final BeanMultiFavorite beanMultiFavorite) {
        beanFavoriteCategoryList = new ArrayList<>();
        dletedBeanSectionItemSpaseArray = new SparseArray<>();

        for(int i = 0; i < MainCons.EnumBmCategory.values().length; i++) {
            final MainCons.EnumBmCategory enumBmCategory = MainCons.EnumBmCategory.values()[i];
            beanFavoriteCategoryList.add(new BeanName_Res(enumBmCategory.getId(), enumBmCategory.getNm(), enumBmCategory.getRes()));
        }

        beanFavoriteShopList = beanMultiFavorite.d4;
        beanNewEventList = beanMultiFavorite.d5;
        beanNewPostList = beanMultiFavorite.d7;

        beanSectionHeaderList.add(new BeanSectionHeader(CN_SECTION_FAVORITE_CATEGORY, "업체 카테고리", beanFavoriteCategoryList.size(), false));
        beanSectionHeaderList.add(new BeanSectionHeader(CN_SECTION_MOU_CATEGORY, "제휴 카테고리", 0, false));
        beanSectionHeaderList.add(new BeanSectionHeader(CN_SECTION_BEST_SHOP, "베스트 업체", 0, false));
        beanSectionHeaderList.add(new BeanSectionHeader(CN_SECTION_FAVORITE_SHOP, "즐겨찾는 업체", beanFavoriteShopList.size(), false));
        beanSectionHeaderList.add(new BeanSectionHeader(CN_SECTION_NEW_EVENT, "최신 이벤트", beanNewEventList.size(), false));
        beanSectionHeaderList.add(new BeanSectionHeader(CN_SECTION_NEW_USEDCAR, "최신 중고차 매물", 0, false));
        beanSectionHeaderList.add(new BeanSectionHeader(CN_SECTION_NEW_POST, "전체 소식", beanNewPostList.size(), false));

        //1. 업체 카테고리 section header 및 item 추가
        beanSectionItemList.add(new BeanSectionItem(CN_SECTION_FAVORITE_CATEGORY, 0, 0, 0)); //0 == section header
        int op = -1;
        for(BeanName_Res beanFavoriteCategory : beanFavoriteCategoryList) {
            op++;
            beanSectionItemList.add(new BeanSectionItem(CN_SECTION_FAVORITE_CATEGORY, beanFavoriteCategory.id, 0, op));
        }

        /*
        //2. 제휴 카테고리 section header 및 item 추가
        beanSectionItemList.add(new BeanSectionItem(CN_SECTION_MOU_CATEGORY, 0, 1));
        */

        /*
        //3. 베스트 업체 section header 및 item 추가
        beanSectionItemList.add(new BeanSectionItem(CN_SECTION_BEST_SHOP, 0, 2));
        */

        //4. 즐겨찾는 업체 section header 및 추가
        beanSectionItemList.add(new BeanSectionItem(CN_SECTION_FAVORITE_SHOP, 0, 3, 3));
        op = -1;
        for(BeanMemberAndShop beanMemberAndShop : beanFavoriteShopList) {
            op++;
            beanSectionItemList.add(new BeanSectionItem(CN_SECTION_FAVORITE_SHOP, beanMemberAndShop.id, 3, op));
        }

        //5. 최신 이벤트 section header 및 item 추가
        beanSectionItemList.add(new BeanSectionItem(CN_SECTION_NEW_EVENT, 0, 4, 4));
        op = -1;
        for(BeanPost beanPost : beanNewEventList) {
            op++;
            beanSectionItemList.add(new BeanSectionItem(CN_SECTION_NEW_EVENT, beanPost.id, 4, op));
        }

        /*
        //6. 최신 중고차 매물 section header 및 item 추가
        beanSectionItemList.add(new BeanSectionItem(CN_SECTION_NEW_USEDCAR, 0, 5));
        */

        //7. 최신 소식 section header 및 item 추가
        beanSectionItemList.add(new BeanSectionItem(CN_SECTION_NEW_POST, 0, 6, 6));
        op = -1;
        for(BeanPost beanPost : beanNewPostList) {
            op++;
            beanSectionItemList.add(new BeanSectionItem(CN_SECTION_NEW_POST, beanPost.id, 6, op));
        }

        rva.notifyDataSetChanged();
    }

    private void loadData() {
        final RetrofitInterface.MultiService service = ServiceGenerator.createService(RetrofitInterface.MultiService.class);
        final Call<BeanMultiFavorite> call = service.selectFavoriteByMemberId(beanMember.id);
        call.enqueue(new Callback<BeanMultiFavorite>() {
            @Override
            public void onResponse(Call<BeanMultiFavorite> call, Response<BeanMultiFavorite> response) {
                if(response.isSuccessful()) {
                    xxx(response.body());
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
            public void onFailure(Call<BeanMultiFavorite> call, Throwable t) {
                Toast.makeText(getActivity(), ServiceGenerator.getExceptionMsgByCause(t), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //801 : PostReadFragment
        //803 : PostReplFragment

        final BeanSectionItem beanSectionItem = beanSectionItemList.get(modifyPos);
        final int st = beanSectionItem.st;
        final int op = beanSectionItem.op;
        final BeanPost beanPost = rva.getBeanPost(st, op);

        if (requestCode == 801) {
            beanPost.rec += 1;
            rva.notifyItemChanged(modifyPos);
        }

        //1. post의 댓글 아이콘을 클릭하여 댓글창으로 직접 진입했을 경우
        //   PostReplFragment의 FragmentContainerActivity에서 편법으로 전달된다
        //2. post 클릭하여 글보기 창으로 진입했을 경우
        //   PostReadFragment의 FragmentContainerActivity에서 편법으로 전달된다
        final int replCount = data.getIntExtra("replCount", 0);

        if(replCount != 0) {
            beanPost.coc += replCount;
            rva.notifyItemChanged(modifyPos);
        }

        if (resultCode != RESULT_OK) return;

        if (requestCode == 801) {
            //글보기 - 글수정 - 수정 또는 삭제후 이곳으로 바로 자동 점프
            final BeanPost modifiedBeanPost = (BeanPost)data.getSerializableExtra("beanPost");

            if(modifiedBeanPost.id == -1) {
                //rva.removeItem(modifyPos);
                rva.getBeanPostList(st).remove(beanPost);
                beanSectionItemList.remove(beanSectionItem);
                final BeanSectionHeader beanSectionHeader = beanSectionHeaderList.get(op);
                beanSectionHeader.itemCount -= 1;

                rva.notifyItemRemoved(modifyPos);

            } else {
                beanPost.tt =   modifiedBeanPost.tt;
                beanPost.ud =   modifiedBeanPost.ud;
                beanPost.fcb =   modifiedBeanPost.fcb;
                beanPost.ffn =   modifiedBeanPost.ffn;

                rva.notifyItemChanged(modifyPos);
            }
        }
    }

    public class MyRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {
        private final ImageLoader imageLoader = ImageLoader.getInstance();
        private final DisplayImageOptions options;

        public MyRecyclerAdapter() {
            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .showImageOnFail(R.drawable.shop)
                    .displayer(new RoundedBitmapDisplayer(15))
                    .build();
        }

        private BeanMemberAndShop getBeanMemberAndShop(final int st, final int op) {
            return st == CN_SECTION_FAVORITE_SHOP ? beanFavoriteShopList.get(op) : beanBestShopList.get(op);
        }

        private BeanPost getBeanPost(final int st, final int op) {
            return st == CN_SECTION_NEW_EVENT ? beanNewEventList.get(op) : beanNewPostList.get(op);
        }

        private List<BeanPost> getBeanPostList(final int st) {
            return st == CN_SECTION_NEW_EVENT ? beanNewEventList : beanNewPostList;
        }

        public void onClick(final View v) {
            final int viewId = v.getId();
            final int pos = CommonUtil.getAdapterPositionFromView(rv, v);
            final int itemViewType = getItemViewType(pos);

            final BeanSectionItem beanSectionItem = beanSectionItemList.get(pos);
            final int op = beanSectionItem.op;
            final int st = beanSectionItem.st;

            if(itemViewType <= 7) {
                final BeanSectionHeader beanSectionHeader = beanSectionHeaderList.get(op);
                final int itemCount = beanSectionHeader.itemCount;

                if(itemCount == 0) return;

                final int sectionId = beanSectionHeader.id;
                final boolean collapsed = !beanSectionHeader.collapsed;
                final ImageView iv_31 = (ImageView)v.findViewById(R.id.iv_31);

                iv_31.setRotation(collapsed ? 0f : 180f);
                iv_31.animate().rotation(collapsed ? 180f : 0f).setDuration(500).start();

                if(collapsed) {
                    /*
                    final List<BeanSectionItem> deletingList = beanSectionItemList.subList(pos + 1, pos + 1 + itemCount);

                    dletedBeanSectionItemSpaseArray.put(sectionId, deletingList);

                    //1. beanMainFavorites.removeAll(sub) 헐 거꿀로다
                    //2. removeAll은 우쨌거니 졸라 늦다.
                    //3, 더 헐..deletingList.removeAll 하니깐 dletedBeanSectionItemSpaseArray 해당 요소도 삭제된다 - 참조 형식인가 보다
                    //4. 아래에서 iterator로 삭제하니깐 졸라 빠르다
                    deletingList.removeAll(beanSectionItemList);
                    notifyItemRangeRemoved(pos + 1, itemCount);
                    */

                    final List<BeanSectionItem> deletingList = beanSectionItemList.subList(pos + 1, pos + 1 + itemCount);
                    final List<BeanSectionItem> deletingListCopy = new ArrayList<>();

                    final Iterator<BeanSectionItem> iterator = deletingList.iterator();
                    while (iterator.hasNext()) {
                        final BeanSectionItem ir = iterator.next();
                        deletingListCopy.add(ir);

                        iterator.remove();
                    }
                    notifyItemRangeRemoved(pos + 1, itemCount);

                    dletedBeanSectionItemSpaseArray.put(sectionId, deletingListCopy);


                } else {
                    final List<BeanSectionItem> restoreList = dletedBeanSectionItemSpaseArray.get(sectionId);
                    beanSectionItemList.addAll(pos + 1, restoreList);
                    rva.notifyItemRangeInserted(pos + 1, itemCount);
                    dletedBeanSectionItemSpaseArray.remove(sectionId);
                }

                beanSectionHeader.collapsed = collapsed;
            } else {
                switch (viewId) {
                    case R.id.tv_share:
                        break;
                    case R.id.tv_like:
                        break;
                    case R.id.tv_repl:
                    default:
                        final Intent intent;

                        if(viewId == R.id.tv_repl) {
                            modifyPos = pos;
                            intent = new Intent(getActivity(), FragmentContainerActivity.class);
                            intent.putExtra(MainCons.EnumExtraName.NAME1.name(), PostReplFragment.class.getSimpleName());
                            intent.putExtra("beanPost", getBeanPost(st, op));
                            startActivityForResult(intent, 803);
                            getActivity().overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
                        }else {
                            //vh.viewItem click
                            switch (st) {
                                case CN_SECTION_FAVORITE_CATEGORY:
                                    final BeanName_Res beanFavoriteCategory = beanFavoriteCategoryList.get(op);
                                    intent =  new Intent(getActivity(), MainOfShopsActivity.class);

                                    intent.putExtra(MainCons.EnumExtraName.ANIM_START_POINT.name(), CommonUtil.getCenterPointFromView(v.findViewById(R.id.iv_31)));
                                    intent.putExtra(BeanName_Res.class.getSimpleName(), beanFavoriteCategory);
                                    startActivity(intent);

                                    break;
                                case CN_SECTION_BEST_SHOP:
                                case CN_SECTION_FAVORITE_SHOP:
                                    if(op == -1) return;

                                    intent = new Intent(ctx, ShopActivity.class);

                                    final BeanMemberAndShop beanMemberAndShop = getBeanMemberAndShop(st, op);
                                    final String transitionName = MainCons.EnumTransitionName.THUMB.name();

                                    //intent.putExtra("owner_id", owner_id); --> 너무 늦어 animation이 이상하게 작동한다
                                    intent.putExtra(BeanMemberAndShop.class.getSimpleName(), beanMemberAndShop);
                                    intent.putExtra(MainCons.EnumTransitionName.class.getSimpleName(), transitionName);

                                    final View vvv = v.findViewById(R.id.iv_31);

                                    final Bundle animBundle;
                                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        intent.putExtra(MainCons.EnumActivityAnimType.class.getSimpleName(), MainCons.EnumActivityAnimType.ANIM_SCENE_TRANSITION);
                                        vvv.setTransitionName(transitionName);
                                        animBundle = ActivityOptions.makeSceneTransitionAnimation((Activity)ctx, vvv, transitionName).toBundle();

                                        /*
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                        animBundle = new Bundle();
                                        animBundle.putInt("android:activity.animType", 11);
                                        */
                                    }
                                    else {
                                        intent.putExtra(MainCons.EnumActivityAnimType.class.getSimpleName(), MainCons.EnumActivityAnimType.ANIM_THUMBNAIL_SCALE_UP);
                                        animBundle = ActivityOptionsCompat.makeScaleUpAnimation(vvv, vvv.getMeasuredWidth()/2, vvv.getMeasuredHeight()/2, 0, 0).toBundle();

                                        /*
                                        ViewCompat.setTransitionName(vvv, transitionName);
                                        animBundle = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)ctx, vvv, transitionName).toBundle();
                                        */
                                    }

                                    startActivity(intent, animBundle);

                                    break;
                                default:
                                    modifyPos = pos;
                                    intent = new Intent(getActivity(), FragmentContainerActivity.class);
                                    intent.putExtra(MainCons.EnumExtraName.NAME1.name(), PostReadFragment.class.getSimpleName());
                                    intent.putExtra("beanPost", getBeanPost(st, op));
                                    startActivityForResult(intent, 801);
                                    getActivity().overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
                            }
                        }

                }
            }
        }

        public boolean onLongClick(View v) {
            final int viewId = v.getId();
            final int pos = CommonUtil.getAdapterPositionFromView(rv, v);
            final int itemViewType = getItemViewType(pos);

            final BeanSectionItem beanSectionItem = beanSectionItemList.get(pos);
            final int st = beanSectionItem.st;
            final int headerOp = beanSectionItem.headerOp;
            final int op = beanSectionItem.op;

            final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if(which == DialogInterface.BUTTON_POSITIVE) {
                        final RetrofitInterface.FavoriteShopService service = ServiceGenerator.createService(RetrofitInterface.FavoriteShopService.class);
                        BeanMemberAndShop beanMemberAndShop = getBeanMemberAndShop(st, op);
                        final Call<Void> call = service.delete(beanMember.id, beanMemberAndShop.id);

                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                final String msg;

                                if(response.isSuccessful()) {
                                    msg = "즐겨찾기 목록에서 성공적으로 제거되었습니다";

                                    beanSectionItemList.remove(pos);
                                    rva.notifyItemRemoved(pos);
                                    beanFavoriteShopList.remove(op);
                                    beanSectionHeaderList.get(headerOp).itemCount -= 1;
                                } else {
                                    msg = "즐겨찾기 목록 제거에 실패하였습니다\n" + response.errorBody().source().toString();
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
            builder.setMessage("즐겨찾기 목록에서 제거하시겠습니까?").setPositiveButton("예", dialogClickListener).setNegativeButton("아니오", dialogClickListener).show();

            return true;
        }

        @Override
        public int getItemCount() {
            return beanSectionItemList.size();
        }

        @Override
        public int getItemViewType(int pos) {
            final BeanSectionItem beanSectionItem = beanSectionItemList.get(pos);
            final int st = beanSectionItem.st;
            final int op = beanSectionItem.op;
            final int itemViewType;

            //대표 텍스트만 있을 경우 1, 대표 이미지만 있을 경우 2, 둘 다 있을 경우 3

            if(beanSectionItem.id == 0) {
                itemViewType = st;
            } /*else if(beanSectionItem.id == -1) {
                itemViewType = CN_IVT_EMPTY_ITEM;
            }*/ else {
                //if(st == CN_SECTION_NEW_EVENT || st == CN_SECTION_NEW_USEDCAR || st == CN_SECTION_NEW_POST) {
                if(st == CN_SECTION_NEW_EVENT || st == CN_SECTION_NEW_POST) {
                    final BeanPost beanPost = st == CN_SECTION_NEW_EVENT ? beanNewEventList.get(op) : beanNewPostList.get(op);
                    final int zz = st * 10;

                    if(beanPost.fcb.isEmpty() || beanPost.ffn.isEmpty()) {
                        itemViewType = zz + (beanPost.fcb.isEmpty() ? 2 : 1);
                    } else {
                        itemViewType = zz + 3;
                    }
                } else {
                    itemViewType = (st * 10) + 1;
                }
            }

            return itemViewType;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup vg, int itemViewType) {
            final RecyclerView.ViewHolder vh;

            switch (itemViewType) {
                case CN_IVT_FAVORITE_CATEGORY:
                    vh = new VhImage1Text1(LayoutInflater.from(getActivity()).inflate(R.layout.vh_category_favorite, vg, false));
                    break;
                case CN_IVT_MOU_GROUP:
                case CN_IVT_FAVORITE_SHOP:
                case CN_IVT_BEST_SHOP:
                    vh = new VhImage1Text1(LayoutInflater.from(getActivity()).inflate(R.layout.vh_shop_favorite, vg, false));
                    vh.itemView.setOnLongClickListener(this);
                    break;
                case CN_IVT_NEW_USEDCAR:
                    vh = new VhImage1Text3_stats(LayoutInflater.from(getActivity()).inflate(R.layout.vh_good_favorite, vg, false));
                    break;
                case CN_IVT_NEW_EVENT_TEXT:
                case CN_IVT_NEW_EVENT_MEDIA:
                case CN_IVT_NEW_EVENT_BOTH:
                case CN_IVT_NEW_POST_TEXT:
                case CN_IVT_NEW_POST_MEDIA:
                case CN_IVT_NEW_POST_BOTH:
                    final VhPostStatus vhPostStatus = new VhPostStatus(LayoutInflater.from(ctx).inflate(R.layout.vh_post, vg, false));

                    if(itemViewType == CN_IVT_NEW_EVENT_TEXT || itemViewType == CN_IVT_NEW_POST_TEXT) {
                        vhPostStatus.fl_image.setVisibility(View.GONE);
                    }

                    vhPostStatus.itemView.setOnClickListener(this);
                    vhPostStatus.tv_repl.setOnClickListener(this);

                    vh = vhPostStatus;

                    break;
                default:
                    //section header
                    vh = new VhImage1Text1(LayoutInflater.from(getActivity()).inflate(R.layout.vh_header_favorite, vg, false));
                    break;
            }

            vh.itemView.setOnClickListener(this);
            return vh;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder vh, int pos) {
            final BeanSectionItem beanSectionItem = beanSectionItemList.get(pos);
            final int st = beanSectionItem.st; //original section type
            final int op = beanSectionItem.op; //original position
            final int itemViewType = getItemViewType(pos);

            switch (itemViewType) {
                case CN_IVT_FAVORITE_CATEGORY:
                    final BeanName_Res beanFavoriteCategory = beanFavoriteCategoryList.get(op);
                    final VhImage1Text1 vhFc = (VhImage1Text1)vh;
                    vhFc.tv_31.setText(beanFavoriteCategory.nm);
                    vhFc.iv_31.setImageResource(beanFavoriteCategory.resId);

                    break;
                case CN_IVT_MOU_GROUP:
                    break;
                case CN_IVT_FAVORITE_SHOP:
                case CN_IVT_BEST_SHOP:
                    final BeanMemberAndShop beanMemberAndShop = getBeanMemberAndShop(st, op);
                    final VhImage1Text1 vhBs = (VhImage1Text1)vh;

                    final int owner_sr = Integer.parseInt(beanMemberAndShop.sr);
                    String ifn = beanMemberAndShop.ifn;

                    if(ifn.isEmpty()) {
                        ifn = owner_sr == 0 ? "ifn_m.jpg" : (owner_sr == 1 || owner_sr == 9 ? "ifn_c.jpg" : "ifn_s.jpg");
                    }
                    ifn = CommonUtil.getFileName(ifn).concat("_200x200.jpg");

                    vhBs.tv_31.setText(beanMemberAndShop.cn);
                    imageLoader.displayImage(MainCons.EnumContentPath.MEMBER_I.getPath().concat(ifn), vhBs.iv_31, options);

                    break;
                case CN_IVT_NEW_USEDCAR:
                    break;
                case CN_IVT_NEW_EVENT_TEXT:
                case CN_IVT_NEW_EVENT_MEDIA:
                case CN_IVT_NEW_EVENT_BOTH:
                case CN_IVT_NEW_POST_TEXT:
                case CN_IVT_NEW_POST_MEDIA:
                case CN_IVT_NEW_POST_BOTH:
                    final SpannableStringBuilder ssb = new SpannableStringBuilder();
                    SpannableString ss;
                    String s;
                    
                    final VhPostStatus vhPostStatus = (VhPostStatus)vh;
                    final BeanPost beanPost = st == CN_SECTION_NEW_EVENT ? beanNewEventList.get(op) : beanNewPostList.get(op);

                    s = beanPost.tt;
                    ss = new SpannableString(s);
                    ss.setSpan(new ForegroundColorSpan(CommonUtil.getColor(ctx, R.color.tc_blue_highligh)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ss.setSpan(new AbsoluteSizeSpan(15, true), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb.append(ss);

                    s = "\n" + beanPost.nn + "   " + beanPost.ud.substring(0, 16) + "   조회수 " + beanPost.rec;
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

                    if(itemViewType == CN_IVT_NEW_EVENT_TEXT || itemViewType == CN_IVT_NEW_EVENT_BOTH || itemViewType == CN_IVT_NEW_POST_TEXT || itemViewType == CN_IVT_NEW_POST_BOTH) {
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

                    if(itemViewType == CN_IVT_NEW_EVENT_MEDIA || itemViewType == CN_IVT_NEW_EVENT_BOTH || itemViewType == CN_IVT_NEW_POST_MEDIA || itemViewType == CN_IVT_NEW_POST_BOTH) {
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

                    break;
                default:
                    //section header
                    final VhImage1Text1 vhHeader = (VhImage1Text1)vh;
                    vhHeader.tv_31.setText(beanSectionHeaderList.get(op).nm);
            }
        }
    }
}