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

package utils;

public class Field {

	private String name;
	private String alias;
	private String unit = null;
	private float min;
	private float max;
	private boolean selected;

	public Field(String n) {
		setName(n);
	}

	public Field(String n, String a, String u, float min, float max, boolean selected) {
		setName(n);
		setAlias(a);
		setUnit(u);
		setMin(min);
		setMax(max);
		setSelected(selected);
	}

	public void setSelected(boolean s) {
		this.selected = s;
	}

	public boolean getSelected() {
		return this.selected;
	}

	public void setName(String n) {
		this.name = n;
	}

	public String getName() {
		return this.name;
	}

	public void setAlias(String a) {
		this.alias = a;
	}

	public String getAlias() {
		return this.alias;
	}

	public void setUnit(String u) {
		this.unit = u;
	}

	public String getUnit() {
		return this.unit;
	}

	public void setMin(float m) {
		this.min = m;
	}

	public float getMin() {
		return this.min;
	}

	public void setMax(float m) {
		this.max = m;
	}

	public float getMax() {
		return this.max;
	}

	public String printInfo() {
		return getName() + " " + getAlias() + " " + getUnit() + " " + getMin() + " " + getMax() + " " + getSelected();
	}
}
