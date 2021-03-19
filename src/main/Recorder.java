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

package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import javax.swing.JToggleButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.Timer;

import org.joda.time.DateTime;
import javax.swing.JComboBox;


public class Recorder extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	DesktopPane parent;
	SketchData data;
	int timeDelay = 10;
	ActionListener time;
	Timer timer;
	DateTime dateTime;
	JLabel timeLabel;
	JCheckBox chkTemp;
	JButton btnSave;

	public Recorder(DesktopPane father) {
		parent = father;
		data = father.data;
		setLayout(new MigLayout("", "[grow][][]", "[][][]"));
		time = new ActionListener() {

		    @Override
		    public void actionPerformed(ActionEvent evt) {
		    	double frames = ((double) data.frameCounter) / 25d;
		    	timeLabel.setText(new DecimalFormat("00.00").format(frames) + "s");
		    }
		};
		timer = new Timer(timeDelay, time);

		// checkbox to store frames
		chkTemp = new JCheckBox("Store frames");
		add(chkTemp, "cell 0 0 2,alignx left");
		// chkTemp.addItemListener(new ItemListener() {
		// 	public void itemStateChanged(ItemEvent evt) {
		// 		JCheckBox cb = (JCheckBox) evt.getSource();
		// 		if (cb.isSelected()) {

		// 		} else {

		// 		}
		// 	}
		// });

		timeLabel = new JLabel("00.00s");
		add(timeLabel, "cell 1 0 2,alignx right");
		
		final JToggleButton tglbtnRecord = new JToggleButton("Record");
		add(tglbtnRecord, "cell 0 1");
		tglbtnRecord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {			
				data.save = !data.save;
				if (data.save){					
					timer.start();
					btnSave.setEnabled(false);
				} else {
					timer.stop();
					btnSave.setEnabled(true);
				}
			}
		});		
		
		JButton btnStop = new JButton("Stop");
		add(btnStop, "cell 1 1");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (data.save){
					timer.stop();
					data.save = !data.save;
					tglbtnRecord.setSelected(false);
					btnSave.setEnabled(true);
				}
			}
		});	
		
		btnSave = new JButton("Save");
		btnSave.setEnabled(false);
		add(btnSave, "cell 2 1");
		
		JComboBox comboBox = new JComboBox();
		add(comboBox, "flowx,cell 0 2 3 1,growx");
		comboBox.addItem("h264 Baseline");
		comboBox.setEnabled(false);
		
		JComboBox comboBox_1 = new JComboBox();
		add(comboBox_1, "flowx,cell 0 2 3 1,growx");
		comboBox_1.addItem("30 fps");
		comboBox_1.setEnabled(false);
		
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					new SequenceEncoder(parent, parent.animationTitle + parent.exportCounter + ".mp4", 
						0, data.frameCounter, 
						!chkTemp.isSelected());						
				} catch (IOException e) {
					e.printStackTrace();
				}

				data.frameCounter = 0;
				timeLabel.setText("00.00s");	
			}
		});	
		
		
	}

}



// /*

//   	DynamoVis Animation Tool
//     Copyright (C) 2016 Glenn Xavier
//     UPDATED: 2021 Mert Toka

//     This program is free software: you can redistribute it and/or modify
//     it under the terms of the GNU General Public License as published by
//     the Free Software Foundation, either version 3 of the License, or
//     (at your option) any later version.

//     This program is distributed in the hope that it will be useful,
//     but WITHOUT ANY WARRANTY; without even the implied warranty of
//     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//     GNU General Public License for more details.

//     You should have received a copy of the GNU General Public License
//     along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
// */

// package main;

// import gui.TimeBoxPanel;
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;
// import java.io.IOException;
// import java.text.DecimalFormat;

// import javax.swing.JButton;
// import javax.swing.JComboBox;
// import javax.swing.JLabel;
// import javax.swing.JPanel;
// import javax.swing.JToggleButton;
// import javax.swing.Timer;

// import org.joda.time.DateTime;

// import net.miginfocom.swing.MigLayout;

// public class Recorder extends JPanel {

// 	/**
// 	 * 
// 	 */
// 	private static final long serialVersionUID = 1L;
// 	DesktopPane parent;
// 	SketchData data;
// 	int timeDelay = 10;
// 	ActionListener time;
// 	Timer timer;
// 	DateTime dateTime;
// 	JLabel timeLabel;
// 	JButton btnSave;

// 	public Recorder(DesktopPane father, TimeBoxPanel tb) {
// 		parent = father;
// 		data = father.data;
// 		setLayout(new MigLayout("", "[grow][][]", "[][][]"));
// 		time = new ActionListener() {

// 			@Override
// 			public void actionPerformed(ActionEvent evt) {
// 				double frames = ((double) data.frameCounter) / 25d;
// 				timeLabel.setText(new DecimalFormat("00.00").format(frames) + "s");
// 			}
// 		};
// 		timer = new Timer(timeDelay, time);

// 		timeLabel = new JLabel("00.00s");
// 		add(timeLabel, "cell 0 0 3 1,alignx right");

// 		final JToggleButton tglbtnRecord = new JToggleButton("Record");
// 		add(tglbtnRecord, "cell 0 1");
// 		tglbtnRecord.addActionListener(new ActionListener() {
// 			public void actionPerformed(ActionEvent evt) {
// 				data.save = !data.save;
// 				if (data.save) {
// 					timer.start();
// 					btnSave.setEnabled(false);
// 				} else {
// 					timer.stop();
// 					btnSave.setEnabled(true);
// 				}
// 			}
// 		});

// 		JButton btnStop = new JButton("Stop");
// 		add(btnStop, "cell 1 1");
// 		btnStop.addActionListener(new ActionListener() {
// 			public void actionPerformed(ActionEvent evt) {
// 				if (data.save) {
// 					timer.stop();
// 					data.save = !data.save;
// 					tglbtnRecord.setSelected(false);
// 					btnSave.setEnabled(true);
// 				}
// 			}
// 		});

// 		btnSave = new JButton("Save");
// 		btnSave.setEnabled(false);
// 		add(btnSave, "cell 2 1");

// 		JComboBox comboBox = new JComboBox();
// 		add(comboBox, "flowx,cell 0 2 3 1,growx");
// 		comboBox.addItem("h264 Baseline");
// 		comboBox.setEnabled(false);

// 		JComboBox comboBox_1 = new JComboBox();
// 		add(comboBox_1, "flowx,cell 0 2 3 1,growx");
// 		comboBox_1.addItem("30 fps");
// 		comboBox_1.setEnabled(false);

// 		btnSave.addActionListener(new ActionListener() {
// 			public void actionPerformed(ActionEvent evt) {
// 				try {
// 					new SequenceEncoder(parent, parent.animationTitle + parent.exportCounter + ".mp4", 0,
// 							data.frameCounter);
// 					if (tb.isVisible()) {
// 						new BoxSequenceEncoder(tb, parent.animationTitle + "_3D_" + parent.exportCounter + ".mp4", 0,
// 								data.frameCounter);
// 					}
// 				} catch (IOException e) {
// 					// TODO Auto-generated catch block
// 					e.printStackTrace();
// 				}

// 				data.frameCounter = 0;
// 				timeLabel.setText("00.00s");

// 			}
// 		});

// 	}

// }
