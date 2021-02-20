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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.gicentre.utils.colour.ColourRule;
import org.gicentre.utils.colour.ColourTable;

import main.DesktopPane;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class GradientEditor extends JPanel {

	public DesktopPane parent;
	private ArrayList<ControlPoint> list;
	private ControlPoint selected;
	private Polygon poly = new Polygon();
	private Polygon square = new Polygon();
	private JButton del = new JButton("Delete");
	private int x;
	private int y;
	private int width;
	private int barHeight;
	public ColourTable currentSwatch;
	private ArrayList<ActionListener> listeners = new ArrayList<ActionListener>();
	private JComboBox comboBox;
	private JButton okButton = new JButton("OK");
	private JButton resetButton = new JButton("Reset");
	private JButton loadButton = new JButton("Load File");
	private JButton saveButton = new JButton("Save File");
	private JButton newButton = new JButton("Add to List");
	private JButton colButton = new JButton("");
	private JTextField textField = new JTextField();
	private JLabel colLabel = new JLabel("Color:");
	private JLabel locLabel = new JLabel("Location:");
	private JLabel label = new JLabel("%");
	private JTextField nameField = new JTextField();
	private JDialog mom;
	DecimalFormat df = new DecimalFormat("##.##");

	LinePanel lp;
	PointPanel pp;
	VectorPanel vp;
	BoundaryPanel bp;
	static int LINE = 1;
	static int POINT = 2;
	static int VECTOR = 3;
	static int BOUNDARY = 4;
	int owner;

	public GradientEditor(DesktopPane father, JDialog m, LinePanel l) {
		this(father, m, LINE);
	}

	public GradientEditor(DesktopPane father, JDialog m, PointPanel p) {
		this(father, m, POINT);
	}

	public GradientEditor(DesktopPane father, JDialog m, VectorPanel v) {
		this(father, m, VECTOR);
	}

	public GradientEditor(DesktopPane father, JDialog m, BoundaryPanel b) {
		this(father, m, BOUNDARY);
	}

	public GradientEditor(DesktopPane father, JDialog m, int o) {

		owner = o;
		parent = father;
		mom = m;

		textField.setMaximumSize(new Dimension(50, 2147483647));
		textField.setColumns(10);

		comboBox = new JComboBox(parent.colors.largeRampList.toArray());

		setLayout(new MigLayout("", "[grow][grow][fill]", "[][][][][][grow][]"));

		Gradient gradient = new Gradient();
		gradient.setMinimumSize(new Dimension(400, 65));
		add(gradient, "cell 0 5 3 1,growx");

		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox comboBox = (JComboBox) e.getSource();
				convertSwatch(parent.colors.coloursCont.get(comboBox.getSelectedIndex()));
			}
		});

		setSelected();

		// add(lblName, "flowx,cell 0 4 2 1,alignx trailing");
		nameField.setColumns(10);

		// add(nameField, "cell 0 4 2 1,growx");

		add(newButton, "cell 2 4,growx");
		newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ImageIcon largeSwatch = parent.colors.createSwatch(currentSwatch, "large");
				ImageIcon smallSwatch = parent.colors.createSwatch(currentSwatch, "small");
				parent.colors.coloursCont.add(currentSwatch);
				parent.colors.colorRampList.add(smallSwatch);
				parent.cp.linePanel.colorRampList.addItem(smallSwatch);
				parent.cp.pointPanel.colorRampList.addItem(smallSwatch);
				parent.cp.vectorPanel.colorRampList.addItem(smallSwatch);
				parent.colors.largeRampList.add(largeSwatch);
				comboBox.addItem(largeSwatch);
				comboBox.setSelectedIndex(comboBox.getItemCount() - 1);
			}
		});

		add(saveButton, "cell 2 3,growx");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new File("./config/color"));
				chooser.setAcceptAllFileFilterUsed(false);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("ColourTable File", "ctb", "CTB");
				chooser.addChoosableFileFilter(filter);
				int retrival = chooser.showSaveDialog(null);
				if (retrival == JFileChooser.APPROVE_OPTION) {
					try {
						String string = chooser.getSelectedFile().getAbsolutePath();
						if (!string.endsWith(".ctb")) {
							string = string + ".ctb";
						}
						OutputStream out = new FileOutputStream(string);
						ColourTable.writeFile(currentSwatch, out);
						System.out.println("Saved Color Table: " + string);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});

		add(loadButton, "cell 2 2,growx");
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new File("./config/color"));
				chooser.setAcceptAllFileFilterUsed(false);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("ColourTable File", "ctb", "CTB");
				chooser.addChoosableFileFilter(filter);
				int retrival = chooser.showOpenDialog(null);
				if (retrival == JFileChooser.APPROVE_OPTION) {
					try {
						InputStream in = new FileInputStream(chooser.getSelectedFile());
						convertSwatch(ColourTable.readFile(in));
						newButton.doClick();
						System.out.println("Loaded Color Table: " + chooser.getSelectedFile());
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});

		add(resetButton, "cell 2 1,growx");
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				convertSwatch(parent.colors.coloursCont.get(comboBox.getSelectedIndex()));
			}
		});

		add(okButton, "cell 2 0,growx");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (owner == LINE)
					parent.cp.linePanel.colorRampList.setSelectedIndex(comboBox.getSelectedIndex());
				if (owner == POINT)
					parent.cp.pointPanel.colorRampList.setSelectedIndex(comboBox.getSelectedIndex());
				if (owner == VECTOR)
					parent.cp.vectorPanel.colorRampList.setSelectedIndex(comboBox.getSelectedIndex());
				mom.setVisible(false);
			}
		});

		add(comboBox, "cell 0 0 2 1,growx");

		add(colLabel, "flowx,cell 0 6,alignx center");
		colButton.setMinimumSize(new Dimension(50, 20));

		add(colButton, "cell 0 6,alignx center");
		colButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editPoint();
			}
		});
		add(locLabel, "flowx,cell 1 6");

		add(textField, "cell 1 6");

		add(label, "cell 1 6");
		add(del, "cell 2 6,growx");
		del.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				delPoint();
			}
		});

		poly.addPoint(0, 0);
		poly.addPoint(5, 5);
		poly.addPoint(-5, 5);

		square.addPoint(-5, 5);
		square.addPoint(5, 5);
		square.addPoint(5, 15);
		square.addPoint(-5, 15);

		gradient.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				selectPoint(e.getX(), e.getY());
				repaint(0);

				if (e.getClickCount() == 2) {
					editPoint();
				}
			}
		});

		gradient.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				movePoint(e.getX(), e.getY());
				repaint(0);
			}

			public void mouseMoved(MouseEvent e) {
			}
		});
	}

	public void reBuildList() {
		comboBox.setModel(new DefaultComboBoxModel(parent.colors.largeRampList.toArray()));
	}

	public void setSelected() {
		if (owner == LINE)
			comboBox.setSelectedIndex(parent.data.selectedLineSwatch);
		if (owner == POINT)
			comboBox.setSelectedIndex(parent.data.selectedPointSwatch);
		if (owner == VECTOR)
			comboBox.setSelectedIndex(parent.data.selectedVectorSwatch);
		if (owner == BOUNDARY)
			comboBox.setSelectedIndex(parent.data.selectedVectorSwatch);
	}

	private void convertSwatch(ColourTable colourTable) {
		list = new ArrayList<ControlPoint>();
		currentSwatch = colourTable;
		Vector<ColourRule> rules = currentSwatch.getColourRules();
		for (int i = 1; i < rules.size(); i++) {
			ColourRule rule = rules.get(i);
			String string = Integer.toHexString(rule.getlColour());
			string = string.substring(2, string.length());
			Color color = Color.decode("#" + string);
			list.add(new ControlPoint(color, rule.getlIndex()));
		}
		repaint();
	}

	public void addActionListener(ActionListener listener) {
		listeners.add(listener);
	}

	public void removeActionListener(ActionListener listener) {
		listeners.remove(listener);
	}

	private void fireUpdate() {
		currentSwatch = new ColourTable();
		for (int i = 0; i < list.size(); i++) {
			ControlPoint now = (ControlPoint) list.get(i);
			currentSwatch.addContinuousColourRule(now.pos, now.col.getRGB());
		}
		repaint();
		ActionEvent event = new ActionEvent(this, 0, "");
		for (int i = 0; i < listeners.size(); i++) {
			((ActionListener) listeners.get(i)).actionPerformed(event);
		}
	}

	private boolean checkPoint(int mx, int my, ControlPoint pt) {
		int dx = (int) Math.abs((10 + (width * pt.pos)) - mx);
		int dy = Math.abs((y + barHeight + 7) - my);

		if ((dx < 5) && (dy < 7)) {
			return true;
		}

		return false;
	}

	private void addPoint() {
		ControlPoint point = new ControlPoint(colButton.getBackground(), 0.5f);
		for (int i = 0; i < list.size() - 1; i++) {
			ControlPoint now = (ControlPoint) list.get(i);
			ControlPoint next = (ControlPoint) list.get(i + 1);
			if ((now.pos <= 0.5f) && (next.pos >= 0.5f)) {
				list.add(i + 1, point);
				break;
			}

		}
		selected = point;
		sortPoints();
		repaint(0);

		fireUpdate();
	}

	private void sortPoints() {
		final ControlPoint firstPt = (ControlPoint) list.get(0);
		final ControlPoint lastPt = (ControlPoint) list.get(list.size() - 1);
		Comparator<Object> compare = new Comparator<Object>() {
			public int compare(Object first, Object second) {
				if (first == firstPt) {
					return -1;
				}
				if (second == lastPt) {
					return -1;
				}

				float a = ((ControlPoint) first).pos;
				float b = ((ControlPoint) second).pos;
				return (int) ((a - b) * 10000);
			}
		};
		Collections.sort(list, compare);
	}

	private void editPoint() {
		if (selected == null) {
			Color color = null;
			Color col = JColorChooser.showDialog(this, "Select Color", color);
			if (col != null) {
				colButton.setBackground(col);
			}
			return;
		}
		Color col = JColorChooser.showDialog(this, "Select Color", selected.col);
		if (col != null) {
			selected.col = col;
			colButton.setBackground(col);
			repaint(0);
			fireUpdate();
		}
	}

	private void selectPoint(int mx, int my) {
		if (!isEnabled()) {
			return;
		}

		for (int i = 1; i < list.size() - 1; i++) {
			if (checkPoint(mx, my, (ControlPoint) list.get(i))) {
				selected = (ControlPoint) list.get(i);
				textField.setText(df.format(selected.pos * 100));
				colButton.setBackground(selected.col);
				return;
			}
		}
		if (checkPoint(mx, my, (ControlPoint) list.get(0))) {
			selected = (ControlPoint) list.get(0);
			textField.setText(df.format(selected.pos * 100));
			colButton.setBackground(selected.col);
			return;
		}
		if (checkPoint(mx, my, (ControlPoint) list.get(list.size() - 1))) {
			selected = (ControlPoint) list.get(list.size() - 1);
			textField.setText(df.format(selected.pos * 100));
			colButton.setBackground(selected.col);
			return;
		} else {
			// click to add like ps
			addPoint();
			movePoint(mx, my);
			return;
		}
	}

	private void delPoint() {
		if (!isEnabled()) {
			return;
		}

		if (selected == null) {
			return;
		}
		if (list.indexOf(selected) == 0) {
			return;
		}
		if (list.indexOf(selected) == list.size() - 1) {
			return;
		}

		list.remove(selected);
		sortPoints();
		repaint(0);
		fireUpdate();
	}

	private void movePoint(int mx, int my) {
		if (!isEnabled()) {
			return;
		}

		// drag down to delete like ps
		if (my > 75) {
			delPoint();
			return;
		}

		if (selected == null) {
			return;
		}
		if (list.indexOf(selected) == 0) {
			return;
		}
		if (list.indexOf(selected) == list.size() - 1) {
			return;
		}

		float newPos = (mx - 10) / (float) width;
		newPos = Math.min(1, newPos);
		newPos = Math.max(0, newPos);

		selected.pos = newPos;
		textField.setText(df.format(selected.pos * 100));
		sortPoints();
		fireUpdate();
	}

	public class Gradient extends JComponent {

		@Override
		public void paintComponent(Graphics g1d) {

			Graphics2D g = (Graphics2D) g1d;
			width = getWidth() - 20;
			x = 10;
			y = 20;
			barHeight = 27;
			for (int s = 0; s < width; s++) {
				Rectangle rect = new Rectangle(s + x, y, 1, barHeight);
				String string = Integer.toHexString(currentSwatch.findColour((float) s / (width)));
				string = string.substring(2, string.length());
				Color color = Color.decode("#" + string);
				g.setPaint(color);
				g.fill(rect);
			}

			g.setColor(Color.black);
			g.drawRect(10, y, width, barHeight - 1);

			for (int i = 0; i < list.size(); i++) {
				ControlPoint pt = (ControlPoint) list.get(i);
				g.translate(10 + (width * pt.pos), y + barHeight);

				if (pt == selected) {
					// g.drawLine(-5, 12, 5, 12);
					g.setColor(Color.DARK_GRAY);
					g.fillPolygon(poly);
				}
				g.setColor(pt.col);
				g.fillPolygon(square);
				g.setColor(Color.black);
				g.drawPolygon(square);
				g.drawPolygon(poly);

				g.translate(-10 - (width * pt.pos), -y - barHeight);
			}
		}
	}

	public void addPoint(float pos, Color col) {
		ControlPoint point = new ControlPoint(col, pos);
		for (int i = 0; i < list.size() - 1; i++) {
			ControlPoint now = (ControlPoint) list.get(i);
			ControlPoint next = (ControlPoint) list.get(i + 1);
			if ((now.pos <= 0.5f) && (next.pos >= 0.5f)) {
				list.add(i + 1, point);
				break;
			}
		}
		repaint(0);
	}

	public void setStart(Color col) {
		((ControlPoint) list.get(0)).col = col;
		repaint(0);
	}

	public void setEnd(Color col) {
		((ControlPoint) list.get(list.size() - 1)).col = col;
		repaint(0);
	}

	public void clearPoints() {
		for (int i = 1; i < list.size() - 1; i++) {
			list.remove(1);
		}

		repaint(0);
		fireUpdate();
	}

	public int getControlPointCount() {
		return list.size();
	}

	public float getPointPos(int index) {
		return ((ControlPoint) list.get(index)).pos;
	}

	public Color getColor(int index) {
		return ((ControlPoint) list.get(index)).col;
	}

	public class ControlPoint {

		public Color col;
		public float pos;

		private ControlPoint(Color col, float pos) {
			this.col = col;
			this.pos = pos;
		}
	}

}
