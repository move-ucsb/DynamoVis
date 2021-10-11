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
    public JCheckBox rotateLabel;
    public JCheckBox bdyInteraction;
    public JCheckBox staticbox;

    public TimeBoxControlPanel(DesktopPane father) {
        parent = father;
        data = parent.data;

        setLayout(new MigLayout("insets 0", "[][][][][]", "[][][][]"));

        Object[] tags = new Object[parent.tagList.size()];// create array of all tags used
        for (int i = 0; i < parent.tagList.size(); i++) {
            tags[i] = parent.tagList.get(i);
        }

        data.timeBoxStartYear = data.startTime.getYear();
        data.timeBoxStartMonth = data.startTime.getMonthOfYear();
        data.timeBoxEndYear = data.endTime.getYear();
        data.timeBoxEndMonth = data.endTime.getMonthOfYear();

        // check box for the month labels
        timeLabel = new JCheckBox("Label Time");
        timeLabel.setIconTextGap(5);
        timeLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        timeLabel.setForeground(Color.BLACK);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        timeLabel.setMargin(new Insets(10, 5, 10, 10));
        this.add(timeLabel, "cell 0 0, gapleft 10");
        timeLabel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                JCheckBox cb = (JCheckBox) evt.getSource();
                if (cb.isSelected()) {// sets boolean variable to true or false
                    data.labelMonth = true;

                } else {
                    data.labelMonth = false;

                }
            }
        });

        // boundary alpha values
        JLabel label = new JLabel("Dim Edge Alpha (%)");
		label.setBackground(Color.LIGHT_GRAY);
		label.setForeground(Color.BLACK);
		label.setFont(new Font("Arial", Font.PLAIN, 9));
		this.add(label, "cell 1 0");
        JSpinner dimAlpha = new JSpinner();
		dimAlpha.setForeground(Color.BLACK);
		dimAlpha.setBackground(Color.LIGHT_GRAY);
		dimAlpha.setFont(new Font("Arial", Font.PLAIN, 9));
		dimAlpha.setModel(new SpinnerNumberModel(25, 0, 100, 1));
		dimAlpha.setToolTipText("Adjust the alpha value of boundary when it occludes the visualization.");
		this.add(dimAlpha, "cell 1 1");
		dimAlpha.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JSpinner spin = (JSpinner) evt.getSource();
				data.dimAlpha = (Integer)(spin.getValue()) * 2.55f;
			}
		});
        label = new JLabel("Normal Edge Alpha (%)");
		label.setBackground(Color.LIGHT_GRAY);
		label.setForeground(Color.BLACK);
		label.setFont(new Font("Arial", Font.PLAIN, 9));
		this.add(label, "cell 2 0");
        JSpinner normalAlpha = new JSpinner();
		normalAlpha.setForeground(Color.BLACK);
		normalAlpha.setBackground(Color.LIGHT_GRAY);
		normalAlpha.setFont(new Font("Arial", Font.PLAIN, 9));
		normalAlpha.setModel(new SpinnerNumberModel(75, 0, 100, 1));
		normalAlpha.setToolTipText("Adjust the alpha value of boundary when it occludes the visualization.");
		this.add(normalAlpha, "cell 2 1");
		normalAlpha.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JSpinner spin = (JSpinner) evt.getSource();
				data.normalAlpha = (Integer)(spin.getValue()) * 2.55f;
			}
		});

        // checkbox for idle rotation
        rotateLabel = new JCheckBox("Rotate view");
        rotateLabel.setIconTextGap(5);
        rotateLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        rotateLabel.setForeground(Color.BLACK);
        rotateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        rotateLabel.setMargin(new Insets(10, 5, 10, 10));
        this.add(rotateLabel, "cell 1 1, gapleft 10");
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
		
    }
}
