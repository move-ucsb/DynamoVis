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
import javax.swing.JPanel;
import javax.swing.SpinnerModel;
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
    public JCheckBox label;
    public JCheckBox bdyInteraction;
    public JCheckBox staticbox;

    public TimeBoxControlPanel(DesktopPane father) {
        parent = father;
        data = parent.data;

        setLayout(new MigLayout("insets 0", "[][][]", "[][]"));

        Object[] tags = new Object[parent.tagList.size()];// create array of all tags used
        for (int i = 0; i < parent.tagList.size(); i++) {
            tags[i] = parent.tagList.get(i);
        }

        data.timeBoxStartYear = data.startTime.getYear();
        data.timeBoxStartMonth = data.startTime.getMonthOfYear();
        data.timeBoxEndYear = data.endTime.getYear();
        data.timeBoxEndMonth = data.endTime.getMonthOfYear();

        // check box for the month labels
        label = new JCheckBox("Label Months");
        label.setIconTextGap(5);
        label.setHorizontalTextPosition(SwingConstants.RIGHT);
        label.setForeground(Color.BLACK);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setMargin(new Insets(10, 5, 10, 10));
        this.add(label, "cell 0 1, gapleft 10");
        label.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                JCheckBox cb = (JCheckBox) evt.getSource();
                if (cb.isSelected()) {// sets boolean variable to true or false
                    data.labelMonth = true;

                } else {
                    data.labelMonth = false;

                }
            }
        });
    }
}
