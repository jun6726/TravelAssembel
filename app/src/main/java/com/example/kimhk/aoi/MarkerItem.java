package com.example.kimhk.aoi;

public class MarkerItem {
    double lat;
    double lon;
    int markernumber;

    public MarkerItem(double lat, double lon, int markernumber) {
        this.lat = lat; this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public int getmarkernumber() {
        return markernumber;
    }

    public void setmarkernumber(int markernumber) {
        this.markernumber = markernumber;
    }

}
