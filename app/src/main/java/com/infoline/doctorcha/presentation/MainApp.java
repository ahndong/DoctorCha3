/*
android:backgroundTint="@android:color/holo_green_light"

 */

package com.infoline.doctorcha.presentation;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.multidex.MultiDexApplication;
import android.widget.EditText;

import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.util.CommonUtil;
import com.infoline.doctorcha.presentation.bean.BeanBoarde;
import com.infoline.doctorcha.presentation.bean.BeanMember;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

import static com.infoline.doctorcha.core.util.CommonUtil.writeLog;

/*
String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";

AQuery aq = new AQuery(this);

  

  ProgressDialog dialog = new ProgressDialog(this);

  dialog.setTitle("Loading");

  dialog.setMessage("로딩중입니다.");

  

  aq.progress(dialog).ajax(url,null,String.class,new AjaxCallback<String>(){

   @Override

   public void callback(String url, String object, AjaxStatus status) {

    TextView textview = (TextView) findViewById(R.id.textView1);

    textview.setText(object);

    super.callback(url, object, status);

   }});
*/

public class MainApp extends MultiDexApplication {
	//변수 사용가능 콤포넌트 - 사용방법은 http://arabiannight.tistory.com/316 참고할 것
	//1. Activity
	//2. BroadCastReceiver
	//3. ContentProvider
	//4. Service - 가능한지 글쓴이가 확인중


	//1. LoginActivity에서 로그인 또는 회원등록 후 초기화 된다.
	public static BeanMember beanMember;
	public static List<BeanBoarde> beanBoardeList;

	//public static List<BeanMainCategory> beanCategories = new ArrayList<>();

	public static DisplayImageOptions optionsForBasic;
	public static DisplayImageOptions optionsForRectThumb;
	public static DisplayImageOptions optionsForCircleThumb;

	//-----------------------------------------------------------------------------------------------------------------------

	public MainApp() {
		writeLog("Application Start");

		//android의 모든 생성자 내에서는 context가 null이다
	}
		
    //액티비티, 리시버, 서비스가 생성되기전 어플리케이션이 시작될 때
    @Override
    public void onCreate() {
        super.onCreate();

		//카테고리 전체목록
		buildMainCategories();
        
        /*Enabled Option of all
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
        .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
        .discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, null)
        .taskExecutor(...)
        .taskExecutorForCachedImages(...)
        .threadPoolSize(3) // default
        .threadPriority(Thread.NORM_PRIORITY - 1) // default
        .tasksProcessingOrder(QueueProcessingType.FIFO) // default
        .denyCacheImageMultipleSizesInMemory()
        .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
        .memoryCacheSize(2 * 1024 * 1024)
        .memoryCacheSizePercentage(13) // default
        .discCache(new UnlimitedDiscCache(cacheDir)) // default
        .discCacheSize(50 * 1024 * 1024)
        .discCacheFileCount(100)
        .discCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
        .imageDownloader(new BaseImageDownloader(context)) // default
        .imageDecoder(new BaseImageDecoder()) // default
        .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
        .writeDebugLogs()
        .build()
    	*/

		DisplayImageOptions defaultOption = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.considerExifParams(true)
				.build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
				.defaultDisplayImageOptions(defaultOption)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.build();

		ImageLoader.getInstance().init(config);

		optionsForBasic = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.considerExifParams(true)
				.build();

		optionsForRectThumb = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisk(true)
				//.displayer(new RoundedBitmapDisplayer(MainApp.getPxFromDip(ctx, 7)))
				.displayer(new RoundedBitmapDisplayer(40))
				.build();

		optionsForCircleThumb = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.displayer(new RoundedBitmapDisplayer(100))
				.build();

		beanBoardeList = new ArrayList<>();

		beanBoardeList.add(new BeanBoarde(1, "공지사항"));
		beanBoardeList.add(new BeanBoarde(2, "수리의뢰"));
		beanBoardeList.add(new BeanBoarde(3, "서비스사례"));
		beanBoardeList.add(new BeanBoarde(4, "고객리뷰"));
		beanBoardeList.add(new BeanBoarde(5, "이벤트"));
		beanBoardeList.add(new BeanBoarde(6, "중고차매물"));
		beanBoardeList.add(new BeanBoarde(7, "벼룩시장"));
		beanBoardeList.add(new BeanBoarde(8, "경정비상식"));
		beanBoardeList.add(new BeanBoarde(9, "블로그소개"));
		beanBoardeList.add(new BeanBoarde(10, "Q/A"));
		beanBoardeList.add(new BeanBoarde(11, "자유게시판"));
	}
    
    //컴포넌트가 실행되는 동안 단말의 화면이 바뀌면 시스템이 실행 한다.
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		writeLog(null);
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onTerminate() {
		//1. 애플리케이션 객체와 모든 컴포넌트가 종료될 때 호출
		//2. 항상 발생되지 않음. 종료처리할 때만 사용됨
		writeLog(null);
		super.onTerminate();
	}

	//----------------------------------------------------------------------------------------------
    
    private static void buildMainCategories() {
		/*
        for(MainCons.EnumMainCategory enumMainCategory : MainCons.EnumMainCategory.values()) {
            beanCategories.add(new BeanMainCategory(enumMainCategory.ordinal(), enumMainCategory.getNm(), enumMainCategory.getMcs(), enumMainCategory.getRes(), enumMainCategory.ordinal()));
        }
        */
    }

	public static boolean validateInputText(final Context ctx, final EditText et, final boolean required, final int minLength) {
		String errMsg = null;

		final String inputText = et.getText().toString().trim();
		final int len = inputText.length();

		if(required && len == 0) {
			errMsg = ctx.getString(R.string.error_invalid_required);
		} else {
			final int inputType = et.getInputType();

			if(inputType == 33) { //InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
				if(!(android.util.Patterns.EMAIL_ADDRESS.matcher(inputText).matches())) {
					errMsg = ctx.getString(R.string.error_invalid_email, "이메일");;
				}
			} else {
				if(len < minLength) {
					errMsg = ctx.getString(R.string.error_invalid_minLength, minLength);
				}
			}
		}

		if(errMsg == null) {
			return true;
		} else {
			et.setError(errMsg);
			et.requestFocus();
			return false;
		}
	}
}
