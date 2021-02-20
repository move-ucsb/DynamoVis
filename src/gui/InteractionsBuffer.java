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

package gui;

import java.util.ArrayList;

import javax.swing.JPanel;

import org.joda.time.DateTime;
import org.joda.time.Hours;

import de.fhpotsdam.unfolding.utils.GeoUtils;
import utils.PointRecord;
import utils.Track;
import main.SketchData;

public class InteractionsBuffer extends JPanel {
	/**
	 * 
	 */
	private double bufferDistance;
	private double bufferTime;
	private SketchData data;
	public GeoUtils gu = new GeoUtils();

	// could make initial option blank here
	public InteractionsBuffer() {// set defaults to first option
		bufferDistance = 100;
		bufferTime = 0;

	}

	public double getBufferDistance() {
		return bufferDistance;
	}// returns current selection for the distance of the buffer

	public double getBufferTime() {
		return bufferTime;
	}// returns current selection for the time of the buffer

	public void setBufferDistance(double inDistance) {
		bufferDistance = inDistance;
	}// sets variable keeping track of distance selection for buffer to input

	public void setBufferTime(double inTime) {
		bufferTime = inTime;
	}// sets variable keeping track of time selection to buffer to input

	/*
	 * public String[] getBufferDistances(){ return distances; }//returns array of
	 * distance options in string form
	 * 
	 * public String[] getBufferTimes(){ return times; }//returns array of all time
	 * options in string form
	 */

	public double timeBetween(PointRecord p1, PointRecord p2) {// potential for error
		// int time = -1;
		DateTime t1 = p1.getTime();
		DateTime t2 = p2.getTime();
		double diff = Hours.hoursBetween(t1, t2).getHours();
		return diff;
	}

	public boolean withinDistanceBuffer(PointRecord p1, PointRecord p2, double buffer) {
		// pt_one.getDistance(pt_two) returns distance in kilometers between two points
		// on earth
		double dist = gu.getDistance(p1.getLocation(), p2.getLocation());// returns distance in kilometers between two
																			// points on earth
		if (dist * 1000 <= buffer) {
			return true;// point is within buffer distance
		} else {
			return false;
		} // point too far
	}// returns true if two passed in locations are within the buffer distance passed
		// in, false otherwise

	public boolean withinTimeBuffer(PointRecord p1, PointRecord p2, double buffer) {
		// the buffers in the animation do not disappear. This means that if an
		// interaction occurs and one track keeps animating, but there isn't data for
		// the other the
		// highlighted buffer remains. Buffers also may touch but unless the animals are
		// with the others', interaction does not happen
		if (Math.abs(timeBetween(p1, p2)) <= buffer) {
			return true;// time between points is less than the buffer
		} else {
			return false;
		}
	}// returns true if times recorded in passed in PointRecord objects are within
		// buffer(also passed in), false otherwise

	public ArrayList<PointRecord> calculateInteractingPoints(Track track1, Track track2, double bufferDist,
			double bufferTime) {
		ArrayList<PointRecord> p1 = track1.getPoints();// list of points in track1
		ArrayList<PointRecord> p2 = track2.getPoints();// list of points in track2
		ArrayList<PointRecord> interacting = new ArrayList<PointRecord>();// make a new list for interacting points
		// test every point in both tracks against each other
		for (int i = 0; i < p1.size(); i++) {
			for (int j = 0; j < p2.size(); j++) {
				if (withinDistanceBuffer(p1.get(i), p2.get(j), bufferDist)
						&& withinTimeBuffer(p1.get(i), p2.get(j), bufferTime))
				// test if the data points are within the buffer distance and
				{
					interacting.add(p1.get(i));// add point of interaction from track1
					interacting.add(p2.get(j));// add point of interaction from track2
				}
			}
		}
		return interacting;
	}// returns boolean array of all measured points, true if conditions met for
		// interaction, false otherwise

	public String buffertoString(boolean[] arr) {
		String total = "";
		for (int i = 0; i < arr.length; i++) {
			total += arr[i] + "\n";
		}
		return total;
	}// returns string version of input buffer

	public int getShortTrackLength(Track track1, Track track2) {
		ArrayList<PointRecord> p1 = track1.getPoints();
		ArrayList<PointRecord> p2 = track2.getPoints();
		int max;
		if (p1.size() >= p2.size()) {
			max = p2.size();
			return p2.size();
		} else {
			max = p1.size();
			return p1.size();
		}
	}// returns the length of the shortest selected track

}