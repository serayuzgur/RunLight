package tr.com.serayuzgur.runlight.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import tr.com.serayuzgur.runlight.db.dao.GpsArchiveDao;
import tr.com.serayuzgur.runlight.db.dao.GpsLogDao;
import tr.com.serayuzgur.runlight.util.SettingsUtils;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {
	
	

	private final static String TAG = DataBaseHelper.class.getName();


	


    private Context helperContext;

	private static SQLiteDatabase mDb;


    public DataBaseHelper(Context context,int version) {
        super(context, SettingsUtils.DATABASE_NAME, null, version);
        helperContext = context;
    }
    public DataBaseHelper(Context context, String name, CursorFactory factory,int version) {
    	super(context, name, factory, version);
    }
    
    
    

    @Override
    public void onCreate(SQLiteDatabase db) {
//    	CreateTables
    	GpsLogDao.createTable(mDb);
    	GpsArchiveDao.createTable(mDb);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade() from " + oldVersion + " to " + newVersion);

        try {
			copyDataBase();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(),e);
		}

        Log.d(TAG, "after upgrade logic, at version " + oldVersion);
        if (oldVersion != newVersion) {
        	Log.w(TAG, "Destroying old data during upgrade");
        	
        	GpsLogDao.dropTable();
            GpsArchiveDao.dropTable();
            onCreate(db);
        }
    }
	public static SQLiteDatabase getDatabase() {
		try {		
			if (mDb == null || !mDb.isOpen())
				
				mDb = SQLiteDatabase.openDatabase(SettingsUtils.DATABASE_NAME, null,SQLiteDatabase.OPEN_READWRITE);		
		} catch (Exception e) {
			if (mDb == null){			
				File f = new File(SettingsUtils.DATABASE_DIR);
				if(!f.isDirectory()){
					f = new File(SettingsUtils.DATABASE_DIR);
					f.mkdirs();
				}
				f = new File(SettingsUtils.DATABASE_NAME);
				try {
					f.createNewFile();
					mDb = SQLiteDatabase.openOrCreateDatabase(SettingsUtils.DATABASE_NAME, null);
					GpsLogDao.createTable(mDb);    	
					GpsArchiveDao.createTable(mDb);
				} catch (IOException e1) {
					System.out.println(e1.getMessage());
				}

			}
		}
		return mDb;
	}

    public void copyDataBase() throws IOException {

        // Open your local db as the input stream
        InputStream myInput = helperContext.getAssets().open(SettingsUtils.DATABASE_NAME);

        // Path to the just created empty db
        String outFileName = SettingsUtils.DATABASE_NAME+SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT);

        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    @Override
    public synchronized void close() {

        if (mDb != null)
            mDb.close();

        super.close();

    }
	
}