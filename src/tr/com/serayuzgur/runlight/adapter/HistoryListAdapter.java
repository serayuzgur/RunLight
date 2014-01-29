package tr.com.serayuzgur.runlight.adapter;

import java.io.File;
import java.text.SimpleDateFormat;

import tr.com.serayuzgur.runlight.R;
import tr.com.serayuzgur.runlight.db.pojo.GpsArchive;
import tr.com.serayuzgur.runlight.util.SettingsUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class HistoryListAdapter extends ArrayAdapter<GpsArchive>{
	
	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat DATE = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Context context; 
    int layoutResourceId;    
    GpsArchive data[] = null;
    
    public HistoryListAdapter(Context context, int layoutResourceId, GpsArchive[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        HistoryListItemHolder holder = null;
        
        if(row == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new HistoryListItemHolder(row);
            
            row.setTag(holder);
        }else{
            holder = (HistoryListItemHolder)row.getTag();
        }
        
        GpsArchive archive = data[position];
        if(archive != null){
			File sc = new File(SettingsUtils.PHOTO_PATH+ archive.getStart().getTime()+".jpg");
			if(sc.exists() && sc.isFile())
				holder.getHistoryListItemImage().setImageBitmap(
						BitmapFactory.decodeFile(SettingsUtils.PHOTO_PATH+ archive.getStart().getTime()+".jpg"));
			else
				holder.getHistoryListItemImage().setImageResource(R.drawable.map_on);
				
			
        	holder.getHistoryListItemStart().setText(DATE.format(archive.getStart()));
        	holder.getHistoryListItemDuration().setText(context.getString(R.string.history_duration)+" "+archive.getDuration());
        	holder.getHistoryListItemDistance().setText(context.getString(R.string.history_distance)+" "+archive.getDistance());
        	holder.getHistoryListItemSpeed().setText(context.getString(R.string.history_avg_speed)+" "+archive.getSpeed());
        }
        
        return row;
    }
    
}