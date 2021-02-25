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
// import java.awt.Window;
// import java.lang.reflect.Method;

// import javax.swing.JFrame;
// import javax.swing.UIManager;

// import main.DesktopPane;
// import main.StaticBox;
// import processing.core.PApplet;

// public class StaticTimeBoxPanel extends JFrame {

// 	// Creates the panel that holds static
// 	private static final long serialVersionUID = 1L;
// 	// JFrame boxPanel;
// 	StaticTimeBoxPanel me;
// 	public StaticBox box = new StaticBox();
// 	public Dimension boxSize = new Dimension(500, 850);

// 	public StaticTimeBoxPanel() {
// 		me = this;
// 		setResizable(true);
// 		setTitle("3D Static Space-Time Analysis");
// 		pack();
// 	}

// 	public void setupStaticBox(DesktopPane father) {
// 		box = null;
// 		box = new StaticBox();
// 		box.setParent(father);
// 		setLocation((int) (father.getBounds().getMinX() + father.animationSize.getWidth()),
// 				(int) (father.getBounds().getMinY() + 50));
// 		// this.setContentPane(box.frame);//this.setContentPane(box);
// 		this.getContentPane().setPreferredSize(boxSize);
// 		this.pack();
// 		PApplet.runSketch(new String[]{""}, box);//		box.init();
// 	}

// 	public void clear() {
// 		box = null;
// 		this.setContentPane(null);
// 	}

// }