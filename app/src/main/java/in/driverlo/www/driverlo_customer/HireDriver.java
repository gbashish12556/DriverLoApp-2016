package in.driverlo.www.driverlo_customer;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PointF;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Range;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class HireDriver extends Fragment implements  View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    protected RequestQueue requestQueue;
    protected  DBController controller;
    private SQLiteDatabase database;
    private GoogleMap mMap;
    Location location;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static String TAG = "MAP LOCATION";
    private Context mContext;
    private TextView mLocationMarkerText;
    TextView pickup_location,dropoff_location;
    int journey_type = 0;
    private LatLng mCenterLatLong;
    double current_lat = 22.58, current_lng = 88.34;
    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;
    /**
     * The formatted location address.
     */
    protected String mAddressOutput;
    protected String mAreaOutput;
    protected String mCityOutput;
    protected String mStreetOutput;
    protected String mPostaleCode;
    private TextView mLocationText;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE_PICKUP = 1;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE_DROPOFF = 2;
    private String pickup_address = "", dropoff_address = "";
    private LatLng southwest, northeast;
    SupportMapFragment mMapFragment;
    LinearLayout lower_view, dropoff_container,pickup_container, marker_container;
    RadioButton round_trip, one_way, out_station;
    ImageView imageMarker;
    View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (container == null) {
            return null;
        } else {
            view = inflater.inflate(R.layout.fragment_hire_driver, container, false);
            pickup_location = (TextView) view.findViewById(R.id.pickup_location);
            dropoff_location = (TextView) view.findViewById(R.id.dropoff_location);
            lower_view = (LinearLayout) view.findViewById(R.id.lower_view);
            marker_container = (LinearLayout) view.findViewById(R.id.marker_container);
            round_trip = (RadioButton) view.findViewById(R.id.round_trip);
            one_way = (RadioButton) view.findViewById(R.id.one_way);
            out_station = (RadioButton) view.findViewById(R.id.out_station);
            pickup_container = (LinearLayout) view.findViewById(R.id.pickup_point_container);
            dropoff_container = (LinearLayout) view.findViewById(R.id.dropoff_point_container);
            imageMarker = (ImageView) view.findViewById(R.id.imageMarker);
            return view;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getActivity()!= null) {
            controller = new DBController(getActivity());
            database = controller.getWritableDatabase();
            mResultReceiver = new AddressResultReceiver(new Handler());
            mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mMapFragment);
            one_way.setOnClickListener(this);
            round_trip.setOnClickListener(this);
            out_station.setOnClickListener(this);
            imageMarker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickup_address = pickup_location.getText().toString();
                    dropoff_address = dropoff_location.getText().toString();
                    Bundle bundle = new Bundle();
                    if (isValid(pickup_address, dropoff_address)) {
                        String[] journey_type_list = getResources().getStringArray(R.array.journey_type);
                        Fn.putPreference(getActivity(), Constants.Keys.PICKUP_ADDRESS, pickup_address);
                        Fn.putPreference(getActivity(), Constants.Keys.DROPOFF_ADDRESS, dropoff_address);
                        Fn.putPreference(getActivity(), Constants.Keys.JOURNEY_TYPE , journey_type_list[journey_type]);
                        DatePickerDialog date_picker_dialog = new DatePickerDialog();
                        date_picker_dialog.show(getActivity().getFragmentManager(), "ABC");
                    } else {
                        Fn.ToastShort(getActivity(), Constants.Message.INVALID_ADDRESS);
                    }
                    }
                }

                );
                final AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                        .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                        .build();
                if(FullActivity.mGoogleApiClient.isConnected())

                {
                    location = Fn.getAccurateCurrentlocation(FullActivity.mGoogleApiClient, getActivity());
                    if (location != null) {
                        southwest = new LatLng(location.getLatitude() - 2, location.getLongitude() - 2);
                        northeast = new LatLng(location.getLatitude() + 2, location.getLongitude() + 2);
                    } else {
                        southwest = new LatLng(Constants.Config.DEFAULT_CURRENT_LAT - 2, Constants.Config.DEFAULT_CURRENT_LNG - 2);
                        northeast = new LatLng(Constants.Config.DEFAULT_CURRENT_LAT + 2, Constants.Config.DEFAULT_CURRENT_LNG + 2);
                    }
                }
                else
                {
                    southwest = new LatLng(Constants.Config.DEFAULT_CURRENT_LAT - 2, Constants.Config.DEFAULT_CURRENT_LNG - 2);
                    northeast = new LatLng(Constants.Config.DEFAULT_CURRENT_LAT + 2, Constants.Config.DEFAULT_CURRENT_LNG + 2);
                }
                pickup_container.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick (View v){
                    try {
                        Fn.SystemPrintLn("pickup_location clicked");
                        Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                .setBoundsBias(new LatLngBounds(southwest, northeast))
                                .setFilter(typeFilter)
                                .build(getActivity());
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_PICKUP);
                    } catch (GooglePlayServicesRepairableException e) {
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                    }
                }
                }
                );
                dropoff_container.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick (View v){
                    try {
                        Fn.SystemPrintLn("dropoff_location clicked");
                        Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                .setBoundsBias(new LatLngBounds(southwest, northeast))
                                .setFilter(typeFilter)
                                .build(getActivity());
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_DROPOFF);
                    } catch (GooglePlayServicesRepairableException e) {
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                    }
                }
                }

                );

                LoadAddress();
            }
        }
    private boolean isValid(String pickup, String dropoff) {
        if(journey_type == 0){
            if (pickup == null)
                return false;
            if (pickup.length() == 0)
                return false;
        }else{
            if (pickup == null)
                return false;
            if (dropoff == null)
                return false;
            if (pickup.length() == 0)
                return false;
            if (dropoff.length() == 0)
                return false;
        }
        return true;
    }
    @Override
    public void onClick(View v) {
        // Is the button now checked?
        boolean checked = ((RadioButton) v).isChecked();
        // Check which radio button was clicked
        switch(v.getId()) {
            case R.id.round_trip:
                if (checked){
                    journey_type = 0;
                    dropoff_container.setVisibility(View.GONE);
                }
                    // Pirates are the best
                    break;
            case R.id.one_way:
                if (checked){
                    journey_type = 1;
                    dropoff_container.setVisibility(View.VISIBLE);
                }
                    // Ninjas rule
                    break;
            case R.id.out_station:
                if (checked){
                    journey_type = 2;
                    dropoff_container.setVisibility(View.VISIBLE);
                }
                    // Ninjas rule
                    break;
        }

    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    public void LoadAddress(){
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if(FullActivity.mGoogleApiClient.isConnected()) {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(FullActivity.mGoogleApiClient);
            if (mLastLocation != null) {
                changeMap(mLastLocation);
            } else
                try {
                    LocationServices.FusedLocationApi.removeLocationUpdates(FullActivity.mGoogleApiClient, this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            try {
                LocationRequest mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(Constants.Config.GPS_INTERVAL);
                mLocationRequest.setFastestInterval(Constants.Config.GPS_FASTEST_INTERVAL);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                LocationServices.FusedLocationApi.requestLocationUpdates(FullActivity.mGoogleApiClient, mLocationRequest, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}

    @Override
    public void onLocationChanged(Location location) {
        if(getActivity() != null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            if (FullActivity.mGoogleApiClient.isConnected()) {
                try {
                    if (location != null)
                         changeMap(location);
                         LocationServices.FusedLocationApi.removeLocationUpdates(FullActivity.mGoogleApiClient, this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void changeMap(Location location) {
        if(getActivity() != null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            // check if map is created successfully or not
            if (mMap == null) {
                mMap = mMapFragment.getMap();
                mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        mCenterLatLong = cameraPosition.target;
                        current_lat = mCenterLatLong.latitude;
                        current_lng = mCenterLatLong.longitude;
                        mMap.clear();
                        try {
                            Location mLocation = new Location("");
                            mLocation.setLatitude(current_lat);
                            mLocation.setLongitude(current_lng);
                            if (Fn.IsInServiceArea(current_lat,current_lng,"pickup",getActivity())) {
                                lower_view.setVisibility(View.VISIBLE);
                                marker_container.setVisibility(View.VISIBLE);
                            } else {
                                lower_view.setVisibility(View.GONE);
                                marker_container.setVisibility(View.GONE);
                                ErrorDialog(Constants.Title.ERROR, Constants.Message.NO_SERVICE_AREA);
                            }
                            startIntentService(mLocation);
//                    mLocationMarkerText.setText("Lat : " + mCenterLatLong.latitude + "," + "Long : " + mCenterLatLong.longitude);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            if (mMap != null) {
                mMap.getUiSettings().setZoomControlsEnabled(false);
                LatLng latLong;
                latLong = new LatLng(location.getLatitude(), location.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLong).zoom(19f).tilt(70).build();
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                Fn.SystemPrintLn(location);
                startIntentService(location);
            } else {
                Toast.makeText(getActivity(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService(Location mLocation) {
        if(getActivity() != null) {
            // Create an intent for passing to the intent service responsible for fetching the address.
            Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
            // Pass the result receiver as an extra to the service.
            intent.putExtra(AppUtils.LocationConstants.RECEIVER, mResultReceiver);
            // Pass the location data as an extra to the service.
            intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation);
            // Start the service. If the service isn't already running, it is instantiated and started
            // (creating a process for it if needed); if it is running then it remains running. The
            // service kills itself automatically once all intents are processed.
            getActivity().startService(intent);
        }
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            // Display the address string or an error message sent from the intent service.
            Fn.SystemPrintLn(resultData);
            mAddressOutput = resultData.getString(AppUtils.LocationConstants.RESULT_DATA_KEY);
            Fn.SystemPrintLn(mAddressOutput);
            mAreaOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_AREA);
            mCityOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_CITY);
            mPostaleCode  = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_EXTRA);
            mStreetOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_STREET);
            mAddressOutput = mStreetOutput;
            displayAddressOutput();
            // Show a toast message if an address was found.
            if (resultCode == AppUtils.LocationConstants.SUCCESS_RESULT) {
                //  showToast(getString(R.string.address_found));

            }
        }
    }
    /**
     * Updates the address in the UI.
     */
    protected void displayAddressOutput() {
        try {
            if (mAreaOutput != null)
                pickup_location.setText(mAddressOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Called after the autocomplete activity has finished to return its result.
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(getActivity() != null) {
            // Check that the result was from the autocomplete widget.
            if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_PICKUP) {
                if (resultCode == getActivity().RESULT_OK) {
                    // Get the user's selected place from the Intent.
                    Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                    // TODO call location based filter
                    LatLng latLong;
                    latLong = place.getLatLng();
                    pickup_location.setText(place.getName().toString().replaceAll("[\r\n]+", " ") + "");
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(latLong).zoom(Constants.Config.MAP_HIGH_ZOOM_LEVEL).build();
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_DROPOFF) {
                if (resultCode == getActivity().RESULT_OK) {
                    // Get the user's selected place from the Intent.
                    Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                    // TODO call location based filter
                    LatLng latLong = place.getLatLng();
                    Double current_lat = latLong.latitude;
                    Double current_lng = latLong.longitude;
                    dropoff_location.setText(place.getName().toString().replaceAll("[\r\n]+", " ") + "");
                    if(journey_type == 1){
                        if(Fn.IsInServiceArea(current_lat,current_lng,"dropoff",getActivity())) {
                            lower_view.setVisibility(View.VISIBLE);
                            marker_container.setVisibility(View.VISIBLE);
                        } else {
                            lower_view.setVisibility(View.GONE);
                            marker_container.setVisibility(View.GONE);
                            ErrorDialog(Constants.Title.ERROR, Constants.Message.NO_SERVICE_AREA);
                        }
                    }else if(journey_type == 2){
                        if(!Fn.IsInServiceArea(current_lat,current_lng,"dropoff",getActivity())) {
                            lower_view.setVisibility(View.VISIBLE);
                            marker_container.setVisibility(View.VISIBLE);
                        } else {
                            lower_view.setVisibility(View.GONE);
                            marker_container.setVisibility(View.GONE);
                            ErrorDialog(Constants.Title.ERROR, Constants.Message.SHOULD_OUT_SERVICE_AREA);
                        }
                    }else {
                        lower_view.setVisibility(View.GONE);
                        marker_container.setVisibility(View.GONE);
                        ErrorDialog(Constants.Title.ERROR, Constants.Message.SHOULD_OUT_SERVICE_AREA);
                    }

                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
            }
        }
    }
    private void ErrorDialog(String Title, String Message){
        if(getActivity() !=  null) {
            Fn.showDialog(getActivity(), Title, Message);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }
    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public void onResume() {
        super.onResume();
        Fn.startAllVolley(requestQueue);
    }
    @Override
    public void onPause() {
        super.onPause();
        Fn.stopAllVolley(requestQueue);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Fn.cancelAllRequest(requestQueue, TAG);
        if (mMap != null) {
            mMap = null;
        }
        database.close();
        FullActivity.fragmentManager.beginTransaction().remove(getChildFragmentManager().findFragmentById(R.id.mMapFragment)).commitAllowingStateLoss();
    }
}
