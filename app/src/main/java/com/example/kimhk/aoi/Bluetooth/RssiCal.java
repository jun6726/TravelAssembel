package com.example.kimhk.aoi.Bluetooth;

import android.util.Log;

import com.example.kimhk.aoi.R;

public class RssiCal {
    int q=1,e=2,r=3,t=4;
    // q = 1  직진, e = 2 우회전, r = 3 좌회전, t = 4 정지
    public int RssiCalcul (double RssiBeacon1, double RssiBeacon2, double RssiBeacon3) {
        if (RssiBeacon3 != 0.0 && RssiBeacon3 != 1.0E11 && RssiBeacon1 != 1.0E11 && RssiBeacon2 != 1.0E11) {
            Log.d("RssiCalcul", "\nRssi1 : " + RssiBeacon1 + "\nRssi2 : " + RssiBeacon2 + "\nRssi3 : " + RssiBeacon3);
            if (RssiBeacon3 < RssiBeacon1 && RssiBeacon1 < RssiBeacon2) {
                return r;
            } else if (RssiBeacon1 <= RssiBeacon3 && RssiBeacon3 < RssiBeacon2) {
                return q;
            } else if (RssiBeacon1 <= RssiBeacon2 && RssiBeacon2 < RssiBeacon3) {
                return q;
            } else if (RssiBeacon2 < RssiBeacon1 && RssiBeacon1 < RssiBeacon3) {
                return e;
            }
//            else if (R)
        }
        return q;
    }
}
