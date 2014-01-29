package tr.com.serayuzgur.runlight;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by serayuzgur on 9/7/13.
 */
public class StatsLocationActivityTest extends ActivityInstrumentationTestCase2<StatsLocationActivity> {


    private StatsLocationActivity mSplashActivity;
    private TextView startTime = null;
    private String startTimeValue = "12.12.12";

    public StatsLocationActivityTest() {
        super(StatsLocationActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();

        mSplashActivity = getActivity();
        startTime = (TextView) getActivity().findViewById(R.id.statsStart);
    }

    public void testTextView() {

    }
    @UiThreadTest
    public void testStateDestroy(){
        startTime.setText(startTimeValue);
        mSplashActivity.finish();
        try {
            setUp();
            String postStartTimeValue = startTime.getText().toString();
            Log.e("Runlight", postStartTimeValue);
            assertNotSame(startTimeValue, postStartTimeValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
