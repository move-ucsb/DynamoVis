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

import gui.ControlPanel;
import utils.PointRecord;
import utils.Track;

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.events.EventDispatcher;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;

public class Sketch extends PApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private DesktopPane parent;
	private SketchData data;
	private ControlPanel controlPanel;

	public UnfoldingMap map;

	public Legend legend;

	int colorMin;
	int colorMax;
	int colorMid;

	EventDispatcher eventDispatcher;

	public void setParent(DesktopPane father) {
		parent = father;
		data = parent.data;
		controlPanel = parent.controlPanel;
	}

	// add rasterimage
	PImage visImg;
	Location visNorthWest = new Location(15.1873, 99.1483);
	Location visSouthEast = new Location(15.1107, 99.2069);
	// end raster

	public void setup() {

		// add raster
		// visImg =
		// loadImage("./data/tiger7203_access_kde99ext_obsmexp2_threeClass1.png");
		// end raster

		// int containerWidth = (int)
		// parent.sketchContainer.getContentPane().getSize().getWidth();
		// int containerHeight = (int)
		// parent.sketchContainer.getContentPane().getSize().getHeight();
		int containerWidth = (int) parent.getContentPane().getSize().getWidth();
		int containerHeight = (int) parent.getContentPane().getSize().getHeight();
		size(containerWidth, containerHeight, OPENGL);

		parent.dataPoints = null;
		println("Animation Dimensions: " + containerWidth + "x" + containerHeight);
		println("Polling Interval: " + data.dataInterval + " " + data.timeUnit);
		frameRate(25);
		colorMode(HSB, 360, 100, 100);

		map = new UnfoldingMap(this, 0, 0, containerWidth, containerHeight, data.provider);
		map.zoomAndPanToFit(data.locations);

		map.setTweening(true);

		eventDispatcher = MapUtils.createDefaultEventDispatcher(this, map);

		map.zoomAndPanToFit(data.locations); // sometimes the first attempt zooms too far and bugs out, so do it again

		legend = new Legend(this, data, parent, map);
		resetLegend();
		println();
	}

	public void resetLegend() {
		legend.setLocation(0, height - 40);
	}

	public void keyPressed() {
		switch (key) {
			case 'z':
				map.zoomIn();
				break;
			case 'x':
				map.zoomOut();
				break;
			case 'c':
				map.zoomAndPanToFit(data.locations);
				break;
		}
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

	public void draw() {
		background(0, 0, 35);
		pushMatrix();
		translate(0, 0, -5);

		map.draw();

		// add raster image
		ScreenPosition topRight = map.getScreenPosition(visNorthWest);
		ScreenPosition bottomLeft = map.getScreenPosition(visSouthEast);
		float width = bottomLeft.x - topRight.x;
		float height = bottomLeft.y - topRight.y;
		tint(255, 175);
		// image(visImg, topRight.x, topRight.y, width, height);
		noTint();
		// end add raster image

		popMatrix();

		for (Entry<String, Track> entry : parent.trackList.entrySet()) {

			String key = entry.getKey();
			Track track = entry.getValue();

			if (track.getVisibility()) {
				PShape path = createShape();
				path.beginShape();
				path.noFill();
				path.strokeWeight(4);

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
								pushMatrix();
								translate(0, 0, -1);
								ellipse(pos.x, pos.y, size, size);
								popMatrix();

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
				pushMatrix();
				translate(0, 0, -3);
				if (brushed)
					brush.endShape();
				shape(brush);
				popMatrix();

				pushMatrix();
				translate(0, 0, -4);
				if (data.ghost)
					ghost.endShape();
				shape(ghost);
				popMatrix();

				pushMatrix();
				translate(0, 0, -2);
				path.endShape();
				shape(path);
				popMatrix();
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

		pushMatrix();
		translate(0, 0, 0);
		legend.display();
		legend.drag(mouseX, mouseY);
		popMatrix();

		if (data.save) {
			String file = String.format("temp/" + parent.animationTitle + parent.exportCounter + "/temp%08d.jpeg",
					data.frameCounter);
			saveFrame(file);
			data.frameCounter++;
		}

	}

}
