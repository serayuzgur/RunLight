package tr.com.serayuzgur.runlight.db.pojo;

import java.io.Serializable;
import java.util.Date;

public class GpsArchive implements Serializable{

	private static final long serialVersionUID = 7324666373991095562L;
	
	private Date	start ;
	private Date	end ;
	private double	speed ;
	private String 	data;
	private double 	distance;
	private double 	duration;
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}

	public double getSpeed() {
		return speed;
	}
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public double getDuration() {
		return duration;
	}
	public void setDuration(double duration) {
		this.duration = duration;
	}

}
