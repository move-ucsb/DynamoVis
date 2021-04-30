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
import gui.TimeBoxControlPanel;
// import gui.TimeBoxPanel;
import gui.DataPanel;
import gui.TimeLine;
import utils.Attributes;
import utils.Track;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
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
	/*
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
	public TimeBoxControlPanel timeBoxControlPanel; // STC

	public JDialog timelineContainer;
	public JDialog tbcContainer; // STC
	public Box box; // STC

	public TimeLine timeLine;
	public JDialog textOutput;
	public CombinedControlPanel cp;
	public JDialog controlContainer;

	JCheckBoxMenuItem status;
	JMenu editMenu;
	JCheckBoxMenuItem timeline;
	JCheckBoxMenuItem cpCheck;

	JMenu vAnalytics; // STC
	JCheckBoxMenuItem timeBoxCheck; // STC

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

		setTitle("DynamoVis - Configure Animation");
		setResizable(true);
		setSize(250, 700);
		setLocation(sWidth / 5, sHeight / 5);
		dataConfigPanel = new DataPanel(this);
		setContentPane(dataConfigPanel);

		// App Icon
		if (isMacOSX()) {
			// macos app icon
			Image icon = new ImageIcon(this.getClass().getClassLoader().getResource("logo1024.png")).getImage();
			com.apple.eawt.Application.getApplication().setDockIconImage(icon);
			// System.out.println("Platform: macOS");
		} else {
			// windows app icon
			Image icon = new ImageIcon(this.getClass().getClassLoader().getResource("logo32e.png")).getImage();
			setIconImage(icon);
			// System.out.println("Platform: non-mac (Windows or Linux)");
		}
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
		tbcContainer = new JDialog(this); // STC

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
		System.out.println("# Build 0.5.0-dev, Apr 30, 2021");
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

	// STC -----------------------------
	public void setupSpaceTimeCubeSketch() {
		box = new Box();
		box.setParent(this);
		box.run(100, 100); // temp location
		box.getSurface().setTitle("3D Space-Time Cube");
	}
	// STC -----------------------------

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

		// STC -----------------------------
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
		// STC -----------------------------

		legendPanel = new LegendPanel(this);
		cp = new CombinedControlPanel(this);

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
		// Recorder recorder = new Recorder(this,bContainer);//kate added passing in
		// bContainer
		recordContainer.setContentPane(recorder);
		recordContainer.pack();

		if (startup) {
			this.setLocation(textOutput.getLocation().x, textOutput.getLocation().y + textOutput.getBounds().height);
			this.setSize(textOutput.getBounds().width, (int) (sHeight - (this.getLocation().y) * 1.1));
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
			recordContainer.getContentPane().removeAll();
			tbcContainer.getContentPane().removeAll(); // STC
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
		timeline.setEnabled(true);
		timeline.setSelected(true);
		cpCheck.setEnabled(true);
		cpCheck.setSelected(true);
		export.setEnabled(true);

		vAnalytics.setEnabled(true);   // STC
		timeBoxCheck.setEnabled(true); // STC

		if (!startup) {
			wl.restoreLocations();
		}

		if (startup) {
			timelineContainer.setVisible(true);
		}

		// STC -----------------------------
		if (timeBoxCheck.isSelected()) {
			tbcContainer.setVisible(true);
			box.getSurface().setVisible(true);
			data.boxvisible = true;
		}
		// STC -----------------------------

		startup = false;

		// prevent creating new animation when sketch is running
		dataConfigPanel.SetComponentsEnabled(false);
		resetWindowLocs();

		System.gc();
	}

	public void resetWindowLocs() {
		int sketchW = (int) animationSize.getWidth();
		int sketchH = (int) animationSize.getHeight();

		int cpW = (int) controlContainer.getBounds().getWidth();
		int cpH = sketchH + 40;
		int cpX = 10;
		cpX = cpX < 0 ? 0 : cpX % sWidth;
		int cpY = 20;
		cpY = cpY < 0 ? 0 : cpY % sHeight;

		int sketchX = cpW + 10;
		sketchX = sketchX < 0 ? 0 : sketchX % sWidth;
		int sketchY = cpY;
		sketchY = sketchY < 0 ? 0 : sketchY % sHeight;

		int timelineX = sketchX;
		timelineX = timelineX < 0 ? 0 : timelineX % sWidth;
		int timelineY = sketchY + sketchH + 40;
		timelineY = timelineY < 0 ? 0 : timelineY % sHeight;
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
		if (sketch != null) {
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

		editMenu = new JMenu("Edit");
		editMenu.setMnemonic(KeyEvent.VK_E);
		editMenu.setEnabled(false);
		menuBar.add(editMenu);

		JMenuItem baseMap = new JMenuItem("Basemap Provider");
		baseMap.setEnabled(true);
		baseMap.setMnemonic(KeyEvent.VK_B);
		baseMap.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.ALT_MASK));
		baseMap.setActionCommand("basemap");
		baseMap.addActionListener(this);
		editMenu.add(baseMap);

		// JMenuItem editLegend = new JMenuItem("Legend Layout");
		// editLegend.setEnabled(true);
		// editLegend.setMnemonic(KeyEvent.VK_L);
		// editLegend.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
		// ActionEvent.ALT_MASK));
		// editLegend.setActionCommand("legend");
		// editLegend.addActionListener(this);
		// editMenu.add(editLegend);

		// JMenuItem editColors = new JMenuItem("Color Ramps");
		// editColors.setEnabled(true);
		// editColors.setMnemonic(KeyEvent.VK_R);
		// editColors.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
		// ActionEvent.ALT_MASK));
		// editColors.setActionCommand("colors");
		// editColors.addActionListener(this);
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

		// STC -----------------------------
		vAnalytics = new JMenu("Visual Analytics");
		vAnalytics.setMnemonic(KeyEvent.VK_A);
		vAnalytics.setEnabled(false);
		menuBar.add(vAnalytics);

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
					tbcContainer.setVisible(true);
					if(box == null)	 setupSpaceTimeCubeSketch();
					box.setVisible(true);
				} else {
					tbcContainer.setVisible(false);
					if(box != null)	 box.exit();
				}
			}
		});
		// STC -----------------------------

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

	public static boolean isMacOSX() {
		return System.getProperty("os.name").indexOf("Mac OS X") >= 0;
	}

	// ACTION LISTENER OVERRIDE ----------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "new" -> {
			if (sketch != null) {
				sketch.isExiting = true;
				// sketch.exit();
			}
		}
		case "edit" -> {
		}
		case "color" -> {
		}
		case "legend" -> {
		}
		case "quit" -> quit();
		case "record" -> recordContainer.setVisible(true);
		case "basemap" -> baseMapContainer.setVisible(true);
		case "reset" -> resetWindowLocs();
		
		}
		;
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
