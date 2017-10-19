package com.infoline.doctorcha.core.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.infoline.doctorcha.core.CoreCons;
import com.infoline.doctorcha.presentation.activity.MainActivity;
import com.sendbird.android.SendBirdException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

//import org.apache.commons.io.FileUtils;

/**
 * Created by Administrator on 2016-01-09.
 */
public class CommonUtil {
    /*
    public static boolean checkNetworkState(Context ctx) {
        final ConnectivityManager cm = (ConnectivityManager)(ctx.getSystemService(Context.CONNECTIVITY_SERVICE));
        final NetworkInfo ni = cm.getActiveNetworkInfo();
        final int type;

        if (ni == null) { // connected to the internet
            type = ConnectivityManager.
        } else {
            if (ni.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                Toast.makeText(context, ni.getTypeName(), Toast.LENGTH_SHORT).show();
            } else if (ni.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                Toast.makeText(context, ni.getTypeName(), Toast.LENGTH_SHORT).show();
            }
            // not connected to the internet
        }
    }
    */

    public static boolean checkNetworkState(Context ctx) {
        final ConnectivityManager cm = (ConnectivityManager)(ctx.getSystemService(Context.CONNECTIVITY_SERVICE));
        final NetworkInfo ni = cm.getActiveNetworkInfo();

        return ni != null;
    }

    public static void setSpinnerPosFromText(Spinner sp, String s) {
        for(int i= 0; i < sp.getAdapter().getCount(); i++)
        {
            if(sp.getAdapter().getItem(i).toString().equals(s))
            {
                sp.setSelection(i);
            }
        }

    }

    public static void showSendBirdErr(SendBirdException e, Context ctx) {
        final String msg = "SendBird : " + e.getCode() + " - " + e.getMessage();
        CommonUtil.writeLog(msg);
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }

    public static int getNumberFromString(String s) {
        return TextUtils.isEmpty(s) ? 0 : Integer.parseInt(s);
    }

    public static String getStringFromNumeric(int num) {
        return num == 0 ? "" : String.valueOf(num);
    }

    public static int getColor(Context ctx, final @ColorRes int colorId ) {
        int integerValue;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            integerValue = ctx.getColor(colorId);
        }
        else {
            integerValue = ContextCompat.getColor(ctx, colorId);
        }

        return integerValue;
    }

    public static String getDisplayTimeOrDate(long milli) {
        /*
        final Date date = new Date(milli);
        final SimpleDateFormat sdf;

        if(System.currentTimeMillis() - milli > 60 * 60 * 24 * 1000) {
            sdf = new SimpleDateFormat("MM.dd-HH:mm", Locale.KOREA);
        } else {
            sdf = new SimpleDateFormat("HH:mm", Locale.KOREA);
        }

        return sdf.format(date);
        */

        final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);;
        final String date1 = sdf1.format(new Date(milli));
        final String date2 = sdf1.format(new Date());
        final SimpleDateFormat sdf;

        if(date1.equals(date2)) {
            sdf = new SimpleDateFormat("HH:mm", Locale.KOREA);
        } else {
            sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.KOREA);
        }

        return sdf.format(new Date(milli));
    }

    public static String getFormattedDate(CoreCons.EnumDateFormat enumDateFormat) {
        //yyyyMMdd_HHmmss_SSS
        final SimpleDateFormat sdf = new SimpleDateFormat(enumDateFormat.getText(), Locale.KOREA);
        return sdf.format(new Date());
    }

    public static String getCurrentMethodName(@Nullable String menuName) {
        //0. getThreadStackTrace
        //1. getStackTrace
        //2. getCurrentMethodName
        //3. writeLog
        //4. writeLog()를 호출한 실제 method

        final StackTraceElement[] stes = Thread.currentThread().getStackTrace();

        /*
        for (StackTraceElement ste : stes) {
            Log.d("===============>", ste.getMethodName());
        }
        */

        //stes[4].getClass().getSimpleName() == "StackTraceElement"

        String className = stes[4].getClassName(); //com.infoline.com.infoline.doctorcha.presentation.fragment.MainSectionFragment
        className = className.substring(className.lastIndexOf(".") + 1);

        if(!TextUtils.isEmpty(menuName)) className = className + "." + menuName;

        return String.format("[%s] [%s]", className, stes[4].getMethodName());
    }

    public static void writeLog(@Nullable String msg) {
        if(msg == null) msg = "";
        Log.d("닥터차=====>", String.format("%s :: %s", getCurrentMethodName(null), msg));
    }

    public static void writeLog(@Nullable String menuName, @Nullable String msg) {
        if(msg == null) msg ="";
        Log.d("닥터차=====>", String.format("%s :: %s", getCurrentMethodName(menuName), msg));
    }

    public static void playVibrate(Context ctx, long milliseconds) {
        Vibrator vibrator = (Vibrator)ctx.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(milliseconds);
    }

    private boolean isPointInsideView(float x, float y, View view) {
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];

        // point is inside view bounds
        return ((x > viewX && x < (viewX + view.getWidth())) && (y > viewY && y < (viewY + view.getHeight())));
    }

    public static int getBitmapOfWidth( String fileName ){
        try {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(fileName, options);

            return options.outWidth;

        } catch(Exception e) {
            return 0;
        }
    }

    public static int getBitmapOfHeight( String fileName ){

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(fileName, options);

            return options.outHeight;

        } catch(Exception e) {
            return 0;
        }
    }

    public static void assignOnClickListener(View views[], View.OnClickListener onClickListener) {
        for(View v : views){
            v.setOnClickListener(onClickListener);
        }
    }

    public static void assignOnClickListener(ArrayList<View> views, View.OnClickListener onClickListener) {
        for(View v : views){
            v.setOnClickListener(onClickListener);
        }
    }

    public static void ApplyMaskShape(View[] views, int maskId, int oldId, int NewId) {
        if(oldId != -1) {
            for(View v : views) {
                if(v.getId() == oldId) {
                    v.setBackgroundResource(0);
                }
            }
        }

        for(View v : views)
        {if(v.getId() == NewId) {
            v.setBackgroundResource(maskId);
        }
        }
    }

    public static String getFileNameWithExt(@NonNull final String url) {
        String fileName;
        final int slashPos = url.lastIndexOf("/");

        if(slashPos == -1) {
            fileName = url;
        } else {
            fileName = url.substring(slashPos + 1);
        }

        return fileName;
    }

    public static String getFileName(final String url) {
        final String fileNameWithExt = getFileNameWithExt(url);
        final int pos = fileNameWithExt.indexOf(".");

        return url.substring(0, pos);
    }

    public static String getExtName(@NonNull final String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public static int getPxFromDip(Context co, int dipSize) {
        final float scale = co.getResources().getDisplayMetrics().density; // 화면의 밀도를 구한다.
        return (int) (dipSize  * scale + 0.5f); // 변환하는데 0.5 는 반올림을 위하여 붙여줌.
    }

    public static void setTextViewImage(Context co, TextView tv, int imageId, int imageDip, int paddingDip, int imagePosition) {
        //1. left 2.top 3.right 4.bottom

        final Drawable dr = ContextCompat.getDrawable(co, imageId);
        final int size = getPxFromDip(co, imageDip);

        if(paddingDip != 0) tv.setCompoundDrawablePadding(getPxFromDip(co, paddingDip));

        dr.setBounds(0, 0, size, size);
        //dr.setColorFilter(co.getResources().getColor(android.R.color.holo_red_dark), PorterDuff.Mode.SRC_ATOP);
        //dr.setColorFilter(0xffffff, PorterDuff.Mode.MULTIPLY);
        //dr.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);

        switch (imagePosition) {
            case 1:
                tv.setCompoundDrawables(dr, null, null, null);
                break;

            case 2:
                tv.setCompoundDrawables(null, dr, null, null);
                break;

            case 3:
                tv.setCompoundDrawables(null, null, dr, null);
                break;

            case 4:
                tv.setCompoundDrawables(null, null, null, dr);
                break;
        }
    }

    public static void setCompoundDrawablesWithTintList(TextView v, int drawableId, int stateId) {
        Context ctx = v.getContext();

        Drawable dr = ContextCompat.getDrawable(ctx, drawableId);
        //dr = dr.mutate(); //상수공유금지
        dr = DrawableCompat.wrap(dr.mutate());
        DrawableCompat.setTintMode(dr, PorterDuff.Mode.SRC_ATOP);

        DrawableCompat.setTintList(dr, ContextCompat.getColorStateList(ctx, stateId));

        /*
        Resources resources = ctx.getResources();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            DrawableCompat.setTintList(dr, ctx.getResources().getColorStateList(stateId, null));
        } else {
            DrawableCompat.setTintList(dr, ctx.getResources().getColorStateList(stateId));
        }
        */

        v.setCompoundDrawablesWithIntrinsicBounds(dr, null, null, null);

    }

    public static boolean isTouchInView(View v, MotionEvent e) {
        Rect rect = new Rect();
        v.getGlobalVisibleRect(rect);
        return rect.contains((int) e.getRawX(), (int) e.getRawY());
    }


    //검증 안됨
    public static int getSqrt(View v, PointF point) {

        Context ctx = v.getContext();

        WindowManager wm = (WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point screenSize = new Point();
        display.getSize(screenSize);     //1080px, 1920px

        /////////////////int w = v.getWidth();  //1080px
        ////////////////int h = v.getHeight(); //1845px   ==> 75는 StatusBar의 높이다.... why!

        final float garo = point.x >= screenSize.x / 2 ?  point.x : screenSize.x - point.x;
        final float sero = point.y >= screenSize.y / 2 ?  point.y : screenSize.y - point.y;

        return (int)Math.sqrt(Math.pow(garo, 2) + Math.pow(sero, 2));

        //return (int)Math.sqrt(Math.pow(v.getWidth(), 2) + Math.pow(v.getHeight(), 2));
    }

    public static int getSqrt(View v) {

        Context ctx = v.getContext();

        WindowManager wm = (WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point p = new Point();
        display.getSize(p);

        return (int)Math.sqrt(Math.pow(v.getWidth(), 2) + Math.pow(v.getHeight(), 2));
    }

    //1. 현재는 ViewHolder의 최상위 View가 반드시 CardView이어야 한다
    //2. 전달된 View는 CardView의 childView이어야 한다.
    //3. 1,2의 조건에 부합하지 않으면 App 사망하므로 정확히 맞추어 사용할 것
    //4. 다른 Layout과도 호환되도록 수정할 것

    public static int getAdapterPositionFromView(RecyclerView rv, View v) {
        int pos = -1;

        try {
            pos = rv.getChildAdapterPosition(v);
        }
        catch (Exception e) {
            ViewGroup parentView = (ViewGroup)(v.getParent());

            while (parentView != null) {
                try {
                    pos = rv.getChildAdapterPosition(parentView);
                    break;
                }
                catch (Exception e2) {
                    parentView = (ViewGroup)(parentView.getParent());
                }
            }
        }

        return pos;
    }

    public static Point getCenterPointFromView(View v) {
        int[] xy = new int[2];

        v.getLocationOnScreen(xy);
        //v.getLocationInWindow(xy);

        int w = v.getWidth();
        int h = v.getHeight();

        xy[0] += v.getWidth() / 2;
        xy[1] += v.getHeight() / 2;

        return new Point(xy[0], xy[1]);
    }

    public static Point getXYPointFromView(View v) {
        int[] xy = new int[2];

        v.getLocationOnScreen(xy);

        return new Point(xy[0], xy[1]);
    }

    //////////////////////////

    public static final String DATE_TIME = "dd MMM yyyy HH:mm:ss z";
    private static int colorAccent = -1;

    public static Point getScreenDimensions(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);

        Point point = new Point();
        point.set(dm.widthPixels, dm.heightPixels);
        return point;
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    public static int dpToPx(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * scale);
    }

    /**
     * dd MMM yyyy HH:mm:ss z
     * @param date
     * @return The date formatted.
     */
    public static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME, Locale.getDefault());
        return dateFormat.format(date);
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean hasMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static String getVersionName(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return "v"+pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return context.getString(android.R.string.unknownName);
        }
    }

    public static int getVersionCode(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    /*
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static int getColorAccent(Context context) {
        if (colorAccent < 0) {
            int accentAttr = CommonUtil.hasLollipop() ? android.R.attr.colorAccent : R.attr.colorAccent;
            TypedArray androidAttr = context.getTheme().obtainStyledAttributes(new int[] { accentAttr });
            colorAccent = androidAttr.getColor(0, 0xFF009688); //Default: material_deep_teal_500
        }
        return colorAccent;
    }
    */

    /**
     * Show Soft Keyboard with new Thread
     * @param activity
     */
    public static void hideSoftInput(final Activity activity) {
        if (activity.getCurrentFocus() != null) {
            new Runnable() {
                public void run() {
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                }
            }.run();
        }
    }

    /**
     * Hide Soft Keyboard from Dialogs with new Thread
     * @param context
     * @param view
     */
    public static void hideSoftKeyboardInput(final Context context, final View view) {
        new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }.run();
    }

    /**
     * Show Soft Keyboard with new Thread
     * @param context
     * @param view
     */
    public static void showSoftKeyboard(final Context context, final View view) {
        new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, InputMethodManager.SHOW_FORCED); //SHOW_IMPLICIT
            }
        }.run();
    }

    /**
     * Create the reveal effect animation
     * @param view the View to reveal
     * @param cx coordinate X
     * @param cy coordinate Y
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void reveal(final View view, int cx, int cy) {
        if (!hasLollipop()) {
            view.setVisibility(View.VISIBLE);
            return;
        }

        //Get the final radius for the clipping circle
        int finalRadius = Math.max(view.getWidth(), view.getHeight());

        //Create the animator for this view (the start radius is zero)
        Animator animator =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);

        //Make the view visible and start the animation
        view.setVisibility(View.VISIBLE);
        animator.start();
    }

    /**
     * Create the un-reveal effect animation
     * @param view the View to hide
     * @param cx coordinate X
     * @param cy coordinate Y
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void unReveal(final View view, int cx, int cy) {
        if (!hasLollipop()) {
            view.setVisibility(View.GONE);
            return;
        }

        //Get the initial radius for the clipping circle
        int initialRadius = view.getWidth();

        //Create the animation (the final radius is zero)
        Animator animator =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0);

        //Make the view invisible when the animation is done
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.GONE);
            }
        });

        //Start the animation
        animator.start();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    /*
    테스트 전무
    private HashMap<String, String> getHashMap(Object obj) {
        HashMap<String, String> map = new HashMap<String, String>();

        Class<?> cls = obj.getClass();

        Field fields[] = cls.getFields();
        for(Field f : fields) {
            String name = f.getName();
            String value = f.get(obj).toString();
            map.put(name, value);
        }
        return map;
    }
    */
}