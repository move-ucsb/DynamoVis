package main;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;

import de.fhpotsdam.unfolding.geo.Location;
import main.DesktopPane;
import main.SketchData;
import utils.PointRecord;
import utils.Track;
import processing.core.PApplet;
import processing.core.PShape;

public class StaticBox extends PApplet {

    private DesktopPane parent;
    public SketchData data;
    public int timeLengthMonths;// how long the data is in month
    public int newDay = 0;
    public String monthLabel[] = { "", "January", "February", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December" };
    public float mx;// mapped x
    public float my;// mapped y
    public float z;// current z level
    public float zinterval = 3;// how much the z should change by- vary by day in month
    public float startz; // where the z starts
    public float bheight = 100;
    Map<String, Rectangle> bounds = new HashMap<String, Rectangle>();

    // RUN/EXIT BEHAVIOURS -----------------
    public void run(int x, int y) {
        String[] processingArgs = { "--location=" + x + "," + y, "DynamoVis Animation" };
        PApplet.runSketch(processingArgs, this);
    }

    // Overriden to prevent System.exit(0) command, that
    // shuts down the whole java environment
    @Override
    public void exitActual() {
        // minimize the window if it doesn't get disposed
        getSurface().setVisible(false);

        // clean flags
        // parent.staticbox = null;
        // parent.timeBoxCheck.setSelected(false);

        noLoop();
    }
    ////

    public void settings() {
        size(500, 850, P3D);// size of the canvas
    }

    public void setup() {
        timeLengthMonths = findTimeInterval();// find the data's time range in months
        find_intersect_bound();// set bounds
        if (timeLengthMonths > 8)// interval is reduced if more than 8 months of data to compress tracks
        {
            // height of all of the boxes/average number of days in a month
            bheight = (height - 50) / timeLengthMonths;
            zinterval = bheight / 30;
        }
    }

    public void draw() {
        background(0);// black background
        drawBox();// make space-time cubes

        // go through each data track uploaded
        for (Entry<String, Track> entry : parent.trackList.entrySet()) {

            String key = entry.getKey();// the track ID
            Track track = entry.getValue();// the data track points

            // if the data is showing then draw the path
            if (track.getVisibility()) {
                // set up the shape to hold the data path
                PShape timePath = createShape();
                timePath.beginShape();
                timePath.noFill();
                timePath.strokeWeight(2);

                PShape brush = createShape();
                boolean brushed = false;

                if (data.brushedTag != null && data.brushedTag.equals(key)) {
                    brushed = true;
                    brush.beginShape();
                    brush.noFill();
                    brush.stroke(180, 100, 100);
                    brush.strokeWeight(4);
                }

                int color = parent.colors.getTagColor(key).getRGB();// retrieve the default color of the track
                ArrayList<PointRecord> points = track.getPoints();// list of all of the data points for the track

                // go through each point in the data track
                for (int i = 0; i < points.size() - 1; i++) {
                    PointRecord marker = points.get(i);// the data point
                    DateTime markerTime = marker.getTime();// get the point's time

                    Location pos = marker.getLocation();
                    int days = Days.daysBetween(points.get(0).getTime(), markerTime).getDays();// number of days that
                                                                                               // have passed since the
                                                                                               // first day
                    float daysinmonth = markerTime.dayOfMonth().getMaximumValue();// find what month the point is in and
                                                                                  // find how many days are in that
                                                                                  // month

                    if (timeLengthMonths > 8)// condensed height of box
                    {
                        bheight = (height - 50) / timeLengthMonths;// box height- height of the all boxes/number of
                                                                   // months
                        zinterval = bheight / daysinmonth;// interval z is incremented by
                        if (i == 0)// first time so where z starts
                        {
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
                                        1, 1);// changeable start date

                                // goes through each month
                                while (tempStartTime.getMonthOfYear() != markerTime
                                        .getMonthOfYear()/*
                                                          * && tempStartTime.getYearOfCentury()!=markerTime.
                                                          * getYearOfCentury()
                                                          */) {
                                    minterval = (float) (bheight / (tempStartTime.dayOfMonth().getMaximumValue()));// interval
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
                    } else// normal height
                    {
                        zinterval = (float) (100.0 / daysinmonth);
                        if (i == 0)// first time so where z starts
                        {
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
                    }

                    if (newDay < days) {
                        // new day so increment z
                        z = (startz) - days * zinterval;// based on the number of days that has passed * increment
                    }
                    newDay = days;// reset new day either same day or a new day

                    // scale the data
                    float ubx = (float) (bounds.get(entry.getKey()).getMaxX() / 1000);// max x of data
                    float lbx = (float) (bounds.get(entry.getKey()).getMinX() / 1000);// min x of data
                    float uby = (float) (bounds.get(entry.getKey()).getMaxY() / 1000);// max y of data
                    float lby = (float) (bounds.get(entry.getKey()).getMinY() / 1000);// min y of data

                    // Processing map function to map to cube
                    mx = map(pos.x, lbx, ubx, -130, 130);
                    my = map(pos.y, lby, uby, -130, 130);

                    // color fade
                    float alpha = 255;
                    // if data is visible
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
                            // translate((width/2),0+resetZ,-145);
                            translate((width / 2), 0, -145);
                            rotateY(radians(90));
                            line(vx, z, vy, vx + x, z + y, vy + x);
                            popMatrix();
                        }

                        // visual variable points
                        if (data.pointColorToggle) {
                            float size = 7;// default size
                            int ptcolor;// point color
                            String pointColorVar = data.pointColorSelection;
                            if (pointColorVar.equals(parent.attributes.getIndex())) {
                                fill(color, alpha);
                                ptcolor = color;// default color
                            } else {
                                float pointColorValue = (Float) marker.getProperty(pointColorVar);
                                float pointColorPercent = norm(pointColorValue, parent.attributes.getMin(pointColorVar),
                                        parent.attributes.getMax(pointColorVar));
                                int strokeColor = parent.colors.coloursCont.get(data.selectedPointSwatch)
                                        .findColour(pointColorPercent);
                                fill(strokeColor, alpha);
                                stroke(strokeColor, alpha);
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
                                        parent.attributes.getMax(pointSizeVar), data.pointSizeMin, data.pointSizeMax);
                                size = pointSize;// user size range
                            }

                            pushMatrix();
                            translate((width / 2), 0, -145);// translate points
                            rotateY(radians(90));// rotate so fit with other data
                            stroke(ptcolor);// point color
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

                            timePath.vertex(mx, z, my);// draw the track with line
                        }
                    }
                    data.holdAlpha = alpha;
                    data.points = marker;
                }

                pushMatrix();
                translate(0, 0, -3);
                if (brushed)
                    brush.endShape();
                shape(brush);
                popMatrix();

                pushMatrix();
                translate((width / 2), 0, -145);// half the box + amount the box was set back
                rotateY(radians(90));
                timePath.endShape();// draw data path
                shape(timePath);
                popMatrix();
            } // end if statement
        } // end for loop
    }// end draw()

    // draws the cubes
    public void drawBox() {
        int startmonth = data.timeBoxStartMonth;// first month in all the data used for labels
        float bheight = 100;// default box height
        int bsize = 260;// default box width and length
        timeLengthMonths = findTimeInterval();// number of months the data lasts
        if (timeLengthMonths > 8)// maximum number of months that can be shown
        {
            bheight = (height - 50) / timeLengthMonths;// scale box height down
        }

        for (int i = 0; i < timeLengthMonths; i++) {
            pushMatrix();
            translate(width / 2, height - ((bheight * i) + 60), -145);// translate to see whole cube. Shift back and
                                                                      // increment
            noFill();// empty cube
            stroke(255);// white lines
            strokeWeight(1);// line size of 1
            box(bsize, bheight, bsize);// draw box (l,h,w)

            // label the cubes if user has chosen it
            if (data.labelMonth) {
                textSize(16);// font size
                fill(255);
                text(monthLabel[startmonth], 140, 0, 150);// month drawn to side of the cube
                if (startmonth == 0)// January so increase
                {
                    startmonth++;
                } else if (startmonth < 12)// not December
                {
                    startmonth++;
                } else if (startmonth == 12)// December
                {
                    startmonth = 1;// set back to January
                }
            }
            popMatrix();// labels and boxes are translated back
        }
    }// end drawbox

    public void setParent(DesktopPane father) {
        parent = father;
        data = parent.data;
        // controlPanel = parent.controlPanel;
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

}
