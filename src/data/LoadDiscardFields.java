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

package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoadDiscardFields {

	public List<String> loadData(String fileName) {
		List<String> list = new ArrayList<String>();
		CustomCSVReader reader = null;
		try {
			File file = new File(fileName);
			reader = new CustomCSVReader(new FileReader(file));
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}

		String row[];
		try {
			while ((row = reader.readNext()) != null) {
				list.add(row[0].replace(":", "").replace("-", "").replace(".", ""));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return list;
	}

}
