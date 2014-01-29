package tr.com.serayuzgur.runlight;

import java.util.ArrayList;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;

public class CustomLocationListener implements LocationListener {
	private static final float MAURICE_GREENE = 11.8f; // m/s
	private static final float MIN_SPEED = -1;
	private static final float MAX_SPEED = 10;
	private static final String TAG = CustomLocationListener.class.getSimpleName();
	private final long GOOD_TIME_FREQUENCY=2000;
	private final long BAD_TIME_FREQUENCY=10000;
	private final float ACC_FREQUENCY=50;
	private LocationManager locationManager;
	private Location lastfix = null;
	private Criteria criteria;
	private Context context;
	private ArrayList<OnLocationChangedListener> listeners = new ArrayList<OnLocationChangedListener>();
	
	public CustomLocationListener(Context context) {
		this.context = context;
	}
	

	public void initialize(boolean detailed ){
		locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		criteria = new Criteria();
	    criteria.setAccuracy(Criteria.ACCURACY_FINE);
	    if(detailed)
	    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GOOD_TIME_FREQUENCY, 2, this);
	    else
	    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, BAD_TIME_FREQUENCY, 10, this);
	    	
	}
	public void destroy(){
		locationManager.removeUpdates(this);
	}
	
	public void addListener(OnLocationChangedListener listener){
		listeners.add(listener);
	}
	public void remoceListener(OnLocationChangedListener listener){
		listeners.remove(listener);
	}


	@Override
	public void onLocationChanged(Location location) {
		Log.d(TAG,"Location changed Acc:" + location.getAccuracy() + " Speed:" + location.getSpeed());
		if(//(Calendar.getInstance().getTimeInMillis()-location.getTime() > TIME_FREQUENCY)//time check
				(location.getAccuracy()<ACC_FREQUENCY) //accuracy check
				&&(location.getSpeed() > MIN_SPEED && location.getSpeed() < MAX_SPEED ) // speed check
		){
			if(lastfix != null){
				float distance = calculateDistance(location); // Meters
				Log.d(TAG,"Distance: " + distance);
				if(distance == 0) // Do nothing if guy stands.
					return;
				long timeDiff = (location.getTime() - lastfix.getTime())/1000; // Convert to secs.
				
				//Compare with fastest human
				float speed = distance/timeDiff; // mt./sec.
				Log.d(TAG,"Speed: " + speed);
				
				if(speed > MAURICE_GREENE){
					//TODO: Send mail to guinness world records.
					Log.d(TAG,"Too fast cant be True! GPS_Speed: " +location.getSpeed() + "CompSpeed: " + speed);
					return;
				}
			}else{
				Log.d(TAG,"Location firstFix");
			}
			lastfix = location;
			for(OnLocationChangedListener listener : listeners){
				listener.onLocationChanged(lastfix);
			}
		}

	}

	private float calculateDistance(Location location) {
		float[] distances = new float[3]; 
		Location.distanceBetween(lastfix.getLatitude(), lastfix.getLongitude(), location.getLatitude(), location.getLongitude(), distances);
		if(distances[0]<0)
			return 0;
		return distances[0];
	}

	@Override
	public void onProviderDisabled(String provider) {	}

	@Override
	public void onProviderEnabled(String provider) {}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}

	public Location getLocation(){
		return lastfix;	
	}


}

