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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.joda.time.DateTime;

import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.utils.ScreenPosition;

import main.DesktopPane;
import main.SketchData;

import utils.Buffer;
import utils.PointRecord;
import utils.Track;
//import org.jfree.ui.RefineryUtilities; 
import net.miginfocom.swing.MigLayout;

public class ActivitySpacePanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static int openFrameCount = 0;
	static final int xOffset = 30, yOffset = 30;

	public JFileChooser dataChooser;
	private DesktopPane parent;
	private SketchData data;

	// public static JButton generateBuffer;
	public static JButton generateChart;
	public static JButton exportFile;
	public WideComboBox bufferDistSelect;
	public Buffer b = new Buffer();
	public SpinnerModel bufferSpinnerModel;
	public SpinnerModel confidenceSpinnerModel;
	public WideComboBox tagSelect1;
	public int[] visitedPoints;
	public Hashtable<Location, Integer> hullPos;
	// public BoundaryBuffer bb = new BoundaryBuffer();

	public ActivitySpacePanel(DesktopPane father) {

		parent = father;
		data = parent.data;

		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		setLayout(new MigLayout("insets 0", "[grow][]", "[][][][][]"));

		// at start of program assess if there are multiple animals or not ( give
		// interacting options or not)
		if (parent.tagList.size() > 1) {
			data.multipleAnimals = true;
		} else {
			data.multipleAnimals = false;
		}

		data.selectedID = parent.tagList.get(0);
		Object[] tags = new Object[parent.tagList.size()];// create array of all tags used
		for (int i = 0; i < parent.tagList.size(); i++) {
			tags[i] = parent.tagList.get(i);
		}

		// add a drop down table for selecting a tag for a specific tiger
		JLabel tagSelected = new JLabel("Tag:");
		tagSelected.setBackground(Color.LIGHT_GRAY);
		tagSelected.setForeground(Color.BLACK);
		tagSelected.setFont(new Font("Arial", Font.PLAIN, 12));
		this.add(tagSelected, "cell 0 0");

		tagSelect1 = new WideComboBox(tags);// tagSelect label
		tagSelect1.setMaximumSize(new Dimension(120, 32767));
		tagSelect1.setMaximumRowCount(100);
		tagSelect1.setForeground(Color.BLACK);
		tagSelect1.setBackground(UIManager.getColor("CheckBox.background"));
		tagSelect1.setFont(new Font("Arial", Font.PLAIN, 12));
		this.add(tagSelect1, "cell 0 0,growx");
		if (data.multipleAnimals) {// only enabled if there are multiple animals being analyzed
			tagSelect1.setEnabled(true);
		} else {
			tagSelect1.setEnabled(false);
		}
		tagSelect1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WideComboBox cb = (WideComboBox) e.getSource();
				String item = (String) cb.getSelectedItem();
				for (int i = 0; i < parent.tagList.size(); i++) {
					if (parent.tagList.get(i).equals(item)) {
						data.selectedID = parent.tagList.get(i);// set id1 variable to selected tag
						data.updateSelectedID = true;
					}
				}

			}
		});

		// check box for visualizing the convex hull
		JCheckBox Draw = new JCheckBox("Draw Activity Space (Convex Hull)");
		Draw.setForeground(Color.BLACK);
		Draw.setBackground(UIManager.getColor("CheckBox.background"));
		Draw.setFont(new Font("Arial", Font.PLAIN, 12));
		this.add(Draw, "cell 0 0,grow");
		Draw.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JCheckBox cb = (JCheckBox) evt.getSource();
				if (cb.isSelected()) {
					data.boundary = true; // if check box is checked, set data.boundary to true (used in sketch.java)
				} else {
					data.boundary = false; // if check box is not checked, set data.boundary to false (used in
											// sketch.java)
				}
			}
		});

		// color selection for boundary color
		// add opacity options (ref to boundary polygon panel)
		JLabel lblOpac = new JLabel("Boundary Color:");
		lblOpac.setForeground(Color.BLACK);
		lblOpac.setFont(new Font("Arial", Font.PLAIN, 12));
		lblOpac.setBackground(Color.LIGHT_GRAY);
		this.add(lblOpac, "cell 0 2");

		final JButton colorButton = new JButton("");
		add(colorButton, "cell 0 2");
		colorButton.setMinimumSize(new Dimension(15, 15));
		colorButton.setMaximumSize(new Dimension(15, 15));
		colorButton.setBackground(data.boundaryColor);
		colorButton.setMargin(new Insets(0, 0, 0, 0));
		colorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				final JColorChooser colorChooser = new JColorChooser();
				JDialog maxDialog = JColorChooser.createDialog((Component) evt.getSource(), "Pick a Color", true, // modal
						colorChooser, new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								data.boundaryColor = colorChooser.getColor();
								colorButton.setBackground(data.boundaryColor);
							}
						}, new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent event) {
							}
						});
				maxDialog.setVisible(true);
			}
		});

		JSpinner weight = new JSpinner();
		weight.setForeground(Color.BLACK);
		weight.setBackground(Color.LIGHT_GRAY);
		weight.setFont(new Font("Arial", Font.PLAIN, 9));
		weight.setModel(new SpinnerNumberModel(1, 1, 255, 1));
		weight.setToolTipText("");
		ToolTipManager.sharedInstance().registerComponent(weight);
		weight.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JSpinner spin = (JSpinner) evt.getSource();
				data.boundaryWeight = (Integer) spin.getValue();
			}
		});
		weight.setValue(2);
		this.add(weight, "cell 0 2");

		JLabel transparency = new JLabel("Transparency:");
		transparency.setBackground(Color.LIGHT_GRAY);
		transparency.setForeground(Color.BLACK);
		transparency.setFont(new Font("Arial", Font.PLAIN, 12));
		this.add(transparency, "cell 0 4");

		JTextField Transparency_TextField = new JTextField(3);
		Transparency_TextField.setText(Integer.toString(data.boundaryColor.getAlpha()));
		Transparency_TextField.setEditable(false);
		Transparency_TextField.setBackground(Color.white);
		JSlider Transparency_Slider = new JSlider(0, 255, 255);
		Transparency_Slider.setMajorTickSpacing(85);
		Transparency_Slider.setMinorTickSpacing(10);
		Transparency_Slider.setPaintTrack(true);
		Transparency_Slider.setPaintTicks(true);
		Transparency_Slider.setPaintLabels(true);
		Transparency_Slider.setFont(new Font("Arial", Font.PLAIN, 8));
		Transparency_Slider.setToolTipText("Transparency");
		Transparency_Slider.setSnapToTicks(true);
		Transparency_Slider.setInverted(false);
		this.add(Transparency_Slider, "cell 0 4");
		this.add(Transparency_TextField, "cell 0 4");
		Transparency_Slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {

				data.boundaryColor = new Color(data.boundaryColor.getRed(), data.boundaryColor.getGreen(),
						data.boundaryColor.getBlue(), (int) Transparency_Slider.getValue());
				Transparency_TextField.setText(Integer.toString(data.boundaryColor.getAlpha()));
			}
		});

		JCheckBox highlight_all = new JCheckBox("Highlight Boundary Visits (All History)");
		JCheckBox highlight_fly = new JCheckBox("Highlight Boundary Visits (On the Fly)");
		// highlight_all.setEnabled(true);
		// highlight_fly.setEnabled(true);
		highlight_all.setForeground(Color.BLACK);
		highlight_all.setBackground(UIManager.getColor("CheckBox.background"));
		highlight_all.setFont(new Font("Arial", Font.PLAIN, 12));
		this.add(highlight_all, "cell 0 6,grow");
		highlight_all.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JCheckBox cb = (JCheckBox) evt.getSource();
				if (cb.isSelected()) {
					highlight_fly.setSelected(false);
					data.highlight_all = true;// if check box is checked, set data.highlight_all to true (used in
												// sketch.java)
					// data.highlight_fly = false;
				} else {
					// highlight_fly.setSelected(true);
					data.highlight_all = false;// if check box is not checked, set data.highlight_all to false (used in
												// sketch.java)
				}
			}
		});

		highlight_fly.setForeground(Color.BLACK);
		highlight_fly.setBackground(UIManager.getColor("CheckBox.background"));
		highlight_fly.setFont(new Font("Arial", Font.PLAIN, 12));
		this.add(highlight_fly, "cell 0 6,grow");
		highlight_fly.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JCheckBox cb = (JCheckBox) evt.getSource();
				if (cb.isSelected()) {
					highlight_all.setSelected(false);
					data.highlight_fly = true;
					// data.highlight_all = false;//if check box is checked, set data.highlight_fly
					// to true (used in sketch.java)
				} else {
					// highlight_all.setSelected(true);
					data.highlight_fly = false;
					// data.highlight_all = true;//if check box is not checked, set
					// data.highlight_fly to false (used in sketch.java)
				}
			}
		});

		JLabel confidenceIntervalLbl = new JLabel("Confidence Interval:");
		confidenceIntervalLbl.setBackground(Color.LIGHT_GRAY);
		confidenceIntervalLbl.setForeground(Color.BLACK);
		confidenceIntervalLbl.setFont(new Font("Arial", Font.PLAIN, 12));
		this.add(confidenceIntervalLbl, "cell 0 8");

		confidenceSpinnerModel = new SpinnerNumberModel(100, // initial value
				96, // min
				100, // max
				2);// step
		JSpinner cspinner = new JSpinner(confidenceSpinnerModel);
		this.add(cspinner, "cell 0 8");
		cspinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				data.confidenceInterval = (int) cspinner.getValue();
				data.updateConfidenceInterval = true;

			}
		});

		JCheckBox includeFilteredPts = new JCheckBox("Include Filtered Points");
		includeFilteredPts.setForeground(Color.BLACK);
		includeFilteredPts.setBackground(UIManager.getColor("CheckBox.background"));
		includeFilteredPts.setFont(new Font("Arial", Font.PLAIN, 12));
		this.add(includeFilteredPts, "cell 0 8,grow");
		includeFilteredPts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JCheckBox cb = (JCheckBox) evt.getSource();
				if (cb.isSelected()) {
					data.includeFilteredPts = true;

				} else {
					data.includeFilteredPts = false;
				}
			}
		});

		// user select buffer distance
		JLabel bufferDistLbl = new JLabel("Buffer Distance:");
		bufferDistLbl.setBackground(Color.LIGHT_GRAY);
		bufferDistLbl.setForeground(Color.BLACK);
		bufferDistLbl.setFont(new Font("Arial", Font.PLAIN, 12));
		this.add(bufferDistLbl, "cell 0 10");

		bufferSpinnerModel = new SpinnerNumberModel(5, // initial value
				0, // min
				35, // max
				1);// step
		JSpinner spinner = new JSpinner(bufferSpinnerModel);
		this.add(spinner, "cell 0 10");
		spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				data.boundaryDist = (int) spinner.getValue();

			}
		});

		exportFile = new JButton("Generate Boundary Visits File");// change color if displaying or not?
		exportFile.setIconTextGap(5);
		exportFile.setHorizontalTextPosition(SwingConstants.LEFT);
		exportFile.setForeground(Color.BLACK);
		exportFile.setBackground(new Color(227, 227, 227));
		exportFile.setFont(new Font("Arial", Font.PLAIN, 12));
		exportFile.setMargin(new Insets(5, 5, 5, 5));
		this.add(exportFile, "cell 0 10");
		// generateBuffer enabled or disabled in sketch.java
		exportFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				data.bvFile = true;
			}

		});

		// bufferDistSelect = new WideComboBox(b.getBufferDistances());
		// bufferDistSelect.setMaximumSize(new Dimension(120, 32767));
		// bufferDistSelect.setMaximumRowCount(100);
		// bufferDistSelect.setForeground(Color.BLACK);
		// bufferDistSelect.setBackground(UIManager.getColor("CheckBox.background"));
		// bufferDistSelect.setFont(new Font("Arial", Font.PLAIN, 12));
		// this.add(bufferDistSelect, "cell 0 4,grow");
		// bufferDistSelect.setEnabled(true);
		//
		// bufferDistSelect.addActionListener(new ActionListener () {
		// public void actionPerformed(ActionEvent e) {
		// WideComboBox cb = (WideComboBox)e.getSource();
		// double item = Double.parseDouble((String)cb.getSelectedItem());
		// data.boundaryDist = item;
		// }
		// });

		// generateChart = new JButton("Bar Chart of Boundary Visit");
		// generateChart.setIconTextGap(0);
		// generateChart.setHorizontalTextPosition(SwingConstants.LEFT);
		// generateChart.setForeground(Color.BLACK);
		// generateChart.setBackground(new Color(227,227,227));
		// generateChart.setFont(new Font("Arial", Font.PLAIN, 12));
		// //generateBuffer.setMargin(new Insets(0, 0, 0, 0));
		// this.add(generateChart, "cell 0 8,grow");
		// //generateBuffer enabled or disabled in sketch.java
		// generateChart.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent evt) {
		// data.drawBuffer=true;
		//
		// }
		// });

		// if (data.drawBuffer&&data.drawChart){
		// for (int i=0; i<data.finalSectorName.size(); i++){
		//
		// System.out.println(data.finalSectorName.get(i)+": "+data.finalVisit.get(i));
		// }

		// BufferChartPanel chart = new BufferChartPanel("Buffer Bar Chart", "Frequency
		// of boundary visit",
		// data.finalSectorName, data.numVisit );
		// chart.pack( );
		// RefineryUtilities.centerFrameOnScreen( chart );
		// chart.setVisible( true );
		// }

	}

	public static int orientation(PointRecord p, PointRecord q, PointRecord r) {
		float val = (q.getLocation().y - p.getLocation().y) * (r.getLocation().x - q.getLocation().x)
				- (q.getLocation().x - p.getLocation().x) * (r.getLocation().y - q.getLocation().y);

		if (val == 0)
			return 0; // collinear
		return (val > 0) ? 1 : 2; // clock or counterclock wise
	}

	public void writeToFile(int[] visitedPt) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("boundary_visit"));
		writer.append("Tiger_Tag:" + data.selectedID);
		writer.newLine();
		writer.append("point");
		writer.append(",");
		writer.append("Visited");
		writer.newLine();
		for (int i = 0; i < visitedPt.length; i++) {
			writer.append(Integer.toString(i + 1));
			writer.append(",");
			writer.append(Integer.toString(visitedPt[i]));
			writer.newLine();
		}

		writer.close();
	}
}
