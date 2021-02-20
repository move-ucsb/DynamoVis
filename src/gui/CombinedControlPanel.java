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
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import main.DesktopPane;
import net.miginfocom.swing.MigLayout;

import javax.swing.JSeparator;
import javax.swing.JLabel;

public class CombinedControlPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DesktopPane parent;
	boolean line = true;
	boolean point = false;
	boolean vector = false;
	boolean ghost = false;
	LinePanel linePanel;
	PointPanel pointPanel;
	VectorPanel vectorPanel;
	GhostPanel ghostPanel;

	public CombinedControlPanel(DesktopPane father) {
		parent = father;
		setLayout(new MigLayout("insets 10", "[145!]", "[]10[]10[]10[]10[]10[]10[]10[]10[]10[]10[]10[]10[]"));
		add(parent.controlPanel, "cell 0 0,growx");

		linePanel = new LinePanel(parent);
		pointPanel = new PointPanel(parent);
		vectorPanel = new VectorPanel(parent);
		ghostPanel = new GhostPanel(parent);

		JSeparator separator = new JSeparator();
		add(separator, "cell 0 1,growx");

		JLabel linelabel = new JLabel("Tracks   \u25BC");
		linelabel.setForeground(Color.BLACK);
		linelabel.setFont(new Font("Arial", Font.PLAIN, 8));
		linelabel.setBackground(Color.LIGHT_GRAY);
		add(linelabel, "cell 0 2");
		linelabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (line) {
					remove(linePanel);
					revalidate();
					parent.cpContainer.pack();
					((JLabel) e.getSource()).setText("Tracks   \u25BC");
				} else {
					add(linePanel, "cell 0 3,growx");
					revalidate();
					parent.cpContainer.pack();
					((JLabel) e.getSource()).setText("Tracks   \u25B2");
				}
				line = !line;
			}
		});

		JSeparator separator1 = new JSeparator();
		add(separator1, "cell 0 4,growx");

		JLabel pointlabel = new JLabel("Points   \u25BC");
		pointlabel.setForeground(Color.BLACK);
		pointlabel.setFont(new Font("Arial", Font.PLAIN, 8));
		pointlabel.setBackground(Color.LIGHT_GRAY);
		add(pointlabel, "cell 0 5");
		pointlabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (point) {
					remove(pointPanel);
					revalidate();
					parent.cpContainer.pack();
					((JLabel) e.getSource()).setText("Points   \u25BC");
				} else {
					add(pointPanel, "cell 0 6,growx");
					revalidate();
					parent.cpContainer.pack();
					((JLabel) e.getSource()).setText("Points   \u25B2");
				}
				point = !point;
			}
		});

		JSeparator separator2 = new JSeparator();
		add(separator2, "cell 0 7,growx");

		JLabel vectorlabel = new JLabel("Vectors   \u25BC");
		vectorlabel.setForeground(Color.BLACK);
		vectorlabel.setFont(new Font("Arial", Font.PLAIN, 8));
		vectorlabel.setBackground(Color.LIGHT_GRAY);
		add(vectorlabel, "cell 0 8");
		vectorlabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (vector) {
					remove(vectorPanel);
					revalidate();
					parent.cpContainer.pack();
					((JLabel) e.getSource()).setText("Vectors   \u25BC");
				} else {
					add(vectorPanel, "cell 0 9,growx");
					revalidate();
					parent.cpContainer.pack();
					((JLabel) e.getSource()).setText("Vectors   \u25B2");
				}
				vector = !vector;
			}
		});

		JSeparator separator3 = new JSeparator();
		add(separator3, "cell 0 10,growx");

		JLabel legendlabel = new JLabel("Legend   \u25BC");
		legendlabel.setForeground(Color.BLACK);
		legendlabel.setFont(new Font("Arial", Font.PLAIN, 8));
		legendlabel.setBackground(Color.LIGHT_GRAY);
		add(legendlabel, "cell 0 11");
		legendlabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (parent.legend) {
					remove(parent.legendPanel);
					revalidate();
					parent.cpContainer.pack();
					((JLabel) e.getSource()).setText("Legend   \u25BC");
				} else {
					add(parent.legendPanel, "cell 0 12,growx");
					revalidate();
					parent.cpContainer.pack();
					((JLabel) e.getSource()).setText("Legend   \u25B2");
				}
				parent.legend = !parent.legend;
			}
		});

		JSeparator separator4 = new JSeparator();
		add(separator4, "cell 0 13,growx");

		JLabel ghostLabel = new JLabel("Underlay   \u25BC");
		ghostLabel.setForeground(Color.BLACK);
		ghostLabel.setFont(new Font("Arial", Font.PLAIN, 8));
		ghostLabel.setBackground(Color.LIGHT_GRAY);
		add(ghostLabel, "cell 0 14");
		ghostLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (ghost) {
					remove(ghostPanel);
					revalidate();
					parent.cpContainer.pack();
					((JLabel) e.getSource()).setText("Underlay   \u25BC");
				} else {
					add(ghostPanel, "cell 0 15,growx");
					revalidate();
					parent.cpContainer.pack();
					((JLabel) e.getSource()).setText("Underlay   \u25B2");
				}
				ghost = !ghost;
			}
		});

		if (line) {
			add(linePanel, "cell 0 3");
			linelabel.setText("Tracks   \u25B2");
		}

		if (parent.legend) {
			add(parent.legendPanel, "cell 0 12,growx");
			legendlabel.setText("Legend   \u25B2");
		}

		revalidate();
		parent.cpContainer.pack();

	}
}
