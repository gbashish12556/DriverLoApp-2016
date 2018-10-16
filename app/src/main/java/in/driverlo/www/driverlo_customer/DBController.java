package in.driverlo.www.driverlo_customer;

/**
 * Created by root on 8/5/16.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.util.HashMap;

public class DBController extends SQLiteOpenHelper {


    protected static final String ID = BaseColumns._ID;
    protected static final String TABLE_VIEW_FARE_CHART = "view_fare_chart";
    protected static final String CITY_ID = "city_id";
    protected static final String CITY_NAME = "city_name";
    protected static final String CITY_LAT = "city_lat";
    protected static final String CITY_LNG = "city_lng";
    protected static final String BASE_FARE = "base_fare";
    protected static final String FARE = "fare";
    protected static final String NIGHT_CHARGE = "night_charge";
    protected static final String RETURN_CHARGE = "return_charge";
    protected static final String OUTSTATION_BASE_FARE = "outstation_base_fare";
    protected static final String OUTSTATION_FARE = "outstation_fare";
    protected static final String UPDATE_DATE = "update_date";
    protected static final String IS_ACTIVE = "is_active";

    private static final String CREATE_TABLE_VIEW_FARE_CHART  = "CREATE TABLE " + TABLE_VIEW_FARE_CHART
            + "(" + ID+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + CITY_ID + " INTEGER,"
            + CITY_NAME + " TEXT,"
            + CITY_LAT + " REAL,"
            + CITY_LNG + " REAL,"
            + BASE_FARE + " INTEGER,"
            + FARE + " INTEGER,"
            + NIGHT_CHARGE + " INTEGER,"
            + RETURN_CHARGE + " INTEGER,"
            + OUTSTATION_BASE_FARE + " INTEGER,"
            + OUTSTATION_FARE + " INTEGER,"
            + IS_ACTIVE + " INTEGER CHECK ( "
            + IS_ACTIVE +" IN (0,1) ),"
            + UPDATE_DATE + " TEXT" + ")";

    private static final String DELETE = "DELETE FROM ";
    private static final String DELETE_VIEW_FARE_CHART = "DROP TABLE IF EXISTS " + TABLE_VIEW_FARE_CHART;

    public DBController(Context context) {
        super(context, "DriverLo.db", null, 1);
    }
    //Creates Table
    @Override
    public void onCreate(SQLiteDatabase database) {
        Fn.logD("DBController_onCreate", "onCreate");
        try {
            database.execSQL(CREATE_TABLE_VIEW_FARE_CHART);
//            database.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
        Fn.logD("DBController_onUpgrade","onUpgrade");
        try {
            database.execSQL(DELETE_VIEW_FARE_CHART);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        onCreate(database);
    }
    public void createTable()
    {
        Fn.logD("DBController_createTable","createTable");
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            database.execSQL(CREATE_TABLE_VIEW_FARE_CHART);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deleteTable() {
        Fn.logD("DBController_deleteTable","deleteTable");
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            database.execSQL(DELETE + TABLE_VIEW_FARE_CHART);
//            database.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Inserts User into SQLite DB
     * @param queryValues
     */
    public void insert(HashMap<String, String> queryValues) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            Fn.logD("DBController_insert", "insert");
            Fn.logD("table_no", "1");
            values.put(CITY_ID, queryValues.get(CITY_ID));
            values.put(CITY_NAME, queryValues.get(CITY_NAME));
            values.put(CITY_LAT, queryValues.get(CITY_LAT));
            values.put(CITY_LNG, queryValues.get(CITY_LNG));

            values.put(BASE_FARE, queryValues.get(BASE_FARE));
            values.put(FARE, queryValues.get(FARE));
            values.put(NIGHT_CHARGE, queryValues.get(NIGHT_CHARGE));
            values.put(RETURN_CHARGE, queryValues.get(RETURN_CHARGE));
            values.put(OUTSTATION_BASE_FARE, queryValues.get(OUTSTATION_BASE_FARE));
            values.put(OUTSTATION_FARE, queryValues.get(OUTSTATION_FARE));
            values.put(IS_ACTIVE, queryValues.get(IS_ACTIVE));
            values.put(UPDATE_DATE, queryValues.get(UPDATE_DATE));
            database.insert(TABLE_VIEW_FARE_CHART, null, values);
//                database.close();
//            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Get list of Users from SQLite DB as Array List
     * @return
     */
    public void getAll() throws ParseException {
        try {
            String selectQuery = "SELECT "+CITY_NAME  +", "+FARE+" FROM "+TABLE_VIEW_FARE_CHART;
            SQLiteDatabase database = this.getWritableDatabase();
            Cursor cursor = database.rawQuery(selectQuery, null);
            Fn.logD("ArrayList", "ArrayList");
            if (cursor.moveToFirst()) {
                do {

                    Fn.logD("onCreate",cursor.getString(0)+" "+cursor.getString(1));


                } while (cursor.moveToNext());
            }
//            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

