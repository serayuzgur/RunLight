package tr.com.serayuzgur.runlight;

import java.io.File;

import tr.com.serayuzgur.runlight.adapter.HistoryListAdapter;
import tr.com.serayuzgur.runlight.db.dao.GpsArchiveDao;
import tr.com.serayuzgur.runlight.db.pojo.GpsArchive;
import tr.com.serayuzgur.runlight.util.SettingsUtils;
import tr.com.serayuzgur.runlight.util.SharingUtils;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class HistoryActivity extends Activity {
	
	ListView listView =  null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);

		GpsArchive[] archives = GpsArchiveDao.loadAll();
		
		HistoryListAdapter adapter = new HistoryListAdapter(this, R.layout.listview_item_history, archives);
		
		listView = (ListView) findViewById(R.id.historyList);
		
		listView.setAdapter(adapter);
		
		listView.setHeaderDividersEnabled(true);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent detail = new Intent(HistoryActivity.this, HistoryDetailActivity.class);
				detail.putExtra("gpsarchive", ((GpsArchive)arg0.getItemAtPosition(arg2)));
				startActivity(detail);
			}
		});
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				final Dialog dialog = new Dialog(HistoryActivity.this);
				dialog.setContentView(R.layout.dialog_history_item_ops);
				dialog.setTitle(R.string.dialog_history_item_title);
				final GpsArchive archive = (GpsArchive) arg0.getItemAtPosition(arg2);
				Button cancel = (Button) dialog.findViewById(R.id.dialogHistoryItemCancel);
				cancel.setOnClickListener(new OnClickListener() {				
					@Override
					public void onClick(View v) {
						dialog.cancel();
					}
				});
				Button delete = (Button) dialog.findViewById(R.id.dialogHistoryItemDelete);
				delete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						QuestionDialog dialog = new QuestionDialog(HistoryActivity.this){
							@Override
							protected void onPositive() {
								File myFile = new File(SettingsUtils.PHOTO_PATH + archive.getStart().getTime()+".jpg");
								myFile.delete();
								GpsArchiveDao.delete(archive);
							}
						};
						dialog.setTitle(getString(R.string.dialog_history_item_delete));
						dialog.setMessage(getString(R.string.dialog_history_item_delete_question));
						dialog.show();
					}
				});
				Button share = (Button) dialog.findViewById(R.id.dialogHistoryItemShare);
				share.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {					    
					     try {
					    	 SharingUtils.share(HistoryActivity.this,archive);
					    	 dialog.dismiss();
					     }
					     catch(Exception e){
					         Toast.makeText(getBaseContext(), e.getMessage(),Toast.LENGTH_SHORT).show();  
					     } 
					}
				});
				dialog.show();
				return true;
			}
		});
	}

	@Override
	protected void onPause() {
		this.overridePendingTransition(R.anim.left_slide_in,R.anim.right_slide_out);
		super.onPause();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();

	}

}
