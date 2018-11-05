package in.driverlo.www.driverlo_customer.DialogFragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import in.driverlo.www.driverlo_customer.Activities.CompleteActivity;
import in.driverlo.www.driverlo_customer.Constants;
import in.driverlo.www.driverlo_customer.Helper;
import in.driverlo.www.driverlo_customer.R;
import in.driverlo.www.driverlo_customer.Fragments.RideDetails;

/**
 * Created by Shubham on 14/07/2016.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class RatingDialog extends DialogFragment implements View.OnClickListener {

    public RequestQueue requestQueue;
    private String TAG = RatingDialog.class.getName();
    private EditText feedbackText;
    private RatingBar ratingBar;
    private Button submitButton;
    private float rating;
    private String booking_id, feedback,brn_no;
    private AlertDialog.Builder b;
    private Dialog rd;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        b = new AlertDialog.Builder(getActivity());
        b.setTitle("How Was Your Experience?");
        View v = getActivity().getLayoutInflater().inflate(R.layout.rating_dialog, null);
        b.setView(v);
        b.setCancelable(false);
        if((getArguments() != null)) {
            Bundle extras = getArguments();
            brn_no = extras.getString("brn_no");
            rating = Float.parseFloat(extras.getString("rating"));
//            booking_id = extras.getString("booking_id");
        }
        feedbackText = (EditText) v.findViewById(R.id.feedbackText);
        ratingBar = (RatingBar) v.findViewById(R.id.ratingBar);
        ratingBar.setRating(rating);
        submitButton = (Button) v.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this);
        rd = b.create();
        return rd;
        //return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        rating = ratingBar.getRating();
        feedback = feedbackText.getText().toString();
        if (rating == 0.0f) {
            if(getActivity() !=  null) {
                Toast.makeText(getActivity(), "Give a rating!!", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            HashMap<String, String> hashMap = new HashMap<String, String>();
            String rating_url = Constants.Config.ROOT_PATH + "rate_driver";
            // String CrnNo = Helper.getPreference(getActivity(),"current_crn_no");
           /* if(getActivity() !=  null) {
                String user_token = Helper.getPreference(getActivity(), "user_token");
            }*/
            hashMap.put("brn_no", brn_no);
            hashMap.put("driver_rating", String.valueOf(rating));
            hashMap.put("customer_feedback", feedback);
            sendVolleyRequest(rating_url, Helper.checkParams(hashMap));
        }
    }

    public void sendVolleyRequest(String URL, final HashMap<String, String> hMap) {
        if(getActivity() !=  null) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Helper.logD("onResponse_booking_status", String.valueOf(response));
                    handleResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Helper.logD("onErrorResponse", String.valueOf(error));
                }
            }) {
                @Override
                public HashMap<String, String> getParams() {
                    return hMap;
                }
            };
            stringRequest.setTag(TAG);
            Helper.addToRequestQue(requestQueue, stringRequest, getActivity());
        }
    }

    public void handleResponse(String response) {
        if(getActivity() !=  null) {
            if (!Helper.CheckJsonError(response)) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response);
                    String errFlag = jsonObject.getString("errFlag");
                    String errMsg = jsonObject.getString("errMsg");
                    if (errFlag.equals("1")) {
                        Helper.ToastShort(getActivity(), "Error in submission");
                    } else {
                        Helper.ToastShort(getActivity(), "Feedback submitted");
                        Fragment fragment = new RideDetails();
                        Bundle bundle = new Bundle();
                        bundle.putString("brn_no", brn_no);
                        Helper.logD("passed_brn_no", brn_no);
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void show(FragmentManager childFragmentManager, String abc) {
        rd.show();
    }
    @Override
    public void dismiss() {
        super.dismiss();
        Helper.cancelAllRequest(requestQueue, TAG);
    }
}