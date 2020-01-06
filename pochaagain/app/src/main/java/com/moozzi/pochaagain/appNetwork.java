package com.moozzi.pochaagain;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Button;

import static com.moozzi.pochaagain.MainActivity.레이14작동버튼;

public class appNetwork extends BroadcastReceiver {
    private Activity activity;
    Context context;
    MainActivity ms= new MainActivity();

    public appNetwork() {
        super();
    }
    public appNetwork(Activity activity) {
        this.activity = activity;
    }
    void 버튼리스너(){
        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (view.getId()) {

                    case R.id.레이13작동버튼1:
                        ms.finish();

                        break ;

                }
            }
        } ;


        레이14작동버튼.setOnClickListener(onClickListener);

    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action= intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            try {
                ConnectivityManager connectivityManager =
                        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
                NetworkInfo _wifi_network =
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if(_wifi_network != null) {


                    if(_wifi_network != null && activeNetInfo != null){

                        MainActivity.인터넷연결여부=0;
                        MainActivity.레이[13].setVisibility(View.GONE);


                    }else{
                        MainActivity.인터넷연결여부=1;
                        MainActivity.레이[13].setVisibility(View.VISIBLE);
                        MainActivity.레이14정보텍스트.setText("인터넷 연결을 확인해 주세요");
                        레이14작동버튼.setText("종료");


                    }
                }else{

                }
            } catch (Exception e) {

            }
        }
    }}