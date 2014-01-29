package tr.com.serayuzgur.runlight;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

public class SplashActivity extends Activity {
	Handler autoClose = null;

	private static final int GPS_REQ = 2829181;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
		setContentView(R.layout.activity_splash);

	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(!hasFocus)
			return;
		// Create a handler to close wellcome after 1 sec.
		autoClose = new Handler(Looper.getMainLooper());
		
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.appear);
		findViewById(R.id.splashLogo).startAnimation(animation);
		findViewById(R.id.splashWelcome).startAnimation(animation);
		findViewById(R.id.splashMotto).startAnimation(animation);
		checkGPSSettings();
	}

	private void checkGPSSettings() {
		boolean gpsEnabled = ((LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE))
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!gpsEnabled)
			openGpsDialog();
		else {
			autoClose.postDelayed(new Thread() {
				@Override
				public void run() {
					startFinishAnimations();
				}

			}, 1000);
		}
	}

	protected void startFinishAnimations() {
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.right_fly_out);
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				gotoStartLine();
			}
		});
		findViewById(R.id.splashLogo).startAnimation(animation);
		findViewById(R.id.splashLogo).setVisibility(View.INVISIBLE);
	}

	private void gotoStartLine() {

		Intent intent = new Intent(getApplicationContext(),
				StartLineActivity.class);
		startActivity(intent);
		finish();
	}

	int questionCounter = 0;
	private void openGpsDialog() {
		QuestionDialog dialog = new QuestionDialog(this) {
			@Override
			protected void onPositive() {
				Intent gpsOptionsIntent = new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				SplashActivity.this.startActivityForResult(gpsOptionsIntent,GPS_REQ);
			}

			@Override
			protected void onNegative() {
				hide();
				if(questionCounter++ < 1){
					AlertDialog.Builder builder = new Builder(SplashActivity.this);
					builder.setMessage(R.string.splash_no_gps_warning);
					builder.show();
				}else{
					finish();
				}
			}
		};
		dialog.setMessage(getString(R.string.splash_need_gps_service));
		dialog.show();
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == GPS_REQ) {
			checkGPSSettings();
		}
	}

}
