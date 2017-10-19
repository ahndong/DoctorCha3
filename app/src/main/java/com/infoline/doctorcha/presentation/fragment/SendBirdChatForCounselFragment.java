package com.infoline.doctorcha.presentation.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.CoreCons;
import com.infoline.doctorcha.core.httphelper.ServiceGenerator;
import com.infoline.doctorcha.core.listener.MediaScanFileHelper;
import com.infoline.doctorcha.core.listener.ViewOnClickListener;
import com.infoline.doctorcha.core.util.AnimUtil;
import com.infoline.doctorcha.core.util.CommonUtil;
import com.infoline.doctorcha.core.util.MediaUtil;
import com.infoline.doctorcha.core.util.PermissionUtil;
import com.infoline.doctorcha.core.view.RevealForegroundView;
import com.infoline.doctorcha.presentation.MainApp;
import com.infoline.doctorcha.presentation.MainCons;
import com.infoline.doctorcha.presentation.RetrofitInterface;
import com.infoline.doctorcha.presentation.activity.FragmentContainerActivity;
import com.infoline.doctorcha.presentation.activity.MapsActivity;
import com.infoline.doctorcha.presentation.activity.ShopActivity;
import com.infoline.doctorcha.presentation.adapter.SimpleItemAdapter;
import com.infoline.doctorcha.presentation.bean.BeanChatChannelData;
import com.infoline.doctorcha.presentation.bean.BeanChatMessage;
import com.infoline.doctorcha.presentation.bean.BeanChatMessageData;
import com.infoline.doctorcha.presentation.bean.BeanErrResponse;
import com.infoline.doctorcha.presentation.bean.BeanPost;
import com.infoline.doctorcha.presentation.bean.BeanSimpleItem;
import com.infoline.doctorcha.presentation.util.ChatUtil;
import com.infoline.doctorcha.presentation.viewholder.VhImage1;
import com.infoline.doctorcha.presentation.viewholder.VhImage1Text1;
import com.infoline.doctorcha.presentation.viewholder.VhImage1Text2;
import com.infoline.doctorcha.presentation.viewholder.VhImage1Text3;
import com.infoline.doctorcha.presentation.viewholder.VhImage2Text2;
import com.infoline.doctorcha.presentation.viewholder.VhText2;
import com.infoline.doctorcha.presentation.viewholder.VhText4;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.UserMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import static android.content.Context.MODE_PRIVATE;
import static com.infoline.doctorcha.core.CoreCons.EnumDateFormat.UNIQUE_FILENAME;
import static com.infoline.doctorcha.core.util.MediaUtil.getMimeType;
import static com.infoline.doctorcha.presentation.MainApp.beanMember;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_CK_DOCTORCHAR;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_CK_DOCTORCHAR_PARTNER;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_MT_COMMAND;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_MT_MAP;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_MT_TEXT;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_MT_IMAGE;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_MT_VIDEO;

import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_MT_VIEW;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_ST_ADMIN;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_COMMAND;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_CONFIRM_REQUEST;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_CONFIRM_RESPONSE;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_MAP_LEFT;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_MAP_RIGHT;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_TEXT;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_TEXT_LEFT;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_TEXT_RIGHT;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_IMAGE;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_IMAGE_LEFT;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_IMAGE_RIGHT;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.infoline.doctorcha.core.util.CommonUtil.writeLog;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_VIDEO;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_VIDEO_LEFT;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_VIDEO_RIGHT;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_VT_VIEW;
import static com.infoline.doctorcha.presentation.MainCons.CN_PATH_DOCTORCHA_DOWNLOAD;
import static com.infoline.doctorcha.presentation.MainCons.CN_REQUEST_ADDRESS_BY_GPS;
import static com.infoline.doctorcha.presentation.MainCons.CN_REQUEST_CARINFO;
import static com.infoline.doctorcha.presentation.MainCons.CN_REQUEST_CHANNELLIST;
import static com.infoline.doctorcha.presentation.MainCons.CN_REQUEST_COUNSELLIST_FOR_CHAT;
import static com.infoline.doctorcha.presentation.MainCons.CN_REQUEST_GALLARY;
import static com.infoline.doctorcha.presentation.MainCons.CN_REQUEST_POST_FOR_CHAT;
import static com.infoline.doctorcha.presentation.MainCons.CN_REQUEST_SHOPLIST_FOR_CHAT;

/**
 * Created by Administrator on 2016-08-26.
 */
public class SendBirdChatForCounselFragment extends Fragment {
    @BindView(R.id.rv) RecyclerView rv;
    @BindView(R.id.et) EditText et;
    @BindView(R.id.iv_upload) ImageView iv_upload;
    @BindView(R.id.tv_send)
    TextView tv_send;
    @BindView(R.id.tv_bm_select_channel)
    TextView tv_bm_select_channel;
    @BindView(R.id.tv_bm_invite_counsellor)
    TextView tv_bm_invite_counsellor;
    @BindView(R.id.tv_bm_invite_partener)
    TextView tv_bm_invite_partener;
    @BindView(R.id.ll_bottom_menu)
    LinearLayout ll_bottom_menu;
    @BindView(R.id.ll_message_popup)
    LinearLayout ll_message_popup;
    @BindView(R.id.iv_xx) ImageView iv_xx;
    @BindView(R.id.tv_xx) TextView tv_xx;
    @BindView(R.id.ll_help_popup)
    LinearLayout ll_help_popup;
    @BindView(R.id.fl_progress)
    FrameLayout fl_progress;
    @BindView(R.id.tv_progress)
    TextView tv_progress;

    private static final String identifier = SendBirdChatForCounselFragment.class.getSimpleName();

    private boolean isFileUploading;
    private GroupChannel groupChannel;
    final MediaUtil mediaUtil;
    private final int sr;
    private String channelUrl;
    private BeanChatChannelData beanChatChannelData;
    private boolean isShiftPressed = false;
    private File downLoadedFile;
    private boolean isLoading;

    //1. 회원이 선택한 질문 카테고리
    //2. id를 별도 관리하지 않고 position + 1을 코드로 활용한다.
    //3. BottomSheet의 질문 카테고리 순서를 변경하면 전체적으로 다 손보아야 됨 - 명심할 것질문 카테고리
    private int categoryId;
    private boolean isGate = false;

    private MyRecyclerAdapter rva;

    private Handler mHandler;
    private Runnable mRunnable;

    private boolean isShownKeyboard;
    private boolean mIsMessageListLoading;

    public SendBirdChatForCounselFragment() {
        mediaUtil = new MediaUtil();
        sr = Integer.parseInt(beanMember.sr);
    }

    public static SendBirdChatForCounselFragment newInstance() {
        return new SendBirdChatForCounselFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        isLoading = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sendbirdchatforcounsel, container, false);
        ButterKnife.bind(this, rootView);

        if(sr == 1 || sr == 2 || sr == 9) {
            ll_bottom_menu.setVisibility(View.VISIBLE);
            tv_send.setText("전송");
        }

        ll_help_popup.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                ll_help_popup.getViewTreeObserver().removeOnPreDrawListener(this);

                ll_help_popup.setTranslationY(-ll_help_popup.getHeight());
                return true;
            }
        });

        et.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(groupChannel == null) return;

                //로딩시에 왜 들어오지?
                if(ll_help_popup.getVisibility() == View.VISIBLE && s.length() != 0) {
                    AnimUtil.togglePopup(ll_help_popup, false);
                }

                final int len = s.length();

                if(sr == 0) {
                    //1. 카운셀러, 협력업체의 경우 bottomshhet기능이 아직 확정되지 않았으므로 메뉴 기능을 막는다
                    if (len == 1) {
                        tv_send.setText("전송");
                        groupChannel.startTyping();
                    } else if(len == 0) {
                        tv_send.setText("메뉴");
                        groupChannel.endTyping();
                    }
                }
            }
        });


        et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //isShiftPressed()는 눌름상태를 인식 못한다. 눌러는 순간만 인식한다
                boolean isKeyResumed = false;

                //if(Build.TAGS.equals("test-key")) {
                if(sr == 1 || sr == 9) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN)) {
                        if(event.isShiftPressed()) {
                            isShiftPressed = true;
                        } else {
                            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                                final String text = et.getText().toString();
                                final int len = et.getText().length();

                                if(isShiftPressed) {
                                    et.setText(text + "\n");
                                    et.setSelection(len + 1); //이해할 수 없지만 줄바꿈 문자가 있는 경우 +1을 해주어야 된다

                                } else {
                                    if(len > 0) {
                                        categoryId = -1;
                                        send();
                                    }
                                }

                                isKeyResumed = true;
                            }

                            isShiftPressed = false;
                        }
                    }
                }

                return isKeyResumed;
            }
        });

        final LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        lm.setReverseLayout(true);

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(ll_message_popup.getVisibility() == View.VISIBLE) {
                    togglePopup(false);
                }

                /*
                if(ll_help_popup.getVisibility() == View.VISIBLE) {
                    AnimUtil.togglePopup(ll_help_popup, false);
                }
                */

                //---------------------------------------------------------------------------


            }
        });

        et.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //1. lm.findLastCompletelyVisibleItemPosition() = -1
                //2. 카카오톡은 정확히 findLastCompletelyVisibleItemPosition() = -1일 경우에만 스크롤 된다
                isShownKeyboard = lm.findLastVisibleItemPosition() == rva.getItemCount() - 1;
                return false;
            }
        });

        rv.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(isShownKeyboard) {
                    if (bottom < oldBottom) {
                        //edittext keyboard shown
                        isShownKeyboard = false;

                        rv.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //rv.smoothScrollToPosition(rv.getAdapter().getItemCount() - 1); //많은 항목을 움직일 경우 더더더득 하면서 올라간다
                                rv.scrollToPosition(rv.getAdapter().getItemCount() - 1);
                            }
                        }, 500);
                    }
                }
            }
        });

        rva = new MyRecyclerAdapter(getActivity());

        rv.setLayoutManager(lm);
        rv.setAdapter(rva);

        addChannelHandler();

        final SharedPreferences sp = getActivity().getSharedPreferences(this.getClass().getSimpleName(), MODE_PRIVATE);
        channelUrl = sp.getString("channelUrl", null);

        if(channelUrl == null) {
            if(sr == 0) {
                //일반회원
                //beanPreferAuth.ccu = null;
                if (TextUtils.isEmpty(beanMember.ccu)) {
                    //1. async
                    //2. channel create후 initGroupChannel 호출
                    //      1) init channel
                    //      2) init rva
                    //      3) rv에 rva 부착
                    final List<String> idList = new ArrayList<>();
                    idList.add(String.valueOf(beanMember.id)); //자기자신의 id
                    idList.add(String.valueOf(1)); //닥터차(슈퍼바이저)
                    createGroupChannel(idList, CN_CHAT_CK_DOCTORCHAR);
                }
                else {
                    channelUrl = beanMember.ccu;
                    initGroupChannel();
                }
            } else {
                tv_bm_select_channel.performClick();
            }
        } else {
            initGroupChannel();
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_sendbirdchatforcounsel, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        selectChannelList();

        //더 이상의 하위 Fragment가 없으므로 return true, false, super.onOptionsItemSelected(item) 관계없이 모든 게 종료된다
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

    }

    @Override
    public void onResume() {
        //onActivityResult() 이후에 trigger된다
        super.onResume();

        if(isLoading) {
            isLoading = false;
            return;
        }

        initGroupChannel();

        /*
        if(sr == 0) {
            //일반회원일 경우만 안내 image가 popup되어 있다
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    AnimUtil.togglePopup(ll_help_popup, false);
                }
            };

            mHandler = new Handler();
            mHandler.postDelayed(mRunnable, 5000);
        }
        */
    }

    @Override
    public void onPause() {
        super.onPause();
        writeLog(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        writeLog(null);

        SendBird.removeChannelHandler(identifier);
    }

    private synchronized boolean isMessageListLoading() {
        return mIsMessageListLoading;
    }

    private synchronized void setMessageListLoading(boolean tf) {
        mIsMessageListLoading = tf;
    }

    private void selectChannelList() {
        final Intent intent = new Intent(getActivity(), FragmentContainerActivity.class);
        intent.putExtra(MainCons.EnumExtraName.NAME1.name(), SendBirdChannelListFragment.class.getSimpleName());
        intent.putExtra(MainCons.EnumExtraName.NAME2.name(), sr);
        intent.putExtra("channelUrl", channelUrl);
        startActivityForResult(intent, CN_REQUEST_CHANNELLIST);
        getActivity().overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
    }

    @OnClick({R.id.tv_bm_select_channel, R.id.tv_bm_invite_counsellor, R.id.tv_bm_invite_partener, R.id.ll_message_popup})
    protected void onClick_x1(final View v) {
        final int viewId = v.getId();

        if(ll_help_popup.getVisibility() == View.VISIBLE) {
            AnimUtil.togglePopup(ll_help_popup, false);
        }

        if(viewId == R.id.tv_bm_select_channel) {
            selectChannelList();
        } else if(viewId == R.id.ll_message_popup) {
            //신규 채팅 메세지 팝업
            togglePopup(false);
            channelUrl = (String)v.getTag();
            initGroupChannel();
        }  else {
            if(groupChannel == null) {
                Toast.makeText(getActivity(), "상담 채팅방을 선택하세요", Toast.LENGTH_LONG).show();
                selectChannelList();
                return;
            }

            final Intent intent = new Intent(getActivity(), FragmentContainerActivity.class);
            intent.putExtra(MainCons.EnumExtraName.NAME1.name(), SendBirdCounsellorListFragment.class.getSimpleName());
            intent.putExtra("sr", viewId == R.id.tv_bm_invite_counsellor ? 1 : 2);
            intent.putIntegerArrayListExtra(MainCons.EnumExtraName.ID_LIST.name(), (ArrayList<Integer>)ChatUtil.getChatMemberIdList(groupChannel));

            startActivityForResult(intent, CN_REQUEST_COUNSELLIST_FOR_CHAT);
            getActivity().overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
        }
    }

    @OnClick({R.id.iv_upload, R.id.tv_send})
    protected void imageViewClickListener(final View v) {
        if(ll_help_popup.getVisibility() == View.VISIBLE) {
            AnimUtil.togglePopup(ll_help_popup, false);
        }

        if(groupChannel == null) {
            Toast.makeText(getActivity(), "상담 채팅방을 선택하세요", Toast.LENGTH_LONG).show();
            selectChannelList();
            return;
        }

        final RevealForegroundView rfv = RevealForegroundView.createRevealForegroundView(getActivity(), v, Color.parseColor("#35000000"));

        rfv.setListener(new RevealForegroundView.AnimationEndListener() {
            public void AnimationEnd() {
                final int viewId = v.getId();

                if(v.getId() == R.id.tv_send) {
                    if(et.getText().length() > 0) {
                        //문자전송
                        categoryId = -1;
                        send();

                        return;
                    }

                    //1. 카운셀러, 협력업체에 대한 BottomSheet menu기능이 확정되지 않았으므로 취소
                    if(sr != 0) {
                        return;
                    }

                    final BottomSheetDialog bsd = new BottomSheetDialog(getActivity());
                    final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_selectchatcategory, null);
                    final RecyclerView rv2 = dialogView.findViewById(R.id.rv);
                    final List<BeanSimpleItem> beanSimpleItemList = new ArrayList<>();

                    /*
                    if(sr == 0) {
                        for(int i = 0; i < MainCons.EnumQueryCategory.values().length; i++) {
                            beanSimpleItemList.add(new BeanSimpleItem(MainCons.EnumQueryCategory.values()[i].getRes(), MainCons.EnumQueryCategory.values()[i].getNm()));
                        }
                    } else if(sr == 1) {
                        for(int i = 0; i < MainCons.EnumCommandCategory.values().length; i++) {
                            beanSimpleItemList.add(new BeanSimpleItem(MainCons.EnumCommandCategory.values()[i].getRes(), MainCons.EnumCommandCategory.values()[i].getNm()));
                        }
                    } else {

                    }
                    */

                    for(int i = 0; i < MainCons.EnumQueryCategory.values().length; i++) {
                        beanSimpleItemList.add(new BeanSimpleItem(MainCons.EnumQueryCategory.values()[i].getRes(), MainCons.EnumQueryCategory.values()[i].getNm()));
                    }

                    rv2.setHasFixedSize(true);
                    rv2.setLayoutManager(new GridLayoutManager(getActivity(), 3));

                    rv2.setAdapter(new SimpleItemAdapter(beanSimpleItemList, new SimpleItemAdapter.SimpleItemClickListener() {
                        @Override
                        public void onSimpleItemClick(int pos) {
                            categoryId = pos + 1;
                            send();
                            bsd.dismiss();
                        }
                    }));

                    //bsd.setTitle("질문할 카테고리 선택"); ==> 안먹는다
                    bsd.setContentView(dialogView);
                    bsd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            //bsd = null;
                        }
                    });

                    bsd.show();
                }
                else {
                    ////////////startActivityForResult(mediaUtil.getMediaChooser(), CN_REQUEST_PICK_FILE);

                    final String[] items = { "사진 갤러리", "사진 촬영", "동영상 갤러리", "동영상 촬영", "현재위치"};
                    final AlertDialog ad;
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int pos) {
                            if(pos == 4) {
                                if(PermissionUtil.requestGps(getActivity())) {
                                    final Intent intent = new Intent(getActivity(), MapsActivity.class);
                                    startActivityForResult(intent, CN_REQUEST_ADDRESS_BY_GPS);
                                }
                            } else {
                                final Intent intent = mediaUtil.getGallaryIntent(getActivity(), pos);

                                if(intent != null) {
                                    try {
                                        startActivityForResult(intent, CN_REQUEST_GALLARY);
                                    } catch (Exception e) {
                                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    /**
                                     * Set this as false to maintain SendBird connection,
                                     * even when an external Activity is started.
                                     */
                                    SendBird.setAutoBackgroundDetection(false);
                                }
                            }
                        }
                    });

                    ad = builder.create();
                    ad.show();
                }
            }
        });
    }

    private void togglePopup(boolean show) {
        if(show) {
            if(ll_message_popup.getVisibility() == View.GONE) {
                ll_message_popup.setTranslationY(-ll_message_popup.getHeight());
                ll_message_popup.setVisibility(View.VISIBLE);

                ll_message_popup.animate().translationY(0).setDuration(1000).setInterpolator(new BounceInterpolator()).setListener(new AnimatorListenerAdapter() {
                    //1. empty AnimatorListenerAdapter()를 설정하지 않을 경우 hideTrashcan()에서 설정된 AnimatorListenerAdapter()가 적용된다.
                    //2. setInterpolator등 다른 것도 마찬가지이다.
                }).start();
            }
        }
        else {
            ll_message_popup.animate().translationY(-ll_message_popup.getHeight()).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ll_message_popup.setVisibility(View.GONE);
                }
            }).start();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == CN_REQUEST_CHANNELLIST) {
            channelUrl = data.getStringExtra(MainCons.EnumExtraName.NAME1.name());
            //모든 경우의 메세지 refresh를 위해 onResume에서 통합 처리
            return;
        }

        if (requestCode == CN_REQUEST_SHOPLIST_FOR_CHAT) {
            List<String> idList = data.getStringArrayListExtra(MainCons.EnumExtraName.NAME1.name()); //선택된 파트너 리스트

            if(isGate) {
                //1. 상담사가 선택한 협력업체 + 상담사자신 + 상담챈널의 개설자
                //2. 위 1번 폐기할 것 - 모든 챈널은 슈퍼바이저가 상담사 또는 업체에 분배(초대) - 1인 초대 후 상담사는 추가 초대 가능해야 될 것 같다
                idList.add(0, String.valueOf(beanChatChannelData.channel_mi));
            }

            createGroupChannel(idList, CN_CHAT_CK_DOCTORCHAR_PARTNER);
            return;
        }

        if (requestCode == CN_REQUEST_COUNSELLIST_FOR_CHAT) {
            //1. 여기로 최초 진입하는 시기는 최고상담사가 담당 상담사를 초대하는 경우
            //2. 그 이후로는 최고상담사가 또 다른 상담사를 초대하거나 초대된 상담사가 또 다른 상담사를 초대하는 경우이다

            final String id = data.getStringExtra(MainCons.EnumExtraName.ID.name()); //선택된 닥터차 상담원 id
            final String nn = data.getStringExtra(MainCons.EnumExtraName.NN.name()); //선택된 닥터차 상담원 nn

            final List<String> idList = new ArrayList<>();
            idList.add(String.valueOf(id));

            groupChannel.inviteWithUserIds(idList, new GroupChannel.GroupChannelInviteHandler() {
                @Override
                public void onResult(SendBirdException e) {
                    if (e != null) {
                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    final String message;
                    final BeanChatMessageData beanChatMessageData = new BeanChatMessageData();

                    //message = String.format("%s님의 상담 채팅방에 %s님을 초대하였습니다", ChatUtil.getNickNameFromId(groupChannel, beanChatChannelData.channel_mi), nn);
                    message = String.format("%s님을 초대하였습니다", nn);
                    beanChatMessageData.chat_st = sr;
                    beanChatMessageData.chat_mt = CN_CHAT_MT_TEXT;
                    beanChatMessageData.chat_hg = "0"; //member(sr=0)에게는 메세지를 숨긴다

                    groupChannel.sendUserMessage(message, beanChatMessageData.toString(), new BaseChannel.SendUserMessageHandler() {
                        @Override
                        public void onSent(UserMessage userMessage, SendBirdException e) {
                            if (e != null) {
                                Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            rva.appendMessage(userMessage);
                            et.setText("");
                        }
                    });
                }
            });

            return;
        }

        if (requestCode == CN_REQUEST_POST_FOR_CHAT) {
            //1. 사고수리 상담을 위하여 사고수리 정보 입력 창에서 전송 버튼을 클릭

            BeanChatMessageData beanChatMessageData = new BeanChatMessageData();
            beanChatMessageData.chat_st = CN_CHAT_ST_ADMIN;
            beanChatMessageData.chat_mt = CN_CHAT_MT_VIEW;
            beanChatMessageData.chat_sv = data.getIntExtra(MainCons.EnumExtraName.ID.name(), 0) + ""; //0일 수는 없을 것이야
            beanChatMessageData.chat_ct = 2;

            //1. 아래 메세지는 GCM, 새 메세지 팝업 View에서 사용하며 chating창에서 실제 표시되는 메세지는 sr로 분기시켜 별도 buil
            //2. 질문자의 폰에서 이 메세지가 나올 일이 없다
            groupChannel.sendUserMessage(beanMember.nn + "님께서 사고수리에 필요한 정보를 전송하였습니다\"", beanChatMessageData.toString(), new BaseChannel.SendUserMessageHandler() {
                @Override
                public void onSent(UserMessage userMessage, SendBirdException e) {
                    if (e != null) {
                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    rva.appendMessage(userMessage);
                }
            });
            return;
        }

        if (requestCode == CN_REQUEST_CARINFO) {
            //1. 중고차량 판매를 위하여 차량정보 창에서 전송 버튼을 클릭

            BeanChatMessageData beanChatMessageData = new BeanChatMessageData();
            beanChatMessageData.chat_st = CN_CHAT_ST_ADMIN;
            beanChatMessageData.chat_mt = CN_CHAT_MT_VIEW;
            beanChatMessageData.chat_sv = data.getIntExtra(MainCons.EnumExtraName.ID.name(), 0) + ""; //0일 수는 없을 것이야
            beanChatMessageData.chat_ct = 5;

            //아래 메세지는 GCM, 새 메세지 팝업 View에서 사용하며 chating창에서 실제 표시되는 메세지는 sr로 분기시켜 별도 buil
            groupChannel.sendUserMessage(beanMember.nn + "님께서 소유차량 정보를 전송하였습니다", beanChatMessageData.toString(), new BaseChannel.SendUserMessageHandler() {
                @Override
                public void onSent(UserMessage userMessage, SendBirdException e) {
                    if (e != null) {
                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    rva.appendMessage(userMessage);
                }
            });
            return;
        }

        if (requestCode == CN_REQUEST_ADDRESS_BY_GPS) {
            final String addr = data.getStringExtra(MainCons.EnumExtraName.NAME1.name());
            final Location location = data.getParcelableExtra(MainCons.EnumExtraName.NAME2.name());

            if(TextUtils.isEmpty(addr) || location == null) {
                Toast.makeText(getActivity(),"현재위치가 지정되지 않았습니다", Toast.LENGTH_LONG).show();
            } else {
                sendMapMessage(addr, location);
            }

            return;
        }

        //-------------//-------------//-------------//-------------//-------------//-------------//-------------

        final List<Uri> tmpUriList = new ArrayList<>();

        if(data == null) {
            //1. camera 촬영
            /*
            Uri imageUri = mediaUtil.getLastCaptureImageUri(this);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            */

            MediaScanFileHelper.OnMediaScanListener onMediaScanListener = new MediaScanFileHelper.OnMediaScanListener() {
                public void onError() {
                    CommonUtil.writeLog("onError");
                }

                public void onSuccess(Uri uri) {
                    //CommonUtil.writeLog(Uri.decode(uri.toString()));
                    tmpUriList.add(uri);

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
                                    applySelectedImages(tmpUriList);
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
                final int selectedCount = cd.getItemCount() > 5 ? 5 : cd.getItemCount();

                for (int i = 0; i < selectedCount; i++) {
                    tmpUriList.add(cd.getItemAt(i).getUri());
                }

            } else {
                //1. picasa에 sync되어있는 경우 처리할 것
                //2. Intent { dat=content://com.google.android.gallery3d.provider/picasa/item/6322201232878530546 flg=0x1 }

                tmpUriList.add( data.getData());
            }

            applySelectedImages(tmpUriList);
        }
    }

    private void applySelectedImages(List<Uri> tmpUriList) {
        for(final Uri uri : tmpUriList) {
            //CommonUtil.writeLog(Uri.decode(uri.toString()));
            //content://media/external/video/media/21787 -- /storage/emulated/0/DCIM/Camera/20160619_194850.mp4
            //content://com.android.providers.media.documents/document/video%3A188

            //  content://media/external/video/media/21919
            //  /storage/emulated/0/DCIM/Camera/20160717_125558.mp4
            //final String realPath = mediaUtil.getRealPathFromUri(getActivity(), uri);

            final String realPath = MediaUtil.getRealPathFromUri(getActivity(), uri);

            if(realPath == null) {
                Toast.makeText(getActivity(), "파일경로를 확인할 수 없습니다\n" + Uri.decode(uri.toString()), Toast.LENGTH_LONG).show();
                continue;
            }

            final BeanChatMessageData beanChatMessageData = new BeanChatMessageData();


            /////

            File destFile = null;

            if(realPath.contains("mp4")) {
                destFile = new File(realPath);
            } else {
                try {
                    Bitmap bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);

                    final int originWidth = bm.getWidth();
                    final int originHeight = bm.getHeight();

                    final int maxHeight = 480;

                    if(originHeight > 640) {
                        bm = Bitmap.createScaledBitmap(bm, (originWidth * maxHeight) / originHeight, maxHeight, true);
                    }

                    final File originFile = new File(realPath);
                    final int lotation = MediaUtil.getCameraPhotoOrientation(originFile);

                    if(lotation != 0) {
                        final Matrix matrix = new Matrix();
                        matrix.postRotate(lotation);

                        bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                    }

                    destFile = File.createTempFile("temp", ".jpg");
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, new BufferedOutputStream(new FileOutputStream(destFile)));

                    boolean xxx = destFile.exists();
                    long yyy = destFile.length();
                    int zzz = 1;


                } catch(Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    continue;
                }
            }


            /////

            ////////////////////////////////////final File file = new File(realPath);
            final String name = destFile.getName();
            final int size = (int)destFile.length();

            final String mime = getMimeType(realPath);
            final MediaType mediaType = MediaType.parse(mime);
            final String mimeType = mediaType.type();
            final int chat_mt = mimeType.equals("video") ? CN_CHAT_MT_VIDEO : CN_CHAT_MT_IMAGE;

            final ProgressDialog pd = ProgressDialog.show(getActivity(), "", "파일 업로드 중입니다", true);

            //1. 차후 pdf, xls, doc등 모든 문서에 대응할 수 있도록 할 것
            if(chat_mt == CN_CHAT_MT_VIDEO) {
                final MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);

                final String videoFileName = beanMember.id + "_" + CommonUtil.getFormattedDate(UNIQUE_FILENAME) + name.substring(name.indexOf("."));
                final RequestBody requestBody = RequestBody.create(MediaType.parse(mime), destFile);

                builder.addFormDataPart("fileList", videoFileName, requestBody);
                builder.addFormDataPart("pathId", MainCons.EnumContentPath.CHAT_V.getId()+"");

                final RequestBody finalRequestBody = builder.build();

                final RetrofitInterface.UploadService service = ServiceGenerator.createService(RetrofitInterface.UploadService.class);
                final Call<Void> call = service.files(finalRequestBody);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.isSuccessful()) {
                            /*
                                //1. pc환경에서 앱을 실행할 경우는 어떻게 할 것인가?
                                //2. 시나리오가 어떻게 전개되는지 실데 확인해 볼 것
                                final String thumnbal_path = mediaUtil.getThumbnailPathFromUri(getActivity(), uri);

                                if(!TextUtils.isEmpty(thumnbal_path)) {
                                    final File thumbnail_file = new File(thumnbal_path);

                                    final String name = thumbnail_file.getName();
                                    long ttt = thumbnail_file.length();
                                    final int size = (int)thumbnail_file.length();

                                    //1. video/mp4
                                    final MimeTypeMap type = MimeTypeMap.getSingleton();
                                    //1. finename이 한글일 경우 mime = null이므로 encoding해야한다
                                    final String mime = type.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(Uri.encode(thumnbal_path)));

                                    sendFileMessage(thumbnail_file, name, mime, size, "3,3," + xxx, loadingDialog);
                                    return;
                                }
                                else {
                                    msg = "썸네일 이미지가 없어 파일 메세지를 전송하지 못하였습니다";
                                }
                                */

                            final Bitmap bm = MediaUtil.createThumbnailFromVideFile(realPath);
                            final File thumbnail_file = MediaUtil.saveBitmaptoJpeg(bm);

                            final String name = thumbnail_file.getName();
                            final int size = (int)thumbnail_file.length();

                            beanChatMessageData.chat_st = sr;
                            beanChatMessageData.chat_mt = CN_CHAT_MT_VIDEO;
                            beanChatMessageData.chat_vu = videoFileName;

                            sendFileMessage(thumbnail_file, name, mime, size, beanChatMessageData, pd);
                        } else {
                            final String msg = "동영상 전송이 실패하였습니다\n" + response.errorBody().source().toString();
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                        }

                        pd.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        pd.dismiss();
                        final String msg = ServiceGenerator.getExceptionMsgByCause(t);
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                    }
                });
            }
            else {
                beanChatMessageData.chat_st = sr;
                beanChatMessageData.chat_mt = CN_CHAT_MT_IMAGE;
                sendFileMessage(destFile, name, mime, size, beanChatMessageData, pd);
            }
        }
    }

    private void sendFileMessage(final File file, final String name, final String mime, final int size, final BeanChatMessageData beanChatMessageData, final ProgressDialog pd) {
        try {
            groupChannel.sendFileMessage(file, name, mime, size, beanChatMessageData.toString(), new BaseChannel.SendFileMessageHandler() {
                @Override
                public void onSent(FileMessage fileMessage, SendBirdException e) {
                    pd.dismiss();

                    if (e != null) {
                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(getActivity(), "파일업로드가 완료되었습니다", Toast.LENGTH_SHORT).show();
                    rva.appendMessage(fileMessage);
                }
            });
        } catch (Exception e) {
            pd.dismiss();
            String eee = e.getMessage();
        }

    }

    private void sendMapMessage(final String addr, final Location location) {
        final BeanChatMessageData beanChatMessageData = new BeanChatMessageData();

        try {
            isShiftPressed = false;

            beanChatMessageData.chat_st = sr;
            beanChatMessageData.chat_mt = CN_CHAT_MT_MAP;
            beanChatMessageData.chat_sv = location.getLatitude() + "," + location.getLongitude();

            groupChannel.sendUserMessage(addr, beanChatMessageData.toString(), new BaseChannel.SendUserMessageHandler() {
                @Override
                public void onSent(UserMessage userMessage, SendBirdException e) {
                    if (e != null) {
                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    rva.appendMessage(userMessage);
                    et.setText("");
                }
            });
        } catch (Exception e) {
            String eee = e.getMessage();
        }

    }

    private void createGroupChannel(List<String> idList, final int channel_ck) { //ck - channel kind
        //channel kind - 1.일반 그룹채팅 챈널 2.멤버 vs 닥터차 상담전용 3.멤버 vs 닥터차, 파트너 상담 챈널(닥터차가 연결시킨) 4.멤버 vs 파트너(멤버가 직접 생성한 챈널)
        ////////////////////final String name = beanMember.nn + "님 : " + (channel_ck == CN_CHAT_CK_DOCTORCHAR ? "닥터차" : (channel_ck == CN_CHAT_CK_PARTNER ? "파트너사" : "닥터차 & 파트너사"));
        final String name = beanMember.nn + "님 상담전용";

        //1. counsellor security role에 속하는 member id list
        //2. test완료 후 상담 대표 id 한개로 chcnnel을 open하도록 하자 - sendbird 자체에서 막았으므로 불가능하다
        //3. 로직이 안정화되면 무조건 IsDistinct = true로 변경할 것
        //4. data format(BeanChatChannelData) 확정하여 전달할 것

        final BeanChatChannelData beanChatChannelData = new BeanChatChannelData();
        beanChatChannelData.channel_ci = beanMember.id;
        beanChatChannelData.channel_mi = channel_ck == CN_CHAT_CK_DOCTORCHAR ? beanMember.id : beanChatChannelData.channel_mi; //--> 상담사 또는 파트너가 특정 파트너들을 대상으로 챈널을 생성하는 경우는? - 원천봉쇄할까
        beanChatChannelData.channel_ck = channel_ck;

        //TODO : app이 setup되면 isDistinct = true로 전달할 것
        GroupChannel.createChannelWithUserIds(idList, false, name, null, beanChatChannelData.toString(), new GroupChannel.GroupChannelCreateHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e != null) {
                    Toast.makeText(getActivity(), e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                channelUrl = groupChannel.getUrl();
                if(channel_ck == CN_CHAT_CK_DOCTORCHAR) {
                    beanMember.ccu = groupChannel.getUrl();

                    final RetrofitInterface.MemberService service = ServiceGenerator.createService(RetrofitInterface.MemberService.class);
                    final Call<Void> call = service.update_ccu(beanMember.id, beanMember.ccu);

                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if(!response.isSuccessful()) {
                                final BeanErrResponse beanErrResponse = new BeanErrResponse(response.errorBody().source().toString()); //{ec:%s,em:'%s',sv:'%s'}

                                //1. 잘못된 nn 또는 upw 이외의 원인으로 인한 오류 메세지
                                //2. 404 같은 경우에는 beanErrResponse 자체가 null이다
                                final String msg = beanErrResponse.em == null ? String.format("[%s]%s", response.code(), response.message()) : beanErrResponse.em;
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(getActivity(), ServiceGenerator.getExceptionMsgByCause(t), Toast.LENGTH_LONG).show();
                        }
                    });
                }

                initGroupChannel();
            }
        });
    }

    private void initGroupChannel() {
        if(channelUrl == null) return;

        final SharedPreferences.Editor editor = getActivity().getSharedPreferences(this.getClass().getSimpleName(), MODE_PRIVATE).edit();

        GroupChannel.getChannel(channelUrl, new GroupChannel.GroupChannelGetHandler() {
            @Override
            public void onResult(GroupChannel _groupChannel, SendBirdException e) {
                if (e != null) {
                    Toast.makeText(getActivity(), "상담채널을 열지 못하였습니다.\n" + e.getCode() + " : " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    channelUrl = null;
                    editor.remove("channelUrl").apply();

                    return;
                }

                groupChannel = _groupChannel;
                editor.putString("channelUrl", channelUrl).apply();

                groupChannel.markAsRead();
                beanChatChannelData = new BeanChatChannelData(groupChannel.getData());

                rva.loadLatestMessages(50, null);
            }
        });
    }

    private void addChannelHandler() {
        if(!isFileUploading) {
            SendBird.addChannelHandler(identifier, new SendBird.ChannelHandler() {
                @Override
                public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
                    if (baseChannel.getUrl().equals(channelUrl)) {
                        if (rva != null && groupChannel != null) {
                            groupChannel.markAsRead();
                            rva.appendMessage(baseMessage);
                        }
                    } else {
                        final BeanChatMessage beanChatMessage = new BeanChatMessage(baseMessage);

                        if(beanChatMessage.chat_hg.isEmpty() || !beanChatMessage.chat_hg.contains(String.valueOf(sr))) {
                            final SpannableStringBuilder ssb = new SpannableStringBuilder();
                            SpannableString ss;
                            String s = ChatUtil.getSenderName(beanChatMessage);
                            ss = new SpannableString(s);
                            //ss.setSpan(new AbsoluteSizeSpan(15, true), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            ss.setSpan(new ForegroundColorSpan(CommonUtil.getColor(getActivity(), android.R.color.holo_blue_dark)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            ssb.append(ss);

                            s = "\n" + ChatUtil.getDisplayMessage(beanChatMessage);
                            ss = new SpannableString(s);
                            //ss.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            ssb.append(ss);

                            final ImageLoader imageLoader = ImageLoader.getInstance();
                            imageLoader.displayImage(beanChatMessage.chat_pu, iv_xx, MainApp.optionsForCircleThumb);
                            tv_xx.setText(ssb);

                            ll_message_popup.setTag(baseChannel.getUrl());
                            togglePopup(true);
                        }
                    }
                }

                @Override
                public void onReadReceiptUpdated(GroupChannel groupChannel) {
                    if (groupChannel.getUrl().equals(channelUrl)) {
                        rva.notifyDataSetChanged();
                    }
                }

                @Override
                public void onTypingStatusUpdated(GroupChannel groupChannel) {
                    if (groupChannel.getUrl().equals(channelUrl)) {
                        //rva.notifyDataSetChanged();
                    }
                }

                @Override
                public void onUserJoined(GroupChannel groupChannel, User user) {
                    if (groupChannel.getUrl().equals(channelUrl)) {
                        //////////updateGroupChannelTitle();
                    }
                }

                @Override
                public void onUserLeft(GroupChannel groupChannel, User user) {
                    if (groupChannel.getUrl().equals(channelUrl)) {
                        ///////////updateGroupChannelTitle();
                    }
                }
            });
        }
    }

    private void send() {
        final String message;
        final BeanChatMessageData beanChatMessageData = new BeanChatMessageData(); //{"st:-1,"mt":-1,"ct":-1,"vu":""}

        if(categoryId == -1) {
            //1. 단문 텍스트 메세지 송신

            isShiftPressed = false;

            message = et.getText().toString();
            beanChatMessageData.chat_st = sr;
            beanChatMessageData.chat_mt = CN_CHAT_MT_TEXT;

            groupChannel.sendUserMessage(message, beanChatMessageData.toString(), new BaseChannel.SendUserMessageHandler() {
                @Override
                public void onSent(UserMessage userMessage, SendBirdException e) {
                    if (e != null) {
                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    rva.appendMessage(userMessage);
                    et.setText("");
                }
            });
        } else {
            beanChatMessageData.chat_st = CN_CHAT_ST_ADMIN;

            if(sr == 0) {
                //1. 질문 카테고리 송신 - image name을 pos+1이 아닌 pos로 할까?
                message = MainCons.EnumContentPath.RESOURCE_COUNSELHELP_I.getPath() + "/" + categoryId + ".png";
                beanChatMessageData.chat_mt = CN_CHAT_MT_IMAGE;
                beanChatMessageData.chat_hg = "1,9"; //상담사, 최고상담사는 국이 카테고리 소개 이미지를 볼 필요가 없다

                //1. 질문 카테고리별 안내이미지를 Local에 둘 경우 UIL이 drawable id를 image를 load하는데 문제가 있다.
                //1. sendFileMessage를 사용할 경우 image를 반복적으로 송신하게 되고 cache 사용의 효과를 못 본다
                groupChannel.sendUserMessage(message, beanChatMessageData.toString(), new BaseChannel.SendUserMessageHandler() {
                    @Override
                    public void onSent(UserMessage userMessage, SendBirdException e) {
                        if (e != null) {
                            Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        rva.appendMessage(userMessage);

                        if(categoryId == 2 || categoryId == 5) {
                            beanChatMessageData.chat_mt = CN_CHAT_MT_COMMAND;
                            beanChatMessageData.chat_ct = categoryId;
                            beanChatMessageData.chat_hg = "";

                            //1. gcm수신, 싱딤챈널리스트의 last message 표현을 위한 메세지
                            //2. 채팅창에서는 chat_ct, sr등으로 분기하여 메세지 별도 build함
                            String imsi = beanChatMessageData.chat_ct == 1 ? "사고수리 상담에 필요한 정보 요청 중입니다" : "중고차판매 상담에 필요한 정보 요청 중입니다";

                            groupChannel.sendUserMessage(imsi, beanChatMessageData.toString(), new BaseChannel.SendUserMessageHandler() {
                                @Override
                                public void onSent(UserMessage userMessage, SendBirdException e) {
                                    if (e != null) {
                                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    rva.appendMessage(userMessage);
                                }
                            });
                        }
                    }
                });
            } else {
                //의미없을 것 같아서 상담사가 자유롭게 신규상담사, 업체를 초대할 수 있도록 조정하였다
                /*
                beanChatMessageData.chat_mt = CN_CHAT_MT_CONFIRM_REQUEST;
                beanChatMessageData.chat_ct = 21 + categoryId; //command type : 21 + 1 = 22부터 시작

                String imsi = "전문 협력업체를 대화에 초대하고자 합니다\n새로운 상담챈널 개설을 허용하시겠습니까?";

                groupChannel.sendUserMessage(imsi, beanChatMessageData.toString(), new BaseChannel.SendUserMessageHandler() {
                    @Override
                    public void onSent(UserMessage userMessage, SendBirdException e) {
                        if (e != null) {
                            Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        rva.appendMessage(userMessage);
                    }
                });
                */
            }
        }
    }

    private class MyRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {
        private final ImageLoader imageLoader;
        private final DisplayImageOptions options_cache;
        private final Context ctx;
        private final List<BaseMessage> baseMessageList;

        public MyRecyclerAdapter(final Context ctx) {
            this.ctx = ctx;
            this.imageLoader = ImageLoader.getInstance();
            options_cache = new DisplayImageOptions.Builder()
                    .considerExifParams(true)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();

            this.baseMessageList = new ArrayList<>();
        }

        public void onClick(View v) {
            final int pos = CommonUtil.getAdapterPositionFromView(rv, v);
            final BeanChatMessage beanChatMessage = new BeanChatMessage(baseMessageList.get(pos));

            if(v.getId() == R.id.iv_31) {
                final String[] items = { beanChatMessage.chat_nn.concat("님 블로그로 가기"), beanChatMessage.chat_nn.concat("님에 대한 메모")};
                final AlertDialog ad;
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final int userId = Integer.valueOf(beanChatMessage.chat_uid);

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        if(pos == 0) {
                            //if(chat_vt == CN_CHAT_VT_TEXT_LEFT || chat_vt == CN_CHAT_VT_IMAGE_LEFT || chat_vt == CN_CHAT_VT_VIDEO_LEFT || chat_vt == CN_CHAT_VT_MAP_LEFT) {
                            //1. 썸네일 아이콘
                            //2. 내 블로그 홈으로 가기
                            final Intent intent1 = new Intent(getActivity(), ShopActivity.class);
                            final MainCons.EnumActivityAnimType enumActivityAnimType;

                            enumActivityAnimType = MainCons.EnumActivityAnimType.LEFTINRIGHTOUT;

                            intent1.putExtra(MainCons.EnumActivityAnimType.class.getSimpleName(), enumActivityAnimType);
                            intent1.putExtra("owner_id", userId);
                            //intent.putExtra(BeanMemberAndShop.class.getSimpleName(), null);
                            startActivity(intent1);
                            getActivity().overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
                            //}
                        } else {
                            //1. 썸네일 아이콘
                            //2. 메모장
                            final Intent intent = new Intent(getActivity(), FragmentContainerActivity.class);

                            intent.putExtra(MainCons.EnumExtraName.NAME1.name(), MemoListFragment.class.getSimpleName());
                            intent.putExtra("target_id", userId);

                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
                        }
                    }
                });

                ad = builder.create();
                ad.show();
                return;
            }

            final int chat_mt = beanChatMessage.chat_mt;

            if(chat_mt == CN_CHAT_MT_IMAGE || chat_mt == CN_CHAT_MT_VIDEO) {
                final Intent intent = new Intent(Intent.ACTION_VIEW);
                final Uri uri = Uri.parse(chat_mt == CN_CHAT_MT_VIDEO ? MainCons.EnumContentPath.CHAT_V.getPath() + beanChatMessage.chat_vu : beanChatMessage.chat_mb);
                intent.setDataAndType(uri, chat_mt == CN_CHAT_MT_VIDEO ? "video/*" : "image/*");
                startActivity(intent);
            } else if(chat_mt == CN_CHAT_MT_MAP) {
                final Intent intent = new Intent(getActivity(), FragmentContainerActivity.class);
                intent.putExtra(MainCons.EnumExtraName.NAME1.name(), MapViewFragment.class.getSimpleName());
                intent.putExtra(MainCons.EnumExtraName.NAME2.name(), beanChatMessage.chat_mb);
                intent.putExtra(MainCons.EnumExtraName.NAME3.name(), beanChatMessage.chat_sv);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);

            } else if(chat_mt == CN_CHAT_MT_COMMAND) {
                final int chat_ct = beanChatMessage.chat_ct; //chat command type

                if(sr == 0) {
                    if(chat_ct == 2) {
                        //사고수리(판금도색)
                        final Intent intent = new Intent(getActivity(), FragmentContainerActivity.class);
                        intent.putExtra(MainCons.EnumExtraName.NAME1.name(), PostEditOfChatForAccidentFragment.class.getSimpleName());
                        intent.putExtra(MainCons.EnumFragmentOpenType.class.getSimpleName(), MainCons.EnumFragmentOpenType.SEND_READY);
                        startActivityForResult(intent, CN_REQUEST_POST_FOR_CHAT);
                        getActivity().overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
                    } else if(chat_ct == 5) {
                        //중고차 팔기
                        final Intent intent = new Intent(getActivity(), FragmentContainerActivity.class);
                        intent.putExtra(MainCons.EnumExtraName.NAME1.name(), CarInfoFragment.class.getSimpleName());
                        intent.putExtra(MainCons.EnumFragmentOpenType.class.getSimpleName(), MainCons.EnumFragmentOpenType.SEND_READY);
                        intent.putExtra(MainCons.EnumExtraName.ID.name(), beanMember.id);
                        startActivityForResult(intent, CN_REQUEST_CARINFO);
                        getActivity().overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
                    }
                }
            } else if (chat_mt == CN_CHAT_MT_VIEW) {
                if(beanChatMessage.chat_ct == 2) {
                    final RetrofitInterface.PostService service = ServiceGenerator.createService(RetrofitInterface.PostService.class);
                    final Call<List<BeanPost>> call = service.select(Integer.valueOf(beanChatMessage.chat_sv));
                    call.enqueue(new Callback<List<BeanPost>>() {
                        @Override
                        public void onResponse(Call<List<BeanPost>> call, Response<List<BeanPost>> response) {
                            if(response.isSuccessful()) {
                                if(response.body().size() == 0) {
                                    Toast.makeText(getActivity(), "사고수리 의뢰 내역이 삭제되었습니다", Toast.LENGTH_LONG).show();
                                } else {
                                    final Intent intent = new Intent(getActivity(), FragmentContainerActivity.class);
                                    intent.putExtra(MainCons.EnumExtraName.NAME1.name(), PostReadFragment.class.getSimpleName());
                                    intent.putExtra("beanPost", response.body().get(0));
                                    startActivity(intent);
                                    getActivity().overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
                                }
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
                } else if(beanChatMessage.chat_ct == 5) {
                    final Intent intent = new Intent(getActivity(), FragmentContainerActivity.class);
                    intent.putExtra(MainCons.EnumExtraName.NAME1.name(), CarInfoFragment.class.getSimpleName());
                    intent.putExtra(MainCons.EnumExtraName.ID.name(), Integer.parseInt(beanChatMessage.chat_sv));
                    intent.putExtra(MainCons.EnumFragmentOpenType.class.getSimpleName(), MainCons.EnumFragmentOpenType.READ_ONLY);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
                }
            }
        }

        public boolean onLongClick(final View v) {
            /* 일단 clipboar 넣기까지는 성공
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
            final BeanChatMessage beanChatMessage = new BeanChatMessage(rva.getItem(pos));
            final int chat_mt = beanChatMessage.chat_mt;

            final boolean isFile = chat_mt == CN_CHAT_MT_IMAGE || chat_mt == CN_CHAT_MT_VIDEO;

            //TODO : 전달 기능 고민할 것
            //final String[] itemsText = { "복사", "공유", "전달"};
            //final String[] itemsFile = { "다운로드", "공유", "전달"};
            String[] itemsText; // = { "복사", "공유", "삭제"};
            String[] itemsFile; // = { "다운로드", "공유", "삭제"};

            if(sr == 1 || sr == 9 || beanMember.id == 2) {
                itemsText = new String[3]; itemsText[0] = "복사"; itemsText[1] = "공유"; itemsText[2] = "삭제";
                itemsFile = new String[3]; itemsFile[0] = "다운로드"; itemsFile[1] = "공유"; itemsFile[2] = "삭제";
            } else {
                itemsText = new String[2]; itemsText[0] = "복사"; itemsText[1] = "공유";
                itemsFile = new String[2]; itemsFile[0] = "다운로드"; itemsFile[1] = "공유";
            }

            final AlertDialog ad;
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setItems(isFile ? itemsFile : itemsText, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            if(isFile) {
                                downloadFile(beanChatMessage, 1);
                            } else {
                                final ClipboardManager ClipMan = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                                final ClipData clipData = ClipData.newPlainText("DoctorCha", beanChatMessage.chat_mb);
                                ClipMan.setPrimaryClip(clipData);

                                //복사완료 메세지가 자동으로 안나오네. 위 이미지 복사 sample에서는 '클립보드에 복사되었습니다' 나온다. 일단 수동으로
                                Toast.makeText(getActivity(), "클립보드에 복사 되었습니다", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 1:
                            if(isFile) {
                                downloadFile(beanChatMessage, 2);
                            } else {
                                final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                                intent.setType("text/plain");
                                //intent.putExtra(Intent.EXTRA_SUBJECT, "제목);
                                intent.putExtra(Intent.EXTRA_TEXT, beanChatMessage.chat_mb);
                                //
                                Intent chooser = Intent.createChooser(intent, null);
                                startActivity(chooser);
                            }
                            break;
                        case 2:
                            groupChannel.deleteMessage(rva.getItem(pos), new BaseChannel.DeleteMessageHandler() {
                                @Override
                                public void onResult(SendBirdException e) {
                                    if (e == null) {
                                        baseMessageList.remove(pos);
                                        notifyItemRemoved(pos);
                                    }
                                }
                            });
                    }
                }
            });

            ad = builder.create();
            ad.show();

            return false;
        }

        private void downloadFile(final BeanChatMessage beanChatMessage, final int downloadType) {
            if(!PermissionUtil.requestWriteStoragePermissions(getActivity())) {
                return;
            }

            fl_progress.setVisibility(View.VISIBLE);

            final int chat_mt = beanChatMessage.chat_mt;

            final String serverFilePath = chat_mt == MainCons.CN_CHAT_MT_VIDEO ? MainCons.EnumContentPath.CHAT_V.getPath() + beanChatMessage.chat_vu : beanChatMessage.chat_mb;
            final RetrofitInterface.DownloadService service = chat_mt == MainCons.CN_CHAT_MT_VIDEO ? ServiceGenerator.createService(RetrofitInterface.DownloadService.class) : ServiceGenerator.createServiceaForAmazon(RetrofitInterface.DownloadService.class);
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
                                    final Intent intent = new Intent(Intent.ACTION_SEND);
                                    intent.setType(chat_mt == MainCons.CN_CHAT_MT_VIDEO ? "video/*" : "image/*");
                                    intent.putExtra(Intent.EXTRA_STREAM, chat_mt == MainCons.CN_CHAT_MT_VIDEO ? MediaUtil.getContentUriFromVideoFile(getActivity(), downLoadedFile) : MediaUtil.getContentUriFromImageFile(getActivity(), downLoadedFile));
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
                    //download
                    file = new File(CN_PATH_DOCTORCHA_DOWNLOAD, saveFileName + "." + extName);
                } else {
                    //sharing
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
                    //createdTempFileUri = Uri.fromFile(file);

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

        private void loadLatestMessages(int limit, final BaseChannel.GetMessagesHandler handler) {
            if(isMessageListLoading()) {
                return;
            }

            setMessageListLoading(true);
            groupChannel.getPreviousMessagesByTimestamp(Long.MAX_VALUE, true, limit, true, BaseChannel.MessageTypeFilter.ALL, null, new BaseChannel.GetMessagesHandler() {
                @Override
                public void onResult(List<BaseMessage> list, SendBirdException e) {
                    if(handler != null) {
                        handler.onResult(list, e);
                    }

                    setMessageListLoading(false);
                    if(e != null) {
                        e.printStackTrace();
                        return;
                    }

                    if(list.size() == 0) {
                        //첫가입시 환영 메세지

                        ll_help_popup.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AnimUtil.togglePopup(ll_help_popup, true);
                            }
                        }, 50);

                        final String message;
                        final BeanChatMessageData beanChatMessageData = new BeanChatMessageData(); //{"st:-1,"mt":-1,"ct":-1,"vu":""}

                        message = beanMember.nn + "님 닥터차 회원가입을 환영합니다.\n메뉴 버튼을 터치하시면 상담 카테고리를 지정할 수 있습니다\n업체 사징님이신 경우 사업자등록증 사진을 전송하시면\n기업회원으로 변경해 드립니다";
                        beanChatMessageData.chat_st = CN_CHAT_ST_ADMIN;
                        beanChatMessageData.chat_mt = CN_CHAT_MT_TEXT;

                        groupChannel.sendUserMessage(message, beanChatMessageData.toString(), new BaseChannel.SendUserMessageHandler() {
                            @Override
                            public void onSent(UserMessage userMessage, SendBirdException e) {
                                if (e != null) {
                                    Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                rva.appendMessage(userMessage);
                                et.setText("");
                            }
                        });

                        return;
                    } else {
                        ll_help_popup.setVisibility(View.GONE);
                    }

                    /*
                    for (BaseMessage message : baseMessageList) {
                        if (isTempMessage(message) || isFailedMessage(message)) {
                            list.add(0, message);
                        }
                    }
                    */

                    baseMessageList.clear();

                    appendMessageList(list);
                }
            });
        }

        private void appendMessage(BaseMessage baseMessage) {
            final BeanChatMessage beanChatMessage = new BeanChatMessage(baseMessage);

            if(beanChatMessage.chat_hg.isEmpty() || !beanChatMessage.chat_hg.contains(String.valueOf(sr))) {
                baseMessageList.add(0, baseMessage);
                //notifyItemInserted(0);
                notifyDataSetChanged();
                rv.scrollToPosition(0);
            }
        }

        //임시
        private void appendMessageList(List<BaseMessage> list) {
            for(BaseMessage baseMessage : list) {
                final BeanChatMessage beanChatMessage = new BeanChatMessage(baseMessage);

                if(beanChatMessage.chat_hg.isEmpty() || !beanChatMessage.chat_hg.contains(String.valueOf(sr))) {
                    baseMessageList.add(baseMessage);
                }
            }

            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return baseMessageList.size();
        }

        public BaseMessage getItem(int pos) {
            //ViewHolder로 상속된다.
            //return posts.get(pos).getId();
            return baseMessageList.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            //ViewHolder로 상속된다.
            //return posts.get(pos).getId();
            return pos;
        }

        @Override
        public int getItemViewType(int pos) {
            final BeanChatMessage beanChatMessage = new BeanChatMessage(baseMessageList.get(pos));
            return BeanChatMessage.getChatMessageViewType(beanChatMessage.chat_st, beanChatMessage.chat_mt, beanChatMessage.chat_uid);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup vg, int itemViewType) {
            final RecyclerView.ViewHolder vh;

            switch (itemViewType) {
                case CN_CHAT_VT_TEXT:
                    //1. 나중에 입장, 퇴장 날릴때도 필요
                    vh = new VhText2(LayoutInflater.from(ctx).inflate(R.layout.vh_chat_text, vg, false));
                    break;
                case CN_CHAT_VT_TEXT_LEFT:
                    VhImage1Text3 vhImage1Text3 = new VhImage1Text3(LayoutInflater.from(ctx).inflate(R.layout.vh_chat_text_left, vg, false));
                    vhImage1Text3.iv_31.setOnClickListener(this); //해당 Blog로 가기
                    vh = vhImage1Text3;
                    break;
                case CN_CHAT_VT_TEXT_RIGHT:
                    vh = new VhText2(LayoutInflater.from(ctx).inflate(R.layout.vh_chat_text_right, vg, false));
                    break;
                case CN_CHAT_VT_MAP_LEFT:
                    VhImage1Text3 vhImage1Text3_2 = new VhImage1Text3(LayoutInflater.from(ctx).inflate(R.layout.vh_chat_map_left, vg, false));
                    vhImage1Text3_2.iv_31.setOnClickListener(this); //해당 Blog로 가기
                    vh = vhImage1Text3_2;
                    break;
                case CN_CHAT_VT_MAP_RIGHT:
                    vh = new VhText2(LayoutInflater.from(ctx).inflate(R.layout.vh_chat_map_right, vg, false));
                    break;
                case CN_CHAT_VT_IMAGE:
                    vh = new VhImage1Text1(LayoutInflater.from(ctx).inflate(R.layout.vh_chat_image, vg, false));
                    break;
                case CN_CHAT_VT_IMAGE_LEFT:
                    VhImage2Text2 vhImage2Text2 = new VhImage2Text2(LayoutInflater.from(ctx).inflate(R.layout.vh_chat_image_left, vg, false));
                    vhImage2Text2.iv_31.setOnClickListener(this); //해당 Blog로 가기
                    vh = vhImage2Text2;
                    break;
                case CN_CHAT_VT_IMAGE_RIGHT:
                    vh = new VhImage1Text1(LayoutInflater.from(ctx).inflate(R.layout.vh_chat_image_right, vg, false));
                    break;
                /*
                case CN_CHAT_VT_VIDEO:
                    //사용할 일 없을 것 같다
                    vh = new VhImage1(LayoutInflater.from(ctx).inflate(R.layout.vh_chat_image, vg, false));
                    break;
                */
                case CN_CHAT_VT_VIDEO_LEFT:
                    final VhImage2Text2 vhImage2Text2_2 = new VhImage2Text2(LayoutInflater.from(ctx).inflate(R.layout.vh_chat_video_left, vg, false));
                    vhImage2Text2_2.iv_31.setOnClickListener(this); //해당 Blog로 가기
                    vh = vhImage2Text2_2;
                    break;
                case CN_CHAT_VT_VIDEO_RIGHT:
                    vh = new VhImage1Text1(LayoutInflater.from(ctx).inflate(R.layout.vh_chat_video_right, vg, false));
                    break;
                case CN_CHAT_VT_COMMAND:
                    //1. 수리내역, 차량정보 작성버튼
                    //2. 굳이 명령 icon과 textview의 click event를 분리할 필요가 없다
                    final VhImage1Text2 vhImage1Text2 = new VhImage1Text2(LayoutInflater.from(ctx).inflate(R.layout.vh_chat_command, vg, false));
                    if(sr == 0) {
                        //일반회원 화면
                        vhImage1Text2.iv_31.setVisibility(View.VISIBLE);
                    } else {
                        //닥터차, 상담사, 협력업체 화면
                        vhImage1Text2.iv_31.setVisibility(View.GONE);
                    }

                    vh = vhImage1Text2;
                    break;
                case CN_CHAT_VT_VIEW:
                default:
                    //1. 전송된 차량정보 및 수리의뢰내역 보기
                    //2.  굳이 명령 icon과 textview의 click event를 분리할 필요가 없다
                    vh = new VhImage1Text2(LayoutInflater.from(ctx).inflate(R.layout.vh_chat_view, vg, false));
                    break;
                /*
                case CN_CHAT_VT_CONFIRM_REQUEST:
                    //폐기예정
                    //1. 원래는 협력업체 전문가 초대에 대한 승인요청을 위하여 사용하였으나
                    //   현재는 승인없이 바로 협력업체를 대화에 초대하는 것으로
                    final VhText4 vhText4 = new VhText4(LayoutInflater.from(ctx).inflate(R.layout.vh_chat_confirm_request, vg, false));
                    if(sr == 0) {
                        vhText4.tv_33.setOnTouchListener(viewOnClickListener);
                        vhText4.tv_34.setOnTouchListener(viewOnClickListener);
                    } else {
                        vhText4.tv_33.setVisibility(View.GONE);
                        vhText4.tv_34.setVisibility(View.GONE);
                    }
                    vh = vhText4;
                    break;
                case CN_CHAT_VT_CONFIRM_RESPONSE:
                    //폐기예정
                    final VhImage1Text2 vhImage1Text2_3 = new VhImage1Text2(LayoutInflater.from(ctx).inflate(R.layout.vh_chat_confirm_response, vg, false));
                    if(sr == 0) {
                        vhImage1Text2_3.iv_31.setVisibility(View.GONE);
                    } else {
                        vhImage1Text2_3.iv_31.setOnTouchListener(viewOnClickListener);
                    }
                    vh = vhImage1Text2_3;
                    break;
                */
            }

            vh.itemView.setOnClickListener(this); //사진확대, 영상보기
            vh.itemView.setOnLongClickListener(this); //복사, 공유, 삭제

            return vh;
        }

        private String unreadMembers(List<User> userList) {
            String xx = "";

            for(User user : userList) {
                if(xx.equals("")) {
                    xx = user.getNickname();
                } else {
                    xx += ", " + user.getNickname();
                }
            }

            return xx;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder vh, int pos) {
            final BaseMessage baseMessage = baseMessageList.get(pos);
            final BeanChatMessage beanChatMessage = new BeanChatMessage(baseMessage);


            final SpannableStringBuilder ssb = new SpannableStringBuilder();
            SpannableString ss;
            String s;
            //int unreadCount = groupChannel.getReadReceipt(baseMessageList.get(pos));
            final String unreadString = unreadMembers(groupChannel.getUnreadMembers(baseMessage));

            //1. 일반 member, 파트너 상담창에는 상담사가 초대한 업체회원의 profile도 닥터차로 나오도록 한다

            final int viewType = getItemViewType(pos);
            switch (viewType) {
                case CN_CHAT_VT_TEXT:
                    final VhText2 vhText2 = (VhText2)vh;

                    s ="닥터차 AI";
                    ss = new SpannableString(s);
                    //ss.setSpan(new AbsoluteSizeSpan(15, true), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ss.setSpan(new ForegroundColorSpan(CommonUtil.getColor(ctx, android.R.color.holo_blue_dark)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb.append(ss);

                    s = "  " + beanChatMessage.chat_ca;
                    ss = new SpannableString(s);
                    //ss.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb.append(ss);

                    vhText2.tv_31.setText(ssb);
                    vhText2.tv_32.setText(beanChatMessage.chat_mb);

                    break;
                case CN_CHAT_VT_TEXT_LEFT:
                    //상대방 MESSAGE
                    final VhImage1Text3 vhImage1Text3 = (VhImage1Text3)vh;
                    imageLoader.displayImage(MainCons.EnumContentPath.MEMBER_I.getPath() + beanChatMessage.chat_itn, vhImage1Text3.iv_31, MainApp.optionsForCircleThumb);
                    vhImage1Text3.tv_31.setText(beanChatMessage.chat_nn);
                    vhImage1Text3.tv_32.setText(beanChatMessage.chat_mb);

                    if(sr != 0 && unreadString.length() > 0) {
                        s = "  " + unreadString + "\n";
                        ss = new SpannableString(s);
                        //ss.setSpan(new AbsoluteSizeSpan(15, true), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ss.setSpan(new ForegroundColorSpan(CommonUtil.getColor(ctx, android.R.color.holo_red_dark)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ssb.append(ss);
                    }

                    s = "  " + beanChatMessage.chat_ca;
                    ss = new SpannableString(s);
                    //ss.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb.append(ss);

                    vhImage1Text3.tv_33.setText(ssb);
                    break;
                case CN_CHAT_VT_TEXT_RIGHT:
                    final VhText2 vhText2_2 = (VhText2)vh;
                    vhText2_2.tv_32.setText(beanChatMessage.chat_mb);

                    if(sr != 0 && unreadString.length() > 0) {
                        s = "  " + unreadString + "\n";
                        ss = new SpannableString(s);
                        //ss.setSpan(new AbsoluteSizeSpan(15, true), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ss.setSpan(new ForegroundColorSpan(CommonUtil.getColor(ctx, android.R.color.holo_red_dark)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ssb.append(ss);
                    }

                    s = "  " + beanChatMessage.chat_ca;
                    ss = new SpannableString(s);
                    //ss.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb.append(ss);

                    vhText2_2.tv_31.setText(ssb);
                    break;
                case CN_CHAT_VT_MAP_LEFT:
                    //상대방 MESSAGE
                    final VhImage1Text3 vhImage1Text3_6 = (VhImage1Text3)vh;

                    imageLoader.displayImage(MainCons.EnumContentPath.MEMBER_I.getPath() + beanChatMessage.chat_itn, vhImage1Text3_6.iv_31, MainApp.optionsForCircleThumb);
                    vhImage1Text3_6.tv_31.setText(beanChatMessage.chat_nn);

                    s = beanChatMessage.chat_mb;
                    ss = new SpannableString(s);
                    ss.setSpan(new UnderlineSpan(), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    vhImage1Text3_6.tv_32.setText(ss);

                    if(sr != 0 && unreadString.length() > 0) {
                        s = "  " + unreadString + "\n";
                        ss = new SpannableString(s);
                        //ss.setSpan(new AbsoluteSizeSpan(15, true), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ss.setSpan(new ForegroundColorSpan(CommonUtil.getColor(ctx, android.R.color.holo_red_dark)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ssb.append(ss);
                    }

                    s = "  " + beanChatMessage.chat_ca;
                    ss = new SpannableString(s);
                    //ss.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb.append(ss);

                    vhImage1Text3_6.tv_33.setText(ssb);
                    break;
                case CN_CHAT_VT_MAP_RIGHT:
                    final VhText2 vhText2_6 = (VhText2)vh;

                    s = beanChatMessage.chat_mb;
                    ss = new SpannableString(s);
                    ss.setSpan(new UnderlineSpan(), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    vhText2_6.tv_32.setText(ss);

                    if(sr != 0 && unreadString.length() > 0) {
                        s = "  " + unreadString + "\n";
                        ss = new SpannableString(s);
                        //ss.setSpan(new AbsoluteSizeSpan(15, true), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ss.setSpan(new ForegroundColorSpan(CommonUtil.getColor(ctx, android.R.color.holo_red_dark)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ssb.append(ss);
                    }

                    s = "  " + beanChatMessage.chat_ca;
                    ss = new SpannableString(s);
                    //ss.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb.append(ss);

                    vhText2_6.tv_31.setText(ssb);
                    break;
                case CN_CHAT_VT_IMAGE:
                    if(beanChatMessage.chat_mt == CN_CHAT_MT_IMAGE) {
                        final VhImage1Text1 vhImage1Text1 = (VhImage1Text1) vh;

                        imageLoader.displayImage(beanChatMessage.chat_mb, vhImage1Text1.iv_31, options_cache);

                        s ="닥터차 AI";
                        ss = new SpannableString(s);
                        //ss.setSpan(new AbsoluteSizeSpan(15, true), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ss.setSpan(new ForegroundColorSpan(CommonUtil.getColor(ctx, android.R.color.holo_blue_dark)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ssb.append(ss);

                        s = "  " + beanChatMessage.chat_ca;
                        ss = new SpannableString(s);
                        //ss.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ssb.append(ss);

                        vhImage1Text1.tv_31.setText(ssb);
                    }
                    else {
                        //admin으로 video file을 전송할 경우는 없을 것 같다
                    }
                    break;
                case  CN_CHAT_VT_IMAGE_LEFT:
                    //1) ImageLoader설명에는 꼭 필요한 경우를 제외하고는 drawable://" + R.drawable.img대신에 mageView.setImageResource(...)를 사용하기를 권고하고 있다
                    final VhImage2Text2 vhImage2Text2 = (VhImage2Text2)vh;
                    imageLoader.displayImage(MainCons.EnumContentPath.MEMBER_I.getPath() + beanChatMessage.chat_itn, vhImage2Text2.iv_31, MainApp.optionsForCircleThumb);

                    vhImage2Text2.tv_31.setText(beanChatMessage.chat_nn);
                    //imageLoader.displayImage(beanChatMessage.chat_mb, vhImage2Text2.iv_32, new ImageSize(90, 60));
                    imageLoader.displayImage(beanChatMessage.chat_mb, vhImage2Text2.iv_32);

                    if(sr != 0 && unreadString.length() > 0) {
                        s = "  " + unreadString + "\n";
                        ss = new SpannableString(s);
                        //ss.setSpan(new AbsoluteSizeSpan(15, true), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ss.setSpan(new ForegroundColorSpan(CommonUtil.getColor(ctx, android.R.color.holo_red_dark)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ssb.append(ss);
                    }

                    s = "  " + beanChatMessage.chat_ca;
                    ss = new SpannableString(s);
                    //ss.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb.append(ss);

                    vhImage2Text2.tv_32.setText(ssb);
                    break;
                case  CN_CHAT_VT_IMAGE_RIGHT:
                    final VhImage1Text1 vhImage1Text1 = (VhImage1Text1)vh;
                    imageLoader.displayImage(beanChatMessage.chat_mb, vhImage1Text1.iv_31, options_cache);

                    if(sr != 0 && unreadString.length() > 0) {
                        s = "  " + unreadString + "\n";
                        ss = new SpannableString(s);
                        //ss.setSpan(new AbsoluteSizeSpan(15, true), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ss.setSpan(new ForegroundColorSpan(CommonUtil.getColor(ctx, android.R.color.holo_red_dark)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ssb.append(ss);
                    }

                    s = "  " + beanChatMessage.chat_ca;
                    ss = new SpannableString(s);
                    //ss.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb.append(ss);

                    vhImage1Text1.tv_31.setText(ssb);
                    break;
                case  CN_CHAT_VT_VIDEO_LEFT:
                    final VhImage2Text2 vhImage2Text2_2 = (VhImage2Text2)vh;
                    imageLoader.displayImage(MainCons.EnumContentPath.MEMBER_I.getPath() + beanChatMessage.chat_itn, vhImage2Text2_2.iv_31, MainApp.optionsForCircleThumb);

                    vhImage2Text2_2.tv_31.setText(beanChatMessage.chat_nn);
                    imageLoader.displayImage(beanChatMessage.chat_mb, vhImage2Text2_2.iv_32, options_cache);

                    if(sr != 0 && unreadString.length() > 0) {
                        s = "  " + unreadString + "\n";
                        ss = new SpannableString(s);
                        //ss.setSpan(new AbsoluteSizeSpan(15, true), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ss.setSpan(new ForegroundColorSpan(CommonUtil.getColor(ctx, android.R.color.holo_red_dark)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ssb.append(ss);
                    }

                    s = "  " + beanChatMessage.chat_ca;
                    ss = new SpannableString(s);
                    //ss.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb.append(ss);

                    vhImage2Text2_2.tv_32.setText(ssb);
                    break;
                case  CN_CHAT_VT_VIDEO_RIGHT:
                    final VhImage1Text1 vhImage1Text1_2 = (VhImage1Text1)vh;
                    imageLoader.displayImage(beanChatMessage.chat_mb, vhImage1Text1_2.iv_31, options_cache);

                    if(sr != 0 && unreadString.length() > 0) {
                        s = "  " + unreadString + "\n";
                        ss = new SpannableString(s);
                        //ss.setSpan(new AbsoluteSizeSpan(15, true), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ss.setSpan(new ForegroundColorSpan(CommonUtil.getColor(ctx, android.R.color.holo_red_dark)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ssb.append(ss);
                    }

                    s = "  " + beanChatMessage.chat_ca;
                    ss = new SpannableString(s);
                    //ss.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb.append(ss);

                    vhImage1Text1_2.tv_31.setText(ssb);
                    break;
                case CN_CHAT_VT_COMMAND:
                case CN_CHAT_VT_CONFIRM_REQUEST:
                case CN_CHAT_VT_CONFIRM_RESPONSE:
                case CN_CHAT_VT_VIEW:
                    s ="닥터차 AI";
                    ss = new SpannableString(s);
                    //ss.setSpan(new AbsoluteSizeSpan(15, true), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ss.setSpan(new ForegroundColorSpan(CommonUtil.getColor(ctx, android.R.color.holo_blue_dark)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb.append(ss);

                    s = "  " + beanChatMessage.chat_ca;
                    ss = new SpannableString(s);
                    //ss.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb.append(ss);

                    if(viewType == CN_CHAT_VT_COMMAND) {
                        final VhImage1Text2 vhImage1Text2 = (VhImage1Text2)vh;
                        vhImage1Text2.tv_31.setText(ssb);
                        //////////////vhImage1Text2.tv_32.setText(beanChatMessage.chat_mb);

                        final String commandText;
                        if(beanChatMessage.chat_ct == 2) {
                            //사고수리(판금도색)
                            commandText = sr == 0 ? "신속하고 정확한 사고수리 상담을 위하여\n" + beanMember.nn + "님의 사고내역에 관련된 정보를 전송해 주세요" : ChatUtil.getNickNameFromId(groupChannel, beanChatChannelData.channel_mi) + "님께 사고수리할 내역에 관한 정보를 요청하였습니다";
                        } else {
                            //중고차 팔기
                            commandText = sr == 0 ? "신속하고 정확한 중고차판매 상담을 위하여\n" + beanMember.nn + "님의 차량에 관련된 정보를 전송해 주세요" : ChatUtil.getNickNameFromId(groupChannel, beanChatChannelData.channel_mi) + "님께 판매할 차량에 관한 정보를 요청하였습니다.";
                        }
                        vhImage1Text2.tv_32.setText(commandText);

                    } else if(viewType == CN_CHAT_VT_VIEW) {
                        final VhImage1Text2 vhImage1Text2 = (VhImage1Text2)vh;
                        vhImage1Text2.tv_31.setText(ssb);
                        vhImage1Text2.tv_32.setText(beanChatMessage.chat_mb);
                    }else if(viewType == CN_CHAT_VT_CONFIRM_REQUEST) {
                        //현재 사용 안한다
                        final VhText4 vhText4 = (VhText4)vh;
                        vhText4.tv_31.setText(ssb);
                        vhText4.tv_32.setText(beanChatMessage.chat_mb);
                    } else {
                        //현재 사용 안한다
                        final VhImage1Text2 vhImage1Text2 = (VhImage1Text2)vh;
                        vhImage1Text2.tv_31.setText(ssb);
                        vhImage1Text2.tv_32.setText(beanChatMessage.chat_mb);

                        if(beanChatMessage.chat_ct == 21) {
                            vhImage1Text2.iv_31.setImageResource(R.drawable.ic_device_hub_white_24dp);
                        }
                    }

                    break;

            }
        }
    }
}