package in.driverlo.www.driverlo_customer;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
public class ReferEarn extends Fragment {


    View view;
    Button start_inviting, apply_referal_code;
    protected RequestQueue requestQueue;
    private String TAG = ReferEarn.class.getName();
    TextView referral_code, points_earned;
    public ReferEarn() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_refer_earn, container, false);
        start_inviting = (Button) view.findViewById(R.id.start_inviting);
        apply_referal_code = (Button) view.findViewById(R.id.apply_referal_code);
        referral_code = (TextView) view.findViewById(R.id.referal_code);
        points_earned = (TextView) view.findViewById(R.id.points_earned);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getActivity() != null) {
            referral_code.setText(Fn.getPreference(getActivity(), Constants.Keys.REFERAL_CODE));
            start_inviting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String referal_code = Fn.getPreference(getActivity(), Constants.Keys.REFERAL_CODE);
                    String app_link = Constants.Config.APP_LINK;
                    String send_text = "Download the App DriverLo " + app_link + " and enter code " + referal_code + " to earn 200 points";
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, send_text);
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
                }
            });
            apply_referal_code.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ApplyReferalCode referal_code_dialog = new ApplyReferalCode();
                    referal_code_dialog.show(getActivity().getFragmentManager(), "ABC");
                }
            });
            HashMap<String,String> hashMap = new HashMap<>();
            String get_point_url = Constants.Config.ROOT_PATH+"get_total_point";
            hashMap.put(Constants.Keys.CUSTOMER_TOKEN, Fn.getPreference(getActivity(), Constants.Keys.CUSTOMER_TOKEN));
            sendVolleyRequest(get_point_url,Fn.checkParams(hashMap));
        }
    }
    public void sendVolleyRequest(String URL, final HashMap<String, String> hMap) {
        if(getActivity() !=  null) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                     PointReceiveSuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ErrorDialog(Constants.Title.NETWORK_ERROR,Constants.Message.NETWORK_ERROR);
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
    public void PointReceiveSuccess(String response){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            String errFlag = jsonObject.getString("errFlag");
            if (errFlag.equals("0")) {
                if (jsonObject.has("likes")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("likes");
                    int count = 0;
                    while (count < jsonArray.length()) {
                        JSONObject JO = jsonArray.getJSONObject(count);
                        String total_point = JO.getString("total_point");
                        points_earned.setText("Points Earned: "+ total_point);
                        count++;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    private void ErrorDialog(String Title,String Message){
        Fn.showDialog(getActivity(), Title, Message);
    }
}
