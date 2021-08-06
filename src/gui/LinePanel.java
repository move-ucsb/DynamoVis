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

import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import main.DesktopPane;
import main.SketchData;
import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import java.awt.Color;
import java.awt.Component;
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
		setLayout(new MigLayout("insets 0", "[grow][grow][][]", "[][][][][][][][][][]"));

		JLabel lblLineColor = new JLabel("Line Color");
		lblLineColor.setForeground(Color.BLACK);
		lblLineColor.setFont(new Font("Arial", Font.PLAIN, 8));
		lblLineColor.setBackground(Color.LIGHT_GRAY);
		this.add(lblLineColor, "cell 0 0,alignx left,aligny baseline");

		// 2 - color ramp list
		colorRampList = new WideComboBox(parent.colors.colorRampList.toArray());
		colorRampList.setMaximumSize(new Dimension(120, 32767));
		colorRampList.setMaximumRowCount(100);
		colorRampList.setForeground(Color.BLACK);
		colorRampList.setBackground(UIManager.getColor("CheckBox.background"));
		colorRampList.setFont(new Font("Arial", Font.PLAIN, 9));
		this.add(colorRampList, "cell 0 2 3 1,growx");
		colorRampList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WideComboBox comboBox = (WideComboBox) e.getSource();
				parent.data.selectedLineSwatch = (int) comboBox.getSelectedIndex();
			}
		});

		// 1 - line color field name
		strokeColor = new WideComboBox();
		strokeColor.setMaximumSize(new Dimension(120, 32767));
		strokeColor.setMaximumRowCount(100);
		strokeColor.setForeground(Color.BLACK);
		strokeColor.setBackground(UIManager.getColor("CheckBox.background"));
		strokeColor.setFont(new Font("Arial", Font.PLAIN, 9));
		this.add(strokeColor, "cell 0 1 3 1,growx");
		strokeColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WideComboBox cb = (WideComboBox) e.getSource();
				String item = (String) cb.getSelectedItem();

				if(item.equals("Tag")) colorRampList.setEnabled(false);
				else				   colorRampList.setEnabled(true);

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
		this.add(strokeColorToggle, "cell 3 1,growx");
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

		

		JButton editColor = new JButton("Edit");
		editColor.setIconTextGap(0);
		editColor.setHorizontalTextPosition(SwingConstants.LEFT);
		editColor.setForeground(Color.BLACK);
		editColor.setBackground(new Color(227, 227, 227));
		editColor.setFont(new Font("Arial", Font.PLAIN, 9));
		editColor.setMargin(new Insets(0, 0, 0, 0));
		this.add(editColor, "cell 3 2,grow");
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

		JLabel lblLineWeight = new JLabel("Line Width");
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
		this.add(strokeWeight, "cell 0 4 3 1,growx");
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
		this.add(strokeWeightToggle, "cell 3 4,growx");
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
		this.add(slider, "cell 0 5 3 1,growx");
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


		// UNDERLAY
		// ===================

		JSpinner ghostWeight = new JSpinner();
		ghostWeight.setForeground(Color.BLACK);
		ghostWeight.setBackground(Color.LIGHT_GRAY);
		ghostWeight.setFont(new Font("Arial", Font.PLAIN, 9));
		ghostWeight.setModel(new SpinnerNumberModel(1, 1, 100, 1));
		ghostWeight.setToolTipText("");
		ToolTipManager.sharedInstance().registerComponent(ghostWeight);
		ghostWeight.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JSpinner spin = (JSpinner) evt.getSource();
				data.ghostWeight = (Integer) spin.getValue();
			}
		});

		// Underlay label
		JLabel lblUnderlaySubheader = new JLabel("Underlay");
		lblUnderlaySubheader.setForeground(Color.BLACK);
		lblUnderlaySubheader.setFont(new Font("Arial", Font.BOLD, 9));
		lblUnderlaySubheader.setBackground(Color.LIGHT_GRAY);
		this.add(lblUnderlaySubheader, "cell 0 7,alignx left,aligny baseline");
		// 

		JLabel lblLineThickness = new JLabel("Line Width");
		lblLineThickness.setForeground(Color.BLACK);
		lblLineThickness.setFont(new Font("Arial", Font.PLAIN, 8));
		lblLineThickness.setBackground(Color.LIGHT_GRAY);
		this.add(lblLineThickness, "cell 0 8,alignx left,aligny baseline");

		JLabel lblOpac = new JLabel("Opacity (%)");
		lblOpac.setForeground(Color.BLACK);
		lblOpac.setFont(new Font("Arial", Font.PLAIN, 8));
		lblOpac.setBackground(Color.LIGHT_GRAY);
		this.add(lblOpac, "cell 1 8,alignx left,aligny baseline");
		ghostWeight.setValue(2);
		this.add(ghostWeight, "flowx,cell 0 9");

		JCheckBox check = new JCheckBox("");
		check.setForeground(Color.BLACK);
		check.setBackground(Color.LIGHT_GRAY);
		check.setFont(new Font("Arial", Font.PLAIN, 9));
		check.setToolTipText("");
		ToolTipManager.sharedInstance().registerComponent(check);
		this.add(check, "cell 3 9,growx");
		check.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JCheckBox cb = (JCheckBox) evt.getSource();
				if (cb.isSelected()) {
					data.ghost = true;
				} else {
					data.ghost = false;
				}
			}
		});

		final JButton colorButton = new JButton("");
		add(colorButton, "cell 2 9,grow");
		colorButton.setMinimumSize(new Dimension(15, 15));
		colorButton.setMaximumSize(new Dimension(15, 15));
		colorButton.setBackground(data.ghostColor);
		colorButton.setMargin(new Insets(0, 0, 0, 0));
		colorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				final JColorChooser colorChooser = new JColorChooser();
				JDialog maxDialog = JColorChooser.createDialog((Component) evt.getSource(), "Pick a Color", true, // modal
						colorChooser, new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								data.ghostColor = colorChooser.getColor();
								colorButton.setBackground(data.ghostColor);
							}
						}, new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent event) {
							}
						});
				maxDialog.setVisible(true);
			}
		});

		JSpinner AlphaValue = new JSpinner();
		AlphaValue.setForeground(Color.BLACK);
		AlphaValue.setBackground(Color.LIGHT_GRAY);
		AlphaValue.setFont(new Font("Arial", Font.PLAIN, 9));
		AlphaValue.setModel(new SpinnerNumberModel(1, 1, 100, 1)); 
		AlphaValue.setToolTipText("");
		ToolTipManager.sharedInstance().registerComponent(AlphaValue);
		AlphaValue.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JSpinner spin = (JSpinner) evt.getSource();
				data.ghostAlpha = (Integer) spin.getValue() * 255 / 100; // scale back to [0-255]
			}
		});
		AlphaValue.setValue(15);
		this.add(AlphaValue, "flowx,cell 1 9");
	}
}
