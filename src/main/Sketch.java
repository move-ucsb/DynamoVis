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

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;


import com.jogamp.nativewindow.WindowClosingProtocol.WindowClosingMode;


import javax.swing.JFrame;
import javax.swing.WindowConstants;

import gui.ControlPanel;
import utils.PointRecord;
import utils.Track;

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

import processing.core.PApplet;
import processing.core.PShape;
import processing.opengl.PJOGL;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.events.EventDispatcher;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;

public class Sketch extends PApplet  {
	// Connect processing sketch to the rest of the application
	private DesktopPane parent;
	private SketchData data;
	private ControlPanel controlPanel;

	//
	public void setParent(DesktopPane father) {
		parent = father;
		data = parent.data;
		controlPanel = parent.controlPanel;
	}

	public Legend legend;
	public UnfoldingMap map;
	// 
	public boolean leftMapNeeded = false;
	public boolean rightMapNeeded = false;
	UnfoldingMap leftMap;
	UnfoldingMap rightMap;

	int colorMin;
	int colorMax;
	int colorMid;

	EventDispatcher eventDispatcher;

	// RUN/EXIT BEHAVIOURS -----------------
	public void run(int x, int y) {
		String[] processingArgs = {"--location="+x+","+y, "DynamoVis Animation"};
		PApplet.runSketch(processingArgs, this);
	}
	// Overriden to prevent System.exit(0) command, that 
	// shuts down the whole java environment
	@Override
	public void exitActual() {
		// minimize the window if it doesn't get disposed
		getSurface().setVisible(false);

		// hide timeline and control panel
		parent.controlContainer.setVisible(false);
		parent.timelineContainer.setVisible(false);
		
		// get ready for another animation
		parent.sketch = null;
		parent.startup = true;
		parent.dataConfigPanel.SetComponentsEnabled(true);

		noLoop();
	}
	//// 

	int w, h;
	public boolean isExiting = false;
	public void setSize(int w, int h) {
		this.w = w;
		this.h = h; 
	}
	// setup is replaced by settings in Processing 3
	public void settings() {
		size(w, h, P3D);

		// set icon
		PJOGL.setIcon(parent.getClass().getClassLoader().getResource("logo32.png").getPath());
	}
	
	public void setup() {
		colorMode(HSB, 360, 100, 100);
		frameRate(60);

		// Closes only the sketch on exit
		if (getGraphics().isGL()) {
			final com.jogamp.newt.Window w = (com.jogamp.newt.Window) getSurface().getNative();
			w.setDefaultCloseOperation(WindowClosingMode.DISPOSE_ON_CLOSE);
			// w.setAlwaysOnTop(true);
		}
		else {
			// Not Tested
			final JFrame w = (JFrame) getSurface().getNative();
			w.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		}

		// NONOPENGL ICON SETUP
		// PImage icon = loadImage(parent.getClass().getResource("logo32.png").getPath()); 
		// surface.setIcon(icon);

		parent.dataPoints = null;
		println("Animation Dimensions: " + w + "x" + h);
		println("Polling Interval: " + data.dataInterval + " " + data.timeUnit);

		// 
		leftMapNeeded = data.needLeftMap;
		rightMapNeeded = data.needRightMap;
		map = new UnfoldingMap(this, "0", 0, 0, w, h, false, false, data.provider);

		map.zoomAndPanToFit(data.locations);
		if(!leftMapNeeded && !rightMapNeeded) map.setTweening(true);
		eventDispatcher = MapUtils.createDefaultEventDispatcher(this, map);
		map.zoomAndPanToFit(data.locations); // sometimes the first attempt zooms too far and bugs out, so do it again

		// extra maps
		if(leftMapNeeded) leftMap = createWrappedMap(map, eventDispatcher, 1);
		if(rightMapNeeded) rightMap = createWrappedMap(map, eventDispatcher, 2);


		//
		legend = new Legend(this, data, parent, map);
		resetLegend();
		println();
	}
	/// extra map functions
	// https://github.com/tillnagel/unfolding/commit/46d03cf6ebc6e01a35dc0aede0a02b428b9cf68d
	public UnfoldingMap createWrappedMap(UnfoldingMap mainMap, EventDispatcher eventDispatcher, int id) {
		float x = id==1 ? -w : w;
		UnfoldingMap wrappedMap = new UnfoldingMap(this, Integer.toString(id), x, 0, w, h, false, false, data.provider);
		wrappedMap.zoomToLevel(mainMap.getZoomLevel());
		eventDispatcher.register(wrappedMap, "zoom", mainMap.getId());
		return wrappedMap;
	}
	public void updateMap(UnfoldingMap mainMap, UnfoldingMap nextMap, boolean left) {
		float degree = (left) ? -180 : 180;

		// Move next map
		ScreenPosition pos = mainMap.getScreenPosition(new Location(0, degree));
		nextMap.move(pos.x, 0);
		if (left) {
			nextMap.moveBy(-800, 0);
		}

		// Pan next map
		nextMap.panTo(new Location(0, 0));
		ScreenPosition map1RightPos = mainMap.getScreenPosition(new Location(0, degree));
		Location map1RightLocation = nextMap.getLocation(map1RightPos);
		float lonDiff = (-map1RightLocation.getLon()) - degree;
		nextMap.panTo(new Location(-map1RightLocation.getLat(), lonDiff));

		// Ensure next map is always over main map (push 1px)
		float fixLastPixel = (left) ? 1 : -1;
		nextMap.panBy(fixLastPixel, 0);
	}
	////

	//
	public void draw() {
		background(0, 0, 35);

		if(leftMapNeeded) updateMap(map, leftMap, true);
		if(rightMapNeeded) updateMap(map, rightMap, false);
		if(!isExiting) {
			// pushMatrix();
			// translate(0, 0, -5);
			map.draw();
			if(leftMapNeeded) leftMap.draw();
			if(rightMapNeeded) rightMap.draw();
			// popMatrix();
		}

		for (Entry<String, Track> entry : parent.trackList.entrySet()) {
			// 
			String key = entry.getKey();
			Track track = entry.getValue();

			if (track.getVisibility()) {
				PShape path = createShape();
				path.beginShape();
				path.noFill();
				path.strokeWeight(4);
				path.strokeJoin(ROUND);  // make line connections rounded
				// path.strokeCap(PROJECT); // make start and end of lines square

				PShape brush = createShape();
				boolean brushed = false;

				if (data.brushedTag != null && data.brushedTag.equals(key)) {
					brushed = true;
					brush.beginShape();
					brush.noFill();
					brush.stroke(180, 100, 100);
					brush.strokeWeight(6);
				}

				PShape ghost = createShape();

				if (data.ghost) {
					ghost.beginShape();
					ghost.noFill();
					ghost.stroke(data.ghostColor.getRGB(), data.ghostAlpha);
					ghost.strokeWeight(data.ghostWeight);
				}

				int color = parent.colors.getTagColor(key).getRGB();
				ArrayList<PointRecord> points = track.getPoints();

				for (int i = 0; i < points.size(); i++) {
					PointRecord marker = points.get(i);
					DateTime markerTime = marker.getTime();
					if (markerTime.isBefore(data.currentTime) || markerTime.isEqual(data.currentTime)) {
						ScreenPosition pos = map.getScreenPosition(marker.getLocation());

						if (data.ghost)
							ghost.vertex(pos.x, pos.y);

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
							fill(360, 100, 100, alpha);
							path.stroke(360, 100, 100, alpha);
						} else if (hours <= 48) {
							fill(360, 100, 63, alpha);
							path.stroke(360, 100, 63, alpha);
						} else if (hours <= 72) {
							fill(360, 100, 30, alpha);
							path.stroke(360, 100, 30, alpha);
						}

						if (alpha != 0) {
							if (data.pointColorToggle) {
								float size = 7;
								String pointColorVar = data.pointColorSelection;
								if (pointColorVar.equals(parent.attributes.getIndex())) {
									fill(color, alpha);
								} else {
									float pointColorValue = (Float) marker.getProperty(pointColorVar);
									float pointColorPercent = norm(pointColorValue,
											parent.attributes.getMin(pointColorVar),
											parent.attributes.getMax(pointColorVar));
									int strokeColor = parent.colors.coloursCont.get(data.selectedPointSwatch)
											.findColour(pointColorPercent);
									fill(strokeColor, alpha);
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
									size = pointSize;
								}
								// pushMatrix();
								// translate(0, 0, -1);
								ellipse(pos.x, pos.y, size, size);
								// popMatrix();
							}

							
							if (data.strokeWeightToggle && data.strokeWeightSelection != null) {
								String strokeWeightVar = data.strokeWeightSelection;
								float strokeWeightValue = (Float) marker.getProperty(strokeWeightVar);
								float strokeWeight = map(strokeWeightValue, parent.attributes.getMin(strokeWeightVar),
										parent.attributes.getMax(strokeWeightVar), data.strokeWeightMin,
										data.strokeWeightMax);
								path.strokeWeight(strokeWeight);
							}

							if (data.strokeColorToggle) {
								String strokeColorVar = data.strokeColorSelection;
								if (strokeColorVar.equals(parent.attributes.getIndex())) {
									path.stroke(color, alpha);
								} else {
									float strokeColorValue = (Float) marker.getProperty(strokeColorVar);
									float strokeColorPercent = norm(strokeColorValue,
											parent.attributes.getMin(strokeColorVar),
											parent.attributes.getMax(strokeColorVar));
									int strokeColor = parent.colors.coloursCont.get(data.selectedLineSwatch)
											.findColour(strokeColorPercent);
									path.stroke(strokeColor, alpha);
								}
								path.vertex(pos.x, pos.y);
								if (brushed)
									brush.vertex(pos.x, pos.y);
							}

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
								line(pos.x, pos.y, pos.x + x, pos.y + y);
							}
						}
					}
				}
				if(!isExiting) {
					// pushMatrix();
						// translate(0, 0, -3);
						if (brushed)
							brush.endShape();
						shape(brush);
					// popMatrix();
	
					// pushMatrix();
					// 	translate(0, 0, -4);
						if (data.ghost)
							ghost.endShape();
						shape(ghost);
					// popMatrix();
	
					// pushMatrix();
					// 	translate(0, 0, -2);
						path.endShape();
						shape(path);
					// popMatrix();
				}
			}
		}

		// advance time every x frames
		if (!data.pause && !data.mouse && frameCount % data.speed == 0) {

			if (data.currentTime.isBefore(data.endTime.plusMinutes(1))) {
				if (data.timeUnit.equals("minutes")) {
					data.currentTime = data.currentTime.plusMinutes(data.dataInterval);
				} else if (data.timeUnit.equals("seconds")) {
					data.currentTime = data.currentTime.plusSeconds(data.dataInterval);
				}
			}

			if (data.timeUnit.equals("minutes")) {
				data.seek = Minutes.minutesBetween(data.startTime, data.currentTime).getMinutes();
			} else if (data.timeUnit.equals("seconds")) {
				data.seek = Seconds.secondsBetween(data.startTime, data.currentTime).getSeconds();
			}

			data.seek = data.seek / data.dataInterval;
			controlPanel.seek.setValue(data.seek);

			if (data.currentTime.isAfter(data.endTime) && data.loop) {
				data.currentTime = data.startTime;
			}
		}

		if(!isExiting) {
			// pushMatrix();
			// 	translate(0, 0, 0);
				legend.display();
				legend.drag(mouseX, mouseY);
			// popMatrix();
		}

		if (data.save) {
			String file = String.format("export/temp/" + parent.animationTitle + parent.exportCounter + "/temp%08d.jpeg",
					data.frameCounter);
			saveFrame(file);
			data.frameCounter++;
		}

		if (isExiting) {
			textFont(legend.font2);
			background(20);
			fill(245);
			textAlign(CENTER, CENTER);
			text("\"" + parent.animationTitle + "\" animation was terminated.\nFeel free to close this window.", width/2, height/2);

			exitActual();
		}
	}

	// 
	public void resetLegend() {
		legend.setLocation(0, height - 40);
	}

	public void keyPressed() {
		switch (key) {
			case 'z':
				zoomIn();
				break;
			case 'x':
				zoomOut();
				break;
			case 'c':
				zoomAndPan(data.locations);
				break;
		}
	}

	// 
	public void zoomIn() {
		map.zoomIn();
		if(leftMap != null) leftMap.zoomIn();
		if(rightMap != null) rightMap.zoomIn();
	}
	public void zoomOut() {
		map.zoomOut();
		if(leftMap != null) leftMap.zoomOut();
		if(rightMap != null) rightMap.zoomOut();
	}
	public void zoomAndPan(List<Location> locations) {
		map.zoomAndPanToFit(locations);
		if(leftMap != null) leftMap.zoomAndPanToFit(locations);
		if(rightMap != null) rightMap.zoomAndPanToFit(locations);
	}

	public void register() {
		eventDispatcher.register(map, "pan", map.getId());
	}

	public void unregister() {
		eventDispatcher.unregister(map, "pan", map.getId());
	}

	public void mousePressed() {
		legend.clicked(mouseX, mouseY);
	}

	public void mouseReleased() {
		legend.stopDragging();
	}

	public void mouseMoved() {

	}


}
