package com.example.kimhk.aoi.Bluetooth;

public class RssiCal {
    int q=1,e=2,r=3,t=4;
    // q = 1  직진, e = 2 우회전, r = 3 좌회전, t = 4 정지
    public int RssiCalcul (double RssiBeacon1, double RssiBeacon2, double RssiBeacon3){
        if (RssiBeacon3 < RssiBeacon1 && RssiBeacon1 < RssiBeacon2) {
            return r;
        } else if (RssiBeacon1 < RssiBeacon3 && RssiBeacon3 < RssiBeacon2) {
            return q;
        } else if (RssiBeacon1 < RssiBeacon2 && RssiBeacon2 < RssiBeacon3) {
            return q;
        } else if (RssiBeacon2 < RssiBeacon1 && RssiBeacon1 < RssiBeacon3) {
            return e;
        } else if (RssiBeacon1 < 50 && RssiBeacon2 <50 && RssiBeacon3<50){
            return t;
        } else {
            return q;
        }
    }
}
