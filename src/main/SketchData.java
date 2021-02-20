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

package main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import utils.PointRecord;
import utils.Track;

import org.joda.time.DateTime;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.providers.AbstractMapProvider;
import de.fhpotsdam.unfolding.utils.GeoUtils;
import de.fhpotsdam.unfolding.utils.MapUtils;

public class SketchData {

	public Location[] mapExtent;
	public List<Location> locations;
	public HashMap<String, ArrayList<Float>> fieldMinMax;

	public DateTime startTime;
	public DateTime endTime;
	public DateTime currentTime;
	public int totalTime;
	public int dataInterval;
	public String timeUnit;
	
	public int seek;
	public int alphaMaxHours;
	public int speed;
		
	public boolean falloff;	
	public boolean pause = true;
	public boolean mouse = false;
	public boolean loop;
	public boolean strokeWeightToggle;	
	public boolean pointColorToggle;
	public boolean pointSizeToggle;
	public boolean strokeColorToggle;
	public boolean vectorToggle;
	public boolean vectorLengthToggle;
	public boolean vectorColorToggle;
	
	public String strokeWeightSelection;
	public String pointColorSelection;
	public String pointSizeSelection;
	public String strokeColorSelection;
	public String selectedColorUnit;
	public String selectedStrokeWeightUnit;
	public String vectorFieldSelection;	
	public String headingFieldSelection;
	public String vectorColorSelection;		
	
	public int strokeWeightMin = 1;
	public int strokeWeightMax = 10;
	public int pointSizeMin = 1;
	public int pointSizeMax = 10;
	public int vectorLengthMin = 5;
	public int vectorLengthMax = 20;
	
	public boolean ghost;
	public Color ghostColor = Color.WHITE;
	public int ghostAlpha;
	public int ghostWeight;
	
	public Location mapCenter;
	
	public boolean legendLocked = true;
	public boolean save = false;
	public int frameCounter = 0;
	
	public AbstractMapProvider provider;
	
	public int selectedLineSwatch;
	public int selectedPointSwatch;
	public int selectedVectorSwatch;
	
	public Map<String,Integer> fieldColors = new HashMap<String,Integer>();
	public String brushedTag;
	

	

	public void processFeatureList(HashMap<String,Track> data) {

		locations = new ArrayList<Location>();
		
		for (Entry<String,Track> entry:data.entrySet()){
			Track track = entry.getValue();
			
			for (PointRecord point: track.getPoints() ){
				locations.add(point.getLocation());
			}
		}	

		mapExtent = GeoUtils.getBoundingBox(locations);		
		System.out.println("Setting Map Extent to: " + mapExtent[0] + " " + mapExtent[1]);
	
	
		currentTime = startTime;

		System.gc();
	}
}
