package in.driverlo.www.driverlo_customer.DialogFragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import in.driverlo.www.driverlo_customer.Constants;
import in.driverlo.www.driverlo_customer.Helper;
import in.driverlo.www.driverlo_customer.R;

/**
 * Created by Ashish on 7/21/2016.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DatePickerDialog extends DialogFragment {

    Dialog dialog;
    DatePicker datePicker;
    TimePicker timePicker;
    AlertDialog.Builder alertDialog;
    Button cancel_button, next_button;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(getActivity() != null) {
            alertDialog = new AlertDialog.Builder(getActivity());
            View view = getActivity().getLayoutInflater().inflate(R.layout.datetime_picker_dialog_fragment, null);
            cancel_button = (Button) view.findViewById(R.id.cancel_button);
            next_button = (Button) view.findViewById(R.id.next_button);
            alertDialog.setView(view);
            alertDialog.setCancelable(false);
//            alertDialog.setTitle(Constants.Title.BOOKING_DATETIME);
            datePicker = (DatePicker) view.findViewById(R.id.date_picker);
            timePicker = (TimePicker) view.findViewById(R.id.time_picker);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                datePicker.setMinDate(System.currentTimeMillis() - Constants.Config.MIN_DATE_DURATION);
                datePicker.setMaxDate((System.currentTimeMillis() + Constants.Config.MAX_DATE_DURATION));
            }
            next_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                            datePicker.getMonth(),
                            datePicker.getDayOfMonth(),
                            timePicker.getCurrentHour(),
                            timePicker.getCurrentMinute());
                    long datetime = calendar.getTimeInMillis();
                    long currentTime = Helper.getDateTimeNowMillis();
                    currentTime = currentTime + Constants.Config.BOOK_LATER_DELAY;
                    if ((datetime >= currentTime) && (datetime > 0)) {
                        Helper.putPreference(getActivity(), Constants.Keys.LATER_BOOKING_DATETIME, Helper.getDate(datetime));
                        Helper.SystemPrintLn(Helper.getPreference(getActivity(), Constants.Keys.LATER_BOOKING_DATETIME));
                        if(Helper.isTimeNight(Helper.getPreference(getActivity(), Constants.Keys.LATER_BOOKING_DATETIME), 0, 0)){
                            Helper.SystemPrintLn("this is night time");
                        }else{
                            Helper.SystemPrintLn("this is day");
                        }
                        SelectVehicleDialog selectVehicle = new SelectVehicleDialog();
                        selectVehicle.show(getActivity().getFragmentManager(), "ABC");
                        dialog.dismiss();
                    } else {
//                    show(getActivity().getFragmentManager(),"ABC");
                        Helper.ToastShort(getActivity(), Constants.Message.INVALID_DATETIME);
                    }
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
                public boolean onKey(DialogInterface arg0, int keyCode,
                                     KeyEvent event) {
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

    public void show(FragmentManager childFragmentManager, String abc) {
        dialog.show();
    }
}
