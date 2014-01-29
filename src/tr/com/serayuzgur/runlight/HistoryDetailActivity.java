package tr.com.serayuzgur.runlight;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

import tr.com.serayuzgur.runlight.db.pojo.GpsArchive;
import tr.com.serayuzgur.runlight.json.JSONManager;
import tr.com.serayuzgur.runlight.util.GoogleUtils;
import tr.com.serayuzgur.runlight.util.SettingsUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;

public class HistoryDetailActivity extends Activity {

	TextView startTxt;
	TextView endTxt;
	TextView durationTxt;
	TextView distanceTxt;
	TextView avgSpeedTxt;
	ImageView snap;
	@SuppressLint("SimpleDateFormat")
	private static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy \n HH:mm:ss");
	LinkedList<double[]> data;
	GpsArchive archive;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);

		setContentView(R.layout.activity_history_detail);
		archive = (GpsArchive) getIntent().getExtras().getSerializable("gpsarchive");


		startTxt = (TextView) findViewById(R.id.historyDetailStart);
		endTxt = (TextView) findViewById(R.id.historyDetailEnd);
		durationTxt = (TextView) findViewById(R.id.historyDetailDuration);
		distanceTxt = (TextView) findViewById(R.id.historyDetailDistance);
		avgSpeedTxt = (TextView) findViewById(R.id.historyDetailAvgSpeed);
		snap = (ImageView) findViewById(R.id.historyDetailSnap);
		
		
		startTxt.setText(format.format(archive.getStart()));
		endTxt.setText(format.format(archive.getEnd()));
		durationTxt.setText(archive.getDuration()+ "");
		distanceTxt.setText(archive.getDistance()+"");
		avgSpeedTxt.setText(archive.getSpeed() + "");
		File sc = new File(SettingsUtils.PHOTO_PATH + archive.getStart().getTime()+".jpg");
		if(sc.exists() && sc.isFile())
			snap.setImageBitmap(BitmapFactory.decodeFile(SettingsUtils.PHOTO_PATH + archive.getStart().getTime()+".jpg"));
		else
			loadStaticMap();
		

	}

	private void loadStaticMap() {
		new AsyncTask<String, String, String>() {
			ProgressDialog dialog;
			protected void onPreExecute() {
				dialog = new ProgressDialog(HistoryDetailActivity.this);
				dialog.setMessage(getString(R.string.saving));
				dialog.setCancelable(false);
				dialog.show();
			};

			@Override
			protected String doInBackground(String... params) {
				try {
					data = JSONManager.getMapper().readValue(archive.getData(),new TypeReference<LinkedList<double[]>>(){});
					File renamed = new File(SettingsUtils.PHOTO_PATH + archive.getStart().getTime()+".jpg");
					URLConnection connection = new URL(GoogleUtils.pleaseDontEncode(HistoryDetailActivity.this,data)).openConnection();					
					BitmapFactory.decodeStream(connection.getInputStream()).compress(CompressFormat.JPEG,100, new FileOutputStream(renamed));
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
			protected void onPostExecute(String result) {
				dialog.dismiss();
				snap.setImageBitmap(BitmapFactory.decodeFile(SettingsUtils.PHOTO_PATH + archive.getStart().getTime()+".jpg"));
				
			};
		}.execute("");
	}
	
	   
}
