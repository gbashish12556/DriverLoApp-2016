package in.driverlo.www.driverlo_customer;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Ashish on 11/17/2016.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ConfirmBookingDialog extends DialogFragment {
    protected RequestQueue requestQueue;
    private String TAG = ConfirmBookingDialog.class.getName();
    DBController controller;
    SQLiteDatabase database;
    Dialog dialog;
    AlertDialog.Builder alertDialog;
    String pickup_location_string,dropoff_location_string = "",total_time, journey_type_string, vehicle_type_string,
            vehicle_mode_string,booking_datetime_string,booking_day_string, booking_hour_string,booking_minute_string,city_id_string;
    int booking_day, booking_hour, booking_minute,total_minute,total_hour,base_fare,fare,night_charge,return_charge,outstation_base_fare, outstation_fare,total_fare;
    Button request_driver;
    EditText promo_code;
    TextView trip_type,booking_datetime,pickup_location,dropoff_location,estimated_time,vehicle_type,estimated_fare,journey_type;
    LinearLayout pickup_location_container, dropoff_location_container;
    boolean is_night = false;
    double estimated_fare_strring;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(getActivity() != null) {
            alertDialog = new AlertDialog.Builder(getActivity());
            View view = getActivity().getLayoutInflater().inflate(R.layout.confirm_booking_dialog_layout, null);

            controller = new DBController(getActivity());
            database = controller.getWritableDatabase();
            /*
            * getting references of views
            *
            * */
            journey_type = (TextView) view.findViewById(R.id.journey_type);
            booking_datetime = (TextView) view.findViewById(R.id.booking_datetime);
            pickup_location = (TextView) view.findViewById(R.id.pickup_location);
            dropoff_location = (TextView) view.findViewById(R.id.dropoff_location);
            estimated_time = (TextView) view.findViewById(R.id.estimated_time);
            vehicle_type = (TextView) view.findViewById(R.id.vehicle_type);
            estimated_fare = (TextView) view.findViewById(R.id.estimated_fare);
            pickup_location_container = (LinearLayout) view.findViewById(R.id.pickup_location_container);
            dropoff_location_container = (LinearLayout) view.findViewById(R.id.dropoff_location_container);
            request_driver = (Button) view.findViewById(R.id.request_driver);
            promo_code = (EditText) view.findViewById(R.id.promo_code);
            if(getActivity() != null) {

                /*
                * getting the stored value in shared
                * prference and storing it into in local variables
                * */
                city_id_string =  Fn.getPreference(getActivity(), Constants.Keys.SELECTED_CITY_ID);
                String query = "SELECT " + controller.BASE_FARE +
                        ", " +controller.FARE +
                        ", " +controller.NIGHT_CHARGE +
                        ", " +controller.RETURN_CHARGE +
                        ", " +controller.OUTSTATION_BASE_FARE +
                        ", " +controller.OUTSTATION_FARE +
                        " FROM "+controller.TABLE_VIEW_FARE_CHART+" WHERE "+controller.CITY_ID+" = "+ city_id_string;
                Fn.SystemPrintLn(query);
                Cursor cit = database.rawQuery(query, null);
                try {
                    if (cit.moveToFirst()) {
//                            String city_id = cit.getString(0);
                     base_fare = Integer.parseInt(cit.getString(0));
                     fare = Integer.parseInt(cit.getString(1));
                     night_charge = Integer.parseInt(cit.getString(2));
                     return_charge = Integer.parseInt(cit.getString(3));
                     outstation_base_fare = Integer.parseInt(cit.getString(4));
                     outstation_fare = Integer.parseInt(cit.getString(5));

                    Fn.SystemPrintLn("base_fare"+cit.getString(0));
                    Fn.SystemPrintLn("fare"+cit.getString(1));
                    Fn.SystemPrintLn("night_charge"+cit.getString(2));
                    Fn.SystemPrintLn("return_charge"+cit.getString(3));
                    Fn.SystemPrintLn("outstation_base_fare"+cit.getString(4));
                    Fn.SystemPrintLn("outstation_fare"+cit.getString(5));
//                            Fn.SystemPrintLn("selected_city" + city_id);
//                            Fn.putPreference(getActivity(), Constants.Keys.SELECTED_CITY_ID, city_id);
                    }
                } catch (Exception e) {
                    Fn.SystemPrintLn("no rows");
                }
                pickup_location_string = Fn.getPreference(getActivity(), Constants.Keys.PICKUP_ADDRESS);
                dropoff_location_string = Fn.getPreference(getActivity(), Constants.Keys.DROPOFF_ADDRESS);
                journey_type_string = Fn.getPreference(getActivity(), Constants.Keys.JOURNEY_TYPE);
                vehicle_type_string = Fn.getPreference(getActivity(), Constants.Keys.SELECTED_VEHICLE_TYPE);
                vehicle_mode_string = Fn.getPreference(getActivity(), Constants.Keys.SELECTED_VEHICLE_MODE);
                booking_datetime_string = Fn.getPreference(getActivity(), Constants.Keys.LATER_BOOKING_DATETIME);
                Fn.SystemPrintLn("booking_datetime" + booking_datetime_string);
                if(booking_hour>0){
                    //
                }
                final String[] journey_type_list = getResources().getStringArray(R.array.journey_type);
                if(journey_type_string.equals(journey_type_list[2])) {
                    booking_day_string = Fn.getPreference(getActivity(), Constants.Keys.BOOKING_DAY);
                    booking_hour_string = Fn.getPreference(getActivity(), Constants.Keys.BOOKING_HOUR);
                    total_time = booking_day_string+" Day "+booking_hour_string+" Hour";
                    estimated_time.setText(total_time);
                    booking_day = Integer.parseInt(booking_day_string);
                    booking_hour = Integer.parseInt(booking_hour_string);
                    if (booking_day > 0) {
                        total_hour = ((booking_day - 1) * 24) + booking_hour;
                        total_fare =  outstation_base_fare+(total_hour * outstation_fare)/Constants.Config.HOUR_PICKER_SLAB;
                        Fn.SystemPrintLn("booking_day_string"+booking_day_string+"booking_hour"+booking_hour_string+"outstation_base_fare"+String.valueOf(outstation_base_fare)+"outstation_fare"+String.valueOf(outstation_fare));
                    } else {
//                    total_minute = booking_minute;
                        total_fare = outstation_base_fare;
                        Fn.SystemPrintLn("booking_day_string"+booking_day_string+"booking_hour"+booking_hour_string+"outstation_base_fare"+String.valueOf(outstation_base_fare)+"outstation_fare"+String.valueOf(outstation_fare));
                    }
                }else{
                    booking_hour_string = Fn.getPreference(getActivity(), Constants.Keys.BOOKING_HOUR);
                    booking_minute_string = Fn.getPreference(getActivity(), Constants.Keys.BOOKING_MINUTE);
                    total_time = booking_hour_string+" Hour "+booking_minute_string+" Minute";
                    estimated_time.setText(total_time);
                    booking_hour = Integer.parseInt(booking_hour_string);
                    booking_minute = Integer.parseInt(booking_minute_string);
                    if (Fn.isTimeNight(booking_datetime_string, booking_hour, booking_minute)) {
                        Fn.SystemPrintLn("this is night");
//                    is_night = true;
                        if (booking_hour > 0) {
                            total_minute = (booking_hour - 1) * 60 + booking_minute;
                            total_fare = base_fare + total_minute * fare + night_charge;
                            Fn.SystemPrintLn("booking_hour"+booking_hour_string+"booking_minute"+booking_minute_string+"base_fare"+String.valueOf(base_fare)+"fare"+String.valueOf(fare));
                        } else {
//                    total_minute = booking_minute;
                            total_fare = base_fare + night_charge;
                            Fn.SystemPrintLn("booking_hour"+booking_hour_string+"booking_minute"+booking_minute_string+"base_fare"+String.valueOf(base_fare)+"fare"+String.valueOf(fare));
                        }
                    } else {
                        Fn.SystemPrintLn("this is day");
//                    is_night = false;
                        if (booking_hour > 0) {
                            total_minute = (booking_hour - 1) * 60 + booking_minute;
                            total_fare = base_fare + total_minute * fare;
                            Fn.SystemPrintLn("booking_hour"+booking_hour_string+"booking_minute"+booking_minute_string+"base_fare"+String.valueOf(base_fare)+"fare"+String.valueOf(fare));
                        } else {
//                    total_minute = booking_minute;
                            total_fare = base_fare;
                            Fn.SystemPrintLn("booking_hour"+booking_hour_string+"booking_minute"+booking_minute_string+"base_fare"+String.valueOf(base_fare)+"fare"+String.valueOf(fare));
                        }
                    }
                    if(journey_type_string.equals(journey_type_list[1])){
                        total_fare = total_fare+return_charge;
                    }
                }

                /*
                * displaying the stored variables to the text view
                * */

                journey_type.setText(Fn.getUpperCase(journey_type_string));
                booking_datetime.setText(Fn.getDisplayDate(booking_datetime_string));
                pickup_location.setText(pickup_location_string);
                if(journey_type_string.equals(journey_type_list[0])){
                    Fn.SystemPrintLn("journey_type"+journey_type_list[0]);
                    dropoff_location_container.setVisibility(View.GONE);
                }else{
                    Fn.SystemPrintLn("journey_type"+journey_type);
                    Fn.SystemPrintLn("journey_type_list"+journey_type_list[0]);
                    dropoff_location_container.setVisibility(View.VISIBLE);
                    dropoff_location.setText(dropoff_location_string);
                }
                vehicle_type.setText(Fn.getUpperCase(vehicle_type_string)+" - "+Fn.getUpperCase(vehicle_mode_string));
                estimated_fare.setText("Rs. " + String.valueOf(total_fare));
                Fn.putPreference(getActivity(), Constants.Keys.DROPOFF_ADDRESS, "");
                request_driver.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String confirm_booking_url = Constants.Config.ROOT_PATH + "confirm_booking";
                        String promo_code_string = promo_code.getText().toString();
                        String customer_token = Fn.getPreference(getActivity(), Constants.Keys.CUSTOMER_TOKEN);
//                        String mobile_no = Fn.getPreference(getActivity(), Constants.Keys.MOBILE_NO);
                        if (journey_type_string.equals(journey_type_list[0])) {
                            dropoff_location_string = "N.A.";
                        }
                        HashMap<String, String> hashMap = new HashMap<String, String>();
//                        hashMap.put("name",name);
                        hashMap.put(Constants.Keys.CUSTOMER_TOKEN, customer_token);
                        hashMap.put("pickup_point", pickup_location_string);
                        hashMap.put("dropoff_point", dropoff_location_string);
                        hashMap.put("booking_datetime", booking_datetime_string);
                        hashMap.put("journey_type", journey_type_string);
                        hashMap.put("vehicle_type", vehicle_type_string);
                        hashMap.put("vehicle_mode", vehicle_mode_string);
                        hashMap.put("estimated_fare", String.valueOf(total_fare));
                        hashMap.put("estimated_time", String.valueOf(total_time));
                        hashMap.put("coupon_code", promo_code_string);
                        sendVolleyRequest(confirm_booking_url, Fn.checkParams(hashMap));
                    }
                });
            }
            alertDialog.setView(view);
            alertDialog.setCancelable(false);
        }
        dialog = alertDialog.create();
        return dialog;
    }
    public void sendVolleyRequest(String URL, final HashMap<String, String> hMap) {
        if(getActivity() !=  null) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Fn.SystemPrintLn(response);
                    Fn.logD("onResponse",response);
                    String trimmed_response = response.substring(response.indexOf("{"));
                    Fn.logD("trimmed_response", trimmed_response);
                    ConfirmBookingSuccess(trimmed_response );
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Fn.logD("onErrorResponse", String.valueOf(error));
                    ErrorDialog(Constants.Title.NETWORK_ERROR, Constants.Message.NETWORK_ERROR);
                }
            }) {
                @Override
                protected HashMap<String, String> getParams() {
                    return hMap;
                }
            };
            stringRequest.setTag(TAG);
            Fn.addToRequestQue(requestQueue, stringRequest, getActivity());
        }
    }

    protected void ConfirmBookingSuccess(String response) {
        if(getActivity() !=  null) {
            Fn.logD("CONFIRM_BOOKING_FRAGMENT_LIFECYCLE", "ConfirmBookingSuccess Called");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String errFlag = jsonObject.getString("errFlag");
                String errMsg = jsonObject.getString("errMsg");
                if(errFlag.equals("0")){
//                    Fn.Toast(this,errMsg);
                    if(jsonObject.has("likes")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("likes");
                        Fn.logD("toastdone", "toastdone");
                        int count = 0;
                        while (count < jsonArray.length())
                        {
                            Fn.logD("likes_entered", "likes_entered");
                            JSONObject JO = jsonArray.getJSONObject(count);
                            String brn_no = JO.getString(Constants.Keys.BRN_NO);
                            Fragment fragment = new RideDetails();
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.Keys.BRN_NO,brn_no);
                            Fn.logD("passed_bcn_no", brn_no);
                            fragment.setArguments(Fn.CheckBundle(bundle));
                            FragmentManager fragmentManager =FullActivity.fragmentManager;
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            //                Fragment fragment = new BookNow();
                            transaction.replace(R.id.main_content, fragment,Constants.Config.CURRENT_FRAG_TAG);
                            if((FullActivity.homeFragmentIndentifier == -5)){
                                transaction.addToBackStack(null);
                                FullActivity.homeFragmentIndentifier =  transaction.commit();
                            }else{
                                transaction.commit();
                                Fn.logD("fragment instanceof Book","homeidentifier != -1");
                            }
                            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Ride Details");
                            Fn.ToastShort(getActivity(),errMsg);
                            dialog.dismiss();
                            count++;
                        }
                    }
                }else if(errFlag.equals("1")){
                    ErrorDialog(Constants.Title.SERVER_ERROR,errMsg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void ErrorDialog(String Title, String Message){
        if(getActivity() != null) {
            Fn.showDialog(getActivity(), Title, Message);
        }
    }
}
