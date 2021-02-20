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

public class PointPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	DesktopPane parent;
	SketchData data;

	public WideComboBox pointColor;
	public WideComboBox pointSize;
	public WideComboBox colorRampList;
	GradientEditor ge;
	JDialog geCtr;
	PointPanel me;

	public PointPanel(DesktopPane father) {
		parent = father;
		data = parent.data;
		me = this;

		setLayout(new MigLayout("insets 0", "[grow][]", "[][]"));

		JLabel lblPointColor = new JLabel("Point Color");
		lblPointColor.setForeground(Color.BLACK);
		lblPointColor.setFont(new Font("Arial", Font.PLAIN, 8));
		lblPointColor.setBackground(Color.LIGHT_GRAY);
		this.add(lblPointColor, "cell 0 0,alignx left,aligny baseline");

		pointColor = new WideComboBox();
		pointColor.setMaximumSize(new Dimension(120, 32767));
		pointColor.setMaximumRowCount(100);
		pointColor.setForeground(Color.BLACK);
		pointColor.setBackground(UIManager.getColor("CheckBox.background"));
		pointColor.setFont(new Font("Arial", Font.PLAIN, 9));
		this.add(pointColor, "cell 0 1,growx");
		pointColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WideComboBox cb = (WideComboBox) e.getSource();
				String item = (String) cb.getSelectedItem();
				data.pointColorSelection = parent.attributes.getName(item);
			}
		});

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
				parent.data.selectedPointSwatch = (int) comboBox.getSelectedIndex();
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

		JLabel lblPointSize = new JLabel("Point Size");
		lblPointSize.setForeground(Color.BLACK);
		lblPointSize.setFont(new Font("Arial", Font.PLAIN, 8));
		lblPointSize.setBackground(Color.LIGHT_GRAY);
		this.add(lblPointSize, "cell 0 3,alignx left,aligny baseline");

		pointSize = new WideComboBox();
		pointSize.setMaximumSize(new Dimension(120, 32767));
		pointSize.setMaximumRowCount(100);
		pointSize.setForeground(Color.BLACK);
		pointSize.setBackground(UIManager.getColor("CheckBox.background"));
		pointSize.setFont(new Font("Arial", Font.PLAIN, 9));
		this.add(pointSize, "cell 0 4,growx");
		pointSize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WideComboBox cb = (WideComboBox) e.getSource();
				String item = (String) cb.getSelectedItem();
				data.pointSizeSelection = parent.attributes.getName(item);
			}
		});

		JCheckBox pointColorToggle = new JCheckBox("");
		pointColorToggle.setForeground(Color.BLACK);
		pointColorToggle.setBackground(Color.LIGHT_GRAY);
		pointColorToggle.setFont(new Font("Arial", Font.PLAIN, 9));
		this.add(pointColorToggle, "cell 1 1,growx");
		pointColorToggle.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JCheckBox cb = (JCheckBox) evt.getSource();
				if (cb.isSelected()) {
					data.pointColorToggle = true;
				} else {
					data.pointColorToggle = false;
				}
			}
		});
		pointColorToggle.setSelected(false);

		JCheckBox pointSizeToggle = new JCheckBox("");
		pointSizeToggle.setForeground(Color.BLACK);
		pointSizeToggle.setBackground(Color.LIGHT_GRAY);
		pointSizeToggle.setFont(new Font("Arial", Font.PLAIN, 9));
		this.add(pointSizeToggle, "cell 1 4,growx");
		pointSizeToggle.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JCheckBox cb = (JCheckBox) evt.getSource();
				if (cb.isSelected()) {
					data.pointSizeToggle = true;
				} else {
					data.pointSizeToggle = false;
				}
			}
		});
		pointSizeToggle.setSelected(false);

		RangeSlider slider = new RangeSlider();
		slider.setValue(1);
		slider.setUpperValue(10);
		slider.setMinimum(0);
		slider.setMaximum(30);
		slider.setMaximumSize(new Dimension(120, 32767));
		this.add(slider, "cell 0 5 2 1,growx");
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				RangeSlider slider = (RangeSlider) evt.getSource();
				data.pointSizeMin = slider.getValue();
				data.pointSizeMax = slider.getUpperValue();
			}
		});

		ArrayList<String> fields = parent.attributes.getSelectedFieldNames();
		for (String s : fields) {
			pointColor.addItem(s);
			pointSize.addItem(s);
		}
		pointSize.removeItem(parent.attributes.getIndexAlias());

		if (data.pointSizeSelection == null) {
			pointSize.setEnabled(false);
			pointSizeToggle.setSelected(false);
			pointSizeToggle.setEnabled(false);
		}

	}
}
