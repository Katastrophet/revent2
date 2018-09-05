package de.ur.mi.revent.Navigation;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import de.ur.mi.revent.Navigation.NavigationListener;

public class NavigationController implements LocationListener {

    private Context context;

    private NavigationListener navigationListener;
    private LocationManager locationManger;
    //private LatLng target;
    private LatLng position;
    private Location lastKnownLocation;
    private String bestProvider;

    public NavigationController(Context context) {
        this.context = context.getApplicationContext();
        locationManger = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        setBestProvider();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lastKnownLocation = locationManger.getLastKnownLocation(bestProvider);
        }
    }

    private void setBestProvider() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        bestProvider = locationManger.getBestProvider(criteria, true);
        if (bestProvider == null) {
            Log.e("setbestprovider", "no Provider set");
        }
    }

    public void setNavigationListener(NavigationListener navigationListener) {
        this.navigationListener = navigationListener;
        //Set Listener, make for Loop with setTarget + getDistance
    }

    public void startNavigation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManger.requestLocationUpdates(bestProvider, 1000, 1, this);
                Log.d("startnavigation", "called");
            }
        }
    }

    public void stopNavigation() {
        locationManger.removeUpdates(this);
    }

    public float[] getEstimatedDistanceForLocation(LatLng location) {
        float[] results = new float[1];
        if (lastKnownLocation == null) {
            lastKnownLocation = new Location(bestProvider);
        }
        double startLat = lastKnownLocation.getLatitude();
        double startLng = lastKnownLocation.getLongitude();
        double targetLat = location.latitude;
        double targetLng = location.longitude;
        Location.distanceBetween(startLat, startLng, targetLat, targetLng, results);
        return results;
    }

    public LatLng getLastKnownLocation(){
        double latitude = lastKnownLocation.getLatitude();
        double longitude = lastKnownLocation.getLongitude();
        LatLng lastKnownLocationInLatLng = new LatLng(latitude, longitude);
        return lastKnownLocationInLatLng;
    }

    @Override
    public void onLocationChanged(Location location) {
        lastKnownLocation = location;
        if (navigationListener == null) {
            return;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (navigationListener == null) {
            return;
        }
        if (status == LocationProvider.AVAILABLE) {
            navigationListener.onSignalFound();
        }
        if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
            navigationListener.onSignalLost();
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Hier keine Implementierung notwendig
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Hier keine Implementierung notwendig
    }
}