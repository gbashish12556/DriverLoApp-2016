package in.driverlo.www.driverlo_customer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class OtpVerification extends AppCompatActivity {
    private  String TAG = OtpVerification.class.getName();
    protected RequestQueue requestQueue;
    private  EditText otp_value;
    private String entered_otp;
    private String OTP,name,email,mobile_no,password, referal_code;
    private Bundle b;
    private Button otp_verify_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        otp_value = (EditText) findViewById(R.id.registration_otp);
        otp_verify_button = (Button) findViewById(R.id.otp_verify_button);
        if(getIntent().getExtras() != null) {
            b = getIntent().getExtras();
            OTP = Fn.getValueFromBundle(b, "OTP");
            name = Fn.getValueFromBundle(b, "signup_name");
            email= Fn.getValueFromBundle(b, "signup_email");
            mobile_no = Fn.getValueFromBundle(b, "signup_mobile_no");
            password = Fn.getValueFromBundle(b, "signup_password");
            referal_code = Fn.getValueFromBundle(b, Constants.Keys.REFERAL_CODE);
//            Fn.Toast(this, String.valueOf(OTP));
        }
        otp_verify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOtp();
            }
        });
    }

    public void verifyOtp(){
        if(checkValidation()) {
//            Fn.showProgressDialog(Constants.Message.LOADING,this);
            entered_otp = otp_value.getText().toString();
            if (OTP.equals(entered_otp)) {
               HashMap<String, String> hashMap = new HashMap<String, String>();
               String rating_url = Constants.Config.ROOT_PATH + "customer_registration";
               hashMap.put("signup_name", name);
               hashMap.put("signup_email", email);
               hashMap.put("signup_mobile_no", mobile_no);
               hashMap.put("signup_password", password);
                hashMap.put(Constants.Keys.REFERAL_CODE, referal_code);
               sendVolleyRequest(rating_url, Fn.checkParams(hashMap));
            } else {
                Fn.showDialog(this,Constants.Title.OTP_VERIFICATION_ERROR,Constants.Message.OTP_VERIFICATION_ERROR);
            }
        }
        else {
            Fn.ToastShort(this, Constants.Message.FORM_ERROR);
        }
    }
    private boolean checkValidation() {
        boolean ret = true;
        if (!FormValidation.isValidOTP(otp_value, true)) ret = false;
        return ret;
    }
    public void sendVolleyRequest(String URL, final HashMap<String,String> hMap){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Fn.logD("onResponse", String.valueOf(response));
                Fn.SystemPrintLn(response);
                OtpVerificationSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorDialog(Constants.Title.NETWORK_ERROR,Constants.Message.NETWORK_ERROR);
            }
        }){
            @Override
            protected HashMap<String,String> getParams(){
                return hMap;
            }
        };
        stringRequest.setTag(TAG);
        Fn.addToRequestQue(requestQueue, stringRequest, this);
    }
    public void OtpVerificationSuccess(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            String errFlag = jsonObject.getString("errFlag");
            String errMsg = jsonObject.getString("errMsg");
            if(errFlag.equals("0")){
                if(jsonObject.has("likes")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("likes");
                    int count = 0;
                    while (count < jsonArray.length())
                    {

                        JSONObject JO = jsonArray.getJSONObject(count);
                        String customer_token = JO.getString(Constants.Keys.CUSTOMER_TOKEN);
                        String mobile_no = JO.getString(Constants.Keys.MOBILE_NO);
                        String name = JO.getString(Constants.Keys.NAME);
                        String referal_code = JO.getString(Constants.Keys.REFERAL_CODE);
                        Fn.putPreference(this,Constants.Keys.CUSTOMER_TOKEN, customer_token);
                        Fn.putPreference(this,Constants.Keys.MOBILE_NO, mobile_no);
                        Fn.putPreference(this,Constants.Keys.NAME, name);
                        Fn.putPreference(this,Constants.Keys.REFERAL_CODE, referal_code);
                        Intent intent1 = new Intent(this, FullActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent1);
                        finish();
                        count++;

                    }
                }
            }else if(errFlag.equals("1")){
                ErrorDialog(Constants.Title.ERROR,errMsg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void ErrorDialog(String Title,String Message){
        Fn.showDialog(this, Title, Message);
    }

}
