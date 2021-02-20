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

import java.util.HashMap;

import org.joda.time.DateTime;

import de.fhpotsdam.unfolding.geo.Location;


public class PointRecord {

	//public static final Comparator POLAR_ORDER = null;
	private Location location;
	private int sectorNum;
	private String tag;
	private DateTime timestamp;
	private boolean buff;
	private HashMap<String, Object> properties = new HashMap<String, Object>();
	
	/*
	 * buffer_size_space takes in two points and the selected buffer value to calculate if the
	 * distance between two points is within buffer
	 */
	public boolean buffer_size_space(Location pt_one, Location pt_two, double buffer){
		double distance = Math.sqrt(Math.pow(pt_one.x - pt_two.x, 2)+Math.pow(pt_one.y-pt_two.y, 2));
		if (distance <= buffer)
			return true;
		return false;
	}
	
	public void calculateDistance(double radius, double x ,double y, double a, double b, double c){
    	// Finding the distance of line from center.
        double dist = (Math.abs(a * x + b * y + c)) / 
                        Math.sqrt(a * a + b * b);   
        // Checking if the distance is less than, 
        // greater than or equal to radius.
        this.buff= (radius > dist || radius == dist) ;
    }
	
	public void setBuff(boolean b){
		this.buff = b;
	}
	
	public boolean getBuff(){
		return this.buff;
	}
	
	public void setSectorNum(int num){
		this.sectorNum = num;
	}
	
	public int getSectorNum(){
		return this.sectorNum;
	}
	
//	public boolean getBuffer(){
//		return this.buff.b;
//	}
//	
//	public void setBuffer(boolean b){
//		this.buff.b = b;
//	}
	
	public Location getLocation(){
		return this.location;
	}
	
	public void setLocation(float x, float y){
		this.location = new Location(x,y);
	}
	
	public void setID(String id){
		this.tag = id;
	}
	
	public String getID(){
		return this.tag;
	}
	
	public void setTime(DateTime time){
		this.timestamp = time;
	}
	
	public DateTime getTime(){
		return this.timestamp;
	}
	
	public int getYear() {
		return this.timestamp.getYear();
	}
	
	public int getMonth()
	{
		return this.timestamp.getMonthOfYear();
	}
	
	public int getDay()
	{
		return this.timestamp.getDayOfMonth();
	}
	
	public HashMap<String, Object> getProperties() {
		return this.properties;
	}

	public Object getProperty(String key) {
		return this.properties.get(key);
	}

	public Object addProperty(String key, Object value) {
		return this.properties.put(key, value);
	}

	public void removeProperty(String key){
		this.properties.remove(key);
	}	
	
}
