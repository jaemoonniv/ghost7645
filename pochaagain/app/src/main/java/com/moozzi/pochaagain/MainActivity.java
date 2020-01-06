package com.moozzi.pochaagain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.INVISIBLE;
import static com.moozzi.pochaagain.register.prefs;

public class MainActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler{
    DocumentSnapshot 불러오기;
    BillingProcessor bp;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    int 현재버전=0;

    Context context;
    String itemId;

    String 인앱아이디[]={"som_10","som_35","som_70","som_150","som_330","som_520","som_1000","som_2500","som_10000",};
//<*******설명***********>
    // 요리강화 성공시 현재정가*1.3 가격 = 현재정가*100;

    //저장변수=================================================================================================================================================================
   //★캐릭터= 강화단계,최소,최대
    //★요리 = 강화단계;
    //★ 마법제조기 = 마법제조기단계
    //★포장마차단계
   //소지금,은행잔고,명성,경험치,레벨,승점,솜,마케팅쿠폰,마법기계깡화쿠폰,요리강화쿠폰,강화보조제,강화보호제,
    //평균최소갯수(캐릭터구매최대갯수평균),평균최대갯수(캐릭터구매최대갯수평균),평균정가가격(50기준판매금평균치),크리티컬배수(마법제조기단계)
    int a=0,b=0,회차=0,처음접속=0;
    Date time;
    SimpleDateFormat format1,format2,format3;
    String 종료시간일,종료시간시,종료시간분,시작시간일,시작시간시,시작시간분;
    String 현재접속아이디 ="",종합고유키;
    long 소지금=30000,명성=0,경험치=0,은행잔고=0,입금액=0,입금가능액=0;
    int 레벨=1,승점=1000,포장마차단계=0,마법제조기단계=0;;
    int 마케팅쿠폰=0,마법기계강화쿠폰=0,요리강화쿠폰=0,강화보조제=0,강화보호제=0;
    int 솜=5,결투광고=0,결투도전권=10;
    int 결투장가능여부=0;

    //현재수량 포장마차단계 판매가 필요레벨 필요원재료1 필요원재료1수량 필요원재료2 필요원재료2수량 필요원재료3 필요원재료3수량 경험치 획득명성 강화
    int 요리[][]={
            {10,0,300,0,3,2,1,1,0,0,10,90,0},{10,0,360,0,3,2,2,1,0,0,12,110,0},{10,0,440,0,3,3,1,1,0,0,14,130,0},{10,0,530,0,3,3,2,1,1,2,17,160,0},{10,0,640,0,3,4,2,1,0,0,21,190,0},
            {0,2,770,10,4,1,1,4,0,0,25,230,0},{0,2,930,10,4,1,3,1,2,1,30,280,0},{0,2,1120,10,4,1,3,3,0,0,36,320,0},{0,2,1350,10,4,1,3,4,2,1,43,400,0},{0,2,3620,10,5,1,3,1,1,3,52,490,0},
            {0,3,1950,20,5,1,3,3,2,1,62,600,0},{0,3,2340,20,5,1,4,1,2,3,74,690,0},{0,3,2810,20,5,2,0,0,0,0,89,810,0},{0,3,3380,20,5,2,3,3,2,1,107,1030,0},{0,3,4060,20,5,2,4,1,3,3,128,1260,0},
            {0,4,4880,30,5,3,3,4,0,0,154,1480,0},{0,4,5860,30,5,4,3,1,0,0,185,1760,0},{0,4,7040,30,6,1,0,0,0,0,222,2040,0},{0,4,8450,30,6,1,4,1,3,4,266,2550,0},{0,4,10140,30,6,1,5,2,3,1,319,3040,0},
            {0,5,12170,40,6,1,5,3,4,1,383,3670,0},{0,5,14610,40,7,1,3,2,1,3,460,4380,0},{0,5,17540,40,7,1,5,2,2,5,552,5290,0},{0,5,21050,40,7,1,5,4,4,2,662,6050,0},{0,5,25260,40,7,1,6,1,5,3,795,7260,0},
            {0,6,30320,50,7,2,5,1,3,2,954,9120,0},{0,6,36390,50,7,3,6,1,4,1,1145,10890,0},{0,6,43670,50,7,3,4,1,2,1,1374,13120,0},{0,6,52410,50,7,3,6,1,4,3,1648,15910,0},{0,6,62900,50,7,4,5,4,1,3,1978,18870,0},
            {0,7,75480,60,8,1,5,3,0,0,2374,22480,0},{0,7,90580,60,8,1,7,1,5,3,2849,27580,0},{0,7,108700,60,8,1,7,2,6,1,3418,33700,0},{0,7,130440,60,8,1,7,4,4,3,4102,38940,0},{0,7,156530,60,9,1,7,1,0,0,4922,46530,0},
            {0,8,187840,70,9,1,7,3,4,3,5907,56340,0},{0,8,225410,70,9,1,8,1,7,1,7088,65410,0},{0,8,270500,70,9,1,8,1,7,4,8506,80500,0},{0,8,324600,70,9,2,7,2,6,1,10207,99600,0},{0,8,389520,70,9,2,8,1,7,2,12248,119520,0},
            {0,9,467430,80,9,3,7,2,6,1,14698,142430,0},{0,9,560920,80,9,4,0,0,0,0,17637,160920,0},{0,9,673110,80,9,4,8,1,7,2,21165,203110,0},{0,9,807740,80,10,1,8,1,6,3,25398,242740,0},{0,9,969290,80,10,1,8,3,7,3,30477,289290,0},
            {0,10,1163150,90,10,1,9,3,7,2,36573,343150,0},{0,10,1395780,90,10,1,9,4,8,1,43887,445780,0},{0,10,1674940,90,10,2,9,1,8,1,52665,524940,0}};
    //현재수량 가격
    int 재료[][]={{10000,10000},{10,10},{10,50},{10,100},{0,500},{0,1000},{0,5000},{0,10000},{0,50000},{0,100000},{0,500000}};
    //1.캐릭터 20명(이름/ 최소주문갯수증가 / 최대주문갯수증가 / 강화단계 /강화가격/강화증가량)
    long 캐릭터[][]={
            {0,0,0,0,5000,1},{1,0,0,0,10000,1}
            ,{2,0,0,0,15000,1},{3,0,0,0,20000,1}
            ,{4,1,0,0,30000,1},{5,0,1,0,40000,1}
            ,{6,1,1,0,50000,1},{7,1,1,0,60000,1}
            ,{8,2,1,0,80000,1},{9,1,2,0,100000,1}
            ,{10,2,2,0,120000,2},{11,2,2,0,140000,2}
            ,{12,3,2,0,200000,2},{13,2,3,0,300000,2}
            ,{14,3,3,0,400000,2},{15,3,3,0,500000,2}
            ,{16,3,4,0,1000000,2},{17,4,3,0,2000000,2}
            ,{18,4,5,0,3000000,2},{19,5,4,0,4000000,2}
            ,{20,5,5,0,10000000,3},{21,6,6,0,10000000,3}};
    //필요레벨,필요명성,가격,예금한도,구입갯수,손님출현률,요리속도,웨이팅,리워드보상,음식,캐릭터,인테리어
    int 포장마차[][]={
            {1,0,0,100000,5,15,6000,1,1400,4,3},
            {1,1000,10000,500000,6,14,5600,1,2800,4,3},
            {10,30000,300000,1000000,7,13,5200,2,4800,9,5},
            {19,150000,1500000,2000000,8,12,4800,3,9600,14,7},
            {28,500000,5000000,5000000,9,11,4400,4,19200,19,9},
            {39,2500000,25000000,10000000,10,10,4000,5,38400,24,11},
            {47,5000000,50000000,30000000,11,9,3600,6,76800,29,13},
            {57,10000000,100000000,50000000,12,8,3200,7,153600,34,15},
            {69,30000000,300000000,100000000,13,7,2800,8,307200,39,17},
            {84,50000000,500000000,300000000,14,6,2400,9,614400,44,19},
            {97,200000000,2000000000,500000000,15,5,2000,10,1228800,47,21},
    };
    //공통변수=================================================================================================================================================================

    FirebaseFirestore 유저정보;
    CollectionReference 유저;
    String 적아이디[]=new String[4],적아이디쓰;
    LottieAnimationView 로티레이[]=new LottieAnimationView[10];
    RewardedAd rewardedAd;
    int 화질=0;
    int 적아이디카운트=0;
    int 경제지수=0,날씨지수=0,경험치이벤트=0,명성이벤트=0,판매금이벤트=0,현재선택요리=0,이전선택요리=0,현재줄요리=0,이전줄요리=0,현재재료=0,이전재료=0,현재보상=-1,현재보상수량,이벤트번호=-1,보상상태=0;
    int 요리하락=1,요리보조=1,제조기하락=1,제조기보조=1,캐릭터하락=1,캐릭터보조=1;
    int 요리남은수량=0,현재만들고있는요리,요리중=0;
    int 터치순서=0;
    int 주문음식[]=new int[3],주문갯수[]=new int[3],종류,대기손님=0,판매중=0;
    int 확률[]={100,90,80,70,60,50,40,30,20,10,5,4,3,2,1,1,1,1};
    int 마법제조기생산[]={1,2,3,4,5,6,7,8,9,12,14,16,18,20,25,30};
    int 인앱가격[]={1100,3300,5500,11000,22000,33000,55000,110000,330000};
    int 인앱수량[]={10,35,70,150,330,520,1000,2500,10000};
    long 캐쉬가격수량[][]={{100000,10},{500000,35},{2500000,70},{12500000,150},{62500000,330},{321500000,520},{1562500000,1000},{7812500000l,2500},{1,10},{1,30},{1,10},{11,90},{11,270},{11,90},{110,800},{110,2400},{110,800},{1,30},{11,300},{1,50},{11,500}};
    long 마법제조기강화비용[]={100000,300000,500000,1000000,3000000,5000000,10000000,30000000,50000000,100000000,300000000,500000000,1000000000l,3000000000l,5000000000l};
    long 캐릭터강화비용[][]=new long[22][16];
    long 요리가격[][]=new long[48][16];
    long 요리강화비용[][]=new long[48][16];
    long 레벨별[][]={
            {0,0},
            {100,2},{120,2},{144,3},{173,3},{207,4},{249,4},{299,5},{358,5},{430,6},{516,6},
            {619,7},{743,8},{892,10},{1070,12},{1284,13},{1541,15},{1849,17},{2219,19},{2662,21},{3195,23},
            {3834,26},{4601,28},{5521,31},{6625,34},{7950,37},{9540,40},{11448,43},{13737,47},{16484,51},{19781,55},
            {23738,59},{28485,64},{34182,69},{41019,74},{49222,79},{59067,85},{70880,91},{85056,97},{102067,104},{122481,112},
            {146977,119},{176373,127},{211647,145},{253977,155},{304772,165},{365726,176},{438871,188},{526646,200},{631975,213},{758370,227},
            {910044,241},{1092053,257},{1310463,273},{1572556,291},{1887067,309},{2264480,329},{2717376,349},{3260852,371},{3913022,395},{4695626,419},
            {5634751,445},{6761702,473},{8114042,503},{9736850,534},{11684221,567},{14021065,602},{16825278,639},{20190333,678},{24228400,720},{29074080,764},
            {34888896,811},{41866675,861},{50240010,913},{60288012,969},{72345614,1028},{86814737,1091},{104177684,1157},{125013221,1228},{150015865,1302},{180019039,1302},
            {216022846,1381},{259227415,1465},{311072899,1554},{373287478,1649},{447944974,1748},{537533969,1854},{645040762,1967},{774048915,2086},{928858698,2212},{1114630437,2345},
            {1337556525,2487},{1605067830,2637},{1926081396,2797},{2311297675l,2965},{2773557210l,3144},{3328268652l,3334},{3993922382l,3535},{4792706859l,3748},{5751248231l,3974},{6901497877l,4214}};

    String 공지사항="없음";
    String 인앱이름[]={"매우 작은 솜뭉치","작은 솜뭉치","솜뭉치","빵빵한 솜뭉치","매우 빵빵한 솜뭉치","무거운 솜뭉치","매우 무거운 솜뭉치"," 들수없는 솜뭉치","겉잡을수 없는 솜뭉치"};
    String 인앱설명[]={"너무 작아 잘 보이지 않는다.","들고 있는 느낌도 없다.","적당한 양의 솜뭉치다","부피가 커진 솜뭉치다.","부피가 많이 커진 솜뭉치다.","솜이지만 무거울 정도이다.","솜이지만 들고 가기 힘들다.","들고 갈수 없이 많은 양의 솜이다","혼자서 가지고 가는 것은 무리다."};
    String 요리이름[]={
            "클로버계란","토마토꽃","붉은물두부","하이케익","아보드랍",
            "붉은물수프","새우잠","발자국크래커","스프링토마토","굴에사는레몬",
            "보름달또디아","해골갈비","아크나초", "콩알핫바","나무타르트",
            "탬버린베이글","피자콤비","수상한알밥","책책버거","눈알셀러드",
            "연어통구이","발자국파스타","뼈감자튀김","만화고기","눈알스테이크",
            "지옥온천수프","양탄자계란","콩알스테이크","모듬꼬치","꼬깔콘",
            "통나무핫도그","총알당근퐁듀","불고기드랍","진한고기수프","손가락파스타",
            "만화핫도그","바닷속보석","나무수프","광대또띠아","잠자는연어",
            "손톱수프","빗자루타르트","좀비미트볼","매운뼈스틱","해골스테이크",
            "머리없는통닭","좀비스테이크","손가락튀김"};
    String 재료이름[]={"없음","주황시약","검정시약","빨간시약","초록시약","민트시약","보라시약","버섯시약","눈깔시약","손톱시약","왕눈깔시약"};
    String 캐릭터이름[]={"유령뭉치","좀비분장 소년","오늘 생일","해리포터","저승사자","나무컨셉 아저씨","짱이","옆동네 레이스","거꾸로 박쥐","똑바로 박쥐","호박머리","억울한 스크림","뭉치","이상한 유령","짱이 친구","분장한 제비","이빨뽑은 유령","진진킹","문문킹","필라테스 호박","그 것","왓"};
    String 캐릭터대사[]={"여기가 바로 "+'\n'+"유령포차 인가요??","안녕하세요!!"+'\n'+" 너무 맛있어보여요!!","사장님 얼굴은"+'\n'+" 좀비분장하신건가요??","맛있는 냄새...."+'\n'+" 얼른 먹고싶어요!!","제 친구 무찌는 "+'\n'+"대형무민이에요!!","알고 계시나요 ??"+'\n'+" 무뭉은 전설속 "+'\n'+"괴물이래요",
                         "날씨가 몹시 쌀쌀하네요ㅠ_ㅠ","동네 분위기가 "+'\n'+" 너무 으스스해요","오늘은 신나는 "+'\n'+"할로윈파티라구요!","무뎅이는 "+'\n'+"솜이 없어요ㅠㅠ","무피는 우연히 "+'\n'+"만난 아이에요!!",
                         "짱이와 진진킹은 "+'\n'+"친구래요","짱이네 가게는 "+'\n'+"아이템을 솜으로 "+'\n'+"바꿔준데요!!","은행잔고가"+'\n'+" 넉넉하신가요??","미스터쉐프에서"+'\n'+"명성을 떨치면"+'\n'+"솜을 줘요!!","요리학원은 "+'\n'+"다니고 계신가요??"};
    String 마법제조기이름[]={"고장난 마법기계","부숴진 마법기계","낡은 마법기계","허름한 마법기계","쓸만한 마법기계","뛰어난 마법기계","광택나는 마법기계","찬란한 마법기계","최고의 마법기계","최고의 마법기계+1","최고의 마법기계+2","최고의 마법기계+3","최고의 마법기계+4","최고의 마법기계+5","최고의 마법기계+6","최고의 마법기계+7"};
    String 캐쉬이름[]={"매우 작은 돈주머니","작은 돈주머니","돈주머니","두둑한 돈주머니","무거운 돈주머니",
            "매우 무거운 돈주머니","가득찬 돈주머니","흘러넘치는 돈주머니",
            "마케팅쿠폰 x1","마법기계강화쿠폰 x1","요리교실수강권 x1","마케팅쿠폰 x11","마법기계강화쿠폰 x11","요리교실수강권 x11",
            "마케팅쿠폰 x110","마법기계강화쿠폰 x110","요리교실수강권 x110","강화보조제 x1","강화보조제 x11","강화보호제 x1","강화보호제 x11"};
    String 캐쉬설명[]={"그저 그런 \n양의 돈이다","조금 넉넉한 \n양의 돈이다","넉넉한 양의\n 돈이다","아주 넉넉한 양의 \n돈이다","많은 양의\n 돈이다"
            ,"아주 많은 \n양의 돈이다","잔고를 가득\n채울 만한 돈이다","잔고가 흘러 \n넘치는 만한 돈이다","캐릭터를 무료로\n강화할수 있는 쿠폰",
            "마법기계를 무료로\n강화할수 있는 쿠폰","요리를 무료로\n강화할수 있는 쿠폰","캐릭터를 무료로\n강화할수 있는 쿠폰",
            "마법기계를 무료로\n강화할수 있는 쿠폰","요리를 무료로\n강화할수 있는 쿠폰","캐릭터를 무료로\n강화할수 있는 쿠폰",
            "마법기계를 무료로\n강화할수 있는 쿠폰","요리를 무료로\n강화할수 있는 쿠폰","강화확률을\n2배 올려준다","강화확률을\n2배 올려준다"
            ,"강화 실패시\n하락을 방지한다","강화 실패시\n하락을 방지한다"};
    //공통id=================================================================================================================================================================
    int 애니메이션id[]={R.anim.allani,R.anim.alpha1,R.anim.alpha2,R.anim.tran1,R.anim.tran2,R.anim.size3};
    int 오븐fhd[]={
            R.drawable.ovenfhd,R.drawable.ovenfhd,R.drawable.ovenfhd,R.drawable.oven1fhd,R.drawable.oven1fhd,
            R.drawable.oven1fhd,R.drawable.oven2fhd,R.drawable.oven2fhd,R.drawable.oven2fhd,R.drawable.oven3fhd,
            R.drawable.oven3fhd,R.drawable.oven3fhd,R.drawable.oven4fhd,R.drawable.oven4fhd,R.drawable.oven4fhd,R.drawable.oven4fhd,
    };
    int 오븐hd[]={
            R.drawable.ovenhd,R.drawable.ovenhd,R.drawable.ovenhd,R.drawable.oven1hd,R.drawable.oven1hd,
            R.drawable.oven1hd,R.drawable.oven2hd,R.drawable.oven2hd,R.drawable.oven2hd,R.drawable.oven3hd,
            R.drawable.oven3hd,R.drawable.oven3hd,R.drawable.oven4hd,R.drawable.oven4hd,R.drawable.oven4hd,R.drawable.oven4hd
    };
    int 오븐sd[]={
            R.drawable.ovensd,R.drawable.ovensd,R.drawable.ovensd,R.drawable.oven1sd,R.drawable.oven1sd,
            R.drawable.oven1sd,R.drawable.oven2sd,R.drawable.oven2sd,R.drawable.oven2sd,R.drawable.oven3sd,
            R.drawable.oven3sd,R.drawable.oven3sd,R.drawable.oven4sd,R.drawable.oven4sd,R.drawable.oven4sd,R.drawable.oven4sd
    };
    int 캐릭터이미지fhd[]={
            R.drawable.char1fhd,R.drawable.char2fhd,R.drawable.char3fhd,R.drawable.char4fhd,R.drawable.char5fhd,
            R.drawable.char6fhd,R.drawable.char7fhd,R.drawable.char8fhd,R.drawable.char9fhd,R.drawable.char10fhd,
            R.drawable.char11fhd,R.drawable.char12fhd,R.drawable.char13fhd,R.drawable.char14fhd,R.drawable.char15fhd,
            R.drawable.char16fhd,R.drawable.char17fhd,R.drawable.char18fhd,R.drawable.char19fhd,R.drawable.char20fhd,
            R.drawable.char21fhd,R.drawable.char22fhd,
    };
    int 캐릭터이미지hd[]={
            R.drawable.char1hd,R.drawable.char2hd,R.drawable.char3hd,R.drawable.char4hd,R.drawable.char5hd,
            R.drawable.char6hd,R.drawable.char7hd,R.drawable.char8hd,R.drawable.char9hd,R.drawable.char10hd,
            R.drawable.char11hd,R.drawable.char12hd,R.drawable.char13hd,R.drawable.char14hd,R.drawable.char15hd,
            R.drawable.char16hd,R.drawable.char17hd,R.drawable.char18hd,R.drawable.char19hd,R.drawable.char20hd,
            R.drawable.char21hd,R.drawable.char22hd,
    };
    int 캐릭터이미지sd[]={
            R.drawable.char1sd,R.drawable.char2sd,R.drawable.char3sd,R.drawable.char4sd,R.drawable.char5sd,
            R.drawable.char6sd,R.drawable.char7sd,R.drawable.char8sd,R.drawable.char9sd,R.drawable.char10sd,
            R.drawable.char11sd,R.drawable.char12sd,R.drawable.char13sd,R.drawable.char14sd,R.drawable.char15sd,
            R.drawable.char16sd,R.drawable.char17sd,R.drawable.char18sd,R.drawable.char19sd,R.drawable.char20sd,
            R.drawable.char21sd,R.drawable.char22sd,
    };
    int 요리이미지fhd[]={
            R.drawable.fooda1fhd,R.drawable.fooda2fhd,R.drawable.fooda3fhd,R.drawable.fooda4fhd,R.drawable.fooda5fhd,
            R.drawable.fooda6fhd,R.drawable.fooda7fhd,R.drawable.fooda8fhd,R.drawable.fooda9fhd,R.drawable.fooda10fhd,
            R.drawable.fooda11fhd,R.drawable.fooda12fhd,R.drawable.fooda13fhd,R.drawable.fooda14fhd,R.drawable.fooda15fhd,
            R.drawable.fooda16fhd,R.drawable.fooda17fhd,R.drawable.fooda18fhd,R.drawable.fooda19fhd,R.drawable.fooda20fhd,
            R.drawable.fooda21fhd,R.drawable.fooda22fhd,R.drawable.fooda23fhd,R.drawable.fooda24fhd,R.drawable.fooda25fhd,
            R.drawable.fooda26fhd,R.drawable.fooda27fhd,R.drawable.fooda28fhd,R.drawable.fooda29fhd,R.drawable.fooda30fhd,
            R.drawable.fooda31fhd,R.drawable.fooda32fhd,R.drawable.fooda33fhd,R.drawable.fooda34fhd,R.drawable.fooda35fhd,
            R.drawable.fooda36fhd,R.drawable.fooda37fhd,R.drawable.fooda38fhd,R.drawable.fooda39fhd,R.drawable.fooda40fhd,
            R.drawable.fooda41fhd,R.drawable.fooda42fhd,R.drawable.fooda43fhd,R.drawable.fooda44fhd,R.drawable.fooda45fhd,
            R.drawable.fooda46fhd,R.drawable.fooda47fhd,R.drawable.fooda48fhd};
    int 요리이미지hd[]={
            R.drawable.fooda1hd,R.drawable.fooda2hd,R.drawable.fooda3hd,R.drawable.fooda4hd,R.drawable.fooda5hd,
            R.drawable.fooda6hd,R.drawable.fooda7hd,R.drawable.fooda8hd,R.drawable.fooda9hd,R.drawable.fooda10hd,
            R.drawable.fooda11hd,R.drawable.fooda12hd,R.drawable.fooda13hd,R.drawable.fooda14hd,R.drawable.fooda15hd,
            R.drawable.fooda16hd,R.drawable.fooda17hd,R.drawable.fooda18hd,R.drawable.fooda19hd,R.drawable.fooda20hd,
            R.drawable.fooda21hd,R.drawable.fooda22hd,R.drawable.fooda23hd,R.drawable.fooda24hd,R.drawable.fooda25hd,
            R.drawable.fooda26hd,R.drawable.fooda27hd,R.drawable.fooda28hd,R.drawable.fooda29hd,R.drawable.fooda30hd,
            R.drawable.fooda31hd,R.drawable.fooda32hd,R.drawable.fooda33hd,R.drawable.fooda34hd,R.drawable.fooda35hd,
            R.drawable.fooda36hd,R.drawable.fooda37hd,R.drawable.fooda38hd,R.drawable.fooda39hd,R.drawable.fooda40hd,
            R.drawable.fooda41hd,R.drawable.fooda42hd,R.drawable.fooda43hd,R.drawable.fooda44hd,R.drawable.fooda45hd,
            R.drawable.fooda46hd,R.drawable.fooda47hd,R.drawable.fooda48hd};
    int 요리이미지sd[]={
            R.drawable.fooda1sd,R.drawable.fooda2sd,R.drawable.fooda3sd,R.drawable.fooda4sd,R.drawable.fooda5sd,
            R.drawable.fooda6sd,R.drawable.fooda7sd,R.drawable.fooda8sd,R.drawable.fooda9sd,R.drawable.fooda10sd,
            R.drawable.fooda11sd,R.drawable.fooda12sd,R.drawable.fooda13sd,R.drawable.fooda14sd,R.drawable.fooda15sd,
            R.drawable.fooda16sd,R.drawable.fooda17sd,R.drawable.fooda18sd,R.drawable.fooda19sd,R.drawable.fooda20sd,
            R.drawable.fooda21sd,R.drawable.fooda22sd,R.drawable.fooda23sd,R.drawable.fooda24sd,R.drawable.fooda25sd,
            R.drawable.fooda26sd,R.drawable.fooda27sd,R.drawable.fooda28sd,R.drawable.fooda29sd,R.drawable.fooda30sd,
            R.drawable.fooda31sd,R.drawable.fooda32sd,R.drawable.fooda33sd,R.drawable.fooda34sd,R.drawable.fooda35sd,
            R.drawable.fooda36sd,R.drawable.fooda37sd,R.drawable.fooda38sd,R.drawable.fooda39sd,R.drawable.fooda40sd,
            R.drawable.fooda41sd,R.drawable.fooda42sd,R.drawable.fooda43sd,R.drawable.fooda44sd,R.drawable.fooda45sd,
            R.drawable.fooda46sd,R.drawable.fooda47sd,R.drawable.fooda48sd};
    int 재료이미지fhd[]={
            R.drawable.water1fhd,R.drawable.water2fhd,R.drawable.water3fhd,R.drawable.water4fhd,R.drawable.water5fhd,
            R.drawable.water6fhd,R.drawable.water7fhd,R.drawable.water8fhd,R.drawable.water9fhd,R.drawable.water10fhd};
    int 재료이미지hd[]={
            R.drawable.water1hd,R.drawable.water2hd,R.drawable.water3hd,R.drawable.water4hd,R.drawable.water5hd,
            R.drawable.water6hd,R.drawable.water7hd,R.drawable.water8hd,R.drawable.water9hd,R.drawable.water10hd};
    int 재료이미지sd[]={
            R.drawable.water1sd,R.drawable.water2sd,R.drawable.water3sd,R.drawable.water4sd,R.drawable.water5sd,
            R.drawable.water6sd,R.drawable.water7sd,R.drawable.water8sd,R.drawable.water9sd,R.drawable.water10sd};
    int 메뉴버튼이미지fhd[]={
            R.drawable.cartfhd,R.drawable.lightfhd,R.drawable.cheffhd,R.drawable.machinefhd,R.drawable.megaphonefhd,
            R.drawable.bankfhd,R.drawable.fightfhd,R.drawable.giftbox4fhd};
    int 메뉴버튼이미지hd[]={
            R.drawable.carthd,R.drawable.lighthd,R.drawable.chefhd,R.drawable.machinehd,R.drawable.megaphonehd,
            R.drawable.bankhd,R.drawable.fighthd,R.drawable.giftbox4hd};
    int 메뉴버튼이미지sd[]={
            R.drawable.cartsd,R.drawable.lightsd,R.drawable.chefsd,R.drawable.machinesd,R.drawable.megaphonesd,
            R.drawable.banksd,R.drawable.fightsd,R.drawable.giftbox4sd};
    int 구글버튼이미지fhd[]={
            R.drawable.rewardfhd,R.drawable.inappfhd,R.drawable.salefhd,R.drawable.inforfhd,R.drawable.qustionfhd};
    int 구글버튼이미지hd[]={
            R.drawable.rewardhd,R.drawable.inapphd,R.drawable.salehd,R.drawable.inforhd,R.drawable.qustionhd};
    int 구글버튼이미지sd[]={
            R.drawable.rewardsd,R.drawable.inappsd,R.drawable.salesd,R.drawable.inforsd,R.drawable.qustionsd};
    int 솜이미지fhd[]={
            R.drawable.som1fhd,R.drawable.som2fhd,R.drawable.som3fhd,R.drawable.som4fhd,R.drawable.som5fhd,
            R.drawable.som6fhd,R.drawable.som7fhd,R.drawable.som8fhd,R.drawable.som9fhd};
    int 솜이미지hd[]={
            R.drawable.som1hd,R.drawable.som2hd,R.drawable.som3hd,R.drawable.som4hd,R.drawable.som5hd,
            R.drawable.som6hd,R.drawable.som7hd,R.drawable.som8hd,R.drawable.som9hd};
    int 솜이미지sd[]={
            R.drawable.som1sd,R.drawable.som2sd,R.drawable.som3sd,R.drawable.som4sd,R.drawable.som5sd,
            R.drawable.som6sd,R.drawable.som7sd,R.drawable.som8sd,R.drawable.som9sd};
    int 보상이미지fhd[]={
            R.drawable.giftbox1fhd,R.drawable.giftbox2fhd,R.drawable.giftbox3fhd,R.drawable.somdollfhd,R.drawable.openboxfhd};
    int 보상이미지hd[]={
            R.drawable.giftbox1hd,R.drawable.giftbox2hd,R.drawable.giftbox3hd,R.drawable.somdollhd,R.drawable.openboxhd};
    int 보상이미지sd[]={
            R.drawable.giftbox1sd,R.drawable.giftbox2sd,R.drawable.giftbox3sd,R.drawable.somdollsd,R.drawable.openboxsd};
    int  캐쉬이미지fhd[]={
            R.drawable.coinfhd,R.drawable.coin1fhd,R.drawable.coin2fhd,R.drawable.coin3fhd,R.drawable.coin4fhd,
            R.drawable.coin5fhd,R.drawable.coin6fhd,R.drawable.coin7fhd,R.drawable.couponfhd,R.drawable.coupon1fhd,
            R.drawable.coupon2fhd,R.drawable.couponfhd,R.drawable.coupon1fhd,
            R.drawable.coupon2fhd,R.drawable.couponfhd,R.drawable.coupon1fhd,
            R.drawable.coupon2fhd,R.drawable.ganghwabohofhd,R.drawable.ganghwabohofhd,R.drawable.ganghwabojofhd,R.drawable.ganghwabojofhd
    };
    int  캐쉬이미지hd[]={
            R.drawable.coinhd,R.drawable.coin1hd,R.drawable.coin2hd,R.drawable.coin3hd,R.drawable.coin4hd,
            R.drawable.coin5hd,R.drawable.coin6hd,R.drawable.coin7hd,R.drawable.couponhd,R.drawable.coupon1hd,
            R.drawable.coupon2hd,R.drawable.couponhd,R.drawable.coupon1hd,
            R.drawable.coupon2hd,R.drawable.couponhd,R.drawable.coupon1hd,
            R.drawable.coupon2hd,R.drawable.ganghwabohohd,R.drawable.ganghwabohohd,R.drawable.ganghwabojohd,R.drawable.ganghwabojohd
    };
    int  캐쉬이미지sd[]={
            R.drawable.coinsd,R.drawable.coin1sd,R.drawable.coin2sd,R.drawable.coin3sd,R.drawable.coin4sd,
            R.drawable.coin5sd,R.drawable.coin6sd,R.drawable.coin7sd,R.drawable.couponsd,R.drawable.coupon1sd,
            R.drawable.coupon2sd,R.drawable.couponsd,R.drawable.coupon1sd,
            R.drawable.coupon2sd,R.drawable.couponsd,R.drawable.coupon1sd,
            R.drawable.coupon2sd,R.drawable.ganghwabohosd,R.drawable.ganghwabohosd,R.drawable.ganghwabojosd,R.drawable.ganghwabojosd
    };
    int 포장마차이미지fhd[]={
            R.drawable.intelier0fhd,R.drawable.intelier1fhd,R.drawable.intelier2fhd,R.drawable.intelier3fhd,R.drawable.intelier4fhd,
            R.drawable.intelier5fhd,R.drawable.intelier6fhd,R.drawable.intelier7fhd,R.drawable.intelier8fhd,R.drawable.intelier9fhd,R.drawable.intelier9fhd,
    };
    int 포장마차이미지hd[]={
            R.drawable.intelier0hd,R.drawable.intelier1hd,R.drawable.intelier2hd,R.drawable.intelier3hd,R.drawable.intelier4hd,
            R.drawable.intelier5hd,R.drawable.intelier6hd,R.drawable.intelier7hd,R.drawable.intelier8hd,R.drawable.intelier9hd,R.drawable.intelier9hd,
    };
    int 포장마차이미지sd[]={
            R.drawable.intelier0sd,R.drawable.intelier1sd,R.drawable.intelier2sd,R.drawable.intelier3sd,R.drawable.intelier4sd,
            R.drawable.intelier5sd,R.drawable.intelier6sd,R.drawable.intelier7sd,R.drawable.intelier8sd,R.drawable.intelier9sd,R.drawable.intelier9sd,
    };
    //튜토리얼변수
    FrameLayout 튜토리얼레이,튜토리얼버튼레이;
    Button 튜토리얼다음,튜토리얼이전,튜토리얼나가기;
    Switch 튜토리얼체크;
    ImageView 튜토리얼이미지;
    int 튜토이미지id[]={R.drawable.tt2,R.drawable.tt3,R.drawable.tt4,R.drawable.tt5,},튜토이미지번호=0;
    //메인변수=================================================================================================================================================================
    LinearLayout 상위,상단,하단,경험치레이아웃,요리하기레이아웃,로딩화면레이아웃;
    static FrameLayout 최상위,중단,손님레이아웃,말풍선레이아웃,레이[]=new FrameLayout[14],터치존,퀵슬롯레이[]=new FrameLayout[48],레이인포레이,레이도움말레이;
    ProgressBar 로딩프로그레스,경험치프로그레스;
    ScrollView 메뉴스크롤,캐쉬스크롤;
    HorizontalScrollView 퀵슬롯스크롤;
    TextView 경험치텍스트,레벨텍스트,돈텍스트,명성텍스트,경제지수텍스트,날씨지수텍스트,손님이름텍스트,터치유도텍스트,말풍선텍스트,대기손님텍스트,
             시스템텍스트,시스템텍스트2,생산텍스트,로딩텍스트,로딩멘트텍스트,주문텍스트[]=new TextView[3],메인레이정보텍스트[]=new TextView[10];
    ImageView 손님이미지,요리하기버튼이미지,말풍선이미지,배경이미지;
     static Button 요리하기버튼,구글버튼[]=new Button[5],메뉴버튼[]=new Button[10],퀵슬롯버튼[]=new Button[48];
    int 광고봄=0;
    int 메인id[][]={
                   {R.id.주문1텍스트,R.id.주문2텍스트,R.id.주문3텍스트},
                   {R.id.구글버튼1,R.id.구글버튼2,R.id.구글버튼3},
                   {R.id.메뉴버튼1,R.id.메뉴버튼2,R.id.메뉴버튼3,R.id.메뉴버튼4,R.id.메뉴버튼5,R.id.메뉴버튼6,R.id.메뉴버튼7,R.id.메뉴버튼8},
                   {R.id.퀵슬롯버튼1,R.id.퀵슬롯버튼2,R.id.퀵슬롯버튼3,R.id.퀵슬롯버튼4,R.id.퀵슬롯버튼5,
                    R.id.퀵슬롯버튼6,R.id.퀵슬롯버튼7,R.id.퀵슬롯버튼8,R.id.퀵슬롯버튼9,R.id.퀵슬롯버튼10,
                    R.id.퀵슬롯버튼11,R.id.퀵슬롯버튼12,R.id.퀵슬롯버튼13,R.id.퀵슬롯버튼14,R.id.퀵슬롯버튼15,
                    R.id.퀵슬롯버튼16,R.id.퀵슬롯버튼17,R.id.퀵슬롯버튼18,R.id.퀵슬롯버튼19,R.id.퀵슬롯버튼20,
                    R.id.퀵슬롯버튼21,R.id.퀵슬롯버튼22,R.id.퀵슬롯버튼23,R.id.퀵슬롯버튼24,R.id.퀵슬롯버튼25,
                    R.id.퀵슬롯버튼26,R.id.퀵슬롯버튼27,R.id.퀵슬롯버튼28,R.id.퀵슬롯버튼29,R.id.퀵슬롯버튼30,
                    R.id.퀵슬롯버튼31,R.id.퀵슬롯버튼32,R.id.퀵슬롯버튼33,R.id.퀵슬롯버튼34,R.id.퀵슬롯버튼35,
                    R.id.퀵슬롯버튼36,R.id.퀵슬롯버튼37,R.id.퀵슬롯버튼38,R.id.퀵슬롯버튼39,R.id.퀵슬롯버튼40,
                    R.id.퀵슬롯버튼41,R.id.퀵슬롯버튼42,R.id.퀵슬롯버튼43,R.id.퀵슬롯버튼44,R.id.퀵슬롯버튼45,
                    R.id.퀵슬롯버튼46,R.id.퀵슬롯버튼47,R.id.퀵슬롯버튼48,},
                   {R.id.레이1,R.id.레이2,R.id.레이3,R.id.레이4,R.id.레이5,
                    R.id.레이6,R.id.레이7,R.id.레이8,R.id.레이9,R.id.레이10,
                    R.id.레이11,R.id.레이12,R.id.레이13,R.id.레이14},
                   {R.id.메인정보텍스트1,R.id.메인정보텍스트2,R.id.메인정보텍스트3,R.id.메인정보텍스트4,R.id.메인정보텍스트5,
                    R.id.메인정보텍스트6,R.id.메인정보텍스트7,R.id.메인정보텍스트8,R.id.메인정보텍스트9,R.id.메인정보텍스트10},
                   {R.id.퀵슬롯레이1,R.id.퀵슬롯레이2,R.id.퀵슬롯레이3,R.id.퀵슬롯레이4,R.id.퀵슬롯레이5,
                    R.id.퀵슬롯레이6,R.id.퀵슬롯레이7,R.id.퀵슬롯레이8,R.id.퀵슬롯레이9,R.id.퀵슬롯레이10,
                    R.id.퀵슬롯레이11,R.id.퀵슬롯레이12,R.id.퀵슬롯레이13,R.id.퀵슬롯레이14,R.id.퀵슬롯레이15,
                    R.id.퀵슬롯레이16,R.id.퀵슬롯레이17,R.id.퀵슬롯레이18,R.id.퀵슬롯레이19,R.id.퀵슬롯레이20,
                    R.id.퀵슬롯레이21,R.id.퀵슬롯레이22,R.id.퀵슬롯레이23,R.id.퀵슬롯레이24,R.id.퀵슬롯레이25,
                    R.id.퀵슬롯레이26,R.id.퀵슬롯레이27,R.id.퀵슬롯레이28,R.id.퀵슬롯레이29,R.id.퀵슬롯레이30,
                    R.id.퀵슬롯레이31,R.id.퀵슬롯레이32,R.id.퀵슬롯레이33,R.id.퀵슬롯레이34,R.id.퀵슬롯레이35,
                    R.id.퀵슬롯레이36,R.id.퀵슬롯레이37,R.id.퀵슬롯레이38,R.id.퀵슬롯레이39,R.id.퀵슬롯레이40,
                    R.id.퀵슬롯레이41,R.id.퀵슬롯레이42,R.id.퀵슬롯레이43,R.id.퀵슬롯레이44,R.id.퀵슬롯레이45,
                    R.id.퀵슬롯레이46,R.id.퀵슬롯레이47,R.id.퀵슬롯레이48}};
    //레이1변수=================================================================================================================================================================
    LinearLayout 레이1상위,레이1요리정보레이[]=new LinearLayout[48],레이1요리재료레이[]=new LinearLayout[48],요리스크롤[]=new LinearLayout[48];
    FrameLayout 레이1요리이미지레이[]=new FrameLayout[48],레이1요리버튼레이[]=new FrameLayout[48];
    Button 레이1나가기,레이1작동버튼,레이1선택버튼[]=new Button[48];
    ImageView 레이1요리이미지[]=new ImageView[48];
    TextView 레이1식바수량텍스트,레이1요리이름텍스트[]=new TextView[48],레이1요리가격텍스트[]=new TextView[48],레이1요리마진텍스트[]=new TextView[48],
            레이1요리재료1[]=new TextView[48],레이1요리재료2[]=new TextView[48], 레이1요리재료3[]=new TextView[48];
    SeekBar 레이1식바;
    int 레이1id[][]={
                   {R.id.레이1요리1정보레이,R.id.레이1요리2정보레이,R.id.레이1요리3정보레이,R.id.레이1요리4정보레이,R.id.레이1요리5정보레이,
                    R.id.레이1요리6정보레이,R.id.레이1요리7정보레이,R.id.레이1요리8정보레이,R.id.레이1요리9정보레이,R.id.레이1요리10정보레이,
                    R.id.레이1요리11정보레이,R.id.레이1요리12정보레이,R.id.레이1요리13정보레이,R.id.레이1요리14정보레이,R.id.레이1요리15정보레이,
                    R.id.레이1요리16정보레이,R.id.레이1요리17정보레이,R.id.레이1요리18정보레이,R.id.레이1요리19정보레이,R.id.레이1요리20정보레이,
                    R.id.레이1요리21정보레이,R.id.레이1요리22정보레이,R.id.레이1요리23정보레이,R.id.레이1요리24정보레이,R.id.레이1요리25정보레이,
                    R.id.레이1요리26정보레이,R.id.레이1요리27정보레이,R.id.레이1요리28정보레이,R.id.레이1요리29정보레이,R.id.레이1요리30정보레이,
                    R.id.레이1요리31정보레이,R.id.레이1요리32정보레이,R.id.레이1요리33정보레이,R.id.레이1요리34정보레이,R.id.레이1요리35정보레이,
                    R.id.레이1요리36정보레이,R.id.레이1요리37정보레이,R.id.레이1요리38정보레이,R.id.레이1요리39정보레이,R.id.레이1요리40정보레이,
                    R.id.레이1요리41정보레이,R.id.레이1요리42정보레이,R.id.레이1요리43정보레이,R.id.레이1요리44정보레이,R.id.레이1요리45정보레이,
                    R.id.레이1요리46정보레이,R.id.레이1요리47정보레이,R.id.레이1요리48정보레이},

                   {R.id.레이1요리1재료레이,R.id.레이1요리2재료레이,R.id.레이1요리3재료레이,R.id.레이1요리4재료레이,R.id.레이1요리5재료레이,
                    R.id.레이1요리6재료레이,R.id.레이1요리7재료레이,R.id.레이1요리8재료레이,R.id.레이1요리9재료레이,R.id.레이1요리10재료레이,
                    R.id.레이1요리11재료레이,R.id.레이1요리12재료레이,R.id.레이1요리13재료레이,R.id.레이1요리14재료레이,R.id.레이1요리15재료레이,
                    R.id.레이1요리16재료레이,R.id.레이1요리17재료레이,R.id.레이1요리18재료레이,R.id.레이1요리19재료레이,R.id.레이1요리20재료레이,
                    R.id.레이1요리21재료레이,R.id.레이1요리22재료레이,R.id.레이1요리23재료레이,R.id.레이1요리24재료레이,R.id.레이1요리25재료레이,
                    R.id.레이1요리26재료레이,R.id.레이1요리27재료레이,R.id.레이1요리28재료레이,R.id.레이1요리29재료레이,R.id.레이1요리30재료레이,
                    R.id.레이1요리31재료레이,R.id.레이1요리32재료레이,R.id.레이1요리33재료레이,R.id.레이1요리34재료레이,R.id.레이1요리35재료레이,
                    R.id.레이1요리36재료레이,R.id.레이1요리37재료레이,R.id.레이1요리38재료레이,R.id.레이1요리39재료레이,R.id.레이1요리40재료레이,
                    R.id.레이1요리41재료레이,R.id.레이1요리42재료레이,R.id.레이1요리43재료레이,R.id.레이1요리44재료레이,R.id.레이1요리45재료레이,
                    R.id.레이1요리46재료레이,R.id.레이1요리47재료레이,R.id.레이1요리48재료레이},

                   {R.id.요리스크롤1,R.id.요리스크롤2,R.id.요리스크롤3,R.id.요리스크롤4,R.id.요리스크롤5,
                    R.id.요리스크롤6,R.id.요리스크롤7,R.id.요리스크롤8,R.id.요리스크롤9,R.id.요리스크롤10,
                    R.id.요리스크롤11,R.id.요리스크롤12,R.id.요리스크롤13,R.id.요리스크롤14,R.id.요리스크롤15,
                    R.id.요리스크롤16,R.id.요리스크롤17,R.id.요리스크롤18,R.id.요리스크롤19,R.id.요리스크롤20,
                    R.id.요리스크롤21,R.id.요리스크롤22,R.id.요리스크롤23,R.id.요리스크롤24,R.id.요리스크롤25,
                    R.id.요리스크롤26,R.id.요리스크롤27,R.id.요리스크롤28,R.id.요리스크롤29,R.id.요리스크롤30,
                    R.id.요리스크롤31,R.id.요리스크롤32,R.id.요리스크롤33,R.id.요리스크롤34,R.id.요리스크롤35,
                    R.id.요리스크롤36,R.id.요리스크롤37,R.id.요리스크롤38,R.id.요리스크롤39,R.id.요리스크롤40,
                    R.id.요리스크롤41,R.id.요리스크롤42,R.id.요리스크롤43,R.id.요리스크롤44,R.id.요리스크롤45,
                    R.id.요리스크롤46,R.id.요리스크롤47,R.id.요리스크롤48},

                   {R.id.레이1요리1이미지레이,R.id.레이1요리2이미지레이,R.id.레이1요리3이미지레이,R.id.레이1요리4이미지레이,R.id.레이1요리5이미지레이,
                    R.id.레이1요리6이미지레이,R.id.레이1요리7이미지레이,R.id.레이1요리8이미지레이,R.id.레이1요리9이미지레이,R.id.레이1요리10이미지레이,
                    R.id.레이1요리11이미지레이,R.id.레이1요리12이미지레이,R.id.레이1요리13이미지레이,R.id.레이1요리14이미지레이,R.id.레이1요리15이미지레이,
                    R.id.레이1요리16이미지레이,R.id.레이1요리17이미지레이,R.id.레이1요리18이미지레이,R.id.레이1요리19이미지레이,R.id.레이1요리20이미지레이,
                    R.id.레이1요리21이미지레이,R.id.레이1요리22이미지레이,R.id.레이1요리23이미지레이,R.id.레이1요리24이미지레이,R.id.레이1요리25이미지레이,
                    R.id.레이1요리26이미지레이,R.id.레이1요리27이미지레이,R.id.레이1요리28이미지레이,R.id.레이1요리29이미지레이,R.id.레이1요리30이미지레이,
                    R.id.레이1요리31이미지레이,R.id.레이1요리32이미지레이,R.id.레이1요리33이미지레이,R.id.레이1요리34이미지레이,R.id.레이1요리35이미지레이,
                    R.id.레이1요리36이미지레이,R.id.레이1요리37이미지레이,R.id.레이1요리38이미지레이,R.id.레이1요리39이미지레이,R.id.레이1요리40이미지레이,
                    R.id.레이1요리41이미지레이,R.id.레이1요리42이미지레이,R.id.레이1요리43이미지레이,R.id.레이1요리44이미지레이,R.id.레이1요리45이미지레이,
                    R.id.레이1요리46이미지레이,R.id.레이1요리47이미지레이,R.id.레이1요리48이미지레이},

                   {R.id.레이1요리1버튼레이,R.id.레이1요리2버튼레이,R.id.레이1요리3버튼레이,R.id.레이1요리4버튼레이,R.id.레이1요리5버튼레이,
                    R.id.레이1요리6버튼레이,R.id.레이1요리7버튼레이,R.id.레이1요리8버튼레이,R.id.레이1요리9버튼레이,R.id.레이1요리10버튼레이,
                    R.id.레이1요리11버튼레이,R.id.레이1요리12버튼레이,R.id.레이1요리13버튼레이,R.id.레이1요리14버튼레이,R.id.레이1요리15버튼레이,
                    R.id.레이1요리16버튼레이,R.id.레이1요리17버튼레이,R.id.레이1요리18버튼레이,R.id.레이1요리19버튼레이,R.id.레이1요리20버튼레이,
                    R.id.레이1요리21버튼레이,R.id.레이1요리22버튼레이,R.id.레이1요리23버튼레이,R.id.레이1요리24버튼레이,R.id.레이1요리25버튼레이,
                    R.id.레이1요리26버튼레이,R.id.레이1요리27버튼레이,R.id.레이1요리28버튼레이,R.id.레이1요리29버튼레이,R.id.레이1요리30버튼레이,
                    R.id.레이1요리31버튼레이,R.id.레이1요리32버튼레이,R.id.레이1요리33버튼레이,R.id.레이1요리34버튼레이,R.id.레이1요리35버튼레이,
                    R.id.레이1요리36버튼레이,R.id.레이1요리37버튼레이,R.id.레이1요리38버튼레이,R.id.레이1요리39버튼레이,R.id.레이1요리40버튼레이,
                    R.id.레이1요리41버튼레이,R.id.레이1요리42버튼레이,R.id.레이1요리43버튼레이,R.id.레이1요리44버튼레이,R.id.레이1요리45버튼레이,
                    R.id.레이1요리46버튼레이,R.id.레이1요리47버튼레이,R.id.레이1요리48버튼레이},

                   {R.id.레이1선택버튼1,R.id.레이1선택버튼2,R.id.레이1선택버튼3,R.id.레이1선택버튼4,R.id.레이1선택버튼5,
                    R.id.레이1선택버튼6,R.id.레이1선택버튼7,R.id.레이1선택버튼8,R.id.레이1선택버튼9,R.id.레이1선택버튼10,
                    R.id.레이1선택버튼11,R.id.레이1선택버튼12,R.id.레이1선택버튼13,R.id.레이1선택버튼14,R.id.레이1선택버튼15,
                    R.id.레이1선택버튼16,R.id.레이1선택버튼17,R.id.레이1선택버튼18,R.id.레이1선택버튼19,R.id.레이1선택버튼20,
                    R.id.레이1선택버튼21,R.id.레이1선택버튼22,R.id.레이1선택버튼23,R.id.레이1선택버튼24,R.id.레이1선택버튼25,
                    R.id.레이1선택버튼26,R.id.레이1선택버튼27,R.id.레이1선택버튼28,R.id.레이1선택버튼29,R.id.레이1선택버튼30,
                    R.id.레이1선택버튼31,R.id.레이1선택버튼32,R.id.레이1선택버튼33,R.id.레이1선택버튼34,R.id.레이1선택버튼35,
                    R.id.레이1선택버튼36,R.id.레이1선택버튼37,R.id.레이1선택버튼38,R.id.레이1선택버튼39,R.id.레이1선택버튼40,
                    R.id.레이1선택버튼41,R.id.레이1선택버튼42,R.id.레이1선택버튼43,R.id.레이1선택버튼44,R.id.레이1선택버튼45,
                    R.id.레이1선택버튼46,R.id.레이1선택버튼47,R.id.레이1선택버튼48},

                   {R.id.레이1요리1이미지,R.id.레이1요리2이미지,R.id.레이1요리3이미지,R.id.레이1요리4이미지,R.id.레이1요리5이미지,
                    R.id.레이1요리6이미지,R.id.레이1요리7이미지,R.id.레이1요리8이미지,R.id.레이1요리9이미지,R.id.레이1요리10이미지,
                    R.id.레이1요리11이미지,R.id.레이1요리12이미지,R.id.레이1요리13이미지,R.id.레이1요리14이미지,R.id.레이1요리15이미지,
                    R.id.레이1요리16이미지,R.id.레이1요리17이미지,R.id.레이1요리18이미지,R.id.레이1요리19이미지,R.id.레이1요리20이미지,
                    R.id.레이1요리21이미지,R.id.레이1요리22이미지,R.id.레이1요리23이미지,R.id.레이1요리24이미지,R.id.레이1요리25이미지,
                    R.id.레이1요리26이미지,R.id.레이1요리27이미지,R.id.레이1요리28이미지,R.id.레이1요리29이미지,R.id.레이1요리30이미지,
                    R.id.레이1요리31이미지,R.id.레이1요리32이미지,R.id.레이1요리33이미지,R.id.레이1요리34이미지,R.id.레이1요리35이미지,
                    R.id.레이1요리36이미지,R.id.레이1요리37이미지,R.id.레이1요리38이미지,R.id.레이1요리39이미지,R.id.레이1요리40이미지,
                    R.id.레이1요리41이미지,R.id.레이1요리42이미지,R.id.레이1요리43이미지,R.id.레이1요리44이미지,R.id.레이1요리45이미지,
                    R.id.레이1요리46이미지,R.id.레이1요리47이미지,R.id.레이1요리48이미지},

                   {R.id.레이1요리1이름텍스트,R.id.레이1요리2이름텍스트,R.id.레이1요리3이름텍스트,R.id.레이1요리4이름텍스트,R.id.레이1요리5이름텍스트,
                    R.id.레이1요리6이름텍스트,R.id.레이1요리7이름텍스트,R.id.레이1요리8이름텍스트,R.id.레이1요리9이름텍스트,R.id.레이1요리10이름텍스트,
                    R.id.레이1요리11이름텍스트,R.id.레이1요리12이름텍스트,R.id.레이1요리13이름텍스트,R.id.레이1요리14이름텍스트,R.id.레이1요리15이름텍스트,
                    R.id.레이1요리16이름텍스트,R.id.레이1요리17이름텍스트,R.id.레이1요리18이름텍스트,R.id.레이1요리19이름텍스트,R.id.레이1요리20이름텍스트,
                    R.id.레이1요리21이름텍스트,R.id.레이1요리22이름텍스트,R.id.레이1요리23이름텍스트,R.id.레이1요리24이름텍스트,R.id.레이1요리25이름텍스트,
                    R.id.레이1요리26이름텍스트,R.id.레이1요리27이름텍스트,R.id.레이1요리28이름텍스트,R.id.레이1요리29이름텍스트,R.id.레이1요리30이름텍스트,
                    R.id.레이1요리31이름텍스트,R.id.레이1요리32이름텍스트,R.id.레이1요리33이름텍스트,R.id.레이1요리34이름텍스트,R.id.레이1요리35이름텍스트,
                    R.id.레이1요리36이름텍스트,R.id.레이1요리37이름텍스트,R.id.레이1요리38이름텍스트,R.id.레이1요리39이름텍스트,R.id.레이1요리40이름텍스트,
                    R.id.레이1요리41이름텍스트,R.id.레이1요리42이름텍스트,R.id.레이1요리43이름텍스트,R.id.레이1요리44이름텍스트,R.id.레이1요리45이름텍스트,
                    R.id.레이1요리46이름텍스트,R.id.레이1요리47이름텍스트,R.id.레이1요리48이름텍스트},

                    {R.id.레이1요리1가격텍스트,R.id.레이1요리2가격텍스트,R.id.레이1요리3가격텍스트,R.id.레이1요리4가격텍스트,R.id.레이1요리5가격텍스트,
                    R.id.레이1요리6가격텍스트,R.id.레이1요리7가격텍스트,R.id.레이1요리8가격텍스트,R.id.레이1요리9가격텍스트,R.id.레이1요리10가격텍스트,
                    R.id.레이1요리11가격텍스트,R.id.레이1요리12가격텍스트,R.id.레이1요리13가격텍스트,R.id.레이1요리14가격텍스트,R.id.레이1요리15가격텍스트,
                    R.id.레이1요리16가격텍스트,R.id.레이1요리17가격텍스트,R.id.레이1요리18가격텍스트,R.id.레이1요리19가격텍스트,R.id.레이1요리20가격텍스트,
                    R.id.레이1요리21가격텍스트,R.id.레이1요리22가격텍스트,R.id.레이1요리23가격텍스트,R.id.레이1요리24가격텍스트,R.id.레이1요리25가격텍스트,
                    R.id.레이1요리26가격텍스트,R.id.레이1요리27가격텍스트,R.id.레이1요리28가격텍스트,R.id.레이1요리29가격텍스트,R.id.레이1요리30가격텍스트,
                    R.id.레이1요리31가격텍스트,R.id.레이1요리32가격텍스트,R.id.레이1요리33가격텍스트,R.id.레이1요리34가격텍스트,R.id.레이1요리35가격텍스트,
                    R.id.레이1요리36가격텍스트,R.id.레이1요리37가격텍스트,R.id.레이1요리38가격텍스트,R.id.레이1요리39가격텍스트,R.id.레이1요리40가격텍스트,
                    R.id.레이1요리41가격텍스트,R.id.레이1요리42가격텍스트,R.id.레이1요리43가격텍스트,R.id.레이1요리44가격텍스트,R.id.레이1요리45가격텍스트,
                    R.id.레이1요리46가격텍스트,R.id.레이1요리47가격텍스트,R.id.레이1요리48가격텍스트},

                   {R.id.레이1요리1마진텍스트,R.id.레이1요리2마진텍스트,R.id.레이1요리3마진텍스트,R.id.레이1요리4마진텍스트,R.id.레이1요리5마진텍스트,
                    R.id.레이1요리6마진텍스트,R.id.레이1요리7마진텍스트,R.id.레이1요리8마진텍스트,R.id.레이1요리9마진텍스트,R.id.레이1요리10마진텍스트,
                    R.id.레이1요리11마진텍스트,R.id.레이1요리12마진텍스트,R.id.레이1요리13마진텍스트,R.id.레이1요리14마진텍스트,R.id.레이1요리15마진텍스트,
                    R.id.레이1요리16마진텍스트,R.id.레이1요리17마진텍스트,R.id.레이1요리18마진텍스트,R.id.레이1요리19마진텍스트,R.id.레이1요리20마진텍스트,
                    R.id.레이1요리21마진텍스트,R.id.레이1요리22마진텍스트,R.id.레이1요리23마진텍스트,R.id.레이1요리24마진텍스트,R.id.레이1요리25마진텍스트,
                    R.id.레이1요리26마진텍스트,R.id.레이1요리27마진텍스트,R.id.레이1요리28마진텍스트,R.id.레이1요리29마진텍스트,R.id.레이1요리30마진텍스트,
                    R.id.레이1요리31마진텍스트,R.id.레이1요리32마진텍스트,R.id.레이1요리33마진텍스트,R.id.레이1요리34마진텍스트,R.id.레이1요리35마진텍스트,
                    R.id.레이1요리36마진텍스트,R.id.레이1요리37마진텍스트,R.id.레이1요리38마진텍스트,R.id.레이1요리39마진텍스트,R.id.레이1요리40마진텍스트,
                    R.id.레이1요리41마진텍스트,R.id.레이1요리42마진텍스트,R.id.레이1요리43마진텍스트,R.id.레이1요리44마진텍스트,R.id.레이1요리45마진텍스트,
                    R.id.레이1요리46마진텍스트,R.id.레이1요리47마진텍스트,R.id.레이1요리48마진텍스트},



                   {R.id.레이1요리1재료1,R.id.레이1요리2재료1,R.id.레이1요리3재료1,R.id.레이1요리4재료1,R.id.레이1요리5재료1,
                    R.id.레이1요리6재료1,R.id.레이1요리7재료1,R.id.레이1요리8재료1,R.id.레이1요리9재료1,R.id.레이1요리10재료1,
                    R.id.레이1요리11재료1,R.id.레이1요리12재료1,R.id.레이1요리13재료1,R.id.레이1요리14재료1,R.id.레이1요리15재료1,
                    R.id.레이1요리16재료1,R.id.레이1요리17재료1,R.id.레이1요리18재료1,R.id.레이1요리19재료1,R.id.레이1요리20재료1,
                    R.id.레이1요리21재료1,R.id.레이1요리22재료1,R.id.레이1요리23재료1,R.id.레이1요리24재료1,R.id.레이1요리25재료1,
                    R.id.레이1요리26재료1,R.id.레이1요리27재료1,R.id.레이1요리28재료1,R.id.레이1요리29재료1,R.id.레이1요리30재료1,
                    R.id.레이1요리31재료1,R.id.레이1요리32재료1,R.id.레이1요리33재료1,R.id.레이1요리34재료1,R.id.레이1요리35재료1,
                    R.id.레이1요리36재료1,R.id.레이1요리37재료1,R.id.레이1요리38재료1,R.id.레이1요리39재료1,R.id.레이1요리40재료1,
                    R.id.레이1요리41재료1,R.id.레이1요리42재료1,R.id.레이1요리43재료1,R.id.레이1요리44재료1,R.id.레이1요리45재료1,
                    R.id.레이1요리46재료1,R.id.레이1요리47재료1,R.id.레이1요리48재료1},

                   {R.id.레이1요리1재료2,R.id.레이1요리2재료2,R.id.레이1요리3재료2,R.id.레이1요리4재료2,R.id.레이1요리5재료2,
                    R.id.레이1요리6재료2,R.id.레이1요리7재료2,R.id.레이1요리8재료2,R.id.레이1요리9재료2,R.id.레이1요리10재료2,
                    R.id.레이1요리11재료2,R.id.레이1요리12재료2,R.id.레이1요리13재료2,R.id.레이1요리14재료2,R.id.레이1요리15재료2,
                    R.id.레이1요리16재료2,R.id.레이1요리17재료2,R.id.레이1요리18재료2,R.id.레이1요리19재료2,R.id.레이1요리20재료2,
                    R.id.레이1요리21재료2,R.id.레이1요리22재료2,R.id.레이1요리23재료2,R.id.레이1요리24재료2,R.id.레이1요리25재료2,
                    R.id.레이1요리26재료2,R.id.레이1요리27재료2,R.id.레이1요리28재료2,R.id.레이1요리29재료2,R.id.레이1요리30재료2,
                    R.id.레이1요리31재료2,R.id.레이1요리32재료2,R.id.레이1요리33재료2,R.id.레이1요리34재료2,R.id.레이1요리35재료2,
                    R.id.레이1요리36재료2,R.id.레이1요리37재료2,R.id.레이1요리38재료2,R.id.레이1요리39재료2,R.id.레이1요리40재료2,
                    R.id.레이1요리41재료2,R.id.레이1요리42재료2,R.id.레이1요리43재료2,R.id.레이1요리44재료2,R.id.레이1요리45재료2,
                    R.id.레이1요리46재료2,R.id.레이1요리47재료2,R.id.레이1요리48재료2},

                   {R.id.레이1요리1재료3,R.id.레이1요리2재료3,R.id.레이1요리3재료3,R.id.레이1요리4재료3,R.id.레이1요리5재료3,
                    R.id.레이1요리6재료3,R.id.레이1요리7재료3,R.id.레이1요리8재료3,R.id.레이1요리9재료3,R.id.레이1요리10재료3,
                    R.id.레이1요리11재료3,R.id.레이1요리12재료3,R.id.레이1요리13재료3,R.id.레이1요리14재료3,R.id.레이1요리15재료3,
                    R.id.레이1요리16재료3,R.id.레이1요리17재료3,R.id.레이1요리18재료3,R.id.레이1요리19재료3,R.id.레이1요리20재료3,
                    R.id.레이1요리21재료3,R.id.레이1요리22재료3,R.id.레이1요리23재료3,R.id.레이1요리24재료3,R.id.레이1요리25재료3,
                    R.id.레이1요리26재료3,R.id.레이1요리27재료3,R.id.레이1요리28재료3,R.id.레이1요리29재료3,R.id.레이1요리30재료3,
                    R.id.레이1요리31재료3,R.id.레이1요리32재료3,R.id.레이1요리33재료3,R.id.레이1요리34재료3,R.id.레이1요리35재료3,
                    R.id.레이1요리36재료3,R.id.레이1요리37재료3,R.id.레이1요리38재료3,R.id.레이1요리39재료3,R.id.레이1요리40재료3,
                    R.id.레이1요리41재료3,R.id.레이1요리42재료3,R.id.레이1요리43재료3,R.id.레이1요리44재료3,R.id.레이1요리45재료3,
                    R.id.레이1요리46재료3,R.id.레이1요리47재료3,R.id.레이1요리48재료3}
};

    //레이2변수=================================================================================================================================================================
    LinearLayout 레이2상위,레이2요리정보레이[]=new LinearLayout[10],장보기스크롤[]=new LinearLayout[10];
    FrameLayout 레이2요리이미지레이[]=new FrameLayout[10],레이2요리버튼레이[]=new FrameLayout[10];
    SeekBar 레이2식바;
    Button 레이2나가기,레이2작동버튼,레이2선택버튼[]=new Button[10];
    ImageView 레이2요리이미지[]=new ImageView[10];
    TextView  레이2식바수량텍스트,레이2요리이름텍스트[]=new TextView[10],레이2요리가격텍스트[]=new TextView[10],레이2요리보유텍스트[]=new TextView[10];
    int 레이2id[][]={
                   {R.id.레이2요리1정보레이,R.id.레이2요리2정보레이,R.id.레이2요리3정보레이,R.id.레이2요리4정보레이,R.id.레이2요리5정보레이,
                    R.id.레이2요리6정보레이,R.id.레이2요리7정보레이,R.id.레이2요리8정보레이,R.id.레이2요리9정보레이,R.id.레이2요리10정보레이},

                   {R.id.장보기스크롤1,R.id.장보기스크롤2,R.id.장보기스크롤3,R.id.장보기스크롤4,R.id.장보기스크롤5,
                    R.id.장보기스크롤6,R.id.장보기스크롤7,R.id.장보기스크롤8,R.id.장보기스크롤9,R.id.장보기스크롤10},

                   {R.id.레이2요리1이미지레이,R.id.레이2요리2이미지레이,R.id.레이2요리3이미지레이,R.id.레이2요리4이미지레이,R.id.레이2요리5이미지레이,
                    R.id.레이2요리6이미지레이,R.id.레이2요리7이미지레이,R.id.레이2요리8이미지레이,R.id.레이2요리9이미지레이,R.id.레이2요리10이미지레이},

                   {R.id.레이2요리1버튼레이,R.id.레이2요리2버튼레이,R.id.레이2요리3버튼레이,R.id.레이2요리4버튼레이,R.id.레이2요리5버튼레이,
                    R.id.레이2요리6버튼레이,R.id.레이2요리7버튼레이,R.id.레이2요리8버튼레이,R.id.레이2요리9버튼레이,R.id.레이2요리10버튼레이},

                   {R.id.레이2선택버튼1,R.id.레이2선택버튼2,R.id.레이2선택버튼3,R.id.레이2선택버튼4,R.id.레이2선택버튼5,
                    R.id.레이2선택버튼6,R.id.레이2선택버튼7,R.id.레이2선택버튼8,R.id.레이2선택버튼9,R.id.레이2선택버튼10},

                   {R.id.레이2요리1이미지,R.id.레이2요리2이미지,R.id.레이2요리3이미지,R.id.레이2요리4이미지,R.id.레이2요리5이미지,
                    R.id.레이2요리6이미지,R.id.레이2요리7이미지,R.id.레이2요리8이미지,R.id.레이2요리9이미지,R.id.레이2요리10이미지},

                   {R.id.레이2요리1이름텍스트,R.id.레이2요리2이름텍스트,R.id.레이2요리3이름텍스트,R.id.레이2요리4이름텍스트,R.id.레이2요리5이름텍스트,
                    R.id.레이2요리6이름텍스트,R.id.레이2요리7이름텍스트,R.id.레이2요리8이름텍스트,R.id.레이2요리9이름텍스트,R.id.레이2요리10이름텍스트},

                   {R.id.레이2요리1가격텍스트,R.id.레이2요리2가격텍스트,R.id.레이2요리3가격텍스트,R.id.레이2요리4가격텍스트,R.id.레이2요리5가격텍스트,
                    R.id.레이2요리6가격텍스트,R.id.레이2요리7가격텍스트,R.id.레이2요리8가격텍스트,R.id.레이2요리9가격텍스트,R.id.레이2요리10가격텍스트},

                   {R.id.레이2요리1보유텍스트,R.id.레이2요리2보유텍스트,R.id.레이2요리3보유텍스트,R.id.레이2요리4보유텍스트,R.id.레이2요리5보유텍스트,
                    R.id.레이2요리6보유텍스트,R.id.레이2요리7보유텍스트,R.id.레이2요리8보유텍스트,R.id.레이2요리9보유텍스트,R.id.레이2요리10보유텍스트},
};
    //레이3변수=================================================================================================================================================================
    LinearLayout 레이3상위,레이3정보레이;
    FrameLayout 레이3이미지레이;
    ImageView 레이3이미지;
    TextView 레이3정보텍스트[]=new TextView[10];
    Button 레이3나가기,레이3작동버튼;
    int 레이3id[][]={
           {R.id.레이3정보텍스트1,R.id.레이3정보텍스트2,R.id.레이3정보텍스트3,R.id.레이3정보텍스트4,R.id.레이3정보텍스트5,
            R.id.레이3정보텍스트6,R.id.레이3정보텍스트7,R.id.레이3정보텍스트8,R.id.레이3정보텍스트9,R.id.레이3정보텍스트10}};
    //레이4변수=================================================================================================================================================================
   LinearLayout 레이4상위,레이4정보레이;
   FrameLayout 레이4이미지레이;
   ImageView 레이4이미지;
   TextView 레이4정보텍스트[]=new TextView[5];
    Button 레이4나가기,레이4작동버튼;
    SeekBar 레이4식바;
    Switch 레이4스위치1,레이4스위치2;
   int 레이4id[][]={
            {R.id.레이4정보텍스트1,R.id.레이4정보텍스트2,R.id.레이4정보텍스트3,R.id.레이4정보텍스트4,R.id.레이4정보텍스트5}};
    //레이5변수=================================================================================================================================================================
    LinearLayout 레이5상위,레이5정보레이;
    FrameLayout 레이5이미지레이;
    ImageView 레이5이미지;
    TextView 레이5정보텍스트[]=new TextView[5];
    Button 레이5나가기,레이5작동버튼;
    Switch 레이5스위치1,레이5스위치2;

    int 레이5id[][]={
            {R.id.레이5정보텍스트1,R.id.레이5정보텍스트2,R.id.레이5정보텍스트3,R.id.레이5정보텍스트4,R.id.레이5정보텍스트5}};
    //레이6변수=================================================================================================================================================================
    LinearLayout 레이6상위,레이6정보레이;
    FrameLayout 레이6이미지레이;
    ImageView 레이6이미지;
    TextView 레이6정보텍스트[]=new TextView[6];
    Button 레이6나가기,레이6작동버튼;
    Switch 레이6스위치1,레이6스위치2;
    SeekBar 레이6식바;
    int 레이6id[][]={
            {R.id.레이6정보텍스트1,R.id.레이6정보텍스트2,R.id.레이6정보텍스트3,R.id.레이6정보텍스트4,R.id.레이6정보텍스트5,R.id.레이6정보텍스트6}};
    //레이7변수=================================================================================================================================================================
    LinearLayout 레이7상위;
    TextView 레이7정보텍스트[]=new TextView[9];
    Button 레이7나가기,레이7작동버튼;
    SeekBar 레이7식바;
    int 레이7id[][]={
            {R.id.레이7정보텍스트1,R.id.레이7정보텍스트2,R.id.레이7정보텍스트3,R.id.레이7정보텍스트4,R.id.레이7정보텍스트5,
             R.id.레이7정보텍스트6,R.id.레이7정보텍스트7,R.id.레이7정보텍스트8,R.id.레이7정보텍스트9}};
    //레이8변수=================================================================================================================================================================
    LinearLayout 레이8상위,레이8정보레이;
    FrameLayout 레이8이미지레이;
    ImageView 레이8이미지;
    TextView 레이8정보텍스트[]=new TextView[4];
    Button 레이8나가기,레이8작동버튼;
    SeekBar 레이8식바;
    int 레이8id[][]={
            {R.id.레이8정보텍스트1,R.id.레이8정보텍스트2,R.id.레이8정보텍스트3,R.id.레이8정보텍스트4}};
    //레이9변수=================================================================================================================================================================
    LinearLayout 레이9상위,레이9정보레이;
    FrameLayout 레이9이미지레이;
    ImageView 레이9이미지;
    TextView 레이9정보텍스트[]=new TextView[5];
    Button 레이9나가기,레이9작동버튼,레이9작동버튼2;
    SeekBar 레이9식바;
    int 레이9id[][]={
            {R.id.레이9정보텍스트1,R.id.레이9정보텍스트2,R.id.레이9정보텍스트3,R.id.레이9정보텍스트4,R.id.레이9정보텍스트5}};
    //레이10변수=================================================================================================================================================================
    LinearLayout 레이10상위,레이10정보레이1,레이10정보레이2,레이랭크2정보레이[]=new LinearLayout[18];
    FrameLayout 레이10이미지레이1,레이10이미지레이2,랭크1레이,랭크2레이;
    ImageView 레이10이미지1,레이10이미지2,레이랭크이미지1,레이랭크이미지2,레이랭크이미지3,레이랭크2이미지[]=new ImageView[18];
    ProgressBar 레이10프로그레스1,레이10프로그레스2;
    TextView 레이10정보텍스트[]=new TextView[18],레이랭크1정보텍스트[]=new TextView[20];
    Button 레이10나가기,레이10작동버튼,레이10작동버튼2,레이10작동버튼3,레이랭크나가기,레이랭크2나가기;
    int 레이10id[][]={
            {R.id.레이10정보텍스트1,R.id.레이10정보텍스트2,R.id.레이10정보텍스트3,R.id.레이10정보텍스트4,R.id.레이10정보텍스트5,
             R.id.레이10정보텍스트6,R.id.레이10정보텍스트7,R.id.레이10정보텍스트8,R.id.레이10정보텍스트9,R.id.레이10정보텍스트10},
            {
             R.id.레이랭크2정보레이1,R.id.레이랭크2정보레이2,R.id.레이랭크2정보레이3,R.id.레이랭크2정보레이4,R.id.레이랭크2정보레이5,
             R.id.레이랭크2정보레이6,R.id.레이랭크2정보레이7,R.id.레이랭크2정보레이8,R.id.레이랭크2정보레이9,R.id.레이랭크2정보레이10,
             R.id.레이랭크2정보레이11,R.id.레이랭크2정보레이12,R.id.레이랭크2정보레이13,R.id.레이랭크2정보레이14,R.id.레이랭크2정보레이15,
             R.id.레이랭크2정보레이16,R.id.레이랭크2정보레이17,R.id.레이랭크2정보레이18},
            {R.id.레이랭크2이미지1,R.id.레이랭크2이미지2,R.id.레이랭크2이미지3,R.id.레이랭크2이미지4,R.id.레이랭크2이미지5,
             R.id.레이랭크2이미지6,R.id.레이랭크2이미지7,R.id.레이랭크2이미지8,R.id.레이랭크2이미지9,R.id.레이랭크2이미지10,
             R.id.레이랭크2이미지11,R.id.레이랭크2이미지12,R.id.레이랭크2이미지13,R.id.레이랭크2이미지14,R.id.레이랭크2이미지15,
             R.id.레이랭크2이미지16,R.id.레이랭크2이미지17,R.id.레이랭크2이미지18,},
            {R.id.레이랭크정보텍스트1,R.id.레이랭크정보텍스트2,R.id.레이랭크정보텍스트3,R.id.레이랭크정보텍스트4,R.id.레이랭크정보텍스트5,
             R.id.레이랭크정보텍스트6,R.id.레이랭크정보텍스트7,R.id.레이랭크정보텍스트8,R.id.레이랭크정보텍스트9,R.id.레이랭크정보텍스트10,
             R.id.레이랭크정보텍스트11,R.id.레이랭크정보텍스트12,R.id.레이랭크정보텍스트13,R.id.레이랭크정보텍스트14,R.id.레이랭크정보텍스트15,
             R.id.레이랭크정보텍스트16,R.id.레이랭크정보텍스트17,R.id.레이랭크정보텍스트18,R.id.레이랭크정보텍스트19,R.id.레이랭크정보텍스트20,

            }};
    int 대결중=0,랜덤,카운트;
    long 내체력=0,내현재체력,내공격력 = 0,내크리티컬배수=0,내크리티컬확률=0,공격력기본최소배수=0,공격력기본최대배수=0,내최고의요리=0,내요리속도=0;
    long 상대체력=0,상대현재체력,상대공격력=0,상대크리티컬배수=0,상대크리티컬확률=0,상대공격력기본최소배수=0,상대공격력기본최대배수=0,상대최고의요리=0,상대요리속도=0,상대승점=0,상대요리[]=new long[48],상대캐릭터[]=new long[22];
    //레이11변수=================================================================================================================================================================
    LinearLayout 레이11상위,레이11정보레이;
    FrameLayout 레이11이미지레이;
    ImageView 레이11이미지;
    TextView 레이11정보텍스트[]=new TextView[6];
    Button 레이11나가기,레이11작동버튼;
    int 레이11id[][]={
            {R.id.레이11정보텍스트1,R.id.레이11정보텍스트2,R.id.레이11정보텍스트3,R.id.레이11정보텍스트4,R.id.레이11정보텍스트5,R.id.레이11정보텍스트6}};
    //레이12변수=================================================================================================================================================================
    LinearLayout 레이12상위,레이12정보레이;
    TextView 레이12정보텍스트[]=new TextView[4];
    Button 레이12작동버튼;
    int 레이12id[][]={
            {R.id.레이12정보텍스트1,R.id.레이12정보텍스트2,R.id.레이12정보텍스트3,R.id.레이12정보텍스트4}};
    //레이13변수=================================================================================================================================================================
    LinearLayout 레이13상위,레이13정보레이;
    TextView 레이13정보텍스트;
    Button 레이13작동버튼1,레이13작동버튼2;

    //레이14변수=================================================================================================================================================================
    LinearLayout 레이14상위,레이14정보레이;
    static TextView 레이14정보텍스트;
    static Button 레이14작동버튼;
    static int 인터넷연결여부=0;
    //레이인포변수=================================================================================================================================================================

    TextView 레이인포정보텍스트[]=new TextView[11];
    Button 레이인포나가기,도움말나가기;
    int 레이인포id[]={
      R.id.레이인포정보텍스트1,R.id.레이인포정보텍스트2,R.id.레이인포정보텍스트3,R.id.레이인포정보텍스트4,R.id.레이인포정보텍스트5,
            R.id.레이인포정보텍스트6,R.id.레이인포정보텍스트7,R.id.레이인포정보텍스트8,R.id.레이인포정보텍스트9,R.id.레이인포정보텍스트10,
            R.id.레이인포정보텍스트11
    };



    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
    }

    Handler han = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    크기조정();
                    prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
                    현재접속아이디=prefs.getString("현재접속아이디",현재접속아이디);
                    로딩멘트텍스트.setText("재료를 검수 중 입니다.");
                    로딩프로그레스.setProgress(25);
                    로딩텍스트.setText(""+로딩프로그레스.getProgress()+"%");
                    han.sendEmptyMessageDelayed(1,1000);
                    b++;
              ;
                    break;
                case 1:
                    이미지삽입();
                    로딩멘트텍스트.setText("트럭을 청소하고 있습니다.");
                    로딩프로그레스.setProgress(50);
                    로딩텍스트.setText(""+로딩프로그레스.getProgress()+"%");
                    han.sendEmptyMessageDelayed(2,1000);
                    break;
                case 2 :

                    서버불러오기();

                    로딩멘트텍스트.setText("간판에 불을 켜고 있습니다.");
                    로딩프로그레스.setProgress(75);
                    로딩텍스트.setText(""+로딩프로그레스.getProgress()+"%");

                    break;
                case 3 :
                    초반설정();
                    로딩멘트텍스트.setText("오픈준비가 완료 되었습니다.");
                    로딩프로그레스.setProgress(100);
                    로딩텍스트.setText(""+로딩프로그레스.getProgress()+"%");
                    han.sendEmptyMessageDelayed(4,1000);
                    break;
                case 4 :
                    로딩화면레이아웃.setVisibility(INVISIBLE);
                    break;
                case 10: //요리

                    if(요리남은수량>0){
                        if(요리남은수량>=마법제조기생산[마법제조기단계]){
                            생산텍스트애니(현재만들고있는요리,마법제조기생산[마법제조기단계]);
                            요리남은수량-=마법제조기생산[마법제조기단계];
                            요리[현재만들고있는요리][0]+=마법제조기생산[마법제조기단계];
                            재료[요리[현재만들고있는요리][4]][0]-=요리[현재만들고있는요리][5]*마법제조기생산[마법제조기단계];
                            재료[요리[현재만들고있는요리][6]][0]-=요리[현재만들고있는요리][7]*마법제조기생산[마법제조기단계];
                            재료[요리[현재만들고있는요리][8]][0]-=요리[현재만들고있는요리][9]*마법제조기생산[마법제조기단계];
                        }else{
                            생산텍스트애니(현재만들고있는요리,요리남은수량);
                            요리[현재만들고있는요리][0]+=요리남은수량;
                            재료[요리[현재만들고있는요리][4]][0]-=요리[현재만들고있는요리][5]*요리남은수량;
                            재료[요리[현재만들고있는요리][6]][0]-=요리[현재만들고있는요리][7]*요리남은수량;
                            재료[요리[현재만들고있는요리][8]][0]-=요리[현재만들고있는요리][9]*요리남은수량;
                            요리남은수량-=요리남은수량;


                        }
                        if(요리남은수량<=0){
                            로티레이[1].cancelAnimation();
                            요리중=0;
                        }
                        han.sendEmptyMessageDelayed(10,포장마차[포장마차단계][6]);
                    }else{
                        로티레이[1].cancelAnimation();
                    }
                   퀵슬롯전체업데이트(현재만들고있는요리);
                   레이2전체업데이트();
                    break;
                case 100 :
                    로티레이[0].cancelAnimation();
                    break;
                case 101 :
                    시스템텍스트애니("축하한다 뭉!"+'\n'+"단계 +1");
                    break;
                case 102 :
                    시스템텍스트애니("미안하다 뭉ㅠㅠ");
                    break;
                case 103 :
                    시스템텍스트애니("오히려 더 안 좋아졌다 뭉 ㅠㅠ"+'\n'+"단계 -1");
                    break;
                case 499:
                    로티애니(3);


                    break;
                case 500 : //결투시작




                    han.sendEmptyMessageDelayed(501,내요리속도/5);
                    han.sendEmptyMessageDelayed(502,상대요리속도/5);
                    승점-=5*승점/1000;
                    if(승점<1000){
                        승점=1000;
                    }
                    break;
                case 501 ://내채력
                    레이10작동2(1);


                    if (대결중 == 1) {
                        han.sendEmptyMessageDelayed(501,내요리속도/5);

                    }
                    break;
                case 502 ://상대채력
                    레이10작동2(0);


                    if (대결중 == 1) {
                        han.sendEmptyMessageDelayed(502,상대요리속도/5);

                    }
                    break;

                case 1000 :
                   int 손님출현시간=포장마차[포장마차단계][5]*1000- (포장마차[포장마차단계][5]*1000*날씨지수-50/100);
                    손님로테이션();
                    han.sendEmptyMessageDelayed(1000,포장마차[포장마차단계][5]*1000);
                    break;
                case 1001 :
                    대기손님텍스트.setText("웨이팅"+'\n'+대기손님+" 명");
                    han.removeMessages(1001);
                    손님출현();
                    break;
                case 10000 :
                    구글버튼[0].setVisibility(View.VISIBLE);
                    break;
                case 10001 :


                    han.sendEmptyMessageDelayed(10000,10000);
                    break;
            }
        }
    };
    public   void onPause(){
        super.onPause();
        로그아웃();
        서버저장하기();

    }
    public void onStop(){
        super.onStop();
        로그아웃();

    }

    public void onStart(){
        super.onStart();
        로그인();


    }

    public void onResume(){
        super.onResume();
        로그인();

        int a = 로딩화면레이아웃.getVisibility();
        if(a==8){
        로딩화면레이아웃.setVisibility(View.VISIBLE);
        로딩멘트텍스트.setText("출근 준비 중입니다");
        로딩프로그레스.setProgress(0);
        로딩텍스트.setText(""+로딩프로그레스.getProgress()+"%");
        han.sendEmptyMessageDelayed(0,1000);

        }
    }
    public RewardedAd createAndLoadRewardedAd() {
        RewardedAd rewardedAd = new RewardedAd(this,
                "ca-app-pub-8598333709375599/3163800292");
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                시스템텍스트애니("광고를 볼수있어!!");
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
        return rewardedAd;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.laymain);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //상태바없애기
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility(); //소프트바없애기
        int newUiOptions = uiOptions;
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
//튜토리얼파인드뷰==========================================================================
        튜토리얼레이=findViewById(R.id.튜토리얼레이);
        튜토리얼버튼레이=findViewById(R.id.튜토리얼버튼레이);
        튜토리얼이전=findViewById(R.id.튜토리얼이전);
        튜토리얼다음=findViewById(R.id.튜토리얼다음);
        튜토리얼이미지=findViewById(R.id.튜토리얼이미지);
        튜토리얼나가기=findViewById(R.id.튜토리얼나가기);
        튜토리얼체크=findViewById(R.id.튜토리얼체크);
//메인레이파인드뷰==========================================================================
        상위=findViewById(R.id.상위);
        상단=findViewById(R.id.상단);
        하단=findViewById(R.id.하단);
        터치존=findViewById(R.id.터치존);
        경험치레이아웃=findViewById(R.id.경험치레이아웃);
        요리하기레이아웃=findViewById(R.id.요리하기레이아웃);
        로딩화면레이아웃=findViewById(R.id.로딩화면레이아웃);
        최상위=findViewById(R.id.최상위);
        중단=findViewById(R.id.중단);
        손님레이아웃=findViewById(R.id.손님레이아웃);
        말풍선레이아웃=findViewById(R.id.말풍선레이아웃);
        로딩프로그레스=findViewById(R.id.로딩프로그레스);
        경험치프로그레스=findViewById(R.id.경험치프로그레스);
        메뉴스크롤=findViewById(R.id.메뉴스크롤);
        캐쉬스크롤=findViewById(R.id.캐쉬스크롤);
        퀵슬롯스크롤=findViewById(R.id.퀵슬롯스크롤);
        경험치텍스트=findViewById(R.id.경험치텍스트);
        레벨텍스트=findViewById(R.id.레벨텍스트);
        돈텍스트=findViewById(R.id.돈텍스트);명성텍스트=findViewById(R.id.명성텍스트);경제지수텍스트=findViewById(R.id.경제지수텍스트);
        날씨지수텍스트=findViewById(R.id.날씨지수텍스트);손님이름텍스트=findViewById(R.id.손님이름텍스트);터치유도텍스트=findViewById(R.id.터치유도텍스트);
        말풍선텍스트=findViewById(R.id.말풍선텍스트);대기손님텍스트=findViewById(R.id.대기손님텍스트);시스템텍스트=findViewById(R.id.시스템텍스트);시스템텍스트2=findViewById(R.id.시스템텍스트2);
        생산텍스트=findViewById(R.id.생산텍스트);로딩텍스트=findViewById(R.id.로딩텍스트);로딩멘트텍스트=findViewById(R.id.로딩멘트텍스트);
        배경이미지=findViewById(R.id.배경이미지);
        for(int i=0;i<3;i++){
        주문텍스트[i]=findViewById(메인id[0][i]);
        구글버튼[i]=findViewById(메인id[1][i]);
        }
        구글버튼[3]=findViewById(R.id.구글버튼4);
        구글버튼[4]=findViewById(R.id.구글버튼5);
        손님이미지=findViewById(R.id.손님이미지);요리하기버튼이미지=findViewById(R.id.요리하기버튼이미지);말풍선이미지=findViewById(R.id.말풍선이미지);
        요리하기버튼=findViewById(R.id.요리하기버튼);
        for(int i=0;i<8;i++){메뉴버튼[i]=findViewById(메인id[2][i]);}
        for(int i=0;i<10;i++){메인레이정보텍스트[i]=findViewById(메인id[5][i]);}
        for(int i=0;i<14;i++){레이[i]=findViewById(메인id[4][i]);}
        for(int i=0;i<48;i++){퀵슬롯버튼[i]=findViewById(메인id[3][i]);퀵슬롯레이[i]=findViewById(메인id[6][i]);}
        레이인포레이=findViewById(R.id.내정보레이);
        레이도움말레이=findViewById(R.id.도움말레이);
        로티레이[0]=findViewById(R.id.animation_view);
        로티레이[1]=findViewById(R.id.animation_view2);
        로티레이[2]=findViewById(R.id.animation_view);
        로티레이[3]=findViewById(R.id.레이10로티레이);

//레이1파인드뷰==========================================================================
        레이1상위=findViewById(R.id.레이1상위);
        레이1나가기=findViewById(R.id.레이1나가기);
        레이1작동버튼=findViewById(R.id.레이1작동버튼);
        레이1식바=findViewById(R.id.레이1식바);
        레이1식바수량텍스트=findViewById(R.id.레이1식바수량텍스트1);
        for(int i=0;i<48;i++){
        레이1요리정보레이[i]=findViewById(레이1id[0][i]);
        레이1요리재료레이[i]=findViewById(레이1id[1][i]);
        요리스크롤[i]=findViewById(레이1id[2][i]);
        레이1요리이미지레이[i]=findViewById(레이1id[3][i]);
        레이1요리버튼레이[i]=findViewById(레이1id[4][i]);
        레이1선택버튼[i]=findViewById(레이1id[5][i]);
        레이1요리이미지[i]=findViewById(레이1id[6][i]);
        레이1요리이름텍스트[i]=findViewById(레이1id[7][i]);
        레이1요리가격텍스트[i]=findViewById(레이1id[8][i]);
        레이1요리마진텍스트[i]=findViewById(레이1id[9][i]);
        레이1요리재료1[i]=findViewById(레이1id[10][i]);
        레이1요리재료2[i]=findViewById(레이1id[11][i]);
        레이1요리재료3[i]=findViewById(레이1id[12][i]);
        }
//레이2파인드뷰==========================================================================
        레이2상위=findViewById(R.id.레이2상위);
        레이2나가기=findViewById(R.id.레이2나가기);
        레이2작동버튼=findViewById(R.id.레이2작동버튼);
        레이2식바=findViewById(R.id.레이2식바);
        레이2식바수량텍스트=findViewById(R.id.레이2식바수량텍스트1);
        for(int i=0;i<10;i++){
            레이2요리정보레이[i]=findViewById(레이2id[0][i]);
            장보기스크롤[i]=findViewById(레이2id[1][i]);
            레이2요리이미지레이[i]=findViewById(레이2id[2][i]);
            레이2요리버튼레이[i]=findViewById(레이2id[3][i]);
            레이2선택버튼[i]=findViewById(레이2id[4][i]);
            레이2요리이미지[i]=findViewById(레이2id[5][i]);
            레이2요리이름텍스트[i]=findViewById(레이2id[6][i]);
            레이2요리가격텍스트[i]=findViewById(레이2id[7][i]);
            레이2요리보유텍스트[i]=findViewById(레이2id[8][i]);
        }
//레이3파인드뷰==========================================================================
        레이3상위=findViewById(R.id.레이3상위);
        레이3정보레이=findViewById(R.id.레이3정보레이);
        레이3이미지레이=findViewById(R.id.레이3이미지레이);
        레이3이미지=findViewById(R.id.레이3이미지);
        레이3나가기=findViewById(R.id.레이3나가기);
        레이3작동버튼=findViewById(R.id.레이3작동버튼);
        for(int i=0;i<10;i++){
        레이3정보텍스트[i]=findViewById(레이3id[0][i]);
        }
//레이4파인드뷰==========================================================================
        레이4상위=findViewById(R.id.레이4상위);
        레이4정보레이=findViewById(R.id.레이4정보레이);
        레이4이미지레이=findViewById(R.id.레이4이미지레이);
        레이4이미지=findViewById(R.id.레이4이미지);
        레이4나가기=findViewById(R.id.레이4나가기);
        레이4작동버튼=findViewById(R.id.레이4작동버튼);
        레이4스위치1=findViewById(R.id.레이4스위치1);
        레이4스위치2=findViewById(R.id.레이4스위치2);
        레이4식바=findViewById(R.id.레이4식바);
        for(int i=0;i<5;i++){
            레이4정보텍스트[i]=findViewById(레이4id[0][i]);
        }
//레이5파인드뷰==========================================================================
        레이5상위=findViewById(R.id.레이5상위);
        레이5정보레이=findViewById(R.id.레이5정보레이);
        레이5이미지레이=findViewById(R.id.레이5이미지레이);
        레이5이미지=findViewById(R.id.레이5이미지);
        레이5나가기=findViewById(R.id.레이5나가기);
        레이5작동버튼=findViewById(R.id.레이5작동버튼);
        레이5스위치1=findViewById(R.id.레이5스위치1);
        레이5스위치2=findViewById(R.id.레이5스위치2);
        for(int i=0;i<5;i++){
            레이5정보텍스트[i]=findViewById(레이5id[0][i]);
        }
//레이6파인드뷰==========================================================================
        레이6상위=findViewById(R.id.레이6상위);
        레이6정보레이=findViewById(R.id.레이6정보레이);
        레이6이미지레이=findViewById(R.id.레이6이미지레이);
        레이6이미지=findViewById(R.id.레이6이미지);
        레이6나가기=findViewById(R.id.레이6나가기);
        레이6작동버튼=findViewById(R.id.레이6작동버튼);
        레이6스위치1=findViewById(R.id.레이6스위치1);
        레이6스위치2=findViewById(R.id.레이6스위치2);
        레이6식바=findViewById(R.id.레이6식바);
        for(int i=0;i<6;i++){
            레이6정보텍스트[i]=findViewById(레이6id[0][i]);
        }
//레이7파인드뷰==========================================================================
        레이7상위=findViewById(R.id.레이7상위);
        레이7나가기=findViewById(R.id.레이7나가기);
        레이7작동버튼=findViewById(R.id.레이7작동버튼);
        레이7식바=findViewById(R.id.레이7식바);
        for(int i=0;i<9;i++){
            레이7정보텍스트[i]=findViewById(레이7id[0][i]);
        }
//레이8파인드뷰==========================================================================
        레이8상위=findViewById(R.id.레이8상위);
        레이8정보레이=findViewById(R.id.레이8정보레이);
        레이8이미지레이=findViewById(R.id.레이8이미지레이);
        레이8이미지=findViewById(R.id.레이8이미지);
        레이8나가기=findViewById(R.id.레이8나가기);
        레이8작동버튼=findViewById(R.id.레이8작동버튼);
        레이8식바=findViewById(R.id.레이8식바);
        for(int i=0;i<4;i++){
            레이8정보텍스트[i]=findViewById(레이8id[0][i]);
        }
//레이9파인드뷰==========================================================================
        레이9상위=findViewById(R.id.레이9상위);
        레이9정보레이=findViewById(R.id.레이9정보레이);
        레이9이미지레이=findViewById(R.id.레이9이미지레이);
        레이9이미지=findViewById(R.id.레이9이미지);
        레이9나가기=findViewById(R.id.레이9나가기);
        레이9작동버튼=findViewById(R.id.레이9작동버튼);
        레이9작동버튼2=findViewById(R.id.레이9작동버튼2);
        레이9식바=findViewById(R.id.레이9식바);
        for(int i=0;i<5;i++){
            레이9정보텍스트[i]=findViewById(레이9id[0][i]);
        }
//레이10파인드뷰==========================================================================
        레이10상위=findViewById(R.id.레이10상위);
        레이10정보레이1=findViewById(R.id.레이10정보레이1);
        레이10정보레이2=findViewById(R.id.레이10정보레이2);
        레이10이미지레이1=findViewById(R.id.레이10이미지레이1);
        레이10이미지레이2=findViewById(R.id.레이10이미지레이2);
        레이랭크이미지1=findViewById(R.id.레이랭크이미지1);
        레이랭크이미지2=findViewById(R.id.레이랭크이미지2);
        레이랭크이미지3=findViewById(R.id.레이랭크이미지3);
        레이10이미지1=findViewById(R.id.레이10이미지1);
        레이10이미지2=findViewById(R.id.레이10이미지2);
        레이10프로그레스1=findViewById(R.id.레이10프로그레스1);
        레이10프로그레스2=findViewById(R.id.레이10프로그레스2);
        레이10나가기=findViewById(R.id.레이10나가기);
        레이10작동버튼=findViewById(R.id.레이10작동버튼);
        레이10작동버튼2=findViewById(R.id.레이10작동버튼2);
        레이10작동버튼3=findViewById(R.id.레이10작동버튼3);
        랭크1레이=findViewById(R.id.랭크1레이);
        랭크2레이=findViewById(R.id.랭크2레이);
        레이랭크나가기=findViewById(R.id.레이랭크나가기);
        레이랭크2나가기=findViewById(R.id.레이랭크2나가기);
        for(int i=0;i<10;i++){
            레이10정보텍스트[i]=findViewById(레이10id[0][i]);

        }
        for(int i=0;i<18;i++){
            레이랭크2정보레이[i]=findViewById(레이10id[1][i]);
            레이랭크2이미지[i]=findViewById(레이10id[2][i]);
        }
        for(int i=0;i<20;i++){
            레이랭크1정보텍스트[i]=findViewById(레이10id[3][i]);
        }
//레이11파인드뷰==========================================================================
        레이11상위=findViewById(R.id.레이11상위);
        레이11정보레이=findViewById(R.id.레이11정보레이);
        레이11이미지레이=findViewById(R.id.레이11이미지레이);
        레이11이미지=findViewById(R.id.레이11이미지);
        레이11나가기=findViewById(R.id.레이11나가기);
        레이11작동버튼=findViewById(R.id.레이11작동버튼);
        for(int i=0;i<6;i++){
            레이11정보텍스트[i]=findViewById(레이11id[0][i]);
        }
//레이12파인드뷰==========================================================================
        레이12상위=findViewById(R.id.레이12상위);
        레이12정보레이=findViewById(R.id.레이12정보레이);
        레이12작동버튼=findViewById(R.id.레이12작동버튼);
        for(int i=0;i<4;i++){
            레이12정보텍스트[i]=findViewById(레이12id[0][i]);
        }
//레이13파인드뷰==========================================================================
        레이13상위=findViewById(R.id.레이13상위);
        레이13정보레이=findViewById(R.id.레이13정보레이);
        레이13작동버튼1=findViewById(R.id.레이13작동버튼1);
        레이13작동버튼2=findViewById(R.id.레이13작동버튼2);
        레이13정보텍스트=findViewById(R.id.레이13정보텍스트1);
//레이14파인드뷰==========================================================================
        레이14상위=findViewById(R.id.레이14상위);
        레이14정보레이=findViewById(R.id.레이14정보레이);
        레이14작동버튼=findViewById(R.id.레이14작동버튼);
        레이14정보텍스트=findViewById(R.id.레이14정보텍스트1);
//레이인포파인드뷰==========================================================================
        for(int i=0;i<11;i++){
        레이인포정보텍스트[i]=findViewById(레이인포id[i]);
        }
        레이인포나가기=findViewById(R.id.레이인포나가기);
        도움말나가기=findViewById(R.id.도움말나가기);
        han.sendEmptyMessageDelayed(1000,포장마차[포장마차단계][5]*1000);
    }




    void 레벨업(){


    }
    void 소지금업데이트(){
        double 경험치비율=(double)((double)경험치/(double)레벨별[레벨][0]*100);
        if(경험치비율>=100&&레벨<100){
            경험치=경험치-레벨별[레벨][0];
            레벨++;
            경험치비율=(double)((double)경험치/(double)레벨별[레벨][0]*100);

            경험치텍스트.setText(""+  String.format("%.2f",경험치비율)+"%");
            레벨텍스트.setText("Level "+레벨);
            경험치프로그레스.setProgress((int)경험치비율);
            로티애니(2);
        }else if(레벨 ==100){
            경험치=0;
        }

        경험치프로그레스.setProgress((int)경험치비율);
        돈텍스트.setText("Money "+String.format("%,d",소지금));
        명성텍스트.setText("Fame "+String.format("%,d",명성));
        경험치텍스트.setText(""+  String.format("%.2f",경험치비율)+"%");
        레벨텍스트.setText("Level "+레벨);
    }
    void 터치(){

        final Animation mAni1[]= new Animation[10];
        for(int i=0;i<10;i++){
        mAni1[i] = AnimationUtils.loadAnimation(this, R.anim.allani3);

        }
        mAni1[0].setAnimationListener(new Animation.AnimationListener(){
            public void onAnimationEnd(Animation animation){
                메인레이정보텍스트[0].setVisibility(INVISIBLE);}
            public void onAnimationStart(Animation animation){
                메인레이정보텍스트[0].setVisibility(View.VISIBLE);}
            public void onAnimationRepeat(Animation animation){;}
        });
        mAni1[1].setAnimationListener(new Animation.AnimationListener(){
            public void onAnimationEnd(Animation animation){
                메인레이정보텍스트[1].setVisibility(INVISIBLE);}
            public void onAnimationStart(Animation animation){
                메인레이정보텍스트[1].setVisibility(View.VISIBLE);}
            public void onAnimationRepeat(Animation animation){;}
        });
        mAni1[2].setAnimationListener(new Animation.AnimationListener(){
            public void onAnimationEnd(Animation animation){
                메인레이정보텍스트[2].setVisibility(INVISIBLE);}
            public void onAnimationStart(Animation animation){
                메인레이정보텍스트[2].setVisibility(View.VISIBLE);}
            public void onAnimationRepeat(Animation animation){;}
        });
        mAni1[3].setAnimationListener(new Animation.AnimationListener(){
            public void onAnimationEnd(Animation animation){
                메인레이정보텍스트[3].setVisibility(INVISIBLE);}
            public void onAnimationStart(Animation animation){
                메인레이정보텍스트[3].setVisibility(View.VISIBLE);}
            public void onAnimationRepeat(Animation animation){;}
        });
        mAni1[4].setAnimationListener(new Animation.AnimationListener(){
            public void onAnimationEnd(Animation animation){
                메인레이정보텍스트[4].setVisibility(INVISIBLE);}
            public void onAnimationStart(Animation animation){
                메인레이정보텍스트[4].setVisibility(View.VISIBLE);}
            public void onAnimationRepeat(Animation animation){;}
        });
        mAni1[5].setAnimationListener(new Animation.AnimationListener(){
            public void onAnimationEnd(Animation animation){
                메인레이정보텍스트[5].setVisibility(INVISIBLE);}
            public void onAnimationStart(Animation animation){
                메인레이정보텍스트[5].setVisibility(View.VISIBLE);}
            public void onAnimationRepeat(Animation animation){;}
        });
        mAni1[6].setAnimationListener(new Animation.AnimationListener(){
            public void onAnimationEnd(Animation animation){
                메인레이정보텍스트[6].setVisibility(INVISIBLE);}
            public void onAnimationStart(Animation animation){
                메인레이정보텍스트[6].setVisibility(View.VISIBLE);}
            public void onAnimationRepeat(Animation animation){;}
        });
        mAni1[7].setAnimationListener(new Animation.AnimationListener(){
            public void onAnimationEnd(Animation animation){
                메인레이정보텍스트[7].setVisibility(INVISIBLE);}
            public void onAnimationStart(Animation animation){
                메인레이정보텍스트[7].setVisibility(View.VISIBLE);}
            public void onAnimationRepeat(Animation animation){;}
        });
        mAni1[8].setAnimationListener(new Animation.AnimationListener(){
            public void onAnimationEnd(Animation animation){
                메인레이정보텍스트[8].setVisibility(INVISIBLE);}
            public void onAnimationStart(Animation animation){
                메인레이정보텍스트[8].setVisibility(View.VISIBLE);}
            public void onAnimationRepeat(Animation animation){;}
        });
        mAni1[9].setAnimationListener(new Animation.AnimationListener(){
            public void onAnimationEnd(Animation animation){
                메인레이정보텍스트[9].setVisibility(INVISIBLE);}
            public void onAnimationStart(Animation animation){
                메인레이정보텍스트[9].setVisibility(View.VISIBLE);}
            public void onAnimationRepeat(Animation animation){;}
        });

        터치존.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        long 판매가=요리가격[현재줄요리][요리[현재줄요리][12]]+(long)(요리가격[현재줄요리][요리[현재줄요리][12]]*(경제지수-50)*0.002);;
                        if(판매중==1){
                        메인레이정보텍스트[터치순서].setX( event.getX()-(메인레이정보텍스트[0].getLayoutParams().width/2));
                        메인레이정보텍스트[터치순서].setY( event.getY()-(메인레이정보텍스트[0].getLayoutParams().height/2));
                        메인레이정보텍스트[터치순서].startAnimation(mAni1[터치순서]);

                        if(현재줄요리==주문음식[0]){
                            if(요리[현재줄요리][0]>0){
                                if(주문갯수[0]>0){
                                    메인레이정보텍스트[터치순서].setTextColor(Color.parseColor("#FFA923"));
                                    메인레이정보텍스트[터치순서].setText("Money + "+String.format("%,d",판매가*판매금이벤트)+'\n'+"Exp + "+String.format("%,d",요리[현재줄요리][10]*경험치이벤트));
                                    요리[현재줄요리][0]--;
                                    주문갯수[0]--;
                                    소지금+=판매가*판매금이벤트;
                                    경험치+=요리[현재줄요리][10]*경험치이벤트;
                                    주문텍스트[0].setText(""+요리이름[주문음식[0]]+'\n'+주문갯수[0]+" 개");
                                    퀵슬롯단일업데이트(현재줄요리);
                                        if(주문갯수[0]<=0){
                                        퀵슬롯버튼[주문음식[0]].setTextColor(Color.parseColor("#FFFFFF"));
                                            레이1요리이름텍스트[주문음식[0]].setTextColor(Color.parseColor("#FFFFFF"));
                                        }
                                }else{
                                    메인레이정보텍스트[터치순서].setTextColor(Color.RED);
                                    메인레이정보텍스트[터치순서].setText("이 음식은 다 나왔어요!!");
                                }
                            }else{
                                메인레이정보텍스트[터치순서].setTextColor(Color.RED);
                                메인레이정보텍스트[터치순서].setText("재고를 확인해 주세요!!");
                            }
                        }else if(현재줄요리==주문음식[1]){

                            if(요리[현재줄요리][0]>0){
                                if(주문갯수[1]>0){
                                    메인레이정보텍스트[터치순서].setTextColor(Color.parseColor("#FFA923"));
                                    메인레이정보텍스트[터치순서].setText("Money + "+String.format("%,d",판매가*판매금이벤트)+'\n'+"Exp + "+String.format("%,d",요리[현재줄요리][10]*경험치이벤트));
                                    요리[현재줄요리][0]--;
                                주문갯수[1]--;
                                    소지금+=판매가*판매금이벤트;
                                    경험치+=요리[현재줄요리][10]*경험치이벤트;
                                    퀵슬롯단일업데이트(현재줄요리);
                                주문텍스트[1].setText(""+요리이름[주문음식[1]]+'\n'+주문갯수[1]+" 개");
                                    if(주문갯수[1]<=0){
                                        퀵슬롯버튼[주문음식[1]].setTextColor(Color.parseColor("#FFFFFF"));
                                        레이1요리이름텍스트[주문음식[1]].setTextColor(Color.parseColor("#FFFFFF"));
                                    }
                                }else{
                                    메인레이정보텍스트[터치순서].setTextColor(Color.RED);
                                    메인레이정보텍스트[터치순서].setText("이 음식은 다 나왔어요!!");
                                }
                            }else{
                                메인레이정보텍스트[터치순서].setTextColor(Color.RED);
                                메인레이정보텍스트[터치순서].setText("재고를 확인해 주세요!!");
                            }
                        }else if(현재줄요리==주문음식[2]){
                            if(요리[현재줄요리][0]>0){
                                if(주문갯수[2]>0){
                                    메인레이정보텍스트[터치순서].setTextColor(Color.parseColor("#FFA923"));
                                    메인레이정보텍스트[터치순서].setText("Money + "+String.format("%,d",판매가*판매금이벤트)+'\n'+"Exp + "+String.format("%,d",요리[현재줄요리][10]*경험치이벤트));요리[현재줄요리][0]--;
                                    주문갯수[2]--;
                                    소지금+=판매가*판매금이벤트;
                                    경험치+=요리[현재줄요리][10]*경험치이벤트;
                                    퀵슬롯단일업데이트(현재줄요리);
                                    주문텍스트[2].setText(""+요리이름[주문음식[2]]+'\n'+주문갯수[2]+" 개");
                                    if(주문갯수[2]<=0){
                                        퀵슬롯버튼[주문음식[2]].setTextColor(Color.parseColor("#FFFFFF"));
                                        레이1요리이름텍스트[주문음식[2]].setTextColor(Color.parseColor("#FFFFFF"));
                                    }
                                }else{
                                    메인레이정보텍스트[터치순서].setTextColor(Color.RED);
                                    메인레이정보텍스트[터치순서].setText("이 음식은 다 나왔어요!!");
                                }
                            }else{
                                메인레이정보텍스트[터치순서].setTextColor(Color.RED);
                                메인레이정보텍스트[터치순서].setText("재고를 확인해 주세요!!");
                            }
                        }else{
                            if(요리[현재줄요리][0]>0){
                                메인레이정보텍스트[터치순서].setTextColor(Color.parseColor("#8BC34A"));
                                메인레이정보텍스트[터치순서].setText("Fame + "+String.format("%,d",요리[현재줄요리][10]*명성이벤트));
                                요리[현재줄요리][0]--;
                                퀵슬롯단일업데이트(현재줄요리);
                                명성+=요리[현재줄요리][10]*명성이벤트;
                            }else{
                                메인레이정보텍스트[터치순서].setTextColor(Color.RED);
                                메인레이정보텍스트[터치순서].setText("재고를 확인해 주세요!!");
                            }
                        }


                        if(주문갯수[0]==0&&주문갯수[1]==0&&주문갯수[2]==0){
                            for(int i=0;i<종류+1;i++){
                            퀵슬롯버튼[주문음식[i]].setTextColor(Color.parseColor("#FFFFFF"));
                            레이1요리이름텍스트[주문음식[i]].setTextColor(Color.parseColor("#FFFFFF"));
                                레이1작동버튼.setVisibility(View.VISIBLE);
                                저장하기();
                            }

                            손님초기화();
                        }
                        터치순서++;
                        if(터치순서==10){
                            터치순서=0;
                        }
                            소지금업데이트();
                        }

                        break;
                    case MotionEvent.ACTION_MOVE :
                    case MotionEvent.ACTION_UP :
                        break;
                }
                return false;
            }
        });



        메뉴버튼[0].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                    Animation 원샷애니 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sizeone);
                    Animation 원샷백애니 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sizeoneback);
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        메뉴버튼[0].startAnimation(원샷애니);
                        break;
                    case MotionEvent.ACTION_MOVE :
                    case MotionEvent.ACTION_UP :
                        메뉴버튼[0].startAnimation(원샷백애니);
                        break;
                }
                return false;
            }
        });
        메뉴버튼[1].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                Animation 원샷애니 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sizeone);
                Animation 원샷백애니 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sizeoneback);
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        메뉴버튼[1].startAnimation(원샷애니);
                        break;
                    case MotionEvent.ACTION_MOVE :
                    case MotionEvent.ACTION_UP :
                        메뉴버튼[1].startAnimation(원샷백애니);
                        break;
                }
                return false;
            }
        });
        메뉴버튼[2].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                Animation 원샷애니 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sizeone);
                Animation 원샷백애니 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sizeoneback);
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        메뉴버튼[2].startAnimation(원샷애니);
                        break;
                    case MotionEvent.ACTION_MOVE :
                    case MotionEvent.ACTION_UP :
                        메뉴버튼[2].startAnimation(원샷백애니);
                        break;
                }
                return false;
            }
        });
        메뉴버튼[3].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                Animation 원샷애니 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sizeone);
                Animation 원샷백애니 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sizeoneback);
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        메뉴버튼[3].startAnimation(원샷애니);
                        break;
                    case MotionEvent.ACTION_MOVE :
                    case MotionEvent.ACTION_UP :
                        메뉴버튼[3].startAnimation(원샷백애니);
                        break;
                }
                return false;
            }
        });
        메뉴버튼[4].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                Animation 원샷애니 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sizeone);
                Animation 원샷백애니 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sizeoneback);
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        메뉴버튼[4].startAnimation(원샷애니);
                        break;
                    case MotionEvent.ACTION_MOVE :
                    case MotionEvent.ACTION_UP :
                        메뉴버튼[4].startAnimation(원샷백애니);
                        break;
                }
                return false;
            }
        });
        메뉴버튼[5].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                Animation 원샷애니 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sizeone);
                Animation 원샷백애니 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sizeoneback);
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        메뉴버튼[5].startAnimation(원샷애니);
                        break;
                    case MotionEvent.ACTION_MOVE :
                    case MotionEvent.ACTION_UP :
                        메뉴버튼[5].startAnimation(원샷백애니);
                        break;
                }
                return false;
            }
        });
        메뉴버튼[6].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                Animation 원샷애니 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sizeone);
                Animation 원샷백애니 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sizeoneback);
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        메뉴버튼[6].startAnimation(원샷애니);
                        break;
                    case MotionEvent.ACTION_MOVE :
                    case MotionEvent.ACTION_UP :
                        메뉴버튼[6].startAnimation(원샷백애니);
                        break;
                }
                return false;
            }
        });
        메뉴버튼[7].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                Animation 원샷애니 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sizeone);
                Animation 원샷백애니 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sizeoneback);
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        메뉴버튼[7].startAnimation(원샷애니);
                        break;
                    case MotionEvent.ACTION_MOVE :
                    case MotionEvent.ACTION_UP :
                        메뉴버튼[7].startAnimation(원샷백애니);
                        break;
                }
                return false;
            }
        });
        구글버튼[0].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                Animation 원샷애니 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sizeone);
                Animation 원샷백애니 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sizeoneback);
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        구글버튼[0].startAnimation(원샷애니);
                        break;
                    case MotionEvent.ACTION_MOVE :
                    case MotionEvent.ACTION_UP :
                        구글버튼[0].startAnimation(원샷백애니);
                        break;
                }
                return false;
            }
        });
        구글버튼[1].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                Animation 원샷애니 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sizeone);
                Animation 원샷백애니 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sizeoneback);
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        구글버튼[1].startAnimation(원샷애니);
                        break;
                    case MotionEvent.ACTION_MOVE :
                    case MotionEvent.ACTION_UP :
                        구글버튼[1].startAnimation(원샷백애니);
                        break;
                }
                return false;
            }
        });
        구글버튼[2].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                Animation 원샷애니 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sizeone);
                Animation 원샷백애니 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sizeoneback);
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        구글버튼[2].startAnimation(원샷애니);
                        break;
                    case MotionEvent.ACTION_MOVE :
                    case MotionEvent.ACTION_UP :
                        구글버튼[2].startAnimation(원샷백애니);
                        break;
                }
                return false;
            }
        });
        구글버튼[3].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                Animation 원샷애니 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sizeone);
                Animation 원샷백애니 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sizeoneback);
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        구글버튼[3].startAnimation(원샷애니);
                        break;
                    case MotionEvent.ACTION_MOVE :
                    case MotionEvent.ACTION_UP :
                        구글버튼[3].startAnimation(원샷백애니);
                        break;
                }
                return false;
            }
        });
        구글버튼[4].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                Animation 원샷애니 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sizeone);
                Animation 원샷백애니 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sizeoneback);
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        구글버튼[4].startAnimation(원샷애니);
                        break;
                    case MotionEvent.ACTION_MOVE :
                    case MotionEvent.ACTION_UP :
                        구글버튼[4].startAnimation(원샷백애니);
                        break;
                }
                return false;
            }
        });
    }
    void 손님초기화(){
        for(int i=0;i<3;i++){
            주문텍스트[i].setText("");
            주문갯수[i]=0;

            주문음식[i]=100;
            말풍선텍스트.setText("");
        }

        판매중=0;
        if(대기손님>0){
            대기손님--;
            대기손님텍스트.setText("웨이팅"+'\n'+대기손님+" 명");
            han.sendEmptyMessageDelayed(1001,2000);
        }
        Animation mAni1;
        mAni1 = AnimationUtils.loadAnimation(this, R.anim.alpha4);
        터치존.startAnimation(mAni1);
        mAni1.setAnimationListener(new Animation.AnimationListener(){
            public void onAnimationEnd(Animation animation){
                터치존.setVisibility(INVISIBLE);}
            public void onAnimationStart(Animation animation){
                터치존.setVisibility(View.VISIBLE);}
            public void onAnimationRepeat(Animation animation){;}
        });

    }
    void 손님로테이션(){

       if(판매중==1){
           대기손님++;
           if(대기손님>포장마차[포장마차단계][7]){
               대기손님=포장마차[포장마차단계][7];
           }
           대기손님텍스트.setText("웨이팅"+'\n'+대기손님+" 명");
       }else{
           han.sendEmptyMessageDelayed(1001,2000);
       }

    }

    void 손님출현(){
        판매중=1;
        종류 =(int)(Math.random()*3);

        int 캐릭터랜덤 = (int)(Math.random()*(포장마차[포장마차단계][10]+1));
        int 캐릭터대사랜덤 =(int)(Math.random()*15);
        손님이름텍스트.setText(""+캐릭터이름[캐릭터랜덤]);
        if(손님레이아웃.getLayoutParams().height>=1024){
            손님이미지.setBackgroundResource(캐릭터이미지fhd[캐릭터랜덤]);
        }else if(손님레이아웃.getLayoutParams().height>=512){
            손님이미지.setBackgroundResource(캐릭터이미지hd[캐릭터랜덤]);
        }else{
            손님이미지.setBackgroundResource(캐릭터이미지sd[캐릭터랜덤]);
        }
        말풍선텍스트.setText(""+캐릭터대사[캐릭터대사랜덤]);
        for(int i=0;i<(종류+1);i++){
            주문음식[i]=(int)(Math.random()*(포장마차[포장마차단계][9]+1));
            주문갯수[i]=(int)(Math.random()*(포장마차[포장마차단계][4]+캐릭터[캐릭터랜덤][2]-캐릭터[캐릭터랜덤][1])+(캐릭터[캐릭터랜덤][1]+1));
            for(int j=0;j<i;j++){
                if(주문음식[i]==주문음식[j]){
                   i--;  break;  }

            }
            주문텍스트[i].setText(""+요리이름[주문음식[i]]+'\n'+주문갯수[i]+" 개");
            레이1요리이름텍스트[주문음식[i]].setTextColor(Color.RED);
            퀵슬롯버튼[주문음식[i]].setTextColor(Color.RED);
            손님애니();
        }

    }
    void 레이13작동(){
        저장하기();
    }
    void 레이12작동(){
        저장하기();
    }

    void 레이11작동2(){
        보상상태=1;
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
        if(보상상태==1){
        이벤트번호=Integer.parseInt(snapshot.child("이벤트번호").getValue().toString());
            prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
            SharedPreferences.Editor editor =  prefs.edit();
            editor.putInt("이벤트번호",이벤트번호);
            editor.commit();
            보상상태=0;
        }
        }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {}});
        퀵슬롯전체업데이트(48);
        저장하기();
    }
    void 레이11작동1(){
        switch (현재보상){
            case 0:
                for(int i=현재보상*5;i<(현재보상+1)*5;i++){요리[i][0]+=현재보상수량;}
                현재보상=1000;
                레이11작동2();
                break;
            case 1:
                for(int i=현재보상*5;i<(현재보상+1)*5;i++){요리[i][0]+=현재보상수량;}
                현재보상=1000;
                 레이11작동2();
                break;
            case 2:
                for(int i=현재보상*5;i<(현재보상+1)*5;i++){요리[i][0]+=현재보상수량;}
                현재보상=1000;
                 레이11작동2();
                break;
            case 3:
                for(int i=현재보상*5;i<(현재보상+1)*5;i++){요리[i][0]+=현재보상수량;}
                현재보상=1000;
                 레이11작동2();
                break;
            case 4:
                for(int i=현재보상*5;i<(현재보상+1)*5;i++){요리[i][0]+=현재보상수량;}
                현재보상=1000;
                 레이11작동2();
                break;
            case 5:
                for(int i=현재보상*5;i<(현재보상+1)*5;i++){요리[i][0]+=현재보상수량;}
                현재보상=1000;
                 레이11작동2();
                break;
            case 6:
                for(int i=현재보상*5;i<(현재보상+1)*5;i++){요리[i][0]+=현재보상수량;}
                현재보상=1000;
                 레이11작동2();
                break;
            case 7:
                for(int i=현재보상*5;i<(현재보상+1)*5;i++){요리[i][0]+=현재보상수량;}
                현재보상=1000;
                 레이11작동2();
                break;
            case 8:
                for(int i=현재보상*5;i<(현재보상+1)*5;i++){요리[i][0]+=현재보상수량; }
                현재보상=1000;
                 레이11작동2();
                break;
            case 9:
                for(int i=현재보상*5;i<(현재보상+1)*5-2;i++){요리[i][0]+=현재보상수량; }
                현재보상=1000;
                 레이11작동2();
                break;
            case 10:
                for(int i=0;i<48;i++){요리[i][0]+=현재보상수량; }
                현재보상=1000;
                 레이11작동2();
                break;
            case 11:
                소지금+=현재보상수량;
                현재보상=1000;
                 레이11작동2();
                break;
            case 12:
                명성+=현재보상수량;
                현재보상=1000;
                 레이11작동2();
                break;
            case 13:
                명성+=현재보상수량;
                소지금+=현재보상수량;
                현재보상=1000;
                 레이11작동2();
                break;
            case 14:
                솜+=현재보상수량;
                현재보상=1000;
                 레이11작동2();
                break;
            case 15:
                강화보조제+=현재보상수량;
                현재보상=1000;
                 레이11작동2();
                break;
            case 16:
                강화보호제+=현재보상수량;
                현재보상=1000;
                 레이11작동2();
                break;
            case 17:
                강화보조제+=현재보상수량;
                강화보호제+=현재보상수량;
                현재보상=1000;
                 레이11작동2();
                break;
            default:
                break;}
                레이11전체업데이트();
                레이9전체업데이트();
                소지금업데이트();
        저장하기();
    }

    void a(){


    }
    void 레이랭크작동(){

        유저정보.collection("rank")
                .whereEqualTo("capital", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                레이랭크1정보텍스트[0].setText(""+document.getData().get("1위아이디"));
                                레이랭크1정보텍스트[1].setText(""+document.getData().get("2위아이디"));
                                레이랭크1정보텍스트[2].setText(""+document.getData().get("3위아이디"));
                                레이랭크1정보텍스트[3].setText(""+document.getData().get("4위아이디"));
                                레이랭크1정보텍스트[4].setText(""+document.getData().get("5위아이디"));
                                레이랭크1정보텍스트[5].setText(""+document.getData().get("6위아이디"));
                                레이랭크1정보텍스트[6].setText(""+document.getData().get("7위아이디"));
                                레이랭크1정보텍스트[7].setText(""+document.getData().get("8위아이디"));
                                레이랭크1정보텍스트[8].setText(""+document.getData().get("9위아이디"));
                                레이랭크1정보텍스트[9].setText(""+document.getData().get("10위아이디"));
                                레이랭크1정보텍스트[10].setText(""+document.getData().get("1위승점"));
                                레이랭크1정보텍스트[11].setText(""+document.getData().get("2위승점"));
                                레이랭크1정보텍스트[12].setText(""+document.getData().get("3위승점"));
                                레이랭크1정보텍스트[13].setText(""+document.getData().get("4위승점"));
                                레이랭크1정보텍스트[14].setText(""+document.getData().get("5위승점"));
                                레이랭크1정보텍스트[15].setText(""+document.getData().get("6위승점"));
                                레이랭크1정보텍스트[16].setText(""+document.getData().get("7위승점"));
                                레이랭크1정보텍스트[17].setText(""+document.getData().get("8위승점"));
                                레이랭크1정보텍스트[18].setText(""+document.getData().get("9위승점"));
                                레이랭크1정보텍스트[19].setText(""+document.getData().get("10위승점"));

                            }
                        } else {

                        }
                    }
                });

    }
    void 레이10결투보상(){
        if(승점>=15000){
            시스템텍스트애니("여기 300솜이야 "+'\n'+"정말 최고의 실력자구나!!");
            솜+=300;
        }else if(승점>=14000){
            시스템텍스트애니("여기 200솜이야 "+'\n'+"솜씨가 엄청나네!!");
            솜+=200;
        }else if(승점>=13000){
            시스템텍스트애니("여기 100솜이야 "+'\n'+"최고의 경기였어!!");
            솜+=100;
        }else if(승점>=12000){
            시스템텍스트애니("여기 80솜이야 "+'\n'+"멋진 경기였어!!");
            솜+=80;
        }else if(승점>=11000){
            시스템텍스트애니("여기 60솜이야 "+'\n'+"기대 이상인걸??");
            솜+=60;
        }else if(승점>=10000){
            시스템텍스트애니("여기 50솜이야 "+'\n'+"꽤 많이 노력했구나!!");
            솜+=50;
        }else if(승점>=9000){
            시스템텍스트애니("여기 45솜이야 "+'\n'+"벌써 이정도라니..!!");
            솜+=45;
        }else if(승점>=8000){
            시스템텍스트애니("여기 40솜이야 "+'\n'+"이정도면 잘했어!!");
            솜+=40;
        }else if(승점>=7000){
            시스템텍스트애니("여기 35솜이야 "+'\n'+"조금 아쉽지만...괜찮아!!");
            솜+=35;
        }else if(승점>=6000){
            시스템텍스트애니("여기 30솜이야 "+'\n'+"조금만 노력하면 좋겠군..");
            솜+=30;
        }else if(승점>=5000){
            시스템텍스트애니("여기 25솜이야 "+'\n'+"다음엔 좀더 잘하자구!!");
            솜+=25;
        }else if(승점>=4000){
            시스템텍스트애니("여기 20솜이야 "+'\n'+"실망스럽군...");
            솜+=20;
        }else if(승점>=3000){
            시스템텍스트애니("여기 15솜이야 "+'\n'+"겨우 이정도야??");
            솜+=15;
        }else if(승점>=2000){
            시스템텍스트애니("여기 10솜이야 "+'\n'+"너무 형편 없구나!!");
            솜+=10;
        }else if(승점>=1000){
            시스템텍스트애니("보상이라 할것도 없군...");
        }else{

        }

        유저정보.collection("userid")
                .whereLessThanOrEqualTo("승점",10000000).orderBy("승점").limit(3)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                String 랭커아이디[]=new String[3];
                                int 랭커카운터=0;
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    랭커카운터++;


                                }
                                if(랭커카운터==3){
                                    랭커카운터=0;
                                    if(랭커아이디[0].equals(현재접속아이디)){
                                        시스템텍스트애니("1등이라니 대단하구나!!");
                                        솜+=10*레벨;
                                    }else if(랭커아이디[1].equals(현재접속아이디)){
                                        시스템텍스트애니("2등이라니 대단하구나!!");
                                        솜+=5*레벨;
                                    }else if(랭커아이디[2].equals(현재접속아이디)){
                                        시스템텍스트애니("3등이라니 대단하구나!!");
                                        솜+=3*레벨;
                                    }
                                }
                                }
                             else {


                            }

                    }

                });
        레이9전체업데이트();
        저장하기();

    }
    void 레이10작동4(){
        DocumentReference docRef =유저정보.collection("rank").document("회차");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        if(회차!=Integer.parseInt(document.getData().get("회차").toString())){
                            회차=Integer.parseInt(document.getData().get("회차").toString());
                            레이10결투보상();
                        }
                    } else {

                    }
                } else {


                }
            }
        });
    }
    void 레이10작동3(){
        //초기화
        레이10정보텍스트[1].setText(""+현재접속아이디);
        레이10정보텍스트[2].setText(""+레벨);
        레이10정보텍스트[3].setText(""+String.format("%,d",승점));
        레이10정보텍스트[9].setText(""+" / "+String.format("%,d",내체력));
        레이10프로그레스1.setMax(100);
        레이10프로그레스1.setProgress(0);
        레이10프로그레스2.setMax(100);
        레이10프로그레스2.setProgress(0);
        레이10정보텍스트[3].setText(""+String.format("%,d",승점));
        레이10정보텍스트[6].setText("?");
        레이10정보텍스트[7].setText("?");
        레이10정보텍스트[8].setText("?");
        레이10정보텍스트[4].setText("?");
        레이10정보텍스트[9].setText("0"+" / "+String.format("%,d",명성));
        저장하기();
    }
    void 레이10작동2(int 누구){
        double 내체력비율=(double)(((double)내현재체력/(double)내체력)*(double)100);
        double 상대체력비율=(double)(((double)상대현재체력/(double)상대체력)*(double)100);
        Animation mAni1= AnimationUtils.loadAnimation(this, R.anim.size3);
        Animation mAni2= AnimationUtils.loadAnimation(this, R.anim.size3);
        switch (누구){
            case 0:
               if(대결중==1){
                int 요리랜덤=(int)(Math.random()*상대최고의요리);
                int 캐릭터랜덤=(int)(Math.random()*22);
                int 크리티컬랜덤=(int)(Math.random()*100);//1*
                int 일반랜덤=(int)(Math.random()*((상대캐릭터[캐릭터랜덤]*캐릭터[캐릭터랜덤][5]+캐릭터[캐릭터랜덤][2])-(상대캐릭터[캐릭터랜덤]*캐릭터[캐릭터랜덤][5]+캐릭터[캐릭터랜덤][1]))+(상대캐릭터[캐릭터랜덤]*캐릭터[캐릭터랜덤][5]+캐릭터[캐릭터랜덤][1]));
                상대공격력=요리가격[요리랜덤][(int) 상대요리[요리랜덤]]*일반랜덤;

                if(크리티컬랜덤<=상대크리티컬확률){
                    상대공격력*=상대크리티컬배수;
                    내현재체력+=상대공격력;
                    레이10정보텍스트[5].setText("크리티컬!! X "+상대크리티컬배수+'\n'+요리이름[요리랜덤]+" X "+일반랜덤+" 개"+'\n'+"+ "+String.format("%,d",상대공격력));

                    레이10정보텍스트[5].setTextColor(Color.RED);
                }else{
                    내현재체력+=상대공격력;
                    레이10정보텍스트[5].setText(""+요리이름[요리랜덤]+" X "+일반랜덤+" 개"+'\n'+"+ "+String.format("%,d",상대공격력));
                    레이10정보텍스트[5].setTextColor(Color.parseColor("#FFFFFF"));
                }


                   레이10정보텍스트[5].startAnimation(mAni1);
                   내체력비율=(double)(((double)내현재체력/(double)내체력)*(double)100);
                레이10프로그레스2.setProgress((int)내체력비율);
                레이10정보텍스트[9].setText(""+String.format("%,d",내현재체력)+" / "+String.format("%,d",내체력));
                if(내현재체력>=내체력){

                        if(대결중==1){
                            시스템텍스트애니("패배!!"+'\n'+"승점-5");
                            han.removeMessages(501);
                            han.removeMessages(502);
                            레이10정보텍스트[3].setText(""+String.format("%,d",승점));
                            레이10작동버튼.setVisibility(View.VISIBLE);
                          //  레이10작동3();
                        }



                    if(결투광고>=5){
                        광고라이징2();

                    }
                대결중=0;}}
                저장하기();
                break;
            case 1:
                if(대결중==1){
                int 내요리랜덤=(int)(Math.random()*내최고의요리);
                int 내캐릭터랜덤=(int)(Math.random()*22);
                int 내크리티컬랜덤=(int)(Math.random()*100);
                int 내일반랜덤=(int)(Math.random()*((캐릭터[내캐릭터랜덤][2]+1)-(캐릭터[내캐릭터랜덤][1]+1))+(캐릭터[내캐릭터랜덤][1]+1));

                내공격력=요리가격[내요리랜덤][(int)요리[내요리랜덤][12]]*내일반랜덤;
                if(내크리티컬랜덤<=내크리티컬확률){
                    내공격력*=내크리티컬배수;
                    상대현재체력+=내공격력;
                    레이10정보텍스트[0].setText("크리티컬!! X "+내크리티컬배수+'\n'+요리이름[내요리랜덤]+" X "+내일반랜덤+" 개"+'\n'+"+ "+String.format("%,d",내공격력));
                    레이10정보텍스트[0].setTextColor(Color.RED);
                }else{
                    상대현재체력+=내공격력;
                    레이10정보텍스트[0].setText(""+요리이름[내요리랜덤]+" X "+내일반랜덤+" 개"+'\n'+"+ "+String.format("%,d",내공격력));
                    레이10정보텍스트[0].setTextColor(Color.parseColor("#FFFFFF"));
                }

                String 말랜덤[]={"좋아좋아!!","조금만 더!!","내가 최고야!!","좀더 힘을 내!!","실력을 보여주지!!"};
                int 무슨말=(int)(Math.random()*5);
                    시스템텍스트애니(""+말랜덤[무슨말]);


                레이10정보텍스트[0].startAnimation(mAni2);
                // 상대현재체력+=내공격력;
                상대체력비율=(double)(((double)상대현재체력/(double)상대체력)*(double)100);
                레이10프로그레스1.setProgress((int)상대체력비율);
                레이10정보텍스트[4].setText(""+String.format("%,d",상대현재체력)+" / "+String.format("%,d",상대체력));
                if(상대현재체력>=상대체력){

                        if(대결중==1){
                            시스템텍스트애니("승리!!"+'\n'+"승점+5");
                            승점+=20;
                            if(승점>30000){
                                승점=30000;
                            }
                            han.removeMessages(501);
                            han.removeMessages(502);
                            레이10정보텍스트[3].setText(""+String.format("%,d",승점));
                            레이10작동버튼.setVisibility(View.VISIBLE);
                        //    레이10작동3();
                        }


                        if(결투광고>=5){
                            광고라이징2();

                        }
                    대결중=0;}}
                break;

        }
        저장하기();
    }
    void 레이10작동(){
        if(대결중==0){
        //    레이10작동버튼.setVisibility(View.INVISIBLE);
        if(결투도전권>0){
            레이10작동3();
            대결중=1;
                                적아이디카운트=(int)(Math.random()*10);
                                DocumentReference docRef =유저정보.collection("rank").document(""+승점/100*100);
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        //상대찾았을떄
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if(document.getData().size()>0) {
                                                적아이디[0] = document.getData().get((승점 / 100 * 100) + "_" + 적아이디카운트).toString();

                                                if (적아이디[0].equals(현재접속아이디)) {
                                                    DocumentReference docRef = 유저정보.collection("userid").document("" + 적아이디[0]);
                                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                DocumentSnapshot document = task.getResult();
                                                                결투광고++;
                                                                결투도전권--;
                                                                레이10작동버튼.setText("도전권 " + 결투도전권);
                                                                String 음식단계[] = {
                                                                        "요리1단계", "요리2단계", "요리3단계", "요리4단계", "요리5단계",
                                                                        "요리6단계", "요리7단계", "요리8단계", "요리9단계", "요리10단계",
                                                                        "요리11단계", "요리12단계", "요리13단계", "요리14단계", "요리15단계",
                                                                        "요리16단계", "요리17단계", "요리18단계", "요리19단계", "요리20단계",
                                                                        "요리21단계", "요리22단계", "요리23단계", "요리24단계", "요리25단계",
                                                                        "요리26단계", "요리27단계", "요리28단계", "요리29단계", "요리30단계",
                                                                        "요리31단계", "요리32단계", "요리33단계", "요리34단계", "요리35단계",
                                                                        "요리36단계", "요리37단계", "요리38단계", "요리39단계", "요리40단계",
                                                                        "요리41단계", "요리42단계", "요리43단계", "요리44단계", "요리45단계",
                                                                        "요리46단계", "요리47단계", "요리48단계"};
                                                                String 캐릭터단계[] = {
                                                                        "캐릭터1단계", "캐릭터2단계", "캐릭터3단계", "캐릭터4단계", "캐릭터5단계",
                                                                        "캐릭터6단계", "캐릭터7단계", "캐릭터8단계", "캐릭터9단계", "캐릭터10단계",
                                                                        "캐릭터11단계", "캐릭터12단계", "캐릭터13단계", "캐릭터14단계", "캐릭터15단계",
                                                                        "캐릭터16단계", "캐릭터17단계", "캐릭터18단계", "캐릭터19단계", "캐릭터20단계",
                                                                        "캐릭터21단계", "캐릭터22단계"};
                                                                적아이디쓰 = document.getData().get("아이디").toString();
                                                                상대체력 = Integer.parseInt(document.getData().get("명성").toString()) + 100000;
                                                                상대현재체력 = 0;
                                                                상대크리티컬확률 = Integer.parseInt(document.getData().get("레벨").toString());
                                                                상대승점 = Integer.parseInt(document.getData().get("승점").toString());
                                                                상대최고의요리 = 포장마차[Integer.parseInt(document.getData().get("포장마차단계").toString())][9] + 1;
                                                                상대크리티컬배수 = 마법제조기생산[Integer.parseInt(document.getData().get("마법제조기단계").toString())];
                                                                상대요리속도 = 포장마차[Integer.parseInt(document.getData().get("포장마차단계").toString())][6];
                                                                for (int i = 0; i < 48; i++) {
                                                                    상대요리[i] = Integer.parseInt(document.getData().get(음식단계[i]).toString());
                                                                }
                                                                for (int i = 0; i < 22; i++) {
                                                                    상대캐릭터[i] = Integer.parseInt(document.getData().get(캐릭터단계[i]).toString());
                                                                }

                                                                내체력 = 명성;
                                                                내현재체력 = 0;
                                                                내크리티컬확률 = 레벨;
                                                                내최고의요리 = 포장마차[포장마차단계][9] + 1;
                                                                내크리티컬배수 = 마법제조기생산[마법제조기단계];
                                                                내요리속도 = 포장마차[포장마차단계][6];
                                                                레이10이미지1.setBackgroundResource(오븐hd[마법제조기단계]);
                                                                레이10이미지2.setBackgroundResource(오븐hd[Integer.parseInt(document.getData().get("마법제조기단계").toString())]);
                                                                double 내체력비율 = (double) (((double) 내현재체력 / (double) 내체력) * (double) 100);
                                                                레이10프로그레스1.setMax(100);
                                                                레이10프로그레스1.setProgress(0);
                                                                레이10정보텍스트[1].setText("jaemoonniv");
                                                                레이10정보텍스트[2].setText("" + 레벨);
                                                                레이10정보텍스트[3].setText("" + String.format("%,d", 승점));
                                                                레이10정보텍스트[4].setText("" + 상대현재체력 + " / " + String.format("%,d", 상대체력));


                                                                double 상대체력비율 = (double) (((double) 상대현재체력 / (double) 상대체력) * (double) 100);
                                                                레이10프로그레스2.setMax(100);
                                                                레이10프로그레스2.setProgress(0);
                                                                레이10정보텍스트[6].setText("" + 적아이디쓰);
                                                                레이10정보텍스트[7].setText("" + 상대크리티컬확률);
                                                                레이10정보텍스트[8].setText("" + String.format("%,d", 상대승점));
                                                                레이10정보텍스트[9].setText("" + 내현재체력 + " / " + String.format("%,d", 내체력));
                                                                로티애니(3);
                                                                han.sendEmptyMessageDelayed(500, 3000);


                                                                if (document.exists()) {

                                                                } else {

                                                                }
                                                            } else {


                                                            }
                                                        }
                                                    });
                                                }else{
                                                    if (카운트 == 5) {
                                                        시스템텍스트애니("상대를 찾지 못했어!!");
                                                        카운트 = 0;

                                                    } else {
                                                        카운트 += 1;
                                                        대결중 = 0;
                                                        레이10작동();
                                                    }
                                                }

                                                    //상대못찾았을떄
                                                } else {
                                                    if (카운트 == 5) {
                                                        시스템텍스트애니("상대를 찾지 못했어!!");
                                                        카운트 = 0;

                                                    } else {
                                                        카운트 += 1;
                                                        대결중 = 0;
                                                        레이10작동();
                                                    }
                                                }


                                            if (document.exists()) {

                                            } else {

                                            }
                                        } else {


                                        }
                                    }
                                });



}else{
    시스템텍스트애니("도전권이 없군....."+'\n' +"광고한편정도 보는게 어떄??");
}}else{
            시스템텍스트애니("지금 대결에 집중해!!");
        }






 //       if(Integer.parseInt(document.getData().get("승점").toString())>=승점/1000*1000){
//                                                결투광고++;
//                                                결투도전권--;
//                                                레이10작동버튼.setText("도전권 "+결투도전권);
//
//                                                String 음식단계[]={
//                                                        "요리1단계","요리2단계","요리3단계","요리4단계","요리5단계",
//                                                        "요리6단계","요리7단계","요리8단계","요리9단계","요리10단계",
//                                                        "요리11단계","요리12단계","요리13단계","요리14단계","요리15단계",
//                                                        "요리16단계","요리17단계","요리18단계","요리19단계","요리20단계",
//                                                        "요리21단계","요리22단계","요리23단계","요리24단계","요리25단계",
//                                                        "요리26단계","요리27단계","요리28단계","요리29단계","요리30단계",
//                                                        "요리31단계","요리32단계","요리33단계","요리34단계","요리35단계",
//                                                        "요리36단계","요리37단계","요리38단계","요리39단계","요리40단계",
//                                                        "요리41단계","요리42단계","요리43단계","요리44단계","요리45단계",
//                                                        "요리46단계","요리47단계","요리48단계"};
//                                                String 캐릭터단계[]={
//                                                        "캐릭터1단계","캐릭터2단계","캐릭터3단계","캐릭터4단계","캐릭터5단계",
//                                                        "캐릭터6단계","캐릭터7단계","캐릭터8단계","캐릭터9단계","캐릭터10단계",
//                                                        "캐릭터11단계","캐릭터12단계","캐릭터13단계","캐릭터14단계","캐릭터15단계",
//                                                        "캐릭터16단계","캐릭터17단계","캐릭터18단계","캐릭터19단계","캐릭터20단계",
//                                                        "캐릭터21단계","캐릭터22단계"};
//                                                적아이디쓰=document.getData().get("아이디").toString();
//                                                상대체력=Integer.parseInt(document.getData().get("명성").toString())+100000;
//                                                상대현재체력=0;
//                                                상대크리티컬확률=Integer.parseInt(document.getData().get("레벨").toString());
//                                                상대승점=Integer.parseInt(document.getData().get("승점").toString());
//                                                상대최고의요리=포장마차[  Integer.parseInt(document.getData().get("포장마차단계").toString())][9]+1;
//                                                상대크리티컬배수=마법제조기생산[Integer.parseInt(document.getData().get("마법제조기단계").toString())];
//                                                상대요리속도=포장마차[Integer.parseInt(document.getData().get("포장마차단계").toString())][6];
//                                                for(int i=0;i<48;i++){
//                                                    상대요리[i]=Integer.parseInt(document.getData().get(음식단계[i]).toString());
//                                                }
//                                                for(int i=0;i<22;i++){
//                                                    상대캐릭터[i]=Integer.parseInt(document.getData().get(캐릭터단계[i]).toString());
//                                                }
//
//                                                내체력=명성;
//                                                내현재체력=0;
//                                                내크리티컬확률=레벨;
//                                                내최고의요리=포장마차[포장마차단계][9]+1;
//                                                내크리티컬배수=마법제조기생산[마법제조기단계];
//                                                내요리속도=포장마차[포장마차단계][6];
//                                                레이10이미지1.setBackgroundResource(오븐hd[마법제조기단계]);
//                                                레이10이미지2.setBackgroundResource(오븐hd[Integer.parseInt(document.getData().get("마법제조기단계").toString())]);
//                                                double 내체력비율=(double)(((double)내현재체력/(double)내체력)*(double)100);
//                                                레이10프로그레스1.setMax(100);
//                                                레이10프로그레스1.setProgress(0);
//                                                레이10정보텍스트[1].setText("jaemoonniv");
//                                                레이10정보텍스트[2].setText(""+레벨);
//                                                레이10정보텍스트[3].setText(""+String.format("%,d",승점));
//                                                레이10정보텍스트[4].setText(""+상대현재체력+" / "+String.format("%,d",상대체력));
//
//
//                                                double 상대체력비율=(double)(((double)상대현재체력/(double)상대체력)*(double)100);
//                                                레이10프로그레스2.setMax(100);
//                                                레이10프로그레스2.setProgress(0);
//                                                레이10정보텍스트[6].setText(""+적아이디쓰);
//                                                레이10정보텍스트[7].setText(""+상대크리티컬확률);
//                                                레이10정보텍스트[8].setText(""+String.format("%,d",상대승점));
//                                                레이10정보텍스트[9].setText(""+내현재체력+" / "+String.format("%,d",내체력));
//                                                로티애니(3);
//                                                han.sendEmptyMessageDelayed(500,3000);
//                                            }else{
//                                                시스템텍스트애니("현재리그에서는 상대가 없습니다...");
//                                                대결중=0;
//                                                레이10작동버튼.setVisibility(View.VISIBLE);
//                                            }


        저장하기();
    }
    void 레이9작동1(){
        int 현재캐쉬= 레이9식바.getProgress();
        if(솜>=캐쉬가격수량[레이9식바.getProgress()][1]){
            시스템텍스트애니("고맙다냥");
            솜-=캐쉬가격수량[레이9식바.getProgress()][1];
            레이9전체업데이트();
            switch (레이9식바.getProgress()){
                case 0: 소지금+=캐쉬가격수량[현재캐쉬][0];
                    break;
                case 1: 소지금+=캐쉬가격수량[현재캐쉬][0];
                    break;
                case 2: 소지금+=캐쉬가격수량[현재캐쉬][0];
                    break;
                case 3: 소지금+=캐쉬가격수량[현재캐쉬][0];
                    break;
                case 4: 소지금+=캐쉬가격수량[현재캐쉬][0];
                    break;
                case 5: 소지금+=캐쉬가격수량[현재캐쉬][0];
                    break;
                case 6: 소지금+=캐쉬가격수량[현재캐쉬][0];
                    break;
                case 7: 소지금+=캐쉬가격수량[현재캐쉬][0];
                    break;
                case 8: 마케팅쿠폰+=캐쉬가격수량[현재캐쉬][0];
                    break;
                case 9: 마법기계강화쿠폰+=캐쉬가격수량[현재캐쉬][0];
                    break;
                case 10: 요리강화쿠폰+=캐쉬가격수량[현재캐쉬][0];
                    break;
                case 11: 마케팅쿠폰+=캐쉬가격수량[현재캐쉬][0];
                    break;
                case 12: 마법기계강화쿠폰+=캐쉬가격수량[현재캐쉬][0];
                    break;
                case 13: 요리강화쿠폰+=캐쉬가격수량[현재캐쉬][0];
                    break;
                case 14: 마케팅쿠폰+=캐쉬가격수량[현재캐쉬][0];
                    break;
                case 15: 마법기계강화쿠폰+=캐쉬가격수량[현재캐쉬][0];
                    break;
                case 16: 요리강화쿠폰+=캐쉬가격수량[현재캐쉬][0];
                    break;
                case 17: 강화보조제+=캐쉬가격수량[현재캐쉬][0];
                    break;
                case 18: 강화보조제+=캐쉬가격수량[현재캐쉬][0];
                    break;
                case 19: 강화보호제+=캐쉬가격수량[현재캐쉬][0];
                    break;
                case 20: 강화보호제+=캐쉬가격수량[현재캐쉬][0];
                    break;

            }
            Map<String, Object> data = new HashMap<>();
            data.put("솜", 솜);
            유저정보.collection("userid").document(현재접속아이디).update(data);
            소지금업데이트();

        }else{
            시스템텍스트애니("나는 솜이 좋다구 ㅠ");
        }
    //    서버저장하기();
    }
    void 레이9작동2(){
        int 현재캐쉬= 레이9식바.getProgress();
        if(솜>=캐쉬가격수량[레이9식바.getProgress()][1]){
            시스템텍스트애니("이렇게 좋은걸 팔다니...고맙다냥");
            switch (레이9식바.getProgress()){
                case 8: 마케팅쿠폰-=캐쉬가격수량[현재캐쉬][0];
                    솜+=5;
                    break;
                case 9: 마법기계강화쿠폰-=캐쉬가격수량[현재캐쉬][0];
                    솜+=15;
                    break;
                case 10: 요리강화쿠폰-=캐쉬가격수량[현재캐쉬][0];
                    솜+=5;
                    break;
                case 11: 마케팅쿠폰-=캐쉬가격수량[현재캐쉬][0];
                    솜+=5*11;
                    break;
                case 12: 마법기계강화쿠폰-=캐쉬가격수량[현재캐쉬][0];
                    솜+=15*11;
                    break;
                case 13: 요리강화쿠폰-=캐쉬가격수량[현재캐쉬][0];
                    솜+=5*11;
                    break;
                case 14: 마케팅쿠폰-=캐쉬가격수량[현재캐쉬][0];
                    솜+=5*110;
                    break;
                case 15: 마법기계강화쿠폰-=캐쉬가격수량[현재캐쉬][0];
                    솜+=15*110;
                    break;
                case 16: 요리강화쿠폰-=캐쉬가격수량[현재캐쉬][0];
                    솜+=5*110;
                    break;
                default:
                    시스템텍스트애니("그건 구입하지 않아!");
                    break;
            }
            Map<String, Object> data = new HashMap<>();
            data.put("솜", 솜);
            유저정보.collection("userid").document(현재접속아이디).update(data);
            레이9전체업데이트();
        }else{
            시스템텍스트애니("물건이 없잔아!");
        }

        저장하기();
    }

    void 레이8작동(){

        bp.purchase(this, 인앱아이디[레이8식바.getProgress()]);

        for(int i=0;i<9;i++){
            bp.consumePurchase(인앱아이디[i]);
        }
        저장하기();


    }//★입앱결제
    void 레이7작동(){
        레이7전체업데이트();
        소지금-=입금액;
        은행잔고+=입금액;
        소지금업데이트();
        if(입금액>0){
            시스템텍스트애니("감사합니다 또 이용해 주세요!!");
        }else{
            시스템텍스트애니("더이상 입금을 할수없군요...");
        }
        레이7전체업데이트();

    }
    void 레이6작동(){
        int 현재선택=레이6식바.getProgress();
        long 비용=캐릭터강화비용[현재선택][(int)캐릭터[현재선택][3]];
        int 랜덤확률 =(int)(Math.random()*100);
        int 하락확률=(int)(Math.random()*100);
        if(캐릭터[현재선택][3]<15){
            //강화15단계미만일떄
            if(소지금>=비용||마케팅쿠폰>0){
                로티애니(0);
                레이6나가기.setVisibility(INVISIBLE);
                레이6스위치1.setVisibility(INVISIBLE);
                레이6스위치2.setVisibility(INVISIBLE);
                레이6작동버튼.setVisibility(INVISIBLE);
                레이6식바.setVisibility(INVISIBLE);
                han.sendEmptyMessageDelayed(100,5000);
                if(마케팅쿠폰>0){
                    마케팅쿠폰--;
                }else{
                    소지금-=비용;
                    소지금업데이트();
                }

                if(확률[(int) 캐릭터[현재선택][3]]*캐릭터보조>랜덤확률){
                    //성공
                    캐릭터[현재선택][3]++;
                    캐릭터[현재선택][1]+=캐릭터[현재선택][5];
                    캐릭터[현재선택][2]+=캐릭터[현재선택][5];
                    han.sendEmptyMessageDelayed(101,5000);
                }else{
                    //실패

                    if(하락확률<(int)((int)100-확률[(int) 캐릭터[현재선택][3]]*캐릭터보조)){
                        if(캐릭터하락==0){
                            han.sendEmptyMessageDelayed(102,5000);
                        }else{
                            han.sendEmptyMessageDelayed(103,5000);
                            캐릭터[현재선택][3]--;}
                    }else{
                        han.sendEmptyMessageDelayed(102,5000);
                    }
                }
                if(캐릭터하락==0){
                    //강화보호중일때
                    강화보호제--;
                    if(강화보호제==0){
                        레이6스위치2.setChecked(false);
                    }}
                if(캐릭터보조==2){
                    강화보조제--;
                    if(강화보조제==0){
                        레이6스위치1.setChecked(false);
                    }}
                레이6전체업데이트();
                소지금업데이트();
            }else{
                //돈없음
                시스템텍스트애니("광고료가 없는 거야??");
            }
        }else{
            //강화15단계이상일떄
            시스템텍스트애니("이 기계는ㅇㄹㅇㄹ 이미 최고야!!");
        }

        저장하기();
    }
    void 레이5작동(){
        long 비용=마법제조기강화비용[마법제조기단계];
        int 랜덤확률 =(int)(Math.random()*100);
        int 하락확률=(int)(Math.random()*100);
        if(마법제조기단계<15){
            //강화15단계미만일떄
            if(소지금>=비용||마법기계강화쿠폰>0){
                로티애니(0);
                레이5나가기.setVisibility(INVISIBLE);
                레이5스위치1.setVisibility(INVISIBLE);
                레이5스위치2.setVisibility(INVISIBLE);
                레이5작동버튼.setVisibility(INVISIBLE);
                han.sendEmptyMessageDelayed(100,5000);
                if(마법기계강화쿠폰>0){
                    마법기계강화쿠폰--;
                }else{
                    소지금-=비용;
                    소지금업데이트();
                }

                if(확률[마법제조기단계]*제조기보조>랜덤확률){
                    //성공
                    마법제조기단계++;
                    han.sendEmptyMessageDelayed(101,5000);
                }else{
                    //실패

                    if(하락확률<(int)((int)100-확률[마법제조기단계]*제조기보조)){
                        if(제조기하락==0){
                            han.sendEmptyMessageDelayed(102,5000);
                        }else{
                            han.sendEmptyMessageDelayed(103,5000);
                            마법제조기단계--;}
                    }else{
                        han.sendEmptyMessageDelayed(102,5000);
                    }
                }
                if(제조기하락==0){
                    //강화보호중일때
                    강화보호제--;
                    if(강화보호제==0){
                        레이5스위치2.setChecked(false);
                    }}
                if(제조기보조==2){
                    강화보조제--;
                    if(강화보조제==0){
                        레이5스위치1.setChecked(false);
                    }}
                레이5전체업데이트();
                소지금업데이트();
            }else{
                //돈없음
                시스템텍스트애니("비용이 없는 거야??");
            }
        }else{
            //강화15단계이상일떄
            시스템텍스트애니("이 기계는 이미 최고야!!");
        }
        저장하기();
    }
    void 레이4작동(){
        int 현재선택=레이4식바.getProgress();
        long 비용=요리강화비용[현재선택][요리[현재선택][12]];
        int 랜덤확률 =(int)(Math.random()*100);
        int 하락확률=(int)(Math.random()*100);
        if(요리[현재선택][12]<15){
            //강화15단계미만일떄
            if(소지금>=비용||요리강화쿠폰>0){
                로티애니(0);
                레이4나가기.setVisibility(INVISIBLE);
                레이4스위치1.setVisibility(INVISIBLE);
                레이4스위치2.setVisibility(INVISIBLE);
                레이4작동버튼.setVisibility(INVISIBLE);
                레이4식바.setVisibility(INVISIBLE);
                han.sendEmptyMessageDelayed(100,5000);
                if(요리강화쿠폰>0){
                    요리강화쿠폰--;
                }else{
                    소지금-=비용;
                    소지금업데이트();
                }

                if(확률[요리[현재선택][12]]*요리보조>랜덤확률){
                    //성공
                    요리[현재선택][12]++;
                    han.sendEmptyMessageDelayed(101,5000);
                }else{
                    //실패

                    if(하락확률<(int)((int)100-확률[요리[현재선택][12]]*요리보조)){
                        if(요리하락==0){
                            han.sendEmptyMessageDelayed(102,5000);
                        }else{
                        han.sendEmptyMessageDelayed(103,5000);
                            요리[현재선택][12]--;}
                    }else{
                        han.sendEmptyMessageDelayed(102,5000);
                    }
                }
                if(요리하락==0){
                    //강화보호중일때
                    강화보호제--;
                    if(강화보호제==0){
                        레이4스위치2.setChecked(false);
                    }}
                if(요리보조==2){
                    강화보조제--;
                    if(강화보조제==0){
                        레이4스위치1.setChecked(false);
                    }}
                레이1전체업데이트();
                레이4전체업데이트();
                소지금업데이트();
            }else{
                //돈없음
                시스템텍스트애니("수업료가 없는 거야??");
            }
        }else{
            //강화15단계이상일떄
            시스템텍스트애니("이 요리는 당신이 최고야!!");
        }
        저장하기();
    }
    void 레이3작동(){
        if(포장마차단계<10&&포장마차[포장마차단계+1][0]<=레벨&&포장마차[포장마차단계+1][1]<=명성&&포장마차[포장마차단계+1][2]<=소지금){
            포장마차단계++;
            소지금-=포장마차[포장마차단계][2];
            시스템텍스트애니("공사가 잘 마무리 됐어!");
            소지금업데이트();
            레이3전체업데이트();
        }else{
            if(포장마차단계==10){
                시스템텍스트애니("이미 충분히 완벽한 가게야!");
            }else if(포장마차[포장마차단계+1][0]>레벨){
                시스템텍스트애니("당신이 운영하기엔 가게가 커보이는군...");
            }else if(포장마차[포장마차단계+1][1]>명성){
                시스템텍스트애니("당신을 어떻게 믿고 계약을 해?");
            }else if(포장마차[포장마차단계+1][2]>소지금){
                시스템텍스트애니("돈은 들고 와야 될것아니야!");
            }

        }
        저장하기();
    }
    void 레이2작동(){
        int 물가상승=재료[현재재료+1][1]-(int)(재료[현재재료+1][1]*(경제지수-50)*0.006);
        if(물가상승*레이2식바.getProgress()<=소지금){
        재료[현재재료+1][0]+=레이2식바.getProgress();
        소지금-=물가상승*레이2식바.getProgress();
            소지금업데이트();
            시스템텍스트애니("감사합니다 또 이용해 주세요!!");
            레이2단일업데이트(현재재료);
            소지금업데이트();
        }else{
            시스템텍스트애니("카드잔액이 부족한데요??");
        }
        저장하기();
    }
    void 레이1작동(int 요리수량){
        if(요리[현재선택요리][1]<=포장마차단계){
        if(요리중==0){
            현재만들고있는요리=현재선택요리;
            if(재료[요리[현재만들고있는요리][4]][0]>=요리[현재만들고있는요리][5]*요리수량
              &&재료[요리[현재만들고있는요리][6]][0]>=요리[현재만들고있는요리][7]*요리수량
              &&재료[요리[현재만들고있는요리][8]][0]>=요리[현재만들고있는요리][9]*요리수량){
                요리중=1;
                로티애니(1);

                시스템텍스트애니("요리가 시작됩니다.");
                요리남은수량 = 요리수량;
                han.sendEmptyMessageDelayed(10,포장마차[포장마차단계][6]);
                레이아웃끄기(0);
            }else{
             //  시스템텍스트애니("재료가 다 떨어졌군....");
                시스템텍스트애니("재료가 부족한것 같군....");

            }
        }else{
            시스템텍스트애니("이미 기계가 작동중이군...");

        }
        }else{
            시스템텍스트애니("아직 제조법을 모르겠어...");

        }


        저장하기();
    }
    void 레이인포전체업데이트(){
        long 평균판매가;
        long 평균주문수;

        레이인포정보텍스트[0].setText(""+현재접속아이디);
        레이인포정보텍스트[1].setText(""+종합고유키);
        레이인포정보텍스트[2].setText(""+레벨);
        레이인포정보텍스트[3].setText(""+(long)(레벨별[레벨][0]-경험치));
        레이인포정보텍스트[4].setText(""+명성);
        레이인포정보텍스트[5].setText(""+승점);
        레이인포정보텍스트[6].setText(""+String.format("%,d",강화보조제)+" 개");
        레이인포정보텍스트[7].setText(""+String.format("%,d",강화보호제)+" 개");
        레이인포정보텍스트[8].setText(""+String.format("%,d",마케팅쿠폰)+" 개");
        레이인포정보텍스트[9].setText(""+String.format("%,d",요리강화쿠폰)+" 개");
        레이인포정보텍스트[10].setText(""+String.format("%,d",마법기계강화쿠폰)+" 개");

    }
    void 레이13전체업데이트(){}
    void 레이12전체업데이트() throws ParseException {
        long 이자,세금=레벨별[레벨][1],미접속시간;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat formatter2 = new SimpleDateFormat("HH");
        SimpleDateFormat formatter3 = new SimpleDateFormat("mm");
        Date beginDate = formatter.parse(시작시간일);
        Date endDate = formatter.parse(종료시간일);
        Date beginDate2 = formatter2.parse(시작시간시);
        Date endDate2 = formatter2.parse(종료시간시);
        Date beginDate3 = formatter3.parse(시작시간분);
        Date endDate3 = formatter3.parse(종료시간분);
        long diff =  beginDate.getTime()-endDate.getTime();
        long diffDays = diff / (24*60*60 * 1000)*60*24;
        long diff2 =  beginDate2.getTime()-endDate2.getTime();
        long diffDays2 = diff2 / (60*60 * 1000)*60;
        long diff3 =  beginDate3.getTime()-endDate3.getTime();
        long diffDays3 = diff3 / (60 * 1000);
        미접속시간 = diffDays+diffDays2+diffDays3;
        레이12정보텍스트[0].setText(""+미접속시간+" 분");
        레이12정보텍스트[1].setText(""+String.format("%,d",(long)(세금*미접속시간))+" 원");
        if(은행잔고>=0){
            이자=(long)(은행잔고*0.02/1440);
            레이12정보텍스트[2].setText(""+String.format("%,d",(long)(0))+" 원");
            레이12정보텍스트[3].setText(""+String.format("%,d",(long)(이자*미접속시간))+" 원");}
        else{
            이자=(long)(은행잔고*0.24/1440);
            레이12정보텍스트[2].setText(""+String.format("%,d",(long)(이자*미접속시간))+" 원");
            레이12정보텍스트[3].setText(""+String.format("%,d",(long)(0))+" 원");}
        if(미접속시간>=1){

            레이[11].setVisibility(View.VISIBLE);
            은행잔고-=(long)(세금*미접속시간);
            은행잔고+=(long)(이자*미접속시간);
            if(은행잔고<포장마차[포장마차단계][3]*-1){
               // 초기화();
            }
            미접속시간=0;
            레이7전체업데이트();

          }
    }
    void 레이11전체업데이트(){
        switch (현재보상){
            case 0:
                if(레이11이미지레이.getLayoutParams().width>=1024){
                    레이11이미지레이.setBackgroundResource(보상이미지fhd[0]);
                }else if(레이11이미지레이.getLayoutParams().width>=512){
                    레이11이미지레이.setBackgroundResource(보상이미지hd[0]);
                }else{
                    레이11이미지레이.setBackgroundResource(보상이미지sd[0]);
                }
                레이11정보텍스트[0].setText("1단계 요리상자");
                레이11정보텍스트[1].setText(""+요리이름[0]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[2].setText(""+요리이름[1]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[3].setText(""+요리이름[2]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[4].setText(""+요리이름[3]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[5].setText(""+요리이름[4]+String.format("%,d",현재보상수량)+"개");
                break;
            case 1:
                if(레이11이미지레이.getLayoutParams().width>=1024){
                    레이11이미지레이.setBackgroundResource(보상이미지fhd[0]);
                }else if(레이11이미지레이.getLayoutParams().width>=512){
                    레이11이미지레이.setBackgroundResource(보상이미지hd[0]);
                }else{
                    레이11이미지레이.setBackgroundResource(보상이미지sd[0]);
                }
                레이11정보텍스트[0].setText("2단계 요리상자");
                레이11정보텍스트[1].setText(""+요리이름[5]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[2].setText(""+요리이름[6]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[3].setText(""+요리이름[7]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[4].setText(""+요리이름[8]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[5].setText(""+요리이름[9]+String.format("%,d",현재보상수량)+"개");
                break;
            case 2:
                if(레이11이미지레이.getLayoutParams().width>=1024){
                    레이11이미지레이.setBackgroundResource(보상이미지fhd[0]);
                }else if(레이11이미지레이.getLayoutParams().width>=512){
                    레이11이미지레이.setBackgroundResource(보상이미지hd[0]);
                }else{
                    레이11이미지레이.setBackgroundResource(보상이미지sd[0]);
                }
                레이11정보텍스트[0].setText("3단계 요리상자");
                레이11정보텍스트[1].setText(""+요리이름[10]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[2].setText(""+요리이름[11]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[3].setText(""+요리이름[12]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[4].setText(""+요리이름[13]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[5].setText(""+요리이름[14]+String.format("%,d",현재보상수량)+"개");
                break;
            case 3:
                if(레이11이미지레이.getLayoutParams().width>=1024){
                    레이11이미지레이.setBackgroundResource(보상이미지fhd[0]);
                }else if(레이11이미지레이.getLayoutParams().width>=512){
                    레이11이미지레이.setBackgroundResource(보상이미지hd[0]);
                }else{
                    레이11이미지레이.setBackgroundResource(보상이미지sd[0]);
                }
                레이11정보텍스트[0].setText("4단계 요리상자");
                레이11정보텍스트[1].setText(""+요리이름[15]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[2].setText(""+요리이름[16]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[3].setText(""+요리이름[17]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[4].setText(""+요리이름[18]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[5].setText(""+요리이름[19]+String.format("%,d",현재보상수량)+"개");
                break;
            case 4:
                if(레이11이미지레이.getLayoutParams().width>=1024){
                    레이11이미지레이.setBackgroundResource(보상이미지fhd[0]);
                }else if(레이11이미지레이.getLayoutParams().width>=512){
                    레이11이미지레이.setBackgroundResource(보상이미지hd[0]);
                }else{
                    레이11이미지레이.setBackgroundResource(보상이미지sd[0]);
                }
                레이11정보텍스트[0].setText("5단계 요리상자");
                레이11정보텍스트[1].setText(""+요리이름[20]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[2].setText(""+요리이름[21]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[3].setText(""+요리이름[22]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[4].setText(""+요리이름[23]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[5].setText(""+요리이름[24]+String.format("%,d",현재보상수량)+"개");
                break;
            case 5:
                if(레이11이미지레이.getLayoutParams().width>=1024){
                    레이11이미지레이.setBackgroundResource(보상이미지fhd[0]);
                }else if(레이11이미지레이.getLayoutParams().width>=512){
                    레이11이미지레이.setBackgroundResource(보상이미지hd[0]);
                }else{
                    레이11이미지레이.setBackgroundResource(보상이미지sd[0]);
                }
                레이11정보텍스트[0].setText("6단계 요리상자");
                레이11정보텍스트[1].setText(""+요리이름[25]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[2].setText(""+요리이름[26]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[3].setText(""+요리이름[27]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[4].setText(""+요리이름[28]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[5].setText(""+요리이름[29]+String.format("%,d",현재보상수량)+"개");
                break;
            case 6:
                if(레이11이미지레이.getLayoutParams().width>=1024){
                    레이11이미지레이.setBackgroundResource(보상이미지fhd[0]);
                }else if(레이11이미지레이.getLayoutParams().width>=512){
                    레이11이미지레이.setBackgroundResource(보상이미지hd[0]);
                }else{
                    레이11이미지레이.setBackgroundResource(보상이미지sd[0]);
                }
                레이11정보텍스트[0].setText("7단계 요리상자");
                레이11정보텍스트[1].setText(""+요리이름[30]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[2].setText(""+요리이름[31]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[3].setText(""+요리이름[32]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[4].setText(""+요리이름[33]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[5].setText(""+요리이름[34]+String.format("%,d",현재보상수량)+"개");
                break;
            case 7:
                if(레이11이미지레이.getLayoutParams().width>=1024){
                    레이11이미지레이.setBackgroundResource(보상이미지fhd[0]);
                }else if(레이11이미지레이.getLayoutParams().width>=512){
                    레이11이미지레이.setBackgroundResource(보상이미지hd[0]);
                }else{
                    레이11이미지레이.setBackgroundResource(보상이미지sd[0]);
                }
                레이11정보텍스트[0].setText("8단계 요리상자");
                레이11정보텍스트[1].setText(""+요리이름[35]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[2].setText(""+요리이름[36]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[3].setText(""+요리이름[37]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[4].setText(""+요리이름[38]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[5].setText(""+요리이름[39]+String.format("%,d",현재보상수량)+"개");
                break;
            case 8:
                if(레이11이미지레이.getLayoutParams().width>=1024){
                    레이11이미지레이.setBackgroundResource(보상이미지fhd[0]);
                }else if(레이11이미지레이.getLayoutParams().width>=512){
                    레이11이미지레이.setBackgroundResource(보상이미지hd[0]);
                }else{
                    레이11이미지레이.setBackgroundResource(보상이미지sd[0]);
                }
                레이11정보텍스트[0].setText("9단계 요리상자");
                레이11정보텍스트[1].setText(""+요리이름[40]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[2].setText(""+요리이름[41]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[3].setText(""+요리이름[42]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[4].setText(""+요리이름[43]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[5].setText(""+요리이름[44]+String.format("%,d",현재보상수량)+"개");
                break;
            case 9:
                if(레이11이미지레이.getLayoutParams().width>=1024){
                    레이11이미지레이.setBackgroundResource(보상이미지fhd[0]);
                }else if(레이11이미지레이.getLayoutParams().width>=512){
                    레이11이미지레이.setBackgroundResource(보상이미지hd[0]);
                }else{
                    레이11이미지레이.setBackgroundResource(보상이미지sd[0]);
                }
                레이11정보텍스트[0].setText("10단계 요리상자");
                레이11정보텍스트[1].setText(""+요리이름[45]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[2].setText(""+요리이름[46]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[3].setText(""+요리이름[47]+String.format("%,d",현재보상수량)+"개");
                레이11정보텍스트[4].setText("X");
                레이11정보텍스트[5].setText("X");
                break;
            case 10:
                if(레이11이미지레이.getLayoutParams().width>=1024){
                    레이11이미지레이.setBackgroundResource(보상이미지fhd[0]);
                }else if(레이11이미지레이.getLayoutParams().width>=512){
                    레이11이미지레이.setBackgroundResource(보상이미지hd[0]);
                }else{
                    레이11이미지레이.setBackgroundResource(보상이미지sd[0]);
                }
                레이11정보텍스트[0].setText("요리 풀패키지");
                레이11정보텍스트[1].setText("모든요리 "+String.format("%,d",현재보상수량)+" (개)");
                레이11정보텍스트[2].setText("X");
                레이11정보텍스트[3].setText("X");
                레이11정보텍스트[4].setText("X");
                레이11정보텍스트[5].setText("X");
                break;
            case 11:
                if(레이11이미지레이.getLayoutParams().width>=1024){
                    레이11이미지레이.setBackgroundResource(보상이미지fhd[1]);
                }else if(레이11이미지레이.getLayoutParams().width>=512){
                    레이11이미지레이.setBackgroundResource(보상이미지hd[1]);
                }else{
                    레이11이미지레이.setBackgroundResource(보상이미지sd[1]);
                }
                레이11정보텍스트[0].setText("머니 선물상자");
                레이11정보텍스트[1].setText(""+String.format("%,d",현재보상수량)+" 원");
                레이11정보텍스트[2].setText("X");
                레이11정보텍스트[3].setText("X");
                레이11정보텍스트[4].setText("X");
                레이11정보텍스트[5].setText("X");
                break;
            case 12:
                if(레이11이미지레이.getLayoutParams().width>=1024){
                    레이11이미지레이.setBackgroundResource(보상이미지fhd[1]);
                }else if(레이11이미지레이.getLayoutParams().width>=512){
                    레이11이미지레이.setBackgroundResource(보상이미지hd[1]);
                }else{
                    레이11이미지레이.setBackgroundResource(보상이미지sd[1]);
                }
                레이11정보텍스트[0].setText("명성 선물상자");
                레이11정보텍스트[1].setText(""+String.format("%,d",현재보상수량)+" 점");
                레이11정보텍스트[2].setText("X");
                레이11정보텍스트[3].setText("X");
                레이11정보텍스트[4].setText("X");
                레이11정보텍스트[5].setText("X");
                break;
            case 13:
                if(레이11이미지레이.getLayoutParams().width>=1024){
                    레이11이미지레이.setBackgroundResource(보상이미지fhd[1]);
                }else if(레이11이미지레이.getLayoutParams().width>=512){
                    레이11이미지레이.setBackgroundResource(보상이미지hd[1]);
                }else{
                    레이11이미지레이.setBackgroundResource(보상이미지sd[1]);
                }
                레이11정보텍스트[0].setText("명성머니 세트상자");
                레이11정보텍스트[1].setText(""+String.format("%,d",현재보상수량)+" 원"+'\n'+""+String.format("%,d",현재보상수량)+" 점");
                레이11정보텍스트[2].setText("X");
                레이11정보텍스트[3].setText("X");
                레이11정보텍스트[4].setText("X");
                레이11정보텍스트[5].setText("X");
                break;
            case 14:
                if(레이11이미지레이.getLayoutParams().width>=1024){
                    레이11이미지레이.setBackgroundResource(보상이미지fhd[3]);
                }else if(레이11이미지레이.getLayoutParams().width>=512){
                    레이11이미지레이.setBackgroundResource(보상이미지hd[3]);
                }else{
                    레이11이미지레이.setBackgroundResource(보상이미지sd[3]);
                }
                레이11정보텍스트[0].setText("솜이 가득한 인형");
                레이11정보텍스트[1].setText("솜 "+ String.format("%,d",현재보상수량));
                레이11정보텍스트[2].setText("X");
                레이11정보텍스트[3].setText("X");
                레이11정보텍스트[4].setText("X");
                레이11정보텍스트[5].setText("X");
                break;
            case 15:
                if(레이11이미지레이.getLayoutParams().width>=1024){
                    레이11이미지레이.setBackgroundResource(보상이미지fhd[2]);
                }else if(레이11이미지레이.getLayoutParams().width>=512){
                    레이11이미지레이.setBackgroundResource(보상이미지hd[2]);
                }else{
                    레이11이미지레이.setBackgroundResource(보상이미지sd[2]);
                }
                레이11정보텍스트[0].setText("강화보조제");
                레이11정보텍스트[1].setText("강화보조제 : "+ String.format("%,d",현재보상수량));
                레이11정보텍스트[2].setText("X");
                레이11정보텍스트[3].setText("X");
                레이11정보텍스트[4].setText("X");
                레이11정보텍스트[5].setText("X");
                break;
            case 16:
                if(레이11이미지레이.getLayoutParams().width>=1024){
                    레이11이미지레이.setBackgroundResource(보상이미지fhd[2]);
                }else if(레이11이미지레이.getLayoutParams().width>=512){
                    레이11이미지레이.setBackgroundResource(보상이미지hd[2]);
                }else{
                    레이11이미지레이.setBackgroundResource(보상이미지sd[2]);
                }
                레이11정보텍스트[0].setText("강화보호제");
                레이11정보텍스트[1].setText("강화보호제 : "+ String.format("%,d",현재보상수량));
                레이11정보텍스트[2].setText("X");
                레이11정보텍스트[3].setText("X");
                레이11정보텍스트[4].setText("X");
                레이11정보텍스트[5].setText("X");
                break;
            case 17:
                if(레이11이미지레이.getLayoutParams().width>=1024){
                    레이11이미지레이.setBackgroundResource(보상이미지fhd[2]);
                }else if(레이11이미지레이.getLayoutParams().width>=512){
                    레이11이미지레이.setBackgroundResource(보상이미지hd[2]);
                }else{
                    레이11이미지레이.setBackgroundResource(보상이미지sd[2]);
                }
                레이11정보텍스트[0].setText("강화세트");
                레이11정보텍스트[1].setText("강화보조제 : "+ String.format("%,d",현재보상수량)+'\n'+"강화보호제 : "+ String.format("%,d",현재보상수량));
                레이11정보텍스트[2].setText("X");
                레이11정보텍스트[3].setText("X");
                레이11정보텍스트[4].setText("X");
                레이11정보텍스트[5].setText("X");
                break;
            default:
                if(레이11이미지레이.getLayoutParams().width>=1024){
                    레이11이미지레이.setBackgroundResource(보상이미지fhd[4]);
                }else if(레이11이미지레이.getLayoutParams().width>=512){
                    레이11이미지레이.setBackgroundResource(보상이미지hd[4]);
                }else{
                    레이11이미지레이.setBackgroundResource(보상이미지sd[4]);
                }
                레이11정보텍스트[0].setText("빈상자");
                레이11정보텍스트[1].setText("X");
                레이11정보텍스트[2].setText("X");
                레이11정보텍스트[3].setText("X");
                레이11정보텍스트[4].setText("X");
                레이11정보텍스트[5].setText("X");
                break;
        }
    }
    void 레이10전체업데이트(){
        레이10작동버튼.setText("도전권 "+결투도전권);
    }
    void 레이9전체업데이트(){
        int 현재선택=레이9식바.getProgress();
        if(레이9이미지레이.getLayoutParams().width>=1024){
            레이9이미지.setBackgroundResource(캐쉬이미지fhd[현재선택]);
        }else if(레이9이미지레이.getLayoutParams().width>=512){
            레이9이미지.setBackgroundResource(캐쉬이미지hd[현재선택]);
        }else{
            레이9이미지.setBackgroundResource(캐쉬이미지sd[현재선택]);
        }
        //레이9이미지.setBackgroundResource(테스트이미지id[현재선택]);
        레이9정보텍스트[0].setText(""+캐쉬이름[현재선택]);
        레이9정보텍스트[1].setText(""+String.format("%,d",캐쉬가격수량[현재선택][1])+" 솜");
        레이9정보텍스트[2].setText(""+String.format("%,d",캐쉬가격수량[현재선택][0]));
        레이9정보텍스트[3].setText(""+캐쉬설명[현재선택]);
        레이9정보텍스트[4].setText(""+String.format("%,d",솜));
    }
    void 레이8전체업데이트(){
       int 현재선택=레이8식바.getProgress();
        if(레이8이미지레이.getLayoutParams().width>=1024){
            레이8이미지.setBackgroundResource(솜이미지fhd[레이8식바.getProgress()]);
        }else if(레이8이미지레이.getLayoutParams().width>=512){
            레이8이미지.setBackgroundResource(솜이미지hd[레이8식바.getProgress()]);
        }else{
            레이8이미지.setBackgroundResource(솜이미지sd[레이8식바.getProgress()]);
        }
    //    레이8이미지.setBackgroundResource(테스트이미지id[현재선택]);
        레이8정보텍스트[0].setText(""+인앱이름[현재선택]);
        레이8정보텍스트[1].setText(""+String.format("%,d",인앱수량[현재선택])+" 솜");
        레이8정보텍스트[2].setText(""+String.format("%,d",인앱가격[현재선택])+" KRW");
        레이8정보텍스트[3].setText(""+인앱설명[현재선택]);
    }
    void 레이7전체업데이트(){

        입금가능액=포장마차[포장마차단계][3]-은행잔고;
        입금액=(int)(입금가능액*0.01*레이7식바.getProgress());
        레이7정보텍스트[0].setText(""+String.format("%,d",소지금)+" 원");
        레이7정보텍스트[1].setText("분당 "+String.format("%,d",레벨별[레벨][1])+" 원");
        레이7정보텍스트[2].setText("분당 "+String.format("%,d",레벨별[레벨][1]/5)+" 원");
        레이7정보텍스트[3].setText(""+String.format("%,d",은행잔고)+" 원");
       if(은행잔고>0){
           레이7정보텍스트[4].setText("분당 "+String.format("%,d",0)+" 원");
           레이7정보텍스트[5].setText("분당 "+String.format("%,d",(int)(은행잔고*0.02)/1440)+" 원");}
       else{
           레이7정보텍스트[4].setText("분당 "+String.format("%,d",(int)(은행잔고*0.24)/1440)+" 원");
           레이7정보텍스트[5].setText("분당 "+String.format("%,d",0)+" 원");}
        레이7정보텍스트[6].setText(""+String.format("%,d",입금가능액)+" 원");
        레이7정보텍스트[7].setText(""+String.format("%,d",포장마차[포장마차단계][3]*-1)+" 원");
        if(입금액>입금가능액){
            입금액=입금가능액;
        }else if(입금액>소지금){
            입금액=소지금;
        }
        레이7정보텍스트[8].setText(""+String.format("%,d",입금액)+" 원");
    }
    void 레이6전체업데이트(){
        int 현재선택=레이6식바.getProgress();
        long 비용=캐릭터강화비용[현재선택][(int) 캐릭터[현재선택][3]];
        if(레이6이미지레이.getLayoutParams().width>=1024){
            레이6이미지.setBackgroundResource(캐릭터이미지fhd[레이6식바.getProgress()]);
        }else if(레이6이미지레이.getLayoutParams().width>=512){
            레이6이미지.setBackgroundResource(캐릭터이미지hd[레이6식바.getProgress()]);
        }else{
            레이6이미지.setBackgroundResource(캐릭터이미지sd[레이6식바.getProgress()]);
        }
        if(캐릭터[현재선택][3]<15){
         //   레이6이미지.setBackgroundResource(테스트이미지id[현재선택]);
            레이6정보텍스트[0].setText(""+캐릭터이름[현재선택]);
            레이6정보텍스트[1].setText(""+캐릭터[현재선택][3]+" -> "+(캐릭터[현재선택][3]+1));
            레이6정보텍스트[2].setText(""+캐릭터[현재선택][1]+" -> "+(캐릭터[현재선택][1]+캐릭터[현재선택][5]));
            레이6정보텍스트[3].setText(""+캐릭터[현재선택][2]+" -> "+(캐릭터[현재선택][2]+캐릭터[현재선택][5]));
            레이6정보텍스트[4].setText(""+String.format("%,d",비용)+" 원");
            레이6정보텍스트[5].setText(""+확률[(int) 캐릭터[현재선택][3]]*캐릭터보조+" %");
        }else{
        //    레이6이미지.setBackgroundResource(테스트이미지id[현재선택]);
            레이6정보텍스트[0].setText(""+캐릭터이름[현재선택]);
            레이6정보텍스트[1].setText(""+캐릭터[현재선택][3]);
            레이6정보텍스트[2].setText(""+캐릭터[현재선택][1]);
            레이6정보텍스트[3].setText(""+캐릭터[현재선택][2]);
            레이6정보텍스트[4].setText("X");
            레이6정보텍스트[5].setText("X");
        }

    }
    void 레이5전체업데이트(){

        if(마법제조기단계<15){
            long 비용=마법제조기강화비용[마법제조기단계];
            레이5이미지.setBackgroundResource(오븐hd[마법제조기단계]);
            레이5정보텍스트[0].setText(""+마법제조기이름[마법제조기단계]);
            레이5정보텍스트[1].setText(""+마법제조기단계+" -> "+(마법제조기단계+1));
            레이5정보텍스트[2].setText(""+마법제조기생산[마법제조기단계]+" -> "+마법제조기생산[마법제조기단계+1]);
            레이5정보텍스트[3].setText(""+String.format("%,d",마법제조기강화비용[마법제조기단계])+" 원");
            레이5정보텍스트[4].setText(""+확률[마법제조기단계]*제조기보조+" %");
        }else{
            레이5이미지.setBackgroundResource(오븐hd[마법제조기단계]);
            레이5정보텍스트[0].setText(""+마법제조기이름[마법제조기단계]);
            레이5정보텍스트[1].setText(""+마법제조기단계);
            레이5정보텍스트[2].setText(""+마법제조기생산[마법제조기단계]);
            레이5정보텍스트[3].setText("X");
            레이5정보텍스트[4].setText("X");
        }
    }
    void 레이4전체업데이트(){
        int 현재선택=레이4식바.getProgress();
        //책갈피
        if(레이4이미지레이.getLayoutParams().width>=1024){
            레이4이미지.setBackgroundResource(요리이미지fhd[레이4식바.getProgress()]);
        }else if(레이4이미지레이.getLayoutParams().width>=512){
            레이4이미지.setBackgroundResource(요리이미지hd[레이4식바.getProgress()]);
        }else{
            레이4이미지.setBackgroundResource(요리이미지sd[레이4식바.getProgress()]);
        }

        long 가격=요리가격[현재선택][요리[현재선택][12]];
        long 비용=요리강화비용[현재선택][요리[현재선택][12]];
        if(요리[현재선택][12]<15){
         //   레이4이미지.setBackgroundResource(테스트이미지id[현재선택]);
            레이4정보텍스트[0].setText(""+요리이름[현재선택]);
            레이4정보텍스트[1].setText(""+요리[현재선택][12]+" -> "+(요리[현재선택][12]+1));
            레이4정보텍스트[2].setText(""+String.format("%,d",가격)+" -> "+String.format("%,d",(long)(가격*1.1)));
            레이4정보텍스트[3].setText(""+String.format("%,d",비용)+" 원");
            레이4정보텍스트[4].setText(""+확률[요리[현재선택][12]]*요리보조+" %");
        }else{
         //   레이4이미지.setBackgroundResource(테스트이미지id[현재선택]);
            레이4정보텍스트[0].setText(""+요리이름[현재선택]);
            레이4정보텍스트[1].setText(""+요리[현재선택][12]);
            레이4정보텍스트[2].setText(""+String.format("%,d",가격));
            레이4정보텍스트[3].setText("X");
            레이4정보텍스트[4].setText("X");
        }
    }
    void 레이3전체업데이트(){
        //필요레벨,필요명성,가격,예금한도,구입갯수,손님출현률,요리속도,웨이팅,리워드보상,음식,캐릭터,인테리어
        if(포장마차단계<10){
            if(레이3이미지레이.getLayoutParams().width>=1024){
                레이3이미지.setBackgroundResource(포장마차이미지fhd[포장마차단계]);
            }else if(레이3이미지레이.getLayoutParams().width>=512){
                레이3이미지.setBackgroundResource(포장마차이미지hd[포장마차단계]);
            }else{
                레이3이미지.setBackgroundResource(포장마차이미지sd[포장마차단계]);
            }

            레이3정보텍스트[0].setText(""+포장마차[포장마차단계+1][0]);
            레이3정보텍스트[1].setText(""+String.format("%,d",포장마차[포장마차단계+1][1]));
            레이3정보텍스트[2].setText(""+String.format("%,d",포장마차[포장마차단계+1][2]));
            레이3정보텍스트[3].setText(""+String.format("%,d",포장마차[포장마차단계][3])+" -> "+String.format("%,d",포장마차[포장마차단계+1][3]));
            레이3정보텍스트[4].setText(""+포장마차[포장마차단계][4]+" -> "+포장마차[포장마차단계+1][4]);
            레이3정보텍스트[5].setText(""+포장마차[포장마차단계][5]+" -> "+포장마차[포장마차단계+1][5]);
            레이3정보텍스트[6].setText(""+((double)포장마차[포장마차단계][6]/1000)+" -> "+((double)포장마차[포장마차단계+1][6]/1000));
            레이3정보텍스트[7].setText(""+포장마차[포장마차단계][7]+" -> "+포장마차[포장마차단계+1][7]);
            레이3정보텍스트[8].setText(""+요리이름[포장마차[포장마차단계][9]]+" -> "+요리이름[포장마차[포장마차단계+1][9]]);
            레이3정보텍스트[9].setText(""+캐릭터이름[포장마차[포장마차단계][10]]+" -> "+캐릭터이름[포장마차[포장마차단계+1][10]]);
        }else{
         //   레이3이미지.setBackgroundResource(테스트이미지id[포장마차단계]);
            레이3정보텍스트[0].setText("X");
            레이3정보텍스트[1].setText("X");
            레이3정보텍스트[2].setText("X");
            레이3정보텍스트[3].setText(""+String.format("%,d",포장마차[포장마차단계][3]));
            레이3정보텍스트[4].setText(""+포장마차[포장마차단계][4]);
            레이3정보텍스트[5].setText(""+포장마차[포장마차단계][5]);
            레이3정보텍스트[6].setText(""+((double)포장마차[포장마차단계][6]/1000));
            레이3정보텍스트[7].setText(""+포장마차[포장마차단계][7]);
            레이3정보텍스트[8].setText(""+요리이름[포장마차[포장마차단계][9]]);
            레이3정보텍스트[9].setText(""+캐릭터이름[포장마차[포장마차단계][10]]);}
    }
    void 레이2전체업데이트(){
        int 현재선택=레이2식바.getProgress();
        for(int i=0;i<10;i++){
            int 물가상승=재료[i+1][1]-(int)(재료[i+1][1]*(경제지수-50)*0.006);
            레이2식바수량텍스트.setText(""+현재선택);
            레이2요리이름텍스트[i].setText(""+재료이름[i+1]);
            레이2요리가격텍스트[i].setText("가격 : "+String.format("%,d",물가상승));
            레이2요리보유텍스트[i].setText("보유 : "+String.format("%,d",재료[i+1][0]));}
    }
    void 레이2단일업데이트(int 현재재료){
        int 현재선택=레이2식바.getProgress();
        if(레이2선택버튼[0].getLayoutParams().width>=256){
            레이2선택버튼[이전재료].setBackgroundResource(R.drawable.checkwhfhd);
            레이2선택버튼[현재재료].setBackgroundResource(R.drawable.checkorfhd);
        }else if(레이2선택버튼[0].getLayoutParams().width>=128){
            레이2선택버튼[이전재료].setBackgroundResource(R.drawable.checkwhhd);
            레이2선택버튼[현재재료].setBackgroundResource(R.drawable.checkorhd);
        }else{
            레이2선택버튼[이전재료].setBackgroundResource(R.drawable.checkwhsd);
            레이2선택버튼[현재재료].setBackgroundResource(R.drawable.checkorsd);
        }
            int 물가상승=재료[현재재료+1][1]-(int)(재료[현재재료+1][1]*(경제지수-50)*0.006);
            int 이전물가상승=재료[이전재료+1][1]-(int)(재료[이전재료+1][1]*(경제지수-50)*0.006);
            레이2요리가격텍스트[이전재료].setText("가격 : "+String.format("%,d",이전물가상승));
            레이2요리보유텍스트[이전재료].setText("보유 : "+재료[이전재료+1][0]);
            레이2요리가격텍스트[이전재료].setTextColor(Color.parseColor("#FFFFFF"));
            레이2요리보유텍스트[이전재료].setTextColor(Color.parseColor("#FFFFFF"));
            이전재료=현재재료;
            레이2요리가격텍스트[현재재료].setText("가격 : "+String.format("%,d",(int)(물가상승*현재선택)));
            레이2요리보유텍스트[현재재료].setText("보유 : "+재료[현재재료+1][0]);
            레이2요리가격텍스트[현재재료].setTextColor(Color.parseColor("#FFFFFF"));
            레이2요리보유텍스트[현재재료].setTextColor(Color.parseColor("#FFFFFF"));
            if(소지금<(int)(물가상승*현재선택)){레이2요리가격텍스트[현재재료].setTextColor(Color.RED);
            }else{레이2요리가격텍스트[현재재료].setTextColor(Color.parseColor("#FFFFFF"));}
    }
    void 레이1전체업데이트(){
        레이1단일업데이트(현재선택요리);
        int 현재선택=레이1식바.getProgress();

        for(int i=0;i<48;i++){
            long 판매상승=요리가격[i][요리[i][12]]+(long)(요리가격[i][요리[i][12]]*(경제지수-50)*0.002);
            long 물가상승=판매상승-(((재료[요리[i][4]][1]*요리[i][5]+재료[요리[i][6]][1]*요리[i][7]+재료[요리[i][8]][1]*요리[i][9])-(long)((재료[요리[i][4]][1]*요리[i][5]+재료[요리[i][6]][1]*요리[i][7]+재료[요리[i][8]][1]*요리[i][9])*(double)((double)경제지수-50)*0.002)));
            레이1식바수량텍스트.setText(""+현재선택);
            레이1요리이름텍스트[i].setText(""+요리이름[i]);
            레이1요리가격텍스트[i].setText("가격 : "+String.format("%,d",판매상승));
            레이1요리마진텍스트[i].setText("마진 : "+String.format("%,d",물가상승));
            레이1요리재료1[i].setText(""+재료이름[요리[i][4]]+" : "+요리[i][5]);
            레이1요리재료2[i].setText(""+재료이름[요리[i][6]]+" : "+요리[i][7]);
            레이1요리재료3[i].setText(""+재료이름[요리[i][8]]+" : "+요리[i][9]);}
    }
    void 레이1단일업데이트(int 현재선택요리){
        int 현재선택=레이1식바.getProgress();
        if(레이1선택버튼[0].getLayoutParams().width>=256){
            레이1선택버튼[이전선택요리].setBackgroundResource(R.drawable.checkwhfhd);
            레이1선택버튼[현재선택요리].setBackgroundResource(R.drawable.checkorfhd);
        }else if(레이1선택버튼[0].getLayoutParams().width>=256){
            레이1선택버튼[이전선택요리].setBackgroundResource(R.drawable.checkwhhd);
            레이1선택버튼[현재선택요리].setBackgroundResource(R.drawable.checkorhd);
        }else{
            레이1선택버튼[이전선택요리].setBackgroundResource(R.drawable.checkwhsd);
            레이1선택버튼[현재선택요리].setBackgroundResource(R.drawable.checkorsd);
        }

            레이1요리재료1[이전선택요리].setText(""+재료이름[요리[이전선택요리][4]]+" : "+요리[이전선택요리][5]);
            레이1요리재료2[이전선택요리].setText(""+재료이름[요리[이전선택요리][6]]+" : "+요리[이전선택요리][7]);
            레이1요리재료3[이전선택요리].setText(""+재료이름[요리[이전선택요리][8]]+" : "+요리[이전선택요리][9]);

            레이1요리재료1[이전선택요리].setTextColor(Color.parseColor("#FFFFFF"));
            레이1요리재료2[이전선택요리].setTextColor(Color.parseColor("#FFFFFF"));
            레이1요리재료3[이전선택요리].setTextColor(Color.parseColor("#FFFFFF"));;
            레이1요리재료1[현재선택요리].setText(""+재료이름[요리[현재선택요리][4]]+" : "+요리[현재선택요리][5]*현재선택);
            레이1요리재료2[현재선택요리].setText(""+재료이름[요리[현재선택요리][6]]+" : "+요리[현재선택요리][7]*현재선택);
            이전선택요리=현재선택요리;
            레이1요리재료3[현재선택요리].setText(""+재료이름[요리[현재선택요리][8]]+" : "+요리[현재선택요리][9]*현재선택);
            if(재료[요리[현재선택요리][4]][0]<요리[현재선택요리][5]*현재선택){레이1요리재료1[현재선택요리].setTextColor(Color.RED);
            }else{레이1요리재료1[현재선택요리].setTextColor(Color.parseColor("#FFFFFF"));}
            if(재료[요리[현재선택요리][6]][0]<요리[현재선택요리][7]*현재선택){레이1요리재료2[현재선택요리].setTextColor(Color.RED);
            }else{레이1요리재료2[현재선택요리].setTextColor(Color.parseColor("#FFFFFF"));}
            if(재료[요리[현재선택요리][8]][0]<요리[현재선택요리][9]*현재선택){레이1요리재료3[현재선택요리].setTextColor(Color.RED);
            }else{레이1요리재료3[현재선택요리].setTextColor(Color.parseColor("#FFFFFF"));}

    }
    void 퀵슬롯전체업데이트(int 업데이트번호){


        switch (업데이트번호){
            case 48:
                for(int i=0;i<48;i++){퀵슬롯버튼[i].setText(""+String.format("%,d",요리[i][0]));}
                    break;
            default:
                퀵슬롯버튼[업데이트번호].setText(""+String.format("%,d",요리[업데이트번호][0]));
                    break;
        }

    }
    void 퀵슬롯단일업데이트(int 현재줄요리){
        퀵슬롯버튼[현재줄요리].setText(""+String.format("%,d",요리[현재줄요리][0]));
        if(퀵슬롯버튼[0].getLayoutParams().width>=512){
            퀵슬롯레이[이전줄요리].setBackgroundResource(R.drawable.buttonwhfhd);
            퀵슬롯레이[현재줄요리].setBackgroundResource(R.drawable.buttonorfhd);
        }else if(퀵슬롯버튼[0].getLayoutParams().width>=256){
            퀵슬롯레이[이전줄요리].setBackgroundResource(R.drawable.buttonwhhd);
            퀵슬롯레이[현재줄요리].setBackgroundResource(R.drawable.buttonorhd);
        }else{
            퀵슬롯레이[이전줄요리].setBackgroundResource(R.drawable.buttonwhsd);
            퀵슬롯레이[현재줄요리].setBackgroundResource(R.drawable.buttonorsd);
        }

        이전줄요리=현재줄요리;

    }
    void 식바(){
        레이6스위치1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(강화보조제>0){캐릭터보조=2;} else{시스템텍스트애니("강화보조제가 없잔아!");레이6스위치1.setChecked(false);}
                }else{
                    캐릭터보조=1;
                }
                레이6전체업데이트();
            }});
        레이6스위치2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(강화보호제>0){제조기하락=0;} else{시스템텍스트애니("강화보호제가 없잔아!");레이6스위치2.setChecked(false);}
                }else{
                    제조기하락=1;
                }
                레이6전체업데이트();
            }});
        레이5스위치1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(강화보조제>0){제조기보조=2;} else{시스템텍스트애니("강화보조제가 없잔아!");레이5스위치1.setChecked(false);}
                }else{
                    제조기보조=1;
                }
                레이5전체업데이트();
            }});
        레이5스위치2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(강화보호제>0){제조기하락=0;} else{시스템텍스트애니("강화보호제가 없잔아!");레이5스위치2.setChecked(false);}
                }else{
                    제조기하락=1;
                }
                레이5전체업데이트();
            }});
        레이4스위치1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(강화보조제>0){요리보조=2;} else{시스템텍스트애니("강화보조제가 없잔아!");레이4스위치1.setChecked(false);}
                }else{
                    요리보조=1;
                }
                레이4전체업데이트();
            }});
        레이4스위치2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(강화보호제>0){요리하락=0;} else{시스템텍스트애니("강화보호제가 없잔아!");레이4스위치2.setChecked(false);}
                }else{
                    요리하락=1;
                }
                레이4전체업데이트();
            }});

        레이1식바.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {}
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                if(레이1식바.getProgress()==0){레이1식바.setProgress(1);} 레이1식바수량텍스트.setText(""+레이1식바.getProgress()); 레이1단일업데이트(현재선택요리);}
        });
        레이2식바.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {}
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {if(레이2식바.getProgress()==0){레이2식바.setProgress(1);}레이2식바수량텍스트.setText(""+레이2식바.getProgress()); 레이2단일업데이트(현재재료);}
        });
        레이4식바.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {}
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {레이4전체업데이트();}
        });
        레이6식바.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {}
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {레이6전체업데이트();}
        });
        레이7식바.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {}
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {레이7전체업데이트();}
        });
        레이8식바.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {}
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {레이8전체업데이트();}
        });
        레이9식바.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {}
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {레이9전체업데이트();}
        });
    }
    void 로티애니(int 로티번호){
        switch (로티번호){
            case 0:
                로티레이[0].setBackgroundColor(Color.parseColor("#4e4e4e"));
                로티레이[0].setAnimation("uping.json");
                로티레이[0].loop(true);
                break;
            case 1:
                로티레이[0].setBackgroundColor(Color.parseColor("#00FFFFFF"));
                로티레이[1].setAnimation("making.json");
                로티레이[1].loop(true);
                break;
            case 2:
                로티레이[0].setBackgroundColor(Color.parseColor("#00FFFFFF"));
                로티레이[2].setAnimation("levelup.json");
                로티레이[2].loop(false);
                break;
            case 3:
                로티레이[0].setBackgroundColor(Color.parseColor("#00FFFFFF"));
                로티레이[3].setAnimation("count.json");
                로티레이[3].loop(false);
                break;
        }

        로티레이[로티번호].playAnimation();
        로티레이[로티번호].setVisibility(View.VISIBLE);
        로티레이[0].addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                로티레이[0].setVisibility(View.GONE);
            }
            @Override
            public void onAnimationCancel(Animator animation) {
                로티레이[0].setVisibility(View.GONE);
                레이4작동버튼.setVisibility(View.VISIBLE);
                레이4나가기.setVisibility(View.VISIBLE);
                레이4식바.setVisibility(View.VISIBLE);
                레이4스위치1.setVisibility(View.VISIBLE);
                레이4스위치2.setVisibility(View.VISIBLE);
                레이5작동버튼.setVisibility(View.VISIBLE);
                레이5나가기.setVisibility(View.VISIBLE);
                레이5스위치1.setVisibility(View.VISIBLE);
                레이5스위치2.setVisibility(View.VISIBLE);
                레이6작동버튼.setVisibility(View.VISIBLE);
                레이6나가기.setVisibility(View.VISIBLE);
                레이6식바.setVisibility(View.VISIBLE);
                레이6스위치1.setVisibility(View.VISIBLE);
                레이6스위치2.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        로티레이[1].addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                로티레이[1].setVisibility(View.GONE);
            }
            @Override
            public void onAnimationCancel(Animator animation) {
                로티레이[1].setVisibility(View.GONE);
            }
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        로티레이[2].addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                로티레이[2].setVisibility(View.GONE);
            }
            @Override
            public void onAnimationCancel(Animator animation) {
                로티레이[2].setVisibility(View.GONE);
            }
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        로티레이[3].addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }
            @Override
            public void onAnimationEnd(Animator animation) {
                로티레이[3].setVisibility(View.GONE);
            }
            @Override
            public void onAnimationCancel(Animator animation) {
                로티레이[3].setVisibility(View.GONE);
            }
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }
    void 손님애니(){
        Animation mAni1,mAni2,mAni3,mAni4;
        mAni1 = AnimationUtils.loadAnimation(this, R.anim.alpha3);
        mAni2 = AnimationUtils.loadAnimation(this, R.anim.size2);
        mAni3 = AnimationUtils.loadAnimation(this, R.anim.size2);
        mAni4 = AnimationUtils.loadAnimation(this, R.anim.size2);

        터치존.startAnimation(mAni1);
        말풍선이미지.startAnimation(mAni2);
        터치유도텍스트.startAnimation(mAni3);
        말풍선레이아웃.startAnimation(mAni4);
        터치존.startAnimation(mAni1);
        mAni1.setAnimationListener(new Animation.AnimationListener(){
            public void onAnimationEnd(Animation animation){
                }
            public void onAnimationStart(Animation animation){
                터치존.setVisibility(View.VISIBLE);}
            public void onAnimationRepeat(Animation animation){;}
        });
    }
    void 생산텍스트애니(int 요리번호, int 생산수){
        Animation mAni1;
        mAni1 = AnimationUtils.loadAnimation(this, R.anim.allani2);
        생산텍스트.setText(""+요리이름[요리번호]+" + "+생산수);
        생산텍스트.startAnimation(mAni1);
        mAni1.setAnimationListener(new Animation.AnimationListener(){
            public void onAnimationEnd(Animation animation){
                생산텍스트.setVisibility(INVISIBLE);}
            public void onAnimationStart(Animation animation){
                생산텍스트.setVisibility(View.VISIBLE);}
            public void onAnimationRepeat(Animation animation){;}
        });
    }
    void 시스템텍스트애니(String 멘트){
        Animation mAni1;
        mAni1 = AnimationUtils.loadAnimation(this, 애니메이션id[5]);
        시스템텍스트.setText(""+멘트);
        시스템텍스트.startAnimation(mAni1);
        mAni1.setAnimationListener(new Animation.AnimationListener(){
            public void onAnimationEnd(Animation animation){
                시스템텍스트.setVisibility(INVISIBLE);}
            public void onAnimationStart(Animation animation){
                시스템텍스트.setVisibility(View.VISIBLE);}
            public void onAnimationRepeat(Animation animation){;}
        });
    }
    void 레이아웃띄우기(int 띄울번호){
        for(int i=0;i<14;i++){레이[i].setVisibility(View.GONE);}
        레이[띄울번호].setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.alpha1);
        레이[띄울번호].startAnimation(animation);
    }
    void 레이아웃끄기(int 끌번호){
        레이[끌번호].setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.alpha2);
        레이[끌번호].startAnimation(animation);
    }
    void 버튼리스너(){
        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (view.getId()) {
                    case R.id.튜토리얼이전:
                        튜토이미지번호--;
                        if(튜토이미지번호<0){
                            튜토이미지번호=0;
                            시스템텍스트애니("처음페이지입니다");
                        }
                        튜토리얼이미지(튜토이미지번호);
                        break;
                    case R.id.튜토리얼다음:
                        튜토이미지번호++;
                        if(튜토이미지번호>3){
                            튜토이미지번호=3;
                            시스템텍스트애니("마지막페이지입니다");
                        }
                        튜토리얼이미지(튜토이미지번호);
                        break;
                    case R.id.튜토리얼나가기:

                        튜토리얼레이.setVisibility(View.GONE);
                        if(튜토리얼체크.isChecked()==true){
                            처음접속=1;
                            prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
                            SharedPreferences.Editor editor =  prefs.edit();
                            editor.putInt("처음접속",처음접속);
                            editor.commit();
                        }
                        break;
                    case R.id.구글버튼5:
                        for(int i=0;i<14;i++){레이[i].setVisibility(View.GONE);}
                        레이인포레이.setVisibility(View.GONE);

                        레이도움말레이.setVisibility(View.VISIBLE);
                        break;
                    case R.id.도움말나가기:
                        레이도움말레이.setVisibility(View.GONE);
                        break;
                    case R.id.구글버튼4:
                        for(int i=0;i<14;i++){레이[i].setVisibility(View.GONE);}
                        레이인포전체업데이트();
                        레이인포레이.setVisibility(View.VISIBLE);
                        break;
                    case R.id.레이인포나가기:
                        레이인포레이.setVisibility(View.GONE);
                        break;
                    case R.id.요리하기버튼 : //요리하기
                        레이1단일업데이트(현재선택요리);
                       레이아웃띄우기(0);
                        break ;
                    case R.id.메뉴버튼1 : //시약가게
                        레이2단일업데이트(현재재료);
                        레이아웃띄우기(1);
                        break ;
                    case R.id.메뉴버튼2 : //
                        레이아웃띄우기(2);
                        break ;
                    case R.id.메뉴버튼3 :
                        레이아웃띄우기(3);
                        break ;
                    case R.id.메뉴버튼4 :
                        레이아웃띄우기(4);
                        break ;
                    case R.id.메뉴버튼5 :
                        레이아웃띄우기(5);
                        break ;
                    case R.id.메뉴버튼6 :
                        레이아웃띄우기(6);
                        break ;
                    case R.id.메뉴버튼7 :

                        레이10작동3();
                        레이아웃띄우기(9);
                        break ;
                    case R.id.메뉴버튼8 :
                        레이아웃띄우기(10);
                        break ;
                    case R.id.구글버튼1 :
                        광고라이징();
                        break ;
                    case R.id.구글버튼2 :
                        레이아웃띄우기(7);
                        break ;
                    case R.id.구글버튼3 :
                        레이아웃띄우기(8);
                        break ;
                    case R.id.레이1나가기 :
                        레이아웃끄기(0);
                        break ;
                    case R.id.레이1작동버튼:
                        if(요리중==0){
                        레이1작동(레이1식바.getProgress());
                        레이아웃끄기(0);
                        }else{
                            시스템텍스트애니("이미 기계가 작동중이군....");
                        }

                      break;
                    case R.id.레이2나가기 :
                        레이아웃끄기(1);
                        break ;
                    case R.id.레이2작동버튼:
                        레이2작동();
                        break;
                    case R.id.레이3나가기 :
                        레이아웃끄기(2);
                        break ;
                    case R.id.레이3작동버튼:
                        레이3작동();
                        break;
                    case R.id.레이4나가기 :
                        레이아웃끄기(3);
                        break ;
                    case R.id.레이4작동버튼:
                        레이4작동();
                        break;
                    case R.id.레이5나가기 :
                        레이아웃끄기(4);
                        break ;
                    case R.id.레이5작동버튼:
                        레이5작동();
                        break;
                    case R.id.레이6나가기 :
                        레이아웃끄기(5);
                        break ;
                    case R.id.레이6작동버튼:
                        레이6작동();
                        break;
                    case R.id.레이7나가기 :
                        레이아웃끄기(6);
                        break ;
                    case R.id.레이7작동버튼:
                        레이7작동();
                    case R.id.레이8나가기 :
                        레이아웃끄기(7);
                        break ;
                    case R.id.레이8작동버튼:
                        레이8작동();
                        break;
                    case R.id.레이9나가기 :
                        레이아웃끄기(8);
                        break ;
                    case R.id.레이9작동버튼:
                        레이9작동1();
                        break;
                    case R.id.레이9작동버튼2:
                        레이9작동2();
                        break;
                    case R.id.레이10나가기 :
                        레이아웃끄기(9);
                        break ;
                    case R.id.레이10작동버튼:
                        레이10전체업데이트();
                        if(결투장가능여부==0){
                        if(명성>=100000){
                        if(대결중==0){

                         레이10작동();
                        }else{
                        시스템텍스트애니("지금 대결에 집중해!!");
                        }}else{
                            시스템텍스트애니("명성 10만 이상만 참여가능하다구!");
                        }
                        }else{
                            시스템텍스트애니("지금은 대결기간이 아니야...");
                        }
                        break;
                    case R.id.레이10작동버튼2:
                            랭크1레이.setVisibility(View.VISIBLE);
                            랭크2레이.setVisibility(INVISIBLE);
                        break;
                    case R.id.레이10작동버튼3:
                        랭크2레이.setVisibility(View.VISIBLE);
                        랭크1레이.setVisibility(INVISIBLE);
                        break;
                    case R.id.레이랭크나가기:
                        랭크1레이.setVisibility(INVISIBLE);
                        break;

                    case R.id.레이랭크2나가기:
                        랭크2레이.setVisibility(INVISIBLE);
                        break;

                    case R.id.레이11나가기 :
                        레이아웃끄기(10);
                        break ;
                    case R.id.레이11작동버튼:
                        레이11작동1();
                        break ;
                    case R.id.레이12작동버튼 :
                        레이아웃끄기(11);
                        break ;
                    case R.id.레이13작동버튼1:
                        레이아웃끄기(12);
                        break ;
                    case R.id.레이13작동버튼2:
                        레이아웃끄기(12);
                        break ;
                    case R.id.레이14작동버튼:
                        finish();
                        break ;
                    case R.id.퀵슬롯버튼1:
                        현재줄요리=0;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼2:
                        현재줄요리=1;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼3:
                        현재줄요리=2;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼4:
                        현재줄요리=3;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼5:
                        현재줄요리=4;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼6:
                        현재줄요리=5;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼7:
                        현재줄요리=6;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼8:
                        현재줄요리=7;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼9:
                        현재줄요리=8;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼10:
                        현재줄요리=9;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼11:
                        현재줄요리=10;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼12:
                        현재줄요리=11;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼13:
                        현재줄요리=12;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼14:
                        현재줄요리=13;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼15:
                        현재줄요리=14;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼16:
                        현재줄요리=15;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼17:
                        현재줄요리=16;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼18:
                        현재줄요리=17;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼19:
                        현재줄요리=18;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼20:
                        현재줄요리=19;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼21:
                        현재줄요리=20;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼22:
                        현재줄요리=21;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼23:
                        현재줄요리=22;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼24:
                        현재줄요리=23;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼25:
                        현재줄요리=24;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼26:
                        현재줄요리=25;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼27:
                        현재줄요리=26;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼28:
                        현재줄요리=27;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼29:
                        현재줄요리=28;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼30:
                        현재줄요리=29;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼31:
                        현재줄요리=30;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼32:
                        현재줄요리=31;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼33:
                        현재줄요리=32;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼34:
                        현재줄요리=33;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼35:
                        현재줄요리=34;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼36:
                        현재줄요리=35;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼37:
                        현재줄요리=36;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼38:
                        현재줄요리=37;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼39:
                        현재줄요리=38;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼40:
                        현재줄요리=39;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼41:
                        현재줄요리=40;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼42:
                        현재줄요리=41;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼43:
                        현재줄요리=42;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼44:
                        현재줄요리=43;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼45:
                        현재줄요리=44;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼46:
                        현재줄요리=45;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼47:
                        현재줄요리=46;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.퀵슬롯버튼48:
                        현재줄요리=47;
                        퀵슬롯단일업데이트(현재줄요리);
                        break ;
                    case R.id.레이1선택버튼1:
                        현재선택요리=0;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼2:
                        현재선택요리=1;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼3:
                        현재선택요리=2;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼4:
                        현재선택요리=3;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼5:
                        현재선택요리=4;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼6:
                        현재선택요리=5;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼7:
                        현재선택요리=6;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼8:
                        현재선택요리=7;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼9:
                        현재선택요리=8;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼10:
                        현재선택요리=9;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼11:
                        현재선택요리=10;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼12:
                        현재선택요리=11;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼13:
                        현재선택요리=12;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼14:
                        현재선택요리=13;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼15:
                        현재선택요리=14;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼16:
                        현재선택요리=15;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼17:
                        현재선택요리=16;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼18:
                        현재선택요리=17;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼19:
                        현재선택요리=18;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼20:
                        현재선택요리=19;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼21:
                        현재선택요리=20;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼22:
                        현재선택요리=21;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼23:
                        현재선택요리=22;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼24:
                        현재선택요리=23;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼25:
                        현재선택요리=24;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼26:
                        현재선택요리=25;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼27:
                        현재선택요리=26;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼28:
                        현재선택요리=27;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼29:
                        현재선택요리=28;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼30:
                        현재선택요리=29;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼31:
                        현재선택요리=30;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼32:
                        현재선택요리=31;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼33:
                        현재선택요리=32;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼34:
                        현재선택요리=33;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼35:
                        현재선택요리=34;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼36:
                        현재선택요리=35;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼37:
                        현재선택요리=36;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼38:
                        현재선택요리=37;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼39:
                        현재선택요리=38;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼40:
                        현재선택요리=39;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼41:
                        현재선택요리=40;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼42:
                        현재선택요리=41;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼43:
                        현재선택요리=42;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼44:
                        현재선택요리=43;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼45:
                        현재선택요리=44;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼46:
                        현재선택요리=45;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼47:
                        현재선택요리=46;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이1선택버튼48:
                        현재선택요리=47;
                        레이1단일업데이트(현재선택요리);
                        break ;
                    case R.id.레이2선택버튼1:
                        현재재료=0;
                        레이2단일업데이트(현재재료);
                        break ;
                    case R.id.레이2선택버튼2:
                        현재재료=1;
                        레이2단일업데이트(현재재료);
                        break ;
                    case R.id.레이2선택버튼3:
                        현재재료=2;
                        레이2단일업데이트(현재재료);
                        break ;
                    case R.id.레이2선택버튼4:
                        현재재료=3;
                        레이2단일업데이트(현재재료);
                        break ;
                    case R.id.레이2선택버튼5:
                        현재재료=4;
                        레이2단일업데이트(현재재료);
                        break ;
                    case R.id.레이2선택버튼6:
                        현재재료=5;
                        레이2단일업데이트(현재재료);
                        break ;
                    case R.id.레이2선택버튼7:
                        현재재료=6;
                        레이2단일업데이트(현재재료);
                        break ;
                    case R.id.레이2선택버튼8:
                        현재재료=7;
                        레이2단일업데이트(현재재료);
                        break ;
                    case R.id.레이2선택버튼9:
                        현재재료=8;
                        레이2단일업데이트(현재재료);
                        break ;
                    case R.id.레이2선택버튼10:
                        현재재료=9;
                        레이2단일업데이트(현재재료);
                        break ;
                }
            }
        } ;

        for(int i=0;i<5;i++){
            구글버튼[i].setOnClickListener(onClickListener) ;
        }
        for(int i=0;i<8;i++){
            메뉴버튼[i].setOnClickListener(onClickListener) ;
        }
        for(int i=0;i<10;i++){
            레이2선택버튼[i].setOnClickListener(onClickListener) ;
        }
        for(int i=0;i<48;i++){
            퀵슬롯버튼[i].setOnClickListener(onClickListener);
            레이1선택버튼[i].setOnClickListener(onClickListener);
        }
        튜토리얼다음.setOnClickListener(onClickListener);
        튜토리얼이전.setOnClickListener(onClickListener);
        튜토리얼나가기.setOnClickListener(onClickListener);
        요리하기버튼.setOnClickListener(onClickListener);
        레이1나가기.setOnClickListener(onClickListener);
        레이2나가기.setOnClickListener(onClickListener);
        레이3나가기.setOnClickListener(onClickListener);
        레이4나가기.setOnClickListener(onClickListener);
        레이5나가기.setOnClickListener(onClickListener);
        레이6나가기.setOnClickListener(onClickListener);
        레이7나가기.setOnClickListener(onClickListener);
        레이8나가기.setOnClickListener(onClickListener);
        레이9나가기.setOnClickListener(onClickListener);
        레이10나가기.setOnClickListener(onClickListener);
        레이랭크나가기.setOnClickListener(onClickListener);
        레이랭크2나가기.setOnClickListener(onClickListener);
        레이11나가기.setOnClickListener(onClickListener);
        레이1작동버튼.setOnClickListener(onClickListener);
        레이2작동버튼.setOnClickListener(onClickListener);
        레이3작동버튼.setOnClickListener(onClickListener);
        레이4작동버튼.setOnClickListener(onClickListener);
        레이5작동버튼.setOnClickListener(onClickListener);
        레이6작동버튼.setOnClickListener(onClickListener);
        레이7작동버튼.setOnClickListener(onClickListener);
        레이8작동버튼.setOnClickListener(onClickListener);
        레이9작동버튼.setOnClickListener(onClickListener);
        레이9작동버튼2.setOnClickListener(onClickListener);
        레이10작동버튼.setOnClickListener(onClickListener);
        레이10작동버튼2.setOnClickListener(onClickListener);
        레이10작동버튼3.setOnClickListener(onClickListener);
        레이11작동버튼.setOnClickListener(onClickListener);
        레이12작동버튼.setOnClickListener(onClickListener);
        레이13작동버튼1.setOnClickListener(onClickListener);
        레이13작동버튼2.setOnClickListener(onClickListener);
        레이14작동버튼.setOnClickListener(onClickListener);
        레이인포나가기.setOnClickListener(onClickListener);
        도움말나가기.setOnClickListener(onClickListener);
        구글버튼[3].setOnClickListener(onClickListener);
    }
    void 로그인(){//책갈피2

        time = new Date();
        format1 = new SimpleDateFormat ( "yyyyMMdd");
        format2 = new SimpleDateFormat ( "HH");
        format3 = new SimpleDateFormat ( "mm");
        시작시간일=format1.format(time);
        시작시간시=format2.format(time);
        시작시간분=format3.format(time);
        prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
        종료시간일 =prefs.getString("종료시간일", 시작시간일);
        종료시간시 =prefs.getString("종료시간시", 시작시간시);
        종료시간분 =prefs.getString("종료시간분", 시작시간분);
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }

    };
    void 로그아웃(){

        time = new Date();
        format1 = new SimpleDateFormat ( "yyyyMMdd");
        format2 = new SimpleDateFormat ( "HH");
        format3 = new SimpleDateFormat ( "mm");
        종료시간일=format1.format(time);
        종료시간시=format2.format(time);
        종료시간분=format3.format(time);
        prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
        SharedPreferences.Editor editor =  prefs.edit();
        editor.putString("종료시간일",종료시간일 );
        editor.putString("종료시간시",종료시간시 );
        editor.putString("종료시간분",종료시간분 );
        editor.commit();
    }
    void 가격설정(){
        요리가격[0][0]=300;
        요리강화비용[0][0]=5000;
        캐릭터강화비용[0][0]=10000;

        for(int i=1;i<48;i++){
            요리가격[i][0]=(int)(요리가격[i-1][0]*1.2);
            요리강화비용[i][0]=(int)((요리강화비용[i-1][0]+5000)*1.1);
        }
        for(int i=1;i<22;i++){
            캐릭터강화비용[i][0]=(int)(캐릭터강화비용[i-1][0]*1.3);
        }
        for(int i=0;i<48;i++){
           for(int j=1;j<16;j++){
            요리가격[i][j]=(long)(요리가격[i][j-1]*1.1);
               요리강화비용[i][j]=(int)(요리강화비용[i][j-1]*1.4);
        }
        }
        for(int i=0;i<22;i++){
            for(int j=1;j<16;j++){
                캐릭터강화비용[i][j]=(long)(캐릭터강화비용[i][j-1]*1.5);
            }}
    }
    void 초반설정(){
        튜토리얼();
        인터넷연결확인();

        파이어베이스();
        가격설정();

        버튼리스너();
        소지금업데이트();
        퀵슬롯전체업데이트(48);
        레이1단일업데이트(0);
        퀵슬롯단일업데이트(0);
        레이2단일업데이트(0);
        레이2전체업데이트();
        레이3전체업데이트();
        레이4전체업데이트();
        레이5전체업데이트();
        레이6전체업데이트();
        레이7전체업데이트();
        레이8전체업데이트();
        레이9전체업데이트();
        레이10전체업데이트();
        레이11전체업데이트();
        레이랭크작동();
        try {
            레이12전체업데이트();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        레이13전체업데이트();

        식바();
        터치();
        광고셋팅();
        레이1전체업데이트();
    }
    void 이미지삽입(){

        레이1나가기.setBackgroundResource(R.drawable.outbuttonfhd);
        레이2나가기.setBackgroundResource(R.drawable.outbuttonfhd);
        레이3나가기.setBackgroundResource(R.drawable.outbuttonfhd);
        레이4나가기.setBackgroundResource(R.drawable.outbuttonfhd);
        레이5나가기.setBackgroundResource(R.drawable.outbuttonfhd);
        레이6나가기.setBackgroundResource(R.drawable.outbuttonfhd);
        레이6나가기.setBackgroundResource(R.drawable.outbuttonfhd);
        레이7나가기.setBackgroundResource(R.drawable.outbuttonfhd);
        레이8나가기.setBackgroundResource(R.drawable.outbuttonfhd);
        레이9나가기.setBackgroundResource(R.drawable.outbuttonfhd);
        레이10나가기.setBackgroundResource(R.drawable.outbuttonfhd);
        레이랭크나가기.setBackgroundResource(R.drawable.outbuttonfhd);
        레이랭크2나가기.setBackgroundResource(R.drawable.outbuttonfhd);
        레이11나가기.setBackgroundResource(R.drawable.outbuttonfhd);
        레이인포나가기.setBackgroundResource(R.drawable.outbuttonfhd);
        도움말나가기.setBackgroundResource(R.drawable.outbuttonfhd);
        if(배경이미지.getLayoutParams().width>=1200){
            배경이미지.setBackgroundResource(R.drawable.fullbackfhd);
        }else  if(배경이미지.getLayoutParams().width>=800){
            배경이미지.setBackgroundResource(R.drawable.fullbackhd);
        }else{
            배경이미지.setBackgroundResource(R.drawable.fullbacksd);
        }


        if(요리하기버튼.getLayoutParams().width>=1024){
            요리하기버튼이미지.setBackgroundResource(R.drawable.cookbuttonfhd);
        }else if(요리하기버튼.getLayoutParams().width>=512){
            요리하기버튼이미지.setBackgroundResource(R.drawable.cookbuttonhd);
        }else{
            요리하기버튼이미지.setBackgroundResource(R.drawable.cookbuttonsd);
        }
        if(퀵슬롯버튼[0].getLayoutParams().width>=512){
            for(int i=0;i<48;i++){
                퀵슬롯버튼[i].setBackgroundResource(요리이미지hd[i]);
                레이1요리이미지[i].setBackgroundResource(요리이미지fhd[i]);
                퀵슬롯레이[i].setBackgroundResource(R.drawable.buttonwhfhd);
                레이1선택버튼[i].setBackgroundResource(R.drawable.checkwhfhd);
            }
        }else if(퀵슬롯버튼[0].getLayoutParams().width>=256){
            for(int i=0;i<48;i++){
                퀵슬롯버튼[i].setBackgroundResource(요리이미지sd[i]);
                레이1요리이미지[i].setBackgroundResource(요리이미지hd[i]);
                퀵슬롯레이[i].setBackgroundResource(R.drawable.buttonwhhd);
                레이1선택버튼[i].setBackgroundResource(R.drawable.checkwhhd);
            }
        }else{
            for(int i=0;i<48;i++){
                퀵슬롯버튼[i].setBackgroundResource(요리이미지sd[i]);
                레이1요리이미지[i].setBackgroundResource(요리이미지sd[i]);
                퀵슬롯레이[i].setBackgroundResource(R.drawable.buttonwhsd);
                레이1선택버튼[i].setBackgroundResource(R.drawable.checkwhsd);
            }
        }

        if(메뉴버튼[0].getLayoutParams().width>=256){
            for(int i=0;i<8;i++){
                메뉴버튼[i].setBackgroundResource(메뉴버튼이미지fhd[i]);
            }
            for(int i=0;i<5;i++){
                구글버튼[i].setBackgroundResource(구글버튼이미지fhd[i]);
            }
        }else if(메뉴버튼[0].getLayoutParams().width>=128){
            for(int i=0;i<8;i++){
                메뉴버튼[i].setBackgroundResource(메뉴버튼이미지hd[i]);
            }
            for(int i=0;i<5;i++){
                구글버튼[i].setBackgroundResource(구글버튼이미지hd[i]);
            }
        }else{
            for(int i=0;i<8;i++){
                메뉴버튼[i].setBackgroundResource(메뉴버튼이미지sd[i]);
            }
            for(int i=0;i<5;i++){
                구글버튼[i].setBackgroundResource(구글버튼이미지sd[i]);
            }
        }
        if(레이2요리이미지레이[0].getLayoutParams().width>=1024){
            for(int i=0;i<10;i++){
                레이2요리이미지[i].setBackgroundResource(재료이미지fhd[i]);
                레이2선택버튼[i].setBackgroundResource(R.drawable.checkwhfhd);
            }
        }else if(레이2요리이미지레이[0].getLayoutParams().width>=512){
            for(int i=0;i<10;i++){
                레이2요리이미지[i].setBackgroundResource(재료이미지hd[i]);
                레이2선택버튼[i].setBackgroundResource(R.drawable.checkwhhd);
            }
        }else{
            for(int i=0;i<10;i++){
                레이2요리이미지[i].setBackgroundResource(재료이미지sd[i]);
                레이2선택버튼[i].setBackgroundResource(R.drawable.checkwhsd);
            }
        }

        if(말풍선레이아웃.getLayoutParams().width>=1024){
            말풍선레이아웃.setBackgroundResource(R.drawable.tolkfhd);
        }else if(말풍선레이아웃.getLayoutParams().width>=512){
            말풍선레이아웃.setBackgroundResource(R.drawable.tolkhd);
        }else{
            말풍선레이아웃.setBackgroundResource(R.drawable.tolksd);
        }


//        레이1요리이미지[i].setBackgroundResource(요리이미지hd[i]);
//        //  레이1요리이미지레이[i].setBackgroundResource(R.drawable.buttonwhhd);
//        for(int i=0;i<8;i++){ 메뉴버튼[i].setBackgroundResource(R.drawable.a0);}
//        for(int i=0;i<3;i++){구글버튼[i].setBackgroundResource(R.drawable.a0);}
//        손님이미지.setBackgroundResource(R.drawable.buttonor);
//        말풍선이미지.setBackgroundResource(R.drawable.buttonor);
//        요리하기버튼이미지.setBackgroundResource(테스트이미지id[0]);
//        //레이1이미지
//        for(int i=0;i<48;i++){
//        레이1요리이미지[i].setBackgroundResource(테스트이미지id[i]);
//        레이1선택버튼[i].setBackgroundResource(테스트이미지id[i]);}
//        레이1나가기.setBackgroundResource(테스트이미지id[0]);
//        //레이2이미지
//        for(int i=0;i<10;i++) {
//        레이2요리이미지[i].setBackgroundResource(R.drawable.a0);
//        레이2선택버튼[i].setBackgroundResource(테스트이미지id[i]);}
//        레이2나가기.setBackgroundResource(테스트이미지id[0]);
//        //레이3이미지
//        레이3이미지.setBackgroundResource(R.drawable.a0);
//        레이3나가기.setBackgroundResource(테스트이미지id[0]);
//        //레이4이미지
//        레이4이미지.setBackgroundResource(R.drawable.a0);
//        레이4나가기.setBackgroundResource(테스트이미지id[0]);
//        //레이5이미지
//        레이5이미지.setBackgroundResource(R.drawable.a0);
//        레이5나가기.setBackgroundResource(테스트이미지id[0]);
//        //레이6이미지
//        레이6이미지.setBackgroundResource(R.drawable.a0);
//        레이6나가기.setBackgroundResource(테스트이미지id[0]);
//        //레이7이미지
//        레이7나가기.setBackgroundResource(테스트이미지id[0]);
//        //레이8이미지
//        레이8이미지.setBackgroundResource(R.drawable.a0);
//        레이8나가기.setBackgroundResource(테스트이미지id[0]);
//        //레이9이미지
//        레이9이미지.setBackgroundResource(R.drawable.a0);
//        레이9나가기.setBackgroundResource(테스트이미지id[0]);
//        //레이10이미지
//        레이10이미지1.setBackgroundResource(R.drawable.a0);
//        레이10이미지2.setBackgroundResource(R.drawable.a0);
//        레이10나가기.setBackgroundResource(테스트이미지id[0]);
//        //레이11이미지
//        레이11이미지.setBackgroundResource(R.drawable.a0);
//        레이11나가기.setBackgroundResource(테스트이미지id[0]);


    }
    void 크기조정(){


        ////laymain구성&&튜토리얼구성==================================================================================================================================================================
        int 최상위y,최상위x,레이1상위y,레이1상위x;
        최상위y = 최상위.getHeight();
        최상위x = 최상위.getWidth();
        레이1상위y = (int)(최상위y*0.75);
        레이1상위x = (int)(최상위x*0.9);
        튜토리얼레이.getLayoutParams().height=(int)(최상위y);
        튜토리얼레이.requestLayout();
        튜토리얼버튼레이.getLayoutParams().height=(int)(최상위x*1.2);
        튜토리얼버튼레이.requestLayout();
        튜토리얼이미지.getLayoutParams().height=(int)(최상위x);
        튜토리얼이미지.requestLayout();
        튜토리얼이전.getLayoutParams().height=(int)(최상위x*0.15);
        튜토리얼이전.getLayoutParams().width=(int)(최상위x*0.15);
        튜토리얼이전.requestLayout();
        튜토리얼다음.getLayoutParams().height=(int)(최상위x*0.15);
        튜토리얼다음.getLayoutParams().width=(int)(최상위x*0.15);
        튜토리얼다음.requestLayout();
        튜토리얼나가기.getLayoutParams().height=(int)(최상위x*0.1);
        튜토리얼나가기.getLayoutParams().width=(int)(최상위x*0.1);
        튜토리얼나가기.requestLayout();

        상위.getLayoutParams().height=최상위y;
        로딩프로그레스.setProgress(10);
        로딩텍스트.setText(""+로딩프로그레스.getProgress()+"%");
        로딩멘트텍스트.setText("가게 오픈 준비중 입니다.");
        상위.requestLayout();
        상단.getLayoutParams().height=(int)(상위.getLayoutParams().height*0.05);
        상단.requestLayout();
        중단.getLayoutParams().height=(int)(상위.getLayoutParams().height*0.5);
        중단.requestLayout();
        손님레이아웃.getLayoutParams().height=(int)(중단.getLayoutParams().height*0.8);
        손님레이아웃.getLayoutParams().width=(int)(손님레이아웃.getLayoutParams().height*0.75);
        손님레이아웃.requestLayout();
        말풍선레이아웃.getLayoutParams().height=(int)(손님레이아웃.getLayoutParams().height*0.6);
        말풍선레이아웃.getLayoutParams().width=(int)(손님레이아웃.getLayoutParams().height*0.6);
        말풍선레이아웃.requestLayout();
        말풍선이미지.getLayoutParams().height=(int)(말풍선레이아웃.getLayoutParams().height*0.85);
        말풍선이미지.getLayoutParams().width=(int)(말풍선레이아웃.getLayoutParams().width*0.85);
        말풍선이미지.requestLayout();
        말풍선텍스트.getLayoutParams().height=(int)(말풍선이미지.getLayoutParams().height*0.66);
        말풍선텍스트.getLayoutParams().width=(int)말풍선이미지.getLayoutParams().width;
        말풍선텍스트.requestLayout();
        메뉴스크롤.getLayoutParams().width=최상위y/12;
        메뉴스크롤.requestLayout();
        캐쉬스크롤.getLayoutParams().width=최상위y/12;
        캐쉬스크롤.requestLayout();
        배경이미지.getLayoutParams().height=(int)(최상위x*0.75);
        배경이미지.getLayoutParams().width=(int)(최상위x);
        배경이미지.requestLayout();
        for(int i=0;i<10;i++){
            메인레이정보텍스트[i].getLayoutParams().width=말풍선텍스트.getLayoutParams().width;
            메인레이정보텍스트[i].getLayoutParams().height=말풍선텍스트.getLayoutParams().height;
            메인레이정보텍스트[i].requestLayout();
        }
        for(int i=0;i<8;i++){
            메뉴버튼[i].getLayoutParams().width=(int)(메뉴스크롤.getLayoutParams().width*0.8);
            메뉴버튼[i].getLayoutParams().height=(int)(메뉴스크롤.getLayoutParams().width*0.8);
            메뉴버튼[i].requestLayout();
        }
        for(int i=0;i<5;i++){
            구글버튼[i].getLayoutParams().width=(int)(메뉴스크롤.getLayoutParams().width*0.8);
            구글버튼[i].getLayoutParams().height=(int)(메뉴스크롤.getLayoutParams().width*0.8);
            구글버튼[i].requestLayout();
        }
        하단.getLayoutParams().height=(int)(상위.getLayoutParams().height*0.45);
        하단.requestLayout();
        퀵슬롯스크롤.getLayoutParams().height=(int)(하단.getLayoutParams().height*0.25);
        퀵슬롯스크롤.requestLayout();
        for(int i=0;i<48;i++){
            퀵슬롯버튼[i].getLayoutParams().width=(int)(퀵슬롯스크롤.getLayoutParams().height*0.9);
            퀵슬롯버튼[i].getLayoutParams().height=(int)(퀵슬롯스크롤.getLayoutParams().height*0.9);
            퀵슬롯버튼[i].requestLayout();
            퀵슬롯레이[i].getLayoutParams().width=(int)(퀵슬롯스크롤.getLayoutParams().height*0.9);
            퀵슬롯레이[i].getLayoutParams().height=(int)(퀵슬롯스크롤.getLayoutParams().height*0.9);
            퀵슬롯레이[i].requestLayout();
        }
        요리하기레이아웃.getLayoutParams().height=(int)(하단.getLayoutParams().height*0.6);
        요리하기레이아웃.requestLayout();
        요리하기버튼.getLayoutParams().height=(int)(요리하기레이아웃.getLayoutParams().height*0.9);
        요리하기버튼.getLayoutParams().width=(int)(요리하기레이아웃.getLayoutParams().height*0.9);
        요리하기버튼.requestLayout();
        요리하기버튼이미지.getLayoutParams().height=(int)(요리하기레이아웃.getLayoutParams().height*0.9);
        요리하기버튼이미지.getLayoutParams().width=(int)(요리하기레이아웃.getLayoutParams().height*0.9);
        요리하기버튼이미지.requestLayout();
        경험치레이아웃.getLayoutParams().height=(int)(하단.getLayoutParams().height*0.15);
        경험치레이아웃.requestLayout();
        로티레이[0].getLayoutParams().width=(int)(레이1상위x);
        로티레이[0].getLayoutParams().height=(int)(레이1상위y);
        시스템텍스트2.getLayoutParams().width=손님레이아웃.getLayoutParams().width;
        시스템텍스트2.getLayoutParams().height=(int)(최상위y*0.1);
        레이인포나가기.getLayoutParams().height=  (int)(레이1상위y*0.1*0.6);
        레이인포나가기.getLayoutParams().width=  (int)(레이1상위y*0.1*0.6);
        레이인포나가기.requestLayout();
        도움말나가기.getLayoutParams().height=  (int)(레이1상위y*0.1*0.6);
        도움말나가기.getLayoutParams().width=  (int)(레이1상위y*0.1*0.6);
        도움말나가기.requestLayout();
        ////lay1구성=====================================================================================================================================================================
        레이1상위.getLayoutParams().width=(int)(레이1상위x);
        레이1상위.getLayoutParams().height=(int)(레이1상위y);
        레이1상위.requestLayout();


        for(int i=0;i<48;i++){
            레이1요리이미지레이[i].getLayoutParams().width=(int)(레이1상위x*0.3);
            레이1요리이미지레이[i].requestLayout();
            레이1요리정보레이[i].getLayoutParams().width=(int)(레이1상위x*0.3);
            레이1요리정보레이[i].requestLayout();
            레이1요리재료레이[i].getLayoutParams().width=(int)(레이1상위x*0.3);
            레이1요리재료레이[i].requestLayout();
            레이1요리버튼레이[i].getLayoutParams().width=(int)(레이1상위x*0.1);
            레이1요리버튼레이[i].requestLayout();
            레이1선택버튼[i].getLayoutParams().width=(int)(레이1요리버튼레이[0].getLayoutParams().width*0.9);
            레이1선택버튼[i].getLayoutParams().height=(int)(레이1요리버튼레이[0].getLayoutParams().width*0.9);
            레이1선택버튼[i].requestLayout();
            요리스크롤[i].getLayoutParams().height=  레이1요리이미지레이[0].getLayoutParams().width;
            요리스크롤[i].requestLayout();
        }
        레이1나가기.getLayoutParams().height=  (int)(레이1상위y*0.1*0.6);
        레이1나가기.getLayoutParams().width=  (int)(레이1상위y*0.1*0.6);
        레이1나가기.requestLayout();

        ////lay2구성=====================================================================================================================================================================
        레이2상위.getLayoutParams().width=(int)(레이1상위x);
        레이2상위.getLayoutParams().height=(int)(레이1상위y);
        레이2상위.requestLayout();
        for (int i=0;i<10;i++){
            레이2요리이미지레이[i].getLayoutParams().width=(int)(레이1상위x*0.4);
            레이2요리이미지레이[i].requestLayout();
            레이2요리정보레이[i].getLayoutParams().width=(int)(레이1상위x*0.5);
            레이2요리정보레이[i].requestLayout();
            레이2요리버튼레이[i].getLayoutParams().width=(int)(레이1상위x*0.1);
            레이2요리버튼레이[i].requestLayout();
            레이2선택버튼[i].getLayoutParams().width=(int)(레이2요리버튼레이[0].getLayoutParams().width*0.9);
            레이2선택버튼[i].getLayoutParams().height=(int)(레이2요리버튼레이[0].getLayoutParams().width*0.9);
            레이2선택버튼[i].requestLayout();
            장보기스크롤[i].getLayoutParams().height=  레이2요리이미지레이[0].getLayoutParams().width;
            장보기스크롤[i].requestLayout();
        }
        레이2나가기.getLayoutParams().height=  (int)(레이1상위y*0.1*0.6);
        레이2나가기.getLayoutParams().width=  (int)(레이1상위y*0.1*0.6);
        레이2나가기.requestLayout();
        ////lay3구성=====================================================================================================================================================================
        레이3상위.getLayoutParams().width=(int)(레이1상위x);
        레이3상위.getLayoutParams().height=(int)(레이1상위y);
        레이3상위.requestLayout();
        레이3이미지레이.getLayoutParams().height=  (int)(레이1상위y*0.8*0.4);
        레이3이미지레이.getLayoutParams().width=  (int)(레이1상위y*0.8*0.4);
        레이3이미지레이.requestLayout();
        레이3정보레이.getLayoutParams().height=  (int)(레이1상위y*0.8*0.6);
        레이3정보레이.requestLayout();
        레이3나가기.getLayoutParams().height=  (int)(레이1상위y*0.1*0.6);
        레이3나가기.getLayoutParams().width=  (int)(레이1상위y*0.1*0.6);
        레이3나가기.requestLayout();
        ////lay4구성=====================================================================================================================================================================
        레이4상위.getLayoutParams().width=(int)(레이1상위x);
        레이4상위.getLayoutParams().height=(int)(레이1상위y);
        레이4상위.requestLayout();
        레이4이미지레이.getLayoutParams().height=  (int)(레이1상위y*0.7*0.5);
        레이4이미지레이.getLayoutParams().width=  (int)(레이1상위y*0.7*0.5);
        레이4이미지레이.requestLayout();
        레이4정보레이.getLayoutParams().height=  (int)(레이1상위y*0.7*0.5);
        레이4정보레이.requestLayout();
        레이4나가기.getLayoutParams().height=  (int)(레이1상위y*0.1*0.6);
        레이4나가기.getLayoutParams().width=  (int)(레이1상위y*0.1*0.6);
        레이4나가기.requestLayout();
        ////lay5구성=====================================================================================================================================================================
        레이5상위.getLayoutParams().width=(int)(레이1상위x);
        레이5상위.getLayoutParams().height=(int)(레이1상위y);
        레이5상위.requestLayout();
        레이5이미지레이.getLayoutParams().height=  (int)(레이1상위y*0.8*0.5);
        레이5이미지레이.getLayoutParams().width=  (int)(레이1상위y*0.8*0.5);
        레이5이미지레이.requestLayout();
        레이5정보레이.getLayoutParams().height=  (int)(레이1상위y*0.8*0.5);
        레이5정보레이.requestLayout();
        레이5나가기.getLayoutParams().height=  (int)(레이1상위y*0.1*0.6);
        레이5나가기.getLayoutParams().width=  (int)(레이1상위y*0.1*0.6);
        레이5나가기.requestLayout();
        ////lay6구성=====================================================================================================================================================================
        레이6상위.getLayoutParams().width=(int)(레이1상위x);
        레이6상위.getLayoutParams().height=(int)(레이1상위y);
        레이6상위.requestLayout();
        레이6이미지레이.getLayoutParams().height=  (int)(레이1상위y*0.7*0.5);
        레이6이미지레이.getLayoutParams().width=  (int)(레이6이미지레이.getLayoutParams().height*0.66);
        레이6이미지레이.requestLayout();
        레이6정보레이.getLayoutParams().height=  (int)(레이1상위y*0.7*0.5);
        레이6정보레이.requestLayout();
        레이6나가기.getLayoutParams().height=  (int)(레이1상위y*0.1*0.6);
        레이6나가기.getLayoutParams().width=  (int)(레이1상위y*0.1*0.6);
        레이6나가기.requestLayout();
        ////lay7구성=====================================================================================================================================================================
        레이7상위.getLayoutParams().width=(int)(레이1상위x);
        레이7상위.getLayoutParams().height=(int)(레이1상위y);
        레이7상위.requestLayout();
        레이7나가기.getLayoutParams().height=  (int)(레이1상위y*0.1*0.6);
        레이7나가기.getLayoutParams().width=  (int)(레이1상위y*0.1*0.6);
        레이7나가기.requestLayout();
        ////lay8구성=====================================================================================================================================================================
        레이8상위.getLayoutParams().width=(int)(레이1상위x);
        레이8상위.getLayoutParams().height=(int)(레이1상위y);
        레이8상위.requestLayout();
        레이8이미지레이.getLayoutParams().height=  (int)(레이1상위y*0.7*0.5);
        레이8이미지레이.getLayoutParams().width=  (int)(레이1상위y*0.7*0.5);
        레이8이미지레이.requestLayout();
        레이8정보레이.getLayoutParams().height=  (int)(레이1상위y*0.7*0.5);
        레이8정보레이.requestLayout();
        레이8나가기.getLayoutParams().height=  (int)(레이1상위y*0.1*0.6);
        레이8나가기.getLayoutParams().width=  (int)(레이1상위y*0.1*0.6);
        레이8나가기.requestLayout();
        ////lay9구성=====================================================================================================================================================================
        레이9상위.getLayoutParams().width=(int)(레이1상위x);
        레이9상위.getLayoutParams().height=(int)(레이1상위y);
        레이9상위.requestLayout();
        레이9이미지레이.getLayoutParams().height=  (int)(레이1상위y*0.7*0.5);
        레이9이미지레이.getLayoutParams().width=  (int)(레이1상위y*0.7*0.5);
        레이9이미지레이.requestLayout();
        레이9정보레이.getLayoutParams().height=  (int)(레이1상위y*0.7*0.5);
        레이9정보레이.requestLayout();
        레이9나가기.getLayoutParams().height=  (int)(레이1상위y*0.1*0.6);
        레이9나가기.getLayoutParams().width=  (int)(레이1상위y*0.1*0.6);
        레이9나가기.requestLayout();
        ////lay10구성=====================================================================================================================================================================
     //   레이10상위.getLayoutParams().width=(int)(레이1상위x);
     //   레이10상위.getLayoutParams().height=(int)(레이1상위y);
     //   레이10상위.requestLayout();
        레이10이미지1.getLayoutParams().height=  (int)(레이1상위y*0.8*0.5*0.75);
        레이10이미지1.getLayoutParams().width=  (int)(레이1상위y*0.8*0.5*0.75);
        레이10이미지1.requestLayout();
        레이랭크이미지1.getLayoutParams().height=  (int)(레이1상위y*0.9*0.166*0.95);
        레이랭크이미지1.getLayoutParams().width=  (int)(레이1상위y*0.9*0.166*0.95);
        레이랭크이미지1.requestLayout();
        레이랭크이미지2.getLayoutParams().height=  (int)(레이1상위y*0.9*0.166*0.95);
        레이랭크이미지2.getLayoutParams().width=  (int)(레이1상위y*0.9*0.166*0.95);
        레이랭크이미지2.requestLayout();
        레이랭크이미지3.getLayoutParams().height=  (int)(레이1상위y*0.9*0.166*0.95);
        레이랭크이미지3.getLayoutParams().width=  (int)(레이1상위y*0.9*0.166*0.95);
        레이랭크이미지3.requestLayout();
        레이10정보레이1.getLayoutParams().height=  (int)(레이1상위y*0.8*0.5);
        레이10정보레이1.requestLayout();
        레이10이미지2.getLayoutParams().height=  (int)(레이1상위y*0.8*0.5*0.75);
        레이10이미지2.getLayoutParams().width=  (int)(레이1상위y*0.8*0.5*0.75);
        레이10이미지2.requestLayout();
        레이10정보레이2.getLayoutParams().height=  (int)(레이1상위y*0.8*0.5);
        레이10정보레이2.requestLayout();
        레이10나가기.getLayoutParams().height=  (int)(레이1상위y*0.1*0.6);
        레이10나가기.getLayoutParams().width=  (int)(레이1상위y*0.1*0.6);
        레이10나가기.requestLayout();
        레이랭크나가기.getLayoutParams().height=  (int)(레이1상위y*0.1*0.6);
        레이랭크나가기.getLayoutParams().width=  (int)(레이1상위y*0.1*0.6);
        레이랭크나가기.requestLayout();
        레이랭크2나가기.getLayoutParams().height=  (int)(레이1상위y*0.1*0.6);
        레이랭크2나가기.getLayoutParams().width=  (int)(레이1상위y*0.1*0.6);
        레이랭크2나가기.requestLayout();
        for(int i=0;i<18;i++){
            레이랭크2정보레이[i].getLayoutParams().height= (int)(최상위y*0.8*0.15);
            레이랭크2정보레이[i].getLayoutParams().width= (int)(최상위x);
            레이랭크2정보레이[i].requestLayout();
            레이랭크2이미지[i].getLayoutParams().width=(int)(최상위y*0.8*0.15*0.95);
            레이랭크2이미지[i].getLayoutParams().height=(int)(최상위y*0.8*0.15*0.95);
            레이랭크2이미지[i].requestLayout();
        }
        ////lay11구성=====================================================================================================================================================================
        레이11상위.getLayoutParams().width=(int)(레이1상위x);
        레이11상위.getLayoutParams().height=(int)(레이1상위y);
        레이11상위.requestLayout();
        레이11이미지레이.getLayoutParams().height=  (int)(레이1상위y*0.8*0.5);
        레이11이미지레이.getLayoutParams().width=  (int)(레이1상위y*0.8*0.5);
        레이11이미지레이.requestLayout();
        레이11정보레이.getLayoutParams().height=  (int)(레이1상위y*0.8*0.5);
        레이11정보레이.requestLayout();
        레이11나가기.getLayoutParams().height=  (int)(레이1상위y*0.1*0.6);
        레이11나가기.getLayoutParams().width=  (int)(레이1상위y*0.1*0.6);
        레이11나가기.requestLayout();
        ////lay12구성=====================================================================================================================================================================
        레이12상위.getLayoutParams().width=(int)(레이1상위x);
        레이12상위.getLayoutParams().height=(int)(레이1상위y);
        레이12상위.requestLayout();
        ////lay13구성=====================================================================================================================================================================
        레이13상위.getLayoutParams().width=(int)(레이1상위x*0.6);
        레이13상위.getLayoutParams().height=(int)(레이1상위x*0.6);
        레이13상위.requestLayout();
        ////lay14구성=====================================================================================================================================================================
        레이14상위.getLayoutParams().width=(int)(레이1상위x*0.6);
        레이14상위.getLayoutParams().height=(int)(레이1상위x*0.6);
        레이14상위.requestLayout();


    }
    void 처음접속(){
        처음접속=1;
    }
    void 서버저장하기(){
        String 음식단계[]={
                "요리1단계","요리2단계","요리3단계","요리4단계","요리5단계",
                "요리6단계","요리7단계","요리8단계","요리9단계","요리10단계",
                "요리11단계","요리12단계","요리13단계","요리14단계","요리15단계",
                "요리16단계","요리17단계","요리18단계","요리19단계","요리20단계",
                "요리21단계","요리22단계","요리23단계","요리24단계","요리25단계",
                "요리26단계","요리27단계","요리28단계","요리29단계","요리30단계",
                "요리31단계","요리32단계","요리33단계","요리34단계","요리35단계",
                "요리36단계","요리37단계","요리38단계","요리39단계","요리40단계",
                "요리41단계","요리42단계","요리43단계","요리44단계","요리45단계",
                "요리46단계","요리47단계","요리48단계"};
        String 캐릭터단계[]={
                "캐릭터1단계","캐릭터2단계","캐릭터3단계","캐릭터4단계","캐릭터5단계",
                "캐릭터6단계","캐릭터7단계","캐릭터8단계","캐릭터9단계","캐릭터10단계",
                "캐릭터11단계","캐릭터12단계","캐릭터13단계","캐릭터14단계","캐릭터15단계",
                "캐릭터16단계","캐릭터17단계","캐릭터18단계","캐릭터19단계","캐릭터20단계",
                "캐릭터21단계","캐릭터22단계"};
        String 음식수량[]={
                "요리1수량","요리2수량","요리3수량","요리4수량","요리5수량",
                "요리6수량","요리7수량","요리8수량","요리9수량","요리10수량",
                "요리11수량","요리12수량","요리13수량","요리14수량","요리15수량",
                "요리16수량","요리17수량","요리18수량","요리19수량","요리20수량",
                "요리21수량","요리22수량","요리23수량","요리24수량","요리25수량",
                "요리26수량","요리27수량","요리28수량","요리29수량","요리30수량",
                "요리31수량","요리32수량","요리33수량","요리34수량","요리35수량",
                "요리36수량","요리37수량","요리38수량","요리39수량","요리40수량",
                "요리41수량","요리42수량","요리43수량","요리44수량","요리45수량",
                "요리46수량","요리47수량","요리48수량"};
        String 재료수량[]={
                "재료1수량","재료2단계","재료3수량","재료4수량","재료5수량",
                "재료6수량","재료7수량","재료8수량","재료9수량","재료10수량",
        };

        Map<String, Object> data = new HashMap<>();
        data.put("접속중", 0);
        data.put("소지금", 소지금);
        data.put("은행잔고", 은행잔고);
        data.put("명성", 명성);
        data.put("레벨", 레벨);
        data.put("솜", 솜);
        data.put("경험치", 경험치);
        data.put("승점", 승점);
        data.put("포장마차단계", 포장마차단계);
        data.put("마법제조기단계", 마법제조기단계);
        data.put("마케팅쿠폰", 마케팅쿠폰);
        data.put("마법기계강화쿠폰", 마법기계강화쿠폰);
        data.put("요리강화쿠폰", 요리강화쿠폰);
        data.put("강화보조제", 강화보조제);
        data.put("이벤트번호", 이벤트번호);
        data.put("결투도전권", 결투도전권);
        data.put("결투광고", 결투광고);
        data.put("회차", 회차);
        data.put("현재보상", 현재보상);
        data.put("보상상태", 보상상태);
        for(int i=0;i<48;i++){
        data.put(음식단계[i], 요리[i][12]);
        data.put(음식수량[i], 요리[i][0]);
        }
        for(int i=0;i<22;i++){
        data.put(캐릭터단계[i], 캐릭터[i][3]);
        }
        for(int i=0;i<10;i++){
            data.put(재료수량[i], 재료[i+1][0]);
        }

//        마케팅쿠폰=Integer.parseInt(document.getData().get("마케팅쿠폰").toString());
//        마법기계강화쿠폰=Integer.parseInt(document.getData().get("마법기계강화쿠폰").toString());
//        요리강화쿠폰=Integer.parseInt(document.getData().get("요리강화쿠폰").toString());
//        강화보조제=Integer.parseInt(document.getData().get("강화보조제").toString());
//        이벤트번호=Integer.parseInt(document.getData().get("이벤트번호").toString());
//        결투도전권=Integer.parseInt(document.getData().get("결투도전권").toString());
//        결투광고=Integer.parseInt(document.getData().get("결투광고").toString());
//        회차=Integer.parseInt(document.getData().get("회차").toString());
//        현재접속아이디=prefs.getString("현재접속아이디",현재접속아이디);
//
//        for(int i=0;i<48;i++){
//            요리[i][12]=Integer.parseInt(document.getData().get(음식단계[i]).toString());
//            요리[i][0]=Integer.parseInt(document.getData().get(음식수량[i]).toString());
//
//        }
//
//        for(int i=0;i<22;i++){
//            캐릭터[i][3]=Integer.parseInt(document.getData().get(캐릭터단계[i]).toString());
//        }
//        for(int i=0;i<10;i++){
//            재료[i][0]=Integer.parseInt(document.getData().get(재료수량[i]).toString());
//        }


        유저정보.collection("userid").document(현재접속아이디).update(data);
    }
    void 서버불러오기(){

        유저정보 = FirebaseFirestore.getInstance();
        유저= (CollectionReference) 유저정보.collection("userid");

        유저정보.collection("userid")
                .whereEqualTo("아이디", 현재접속아이디).limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {


                                String 음식단계[]={
                                        "요리1단계","요리2단계","요리3단계","요리4단계","요리5단계",
                                        "요리6단계","요리7단계","요리8단계","요리9단계","요리10단계",
                                        "요리11단계","요리12단계","요리13단계","요리14단계","요리15단계",
                                        "요리16단계","요리17단계","요리18단계","요리19단계","요리20단계",
                                        "요리21단계","요리22단계","요리23단계","요리24단계","요리25단계",
                                        "요리26단계","요리27단계","요리28단계","요리29단계","요리30단계",
                                        "요리31단계","요리32단계","요리33단계","요리34단계","요리35단계",
                                        "요리36단계","요리37단계","요리38단계","요리39단계","요리40단계",
                                        "요리41단계","요리42단계","요리43단계","요리44단계","요리45단계",
                                        "요리46단계","요리47단계","요리48단계"};
                                String 캐릭터단계[]={
                                        "캐릭터1단계","캐릭터2단계","캐릭터3단계","캐릭터4단계","캐릭터5단계",
                                        "캐릭터6단계","캐릭터7단계","캐릭터8단계","캐릭터9단계","캐릭터10단계",
                                        "캐릭터11단계","캐릭터12단계","캐릭터13단계","캐릭터14단계","캐릭터15단계",
                                        "캐릭터16단계","캐릭터17단계","캐릭터18단계","캐릭터19단계","캐릭터20단계",
                                        "캐릭터21단계","캐릭터22단계"};
                                String 음식수량[]={
                                        "요리1수량","요리2수량","요리3수량","요리4수량","요리5수량",
                                        "요리6수량","요리7수량","요리8수량","요리9수량","요리10수량",
                                        "요리11수량","요리12수량","요리13수량","요리14수량","요리15수량",
                                        "요리16수량","요리17수량","요리18수량","요리19수량","요리20수량",
                                        "요리21수량","요리22수량","요리23수량","요리24수량","요리25수량",
                                        "요리26수량","요리27수량","요리28수량","요리29수량","요리30수량",
                                        "요리31수량","요리32수량","요리33수량","요리34수량","요리35수량",
                                        "요리36수량","요리37수량","요리38수량","요리39수량","요리40수량",
                                        "요리41수량","요리42수량","요리43수량","요리44수량","요리45수량",
                                        "요리46수량","요리47수량","요리48수량"};
                                String 재료수량[]={
                                        "재료1수량","재료2단계","재료3수량","재료4수량","재료5수량",
                                        "재료6수량","재료7수량","재료8수량","재료9수량","재료10수량",
                                };

                                소지금=Long.parseLong(document.getData().get("소지금").toString());
                                은행잔고=Long.parseLong(document.getData().get("은행잔고").toString());
                                명성=Long.parseLong(document.getData().get("명성").toString());
                                레벨=Integer.parseInt(document.getData().get("레벨").toString());
                                솜=Integer.parseInt(document.getData().get("솜").toString());
                                경험치=Long.parseLong(document.getData().get("경험치").toString());
                                승점=Integer.parseInt(document.getData().get("승점").toString());
                                포장마차단계=Integer.parseInt(document.getData().get("포장마차단계").toString());
                                마법제조기단계=Integer.parseInt(document.getData().get("마법제조기단계").toString());
                                마케팅쿠폰=Integer.parseInt(document.getData().get("마케팅쿠폰").toString());
                                마법기계강화쿠폰=Integer.parseInt(document.getData().get("마법기계강화쿠폰").toString());
                                요리강화쿠폰=Integer.parseInt(document.getData().get("요리강화쿠폰").toString());
                                강화보조제=Integer.parseInt(document.getData().get("강화보조제").toString());
                                강화보호제=Integer.parseInt(document.getData().get("강화보호제").toString());
                                이벤트번호=Integer.parseInt(document.getData().get("이벤트번호").toString());
                                결투도전권=Integer.parseInt(document.getData().get("결투도전권").toString());
                                결투광고=Integer.parseInt(document.getData().get("결투광고").toString());
                                회차=Integer.parseInt(document.getData().get("회차").toString());
                                현재보상=Integer.parseInt(document.getData().get("현재보상").toString());
                                보상상태=Integer.parseInt(document.getData().get("보상상태").toString());
                                종합고유키=document.getData().get("종합고유키").toString();
                                for(int i=0;i<48;i++){
                                    요리[i][12]=Integer.parseInt(document.getData().get(음식단계[i]).toString());
                                    요리[i][0]=Integer.parseInt(document.getData().get(음식수량[i]).toString());

                                }

                                for(int i=0;i<22;i++){
                                    캐릭터[i][3]=Integer.parseInt(document.getData().get(캐릭터단계[i]).toString());
                                }
                                for(int i=0;i<10;i++){
                                    재료[i+1][0]=Integer.parseInt(document.getData().get(재료수량[i]).toString());
                                }
                                han.sendEmptyMessageDelayed(3,1000);

                            }
                        } else {

                        }
                    }
                });





    }
    void 저장하기(){
        String 음식단계[]={
                "요리1단계","요리2단계","요리3단계","요리4단계","요리5단계",
                "요리6단계","요리7단계","요리8단계","요리9단계","요리10단계",
                "요리11단계","요리12단계","요리13단계","요리14단계","요리15단계",
                "요리16단계","요리17단계","요리18단계","요리19단계","요리20단계",
                "요리21단계","요리22단계","요리23단계","요리24단계","요리25단계",
                "요리26단계","요리27단계","요리28단계","요리29단계","요리30단계",
                "요리31단계","요리32단계","요리33단계","요리34단계","요리35단계",
                "요리36단계","요리37단계","요리38단계","요리39단계","요리40단계",
                "요리41단계","요리42단계","요리43단계","요리44단계","요리45단계",
                "요리46단계","요리47단계","요리48단계"};
        String 음식수량[]={
                "요리1수량","요리2수량","요리3수량","요리4수량","요리5수량",
                "요리6수량","요리7수량","요리8수량","요리9수량","요리10수량",
                "요리11수량","요리12수량","요리13수량","요리14수량","요리15수량",
                "요리16수량","요리17수량","요리18수량","요리19수량","요리20수량",
                "요리21수량","요리22수량","요리23수량","요리24수량","요리25수량",
                "요리26수량","요리27수량","요리28수량","요리29수량","요리30수량",
                "요리31수량","요리32수량","요리33수량","요리34수량","요리35수량",
                "요리36수량","요리37수량","요리38수량","요리39수량","요리40수량",
                "요리41수량","요리42수량","요리43수량","요리44수량","요리45수량",
                "요리46수량","요리47수량","요리48수량"};
        String 재료수량[]={
                "재료1수량","재료2단계","재료3수량","재료4수량","재료5수량",
                "재료6수량","재료7수량","재료8수량","재료9수량","재료10수량",
               };
        String 캐릭터단계[]={
                "캐릭터1단계","캐릭터2단계","캐릭터3단계","캐릭터4단계","캐릭터5단계",
                "캐릭터6단계","캐릭터7단계","캐릭터8단계","캐릭터9단계","캐릭터10단계",
                "캐릭터11단계","캐릭터12단계","캐릭터13단계","캐릭터14단계","캐릭터15단계",
                "캐릭터16단계","캐릭터17단계","캐릭터18단계","캐릭터19단계","캐릭터20단계",
                "캐릭터21단계","캐릭터22단계"};
        prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
        SharedPreferences.Editor editor =  prefs.edit();
        editor.putLong("소지금",소지금 );
        editor.putLong("은행잔고",은행잔고 );
        editor.putLong("명성",명성 );
        editor.putInt("레벨",레벨 );
        editor.putInt("솜",솜 );
        editor.putLong("경험치",경험치 );
        editor.putInt("승점",승점 );
        editor.putInt("포장마차단계",포장마차단계 );
        editor.putInt("마법제조기단계",마법제조기단계);
        editor.putInt("마케팅쿠폰",마케팅쿠폰 );
        editor.putInt("마법기계강화쿠폰",마법기계강화쿠폰 );
        editor.putInt("요리강화쿠폰",요리강화쿠폰 );
        editor.putInt("강화보조제",강화보호제);
        editor.putInt("이벤트번호",이벤트번호);
        editor.putInt("현재보상",현재보상);
        editor.putInt("결투도전권",결투도전권);
        editor.putInt("결투광고",결투광고);
        editor.putInt("회차",회차);
        editor.putString("현재접속아이디",현재접속아이디);
        for(int i=0;i<48;i++){
            editor.putInt(음식단계[i],요리[i][12]);
            editor.putInt(음식수량[i],요리[i][0]);

        }
        for(int i=0;i<22;i++){
            editor.putLong(캐릭터단계[i],캐릭터[i][3] );
        }
        for(int i=0;i<10;i++){
            editor.putInt(재료수량[i],재료[i][0] );
        }

        editor.commit();
    }
    void 불러오기(){
        String 음식단계[]={
                "요리1단계","요리2단계","요리3단계","요리4단계","요리5단계",
                "요리6단계","요리7단계","요리8단계","요리9단계","요리10단계",
                "요리11단계","요리12단계","요리13단계","요리14단계","요리15단계",
                "요리16단계","요리17단계","요리18단계","요리19단계","요리20단계",
                "요리21단계","요리22단계","요리23단계","요리24단계","요리25단계",
                "요리26단계","요리27단계","요리28단계","요리29단계","요리30단계",
                "요리31단계","요리32단계","요리33단계","요리34단계","요리35단계",
                "요리36단계","요리37단계","요리38단계","요리39단계","요리40단계",
                "요리41단계","요리42단계","요리43단계","요리44단계","요리45단계",
                "요리46단계","요리47단계","요리48단계"};
        String 캐릭터단계[]={
                "캐릭터1단계","캐릭터2단계","캐릭터3단계","캐릭터4단계","캐릭터5단계",
                "캐릭터6단계","캐릭터7단계","캐릭터8단계","캐릭터9단계","캐릭터10단계",
                "캐릭터11단계","캐릭터12단계","캐릭터13단계","캐릭터14단계","캐릭터15단계",
                "캐릭터16단계","캐릭터17단계","캐릭터18단계","캐릭터19단계","캐릭터20단계",
                "캐릭터21단계","캐릭터22단계"};
        String 음식수량[]={
                "요리1수량","요리2수량","요리3수량","요리4수량","요리5수량",
                "요리6수량","요리7수량","요리8수량","요리9수량","요리10수량",
                "요리11수량","요리12수량","요리13수량","요리14수량","요리15수량",
                "요리16수량","요리17수량","요리18수량","요리19수량","요리20수량",
                "요리21수량","요리22수량","요리23수량","요리24수량","요리25수량",
                "요리26수량","요리27수량","요리28수량","요리29수량","요리30수량",
                "요리31수량","요리32수량","요리33수량","요리34수량","요리35수량",
                "요리36수량","요리37수량","요리38수량","요리39수량","요리40수량",
                "요리41수량","요리42수량","요리43수량","요리44수량","요리45수량",
                "요리46수량","요리47수량","요리48수량"};
        String 재료수량[]={
                "재료1수량","재료2단계","재료3수량","재료4수량","재료5수량",
                "재료6수량","재료7수량","재료8수량","재료9수량","재료10수량",
        };
        prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
        현재접속아이디=prefs.getString("현재접속아이디",현재접속아이디);
        소지금=prefs.getLong("소지금",소지금 );
        은행잔고=prefs.getLong("은행잔고",은행잔고 );
        명성=prefs.getLong("명성",명성 );
        레벨= prefs.getInt("레벨",레벨 );
        솜=prefs.getInt("솜",솜 );
        경험치=prefs.getLong("경험치",경험치 );
        승점=prefs.getInt("승점",승점 );
        포장마차단계=prefs.getInt("포장마차단계",포장마차단계 );
        마법제조기단계=prefs.getInt("마법제조기단계",마법제조기단계);
        마케팅쿠폰=prefs.getInt("마케팅쿠폰",마케팅쿠폰 );
        마법기계강화쿠폰=prefs.getInt("마법기계강화쿠폰",마법기계강화쿠폰 );
        요리강화쿠폰=prefs.getInt("요리강화쿠폰",요리강화쿠폰 );
        강화보조제=prefs.getInt("강화보조제",강화보호제);
        이벤트번호=prefs.getInt("이벤트번호",이벤트번호);
        결투도전권=prefs.getInt("결투도전권",결투도전권);
        결투광고=prefs.getInt("결투광고",결투광고);
        회차=prefs.getInt("회차",회차);




        for(int i=0;i<48;i++){
            요리[i][12]=prefs.getInt(음식단계[i],요리[i][12] );
            요리[i][0]=prefs.getInt(음식수량[i],요리[i][0] );

        }

        for(int i=0;i<22;i++){
            캐릭터[i][3]=prefs.getLong(캐릭터단계[i],캐릭터[i][3]);
        }
        for(int i=0;i<10;i++){
            재료[i][0]=prefs.getInt(재료수량[i],재료[i][0] );
        }

    }
    void 파이어베이스(){
        bp = new BillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlCznGYlRyChlr/1zSkHmuTtz/Q9PWhxRLm817sm6cCuZLTBneMSUKQ9NBoqD4HrME5jd8K6laq8GT4wIBcqU7eqrGzNe5cQ9wKyT00dnlOWkQ+bXbvVKHodjmm0Gt4VdIKX7mBv/ogiNXg2XdU2AurD5ZeIAKXToHuo62CQxfslMtTAi3BL/NyvBRzPOx0If2HP8xo12IpPNYW8iIuh6pEOGEwMSWP5YgAMWLAX/okDAjBKsvqxe9fuTZst1E27B6zVoRIfeGGCSEqlm0kDxX1LaWhay6qlNAzY/MjznsS3w9RdwuXocoL88TDiA/Batn0oOpAqKs9k346QgqW9lqwIDAQAB", this); // doesn't bind
        bp.initialize();
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                if(이벤트번호!=Integer.parseInt(snapshot.child("이벤트번호").getValue().toString())&&1!=Integer.parseInt(snapshot.child("수령가능여부").getValue().toString())){
                    현재보상=Integer.parseInt(snapshot.child("아이템번호").getValue().toString());
                    현재보상수량=Integer.parseInt(snapshot.child("아이템갯수").getValue().toString());
                    보상상태=0;
                    레이11전체업데이트();
                }else{
                    현재보상=1000;
                    현재보상수량=0;
                    레이11전체업데이트();
                }
                if(경제지수!=Integer.parseInt(snapshot.child("경제지수").getValue().toString())){
                    경제지수 = Integer.parseInt(snapshot.child("경제지수").getValue().toString());
                    레이1전체업데이트();
                    레이2전체업데이트();
                    경제지수텍스트.setText("경제지수 " + 경제지수); }
                경제지수 = Integer.parseInt(snapshot.child("경제지수").getValue().toString());
                날씨지수 = Integer.parseInt(snapshot.child("날씨지수").getValue().toString());
                결투장가능여부= Integer.parseInt(snapshot.child("결투장이용가능").getValue().toString());
                날씨지수텍스트.setText("날씨지수 " + 날씨지수);
                경험치이벤트 =Integer.parseInt(snapshot.child("경험치이벤트").getValue().toString());
                명성이벤트 =Integer.parseInt(snapshot.child("명성이벤트").getValue().toString());
                판매금이벤트 =Integer.parseInt(snapshot.child("판매금이벤트").getValue().toString());
                시스템텍스트2.setSelected(true);
             String 공지=snapshot.child("공지").getValue().toString();
                시스템텍스트2.setText(""+공지);
            }

        }
        @Override
        public void onCancelled(DatabaseError databaseError) {}});

    }
    void 광고라이징2(){
        if (rewardedAd.isLoaded()) {
            Activity activityContext = MainActivity.this;
            RewardedAdCallback adCallback = new RewardedAdCallback() {
                public void onRewardedAdOpened() {
                    // Ad opened.
                }
                @Override
                public void onRewardedAdClosed() {
                    rewardedAd = createAndLoadRewardedAd();

                }

                @Override
                public void onUserEarnedReward(@NonNull com.google.android.gms.ads.rewarded.RewardItem rewardItem) {
                    결투광고=0;

                }

                public void onRewardedAdFailedToShow(int errorCode) {
                    // Ad failed to display

                }
            };
            rewardedAd.show(activityContext, adCallback);
        } else {
            Log.d("TAG", "The rewarded ad wasn't loaded yet.");
        }
    }
    void 광고라이징(){

        if (rewardedAd.isLoaded()) {
            Activity activityContext = MainActivity.this;
            RewardedAdCallback adCallback = new RewardedAdCallback() {
                public void onRewardedAdOpened() {
                    // Ad opened.
                }
                @Override
                public void onRewardedAdClosed() {
                    rewardedAd = createAndLoadRewardedAd();
                    if(광고봄==1){
                        han.sendEmptyMessageDelayed(10000,60000);
                        구글버튼[0].setVisibility(INVISIBLE);

                        시스템텍스트애니("결투도전권 "+ "+5");
                        광고봄=0;
                    }
                }

                @Override
                public void onUserEarnedReward(@NonNull com.google.android.gms.ads.rewarded.RewardItem rewardItem) {

                    솜+=1;
                    결투도전권+=5;
                    저장하기();
                    레이10전체업데이트();
                    레이9전체업데이트();
                    광고봄=1;

                }

                public void onRewardedAdFailedToShow(int errorCode) {
                    // Ad failed to display

                }
            };
            rewardedAd.show(activityContext, adCallback);
        } else {
            시스템텍스트애니("지금은 광고를 볼수없어!!");
        }
    }
    void 광고셋팅(){
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        구글버튼[0].setVisibility(View.INVISIBLE);
        han.sendEmptyMessageDelayed(10000,10000);
        rewardedAd = new RewardedAd(this,
                "ca-app-pub-8598333709375599/3163800292");
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
                시스템텍스트애니("안녕하세요!!");
            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                // Ad failed to load.
                시스템텍스트애니("반갑습니다!!");
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);





    }
    void 초기화(){
        레벨=1;
        소지금=50000;
        명성=0;
        경험치=0;
        은행잔고=0;
        포장마차단계=0;
        초반설정();
    };
    void 인터넷연결확인(){
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        appNetwork receiver = new appNetwork(this);
        registerReceiver(receiver, filter);

    }







    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        시스템텍스트애니("구매해주셔서 감사합니다");
        솜+=인앱수량[레이8식바.getProgress()];
        bp.consumePurchase(인앱아이디[레이8식바.getProgress()]);
        bp.initialize();
        레이9전체업데이트();

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        시스템텍스트애니("구매를 취소하였습니다.");
    }

    @Override
    public void onBillingInitialized() {


    }
    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {


           super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime)
        {
            super.onBackPressed();
        }
        else
        {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "뒤로가기 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

void 튜토리얼(){
    prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
    처음접속=prefs.getInt("처음접속",처음접속);
        if(처음접속==0){
            튜토리얼레이.setVisibility(View.VISIBLE);
        }

}
void 튜토리얼이미지(int 이미지번호){

        튜토리얼이미지.setBackgroundResource(튜토이미지id[이미지번호]);
}
}


