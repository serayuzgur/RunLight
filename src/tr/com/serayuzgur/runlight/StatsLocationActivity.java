package tr.com.serayuzgur.runlight;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import tr.com.serayuzgur.runlight.db.dao.GpsLogDao;
import tr.com.serayuzgur.runlight.db.pojo.GpsLog;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.model.LatLng;

public class StatsLocationActivity extends FragmentActivity implements iRibbonMenuCallback, OnLocationChangedListener {

    private RibbonMenuView ribbonMenu;
    private ImageView workspaceHome;
    Chronometer chronometer;
    private static final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

    TextView startTxt;
    TextView distanceTxt;
    TextView avgSpeedTxt;
    private CustomLocationListener customLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);

        setContentView(R.layout.activity_stats);
        ribbonMenu = (RibbonMenuView) findViewById(R.id.mapRibbonMenu);
        ribbonMenu.setMenuClickCallback(this);
        ribbonMenu.setMenuItems(R.menu.ribbon_map_menu);
        workspaceHome = (ImageView) findViewById(R.id.mapHome);
        workspaceHome.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ribbonMenu.toggleMenu();
            }
        });

        distanceTxt = (TextView) findViewById(R.id.statsDistance);
        startTxt = (TextView) findViewById(R.id.statsStart);
        avgSpeedTxt = (TextView) findViewById(R.id.statsAvgSpeed);
        startTxt.setText(format.format(Calendar.getInstance().getTime()));
        avgSpeedTxt.setText("---");
        distanceTxt.setText("---");
        startLocationReceiver();
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
        startLocationReceiver();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (chronometer != null && startTxt != null && avgSpeedTxt != null) {
            outState.putString("chronometer", chronometer.getText().toString());
            outState.putString("startTxt", startTxt.getText().toString());
            outState.putString("distanceTxt", String.valueOf(distance));
            outState.putString("avgSpeedTxt", avgSpeedTxt.getText().toString());
        } else {
            return;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        chronometer.setText(savedInstanceState.getString("chronometer"));
        startTxt.setText(savedInstanceState.getString("startTxt"));
        distanceTxt.setText(savedInstanceState.getString("distanceTxt"));
        avgSpeedTxt.setText(savedInstanceState.getString("avgSpeedTxt"));
        distance = Integer.valueOf(savedInstanceState.getString("distanceTxt"));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }



    private void startLocationReceiver() {
        if (customLocationListener == null) {
            customLocationListener = new CustomLocationListener(getBaseContext());
            SharedPreferences preferences = getSharedPreferences("tr.com.serayuzgur.runlight", MODE_PRIVATE);
            customLocationListener.initialize(preferences.getBoolean("detailgps", true));
            customLocationListener.addListener(this);
        }
    }


    LatLng lastPosition = null;
    int distance = 0;

    @Override
    public void onLocationChanged(Location location) {
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());

        if (lastPosition == null) {
            lastPosition = position;
            chronometer = (Chronometer) findViewById(R.id.mapChronometer);
            chronometer.start();
        } else {
            float[] results = new float[3];
            Location.distanceBetween(lastPosition.latitude, lastPosition.longitude, position.latitude, position.longitude, results);
            distance += results[0];
            distanceTxt.setText(distance + "");
            avgSpeedTxt.setText(location.getSpeed() + "");
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
