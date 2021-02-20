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

package gui;

import javax.swing.JPanel;

import main.DesktopPane;
import main.SketchData;
import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class LinePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	DesktopPane parent;
	SketchData data;

	public WideComboBox strokeColor;
	public WideComboBox strokeWeight;
	public WideComboBox colorRampList;
	JDialog geCtr;
	LinePanel me;
	GradientEditor ge;

	public LinePanel(DesktopPane father) {
		parent = father;
		data = parent.data;
		me = this;
		setLayout(new MigLayout("insets 0", "[grow][]", "[][][][][]"));

		JLabel lblLineColor = new JLabel("Line Color");
		lblLineColor.setForeground(Color.BLACK);
		lblLineColor.setFont(new Font("Arial", Font.PLAIN, 8));
		lblLineColor.setBackground(Color.LIGHT_GRAY);
		this.add(lblLineColor, "cell 0 0,alignx left,aligny baseline");

		strokeColor = new WideComboBox();
		strokeColor.setMaximumSize(new Dimension(120, 32767));
		strokeColor.setMaximumRowCount(100);
		strokeColor.setForeground(Color.BLACK);
		strokeColor.setBackground(UIManager.getColor("CheckBox.background"));
		strokeColor.setFont(new Font("Arial", Font.PLAIN, 9));
		this.add(strokeColor, "cell 0 1,growx");
		strokeColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WideComboBox cb = (WideComboBox) e.getSource();
				String item = (String) cb.getSelectedItem();
				data.strokeColorSelection = parent.attributes.getName(item);
				String unit = parent.attributes.getUnit(data.strokeColorSelection);
				if (unit.contains("none")) {
					unit = "";
				}
				data.selectedColorUnit = unit;
				if (data.fieldColors.get(data.strokeColorSelection) != null) {
					// parent.colorPanel.colorRampList.setSelectedIndex(data.fieldColors.get(data.strokeColorSelection));
				}
			}
		});

		JCheckBox strokeColorToggle = new JCheckBox("");
		strokeColorToggle.setForeground(Color.BLACK);
		strokeColorToggle.setFont(new Font("Arial", Font.PLAIN, 9));
		strokeColorToggle.setBackground(Color.LIGHT_GRAY);
		this.add(strokeColorToggle, "cell 1 1,growx");
		strokeColorToggle.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JCheckBox cb = (JCheckBox) evt.getSource();
				if (cb.isSelected()) {
					data.strokeColorToggle = true;
				} else {
					data.strokeColorToggle = false;
				}
			}
		});
		strokeColorToggle.setSelected(true);

		colorRampList = new WideComboBox(parent.colors.colorRampList.toArray());
		colorRampList.setMaximumSize(new Dimension(120, 32767));
		colorRampList.setMaximumRowCount(100);
		colorRampList.setForeground(Color.BLACK);
		colorRampList.setBackground(UIManager.getColor("CheckBox.background"));
		colorRampList.setFont(new Font("Arial", Font.PLAIN, 9));
		this.add(colorRampList, "cell 0 2,growx");
		colorRampList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WideComboBox comboBox = (WideComboBox) e.getSource();
				parent.data.selectedLineSwatch = (int) comboBox.getSelectedIndex();
			}
		});

		JButton editColor = new JButton("Edit");
		editColor.setIconTextGap(0);
		editColor.setHorizontalTextPosition(SwingConstants.LEFT);
		editColor.setForeground(Color.BLACK);
		editColor.setBackground(new Color(227, 227, 227));
		editColor.setFont(new Font("Arial", Font.PLAIN, 9));
		editColor.setMargin(new Insets(0, 0, 0, 0));
		this.add(editColor, "cell 1 2,grow");
		editColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (geCtr == null) {
					geCtr = new JDialog(parent);
					geCtr.setTitle("Gradient Editor");
					geCtr.setLocationRelativeTo(parent);
					ge = new GradientEditor(parent, geCtr, me);
					geCtr.setContentPane(ge);
					geCtr.pack();
					geCtr.setMinimumSize(geCtr.getSize());
				} else {
					ge.reBuildList();
					ge.setSelected();
				}
				geCtr.setVisible(true);
			}
		});

		JLabel lblLineWeight = new JLabel("Line Thickness");
		lblLineWeight.setForeground(Color.BLACK);
		lblLineWeight.setFont(new Font("Arial", Font.PLAIN, 8));
		lblLineWeight.setBackground(Color.LIGHT_GRAY);
		this.add(lblLineWeight, "cell 0 3,alignx left,aligny baseline");

		strokeWeight = new WideComboBox();
		strokeWeight.setMaximumSize(new Dimension(120, 32767));
		strokeWeight.setMaximumRowCount(100);
		strokeWeight.setForeground(Color.BLACK);
		strokeWeight.setBackground(UIManager.getColor("CheckBox.background"));
		strokeWeight.setFont(new Font("Arial", Font.PLAIN, 9));
		this.add(strokeWeight, "cell 0 4,growx");
		strokeWeight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WideComboBox cb = (WideComboBox) e.getSource();
				String item = (String) cb.getSelectedItem();
				data.strokeWeightSelection = parent.attributes.getName(item);
				if (data.strokeWeightSelection != null) {
					String unit = parent.attributes.getUnit(data.strokeWeightSelection);
					if (unit.contains("none")) {
						unit = "";
					}
					data.selectedStrokeWeightUnit = unit;
				}
			}
		});

		JCheckBox strokeWeightToggle = new JCheckBox("");
		strokeWeightToggle.setForeground(Color.BLACK);
		strokeWeightToggle.setBackground(Color.LIGHT_GRAY);
		strokeWeightToggle.setFont(new Font("Arial", Font.PLAIN, 9));
		this.add(strokeWeightToggle, "cell 1 4,growx");
		strokeWeightToggle.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JCheckBox cb = (JCheckBox) evt.getSource();
				if (cb.isSelected()) {
					data.strokeWeightToggle = true;
				} else {
					data.strokeWeightToggle = false;
				}
			}
		});
		strokeWeightToggle.setSelected(true);

		RangeSlider slider = new RangeSlider();
		slider.setValue(1);
		slider.setUpperValue(10);
		slider.setMinimum(0);
		slider.setMaximum(20);
		slider.setMaximumSize(new Dimension(120, 32767));
		this.add(slider, "cell 0 5 2 1,growx");
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				RangeSlider slider = (RangeSlider) evt.getSource();
				data.strokeWeightMin = slider.getValue();
				data.strokeWeightMax = slider.getUpperValue();
			}
		});

		ArrayList<String> fields = parent.attributes.getSelectedFieldNames();
		for (String string : fields) {
			strokeWeight.addItem(string);
			strokeColor.addItem(string);
		}

		strokeWeight.removeItem(parent.attributes.getIndexAlias());

		if (data.strokeWeightSelection == null) {
			strokeWeight.setEnabled(false);
			strokeWeightToggle.setSelected(false);
			strokeWeightToggle.setEnabled(false);
		}
	}
}
