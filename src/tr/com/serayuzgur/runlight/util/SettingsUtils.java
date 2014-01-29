package tr.com.serayuzgur.runlight.util;

import java.io.File;

import android.os.Environment;

public class SettingsUtils {
	
	public static final String APP_PATH = "/data/tr.com.serayuzgur.runlight/";
	
	public static final String PHOTO_PATH = getStorage() + APP_PATH;
	
	public static final String DATABASE_DIR = SettingsUtils.getStorage() + APP_PATH;
	public static final String DATABASE_NAME = DATABASE_DIR + "RunLight.db";

	public static File getStorage(){
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media
		    mExternalStorageAvailable = true;
		    mExternalStorageWriteable = false;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
		    mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		if(mExternalStorageAvailable && mExternalStorageWriteable)
			return Environment.getExternalStorageDirectory();
		else
			return Environment.getDataDirectory();
	}
	
	
}
