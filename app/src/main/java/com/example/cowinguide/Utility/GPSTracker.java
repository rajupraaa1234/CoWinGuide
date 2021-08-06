package com.example.cowinguide.Utility;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.GeoApiContext;
import com.google.maps.TimeZoneApi;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


/**
 * The type Gps tracker.
 */
public class GPSTracker extends Service implements LocationListener {
    private static final String TAG = "GPSTracker";
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    private final Context mContext;
    /**
     * The Location manager.
     */
// Declaring a Location Manager
    protected LocationManager locationManager;
    /**
     * The Is gps enabled.
     */
// flag for GPS status
    boolean isGPSEnabled = false;
    /**
     * The Is network enabled.
     */
// flag for network status
    boolean isNetworkEnabled = false;
    /**
     * The Can get location.
     */
// flag for GPS status
    boolean canGetLocation = false;
    /**
     * The Location.
     */
    Location location; // location
    /**
     * The Latitude.
     */
    double latitude = 0.0; // latitude
    /**
     * The Longitude.
     */
    double longitude = 0.0; // longitude

    String strAdd = "", strAddToReddem = "";

    /**
     * Instantiates a new Gps tracker.
     *
     * @param context the context
     */
    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    /**
     * Gets location.
     *
     * @return the location
     */
    @SuppressLint("MissingPermission")
    public Location getLocation() {
        try {

            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
//                if (MarshMallowPermission.checkPermissionLocation(mContext)) {
                if (checkPermissionLocation(mContext)) {
                    // First get location from Network Provider
                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("Network", "Network");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                    // if GPS Enabled get lat/long using GPS Services
                    if (isGPSEnabled) {
                        if (location == null) {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.d("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                    }
                } else {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Gets latitude.
     *
     * @return the latitude
     */
    public double getLatitude() {
        getLocation();
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Gets longitude.
     *
     * @return the longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        // return longitude
        return longitude;
    }

    private boolean checkPermissionLocation(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int ACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
            return ACCESS_FINE_LOCATION == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    /**
     * Can get location boolean.
     *
     * @return the boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Show settings alert.
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            this.location = location;
//            System.out.println("location======>"+location);

          /*  latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title(“My Position”));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));*/

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    public String getCurrentLocationName() {
        LocationManager lm = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //showLocationServiceDialog();
        } else {
            latitude = getLatitude();
            longitude = getLongitude();
            if (latitude != 0.0 && latitude != 0) {
                Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses != null && addresses.size() > 0) {
                    Address returnedAddress = addresses.get(0);
                    StringBuilder strReturnedAddress = new StringBuilder("");
                    String areaName = addresses.get(0).getSubLocality();
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();

                    System.out.println("===>" + returnedAddress.getAddressLine(0));
                    for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(" ");
                        System.out.println(i + "CityName===>" + returnedAddress.getAddressLine(i) + "\n");
                    }

                    try {

                        if (areaName != null && !(areaName.equals(""))) {
                            strAdd = "" + areaName;
                        } else if (city != null && !(city.equals(""))) {
                            strAdd = "" + city;
                        } else if (state != null && !(state.equals(""))) {
                            strAdd = "" + state;
                        } else {
                            strAdd = "" + country;
                        }

                        strAdd = (strAdd.equals("") ? address : strAdd);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    strAddToReddem = strReturnedAddress.toString();
                    strAddToReddem = (strAddToReddem.equals("") ? address : strAddToReddem);
                    System.out.println("strAdd====>" + strAdd + "====>" + strAddToReddem);
                } else {
                    strAdd = "";
                }
            }
        }


        return strAdd;

    }


    public String getCityName(LatLng latLng, Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String cityName;
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && addresses.size() > 0 && addresses.get(0).getSubAdminArea() != null) {
            cityName = addresses.get(0).getSubAdminArea();
        } else {
            cityName = "";
        }
        Log.e(TAG + " CITY NAME ==", cityName);
        return cityName;

    }

    public static String getTimezone(GeoApiContext context, com.google.maps.model.LatLng latLng) throws Exception {

        // .geometry.location returns an LatLng object coressponding to your address;
//getTimeZone returns the timezone and it will be saved as a TimeZone object
        TimeZone timeZone = TimeZoneApi.getTimeZone(context, latLng).await();
// returns the displayname of the timezone
        double zone = (timeZone.getRawOffset() + timeZone.getDSTSavings()) / 3600000.0;

        return zone + "";

    }

}



