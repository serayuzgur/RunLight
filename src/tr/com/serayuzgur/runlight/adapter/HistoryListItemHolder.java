package tr.com.serayuzgur.runlight.adapter;

import tr.com.serayuzgur.runlight.R;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class HistoryListItemHolder {
	private ImageView historyListItemImage;
	private TextView historyListItemStart;
	private TextView historyListItemDistance;
	private TextView historyListItemDuration;
	private TextView historyListItemSpeed;
	

	public HistoryListItemHolder(View row) {        
		historyListItemImage = (ImageView)row.findViewById(R.id.historyItemSnap);
		historyListItemStart = (TextView)row.findViewById(R.id.historyItemStart);
		historyListItemDistance = (TextView)row.findViewById(R.id.historyItemDistance);
		historyListItemDuration = (TextView)row.findViewById(R.id.historyItemDuration);
		historyListItemSpeed = (TextView)row.findViewById(R.id.historyItemAvgSpeed);
	}

	
	public ImageView getHistoryListItemImage() {
		return historyListItemImage;
	}


	public void setHistoryListItemImage(ImageView historyListItemImage) {
		this.historyListItemImage = historyListItemImage;
	}


	public TextView getHistoryListItemStart() {
		return historyListItemStart;
	}


	public void setHistoryListItemStart(TextView historyListItemStart) {
		this.historyListItemStart = historyListItemStart;
	}


	public TextView getHistoryListItemDistance() {
		return historyListItemDistance;
	}


	public void setHistoryListItemDistance(TextView historyListItemDistance) {
		this.historyListItemDistance = historyListItemDistance;
	}


	public TextView getHistoryListItemDuration() {
		return historyListItemDuration;
	}


	public void setHistoryListItemDuration(TextView historyListItemDuration) {
		this.historyListItemDuration = historyListItemDuration;
	}


	public TextView getHistoryListItemSpeed() {
		return historyListItemSpeed;
	}


	public void setHistoryListItemSpeed(TextView historyListItemSpeed) {
		this.historyListItemSpeed = historyListItemSpeed;
	}



}
