package tr.com.serayuzgur.runlight;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import tr.com.serayuzgur.runlight.db.dao.GpsArchiveDao;
import tr.com.serayuzgur.runlight.db.dao.GpsLogDao;
import tr.com.serayuzgur.runlight.db.pojo.GpsArchive;
import tr.com.serayuzgur.runlight.db.pojo.GpsLog;
import tr.com.serayuzgur.runlight.json.JSONManager;
import tr.com.serayuzgur.runlight.util.GoogleUtils;
import tr.com.serayuzgur.runlight.util.SettingsUtils;
import tr.com.serayuzgur.runlight.util.SharingUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;

public class FinishLineActivity extends Activity {

	TextView startTxt;
	TextView endTxt;
	TextView durationTxt;
	TextView distanceTxt;
	TextView avgSpeedTxt;
	Button finishBtn;
	CheckBox willSave;
	CheckBox willShare;
	@SuppressLint("SimpleDateFormat")
	private static final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
	GpsArchive archive = null;
	LinkedList<double[]> data;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);

		setContentView(R.layout.activity_finish_line);

		startTxt = (TextView) findViewById(R.id.finishlineStart);
		endTxt = (TextView) findViewById(R.id.finishlineEnd);
		durationTxt = (TextView) findViewById(R.id.finishlineDuration);
		distanceTxt = (TextView) findViewById(R.id.finishlineDistance);
		avgSpeedTxt = (TextView) findViewById(R.id.finishlineAvgSpeed);
		finishBtn = (Button) findViewById(R.id.finishlineFinish);
		willSave = (CheckBox) findViewById(R.id.finishline_save_chk);
		willShare = (CheckBox) findViewById(R.id.finishline_share_chk);

		SharedPreferences preferences = getSharedPreferences("tr.com.serayuzgur.runlight", MODE_PRIVATE);		
		willShare.setChecked(preferences.getBoolean("sharerun", true));
		
		GpsLog[] logs = GpsLogDao.loadAll();
		if (logs.length < 2) {
			startTxt.setText("---");
			endTxt.setText("---");
			durationTxt.setText("---");
			avgSpeedTxt.setText("---");
			distanceTxt.setText("---");

			finishBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent startline = new Intent(getApplicationContext(),
							StartLineActivity.class);
					startActivity(startline);
					finish();
				}
			});
			willSave.setVisibility(View.INVISIBLE);
			willShare.setVisibility(View.INVISIBLE);
			return; // Not a real trace
		}
		
		new AsyncTask<GpsLog, String, GpsArchive>(){
			ProgressDialog dialog;
			protected void onPreExecute() {
				dialog = new ProgressDialog(FinishLineActivity.this);
				dialog.setMessage(getString(R.string.calculating));
				dialog.show();
			};

			@Override
			protected GpsArchive doInBackground(GpsLog... logs) {
				double distance = 0;
				Date start = logs[0].getLogDate();
				Date end = logs[logs.length - 1].getLogDate();
				long duration = (end.getTime() - start.getTime()) / 1000; // sec.
				float[] results = new float[3];
				data = new LinkedList<double[]>();
				data.add(new double[] { logs[0].getLatitude(), logs[0].getLongitude() });
				for (int i = 1; i < logs.length; i++) {
					Location.distanceBetween(logs[i - 1].getLatitude(),
							logs[i - 1].getLongitude(), logs[i].getLatitude(),
							logs[i].getLongitude(), results);
					distance += results[0];
					data.add(new double[] { logs[i].getLatitude(),
							logs[i].getLongitude() });
				}
				double avgSpeed = distance / duration; // mt./sec.
				BigDecimal scale = new BigDecimal(avgSpeed);
				avgSpeed = scale.setScale(2, RoundingMode.HALF_UP).doubleValue();
				BigDecimal scaleDist = new BigDecimal(distance);
				distance = scaleDist.setScale(2, RoundingMode.HALF_UP).doubleValue();
				archive = new GpsArchive();
				archive.setStart(start);
				archive.setEnd(end);
				archive.setSpeed(avgSpeed);
				archive.setDuration(((end.getTime() - start.getTime()) / 60000));
				archive.setDistance(distance);
				try {
					archive.setData(JSONManager.getMapper().writeValueAsString(data));
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				return archive;
			}
			
			@Override
			protected void onPostExecute(GpsArchive result) {
				super.onPostExecute(result);
				startTxt.setText(format.format(result.getStart()));
				endTxt.setText(format.format(result.getEnd()));
				durationTxt.setText(result.getDuration()+ "");
				distanceTxt.setText(result.getDistance()+"");
				avgSpeedTxt.setText(result.getSpeed() + "");
				dialog.dismiss();			
			}
			
		}.execute(logs);


		finishBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (willSave.isChecked() && archive != null){
					GpsArchiveDao.insert(archive);
					loadStaticMap();

				} else{
					Intent startline = new Intent(getApplicationContext(),StartLineActivity.class);
					startActivity(startline);
					finish();
				}
			}

			private void loadStaticMap() {
				new AsyncTask<String, String, String>() {
					ProgressDialog dialog;
					protected void onPreExecute() {
						dialog = new ProgressDialog(FinishLineActivity.this);
						dialog.setMessage(getString(R.string.saving));
						dialog.setCancelable(false);
						dialog.show();
					};

					@Override
					protected String doInBackground(String... params) {
						try {
							File renamed = new File(SettingsUtils.PHOTO_PATH + archive.getStart().getTime()+".jpg");
							URLConnection connection = new URL(GoogleUtils.pleaseDontEncode(FinishLineActivity.this,data)).openConnection();										
							BitmapFactory.decodeStream(connection.getInputStream()).compress(CompressFormat.JPEG,100, new FileOutputStream(renamed));
						} catch (IOException e) {
							e.printStackTrace();
						}
						return null;
					}
					protected void onPostExecute(String result) {
						dialog.dismiss();
						Intent history = new Intent(getApplicationContext(),HistoryActivity.class);
						startActivity(history);
						GpsLogDao.deleteAll();
						if(willShare.isChecked())
							SharingUtils.share(FinishLineActivity.this,archive);

						finish();
						
					};
				}.execute("");
			}
		});

	}
	
	
	@Override
	protected void onPause() {
		this.overridePendingTransition(R.anim.left_slide_in,R.anim.right_slide_out);
		super.onPause();
	}

}
