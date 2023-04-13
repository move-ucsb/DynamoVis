/*

  	DynamoVis Animation Tool - Spacetime Cube
    Copyright (C) 2018 Kate Carlson
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

import java.awt.Rectangle;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.List;

import com.jogamp.nativewindow.WindowClosingProtocol.WindowClosingMode;

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Minutes;

import de.fhpotsdam.unfolding.geo.Location;
import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PVector;
import processing.opengl.PJOGL;
import utils.PointRecord;
import utils.Track;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.events.EventDispatcher;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;


public class Box extends PApplet {

    private PeasyCam camera; // 3D navigation

    private DesktopPane parent;
    public SketchData data;
    public int timeLengthMonths; // how long the data is in month
    public String monthLabel[] = { "", "January", "February", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December" };

    public float cubeWidth = 260;   // width
    public float cubeDepth = 260;   // depth
    public float cubeHeight = 100;  // height of each month

    // for labeling the months in space-time cube
    public float mx; // mapped x
    public float my; // mapped y
    public float z; // current z level
    public float zinterval = 3; // how much the z should change by- vary by months in day
    public float startz; // where the z starts
    public DateTime timeofReset = null; // the time when the data tracks reach the top of the screen and the boxes need
                                        // to reset
    public float resetZ = 0; // if data longer than 8 months cubes will shifts. resetZ holds how much the
                             // boxes should shift by
    public int newDay = 0;
    Map<String, Rectangle> bounds = new HashMap<String, Rectangle>();
    // edited bounding boxes that hold tests for bounding box intersection. If there
    // is an intersection the tracks will be
    // set to hold one bounding box

    public UnfoldingMap myMap;
    public boolean leftMapNeeded = false;
	public boolean rightMapNeeded = false;
	public UnfoldingMap leftMap;
	public UnfoldingMap rightMap;
    EventDispatcher eventDispatcher;

    public long durationInDays;
    public float durationInPixels;
    public float currentHeight;
    public float startHeight;

    public void setParent(DesktopPane father) {
        parent = father;
        data = parent.data;
    }

    // RUN/PAUSE/EXIT BEHAVIOURS -----------------
    public void run(int x, int y) {
        String[] processingArgs = { "--location=" + x + "," + y, "DynamoVis Space-time Cubes" };
        PApplet.runSketch(processingArgs, this);
    }
    // Overriden to prevent System.exit(0) command, that
    // shuts down the whole java environment
    @Override
    public void exitActual() {
        // minimize the window if it doesn't get disposed
        setVisible(false);

        // clean flags
        parent.box = null;
    }

    public void setVisible(boolean flag) {
        // minimize the window if it doesn't get disposed
        getSurface().setVisible(flag);
        parent.timeBoxCheck.setSelected(flag);
        data.boxvisible = flag;

        if(flag) {
            loop();
        }
        else {
            noLoop();
        }
    }
    ////

    public void settings() {
        size(500, 850, P3D);
        // Set window icon
        String iconFilename = DesktopPane.isMacOSX() ? "logo1024.png" : "logo32.png";
        URL res = parent.getClass().getClassLoader().getResource(iconFilename);
        if (res.getProtocol().equals("jar")) {
            PJOGL.setIcon(iconFilename); // jar file contains the resource in its root directory
        } else {
            PJOGL.setIcon(res.getPath()); // source compilation can find the resource in bin directory
        }
    }

    public void setup() {
        // timeLengthMonths = findTimeInterval();// find the data's time range in months
        // find_intersect_bound(); // set bounds
		colorMode(HSB, 360, 100, 100);
        frameRate(60);

        // Adjust cube ratio based on the data
        float[] extent = data.getExtentInFloat();
        // TODO: do a better world coverage
        cubeWidth = (extent[2]-extent[0])/(extent[1]-extent[3]) * cubeDepth;
        
        // setup camera navigation
        camera = new PeasyCam(this, 0, 0, 0, 1000);   
        
        // Closes only the sketch on exit
        if (getGraphics().isGL() && !DesktopPane.isMacOSX()) {
            final com.jogamp.newt.Window w = (com.jogamp.newt.Window) getSurface().getNative();
            w.setDefaultCloseOperation(WindowClosingMode.DISPOSE_ON_CLOSE);
        }

        // UnfoldingMap(processing.core.PApplet p, float x, float y, float width, float height, AbstractMapProvider provider)
        //creates map with specific position and dimension
        leftMapNeeded = data.needLeftMap;
		rightMapNeeded = data.needRightMap;
        UnfoldingMap starterMap = new UnfoldingMap(this, 0,0, cubeWidth, cubeDepth, parent.sketch.map.mapDisplay.getMapProvider());
        Location wrapPoint = new Location(0f, 180f);

        starterMap.zoomAndPanToFit(data.locations);
		eventDispatcher = MapUtils.createDefaultEventDispatcher(this, starterMap);
		starterMap.zoomAndPanToFit(data.locations); 

        if (leftMapNeeded || rightMapNeeded) {
            //first need to find where the next wrap will begin if left or right map required
            ScreenPosition screenWrap = starterMap.getScreenPosition(wrapPoint);
            // adjust width to screenwrap position... must come after creating initial map to get the screenPosition
            //maybe shouldn't start at 0,0 for left map
            System.out.println("screenmWrapX: "+ screenWrap.x);
            System.out.println("cubeWidth: "+ cubeWidth);
            float offset = 0;
            if (leftMapNeeded) {
                offset = cubeWidth-screenWrap.x;
            }
            myMap = new UnfoldingMap(this, offset,0, screenWrap.x, cubeDepth, parent.sketch.map.mapDisplay.getMapProvider()); 
            // List<Location> editedLocs = data.locations;
            List<Location> editedLocs = cleanMiddleData(data.locations, leftMapNeeded);
            myMap.zoomAndPanToFit(editedLocs);
            eventDispatcher = MapUtils.createDefaultEventDispatcher(this, myMap);
            myMap.zoomAndPanToFit(editedLocs); // sometimes the first attempt zooms too far and bugs out, so do it again
            // extra maps
            if(leftMapNeeded) leftMap = createWrappedMap(myMap, eventDispatcher, 1, screenWrap.x);
            if(rightMapNeeded) rightMap = createWrappedMap(myMap, eventDispatcher, 2, screenWrap.x);
        } else {
            myMap = starterMap;
        }

    }

    /// extra map functions
	// https://github.com/tillnagel/unfolding/commit/46d03cf6ebc6e01a35dc0aede0a02b428b9cf68d
	public UnfoldingMap createWrappedMap(UnfoldingMap mainMap, EventDispatcher eventDispatcher, int id, float offset) {
		float x = id==1 ? -cubeWidth : cubeWidth;
        float width = cubeWidth-offset;
        System.out.println("width: " + cubeWidth);
        System.out.println("offset: "+ offset);
        System.out.println("cubeWidth-offset: "+ width);
        //TODO: fix left wrapping map
		// UnfoldingMap wrappedMap = new UnfoldingMap(this, Integer.toString(id), 0, 0, cubeWidth, cubeDepth, false, false, parent.sketch.map.mapDisplay.getMapProvider());
		UnfoldingMap wrappedMap = new UnfoldingMap(this, Integer.toString(id), 0, 0, cubeWidth-offset, cubeDepth, false, false, parent.sketch.map.mapDisplay.getMapProvider());
        wrappedMap.zoomToLevel(mainMap.getZoomLevel());
		eventDispatcher.register(wrappedMap, "zoom", mainMap.getId());
		return wrappedMap;
	}

    private List<Location> cleanMiddleData(List<Location> original, boolean left) {
        float degree = (left) ? -180 : 180;
        List<Location> edited = new ArrayList<>();
        for (Location loc : original) {
            // System.out.println("loc: " + loc);
            if (left) {
                if (loc.y > degree) {
                    edited.add(loc);
                }
            } else {
                if (loc.y < 180) {
                    edited.add(loc);
                }
            }
        }
        return edited;
    }

    public void draw() {
        background(0);// black background
        // DrawGizmo(100, 50, false); // DEBUG -- draws the origin of coordinate system

        // rotate the view 
        if(data.rotateView) {
            camera.rotateY(0.001f);
        }

        // draw the encapsulating space-time cubes
        drawBox();

        for (Entry<String, Track> entry : parent.trackList.entrySet()) {
			// 
			String key = entry.getKey();
			Track track = entry.getValue();

			// if the data is not showing, skip draw the path
			if (!track.getVisibility()) continue;

			int color = parent.colors.getTagColor(key).getRGB();
			ArrayList<PointRecord> points = track.getPoints();

			// Underlay
			if(data.ghost) {
				pushStyle();
				beginShape();
				noFill();
				stroke(data.ghostColor.getRGB(), data.ghostAlpha);
				strokeWeight(data.ghostWeight);

				for (int i = 0; i < points.size(); i++) {
					PointRecord marker = points.get(i);
					DateTime markerTime = marker.getTime();
					if (markerTime.isBefore(data.currentTime) || markerTime.isEqual(data.currentTime)) {
						Location pos = marker.getLocation();// get the point's location

                        // extent minx, maxy, maxx, miny
                        float[] extent = data.getExtentInFloat();
                        mx = map(pos.y, extent[0], extent[2], -cubeWidth/2, cubeWidth/2);
                        my = map(pos.x, extent[3], extent[1], cubeDepth/2, -cubeDepth/2);
                        float mheight = getPointHeightBasedOnTime(markerTime);
	
						vertex(mx, mheight, my);
					}
				}
				endShape(); // draws underlay
				popStyle();
			}

			// Track visualizations
			if (data.strokeColorToggle || (data.strokeWeightToggle && data.strokeWeightSelection != null)) {
				pushStyle();
				beginShape();
				noFill();
				strokeWeight(4);
				strokeJoin(ROUND);  // make line connections rounded
				for (int i = 0; i < points.size(); i++) {
					PointRecord marker = points.get(i);
					DateTime markerTime = marker.getTime();
					if (markerTime.isBefore(data.currentTime) || markerTime.isEqual(data.currentTime)) {
						Location pos = marker.getLocation();// get the point's location

                        // extent minx, maxy, maxx, miny
                        float[] extent = data.getExtentInFloat();
                        mx = map(pos.y, extent[0], extent[2], -cubeWidth/2, cubeWidth/2);
                        my = map(pos.x, extent[3], extent[1], cubeDepth/2, -cubeDepth/2);
                        float mheight = getPointHeightBasedOnTime(markerTime);

						int hours;
						if (data.timeUnit.equals("minutes")) {
							hours = Hours.hoursBetween(markerTime, data.currentTime).getHours();
						} else {
							hours = Minutes.minutesBetween(markerTime, data.currentTime).getMinutes();
						}
						float alpha = 255;
						if (data.falloff) {
							alpha = constrain(map(hours, 0, data.alphaMaxHours, 255, 0), 0, 255);
						}

						if (hours <= 24) {
							stroke(360, 100, 100, alpha);
						} else if (hours <= 48) {
							stroke(360, 100, 63, alpha);
						} else if (hours <= 72) {
							stroke(360, 100, 30, alpha);
						}

						if (alpha != 0) {
							if (data.strokeWeightToggle && data.strokeWeightSelection != null) {
								String strokeWeightVar = data.strokeWeightSelection;
								float strokeWeightValue = (Float) marker.getProperty(strokeWeightVar);
								float strokeWeight = map(strokeWeightValue, parent.attributes.getMin(strokeWeightVar),
										parent.attributes.getMax(strokeWeightVar), data.strokeWeightMin,
										data.strokeWeightMax);
								strokeWeight(strokeWeight);
							}

							if (data.strokeColorToggle) {
								String strokeColorVar = data.strokeColorSelection;
								if (strokeColorVar.equals(parent.attributes.getIndex())) {
									stroke(color, alpha);
								} else {
									float strokeColorValue = (Float) marker.getProperty(strokeColorVar);
									float strokeColorPercent = norm(strokeColorValue,
											parent.attributes.getMin(strokeColorVar),
											parent.attributes.getMax(strokeColorVar));
									int strokeColor = parent.colors.coloursCont.get(data.selectedLineSwatch)
											.findColour(strokeColorPercent);
									stroke(strokeColor, alpha);
								}
                                vertex(mx, mheight, my);
							}
						}
					}
				}
				endShape(); // draw tracks				
				popStyle();
			}

			// Point or vector visualizations
			if(data.vectorToggle || data.pointColorToggle) {
				for (int i = 0; i < points.size(); i++) {
					PointRecord marker = points.get(i);
					DateTime markerTime = marker.getTime();
					if (markerTime.isBefore(data.currentTime) || markerTime.isEqual(data.currentTime)) {
						Location pos = marker.getLocation();// get the point's location

                        // extent minx, maxy, maxx, miny
                        float[] extent = data.getExtentInFloat();
                        mx = map(pos.y, extent[0], extent[2], -cubeWidth/2, cubeWidth/2);
                        my = map(pos.x, extent[3], extent[1], cubeDepth/2, -cubeDepth/2);
                        float mheight = getPointHeightBasedOnTime(markerTime);

						int hours;
						if (data.timeUnit.equals("minutes")) {
							hours = Hours.hoursBetween(markerTime, data.currentTime).getHours();
						} else {
							hours = Minutes.minutesBetween(markerTime, data.currentTime).getMinutes();
						}
						float alpha = 255;
						if (data.falloff) {
							alpha = constrain(map(hours, 0, data.alphaMaxHours, 255, 0), 0, 255);
						}

						if (hours <= 24) {
							stroke(360, 100, 100, alpha);
						} else if (hours <= 48) {
							stroke(360, 100, 63, alpha);
						} else if (hours <= 72) {
							stroke(360, 100, 30, alpha);
						}

						if (alpha != 0) {
							if (data.pointColorToggle) {
								pushStyle();
								float size = 7;
								String pointColorVar = data.pointColorSelection;
								if (pointColorVar.equals(parent.attributes.getIndex())) {
									stroke(color, alpha);
								} else {
									float pointColorValue = (Float) marker.getProperty(pointColorVar);
									float pointColorPercent = norm(pointColorValue,
											parent.attributes.getMin(pointColorVar),
											parent.attributes.getMax(pointColorVar));
									int strokeColor = parent.colors.coloursCont.get(data.selectedPointSwatch)
											.findColour(pointColorPercent);
                                    stroke(strokeColor, alpha);
								}
								if (data.pointSizeToggle) {
									String pointSizeVar = data.pointSizeSelection;
									float pointSizeValue = (Float) marker.getProperty(pointSizeVar);
									float pointSize = map(pointSizeValue, parent.attributes.getMin(pointSizeVar),
											parent.attributes.getMax(pointSizeVar), data.pointSizeMin,
											data.pointSizeMax);
									size = pointSize;
								}
                                strokeWeight(size);
                                point(mx, mheight, my);// draw point
								popStyle();
							}

							if (data.vectorToggle) {
								pushStyle();
								String vectorFieldVar = data.vectorFieldSelection;
								float radius = (Float) marker.getProperty(vectorFieldVar);
								float length = 10;
								if (data.vectorLengthToggle) {
									length = map(radius, parent.attributes.getMin(vectorFieldVar),
											parent.attributes.getMax(vectorFieldVar), data.vectorLengthMin,
											data.vectorLengthMax);
								}
								float heading = (Float) marker.getProperty(data.headingFieldSelection);
								float x = cos(radians(heading)) * length;
								float y = sin(radians(heading)) * length;
								if (data.vectorColorToggle) {
									String vectorColorVar = data.vectorColorSelection;
									if (vectorColorVar.equals(parent.attributes.getIndex())) {
										stroke(color, alpha);
									} else {
										float vectorColorValue = (Float) marker.getProperty(vectorColorVar);
										float vectorColorPercent = norm(vectorColorValue,
												parent.attributes.getMin(vectorColorVar),
												parent.attributes.getMax(vectorColorVar));
										int vectorColor = parent.colors.coloursCont.get(data.selectedVectorSwatch)
												.findColour(vectorColorPercent);
										stroke(vectorColor, alpha);
									}
								} else {
									stroke(0, 0, 100, alpha);
								}
								strokeWeight(2);
                                line(mx, mheight, my, mx+x, mheight, my+y);
								popStyle();
							}
						}
					}
				}
			}
		}

        // Export spacetime cube -- do we need exporting spacetime cube?
        // if (data.save) {
        //     String file = String.format("temp/" + parent.animationTitle + parent.exportCounter + "_3D/temp%08d.jpeg",
        //             data.frameCounter);
        //     saveFrame(file);
        //     data.frameCounter++;
        // }

    }// end draw()

    private void drawBaseMap(float currentHeight){
        pushMatrix();
        translate(-cubeWidth/2, currentHeight, -cubeDepth/2);
        rotateX(radians(90));
        // if(leftMapNeeded) Sketch.updateMap(myMap, leftMap, true);
		if(rightMapNeeded) Sketch.updateMap(myMap, rightMap, false);
        try {
            myMap.draw();
            if(leftMapNeeded) leftMap.draw();
            if(rightMapNeeded) rightMap.draw();
        } //this sometimes throws a null pointer exception
        catch(Exception e) {
            System.out.println("map error: "+ e);
        }
        popMatrix();
    }

    // DRAW BOX OUTLINE
    // ==================
    private void drawLines(int steps, PVector inc, PVector start, PVector end) {
        for (int i=0; i<=steps; i++) {
            PVector point = PVector.add(start, PVector.mult(inc, i));
            PVector endPoint = PVector.add(end, PVector.mult(inc, i));
            // from here
            PVector dist = PVector.sub(endPoint, point);
            PVector step = PVector.div(dist, steps*4);
            PVector firstEnd = PVector.add(point, PVector.div(step, 2));
            for (int j=0; j<steps*4; j++) {
                line(point.x, point.y, point.z, firstEnd.x, firstEnd.y, firstEnd.z);
                point.add(step);
                firstEnd.add(step);
            }
            // to here for dashed lines
            // line(point.x, point.y, point.z, endPoint.x, endPoint.y, endPoint.z); //for solid lines
        }
    }

    private void drawLatLongGrid(int steps_width, int steps_depth, PVector[] corners, boolean dimText, boolean first) {
        float[] extent = data.getExtentInFloat();

        textSize(16);
        fill(255, dimText ? data.dimAlpha+20 : data.normalAlpha+20);
        String label;

        //draws the lines and labels for longitude
        PVector inc = PVector.sub(corners[0], corners[1]).div(steps_depth);
        drawLines(steps_depth, inc, corners[1], corners[2]);
        if (first) {
            for(int i=0; i<= steps_depth; i++){
                PVector point = PVector.add(corners[1], PVector.mult(inc, i));
                PVector end = PVector.add(corners[2], PVector.mult(inc, i));
                label = String.format("%.2f", map(point.z, corners[0].z, corners[1].z, extent[3], extent[1]));
                text(label, end.x+10, end.y, end.z);
            }
        }
        
        //draws the lines and labels for latitude
        inc = PVector.sub(corners[1], corners[2]).div(steps_depth);
        drawLines(steps_depth, inc, corners[2], corners[3]);
        if (first) {
            for(int i=0; i<= steps_depth; i++){
                PVector point = PVector.add(corners[2], PVector.mult(inc, i));
                label = String.format("%.2f", map(point.x, corners[1].x, corners[2].x, extent[0], extent[2]));
                pushMatrix();
                translate(point.x, point.y, point.z);
                rotateY(radians(45)); //rotating the text for readability, without this some numbers will run into each other
                text(label, 0,0, 0);
                popMatrix();
            } 
        }
    }

    //draws lines from all the corners and around top/bottom
    private void drawSolidOutline(PVector[] corners) {
        line(corners[0].x, corners[0].y, corners[0].z, corners[1].x, corners[1].y, corners[1].z);
        line(corners[1].x, corners[1].y, corners[1].z, corners[2].x, corners[2].y, corners[2].z);
        line(corners[2].x, corners[2].y, corners[2].z, corners[3].x, corners[3].y, corners[3].z);
        line(corners[0].x, corners[0].y, corners[0].z, corners[3].x, corners[3].y, corners[3].z);
    }

    private void drawCornerLines(PVector[] corners) {
        line(corners[0].x, corners[0].y, corners[0].z, corners[0].x, corners[0].y+cubeHeight, corners[0].z);
        line(corners[1].x, corners[1].y, corners[1].z, corners[1].x, corners[1].y+cubeHeight, corners[1].z);
        line(corners[2].x, corners[2].y, corners[2].z, corners[2].x, corners[2].y+cubeHeight, corners[2].z);
        line(corners[3].x, corners[3].y, corners[3].z, corners[3].x, corners[3].y+cubeHeight, corners[3].z);
    }

    private void drawDayLabels(YearMonth currentYearMonth) {
        int dayNum = currentYearMonth.lengthOfMonth();
        String monthName = monthLabel[currentYearMonth.getMonthValue()].substring(0, 3);
        float dy = -cubeHeight/dayNum;
        for (int i=1; i<=dayNum; i++){
            line((cubeWidth/2)-2.5f, dy*(i-1), cubeDepth/2, (cubeWidth/2)+2.5f, dy*(i-1), cubeDepth/2); //can adjust length of line for visual appeal
            if (i%5 == 0) {
                String label = monthName + " " + i;
                text(label, (cubeWidth/2)+10, dy*(i-1), cubeDepth/2);
            }
        }
    }

    // draws the box outline
    // makes the camera facing edges more transparent
    public boolean drawOutline(int w, int h, int d, int off, boolean last, boolean first) {
        float[] camera_position = camera.getPosition(); // temp

        PVector pos = new PVector(camera_position[0],camera_position[2],camera_position[2]);
        PVector[] corners = {new PVector(-w/2, 0 ,d/2), 
                             new PVector(-w/2, 0 ,-d/2),
                             new PVector(w/2, 0,-d/2),  
                             new PVector(w/2, 0,d/2)};  
        boolean[] dim = {false, false, false, false};
        
        float[] cam_distances = {pos.dist(corners[0]),
            pos.dist(corners[1]),
            pos.dist(corners[2]),
            pos.dist(corners[3])};
        float min_distance = min(cam_distances);
        if(min_distance == cam_distances[0]) {dim[0]=true;dim[3]=true;}
        else if(min_distance == cam_distances[1]) {dim[0]=true;dim[1]=true;}
        else if(min_distance == cam_distances[2]) {dim[1]=true;dim[2]=true;}
        else if(min_distance == cam_distances[3]) {dim[2]=true;dim[3]=true;}
        
        int steps_depth = 5;
        int steps_width = (int)(w/d*steps_depth);

        stroke(255,dim[3] ? data.dimAlpha : data.normalAlpha);
        if (data.labelLatLong) {
            drawLatLongGrid(steps_width, steps_depth, corners, dim[2] && dim[3], first);
        }

        if (data.boxSkeleton) {
            if (first) {
                drawSolidOutline(corners);
                startHeight = currentHeight;
            } else {
                drawCornerLines(corners);
            }
            if (last) {
                pushMatrix(); 
                translate(0, -h, 0);
                drawSolidOutline(corners);
                popMatrix();
            }
        }

        if(last) {pushMatrix(); translate(0, -h, 0); drawOutline(w,h,d,off,false,false); popMatrix();}

        return dim[2] && dim[3];
    }
    // TODO: make into a PShape
    // draws all boxes for all months
    public void drawBox() {
        // used for iterating year-month pairs
        YearMonth currentYearMonth = YearMonth.of(data.timeBoxStartYear, data.timeBoxStartMonth);
        YearMonth endYearMonth = YearMonth.of(data.timeBoxEndYear, data.timeBoxEndMonth);
        YearMonth startYearMonth = currentYearMonth;
        int numMonths = (int) startYearMonth.until(endYearMonth, ChronoUnit.MONTHS)+1;
        
        // used for calculating total number of days of the whole timeline
        LocalDate startDate = LocalDate.of(data.timeBoxStartYear, data.timeBoxStartMonth, 1);
        LocalDate endDate = LocalDate.of(data.timeBoxEndYear, data.timeBoxEndMonth, endYearMonth.lengthOfMonth());
        
        durationInDays = startDate.until(endDate, ChronoUnit.DAYS);
        durationInPixels = cubeHeight/30.0f*durationInDays;
        currentHeight = durationInPixels/2;

        if (data.basemap) {
            drawBaseMap(currentHeight);
        }

        // if (data.basemap) { //why getting called twice?
        //     drawBaseMap(currentHeight);
        // }

        // iterate until we pass the endYearMonth
        while(!currentYearMonth.equals(endYearMonth.plusMonths(1))) {
            pushMatrix();
            translate(0, currentHeight, 0);
            noFill();
            strokeWeight(1);

            int numberOfDaysInCurrentMonth = currentYearMonth.lengthOfMonth();
            int adjustedCubeHeight = (int)(cubeHeight/30.0f*numberOfDaysInCurrentMonth);

            boolean dimText = drawOutline((int)cubeWidth, (int)adjustedCubeHeight, (int)cubeDepth, 
                                5,      // plus sign offset 
                                currentYearMonth.equals(endYearMonth), currentYearMonth.equals(startYearMonth)); // draw the cap if its the last month

            //check zoom level
            double zoomDist = camera.getDistance();
            if (data.labelTime){
                if (zoomDist <= 1000) {
                    data.labelDay = true;
                    data.labelMonth = false;
                } else {
                    data.labelDay = false;
                    data.labelMonth = true;
                }
            }
            if(data.labelMonth) {
                textSize(16);
                fill(255, dimText ? data.dimAlpha+20 : data.normalAlpha+20);

                // Draw month labels as "Aug 2021"
                String label = monthLabel[currentYearMonth.getMonthValue()].substring(0, 3)
                    + " "
                    + currentYearMonth.getYear();
                text(label, cubeWidth/2+10, -adjustedCubeHeight/2, cubeDepth/2);// month drawn to side of the cube
            }
            if (data.labelDay) {
                textSize(12);
                fill(255, dimText ? data.dimAlpha+20 : data.normalAlpha+20);

                //Draw day labels as "1 Aug"
                drawDayLabels(currentYearMonth);
            }

            popMatrix();

            currentHeight -= adjustedCubeHeight;
            currentYearMonth = currentYearMonth.plusMonths(1);
        }

        // start-current-end time indicators
        pushStyle();
        rectMode(CORNERS);
        noFill();

        stroke(255, 10);
        float h = getPointHeightBasedOnTime(data.startTime);
        pushMatrix(); rotateX(HALF_PI); translate(0,0,-h); rect(cubeWidth/2, cubeDepth/2, -cubeWidth/2, -cubeDepth/2); popMatrix();
        h = getPointHeightBasedOnTime(data.endTime);
        pushMatrix(); rotateX(HALF_PI); translate(0,0,-h); rect(cubeWidth/2, cubeDepth/2, -cubeWidth/2, -cubeDepth/2); popMatrix();
        popStyle();
    }
    // end drawbox outline

    // gives the height of the point -- used for translating the point
    public float getPointHeightBasedOnTime(DateTime t) {
        LocalDateTime time = LocalDateTime.of(t.getYear(), t.getMonthOfYear(), t.getDayOfMonth(), t.getHourOfDay(), t.getMinuteOfHour(), t.getSecondOfMinute());

        YearMonth endYearMonth = YearMonth.of(data.timeBoxEndYear, data.timeBoxEndMonth);
        LocalDateTime startDate = LocalDateTime.of(data.timeBoxStartYear, data.timeBoxStartMonth, 1, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(data.timeBoxEndYear, data.timeBoxEndMonth, endYearMonth.lengthOfMonth(), 23, 59, 59);
        
        long totalDurationInDays = startDate.until(endDate, ChronoUnit.DAYS);
        float totalDurationInPixels = cubeHeight/30.0f*totalDurationInDays;

        long durationInMinutes = startDate.until(time, ChronoUnit.MINUTES);

        return map(durationInMinutes, 0, totalDurationInDays*24*60, totalDurationInPixels/2, -totalDurationInPixels/2);        
    }

    // find number of months in time range
    public int findTimeInterval() {
        int months = 0;
        if (data.timeBoxStartYear == data.timeBoxEndYear)// same year
        {
            months = data.timeBoxEndMonth - data.timeBoxStartMonth + 1;// months in between, inclusive
        } else // not in the same year
        {
            int sy = data.timeBoxStartYear;
            int sm = data.timeBoxStartMonth;
            // count up the number of months between years
            while (sy != data.timeBoxEndYear || sm != data.timeBoxEndMonth) {
                if (sm < 12) {
                    months++;
                    sm++;
                } else if (sm == 12) {
                    months++;
                    sy++;
                    sm = 1;
                }
            }
            months++; // add one more to be inclusive
        }
        return months;
    }

    // find months between the marker time and the data tracks start month
    // TODO: just last else if should be enough
    public int monthsBetween(DateTime mTime)// Includes partial months
    {
        int mm = mTime.getMonthOfYear();// first data point month
        int my = mTime.getYear();// first data point year
        if (data.timeBoxStartMonth == mm && data.timeBoxStartYear == my)// same year same month
        {
            return 0;
        } else if (data.timeBoxStartMonth != mm && data.timeBoxStartYear == my)// same year different month
        {
            return mm - data.timeBoxStartMonth;
        } else if (data.timeBoxStartMonth == mm && data.timeBoxStartYear != my)// different year same month
        {
            return (my - data.timeBoxStartYear) * 12;
        } else if (data.timeBoxStartMonth != mm && data.timeBoxStartYear != my)// different year different month
        {
            return ((my - data.timeBoxStartYear) * 12) + Math.abs(mm - data.timeBoxStartMonth);
        }
        return -1;// should never get here
    }

    // find where bounding boxes intersect and assign one to both tracks if they do
    public void find_intersect_bound() {
        // goes through the track list
        // bounding boxes kept in a dictionary based upon track dictionary
        // go through the track key and find bounding box rectangle value
        for (Entry<String, Track> i : parent.trackList.entrySet()) {
            String ki = i.getKey();// bound key1
            Rectangle vi = data.bounds.get(ki);// bounding box1 (rectangle)

            // go through each track so can compare bounds and find overlaps
            for (Entry<String, Track> j : parent.trackList.entrySet()) {
                String kj = j.getKey();// bound key1
                Rectangle vj = data.bounds.get(kj);// bounding box2

                if (!bounds.containsKey(ki))// new key
                {
                    // new key so put in bound copy
                    bounds.put(ki, vi);
                }
                if (!bounds.containsKey(kj))// new key
                {
                    // new key so put in bound copy
                    bounds.put(kj, vj);
                } else if (vi.intersects(vj))// bounding rectangles intersect
                {
                    // both have the same boxes since they intersect
                    // put in bound copy with the same value
                    // overwrites above if statement if both new and in an intersection
                    bounds.put(ki, vi);
                    bounds.put(kj, vi);
                }
            }
        }
    }

    // DRAW UTILITIES
    //
    // Draws the X,Y,Z lines with R,G,B respectively
    // private void DrawGizmo(float scale, float alpha, boolean drawPlanes) {
    //     pushStyle();
    //     colorMode(HSB, 255, 255, 255);

    //     float planeScale = scale * 0.6f;
    //     float planeOpacity = alpha * 0.5f;  
    //     rectMode(CORNER);
    //     strokeWeight(5);
    //     pushMatrix();
        
    //     // xy plane
    //     if(drawPlanes){
    //         noStroke();
    //         fill(0,200,200,planeOpacity);
    //         rect(0,0,planeScale,planeScale);
    //     }
    //     // x axis 
    //     stroke(0,200,200,alpha);
    //     line(0,0,0, scale, 0, 0);
        
    //     // yz plane
    //     if(drawPlanes){
    //         pushMatrix();
    //         rotateY(-PI/2);
    //         noStroke();
    //         fill(255/3,200,200,planeOpacity);
    //         rect(0,0,planeScale,planeScale);
    //         popMatrix();
    //     }
    //     // y axis
    //     stroke(255/3,200,200,alpha);
    //     line(0,0,0, 0, scale, 0);
        
    //     // xz plane
    //     if(drawPlanes){
    //         pushMatrix();
    //         rotateX(PI/2);
    //         noStroke();
    //         fill(255*2/3,200,200,planeOpacity);
    //         rect(0,0,planeScale,planeScale);
    //         popMatrix();
    //     }
    //     // z axis
    //     stroke(255*2/3,200,200,alpha);
    //     line(0,0,0, 0, 0, scale);
        
    //     popMatrix();
    //     popStyle();
    // }
    ////////////////////////////////////////////////////////////////////////////////////////
  

}
