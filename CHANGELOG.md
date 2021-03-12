#### Things to do:
- More legend options
- Save animation state/configuration
- Layers (maybe for advanced layout?)
- Vector EPS outputs for publications
- Plot and corr modules
- Pending library upgrades
  - log4j 1.2.15 ( 2.14.0 available )
  - miglayout 4.2 ( 5.0 available )
  - jcodec 0.1.6 -> 0.2.5 ( broke the video encoder when upgraded, pending for later)
- data loading improvements 
  - header for common data fields
  - time formatting needs to be more flexible
- unfolding maps wrapping at the edge of the map
  - if data contains +/- longtitudes, handle it separately 
- order
  - 1) processing3 integration 
    -> publish as a new release
  - 2) spacetime cube
  - 3) timeline improvements


### CHANGES

#### Project Name: `DynamoVis`

**TODO:**

[ ] "new and edit" does not close sketch window

[ ] remove cancel button
[ ] Help is empty?



[ ] unfolding maps, data longitude, 
[ ] -- dummy data set, shift already existing dataset

[ ] shutdown behaviour, opengl thread is lingering

[ ] close status window after loading the sketch

[ ] remove unused map base

[ ] external "tiger" data variables should be read
[ ] -- shouldn't ignore any variables, let users to select those as well 

[ ] change export location and remove temp files after

[ ] change logos to resources folder 
 
[ ] look and feel of the windows (should obey the system theme settings?)

[ ] timeline shift-click, disable this interaction

[+] default window locations / sizes
[+] -- make sure that it runs with any resolution
[+] data loading pane minimum size has changed
[+] data loading window, enable/disable "create animation" based on sketch
[+] open timeline after the second animation created
[+] "new" button / fix "create animation" bug (after two times)
[+] 3D translate warning?? 
[+] logo for windows
[+] add copyright remark "Created by DynamoVis, MoveLab@UCSB, 2021" on the sketch when exporting
[+] connect sketch window closing with timeline and control panel

##### 0.4.1.5 - 2021/03/12
- Data config pane has smaller minimum width
- Removed 3D translate commands in 2D Processing sketch
- "Create Animation" button is disabled when a sketch is running
- Data configuration panel does not crash after animation is created multiple times 
- Timeline is visible again after the second animation is created
- Resolved a bug where the data config panel was scaling right before creating an animation
- Default window locations and sizes adjusted (reset locations)
- No windows are spawned outside the screen

##### 0.4.1.4 - 2021/03/11
- Closing Processing sketch hides timeline and control panel
- Exported videos now holds "Created by DynamoVis, MoveLab@UCSB, 2021" embedded in the upper right corner.
- Removed month name from the legend and made copyright statement bigger. 
- Changed software icons to MOVELAB icon
- Sketch runs with 60fps, rather than 30fps

##### 0.4.1.3 - 2021/03/04
- Closing Procesing sketch doesn't close Java Application.
- Fixed PSketch title and location. Timeline and control panel positions around the map sketch.
- Export video is fixed.

##### 0.4.1.2 - 2021/02/25
- Changed window hiearchy slightly. We are now running the main sketch as a separate window. Configure animation window stays open (its the main window now).
- Processing 3 and JFrame does not work together. Rolling back the component integrations to start fixing barebones first. 
- Updated to Processing 3.5.4 and Unfolding 0.9.9beta

##### 0.4.1.1 - 2021/02/20
- EnvDATA-variables.csv in Config reformatted as UTF-8
- Merged all components from Kate, Nathan, and Pinki

##### 0.4.1.0 - 2021/02/10
- Mert started working on integration of all releases
- Project name changed to DynamoVis
- Started running with Java 15.0.2.7
- Upgraded libraries: commons-math3 3.3 to 3.6.1, joda-time 2.3 to 2.10.10, opencsv 2.3 to 5.3 (added commons-lang3 3.11 for opencsv 5.3)

#### Project Name: `DYNAMO`
##### 0.4.0.4 - 2020/11/22
- Pushed date outlier filter from 2020 to 2040

##### 0.4.0.3 - 2015/04/30
- Fixed bug where end points of scale wouldn't appear when fields have aliases

##### 0.4.0.2 - 2015/01/25
- Larger default legend
- Points lying directly on the hour are now displayed at the correct time

##### 0.4.0.1 - 2014/11/26
- New name
- Minor bug fix

#### Project Name: `mb_AnimationTool`
##### 0.4.0.0 - 2014/11/17
- rewrite of backend to support future development and new modules
- new PointRecord & Track and Field & Attributes classes
- New track underlay option

##### 0.3.0.4 - 2014/09/30
- New dataprocessing on data load, results shown in status box:
  - Tells user number of dates before 1800 and after 2020 ignored. 
  - Warns of any dates before 1980 and after the current system date, but these are kept. 
  - Tells user number of records marked as outliers (false in visible field)
  - Tells user number of records with bad coordinates (this and outliers were already discarded behind the scenes)
- All timestamps are now properly parsed as UTC again. 
- Build animation button disabled when interval is 0

##### 0.3.0.2 - 2014/09/05
- Changed appearance of color editor slightly
- Couple minor fixes

##### 0.3.0.1 - 2014/08/22
- Bug fixes
- If interval is in seconds, the fade duration is set in minutes instead of hours

##### 0.3.0.0 - 2014/08/21
- Individual tags can now be toggled on and off by using the checkboxes on the side of the timeline panel without rebuilding the animation. 
- Brushing is enabled. Click the tag's bar in the timeline panel to highlight its data on the map in cyan.
- Timeline panel marker can now be used to seek (awkwardly... the marker is hard to grab, the animation doesn't pause while holding the marker, and if the animation is manually paused the time doesn't snap to the polling interval until it's unpaused again).
- Separated out tracks/points/vectors: each have their own color and size range that can be set individually. The color ramp lists are shared.
- New legends for point color/stroke and vector color/length
- Legend layout can be saved and loaded
- Data panel has quick rounding button for range values (nearest integer, down for min, up for max)
- Data panel fields can be sorted
- Switched to individual-local-identifier as default key
- Holding shift while clicking load data button will force selection dialogs for key,long,lat,and timestamp.
- Some ui changes

##### 0.2.4.0 - 2014/08/12
- Overhauled color system. Big list of sequential and diverging color ramps to choose from, and you can assign a ramp to each field. Currently, these assignments are lost if you rebuild the animation.
- Color ramps can be fully edited, saved, and loaded. Ramps can have as many stops as you want, and the editor was designed to work similar to the one in photoshop: click anywhere to make a new stop, drag it around to move, double click or click the color button to edit the stop color, and drag the stop down or click the delete button to remove it.
- Timeline panel now updates marker during animation
- Program now tries a few different timestamp formats before giving up and asking
- Program can now handle different timestamp formats in the same dataset in the case where a user may have merged them from different sources.
- Animation Interval can now be shorter than one minute
- Timestamp on legend is shorter (will add ms again if/when we allow intervals to be that short)

##### 0.2.3.0 - 2014/08/05
- Added basemap provider selections. Need jre 1.7+ to access google providers. Some providers won't load at extreme zooms (Bison data), but the program won't crash if this happens.
- Auto-discarded fields moved to accessible file in config folder, letting user edit as necessary 
- Data fields with "user:" prefix are selected for the animation by default
- Window locations are now saved between animations, reset layout button in menu enabled
- Fixed recording panel breaking when animation is rebuilt
- All prompts should now be centered over their parent
- Removed some redundant calculations during animation building process
- Intervals now round up to nearest whole minute

##### 0.2.2.0 - 2014/08/03
- Quick fix to diverging color ramp calculation
- Added progress monitors for data loading/processing
- Implemented basic video export tool. Lets you start and stop recording, then save to an h.264 mp4. Haven't tested thoroughly, and temp files are not automatically deleted.

##### 0.2.1.1 - 2014/07/31
- Minor bug fixes
- List of auto discarded fields will now match to the older datasets
- Datetime formatter prompt moved to a looping try catch block (it won't go away until it can successfully parse a datetime)

##### 0.2.1.0 - 2014/07/30
- Rebuilt GUI again for compatability with mac's lightweight/heavyweight rendering issues
- Excessive zooming at start is fixed
- An "overview" timeline of the tags is created, will eventually have seeking/brushing interactivity
- Color and legend editing panels implemented, but gradients are limited due to HSB color space and some wonky calculations
- Min/max ranges for each field are editable in the data panel - need to add a quick rounding button
- Rudimentary "point vectors" option added. This option is only available if the data has an obvious directional field (360 degrees with a bit of tolerance). Basic white lines are drawn from each data point with lengths scaled to another field. User interface for this needs to be improved, as well as more visual options.

##### 0.2.0.0 - 2014/07/13
- Overhaul to data structure and gui
- Reads raw Movebank CSV's: works with sets that have some bad data/coords
- Data configuration panel implemented: lets user enable/disable/rename fields and filter tags
- Program attempts to determine an appropriate time interval for the dataset (mode), but user can override.
- Matches data fields from the big list of env-data fields, enables them by default, and pulls units and misc info
	+ Some fields are discarded in preprocessing and won't be available: anything nominal especially
- Sketch should start zoomed to the extent of data points (but doesn't always...)
- A few options for animation size + custom res (must be done during new/edit)
- Existing animation/data can be tweaked with edit menu (animation is then rebuilt)
- Processing 2.2.1 Core
- Unfolding 0.9.6

##### 0.1.3.0 - 2014/05/23
- Rebuilding with EE 1.6
- Latest JOGL libraries (previous ones were failing in OS X)
- Some UI changes for OS X

##### 0.1.2.1 - 2014/05/21
- Added button to swap datasets. If the csv isn't formatted properly it will probably fail super ungracefully
- Changed tag id color symbology method to use an array so it will work for other data.
- Commented up the source

##### 0.1.2.0	- 2014/05/20
- Added class to parse CSV to an Unfolding Feature set. CSV needs longitude, latitude, timestamp, and tag_local_identifier, although we can make this more flexible
- Works with a modified Turkey Vulture dataset, but not very well unless you cut down the number of birds to 1 or 2. 
 
##### 0.1.1.0	- 2014/05/19
- Finished removing all Galapagos stuff
- Rebuilt the CP5 GUI in Swing, breaking backwards compatability with the Processing IDE

##### 0.1.0.0	- 2014/05/18
- Removed most of the static GalapagosSketchTest code
- Added field select dropdowns for each visual variable
- Only has one color gradient + colors for tags
