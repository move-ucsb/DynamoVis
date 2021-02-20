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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import utils.Track;

import org.gicentre.utils.io.DOMProcessor;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.utils.GeoUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PShape;

public class Legend {

	PApplet p;
	SketchData data;
	DesktopPane parent;
	UnfoldingMap map;

	PShape lineColorLegend;
	PShape pointColorLegend;
	PShape vectorColorLegend;
	PShape lineWidthLegend;
	PShape pointSizeLegend;
	PShape vectorLengthLegend;
	PFont font, font2;
	PFont message;
	PFont headers;
	PFont temp;

	int fontColor;

	float x, y;
	float offsetX, offsetY;
	float titleX, titleY, titleSetX, titleSetY;
	float lineColorX, lineColorY, lineColorSetX, lineColorSetY;
	float lineWidthX, lineWidthY, lineWidthSetX, lineWidthSetY;
	float pointColorX, pointColorY, pointColorSetX, pointColorSetY;
	float pointSizeX, pointSizeY, pointSizeSetX, pointSizeSetY;
	float vectorColorX, vectorColorY, vectorColorSetX, vectorColorSetY;
	float vectorLengthX, vectorLengthY, vectorLengthSetX, vectorLengthSetY;

	boolean dragging = false;
	boolean titleDrag = false;
	boolean lineColorDrag = false;
	boolean lineWidthDrag = false;
	boolean pointColorDrag = false;
	boolean pointSizeDrag = false;
	boolean vectorColorDrag = false;
	boolean vectorLengthDrag = false;

	private static final List<Float> DISPLAY_DISTANCES = Arrays.asList(0.01f, 0.02f, 0.05f, 0.1f, 0.2f, 0.5f, 1f, 2f,
			5f, 10f, 20f, 50f, 100f, 200f, 500f, 1000f, 2000f, 5000f);
	private static final float MAX_DISPLAY_DISTANCE = 5000;

	DateTimeFormatter fmt;

	public Legend(PApplet papplet, SketchData sketchdata, DesktopPane father, UnfoldingMap m) {
		parent = father;
		p = papplet;
		data = sketchdata;
		fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		map = m;
		font = p.createFont("Arial", 10);
		font2 = p.createFont("Arial", 30);
		message = p.createFont("Arial Bold", 100);
		headers = p.createFont("Arial Bold", 12);
		temp = p.createFont("Arial", 18);
		p.textFont(font);
		fontColor = p.color(0, 0, 100);
	}

	public void setLocation(float tempX, float tempY) {
		x = tempX;
		y = tempY;
		offsetX = 0;
		offsetY = 0;
		titleX = 0;
		titleY = 0;
		titleSetX = 0;
		titleSetY = 0;
		lineColorX = 0;
		lineColorY = 0;
		lineColorSetX = 0;
		lineColorSetY = 0;
		lineWidthX = 0;
		lineWidthY = 0;
		lineWidthSetX = 0;
		lineWidthSetY = 0;
		pointColorX = 0;
		pointColorY = 0;
		pointColorSetX = 0;
		pointColorSetY = 0;
		pointSizeX = 0;
		pointSizeY = 0;
		pointSizeSetX = 0;
		pointSizeSetY = 0;
		vectorColorX = 0;
		vectorColorY = 0;
		vectorColorSetX = 0;
		vectorColorSetY = 0;
		vectorLengthX = 0;
		vectorLengthY = 0;
		vectorLengthSetX = 0;
		vectorLengthSetY = 0;
	}

	public void clicked(int mx, int my) {
		if (!data.legendLocked) {
			if (mx > x + titleX && mx < x + titleX + 290 && my > y - 40 + titleY && my < y - 20 + titleY) {
				titleDrag = true;
				titleSetX = titleX - mx;
				titleSetY = titleY - my;
			} else if (mx > x + 300 + lineColorX && mx < x + 300 + lineColorX + 290 && my > y - 40 + lineColorY
					&& my < y - 20 + lineColorY) {
				lineColorDrag = true;
				lineColorSetX = lineColorX - mx;
				lineColorSetY = lineColorY - my;
			} else if (mx > x + 600 + lineWidthX && mx < x + 600 + lineWidthX + 290 && my > y - 40 + lineWidthY
					&& my < y - 20 + lineWidthY) {
				lineWidthDrag = true;
				lineWidthSetX = lineWidthX - mx;
				lineWidthSetY = lineWidthY - my;
			} else if (mx > x + 300 + pointColorX && mx < x + 300 + pointColorX + 290 && my > y - 40 + pointColorY
					&& my < y - 20 + pointColorY) {
				pointColorDrag = true;
				pointColorSetX = pointColorX - mx;
				pointColorSetY = pointColorY - my;
			} else if (mx > x + 600 + pointSizeX && mx < x + 600 + pointSizeX + 290 && my > y - 40 + pointSizeY
					&& my < y - 20 + pointSizeY) {
				pointSizeDrag = true;
				pointSizeSetX = pointSizeX - mx;
				pointSizeSetY = pointSizeY - my;
			} else if (mx > x + 300 + vectorColorX && mx < x + 300 + vectorColorX + 290 && my > y - 40 + vectorColorY
					&& my < y - 20 + vectorColorY) {
				vectorColorDrag = true;
				vectorColorSetX = vectorColorX - mx;
				vectorColorSetY = vectorColorY - my;
			} else if (mx > x + 600 + vectorLengthX && mx < x + 600 + vectorLengthX + 290 && my > y - 40 + vectorLengthY
					&& my < y - 20 + vectorLengthY) {
				vectorLengthDrag = true;
				vectorLengthSetX = vectorLengthX - mx;
				vectorLengthSetY = vectorLengthY - my;
			} else if (mx > x && mx < x + 890 && my > y - 60 && my < y - 40) {
				dragging = true;
				offsetX = x - mx;
				offsetY = y - my;
			}
		}
	}

	public void stopDragging() {
		dragging = false;
		titleDrag = false;
		lineColorDrag = false;
		lineWidthDrag = false;
		pointColorDrag = false;
		pointSizeDrag = false;
		vectorColorDrag = false;
		vectorLengthDrag = false;
	}

	public void drag(int mx, int my) {
		if (dragging) {
			x = mx + offsetX;
			y = my + offsetY;
		}
		if (titleDrag) {
			titleX = mx + titleSetX;
			titleY = my + titleSetY;
		}
		if (lineColorDrag) {
			lineColorX = mx + lineColorSetX;
			lineColorY = my + lineColorSetY;
		}
		if (lineWidthDrag) {
			lineWidthX = mx + lineWidthSetX;
			lineWidthY = my + lineWidthSetY;
		}
		if (pointColorDrag) {
			pointColorX = mx + pointColorSetX;
			pointColorY = my + pointColorSetY;
		}
		if (pointSizeDrag) {
			pointSizeX = mx + pointSizeSetX;
			pointSizeY = my + pointSizeSetY;
		}
		if (vectorColorDrag) {
			vectorColorX = mx + vectorColorSetX;
			vectorColorY = my + vectorColorSetY;
		}
		if (vectorLengthDrag) {
			vectorLengthX = mx + vectorLengthSetX;
			vectorLengthY = my + vectorLengthSetY;
		}
	}

	public void display() {
		drawLock();
		drawTitle();
		drawLineColor();
		drawLineWidth();
		drawPointColor();
		drawPointSize();
		drawVectorColor();
		drawVectorLength();
	}

	private void drawVectorLength() {
		p.pushMatrix();
		p.translate(x + 320 + 300 + vectorLengthX, y + 15 + vectorLengthY);
		vectorLengthLegend = p.createShape();
		vectorLengthLegend.beginShape();
		vectorLengthLegend.noFill();
		vectorLengthLegend.strokeWeight(0);

		if (!data.legendLocked) {
			p.strokeWeight(2);
			p.stroke(0, 0, 50);
			p.fill(60, 100, 100, 20);
			p.rect(-20, -35, 290, 65);
			p.fill(60, 100, 100, 100);
			p.rect(-20, -55, 290, 20);
			String title = "DRAG VECTOR LENGTH BLOCK";
			p.textFont(headers);
			p.fill(fontColor);
			p.text(title, (290 / 2) - ((int) p.textWidth(title) / 2) - 20, -40);
			p.textFont(font);
		}

		String strokeWeightVar = parent.attributes.getAlias(data.vectorFieldSelection);

		if (data.vectorLengthToggle) {
			for (int i = 0; i <= 200; i = i + 20) {
				float stroke = PApplet.map(i, 0, 200, data.vectorLengthMin, data.vectorLengthMax);
				p.line(i, 0, i, -stroke);
			}
			p.fill(fontColor);
			String label = strokeWeightVar + " " + data.selectedStrokeWeightUnit;
			int labelWidth = (int) p.textWidth(label);
			p.text(label, (100 - (labelWidth / 2)), -7);
			p.text(parent.attributes.getMin(parent.attributes.getName(strokeWeightVar)), 0, 15);
			p.text(parent.attributes.getMax(parent.attributes.getName(strokeWeightVar)), 200, 15);
		}

		vectorLengthLegend.endShape();
		p.shape(vectorLengthLegend);
		p.popMatrix();

	}

	private void drawVectorColor() {
		p.pushMatrix();
		vectorColorLegend = p.createShape();
		vectorColorLegend.beginShape();
		vectorColorLegend.noFill();
		vectorColorLegend.strokeWeight(10);
		p.translate(x + 320 + vectorColorX, y + 15 + vectorColorY);

		if (!data.legendLocked) {
			p.strokeWeight(2);
			p.stroke(0, 0, 50);
			p.fill(125, 100, 100, 20);
			p.rect(-20, -35, 290, 65);
			p.fill(125, 100, 100, 100);
			p.rect(-20, -55, 290, 20);
			String title = "DRAG VECTOR COLOR BLOCK";
			p.textFont(headers);
			p.fill(fontColor);
			p.text(title, (290 / 2) - ((int) p.textWidth(title) / 2) - 20, -40);
			p.textFont(font);
		}

		String strokeColorVar = parent.attributes.getAlias(data.vectorColorSelection);
		if (data.vectorColorToggle) {
			if (data.vectorColorSelection.equals(parent.attributes.getIndex())) {

				int li = 0;
				int itr = 0;
				int y = 0;
				for (String i : parent.tagList) {

					int color = parent.colors.getTagColor(i).getRGB();
					p.strokeWeight(0);
					p.fill(color);
					p.rect(li - 27, -20 + (y * 25), 25, 10);
					p.fill(fontColor);
					p.text(i, li - 27, -22 + (y * 25));
					li = li + 30;
					itr++;
					if (itr % 10 == 0) {
						li = 0;
						y++;
					}
				}

			} else {
				for (int i = 0; i <= 200; i++) {
					// float percent = PApplet.norm(i, 0, 200);
					vectorColorLegend.stroke(
							parent.colors.coloursCont.get(data.selectedVectorSwatch).findColour((float) i / 200));
					// legend.stroke(p.lerpColor(colorMin,colorMax, percent));
					vectorColorLegend.vertex(i, 0);
				}
				p.fill(fontColor);

				String label = strokeColorVar + " " + data.selectedColorUnit;
				int labelWidth = (int) p.textWidth(label);
				p.text(label, (100 - (labelWidth / 2)), -7);
				p.text(parent.attributes.getMin(parent.attributes.getName(strokeColorVar)), 0, 15);
				p.text(parent.attributes.getMax(parent.attributes.getName(strokeColorVar)), 200, 15);
			}
		}

		vectorColorLegend.endShape();
		p.shape(vectorColorLegend);
		p.popMatrix();
	}

	private void drawPointSize() {
		p.pushMatrix();
		p.translate(x + 320 + 300 + pointSizeX, y + 15 + pointSizeY);
		pointSizeLegend = p.createShape();
		pointSizeLegend.beginShape();
		pointSizeLegend.noFill();
		pointSizeLegend.strokeWeight(0);

		if (!data.legendLocked) {
			p.strokeWeight(2);
			p.stroke(0, 0, 50);
			p.fill(60, 100, 100, 20);
			p.rect(-20, -35, 290, 65);
			p.fill(60, 100, 100, 100);
			p.rect(-20, -55, 290, 20);
			String title = "DRAG POINT SIZE BLOCK";
			p.textFont(headers);
			p.fill(fontColor);
			p.text(title, (290 / 2) - ((int) p.textWidth(title) / 2) - 20, -40);
			p.textFont(font);
		}

		String strokeWeightVar = parent.attributes.getAlias(data.pointSizeSelection);

		if (data.pointSizeToggle) {
			for (int i = 0; i <= 200; i = i + 20) {
				float stroke = PApplet.map(i, 0, 200, data.pointSizeMin, data.pointSizeMax);
				p.ellipse(i, 0, stroke, stroke);
			}

			p.fill(fontColor);
			String label = strokeWeightVar + " " + data.selectedStrokeWeightUnit;
			int labelWidth = (int) p.textWidth(label);
			p.text(label, (100 - (labelWidth / 2)), -7);
			p.text(parent.attributes.getMin(parent.attributes.getName(strokeWeightVar)), 0, 15);
			p.text(parent.attributes.getMax(parent.attributes.getName(strokeWeightVar)), 200, 15);
		}

		pointSizeLegend.endShape();
		p.shape(pointSizeLegend);
		p.popMatrix();
	}

	private void drawPointColor() {
		p.pushMatrix();
		pointColorLegend = p.createShape();
		pointColorLegend.beginShape();
		pointColorLegend.noFill();
		pointColorLegend.strokeWeight(10);
		p.translate(x + 320 + pointColorX, y + 15 + pointColorY);

		if (!data.legendLocked) {
			p.strokeWeight(2);
			p.stroke(0, 0, 50);
			p.fill(125, 100, 100, 20);
			p.rect(-20, -35, 290, 65);
			p.fill(125, 100, 100, 100);
			p.rect(-20, -55, 290, 20);
			String title = "DRAG POINT COLOR BLOCK";
			p.textFont(headers);
			p.fill(fontColor);
			p.text(title, (290 / 2) - ((int) p.textWidth(title) / 2) - 20, -40);
			p.textFont(font);
		}

		String strokeColorVar = parent.attributes.getAlias(data.pointColorSelection);
		if (data.pointColorToggle) {
			if (data.pointColorSelection.equals(parent.attributes.getIndex())) {

				int li = 0;
				int itr = 0;
				int y = 0;
				for (String i : parent.tagList) {

					int color = parent.colors.getTagColor(i).getRGB();
					p.strokeWeight(0);
					p.fill(color);
					p.rect(li - 27, -20 + (y * 25), 25, 10);
					p.fill(fontColor);
					p.text(i, li - 27, -22 + (y * 25));
					li = li + 30;
					itr++;
					if (itr % 10 == 0) {
						li = 0;
						y++;
					}
				}

			} else {
				for (int i = 0; i <= 200; i++) {
					// float percent = PApplet.norm(i, 0, 200);
					pointColorLegend.stroke(
							parent.colors.coloursCont.get(data.selectedPointSwatch).findColour((float) i / 200));
					// legend.stroke(p.lerpColor(colorMin,colorMax, percent));
					pointColorLegend.vertex(i, 0);
				}
				p.fill(fontColor);

				String label = strokeColorVar + " " + data.selectedColorUnit;
				int labelWidth = (int) p.textWidth(label);
				p.text(label, (100 - (labelWidth / 2)), -7);
				p.text(parent.attributes.getMin(parent.attributes.getName(strokeColorVar)), 0, 15);
				p.text(parent.attributes.getMax(parent.attributes.getName(strokeColorVar)), 200, 15);
			}
		}

		pointColorLegend.endShape();
		p.shape(pointColorLegend);
		p.popMatrix();
	}

	private void drawLineWidth() {
		p.pushMatrix();
		p.translate(x + 320 + 300 + lineWidthX, y + 15 + lineWidthY);
		lineWidthLegend = p.createShape();
		lineWidthLegend.beginShape();
		lineWidthLegend.noFill();
		lineWidthLegend.strokeWeight(0);

		if (!data.legendLocked) {
			p.strokeWeight(2);
			p.stroke(0, 0, 50);
			p.fill(60, 100, 100, 20);
			p.rect(-20, -35, 290, 65);
			p.fill(60, 100, 100, 100);
			p.rect(-20, -55, 290, 20);
			String title = "DRAG LINE WIDTH BLOCK";
			p.textFont(headers);
			p.fill(fontColor);
			p.text(title, (290 / 2) - ((int) p.textWidth(title) / 2) - 20, -40);
			p.textFont(font);
		}

		String strokeWeightVar = parent.attributes.getAlias(data.strokeWeightSelection);

		if (data.strokeWeightToggle) {
			for (int i = 0; i <= 200; i++) {
				float stroke = PApplet.map(i, 0, 200, data.strokeWeightMin, data.strokeWeightMax);
				lineWidthLegend.stroke(fontColor);
				lineWidthLegend.strokeWeight(stroke);
				lineWidthLegend.vertex(i, 0);
			}
			p.fill(fontColor);
			String label = strokeWeightVar + " " + data.selectedStrokeWeightUnit;
			int labelWidth = (int) p.textWidth(label);
			p.text(label, (100 - (labelWidth / 2)), -7);
			p.text(parent.attributes.getMin(parent.attributes.getName(strokeWeightVar)), 0, 15);
			p.text(parent.attributes.getMax(parent.attributes.getName(strokeWeightVar)), 200, 15);
		}

		lineWidthLegend.endShape();
		p.shape(lineWidthLegend);
		p.popMatrix();
	}

	private void drawLineColor() {
		p.pushMatrix();
		lineColorLegend = p.createShape();
		lineColorLegend.beginShape();
		lineColorLegend.noFill();
		lineColorLegend.strokeWeight(10);
		p.translate(x + 320 + lineColorX, y + 15 + lineColorY);

		if (!data.legendLocked) {
			p.strokeWeight(2);
			p.stroke(0, 0, 50);
			p.fill(125, 100, 100, 20);
			p.rect(-20, -35, 290, 65);
			p.fill(125, 100, 100, 100);
			p.rect(-20, -55, 290, 20);
			String title = "DRAG LINE COLOR BLOCK";
			p.textFont(headers);
			p.fill(fontColor);
			p.text(title, (290 / 2) - ((int) p.textWidth(title) / 2) - 20, -40);
			p.textFont(font);
		}

		String strokeColorVar = parent.attributes.getAlias(data.strokeColorSelection);

		if (data.strokeColorToggle) {
			if (data.strokeColorSelection.equals(parent.attributes.getIndex())) {

				int li = 0;
				int itr = 0;
				int y = 0;
				for (String i : parent.tagList) {

					int color = parent.colors.getTagColor(i).getRGB();
					p.strokeWeight(0);
					p.fill(color);
					p.rect(li - 27, -20 + (y * 25), 35, 10);
					p.fill(fontColor);
					p.text(i, li - 27, -22 + (y * 25));
					li = li + 45;
					itr++;
					if (itr % 10 == 0) {
						li = 0;
						y++;
					}
				}

			} else {
				for (int i = 0; i <= 200; i++) {
					lineColorLegend
							.stroke(parent.colors.coloursCont.get(data.selectedLineSwatch).findColour((float) i / 200));
					lineColorLegend.vertex(i, 0);
				}
				p.fill(fontColor);

				String label = strokeColorVar + " " + data.selectedColorUnit;
				int labelWidth = (int) p.textWidth(label);
				p.text(label, (100 - (labelWidth / 2)), -7);
				p.text(parent.attributes.getMin(parent.attributes.getName(strokeColorVar)), 0, 15);
				p.text(parent.attributes.getMax(parent.attributes.getName(strokeColorVar)), 200, 15);
			}
		}

		lineColorLegend.endShape();
		p.shape(lineColorLegend);
		p.popMatrix();
	}

	private void drawTitle() {
		p.pushMatrix();
		p.translate(x, y);

		if (!data.legendLocked) {
			p.strokeWeight(2);
			p.stroke(0, 0, 50);
			p.fill(360, 100, 100, 20);
			p.rect(0, -40, 890, 85);
			p.fill(360, 100, 100, 100);
			p.rect(0, -60, 890, 20);
			String text = "DRAG LEGEND BLOCK";
			p.textFont(headers);
			p.fill(fontColor);
			p.text(text, 445 - ((int) p.textWidth(text) / 2), -45);

			p.fill(200, 100, 100, 20);
			p.rect(0 + titleX, -20 + titleY, 290, 65);
			p.fill(200, 100, 100, 100);
			p.rect(0 + titleX, -40 + titleY, 290, 20);
			String title = "DRAG TITLE BLOCK";
			p.textFont(headers);
			p.fill(fontColor);
			p.text(title, (290 / 2) - ((int) p.textWidth(title) / 2) + titleX, -25 + titleY);
			p.textFont(font);
		}

		p.noStroke();
		p.fill(0);
		p.rect(10 + titleX, 10 + titleY, 390, 40);

		p.fill(fontColor);
		p.text(parent.animationTitle, 13 + titleX, 6 + titleY);
		p.textFont(temp);
		String time = fmt.print(data.currentTime);
		// String time = data.currentTime.toString();
		p.fill(0, 0, 100);
		// p.text("Time: " + time + " UTC+3", 13+titleX, 29+titleY);
		p.text("Time: " + time, 13 + titleX, 29 + titleY);
		p.textFont(font);
		drawBarScale(304 + titleX, 26 + titleY);
		p.popMatrix();

		p.textFont(font2);
		p.text(data.currentTime.toString("MMMM"), 20, 45);
		p.textFont(font);

	}

	private void drawLock() {
		if (!data.legendLocked) {
			p.textFont(message);
			p.fill(0, 0, 0, 180);
			p.rect(0, 0, parent.sketch.width, parent.sketch.height);
			p.fill(fontColor, 40);
			String text = "LEGEND UNLOCKED";
			p.text(text, parent.sketch.width / 2 - ((int) p.textWidth(text) / 2), parent.sketch.height / 2);
			p.textFont(font);
		}
	}

	public void drawBarScale(float x, float y) {
		// ripped from unfolding source BarScaleUI for greater customization
		float distance = MAX_DISPLAY_DISTANCE / map.getZoom();
		distance = getClosestDistance(distance);

		Location startLocation = null;
		Location destLocation = null;

		startLocation = map.getLocation(p.width / 2, p.height / 2);
		destLocation = GeoUtils.getDestinationLocation(startLocation, 90f, distance);

		ScreenPosition destPos = map.getScreenPosition(destLocation);
		ScreenPosition startPos = map.getScreenPosition(startLocation);
		float dx = destPos.x - startPos.x;

		p.stroke(0, 0, 100);
		p.strokeWeight(2);
		p.line(x, y - 3, x, y + 3);
		p.line(x, y, x + dx, y);
		p.line(x + dx, y - 3, x + dx, y + 3);
		p.fill(0, 0, 100);
		p.text(PApplet.nfs(distance, 0, 0) + " km", x + dx + 3, y + 4);
	}

	public float getClosestDistance(float distance) {
		return closest(distance, DISPLAY_DISTANCES);
	}

	public float closest(float of, List<Float> in) {
		float min = Float.MAX_VALUE;
		float closest = of;

		for (float v : in) {
			final float diff = Math.abs(v - of);

			if (diff < min) {
				min = diff;
				closest = v;
			}
		}
		return closest;
	}

	public void setFont(String fontName, int fontSize) {
		font = p.createFont(fontName, fontSize);
		p.textFont(font);
	}

	public void setFontColor(int c) {
		fontColor = c;
	}

	public boolean writeFile(OutputStream out) {

		DOMProcessor dom = new DOMProcessor();
		org.w3c.dom.Node root = dom.addElement("LegendLayout");

		org.w3c.dom.Node lb = dom.addElement("LegendBlock", root);
		dom.addAttribute("x", Float.toString(x), lb);
		dom.addAttribute("y", Float.toString(y), lb);
		// dom.addAttribute("offsetX",Float.toString(offsetX),lb);
		// dom.addAttribute("offsetY",Float.toString(offsetY),lb);

		org.w3c.dom.Node tb = dom.addElement("TitleBlock", root);
		dom.addAttribute("titleX", Float.toString(titleX), tb);
		dom.addAttribute("titleY", Float.toString(titleY), tb);
		// dom.addAttribute("titleSetX",Float.toString(titleSetX),tb);
		// dom.addAttribute("titleSetY",Float.toString(titleSetY),tb);

		org.w3c.dom.Node lcb = dom.addElement("LineColorBlock", root);
		dom.addAttribute("lineColorX", Float.toString(lineColorX), lcb);
		dom.addAttribute("lineColorY", Float.toString(lineColorY), lcb);
		// dom.addAttribute("offsetX",Float.toString(offsetX),lcb);
		// dom.addAttribute("offsetY",Float.toString(offsetY),lcb);

		org.w3c.dom.Node lwb = dom.addElement("LineWidthBlock", root);
		dom.addAttribute("lineWidthX", Float.toString(lineWidthX), lwb);
		dom.addAttribute("lineWidthY", Float.toString(lineWidthY), lwb);
		// dom.addAttribute("offsetX",Float.toString(offsetX),lwb);
		// dom.addAttribute("offsetY",Float.toString(offsetY),lwb);

		org.w3c.dom.Node pcb = dom.addElement("PointColorBlock", root);
		dom.addAttribute("pointColorX", Float.toString(pointColorX), pcb);
		dom.addAttribute("pointColorY", Float.toString(pointColorY), pcb);
		// dom.addAttribute("offsetX",Float.toString(offsetX),pcb);
		// dom.addAttribute("offsetY",Float.toString(offsetY),pcb);

		org.w3c.dom.Node psb = dom.addElement("PointSizeBlock", root);
		dom.addAttribute("pointSizeX", Float.toString(pointSizeX), psb);
		dom.addAttribute("pointSizeY", Float.toString(pointSizeY), psb);
		// dom.addAttribute("offsetX",Float.toString(offsetX),psb);
		// dom.addAttribute("offsetY",Float.toString(offsetY),psb);

		org.w3c.dom.Node vcb = dom.addElement("VectorColorBlock", root);
		dom.addAttribute("vectorColorX", Float.toString(vectorColorX), vcb);
		dom.addAttribute("vectorColorY", Float.toString(vectorColorY), vcb);
		// dom.addAttribute("offsetX",Float.toString(offsetX),vcb);
		// dom.addAttribute("offsetY",Float.toString(offsetY),vcb);

		org.w3c.dom.Node vsb = dom.addElement("VectorSizeBlock", root);
		dom.addAttribute("vectorLengthX", Float.toString(vectorLengthX), vsb);
		dom.addAttribute("vectorLengthY", Float.toString(vectorLengthY), vsb);
		// dom.addAttribute("offsetX",Float.toString(offsetX),vsb);
		// dom.addAttribute("offsetY",Float.toString(offsetY),vsb);

		return dom.writeXML(out);
	}

	public void readFile(InputStream inStream) {
		DOMProcessor dom = new DOMProcessor(inStream);
		if (dom.isEmpty()) {
			System.err.println("Could not extract XML data.");
		}

		org.w3c.dom.Node[] roots = dom.getElements("LegendLayout");
		org.w3c.dom.Node root = roots[0];

		org.w3c.dom.Node lb = dom.getNodeElement("LegendBlock", root);
		x = Float.parseFloat(dom.getNodeAttribute("x", lb));
		y = Float.parseFloat(dom.getNodeAttribute("y", lb));

		org.w3c.dom.Node tb = dom.getNodeElement("TitleBlock", root);
		titleX = Float.parseFloat(dom.getNodeAttribute("titleX", tb));
		titleY = Float.parseFloat(dom.getNodeAttribute("titleY", tb));

		org.w3c.dom.Node lcb = dom.getNodeElement("LineColorBlock", root);
		lineColorX = Float.parseFloat(dom.getNodeAttribute("lineColorX", lcb));
		lineColorY = Float.parseFloat(dom.getNodeAttribute("lineColorY", lcb));

		org.w3c.dom.Node lwb = dom.getNodeElement("LineWidthBlock", root);
		lineWidthX = Float.parseFloat(dom.getNodeAttribute("lineWidthX", lwb));
		lineWidthY = Float.parseFloat(dom.getNodeAttribute("lineWidthY", lwb));

		org.w3c.dom.Node pcb = dom.getNodeElement("PointColorBlock", root);
		pointColorX = Float.parseFloat(dom.getNodeAttribute("pointColorX", pcb));
		pointColorY = Float.parseFloat(dom.getNodeAttribute("pointColorY", pcb));

		org.w3c.dom.Node psb = dom.getNodeElement("PointSizeBlock", root);
		pointSizeX = Float.parseFloat(dom.getNodeAttribute("pointSizeX", psb));
		pointSizeY = Float.parseFloat(dom.getNodeAttribute("pointSizeY", psb));

		org.w3c.dom.Node vcb = dom.getNodeElement("VectorColorBlock", root);
		vectorColorX = Float.parseFloat(dom.getNodeAttribute("vectorColorX", vcb));
		vectorColorY = Float.parseFloat(dom.getNodeAttribute("vectorColorY", vcb));

		org.w3c.dom.Node vsb = dom.getNodeElement("VectorSizeBlock", root);
		vectorLengthX = Float.parseFloat(dom.getNodeAttribute("vectorLengthX", vsb));
		vectorLengthY = Float.parseFloat(dom.getNodeAttribute("vectorLengthY", vsb));

	}

}
