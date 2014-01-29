package tr.com.serayuzgur.runlight.db.dao;


import java.util.Date;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import tr.com.serayuzgur.runlight.db.DataBaseHelper;
import tr.com.serayuzgur.runlight.db.pojo.GpsLog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GpsLogDao {

	private static final String TAG = GpsLogDao.class.getSimpleName();
	private static String[] columns = new String[]{"accuracy","latitude","longitude","logDate","speed"};

	public static void insert(GpsLog log){
		ContentValues values = new ContentValues(4);
		values.put("accuracy"	, log.getAccuracy());
		values.put("latitude"	, log.getLatitude());
		values.put("longitude"	, log.getLongitude());
		values.put("logDate"	, log.getLogDate().getTime());
		values.put("speed"		, log.getSpeed());
		try {
			DataBaseHelper.getDatabase().insertOrThrow("" + tableName() + "", "", values);
		} catch (SQLException e) {
			Log.e(TAG,"insert : " + values.toString(), e);
		}		
	}
	public static GpsLog insertAndLoad(GpsLog log){

		try {
			insert(log);
			return load(log.getLogDate().getTime()+"");		
		} catch (SQLException e) {
			Log.e(TAG,"insert : " + log.toString(), e);
			return null;
		}		
	}
	/**
	 * A method that loads log
	 * @param logDate
	 * The time of the log
	 * @return
	 * The object or null
	 * @see IsDao
	 */
	public static GpsLog load(String... params){
		
		Cursor cursor = DataBaseHelper.getDatabase().query( tableName() , columns, "logDate=?", params, null,	null, null);
		if(cursor.moveToNext()){
			GpsLog log = new GpsLog();
			log.setAccuracy(cursor.getDouble(cursor.getColumnIndex("accuracy")));
			log.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
			log.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
			log.setLogDate(new Date(cursor.getLong(cursor.getColumnIndex("logDate"))));
			log.setSpeed(cursor.getDouble(cursor.getColumnIndex("speed")));
			cursor.close();
			return log;
		}
		cursor.close();	
		return null;
	}
	
	public static LatLngBounds getGPSBoundary(){
		try {
			Cursor cursor = DataBaseHelper.getDatabase().rawQuery("select min('latitude') , min('longitude'), max('latitude') ,max('longitude') from " +tableName(), null);
			if(cursor.moveToNext()){
				return new LatLngBounds(new LatLng(cursor.getDouble(0), cursor.getDouble(2)), new LatLng(cursor.getDouble(1), cursor.getDouble(3)));
			}
			return null;
		} catch (SQLException e) {
			Log.e(TAG,"getGPSBoundary : ", e);
			return null;
		}
	}
	public static GpsLog delete(GpsLog log) {
		try {
			DataBaseHelper.getDatabase().delete(tableName(),"logDate = ?",new String[]{log.getLogDate().getTime()+""});
			return log;
		} catch (SQLException e) {
			Log.e(TAG,"delete : " + log.getLogDate().getTime()+"", e);
			return null;
		}
	}
	public static boolean createTable(SQLiteDatabase mDb) {
		 String create = "CREATE TABLE IF NOT EXISTS " + tableName() + " ( " +
		        "oid INTEGER PRIMARY KEY AUTOINCREMENT," +
		        "accuracy REAL," +
		        "latitude REAL," +
		        "longitude REAL," +
		        "logDate INTEGER UNIQUE," +
		        "speed REAL) ";
		try{
			mDb.execSQL(create);
			return true;
		}catch(SQLException e){
			Log.e(TAG, "createTable",e);
			return false;
		}	}
	public static boolean dropTable() {
		String drop = "DROP TABLE IF EXISTS " + tableName() + "";
		try{
			DataBaseHelper.getDatabase().execSQL(drop);
			return true;
		}catch(SQLException e){
			Log.e(TAG, "dropTable",e);
			return false;
		}
	}
	
	public static String tableName() {
		return "gps_log";
	}
	public static GpsLog[] loadAll() {
		Cursor cursor = DataBaseHelper.getDatabase().query("" + tableName() + "", columns, null, null, null,	null, "logDate asc");
		GpsLog[] logs = new GpsLog[cursor.getCount()];
		int i = 0;
		while(cursor.moveToNext()){
			GpsLog log = new GpsLog();
			log.setAccuracy(cursor.getDouble(cursor.getColumnIndex("accuracy")));
			log.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
			log.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
			log.setLogDate(new Date(cursor.getLong(cursor.getColumnIndex("logDate"))));
			log.setSpeed(cursor.getDouble(cursor.getColumnIndex("speed")));
			logs[i++] = log;
		}
		cursor.close();
			
		return logs;
	}
	public static void deleteAll() {

		try {
			DataBaseHelper.getDatabase().delete(tableName(),null,null);
		} catch (SQLException e) {
			Log.e(TAG,"deleteAll", e);
		}
	}
}
