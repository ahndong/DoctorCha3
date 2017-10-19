package com.infoline.doctorcha.presentation.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.httphelper.ServiceGenerator;
import com.infoline.doctorcha.core.listener.ViewOnClickListener;
import com.infoline.doctorcha.core.util.CommonUtil;
import com.infoline.doctorcha.presentation.MainApp;
import com.infoline.doctorcha.presentation.MainCons;
import com.infoline.doctorcha.presentation.RetrofitInterface;
import com.infoline.doctorcha.presentation.bean.BeanChatMessage;
import com.infoline.doctorcha.presentation.bean.BeanChatMessageData;
import com.infoline.doctorcha.presentation.util.ChatUtil;
import com.infoline.doctorcha.presentation.viewholder.VhImage1Text1;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserMessage;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.infoline.doctorcha.core.util.CommonUtil.writeLog;
import static com.infoline.doctorcha.presentation.MainApp.beanMember;
import static com.infoline.doctorcha.presentation.MainCons.CN_CHAT_ST_ADMIN;

/**
 * Created by Administrator on 2016-08-26.
 */
public class SendBirdChannelListFragment extends Fragment {
    @BindView(R.id.rv) RecyclerView rv;

    private static final String CN_CHANNEL_IDENTIFER = SendBirdChannelListFragment.class.getSimpleName();
    private GroupChannelListQuery groupChannelListQuery;

    private MyRecyclerAdapter rva;
    private boolean isDateOver = false;
    private int sr;
    private String channelUrl;

    public SendBirdChannelListFragment() {
        CommonUtil.writeLog(null);
    }

    public static SendBirdChannelListFragment newInstance(int sr, String channelUrl) {
        SendBirdChannelListFragment fragment = new SendBirdChannelListFragment();
        final Bundle bundle = new Bundle();
        bundle.putInt("sr", sr);
        bundle.putString("channelUrl", channelUrl);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CommonUtil.writeLog(null);
        View rootView = inflater.inflate(R.layout.fragment_sendbirdchannellist, container, false);
        ButterKnife.bind(this, rootView);

        this.sr = getArguments().getInt("sr");
        this.channelUrl = getArguments().getString("channelUrl");

        rva = new MyRecyclerAdapter(getActivity());

        final LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        lm.setOrientation(LinearLayoutManager.VERTICAL);

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView _rv, int newState) {

            }

            @Override
            public void onScrolled(RecyclerView _rv, int dx, int dy) {
                CommonUtil.writeLog(null);

                int itemCount = lm.getItemCount();
                int lastVisiblePos = lm.findLastVisibleItemPosition();

                if (lastVisiblePos == itemCount - 1) {
                    loadNextChannels();
                }
            }
        });

        rv.setHasFixedSize(true);
        rv.setLayoutManager(lm);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        rv.setAdapter(rva);

        addChannelHandler();
        groupChannelListQuery = GroupChannel.createMyGroupChannelListQuery();
        groupChannelListQuery.setIncludeEmpty(false);
        groupChannelListQuery.setOrder(GroupChannelListQuery.Order.LATEST_LAST_MESSAGE);

        loadNextChannels();

        return rootView;
    }

    @Override
    public void onResume() {
        CommonUtil.writeLog(null);

        super.onResume();

        /*
        rva.clear();
        rva.notifyDataSetChanged();

        if(SendBird.getConnectionState() != SendBird.ConnectionState.OPEN) {
            CommonUtil.writeLog("call SendBird.connect()");
            SendBird.connect(String.valueOf(beanPreferAuth.id), new SendBird.ConnectHandler() {
                @Override
                public void onConnected(User user, SendBirdException e) {
                    if(e != null) {
                        CommonUtil.writeLog(e.getCode() + ":" + e.getMessage());
                        return;
                    }
                    groupChannelListQuery = GroupChannel.createMyGroupChannelListQuery();
                    groupChannelListQuery.setIncludeEmpty(true);

                    loadNextChannels();
                }
            });
        } else {
            groupChannelListQuery = GroupChannel.createMyGroupChannelListQuery();
            groupChannelListQuery.setIncludeEmpty(true);

            loadNextChannels();
        }
        */
    }

    @Override
    public void onPause() {
        CommonUtil.writeLog(null);
        super.onPause();
        //SendBird.removeChannelHandler(CN_CHANNEL_IDENTIFER);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        writeLog(null);
        SendBird.removeChannelHandler(CN_CHANNEL_IDENTIFER);
    }

    private void addChannelHandler() {
        writeLog(null);

        SendBird.addChannelHandler(CN_CHANNEL_IDENTIFER, new SendBird.ChannelHandler() {
            @Override
            public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
                CommonUtil.writeLog(null);;

                if(baseChannel instanceof GroupChannel) {
                    GroupChannel groupChannel = (GroupChannel)baseChannel;
                    rva.replace(groupChannel);
                }
            }

            @Override
            public void onUserJoined(GroupChannel groupChannel, User user) {
                // Member changed. Refresh group channel item.
                CommonUtil.writeLog(null);
                rva.notifyDataSetChanged();
            }

            @Override
            public void onUserLeft(GroupChannel groupChannel, User user) {
                // Member changed. Refresh group channel item.
                CommonUtil.writeLog(null);
                rva.notifyDataSetChanged();
            }
        });
    }

    private void loadNextChannels() {
        CommonUtil.writeLog(null);

        if(isDateOver || groupChannelListQuery == null || groupChannelListQuery.isLoading() || !groupChannelListQuery.hasNext()) {
            return;
        }

        groupChannelListQuery.next(new GroupChannelListQuery.GroupChannelListQueryResultHandler() {
            @Override
            public void onResult(List<GroupChannel> list, SendBirdException e) {
                if(e != null) {
                    CommonUtil.writeLog(e.getCode() + ":" + e.getMessage());
                }
                else {
                    rva.addAll(list, false);
                    rva.notifyDataSetChanged();
                }
            }
        });
    }

    //inner class
    private class MyRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final ImageLoader imageLoader;
        private final Context ctx;
        private final List<GroupChannel> groupChannelList;
        private final ViewOnClickListener viewOnClickListener;

        public MyRecyclerAdapter(final Context ctx) {
            CommonUtil.writeLog(null);

            this.ctx = ctx;
            this.imageLoader = ImageLoader.getInstance();
            this.groupChannelList = new ArrayList<>();

            viewOnClickListener = new ViewOnClickListener(ctx, new ViewOnClickListener.OnClickListener() {
                @Override
                public void onViewClick(final View v, Point touchPoint, final Point rawTouchPoint) {
                    final int viewId = v.getId();
                    final int pos = CommonUtil.getAdapterPositionFromView(rv, v);
                    final String newChannelUrl = getItem(pos).getUrl();

                    if(viewId == -1) {
                        if(channelUrl != null && newChannelUrl.equals(channelUrl)) {
                            Toast.makeText(getActivity(), "이미 열려있는 상담 채널입니다", Toast.LENGTH_SHORT).show();
                            notifyItemChanged(pos);
                        } else {
                            final Intent intent = new Intent();
                            intent.putExtra(MainCons.EnumExtraName.NAME1.name(), getItem(pos).getUrl());
                            getActivity().setResult(Activity.RESULT_OK, intent);
                            getActivity().finish();
                        }
                    }
                    else {

                    }
                }

                @Override
                public void onViewLongPress(final View v, Point touchPoint, Point rawTouchPoint) {
                    //1. 닥터차는 대화방을 나갈 수 없다
                    //2. 초대받은 상담사, 업체는 언제던지 대화방을 나갈 수 있다
                    //3. 개인, 업체, 상담사 자신에 배당된 전용 상담채널은 나갈 수 없다
                    //4. 채널삭제는 ID == 2만 할 수 있다
                    if(beanMember.id == 1 || beanMember.id == 2) {
                        final String[] items = { "대화방 나가기", "대화방 삭제"};
                        final AlertDialog ad;
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, final int which) {
                                final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which2) {
                                        if(which2 == DialogInterface.BUTTON_POSITIVE) {
                                            final int pos = CommonUtil.getAdapterPositionFromView(rv, v);
                                            final GroupChannel groupChannel = rva.getItem(pos);

                                            if(which == 0) {
                                                groupChannel.leave(new GroupChannel.GroupChannelLeaveHandler() {
                                                    @Override
                                                    public void onResult(SendBirdException e) {
                                                        if (e != null) {
                                                            Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }

                                                        Toast.makeText(getActivity(), "대화방에서 성공적으로 나왔습니다", Toast.LENGTH_SHORT).show();
                                                        rva.remove(pos);
                                                        rva.notifyDataSetChanged();
                                                    }
                                                });
                                            } else {
                                                final RetrofitInterface.SendBirdService service = ServiceGenerator.createService(RetrofitInterface.SendBirdService.class);
                                                final Call<Void> call = service.deleteGroupChannel(groupChannel.getUrl());

                                                call.enqueue(new Callback<Void>() {
                                                    @Override
                                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                                        final String msg;

                                                        if(!response.isSuccessful()) {
                                                            msg = "대화방 삭제에 실패하였습니다\n" + response.errorBody().source().toString();
                                                        } else {
                                                            msg = "대화방이 성공적으로 삭제되었습니다.";
                                                        }

                                                        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();

                                                        if(response.isSuccessful()) {
                                                            rva.remove(pos);
                                                            rva.notifyDataSetChanged();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<Void> call, Throwable t) {
                                                        Toast.makeText(getActivity(), ServiceGenerator.getExceptionMsgByCause(t), Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }
                                        }
                                    }
                                };

                                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage(which == 0 ? "선택하신 대화방에서 나가겠습니까?" : "선택하신 대화방을 영구 삭제 하시겠습니까?").setPositiveButton("예", dialogClickListener).setNegativeButton("아니오", dialogClickListener).show();

                            }
                        });

                        ad = builder.create();
                        ad.show();
                    }
                }
            });
        }

        private void addAll(List<GroupChannel> channels, boolean isTodayOniy) {
            //groupChannelList.addAll(channels);

            for(GroupChannel groupChannel : channels) {
                //최종 message time으로 정렬되어 있다

                final BaseMessage baseMessage = groupChannel.getLastMessage();
                //////////////groupChannelList.add(0, groupChannel);
                groupChannelList.add(groupChannel);

                /*
                if(baseMessage != null) {
                    if(isTodayOniy && System.currentTimeMillis() - baseMessage.getCreatedAt() > 60 * 60 * 24 * 1000) {
                        isDateOver = true;
                        break;
                    }
                    else {
                        groupChannelList.add(0, groupChannel);
                    }
                }
                */
            }
        }

        private void replace(GroupChannel newChannel) {
            for(GroupChannel oldChannel : groupChannelList) {
                if(oldChannel.getUrl().equals(newChannel.getUrl())) {
                    //groupChannelList.remove(oldChannel);
                    oldChannel = newChannel;
                    notifyDataSetChanged();
                    return;
                    //break;
                }
            }

            groupChannelList.add(0, newChannel);
            notifyDataSetChanged();
        }

        public void clear() {
            groupChannelList.clear();
        }

        private void remove(int pos) {
            groupChannelList.remove(pos);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return groupChannelList.size();
        }

        public GroupChannel getItem(int pos) {
            //ViewHolder로 상속된다.
            //return posts.get(pos).getId();
            return groupChannelList.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            //ViewHolder로 상속된다.
            //return posts.get(pos).getId();
            return pos;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup vg, int itemViewType) {
            final VhImage1Text1 vhImage1Text1 = new VhImage1Text1(LayoutInflater.from(ctx).inflate(R.layout.vh_chat_channellist, vg, false));
            vhImage1Text1.iv_31.setOnTouchListener(viewOnClickListener);
            vhImage1Text1.itemView.setOnTouchListener(viewOnClickListener);
            return vhImage1Text1;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder vh, int pos) {
            final GroupChannel groupChannel = groupChannelList.get(pos);

            final VhImage1Text1 vhImage1Text1 = (VhImage1Text1)vh;

            vhImage1Text1.itemView.setPressed(channelUrl != null && channelUrl.equals(groupChannel.getUrl())); //이미 대화에 참여중인 상담사 또는 업체

            //imageLoader.displayImage(groupChannel.getCoverUrl(), vhImage1Text1.iv_31, MainApp.optionsForCircleThumb);
            imageLoader.displayImage("drawable://" + R.mipmap.ic_launcher, vhImage1Text1.iv_31, MainApp.optionsForCircleThumb);

            final SpannableStringBuilder ssb = new SpannableStringBuilder();

            SpannableString ss;
            String s;

            final BaseMessage lastBaseMessage = groupChannel.getLastMessage();

            //대화 참가자
            s = getDisplayMemberNames(groupChannel);
            ss = new SpannableString(s);
            ss.setSpan(new AbsoluteSizeSpan(15, true), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append(ss);

            //unRead Count
            final int xx = groupChannel.getUnreadMessageCount();
            if(xx > 0) {
                s = "  " + groupChannel.getUnreadMessageCount();
                ss = new SpannableString(s);
                ss.setSpan(new AbsoluteSizeSpan(15, true), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new ForegroundColorSpan(CommonUtil.getColor(ctx, android.R.color.holo_red_dark)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.append(ss);
            }

            if(lastBaseMessage == null) {
                s = "\n메세지가 존재하지 않습니다";

            } else {
                final BeanChatMessage beanChatMessage = new BeanChatMessage(lastBaseMessage);
                s = "\n";
                s += ChatUtil.getSenderName(beanChatMessage);
                s += " : " + CommonUtil.getDisplayTimeOrDate(lastBaseMessage.getCreatedAt());
                ss = new SpannableString(s);
                ss.setSpan(new ForegroundColorSpan(CommonUtil.getColor(ctx, android.R.color.holo_blue_dark)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.append(ss);

                //s = "\n" + userMessage.getMessage().replace("\n", " ");
                s = "\n" + ChatUtil.getDisplayMessage(beanChatMessage);
                ss = new SpannableString(s);
                ss.setSpan(new ForegroundColorSpan(CommonUtil.getColor(ctx, R.color.tc_grey_body)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.append(ss);
            }

            vhImage1Text1.tv_31.setText(ssb);
        }

        private String getDisplayMemberNames(GroupChannel groupChannel) {
            final StringBuilder names = new StringBuilder();
            for (User member : groupChannel.getMembers()) {
                if (member.getUserId().equals(SendBird.getCurrentUser().getUserId())) {
                    continue;
                }

                names.append(", ");
                names.append(member.getNickname());
            }
            return names.delete(0, 2).toString();
        }
    }
}
