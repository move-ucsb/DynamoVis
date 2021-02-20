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

	private Location location;
	private String tag;
	private DateTime timestamp;
	private HashMap<String, Object> properties = new HashMap<String, Object>();
	
	
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
