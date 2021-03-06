package in.driverlo.www.driverlo_customer.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import in.driverlo.www.driverlo_customer.Constants;
import in.driverlo.www.driverlo_customer.Controller.DBController;
import in.driverlo.www.driverlo_customer.Helper;
import in.driverlo.www.driverlo_customer.R;

public class FlashActivity extends AppCompatActivity implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{

    public  String TAG = FlashActivity.class.getName();
    public DBController controller;
    public RequestQueue requestQueue;
    public SQLiteDatabase database;
//    public RequestQueue requestQueue;
    private HashMap<String, String> queryValues;
    private int googleCount = 0;
    private Boolean stopTimer = false,stopForEver = false;
    private Timer timer;
    private Location location;
    private ProgressDialog progressDialog;
    private Boolean isNetworkEnabled = false;
    private Boolean isGpsEnabled = false;
    public static final int REQUEST_CHECK_SETTINGS = 0x1;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);
        getSupportActionBar().hide();
        controller = new DBController(this);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        stopTimer = false;
        if (checkPlayServices())
        {
            if (Helper.isGpsEnabled(this)) {
                if (Helper.isNetworkEnabled(this)) {
                    if (mGoogleApiClient.isConnected()) {
                        isNetworkEnabled = true;
                        location = Helper.getAccurateCurrentlocation(mGoogleApiClient, this);
                        if (location != null) {
                            SyncDataBase();
                          //  getCity(location);
                        } else {
                            if (timer == null) {
                                TimerProgramm();
                            }
                        }
                    } else {
                        mGoogleApiClient.connect();
                        googleCount++;
                        if (timer == null) {
                            TimerProgramm();
                        }
                    }
                } else {
                    ErrorDialog(Constants.Title.NETWORK_ERROR, Constants.Message.NETWORK_ERROR);
                    if (timer == null) {
                        TimerProgramm();
                    }
                }
            } else {
                Helper.showGpsAutoEnableRequest(mGoogleApiClient, this);
            }
        }
    }
    public void TimerProgramm() {
        int delay = Constants.Config.DELAY_LOCATION_CHECK; // delay for 0 sec.
        int period = Constants.Config.PERIOD_LOCATION_CHECK; // repeat every 100 msec.
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if ((stopTimer != true) && (stopForEver != true)) {
                            checkLocation();
                        }
                    }
                });
            }
        }, delay, period);
    }
    public void checkLocation(){
        if(Helper.isNetworkEnabled(this)){
            if (mGoogleApiClient.isConnected()) {
                location = Helper.getAccurateCurrentlocation(mGoogleApiClient, this);
                if (location != null) {
                    stopForEver = true;
                    SyncDataBase();
                }
            }else{
                mGoogleApiClient.connect();
            }
        } else {
            googleCount++;
            if(googleCount == 3) {
                ErrorDialog(Constants.Title.NETWORK_ERROR, Constants.Message.NETWORK_ERROR);
            }
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
        stopTimer = true;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }
    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this,result,1).show();
            }
            return false;
        }
        return true;
    }
    public void SyncDataBase(){
        String url_select = Constants.Config.ROOT_PATH+"fetch_view";
        String view_name = controller.TABLE_VIEW_FARE_CHART;
        String primary_key = controller.ID;
        String timestamp;
        String q1="select max(update_date) from ";
        String s = "";
        database = controller.getWritableDatabase();
            long cnt = 0;
            try {
                cnt = DatabaseUtils.queryNumEntries(database, view_name);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Helper.SystemPrintLn("count" + cnt);
            if (cnt > 0) {
//                     Helper.SystemPrintLn("entered");
                Cursor d =database.rawQuery(q1+view_name,null);
                d.moveToFirst();
                s=d.getString(0);
                timestamp = s;
            }
            else {
                timestamp = "";
            }
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("view_name",controller.TABLE_VIEW_FARE_CHART);
        hashMap.put("latest_date",timestamp);
        sendVolleyRequest(url_select, Helper.checkParams(hashMap));
    }
    public void sendVolleyRequest(String URL, final HashMap<String, String> hMap) {
        if(this !=  null) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    DataBaseSyncSuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ErrorDialog(Constants.Title.NETWORK_ERROR, Constants.Message.NETWORK_ERROR);
                }
            }) {
                @Override
                public HashMap<String, String> getParams() {
                    return hMap;
                }
            };
            stringRequest.setTag(TAG);
            Helper.addToRequestQue(requestQueue, stringRequest, this);
        }
    }

    public void DataBaseSyncSuccess(String response) {
        if(this !=  null) {
            try {
                String errFlag;
                String errMsg;
                JSONObject jsonObject = new JSONObject(response);
                errFlag = jsonObject.getString("errFlag");
                errMsg = jsonObject.getString("errMsg");
//                     Helper.SystemPrintLn(errFlag + errMsg);
                JSONObject UpdationObject;
                JSONArray jsonArray;
                if (jsonObject.has("likes"))
                {
                    controller.deleteTable();
                    ////controller.createTable(j);
                    jsonArray = jsonObject.getJSONArray("likes");
                    int count = 0;
                    while (count < jsonArray.length()) {
                        UpdationObject = jsonArray.getJSONObject(count);
                        queryValues = new HashMap<String, String>();
                            queryValues.put(controller.CITY_ID, UpdationObject.get(controller.CITY_ID).toString());
                            queryValues.put(controller.CITY_NAME, UpdationObject.get(controller.CITY_NAME).toString());
                            queryValues.put(controller.CITY_LAT, UpdationObject.get(controller.CITY_LAT).toString());
                            queryValues.put(controller.CITY_LNG, UpdationObject.get(controller.CITY_LNG).toString());

                            queryValues.put(controller.BASE_FARE, UpdationObject.get(controller.BASE_FARE).toString());
                            queryValues.put(controller.FARE, UpdationObject.get(controller.FARE).toString());
                            queryValues.put(controller.NIGHT_CHARGE, UpdationObject.get(controller.NIGHT_CHARGE).toString());
                            queryValues.put(controller.RETURN_CHARGE, UpdationObject.get(controller.RETURN_CHARGE).toString());

                            queryValues.put(controller.OUTSTATION_BASE_FARE, UpdationObject.get(controller.OUTSTATION_BASE_FARE).toString());
                            queryValues.put(controller.OUTSTATION_FARE, UpdationObject.get(controller.OUTSTATION_FARE).toString());

                            queryValues.put(controller.IS_ACTIVE, UpdationObject.get(controller.IS_ACTIVE).toString());
                            queryValues.put(controller.UPDATE_DATE, UpdationObject.get(controller.UPDATE_DATE).toString());
                            controller.insert(queryValues);
                        count++;
                    }
                }
                else if(errMsg.compareTo("Your request successful")==0)
                {
                    controller.deleteTable();
                }
            } catch (Exception e) {
                // handle exception
            }
        }
        database.close();
        NextActivity();
    }
    public void NextActivity() {
        String customer_token = Helper.getPreference(this, Constants.Keys.CUSTOMER_TOKEN);
        if (!customer_token.equals("defaultStringIfNothingFound")) {
            Intent intent1 = new Intent(this, CompleteActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent1);
            finish();
        }else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
    private void ErrorDialog(String Title,String Message){
        Helper.showDialog(this, Title, Message);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
//                        startLocationUpdates();
                        TimerProgramm();
                        break;
                    case Activity.RESULT_CANCELED:
                        Helper.showGpsAutoEnableRequest(mGoogleApiClient, this);//keep asking if imp or do whatever
                        break;
                }
                break;
        }
    }

}
