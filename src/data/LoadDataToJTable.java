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

import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import main.DesktopPane;
import utils.Attributes;
import utils.Field;
import utils.PointRecord;
import utils.Track;

import org.apache.commons.math3.stat.Frequency;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class LoadDataToJTable implements PropertyChangeListener {

	HashMap<String, Track> trackList;
	private DesktopPane parent;
	private DateTimeFormatter fmt;
	private ProgressMonitor progressMonitor;
	private ArrayList<ArrayList<Object[]>> returnData;
	private long max;
	private long read;
	Task operation;
	public boolean override;

	public LoadDataToJTable(DesktopPane father) {
		parent = father;
	}

	int visibleCount = 0;
	int badCoordCounter = 0;
	int records = 0;
	DateTime now = new DateTime();
	int countEarly = 0;
	int countWayEarly = 0;
	int countLate = 0;
	int countWayLate = 0;

	public void loadData(String absolutePath, String filename) {
		parent.attributes = new Attributes();
		progressMonitor = new ProgressMonitor(parent.dataConfigPanel, "Loading Movement Data...", "", 0, 100);
		progressMonitor.setProgress(0);
		operation = new Task(absolutePath, filename);
		operation.addPropertyChangeListener(this);
		operation.execute();
		override = parent.dataConfigPanel.override;
	}

	class Task extends SwingWorker<ArrayList<ArrayList<Object[]>>, Void> {
		private String absolutePath;
		private String filename;

		public Task(String absolutePath, String filename) {
			this.absolutePath = absolutePath;
			this.filename = filename;
		}

		@Override 
		public ArrayList<ArrayList<Object[]>> doInBackground() {
			parent.dataConfigPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			System.out.println();
			System.out.println("-----New File-----");
			System.out.println(absolutePath + "\n");

			returnData = new ArrayList<ArrayList<Object[]>>();
			ArrayList<Object[]> fieldsData = new ArrayList<Object[]>();
			ArrayList<Object[]> tagData = new ArrayList<Object[]>();
			ArrayList<Object[]> miscData = new ArrayList<Object[]>();
			HashMap<String, ArrayList<Float>> fieldMinMax = new HashMap<String, ArrayList<Float>>();
			ArrayList<String> goodHeaders = new ArrayList<String>();
			parent.headers = new String[5];
			parent.headers[0] = "individual-local-identifier";
			parent.headers[1] = "location-long";
			parent.headers[2] = "location-lat";
			parent.headers[3] = "study-local-timestamp";
			parent.headers[4] = "yyyy-MM-dd HH:mm:ss.SSS";

			// load the header fields from file if previously known
			LoadKnownDataHeaders lkdh = new LoadKnownDataHeaders("./config/RememberedHeaders.txt");
			String[] previouslyFoundHeaders = lkdh.queryFilename(filename);
			if(previouslyFoundHeaders != null) {
				System.out.println("Required fields are remembered for "+ filename);

				parent.headers[0] = previouslyFoundHeaders[1];
				parent.headers[1] = previouslyFoundHeaders[2];
				parent.headers[2] = previouslyFoundHeaders[3];
				parent.headers[3] = previouslyFoundHeaders[4];
				parent.headers[4] = previouslyFoundHeaders[5];
			}

			LoadDiscardFields ldf = new LoadDiscardFields();
			List<String> discardFields = ldf.loadData("./config/DiscardedFields.txt");

			CustomCSVReader reader = null;
			try {
				// BufferedReader bfr = new BufferedReader(new InputStreamReader(new
				// FileInputStream(absolutePath)));
				File file = new File(absolutePath);
				reader = new CustomCSVReader(new FileReader(file));
				max = (long) file.length();
			} catch (FileNotFoundException e2) {
				e2.printStackTrace();
			}

			boolean foundRequiredFields = true;
			String[] header = null;
			try {
				header = reader.readNext();

				if (!override) {
					if(foundRequiredFields)
						if (Arrays.asList(header).contains(parent.headers[0])) {
							System.out.println("Individual-local-identifier found");
						} else if (Arrays.asList(header).contains("individual.local.identifier")) {
							System.out.println("Individual.local.identifier found");
							parent.headers[0] = "individual.local.identifier";
						} else {
							foundRequiredFields = getTag(header);
						}

					if(foundRequiredFields) // stop asking for field names if tag selection is already cancelled
						if (Arrays.asList(header).contains(parent.headers[1])) {
							System.out.println("Location-long found");
						} else if (Arrays.asList(header).contains("location.long")) {
							System.out.println("Location.long found");
							parent.headers[1] = "location.long";
						} else {
							foundRequiredFields = getLong(header);
						}
					
					if(foundRequiredFields) // stop asking for field names if previous fields are already cancelled
						if (Arrays.asList(header).contains(parent.headers[2])) {
							System.out.println("Location-lat found");
						} else if (Arrays.asList(header).contains("location.lat")) {
							System.out.println("Location.lat found");
							parent.headers[2] = "location.lat";
						} else {
							foundRequiredFields = getLat(header);
						}

					if(foundRequiredFields) // stop asking for field names if previous fields are already cancelled
						if (Arrays.asList(header).contains(parent.headers[3])) {
							System.out.println("Timestamp found");
						} else if (Arrays.asList(header).contains("study.local.timestamp")) {
							System.out.println("Study.local.timestamp found");
							parent.headers[3] = "study.local.timestamp";
						} else {
							foundRequiredFields = getTime(header);
						}
				} else if (override) {
					if(foundRequiredFields) foundRequiredFields = getTag(header);
					if(foundRequiredFields) foundRequiredFields = getLong(header);
					if(foundRequiredFields) foundRequiredFields = getLat(header);
					if(foundRequiredFields) foundRequiredFields = getTime(header);
				}
				goodHeaders.add(parent.headers[0]);

			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if (foundRequiredFields == false) {
				System.out.println("Aborting...\nRequired Fields: 'Tag', 'Longitude', 'Latitude' and 'Time'.");
				parent.dataConfigPanel.setCursor(null);
				return null;
			}
			if (header == null) {
				System.out.println("Aborting...\nNo header.");
				parent.dataConfigPanel.setCursor(null);
				return null;
			}

			fmt = DateTimeFormat.forPattern(parent.headers[4]);
			setProgress(0);
			String[] row;
			String title = "Untitled";

			HashMap<String, ArrayList<Float>> fieldValues = new HashMap<String, ArrayList<Float>>();
			trackList = new HashMap<String, Track>();
			Boolean first = true;
			Boolean titlef = false;
			try {
				int counter = 0;
				while ((row = reader.readNext()) != null) {
					records++;
					read = reader.readSoFar;
					if (counter > 1000) {
						int prog = (int) ((read * 100 / max));
						setProgress(prog);
						counter = 0;
					}
					counter++;
					boolean visible = true;

					for (int i = 0; i < row.length; i++) {
						if (header[i].equals("visible")) {
							if (row[i].equals("false") || row[i].equals("FALSE")) {
								visibleCount++;
								visible = false;
							}
						}
					}

					if (visible) {
						PointRecord record = new PointRecord();
						float lon = 0;
						float lat = 0;
						String tag = null;

						boolean noCoord = false;

						for (int i = 0; i < row.length; i++) {
							if (header[i].contains(parent.headers[1])) {
								try {
									lon = Float.parseFloat(row[i]);
								} catch (NumberFormatException e) {
									noCoord = true;
								}
							}
							if (header[i].contains(parent.headers[2])) {
								try {
									lat = Float.parseFloat(row[i]);
								} catch (NumberFormatException e) {
									noCoord = true;
								}
							}
						}

						if (noCoord) {
							badCoordCounter++;
						}

						if (!noCoord) {
							Track track = null;
							for (int i = 0; i < row.length; i++) {
								if (header[i].equals(parent.headers[0])) {

									tag = row[i];
									track = trackList.get(tag);
									if (track == null) {
										track = new Track();
										track.setTag(tag);
										trackList.put(tag, track);
									}
								}
							}

							boolean skip = false;
							for (int i = 0; i < row.length; i++) {
								if (header[i].equals(parent.headers[3])) {
									DateTime time;
									if (first) {
										time = formatTime(row[i]);
										System.out.println("Timestamp format: " + parent.headers[4]);
										first = false;
									} else {
										try {
											time = fmt.parseDateTime(row[i]);
										} catch (IllegalArgumentException e) {
											time = formatTime(row[i]);
											System.out.println("Encountered different timestamp format in data: "
													+ parent.headers[4]);
										}
									}

									// if time formatting prompt is cancelled
									if (time == null) {
										foundRequiredFields = false;
										System.out.println("Aborting...\nCorrect time format is required.");
										parent.dataConfigPanel.setCursor(null);
										return null;
									}


									if (time.getYear() < 1980 && time.getYear() > 1800) {
										countEarly++;
									} else if (time.isAfter(now) && time.getYear() < 2040) {
										countLate++;
									} else if (time.getYear() >= 2040) {
										countWayLate++;
										skip = true;
									} else if (time.getYear() <= 1800) {
										countWayEarly++;
										skip = true;
									}

									if (!skip) {
										record.setTime(time);
										record.setID(tag);
										record.setLocation(lat, lon);
										record.addProperty("Tag", tag);
									}
								}
							}

							if (!skip) {
								ArrayList<Float> tempV = null;
								for (int i = 0; i < row.length; i++) {
									if (header[i].equals(parent.headers[0])) {

									} else if (header[i].equals(parent.headers[3])) {

									} else if (!titlef
											&& (header[i].equals("study-name") || header[i].equals("study.name"))) {
										title = row[i];
										titlef = !titlef;
									} else if (!discardFields
											.contains(header[i].replace(":", "").replace("-", "").replace(".", ""))) {
										float value = Float.NaN;
										try {
											float temp = Float.parseFloat(row[i]);
											if (Float.isNaN(temp)) {
												// skip
											} else {
												tempV = fieldValues.get(header[i]);
												if (tempV == null) {
													tempV = new ArrayList<Float>();
													fieldValues.put(header[i], tempV);
												}
												tempV.add(temp);
												if (!goodHeaders.contains(header[i])) {
													goodHeaders.add(header[i]);
												}
												value = temp;
											}
										} catch (NumberFormatException e) {

										}
										record.addProperty(header[i], value);

									}
								}
								track.addPoint(record);
							}
						}
					}

				}

				// check the edge of map
				for (Entry<String, Track> entry : trackList.entrySet()) {
					Track track = entry.getValue();
					track.checkEdgeOfMap();
				}

				for (Entry<String, ArrayList<Float>> entry : fieldValues.entrySet()) {
					ArrayList<Float> values = entry.getValue();
					Float max = Collections.max(values);
					Float min = Collections.min(values);
					@SuppressWarnings("unlikely-arg-type")
					ArrayList<Float> minMaxValues = fieldMinMax.get(entry.getValue());
					if (minMaxValues == null) {
						minMaxValues = new ArrayList<Float>();
						fieldMinMax.put(entry.getKey(), minMaxValues);
					}
					minMaxValues.add(min);
					minMaxValues.add(max);
				}

				Frequency whole = new Frequency();
				for (Entry<String, Track> entry : trackList.entrySet()) {
					Track track = entry.getValue();
					String key = entry.getKey();
					track.calculateTimes();

					Object[] temp = { key, track.getStartDate(), track.getEndDate(), track.getInterval(), true }; // CHANGED
					tagData.add(temp);
					whole.addValue(track.getInterval());
				}

				Long intervalLong = (Long) whole.getMode().get(0);
				Integer inter = intervalLong != null ? intervalLong.intValue() : null;
				// interval = (int) Math.round(interval/60);
				Period interval = new Period(Period.minutes(inter));
				Object[] misc = { title, interval };
				miscData.add(misc);

			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			for (int i = 0; i < goodHeaders.size(); i++) {
				Boolean enabled = false;
				ArrayList<String> match = null;
				for (ArrayList<String> envFields : parent.envFields) {
					if (envFields.contains(goodHeaders.get(i))) {
						match = envFields;
					}
				}

				if (match != null || goodHeaders.get(i).contains("user:") || goodHeaders.get(i).equals(parent.headers[0])) {
					enabled = true;
				}

				String shortName = goodHeaders.get(i);
				String units = "";

				if (goodHeaders.get(i).equals(parent.headers[0])) {
					shortName = "Tag";
				}

				if (match != null) {
					shortName = match.get(1);
					units = match.get(2);
				}

				if (goodHeaders.get(i).contains("user:")) {
					shortName = goodHeaders.get(i).replace("user:", "");
				}

				if (goodHeaders.get(i).contains("User ")) {
					shortName = goodHeaders.get(i).replace("User ", "");
				}

				float min = 0;
				float max = 0;
				if (fieldMinMax.containsKey(goodHeaders.get(i))) {
					min = fieldMinMax.get(goodHeaders.get(i)).get(0);
					max = fieldMinMax.get(goodHeaders.get(i)).get(1);
				}
				Object[] temp = { goodHeaders.get(i), shortName, units, min, max, enabled };

				Field field = new Field(goodHeaders.get(i), shortName, units, min, max, enabled);
				parent.attributes.addField(field);

				fieldsData.add(temp);

			}
			if(foundRequiredFields) {
				lkdh.keepInfoIfMissing(filename, parent.headers[0], parent.headers[1], parent.headers[2], parent.headers[3], parent.headers[4]);
			}

			returnData.add(fieldsData);
			returnData.add(tagData);
			returnData.add(miscData);
			setProgress(100);
			parent.trackList = trackList;
			return returnData;

		}

		public void done() {
			try {
				ArrayList<ArrayList<Object[]>> result = get();
				if(result != null) {
					parent.dataConfigPanel.returned(result);
	
					System.out.println("");
					System.out.println("Total Records in File: " + records);
	
					if (visibleCount > 0) {
						System.out.println("Records marked as outliers (Visible = FALSE): " + visibleCount);
					}
					if (badCoordCounter > 0) {
						System.out.println("Records with bad coordinates: " + badCoordCounter);
					}
					if (countEarly > 0) {
						System.out.println("Dates between 1800 and 1980 (Kept): " + countEarly);
					}
					if (countWayEarly > 0) {
						System.out.println("Dates prior to 1800 (Ignored): " + countWayEarly);
					}
					if (countLate > 0) {
						System.out.println("Dates between now and 2040 (Kept): " + countLate);
					}
					if (countWayLate > 0) {
						System.out.println("Dates after 2040 (Ignored): " + countWayLate);
					}
					if (visibleCount == 0 && badCoordCounter == 0 && countEarly == 0 && countWayEarly == 0 && countLate == 0
							&& countWayLate == 0) {
						System.out.println("No outliers or bad dates found");
					}
					System.out.println("Records available for Visualization: "
							+ (records - visibleCount - badCoordCounter - countWayEarly - countWayLate));
	
					/*
					 * JOptionPane.showMessageDialog(parent, "Total Records: " + records + "\n" +
					 * "Records marked as outliers (Visible = False): " + visibleCount + "\n" +
					 * "Records with bad coordinates: " + badCoordCounter,
					 * 
					 * "Records Processed", JOptionPane.WARNING_MESSAGE);
					 */
				}

			} catch (InterruptedException e) {

			} catch (CancellationException e) {
				System.out.println("Loading Cancelled...\n");
			} catch (ExecutionException e) {
				System.out.println("Something bad happened: " + e.getCause());
				e.printStackTrace();
			}
			parent.dataConfigPanel.setCursor(null);
		}
	}

	public DateTime parseTime(String s) {
		DateTime time = fmt.parseDateTime(s);
		return time;
	}

	public DateTime formatTime(String row) {
		boolean success = false;
		boolean cancelledFormatWindow = false;
		DateTime time = null;
		while (!success) {
			try {
				time = parseTime(row);
				success = true;
			} catch (IllegalArgumentException e) {
				if (row.contains("T")) {
					try {
						parent.headers[4] = "yyyy-MM-dd'T'HH:mm:ss.SSS";
						fmt = DateTimeFormat.forPattern(parent.headers[4]);
						time = parseTime(row);
					} catch (IllegalArgumentException e1) {
						try {
							parent.headers[4] = "yyyy-MM-dd'T'HH:mm:ss";
							fmt = DateTimeFormat.forPattern(parent.headers[4]);
							time = parseTime(row);
						} catch (IllegalArgumentException e2) {
							try {
								parent.headers[4] = "yyyy-MM-dd'T'HH:mm";
								fmt = DateTimeFormat.forPattern(parent.headers[4]);
								time = parseTime(row);
							} catch (IllegalArgumentException e3) {
								try {
									parent.headers[4] = "yyyy-MM-dd'T'HH";
									fmt = DateTimeFormat.forPattern(parent.headers[4]);
									time = parseTime(row);
								} catch (IllegalArgumentException e4) {
									if (e1.toString().contains("time zone")) {
										cancelledFormatWindow = giveUp(e1);
									} else {
										cancelledFormatWindow = giveUp(e4);
									}
								}
							}
						}
					}
				} else if (!row.contains("T")) {
					try {
						parent.headers[4] = "yyyy-MM-dd HH:mm:ss.SSS";
						fmt = DateTimeFormat.forPattern(parent.headers[4]);
						time = parseTime(row);
					} catch (IllegalArgumentException e1) {
						try {
							parent.headers[4] = "yyyy-MM-dd HH:mm:ss";
							fmt = DateTimeFormat.forPattern(parent.headers[4]);
							time = parseTime(row);
						} catch (IllegalArgumentException e2) {
							try {
								cancelledFormatWindow = giveUp(e2);
							}
							catch (IllegalArgumentException e2_5) {
								try {
									parent.headers[4] = "yyyy-MM-dd HH:mm";
									fmt = DateTimeFormat.forPattern(parent.headers[4]);
									time = parseTime(row);
								} catch (IllegalArgumentException e3) {
									try {
										parent.headers[4] = "yyyy-MM-dd HH";
										fmt = DateTimeFormat.forPattern(parent.headers[4]);
										time = parseTime(row);
									} catch (IllegalArgumentException e4) {
										if (e1.toString().contains("time zone")) {
											cancelledFormatWindow = giveUp(e1);
										} else {
											cancelledFormatWindow = giveUp(e4);
										}
									}
								}
							}
						}
					}
				} else {
					cancelledFormatWindow = giveUp(e);
				}
			}
			if(cancelledFormatWindow) break;
		}
		return time;
	}

	public boolean giveUp(IllegalArgumentException e) {
		String prompt = "Invalid Date Format  ##  " + e.getMessage() + 
			"\nUse identifiers below to match the datetime format in your data.\n"+
			"Make sure to use appropriate separators (e.g.: '  : / - .'):\n" +
			"year: 'yy/yyyy', month: 'MM', day: 'dd', hour: 'HH', min: 'mm', sec: 'ss', msec: 'SSS'"; 
		String format = (String) JOptionPane.showInputDialog(parent.dataConfigPanel,
				prompt, "Date Format", JOptionPane.PLAIN_MESSAGE, null, null,
				parent.headers[4]);
		if(format == null) return true;

		parent.headers[4] = format;
		fmt = DateTimeFormat.forPattern(parent.headers[4]);
		
		return false;
	}

	public boolean getTag(String[] header) {
		Object[] fields = header;
		String s = (String) JOptionPane.showInputDialog(parent.dataConfigPanel,
				"individual-local-identifier not found\n" + "Please Select field containing unique tags:",
				"Tag Selection", JOptionPane.PLAIN_MESSAGE, null, fields, null);

		if(s == null) return false;

		for (int i = 0; i < fields.length; i++) {
			if (fields[i].equals(s)) {
				parent.headers[0] = header[i];
				System.out.println("Using " + header[i] + " as individual-local-identifier");
				return true;
			}
		}
		return false;
	}

	public boolean getLong(String[] header) {
		Object[] fields = header;
		String s = (String) JOptionPane.showInputDialog(parent.dataConfigPanel,
				"location-long not found\n" + "Please Select field containing Longitude (WGS 84):",
				"Longitude Selection", JOptionPane.PLAIN_MESSAGE, null, fields, null);

		if(s == null) return false;

		for (int i = 0; i < fields.length; i++) {
			if (fields[i].equals(s)) {
				parent.headers[1] = header[i];
				System.out.println("Using " + header[i] + " as location-long");
				return true;
			}
		}
		return false;
	}

	public boolean getLat(String[] header) {
		Object[] fields = header;
		String s = (String) JOptionPane.showInputDialog(parent.dataConfigPanel,
				"location-lat not found\n" + "Please Select field containing Latitude (WGS 84):", "Latitude Selection",
				JOptionPane.PLAIN_MESSAGE, null, fields, null);

		if(s == null) return false;

		for (int i = 0; i < fields.length; i++) {
			if (fields[i].equals(s)) {
				parent.headers[2] = header[i];
				System.out.println("Using " + header[i] + " as location-lat");
				return true;
			}
		}
		return false;
	}

	public boolean getTime(String[] header) {
		Object[] fields = header;
		String s = (String) JOptionPane.showInputDialog(parent.dataConfigPanel,
				"study-local-timestamp not found\n" + "Please Select field containing a valid timestamp:",
				"Timestamp Selection", JOptionPane.PLAIN_MESSAGE, null, fields, null);

		if(s == null) return false;

		for (int i = 0; i < fields.length; i++) {
			if (fields[i].equals(s)) {
				parent.headers[3] = header[i];
				System.out.println("Using " + header[i] + " as study-local-timestamp");
				return true;
			}
		}
		return false;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			progressMonitor.setProgress(progress);
		}
		if (progressMonitor.isCanceled()) {
			operation.cancel(true);
		}
	}

}
