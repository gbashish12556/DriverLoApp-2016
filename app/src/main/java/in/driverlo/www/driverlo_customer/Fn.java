package in.driverlo.www.driverlo_customer;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;

import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 * Created by Ashish on 10/20/2016.
 */
public class Fn {

    protected static void logD(String key, String value) {
        Log.d(key, value);
    }

    protected static void logE(String key, String value) {
        Log.e(key, value);
    }

    protected static void logV(String key, String value) {
        Log.v(key, value);
    }

    protected static void logW(String key, String value) {
        Log.w(key, value);
    }

    protected static void SystemPrintLn(Object message) {
        System.out.println(message);
    }

    protected static void Toast(Context context, String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

    protected static void ToastShort(Context context, String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

    protected static void putPreference(Context context, String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, String.valueOf(value)).commit();
    }

    protected static String getPreference(Context context, String key) {
        String value = PreferenceManager.getDefaultSharedPreferences(context).getString(key, "defaultStringIfNothingFound");
        return value;
    }

    protected static <T> void addToRequestQue(RequestQueue requestQueue, Request<T> request, Context ctx) {
        if (requestQueue != null) {
            requestQueue.add(request);
        } else {
            requestQueue = Volley.newRequestQueue(ctx);
            requestQueue.add(request);
        }
    }

    protected static void cancelAllRequest(RequestQueue mRequestQueue, String tag) {
//        mRequestQueue.cancelAll(TAG);
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    protected static void stopAllVolley(RequestQueue mRequestQueue) {
        if (mRequestQueue != null) {
            mRequestQueue.stop();
        }
    }

    protected static void startAllVolley(RequestQueue mRequestQueue) {
        if (mRequestQueue != null) {
            mRequestQueue.start();
        }
    }

    protected static void showNetworkSettingDialog(final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        //Setting Dialog Title
        alertDialog.setTitle(R.string.NetworkAlertDialogTitle);
        //Setting Dialog Message
        alertDialog.setMessage(R.string.NetworkAlertDialogMessage);
        //On Pressing Setting button
        alertDialog.setPositiveButton(R.string.action_settings, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                context.startActivity(intent);
                dialog.dismiss();
            }
        });

        //On pressing cancel button
        alertDialog.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
//                ((MainActivity)context).moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
                dialog.dismiss();
            }
        });
        alertDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    //alertDialog.dismiss();
//                    ((MainActivity)context).moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
                return true;
            }
        });
        alertDialog.show();
    }

    protected static void showDialog(Context context, String title, String message) {

        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(context);
        alertDialog2.setTitle(title);
        alertDialog2.setMessage(message);
        alertDialog2.setCancelable(false);
        alertDialog2.setNegativeButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog2.show();
    }

    protected static void showProgressDialog(String message, Context ctx) {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(ctx);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                progressDialog.dismiss();
            }
        }, Constants.Config.PROGRESSBAR_DELAY);  // 3000 milliseconds
    }

    protected static void showProgressDialogLong(String message, Context ctx) {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(ctx);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    protected static String getDateTimeNow() {
        TimeZone tz = TimeZone.getTimeZone("GMT+05:30");
        Calendar c = Calendar.getInstance(tz);
        Date date = c.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = df.format(date);
        return strDate;
    }

    protected static long getDateTimeNowMillis() {
        TimeZone tz = TimeZone.getTimeZone("GMT+05:30");
        Calendar c = Calendar.getInstance(tz);
        Date date = c.getTime();
        long time = date.getTime();
        return time;
    }

    protected static String getDate(long dateInMillis) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(new Date(dateInMillis));
        return dateString;
    }

    protected static String getDisplayDate(String old_date) {
        String return_date="";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat convertFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        Date date = null;
        try {
            date = simpleDateFormat.parse(old_date);
            return_date = String.valueOf(convertFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return return_date;
    }
    protected static boolean CheckJsonError(String json_string) {
        String errFlag, errMsg;
        Boolean flag = false;
        JSONObject jsonObject;
        JSONArray jsonArray;
//        Fn.logD("json_string", json_string);
        try {
            jsonObject = new JSONObject(json_string);
            errFlag = jsonObject.getString("errFlag");
            if (errFlag.equals("1")) {
                flag = true;
            } else if (errFlag.equals("0")) {
                flag = false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return flag;
    }

    protected static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    protected static HashMap<String, String> checkParams(HashMap<String, String> map) {
        Iterator<HashMap.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry<String, String> pairs = (HashMap.Entry<String, String>) it.next();
            if ((pairs.getValue() == null) || (pairs.getValue() == "null")) {
                map.put(pairs.getKey(), "");
            }
        }
        return map;
    }

    protected static Bundle CheckBundle(Bundle bundle) {
        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();
            while (it.hasNext()) {
                String key = it.next();
                if ((bundle.get(key) == null) || (bundle.get(key) == "null")) {
                    bundle.putString(key, "");
                }
            }
        }
        return bundle;
    }

    protected static Intent CheckIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();
            while (it.hasNext()) {
                String key = it.next();
                if ((bundle.get(key) == null) || (bundle.get(key) == "null")) {
                    intent.putExtra(key, "");
                }
            }
        }
        return intent;
    }

    protected static String getValueFromBundle(Bundle b, String key) {
        Object object_value = b.get(key);
        String value = "";
        if ((object_value != null) && ((object_value != "null"))) {
            value = String.valueOf(object_value);
        }
        return value;
    }

    protected static boolean isGpsEnabled(Context ctx) {
        LocationManager locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        //getting GPS status
        Boolean isGPSenabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSenabled == true) {
            return true;
        } else {
            return false;
        }
    }
    protected static boolean isTimeNight(String date_time, int booking_hour, int booking_minute){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date DateTime = df.parse(date_time);
            Calendar cal = Calendar.getInstance(); //Create Calendar-Object
            cal.setTime(DateTime);               //Set the Calendar to now
            int hour = cal.get(Calendar.HOUR_OF_DAY); //Get the hour from the calendar
            int minute = cal.get(Calendar.MINUTE);
            int new_hour = hour+booking_hour;
            int new_minute = minute+booking_minute;
            Fn.SystemPrintLn("hour: "+String.valueOf(hour)+"   minute: "+String.valueOf(minute)+"    new_hour: "+String.valueOf(new_hour)+"     new_minute: "+String.valueOf(new_minute));
            if(new_minute>60){
                new_hour = new_hour+(new_minute%60);
                new_minute = new_minute/60;
                Fn.SystemPrintLn("new_minute>60"+"    new_minute: "+String.valueOf(new_minute)+"    new_hour: "+String.valueOf(new_hour));
            }
            if(new_hour>23){
                new_hour = new_hour-24;
                Fn.SystemPrintLn("new_hour>23"+"    new_minute: "+String.valueOf(new_minute)+"    new_hour: "+String.valueOf(new_hour));
            }
            if((hour == 23) ||((hour >=0)&&(hour <=7))||(new_hour == 23) ||((new_hour >=0)&&(new_hour <=7)))              // Check if hour is between 8 am and 11pm
            {
                Fn.SystemPrintLn("return true");
                return true;
                // do whatever you want
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Fn.SystemPrintLn("return false");
        return false;
    }
    protected static boolean isInternetEnabled(Context ctx) {
        LocationManager locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        //getting GPS status
        Boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isNetworkEnabled == true) {
            return true;
        }
        return false;
    }

    protected static boolean isNetworkEnabled(Context ctx) {
        ConnectivityManager conMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            return false;
        } else {
            return true;
        }
    }

    protected static void delay(int time) {
        for (int i = 0; i < 1000 * time; i++) ;
    }
    public static void showGpsAlertDialg(GoogleApiClient mGoogleApiClient, final Context ctx) {
        final int REQUEST_CHECK_SETTINGS = 0x1;
        if (mGoogleApiClient != null) {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            builder.setAlwaysShow(true); //this is the key ingredient

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult((Activity) ctx, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }
    protected static Location getCurrentlocation(Context ctx) {
        boolean isGPSEnabled = false;
        boolean isNetworkEnabled = false;
        boolean isGPSTrackingEnabled = false;
        String provider_info = "";
        Location location = null;
        LocationManager locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        //getting GPS status
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //getting network status
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        // Try to get location if you GPS Service is enabled
        if (isGPSEnabled) {
            isGPSTrackingEnabled = true;
//            Fn.logD(TAG, "Application use GPS Service");
                        /*
                         * This provider determines location using
                         * satellites. Depending on conditions, this provider may take a while to return
                         * a location fix.
                         */
            provider_info = LocationManager.GPS_PROVIDER;
        } else if (isNetworkEnabled) { // Try to get location if you Network Service is enabled
            isGPSTrackingEnabled = true;

//            Fn.logD(TAG, "Application use Network State to get GPS coordinates");
                    /*
                     * This provider determines location based on
                     * availability of cell tower and WiFi access points. Results are retrieved
                     * by means of a network lookup.
                     */
            provider_info = LocationManager.NETWORK_PROVIDER;
        } else {
        //    Fn.showGpsSettingDialog(ctx);
        }
        // Application can use GPS or Network Provider
        if (!provider_info.isEmpty()) {
            Fn.logD("location_manager", String.valueOf(locationManager));
            if (locationManager != null)
            {
                Fn.logD("location_manager_new", String.valueOf(locationManager));
                if (ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    location = locationManager.getLastKnownLocation(provider_info);
                    Fn.logD("permission_check_done1", "permission_check_done1");
                }
                if(location != null) {
                    return location;
                } else {
                    locationManager.requestLocationUpdates(
                            provider_info,
                            Constants.Config.MIN_TIME_BW_UPDATES,
                            Constants.Config.MIN_DISTANCE_CHANGE_FOR_UPDATES, (android.location.LocationListener) ctx);
                    return location;
                }
            }
        }
        return location;
    }
    public static void showGpsAutoEnableRequest(GoogleApiClient mGoogleApiClient, final Context ctx) {
        final int REQUEST_CHECK_SETTINGS = 0x1;
        if (mGoogleApiClient != null) {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(Constants.Config.GPS_INTERVAL);
            locationRequest.setFastestInterval(Constants.Config.GPS_FASTEST_INTERVAL);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            builder.setAlwaysShow(true); //this is the key ingredient
            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult((Activity) ctx, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }
    protected static Location getAccurateCurrentlocationService(GoogleApiClient mGoogleApiClient, Context ctx) {
        if (Fn.isGpsEnabled(ctx)) {
            LocationRequest mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(2 * 1000)        // 10 seconds, in milliseconds
                    .setFastestInterval(1 * 1000); // 1 second, in milliseconds
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (LocationListener) ctx);
                return location;
            } else {
                return location;
            }
        } else {
            return null;
        }
    }
    protected static Location getAccurateCurrentlocation(GoogleApiClient mGoogleApiClient, Context ctx) {

        if (Fn.isGpsEnabled(ctx)) {
            LocationRequest mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(2 * 1000)        // 10 seconds, in milliseconds
                    .setFastestInterval(1 * 1000); // 1 second, in milliseconds
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (LocationListener) ctx);
                return location;
            } else {
                return location;
            }
        }else{
            showGpsAutoEnableRequest(mGoogleApiClient,ctx);
            return null;
        }
    }

    public static String getLocationAddress(double latitude, double longitude, Context ctx){
        String full_address = "";
        Geocoder geoCoder = new Geocoder(ctx, Locale.getDefault());
        StringBuilder builder = new StringBuilder();
        try {
            List<Address> address = geoCoder.getFromLocation(latitude, longitude, 1);
            int maxLines = address.get(0).getMaxAddressLineIndex();
            for (int i=0; i<maxLines; i++) {
                String addressStr = address.get(0).getAddressLine(i);
                builder.append(addressStr);
                builder.append(" ");
            }

            full_address = builder.toString(); //This is the complete address.
        } catch (IOException e) {}
        catch (NullPointerException e) {}
        return full_address;
    }

    public static Uri getImageContentUri(Context context, String filePath) {
//        String filePath = imageFile.getAbsolutePath();
        File imageFile = new File(filePath);
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
    public static String getBookingStatus(String is_completed, String is_cancelled, String is_approved){
        String status = "";
        if(is_completed.equals("1")){
            status = "Completed";
        }else if(is_cancelled.equals("1")){
            status = "Cancelled";
        }else if(is_approved.equals("1")){
            status = "Approved";
        }else{
            status = "Pending";
        }
        return status;
    }
    public static String getUpperCase(String word){
        String Word = word.substring(0, 1).toUpperCase() + word.substring(1);
        return Word;
    }
    public static PointF calculateDerivedPosition(PointF point,double range, double bearing) {
        double EarthRadius = 6371000; // m
        double latA = Math.toRadians(point.x);
        double lonA = Math.toRadians(point.y);
        double angularDistance = range / EarthRadius;
        double trueCourse = Math.toRadians(bearing);
        double lat = Math.asin(Math.sin(latA) * Math.cos(angularDistance)+Math.cos(latA) * Math.sin(angularDistance)*Math.cos(trueCourse));
        double dlon = Math.atan2(Math.sin(trueCourse)*Math.sin(angularDistance)*Math.cos(latA),Math.cos(angularDistance) - Math.sin(latA)*Math.sin(lat));
        double lon = ((lonA + dlon + Math.PI) % (Math.PI * 2)) - Math.PI;
        lat = Math.toDegrees(lat);
        lon = Math.toDegrees(lon);
        PointF newPoint = new PointF((float) lat, (float) lon);
        return newPoint;
    }
    public static boolean IsInServiceArea(double current_lat, double current_lng,String flag, Context ctx) {

        DBController controller = new DBController(ctx);
        SQLiteDatabase database = controller.getWritableDatabase();
        PointF center = new PointF((float) current_lat, (float) current_lng);
        final double mult = 1; // mult = 1.1; is more reliable
        PointF p1 = Fn.calculateDerivedPosition(center, mult * Constants.Config.SERVICE_RADIUS, 0);
        PointF p2 = Fn.calculateDerivedPosition(center, mult * Constants.Config.SERVICE_RADIUS, 90);
        PointF p3 = Fn.calculateDerivedPosition(center, mult * Constants.Config.SERVICE_RADIUS, 180);
        PointF p4 = Fn.calculateDerivedPosition(center, mult * Constants.Config.SERVICE_RADIUS, 270);

        String startQuery = " WHERE "
                + controller.CITY_LAT + " > " + String.valueOf(p3.x) + " AND "
                + controller.CITY_LAT + " < " + String.valueOf(p1.x) + " AND "
                + controller.CITY_LNG + " < " + String.valueOf(p2.y) + " AND "
                + controller.CITY_LNG + " > " + String.valueOf(p4.y);

        double fudge = Math.pow(Math.cos(Math.toRadians(current_lat)), 2);
        String order_by = " ORDER BY ((" + String.valueOf(current_lat) + " - " + controller.CITY_LAT + ") * (" + String.valueOf(current_lat) + " - " + controller.CITY_LAT + ") +(" + String.valueOf(current_lng) + " - " + controller.CITY_LNG + ") * (" + String.valueOf(current_lng) + " - " + controller.CITY_LNG + ") * " + String.valueOf(fudge) + ") ASC LIMIT 0,1";
        String select_city = " SELECT DISTINCT " + controller.CITY_ID + " FROM " + controller.TABLE_VIEW_FARE_CHART + startQuery + order_by;
        Fn.SystemPrintLn(select_city);
        Cursor cit = database.rawQuery(select_city, null);
        try {
            if (cit.moveToFirst()) {
                if (flag.equals("pickup")) {
                    String city_id = cit.getString(0);
                    Fn.SystemPrintLn("selected_city" + city_id);
                    Fn.putPreference(ctx, Constants.Keys.SELECTED_CITY_ID, city_id);
                }
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Fn.SystemPrintLn("no rows");
            return false;
        }
    }
}
