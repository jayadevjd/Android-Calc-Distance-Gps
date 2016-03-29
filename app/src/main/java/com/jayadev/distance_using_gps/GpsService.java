package com.jayadev.distance_using_gps;

/**
 * Created by Jayadev on 20/11/15.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

public class GpsService extends Service {
    public static final String BROADCAST_ACTION = "com.jayadev.distance_using_gps.updateUI";
    public LocationManager locationManager;
    public MyLocationListener listener;
    static Double lat1 = null;
    static Double lon1 = null;
    static Double lat2 = null;
    static Double lon2 = null;
    static Double distance = 0.0;
    static int status = 0;
    Intent intent;
    private final Handler UIhandler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public void onStart(Intent intent, int startId) {

        UIhandler.removeCallbacks(sendUpdatesToUI);
        UIhandler.postDelayed(sendUpdatesToUI, 0);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, listener);
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            intent.putExtra("distance", distance+"");
            sendBroadcast(intent);
            UIhandler.postDelayed(this, 0);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        UIhandler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        locationManager.removeUpdates(listener);
    }

    public class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (status == 0) {
                lat1 = location.getLatitude();
                lon1 = location.getLongitude();
            } else if ((status % 2) != 0) {
                lat2 = location.getLatitude();
                lon2 = location.getLongitude();
                distance += distanceBetweenTwoPoint(lat1, lon1, lat2, lon2);
            } else if ((status % 2) == 0) {
                lat1 = location.getLatitude();
                lon1 = location.getLongitude();
                distance += distanceBetweenTwoPoint(lat2, lon2, lat1, lon1);
            }
            status++;
            UIhandler.postDelayed(sendUpdatesToUI, 0);
        }

        double distanceBetweenTwoPoint(double srcLat, double srcLng, double desLat, double desLng) {
            double earthRadius = 3958.75;
            double dLat = Math.toRadians(desLat - srcLat);
            double dLng = Math.toRadians(desLng - srcLng);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                    + Math.cos(Math.toRadians(srcLat))
                    * Math.cos(Math.toRadians(desLat)) * Math.sin(dLng / 2)
                    * Math.sin(dLng / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double dist = earthRadius * c;

            double meterConversion = 1609;

            return (int) (dist * meterConversion);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }


}