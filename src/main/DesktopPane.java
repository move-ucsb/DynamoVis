/*
  	DYNAMO Animation Tool
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
import gui.ActivitySpacePanel;
import gui.InteractionPanel;
import gui.MoveParameterPanel;
import gui.BoundaryVisualizationPanel;
import gui.TimeBoxControlPanel;
import gui.TimeBoxPanel;
import gui.StaticTimeBoxPanel;
import utils.PointRecord;

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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.joda.time.Seconds;

import de.fhpotsdam.unfolding.data.Feature;

public class DesktopPane extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	JFrame desktop;
	public Dimension animationSize = new Dimension(1280, 720);
	public Colors colors;
	public Sketch sketch;
	public WindowLocations wl;

	public JPanel sketchContainer;
	public JDialog dataConfigContainer;

	public LegendPanel legendPanel;
	public DataPanel dataConfigPanel;
	public ControlPanel controlPanel;
	public ActivitySpacePanel activityPanel; // New Panel for Activity Space
	public InteractionPanel interactionPanel; // New Panel for Interaction Analysis
	public MoveParameterPanel moveparaPanel; // New Panel for Move Parameter
	public BoundaryVisualizationPanel Bdy_Viz_Panel; // New Panel for boundary Visualization
	public TimeBoxControlPanel timeBoxControlPanel; // New Panel for 3D Time Analysis control //KATE ADDED

	public JDialog asContainer; // Activity Space Container
	public JDialog iContainer; // Interaction Container
	public JDialog mpContainer; // Move Parameter Container
	public JDialog tlContainer;
	public JDialog tbcContainer;// for the time box control panel
	public TimeBoxPanel bContainer; // for the time box

	public TimeLine timeLine;
	public JDialog textOutput;
	public CombinedControlPanel cp;
	public JDialog cpContainer;
	public JDialog vpContainer;
	public JDialog tbContainer;

	JCheckBoxMenuItem status;
	JMenu editMenu;
	JMenuItem shapeFile;
	JCheckBoxMenuItem timeline;
	JCheckBoxMenuItem cpCheck;
	JCheckBoxMenuItem vpCheck;

	JCheckBoxMenuItem activityCheck; // Check box for visibility of asContainer
	JCheckBoxMenuItem setParaCheck; // Check box for visibility of mpContainer
	JCheckBoxMenuItem interactionCheck; // Check box for visibility of iContainer
	JCheckBoxMenuItem timeBoxCheck;
	JCheckBoxMenuItem Bdy_Viz_Check;

	JMenuBar menuBar;
	JMenu export;
	JDialog recContainer;
	JDialog baseMapCtr;
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
	private int sWidth;
	private int sHeight;

	boolean color = false;
	public boolean legend = false;
	boolean vectors = false;
	boolean startup = true;

	public int exportCounter = 1;
	DesktopPane me;

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

	public DesktopPane() {
		me = this;
		DateTimeZone.setDefault(DateTimeZone.UTC);
		setMinimumSize(new Dimension(400, 200));
		wl = new WindowLocations();
		menuBar = createMenuBar();
		setJMenuBar(menuBar);
		setResizable(false);
		setTitle("DynamoVis Animation Tool");
		colors = new Colors(this);

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		sWidth = (int) gd.getDefaultConfiguration().getBounds().getWidth();
		sHeight = (int) gd.getDefaultConfiguration().getBounds().getHeight();

		fonts = ge.getAvailableFontFamilyNames();

		getEnvFieldList();
		textOutput = new JDialog(this);
		textOutput.setResizable(true);
		textOutput.setTitle("Status");
		textOutput.setSize(490, 180);
		textOutput.setLocation(sWidth / 2, sHeight - (sHeight / 4));
		wl.registerWindow(textOutput);

		JTextArea textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		PrintStream printStream = new PrintStream(new CustomOutputStream(textArea));
		System.setOut(printStream);
		// System.setErr(printStream);

		JScrollPane textScrollPane = new JScrollPane(textArea);
		textOutput.setContentPane(textScrollPane);
		textOutput.setVisible(true);
		textOutput.addComponentListener(new ComponentAdapter() {
			public void componentHidden(ComponentEvent e) {
				status.setSelected(false);
			}
		});

		setLocation(200, 50);
		pack();

		cpContainer = new JDialog(this);
		tlContainer = new JDialog(this);
		recContainer = new JDialog(this);
		baseMapCtr = new JDialog(this);
		bm = new BaseMapPanel(this);
		baseMapCtr.setContentPane(bm);
		baseMapCtr.setTitle("Basemap Provider");
		baseMapCtr.setResizable(false);
		baseMapCtr.setLocationRelativeTo(this);
		baseMapCtr.pack();

		System.out.println("");
		System.out.println("# DynamoVis Animation Tool");
		System.out.println("# Copyright (C) 2016 Glenn Xavier");
		System.out.println("#      Updated: 2021 Mert Toka");
		System.out.println("# Build 0.4.1, Feb 10, 2021");
		System.out.println("# This program comes with ABSOLUTELY NO WARRANTY");
		System.out.println("# This is free software, and you are welcome to redistribute it under certain conditions");
		System.out.println("");
		setupDataPanel();
	}

	public void setupDataPanel() {
		dataConfigContainer = new JDialog(this);
		dataConfigContainer.setResizable(true);
		dataConfigContainer.setTitle("Configure Animation");
		dataConfigContainer.setSize(550, 700);
		dataConfigContainer.setLocation(sWidth / 5, sHeight / 5);
		wl.registerWindow(dataConfigContainer);
		dataConfigPanel = new DataPanel(this);
		dataConfigContainer.setContentPane(dataConfigPanel);
		dataConfigContainer.setVisible(true);
	}

	public void setupSketch() {
		sketch = null;
		sketch = new Sketch();
		sketch.setParent(this);
		this.setContentPane(sketch);
		// sketchContainer.getContentPane().setPreferredSize(animationSize);
		// sketchContainer.getContentPane().setPreferredSize(animationSize);
		this.getContentPane().setPreferredSize(animationSize);
		this.pack();
		sketch.init();

		tlContainer.setLocation((int) this.getBounds().getX(),
				(int) (this.getBounds().getY() + this.getBounds().getHeight()));
		if (startup) {
			tlContainer.setSize((int) this.getBounds().getWidth(), 250);
		}
	}

	private void setupGUI() {
		controlPanel = new ControlPanel(this);

		timeLine = new TimeLine(this);
		tlContainer.setResizable(true);
		tlContainer.setTitle("Timeline");
		tlContainer.setContentPane(timeLine);
		tlContainer.addComponentListener(new ComponentAdapter() {
			public void componentHidden(ComponentEvent e) {
				timeline.setSelected(false);
			}
		});

		// Set up GUI for Activity Space Panel // ADDED
		activityPanel = new ActivitySpacePanel(this);
		asContainer.setResizable(true);
		asContainer.setTitle("Activity Space Analysis");
		asContainer.setContentPane(activityPanel);
		asContainer.setSize(500, 200);
		asContainer.addComponentListener(new ComponentAdapter() {
			public void componentHidden(ComponentEvent e) {
				activityCheck.setSelected(false);
			}
		});

		// Set up GUI for Move Parameter Panel // ADDED
		moveparaPanel = new MoveParameterPanel(this);
		mpContainer.setResizable(true);
		mpContainer.setTitle("Move Parameter");
		mpContainer.setContentPane(moveparaPanel);
		mpContainer.setSize(500, 100);
		mpContainer.addComponentListener(new ComponentAdapter() {
			public void componentHidden(ComponentEvent e) {
				setParaCheck.setSelected(false);
			}
		});

		// Set up GUI for Boundary Visualization Panel // MAY NEED TO REMOVE
		Bdy_Viz_Panel = new BoundaryVisualizationPanel(this);
		tbContainer.setResizable(false);
		tbContainer.setTitle("Boundary Visualization");
		tbContainer.setContentPane(Bdy_Viz_Panel);
		tbContainer.setSize(500, 200);
		tbContainer.setLocation(100, 100);
		tbContainer.addComponentListener(new ComponentAdapter() {
			public void componentHidden(ComponentEvent e) {
				setParaCheck.setSelected(false);
			}
		});
		tbContainer.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				// data.Bdy_Viz_Panel_Close = true;
				// data.Bdy_Viz_Enable = false;
				Bdy_Viz_Check.setSelected(false);

			}

			public void windowClosing(WindowEvent e) {
				// data.Bdy_Viz_Panel_Close = true;
				// data.Bdy_Viz_Enable = false;
				Bdy_Viz_Check.setSelected(false);

			}
		});

		// Set up GUI for Interaction Panel
		interactionPanel = new InteractionPanel(this);
		iContainer.setResizable(true);
		iContainer.setTitle("Interaction Analysis");
		iContainer.setContentPane(interactionPanel);
		iContainer.setSize(500, 270);
		iContainer.addComponentListener(new ComponentAdapter() {
			public void componentHidden(ComponentEvent e) {
				interactionCheck.setSelected(false);
			}
		});

		iContainer.addWindowListener(new WindowAdapter() // KATE just addded
		{
			public void windowClosed(WindowEvent e) {
				data.highlight_interaction_boundary = false;
			}
		});
		// Set up GUI for 3D Time analysis

		timeBoxControlPanel = new TimeBoxControlPanel(this);
		tbcContainer.setResizable(false);
		tbcContainer.setTitle("3D Space-Time Analysis Control Panel");
		tbcContainer.setContentPane(timeBoxControlPanel);
		tbcContainer.setSize(400, 170);
		tbcContainer.setLocation((int) (this.getBounds().getX()), 100);
		tbcContainer.addComponentListener(new ComponentAdapter() {
			public void componentHidden(ComponentEvent e) {
				timeBoxCheck.setSelected(false);
			}
		});

		// for the dynamic time box
		bContainer.setupBox(this);
		bContainer.addComponentListener(new ComponentAdapter() {
			public void componentHidden(ComponentEvent e) {
				timeBoxCheck.setSelected(false);
			}
		});

		legendPanel = new LegendPanel(this);
		cp = new CombinedControlPanel(this);
		// vp = new VisPanel(this);

		cpContainer.setContentPane(cp);
		cpContainer.pack();
		cpContainer.setResizable(false);
		cpContainer.setVisible(true);
		cpContainer.addComponentListener(new ComponentAdapter() {
			public void componentHidden(ComponentEvent e) {
				cpCheck.setSelected(false);
			}
		});

		recContainer.setTitle("Video Recorder");
		Recorder recorder = new Recorder(this, bContainer);// kate added passing in bContainer
		recContainer.setContentPane(recorder);
		recContainer.pack();

		if (startup) {
			int locw = (int) cpContainer.getBounds().getWidth();
			int thisx = (int) this.getBounds().getX();
			int thisy = (int) this.getBounds().getY();
			cpContainer.setLocation(thisx - locw, thisy);
			vpContainer.setLocation(thisx - locw, thisy + 250);
			recContainer.setLocationRelativeTo(this);
			asContainer.setLocationRelativeTo(this);
			mpContainer.setLocationRelativeTo(this);
			iContainer.setLocationRelativeTo(this);
			wl.registerWindow(tlContainer);
			wl.registerWindow(cpContainer);
			wl.registerWindow(recContainer);
		}

	}

	public void getEnvFieldList() {
		// gets a list of all potential envData fields and their units
		LoadEnvFieldsFromCSV envLoader = new LoadEnvFieldsFromCSV();
		envFields = envLoader.loadData("./config/EnvDATA-variables.csv");
	}

	public void newData(String path, String file, String title, Period period, Dimension dimension) {

		if (!startup) {
			wl.saveLocations();
			getContentPane().removeAll();
			cpContainer.getContentPane().removeAll();
			tlContainer.getContentPane().removeAll();
			asContainer.getContentPane().removeAll();
			mpContainer.getContentPane().removeAll();
			iContainer.getContentPane().removeAll();
			tbcContainer.getContentPane().removeAll();// KATE ADDED
			recContainer.getContentPane().removeAll();
			pack();
		}

		setTitle("DYNAMO Animation Tool   -   " + title + "   -   " + file);
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
		for (Entry<String, Track> entry : trackList.entrySet()) {
			Track track = entry.getValue();
			if (track.getVisibility()) {
				if (data.Times_hash != null) {
					// System.out.println("the times arraylist is adding other times!");
					Hashtable<Integer, ArrayList<Integer>> temp_Times_hash = track.get_filter_times();
					data.Times_hash.forEach((Year, Months) -> {
						if (temp_Times_hash.containsKey(Year)) {
							Months.addAll(temp_Times_hash.get(Year));
							Set<Integer> NoDuplicates = new LinkedHashSet<Integer>(Months);
							Months.clear();
							Months.addAll(NoDuplicates);
							Collections.sort(Months);
						} else {
							data.Times_hash.put(Year, Months);
						}
					});

				} else {
					// System.out.println("the times arraylist is EMpty!");
					data.Times_hash = track.get_filter_times();
				}

				data.All_the_Times.addAll(track.gettheTimes());
			}

		}
		Collections.sort(data.All_the_Times);
		// System.out.println(data.All_the_Times);
		data.Times_hash.forEach((Year, Months) -> {
			int min = Collections.min(Months);
			int max = Collections.max(Months);
			Months.clear();
			if (min == max) {
				Months.add(min);
			} else {
				Months.add(min);
				Months.add(max);
			}
		});
		// System.out.println(data.Times_hash);
		data.provider = bm.chosenProvider;

		if (period.toStandardMinutes().getMinutes() > 0) {
			data.alphaMaxHours = 120;
			data.startTime = Collections.min(dateCollection).minusHours(1).hourOfDay().roundFloorCopy();
			// data.endTime =
			// Collections.max(dateCollection).plusHours(data.alphaMaxHours).dayOfMonth().roundCeilingCopy();
			data.endTime = Collections.max(dateCollection);
			dataInterval = period.toStandardMinutes().getMinutes();
			timeUnit = "minutes";
			data.totalTime = Minutes.minutesBetween(data.startTime, data.endTime).getMinutes();
		} else {
			data.alphaMaxHours = 10;
			data.startTime = Collections.min(dateCollection).minuteOfHour().roundFloorCopy();
			;
			// data.endTime =
			// Collections.max(dateCollection).plusMinutes(data.alphaMaxHours).minuteOfHour().roundCeilingCopy();
			data.endTime = Collections.max(dateCollection);
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
		shapeFile.setEnabled(true);
		timeline.setEnabled(true);
		activityCheck.setEnabled(true);
		setParaCheck.setEnabled(true);
		Bdy_Viz_Check.setEnabled(true);
		interactionCheck.setEnabled(true);
		timeBoxCheck.setEnabled(true);
		cpCheck.setEnabled(true);
		cpCheck.setSelected(true);
		export.setEnabled(true);

		if (!startup) {
			wl.restoreLocations();
		}

		if (timeline.isSelected()) {
			tlContainer.setVisible(true);
		}

		if (Bdy_Viz_Check.isSelected()) {
			tbContainer.setVisible(true);
		}

		if (activityCheck.isSelected()) {
			asContainer.setVisible(true);
		}

		if (setParaCheck.isSelected()) {
			mpContainer.setVisible(true);
		}

		if (interactionCheck.isSelected()) {
			iContainer.setVisible(true);
		}
		if (timeBoxCheck.isSelected()) {
			tbcContainer.setVisible(true);
			bContainer.setVisible(true);
			data.boxvisible = true;
		}

		startup = false;
		System.gc();
	}

	public void resetWindowLocs() {
		int locw = (int) cpContainer.getBounds().getWidth();
		int thisx = (int) this.getBounds().getX();
		int thisy = (int) this.getBounds().getY();
		tlContainer.setLocation(thisx, (int) (thisy + this.getBounds().getHeight()));
		tlContainer.setSize((int) this.getBounds().getWidth(), 250);
		asContainer.setLocationRelativeTo(this);
		cpContainer.setLocation(thisx - locw, thisy);
		recContainer.setLocationRelativeTo(this);
		baseMapCtr.setLocationRelativeTo(this);
		textOutput.setLocation(sWidth / 2, sHeight - (sHeight / 4));
	}

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
		JMenu importFile = new JMenu("Import");
		file.setMnemonic(KeyEvent.VK_I);
		menuBar.add(importFile);

		// for displaying shape file - Nathan
		shapeFile = new JMenuItem("Import Shape File");
		shapeFile.setMnemonic(KeyEvent.VK_S);
		shapeFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
		shapeFile.setActionCommand("shapeFile");
		shapeFile.addActionListener(this);
		shapeFile.setEnabled(false);
		importFile.add(shapeFile);

		editMenu = new JMenu("Edit");
		editMenu.setMnemonic(KeyEvent.VK_E);
		editMenu.setEnabled(false);
		menuBar.add(editMenu);

		JMenuItem editData = new JMenuItem("Data Configuration");
		editData.setMnemonic(KeyEvent.VK_D);
		editData.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.ALT_MASK));
		editData.setActionCommand("edit");
		editData.addActionListener(this);
		editMenu.add(editData);

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
					cpContainer.setVisible(true);
				} else {
					cpContainer.setVisible(false);
				}
			}
		});

		timeline = new JCheckBoxMenuItem("Timeline");
		timeline.setSelected(true);
		timeline.setEnabled(false);
		timeline.setMnemonic(KeyEvent.VK_T);
		timeline.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.ALT_MASK));
		timeline.addActionListener(this);
		view.add(timeline);
		timeline.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				JCheckBoxMenuItem cb = (JCheckBoxMenuItem) evt.getSource();
				if (cb.isSelected()) {
					tlContainer.setVisible(true);
				} else {
					tlContainer.setVisible(false);
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
		JMenu vAnalytics = new JMenu("Visual Analytics");
		view.setMnemonic(KeyEvent.VK_A);
		menuBar.add(vAnalytics);

		Bdy_Viz_Check = new JCheckBoxMenuItem("Boundary Visualization");
		Bdy_Viz_Check.setSelected(false);
		Bdy_Viz_Check.setEnabled(false);
		Bdy_Viz_Check.setMnemonic(KeyEvent.VK_R);
		Bdy_Viz_Check.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.ALT_MASK));
		Bdy_Viz_Check.addActionListener(this);
		vAnalytics.add(Bdy_Viz_Check);
		Bdy_Viz_Check.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				JCheckBoxMenuItem cb = (JCheckBoxMenuItem) evt.getSource();
				if (cb.isSelected()) {
					tbContainer.setVisible(true);// if check box is checked, open up move parameter window
					// data.Bdy_Viz_Enable = true;
				} else {
					tbContainer.setVisible(false);
					// data.Bdy_Viz_Enable = false;
				}
			}
		});

		setParaCheck = new JCheckBoxMenuItem("Move Parameter");
		setParaCheck.setSelected(false);
		setParaCheck.setEnabled(false);
		setParaCheck.setMnemonic(KeyEvent.VK_P);
		setParaCheck.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
		setParaCheck.addActionListener(this);
		vAnalytics.add(setParaCheck);
		setParaCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				JCheckBoxMenuItem cb = (JCheckBoxMenuItem) evt.getSource();
				if (cb.isSelected()) {
					mpContainer.setVisible(true); // if check box is checked, open up move parameter window
				} else {
					mpContainer.setVisible(false);
				}
			}
		});

		interactionCheck = new JCheckBoxMenuItem("Interaction Analysis");
		interactionCheck.setSelected(false);
		interactionCheck.setEnabled(false);
		interactionCheck.setMnemonic(KeyEvent.VK_I);
		interactionCheck.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
		interactionCheck.addActionListener(this);
		vAnalytics.add(interactionCheck);
		interactionCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				JCheckBoxMenuItem cb = (JCheckBoxMenuItem) evt.getSource();
				if (cb.isSelected()) {
					iContainer.setVisible(true); // if check box is checked, open up interaction analysis window
				} else {
					iContainer.setVisible(false);
				}
			}
		});

		timeBoxCheck = new JCheckBoxMenuItem("3D Space-Time Analysis");
		timeBoxCheck.setSelected(false);
		timeBoxCheck.setEnabled(false);
		timeBoxCheck.setMnemonic(KeyEvent.VK_3);
		timeBoxCheck.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
		timeBoxCheck.addActionListener(this);
		vAnalytics.add(timeBoxCheck);
		timeBoxCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				JCheckBoxMenuItem cb = (JCheckBoxMenuItem) evt.getSource();
				if (cb.isSelected()) {
					tbcContainer.setVisible(true); // if check box is checked, open up interaction analysis window
					bContainer.setVisible(true); // if checked, open up the time box panel
					data.boxvisible = true;
				} else {
					tbcContainer.setVisible(false);
					bContainer.setVisible(false);
					data.boxvisible = false;
				}
			}
		});

		activityCheck = new JCheckBoxMenuItem("Activity Space Analysis");
		activityCheck.setSelected(false);
		activityCheck.setEnabled(false);
		activityCheck.setMnemonic(KeyEvent.VK_Y);
		activityCheck.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
		activityCheck.addActionListener(this);
		vAnalytics.add(activityCheck);
		activityCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				JCheckBoxMenuItem cb = (JCheckBoxMenuItem) evt.getSource();
				if (cb.isSelected()) {
					asContainer.setVisible(true); // if check box is checked, open up activity space analysis window
				} else {
					asContainer.setVisible(false); // if check box is not checked, nothing happenss
				}
			}
		});

		export = new JMenu("Export");
		export.setMnemonic(KeyEvent.VK_X);
		export.setEnabled(false);
		menuBar.add(export);

		JMenuItem record = new JMenuItem("Record");
		record.setEnabled(true);
		record.setActionCommand("record");
		record.addActionListener(this);
		export.add(record);

		JMenu help = new JMenu("Help");
		help.setMnemonic(KeyEvent.VK_H);
		menuBar.add(help);

		JMenu dev = new JMenu("Dev");
		menuBar.add(dev);

		JMenuItem histo = new JMenuItem("HISTO");
		histo.setActionCommand("histo");
		histo.addActionListener(this);
		histo.setEnabled(false);
		dev.add(histo);

		JMenuItem plot = new JMenuItem("PLOT");
		plot.setActionCommand("plot");
		plot.addActionListener(this);
		plot.setEnabled(false);
		dev.add(plot);

		JMenuItem corr = new JMenuItem("CORR");
		corr.setActionCommand("corr");
		corr.addActionListener(this);
		corr.setEnabled(false);
		dev.add(corr);

		JMenuItem fps = new JMenuItem("FPS");
		fps.setActionCommand("fps");
		fps.addActionListener(this);
		fps.setEnabled(false);
		dev.add(fps);

		JMenuItem about = new JMenuItem("About");
		about.setActionCommand("about");
		about.addActionListener(this);
		about.setEnabled(false);
		help.add(about);

		return menuBar;
	}

	protected void quit() {
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
		if ("new".equals(e.getActionCommand())) {
			dataConfigContainer.setVisible(true);
		} else if ("edit".equals(e.getActionCommand())) {
			dataConfigContainer.setVisible(true);
		} else if ("colors".equals(e.getActionCommand())) {

		} else if ("legend".equals(e.getActionCommand())) {

		} else if ("quit".equals(e.getActionCommand())) {
			quit();
		} else if ("record".equals(e.getActionCommand())) {
			recContainer.setVisible(true);
		} else if ("basemap".equals(e.getActionCommand())) {
			baseMapCtr.setVisible(true);
		} else if ("reset".equals(e.getActionCommand())) {
			resetWindowLocs();
		} else if ("histo".equals(e.getActionCommand())) {
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
