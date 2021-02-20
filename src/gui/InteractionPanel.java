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

import main.DesktopPane;
import main.SketchData;
import utils.Track;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

public class InteractionPanel extends JPanel {
	/**
	 * 
	 */
	// panel to control interaction
	// picks tags, buffer, and type of interactions
	// after each update all of the interactions are calculated
	private static final long serialVersionUID = 1L;
	static int openFrameCount = 0;
	static final int xOffset = 30, yOffset = 30;

	public JFileChooser dataChooser;
	private DesktopPane parent;
	private SketchData data;
	boolean viewHighlight = false;

	public static JButton generateBuffer;

	public WideComboBox tagSelect2;// create wideCombo Boxes for tag selection
	public WideComboBox tagSelect1;

	public WideComboBox bufferDistSelect;// create wideCombo boxes for buffer distance and time selection
	public WideComboBox bufferTimeSelect;

	public SpinnerModel tbSpinnerModel;
	public SpinnerModel dbSpinnerModel;

	public InteractionsBuffer ib = new InteractionsBuffer();// initialize buffer object

	public JCheckBox highlightBoundary;

	public void getInteractions() {
		data.interactingPoints = ib.calculateInteractingPoints(data.track1, data.track2, data.bufferDist,
				data.bufferTime);// add bufferTime to enable
		if (data.interactingPoints.isEmpty()) {
			data.noInteraction = true;
		} else {
			data.noInteraction = false;
		}
		data.interactionsGenerated = true;
	}

	public InteractionPanel(DesktopPane father) {
		parent = father;
		data = parent.data;
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}

		setLayout(new MigLayout("insets 0", "[][][]", "[][]"));

		// at start of program asks if there are multiple animals or not ( give
		// interacting options or not)
		if (parent.tagList.size() > 1) {
			data.multipleAnimals = true;
		} else {
			data.multipleAnimals = false;
		}

		Object[] tags = new Object[parent.tagList.size()];// create array of all tags used
		for (int i = 0; i < parent.tagList.size(); i++) {
			tags[i] = parent.tagList.get(i);
		}

		// only set if there are multiple animals to test interaction
		if (data.multipleAnimals == true) {
			data.id1 = null;// initialize 1st selected id to null
			data.id2 = null;// initialize 2nd selected id to null
			data.selectedIDs = new String[] { data.id1, data.id2 };// create string of selected IDs (gotten from
																	// SketchData)
			data.bufferTime = -1;// initially -1 so that time is invalid
			data.bufferDist = -1;// initially -1 so that distance is invalid

			// initialize tracks 1 and 2 to be default tag entries
			data.track1 = parent.trackList.get(parent.tagList.indexOf(data.id1));
			data.track2 = parent.trackList.get(parent.tagList.indexOf(data.id2));

			// tagSelect1
			JLabel tagSelectLbl1 = new JLabel("Tag 1:");
			tagSelectLbl1.setBackground(Color.LIGHT_GRAY);
			tagSelectLbl1.setForeground(Color.BLACK);
			tagSelectLbl1.setFont(new Font("Arial", Font.PLAIN, 12));
			this.add(tagSelectLbl1, "cell 0 0");

			tagSelect1 = new WideComboBox(tags);// tagSelect label
			tagSelect1.setMaximumSize(new Dimension(120, 32767));
			tagSelect1.setMaximumRowCount(100);
			tagSelect1.setForeground(Color.BLACK);
			tagSelect1.setBackground(UIManager.getColor("CheckBox.background"));
			tagSelect1.setFont(new Font("Arial", Font.PLAIN, 12));
			this.add(tagSelect1, "cell 0 0,growx");

			tagSelect1.setEnabled(true);
			tagSelect1.setSelectedItem(parent.tagList.get(0));
			data.id1 = parent.tagList.get(0);
			// automatically picks a track
			for (Entry<String, Track> entry : parent.trackList.entrySet()) {
				if (entry.getValue().getTag().equals(data.id1)) {
					data.track1 = entry.getValue();// set track1 variable to actual track selected
				}
			}
			data.selectedIDs[0] = (String) tagSelect1.getSelectedItem();

			tagSelect1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					data.id1 = (String) tagSelect1.getSelectedItem();
					for (Entry<String, Track> entry : parent.trackList.entrySet()) {
						if (entry.getValue().getTag().equals(data.id1)) {
							data.track1 = entry.getValue();// set track1 variable to actual track selected
						}
					}
					data.selectedIDs[0] = (String) tagSelect1.getSelectedItem();// set 1st element in selected ID array
																				// to tag1 selection
					data.tagChange = true;
					data.interactingPoints = ib.calculateInteractingPoints(data.track1, data.track2, data.bufferDist,
							data.bufferTime);// add bufferTime to enable
					data.interactionsGenerated = true;
					data.upToDate = false;
				}
			});

			// tagSelect2
			JLabel tagSelectLbl2 = new JLabel("Tag 2:");
			tagSelectLbl2.setBackground(Color.LIGHT_GRAY);
			tagSelectLbl2.setForeground(Color.BLACK);
			tagSelectLbl2.setFont(new Font("Arial", Font.PLAIN, 12));
			this.add(tagSelectLbl2, "cell 0 1");

			tagSelect2 = new WideComboBox(tags);
			tagSelect2.setMaximumSize(new Dimension(120, 32767));
			tagSelect2.setMaximumRowCount(100);
			tagSelect2.setForeground(Color.BLACK);
			tagSelect2.setBackground(UIManager.getColor("CheckBox.background"));
			tagSelect2.setFont(new Font("Arial", Font.PLAIN, 12));
			this.add(tagSelect2, "cell 0 1,growx");

			tagSelect2.setEnabled(true);
			tagSelect2.setSelectedItem(parent.tagList.get(1));
			data.id2 = parent.tagList.get(1);
			data.selectedIDs[1] = (String) tagSelect2.getSelectedItem();
			for (Entry<String, Track> entry : parent.trackList.entrySet()) {
				if (entry.getValue().getTag().equals(data.id2)) {
					data.track2 = entry.getValue();// set track2 variable to actual track selected
				}
			}

			tagSelect2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					data.id2 = (String) tagSelect2.getSelectedItem();
					for (Entry<String, Track> entry : parent.trackList.entrySet()) {
						if (entry.getValue().getTag().equals(data.id2)) {
							data.track2 = entry.getValue();// set track2 variable to actual track selected
						}
					}
					data.selectedIDs[1] = (String) tagSelect2.getSelectedItem();// set second element in selected ID
																				// array to tag2 selection
					data.interactingPoints = ib.calculateInteractingPoints(data.track1, data.track2, data.bufferDist,
							data.bufferTime);// add bufferTime to enable
					data.interactionsGenerated = true;
					data.tagChange = true;
					data.upToDate = false;
				}

			});
			JLabel both = new JLabel("Visualize Interactions in 2D and 3D");
			this.add(both, "cell 0 5");

			// checkbox for if highlights should be done on fly(makes buffer "ring" around
			// lead point
			JCheckBox highlightOnFly = new JCheckBox("Highlight Interactions");
			highlightOnFly.setIconTextGap(5);
			highlightOnFly.setHorizontalTextPosition(SwingConstants.RIGHT);
			highlightOnFly.setForeground(Color.BLACK);
			highlightOnFly.setFont(new Font("Arial", Font.PLAIN, 12));
			highlightOnFly.setMargin(new Insets(10, 5, 10, 10));
			this.add(highlightOnFly, "cell 0 6");
			highlightOnFly.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent evt) {
					JCheckBox cb = (JCheckBox) evt.getSource();
					if (cb.isSelected()) {// sets boolean variable to true or false
						data.highlightOnFly = true;

					} else {
						data.highlightOnFly = false;
					}
				}
			});

			// checkbox for highlightAll option(highlights all points that have been passed
			// in timeline where interactions happened)
			JCheckBox highlightAllInteractions = new JCheckBox("Interaction History");
			highlightAllInteractions.setForeground(Color.BLACK);
			highlightAllInteractions.setFont(new Font("Arial", Font.PLAIN, 12));
			highlightAllInteractions.setMargin(new Insets(10, 5, 10, 10));
			this.add(highlightAllInteractions, "cell 0 6");
			highlightAllInteractions.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent evt) {
					JCheckBox cb = (JCheckBox) evt.getSource();
					if (cb.isSelected()) {// sets boolean variable to true or false
						data.highlightAllInteractions = true;

					} else {
						data.highlightAllInteractions = false;
					}
				}
			});

			JLabel one = new JLabel("Visualize Interactions in 2D only");
			this.add(one, "cell 0 8");

			// checkbox for highlighting Boundary (shows the boundary colors the
			// intersection and highlights it when both inside)
			highlightBoundary = new JCheckBox("Highlight Interactions and Boundary");
			highlightBoundary.setForeground(Color.BLACK);
			highlightBoundary.setFont(new Font("Arial", Font.PLAIN, 12));
			highlightBoundary.setMargin(new Insets(10, 5, 10, 10));
			this.add(highlightBoundary, "cell 0 9");

			highlightBoundary.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent evt) {
					JCheckBox cb = (JCheckBox) evt.getSource();
					if (cb.isSelected()) {// sets boolean variable to true or false
						data.highlight_interaction_boundary = true;
					} else {
						data.highlight_interaction_boundary = false;
					}
				}
			});

			JLabel bufferDistLbl = new JLabel("Buffer Distance(m):");
			bufferDistLbl.setBackground(Color.LIGHT_GRAY);
			bufferDistLbl.setForeground(Color.BLACK);
			bufferDistLbl.setFont(new Font("Arial", Font.PLAIN, 12));
			this.add(bufferDistLbl, "cell 0 2");

			dbSpinnerModel = new SpinnerNumberModel(100, // initial value
					0, // min
					1000, // max
					10);// step

			JSpinner dbspinner = new JSpinner(dbSpinnerModel);
			data.bufferDist = (int) dbspinner.getValue();
			JComponent dist_buff_editor = new JSpinner.NumberEditor(dbspinner, "####");
			dbspinner.setEditor(dist_buff_editor);
			this.add(dbspinner, "cell 0 2");

			dbspinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					data.bufferDist = (int) dbspinner.getValue();
					data.interactingPoints = ib.calculateInteractingPoints(data.track1, data.track2, data.bufferDist,
							data.bufferTime);// add bufferTime to enable
					data.interactionsGenerated = true;
					data.upToDate = true;
				}
			});

			JLabel bufferTimeLbl = new JLabel("Buffer Time(hours):");
			bufferTimeLbl.setBackground(Color.LIGHT_GRAY);
			bufferTimeLbl.setForeground(Color.BLACK);
			bufferTimeLbl.setFont(new Font("Arial", Font.PLAIN, 12));
			this.add(bufferTimeLbl, "cell 0 3");

			tbSpinnerModel = new SpinnerNumberModel(0, // initial value
					0, // min
					32, // max
					2);// step

			JSpinner tbspinner = new JSpinner(tbSpinnerModel);
			data.bufferTime = (int) tbspinner.getValue();
			JComponent time_buff_editor = new JSpinner.NumberEditor(tbspinner, "####");
			tbspinner.setEditor(time_buff_editor);
			this.add(tbspinner, "cell 0 3");

			tbspinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					data.bufferTime = (int) tbspinner.getValue();
					data.interactingPoints = ib.calculateInteractingPoints(data.track1, data.track2, data.bufferDist,
							data.bufferTime);// add bufferTime to enable
					data.interactionsGenerated = true;
					data.upToDate = true;
				}
			});

			getInteractions();
			System.out.println("last");
			data.interactingPoints = ib.calculateInteractingPoints(data.track1, data.track2, data.bufferDist,
					data.bufferTime);// add bufferTime to enable
			data.interactionsGenerated = true;

			data.upToDate = true;

		} else// only one animal
		{
			data.id1 = null;// initialize 1st selected id to null
			data.id2 = null;// initialize 2nd selected id to null
			data.selectedIDs = new String[] { data.id1, data.id2 };// create string of selected IDs (gotten from
																	// SketchData)
			data.bufferTime = -1;// initially -1 so that time is invalid
			data.bufferDist = -1;// initially -1 so that distance is invalid
		}

	}

}