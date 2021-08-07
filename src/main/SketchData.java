/*

  	DynamoVis Animation Tool
    Copyright (C) 2016 Glenn Xavier
    UPDATED: 2021 Mert Toka

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

import utils.PointRecord;
import utils.Track;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JCheckBox;

import org.joda.time.DateTime;

import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.AbstractMapProvider;
import de.fhpotsdam.unfolding.utils.GeoUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;

public class SketchData {

	public ArrayList<DateTime> Times = null;
	public ArrayList<DateTime> All_the_Times = new ArrayList<DateTime>();
	public Hashtable<Integer, ArrayList<Integer>> Times_hash = null;
	public Location[] mapExtent;
	public List<Location> locations;
	public HashMap<String, ArrayList<Float>> fieldMinMax;

	public DateTime startTime;
	public DateTime endTime;
	public DateTime currentTime;

	public DateTime user_end_time;
	public DateTime user_start_time;

	// setting for boundary visualization
	public ArrayList<PointRecord> Bdy_hull = new ArrayList<PointRecord>(); // storing all the points that forming the
																			// convex hull
	public boolean Bdy_Viz_Enable;
	public Color Bdy_Viz_Color = new Color(255, 255, 255, 255);
	public Color Bdy_Viz_Stroke_Color = Color.RED;
	public int Bdy_Viz_Stroke_Weight = 5;
	public boolean Bdy_Viz_Panel_Close = false;
	public boolean Bdy_Viz_Changes = false;
	public boolean init_setting = true;
	public int boundaryStartYear;
	public int boundaryEndYear;
	public int boundaryStartMonth;
	public int boundaryEndMonth;
	public int boundaryStartDate;
	public int boundaryEndDate;
	public boolean boundarySelect = true;
	public boolean drawBoundary;
	public int totalTime;
	public int dataInterval;
	public String timeUnit;
	public boolean needRightMap = false;
	public boolean needLeftMap = false;
	

	// setting for enable the time selection of the time line with boundary
	// visualization
	public JCheckBox enable_check = new JCheckBox("Enable");

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
	public boolean boundaryColorToggle;

	public String strokeWeightSelection;
	public String pointColorSelection;
	public String pointSizeSelection;
	public String strokeColorSelection;
	public String selectedColorUnit;
	public String selectedStrokeWeightUnit;
	public String vectorFieldSelection;
	public String headingFieldSelection;
	public String vectorColorSelection;
	public String boundaryColorSelection;

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

	// Settings for boundary visit (Activity Space Panel)
	public boolean drawChart = false; // commented out: no longer needed
	public boolean boundary; // checking whether need to map the boundary
	public boolean drawBuffer = false; // checking whether generate buffer file
	public boolean highlight_all; // no longer needed
	public boolean highlight_fly; // checking whether visualize on the fly boundary visit
	public boolean includeFilteredPts = false;; // Allow user to select if filtered points are mapped
	public Color boundaryColor = Color.BLACK; // stroke color for convex hull boundary
	public Color highlightColor = Color.WHITE; // point highlight color
	public int highlightAlpha; // opacity for point color
	public int boundaryAlpha; // opacity for boundary color
	public int highlightWeight; // stroke weight for point
	public int boundaryWeight = 2; // stroke weight for boundary
	public int numSect; // sector number for boundary
	public boolean bvFile = false; // whether user selected generating buffer file
	public ArrayList<Integer> visitedPoints = new ArrayList<Integer>(); // int array for indicating if the point visit
																		// the boundary
	public Hashtable<Location, Integer> hullPos; // use for tracking index of points that visit boundary
	public double boundaryDist = 5; // buffer size
	public int confidenceInterval = 100; // confidence interval for filtering points
	public ArrayList<PointRecord> hull = new ArrayList<PointRecord>(); // storing all the points that forming the convex
																		// hull
	public ArrayList<PointRecord> selectedPoints; //
	public String selectedID; // selected tag from the dropdown
	public ArrayList<Integer> numVisit = new ArrayList<Integer>();; // storing the number of buffer visits
	public ArrayList<String> finalSectorName = new ArrayList<String>(); // storing the sector name
	public int finalnumSect = 0;
	public boolean updateConfidenceInterval = false;
	public boolean updateSelectedID = true;
	public ArrayList<PointRecord> clonePt = new ArrayList<PointRecord>();
	public Hashtable<String, ArrayList<PointRecord>> filteredPts = new Hashtable<String, ArrayList<PointRecord>>();
	public int filteredPoints = 0;
	public int currentNumOfPoints = 0;
	public int pointsNeedRemove;
	// End Boundary setting

	public Location mapCenter;

	public boolean legendLocked = true;
	public boolean save = false;
	public int frameCounter = 0;

	public AbstractMapProvider provider;
	public PointRecord points;

	public int selectedLineSwatch;
	public int selectedBoundarySwatch;
	public int selectedPointSwatch;
	public int selectedVectorSwatch;

	public Map<String, Integer> fieldColors = new HashMap<String, Integer>();
	public String brushedTag;

	// public int[] visitBoundary;
	// public String[] sector;

	// time Box Variables
	public int timeBoxStartYear;
	public int timeBoxStartMonth;
	public int timeBoxEndYear;
	public int timeBoxEndMonth;
	public boolean labelMonth = true; // for labeling cubes
	public boolean timeBoxBdyHighlight = false; // highlight interactions in 3D
	public boolean stbclose = false;
	public boolean boxvisible = false;

	public boolean staticBox = false;// make static box
	public Rectangle boundingBox = null;
	Map<String, Rectangle> bounds = new HashMap<String, Rectangle>();// All the bounding boxes for each track
	public ScreenPosition coords;

	// boundary interaction variable
	public boolean overbound1 = false; // test if in other boundary
	public boolean overbound2 = false;
	public boolean highlight_interaction_boundary = false;// highlight
	public boolean pointInteract = false; // test if current point is interacting
	public boolean noInteraction = true;// the two tags do not interact true
	public boolean tagChange = false;

	// Interactions variables
	public boolean multipleAnimals;// boolean for if there is data for multiple animals or not
	public String id1; // ID value of first animal selected to highlight interactions
	public String id2; // ID value of second animal selected to highlight interactions
	public String[] selectedIDs;// array of two selected IDs
	public Track track1;// track of first animal selected to highlight interactions
	public Track track2;// track of second animal selected to highlight interactions
	public float highlightSize = 15;// size of highlighted points for when animals interacting
	public float xpos;// x position of current point
	public float ypos;// y position of current point
	public boolean highlightOnFly = false;// checkbox user option. Highlights circular buffer when interacting
	public boolean highlightAllInteractions = false;// checkbox user option. Highlights all points where interacting
	public double bufferDist;// buffer distance tolerance (user selection)
	public double bufferTime;// buffer time tolerance (user selection)
	public boolean interactionsGenerated = false;// if all user selections are up to date, interactionsGenerated == true
	public boolean upToDate = false;// is false if user has changed selections since last pressing Generate Buffer
									// button
	public ArrayList<PointRecord> interactingPoints;// boolean array of all all points, true if interacting, false if
													// otherwise
	public float holdAlpha;
	//

	//
	// public Layer layer;
	public boolean layerOpt = false;
	public boolean onetime = false;
	public String geoJson;

	public void processFeatureList(HashMap<String, Track> data) {

		locations = new ArrayList<Location>();

		for (Entry<String, Track> entry : data.entrySet()) {
			Track track = entry.getValue();

			if(track.requiresLeftMap) needLeftMap = true; 
			if(track.requiresRightMap) needRightMap = true;

			for (PointRecord point : track.getPoints()) {
				locations.add(point.getLocation());
			}
		}
		mapExtent = GeoUtils.getBoundingBox(locations);
		System.out.println("Setting Map Extent to: " + mapExtent[0] + " " + mapExtent[1]);

		currentTime = startTime;

		System.gc();
	}

	// return data extent as a float array
	// {upperleft_x, upperleft_y, lowerright_x, lowerright_y}
	public float[] getExtentInFloat() {
		// 					lat, long, lat, long
		return new float[] {mapExtent[0].y, mapExtent[0].x, mapExtent[1].y, mapExtent[1].x};
	}
}
