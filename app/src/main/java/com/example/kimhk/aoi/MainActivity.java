package com.example.kimhk.aoi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.auth.Session;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    int mPariedDeviceCount = 1; // 블루투스 연결 확인 변수
    Button btn_Nologin;
    public static Intent Mypage_intent, Login_intent; //마이페이지 인텐트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //로그인없이 사용 - > 마이페이지
        btn_Nologin = (Button) findViewById(R.id.btn_Nologin);
        btn_Nologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mypage_intent = new Intent(getApplicationContext(), Mypage.class);
                startActivityForResult(Mypage_intent, 1000);
            }
        });
    }

    //메뉴바 인플레이터
     @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    //메뉴바 옵션 선택 함수
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_login:
                Login_intent = new Intent(getApplicationContext(), com.example.kimhk.aoi.Login.class);
                startActivityForResult(Login_intent, 1000);
                break;
                //캐리어 연결
            case R.id.menu_carrer_connect:
                checkBlueTooth(); // 블루투스 체크 함수호출
                break;
            //설정
            case R.id.menu_settings:
                Toast.makeText(this, "설정이 아직 없습니다.", Toast.LENGTH_SHORT).show();
                break;
            //로그아웃
            case R.id.menu_logout:
                ((Login)Login.mrequestLogout).requestLogout(); // 로그인클래스에서 로그아웃 함수 불러주기 위한 코드
                break;
        }
        return true;
    }
    int REQUEST_ENABLE_BT = 1; //블루투스 연결 상태 사용자 정의 상수 0이상
    //블루투스 어댑터 얻기
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    //블루투스 활성화
    Set<BluetoothDevice> pairDevices = mBluetoothAdapter.getBondedDevices();

    // 블루투스 장치의 이름이 주어졌을때 해당 블루투스 장치 객체를 페어링 된 장치 목록에서 찾아내는 코드
    BluetoothDevice getDeviceFromBondedList(String name)
    {
        BluetoothDevice selectedDevice = null;
        // BluetoothDevice : 페어링 된 기기 목록을 얻어옴
        for(BluetoothDevice device : pairDevices)
        {
            if (name.equals(device.getName())){
                selectedDevice = device;
                break;
            }
        }
        return selectedDevice;
    }


    // 블루투스 지원하며 활성 상태인 경우.
    void selectDevice() {
        // 블루투스 디바이스는 연결해서 사용하기 전에 먼저 페어링 되어야만 한다
        // getBondedDevices() : 페어링된 장치 목록 얻어오는 함수.
        pairDevices = mBluetoothAdapter.getBondedDevices();
        mPariedDeviceCount = pairDevices.size();

        if(mPariedDeviceCount == 0 ) { // 페어링된 장치가 없는 경우.
            Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
        }
        // 페어링된 장치가 있는 경우.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("블루투스 장치 선택");

        // 각 디바이스는 이름과(서로 다른) 주소를 가진다. 페어링 된 디바이스들을 표시한다.
        List<String> listItems = new ArrayList<String>();
        for(BluetoothDevice device : pairDevices) {
            // device.getName() : 단말기의 Bluetooth Adapter 이름을 반환.
            listItems.add(device.getName());
        }
        listItems.add("취소");  // 취소 항목 추가.


        // CharSequence : 변경 가능한 문자열.
        // toArray : List형태로 넘어온것 배열로 바꿔서 처리하기 위한 toArray() 함수.
        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
        // toArray 함수를 이용해서 size만큼 배열이 생성 되었다.
        listItems.toArray(new CharSequence[listItems.size()]);

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                // TODO Auto-generated method stub
                if(item == mPariedDeviceCount) { // 연결할 장치를 선택하지 않고 '취소' 를 누른 경우.
                    Toast.makeText(getApplicationContext(), "연결할 장치를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
                }
                else { // 연결할 장치를 선택한 경우, 선택한 장치와 연결을 시도함.
//                    connectToSelectedDevice(items[item].toString());
                }
            }
        });
        builder.setCancelable(false);  // 뒤로 가기 버튼 사용 금지.
        AlertDialog alert = builder.create();
        alert.show();
    }

    //블루투스 연결상태 확인 함수
    private void checkBlueTooth() {
        {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            //장치가 블루투스를 지원하지 않는 경우.
            if (mBluetoothAdapter == null) {
                Toast.makeText(getApplicationContext(), "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_SHORT).show();
            }
            // 장치가 블루투스를 지원하는 경우.
            else {
                //블루투스를 지원하지만 비활성인 상태
                //블루투스를 활성 상태로 바꾸기 위해 사용자 동의 요청
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                } else { //페어링 된 장치가 없는 경우
                    Toast.makeText(getApplicationContext(), "장치가 없습니다.", Toast.LENGTH_SHORT).show();
                    selectDevice();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    //블루투스가 활성 상태로 변경
                    Toast.makeText(this, "블루투스가 활성화 되었습니다.", Toast.LENGTH_SHORT).show();
                    selectDevice();
                }
                else if (resultCode == RESULT_CANCELED) {
                    //블루투스가 비활성 상태임
                    Toast.makeText(this, "블루투스가 비활성 되었습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}