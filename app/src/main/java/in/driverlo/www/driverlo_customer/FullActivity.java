package in.driverlo.www.driverlo_customer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

public class FullActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener,AdapterView.OnItemClickListener{

    protected  String TAG = FullActivity.class.getName();
    private DrawerLayout drawerLayout;
    private ListView listView;
    private String[] NavList,TitleList;
    private ActionBarDrawerToggle drawerListener;
    public static FragmentManager fragmentManager;
    private Fragment fragment;
    private int vehicle_type;
    protected int intialConnect = 0;
    protected static int homeFragmentIndentifier = -5;
    protected static int CurrentFrag = 0;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public static GoogleApiClient mGoogleApiClient;
    private String method = "",fragment_title = "";
    // private View header;

    //    public FragmentTransaction transaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fn.logW("FULL_ACTIVITY_LIFECYCLE", "onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full);
        buildGoogleApiClient();
        if((getIntent() != null)&&(getIntent().getExtras() != null)) {
            Bundle bundle = getIntent().getExtras();
            fragment_title = Fn.getValueFromBundle(bundle,"menuFragment");
            method = Fn.getValueFromBundle(bundle, "method");
        }
        int item = 0;
        if (fragment_title.equals("RideDetails")) {
//              Fn.logD("BillDetails_fragement_called",fragment_title);
            item = 7;
        }
        fragmentManager = getSupportFragmentManager();
        getSupportActionBar().setHomeButtonEnabled(true);
        try {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        //TextView user_name = (TextView) header.findViewById(R.id.user_name);
        //user_name.setText("Shubham Agrawal");
        listView = (ListView) findViewById(R.id.nav_menu);
        NavList = getResources().getStringArray(R.array.nav_menu);
        TitleList = getResources().getStringArray(R.array.title_menu);
        NavMenu weather_data[] = new NavMenu[]
        {
                new NavMenu(R.drawable.ic_driver_black, NavList[0]),
                new NavMenu(R.drawable.ic_booking_status_black, NavList[1]),
                new NavMenu(R.drawable.ic_rate_card_black, NavList[2]),
                new NavMenu(R.drawable.ic_support_black, NavList[3]),
                new NavMenu(R.drawable.ic_about_black, NavList[4]),
                new NavMenu(R.drawable.ic_share_black, NavList[5]),
                new NavMenu(R.drawable.ic_logout_black, NavList[6])
        };
        NavMenuAdapter adapter = new NavMenuAdapter(this, R.layout.activity_list_item, weather_data);
        listView.setAdapter(adapter);
        drawerListener = new ActionBarDrawerToggle(this,drawerLayout,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened
//                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
//                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerListener);
        listView.setOnItemClickListener(this);
        if (null == savedInstanceState) {
            selectItem(item);
        }
    }
    protected synchronized void buildGoogleApiClient() {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
    }
    @Override
    public void onAttachFragment(android.support.v4.app.Fragment fragment) {
        Fn.logW("FULL_ACTIVITY_LIFECYCLE", "V4onAttachFragment called");
        super.onAttachFragment(fragment);
    }
    @Override
    public void onAttachFragment(android.app.Fragment fragment) {
        super.onAttachFragment(fragment);
        Fn.logW("FULL_ACTIVITY_LIFECYCLE", "onAttachFragment called");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Fn.logW("FULL_ACTIVITY_LIFECYCLE", "onRestart called");
    }
    @Override
    protected void onStart() {
        super.onStart();
        Fn.logW("FULL_ACTIVITY_LIFECYCLE", "onStart called");
    }
    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        Fn.logW("FULL_ACTIVITY_LIFECYCLE", "onResume called");
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
        Fn.logW("FULL_ACTIVITY_LIFECYCLE", "onPause called");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Fn.logW("FULL_ACTIVITY_LIFECYCLE", "onStop called");
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
        Fn.logW("FULL_ACTIVITY_LIFECYCLE", "onSaveInstanceState called");
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Fn.logW("FULL_ACTIVITY_LIFECYCLE", "onRestoreInstanceState called");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Fn.logW("FULL_ACTIVITY_LIFECYCLE", "onDestroy called");
    }
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        Fn.logW("FULL_ACTIVITY_LIFECYCLE", "onPostCreate called");
        super.onPostCreate(savedInstanceState);
        drawerListener.syncState();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_full, menu);
        MenuItem item = menu.findItem(R.id.location_switch);
        RelativeLayout relativeLayout = (RelativeLayout) MenuItemCompat.getActionView(item);
        ImageView gift_icon = (ImageView) relativeLayout.findViewById(R.id.gift_icon);
        gift_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ReferEarn();
                FragmentManager fragmentManager =FullActivity.fragmentManager;
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                //                Fragment fragment = new BookNow();
                transaction.replace(R.id.main_content, fragment,Constants.Config.CURRENT_FRAG_TAG);
                if((FullActivity.homeFragmentIndentifier == -5)){
                    transaction.addToBackStack(null);
                    FullActivity.homeFragmentIndentifier =  transaction.commit();
                }else{
                    transaction.commit();
                    Fn.logD("fragment instanceof Book","homeidentifier != -1");
                }
                getSupportActionBar().setTitle(getResources().getString(R.string.title_fragment_refer_earn));
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fn.logW("FULL_ACTIVITY_LIFECYCLE", "onOptionsItemSelected called");
        if(drawerListener.onOptionsItemSelected(item)){
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Fn.logW("FULL_ACTIVITY_LIFECYCLE", "onConfigurationChanged called");
        //super.onConfigurationChanged(newConfig);
        drawerListener.onConfigurationChanged(newConfig);
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Fn.logW("FULL_ACTIVITY_LIFECYCLE", "onItemClick called");
//        Toast.makeText(this,planets[position],Toast.LENGTH_LONG).show();
        drawerLayout.closeDrawers();
        if(position==6){
            showLogoutDialog();
        }else{
            selectItem(position);
        }
    }
    protected  void showLogoutDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        //Setting Dialog Title
        alertDialog.setTitle(R.string.LogoutAlertDialogTitle);
        //Setting Dialog Message
        alertDialog.setMessage(R.string.LogoutAlertDialogMessage);
        //On Pressing Setting button
        alertDialog.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        });
        //On pressing cancel button
        alertDialog.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }
    private void shareAppLinkViaFacebook() {
       /* try {
            Intent intent1 = new Intent();
            intent1.setClassName("com.facebook.katana", "com.facebook.katana.activity.composer.ImplicitShareIntentHandler");
            intent1.setAction("android.intent.action.SEND");
            intent1.setType("text/plain");
            intent1.putExtra("android.intent.extra.TEXT", urlToShare);
            startActivity(intent1);
        } catch (Exception e) {
            // If we failed (not native FB app installed), try share through SEND
            Intent intent = new Intent(Intent.ACTION_SEND);
            String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + urlToShare;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
            startActivity(intent);
        }*/
    }
    protected void logout(){
        Fn.SystemPrintLn("**Before logout: "+Fn.getPreference(this,Constants.Keys.CUSTOMER_TOKEN));
        Fn.putPreference(this, Constants.Keys.CUSTOMER_TOKEN, "defaultStringIfNothingFound");
        Fn.putPreference(this, Constants.Keys.MOBILE_NO, null);
        Fn.putPreference(this, Constants.Keys.NAME, null);
        Fn.putPreference(this, Constants.Keys.REFERAL_CODE, null);
        Fn.SystemPrintLn("##After logout: " + Fn.getPreference(this, "user_token"));
        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag(Constants.Config.CURRENT_FRAG_TAG)).commit();
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    public void selectItem(int position){
        setTitle(position);
        CurrentFrag = position;
        Fn.logW("FULL_ACTIVITY_LIFECYCLE", "selectItem called");
        // listView.setItemChecked(position,true);
        //FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();// For AppCompat use getSupportFragmentManager
        switch(position){
            case 0:
                fragment = new HireDriver();
                break;
            case 1:
                fragment = new RideHistory();
                break;
            case 2:
                fragment = new FareChart();
                break;
            case 3:
                fragment = new ContactUs();
                break;
            case 4:
                fragment = new Terms();
                break;
            case 5:
                fragment = new ReferEarn();
                break;
            case 7:
                fragment = new RideDetails();
                break;
            default:
                fragment = new HireDriver();
                break;
        }
        transaction.replace(R.id.main_content, fragment, Constants.Config.CURRENT_FRAG_TAG);
        if((homeFragmentIndentifier == -5)&&(!(fragment instanceof  HireDriver))){
            Fn.logD("fragment instanceof Book","called with homeFragmentIndentifier = "+String.valueOf(homeFragmentIndentifier));
            if(method.equals("push")) {
                transaction.commit();
                method = "";
            }else{
                transaction.addToBackStack(null);
                homeFragmentIndentifier =  transaction.commit();
            }
            Fn.logD("homeFragmentIndentifier value",String.valueOf(homeFragmentIndentifier));
        }else{
            transaction.commit();
            Fn.logD("fragment instanceof Book","homeidentifier != -1");
        }

    }
    @Override
    public void onBackPressed() {
        Fn.logW("FULL_ACTIVITY_LIFECYCLE", "onBackPressed called");
        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag(Constants.Config.CURRENT_FRAG_TAG)).commit();
        super.onBackPressed();
        if(homeFragmentIndentifier != -5) {
            getSupportActionBar().setTitle(R.string.title_hire_driver_fragment);
            Fn.logD("homeFragmentIndentifier", String.valueOf(homeFragmentIndentifier));
            fragmentManager.popBackStack(homeFragmentIndentifier, 0);
        }
        homeFragmentIndentifier = -5;
    }
    public void setTitle(int position){
        Fn.logW("FULL_ACTIVITY_LIFECYCLE", "setTitle called");
        getSupportActionBar().setTitle(TitleList[position]);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Fn.logW("FULL_ACTIVITY_LIFECYCLE", "onConnected called");
        if((fragment instanceof HireDriver)&&(intialConnect == 0)) {
            selectItem(0);
            intialConnect++;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }
    @Override
    public void onLocationChanged(Location location) {

//        }
    }

}
