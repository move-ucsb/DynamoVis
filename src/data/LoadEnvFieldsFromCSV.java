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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;


public class LoadEnvFieldsFromCSV {

	public ArrayList<ArrayList<String>> loadData(String fileName) {
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();

		CSVReader reader = null;
		try {
			Path path = Paths.get(fileName);
			reader = new CSVReaderBuilder(Files.newBufferedReader(path, StandardCharsets.UTF_8)).withCSVParser(new CSVParser()).build();
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] header = null;
		try {
			header = reader.readNext();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (CsvValidationException e) {
			e.printStackTrace();
		}
		if (header == null) {
			throw new RuntimeException("No header");
		}

		String[] row;
		// int i = 0;
		try {
			while ((row = reader.readNext()) != null) {
				// i++;

				String fieldFull = row[0] + " ";
				if (!row[1].equals("N/A")) {
					fieldFull = fieldFull + row[1] + " ";
				}
				fieldFull = fieldFull + row[2];
				String fieldShort = row[2];
				String units = row[3].replace("^-2", "\u00b2").replace("^-3", "\u00b3");
				ArrayList<String> temp = new ArrayList<String>();
				temp.add(fieldFull);
				temp.add(fieldShort);
				temp.add(units);
				data.add(temp);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (CsvValidationException e) {
			e.printStackTrace();
		}
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;

	}

}
