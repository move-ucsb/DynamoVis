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

package main;

import data.CustomOutputStream;
import data.LoadEnvFieldsFromCSV;
import gui.BaseMapPanel;
import gui.CombinedControlPanel;
import gui.ControlPanel;
import gui.LegendPanel;
import gui.DataPanel;
import gui.TimeLine;
import utils.Attributes;
import utils.Track;

// from the components
// import gui.ActivitySpacePanel;
// import gui.InteractionPanel;
// import gui.MoveParameterPanel;
// import gui.BoundaryVisualizationPanel;
// import gui.TimeBoxControlPanel;
// import gui.TimeBoxPanel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.joda.time.Seconds;

import de.fhpotsdam.unfolding.data.Feature;

public class DesktopPane extends JFrame implements ActionListener {
	/*
	 *
	 * 
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JFrame desktop;
	public Dimension animationSize = new Dimension(1280, 720);
	public Colors colors;
	public Sketch sketch;
	public WindowLocations wl;

	public LegendPanel legendPanel;
	public DataPanel dataConfigPanel;
	public ControlPanel controlPanel;
	// public ActivitySpacePanel activityPanel; // New Panel for Activity Space
	// public InteractionPanel interactionPanel; // New Panel for Interaction
	// Analysis
	// public MoveParameterPanel moveparaPanel; // New Panel for Move Parameter
	// public BoundaryVisualizationPanel Bdy_Viz_Panel; // New Panel for Boundary
	// Visualization
	// public TimeBoxControlPanel timeBoxControlPanel; // New Panel for 3D Time
	// Analysis control by Kate

	// public JDialog asContainer; // Activity Space Container
	// public JDialog iContainer; // Interaction Container
	// public JDialog mpContainer; // Move Parameter Container
	public JDialog timelineContainer;
	// public JDialog tbcContainer; // for the time box control panel
	// public TimeBoxPanel bContainer; // for the time box

	public TimeLine timeLine;
	public JDialog textOutput;
	public CombinedControlPanel cp;
	public JDialog controlContainer;
	// public JDialog vpContainer;
	// public JDialog tbContainer;

	JCheckBoxMenuItem status;
	JMenu editMenu;
	// JMenuItem shapeFile;
	JCheckBoxMenuItem timeline;
	JCheckBoxMenuItem cpCheck;
	// JCheckBoxMenuItem vpCheck;

	// JCheckBoxMenuItem activityCheck; // Check box for visibility of asContainer
	// JCheckBoxMenuItem setParaCheck; // Check box for visibility of mpContainer
	// JCheckBoxMenuItem interactionCheck; // Check box for visibility of iContainer
	// JCheckBoxMenuItem timeBoxCheck;
	// JCheckBoxMenuItem Bdy_Viz_Check;

	JMenuBar menuBar;
	JMenu export;
	JDialog recordContainer;
	JDialog baseMapContainer;
	BaseMapPanel bm;
	JDialog hc;

	public List<ArrayList<String>> envFields;
	public String dataFilePath;
	public List<Feature> dataPoints;
	public String[] headers;
	public ArrayList<String> editedFieldName;
	public ArrayList<String> selectedUnits;
	public List<String> tagList;
	public int dataInterval;
	public String timeUnit;
	public String animationTitle;
	public SketchData data;
	public Map<String, Object[]> timeLineDates;
	public String[] fonts;

	public Attributes attributes;
	public HashMap<String, Track> trackList;
	private int sWidth; // screen width
	private int sHeight; // screen height

	boolean color = false;
	public boolean legend = false;
	boolean vectors = false;
	boolean startup = true; // is data loaded

	public int exportCounter = 1;
	DesktopPane me;

	// Class that stores the locations of each window
	class WindowLocations {
		Map<Component, Point> windows;

		public WindowLocations() {
			windows = new HashMap<Component, Point>();
		}

		public void registerWindow(Component c) {
			Point p = c.getLocation();
			windows.put(c, p);
		}

		public void saveLocations() {
			for (Entry<Component, Point> entry : windows.entrySet()) {
				Component c = entry.getKey();
				windows.put(c, c.getLocation());
			}
		}

		public void restoreLocations() {
			for (Entry<Component, Point> entry : windows.entrySet()) {
				entry.getKey().setLocation(entry.getValue());
			}
		}
	}

	//
	public DesktopPane() {

		me = this;
		wl = new WindowLocations();
		DateTimeZone.setDefault(DateTimeZone.UTC);

		// Graphics device
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		sWidth = (int) gd.getDefaultConfiguration().getBounds().getWidth();
		sHeight = (int) gd.getDefaultConfiguration().getBounds().getHeight();
		fonts = ge.getAvailableFontFamilyNames();

		// CONFIGURE DATA WINDOW ----------------------
		setMinimumSize(new Dimension(200, 200));
		menuBar = createMenuBar();
		setJMenuBar(menuBar);

		setTitle("Configure Animation");
		setResizable(true);
		setSize(250, 700);
		setLocation(sWidth / 5, sHeight / 5);
		dataConfigPanel = new DataPanel(this);
		setContentPane(dataConfigPanel);
		setIconImage(new ImageIcon(this.getClass().getClassLoader().getResource("logo32_empty.png")).getImage());  // app icon
		pack();
		wl.registerWindow(this);

		// STATUS WINDOW -----------------------------
		textOutput = new JDialog(this);
		textOutput.setTitle("Status");
		textOutput.setResizable(true);
		textOutput.setSize(410, (int) (sHeight * 0.3));
		textOutput.setLocation(sWidth - 420, (int) (sHeight * 0.03));
		wl.registerWindow(textOutput);

		// Use status GUI element as system out
		JTextArea textArea = new JTextArea();
		textArea.setLineWrap(false);
		textArea.setEditable(false);
		PrintStream printStream = new PrintStream(new CustomOutputStream(textArea));
		System.setOut(printStream);

		JScrollPane textScrollPane = new JScrollPane(textArea);
		textOutput.setContentPane(textScrollPane);
		textOutput.setVisible(true);
		textOutput.addComponentListener(new ComponentAdapter() {
			public void componentHidden(ComponentEvent e) {
				status.setSelected(false);
			}
		});

		colors = new Colors(this);
		timelineContainer = new JDialog(this);
		controlContainer = new JDialog(this);
		recordContainer = new JDialog(this);
		baseMapContainer = new JDialog(this);
		// asContainer = new JDialog(this);
		// iContainer = new JDialog(this);
		// mpContainer = new JDialog(this);
		// tbcContainer = new JDialog(this);
		// bContainer = new TimeBoxPanel();
		// vpContainer = new JDialog(this);
		// tbContainer = new JDialog(this);

		// BASE MAP WINDOW -----------------------------
		bm = new BaseMapPanel(this);
		baseMapContainer.setTitle("Basemap Provider");
		baseMapContainer.setContentPane(bm);
		baseMapContainer.setResizable(false);
		baseMapContainer.setLocationRelativeTo(this);
		baseMapContainer.pack();

		// Gets a list of all potential envData fields and their units
		LoadEnvFieldsFromCSV envLoader = new LoadEnvFieldsFromCSV();
		envFields = envLoader.loadData("./config/EnvDATA-variables.csv");

		System.out.println("");
		System.out.println("# DynamoVis Animation Tool");
		System.out.println("# Copyright (C) 2016 Glenn Xavier");
		System.out.println("#      Updated: 2021 Mert Toka");
		System.out.println("# Build 0.4.1.7-dev, Mar 25, 2021");
		System.out.println("# This program comes with ABSOLUTELY NO WARRANTY");
		System.out.println("# This is free software, and you are welcome to \nredistribute it under certain conditions.");
		System.out.println("");
	}

	public void setupSketch() {
		sketch = new Sketch();
		sketch.setParent(this);
		sketch.setSize((int) animationSize.getWidth(), (int) animationSize.getHeight());
		sketch.run(0, 0); // temp location
		sketch.getSurface().setTitle(dataConfigPanel.getSurfaceTitle());
	}

	private void setupGUI() {
		controlPanel = new ControlPanel(this);

		timeLine = new TimeLine(this);
		timelineContainer.setResizable(true);
		timelineContainer.setTitle("Timeline");
		timelineContainer.setContentPane(timeLine);
		timelineContainer.addComponentListener(new ComponentAdapter() {
			public void componentHidden(ComponentEvent e) {
				timeline.setSelected(false);
			}
		});

		// Set up GUI for Activity Space Panel // ADDED
		// activityPanel = new ActivitySpacePanel(this);
		// asContainer.setResizable(true);
		// asContainer.setTitle("Activity Space Analysis");
		// asContainer.setContentPane(activityPanel);
		// asContainer.setSize(500, 200);
		// asContainer.addComponentListener(new ComponentAdapter() {
		// public void componentHidden(ComponentEvent e) {
		// activityCheck.setSelected(false);
		// }
		// });

		// // Set up GUI for Move Parameter Panel // ADDED
		// moveparaPanel = new MoveParameterPanel(this);
		// mpContainer.setResizable(true);
		// mpContainer.setTitle("Move Parameter");
		// mpContainer.setContentPane(moveparaPanel);
		// mpContainer.setSize(500, 100);
		// mpContainer.addComponentListener(new ComponentAdapter() {
		// public void componentHidden(ComponentEvent e) {
		// setParaCheck.setSelected(false);
		// }
		// });

		// // Set up GUI for Boundary Visualization Panel // MAY NEED TO REMOVE
		// Bdy_Viz_Panel = new BoundaryVisualizationPanel(this);
		// tbContainer.setResizable(false);
		// tbContainer.setTitle("Boundary Visualization");
		// tbContainer.setContentPane(Bdy_Viz_Panel);
		// tbContainer.setSize(500, 200);
		// tbContainer.setLocation(100, 100);
		// tbContainer.addComponentListener(new ComponentAdapter() {
		// public void componentHidden(ComponentEvent e) {
		// setParaCheck.setSelected(false);
		// }
		// });
		// tbContainer.addWindowListener(new WindowAdapter() {
		// public void windowClosed(WindowEvent e) {
		// // data.Bdy_Viz_Panel_Close = true;
		// // data.Bdy_Viz_Enable = false;
		// Bdy_Viz_Check.setSelected(false);

		// }

		// public void windowClosing(WindowEvent e) {
		// // data.Bdy_Viz_Panel_Close = true;
		// // data.Bdy_Viz_Enable = false;
		// Bdy_Viz_Check.setSelected(false);

		// }
		// });

		// // Set up GUI for Interaction Panel
		// interactionPanel = new InteractionPanel(this);
		// iContainer.setResizable(true);
		// iContainer.setTitle("Interaction Analysis");
		// iContainer.setContentPane(interactionPanel);
		// iContainer.setSize(500, 270);
		// iContainer.addComponentListener(new ComponentAdapter() {
		// public void componentHidden(ComponentEvent e) {
		// interactionCheck.setSelected(false);
		// }
		// });

		// iContainer.addWindowListener(new WindowAdapter() // KATE just addded
		// {
		// public void windowClosed(WindowEvent e) {
		// data.highlight_interaction_boundary = false;
		// }
		// });
		// // Set up GUI for 3D Time analysis

		// timeBoxControlPanel = new TimeBoxControlPanel(this);
		// tbcContainer.setResizable(false);
		// tbcContainer.setTitle("3D Space-Time Analysis Control Panel");
		// tbcContainer.setContentPane(timeBoxControlPanel);
		// tbcContainer.setSize(400, 170);
		// tbcContainer.setLocation((int) (this.getBounds().getX()), 100);
		// tbcContainer.addComponentListener(new ComponentAdapter() {
		// public void componentHidden(ComponentEvent e) {
		// timeBoxCheck.setSelected(false);
		// }
		// });

		// // for the dynamic time box
		// bContainer.setupBox(this);
		// bContainer.addComponentListener(new ComponentAdapter() {
		// public void componentHidden(ComponentEvent e) {
		// timeBoxCheck.setSelected(false);
		// }
		// });

		legendPanel = new LegendPanel(this);
		cp = new CombinedControlPanel(this);
		// vp = new VisPanel(this);

		controlContainer.setTitle("Control Panel");
		controlContainer.setContentPane(cp);
		controlContainer.pack();
		controlContainer.setResizable(false);
		controlContainer.setVisible(true);
		controlContainer.addComponentListener(new ComponentAdapter() {
			public void componentHidden(ComponentEvent e) {
				cpCheck.setSelected(false);
			}
		});

		recordContainer.setTitle("Video Recorder");
		Recorder recorder = new Recorder(this);
		// Recorder recorder = new Recorder(this, bContainer);// kate added passing in
		recordContainer.setContentPane(recorder);
		recordContainer.pack();

		if (startup) {
			// int locw = (int) controlContainer.getBounds().getWidth();
			// int thisx = (int) this.getBounds().getX();
			// int thisy = (int) this.getBounds().getY();
			this.setLocation(textOutput.getLocation().x, textOutput.getLocation().y + textOutput.getBounds().height);
			this.setSize(textOutput.getBounds().width, (int) (sHeight - (this.getLocation().y) * 1.1));
			// controlContainer.setLocation(thisx - locw, thisy);
			// vpContainer.setLocation(thisx - locw, thisy + 250);
			// asContainer.setLocationRelativeTo(this);
			// mpContainer.setLocationRelativeTo(this);
			// iContainer.setLocationRelativeTo(this);
			recordContainer.setLocationRelativeTo(this);
			wl.registerWindow(timelineContainer);
			wl.registerWindow(controlContainer);
			wl.registerWindow(recordContainer);
		}

	}

	public void newData(String path, String file, String title, Period period, Dimension dimension) {

		if (!startup) {
			wl.saveLocations();
			getContentPane().removeAll();
			controlContainer.getContentPane().removeAll();
			timelineContainer.getContentPane().removeAll();
			// asContainer.getContentPane().removeAll();
			// mpContainer.getContentPane().removeAll();
			// iContainer.getContentPane().removeAll();
			// tbcContainer.getContentPane().removeAll();// KATE ADDED
			recordContainer.getContentPane().removeAll();
			pack();
		}

		setTitle("DynamoVis Animation Tool");

		dataFilePath = path;

		animationTitle = title;

		animationSize = dimension;

		List<DateTime> dateCollection = new ArrayList<DateTime>();
		tagList = new ArrayList<String>();
		for (Entry<String, Track> entry : trackList.entrySet()) {
			Track track = entry.getValue();
			if (track.getVisibility()) {

				tagList.add(track.getTag());
				DateTime dateStart = track.getStartDate();
				DateTime dateEnd = track.getEndDate();
				dateCollection.add(dateStart);
				dateCollection.add(dateEnd);
			}
		}
		Collections.sort(tagList);
		data = new SketchData();

		// for (Entry<String, Track> entry : trackList.entrySet()) {
		// Track track = entry.getValue();
		// if (track.getVisibility()) {
		// if (data.Times_hash != null) {
		// // System.out.println("the times arraylist is adding other times!");
		// Hashtable<Integer, ArrayList<Integer>> temp_Times_hash =
		// track.get_filter_times();
		// data.Times_hash.forEach((Year, Months) -> {
		// if (temp_Times_hash.containsKey(Year)) {
		// Months.addAll(temp_Times_hash.get(Year));
		// Set<Integer> NoDuplicates = new LinkedHashSet<Integer>(Months);
		// Months.clear();
		// Months.addAll(NoDuplicates);
		// Collections.sort(Months);
		// } else {
		// data.Times_hash.put(Year, Months);
		// }
		// });

		// } else {
		// // System.out.println("the times arraylist is EMpty!");
		// data.Times_hash = track.get_filter_times();
		// }

		// data.All_the_Times.addAll(track.gettheTimes());
		// }

		// }
		// Collections.sort(data.All_the_Times);
		// // System.out.println(data.All_the_Times);
		// data.Times_hash.forEach((Year, Months) -> {
		// int min = Collections.min(Months);
		// int max = Collections.max(Months);
		// Months.clear();
		// if (min == max) {
		// Months.add(min);
		// } else {
		// Months.add(min);
		// Months.add(max);
		// }
		// });
		// // System.out.println(data.Times_hash);

		data.provider = bm.chosenProvider;

		if (period.toStandardMinutes().getMinutes() > 0) {
			data.alphaMaxHours = 120;
			data.startTime = Collections.min(dateCollection).minusHours(1).hourOfDay().roundFloorCopy();
			data.endTime = Collections.max(dateCollection).plusHours(data.alphaMaxHours).dayOfMonth()
					.roundCeilingCopy();
			// data.endTime = Collections.max(dateCollection);
			dataInterval = period.toStandardMinutes().getMinutes();
			timeUnit = "minutes";
			data.totalTime = Minutes.minutesBetween(data.startTime, data.endTime).getMinutes();
		} else {
			data.alphaMaxHours = 10;
			data.startTime = Collections.min(dateCollection).minuteOfHour().roundFloorCopy();
			data.endTime = Collections.max(dateCollection).plusMinutes(data.alphaMaxHours).minuteOfHour()
					.roundCeilingCopy();
			// data.endTime = Collections.max(dateCollection);
			dataInterval = period.toStandardSeconds().getSeconds();
			timeUnit = "seconds";
			data.totalTime = Seconds.secondsBetween(data.startTime, data.endTime).getSeconds();
		}

		dataCompleted();
	}

	public void dataCompleted() {
		data.processFeatureList(trackList);
		data.dataInterval = dataInterval;
		data.timeUnit = timeUnit;
		setupGUI();
		setupSketch();

		editMenu.setEnabled(true);
		// shapeFile.setEnabled(true);
		timeline.setEnabled(true);
		timeline.setSelected(true);
		// activityCheck.setEnabled(true);
		// setParaCheck.setEnabled(true);
		// Bdy_Viz_Check.setEnabled(true);
		// interactionCheck.setEnabled(true);
		// timeBoxCheck.setEnabled(true);
		cpCheck.setEnabled(true);
		cpCheck.setSelected(true);
		export.setEnabled(true);

		if (!startup) {
			wl.restoreLocations();
		}

		if (startup) {
			timelineContainer.setVisible(true);
		}

		// if (Bdy_Viz_Check.isSelected()) {
		// tbContainer.setVisible(true);
		// }

		// if (activityCheck.isSelected()) {
		// asContainer.setVisible(true);
		// }

		// if (setParaCheck.isSelected()) {
		// mpContainer.setVisible(true);
		// }

		// if (interactionCheck.isSelected()) {
		// iContainer.setVisible(true);
		// }
		// if (timeBoxCheck.isSelected()) {
		// tbcContainer.setVisible(true);
		// bContainer.setVisible(true);
		// data.boxvisible = true;
		// }

		startup = false;

		// prevent creating new animation when sketch is running
		dataConfigPanel.okButton.setEnabled(false);
		resetWindowLocs();

		System.gc();
	}

	public void resetWindowLocs() {
		int sketchW = (int) animationSize.getWidth();
		int sketchH = (int) animationSize.getHeight();

		int cpW = (int) controlContainer.getBounds().getWidth();
		int cpH = sketchH + 40;
		int cpX = 10;
		cpX = cpX < 0 ? 0 : cpX%sWidth;
		int cpY = 20;
		cpY = cpY < 0 ? 0 : cpY%sHeight;

		int sketchX = cpW + 10;
		sketchX = sketchX < 0 ? 0 : sketchX%sWidth;
		int sketchY = cpY;
		sketchY = sketchY < 0 ? 0 : sketchY%sHeight;

		int timelineX = sketchX;
		timelineX = timelineX < 0 ? 0 : timelineX%sWidth;
		int timelineY = sketchY + sketchH + 40;
		timelineY = timelineY < 0 ? 0 : timelineY%sHeight;
		int timelineW = sketchW;
		int timelineH = 250;
		
		int statusW = 410;
		int statusH = (int) (sHeight * 0.3);
		int statusX = sWidth - statusW - 10;
		int statusY = sketchY;

		int dataX = statusX;
		int dataY = statusY + statusH;
		int dataW = statusW;
		int dataH = sHeight - statusH - 80;

		setLocation(dataX, dataY);
		setSize(dataW, dataH);
		if(sketch != null) {
			sketch.getSurface().setLocation(sketchX, sketchY);
			sketch.getSurface().setSize(sketchW, sketchH);
		}
		
		timelineContainer.setLocation(timelineX, timelineY);
		timelineContainer.setSize(timelineW, timelineH);
		// asContainer.setLocationRelativeTo(this);
		controlContainer.setLocation(cpX, cpY);
		controlContainer.setSize(cpW, cpH);
		controlContainer.setResizable(true);
		recordContainer.setLocationRelativeTo(this);
		baseMapContainer.setLocationRelativeTo(this);
		textOutput.setLocation(statusX, statusY);
		textOutput.setSize(statusW, statusH);
	}

	// Menu Bar Items and Actions
	protected JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);
		menuBar.add(file);

		JMenuItem newData = new JMenuItem("New");
		newData.setMnemonic(KeyEvent.VK_N);
		newData.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.ALT_MASK));
		newData.setActionCommand("new");
		newData.addActionListener(this);
		file.add(newData);

		JMenuItem exit = new JMenuItem("Exit");
		exit.setMnemonic(KeyEvent.VK_Q);
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.ALT_MASK));
		exit.setActionCommand("quit");
		exit.addActionListener(this);
		file.add(exit);

		// Import menu - Nathan
		// JMenu importFile = new JMenu("Import");
		// file.setMnemonic(KeyEvent.VK_I);
		// menuBar.add(importFile);

		// for displaying shape file - Nathan
		// shapeFile = new JMenuItem("Import Shape File");
		// shapeFile.setMnemonic(KeyEvent.VK_S);
		// shapeFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
		// ActionEvent.ALT_MASK));
		// shapeFile.setActionCommand("shapeFile");
		// shapeFile.addActionListener(this);
		// shapeFile.setEnabled(false);
		// importFile.add(shapeFile);

		editMenu = new JMenu("Edit");
		editMenu.setMnemonic(KeyEvent.VK_E);
		editMenu.setEnabled(false);
		menuBar.add(editMenu);

		// JMenuItem editData = new JMenuItem("Data Configuration");
		// editData.setMnemonic(KeyEvent.VK_D);
		// editData.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.ALT_MASK));
		// editData.setActionCommand("edit");
		// editData.addActionListener(this);
		// editMenu.add(editData);

		JMenuItem baseMap = new JMenuItem("Basemap Provider");
		baseMap.setEnabled(true);
		baseMap.setMnemonic(KeyEvent.VK_B);
		baseMap.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.ALT_MASK));
		baseMap.setActionCommand("basemap");
		baseMap.addActionListener(this);
		editMenu.add(baseMap);

		JMenuItem editLegend = new JMenuItem("Legend Layout");
		editLegend.setEnabled(true);
		editLegend.setMnemonic(KeyEvent.VK_L);
		editLegend.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.ALT_MASK));
		editLegend.setActionCommand("legend");
		editLegend.addActionListener(this);
		// editMenu.add(editLegend);

		JMenuItem editColors = new JMenuItem("Color Ramps");
		editColors.setEnabled(true);
		editColors.setMnemonic(KeyEvent.VK_R);
		editColors.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
		editColors.setActionCommand("colors");
		editColors.addActionListener(this);
		// editMenu.add(editColors);

		JMenu view = new JMenu("View");
		view.setMnemonic(KeyEvent.VK_V);
		menuBar.add(view);

		cpCheck = new JCheckBoxMenuItem("Control Panel");
		cpCheck.setSelected(false);
		cpCheck.setEnabled(false);
		cpCheck.setMnemonic(KeyEvent.VK_C);
		cpCheck.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
		cpCheck.addActionListener(this);
		view.add(cpCheck);
		cpCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				JCheckBoxMenuItem cb = (JCheckBoxMenuItem) evt.getSource();
				if (cb.isSelected()) {
					controlContainer.setVisible(true);
				} else {
					controlContainer.setVisible(false);
				}
			}
		});

		timeline = new JCheckBoxMenuItem("Timeline");
		timeline.setSelected(false);
		timeline.setEnabled(false);
		timeline.setMnemonic(KeyEvent.VK_T);
		timeline.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.ALT_MASK));
		timeline.addActionListener(this);
		view.add(timeline);
		timeline.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				JCheckBoxMenuItem cb = (JCheckBoxMenuItem) evt.getSource();
				if (cb.isSelected()) {
					timelineContainer.setVisible(true);
				} else {
					timelineContainer.setVisible(false);
				}
			}
		});

		status = new JCheckBoxMenuItem("Status");
		status.setSelected(true);
		status.setEnabled(true);
		status.setMnemonic(KeyEvent.VK_S);
		status.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
		status.addActionListener(this);
		view.add(status);
		status.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				JCheckBoxMenuItem cb = (JCheckBoxMenuItem) evt.getSource();
				if (cb.isSelected()) {
					textOutput.setVisible(true);
				} else {
					textOutput.setVisible(false);
				}
			}
		});

		view.addSeparator();

		JMenuItem resetLayout = new JMenuItem("Reset Window Layout");
		resetLayout.setEnabled(true);
		resetLayout.setMnemonic(KeyEvent.VK_R);
		resetLayout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.ALT_MASK));
		resetLayout.setActionCommand("reset");
		view.add(resetLayout);
		resetLayout.addActionListener(this);

		// new menu bar
		// JMenu vAnalytics = new JMenu("Visual Analytics");
		// view.setMnemonic(KeyEvent.VK_A);
		// menuBar.add(vAnalytics);

		// Bdy_Viz_Check = new JCheckBoxMenuItem("Boundary Visualization");
		// Bdy_Viz_Check.setSelected(false);
		// Bdy_Viz_Check.setEnabled(false);
		// Bdy_Viz_Check.setMnemonic(KeyEvent.VK_R);
		// Bdy_Viz_Check.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
		// ActionEvent.ALT_MASK));
		// Bdy_Viz_Check.addActionListener(this);
		// vAnalytics.add(Bdy_Viz_Check);
		// Bdy_Viz_Check.addItemListener(new ItemListener() {
		// public void itemStateChanged(ItemEvent evt) {
		// JCheckBoxMenuItem cb = (JCheckBoxMenuItem) evt.getSource();
		// if (cb.isSelected()) {
		// tbContainer.setVisible(true);// if check box is checked, open up move
		// parameter window
		// // data.Bdy_Viz_Enable = true;
		// } else {
		// tbContainer.setVisible(false);
		// // data.Bdy_Viz_Enable = false;
		// }
		// }
		// });

		// setParaCheck = new JCheckBoxMenuItem("Move Parameter");
		// setParaCheck.setSelected(false);
		// setParaCheck.setEnabled(false);
		// setParaCheck.setMnemonic(KeyEvent.VK_P);
		// setParaCheck.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
		// ActionEvent.ALT_MASK));
		// setParaCheck.addActionListener(this);
		// vAnalytics.add(setParaCheck);
		// setParaCheck.addItemListener(new ItemListener() {
		// public void itemStateChanged(ItemEvent evt) {
		// JCheckBoxMenuItem cb = (JCheckBoxMenuItem) evt.getSource();
		// if (cb.isSelected()) {
		// mpContainer.setVisible(true); // if check box is checked, open up move
		// parameter window
		// } else {
		// mpContainer.setVisible(false);
		// }
		// }
		// });

		// interactionCheck = new JCheckBoxMenuItem("Interaction Analysis");
		// interactionCheck.setSelected(false);
		// interactionCheck.setEnabled(false);
		// interactionCheck.setMnemonic(KeyEvent.VK_I);
		// interactionCheck.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
		// ActionEvent.ALT_MASK));
		// interactionCheck.addActionListener(this);
		// vAnalytics.add(interactionCheck);
		// interactionCheck.addItemListener(new ItemListener() {
		// public void itemStateChanged(ItemEvent evt) {
		// JCheckBoxMenuItem cb = (JCheckBoxMenuItem) evt.getSource();
		// if (cb.isSelected()) {
		// iContainer.setVisible(true); // if check box is checked, open up interaction
		// analysis window
		// } else {
		// iContainer.setVisible(false);
		// }
		// }
		// });

		// timeBoxCheck = new JCheckBoxMenuItem("3D Space-Time Analysis");
		// timeBoxCheck.setSelected(false);
		// timeBoxCheck.setEnabled(false);
		// timeBoxCheck.setMnemonic(KeyEvent.VK_3);
		// timeBoxCheck.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
		// ActionEvent.ALT_MASK));
		// timeBoxCheck.addActionListener(this);
		// vAnalytics.add(timeBoxCheck);
		// timeBoxCheck.addItemListener(new ItemListener() {
		// public void itemStateChanged(ItemEvent evt) {
		// JCheckBoxMenuItem cb = (JCheckBoxMenuItem) evt.getSource();
		// if (cb.isSelected()) {
		// tbcContainer.setVisible(true); // if check box is checked, open up
		// interaction analysis window
		// bContainer.setVisible(true); // if checked, open up the time box panel
		// data.boxvisible = true;
		// } else {
		// tbcContainer.setVisible(false);
		// bContainer.setVisible(false);
		// data.boxvisible = false;
		// }
		// }
		// });

		// activityCheck = new JCheckBoxMenuItem("Activity Space Analysis");
		// activityCheck.setSelected(false);
		// activityCheck.setEnabled(false);
		// activityCheck.setMnemonic(KeyEvent.VK_Y);
		// activityCheck.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
		// ActionEvent.ALT_MASK));
		// activityCheck.addActionListener(this);
		// vAnalytics.add(activityCheck);
		// activityCheck.addItemListener(new ItemListener() {
		// public void itemStateChanged(ItemEvent evt) {
		// JCheckBoxMenuItem cb = (JCheckBoxMenuItem) evt.getSource();
		// if (cb.isSelected()) {
		// asContainer.setVisible(true); // if check box is checked, open up activity
		// space analysis window
		// } else {
		// asContainer.setVisible(false); // if check box is not checked, nothing
		// happenss
		// }
		// }
		// });

		export = new JMenu("Export");
		export.setMnemonic(KeyEvent.VK_X);
		export.setEnabled(false);
		menuBar.add(export);

		JMenuItem record = new JMenuItem("Record");
		record.setEnabled(true);
		record.setActionCommand("record");
		record.addActionListener(this);
		export.add(record);

		// JMenu help = new JMenu("Help");
		// help.setMnemonic(KeyEvent.VK_H);
		// menuBar.add(help);

		// JMenuItem about = new JMenuItem("About");
		// about.setActionCommand("about");
		// about.addActionListener(this);
		// about.setEnabled(false);
		// help.add(about);

		// JMenu dev = new JMenu("Dev");
		// menuBar.add(dev);

		// JMenuItem histo = new JMenuItem("HISTO");
		// histo.setActionCommand("histo");
		// histo.addActionListener(this);
		// histo.setEnabled(false);
		// dev.add(histo);

		// JMenuItem plot = new JMenuItem("PLOT");
		// plot.setActionCommand("plot");
		// plot.addActionListener(this);
		// plot.setEnabled(false);
		// dev.add(plot);

		// JMenuItem corr = new JMenuItem("CORR");
		// corr.setActionCommand("corr");
		// corr.addActionListener(this);
		// corr.setEnabled(false);
		// dev.add(corr);

		// JMenuItem fps = new JMenuItem("FPS");
		// fps.setActionCommand("fps");
		// fps.addActionListener(this);
		// fps.setEnabled(false);
		// dev.add(fps);

		return menuBar;
	}

	protected void quit() {
		if (sketch != null)
			sketch.exit();
		System.exit(0);
	}

	public static void enableFullScreenMode(Window window) {
		String className = "com.apple.eawt.FullScreenUtilities";
		String methodName = "setWindowCanFullScreen";
		try {
			Class<?> clazz = Class.forName(className);
			Method method = clazz.getMethod(methodName, new Class<?>[] { Window.class, boolean.class });
			method.invoke(null, window, true);
		} catch (Throwable t) {
			System.err.println("Can't Full Screen");
			t.printStackTrace();
		}
	}

	private static boolean isMacOSX() {
		return System.getProperty("os.name").indexOf("Mac OS X") >= 0;
	}

	// ACTION LISTENER OVERRIDE ----------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
			case "new" -> {
				if (sketch != null) 	sketch.exit();
			}
			case "edit" -> {}
			case "color" -> {}
			case "legend" -> {}
			case "quit" -> quit();
			case "record" -> recordContainer.setVisible(true);
			case "basemap" -> baseMapContainer.setVisible(true);
			case "reset" -> resetWindowLocs();
			case "histo" -> {
				if (hc == null) {
					hc = new JDialog(this);
					hc.setTitle("HISTO TEST");
					Histo histo = new Histo();
					hc.setContentPane(histo);
					hc.setLocationRelativeTo(this);
					hc.pack();
				}
				hc.setVisible(true);
			}
		};
	}

	// MAIN ------------------------------
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (isMacOSX()) {
					System.setProperty("apple.laf.useScreenMenuBar", "true");
					System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Animation Tool");
				}
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					e.printStackTrace();
				}
				JFrame.setDefaultLookAndFeelDecorated(true);
				DesktopPane frame = new DesktopPane();
				if (isMacOSX()) {
					enableFullScreenMode(frame);
				}
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}
}
