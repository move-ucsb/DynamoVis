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

// import main.DesktopPane;
// import main.SketchData;
// import gui.RangeSlider;

// import java.awt.Color;
// import java.awt.Component;
// import java.awt.Dimension;
// import java.awt.Font;
// import java.awt.Insets;
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;
// import java.awt.event.ItemEvent;
// //import java.awt.Color;
// //import java.util.Map.Entry;
// import java.awt.event.ItemListener;

// import javax.swing.JButton;
// import javax.swing.JCheckBox;
// //import javax.swing.JCheckBox;
// import javax.swing.JColorChooser;
// import javax.swing.JComponent;
// import javax.swing.JDialog;
// import javax.swing.JFileChooser;
// //import javax.swing.JFrame;
// import javax.swing.JLabel;
// import javax.swing.JPanel;
// import javax.swing.JSlider;
// import javax.swing.JSpinner;
// import javax.swing.JTextField;
// //import javax.swing.SpinnerDateModel;
// import javax.swing.SpinnerModel;
// import javax.swing.SpinnerNumberModel;
// //import javax.swing.SwingConstants;
// import javax.swing.UIManager;
// import javax.swing.event.ChangeEvent;
// import javax.swing.event.ChangeListener;
// //import javax.swing.text.DateFormatter;

// import org.joda.time.DateTime;

// //import de.fhpotsdam.unfolding.data.MarkerFactory;
// //import mb_animationTool.utils.Track;
// import net.miginfocom.swing.MigLayout;

// public class BoundaryVisualizationPanel extends JPanel {
// 	/**
// 	 * 
// 	 */

// 	public RangeSlider Testing_Bdy_Viz_Panel; // testing for adding boundary Visualization in the TimeLine

// 	private static final long serialVersionUID = 1L;
// 	static int openFrameCount = 0;
// 	static final int xOffset = 30, yOffset = 30;

// 	public JFileChooser dataChooser;
// 	private DesktopPane parent;
// 	private SketchData data;

// 	// public SpinnerModel edaySpinnerModel;
// 	public SpinnerModel Transparency_Spinner_Model;
// 	public SpinnerModel Stroke_Weight_Model;

// 	public WideComboBox boundaryColor;

// 	public BoundaryVisualizationPanel(DesktopPane father) {
// 		parent = father;
// 		data = parent.data;
// 		try {
// 			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
// 		} catch (Exception e) {
// 			e.printStackTrace();
// 		}

// 		// setLayout(new MigLayout("insets 0",
// 		// "[150px:150px:150px][50.00][150px:150px:150px,fill][][grow,fill][]",
// 		// "[][][][grow,fill][][grow,fill][][][]"));
// 		setLayout(new MigLayout("insets 0", "[][][]", "[][]"));

// 		// JCheckBox enable_check = new JCheckBox("Enable");
// 		// data.enable_check.setSelected(true);

// 		JLabel Bdy_Color = new JLabel("Area Color:");
// 		Bdy_Color.setForeground(Color.BLACK);
// 		Bdy_Color.setEnabled(false);
// 		Bdy_Color.setFont(new Font("Arial", Font.PLAIN, 12));
// 		Bdy_Color.setBackground(Color.LIGHT_GRAY);
// 		this.add(Bdy_Color, "cell 0 4, gapleft 10");

// 		final JButton Color_Button = new JButton("");
// 		add(Color_Button, "cell 0 4");
// 		Color_Button.setEnabled(false);
// 		Color_Button.setMinimumSize(new Dimension(15, 15));
// 		Color_Button.setMaximumSize(new Dimension(15, 15));
// 		Color_Button.setBackground(data.Bdy_Viz_Color);
// 		Color_Button.setMargin(new Insets(0, 0, 0, 0));
// 		Color_Button.addActionListener(new ActionListener() {
// 			public void actionPerformed(ActionEvent evt) {
// 				final JColorChooser colorChooser = new JColorChooser(new Color(data.Bdy_Viz_Color.getRed(),
// 						data.Bdy_Viz_Color.getGreen(), data.Bdy_Viz_Color.getBlue(), data.Bdy_Viz_Color.getAlpha()));
// 				JDialog maxDialog = JColorChooser.createDialog((Component) evt.getSource(), "Pick a Color", true, // modal
// 						colorChooser, new ActionListener() {

// 							@Override
// 							public void actionPerformed(ActionEvent e) {
// 								data.Bdy_Viz_Changes = true;
// 								data.Bdy_Viz_Color = new Color(colorChooser.getColor().getRed(),
// 										colorChooser.getColor().getGreen(), colorChooser.getColor().getBlue(),
// 										data.Bdy_Viz_Color.getAlpha());
// 								Color_Button.setBackground(data.Bdy_Viz_Color);
// 							}
// 						}, new ActionListener() {
// 							@Override
// 							public void actionPerformed(ActionEvent event) {
// 							}
// 						});
// 				maxDialog.setVisible(true);
// 			}
// 		});
// 		JLabel transparency = new JLabel("Transparency:");
// 		transparency.setBackground(Color.LIGHT_GRAY);
// 		transparency.setEnabled(false);
// 		transparency.setForeground(Color.BLACK);
// 		transparency.setFont(new Font("Arial", Font.PLAIN, 12));
// 		this.add(transparency, "cell 1 4 6 1");

// 		JTextField Transparency_TextField = new JTextField(3);
// 		Transparency_TextField.setText(Integer.toString(data.Bdy_Viz_Color.getAlpha()));
// 		Transparency_TextField.setEditable(false);
// 		Transparency_TextField.setEnabled(false);
// 		Transparency_TextField.setBackground(Color.white);
// 		JSlider Transparency_Slider = new JSlider(0, 255, 255);
// 		Transparency_Slider.setEnabled(false);
// 		Transparency_Slider.setMajorTickSpacing(85);
// 		Transparency_Slider.setMinorTickSpacing(10);
// 		Transparency_Slider.setPaintTrack(true);
// 		Transparency_Slider.setPaintTicks(true);
// 		Transparency_Slider.setPaintLabels(true);
// 		Transparency_Slider.setFont(new Font("Arial", Font.PLAIN, 8));
// 		Transparency_Slider.setToolTipText("Transparency");
// 		Transparency_Slider.setSnapToTicks(true);
// 		Transparency_Slider.setInverted(false);
// 		this.add(Transparency_Slider, "cell 1 4 6 1");
// 		this.add(Transparency_TextField, "cell 1 4 3 1");
// 		Transparency_Slider.addChangeListener(new ChangeListener() {
// 			public void stateChanged(ChangeEvent evt) {
// 				data.Bdy_Viz_Changes = true;
// 				data.Bdy_Viz_Color = new Color(data.Bdy_Viz_Color.getRed(), data.Bdy_Viz_Color.getGreen(),
// 						data.Bdy_Viz_Color.getBlue(), (int) Transparency_Slider.getValue());
// 				Transparency_TextField.setText(Integer.toString(data.Bdy_Viz_Color.getAlpha()));
// 			}
// 		});

// 		JLabel Bdy_Stroke_Color = new JLabel("Area Line Color:");
// 		Bdy_Stroke_Color.setForeground(Color.BLACK);
// 		Bdy_Stroke_Color.setEnabled(false);
// 		Bdy_Stroke_Color.setFont(new Font("Arial", Font.PLAIN, 12));
// 		Bdy_Stroke_Color.setBackground(Color.LIGHT_GRAY);
// 		this.add(Bdy_Stroke_Color, "cell 0 5, gapleft 10");

// 		final JButton Stroke_Color_Button = new JButton("");
// 		add(Stroke_Color_Button, "cell 0 5");
// 		Stroke_Color_Button.setEnabled(false);
// 		Stroke_Color_Button.setMinimumSize(new Dimension(15, 15));
// 		Stroke_Color_Button.setMaximumSize(new Dimension(15, 15));
// 		Stroke_Color_Button.setBackground(data.Bdy_Viz_Stroke_Color);
// 		Stroke_Color_Button.setMargin(new Insets(0, 0, 0, 0));
// 		Stroke_Color_Button.addActionListener(new ActionListener() {
// 			public void actionPerformed(ActionEvent evt) {
// 				final JColorChooser Stroke_Color_Chooser = new JColorChooser(
// 						new Color(data.Bdy_Viz_Stroke_Color.getRed(), data.Bdy_Viz_Stroke_Color.getGreen(),
// 								data.Bdy_Viz_Stroke_Color.getBlue(), data.Bdy_Viz_Stroke_Color.getAlpha()));
// 				JDialog Stroke_maxDialog = JColorChooser.createDialog((Component) evt.getSource(), "Pick a Color", true, // modal
// 						Stroke_Color_Chooser, new ActionListener() {

// 							@Override
// 							public void actionPerformed(ActionEvent e) {
// 								data.Bdy_Viz_Changes = true;
// 								data.Bdy_Viz_Stroke_Color = new Color(Stroke_Color_Chooser.getColor().getRed(),
// 										Stroke_Color_Chooser.getColor().getGreen(),
// 										Stroke_Color_Chooser.getColor().getBlue(),
// 										data.Bdy_Viz_Stroke_Color.getAlpha());
// 								Stroke_Color_Button.setBackground(data.Bdy_Viz_Stroke_Color);
// 							}
// 						}, new ActionListener() {
// 							@Override
// 							public void actionPerformed(ActionEvent event) {
// 							}
// 						});
// 				Stroke_maxDialog.setVisible(true);
// 			}
// 		});
// 		JLabel Stroke_Transparency = new JLabel("Transparency:");
// 		Stroke_Transparency.setBackground(Color.LIGHT_GRAY);
// 		Stroke_Transparency.setEnabled(false);
// 		Stroke_Transparency.setForeground(Color.BLACK);
// 		Stroke_Transparency.setFont(new Font("Arial", Font.PLAIN, 12));
// 		this.add(Stroke_Transparency, "cell 1 5 6 1");

// 		JTextField Stroke_Transparency_TextField = new JTextField(3);
// 		Stroke_Transparency_TextField.setText(Integer.toString(data.Bdy_Viz_Stroke_Color.getAlpha()));
// 		Stroke_Transparency_TextField.setEditable(false);
// 		Stroke_Transparency_TextField.setEnabled(false);
// 		Stroke_Transparency_TextField.setBackground(Color.white);
// 		JSlider Stroke_Transparency_Slider = new JSlider(0, 255, 255);
// 		Stroke_Transparency_Slider.setEnabled(false);
// 		Stroke_Transparency_Slider.setMajorTickSpacing(85);
// 		Stroke_Transparency_Slider.setMinorTickSpacing(10);
// 		Stroke_Transparency_Slider.setPaintTrack(true);
// 		Stroke_Transparency_Slider.setPaintTicks(true);
// 		Stroke_Transparency_Slider.setPaintLabels(true);
// 		Stroke_Transparency_Slider.setFont(new Font("Arial", Font.PLAIN, 8));
// 		Stroke_Transparency_Slider.setToolTipText("Transparency");
// 		Stroke_Transparency_Slider.setSnapToTicks(true);
// 		Stroke_Transparency_Slider.setInverted(false);
// 		this.add(Stroke_Transparency_Slider, "cell 1 5 6 1");
// 		this.add(Stroke_Transparency_TextField, "cell 1 5 3 1");
// 		Stroke_Transparency_Slider.addChangeListener(new ChangeListener() {
// 			public void stateChanged(ChangeEvent evt) {
// 				data.Bdy_Viz_Changes = true;
// 				data.Bdy_Viz_Stroke_Color = new Color(data.Bdy_Viz_Stroke_Color.getRed(),
// 						data.Bdy_Viz_Stroke_Color.getGreen(), data.Bdy_Viz_Stroke_Color.getBlue(),
// 						(int) Stroke_Transparency_Slider.getValue());
// 				Stroke_Transparency_TextField.setText(Integer.toString(data.Bdy_Viz_Stroke_Color.getAlpha()));
// 			}
// 		});

// 		JLabel Bdy_Stroke_Weight = new JLabel("Area Line Weight:");
// 		Bdy_Stroke_Weight.setEnabled(false);
// 		Bdy_Stroke_Weight.setForeground(Color.BLACK);
// 		Bdy_Stroke_Weight.setFont(new Font("Arial", Font.PLAIN, 12));
// 		Bdy_Stroke_Weight.setBackground(Color.LIGHT_GRAY);
// 		this.add(Bdy_Stroke_Weight, "cell 0 6, gapleft 10");

// 		Stroke_Weight_Model = new SpinnerNumberModel(data.Bdy_Viz_Stroke_Weight, // initial value
// 				0, // min
// 				100, // max
// 				1);// step

// 		JSpinner Stroke_Weight_Spinner = new JSpinner(Stroke_Weight_Model);
// 		data.Bdy_Viz_Stroke_Weight = (int) Stroke_Weight_Spinner.getValue();
// 		JComponent Bdy_Stroke_Weight_editor = new JSpinner.NumberEditor(Stroke_Weight_Spinner, "###");
// 		Stroke_Weight_Spinner.setEnabled(false);
// 		Stroke_Weight_Spinner.setEditor(Bdy_Stroke_Weight_editor);
// 		this.add(Stroke_Weight_Spinner, "cell 0 6");
// 		Stroke_Weight_Spinner.addChangeListener(new ChangeListener() {
// 			public void stateChanged(ChangeEvent e) {
// 				data.Bdy_Viz_Changes = true;
// 				data.Bdy_Viz_Stroke_Weight = (int) Stroke_Weight_Spinner.getValue();
// 				// System.out.println("The selected StartYear in the TimeBar.java is:" +
// 				// data.boundaryStartYear);

// 			}
// 		});

// 		this.add(data.enable_check, "cell 0 0, gapleft 10");
// 		data.enable_check.addItemListener(new ItemListener() {

// 			@Override
// 			public void itemStateChanged(ItemEvent arg0) {
// 				if (!data.enable_check.isSelected()) {
// 					Color_Button.setEnabled(false);
// 					transparency.setEnabled(false);
// 					Transparency_Slider.setEnabled(false);
// 					Stroke_Color_Button.setEnabled(false);
// 					Stroke_Transparency.setEnabled(false);
// 					Bdy_Stroke_Color.setEnabled(false);
// 					Transparency_TextField.setEnabled(false);
// 					Bdy_Color.setEnabled(false);
// 					Stroke_Transparency_TextField.setEnabled(false);
// 					Stroke_Transparency_Slider.setEnabled(false);
// 					Bdy_Stroke_Weight.setEnabled(false);
// 					Stroke_Weight_Spinner.setEnabled(false);
// 					data.Bdy_Viz_Panel_Close = true;
// 					data.Bdy_Viz_Enable = false;

// 				} else {
// 					Color_Button.setEnabled(true);
// 					transparency.setEnabled(true);
// 					Transparency_Slider.setEnabled(true);
// 					Stroke_Color_Button.setEnabled(true);
// 					Bdy_Stroke_Color.setEnabled(true);
// 					Stroke_Transparency.setEnabled(true);
// 					Transparency_TextField.setEnabled(true);
// 					Bdy_Color.setEnabled(true);
// 					Stroke_Transparency_TextField.setEnabled(true);
// 					Stroke_Transparency_Slider.setEnabled(true);
// 					Bdy_Stroke_Weight.setEnabled(true);
// 					Stroke_Weight_Spinner.setEnabled(true);
// 					data.Bdy_Viz_Panel_Close = false;
// 					data.Bdy_Viz_Enable = true;
// 				}

// 			}

// 		});

// 		// int start_time =calucate_time(data.startTime);
// 		// int end_time =calucate_time(data.endTime);
// 		// RangeSlider Testing_Bdy_Viz_Panel = new RangeSlider(start_time,end_time);
// 		// Testing_Bdy_Viz_Panel.setValue(start_time);
// 		// Testing_Bdy_Viz_Panel.setUpperValue(end_time);
// 		// Testing_Bdy_Viz_Panel.setMajorTickSpacing(2000);
// 		//// Testing_Bdy_Viz_Panel.setMinorTickSpacing(1000);
// 		// Testing_Bdy_Viz_Panel.setPaintLabels(true);
// 		// Testing_Bdy_Viz_Panel.setPaintTrack(true);
// 		// Testing_Bdy_Viz_Panel.setPaintTicks(true);
// 		// this.add(Testing_Bdy_Viz_Panel, " cell 0 6, gapleft 10");
// 		// Testing_Bdy_Viz_Panel.addChangeListener(new ChangeListener() {
// 		// public void stateChanged(ChangeEvent e) {
// 		// data.Bdy_Viz_Changes = true;
// 		// data.boundaryStartYear = (int)Testing_Bdy_Viz_Panel.getValue();
// 		// data.boundaryEndYear = (int)Testing_Bdy_Viz_Panel.getUpperValue();
// 		// }
// 		// });

// 		// RangeSlider Testing2_Bdy_Viz_Panel = new RangeSlider(1,12);
// 		// Testing2_Bdy_Viz_Panel.setValue(data.startTime.getMonthOfYear());
// 		// Testing2_Bdy_Viz_Panel.setUpperValue(data.endTime.getMonthOfYear());
// 		// Testing2_Bdy_Viz_Panel.setMajorTickSpacing(1);
// 		// Testing2_Bdy_Viz_Panel.setMinorTickSpacing(1);
// 		// Testing2_Bdy_Viz_Panel.setPaintLabels(true);
// 		// Testing2_Bdy_Viz_Panel.setPaintTrack(true);
// 		// Testing2_Bdy_Viz_Panel.setPaintTicks(true);
// 		// this.add(Testing2_Bdy_Viz_Panel, " cell 0 7, gapleft 10");
// 		// Testing2_Bdy_Viz_Panel.addChangeListener(new ChangeListener() {
// 		// public void stateChanged(ChangeEvent e) {
// 		// data.Bdy_Viz_Changes = true;
// 		// data.boundaryStartMonth = (int)Testing2_Bdy_Viz_Panel.getValue();
// 		// data.boundaryEndMonth = (int)Testing2_Bdy_Viz_Panel.getUpperValue();
// 		// }
// 		// });

// 		// JLabel sdayLbl = new JLabel("Start Date:");
// 		// sdayLbl.setBackground(Color.LIGHT_GRAY);
// 		// sdayLbl.setForeground(Color.BLACK);
// 		// sdayLbl.setFont(new Font("Arial", Font.PLAIN, 12));
// 		// this.add(sdayLbl, "cell 0 4");
// 		//
// 		// sdaySpinnerModel = new SpinnerNumberModel(data.startTime.getDayOfMonth(),
// 		// //initial value
// 		// 1, //min
// 		// 31, //max
// 		// 1);//step
// 		// JSpinner sdayspinner = new JSpinner(sdaySpinnerModel);
// 		// this.add(sdayspinner, "cell 0 4");
// 		// sdayspinner.addChangeListener(new ChangeListener() {
// 		// public void stateChanged(ChangeEvent e) {
// 		// data.boundaryStartDate= (int)sdayspinner.getValue();
// 		// }
// 		// });
// 		//
// 		// JLabel edayLbl = new JLabel("End Date:");
// 		// edayLbl.setBackground(Color.LIGHT_GRAY);
// 		// edayLbl.setForeground(Color.BLACK);
// 		// edayLbl.setFont(new Font("Arial", Font.PLAIN, 12));
// 		// this.add(edayLbl, "cell 5 4");
// 		//
// 		// edaySpinnerModel = new SpinnerNumberModel(data.startTime.getDayOfMonth(),
// 		// //initial value
// 		// 1, //min
// 		// 31, //max
// 		// 1);//step
// 		// JSpinner edayspinner = new JSpinner(edaySpinnerModel);
// 		// this.add(edayspinner, "cell 5 4");
// 		// edayspinner.addChangeListener(new ChangeListener() {
// 		// public void stateChanged(ChangeEvent e) {
// 		// data.boundaryEndDate= (int)edayspinner.getValue();
// 		// }
// 		// });

// 		// JButton generateBoundary = new JButton("Generate Boundary");//change color if
// 		// displaying or not?
// 		// generateBoundary.setIconTextGap(5);
// 		// generateBoundary.setHorizontalTextPosition(SwingConstants.LEFT);
// 		// generateBoundary.setForeground(Color.BLACK);
// 		// generateBoundary.setBackground(new Color(227,227,227));
// 		// generateBoundary.setFont(new Font("Arial", Font.PLAIN, 12));
// 		// generateBoundary.setMargin(new Insets(5, 5, 5, 5));
// 		// this.add(generateBoundary, "cell 0 6");
// 		// generateBoundary.addActionListener(new ActionListener() {
// 		// public void actionPerformed(ActionEvent evt) {
// 		// //create array of booleans that tell true or false to if animals are
// 		// interacting for entire length of time
// 		// data.drawBoundary = true;
// 		//
// 		// }
// 		// });
// 		//
// 	}

// 	// public int calucate_time(DateTime time)
// 	// {
// 	// int month = time.getMonthOfYear();
// 	// String temp_month;
// 	//
// 	// if(month >= 10)
// 	// {
// 	// temp_month = Integer.toString(month);
// 	// }
// 	// else
// 	// {
// 	// temp_month = String.format("%02d", time.getMonthOfYear());
// 	// }
// 	// String year = Integer.toString(time.getYear());
// 	// return Integer.parseInt(year+temp_month);
// 	//
// 	// }
// }