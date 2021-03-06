package in.driverlo.www.driverlo_customer.DialogFragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import java.util.Arrays;

import in.driverlo.www.driverlo_customer.Constants;
import in.driverlo.www.driverlo_customer.Helper;
import in.driverlo.www.driverlo_customer.Fragments.ConfirmBookingDialog;
import in.driverlo.www.driverlo_customer.R;

/**
 * Created by Ashish on 11/8/2016.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class EstimatedTimeDialog  extends DialogFragment {

    Dialog dialog;
    AlertDialog.Builder alertDialog;
    NumberPicker hour_picker, minute_picker;
    Button cancel_button, next_button;
    int booking_hour = 0, booking_minute = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(getActivity() != null) {
            alertDialog = new AlertDialog.Builder(getActivity());
            View view = getActivity().getLayoutInflater().inflate(R.layout.estimated_time_dialog_fragment, null);
            cancel_button = (Button) view.findViewById(R.id.cancel_button);
            next_button = (Button) view.findViewById(R.id.next_button);
            hour_picker = (NumberPicker) view.findViewById(R.id.hour_picker);
            minute_picker = (NumberPicker) view.findViewById(R.id.minute_picker);
            hour_picker.setMaxValue(8);
            hour_picker.setMinValue(0);
            hour_picker.setWrapSelectorWheel(true);
            minute_picker.setMaxValue(11);
            minute_picker.setMinValue(0);
            minute_picker.setWrapSelectorWheel(true);
            String[] minuteValues = new String[12];
            for (int i = 0; i < minuteValues.length; i++) {
                String number = Integer.toString(i*Constants.Config.MINUTE_PICKER_SLAB);
                minuteValues[i] = number.length() < 2 ? "0" + number : number;
            }
            Helper.SystemPrintLn(Arrays.toString(minuteValues));
            minute_picker.setDisplayedValues(minuteValues);
            alertDialog.setView(view);
            alertDialog.setCancelable(false);
            next_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    booking_hour = hour_picker.getValue();
                    booking_minute = minute_picker.getValue()*Constants.Config.MINUTE_PICKER_SLAB;
                    Helper.putPreference(getActivity(), Constants.Keys.BOOKING_HOUR, String.valueOf(booking_hour));
                    Helper.putPreference(getActivity(), Constants.Keys.BOOKING_MINUTE, String.valueOf(booking_minute));
                    if(Helper.isTimeNight(Helper.getPreference(getActivity(), Constants.Keys.LATER_BOOKING_DATETIME), booking_hour, booking_minute)){
                        Helper.SystemPrintLn("this is night time");
                    }else{
                        Helper.SystemPrintLn("this is day");
                    }
                    ConfirmBookingDialog requestDriver = new ConfirmBookingDialog();
                    requestDriver.show(getActivity().getFragmentManager(), "ABC");
                    dialog.dismiss();
                }
            });
            cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            alertDialog.setOnKeyListener(new Dialog.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                    // TODO Auto-generated method stub
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        //alertDialog.dismiss();
//                    ((MainActivity)context).moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                    return true;
                }
            });
        }
        dialog = alertDialog.create();
        return dialog;
    }
}
