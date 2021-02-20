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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import org.gicentre.utils.colour.ColourTable;

public class Colors {

	// swatch dimensions, small for control panel, large for gradient editor
	public static final int LARGE_WIDTH = 300;
	public static final int LARGE_HEIGHT = 20;
	public static final int SMALL_WIDTH = 95;
	public static final int SMALL_HEIGHT = 12;

	DesktopPane parent;
	SketchData data;
	Color[] colorList12 = {
			// Brewer for up to 12 classes
			new Color(166, 206, 227), new Color(31, 120, 180), new Color(178, 223, 138), new Color(51, 160, 44),
			new Color(251, 154, 153), new Color(227, 26, 28), new Color(253, 191, 111), new Color(255, 127, 0),
			new Color(202, 178, 214), new Color(106, 61, 154), new Color(255, 255, 153), new Color(177, 89, 40), };
	Color[] colorList9 = {
			// Brewer for up to 9 classes
			new Color(228, 26, 28),
			// new Color(55,126,184),
			new Color(26, 85, 210),
			// new Color(77,175,74),
			new Color(34, 188, 8),
			// new Color(152,78,163),
			new Color(176, 2, 207),
			// new Color(255,127,0),
			new Color(255, 105, 51), new Color(255, 255, 51), new Color(166, 86, 40), new Color(247, 129, 191),
			new Color(153, 153, 153), };

	public List<ColourTable> coloursCont;
	public List<ImageIcon> colorRampList;
	public List<ImageIcon> largeRampList;

	private Color legendColor = Color.WHITE;

	public Colors(DesktopPane father) {
		parent = father;

		ColourTable bToP = new ColourTable();
		bToP.addContinuousColourRule((float) (0 / 1), 45, 0, 255);
		bToP.addContinuousColourRule((float) (1 / 1), 255, 0, 38);

		ColourTable bToG = new ColourTable();
		bToG.addContinuousColourRule((float) (0 / 1), Color.blue.getRGB());
		bToG.addContinuousColourRule((float) (1 / 1), Color.green.getRGB());

		ColourTable yToR = new ColourTable();
		yToR.addContinuousColourRule((float) (0 / 1), Color.yellow.getRGB());
		yToR.addContinuousColourRule((float) (1 / 1), Color.red.getRGB());

		ColourTable yToG = new ColourTable();
		yToG.addContinuousColourRule((float) (0 / 1), Color.yellow.getRGB());
		yToG.addContinuousColourRule((float) (1 / 1), Color.green.getRGB());

		ColourTable spectrum = new ColourTable();
		spectrum.addContinuousColourRule((float) (0), 5, 0, 206);
		spectrum.addContinuousColourRule((float) (0.1), 2, 96, 206);
		spectrum.addContinuousColourRule((float) (0.2), 10, 204, 203);
		spectrum.addContinuousColourRule((float) (0.3), 48, 205, 184);
		spectrum.addContinuousColourRule((float) (0.4), 103, 203, 168);
		spectrum.addContinuousColourRule((float) (0.5), 181, 232, 73);
		spectrum.addContinuousColourRule((float) (0.6), 255, 252, 0);
		spectrum.addContinuousColourRule((float) (0.7), 254, 207, 5);
		spectrum.addContinuousColourRule((float) (0.8), 253, 154, 0);
		spectrum.addContinuousColourRule((float) (0.9), 255, 77, 1);
		spectrum.addContinuousColourRule((float) (1.0), 255, 0, 0);

		coloursCont = new ArrayList<ColourTable>();
		coloursCont.add(bToP);
		coloursCont.add(bToG);
		coloursCont.add(yToR);
		coloursCont.add(yToG);
		coloursCont.add(spectrum);
		coloursCont.add(ColourTable.getPresetColourTable(ColourTable.YL_GN, 0, 1));
		coloursCont.add(ColourTable.getPresetColourTable(ColourTable.YL_GN_BU, 0, 1));
		coloursCont.add(ColourTable.getPresetColourTable(ColourTable.GN_BU, 0, 1));
		coloursCont.add(ColourTable.getPresetColourTable(ColourTable.BU_GN, 0, 1));
		coloursCont.add(ColourTable.getPresetColourTable(ColourTable.PU_BU_GN, 0, 1));
		coloursCont.add(ColourTable.getPresetColourTable(ColourTable.PU_BU, 0, 1));
		coloursCont.add(ColourTable.getPresetColourTable(ColourTable.BU_PU, 0, 1));
		coloursCont.add(ColourTable.getPresetColourTable(ColourTable.RD_PU, 0, 1));
		coloursCont.add(ColourTable.getPresetColourTable(ColourTable.PU_RD, 0, 1));
		coloursCont.add(ColourTable.getPresetColourTable(ColourTable.OR_RD, 0, 1));
		coloursCont.add(ColourTable.getPresetColourTable(ColourTable.YL_OR_RD, 0, 1));
		coloursCont.add(ColourTable.getPresetColourTable(ColourTable.YL_OR_BR, 0, 1));
		coloursCont.add(ColourTable.getPresetColourTable(ColourTable.PURPLES, 0, 1));
		coloursCont.add(ColourTable.getPresetColourTable(ColourTable.BLUES, 0, 1));
		coloursCont.add(ColourTable.getPresetColourTable(ColourTable.GREENS, 0, 1));
		coloursCont.add(ColourTable.getPresetColourTable(ColourTable.ORANGES, 0, 1));
		coloursCont.add(ColourTable.getPresetColourTable(ColourTable.REDS, 0, 1));
		coloursCont.add(ColourTable.getPresetColourTable(ColourTable.GREYS, 0, 1));
		coloursCont.add(ColourTable.getPresetColourTable(ColourTable.PU_OR, 0, 1));
		coloursCont.add(ColourTable.getPresetColourTable(ColourTable.BR_B_G, 0, 1));
		coloursCont.add(ColourTable.getPresetColourTable(ColourTable.P_R_GN, 0, 1));
		coloursCont.add(ColourTable.getPresetColourTable(ColourTable.PI_Y_G, 0, 1));
		coloursCont.add(ColourTable.getPresetColourTable(ColourTable.RD_BU, 0, 1));
		coloursCont.add(ColourTable.getPresetColourTable(ColourTable.RD_GY, 0, 1));
		coloursCont.add(ColourTable.getPresetColourTable(ColourTable.RD_YL_BU, 0, 1));
		coloursCont.add(ColourTable.getPresetColourTable(ColourTable.SPECTRAL, 0, 1));
		coloursCont.add(ColourTable.getPresetColourTable(ColourTable.RD_YL_GN, 0, 1));

		colorRampList = createSwatchList(coloursCont, "small");
		largeRampList = createSwatchList(coloursCont, "large");
	}

	public List<ImageIcon> createSwatchList(List<ColourTable> list, String size) {
		int w, h;
		if (size.equals("large")) {
			w = LARGE_WIDTH;
			h = LARGE_HEIGHT;
		} else if (size.equals("small")) {
			w = SMALL_WIDTH;
			h = SMALL_HEIGHT;
		} else {
			return null;
		}
		return createSwatchList(list, w, h);
	}

	public List<ImageIcon> createSwatchList(List<ColourTable> list, int width, int height) {
		List<ImageIcon> icons = new ArrayList<ImageIcon>();
		for (int i = 0; i < list.size(); i++) {
			icons.add(createSwatch(list.get(i), width, height));
		}
		return icons;
	}

	public ImageIcon createSwatch(ColourTable colorTable, String size) {
		int w, h;
		if (size.equals("large")) {
			w = LARGE_WIDTH;
			h = LARGE_HEIGHT;
		} else if (size.equals("small")) {
			w = SMALL_WIDTH;
			h = SMALL_HEIGHT;
		} else {
			return null;
		}
		return createSwatch(colorTable, w, h);
	}

	public ImageIcon createSwatch(ColourTable colorTable, int width, int height) {
		BufferedImage swatch = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = swatch.createGraphics();
		for (int i = 0; i < width; i++) {
			Rectangle rect = new Rectangle(i, 0, 1, height);
			String string = Integer.toHexString(colorTable.findColour((float) i / width));
			string = string.substring(2, string.length());
			Color color = Color.decode("#" + string);
			g.setPaint(color);
			g.fill(rect);
		}
		return new ImageIcon(swatch);

	}

	public Color getTagColor(String key) {
		data = parent.data;
		int tagSize = parent.tagList.size();
		int index = parent.tagList.indexOf(key);
		Color color;
		if (tagSize <= 9) {
			color = colorList9[index];
		} else if (tagSize <= 12) {
			color = colorList12[index];
		} else {
			if (index > colorList12.length - 1) {
				float num = (((float) index - 12) / ((float) tagSize - 12)) * 255;
				int rgbNum = 255 - (int) num;
				color = new Color(rgbNum, rgbNum, rgbNum);
			} else {
				color = colorList12[index];
			}
		}
		return (color);
	}

	public void setLegendColor(Color c) {
		legendColor = c;
	}

	public Color getLegendColor() {
		return legendColor;
	}

}
