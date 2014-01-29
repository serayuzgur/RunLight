package tr.com.serayuzgur.runlight.util;

import java.util.LinkedList;

import android.app.Activity;
import android.view.Display;

public class GoogleUtils {

	public static String pleaseDontEncode(Activity activity, LinkedList<double[]> data){
		StringBuilder staticLoc = new StringBuilder();
		int interval = 1;
		if(data.size()>100){
			interval = data.size()/70;
			interval++;
		}
		int i = 0;
		for (double[]loc : data) {
			if(i++%interval == 0)
				staticLoc.append("|").append(loc[0]).append(',').append(loc[1]);
		}

		Display display = activity.getWindowManager().getDefaultDisplay();
		int width =display.getWidth();
		
		double[] start = data.getFirst();
		double[] end = data.getLast();
		staticLoc.append("|").append(end[0]).append(',').append(end[1]);
		return "http://maps.googleapis.com/maps/api/staticmap?sensor=false&size="+width+"x"+width+
				"&path=color:0xff0000ff|weight:5"+staticLoc.toString() 
				+ "&markers=color:blue%7Clabel:1%7C"+start[0]+','+start[1]
				+ "&markers=color:green%7Clabel:2%7C"+end[0]+','+end[1];
	}

   
}
