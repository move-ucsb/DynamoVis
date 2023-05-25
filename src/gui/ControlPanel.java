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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.DesktopPane;
import main.SketchData;
import net.miginfocom.swing.MigLayout;

import java.awt.Dimension;

public class ControlPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static int openFrameCount = 0;
	static final int xOffset = 30, yOffset = 30;

	public JSlider seek;
	public JFileChooser dataChooser;
	private DesktopPane parent;
	private SketchData data;

	private int lastSeekVal;

	public ControlPanel(DesktopPane father) {
		parent = father;
		data = parent.data;
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		setLayout(new MigLayout("insets 0", "[20px:20px:20px][15.00][20px:20px:20px,fill][][grow,fill][]",
				"[][][][grow,fill][][grow,fill][][][]"));

		JSlider speed = new JSlider();
		speed.setForeground(Color.BLACK);
		speed.setBackground(Color.LIGHT_GRAY);
		speed.setToolTipText("Speed");
		speed.setPaintTicks(true);
		speed.setMajorTickSpacing(1);
		speed.setSnapToTicks(true);
		speed.setMinimum(0);
		speed.setMaximum(10);
		speed.setOrientation(SwingConstants.VERTICAL);
		speed.setInverted(false);
		this.add(speed, "cell 0 0 1 8,alignx center");
		speed.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JSlider slider = (JSlider) evt.getSource();
				int reverseValue = 1 + ((slider.getValue() - 10) * -1); // remap 10-1 to 1-10
				data.speed = reverseValue;
			}
		});
		speed.setValue(7);

		seek = new JSlider();
		seek.setForeground(Color.BLACK);
		seek.setBackground(Color.LIGHT_GRAY);
		seek.setToolTipText("Seek");
		seek.setMajorTickSpacing(5);
		seek.setOrientation(SwingConstants.VERTICAL);
		seek.setValue(data.seek);
		seek.setMinimum(0);
		seek.setMaximum((data.totalTime / data.dataInterval));
		lastSeekVal = seek.getValue();
		this.add(seek, "cell 2 0 1 8,alignx center");
		seek.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JSlider slider = (JSlider) evt.getSource();
				//if value didn't change, don't change marker
				if (slider.getValueIsAdjusting() || data.seek != lastSeekVal) {
					if (data.timeUnit.equals("minutes")) {
						data.currentTime = data.startTime.plusMinutes(slider.getValue() * data.dataInterval);
					} else if (data.timeUnit.equals("seconds")) {
						data.currentTime = data.startTime.plusSeconds(slider.getValue() * data.dataInterval);
					}
					parent.timeLine.setMarker(data.currentTime);
					lastSeekVal = data.seek;
				}
			}
		});
		seek.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				data.mouse = true;
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				data.mouse = false;
			}
		});

		final JButton Pause = new JButton("Play");
		Pause.setIconTextGap(0);
		Pause.setHorizontalTextPosition(SwingConstants.LEFT);
		Pause.setForeground(Color.BLACK);
		Pause.setBackground(new Color(227, 227, 227));
		Pause.setFont(new Font("Arial", Font.PLAIN, 9));
		Pause.setMargin(new Insets(0, 0, 0, 0));
		Pause.setFont(new Font("Arial", Font.PLAIN, 9));
		this.add(Pause, "cell 4 0 2 1,growx");
		Pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JButton button = (JButton) evt.getSource();
				data.pause = !data.pause;
				if (data.pause) {
					button.setText("Play");
				} else {
					button.setText("Pause");

				}
			}
		});

		JButton Reset = new JButton("Stop");
		Reset.setIconTextGap(0);
		Reset.setHorizontalTextPosition(SwingConstants.LEFT);
		Reset.setForeground(Color.BLACK);
		Reset.setBackground(new Color(227, 227, 227));
		Reset.setFont(new Font("Arial", Font.PLAIN, 9));
		Reset.setMargin(new Insets(0, 0, 0, 0));
		this.add(Reset, "cell 4 1 2 1,growx");
		Reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				seek.setValue(0);
				data.pause = true;
				Pause.setText("Play");
			}
		});

		JCheckBox Loop = new JCheckBox("Loop");
		Loop.setForeground(Color.BLACK);
		Loop.setBackground(Color.LIGHT_GRAY);
		Loop.setFont(new Font("Arial", Font.PLAIN, 9));
		this.add(Loop, "cell 4 2 2 1,growx");
		Loop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JCheckBox cb = (JCheckBox) evt.getSource();
				if (cb.isSelected()) {
					data.loop = true;
				} else {
					data.loop = false;
				}
			}
		});

		JButton zoomIn = new JButton("+");
		zoomIn.setMaximumSize(new Dimension(45, 26));
		zoomIn.setIconTextGap(0);
		zoomIn.setHorizontalTextPosition(SwingConstants.LEFT);
		zoomIn.setForeground(Color.BLACK);
		zoomIn.setBackground(new Color(227, 227, 227));
		zoomIn.setFont(new Font("Arial", Font.PLAIN, 9));
		zoomIn.setMargin(new Insets(0, 0, 0, 0));
		zoomIn.setFont(new Font("Arial", Font.PLAIN, 9));
		this.add(zoomIn, "flowy,cell 4 4 2 1,alignx right");
		zoomIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				parent.sketch.zoomIn();
			}
		});

		JButton center = new JButton("Reset");
		center.setMaximumSize(new Dimension(45, 26));
		center.setIconTextGap(0);
		center.setHorizontalTextPosition(SwingConstants.LEFT);
		center.setForeground(Color.BLACK);
		center.setBackground(new Color(227, 227, 227));
		center.setFont(new Font("Arial", Font.PLAIN, 9));
		center.setMargin(new Insets(0, 0, 0, 0));
		center.setFont(new Font("Arial", Font.PLAIN, 9));
		this.add(center, "cell 4 4 2 1,alignx right");
		center.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				parent.sketch.zoomAndPan(data.locations);
			}
		});

		JButton zoomOut = new JButton("-");
		zoomOut.setMaximumSize(new Dimension(45, 26));
		zoomOut.setIconTextGap(0);
		zoomOut.setHorizontalTextPosition(SwingConstants.LEFT);
		zoomOut.setForeground(Color.BLACK);
		zoomOut.setBackground(new Color(227, 227, 227));
		zoomOut.setFont(new Font("Arial", Font.PLAIN, 9));
		zoomOut.setMargin(new Insets(0, 0, 0, 0));
		zoomOut.setFont(new Font("Arial", Font.PLAIN, 9));
		this.add(zoomOut, "cell 4 4 2 1,alignx right");
		zoomOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				parent.sketch.zoomOut();
			}
		});

		JCheckBox Alpha = new JCheckBox("Fade Data");
		Alpha.setForeground(Color.BLACK);
		Alpha.setBackground(Color.LIGHT_GRAY);
		Alpha.setFont(new Font("Arial", Font.PLAIN, 9));
		Alpha.setToolTipText("");
		ToolTipManager.sharedInstance().registerComponent(Alpha);
		this.add(Alpha, "cell 4 6 2 1,growx");
		Alpha.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JCheckBox cb = (JCheckBox) evt.getSource();
				if (cb.isSelected()) {
					data.falloff = true;
				} else {
					data.falloff = false;
				}
			}
		});
		Alpha.setSelected(true);

		JSpinner AlphaValue = new JSpinner();
		AlphaValue.setForeground(Color.BLACK);
		AlphaValue.setBackground(Color.LIGHT_GRAY);
		AlphaValue.setFont(new Font("Arial", Font.PLAIN, 9));
		AlphaValue.setModel(new SpinnerNumberModel(data.alphaMaxHours, 1, data.alphaMaxHours * 100, 1));
		AlphaValue.setToolTipText("");
		ToolTipManager.sharedInstance().registerComponent(AlphaValue);
		this.add(AlphaValue, "flowx,cell 4 7 2 1,growx");
		AlphaValue.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JSpinner spin = (JSpinner) evt.getSource();
				data.alphaMaxHours = (Integer) spin.getValue();
			}
		});

		JLabel label = new JLabel("Speed");
		label.setBackground(Color.LIGHT_GRAY);
		label.setForeground(Color.BLACK);
		label.setFont(new Font("Arial", Font.PLAIN, 8));
		this.add(label, "cell 0 8");

		JLabel lblSeek = new JLabel("Seek");
		lblSeek.setBackground(Color.LIGHT_GRAY);
		lblSeek.setForeground(Color.BLACK);
		lblSeek.setFont(new Font("Arial", Font.PLAIN, 8));
		this.add(lblSeek, "cell 2 8");

		String unit;
		if (data.timeUnit.equals("minutes")) {
			unit = "(h)";
		} else {
			unit = "(m)";
		}
		JLabel lblPointLifehours = new JLabel("Fade Duration " + unit);
		lblPointLifehours.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPointLifehours.setForeground(Color.BLACK);
		lblPointLifehours.setFont(new Font("Arial", Font.PLAIN, 8));
		lblPointLifehours.setBackground(Color.LIGHT_GRAY);
		this.add(lblPointLifehours, "cell 4 8 2 1,alignx right");

	}
}
