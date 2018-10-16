package in.driverlo.www.driverlo_customer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class RideHistory extends Fragment {

    View view;
    ListView list;
    ListAdapter listAdapter;
    private String TAG = RideHistory.class.getName();
    protected RequestQueue requestQueue;
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
            Fn.logD("Setting adapter","for finished booking");
            list.setAdapter(listAdapter);
            return view;
        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Fn.SystemPrintLn("***Finished Volley request first time****");
        createRequest(0);
        list.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page) {
                Fn.SystemPrintLn("*****page on loadMore: " + page);
                createRequest(page);
                //return true;
            }
        });
        Fn.logW("FINISHED_BOOKING_FRAGMENT_LIFECYCLE", "onActivityCreated called");
    }

    private void createRequest(final int page_no){
        if(getActivity() !=  null) {
            final String customer_token = Fn.getPreference(getActivity(), Constants.Keys.CUSTOMER_TOKEN);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.Config.ROOT_PATH + "ride_history",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Toast.makeText(MainActivity.this,response,Toast.LENGTH_LONG).show();
                            Fn.SystemPrintLn(response);
                            uiUpdate(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                            Fn.logD("error", ": volley request failed for FINISHED_BOOKING_FRAGMENT");
//                        ErrorDialog(Constants.Title.NETWORK_ERROR,Constants.Message.NETWORK_ERROR);
                            Fn.ToastShort(getActivity(), Constants.Message.NETWORK_ERROR);
                        }
                    }) {
                @Override
                protected HashMap<String, String> getParams() {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("customer_token", customer_token);
                    params.put("page_no", String.valueOf(page_no));
//                Fn.logD("user_token",user_token);
                    return Fn.checkParams(params);
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
            System.out.println("******FINISHED response :::: " + response);
            try {
                    String errFlag;
                    String errMsg;
                    JSONObject jsonObject = new JSONObject(response);
                    errFlag = jsonObject.getString("errFlag");
                    errMsg = jsonObject.getString("errMsg");
                    if(errFlag.equals("0")){
                        Fn.logD("response errflag and errMsg", errFlag + " " + errMsg);
                        JSONObject UpdationObject;
                        JSONArray jsonArray;
                        if (jsonObject.has("likes")) {
                            jsonArray = jsonObject.getJSONArray("likes");
                            int count = 0;
                            while (count < jsonArray.length()) {
                                UpdationObject = jsonArray.getJSONObject(count);
                                HashMap<String, String> qvalues = new HashMap<String, String>();
                                Fn.logD("brn_no and datetime1 recieved ", UpdationObject.get("brn_no").toString() + UpdationObject.get("booking_datetime").toString());
                                qvalues.put("brn_no", UpdationObject.get("brn_no").toString());
    //                            qvalues.put("vehicle_type", Fn.VehicleName(UpdationObject.get("vehicletype_id").toString(), getActivity()));
    //                            qvalues.put("datetime1", Fn.getDateName(UpdationObject.get("datetime1").toString()));
                                qvalues.put("booking_datetime", Fn.getDisplayDate(UpdationObject.get("booking_datetime").toString()));
                                qvalues.put("vehicle_type", Fn.getUpperCase(UpdationObject.get("vehicle_type").toString()) + " - " + Fn.getUpperCase(UpdationObject.get("vehicle_mode").toString()));
    //                            qvalues.put("vehicle_image", Integer.toString(Fn.getVehicleImage(Integer.parseInt(UpdationObject.get("vehicletype_id").toString()))));
                                qvalues.put("booking_status", Fn.getBookingStatus(UpdationObject.get("is_completed").toString()
                                        , UpdationObject.get("is_cancelled").toString()
                                        , UpdationObject.get("is_approved").toString()));
                                qvalues.put("pickup_point", UpdationObject.get("pickup_point").toString());
                                qvalues.put("journey_type", Fn.getUpperCase(UpdationObject.get("journey_type").toString()));
                                values.add(qvalues);
                                count++;
                            }
                        }
                    }else if(errFlag.equals("1")){
                        Fn.ToastShort(getActivity(), errMsg);
                    }

            } catch (Exception e) {
                // handle exception
                Fn.logE("log_tag", "Error parsing data " + e.toString());
            }
            ((BaseAdapter) listAdapter).notifyDataSetChanged();
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Fn.logD("Clicked at position :", String.valueOf(position));
                    View child_view = list.getChildAt(position - list.getFirstVisiblePosition());
                    Fn.logD("Child View  :", String.valueOf(child_view));
                    TextView brn_no = (TextView) child_view.findViewById(R.id.brn_no);
//                String PhoneNum = number.getText().toString();
                    Fn.logD("onItemClick", "list clicked at position: " + position + " value brn_no =" + brn_no.getText());
//                Fragment fragment = new Fragment();
                    Fragment fragment = new RideDetails();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.Keys.BRN_NO, brn_no.getText().toString());
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
                        Fn.logD("fragment instanceof Book", "homeidentifier != -1");
                    }
//                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_finished_booking_detail_fragment);

                }
            });
        }
    }
    private void ErrorDialog(String Title,String Message){
        if(getActivity() !=  null) {
            Fn.showDialog(getActivity(), Title, Message);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        requestQueue.stop();
        Fn.stopAllVolley(requestQueue);
        Fn.logW("FINISHED_BOOKING_FRAGMENT_LIFECYCLE", "onPause Called");
    }
    @Override
    public void onResume() {
        super.onResume();
        Fn.startAllVolley(requestQueue);
        //requestQueue.start();
        Fn.logW("FINISHED_BOOKING_FRAGMENT_LIFECYCLE", "onResume Called");
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Fn.cancelAllRequest(requestQueue, TAG);
        Fn.logW("FINISHED_BOOKING_FRAGMENT_LIFECYCLE", "onDestroyView Called");
    }
}
