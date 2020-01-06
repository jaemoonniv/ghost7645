package com.moozzi.pochaagain;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class register extends AppCompatActivity {
    static SharedPreferences prefs;
    DocumentSnapshot 테스트도큐먼트;
    FirebaseFirestore 유저정보;
    CollectionReference 유저;
    String 아이디, 패스워드, 패스워드확인, 닉네임, 종합고유키 = "";
    FrameLayout 회원가입뷰,로그인뷰,레지스터뷰;
    Button 가입버튼, 나가기버튼,로그인,회원가입이동;
    TextView 안내텍스트,안내텍스트2;
    EditText 아이디텍스트, 패스워드텍스트, 패스워드확인텍스트, 닉네임텍스트,로그인아이디,로그인비밀번호;
    String 현재접속아이디="";
    int 가입수=0,현재버전=0;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    Handler han;
    public void onResume(){
        super.onResume();
        크기조정();
        han.sendEmptyMessageDelayed(0,1000);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //상태바없애기
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility(); //소프트바없애기
        int newUiOptions = uiOptions;
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);

        가입버튼 = findViewById(R.id.아이디생성);
        나가기버튼 = findViewById(R.id.돌아가기);
        아이디텍스트 = findViewById(R.id.이메일);
        패스워드텍스트 = findViewById(R.id.패스워드);
        패스워드확인텍스트 = findViewById(R.id.패스워드확인);

        안내텍스트 = findViewById(R.id.안내텍스트);
        안내텍스트2 = findViewById(R.id.안내텍스트2);
        레지스터뷰 =findViewById(R.id.레지스터뷰);
        회원가입뷰 =findViewById(R.id.회원가입레이);
        로그인뷰 =findViewById(R.id.로그인레이);
        회원가입이동 =findViewById(R.id.회원가입이동);
        로그인아이디 =findViewById(R.id.로그인아이디);
        로그인비밀번호 =findViewById(R.id.로그인비밀번호);
        로그인 =findViewById(R.id.로그인);
        유저정보 = FirebaseFirestore.getInstance();
        유저 = (CollectionReference) 유저정보.collection("userid");
        아이디텍스트.setFilters(new InputFilter[]{영어필터});


        prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
        가입수=prefs.getInt("가입수",가입수 );
        버전확인();
        버튼리스너();
        크기조정();
    }
    void 크기조정(){


    han = new Handler(){
            public void handleMessage(Message msg) {
                int x=레지스터뷰.getWidth();
                int y=레지스터뷰.getHeight();
                나가기버튼.getLayoutParams().width=(int)(y*0.06);
                나가기버튼.getLayoutParams().height=(int)(y*0.06);
                나가기버튼.requestLayout();
            }
        };
    }

    void 버튼리스너() {

        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (view.getId()) {
                    case R.id.아이디생성:
                        if(가입수<3){
                            패스워드확인();
                        }else{
                            안내텍스트.setText("휴대전화 1회선당"+'\n'+"3개까지만 등록가능합니다."+'\n'+"현재 "+가입수+" 개");
                        }

                        break;
                    case R.id.돌아가기:
                        로그인뷰.setVisibility(View.VISIBLE);
                        회원가입뷰.setVisibility(View.GONE);

                        break;
                    case R.id.회원가입이동:
                            로그인뷰.setVisibility(View.GONE);
                            회원가입뷰.setVisibility(View.VISIBLE);

                        break;
                    case R.id.로그인:


                            if(로그인아이디.length()>0){
                                테스트();
                                //로그인();
                            }else{
                                안내텍스트2.setText("아이디를 입력해주세요");
                            }


                        break;

                }

            }
        };
        회원가입이동.setOnClickListener(onClickListener);
        가입버튼.setOnClickListener(onClickListener);
        나가기버튼.setOnClickListener(onClickListener);
        로그인.setOnClickListener(onClickListener);
    }
    void 테스트(){

        DocumentReference docRef =유저.document(로그인아이디.getText().toString());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {


                        String 아이디확인 = document.getData().get("아이디").toString();
                        String 비밀번호확인 = document.getData().get("비밀번호").toString();
                        int 접속중확인 = Integer.parseInt(document.getData().get("접속중").toString());

                        if(!아이디확인.equals(로그인아이디.getText().toString())){
                            안내텍스트2.setText("아이디를 찾을 수 없습니다.");
                        }else if(!비밀번호확인.equals(로그인비밀번호.getText().toString())){
                            안내텍스트2.setText("비밀번호가 올바르지 않습니다.");
                        }else if(접속중확인!=0){
                            안내텍스트2.setText("현재접속중입니다");
                        }else if(아이디확인.equals(로그인아이디.getText().toString())&&비밀번호확인.equals(로그인비밀번호.getText().toString())&&접속중확인==0){
                            안내텍스트2.setText(아이디확인+"님 환영합니다.");
                            현재접속아이디=로그인아이디.getText().toString();
                            prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
                            SharedPreferences.Editor editor =  prefs.edit();
                            editor.putString("현재접속아이디",현재접속아이디 );
                            editor.commit();

                            Map<String, Object> data = new HashMap<>();
                            data.put("접속중", 1);
                            유저정보.collection("userid").document(현재접속아이디).update(data);

                            Intent intent = new Intent(register.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }



                    } else {
                        안내텍스트2.setText("아이디를 찾을 수 없습니다.");
                    }
                } else {


                }
            }
        });



    }

    void 로그인(){
        DocumentReference docRef =유저.document(로그인아이디.getText().toString());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.getData()==null){
                        안내텍스트2.setText("아이디를 찾을 수 없습니다.");
                    }else{
                        if(document.getData().get("비밀번호").toString().equals(로그인비밀번호.getText().toString())){

                            if(Integer.parseInt(document.getData().get("접속중").toString())==0){
                                DocumentSnapshot a = document;

                                현재접속아이디=a.getId();
                                안내텍스트2.setText(현재접속아이디+"님 환영합니다.");
                                //현재접속아이디 인텐트

                                //접속상황업데이트

                                //클래스 0인텐트
//                                Intent intent = new Intent(register.this,MainActivity.class);
//                                startActivity(intent);

                  ;
                            }else{
                                안내텍스트2.setText("현재 접속중인 아이디입니다.");
                            }

                        }else{
                            안내텍스트2.setText("비밀번호틀림");
                        }
                    }

                    if (document.exists()) {

                    } else {

                    }
                } else {


                }
            }
        });
    }



    void 패스워드확인(){
        패스워드=패스워드텍스트.getText().toString();
        패스워드확인=패스워드확인텍스트.getText().toString();

        if(패스워드텍스트.length()>=8&&패스워드텍스트.length()<=12){
            if(패스워드.equals(패스워드확인)){
                이메일확인();
            }else{
                안내텍스트.setText("패스워드가 일치하지 않습니다.");
            }

        }else{
            안내텍스트.setText("패스워드는"+'\n'+"8~12 글자로"+'\n'+"설정해주세요.");
        }

    }





    void 이메일확인(){

        if(아이디텍스트.length()>=2&&아이디텍스트.length()<=12){
            아이디=아이디텍스트.getText().toString();
            고유키생성();
        }else{
            안내텍스트.setText("아이디는"+'\n'+"6~12글자 이상으로"+'\n'+"설정해주세요.");
        }


    }


    void 고유키생성(){
        String 고유키[]=new String[10];
        String 고유키값[]={"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","0","1","2","3","4","5","6","7","8","9"};
        for(int i=0;i<10;i++){
        int 랜덤=(int)(Math.random()*36);
        고유키[i]=고유키값[랜덤];
        }
        종합고유키=고유키[0]+고유키[1]+고유키[2]+고유키[3]+고유키[4]+고유키[5]+고유키[6]+고유키[7]+고유키[8]+고유키[9];





        회원가입();
    }
    void 정보추가(){
        가입수++;
        prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
        SharedPreferences.Editor editor =  prefs.edit();
        editor.putInt("가입수",가입수 );
        editor.commit();

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

        Map<String, Object> data1 = new HashMap<>();
        data1.put("아이디", 아이디);

        data1.put("비밀번호", 패스워드);
        data1.put("종합고유키", 종합고유키);
        data1.put("레벨", 1);
        data1.put("소지금", 50000);
        data1.put("명성", 1000);
        data1.put("경험치", 0);
        data1.put("승점", 1000);
        data1.put("솜", 5);
        data1.put("포장마차단계", 0);
        data1.put("마법제조기단계", 0);
        for(int i=0;i<48;i++){
            data1.put(음식단계[i], 0);
            data1.put(음식수량[i], 0);
        }
        for(int i=0;i<22;i++){
            data1.put(캐릭터단계[i], 0);
        }
        for(int i=0;i<10;i++){
            data1.put(재료수량[i], 0);
        }
        data1.put("이벤트번호", -1);
        data1.put("현재보상", -1);
        data1.put("보상상태", 0);
        data1.put("결투도전권", 10);
        data1.put("결투광고", 0);
        data1.put("회차", 0);
        data1.put("마케팅쿠폰", 0);
        data1.put("마법기계강화쿠폰", 0);
        data1.put("요리강화쿠폰", 0);
        data1.put("강화보조제", 0);
        data1.put("강화보호제", 0);
        data1.put("은행잔고", 0);
        data1.put("접속중", 0);
        유저.document(아이디).set(data1);
        안내텍스트.setText(아이디+"님 환영합니다!!"+'\n'+"회원가입이 완료되었습니다.");
    }
    void 회원가입(){
        DocumentReference docRef =유저.document(아이디텍스트.getText().toString());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.getData()==null){
                        안내텍스트.setText("가입성공");
                        정보추가();

                    }else{
                        안내텍스트.setText("이미 사용중인 아이디입니다.");
                    }

                    if (document.exists()) {

                    } else {

                    }
                } else {


                }
            }
        });


    }

    protected InputFilter 영어필터= new InputFilter() {

        public CharSequence filter(CharSequence source, int start, int end,

                                   Spanned dest, int dstart, int dend) {



            Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");

            if (!ps.matcher(source).matches()) {

                return "";

            }

            return null;

        }

    };
    public InputFilter 한글필터 = new InputFilter() {

        public CharSequence filter(CharSequence source, int start, int end,

                                   Spanned dest, int dstart, int dend) {



            Pattern ps = Pattern.compile("^[ㄱ-ㅎ가-힣갸-힇]+$");
            if (!ps.matcher(source).matches()) {

                return "";

            }

            return null;

        }

    };
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

    void 버전확인(){
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    if(현재버전<Integer.parseInt(snapshot.child("현재버전").getValue().toString())){
                        AlertDialog.Builder builder = new AlertDialog.Builder(register.this);
                        builder.setMessage("열심히 만들어서 새로운 버전이나왔어요.");
                        builder.setCancelable(false)
                                .setPositiveButton("이동", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.moozzi.pochaagain")); startActivity(intent);
                                        finish();

                                    }
                                });

                        AlertDialog alert = builder.create();
                        alert.setTitle("버전확인");
                        alert.show();

                    }

                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}});


    }


}
