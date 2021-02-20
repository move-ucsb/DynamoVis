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
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
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

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GhostPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	DesktopPane parent;
	SketchData data;

	GhostPanel me;
	
	JComboBox size;
	
	public GhostPanel(DesktopPane father){
		parent = father;
		data = parent.data;
		me = this;		
		setLayout(new MigLayout("insets 0", "[grow][grow][][]", "[][]"));
		
		
		JSpinner ghostWeight = new JSpinner();
		ghostWeight.setForeground(Color.BLACK);
		ghostWeight.setBackground(Color.LIGHT_GRAY);
		ghostWeight.setFont(new Font("Arial", Font.PLAIN, 9));		 
		ghostWeight.setModel(new SpinnerNumberModel(1, 1, 100, 1));
		ghostWeight.setToolTipText("");
		ToolTipManager.sharedInstance().registerComponent(ghostWeight);
		ghostWeight.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JSpinner spin = (JSpinner)evt.getSource();	   
				data.ghostWeight = (Integer) spin.getValue();
			}
		});
		
				JLabel lblLineColor = new JLabel("Weight");
				lblLineColor.setForeground(Color.BLACK);
				lblLineColor.setFont(new Font("Arial", Font.PLAIN, 8));
				lblLineColor.setBackground(Color.LIGHT_GRAY);
				this.add(lblLineColor, "cell 0 0,alignx left,aligny baseline");
		
		JLabel lblOpac = new JLabel("Opacity");
		lblOpac.setForeground(Color.BLACK);
		lblOpac.setFont(new Font("Arial", Font.PLAIN, 8));
		lblOpac.setBackground(Color.LIGHT_GRAY);
		this.add(lblOpac, "cell 1 0,alignx left,aligny baseline");
		ghostWeight.setValue(2);		
		this.add(ghostWeight,"flowx,cell 0 1");	
		
		JCheckBox check = new JCheckBox("");
		check.setForeground(Color.BLACK);
		check.setBackground(Color.LIGHT_GRAY);
		check.setFont(new Font("Arial", Font.PLAIN, 9));
		check.setToolTipText("");
		ToolTipManager.sharedInstance().registerComponent(check);
		this.add(check, "cell 3 1,growx");
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
		add(colorButton, "cell 2 1,grow");
		colorButton.setMinimumSize(new Dimension(15,15));
		colorButton.setMaximumSize(new Dimension(15,15));
		colorButton.setBackground(data.ghostColor);
		colorButton.setMargin(new Insets(0, 0, 0, 0));
		colorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				final JColorChooser colorChooser = new JColorChooser();
				JDialog maxDialog = JColorChooser.createDialog((Component) evt.getSource(),
			                                        "Pick a Color",
			                                        true,  //modal
			                                        colorChooser,
			                                        new ActionListener()
			                                        {													

														@Override
														public void actionPerformed(
																ActionEvent e) {															
															data.ghostColor = colorChooser.getColor();
															colorButton.setBackground(data.ghostColor);															
														}					
			                                        }, new ActionListener()
			                                        {
			                                            @Override
			                                            public void actionPerformed(ActionEvent event)
			                                            { 
			                                            }
			                                        }
			                                       );
				maxDialog.setVisible(true);
			}
		});
						
						JSpinner AlphaValue = new JSpinner();
						AlphaValue.setForeground(Color.BLACK);
						AlphaValue.setBackground(Color.LIGHT_GRAY);
						AlphaValue.setFont(new Font("Arial", Font.PLAIN, 9));		 
						AlphaValue.setModel(new SpinnerNumberModel(1, 1, 255, 1));
						AlphaValue.setToolTipText("");
						ToolTipManager.sharedInstance().registerComponent(AlphaValue);
						AlphaValue.addChangeListener(new ChangeListener() {
							public void stateChanged(ChangeEvent evt) {
								JSpinner spin = (JSpinner)evt.getSource();	   
								data.ghostAlpha = (Integer) spin.getValue();
							}
						});
						AlphaValue.setValue(15);
						this.add(AlphaValue,"flowx,cell 1 1");
		
	}
}
