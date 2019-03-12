package com.example.kimhk.aoi;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.request.LogoutRequest;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by kimhk on 2019-01-19.
 */

public class Login extends AppCompatActivity {
    private SessionCallback callback;
    public TextView user_nickname, user_email;
    CircleImageView user_img;
    LinearLayout success_layout;
    Button logout_btn,mypage;
    LoginButton loginButton;
    public String nickname;
    public long Id;

    public static Login mrequestLogout; // 메인에서 로그아웃 함수 불러주기 위한 변수
    AQuery aQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mrequestLogout = this; // 메인에서 로그아웃 함수 불러주기 위한 변수
        mypage = (Button) findViewById(R.id.mypage);
        aQuery = new AQuery(this);
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        // 카카오톡 로그인 버튼
        loginButton = (LoginButton) findViewById(R.id.com_kakao_login);
        loginButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!isConnected()) {
                        Toast.makeText(com.example.kimhk.aoi.Login.this, "인터넷 연결을 확인해주세요", Toast.LENGTH_SHORT).show();
                    }
                }

                if (isConnected()) {
                    return false;
                } else {
                    return true;
                }
            }
        });

        // 로그인 성공 시 사용할 뷰
        success_layout = (LinearLayout) findViewById(R.id.success_layout);
        user_nickname = (TextView) findViewById(R.id.user_nickname);
        user_img = (CircleImageView) findViewById(R.id.user_img);
        user_email = (TextView) findViewById(R.id.user_email);
        logout_btn = (Button) findViewById(R.id.logout);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Session.getCurrentSession().isOpened()) {

                    username_send username_send = new username_send();
                    username_send.execute("http://jun6726.cafe24.com/php_folder/username_test.php",nickname, String.valueOf(Id));
                    Log.d("닉네임",nickname);
                    requestLogout();
                }
            }
        });


        if (Session.getCurrentSession().isOpened()) {
            requestMe();
        } else {
            success_layout.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
            mypage.setVisibility(View.INVISIBLE);
        }

    }


    //인터넷 연결상태 확인
    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            //access token을 성공적으로 발급 받아 valid access token을 가지고 있는 상태. 일반적으로 로그인 후의 다음 activity로 이동한다.
            if (Session.getCurrentSession().isOpened()) { // 한 번더 세션을 체크해주었습니다.
                requestMe();
            }
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Logger.e(exception);
            }
        }
    }

    public void requestLogout() {
        success_layout.setVisibility(View.GONE);
        loginButton.setVisibility(View.VISIBLE);
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(com.example.kimhk.aoi.Login.this, "로그아웃 성공", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void requestMe() {
        success_layout.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.GONE);

        mypage.setVisibility(View.VISIBLE);
        mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Mypage.class);
                startActivityForResult(intent, 1000);
            }
        });

        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Log.e("onFailure", errorResult + "");
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.e("onSessionClosed", errorResult + "");
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                Log.e("onSuccess", userProfile.toString());
                user_nickname.setText(userProfile.getNickname());
                user_email.setText(userProfile.getEmail());
                aQuery.id(user_img).image(userProfile.getThumbnailImagePath()); // <- 프로필 작은 이미지 , userProfile.getProfileImagePath() <- 큰 이미지
                Log.d("온 석세스", userProfile.getNickname());


                Id = userProfile.getId();
               nickname = userProfile.getNickname();

            }

            @Override
            public void onNotSignedUp() {
                Log.e("onNotSignedUp", "onNotSignedUp");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    public class username_send extends AsyncTask<String, Void, String> {

        @Override
        public String doInBackground(String... params) {

            String serverURL = (String)params[0];
            String username = (String)params[1];
            String userid = (String)params[2];
            String paramerter = "username=" + username + "&userid=" + userid;

            try{
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(paramerter.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                InputStream inputStream;
                inputStream = httpURLConnection.getInputStream();

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();

                return sb.toString();


            }catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
