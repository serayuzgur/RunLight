package tr.com.serayuzgur.runlight.util;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import tr.com.serayuzgur.runlight.R;
import tr.com.serayuzgur.runlight.db.pojo.GpsArchive;

public class SharingUtils {
	
	
	public static void share(Activity activity,GpsArchive archive){
		 File myFile = new File(SettingsUtils.PHOTO_PATH + archive.getStart().getTime()+".jpg");
    	 MimeTypeMap mime = MimeTypeMap.getSingleton();
    	 String ext=myFile.getName().substring(myFile.getName().lastIndexOf(".")+1);
    	 String type = mime.getMimeTypeFromExtension(ext);
    	 Intent sharingIntent = new Intent("android.intent.action.SEND");
    	 sharingIntent.setType(type);
    	 BigDecimal scale = new BigDecimal(archive.getDistance()/1000);
    	 String shareBody = activity.getString(R.string.dialog_history_item_share_message).replace("{distance}",scale.setScale(2, RoundingMode.HALF_UP).doubleValue()+"");
    	 sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
    	 sharingIntent.putExtra("android.intent.extra.STREAM",Uri.fromFile(myFile));   
    	 activity.startActivity(Intent.createChooser(sharingIntent,activity.getString(R.string.dialog_history_item_share)));
	}

}
