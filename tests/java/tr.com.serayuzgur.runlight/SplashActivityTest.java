package tr.com.serayuzgur.runlight;

import android.test.ActivityInstrumentationTestCase2;

/**
 * Created by serayuzgur on 9/7/13.
 */
public class SplashActivityTest extends ActivityInstrumentationTestCase2<SplashActivity> {


    private SplashActivity mSplashActivity;

    // Be careful about letting the IDE create the constructor.  As of this writing,
    // it creates a constructor that's compiles cleanly but doesn't run any tests
    public SplashActivityTest() {
        super(SplashActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();

        mSplashActivity = getActivity();
    }

    // Methods whose names are prefixed with test will automatically be run
    public void testTextView() {
    }
}
