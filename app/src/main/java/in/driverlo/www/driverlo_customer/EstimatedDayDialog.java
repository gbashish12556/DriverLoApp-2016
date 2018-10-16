package in.driverlo.www.driverlo_customer;

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

/**
 * Created by Ashish on 11/18/2016.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class EstimatedDayDialog extends DialogFragment {

    Dialog dialog;
    AlertDialog.Builder alertDialog;
    NumberPicker day_picker, hour_picker;
    Button cancel_button, next_button;
    int booking_day = 0, booking_hour = 0;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(getActivity() != null) {
            alertDialog = new AlertDialog.Builder(getActivity());
            View view = getActivity().getLayoutInflater().inflate(R.layout.estimated_day_dialog_fragment, null);
            cancel_button = (Button) view.findViewById(R.id.cancel_button);
            next_button = (Button) view.findViewById(R.id.next_button);
            day_picker = (NumberPicker) view.findViewById(R.id.day_picker);
            hour_picker = (NumberPicker) view.findViewById(R.id.hour_picker);
            day_picker.setMinValue(0);
            day_picker.setMaxValue(8);
            hour_picker.setMinValue(0);
            hour_picker.setMaxValue(1);
            day_picker.setWrapSelectorWheel(true);
            hour_picker.setWrapSelectorWheel(true);
            String[] hourValues = new String[2];
            Fn.SystemPrintLn("minute_array_length"+String.valueOf(hourValues.length));
            for (int i = 0; i < hourValues.length; i++) {
                String number = Integer.toString(i*Constants.Config.HOUR_PICKER_SLAB);
                hourValues[i] = number.length() < 2 ? "0" + number : number;
            }
            hour_picker.setDisplayedValues(hourValues);
            alertDialog.setView(view);
            alertDialog.setCancelable(false);
            next_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    booking_day = day_picker.getValue();
                    booking_hour = hour_picker.getValue()*Constants.Config.HOUR_PICKER_SLAB;
                    Fn.SystemPrintLn("Booking Hour"+String.valueOf(booking_hour));
                    Fn.putPreference(getActivity(), Constants.Keys.BOOKING_DAY, String.valueOf(booking_day));
                    Fn.putPreference(getActivity(), Constants.Keys.BOOKING_HOUR, String.valueOf(booking_hour));
                    ConfirmBookingDialog  requestDriver = new ConfirmBookingDialog();
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
