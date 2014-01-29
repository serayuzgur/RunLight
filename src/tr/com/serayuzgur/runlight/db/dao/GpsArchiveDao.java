package tr.com.serayuzgur.runlight.db.dao;

import java.util.Date;

import android.database.sqlite.SQLiteDatabase;
import tr.com.serayuzgur.runlight.db.DataBaseHelper;
import tr.com.serayuzgur.runlight.db.pojo.GpsArchive;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

public class GpsArchiveDao {

	private static final String TAG = GpsArchiveDao.class.getSimpleName();
	/**
	 * @see IsEntity
	 */
	public static GpsArchive insert(GpsArchive archive){
		ContentValues values = new ContentValues(4);
		values.put("data"	, archive.getData());
		values.put("start"	, archive.getStart().getTime());
		values.put("end"	, archive.getEnd().getTime());
		values.put("speed"	, archive.getSpeed());
		values.put("duration"	, archive.getDuration());
		values.put("distance"	, archive.getDistance());
		try {
			DataBaseHelper.getDatabase().insertOrThrow("" + tableName() + "", "", values);
			return load(archive.getStart().getTime()+"");		
		} catch (SQLException e) {
			Log.e(TAG,"insert : " + values.toString(), e);
			return null;
		}
		
	}
	/**
	 * A method that loads log
	 * @param logDate
	 * The time of the log
	 * @return
	 * The object or null
	 * @see IsEntity
	 */
	public static GpsArchive load(String... params){
		String[] columns = new String[]{"data","start","end","speed","duration","distance"};
		
		Cursor cursor = DataBaseHelper.getDatabase().query("" + tableName() + "", columns, "start=?", params, null,	null, null);
		if(cursor.moveToNext()){
			GpsArchive archive = new GpsArchive();
			archive.setData(cursor.getString(cursor.getColumnIndex("data")));
			archive.setStart(new Date(cursor.getLong(cursor.getColumnIndex("start"))));
			archive.setEnd(new Date(cursor.getLong(cursor.getColumnIndex("end"))));
			archive.setSpeed(cursor.getDouble(cursor.getColumnIndex("speed")));
			cursor.close();
			return archive;
		}
			
		return null;
	}

	public static GpsArchive delete(GpsArchive archive) {
		try {
			DataBaseHelper.getDatabase().delete(tableName(),"start = ?",new String[]{archive.getStart().getTime()+""});
			return archive;
		} catch (SQLException e) {
			Log.e(TAG,"delete : " + archive.getStart().getTime()+"", e);
			return null;
		}
	}

	public static boolean createTable(SQLiteDatabase mdb) {
		 String create = "CREATE TABLE IF NOT EXISTS " + tableName() + " ( " +
		        "oid INTEGER PRIMARY KEY AUTOINCREMENT," +
		        "data TEXT," +
		        "start INTEGER UNIQUE," +
		        "end INTEGER ," +
		        "speed REAL ," +
				 "duration REAL , " +
				 "distance REAL) ";
		try{
			mdb.execSQL(create);
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
		return "gps_archive";
	}

	public static void deleteAll() {
		
	}

	public static GpsArchive[] loadAll() {
		String[] columns = new String[]{"data","start","end","speed","duration","distance"};
		
		Cursor cursor = DataBaseHelper.getDatabase().query("" + tableName() + "", columns, null, null, null,	null, "start desc");
		GpsArchive[] archives = new GpsArchive[cursor.getCount()];
		int i = 0;
		while(cursor.moveToNext()){
			GpsArchive archive = new GpsArchive();
			archive.setData(cursor.getString(cursor.getColumnIndex("data")));
			archive.setStart(new Date(cursor.getLong(cursor.getColumnIndex("start"))));
			archive.setEnd(new Date(cursor.getLong(cursor.getColumnIndex("end"))));
			archive.setSpeed(cursor.getDouble(cursor.getColumnIndex("speed")));
			archive.setDuration(cursor.getDouble(cursor.getColumnIndex("duration")));
			archive.setDistance(cursor.getDouble(cursor.getColumnIndex("distance")));
			archives[i++] = archive;
		}
		cursor.close();
		return archives;
			
	}

}
