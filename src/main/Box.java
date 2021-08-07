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

import java.awt.Color;
import java.awt.Rectangle;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.jogamp.nativewindow.WindowClosingProtocol.WindowClosingMode;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;

import de.fhpotsdam.unfolding.geo.Location;
import peasy.CameraState;
import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PJOGL;
import utils.PointRecord;
import utils.Track;


public class Box extends PApplet {

    private PeasyCam camera; // 3D navigation

    private DesktopPane parent;
    public SketchData data;
    public int timeLengthMonths; // how long the data is in month
    public String monthLabel[] = { "", "January", "February", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December" };

    // TODO: change proportions based on the extent
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

    // RUN/PAUSE/EXIT BEHAVIOURS -----------------
    public void run(int x, int y) {
        String[] processingArgs = { "--location=" + x + "," + y, "DynamoVis Animation" };
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
        frameRate(60);

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
    }

    public void draw() {
        background(0);// black background
        // DrawGizmo(300, 10, false); // DEBUG -- draws the coordinate system

        drawBox(); // make space-time cubes

        // go through each data track uploaded
        for (Entry<String, Track> entry : parent.trackList.entrySet()) {

            String key = entry.getKey(); // the track ID
            Track track = entry.getValue(); // the data track points

            // if the data is not showing, skip draw the path
            if (!track.getVisibility()) continue;

            // set up the shape to hold the data path
            PShape timePath = createShape();
            timePath.beginShape();
            timePath.noFill();
            timePath.strokeWeight(4);

            PShape brush = createShape();
            boolean brushed = false;

            if (data.brushedTag != null && data.brushedTag.equals(key)) {
                brushed = true;
                brush.beginShape();
                brush.noFill();
                brush.stroke(180, 100, 100);
                brush.strokeWeight(6);
            }

            int color = parent.colors.getTagColor(key).getRGB();// retrieve the default color of the track
            ArrayList<PointRecord> points = track.getPoints();// list of all of the data points for the track

            // go through each point in the data track
            for (int i = 0; i < points.size() - 1; i++) {
                PointRecord marker = points.get(i);// the data point
                DateTime markerTime = marker.getTime();// get the point's time

                // visualize the data point only if the point occurs before or at the current
                // animation time
                if (markerTime.isBefore(data.currentTime) || markerTime.isEqual(data.currentTime)) {
                    Location pos = marker.getLocation();// get the point's location

                    int days = Days.daysBetween(points.get(0).getTime(), markerTime).getDays();// number of days
                                                                                                // that have passed
                                                                                                // since the first
                                                                                                // day
                    float daysinmonth = markerTime.dayOfMonth().getMaximumValue();// find what month the point is in
                                                                                    // and find how many days are in
                                                                                    // that month
                    zinterval = (float) (100.0 / daysinmonth);// increment amount for z- height of cube/days in
                                                                // month

                    // find where z starts
                    // need if the data doesn't start at the first day of the month
                    if (i == 0) {
                        int monthdiff = monthsBetween(markerTime);
                        if (monthdiff == 0) {
                            // (gap between box and screen edge) - (data start date * interval based on days
                            // in month)
                            z = (height - 10) - (markerTime.getDayOfMonth() * zinterval);// location of the first z
                            startz = z;// set the start
                        } else {
                            float monthinterval = 0;
                            float minterval = 0;
                            DateTime tempStartTime = new DateTime(data.timeBoxStartYear, data.timeBoxStartMonth, 1,
                                    1, 1);

                            // goes through each month
                            while (tempStartTime.getMonthOfYear() != markerTime
                                    .getMonthOfYear()/*
                                                        * && tempStartTime.getYearOfCentury()!=markerTime.
                                                        * getYearOfCentury()
                                                        */) {
                                minterval = (float) (100.0 / (tempStartTime.dayOfMonth().getMaximumValue()));// interval
                                                                                                                // based
                                                                                                                // upon
                                                                                                                // month
                                                                                                                // currently
                                                                                                                // in
                                monthinterval = monthinterval
                                        + (minterval * markerTime.dayOfMonth().getMaximumValue());// add up how much
                                                                                                    // to increase per
                                                                                                    // month
                                tempStartTime = tempStartTime.plusMonths(1);// increment months
                            }
                            z = (height - 10 - monthinterval) - (markerTime.getDayOfMonth() * zinterval); // location
                                                                                                            // of the
                                                                                                            // first z
                            startz = z;// set the start value
                        }
                    }

                    if (newDay < days) {
                        // new day so increment z
                        z = (startz) - days * zinterval;// based on the number of days that has passed * increment
                    }
                    newDay = days;// reset new day either same day or a new day

                    // scale the data
                    // float ubx = (float) (bounds.get(entry.getKey()).getMaxX() / 1000);// max x of data
                    // float lbx = (float) (bounds.get(entry.getKey()).getMinX() / 1000);// min x of data
                    // float uby = (float) (bounds.get(entry.getKey()).getMaxY() / 1000);// max y of data
                    // float lby = (float) (bounds.get(entry.getKey()).getMinY() / 1000);// min y of data
                    
                    // extent minx, maxy, maxx, miny
                    float[] extent = data.getExtentInFloat();
                    // Processing map function to map to cube
                    mx = map(pos.x, extent[0], extent[2], -cubeWidth/2, cubeWidth/2);
                    my = map(pos.y, extent[3], extent[1], -cubeDepth/2, cubeDepth/2);

                    int hours;
                    if (data.timeUnit.equals("minutes")) {
                        hours = Hours.hoursBetween(markerTime, data.currentTime).getHours();
                    } else {
                        hours = Minutes.minutesBetween(markerTime, data.currentTime).getMinutes();
                    }

                    // find if track is at top of panel
                    if (z <= 25)// at the top of the screen
                    {
                        // goes off screen so resets at the bottom
                        resetZ = height - 10;
                        // data.resetBox = true;
                        timeofReset = markerTime;// marker time when reset happens
                    }

                    // before the reset so goes back to the original position
                    if (timeofReset == null || timeofReset.isAfter(data.currentTime)) {
                        // data.resetBox = false;
                        resetZ = 0;
                    }

                    // color fade
                    float alpha = 255;
                    if (data.falloff) {
                        alpha = constrain(map(hours, 0, data.alphaMaxHours, 255, 0), 0, 255);
                    }
                    if (hours <= 24) {
                        fill(360, 100, 100, alpha);
                        timePath.stroke(360, 100, 100, alpha);
                    } else if (hours <= 48) {
                        fill(360, 100, 63, alpha);
                        timePath.stroke(360, 100, 63, alpha);
                    } else if (hours <= 72) {
                        fill(360, 100, 30, alpha);
                        timePath.stroke(360, 100, 30, alpha);
                    }

                    // update all interactions
                    // if ((track.getTag().equals(data.selectedIDs[0]) || track.getTag().equals(data.selectedIDs[1]))
                    //         && ((data.interactionsGenerated && data.upToDate))) {
                    //     // check to see if points are close enough to be interacting
                    //     if (data.interactingPoints != null && data.interactingPoints.contains(points.get(i))) {
                    //         // check to make sure user has selected to highlight all interactions
                    //         // data.pointInteract = true;//for highlightOnFly feature
                    //         if (data.highlightAllInteractions) {
                    //             // draw highlighted points for all valid interactions
                    //             pushMatrix();
                    //             translate((width / 2), 0 + resetZ, -145);
                    //             rotateY(radians(90));
                    //             stroke(Color.ORANGE.getRGB(), 100);
                    //             strokeWeight(20);
                    //             point(mx, z, my);
                    //             popMatrix();
                    //         }
                    //     }
                    // }

                    // if visible aka data drawing but no faded
                    if (alpha != 0) {
                        // visual variables vectors
                        if (data.vectorToggle) {
                            String vectorFieldVar = data.vectorFieldSelection;
                            float radius = (Float) marker.getProperty(vectorFieldVar);
                            float length = 10;
                            if (data.vectorLengthToggle) {
                                length = map(radius, parent.attributes.getMin(vectorFieldVar),
                                        parent.attributes.getMax(vectorFieldVar), data.vectorLengthMin,
                                        data.vectorLengthMax);
                            }
                            float heading = (Float) marker.getProperty(data.headingFieldSelection);
                            float x = cos(radians(heading)) * length;// end of the vector
                            float y = sin(radians(heading)) * length;// end of the vector
                            if (data.vectorColorToggle) {
                                String vectorColorVar = data.vectorColorSelection;// color of vector
                                if (vectorColorVar.equals(parent.attributes.getIndex())) {
                                    stroke(color, alpha);// default color
                                } else {
                                    float vectorColorValue = (Float) marker.getProperty(vectorColorVar);
                                    float vectorColorPercent = norm(vectorColorValue,
                                            parent.attributes.getMin(vectorColorVar),
                                            parent.attributes.getMax(vectorColorVar));
                                    int vectorColor = parent.colors.coloursCont.get(data.selectedVectorSwatch)
                                            .findColour(vectorColorPercent);
                                    stroke(vectorColor, alpha);// user picked color
                                }
                            } else {
                                stroke(0, 0, 100, alpha);
                            }
                            // rotateY does not work lines so need to rotate manually
                            // make a copy so they can be altered
                            float vx = mx;
                            float vy = my;

                            strokeWeight(2);
                            pushMatrix();
                            translate((width / 2), 0 + resetZ, -145);
                            rotateY(radians(90));
                            line(vx, z, vy, vx + x, z + y, vy + x);
                            popMatrix();
                        }

                        // visual variable points
                        if (data.pointColorToggle) {
                            float size = 7; // default size
                            String pointColorVar = data.pointColorSelection;
                            int ptcolor;// point color
                            if (pointColorVar.equals(parent.attributes.getIndex())) {
                                fill(color, alpha);
                                ptcolor = color;// default color
                            } else {
                                float pointColorValue = (Float) marker.getProperty(pointColorVar);
                                float pointColorPercent = norm(pointColorValue,
                                        parent.attributes.getMin(pointColorVar),
                                        parent.attributes.getMax(pointColorVar));
                                int strokeColor = parent.colors.coloursCont.get(data.selectedPointSwatch)
                                        .findColour(pointColorPercent);
                                fill(strokeColor, alpha);
                                stroke(strokeColor);
                                ptcolor = strokeColor;// parent color
                            }
                            if (brushed) {
                                stroke(180, 100, 100);
                                strokeWeight(2);
                            } else {
                                noStroke();
                            }
                            if (data.pointSizeToggle) {
                                String pointSizeVar = data.pointSizeSelection;
                                float pointSizeValue = (Float) marker.getProperty(pointSizeVar);
                                float pointSize = map(pointSizeValue, parent.attributes.getMin(pointSizeVar),
                                        parent.attributes.getMax(pointSizeVar), data.pointSizeMin,
                                        data.pointSizeMax);
                                size = pointSize;// user size range
                            }

                            pushMatrix();
                            translate((width / 2), 0 + resetZ, -145);// translate points
                            rotateY(radians(90));// rotate so fit with other data
                            stroke(ptcolor, alpha);// point color
                            fill(ptcolor, alpha);// point color
                            strokeWeight(size);// point size
                            point(mx, z, my);// draw point
                            popMatrix();

                        }
                        // if the user changes line thickness
                        if (data.strokeWeightToggle && data.strokeWeightSelection != null) {
                            String strokeWeightVar = data.strokeWeightSelection;
                            float strokeWeightValue = (Float) marker.getProperty(strokeWeightVar);
                            float strokeWeight = map(strokeWeightValue, parent.attributes.getMin(strokeWeightVar),
                                    parent.attributes.getMax(strokeWeightVar), data.strokeWeightMin,
                                    data.strokeWeightMax);
                            timePath.strokeWeight(strokeWeight);
                        }
                        // if the user changes line color
                        if (data.strokeColorToggle) {
                            String strokeColorVar = data.strokeColorSelection;
                            if (strokeColorVar.equals(parent.attributes.getIndex())) {
                                timePath.stroke(color, alpha);
                            } else {
                                float strokeColorValue = (Float) marker.getProperty(strokeColorVar);
                                float strokeColorPercent = norm(strokeColorValue,
                                        parent.attributes.getMin(strokeColorVar),
                                        parent.attributes.getMax(strokeColorVar));
                                int strokeColor = parent.colors.coloursCont.get(data.selectedLineSwatch)
                                        .findColour(strokeColorPercent);
                                timePath.stroke(strokeColor, alpha);// user picked color
                            }

                            timePath.vertex(mx, z, my);// draw the line path
                        }
                    }
                    data.holdAlpha = alpha;
                }
                data.points = marker;
            }

            // Interaction on the fly
            // if ((track.getTag().equals(data.selectedIDs[0]) || track.getTag().equals(data.selectedIDs[1]))
            //         && data.interactionsGenerated && data.upToDate == true) {
            //     // check to see if points are close enough to be interacting
            //     if ((data.highlightOnFly && data.pointInteract == false)) {
            //         pushMatrix();
            //         translate((width / 2), 0 + resetZ, -145);
            //         rotateY(radians(90));
            //         stroke(Color.GRAY.getRGB(), 255); // grey when not interacting
            //         strokeWeight(8);// set ring size of 8 px
            //         point(mx, z, my);
            //         popMatrix();
            //     }
            //     if ((data.highlightOnFly && data.pointInteract == true)) {
            //         pushMatrix();
            //         translate((width / 2), 0 + resetZ, -145);
            //         rotateY(radians(90));
            //         stroke(Color.ORANGE.getRGB(), 255);// becomes yellow when interacting
            //         strokeWeight(12); // increase in size when interacting
            //         point(mx, z, my);
            //         popMatrix();
            //     }
            // }

            pushMatrix();
            translate(0, 0, -3);
            if (brushed)
                brush.endShape();
            shape(brush);
            popMatrix();

            pushMatrix();
            translate((width / 2), 0 + resetZ, -145);
            rotateY(radians(90));// make sure track draws in correct direction
            timePath.endShape();
            shape(timePath);// draw data path
            popMatrix();
        } // end for loop

        if (data.save) {
            String file = String.format("temp/" + parent.animationTitle + parent.exportCounter + "_3D/temp%08d.jpeg",
                    data.frameCounter);
            saveFrame(file);
            data.frameCounter++;
        }

    }// end draw()

    // DRAW BOX OUTLINE
    // ==================
    // draws a plus at the coordinate with off size
    private void drawPlus(float x, float y, float z, float off) {
        line(x,y,z-off, x,y,z+off);
        line(x,y-off,z, x,y+off,z);
        line(x-off,y,z, x+off,y,z);
    }
    // draws step number of plusses along a line
    private void drawLine(PVector c1, PVector c2, int steps, int offset) {
        PVector inc = PVector.sub(c1, c2).div(steps);
        for(int i=0; i<= steps; i++){
            PVector point = PVector.add(c2, PVector.mult(inc, i));
            drawPlus(point.x, point.y, point.z, (i==0||i==steps)?3*offset:offset);
        }
    }
    // draws the box outline
    public void drawOutline(int w, int h, int d, int off, boolean last) {
        float[] camera_position = camera.getPosition(); // temp

        PVector pos = new PVector(camera_position[0],camera_position[2],camera_position[2]);
        PVector[] corners = {new PVector(-w/2,-h/2,d/2), 
                             new PVector(-w/2,-h/2,-d/2), 
                             new PVector(w/2,-h/2,-d/2),
                             new PVector(w/2,-h/2,d/2)};
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
        int steps_width = (int)(cubeWidth/cubeDepth*steps_depth);
        int offset = 4;
        stroke(255,dim[0] ? 10 : 80); drawLine(corners[0], corners[1], steps_depth, offset);
        stroke(255,dim[1] ? 10 : 80); drawLine(corners[2], corners[1], steps_width, offset);
        stroke(255,dim[2] ? 10 : 80); drawLine(corners[2], corners[3], steps_depth, offset);
        stroke(255,dim[3] ? 10 : 80); drawLine(corners[0], corners[3], steps_width, offset);
        if(last) {pushMatrix(); translate(0, h, 0); drawOutline(w,h,d,off,false); popMatrix();}
    }
    // TODO: make into a PShape
    // draws all boxes for all months
    public void drawBox() {
        int startmonth = data.timeBoxStartMonth;// first month in all the data used for labels
        timeLengthMonths = findTimeInterval();// number of months the data lasts
        int startYear = data.timeBoxStartYear;
        
        for (int i = 0; i < timeLengthMonths; i++)// draw a box for each month
        {
            pushMatrix();
            translate(0, 100 * (timeLengthMonths/2-i), 0);
            // translate(width / 2, height - ((100 * i) + 60), -145); // translate to see whole cube. Shift back and
            //                                                        // increment
            noFill();// empty cube
            strokeWeight(1);// line size of 1
            drawOutline((int)cubeWidth, (int)cubeHeight, (int)cubeDepth, 5, i==0); // corner stars
            
            // label the cubes if user has chosen it
            if (data.labelMonth) {
                textSize(16);// font size
                fill(255, 100);
                // Draw month labels as "Aug '21"
                String label = monthLabel[startmonth].substring(0, 3) + " '" +str(startYear).substring(2);
                text(label, cubeWidth/2+10, 0, cubeDepth/2);// month drawn to side of the cube
                if (startmonth < 12)// not December
                {
                    startmonth++;
                } else if (startmonth == 12)// December
                {
                    startmonth = 1; // set back to January
                    startYear++;    // increment the year
                }
            }
            popMatrix();// labels and boxes are translated back
        }
    }
    // end drawbox outline

    public void setParent(DesktopPane father) {
        parent = father;
        data = parent.data;
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

    // UTILITIES
    //
    // Draws the X,Y,Z lines with R,G,B respectively
    void DrawGizmo(float scale, float alpha, boolean drawPlanes) {
        pushStyle();
        colorMode(HSB, 255, 255, 255);

        float planeScale = scale * 0.6f;
        float planeOpacity = alpha * 0.5f;  
        rectMode(CORNER);
        strokeWeight(5);
        pushMatrix();
        
        // xy plane
        if(drawPlanes){
            noStroke();
            fill(0,200,200,planeOpacity);
            rect(0,0,planeScale,planeScale);
        }
        // x axis 
        stroke(0,200,200,alpha);
        line(0,0,0, scale, 0, 0);
        
        // yz plane
        if(drawPlanes){
            pushMatrix();
            rotateY(-PI/2);
            noStroke();
            fill(255/3,200,200,planeOpacity);
            rect(0,0,planeScale,planeScale);
            popMatrix();
        }
        // y axis
        stroke(255/3,200,200,alpha);
        line(0,0,0, 0, scale, 0);
        
        // xz plane
        if(drawPlanes){
            pushMatrix();
            rotateX(PI/2);
            noStroke();
            fill(255*2/3,200,200,planeOpacity);
            rect(0,0,planeScale,planeScale);
            popMatrix();
        }
        // z axis
        stroke(255*2/3,200,200,alpha);
        line(0,0,0, 0, 0, scale);
        
        popMatrix();
        popStyle();
    }
    ////////////////////////////////////////////////////////////////////////////////////////
  

}
