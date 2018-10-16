package in.driverlo.www.driverlo_customer;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

/**
 * Created by Shubham on 14/07/2016.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ForgotPassword extends DialogFragment implements View.OnClickListener {

    protected RequestQueue requestQueue;
    private String TAG = ForgotPassword.class.getName();
    private EditText reset_mobile_no;
    private Button submitButton;
    private String feedback;
    private AlertDialog.Builder b;
    private Dialog rd;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        b = new AlertDialog.Builder(getActivity());
        b.setTitle("Password will be sent to your mobile no");
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_forgot_password, null);
        b.setView(v);
        b.setCancelable(false);
        reset_mobile_no = (EditText) v.findViewById(R.id.reset_mobile_no);
        submitButton = (Button) v.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this);
        rd = b.create();
        return rd;
        //return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        if(getActivity()!= null) {
            if (checkValidation()) {
                String mobile_no = reset_mobile_no.getText().toString();
                HashMap<String, String> hashMap = new HashMap<String, String>();
                String rating_url = Constants.Config.ROOT_PATH + "forgot_password";
                hashMap.put("reset_mobile_no", mobile_no);
                sendVolleyRequest(rating_url, Fn.checkParams(hashMap));
            } else {
                Fn.Toast(getActivity(), Constants.Message.FORM_ERROR);
            }
        }
    }
    private boolean checkValidation() {
        boolean ret = true;
        if (!FormValidation.isPhoneNumber(reset_mobile_no, true)) ret = false;
        return ret;
    }
    public void sendVolleyRequest(String URL, final HashMap<String, String> hMap) {
        if(getActivity() !=  null) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String trimmed_response = response.substring(response.indexOf("{"));
                    handleResponse(trimmed_response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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

    protected void handleResponse(String response) {
        if(getActivity() !=  null) {
            if (!Fn.CheckJsonError(response)) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response);
                    String errMsg = jsonObject.getString("errMsg");
                    Fn.ToastShort(getActivity(), Constants.Message.PASSWORD_SENT);
                    rd.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response);
                    String errMsg = jsonObject.getString("errMsg");
                    Fn.ToastShort(getActivity(),errMsg);
                    rd.dismiss();
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
        Fn.cancelAllRequest(requestQueue, TAG);
    }
}