package in.driverlo.www.driverlo_customer;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class RideDetails extends Fragment {
    protected RequestQueue requestQueue;
    private String TAG = RideDetails.class.getName();
    LinearLayout dropoff_location_container,driver_detail_container,rating_bar_container, cancel_button_container ,approval_status_container;
    RatingBar rating_bar;
    TextView pickup_location, dropoff_location, booking_datetime, estimated_time,
             vehicle_type,coupon_code, approval_status, rating_text, driver_name, license_no;
    Button cancel_button;
    String brn_no;
    View view;
    public RideDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (container == null) {
            return null;
        } else {
            view = inflater.inflate(R.layout.fragment_ride_details, container, false);
            driver_detail_container = (LinearLayout) view.findViewById(R.id.driver_detail_container);
            rating_bar_container = (LinearLayout) view.findViewById(R.id.rating_bar_container);
            cancel_button_container = (LinearLayout) view.findViewById(R.id.cancel_button_container);
            approval_status_container = (LinearLayout) view.findViewById(R.id.approval_status_container);
            dropoff_location_container = (LinearLayout) view.findViewById(R.id.dropoff_location_container);
            rating_bar = (RatingBar) view.findViewById(R.id.rating_bar);
            pickup_location = (TextView) view.findViewById(R.id.pickup_location);
            dropoff_location = (TextView) view.findViewById(R.id.dropoff_location);
            booking_datetime = (TextView) view.findViewById(R.id.booking_datetime);
            estimated_time = (TextView) view.findViewById(R.id.estimated_time);
            vehicle_type = (TextView) view.findViewById(R.id.vehicle_type);
            coupon_code = (TextView) view.findViewById(R.id.coupon_code);
            approval_status = (TextView) view.findViewById(R.id.approval_status);
            rating_text = (TextView) view.findViewById(R.id.rating_text);
            driver_name = (TextView) view.findViewById(R.id.driver_name);
            license_no = (TextView) view.findViewById(R.id.license_no);
            cancel_button = (Button) view.findViewById(R.id.cancel_button);
            return view;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getActivity() != null) {
            if ((getActivity().getIntent() != null) && (getActivity().getIntent().getExtras() != null)) {
                Bundle bundle = getActivity().getIntent().getExtras();
                brn_no = Fn.getValueFromBundle(bundle, Constants.Keys.BRN_NO);
                getActivity().getIntent().setData(null);
                getActivity().setIntent(null);
            } else if (this.getArguments() != null) {
                Bundle bundle = this.getArguments();
                brn_no = Fn.getValueFromBundle(bundle, Constants.Keys.BRN_NO);
            }
            String booking_status_url = Constants.Config.ROOT_PATH + "ride_details";
            HashMap<String, String> hashMap = new HashMap<String, String>();
            String customer_token = Fn.getPreference(getActivity(), Constants.Keys.CUSTOMER_TOKEN);
            hashMap.put(Constants.Keys.BRN_NO, brn_no);
            hashMap.put(Constants.Keys.CUSTOMER_TOKEN, customer_token);
            sendVolleyRequest(booking_status_url, Fn.checkParams(hashMap),"ride_detail");

            cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String cancel_order_url = Constants.Config.ROOT_PATH+"cancel_order";
                    HashMap<String,String> hashMap = new HashMap<String, String>();
                    hashMap.put(Constants.Keys.BRN_NO, brn_no);
                    hashMap.put(Constants.Keys.CUSTOMER_TOKEN, Fn.getPreference(getActivity(),Constants.Keys.CUSTOMER_TOKEN));
                    sendVolleyRequest(cancel_order_url,Fn.checkParams(hashMap),"cancel_order");
                }
            });
        }
    }
    public void sendVolleyRequest(String URL, final HashMap<String,String> hMap, final String method){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(method.equals("ride_detail")) {
                    bookingStatusSuccess(response);
                }else if(method.equals("cancel_order")){
                    OrderCancelSuccess(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                 if(getActivity()!=null) {
                    Fn.ToastShort(getActivity(), Constants.Message.NETWORK_ERROR);
                }
            }
        }){
            @Override
            protected HashMap<String,String> getParams(){
                return hMap;
            }
        };
        stringRequest.setTag(TAG);
        Fn.addToRequestQue(requestQueue, stringRequest, getActivity());
    }
    protected void bookingStatusSuccess(String response) {
        if(getActivity()!=null) {
            if (!Fn.CheckJsonError(response)) {
                JSONObject jsonObject;
                JSONArray jsonArray;
                try {
                    jsonObject = new JSONObject(response);
                    String errFlag = jsonObject.getString("errFlag");
                    if (errFlag.equals("0")) {
                        if (jsonObject.has("likes")) {
                            jsonArray = jsonObject.getJSONArray("likes");
                            int count = 0;
                            while (count < jsonArray.length()) {
                                JSONObject JO = jsonArray.getJSONObject(count);
                                pickup_location.setText(JO.getString("pickup_point"));
                                String dropoff_point = JO.getString("dropoff_point");
                                if(!dropoff_point.equals("N.A.")){
                                    dropoff_location.setText(dropoff_point);
                                    dropoff_location_container.setVisibility(View.VISIBLE);
                                }
                                booking_datetime.setText(JO.getString("dropoff_point"));
                                booking_datetime.setText(Fn.getDisplayDate(JO.getString("booking_datetime")));
                                estimated_time.setText(JO.getString("estimated_time"));
                                vehicle_type.setText(Fn.getUpperCase(JO.getString("vehicle_type"))+" - "+Fn.getUpperCase(JO.getString("vehicle_mode")));
                                coupon_code.setText(JO.getString("coupon_code"));

                                String received_is_cancelled = JO.getString("is_cancelled");
                                String received_is_approved = JO.getString("is_approved");
                                String received_is_completed = JO.getString("is_completed");
                                brn_no = JO.getString("brn_no");
                                if(received_is_completed.equals("1")){
                                    driver_detail_container.setVisibility(View.VISIBLE);
                                    driver_name.setText(JO.getString("driver_name"));
                                    license_no.setText(JO.getString("driver_license_no"));
                                    approval_status_container.setVisibility(View.GONE);
                                    cancel_button_container.setVisibility(View.GONE);
                                     float received_driver_rating = Float.parseFloat(JO.getString("driver_rating"));
                                    rating_bar_container.setVisibility(View.VISIBLE);
                                    if(received_driver_rating == 0.0f){
                                        handleRating();
                                    }else{
                                        rating_text.setText("You Rated");
                                        rating_bar.setRating(received_driver_rating);
                                        rating_bar.setIsIndicator(true);
                                    }
                                }else if(received_is_approved.equals("1")){
                                    driver_detail_container.setVisibility(View.VISIBLE);
                                    rating_bar_container.setVisibility(View.GONE);
                                    driver_name.setText(JO.getString("driver_name"));
                                    license_no.setText(JO.getString("driver_license_no"));
                                    approval_status_container.setVisibility(View.GONE);
                                }else if(received_is_cancelled.equals("1")){
                                    driver_detail_container.setVisibility(View.GONE);
                                    cancel_button_container.setVisibility(View.GONE);
                                    approval_status.setText("Cancelled");
                                }
                                count++;
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                ErrorDialog(Constants.Title.SERVER_ERROR, Constants.Message.SERVER_ERROR);
            }
        }
    }
    protected void OrderCancelSuccess(String response){
        if(getActivity() != null) {
            if (!Fn.CheckJsonError(response)) {
                Fragment fragment = new RideDetails();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.Keys.BRN_NO, brn_no);
                fragment.setArguments(Fn.CheckBundle(bundle));
                FragmentManager fragmentManager = FullActivity.fragmentManager;
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                //                Fragment fragment = new BookNow();
                transaction.replace(R.id.main_content, fragment, Constants.Config.CURRENT_FRAG_TAG);
                if ((FullActivity.homeFragmentIndentifier == -5)) {
                    transaction.addToBackStack(null);
                    FullActivity.homeFragmentIndentifier = transaction.commit();
                } else {
                    transaction.commit();
                }
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Ride Details");
            } else {
                ErrorDialog(Constants.Title.SERVER_ERROR,Constants.Message.SERVER_ERROR);
            }
        }
    }
    private void ErrorDialog(String Title, String Message){
        if(getActivity() != null) {
            Fn.showDialog(getActivity(), Title, Message);
        }
    }
    protected void handleRating(){
        rating_text.setText("Rate Driver");
        rating_bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                RatingDialog rd = new RatingDialog();
                Bundle bundle = new Bundle();
                bundle.putString("rating", String.valueOf(rating));
                bundle.putString("brn_no", brn_no);
                rd.setArguments(Fn.CheckBundle(bundle));
                rd.show(getActivity().getFragmentManager(), "ABC");
            }
        });
    }
}
