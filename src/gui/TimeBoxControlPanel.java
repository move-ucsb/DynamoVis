/*

  	DynamoVis Animation Tool
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

package gui;

import main.Colors;
import main.DesktopPane;
import main.Sketch;
import main.SketchData;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
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

// Set up a control panel for various parameters of space-time cube 
public class TimeBoxControlPanel extends JPanel {
    /*
     *
     *
     */
    private static final long serialVersionUID = 1L;

    static int openFrameCount = 0;
    static final int xOffset = 30; 
    static final int yOffset = 30;

    JFrame desktop;
    public Dimension animationSize = new Dimension(1280, 720);
    public Dimension boxSize = new Dimension(400, 400);
    public Colors colors;
    public Sketch sketch;

    public JFileChooser dataChooser;
    private DesktopPane parent;
    private SketchData data;

    public WideComboBox tagSelect1;
    public WideComboBox tagSelect2;
    public boolean tag2;

    // buttons for choosing the year and month
    public SpinnerModel syearSpinnerModel;
    public SpinnerModel smonthSpinnerModel;
    public SpinnerModel eyearSpinnerModel;
    public SpinnerModel emonthSpinnerModel;

    // check boxes for labels and highlighting boundary interaction
    public JCheckBox timeLabel;
    public JCheckBox latLongLabel;
    public JCheckBox rotateLabel;
    public JCheckBox bdyInteraction;
    public JCheckBox staticbox;
    public JCheckBox on;

    public TimeBoxControlPanel(DesktopPane father) {
        parent = father;
        data = parent.data;

        setLayout(new MigLayout("insets 0", "[][][][][]"));

        Object[] tags = new Object[parent.tagList.size()];// create array of all tags used
        for (int i = 0; i < parent.tagList.size(); i++) {
            tags[i] = parent.tagList.get(i);
        }

        data.timeBoxStartYear = data.startTime.getYear();
        data.timeBoxStartMonth = data.startTime.getMonthOfYear();
        data.timeBoxEndYear = data.endTime.getYear();
        data.timeBoxEndMonth = data.endTime.getMonthOfYear();

        // boundary alpha values
        JLabel label = new JLabel("Dim Alpha(%)");
		label.setBackground(Color.LIGHT_GRAY);
		label.setForeground(Color.BLACK);
		label.setFont(new Font("Arial", Font.PLAIN, 9));
		this.add(label, "cell 0 0");
        JSpinner dimAlpha = new JSpinner();
		dimAlpha.setForeground(Color.BLACK);
		dimAlpha.setBackground(Color.LIGHT_GRAY);
		dimAlpha.setFont(new Font("Arial", Font.PLAIN, 9));
		dimAlpha.setModel(new SpinnerNumberModel(25, 0, 100, 1));
		dimAlpha.setToolTipText("Adjust the alpha value of boundary when it occludes the visualization.");
		this.add(dimAlpha, "cell 0 1");
		dimAlpha.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JSpinner spin = (JSpinner) evt.getSource();
				data.dimAlpha = (Integer)(spin.getValue()) * 2.55f;
			}
		});
        label = new JLabel("Normal Alpha(%)");
		label.setBackground(Color.LIGHT_GRAY);
		label.setForeground(Color.BLACK);
		label.setFont(new Font("Arial", Font.PLAIN, 9));
		this.add(label, "cell 1 0");
        JSpinner normalAlpha = new JSpinner();
		normalAlpha.setForeground(Color.BLACK);
		normalAlpha.setBackground(Color.LIGHT_GRAY);
		normalAlpha.setFont(new Font("Arial", Font.PLAIN, 9));
		normalAlpha.setModel(new SpinnerNumberModel(75, 0, 100, 1));
		normalAlpha.setToolTipText("Adjust the alpha value of boundary when it occludes the visualization.");
		this.add(normalAlpha, "cell 1 1");
		normalAlpha.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JSpinner spin = (JSpinner) evt.getSource();
				data.normalAlpha = (Integer)(spin.getValue()) * 2.55f;
			}
		});

        on = new JCheckBox("On/Off");
        on.setIconTextGap(5);
        on.setHorizontalTextPosition(SwingConstants.RIGHT);
        on.setForeground(Color.BLACK);
        on.setFont(new Font("Arial", Font.PLAIN, 9));;
        this.add(on, "cell 0 2");
        on.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                JCheckBox cb = (JCheckBox) evt.getSource();
                if (cb.isSelected()) {
                    parent.openSTC();
				} else {
					parent.closeSTC();
				}
            }
        });

        // checkbox for idle rotation
        rotateLabel = new JCheckBox("Rotate");
        rotateLabel.setIconTextGap(5);
        rotateLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        rotateLabel.setForeground(Color.BLACK);
        rotateLabel.setFont(new Font("Arial", Font.PLAIN, 9));;
        this.add(rotateLabel, "cell 1 2");
        rotateLabel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                JCheckBox cb = (JCheckBox) evt.getSource();
                if (cb.isSelected()) {// sets boolean variable to true or false
                    data.rotateView = true;

                } else {
                    data.rotateView = false;

                }
            }
        });
		
        // checkbox for the basemap in the box labels
        //only have the option to have a basemap if map isn't too large for it to be helpful
        float maxEast = data.mapExtent[1].getLon();
        float maxWest = data.mapExtent[0].getLon();
        float longDiff = Math.abs(maxEast)-Math.abs(maxWest);
        float latDiff = data.mapExtent[0].getLat()-data.mapExtent[1].getLat();
        if (longDiff <= 100 && latDiff <=100) {
            latLongLabel = new JCheckBox("Base Map");
            latLongLabel.setIconTextGap(5);
            latLongLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
            latLongLabel.setForeground(Color.BLACK);
            latLongLabel.setFont(new Font("Arial", Font.PLAIN, 9));
            this.add(latLongLabel, "cell 1 4");
            latLongLabel.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent evt) {
                    JCheckBox cb = (JCheckBox) evt.getSource();
                    if (cb.isSelected()) {// sets boolean variable to true or false
                        data.basemap = true;

                    } else {
                        data.basemap = false;

                    }
                }
            });
        }

        //checkbox for overall box outline
        latLongLabel = new JCheckBox("Box Skeleton");
        latLongLabel.setIconTextGap(5);
        latLongLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        latLongLabel.setForeground(Color.BLACK);
        latLongLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        latLongLabel.setSelected(true);
        this.add(latLongLabel, "cell 0 3");
        latLongLabel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                JCheckBox cb = (JCheckBox) evt.getSource();
                if (cb.isSelected()) {// sets boolean variable to true or false
                    data.boxSkeleton = true;

                } else {
                    data.boxSkeleton = false;

                }
            }
        });

        //checkbox for the latitude and longitude labels
        latLongLabel = new JCheckBox("Lat/Long");
        latLongLabel.setIconTextGap(5);
        latLongLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        latLongLabel.setForeground(Color.BLACK);
        latLongLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        latLongLabel.setSelected(true);
        this.add(latLongLabel, "cell 1 3");
        latLongLabel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                JCheckBox cb = (JCheckBox) evt.getSource();
                if (cb.isSelected()) {// sets boolean variable to true or false
                    data.labelLatLong = true;

                } else {
                    data.labelLatLong = false;

                }
            }
        });

        // check box for the month labels
        timeLabel = new JCheckBox("Label Time");
        timeLabel.setIconTextGap(5);
        timeLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        timeLabel.setForeground(Color.BLACK);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        timeLabel.setSelected(true);
        this.add(timeLabel, "cell 0 4");
        timeLabel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                JCheckBox cb = (JCheckBox) evt.getSource();
                if (cb.isSelected()) {// sets boolean variable to true or false
                    data.labelTime = true;

                } else {
                    data.labelTime = false;

                }
            }
        });
    }
}
