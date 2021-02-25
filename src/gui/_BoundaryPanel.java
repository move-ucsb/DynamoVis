// /*

//   	DynamoVis Animation Tool
//     Copyright (C) 2016 Glenn Xavier
//     UPDATED: 2021 Mert Toka

//     This program is free software: you can redistribute it and/or modify
//     it under the terms of the GNU General Public License as published by
//     the Free Software Foundation, either version 3 of the License, or
//     (at your option) any later version.

//     This program is distributed in the hope that it will be useful,
//     but WITHOUT ANY WARRANTY; without even the implied warranty of
//     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//     GNU General Public License for more details.

//     You should have received a copy of the GNU General Public License
//     along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
// */

// package gui;

// import java.awt.Color;
// import java.awt.Dimension;
// import java.awt.Font;
// import java.awt.Insets;
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;

// import javax.swing.JButton;
// import javax.swing.JCheckBox;
// import javax.swing.JDialog;
// import javax.swing.JLabel;
// import javax.swing.JPanel;
// import javax.swing.SwingConstants;
// import javax.swing.UIManager;
// import javax.swing.event.ChangeEvent;
// import javax.swing.event.ChangeListener;

// import main.DesktopPane;
// import main.SketchData;
// import net.miginfocom.swing.MigLayout;

// public class BoundaryPanel extends JPanel {

// 	/**
// 	 * 
// 	 */
// 	private static final long serialVersionUID = 1L;

// 	DesktopPane parent;
// 	SketchData data;

// 	public WideComboBox boundaryColor;
// 	public WideComboBox strokeWeight;
// 	public WideComboBox colorRampList;
// 	JDialog geCtr;
// 	BoundaryPanel me;
// 	GradientEditor ge;

// 	public BoundaryPanel(DesktopPane father) {
// 		parent = father;
// 		data = parent.data;
// 		me = this;
// 		setLayout(new MigLayout("insets 0", "[grow][]", "[][][][][]"));

// 		JLabel lblBoundaryColor = new JLabel("Boundary Color");
// 		lblBoundaryColor.setForeground(Color.BLACK);
// 		lblBoundaryColor.setFont(new Font("Arial", Font.PLAIN, 8));
// 		lblBoundaryColor.setBackground(Color.LIGHT_GRAY);
// 		this.add(lblBoundaryColor, "cell 0 0,alignx left,aligny baseline");

// 		boundaryColor = new WideComboBox();
// 		boundaryColor.setMaximumSize(new Dimension(120, 32767));
// 		boundaryColor.setMaximumRowCount(100);
// 		boundaryColor.setForeground(Color.BLACK);
// 		boundaryColor.setBackground(UIManager.getColor("CheckBox.background"));
// 		boundaryColor.setFont(new Font("Arial", Font.PLAIN, 9));
// 		this.add(boundaryColor, "cell 0 1,growx");
// 		boundaryColor.addActionListener(new ActionListener() {
// 			public void actionPerformed(ActionEvent e) {
// 				WideComboBox cb = (WideComboBox) e.getSource();
// 				String item = (String) cb.getSelectedItem();
// 				data.boundaryColorSelection = parent.attributes.getName(item);
// 				String unit = parent.attributes.getUnit(data.boundaryColorSelection);
// 				if (unit.contains("none")) {
// 					unit = "";
// 				}
// 				data.selectedColorUnit = unit;
// 				if (data.fieldColors.get(data.boundaryColorSelection) != null) {
// 					// parent.colorPanel.colorRampList.setSelectedIndex(data.fieldColors.get(data.strokeColorSelection));
// 				}
// 			}
// 		});

// 		JCheckBox boundaryColorToggle = new JCheckBox("");
// 		boundaryColorToggle.setForeground(Color.BLACK);
// 		boundaryColorToggle.setFont(new Font("Arial", Font.PLAIN, 9));
// 		boundaryColorToggle.setBackground(Color.LIGHT_GRAY);
// 		this.add(boundaryColorToggle, "cell 1 1,growx");
// 		boundaryColorToggle.addChangeListener(new ChangeListener() {
// 			public void stateChanged(ChangeEvent evt) {
// 				JCheckBox cb = (JCheckBox) evt.getSource();
// 				if (cb.isSelected()) {
// 					data.boundaryColorToggle = true;
// 				} else {
// 					data.boundaryColorToggle = false;
// 				}
// 			}
// 		});
// 		boundaryColorToggle.setSelected(false);

// 		colorRampList = new WideComboBox(parent.colors.colorRampList.toArray());
// 		colorRampList.setMaximumSize(new Dimension(120, 32767));
// 		colorRampList.setMaximumRowCount(100);
// 		colorRampList.setForeground(Color.BLACK);
// 		colorRampList.setBackground(UIManager.getColor("CheckBox.background"));
// 		colorRampList.setFont(new Font("Arial", Font.PLAIN, 9));
// 		this.add(colorRampList, "cell 0 2,growx");
// 		colorRampList.addActionListener(new ActionListener() {
// 			public void actionPerformed(ActionEvent e) {
// 				WideComboBox comboBox = (WideComboBox) e.getSource();
// 				parent.data.selectedBoundarySwatch = (int) comboBox.getSelectedIndex();
// 			}
// 		});

// 		JButton editColor = new JButton("Edit");
// 		editColor.setIconTextGap(0);
// 		editColor.setHorizontalTextPosition(SwingConstants.LEFT);
// 		editColor.setForeground(Color.BLACK);
// 		editColor.setBackground(new Color(227, 227, 227));
// 		editColor.setFont(new Font("Arial", Font.PLAIN, 9));
// 		editColor.setMargin(new Insets(0, 0, 0, 0));
// 		this.add(editColor, "cell 1 2,grow");
// 		editColor.addActionListener(new ActionListener() {
// 			public void actionPerformed(ActionEvent evt) {
// 				if (geCtr == null) {
// 					geCtr = new JDialog(parent);
// 					geCtr.setTitle("Gradient Editor");
// 					geCtr.setLocationRelativeTo(parent);
// 					ge = new GradientEditor(parent, geCtr, me);
// 					geCtr.setContentPane(ge);
// 					geCtr.pack();
// 					geCtr.setMinimumSize(geCtr.getSize());
// 				} else {
// 					ge.reBuildList();
// 					ge.setSelected();
// 				}
// 				geCtr.setVisible(true);
// 			}
// 		});

// 	}
// }
