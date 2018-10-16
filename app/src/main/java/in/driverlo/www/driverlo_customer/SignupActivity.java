package in.driverlo.www.driverlo_customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Random;

public class SignupActivity extends AppCompatActivity {

    protected RequestQueue requestQueue;
    private String TAG = SignupActivity.class.getName();
    TextView hide_show, hide_show_re;
    EditText signup_name,signup_email, signup_mobile_no, signup_password, confirm_signup_password, referal_code;
    Button signup_submit_button;
    String password,name,email,mobile_no, referal_code_text;
    private int otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signup_name = (EditText) findViewById(R.id.signup_name);
        signup_email = (EditText) findViewById(R.id.signup_email);
        signup_mobile_no = (EditText) findViewById(R.id.signup_mobile_no);
        referal_code = (EditText) findViewById(R.id.referal_code);
        signup_password = (EditText) findViewById(R.id.signup_password);
        password = signup_password.getText().toString();
        confirm_signup_password = (EditText) findViewById(R.id.confirm_signup_password);
        signup_submit_button = (Button) findViewById(R.id.signup_submit_button);
        hide_show = (TextView) findViewById(R.id.hide_show);
        hide_show_re = (TextView) findViewById(R.id.hide_show_re);
        signup_submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GenerateOtp();
            }
        });
        hide_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = hide_show.getText().toString().trim();
                if(status.equals(getResources().getString(R.string.hide))) {
                    signup_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    hide_show.setText(getResources().getString(R.string.show));
                }else if(status.equals(getResources().getString(R.string.show))){
                    signup_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    hide_show.setText(getResources().getString(R.string.hide));
                }
            }
        });
        hide_show_re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = hide_show_re.getText().toString().trim();
                if(status.equals(getResources().getString(R.string.hide))) {
                    confirm_signup_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    hide_show_re.setText(getResources().getString(R.string.show));
                }else if(status.equals(getResources().getString(R.string.show))){
                    confirm_signup_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    hide_show_re.setText(getResources().getString(R.string.hide));
                }
            }
        });
    }
    public void GenerateOtp(){
        if(checkValidation()) {
             name = signup_name.getText().toString();
             email = signup_email.getText().toString();
             mobile_no = signup_mobile_no.getText().toString();
             referal_code_text = referal_code.getText().toString();
             password = signup_password.getText().toString().trim();
            String confirm_password = confirm_signup_password.getText().toString().trim();
            if(confirm_password.equals(password)) {
                String otp_url = Constants.Config.ROOT_PATH + "gen_registration_otp";
                Fn.logD("otp_url",otp_url);
                Random ran = new Random();
                otp = (100000 + ran.nextInt(900000));
                HashMap<String,String> hashMap = new HashMap<String, String>();
                hashMap.put("signup_email", email);
                hashMap.put("signup_mobile_no", mobile_no);
                hashMap.put("OTP", String.valueOf(otp));
                sendVolleyRequest(otp_url, Fn.checkParams(hashMap));
            }else{
                confirm_signup_password.setError("Please enter same password again");
            }
        }else {
            Fn.ToastShort(this, Constants.Message.FORM_ERROR);
        }
    }

    private boolean checkValidation() {
        boolean ret = true;
        if (!FormValidation.isEmailAddress(signup_email, true)) ret = false;
        if (!FormValidation.isPhoneNumber(signup_mobile_no, true)) ret = false;
        if(!FormValidation.isRequired(signup_name, Constants.Config.NAME_FIELD_LENGTH)) ret = false;
        if(!FormValidation.isRequired(signup_password,Constants.Config.ADDRESS_FIELD_LENGTH)) ret = false;
        if(!FormValidation.isRequired(confirm_signup_password,Constants.Config.ADDRESS_FIELD_LENGTH)) ret = false;
        return ret;
    }
    public void sendVolleyRequest(String URL, final HashMap<String, String> hMap) {
        if(this !=  null) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String trimmed_response = response.substring(response.indexOf("{"));
                    OtpGenerateSuccess(trimmed_response);
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
            Fn.addToRequestQue(requestQueue, stringRequest, this);
        }
    }
    public void OtpGenerateSuccess(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            String errFlag = jsonObject.getString("errFlag");
            String errMsg = jsonObject.getString("errMsg");
            if(errFlag.equals("0")){
                Fn.ToastShort(this, errMsg);
                Intent intent = new Intent(this, OtpVerification.class);
                intent.putExtra("signup_name", name);
                intent.putExtra("signup_email", email);
                intent.putExtra("signup_mobile_no", mobile_no);
                intent.putExtra("signup_password", password);
                intent.putExtra("OTP", otp);
                intent.putExtra(Constants.Keys.REFERAL_CODE, referal_code_text);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
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
