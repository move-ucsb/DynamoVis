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

// import main.Colors;
// import main.DesktopPane;
// import main.Sketch;
// import main.SketchData;

// import java.awt.Color;
// import java.awt.Dimension;
// //import java.awt.Dimension;
// import java.awt.Font;
// import java.awt.Insets;
// import java.awt.Window;
// import java.awt.event.ComponentAdapter;
// import java.awt.event.ComponentEvent;
// import java.awt.event.WindowAdapter;
// import java.awt.event.WindowEvent;
// import java.lang.reflect.Method;

// //import javax.swing.JButton;
// import javax.swing.JCheckBox;
// import javax.swing.JComponent;
// import javax.swing.JFileChooser;
// import javax.swing.JFrame;
// import javax.swing.JLabel;
// import javax.swing.JPanel;
// import javax.swing.JSpinner;
// import javax.swing.SpinnerModel;
// import javax.swing.SpinnerNumberModel;
// import javax.swing.SwingConstants;
// import javax.swing.UIManager;
// import javax.swing.event.ChangeEvent;
// import javax.swing.event.ChangeListener;

// //import mb_animationTool.utils.Track;
// import net.miginfocom.swing.MigLayout;

// public class TimeBoxControlPanel extends JPanel {

// 	// Sets up panel that user can label months, create static 3d visualization, and
// 	// in future interaction

// 	private static final long serialVersionUID = 1L;
// 	static int openFrameCount = 0;
// 	static final int xOffset = 30, yOffset = 30;

// 	JFrame desktop;
// 	public Dimension animationSize = new Dimension(1280, 720);
// 	public Dimension boxSize = new Dimension(400, 400); // KATE ADDED
// 	public Colors colors;
// 	public Sketch sketch;

// 	public JFileChooser dataChooser;
// 	private DesktopPane parent;
// 	private SketchData data;

// 	public WideComboBox tagSelect2;// create wideCombo Boxes for tag selection
// 	public WideComboBox tagSelect1;
// 	public boolean tag2;

// 	// buttons for choosing the year and month
// 	public SpinnerModel syearSpinnerModel;
// 	public SpinnerModel smonthSpinnerModel;
// 	public SpinnerModel eyearSpinnerModel;
// 	public SpinnerModel emonthSpinnerModel;

// 	// check boxes for labels and highlighting boundary interaction
// 	public JCheckBox label;
// 	public JCheckBox bdyInteraction;
// 	public JCheckBox staticbox;

// 	public TimeBoxControlPanel(DesktopPane father) {
// 		parent = father;
// 		data = parent.data;

// 		// so the static panel box matches the rest of the panels
// 		try {
// 			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
// 		} catch (Exception e) {
// 			e.printStackTrace();
// 		}
// 		JFrame.setDefaultLookAndFeelDecorated(true);

// 		StaticTimeBoxPanel stbContainer = new StaticTimeBoxPanel(); // for the static time box

// 		// so everything else looks normal
// 		try {
// 			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
// 		} catch (Exception e) {
// 			e.printStackTrace();
// 		}

// 		setLayout(new MigLayout("insets 0", "[][][]", "[][]"));

// 		Object[] tags = new Object[parent.tagList.size()];// create array of all tags used
// 		for (int i = 0; i < parent.tagList.size(); i++) {
// 			tags[i] = parent.tagList.get(i);
// 		}

// 		data.timeBoxStartYear = data.startTime.getYear();
// 		data.timeBoxStartMonth = data.startTime.getMonthOfYear();
// 		data.timeBoxEndYear = data.endTime.getYear();
// 		data.timeBoxEndMonth = data.endTime.getMonthOfYear();

// 		// check box for the month labels
// 		label = new JCheckBox("Label Months");
// 		label.setIconTextGap(5);
// 		label.setHorizontalTextPosition(SwingConstants.RIGHT);
// 		label.setForeground(Color.BLACK);
// 		label.setFont(new Font("Arial", Font.PLAIN, 12));
// 		label.setMargin(new Insets(10, 5, 10, 10));
// 		this.add(label, "cell 0 1, gapleft 10");
// 		label.addChangeListener(new ChangeListener() {
// 			public void stateChanged(ChangeEvent evt) {
// 				JCheckBox cb = (JCheckBox) evt.getSource();
// 				if (cb.isSelected()) {// sets boolean variable to true or false
// 					data.labelMonth = true;

// 				} else {
// 					data.labelMonth = false;

// 				}
// 			}
// 		});

// 		// sets up the static box
// 		stbContainer.setupStaticBox(parent);
// 		stbContainer.addComponentListener(new ComponentAdapter() {
// 			public void componentHidden(ComponentEvent e) {
// 				staticbox.setSelected(false);
// 			}
// 		});
// 		stbContainer.addWindowListener(new WindowAdapter() {
// 			public void windowClosed(WindowEvent e) {
// 				data.stbclose = true;
// 				stbContainer.setupStaticBox(parent);
// 			}
// 		});

// 		// check box for static image of time box
// 		staticbox = new JCheckBox("Static Time-Box");
// 		staticbox.setIconTextGap(5);
// 		staticbox.setHorizontalTextPosition(SwingConstants.RIGHT);
// 		staticbox.setForeground(Color.BLACK);
// 		staticbox.setFont(new Font("Arial", Font.PLAIN, 12));
// 		staticbox.setMargin(new Insets(10, 5, 10, 10));
// 		this.add(staticbox, "cell 0 2, gapleft 10");
// 		staticbox.addChangeListener(new ChangeListener() {
// 			public void stateChanged(ChangeEvent evt) {
// 				JCheckBox cb = (JCheckBox) evt.getSource();
// 				if (cb.isSelected()) {// sets boolean variable to true or false
// 					data.staticBox = true;
// 					stbContainer.setVisible(true);

// 				} else {
// 					data.staticBox = false;
// 					stbContainer.setVisible(false);
// 					stbContainer.setupStaticBox(parent);

// 				}
// 			}
// 		});

// 		/*
// 		 * //check box for highlighting boundary interaction in time box
// 		 * 
// 		 * bdyInteraction = new JCheckBox("Highlight Interactions in 3D Panel");
// 		 * bdyInteraction.setIconTextGap(5);
// 		 * bdyInteraction.setHorizontalTextPosition(SwingConstants.RIGHT);
// 		 * bdyInteraction.setForeground(Color.BLACK); bdyInteraction.setFont(new
// 		 * Font("Arial", Font.PLAIN, 12)); bdyInteraction.setMargin(new Insets(10, 5,
// 		 * 10, 10)); this.add(bdyInteraction, "cell 0 3, gapleft 10");
// 		 * bdyInteraction.addChangeListener(new ChangeListener() { public void
// 		 * stateChanged(ChangeEvent evt) { JCheckBox cb = (JCheckBox) evt.getSource();
// 		 * if (cb.isSelected()) {//sets boolean variable to true or false
// 		 * data.timeBoxBdyHighlight = true; } else { data.timeBoxBdyHighlight = false; }
// 		 * } });
// 		 * 
// 		 */

// 	}

// 	private static boolean isMacOSX() {
// 		return System.getProperty("os.name").indexOf("Mac OS X") >= 0;
// 	}

// 	public static void enableFullScreenMode(Window window) {
// 		String className = "com.apple.eawt.FullScreenUtilities";
// 		String methodName = "setWindowCanFullScreen";
// 		try {
// 			Class<?> clazz = Class.forName(className);
// 			Method method = clazz.getMethod(methodName, new Class<?>[] { Window.class, boolean.class });
// 			method.invoke(null, window, true);
// 		} catch (Throwable t) {
// 			System.err.println("Can't Full Screen");
// 			t.printStackTrace();
// 		}
// 	}

// 	private static void createAndShowGUI() {
// 		if (isMacOSX()) {
// 			System.setProperty("apple.laf.useScreenMenuBar", "true");
// 			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Animation Tool");
// 		}
// 		try {
// 			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
// 			// UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
// 		} catch (Exception e) {
// 			e.printStackTrace();
// 		}
// 		JFrame.setDefaultLookAndFeelDecorated(true);
// 		DesktopPane frame = new DesktopPane();
// 		if (isMacOSX()) {
// 			enableFullScreenMode(frame);
// 		}
// 		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
// 		frame.setVisible(true);
// 	}

// }