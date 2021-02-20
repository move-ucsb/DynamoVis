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
import utils.Field;
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
import java.util.Map.Entry;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class VectorPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	DesktopPane parent;
	SketchData data;

	public WideComboBox vectorLength;
	public WideComboBox headingField;
	public WideComboBox colorRampList;
	JCheckBox vectorCheck;	
	GradientEditor ge;
	JDialog geCtr;
	VectorPanel me;
	
	public VectorPanel(DesktopPane father){
		parent = father;
		data = parent.data;
		me = this;
		
		setLayout(new MigLayout("insets 0", "[grow][]", "[]"));
		
		JLabel lblDirection = new JLabel("Direction");
		lblDirection.setForeground(Color.BLACK);
		lblDirection.setFont(new Font("Arial", Font.PLAIN, 8));
		lblDirection.setBackground(Color.LIGHT_GRAY);
		this.add(lblDirection, "cell 0 0,alignx left,aligny baseline");	
		
		headingField = new WideComboBox();
		headingField.setMaximumSize(new Dimension(120, 32767));
		headingField.setMaximumRowCount(100);
		headingField.setForeground(Color.BLACK);
		headingField.setBackground(UIManager.getColor("CheckBox.background"));
		headingField.setFont(new Font("Arial", Font.PLAIN, 9));

		this.add(headingField, "cell 0 1,growx");

		headingField.addActionListener(new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		        WideComboBox cb = (WideComboBox)e.getSource();
		        String item = (String) cb.getSelectedItem();
		        data.headingFieldSelection = parent.attributes.getName(item);
		    }
		});
		
		vectorCheck = new JCheckBox("");
		vectorCheck.setForeground(Color.BLACK);
		vectorCheck.setBackground(Color.LIGHT_GRAY);
		vectorCheck.setFont(new Font("Arial", Font.PLAIN, 9));

		this.add(vectorCheck, "cell 1 1,growx");

		vectorCheck.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JCheckBox cb = (JCheckBox) evt.getSource();
				if (cb.isSelected()) {
					data.vectorToggle = true;
				} else {
					data.vectorToggle = false;
				}
			}
		});
		vectorCheck.setSelected(false);
	
		JLabel lblColor = new JLabel("Vector Color");
		lblColor.setForeground(Color.BLACK);
		lblColor.setFont(new Font("Arial", Font.PLAIN, 8));
		lblColor.setBackground(Color.LIGHT_GRAY);
		this.add(lblColor, "cell 0 2,alignx left,aligny baseline");	
		
		WideComboBox vectorColor = new WideComboBox();
		vectorColor.setMaximumSize(new Dimension(120, 32767));
		vectorColor.setMaximumRowCount(100);
		vectorColor.setForeground(Color.BLACK);
		vectorColor.setBackground(UIManager.getColor("CheckBox.background"));
		vectorColor.setFont(new Font("Arial", Font.PLAIN, 9));

		this.add(vectorColor, "cell 0 3,growx");		
		vectorColor.addActionListener(new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		        WideComboBox cb = (WideComboBox)e.getSource();
		        String item = (String) cb.getSelectedItem();
		        data.vectorColorSelection = parent.attributes.getName(item);
		    }
		});	
		
		JCheckBox vectorColorCheck = new JCheckBox("");
		vectorColorCheck.setForeground(Color.BLACK);
		vectorColorCheck.setBackground(Color.LIGHT_GRAY);
		vectorColorCheck.setFont(new Font("Arial", Font.PLAIN, 9));

		this.add(vectorColorCheck, "cell 1 3,growx");
		vectorColorCheck.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JCheckBox cb = (JCheckBox) evt.getSource();
				if (cb.isSelected()) {
					data.vectorColorToggle = true;
				} else {
					data.vectorColorToggle = false;
				}
			}
		});
		vectorColorCheck.setSelected(false);
		
		colorRampList = new WideComboBox(parent.colors.colorRampList.toArray());
		colorRampList.setMaximumSize(new Dimension(120, 32767));
		colorRampList.setMaximumRowCount(100);
		colorRampList.setForeground(Color.BLACK);
		colorRampList.setBackground(UIManager.getColor("CheckBox.background"));
		colorRampList.setFont(new Font("Arial", Font.PLAIN, 9));
		this.add(colorRampList, "cell 0 4,growx");		
		colorRampList.addActionListener(new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
	         WideComboBox comboBox = (WideComboBox) e.getSource();
	         parent.data.selectedVectorSwatch = (int) comboBox.getSelectedIndex();	         
		    }
		});		
		
		
		JButton editColor = new JButton("Edit");
		editColor.setIconTextGap(0);
		editColor.setHorizontalTextPosition(SwingConstants.LEFT);
		editColor.setForeground(Color.BLACK);
		editColor.setBackground(new Color(227,227,227));
		editColor.setFont(new Font("Arial", Font.PLAIN, 9));
		editColor.setMargin(new Insets(0, 0, 0, 0));
		this.add(editColor, "cell 1 4,grow");
		editColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (geCtr == null){
					geCtr = new JDialog(parent);
					geCtr.setTitle("Gradient Editor");
					geCtr.setLocationRelativeTo(parent);					
					ge = new GradientEditor(parent,geCtr,me);					
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
		
		JLabel lblLength = new JLabel("Vector Length");
		lblLength.setForeground(Color.BLACK);
		lblLength.setFont(new Font("Arial", Font.PLAIN, 8));
		lblLength.setBackground(Color.LIGHT_GRAY);
		this.add(lblLength, "cell 0 5,alignx left,aligny baseline");	
		
		vectorLength = new WideComboBox();
		vectorLength.setMaximumSize(new Dimension(120, 32767));
		vectorLength.setMaximumRowCount(100);
		vectorLength.setForeground(Color.BLACK);
		vectorLength.setBackground(UIManager.getColor("CheckBox.background"));
		vectorLength.setFont(new Font("Arial", Font.PLAIN, 9));

		this.add(vectorLength, "cell 0 6,growx");	

		
		vectorLength.addActionListener(new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		        WideComboBox cb = (WideComboBox)e.getSource();
		        String item = (String) cb.getSelectedItem();
		        data.vectorFieldSelection = parent.attributes.getName(item);		        
		    }
		});	
		
		JCheckBox vectorLengthCheck = new JCheckBox("");
		vectorLengthCheck.setForeground(Color.BLACK);
		vectorLengthCheck.setBackground(Color.LIGHT_GRAY);
		vectorLengthCheck.setFont(new Font("Arial", Font.PLAIN, 9));

		this.add(vectorLengthCheck, "cell 1 6,growx");
		vectorLengthCheck.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JCheckBox cb = (JCheckBox) evt.getSource();
				if (cb.isSelected()) {
					data.vectorLengthToggle = true;
				} else {
					data.vectorLengthToggle = false;
				}
			}
		});
		vectorLengthCheck.setSelected(false);
		
	

		RangeSlider slider = new RangeSlider();
		slider.setValue(5);
		slider.setUpperValue(20);
		slider.setMinimum(0);
		slider.setMaximum(50);
		slider.setMaximumSize(new Dimension(120, 32767));
		this.add(slider, "cell 0 7 2 1,growx");
		slider.addChangeListener(new ChangeListener() {
	        public void stateChanged(ChangeEvent evt) {
	        	RangeSlider slider = (RangeSlider)evt.getSource();
	        	data.vectorLengthMin = slider.getValue();
	        	data.vectorLengthMax = slider.getUpperValue();
	        }
	    });
		


		ArrayList<Field> fields = parent.attributes.getSelectedFields();
		for (Field field: fields){
			String name = field.getAlias();
			vectorColor.addItem(name);

			float min = field.getMin();
			float max = field.getMax();
			
			if (max - min >= 345f && max - min <= 375f){
				headingField.addItem(name);
			} else {
				vectorLength.addItem(name);
			}			
		}
		
		headingField.removeItem(parent.attributes.getIndexAlias());
		vectorLength.removeItem(parent.attributes.getIndexAlias());		
		
		
		
		if (headingField.getItemCount() == 0){
			vectorCheck.setEnabled(false);
			headingField.setEnabled(false);
			vectorLength.setEnabled(false);
			vectorLengthCheck.setEnabled(false);
			vectorColor.setEnabled(false);
			vectorColorCheck.setEnabled(false);
		}		

	}
}
