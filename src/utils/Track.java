/*
  	DYNAMO Animation Tool
    Copyright (C) 2016 Glenn Xavier

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
*/

package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map.Entry;

import org.apache.commons.math3.stat.Frequency;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import main.SketchData;

public class Track {
	
	private int interval;
	private String tag;
	private DateTime startDate;
	private DateTime endDate;
	private boolean visible;
	public SketchData data;
	
	private ArrayList<PointRecord> trackPoints = new ArrayList<PointRecord>();
	private ArrayList<String> trackProperties = new ArrayList<String>();
	
	public ArrayList<DateTime> temp = new ArrayList<DateTime>();
	
	public void addPoint(PointRecord point){
		this.trackPoints.add(point);
		HashMap<String, Object> props = point.getProperties();
		for (Entry<String,Object> entry: props.entrySet()){
			String property = entry.getKey();
			if (!trackProperties.contains(property)){
				this.trackProperties.add(property);
			}
		}
	}
	
	
	
	public ArrayList<PointRecord> getPoints(){
		return this.trackPoints;
	}
	
	public ArrayList<String> getProperties(){
		return this.trackProperties;
	}
	
	public void setStartDate(DateTime d){
		this.startDate = d;
	}
	
	public DateTime getStartDate(){
		return this.startDate;
	}
		
	public void setEndDate(DateTime d){
		this.endDate = d;
	}
	
	public DateTime getEndDate(){
		return this.endDate;
	}
	
	public void setInterval(int i){
		this.interval = i;
	}
	
	public int getInterval(){
		return this.interval;
	}
	
	public void setTag(String t){
		this.tag = t;
	}
	
	public String getTag(){
		return this.tag;
	}
	
	public void setVisibility(boolean b){
		this.visible = b;
	}
	
	public boolean getVisibility(){
		return this.visible;
	}
	
	// get_gilter_times by Kin (Nathan) Chan
		public Hashtable<Integer, ArrayList<Integer>> get_filter_times()
		{
//			ArrayList<DateTime> temp_times = new ArrayList<DateTime>();
			Hashtable<Integer, ArrayList<Integer>> temp_hash = new Hashtable<Integer, ArrayList<Integer>>();
			for(PointRecord point1: trackPoints)
			{
				if(temp_hash.containsKey(point1.getYear()))
				{
					ArrayList<Integer> temp_times = temp_hash.get(point1.getYear());
					int temp_month = point1.getMonth();
					if(!temp_times.contains(temp_month)) {
						temp_times.add(temp_month);
						Collections.sort(temp_times);
						temp_hash.replace(point1.getYear(), temp_times);
					}
							
				}
				else
				{
					temp_hash.put(point1.getYear(), new ArrayList<Integer>(point1.getMonth()));
				}
			}
			return temp_hash;
		}
	
	public ArrayList<DateTime> gettheTimes()
	{
		for (PointRecord point1: trackPoints ){
			DateTime temptime = point1.getTime();
			temp.add(temptime);
		}
		
		return temp;
		
	}
	public void calculateTimes(){
		
		ArrayList<DateTime> col = new ArrayList<DateTime>();
		Frequency f = new Frequency();
		
		for (PointRecord point: trackPoints ){
			DateTime time = point.getTime();
			col.add(time);
		}
		
		Collections.sort(col);
		setStartDate(Collections.min(col));
		setEndDate(Collections.max(col));
		
		for (int i = 0; i < col.size() - 1; i++){
			//round up to nearest whole minute
			long seconds = Seconds.secondsBetween(col.get(i),col.get(i+1)).getSeconds();
			long minutes = (long) Math.round((float) seconds/60);		
			
			f.addValue(minutes);
		}
		
		Long intervalLong = (Long) f.getMode().get(0);
		Integer interval = intervalLong != null ? intervalLong.intValue() : null;	
		
		this.setInterval(interval);
	}	

	
	
}
