package in.driverlo.www.driverlo_customer;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmBooking extends Fragment {

    View view;
    String pickup_address,dropoffaddress, journey_type, vehicle_type,
            vehicle_mode,booking_datetime, booking_hour_string,booking_minute_string,city_id;
    int booking_hour, booking_minute,total_minute;
    double estimated_fare;
    public ConfirmBooking() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (container == null) {
            return null;
        } else {
            view = inflater.inflate(R.layout.fragment_confirm_booking, container, false);
            return view;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getActivity() != null) {
            pickup_address = Fn.getPreference(getActivity(), Constants.Keys.PICKUP_ADDRESS);
            dropoffaddress = Fn.getPreference(getActivity(), Constants.Keys.DROPOFF_ADDRESS);
            journey_type = Fn.getPreference(getActivity(), Constants.Keys.JOURNEY_TYPE);
            pickup_address = Fn.getPreference(getActivity(), Constants.Keys.PICKUP_ADDRESS);
            vehicle_type = Fn.getPreference(getActivity(), Constants.Keys.SELECTED_VEHICLE_TYPE);
            vehicle_mode = Fn.getPreference(getActivity(), Constants.Keys.SELECTED_VEHICLE_MODE);
            city_id =     Fn.getPreference(getActivity(), Constants.Keys.SELECTED_CITY_ID);
            booking_datetime = Fn.getPreference(getActivity(), Constants.Keys.LATER_BOOKING_DATETIME);
            Fn.SystemPrintLn("booking_datetime"+booking_datetime);
            booking_hour_string = Fn.getPreference(getActivity(), Constants.Keys.BOOKING_HOUR);
            booking_minute_string = Fn.getPreference(getActivity(), Constants.Keys.BOOKING_MINUTE);
            booking_hour = Integer.parseInt(booking_hour_string);
            booking_minute = Integer.parseInt(booking_minute_string);
            if(booking_hour>0){
                //
            }
            if(Fn.isTimeNight(booking_datetime, booking_hour, booking_minute)){
                Fn.SystemPrintLn("this is night");
            }else{
                Fn.SystemPrintLn("this is day");
            }
            if(booking_hour>0){
                total_minute = (booking_hour-1)*60+booking_minute;
            }else{
                total_minute = booking_minute;
            }
        }

    }
}
