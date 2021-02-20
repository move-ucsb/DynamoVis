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

import javax.swing.JPanel;

import main.DesktopPane;
import main.SketchData;

import net.miginfocom.swing.MigLayout;

import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class LegendPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	DesktopPane parent;
	SketchData data;

	WideComboBox fontChooser;
	JComboBox<Integer> size;

	public LegendPanel(DesktopPane father) {
		parent = father;
		data = parent.data;

		setLayout(new MigLayout("insets 0", "[50%][][][grow]", "[][][][][][]"));

		JLabel lblFont = new JLabel("Font");
		lblFont.setForeground(Color.BLACK);
		lblFont.setFont(new Font("Arial", Font.PLAIN, 8));
		lblFont.setBackground(Color.LIGHT_GRAY);
		this.add(lblFont, "cell 0 0");

		fontChooser = new WideComboBox(parent.fonts);
		// fontChooser.setEditable(true);
		fontChooser.setMaximumSize(new Dimension(80, 200));
		fontChooser.setForeground(Color.BLACK);
		fontChooser.setBackground(UIManager.getColor("CheckBox.background"));
		fontChooser.setFont(new Font("Arial", Font.PLAIN, 9));
		this.add(fontChooser, "cell 0 1 2 1,grow");
		fontChooser.setSelectedItem("Arial");
		fontChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WideComboBox temp = (WideComboBox) e.getSource();
				String name = (String) temp.getSelectedItem();
				if (name != null) {
					parent.sketch.legend.setFont(name, (Integer) size.getSelectedItem());
				}
			}
		});

		size = new JComboBox<Integer>();
		size.setForeground(Color.BLACK);
		size.setBackground(UIManager.getColor("CheckBox.background"));
		size.setFont(new Font("Arial", Font.PLAIN, 9));
		this.add(size, "cell 2 1,growx");
		for (int i = 8; i <= 18; i++) {
			size.addItem(i);
		}
		size.setSelectedIndex(2);
		size.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox<?> temp = (JComboBox<?>) e.getSource();
				int size = (Integer) temp.getSelectedItem();
				parent.sketch.legend.setFont((String) fontChooser.getSelectedItem(), size);
			}
		});

		JButton btnNewButton = new JButton("Unlock");
		btnNewButton.setIconTextGap(0);
		btnNewButton.setHorizontalTextPosition(SwingConstants.LEFT);
		btnNewButton.setForeground(Color.BLACK);
		btnNewButton.setBackground(new Color(227, 227, 227));
		btnNewButton.setFont(new Font("Arial", Font.PLAIN, 9));
		btnNewButton.setMargin(new Insets(0, 0, 0, 0));
		btnNewButton.setFont(new Font("Arial", Font.PLAIN, 9));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JButton button = (JButton) evt.getSource();
				data.legendLocked = !data.legendLocked;
				if (data.legendLocked) {
					button.setText("Unlock");
					parent.sketch.register();
				} else {
					button.setText("Lock");
					parent.sketch.unregister();
				}
			}
		});

		final JButton btnNewButton_1 = new JButton("");
		add(btnNewButton_1, "cell 3 1,grow");
		// btnNewButton_1.setMinimumSize(new Dimension(15,15));
		btnNewButton_1.setBackground(parent.colors.getLegendColor());
		btnNewButton_1.setMargin(new Insets(0, 0, 0, 0));
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				final JColorChooser colorChooser = new JColorChooser();
				JDialog maxDialog = JColorChooser.createDialog((Component) evt.getSource(), "Pick a Color", true, // modal
						colorChooser, new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								parent.colors.setLegendColor(colorChooser.getColor());
								btnNewButton_1.setBackground(parent.colors.getLegendColor());
								parent.sketch.legend.setFontColor(parent.colors.getLegendColor().getRGB());

							}
						}, new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent event) {
							}
						});
				maxDialog.setVisible(true);
			}
		});
		this.add(btnNewButton, "cell 0 3 4 1,growx");

		JButton resetButton = new JButton("Reset");
		resetButton.setIconTextGap(0);
		resetButton.setHorizontalTextPosition(SwingConstants.LEFT);
		resetButton.setForeground(Color.BLACK);
		resetButton.setBackground(new Color(227, 227, 227));
		resetButton.setFont(new Font("Arial", Font.PLAIN, 9));
		resetButton.setMargin(new Insets(0, 0, 0, 0));
		resetButton.setFont(new Font("Arial", Font.PLAIN, 9));
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				parent.sketch.resetLegend();
				fontChooser.setSelectedItem("Arial");
				size.setSelectedIndex(2);
				parent.colors.setLegendColor(Color.WHITE);
				btnNewButton_1.setBackground(parent.colors.getLegendColor());
				parent.sketch.legend.setFontColor(parent.colors.getLegendColor().getRGB());
			}
		});
		this.add(resetButton, "cell 0 4 4 1,growx");

		JButton btnSave = new JButton("Save");
		btnSave.setMargin(new Insets(0, 0, 0, 0));
		btnSave.setIconTextGap(0);
		btnSave.setHorizontalTextPosition(SwingConstants.LEFT);
		btnSave.setForeground(Color.BLACK);
		btnSave.setFont(new Font("Arial", Font.PLAIN, 9));
		btnSave.setBackground(new Color(227, 227, 227));
		add(btnSave, "cell 0 5,growx");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new File("./config/legend"));
				chooser.setAcceptAllFileFilterUsed(false);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Legend File", "xml", "XML");
				chooser.addChoosableFileFilter(filter);
				int retrival = chooser.showSaveDialog(null);
				if (retrival == JFileChooser.APPROVE_OPTION) {
					try {
						String string = chooser.getSelectedFile().getAbsolutePath();
						if (!string.endsWith(".xml")) {
							string = string + ".xml";
						}
						OutputStream out = new FileOutputStream(string);
						parent.sketch.legend.writeFile(out);
						System.out.println("Saved Legend Layout: " + string);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});

		JButton btnLoad = new JButton("Load");
		btnLoad.setMargin(new Insets(0, 0, 0, 0));
		btnLoad.setIconTextGap(0);
		btnLoad.setHorizontalTextPosition(SwingConstants.LEFT);
		btnLoad.setForeground(Color.BLACK);
		btnLoad.setFont(new Font("Arial", Font.PLAIN, 9));
		btnLoad.setBackground(new Color(227, 227, 227));
		add(btnLoad, "cell 1 5 3 1,growx");
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new File("./config/legend"));
				chooser.setAcceptAllFileFilterUsed(false);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Legend File", "xml", "XML");
				chooser.addChoosableFileFilter(filter);
				int retrival = chooser.showOpenDialog(null);
				if (retrival == JFileChooser.APPROVE_OPTION) {
					try {
						InputStream in = new FileInputStream(chooser.getSelectedFile());
						parent.sketch.legend.readFile(in);
						System.out.println("Loaded Legend Layout: " + chooser.getSelectedFile());
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});

	}
}
