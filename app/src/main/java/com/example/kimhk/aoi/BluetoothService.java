package com.example.kimhk.aoi;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

public class BluetoothService {
    private static final String TAG = "BluetoothService";
    private BluetoothAdapter btAdapter;
    private Activity mActivity;
    private Handler mHandler;

    // 생성자
    public BluetoothService(Activity activity, Handler handler) {
        mActivity = activity;
        mHandler = handler;

        // 블루투스 어댑터 얻기
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean getDeviceState() {
        Toast.makeText(mActivity, "Check the Bluetooth support", Toast.LENGTH_SHORT).show();

        if (btAdapter == null) {
            Toast.makeText(mActivity, "Bluetooth is not availablle", Toast.LENGTH_SHORT).show();

            return false;
        } else {
            Toast.makeText(mActivity, "Bluetooth is availablle", Toast.LENGTH_SHORT).show();

            return true;
        }
    }

    public void enableBluetooth() {
        Toast.makeText(mActivity, "Check the enabled Bluetooth", Toast.LENGTH_SHORT).show();

        if (btAdapter.isEnabled()) {
            // 기기의 블루투스 상태가 On인 경우
            Toast.makeText(mActivity, "Bluetooth Enable Now", Toast.LENGTH_SHORT).show();
        } else {
            // 기기의 블루투스 상태가 Off인 경우
            Toast.makeText(mActivity, "Bluetooth Enable Request", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            mActivity.startActivityForResult(i, REQUEST_ENABLE_BT);
        }
    }
}
