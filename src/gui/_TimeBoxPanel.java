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

// package gui;

// import java.awt.Dimension;
// import javax.swing.JFrame;
// import main.DesktopPane;
// import processing.core.PApplet;
// import main.Box;

// public class TimeBoxPanel extends JFrame {

// 	// Creates the panel that holds the time box
// 	private static final long serialVersionUID = 1L;
// 	JFrame boxPanel;
// 	TimeBoxPanel me;
// 	public Box box = new Box();
// 	public Dimension boxSize = new Dimension(500, 850);
// 	public String animationTitle;
// 	public int exportCounter = 1;

// 	public TimeBoxPanel() {
// 		me = this;
// 		setResizable(false);
// 		setTitle("3D Space-Time Analysis");
// 		pack();
// 	}

// 	public void setupBox(DesktopPane father) {
// 		box = null;
// 		box = new Box();
// 		box.setParent(father);
// 		animationTitle = father.animationTitle;
// 		setLocation((int) (father.getBounds().getMinX() + father.animationSize.getWidth()),
// 				(int) (father.getBounds().getMinY()));
// 		// Location at end of the animation
// 		// this.setContentPane(boxPanel);   //this.setContentPane(box);
// 		this.getContentPane().setPreferredSize(boxSize);
// 		this.pack();
// 		PApplet.runSketch(new String[]{""}, box);//		box.init();
// 	}

// }