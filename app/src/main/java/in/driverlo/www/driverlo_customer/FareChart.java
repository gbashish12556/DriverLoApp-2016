package in.driverlo.www.driverlo_customer;


import android.*;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class FareChart extends Fragment {

    protected RequestQueue requestQueue;
    private static String TAG = FareChart.class.getName();
    EditText pickup_location, dropoff_location;
    DBController controller;
    SQLiteDatabase database;
    Spinner city_spinner, journey_spinner;
    private GoogleApiClient mGoogleApiClient;
    private ArrayList<String> city = new ArrayList<String>();
    private ArrayList<String> journey = new ArrayList<String>();
    private ArrayAdapter<String> adapter_city, adapter_journey;
    private String pickup_address = "", dropoff_address = "";
    private LatLng southwest, northeast;
    Location location;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE_PICKUP1 = 1;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE_DROPOFF1 = 2;
    int base_fare, fare, night_charge,return_charge, outstation_base_fare, outstation_fare, total_minute = 60,total_days=1,total_hour = 0, day_estimate, night_estimate, outstation_estimate;
    String general_charge_string,one_way_charge_string,night_charge_string,day_estimate_string,night_estimate_string, outstation_estimate_string, journey_type="Oneway";
    TextView general_charge_view,one_way_charge_view,night_charge_view,day_estimate_view,night_estimate_view,outstation_general_charge_view, outstation_fare_view, outstation_estimate_view;
    LinearLayout one_way_layout, round_trip_layout, outstation_layout,short_trip_layout;
    NumberPicker roundtrip_hour_picker, roundtrip_minute_picker, outstation_hour_picker, outstation_day_picker;
    View view;

    public FareChart() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (container == null) {
            return null;
        } else {
            view = inflater.inflate(R.layout.fragment_fare_chart, container, false);
            city_spinner = (Spinner) view.findViewById(R.id.city_spinner);
            journey_spinner = (Spinner) view.findViewById(R.id.journey_spinner);
            pickup_location = (EditText) view.findViewById(R.id.pickup_location);
            dropoff_location = (EditText) view.findViewById(R.id.dropoff_location);
            general_charge_view = (TextView) view.findViewById(R.id.general_charge);
            one_way_charge_view = (TextView) view.findViewById(R.id.one_way_charge);
            night_charge_view = (TextView) view.findViewById(R.id.night_charge);
            day_estimate_view = (TextView) view.findViewById(R.id.day_estimate);
            night_estimate_view = (TextView) view.findViewById(R.id.night_estimate);
            /*
            * Outstation general charge
            * */
            outstation_general_charge_view = (TextView) view.findViewById(R.id.outstation_general_charge);
            outstation_fare_view = (TextView) view.findViewById(R.id.outstation_fare);
            outstation_estimate_view = (TextView) view.findViewById(R.id.outstation_estimate);
           /*
           * Different linear layouts to manage visibility
           */
            one_way_layout = (LinearLayout) view.findViewById(R.id.one_way_layout);
            round_trip_layout = (LinearLayout) view.findViewById(R.id.round_trip_layout);
            outstation_layout = (LinearLayout) view.findViewById(R.id.outstation_layout);
            short_trip_layout = (LinearLayout) view.findViewById(R.id.short_trip_layout);
            /*
            * Different numberpicker
            * */
            roundtrip_hour_picker = (NumberPicker) view.findViewById(R.id.roundtrip_hour_picker);
            roundtrip_minute_picker = (NumberPicker) view.findViewById(R.id.roundtrip_minute_picker);
            roundtrip_hour_picker.setMaxValue(8);
            roundtrip_hour_picker.setMinValue(1);
            roundtrip_hour_picker.setWrapSelectorWheel(true);
            roundtrip_minute_picker.setMaxValue(11);
            roundtrip_minute_picker.setMinValue(0);
            roundtrip_minute_picker.setWrapSelectorWheel(true);
            String[] minuteValues = new String[12];
            for (int i = 0; i < minuteValues.length; i++) {
                String number = Integer.toString(i*Constants.Config.MINUTE_PICKER_SLAB);
                minuteValues[i] = number.length() < 2 ? "0" + number : number;
            }
            roundtrip_minute_picker.setDisplayedValues(minuteValues);

            outstation_day_picker = (NumberPicker) view.findViewById(R.id.outstation_day_picker);
            outstation_hour_picker = (NumberPicker) view.findViewById(R.id.outstation_hour_picker);
            outstation_day_picker.setMinValue(1);
            outstation_day_picker.setMaxValue(8);
            outstation_day_picker.setWrapSelectorWheel(true);
            outstation_hour_picker.setMinValue(0);
            outstation_hour_picker.setMaxValue(1);
            outstation_hour_picker.setWrapSelectorWheel(true);
            String[] hourValues = new String[2];
            for (int i = 0; i < hourValues.length; i++) {
                String number = Integer.toString(i*Constants.Config.HOUR_PICKER_SLAB);
                hourValues[i] = number.length() < 2 ? "0" + number : number;
            }
            outstation_hour_picker.setDisplayedValues(hourValues);
            return view;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            controller = new DBController(getActivity());
            database = controller.getWritableDatabase();
            journey.add(0, "Oneway");
            journey.add(1, "Roundtrip");
            journey.add(2, "Outstation");
            String query = "SELECT city_name FROM " + controller.TABLE_VIEW_FARE_CHART;
            Cursor cit = database.rawQuery(query, null);
            try {
                if (cit.moveToFirst()) {
                    do {
                        city.add(cit.getString(0));
                    } while (cit.moveToNext());
                }
            } catch (Exception e) {
                Fn.SystemPrintLn("no rows");
            }
            adapter_city = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, city);
            adapter_journey = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, journey);
            city_spinner.setAdapter(adapter_city);
            journey_spinner.setAdapter(adapter_journey);
            city_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView adapter, View v, int i, long lng) {
                    String city_selected = adapter.getItemAtPosition(i).toString();
                    String query = "SELECT base_fare, fare ,night_charge, return_charge, outstation_base_fare, outstation_fare FROM " + controller.TABLE_VIEW_FARE_CHART + " WHERE city_name = '" + city_selected + "' ";
                    Cursor fares = database.rawQuery(query, null);
                    try {
                        if (fares.moveToFirst()) {
                            base_fare = Integer.parseInt(fares.getString(0));
                            fare = Integer.parseInt(fares.getString(1));
                            night_charge = Integer.parseInt(fares.getString(2));
                            return_charge = Integer.parseInt(fares.getString(3));
                            outstation_base_fare = Integer.parseInt(fares.getString(4));
                            outstation_fare = Integer.parseInt(fares.getString(5));
                            CalculateEstimate();
                        }
                    } catch (Exception e) {
                        Fn.SystemPrintLn("no rows");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                }
            });
            journey_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void onItemSelected(AdapterView adapter, View v, int i, long lng) {
                    Fn.SystemPrintLn("Journey Spinner Selected");
                    journey_type = adapter.getItemAtPosition(i).toString();
                    if (journey_type.equals(journey.get(0))) {
                        one_way_charge_view.setVisibility(View.VISIBLE);
                        short_trip_layout.setVisibility(View.VISIBLE);
                        one_way_layout.setVisibility(View.VISIBLE);
                        round_trip_layout.setVisibility(View.GONE);
                        outstation_layout.setVisibility(View.GONE);
                    } else if(journey_type.equals(journey.get(1))) {
                        one_way_charge_view.setVisibility(View.GONE);
                        short_trip_layout.setVisibility(View.VISIBLE);
                        one_way_layout.setVisibility(View.GONE);
                        round_trip_layout.setVisibility(View.VISIBLE);
                        outstation_layout.setVisibility(View.GONE);
                        roundtrip_hour_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                // do something here
                                total_hour = roundtrip_hour_picker.getValue();
                                total_minute = roundtrip_minute_picker.getValue() * Constants.Config.MINUTE_PICKER_SLAB + (total_hour * 60);
                                CalculateEstimate();
                            }
                        });
                        roundtrip_minute_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                // do something here
                                total_hour = roundtrip_hour_picker.getValue();
                                total_minute = roundtrip_minute_picker.getValue() * Constants.Config.MINUTE_PICKER_SLAB + (total_hour * 60);
                                CalculateEstimate();
                            }
                        });
                    } else if(journey_type.equals(journey.get(2))) {
                        short_trip_layout.setVisibility(View.GONE);
                        outstation_layout.setVisibility(View.VISIBLE);
                        outstation_day_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                // do something here
                                total_days = outstation_day_picker.getValue();
                                total_hour = outstation_hour_picker.getValue() * Constants.Config.HOUR_PICKER_SLAB;
                                CalculateEstimate();
                            }
                        });
                        outstation_hour_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                // do something here
                                total_days = outstation_day_picker.getValue();
                                total_hour = outstation_hour_picker.getValue() * Constants.Config.HOUR_PICKER_SLAB;
                                CalculateEstimate();
                            }
                        });
                    }
                    CalculateEstimate();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                }
            });
            final AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                    .build();
            if (FullActivity.mGoogleApiClient.isConnected())
            {
                location = Fn.getAccurateCurrentlocation(FullActivity.mGoogleApiClient, getActivity());
                if (location != null) {
                    southwest = new LatLng(location.getLatitude() - 2, location.getLongitude() - 2);
                    northeast = new LatLng(location.getLatitude() + 2, location.getLongitude() + 2);
                } else {
                    southwest = new LatLng(Constants.Config.DEFAULT_CURRENT_LAT - 2, Constants.Config.DEFAULT_CURRENT_LNG - 2);
                    northeast = new LatLng(Constants.Config.DEFAULT_CURRENT_LAT + 2, Constants.Config.DEFAULT_CURRENT_LNG + 2);
                }
            } else {
                southwest = new LatLng(Constants.Config.DEFAULT_CURRENT_LAT - 2, Constants.Config.DEFAULT_CURRENT_LNG - 2);
                northeast = new LatLng(Constants.Config.DEFAULT_CURRENT_LAT + 2, Constants.Config.DEFAULT_CURRENT_LNG + 2);
            }
            pickup_location.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                   try {
                       Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                               .setBoundsBias(new LatLngBounds(southwest, northeast))
                               .setFilter(typeFilter)
                               .build(getActivity());
                       startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_PICKUP1);
                   } catch (GooglePlayServicesRepairableException e) {
                       // TODO: Handle the error.
                   } catch (GooglePlayServicesNotAvailableException e) {
                       // TODO: Handle the error.
                   }
               }
           }
            );
            dropoff_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                .setBoundsBias(new LatLngBounds(southwest, northeast))
                                .setFilter(typeFilter)
                                .build(getActivity());
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_DROPOFF1);
                    } catch (GooglePlayServicesRepairableException e) {
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                    }
                }
            });
        }
    }

    /*
    * this code is for oneway trip
    */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(getActivity() != null) {
            // Check that the result was from the autocomplete widget.
            if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_PICKUP1) {
                if (resultCode == getActivity().RESULT_OK) {
                    // Get the user's selected place from the Intent.
                    Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                    // TODO call location based filter
                    LatLng latLong = place.getLatLng();
                    Double current_lat = latLong.latitude;
                    Double current_lng = latLong.longitude;
                    pickup_address = place.getName().toString().replaceAll("[\r\n]+", " ") + "";
                    pickup_location.setText(pickup_address);
                    if (Fn.IsInServiceArea(current_lat,current_lng,"pickup",getActivity())) {
                        if(isValid(pickup_address,dropoff_address)){
                            CalculateTravelTime(pickup_address,dropoff_address);
                        }
                    } else {
                          Fn.SystemPrintLn("Address validation failed");
                          ErrorDialog(Constants.Title.ERROR, Constants.Message.NO_SERVICE_AREA);
                    }
                }
            } else if(requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_DROPOFF1) {
                if (resultCode == getActivity().RESULT_OK) {
                    // Get the user's selected place from the Intent.
                    Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                    // TODO call location based filter
                    LatLng latLong = place.getLatLng();
                    Double current_lat = latLong.latitude;
                    Double current_lng = latLong.longitude;
                    dropoff_address = place.getName().toString().replaceAll("[\r\n]+", " ") + "";
                    dropoff_location.setText(dropoff_address);
                    if (Fn.IsInServiceArea(current_lat, current_lng, "dropoff", getActivity())) {
                        if(isValid(pickup_address,dropoff_address)){
                            CalculateTravelTime(pickup_address,dropoff_address);
                        }
                    } else {
                          Fn.SystemPrintLn("Address validation failed");
                        ErrorDialog(Constants.Title.ERROR, Constants.Message.NO_SERVICE_AREA);
                    }
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }
        }
    }
    private boolean isValid(String pickup, String dropoff) {

        if (pickup == null)
            return false;
        if (dropoff == null)
            return false;
        if (pickup.length() == 0)
            return false;
        if (dropoff.length() == 0)
            return false;
        else
            return true;
    }
    /*
    * this code is for oneway trip
    */
    public float CalculateTravelTime(String pickup_address, String dropoff_address){
        float distance = 0;
        try {
            String  URL = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins="+URLEncoder.encode(pickup_address,"UTF-8")+"&destinations="+URLEncoder.encode(dropoff_address,"UTF-8")+"&key="+getResources().getString(R.string.server_APIkey1)+"";
            HashMap<String,String> hashMap = new HashMap<>();
            sendVolleyRequest(URL,hashMap);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return distance;
    }
    public void sendVolleyRequest(String URL, final HashMap<String,String> hMap){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CalculateFare(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorDialog(Constants.Title.NETWORK_ERROR, Constants.Message.NETWORK_ERROR);
            }
        }){
            @Override
            protected HashMap<String,String> getParams(){
                return hMap;
            }
        };
        stringRequest.setTag(TAG);
        if(getActivity()!=null) {
            Fn.addToRequestQue(requestQueue, stringRequest, getActivity());
        }
    }
    public void CalculateFare(String response){
        int fare = 0;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            JSONArray rows = jsonObject.getJSONArray("rows");
            JSONObject firstObject = rows.getJSONObject(0);
            JSONArray elements = firstObject.getJSONArray("elements");
            JSONObject elementsFirst = elements.getJSONObject(0);
            JSONObject durationObject = elementsFirst.getJSONObject("duration");
            total_minute = Integer.parseInt(durationObject.getString("value"))/60;
            CalculateEstimate();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void CalculateEstimate(){
        if(journey_type.equals(journey.get(0))) {
            if (total_minute > 60) {
                day_estimate = base_fare + (total_minute - 60) * fare + return_charge;
                night_estimate = day_estimate + night_charge;
            } else {
                day_estimate = base_fare + return_charge;
                night_estimate = day_estimate + night_charge;
            }
        }else if(journey_type.equals(journey.get(1))){
            if (total_minute > 60) {
                day_estimate = base_fare + (total_minute - 60) * fare;
                night_estimate = day_estimate + night_charge;
            } else {
                day_estimate = base_fare;
                night_estimate = day_estimate + night_charge;
            }
        }else if(journey_type.equals(journey.get(2))){
            if (total_days > 1) {
                outstation_estimate = outstation_base_fare + (((total_days - 1) *(24/Constants.Config.HOUR_PICKER_SLAB)+(total_hour/Constants.Config.HOUR_PICKER_SLAB)) *outstation_fare);
            } else {
                outstation_estimate = outstation_base_fare+(total_hour/Constants.Config.HOUR_PICKER_SLAB) *outstation_fare;
            }
        }
        ChangeUI();
    }
    public void ChangeUI(){
        if(journey_type.equals(journey.get(0))||journey_type.equals(journey.get(1))) {
            general_charge_string = "Anytime @Rs. " + String.valueOf(base_fare) + "/hour";
            one_way_charge_string = "Oneway charge of Rs. " + String.valueOf(return_charge) + "";
            night_charge_string = "Night Time charge of Rs. " + String.valueOf(night_charge) +"\n For drives ending between 10pm to 6am";
            general_charge_view.setText(general_charge_string);
            one_way_charge_view.setText(one_way_charge_string);
            night_charge_view.setSingleLine(false);
            night_charge_view.setText(night_charge_string);
            day_estimate_view.setText(String.valueOf("Rs. "+String.valueOf(day_estimate)));
            night_estimate_view.setText(String.valueOf("Rs. "+String.valueOf(night_estimate)));
        }else if(journey_type.equals(journey.get(2))){
            String outstation_general_charge_string = "@ Rs. " + String.valueOf(outstation_base_fare) + " for min 24 hrs";
            String outstation_estimate_string = "Pay Rs. " + String.valueOf(outstation_fare) + " for additional "+String.valueOf(Constants.Config.HOUR_PICKER_SLAB)+" hrs";
            outstation_general_charge_view.setText(outstation_general_charge_string);
            outstation_fare_view.setText(outstation_estimate_string);
            outstation_estimate_view.setText("Rs. "+String.valueOf(outstation_estimate));
        }
    }
    private void ErrorDialog(String Title, String Message){
        if(getActivity() !=  null) {
            Fn.showDialog(getActivity(), Title, Message);
        }
    }
}
