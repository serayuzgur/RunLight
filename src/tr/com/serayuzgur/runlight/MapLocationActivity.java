package tr.com.serayuzgur.runlight;

import java.util.Date;

import tr.com.serayuzgur.runlight.db.dao.GpsLogDao;
import tr.com.serayuzgur.runlight.db.pojo.GpsLog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapLocationActivity  extends FragmentActivity implements iRibbonMenuCallback ,OnLocationChangedListener{

	private RibbonMenuView ribbonMenu;
	private ImageView workspaceHome;
	private PolylineOptions trackOptions = new PolylineOptions();
    private GoogleMap mMap;
    private TextView distanceText = null;
    Chronometer chronometer ;
    private CustomLocationListener customLocationListener;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);

		setContentView(R.layout.activity_map);
		ribbonMenu = (RibbonMenuView) findViewById(R.id.mapRibbonMenu);
        ribbonMenu.setMenuClickCallback(this);
        ribbonMenu.setMenuItems(R.menu.ribbon_map_menu);
        workspaceHome = (ImageView) findViewById(R.id.mapHome);
        distanceText = (TextView) findViewById(R.id.mapDistance);
        distanceText.setText("0 "+ getString(R.string.map_meters));
        workspaceHome.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ribbonMenu.toggleMenu();
			}
		});
		setUpMapIfNeeded();
		trackOptions.color(Color.RED);
        
	}

	
	@Override
	public void RibbonMenuItemClick(int itemId) {

		switch (itemId) {
		case R.id.ribbon_menu_end:
			finishRun();
			break;

		default:
			break;
		}
	}


	private void finishRun() {
		Intent finishLine = new Intent(getApplicationContext(), FinishLineActivity.class);
		startActivity(finishLine);
		finish();
	}
	
	@Override
	public void onBackPressed() {
		ribbonMenu.toggleMenu();
	}

	  @Override
	    protected void onResume() {
	        super.onResume();
	        setUpMapIfNeeded();
	    }

	  private void setUpMapIfNeeded() {
	        // Do a null check to confirm that we have not already instantiated the map.
	        if (mMap == null) {
	            // Try to obtain the map from the SupportMapFragment.
	            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
	            // Check if we were successful in obtaining the map.
	            if (mMap != null) {
					startMapLocationReceiver();
	            }
	        }else{
				startMapLocationReceiver();
	        }
	    }



	private void startMapLocationReceiver() {
		if(customLocationListener == null){
			customLocationListener = new CustomLocationListener(getBaseContext());
			SharedPreferences	preferences = getSharedPreferences("tr.com.serayuzgur.runlight", MODE_PRIVATE);
			customLocationListener.initialize(preferences.getBoolean("detailgps", true));
			customLocationListener.addListener(this);
		}
		mMap.setMyLocationEnabled(true);

	}



	LatLng lastPosition = null;
	int distance = 0;
	@Override
	public void onLocationChanged(Location location) {
		LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
		trackOptions.add(position);
		mMap.clear();
		mMap.addPolyline(trackOptions);	

		if(lastPosition == null){
			lastPosition = position;
			chronometer = (Chronometer) findViewById(R.id.mapChronometer);
			chronometer.start();
		}else{
			float[] results = new float[3];
			Location.distanceBetween(lastPosition.latitude, lastPosition.longitude, position.latitude, position.longitude, results);
			distance += results[0];
	        distanceText.setText(distance + " "+ getString(R.string.map_meters));
			lastPosition = position;

		}
		
		GpsLog log = new GpsLog();
		log.setAccuracy(location.getAccuracy());
		log.setLatitude(location.getLatitude());
		log.setLongitude(location.getLongitude());
		log.setLogDate(new Date(location.getTime()));   
		GpsLogDao.insert(log);

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		customLocationListener.destroy();
	}

}
