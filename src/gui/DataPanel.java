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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import main.DesktopPane;
import data.LoadDataToJTable;
import utils.Field;
import utils.Track;
import net.miginfocom.swing.MigLayout;
import de.fhpotsdam.unfolding.data.Feature;

import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JComboBox;

import org.joda.time.Period;

public class DataPanel extends JPanel implements ActionListener {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	public String dataFilePath;
	public JFileChooser dataChooser;
	public List<Feature> dataPoints;
	private JTextField dataPathField;
	private JButton openFile;
	public JTable fieldsTable;
	public CustomTableModel fieldModel;
	public CustomTableModel tagModel;
	private JTable tagsTable;
	private JTextField titleField;
	private JComboBox<Dimension> animationSize;
	public JButton okButton;
	private JButton cancelButton;
	private JButton helpButton;
	public DesktopPane parent;
	private String path;
	private String file;
	private JButton btnDeselect1;
	private JButton btnSelectAll1;
	private JButton btnDeselect;
	private JButton btnSelectAll;
	private JSpinner secondsSpinner;
	private JSpinner minutesSpinner;
	private JSpinner hoursSpinner;
	public Period interval;
	private JButton btnRoundRange;
	private JLabel lblTitle;
	private JLabel lblInterval;
	private JLabel lblSize;
	public boolean override;
	boolean noSelectedSpinners = false;
	boolean noSelectedTags = false;

	// enables/disables all components in the view
	public void SetComponentsEnabled(boolean enabled) {
		// misc
		dataChooser.setEnabled(enabled);
		animationSize.setEnabled(enabled);

		// fields
		dataPathField.setEnabled(enabled);
		titleField.setEnabled(enabled);

		// tables
		fieldsTable.setEnabled(enabled);
		tagsTable.setEnabled(enabled);

		// buttons
		openFile.setEnabled(enabled);
		helpButton.setEnabled(enabled);
		cancelButton.setEnabled(enabled);
		okButton.setEnabled(enabled);
		btnDeselect1.setEnabled(enabled);
		btnSelectAll1.setEnabled(enabled);
		btnDeselect.setEnabled(enabled);
		btnSelectAll.setEnabled(enabled);
		btnRoundRange.setEnabled(enabled);

		// spinner
		secondsSpinner.setEnabled(enabled);
		minutesSpinner.setEnabled(enabled);
		hoursSpinner.setEnabled(enabled);
	}

	@SuppressWarnings("serial")
	public DataPanel(DesktopPane father) {
		parent = father;
		setLayout(new MigLayout("", "[233.00:n,grow][][]",
				"[][][200px:n,grow][20px:n][][200px:n,grow][20px:n][][][][][grow][grow]"));

		dataPathField = new JTextField();
		add(dataPathField, "cell 0 0 2 1,growx");

		openFile = new JButton("Load Data");
		openFile.setIconTextGap(0);
		openFile.setHorizontalTextPosition(SwingConstants.LEFT);
		openFile.setForeground(Color.BLACK);
		openFile.setBackground(SystemColor.controlHighlight);
		openFile.setFont(new Font("Arial", Font.PLAIN, 9));
		openFile.setMargin(new Insets(0, 0, 0, 0));
		openFile.setFont(new Font("Arial", Font.PLAIN, 9));
		add(openFile, "cell 2 0,grow");
		openFile.setActionCommand("openFile");
		openFile.addActionListener(this);

		String[] fieldTableCol = { "Field Name", "Alias", "Unit", "Min", "Max", "Enable" };
		Integer[] fieldTableColEditable = { 1, 2, 3, 4, 5 };
		fieldModel = new CustomTableModel();
		fieldModel.setCol(fieldTableCol);
		fieldModel.setEditableCol(fieldTableColEditable);
		fieldsTable = new JTable(fieldModel) {
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);

				if(!fieldsTable.isEnabled()) {
					c.setEnabled(false);
					c.setBackground(Color.WHITE);
				}
				else {
					c.setEnabled(true);
					if (!isRowSelected(row)) {
						c.setBackground(getBackground());
						int modelRow = convertRowIndexToModel(row);
						boolean type = (Boolean) getModel().getValueAt(modelRow, 5);
						if (type) {
							c.setBackground(Color.LIGHT_GRAY);
						} else {
							c.setBackground(Color.WHITE);
						}
					}
				}

				return c;
			}
		};

		// fieldsTable.setDefaultRenderer(Boolean.class, new CustomRenderer());
		fieldsTable.setAutoCreateRowSorter(true);
		fieldsTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
		fieldsTable.setFillsViewportHeight(true);
		fieldsTable.getColumnModel().getColumn(0).setPreferredWidth(120);
		fieldsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
		fieldsTable.getColumnModel().getColumn(2).setPreferredWidth(20);
		fieldsTable.getColumnModel().getColumn(3).setPreferredWidth(10);
		fieldsTable.getColumnModel().getColumn(4).setPreferredWidth(10);
		fieldsTable.getColumnModel().getColumn(5).setPreferredWidth(10);
		fieldsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		JScrollPane fieldsScrollPane = new JScrollPane(fieldsTable);
		add(fieldsScrollPane, "cell 0 2 3 1,grow");

		btnRoundRange = new JButton("Round Range");
		btnRoundRange.setMargin(new Insets(0, 0, 0, 0));
		btnRoundRange.setIconTextGap(0);
		btnRoundRange.setHorizontalTextPosition(SwingConstants.LEFT);
		btnRoundRange.setForeground(Color.BLACK);
		btnRoundRange.setFont(new Font("Arial", Font.PLAIN, 9));
		btnRoundRange.setBackground(SystemColor.controlHighlight);
		add(btnRoundRange, "flowx,cell 0 3 3 1,alignx right,growy");
		btnRoundRange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for (int i = 1; i < fieldsTable.getRowCount(); i++) {
					for (int o = 3; o < 5; o++) {
						if (o == 3) {
							float min = (Float) fieldsTable.getValueAt(i, o);
							min = (float) Math.floor(min);
							fieldsTable.setValueAt(min, i, o);
						} else if (o == 4) {
							float max = (Float) fieldsTable.getValueAt(i, o);
							max = (float) Math.ceil(max);
							fieldsTable.setValueAt(max, i, o);
						}
					}
				}
			}
		});

		btnDeselect1 = new JButton("Deselect All");
		btnDeselect1.setIconTextGap(0);
		btnDeselect1.setHorizontalTextPosition(SwingConstants.LEFT);
		btnDeselect1.setForeground(Color.BLACK);
		btnDeselect1.setBackground(SystemColor.controlHighlight);
		btnDeselect1.setFont(new Font("Arial", Font.PLAIN, 9));
		btnDeselect1.setMargin(new Insets(0, 0, 0, 0));
		btnDeselect1.setFont(new Font("Arial", Font.PLAIN, 9));
		add(btnDeselect1, "cell 0 3 3 1,alignx right,growy");
		btnDeselect1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for (int i = 1; i < fieldsTable.getRowCount(); i++) {
					fieldsTable.setValueAt(false, i, 5);
				}
				fieldsTable.repaint();
			}
		});

		btnSelectAll1 = new JButton("Select All");
		btnSelectAll1.setIconTextGap(0);
		btnSelectAll1.setHorizontalTextPosition(SwingConstants.LEFT);
		btnSelectAll1.setForeground(Color.BLACK);
		btnSelectAll1.setBackground(SystemColor.controlHighlight);
		btnSelectAll1.setFont(new Font("Arial", Font.PLAIN, 9));
		btnSelectAll1.setMargin(new Insets(0, 0, 0, 0));
		btnSelectAll1.setFont(new Font("Arial", Font.PLAIN, 9));
		btnSelectAll1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for (int i = 1; i < fieldsTable.getRowCount(); i++) {
					fieldsTable.setValueAt(true, i, 5);
				}
				fieldsTable.repaint();
			}
		});
		add(btnSelectAll1, "cell 0 3 3 1,alignx right,growy");

		String[] tagTableCol = { "Tag", "Start", "End", "Interval (min)", "Enable" };
		Integer[] tagTableColEditable = { 4 };
		tagModel = new CustomTableModel();
		tagModel.setCol(tagTableCol);
		tagModel.setEditableCol(tagTableColEditable);
		tagsTable = new JTable(tagModel) {
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);

				if(!tagsTable.isEnabled()) {
					c.setEnabled(false);
					c.setBackground(Color.WHITE);
				}
				else {
					c.setEnabled(true);
					if (!isRowSelected(row)) {
						c.setBackground(getBackground());
						int modelRow = convertRowIndexToModel(row);
						boolean type = (Boolean) getModel().getValueAt(modelRow, 4);
						if (type) {
							c.setBackground(Color.LIGHT_GRAY);
						} else {
							c.setBackground(Color.WHITE);
						}
					}
				}

				return c;
			}
		};
		tagsTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
		tagsTable.setFillsViewportHeight(true);
		tagsTable.setAutoCreateRowSorter(true);
		tagsTable.getColumnModel().getColumn(0).setPreferredWidth(10);
		tagsTable.getColumnModel().getColumn(1).setPreferredWidth(30);
		tagsTable.getColumnModel().getColumn(2).setPreferredWidth(30);
		tagsTable.getColumnModel().getColumn(3).setPreferredWidth(40);
		tagsTable.getColumnModel().getColumn(4).setPreferredWidth(10);
		tagsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		JScrollPane tagsScrollPane = new JScrollPane(tagsTable);
		add(tagsScrollPane, "cell 0 5 3 1,grow");
		tagsTable.getModel().addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				checkOK();
			}
		});

		btnDeselect = new JButton("Deselect All");
		btnDeselect.setIconTextGap(0);
		btnDeselect.setHorizontalTextPosition(SwingConstants.LEFT);
		btnDeselect.setForeground(Color.BLACK);
		btnDeselect.setBackground(SystemColor.controlHighlight);
		btnDeselect.setFont(new Font("Arial", Font.PLAIN, 9));
		btnDeselect.setMargin(new Insets(0, 0, 0, 0));
		btnDeselect.setFont(new Font("Arial", Font.PLAIN, 9));
		add(btnDeselect, "flowx,cell 0 6 3 1,alignx right,growy");
		btnDeselect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for (int i = 0; i < tagsTable.getRowCount(); i++) {
					tagsTable.setValueAt(false, i, 4);
				}
				tagsTable.repaint();
			}
		});

		btnSelectAll = new JButton("Select All");
		btnSelectAll.setIconTextGap(0);
		btnSelectAll.setHorizontalTextPosition(SwingConstants.LEFT);
		btnSelectAll.setForeground(Color.BLACK);
		btnSelectAll.setBackground(SystemColor.controlHighlight);
		btnSelectAll.setFont(new Font("Arial", Font.PLAIN, 9));
		btnSelectAll.setMargin(new Insets(0, 0, 0, 0));
		btnSelectAll.setFont(new Font("Arial", Font.PLAIN, 9));
		btnSelectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for (int i = 0; i < tagsTable.getRowCount(); i++) {
					tagsTable.setValueAt(true, i, 4);
				}
				tagsTable.repaint();
			}
		});
		add(btnSelectAll, "cell 0 6 3 1,alignx right,growy");

		titleField = new JTextField();
		titleField.setColumns(10);
		add(titleField, "cell 0 8,growx");

		lblTitle = new JLabel("Title");
		add(lblTitle, "cell 2 8,alignx right,aligny bottom");
		hoursSpinner = new JSpinner();
		hoursSpinner.setMinimumSize(new Dimension(50, 20));
		hoursSpinner
				.setModel(new SpinnerNumberModel(0, 0, 1000, 1)); // new Integer(0), new Integer(0), new Integer(1000), new Integer(1)));
		add(hoursSpinner, "flowx, cell 0 9,alignx right");
		JLabel hours = new JLabel("h");
		add(hours, "cell 0 9,alignx right,aligny bottom");
		hoursSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				checkOK();
			}
		});
		hoursSpinner.setEnabled(false);

		minutesSpinner = new JSpinner();
		minutesSpinner.setMinimumSize(new Dimension(50, 20));
		minutesSpinner
				.setModel(new SpinnerNumberModel(0, 0, 59, 1)); // new Integer(0), new Integer(0), new Integer(1000), new Integer(1)));
		add(minutesSpinner, "cell 0 9,alignx right");
		JLabel minutes = new JLabel("m");
		add(minutes, "cell 0 9,alignx right,aligny bottom");
		minutesSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				checkOK();
			}
		});
		minutesSpinner.setEnabled(false);

		secondsSpinner = new JSpinner();
		secondsSpinner.setMinimumSize(new Dimension(50, 20));
		secondsSpinner
				.setModel(new SpinnerNumberModel(0, 0, 59, 1)); // new Integer(0), new Integer(0), new Integer(1000), new Integer(1)));
		add(secondsSpinner, "cell 0 9,alignx right");
		JLabel seconds = new JLabel("s");
		add(seconds, "cell 0 9,alignx right,aligny bottom");
		secondsSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				checkOK();
			}
		});
		secondsSpinner.setEnabled(false);

		lblInterval = new JLabel("Interval");
		add(lblInterval, "cell 2 9,alignx right,aligny bottom");

		animationSize = new JComboBox<Dimension>();
		add(animationSize, "cell 0 10,growx");
		animationSize.addItem(new Dimension(1920, 1080));
		animationSize.addItem(new Dimension(1600, 900));
		animationSize.addItem(new Dimension(1366, 768));
		animationSize.addItem(new Dimension(1280, 720));
		animationSize.addItem(new Dimension(1024, 576));
		animationSize.setSelectedIndex(3);

		lblSize = new JLabel("Size");
		add(lblSize, "cell 2 10,alignx right,aligny bottom");

		okButton = new JButton("Create Animation");
		okButton.setEnabled(false);
		add(okButton, "cell 0 12 3 1,alignx right,aligny bottom");
		okButton.setActionCommand("ok");
		okButton.addActionListener(this);


		cancelButton = new JButton("Cancel");
		// add(cancelButton, "cell 0 12 3 1,alignx right,aligny bottom");
		// cancelButton.setActionCommand("cancel");
		// cancelButton.addActionListener(this);

		helpButton = new JButton("Help");
		add(helpButton, "cell 0 12 3 1,alignx right,aligny bottom");
		helpButton.setActionCommand("help");
		helpButton.addActionListener(this);
	}

	// format psketch title 
	// TODO: move somewhere better
	public String getSurfaceTitle() {
		if(titleField.getText() != null && path != null) {
			return titleField.getText()+ "  -  "+ path; 
		}
		return "Visualization";
	}

	public void checkSpinners() {
		if (((Integer) hoursSpinner.getValue() == 0) && ((Integer) minutesSpinner.getValue() == 0)
				&& ((Integer) secondsSpinner.getValue() == 0)) {
			noSelectedSpinners = true;
		} else {
			noSelectedSpinners = false;
		}
	}

	public void checkTags() {
		int count = 0;
		// for (int i = 0; i < tagsTable.getRowCount(); i++) {
		for (int i = 0; i < tagModel.getRowCount(); i++) {
			boolean check = (Boolean) tagsTable.getValueAt(i, 4);
			if (check) {
				count++;
			}
			;
		}
		if (count > 0) {
			noSelectedTags = false;
		} else {
			noSelectedSpinners = true;
		}
	}

	public void checkOK() {
		checkSpinners();
		checkTags();
		if (noSelectedSpinners || noSelectedTags) {
			okButton.setEnabled(false);
		} else {
			okButton.setEnabled(true);
		}
	}

	public void actionPerformed(ActionEvent e) {
		if ("openFile".equals(e.getActionCommand())) {
			if ((e.getModifiers() & InputEvent.SHIFT_DOWN_MASK) != 0) {
				override = true;
			} else {
				override = false;
			}
			openData();
		} else if ("cancel".equals(e.getActionCommand())) {
			// parent.dataConfigContainer.setVisible(false);
		} else if ("ok".equals(e.getActionCommand())) {
			doIt();
			// parent.dataConfigContainer.setVisible(false);
		} else if ("help".equals(e.getActionCommand())) {
			DesktopPane.openWebpage(parent.projectWebsite);
		}
	}

	public void openData() {
		File defaultDataDirectiory = new File("./data");
		if(!defaultDataDirectiory.exists()) defaultDataDirectiory = new File("./public-data");
		dataChooser = new JFileChooser(defaultDataDirectiory);
		dataChooser.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("MoveBank Formatted CSV or TXT", "CSV", "csv",
				"TXT", "txt");
		dataChooser.addChoosableFileFilter(filter);
		int returnVal = dataChooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = dataChooser.getSelectedFile().getName();
			path = dataChooser.getSelectedFile().getAbsolutePath();
			dataPathField.setText(path);
			LoadDataToJTable processor = new LoadDataToJTable(parent);
			processor.loadData(path, file);
		}
	}

	public void returned(ArrayList<ArrayList<Object[]>> returnData) {
		fieldModel.setData(returnData.get(0));
		fieldModel.setRestricted(0, 5, true);
		tagModel.setData(returnData.get(1));
		tagModel.setRestricted(0, 0, false);
		titleField.setText((String) returnData.get(2).get(0)[0]);

		interval = (Period) returnData.get(2).get(0)[1];
		interval = interval.normalizedStandard();
		hoursSpinner.setValue(interval.getHours());
		minutesSpinner.setValue(interval.getMinutes());
		secondsSpinner.setValue(interval.getSeconds());

		minutesSpinner.setEnabled(true);
		secondsSpinner.setEnabled(true);
		hoursSpinner.setEnabled(true);
		checkOK();
	}

	private void doIt() {
		// ArrayList<String> selectedFields = new ArrayList<String>();
		// ArrayList<String> editedFieldName = new ArrayList<String>();
		// ArrayList<String> units = new ArrayList<String>();
		// HashMap<String, ArrayList<Float>> minMax = new HashMap<String, ArrayList<Float>>();

		parent.attributes.setIndex(fieldModel.getValueAt(0, 0).toString());

		for (int i = 0; i < fieldModel.getRowCount(); i++) {
			String name = fieldModel.getValueAt(i, 0).toString();
			String alias = fieldModel.getValueAt(i, 1).toString();
			String unit = fieldModel.getValueAt(i, 2).toString();
			float min = (Float) fieldModel.getValueAt(i, 3);
			float max = (Float) fieldModel.getValueAt(i, 4);
			boolean sel = (Boolean) fieldModel.getValueAt(i, 5);

			for (Field field : parent.attributes.getFields()) {
				if (field.getName() == name) {
					field.setAlias(alias);
					field.setUnit(unit);
					field.setMax(max);
					field.setMin(min);
					field.setSelected(sel);
				}
			}
		}

		for (int i = 0; i < tagModel.getRowCount(); i++) {
			for (Entry<String, Track> entry : parent.trackList.entrySet()) {
				if (entry.getKey().equals(tagModel.getValueAt(i, 0).toString())) {
					Track track = entry.getValue();
					track.setVisibility((Boolean) tagModel.getValueAt(i, 4));
				}
			}

		}
		String title = titleField.getText();
		Period period = new Period((Integer) hoursSpinner.getValue(), (Integer) minutesSpinner.getValue(),
				(Integer) secondsSpinner.getValue(), 0);
		Dimension dimension = (Dimension) animationSize.getSelectedItem();
		parent.newData(path, file, title, period, dimension);
	}

	@SuppressWarnings("serial")
	class CustomRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			c.setBackground(new java.awt.Color(255, 72, 72));
			return c;
		}
	}

	@SuppressWarnings("serial")
	class CustomTableModel extends AbstractTableModel {
		private String[] columnNames;
		private Integer[] editableColumns;
		private List<Object[]> data = new ArrayList<Object[]>();
		private boolean[][] restrictedCells;

		public void setCol(String[] col) {
			columnNames = col;
		}

		public void setData(List<Object[]> newData) {
			data = newData;
			fireTableDataChanged();
		}

		public void addData(Object[] line) {
			data.add(line);
			fireTableDataChanged();
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data.size();
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			return data.get(row)[col];
		}

		public Class<?> getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		public void setEditableCol(Integer[] col) {
			editableColumns = col;
		}

		public boolean isCellEditable(int row, int col) {
			if (restrictedCells[row][col] == true) {
				return false;
			} else if (Arrays.asList(editableColumns).contains(col)) {
				return true;
			} else {
				return false;
			}
		}

		public void setRestricted(int row, int col, boolean value) {
			restrictedCells = new boolean[data.size()][columnNames.length];
			restrictedCells[row][col] = value;
			fireTableCellUpdated(row, col);
		}

		public void setValueAt(Object value, int row, int col) {
			data.get(row)[col] = value;
			fireTableCellUpdated(row, col);
		}

	}

}
