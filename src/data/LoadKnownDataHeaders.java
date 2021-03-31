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

package data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class LoadKnownDataHeaders {
    private String filename;
    private List<String[]> list;

    public LoadKnownDataHeaders (String fn) { 
        this.filename = fn;
        list = new ArrayList<String[]>();
        loadData();
    }

    // read
	public boolean loadData() {
		CustomCSVReader reader = null;
		try {
			File file = new File(filename);
			reader = new CustomCSVReader(new FileReader(file));
		} catch (FileNotFoundException e2) {
            e2.printStackTrace();
			return false;
		}

		String row[];
        // HEADER
        // filename, fieldfortag, fieldforlong, fieldforlat, fieldfortime, timeformat
        // row[0],   row[1],      row[2],       row[3],      row[4],       row[5]
		try {
			while ((row = reader.readNext()) != null) {
				list.add(new String[] { row[0], row[1], row[2], row[3], row[4], row[5] } );
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		try {
			reader.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

    // check
    public String[] queryFilename(String fname) {
        // see if it already exists
        for (String[] strings : list) {
            if(fname.equals(strings[0]))    return strings;
        }
        return null;
    }

    // write
    public void keepInfoIfMissing(String fname, 
        String tag, String longitude, String latitude, 
        String time, String timeformat)  {
        
        // see if it already exists
        if(queryFilename(fname) == null) {
            // if not, write it in the file
            Writer output = null;
            try {
                output = new BufferedWriter(new FileWriter(filename, true));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
    
            try {
                StringBuilder sb = new StringBuilder();
                sb.append(fname);sb.append(',');
                sb.append(tag);sb.append(',');
                sb.append(longitude);sb.append(',');
                sb.append(latitude);sb.append(',');
                sb.append(time);sb.append(',');
                sb.append(timeformat);sb.append('\n');
    
                output.append(sb.toString());
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
