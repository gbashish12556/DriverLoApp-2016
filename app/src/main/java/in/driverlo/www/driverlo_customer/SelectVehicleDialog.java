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
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * Created by Ashish on 11/8/2016.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SelectVehicleDialog extends DialogFragment implements View.OnClickListener{

    android.app.AlertDialog dialog;
    AlertDialog.Builder alertDialog;
    View dialogView;
    RadioButton hatchack,sedan,suv,luxury,manual,automatic;
    int vehicle_type = 0;
    int vehicle_mode = 0;
    Button cancel_button, next_button;
    String[] vehicle_type_list, vehicle_mode_list;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(getActivity() != null) {
            alertDialog = new AlertDialog.Builder(getActivity());
            View view = getActivity().getLayoutInflater().inflate(R.layout.select_vehicle_dialog_fragment, null);
            hatchack = (RadioButton) view.findViewById(R.id.hatchback);
            sedan = (RadioButton) view.findViewById(R.id.sedan);
            suv = (RadioButton) view.findViewById(R.id.suv);
            luxury = (RadioButton) view.findViewById(R.id.luxury);
            manual = (RadioButton) view.findViewById(R.id.manual);
            automatic = (RadioButton) view.findViewById(R.id.automatic);
            cancel_button = (Button) view.findViewById(R.id.cancel_button);
            next_button = (Button) view.findViewById(R.id.next_button);
            hatchack.setOnClickListener(this);
            sedan.setOnClickListener(this);
            suv.setOnClickListener(this);
            luxury.setOnClickListener(this);
            manual.setOnClickListener(this);
            automatic.setOnClickListener(this);

            dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_title_view, null);
            TextView title = (TextView) dialogView.findViewById(R.id.dialog_title);
            title.setText("SELECT CAR TYPE");
            alertDialog.setView(view);
            alertDialog.setCancelable(false);
            vehicle_type_list = getResources().getStringArray(R.array.vehicle_type);
            vehicle_mode_list = getResources().getStringArray(R.array.vehicle_mode);
            next_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fn.putPreference(getActivity(), Constants.Keys.SELECTED_VEHICLE_TYPE, vehicle_type_list[vehicle_type]);
                    Fn.putPreference(getActivity(), Constants.Keys.SELECTED_VEHICLE_MODE, vehicle_mode_list[vehicle_mode]);
                    String journey_type = Fn.getPreference(getActivity(),Constants.Keys.JOURNEY_TYPE);
                    String[] journey_type_list = getResources().getStringArray(R.array.journey_type);
                    if(journey_type.equals(journey_type_list[2])){
                        Fn.SystemPrintLn("journey_type"+journey_type);
                        EstimatedDayDialog  estimateDay = new EstimatedDayDialog();
                        estimateDay.show(getActivity().getFragmentManager(), "ABC");

                    }else{
                        Fn.SystemPrintLn("journey_type"+journey_type);
                        EstimatedTimeDialog  estimateTime = new EstimatedTimeDialog();
                        estimateTime.show(getActivity().getFragmentManager(), "ABC");
                    }
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

            alertDialog.setOnKeyListener(new Dialog.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface arg0, int keyCode,
                                     KeyEvent event) {
                    // TODO Auto-generated method stub
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
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

    @Override
    public void onClick(View v) {
        hatchack.setChecked(false);
        sedan.setChecked(false);
        luxury.setChecked(false);
        suv.setChecked(false);

        boolean checked = ((RadioButton) v).isChecked();
        // Check which radio button was clicked
        switch(v.getId()) {
            case R.id.hatchback:
                if (checked){
                    vehicle_type = 0;
                }
                break;
            case R.id.sedan:
                if (checked){
                    vehicle_type = 1;
                }
                break;
            case R.id.suv:
                if (checked){
                    vehicle_type = 2;
                }
                break;
            case R.id.luxury:
                if (checked){
                    vehicle_type = 3;
                }
                break;
            case R.id.manual:
                if (checked){
                    vehicle_mode = 0;
                }
                break;
            case R.id.automatic:
                if (checked){
                    vehicle_mode = 1;
                }
                break;
        }
    }
}
