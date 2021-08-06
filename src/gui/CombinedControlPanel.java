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
	// boolean ghost = false;
	LinePanel linePanel;
	PointPanel pointPanel;
	VectorPanel vectorPanel;
	// GhostPanel ghostPanel;

	String formattedLabel(String str, boolean flag) {
		String downArrowLabel = "\u25BC   "+str;
		String   upArrowLabel = "\u25B2   "+str;
		return (flag?downArrowLabel:upArrowLabel);
	}

	public CombinedControlPanel(DesktopPane father) {
		parent = father;
		setLayout(new MigLayout("insets 10", "[145!]", "[]10[]10[]10[]10[]10[]10[]10[]10[]10[]10[]10[]10[]"));
		add(parent.controlPanel, "cell 0 0,growx");

		linePanel = new LinePanel(parent);
		pointPanel = new PointPanel(parent);
		vectorPanel = new VectorPanel(parent);
		// ghostPanel = new GhostPanel(parent);

		JSeparator separator = new JSeparator();
		add(separator, "cell 0 1,growx");

		// Tracks -- Added Underlay(Ghost) here
		JLabel linelabel = new JLabel(formattedLabel("Tracks", line));
		linelabel.setForeground(Color.BLACK);
		linelabel.setFont(new Font("Arial", Font.BOLD, 10));
		linelabel.setBackground(Color.LIGHT_GRAY);
		add(linelabel, "cell 0 2");
		linelabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (line) {
					remove(linePanel);
				} else {
					add(linePanel, "cell 0 3,grow");
				}
				revalidate();
				parent.controlContainer.pack();
				line = !line;
				((JLabel) e.getSource()).setText(formattedLabel("Tracks", line));
			}
		});
		if (line){
			add(linePanel, "cell 0 3,grow");
		}

		JSeparator separator1 = new JSeparator();
		add(separator1, "cell 0 4,growx");

		JLabel pointlabel = new JLabel(formattedLabel("Points", point));
		pointlabel.setForeground(Color.BLACK);
		pointlabel.setFont(new Font("Arial", Font.BOLD, 10));
		pointlabel.setBackground(Color.LIGHT_GRAY);
		add(pointlabel, "cell 0 5");
		pointlabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (point) {
					remove(pointPanel);
				} else {
					add(pointPanel, "cell 0 6,grow");
				}
				revalidate();
				parent.controlContainer.pack();
				point = !point;
				((JLabel) e.getSource()).setText(formattedLabel("Points", point));
			}
		});

		JSeparator separator2 = new JSeparator();
		add(separator2, "cell 0 7,growx");

		JLabel vectorlabel = new JLabel(formattedLabel("Vectors", vector));
		vectorlabel.setForeground(Color.BLACK);
		vectorlabel.setFont(new Font("Arial", Font.BOLD, 10));
		vectorlabel.setBackground(Color.LIGHT_GRAY);
		add(vectorlabel, "cell 0 8");
		vectorlabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (vector) {
					remove(vectorPanel);
				} else {
					add(vectorPanel, "cell 0 9,grow");
				}
				revalidate();
				parent.controlContainer.pack();
				vector = !vector;
				((JLabel) e.getSource()).setText(formattedLabel("Vectors", vector));
			}
		});

		JSeparator separator3 = new JSeparator();
		add(separator3, "cell 0 10,growx");

		JLabel legendlabel = new JLabel(formattedLabel("Legend", parent.legend));
		legendlabel.setForeground(Color.BLACK);
		legendlabel.setFont(new Font("Arial", Font.BOLD, 10));
		legendlabel.setBackground(Color.LIGHT_GRAY);
		add(legendlabel, "cell 0 11");
		legendlabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (parent.legend) {
					remove(parent.legendPanel);
				} else {
					add(parent.legendPanel, "cell 0 12,grow");
				}
				revalidate();
				parent.controlContainer.pack();
				parent.legend = !parent.legend;
				((JLabel) e.getSource()).setText(formattedLabel("Legend", parent.legend));
			}
		});

		revalidate();
		parent.controlContainer.pack();
	}
}
