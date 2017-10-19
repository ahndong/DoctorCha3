package com.infoline.doctorcha.presentation;

import com.infoline.doctorcha.presentation.bean.BeanCarInfo;
import com.infoline.doctorcha.presentation.bean.BeanCarKind;
import com.infoline.doctorcha.presentation.bean.BeanContent;
import com.infoline.doctorcha.presentation.bean.BeanMember;
import com.infoline.doctorcha.presentation.bean.BeanMemberAndShop;
import com.infoline.doctorcha.presentation.bean.BeanMemo;
import com.infoline.doctorcha.presentation.bean.BeanMultiFavorite;
import com.infoline.doctorcha.presentation.bean.BeanPost;
import com.infoline.doctorcha.presentation.bean.BeanPostAndContents;
import com.infoline.doctorcha.presentation.bean.BeanPostAndContents_test;
import com.infoline.doctorcha.presentation.bean.BeanPushTarget;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2016-09-05.
 */
public class RetrofitInterface {
    public interface MemberService {
        @GET("members/by_sr")
        Call<List<BeanMemberAndShop>> selectBySr(@Query("sr") String sr, @Query("startNo") int startNo, @Query("limitCount") int limitCount);

        //1. 회원가입
        //2. insert후 id, mt를 return한다
        //3. members가 업ㅅ을 경우 404 : Not Found , signin이 없을 경우 405 : Method Not Allowed
        @POST("members/signin")
        Call<List<BeanMember>> signin(@Body BeanMember beanMember);

        //1. 로그인
        @GET("members/login")
        Call<List<BeanMember>> login(@Query("nn") String nn, @Query("upw") String upw);

        //1. select
        @GET("members/{id}")
        Call<List<BeanMember>> select(@Path("id") int id);

        //1. select
        @GET("members/{id}/cn")
        Call<String> selectCn(@Path("id") int id);

        //3. scalar function으로 대체할 것
        @GET("members/counsellor_idlist")
        Call<List<BeanMember>> selectCounsellorIdList();

        //Counsellor Channel Url 저장
        @PUT("members/{id}/ccu")
        @FormUrlEncoded
        Call<Void> update_ccu(@Path("id") int id, @Field("ccu") String ccu);

        //1. GCM Token 저장
        @PUT("members/{id}/gt")
        @FormUrlEncoded
        Call<Void> update_gt(@Path("id") int id, @Field("gt") String gt);

        //1. sr 저장
        @PUT("members/{id}/sr")
        @FormUrlEncoded
        Call<Void> update_sr(@Path("id") int id, @Field("sr") String sr);
    }

    public interface ShopService {
        @GET("shops/{id}")
        Call<List<BeanMemberAndShop>> select(@Path("id") int id);

        @GET("shops/by_bmcategoryid")
        Call<List<BeanMemberAndShop>> selectByBmCategoryId(@Query("bmCategory_id") int bmCategory_id, @Query("startNo") int startNo, @Query("limitCount") int limitCount);

        @GET("shops/by_free")
        Call<List<BeanMemberAndShop>> selectByFree(@Query("bmCategory_id") int bmCategory_id, @Query("ca") String ca, @Query("cn") String cn, @Query("words") String words, @Query("startNo") int startNo, @Query("limitCount") int limitCount);

        @PUT("shops")
        Call<Void> update(@Body BeanMemberAndShop beanMemberAndShop);
    }

    public interface FavoriteShopService {
        @DELETE("favoriteshops/{member_id}/{blog_id}")
        Call<Void> delete(@Path("member_id") int member_id, @Path("blog_id") int blog_id);

        @POST("favoriteshops/{member_id}/{blog_id}")
        Call<Void> insert(@Path("member_id") int member_id, @Path("blog_id") int blog_id);
    }

    public interface PushTargetService {
        @GET("pushtargets/by_memberid")
        Call<List<BeanPushTarget>> selectByMemberId(@Query("member_id") int member_id);

        @DELETE("pushtargets/{member_id}/{blog_id}")
        Call<Void> delete(@Path("member_id") int member_id, @Path("blog_id") int blog_id);

        @POST("pushtargets/{member_id}/{blog_id}")
        Call<Void> insert(@Path("member_id") int member_id, @Path("blog_id") int blog_id);
    }

    public interface FcmService {
        @POST("fcm/toTopics")
        Call<Void> sendToTopics(@Body BeanPost beanPost);

        @POST("fcm/toMultiIds")
        Call<Void> sendtoMultiIds(@Body BeanPost beanPost);
    }

    public interface SendBirdService {
        @DELETE("sendbird/delete_groupchannel/{channel_url}")
        Call<Void> deleteGroupChannel(@Path("channel_url") String channel_url);
    }

    public interface CarInfoService {
        @GET("carinfos/{member_id}")
        Call<List<BeanCarInfo>> select(@Path("member_id") int member_id);

        @POST("carinfos")
        Call<Integer> insert(@Body BeanCarInfo beanCarInfo);

        @PUT("carinfos")
        Call<Integer> update(@Body BeanCarInfo beanCarInfo);

        @DELETE("carinfos/{member_id}")
        Call<Void> delete(@Path("member_id") int member_id);
    }

    public interface PostService {
        @GET("posts/{id}")
        Call<List<BeanPost>> select(@Path("id") int id);

        //나 자신의 해당 카테고리(예:사고수리) 마지막 post 및 contents 요청
        @GET("posts/for_counsel_by_last") // --> for_counsel을 @Path = 특정 post 카테고리로 또는 post type으로 지정해야 할 것이다. --< 채팅을 위한 임시조처
        Call<BeanPostAndContents> selectForCounselByLast(@Query("member_id") int member_id);

        //회원이 작성한 사고수리 내역 등 전달받은 post_id로 post 및 contents 요청
        @GET("posts/{id}/for_counsel_by_id")
        Call<BeanPostAndContents> selectForCounselById(@Path("id") int id);

        /*
        @POST("posts")
        Call<Void> insert(@Body BeanPost beanPost, @Body List<BeanContent> beanContentList);
        */

        //insert된 post_id값이 문자열로 넘어온다
        @POST("posts")
        Call<Integer> insert(@Body BeanPostAndContents_test beanPostAndContents_test);

        @PUT("posts")
        Call<Integer> update(@Body BeanPostAndContents_test BeanPostAndContents_test);

        @DELETE("posts/{id}")
        Call<Void> delete(@Path("id") int id);

        @GET("posts/by_free")
        Call<List<BeanPost>> selectByFree(
                @Query("bmCategory_id") final int bmCategory_id,
                @Query("board_id") final int board_id,
                @Query("owner_id") final int owner_id,
                @Query("creator_id") final int creator_id,
                @Query("words") final String words,
                @Query("startNo") final int startNo,
                @Query("limitCount") final int limitCount);

        /*
        @GET("posts/by_board_id")
        Call<List<BeanPost>> selectByBoardId(@Query("board_id") int board_id);
        */

        //나 자신의 해당 카테고리(예:사고수리) 마지막 post 및 contents 요청
        @GET("posts/by_parentid")
        Call<List<BeanPost>> selectByParentId(@Query("parent_id") int parent_id);
    }

    public interface MemoService {
        @GET("memos/by_writerid_targetid")
        Call<List<BeanMemo>> selectByWriterIdAndTargetId(@Query("writer_id") int writer_id, @Query("target_id") int target_id);

        //insert된 post_id값이 문자열로 넘어온다
        @POST("memos")
        Call<Integer> insert(@Body BeanMemo beanMemo);

        @PUT("memos")
        Call<Void> update(@Body BeanMemo beanMemo);

        @DELETE("memos/{id}")
        Call<Void> delete(@Path("id") int id);
    }

    public interface ContentService {
        //TODO : 아래 @Query 방식으로 통일할 것
        @GET("contents/{post_id}")
        Call<List<BeanContent>> select(@Path("post_id") int member_id);

        @GET("contents/by_postid")
        Call<List<BeanContent>> selectByPostId(@Query("post_id") int post_id);
    }

    public interface CarKindService {
        @GET("carkinds")
        Call<List<BeanCarKind>> select();
    }

    public interface MultiService {
        @GET("multi/favorite")
        Call<BeanMultiFavorite> selectFavoriteByMemberId(@Query("member_id") int member_id);
    }

    public interface UploadService {
        //@Path("pathId") int pathType 함께 사용시 오류
        @POST("uploads/file")
        Call<Void> file(@Body RequestBody body);

        @POST("uploads/files")
        Call<Void> files(@Body RequestBody body);

        @POST("uploads/files2")
        Call<Void> files2(@Body RequestBody body);
    }

    public interface DownloadService {
        @Streaming
        @GET
        Call<ResponseBody> files(@Url String url);
    }

    /*
    public interface YoutubeServiceForYoutubeServer {
        //안먹는다 - 낸중에 확인할 것  =====> 아마도 JSONObject 무제인 듯....낸중에 JsonObject로 바꾸어 test해볼 것
        //https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&key=AIzaSyArvgZqAJkMMokOp0K6Vk5yTL_tJsyTuwM&playlistId=PLgHlOY1RAfgh4s7Be2H_7EwpuL_gpjy8Z
        @GET("playlistItems")
        Call<JSONObject> videos(@Query("key") String key, @Query("playlistId") String playlistId, @Query("part") String part);
    }
    */
}
