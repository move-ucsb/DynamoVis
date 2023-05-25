package gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.joda.time.Months;

import main.DesktopPane;
import main.SketchData;

import net.miginfocom.swing.MigLayout;

public class TimeLinePanel extends JPanel {
    DesktopPane parent;
	SketchData data;
    TimeLinePanel me;

	int numMos =8;
	int numWeeks = 0;
	int numDays = 0;

    public TimeLinePanel(DesktopPane father) {
        parent = father;
		data = parent.data;
		me = this;

        setLayout(new MigLayout("insets 0", "[][][]", "[][][][]"));

        JCheckBox timeRange = new JCheckBox("On/Off");
		timeRange.setForeground(Color.BLACK);
		timeRange.setBackground(Color.LIGHT_GRAY);
		timeRange.setFont(new Font("Arial", Font.PLAIN, 9));
		this.add(timeRange, "cell 0 0, split 2, growx");
		timeRange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				TimeLine myTimeLine = parent.timeLine;
				JCheckBox cb = (JCheckBox) evt.getSource();
				if (cb.isSelected()) {
					data.timeRange = true;
					myTimeLine.addRangeMarkers();
				} else {
					data.timeRange = false;
					myTimeLine.removeRangeMarkers();
					myTimeLine.setTimeSelectableFromSpinners(numMos, numWeeks, numDays);
				}
			}
		});

		JCheckBox moveRangeTogether = new JCheckBox("Lock Range");
		moveRangeTogether.setForeground(Color.BLACK);
		moveRangeTogether.setBackground(Color.LIGHT_GRAY);
		moveRangeTogether.setFont(new Font("Arial", Font.PLAIN, 9));
		this.add(moveRangeTogether, "cell 1 0, growx");
		moveRangeTogether.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
                TimeLine myTimeLine = parent.timeLine;
				JCheckBox cb = (JCheckBox) evt.getSource();
				if (cb.isSelected()) {
					data.moveRangeTogether = true;
                    myTimeLine.setTimeSelectable();
				} else {
					data.moveRangeTogether = false;
					myTimeLine.setTimeSelectableFromSpinners(numMos, numWeeks, numDays);
				}
			}
		});
        moveRangeTogether.setSelected(true);
        
        JLabel lblNumMonths = new JLabel("Months");
		lblNumMonths.setForeground(Color.BLACK);
		lblNumMonths.setFont(new Font("Arial", Font.PLAIN, 8));
		lblNumMonths.setBackground(Color.LIGHT_GRAY);
		this.add(lblNumMonths, "cell 0 2,alignx left,aligny baseline, split 3,growx");


		JLabel lblNumWeeks= new JLabel("Weeks");
		lblNumWeeks.setForeground(Color.BLACK);
		lblNumWeeks.setFont(new Font("Arial", Font.PLAIN, 8));
		lblNumWeeks.setBackground(Color.LIGHT_GRAY);
		this.add(lblNumWeeks, "cell 1 2,alignx left,aligny baseline, growx");


		JLabel lblNumDays= new JLabel("Days");
		lblNumDays.setForeground(Color.BLACK);
		lblNumDays.setFont(new Font("Arial", Font.PLAIN, 8));
		lblNumDays.setBackground(Color.LIGHT_GRAY);
		this.add(lblNumDays, "cell 2 2,alignx left,aligny baseline, growx");

		JSpinner MonthValue = new JSpinner();
		MonthValue.setForeground(Color.BLACK);
		MonthValue.setBackground(Color.LIGHT_GRAY);
		MonthValue.setFont(new Font("Arial", Font.PLAIN, 9));
		int monthsBetween = Months.monthsBetween(data.startTime, data.endTime).getMonths();
		MonthValue.setModel(new SpinnerNumberModel(0, 0, monthsBetween, 1)); 
		MonthValue.setToolTipText("");
		ToolTipManager.sharedInstance().registerComponent(MonthValue);
		MonthValue.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JSpinner spin = (JSpinner) evt.getSource();
				numMos = (int) spin.getValue();
				parent.timeLine.setTimeSelectableFromSpinners(numMos, numWeeks, numDays);
			}
		});
		MonthValue.setValue(numMos);
		this.add(MonthValue, "flowx,cell 0 3,split 3");

        JSpinner WeeksValue = new JSpinner();
		WeeksValue.setForeground(Color.BLACK);
		WeeksValue.setBackground(Color.LIGHT_GRAY);
		WeeksValue.setFont(new Font("Arial", Font.PLAIN, 9));
		WeeksValue.setModel(new SpinnerNumberModel(0, 0, 52, 1)); 
		WeeksValue.setToolTipText("");
		ToolTipManager.sharedInstance().registerComponent(WeeksValue);
		WeeksValue.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JSpinner spin = (JSpinner) evt.getSource();
				numWeeks = (int) spin.getValue();
				parent.timeLine.setTimeSelectableFromSpinners(numMos, numWeeks, numDays);
			}
		});
		WeeksValue.setValue(numWeeks);
		this.add(WeeksValue, "flowx,cell 1 3");

        JSpinner DaysValue = new JSpinner();
		DaysValue.setForeground(Color.BLACK);
		DaysValue.setBackground(Color.LIGHT_GRAY);
		DaysValue.setFont(new Font("Arial", Font.PLAIN, 9));
		DaysValue.setModel(new SpinnerNumberModel(0, 0, 365, 1)); 
		DaysValue.setToolTipText("");
		ToolTipManager.sharedInstance().registerComponent(DaysValue);
		DaysValue.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JSpinner spin = (JSpinner) evt.getSource();
				numDays = (int) spin.getValue();
				parent.timeLine.setTimeSelectableFromSpinners(numMos, numWeeks, numDays);
			}
		});
		DaysValue.setValue(numDays);
		this.add(DaysValue, "flowx,cell 2 3");
    }
    
}
