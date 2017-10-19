package com.infoline.doctorcha.core.httphelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

//import com.franmontiel.persistentcookiejar.PersistentCookieJar;
//import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
//import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.infoline.doctorcha.core.util.CommonUtil;
import com.infoline.doctorcha.presentation.MainApp;
import com.infoline.doctorcha.presentation.bean.BeanErrResponse;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.ConnectException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by DELL on 2016-04-09.
 */
public class ServiceGenerator {
    //private static final String API_BASE_URL = "http://infoline.iptime.org:8088/CarsGramRest/rest/";
    private static final String API_BASE_URL = "http://doctorcha.co.kr:8088/CarsGramRest/rest/";
    //private static final String API_BASE_URL = "http://192.168.0.113:8080/rest/";
    //private static final String API_BASE_URL_YOUTUBE = "https://www.googleapis.com/youtube/v3/";
    private static final String API_BASE_URL_AMAZON = "https://sendbird-upload.s3-ap-northeast-1.amazonaws.com/";
    private static OkHttpClient okHttpClient = new OkHttpClient();
    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(API_BASE_URL).addConverterFactory(GsonConverterFactory.create());
    private static Retrofit.Builder builderForAmazon = new Retrofit.Builder().baseUrl(API_BASE_URL_AMAZON).addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.client(okHttpClient).build();
    private static Retrofit retrofitForAmazon = builderForAmazon.client(okHttpClient).build();

    public static <S> S createService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }

    public static <S> S createServiceaForAmazon(Class<S> serviceClass) {
        return retrofitForAmazon.create(serviceClass);
    }

    /*
    public static APIError parseError(Response<?> response) {
        Converter<ResponseBody, APIError> converter =
                ServiceGenerator.retrofit()
                        .responseBodyConverter(APIError.class, new Annotation[0]);

        APIError error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new APIError();
        }

        return error;
    }

    public static class APIError {

        private int statusCode;
        private String message;

        public APIError() {
        }

        public int status() {
            return statusCode;
        }

        public String message() {
            return message;
        }
    }
    */

    public static void displayErrMessageOnResponse(Context ctx, Response response) {
        //1. 404 같은 경우에는 beanErrResponse 자체가 null이다
        String msg;

        try {
            final BeanErrResponse beanErrResponse = new BeanErrResponse(response.errorBody().source().toString()); //{ec:%s,em:'%s',sv:'%s'}
            msg = beanErrResponse.em == null ? String.format("[%s]%s", response.code(), response.message()) : beanErrResponse.em;

        } catch (Exception e) {
            msg = e.getMessage();
        }

        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }

    public static void displayErrMessageOnFailure(Context ctx, Throwable t) {
        Toast.makeText(ctx, ServiceGenerator.getExceptionMsgByCause(t), Toast.LENGTH_LONG).show();
    }

    public static String getExceptionMsgByCause(Throwable t) {
        //1.  java.net.ConnectException: Failed to connect to /192.168.0.16:8080 - webservice가 응답이 없을 경우
        //2.  java.net.SocketTimeoutException: failed to connect to /192.168.0.16 (port 8080) after 10000ms - device의 network가 정상적으로 작동하지 않을 경우

        //위 설명 잘못되었다

        final String msg;

        if(t instanceof ConnectException) {
            //위 설명 잘못되었다. 모바일 network이 꺼져 있어도 ConnectException이 발생한다
            //msg = "웹 서버스 상태가 좋지 않습니다";
            msg = "네트워크 상태가 좋지 않습니다";
        }
        else if(t instanceof SocketTimeoutException) {
            msg = "네트워크 상태가 좋지 않습니다";
        }
        else {
            msg = t.getMessage();
        }

        return msg;
    }

    /*
    //private static final String API_BASE_URL = "http://infoline.iptime.org:8088/CarsGramRest/rest/";
    private static final String API_BASE_URL = "http://192.168.0.16:8080/rest/";
    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(API_BASE_URL).addConverterFactory(GsonConverterFactory.create());
    private static CookieJar cookieJar = new MyCookieJar();

    public static <S> S createService(Class<S> serviceClass) {
        final OkHttpClient httpClient = new OkHttpClient.Builder().cookieJar(cookieJar).build();

        Retrofit retrofit = builder.client(httpClient).build();
        return retrofit.create(serviceClass);
    }

    public static class MyCookieJar implements CookieJar {
        private List<Cookie> cookies;

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            this.cookies =  cookies;
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            if (cookies != null) {
                return cookies;
            }

            return new ArrayList<Cookie>();

        }
    }
    */
}
