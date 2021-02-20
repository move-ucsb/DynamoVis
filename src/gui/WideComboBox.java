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

package gui;

import java.awt.Dimension;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;


//makes the popup wide enough for listed items
@SuppressWarnings("serial")
public class WideComboBox extends JComboBox {
	  public WideComboBox() { 
	    } 
	 
	    public WideComboBox(final Object items[]){ 
	        super(items); 
	    } 
	 
	    @SuppressWarnings("rawtypes")
		public WideComboBox(Vector items) { 
	        super(items); 
	    } 
	 
	    public WideComboBox(ComboBoxModel aModel) { 
	        super(aModel); 
	    } 
	 
	    private boolean layingOut = false; 
	 
	    public void doLayout(){ 
	        try{ 
	            layingOut = true; 
	            super.doLayout(); 
	        }finally{ 
	            layingOut = false; 
	        } 
	    } 
	 
	    public Dimension getSize(){ 
	        Dimension dim = super.getSize(); 
	        if(!layingOut) 
	            dim.width = Math.max(dim.width, getPreferredSize().width); 
	        return dim; 
	    } 
}
