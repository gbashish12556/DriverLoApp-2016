package in.driverlo.www.driverlo_customer;

/**
 * Created by Ashish on 10/20/2016.
 */

public class Constants {
        public static final class Config {
//      public static final String ROOT_PATH = "http://driverlo.in/driverlo_mobile/";
//      public static final String ROOT_PATH = "http://192.168.0.105/driverlo_mobile/";
        public static final String ROOT_PATH = "http://192.168.1.82/driverlo_mobile/";
//            public static final String ROOT_PATH = "http://192.168.122.1/driverlo_mobile/";
//      public static final String ROOT_PATH = "http://192.168.100.103/driverlo_mobile/";
//      public static final String ROOT_PATH = "http://192.168.0.108/driverlo_mobile/";
//        public static final String ROOT_PATH = "http://192.168.0.181/driverlo_mobile/";
//        public static final String ROOT_PATH = "http://192.168.122.1/driverlo_mobile/";
//      public static final String ROOT_PATH = "http://192.168.0.8/Driverlo.in/driverlo_mobile/";
//      public static final String ROOT_PATH = "http://192.168.1.102/Driverlo.in/driverlo_mobile/";
//      public static final String ROOT_PATH = "http://192.168.43.177/Driverlo.in/driverlo_mobile/";
//        public static final String ROOT_PATH = "http://192.168.43.176/Driverlo.in/driverlo_mobile/";
        protected static final int UPDATE_CUSTOMER_LOCATION_DELAY = 0*10000;
        protected static final int UPDATE_CUSTOMER_LOCATION_PERIOD = 30*1000;
        protected static final int GET_DRIVER_LOCATION_DELAY = 0*10000;
        protected static final int GET_DRIVER_LOCATION_PERIOD = 30*1000;
        protected static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
        protected static final long MIN_TIME_BW_UPDATES = 0;
        protected static final long MIN_DATE_DURATION = 1*1000;
        protected static final long MAX_DATE_DURATION = 3*24*60*60*1000;
        protected static final String SUPPORT_CONTACT1 = "(033)-26655544";
        protected static final String SUPPORT_CONTACT2 = "08276903952";
            protected static final String SUPPORT_EMAIL = "Driverlo.in@gmail.com";
        protected static final int NAME_FIELD_LENGTH = 50;
        protected static final int ADDRESS_FIELD_LENGTH = 50;
        protected static final int DELAY_AFTER_TOAST = 10;
        protected static final int DELAY_LOCATION_CHECK = 0*100;
        protected static final int PERIOD_LOCATION_CHECK  = 2*100;
        protected static final float MAP_HIGH_ZOOM_LEVEL = 17;
        protected static final float MAP_MID_ZOOM_LEVEL = 15;
        protected static final float MAP_SMALL_ZOOM_LEVEL = 13;
        protected static final int IMAGE_WIDTH = 250;
        protected static final int IMAGE_HEIGHT = 250;
        protected static final String CURRENT_FRAG_TAG = "current_fragment";
        protected static final int FLASH_TO_MAIN_DELAY = 3*1000;
        protected static final int GPS_INTERVAL = 2*1000;
        protected static final int GPS_FASTEST_INTERVAL = 1*1000;
        protected static final int PROGRESSBAR_DELAY = 2*1000;
        protected static final int BOOK_LATER_DELAY = 15*60*1000;
        protected static final int MINUTE_PICKER_SLAB = 5;
        protected static final int HOUR_PICKER_SLAB = 12;
        protected static final double DEFAULT_CURRENT_LAT = 22.5851;
        protected static final double DEFAULT_CURRENT_LNG  = 88.3468;
            protected static final int SERVICE_RADIUS = 2500*1000;
            protected static final String APP_LINK  = "https://play.google.com/store/apps/details?id=in.driverlo.www.driverlo_customer";

    }
    public static final class Message{
        protected static final String NEW_USER_ENTER_DETAILS = "Please enter your details";
        protected static final String NO_CURRENT_BOOKING = "No Current Booking";
        protected static final String VEHICLE_ALLOCATION_PENDING = "Vehicle Allocation Pending";
        protected static final String DRIVER_FOUND = "Driver Found";
        protected static final String NETWORK_ERROR = "Unable to connect to server.Check your Internet Connection";
        protected static final String SERVER_ERROR = "Server not responding to request";
        protected static final String GPS_NOT_ENABLED = "GPS not enabled !!";
        protected static final String INTERNET_NOT_ENABLED = "Internet not enabled!!";
        protected static final String CONNECTING = "Connecting...";
        protected static final String LOADING = "Loading...";
        protected static final String OTP_VERIFICATION_ERROR = "OTP could not be verified";
        protected static final String FORM_ERROR = "Form contains error";
        protected static final String TRACKING_ERROR = "Error while updating location";
        protected static final String INVALID_DATETIME = "Form Contains Invalid Date Field";
        protected static final String INVALID_ADDRESS = "Form Contains Invalid Address Field(s)";
        protected static final String EMPTY_IMAGE = "Item Image Required";
        protected static final String PASSWORD_SENT = "Password sent to entered mobile no";
        protected static final String NO_SERVICE_AREA = "Sorry we do not provide service in this area !";
        protected static final String SHOULD_OUT_SERVICE_AREA = "Destination should be out of service area !";
    }
    public static final class Title{
        protected static final String ERROR = "ERROR";
        protected static final String NETWORK_ERROR = "NETWORK ERROR";
        protected static final String SERVER_ERROR = "SERVER ERROR";
        protected static final String OTP_VERIFICATION_ERROR = "VERIFICATION ERROR";
        protected static final String NO_SERVICE = "NO SERVICE AVAILABLE";
        protected static final String BOOKING_DATETIME = "SELECT BOOKING DATE TIME";
        protected static final String PASSWORD_SENT = "PASSWORD REGENERATED";

    }
    public static final class Keys{
        protected static final String VEHICLETYPE_ID = "vehicletype_id";
        protected static final String SELECTED_VEHICLETYPE = "selected_vehicletype";
        protected static final String CUSTOMER_TOKEN = "customer_token";
        protected static final String BRN_NO = "brn_no";
        protected static final String MOBILE_NO = "mobile_no";
        protected static final String NAME = "name";
        protected static final String REFERAL_CODE = "referal_code";
        protected static final String TOTAL_POINT = "total_point";
        protected static final String USER_ID = "user_id";
        protected static final String CITY_ID = "city_id";
        protected static final String EXACT_PICKUP_POINT = "exact_pickup_point";
        protected static final String EXACT_DROPOFF_POINT = "exact_dropoff_point";
        protected static final String ERR_MSG = "errMsg";
        protected static final String ERR_FLAG = "errFlag";
        protected static final String LIKES = "likes";
        protected static final String LATER_BOOKING_DATETIME = "later_booking_datetime";
        protected static final String MY_CURRENT_ADDRESS = "my_current_address";
        protected static final String SELECTED_VEHICLE_TYPE = "selected_vehicle_type";
        protected static final String SELECTED_VEHICLE_MODE = "selected_vehicle_mode";
        protected static final String SELECTED_CITY_ID = "selected_city_id";
        protected static final String BOOKING_DAY = "booking_day";
        protected static final String BOOKING_HOUR = "booking_hour";
        protected static final String BOOKING_MINUTE = "booking_minute";
        protected static final String PICKUP_ADDRESS = "pickup_address";
        protected static final String DROPOFF_ADDRESS = "dropoff_address";
        protected static final String JOURNEY_TYPE = "journey_type";
    }
}
