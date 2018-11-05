package in.driverlo.www.driverlo_customer.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

import in.driverlo.www.driverlo_customer.Activities.CompleteActivity;
import in.driverlo.www.driverlo_customer.Constants;
import in.driverlo.www.driverlo_customer.Helper;
import in.driverlo.www.driverlo_customer.R;
import in.driverlo.www.driverlo_customer.Utils.EndlessScrollListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class RideHistory extends Fragment {

    View view;
    ListView list;
    ListAdapter listAdapter;
    private String TAG = RideHistory.class.getName();
    public RequestQueue requestQueue;
    ArrayList<HashMap<String,String>> values = new ArrayList<HashMap<String, String>>();
    public RideHistory() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (container == null) {
            return null;
        } else {
            view = inflater.inflate(R.layout.fragment_ride_history, container, false);
            list = (ListView) view.findViewById(R.id.booking_list);
            // return inflater.inflate(R.layout.fragment_finished_booking, container, false);
            listAdapter = new SimpleAdapter(getContext(),values,R.layout.booking_list_view,new String[] {"brn_no","booking_datetime","journey_type","vehicle_type","booking_status","pickup_point"},
                    new int[] {R.id.brn_no,R.id.booking_datetime, R.id.journey_type, R.id.vehicle_type,R.id.booking_status,R.id.pickup_point});
            Helper.logD("Setting adapter","for finished booking");
            list.setAdapter(listAdapter);
            return view;
        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        createRequest(0);
        list.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page) {
                createRequest(page);
                //return true;
            }
        });
    }

    private void createRequest(final int page_no){
        if(getActivity() !=  null) {
            final String customer_token = Helper.getPreference(getActivity(), Constants.Keys.CUSTOMER_TOKEN);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.Config.ROOT_PATH + "ride_history",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Toast.makeText(MainActivity.this,response,Toast.LENGTH_LONG).show();
                            Helper.SystemPrintLn(response);
                            uiUpdate(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Helper.ToastShort(getActivity(), Constants.Message.NETWORK_ERROR);
                        }
                    }) {
                @Override
                public HashMap<String, String> getParams() {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("customer_token", customer_token);
                    params.put("page_no", String.valueOf(page_no));
                    return Helper.checkParams(params);
                }
            };
            requestQueue = Volley.newRequestQueue(getContext());
            stringRequest.setTag(TAG);
            requestQueue.add(stringRequest);
        }
    }
    private void uiUpdate(String response)
    {
        if(getActivity() !=  null) {
            try {
                    String errFlag;
                    String errMsg;
                    JSONObject jsonObject = new JSONObject(response);
                    errFlag = jsonObject.getString("errFlag");
                    errMsg = jsonObject.getString("errMsg");
                    if(errFlag.equals("0")){
                        JSONObject UpdationObject;
                        JSONArray jsonArray;
                        if (jsonObject.has("likes")) {
                            jsonArray = jsonObject.getJSONArray("likes");
                            int count = 0;
                            while (count < jsonArray.length()) {
                                UpdationObject = jsonArray.getJSONObject(count);
                                HashMap<String, String> qvalues = new HashMap<String, String>();
                                qvalues.put("brn_no", UpdationObject.get("brn_no").toString());
                                 qvalues.put("booking_datetime", Helper.getDisplayDate(UpdationObject.get("booking_datetime").toString()));
                                qvalues.put("vehicle_type", Helper.getUpperCase(UpdationObject.get("vehicle_type").toString()) + " - " + Helper.getUpperCase(UpdationObject.get("vehicle_mode").toString()));
                                qvalues.put("booking_status", Helper.getBookingStatus(UpdationObject.get("is_completed").toString()
                                        , UpdationObject.get("is_cancelled").toString()
                                        , UpdationObject.get("is_approved").toString()));
                                qvalues.put("pickup_point", UpdationObject.get("pickup_point").toString());
                                qvalues.put("journey_type", Helper.getUpperCase(UpdationObject.get("journey_type").toString()));
                                values.add(qvalues);
                                count++;
                            }
                        }
                    }else if(errFlag.equals("1")){
                        Helper.ToastShort(getActivity(), errMsg);
                    }

            } catch (Exception e) {
                // handle exception
            }
            ((BaseAdapter) listAdapter).notifyDataSetChanged();
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    View child_view = list.getChildAt(position - list.getFirstVisiblePosition());
                    TextView brn_no = (TextView) child_view.findViewById(R.id.brn_no);
                    Fragment fragment = new RideDetails();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.Keys.BRN_NO, brn_no.getText().toString());
                    fragment.setArguments(Helper.CheckBundle(bundle));
                    FragmentManager fragmentManager = CompleteActivity.fragmentManager;
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.main_content, fragment, Constants.Config.CURRENT_FRAG_TAG);
                    if ((CompleteActivity.homeFragmentIndentifier == -5)) {
                        transaction.addToBackStack(null);
                        CompleteActivity.homeFragmentIndentifier = transaction.commit();
                    } else {
                        transaction.commit();
                    }
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Helper.stopAllVolley(requestQueue);
    }
    @Override
    public void onResume() {
        super.onResume();
        Helper.startAllVolley(requestQueue);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Helper.cancelAllRequest(requestQueue, TAG);
    }
}
