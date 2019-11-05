package com.example.kimhk.aoi.Bluetooth;

import android.util.Log;

import com.example.kimhk.aoi.Login;
import com.example.kimhk.aoi.R;

public class RssiCal {
    int q=1,e=2,r=3,t=4;
    // q = 1  직진, e = 2 우회전, r = 3 좌회전, t = 4 정지
    public int RssiCalcul (double RssiBeacon1, double RssiBeacon2, double RssiBeacon3) {
        if (RssiBeacon1 != 0.0 && RssiBeacon2 != 0.0 && RssiBeacon3 != 0.0) {
            Log.d("RssiCalcul", "\t\nRssi1 : " + RssiBeacon1 + "\nRssi2 : " + RssiBeacon2 + "\nRssi3 : " + RssiBeacon3);
            if (RssiBeacon3 < RssiBeacon1 && RssiBeacon1 < RssiBeacon2) {
                return r;
            } else if (RssiBeacon1 <= RssiBeacon3 && RssiBeacon3 < RssiBeacon2) {
                return q;
            } else if (RssiBeacon1 <= RssiBeacon2 && RssiBeacon2 < RssiBeacon3) {
                return q;
            } else if (RssiBeacon2 < RssiBeacon1 && RssiBeacon1 < RssiBeacon3) {
                return e;
            }
        }
        return q;
    }
}
