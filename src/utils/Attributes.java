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

package utils;

import java.util.ArrayList;

public class Attributes {

	private ArrayList<Field> fields = new ArrayList<Field>();
	private String index;

	public void addField(Field field) {
		this.fields.add(field);
	}

	public ArrayList<Field> getSelectedFields() {
		ArrayList<Field> selectedFields = new ArrayList<Field>();
		for (Field field : fields) {
			if (field.getSelected()) {
				selectedFields.add(field);
			}
		}
		return selectedFields;
	}

	public ArrayList<String> getSelectedFieldNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (Field field : this.getSelectedFields()) {
			names.add(field.getAlias());
		}
		return names;
	}

	public ArrayList<Field> getFields() {
		return this.fields;
	}

	public boolean checkIfFieldExists(String s) {
		for (Field field : fields) {
			if (field.getName() == s) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<String> getAliasList() {
		ArrayList<String> list = new ArrayList<String>();
		for (Field field : fields) {
			String s = field.getAlias();
			list.add(s);
		}
		return list;
	}

	public String getName(String alias) {
		for (Field field : fields) {
			if (field.getAlias() == alias) {
				return field.getName();
			}
		}
		return null;
	}

	public String getAlias(String name) {
		for (Field field : fields) {
			if (field.getName() == name) {
				return field.getAlias();
			}
		}
		return null;
	}

	public void setIndex(String name) {
		this.index = name;
	}

	public String getIndex() {
		return this.index;
	}

	public String getIndexAlias() {
		return getAlias(this.index);
	}

	public String getUnit(String s) {
		String unit = null;
		for (Field field : fields) {
			if (field.getName() == s) {
				unit = field.getUnit();
			}
		}
		return unit;
	}

	public float getMin(String s) {
		float min = 0;
		for (Field field : fields) {
			if (field.getName() == s) {
				min = field.getMin();
			}
		}
		return min;
	}

	public float getMax(String s) {
		float max = 0;
		for (Field field : fields) {
			if (field.getName() == s) {
				max = field.getMax();
			}
		}
		return max;
	}

}
