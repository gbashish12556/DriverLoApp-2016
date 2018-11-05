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
import android.support.v7.app.AppCompatActivity;
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

import in.driverlo.www.driverlo_customer.Constants;
import in.driverlo.www.driverlo_customer.Helper;
import in.driverlo.www.driverlo_customer.Utils.FormValidation;
import in.driverlo.www.driverlo_customer.Activities.CompleteActivity;
import in.driverlo.www.driverlo_customer.R;
import in.driverlo.www.driverlo_customer.Fragments.ReferEarn;

/**
 * Created by Shubham on 14/07/2016.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ApplyReferalCode extends DialogFragment implements View.OnClickListener {

    public RequestQueue requestQueue;
    private String TAG = ApplyReferalCode.class.getName();
    private EditText referal_code;
    private Button submitButton;
    private String feedback;
    private AlertDialog.Builder b;
    private Dialog rd;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        b = new AlertDialog.Builder(getActivity());
        b.setTitle("Apply Referal Code");
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_apply_referal_code, null);
        b.setView(v);
        b.setCancelable(false);
        referal_code = (EditText) v.findViewById(R.id.referal_code);
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
                String referal_code_text= referal_code.getText().toString();
                HashMap<String, String> hashMap = new HashMap<String, String>();
                String rating_url = Constants.Config.ROOT_PATH + "apply_referal_code";
                hashMap.put("referal_code", referal_code_text);
                hashMap.put(Constants.Keys.CUSTOMER_TOKEN, Helper.getPreference(getActivity(), Constants.Keys.CUSTOMER_TOKEN));
                sendVolleyRequest(rating_url, Helper.checkParams(hashMap));
            } else {
                Helper.Toast(getActivity(), Constants.Message.FORM_ERROR);
            }
        }
    }
    private boolean checkValidation() {
        boolean ret = true;
        if(!FormValidation.isRequired(referal_code, Constants.Config.NAME_FIELD_LENGTH)) ret = false;
        return ret;
    }
    public void sendVolleyRequest(String URL, final HashMap<String, String> hMap) {
        if(getActivity() !=  null) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    ConfirmBookingSuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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

    public void ConfirmBookingSuccess(String response) {
        if(getActivity() !=  null) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String errFlag = jsonObject.getString("errFlag");
                String errMsg = jsonObject.getString("errMsg");
                if(errFlag.equals("0")){
                    Fragment fragment = new ReferEarn();
                    FragmentManager fragmentManager =CompleteActivity.fragmentManager;
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    //                Fragment fragment = new BookNow();
                    transaction.replace(R.id.main_content, fragment,Constants.Config.CURRENT_FRAG_TAG);
                    if((CompleteActivity.homeFragmentIndentifier == -5)){
                        transaction.addToBackStack(null);
                        CompleteActivity.homeFragmentIndentifier =  transaction.commit();
                    }else{
                        transaction.commit();
                    }
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Ride Details");
                    Helper.ToastShort(getActivity(),errMsg);
                    rd.dismiss();
//                    Helper.Toast(this,errMsg);
                }else if(errFlag.equals("1")){
                    ErrorDialog(Constants.Title.SERVER_ERROR,errMsg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
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
    private void ErrorDialog(String Title, String Message){
        if(getActivity() != null) {
            Helper.showDialog(getActivity(), Title, Message);
        }
    }
}