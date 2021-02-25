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

// package utils;

// import main.DesktopPane;
// import java.awt.Color;

// public class Buffer {

//     static final double DEFAULT_SIZE = 1.0;
//     Color color;
//     double size;
//     boolean b;
//     DesktopPane parent;
//     private String[] distances = { "1", "2", "3" };

//     public Buffer() {
//         size = Double.parseDouble(distances[0]);
//         color = Color.WHITE;
//         b = false;
//     }

//     public Buffer(double size) {
//         color = Color.WHITE;
//         this.size = size;
//     }

//     public Buffer(boolean b) {
//         if (b) {
//             color = Color.YELLOW;
//             size = 1.0;
//             this.b = b;
//         } else {
//             color = Color.WHITE;
//             size = 1.0;
//             this.b = b;
//         }
//     }

//     public double getBufferDistance() {
//         return size;
//     }

//     public String[] getBufferDistances() {
//         return distances;
//     }
// }
