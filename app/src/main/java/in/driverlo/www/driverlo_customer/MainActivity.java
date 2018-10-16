package in.driverlo.www.driverlo_customer;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import in.driverlo.www.driverlo_customer.ForgotPassword;

public class MainActivity extends AppCompatActivity {

    protected RequestQueue requestQueue;
    private String TAG = MainActivity.class.getName();
    TextView new_user, forgot_password, hide_show;
    EditText login_mobile_no, login_password;
    Button login_submit_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login_mobile_no = (EditText) findViewById(R.id.login_mobile_no);
        login_password = (EditText) findViewById(R.id.login_password);
        login_submit_button = (Button) findViewById(R.id.login_submit_button);
        new_user = (TextView) findViewById(R.id.new_user);
        forgot_password = (TextView) findViewById(R.id.forgot_password);
        hide_show = (TextView) findViewById(R.id.hide_show);
        hide_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = hide_show.getText().toString().trim();
                if(status.equals(getResources().getString(R.string.hide))) {
                    login_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    hide_show.setText(getResources().getString(R.string.show));
                }else if(status.equals(getResources().getString(R.string.show))){
                    login_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    hide_show.setText(getResources().getString(R.string.hide));
                }
            }
        });
        new_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleForgotPassword();
            }
        });
        login_submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidation()) {
                    String mobile_no = login_mobile_no.getText().toString();
                    String password = login_password.getText().toString();
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    String rating_url = Constants.Config.ROOT_PATH + "customer_login";
                    Fn.logD("rating_url", rating_url);
                    hashMap.put("login_mobile_no", mobile_no);
                    hashMap.put("login_password", password);
                    sendVolleyRequest(rating_url, Fn.checkParams(hashMap));
                }
            }
        });
    }
    private boolean checkValidation() {
        boolean ret = true;
        if (!FormValidation.isPhoneNumber(login_mobile_no, true)) ret = false;
        if(!FormValidation.isRequired(login_password,Constants.Config.ADDRESS_FIELD_LENGTH)) ret = false;
        return ret;
    }
    protected void handleForgotPassword(){
        ForgotPassword rd = new ForgotPassword();
        rd.show(this.getFragmentManager(), "ABC");
    }
    public void sendVolleyRequest(String URL, final HashMap<String, String> hMap) {
        if(this !=  null) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Fn.SystemPrintLn(response);
                    LoginSuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Fn.SystemPrintLn(error);
                    ErrorDialog(Constants.Title.NETWORK_ERROR, Constants.Message.NETWORK_ERROR);
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

    protected void LoginSuccess(String response) {
        if(this !=  null) {
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
    }
    private void ErrorDialog(String Title, String Message){
        Fn.showDialog(this, Title, Message);
    }
}
