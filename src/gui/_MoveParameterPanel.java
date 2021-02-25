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

// import javax.swing.JFileChooser;
// import javax.swing.JPanel;
// import javax.swing.UIManager;

// import main.DesktopPane;
// import main.SketchData;
// import net.miginfocom.swing.MigLayout;

// public class MoveParameterPanel extends JPanel {
// 	/**
// 	 * 
// 	 */
// 	private static final long serialVersionUID = 1L;
// 	static int openFrameCount = 0;
// 	static final int xOffset = 30, yOffset = 30;

// 	public JFileChooser dataChooser;
// 	private DesktopPane parent;
// 	private SketchData data;

// 	public MoveParameterPanel(DesktopPane father) {
// 		parent = father;
// 		data = parent.data;
// 		try {
// 			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
// 		} catch (Exception e) {
// 			e.printStackTrace();
// 		}
// 		setLayout(new MigLayout("insets 0", "[20px:20px:20px][15.00][20px:20px:20px,fill][][grow,fill][]",
// 				"[][][][grow,fill][][grow,fill][][][]"));

// 	}
// }
