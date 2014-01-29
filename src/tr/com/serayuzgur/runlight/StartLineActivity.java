package tr.com.serayuzgur.runlight;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;

import tr.com.serayuzgur.runlight.db.dao.GpsLogDao;
import tr.com.serayuzgur.runlight.db.pojo.GpsLog;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ToggleButton;

public class StartLineActivity extends Activity implements iRibbonMenuCallback {
	
	ToggleButton liveMap = null;
	ToggleButton detailGPS = null;
	Button start = null;
	SharedPreferences preferences = null;
	private RibbonMenuView ribbonMenu;
	private ImageView workspaceHome;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
		setContentView(R.layout.activity_start_line);
		
		liveMap = (ToggleButton) findViewById(R.id.startlineLiveMapButton);
		detailGPS = (ToggleButton) findViewById(R.id.startlineDetailedGPSButton);
		start = (Button) findViewById(R.id.startlineStartButton);
		
		start.setOnClickListener(new StartLineOnClickListener());
		
		preferences = getSharedPreferences("tr.com.serayuzgur.runlight", MODE_PRIVATE);
		
		liveMap.setChecked(preferences.getBoolean("livemap", false));
		detailGPS.setChecked(preferences.getBoolean("detailgps", false));
		
		ribbonMenu = (RibbonMenuView) findViewById(R.id.mapRibbonMenu);
        ribbonMenu.setMenuClickCallback(this);
        ribbonMenu.setMenuItems(R.menu.ribbon_startline_menu);
        workspaceHome = (ImageView) findViewById(R.id.startlineLogo);
        workspaceHome.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ribbonMenu.toggleMenu();
			}
		});

        if(GpsLogDao.loadAll().length>0){
            QuestionDialog dialog = new QuestionDialog(this){
                @Override
                protected void onPositive() {
                    super.onPositive();
                    Intent finishActivity = new Intent(getApplicationContext(),FinishLineActivity.class);
                    startActivity(finishActivity);
                    finish();
                }

                @Override
                protected void onNegative() {
                    GpsLogDao.deleteAll();
                    super.onNegative();

                }
            };
            dialog.setMessage(getString(R.string.startline_recovery));
            dialog.show();
        }
		


	}

	
	@Override
	public void onBackPressed() {
		ribbonMenu.toggleMenu();
	}
	@Override
	public void RibbonMenuItemClick(int itemId) {

		switch (itemId) {
		case R.id.ribbon_menu_history:
			Intent historyActivity = new Intent(getApplicationContext(),HistoryActivity.class);
			startActivity(historyActivity);
			break;
		case R.id.ribbon_menu_logout:
			finish();
			break;

		default:
			break;
		}
	}

	
	public class StartLineOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			switch (v.getId()) {
			case R.id.startlineStartButton:
				SharedPreferences.Editor prefEditor = preferences.edit();
				//Update location parameters will be decided according this buttons value.
				if(detailGPS.isChecked()){
					prefEditor.putBoolean("detailgps", true);
				}else{
					prefEditor.putBoolean("detailgps", false);
				}
				if(liveMap.isChecked()){
					prefEditor.putBoolean("livemap", true);
					prefEditor.commit();
					Intent mapActivity = new Intent(getApplicationContext(),MapLocationActivity.class);
					startActivity(mapActivity);
					finish();
				}else{
					prefEditor.putBoolean("livemap", false);
					prefEditor.commit();
					Intent statsActivity = new Intent(getApplicationContext(),StatsLocationActivity.class);
					startActivity(statsActivity);
					finish();
				}
				break;

			default:
				break;
			}
			
		}
		
	}


}
