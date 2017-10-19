package com.infoline.doctorcha.presentation;

import android.os.Environment;
import android.support.annotation.DrawableRes;

import com.infoline.doctorcha.R;

public class MainCons {
    /*
    실전사용이 최종 확정된 항목
     */

    //sendbird

    private static final String CN_PATH_DOCTORCHA = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DoctorCha";
    public static final String CN_PATH_DOCTORCHA_PHOTO = CN_PATH_DOCTORCHA + "/DoctorChaPhoto/";
    public static final String CN_PATH_DOCTORCHA_VIDEO = CN_PATH_DOCTORCHA + "/DoctorChaVideo/";
    public static final String CN_PATH_DOCTORCHA_DOWNLOAD = CN_PATH_DOCTORCHA + "/DoctorChaDownload/";
    public static final String CN_PATH_DOCTORCHA_TEMP = CN_PATH_DOCTORCHA + "/DoctorChaTemp/";

    //private static final String CN_PATH_APPCONTENT = "http://infoline.iptime.org:8088/appcontent/carsgram/";
    private static final String CN_PATH_APPCONTENT = "http://doctorcha.co.kr:8088/appcontent/carsgram/";
    private static final String CN_PATH_IMAGE = CN_PATH_APPCONTENT + "image/";
    private static final String CN_PATH_VIDEO = CN_PATH_APPCONTENT + "video/";

    public enum EnumContentPath {
        //1. ContentPath id는 CarsGramRest의 UpladService()의 pathMap()의 key값과 반드시 일치시켜야 한다

        RESOURCE_COUNSELHELP_I(11, CN_PATH_IMAGE + "resource/counselhelp/"),
        MEMBER_I(12, CN_PATH_IMAGE + "member/"),

        CARINFO_I(14, CN_PATH_IMAGE + "carinfo/"),
        CONTENT_I(15, CN_PATH_IMAGE + "content/"),

        CHAT_V(21, CN_PATH_VIDEO + "chat/"),
        CONTENT_V(22, CN_PATH_VIDEO + "content/"),
        CONTENT_V_T(221, CN_PATH_VIDEO + "content/thumbnail/"),;;

        private int id;
        private String path;

        EnumContentPath(int id, String path) {;
            this.id = id;
            this.path = path;
        }

        public int getId() {
            return this.id;
        }
        public String getPath() {
            return this.path;
        }
    }

    //response subCode
    public static final int CN_SQLEXCEPTION_DUPLICATE = 1062;

    public enum EnumPreferAuth {
        //String
        id, //member table primarykey
        sr,  //security role
        hn, //handphone number
        nn, //nick name
        upw,
        ea, //e-mail
        ccu, //counsel channel url
        SAVED_GCMTOKEN_SERVER,
        SAVED_GCMTOKEN_SENDBIRD,
    }

    public enum EnumResponseCode {
        SUCCESS,
        FAILURE,
        EXCEPTION_FIRE,
        LOGIN_NOT_REG,       //회원 미가입
        LOGIN_NOT_DETAIL,   //간편 회원가입 상태 - 거래를 위해서는 실명인증 등 상세한 회원가입정보 필요
        LOGIN_INVALID_NN,
        LOGIN_INVALID_UPW;
    }

    //1. SendBird Sender type
    //2. enum으로 변경할 생각 하지마라. - overhead가 많이 생긴다
    //3. 값 변경하지마라. 좆된다
    //5. member table의 mt, sr과는 독립적이다
    public static final int CN_CHAT_ST_MEMBER = 0;
    public static final int CN_CHAT_ST_COUNSELLOR = 1;
    public static final int CN_CHAT_ST_PARTENER = 2;
    public static final int CN_CHAT_ST_ADMIN = 8;
    public static final int CN_CHAT_ST_TOPCOUNSELLOR = 9;

    //1. SendBird MessageType
    //2. enum으로 변경할 생각 하지마라. - overhead가 많이 생긴다
    //3. 값 변경하지마라. 좆된다
    //4. DB하고 동기화 된다 data="1,2"
    public static final int CN_CHAT_MT_TEXT = 1;
    public static final int CN_CHAT_MT_IMAGE = 2;
    public static final int CN_CHAT_MT_VIDEO = 3;
    public static final int CN_CHAT_MT_HTML = 4;
    public static final int CN_CHAT_MT_MAP = 5;
    public static final int CN_CHAT_MT_COMMAND = 6; //COMMEND_TYPE은 질문 Category와 동일하도록 조정한다
    public static final int CN_CHAT_MT_VIEW = 7; //VIEW_TYPE은 질문 Category와 동일하도록 조정한다
    public static final int CN_CHAT_MT_CONFIRM_REQUEST = 8;
    public static final int CN_CHAT_MT_CONFIRM_RESPONSE = 9;

    //1. SendBird Chat ViewHolder Type
    //2. enum으로 변경할 생각 하지마라. - overhead가 많이 생긴다
    public static final int CN_CHAT_VT_TEXT = 11;
    public static final int CN_CHAT_VT_TEXT_LEFT = 12;
    public static final int CN_CHAT_VT_TEXT_RIGHT = 13;
    public static final int CN_CHAT_VT_IMAGE = 21;
    public static final int CN_CHAT_VT_IMAGE_LEFT = 22;
    public static final int CN_CHAT_VT_IMAGE_RIGHT = 23;
    public static final int CN_CHAT_VT_VIDEO = 31;
    public static final int CN_CHAT_VT_VIDEO_LEFT = 32;
    public static final int CN_CHAT_VT_VIDEO_RIGHT = 33;
    public static final int CN_CHAT_VT_MAP_LEFT = 52;
    public static final int CN_CHAT_VT_MAP_RIGHT = 53;
    public static final int CN_CHAT_VT_COMMAND = 61;
    public static final int CN_CHAT_VT_VIEW = 71;
    public static final int CN_CHAT_VT_CONFIRM_REQUEST = 81;
    public static final int CN_CHAT_VT_CONFIRM_RESPONSE = 91;

    //channel kind - 1.일반 그룹채팅 챈널 2.멤버 vs 닥터차 상담전용 3.멤버 vs 닥터차, 파트너 상담 챈널(닥터차가 연결시킨) 4.멤버 vs 파트너(멤버가 직접 생성한 챈널)
    public static final int CN_CHAT_CK_GENERAL = 1;
    public static final int CN_CHAT_CK_DOCTORCHAR = 2;
    public static final int CN_CHAT_CK_DOCTORCHAR_PARTNER = 3;
    public static final int CN_CHAT_CK_PARTNER  = 4;

    /*
    public enum EnumSendBirdCounselState {
        STANDBY,    //응답해야할 상담챈널없는 상태
        RECIEVING, //회원이 질문 타이핑중 또는 해당 챈널 메세지 수신 또는 aDAPTER에 반영중
        SENDING,   //답변 타이핑중 - SEND 명령 수행즉시 해제
        WAITING,   //질문,답변 타이핑 없이 다음 답변을 기다리는 중
        SEARCHOPEND, //답변을 위한 자료 검색창 OPEN
        CHANNELLISTOPEN, //상담대기중인 CHANNEL LIST Activity open
    }
    */

    public enum EnumFragmentOpenType {
        READ_WRITE,    //자신의 정보 읽기/쓰기
        READ_ONLY,     //상담사, 파트너측에서 상담을 요청한 멤버가 전송한 정보를 열람
        SEND_READY,    //채팅창에서 ADMIN으로부터 특정 COMMAND 요청이 있을 경우 작성 또는 선택하여 즉시 전송버튼을 누를 수 있는 환경
    }

    public enum EnumSendBirdChannelListType {
        GROUP,        //공용 Group channel list
        COUNSEL,     //상담요원이 사용하는 상담대기 groupchannel list
        COUNSELLOR,
        PARTNER
    }

    public enum EnumGeneralMessage {
        //LOGIN_SUCCESS("로그인에 성공하였습니다"),
        LOGIN_REG_SUCCESS("감사합니다. 회원가입이 완료되었습니다"),
        LOGIN_SUCCESS("감사합니다. 로그인에 성공하였습니다"),;

        private String text;

        EnumGeneralMessage(String text) {;
            this.text = text;
        }

        public String getText() {
            return this.text;
        }
    }

    /*
    public static @DrawableRes int getOptionDrawableId(final int id) {
		final @DrawableRes int drawableId;


				drawableId = R.drawable.ic_option_leatherseat;
				drawableId = R.drawable.ic_option_vibrationseat;
				drawableId = R.drawable.ic_option_heaterseat;
				drawableId = R.drawable.ic_option_navi;
				drawableId = R.drawable.ic_option_sunroof;
				drawableId = R.drawable.ic_option_hipass;
				drawableId = R.drawable.ic_option_smartkey;
				drawableId = R.drawable.ic_option_cruisecontrol;
				drawableId = R.drawable.ic_option_airbag;
				drawableId = R.drawable.ic_option_backsensor;
				drawableId = R.drawable.ic_option_backcamera;
				drawableId = R.drawable.ic_option_tpms;
		}
     */

    public enum EnumCarOption {
        leatherseat(1, "가죽시트", R.drawable.ic_option_leatherseat),
        vibrationseat(2, "전동시트", R.drawable.ic_option_vibrationseat),
        heaterseat(3, "열선시트", R.drawable.ic_option_heaterseat),
        navi(4, "네비게이션", R.drawable.ic_option_navi),
        sunroof(5, "선루프", R.drawable.ic_option_sunroof),
        hipass(6, "하이패스룸미러", R.drawable.ic_option_hipass),
        smartkey(7, "스마트키", R.drawable.ic_option_smartkey),
        cruisecontrol(8, "크루즈컨트롤", R.drawable.ic_option_cruisecontrol),
        airbag(9, "에어백", R.drawable.ic_option_airbag),
        backsensor(10, "후방감지센서", R.drawable.ic_option_backsensor),
        backcamera(11, "후방카메라", R.drawable.ic_option_backcamera),
        option_tpms(12, "TPMS", R.drawable.ic_option_tpms),;

        private int id;
        private String nm;
        private @DrawableRes int resId;

        EnumCarOption(int id, String nm, @DrawableRes int resId) {
            this.id = id;
            this.nm = nm;
            this.resId = resId;
        }

        public int getId() {
            return this.id;
        }
        public String getNm() {
            return this.nm;
        }
        public @DrawableRes int getResId() {
            return this.resId;
        }
    }



    /*
        <string-array name="car_photo_area">
        <item>정면(번호판포함)</item>
        <item>후면(번호판포함)</item>
        <item>좌측면</item>
        <item>우측면</item>
        <item>실내</item>
        <item>계기판</item>
        <item>추가1</item>,
        <item>추가2</item>
        <item>추가3</item>
    </string-array>
     */

    public enum EnumMenuMyPage {
        MENU1("회원정보"),
        MENU2("차량정보"),
        MENU3("상담내용"),
        MENU4("단골업체"),;

        private String text;

        EnumMenuMyPage(String text) {
            this.text = text;
        }
        public String getText() {
            return this.text;
        }
    }

    //----------------------------------------------------------------------------------------------
    //정리 전
    public static final int CN_REQUEST_GALLARY = 900;
    public static final int CN_REQUEST_CARKIND = 901;
    public static final int CN_REQUEST_ADDRESS_BY_GPS = 902;
    public static final int CN_REQUEST_PICK_FILE = 903;
    public static final int CN_REQUEST_CHANNELLIST = 904;
    public static final int CN_REQUEST_SHOPLIST_FOR_CHAT = 905;
    public static final int CN_REQUEST_CARINFO = 906;
    public static final int CN_REQUEST_POSTEDIT = 907;
    public static final int CN_REQUEST_POST_FOR_CHAT = 908;
    public static final int CN_REQUEST_COUNSELLIST_FOR_CHAT = 909;

    public enum EnumExtraName {
        NAME1,
        NAME2,
        NAME3,
        NAME4,
        ANIM_START_POINT,
        ID,
        NN,
        GCM_BUNDLE,
        FRAFMENT_NAME,
        SHHOP_ID,
        TITLE,
        MENU4,
        ID_LIST,
        NickNameList;
    }

    public enum EnumTransitionName {
        THUMB,
    }

    /*
    public static final int ANIM_NONE = 0;
    public static final int ANIM_CUSTOM = 1;
    public static final int ANIM_SCALE_UP = 2;
    public static final int ANIM_THUMBNAIL_SCALE_UP = 3;
    public static final int ANIM_THUMBNAIL_SCALE_DOWN = 4;
    public static final int ANIM_SCENE_TRANSITION = 5;
    public static final int ANIM_DEFAULT = 6;
    public static final int ANIM_LAUNCH_TASK_BEHIND = 7;
    public static final int ANIM_THUMBNAIL_ASPECT_SCALE_UP = 8;
    public static final int ANIM_THUMBNAIL_ASPECT_SCALE_DOWN = 9;
    public static final int ANIM_CUSTOM_IN_PLACE = 10;
    public static final int ANIM_CLIP_REVEAL = 11;
    */

    public enum EnumActivityAnimType {
        ANIM_SCENE_TRANSITION,
        ANIM_THUMBNAIL_SCALE_UP,
        ANIM_THUMBNAIL_SCALE_DOWN,
        LEFTINRIGHTOUT
    }

    //수리정비 Section에 포함되는 MainCategories중 1개를 선택할 경우 실행되는 ShopsForRepairFragment에 포함되는 TabLayout 메뉴
    public enum ShopsForRepair {
        MENU1("업체목록"),
        MENU2("서비스사례"),
        MENU3("고객리뷰"),
        MENU4("이벤트"),
        MENU5("새소식"); //CABINET
        //MENU5("보관함"); //CABINET

        private String text;

        ShopsForRepair(String text) {
            this.text = text;
        }
        public String getText() {
            return this.text;
        }
    }

    //수리정비 Section에 포함되는 MainCategories중 1개를 선택할 경우 실행되는 ShopsForRepairFragment에 포함되는 TabLayout 메뉴
    public enum ShopActivityTabMenu {
        MENU1("업체정보"),
        MENU2("서비스사례"),
        MENU3("고객리뷰"),
        MENU4("이벤트"),
        MENU5("새소식"); //CABINET
        //MENU5("보관함"); //CABINET

        private String text;

        ShopActivityTabMenu(String text) {
            this.text = text;
        }
        public String getText() {
            return this.text;
        }
    }

    //1. ordinal()을 id갑으로 사용하므로 순서 자체가 id값이다 따라서 카테고리 확정이후에는 절대 순서를 바꾸면.안된다.
    public enum EnumQueryCategory {
        CATEGORY1("긴급 출동", R.drawable.ic_qc_01),
        CATEGORY2("사고 수리", R.drawable.ic_qc_02),
        CATEGORY3("타이어/휠", R.drawable.ic_qc_03),
        CATEGORY4("경정비", R.drawable.ic_qc_04),
        CATEGORY5("중고차 팔기", R.drawable.ic_qc_05),
        CATEGORY6("구매 도우미", R.drawable.ic_qc_06),
        CATEGORY7("검사/폐차", R.drawable.ic_qc_07),
        //CATEGORY8("오디오/썬틴/네비/블박", R.drawable.ic_query_09),
        CATEGORY8("차계부", R.drawable.ic_qc_08),
        CATEGORY9("기타 문의", R.drawable.ic_qc_09),;

        private String nm;
        private int res;

        EnumQueryCategory(String nm, @DrawableRes int res) {
            this.nm = nm;
            this.res = res;
        }

        public String getNm() {
            return this.nm;
        }
        public @DrawableRes int getRes() {
            return this.res;
        }
    }

    public enum EnumBmCategory {
        USEDCAR(1, "경정비", R.drawable.maincategory_tuning),
        RENTCAR(2, "타이어", R.drawable.maincategory_tire),
        GENERALREPAIR(3, "전문정비", R.drawable.maincategory_generalrepair),
        FACTORY(4, "정비공장", R.drawable.maincategory_accidentrepair),
        DENTRESTORE(5, "덴트", R.drawable.maincategory_dentrestore),
        POLISH(6, "복원", R.drawable.maincategory_etcrestore),
        SUNTINT(7, "유리", R.drawable.maincategory_glass),
        GLASS(8, "배터리KEY출장", R.drawable.maincategory_emergency),
        TIRE(9, "부품", R.drawable.maincategory_parts),
        GOODS(10, "용품", R.drawable.maincategory_navi),
        PARTS(11, "AV a/s", R.drawable.maincategory_audio),
        WASH(12, "검사", R.drawable.maincategory_check),
        EMERGENCY(13, "중고차", R.drawable.maincategory_usedcar),
        CHECK(14, "차도우미", R.drawable.maincategory_insurance),
        SCRAP(15, "폐차", R.drawable.maincategory_scrap),
        PARKING(16, "세차장", R.drawable.maincategory_wash);

        private int id;   //Category id
        private String nm; //category Name
        private int res;

        EnumBmCategory(int id, String nm, int res) {
            this.id = id;
            this.nm = nm;
            this.res = res;
        }

        public int getId() {
            return this.id;
        }

        public String getNm() {
            return this.nm;
        }

        public int getRes() {
            return this.res;
        }

        public static MainCons.EnumBmCategory getEnumById(int id) {
            MainCons.EnumBmCategory returnEnumBmCategory = null;

            for(int i = 0; i < 16; i++) {
                final MainCons.EnumBmCategory enumBmCategory = MainCons.EnumBmCategory.values()[i];

                if(id == enumBmCategory.getId()) {
                    returnEnumBmCategory = enumBmCategory;
                }
            }
            return returnEnumBmCategory;
        }
    }

    public enum EnumContentType {
        TEXT(1),
        PHOTO(2),
        VIDEO(3),
        MAP(4),
        LINK(5);

        private int value;

        EnumContentType(int value) {
            this.value = value;
        }
        public int getValue() {
            return this.value;
        }
    }

    public enum ViewHolderType {
        BIGIMAGE,
        THUMBNAIL,
        STAGGERED;
    }
}
