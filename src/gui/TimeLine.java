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

package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import main.DesktopPane;
import main.SketchData;
import utils.Track;
import net.miginfocom.swing.MigLayout;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

import de.jaret.util.date.Interval;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;
import de.jaret.util.date.iterator.DateIterator;
import de.jaret.util.swing.GraphicsHelper;
import de.jaret.util.ui.timebars.TimeBarMarker;
import de.jaret.util.ui.timebars.TimeBarMarkerImpl;
import de.jaret.util.ui.timebars.TimeBarMarkerListener;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.model.DefaultRowHeader;
import de.jaret.util.ui.timebars.model.DefaultTimeBarModel;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.model.PPSInterval;
import de.jaret.util.ui.timebars.model.TimeBarModel;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.model.TimeBarRowHeader;
import de.jaret.util.ui.timebars.model.TimeBarSelectionModelImpl;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;
import de.jaret.util.ui.timebars.swing.renderer.DefaultMiscRenderer;
import de.jaret.util.ui.timebars.swing.renderer.DefaultTimeScaleRenderer;
import de.jaret.util.ui.timebars.swing.renderer.HeaderRenderer;
import de.jaret.util.ui.timebars.swing.renderer.IMarkerRenderer;
import de.jaret.util.ui.timebars.swing.renderer.TimeBarRenderer;

public class TimeLine extends JPanel implements ComponentListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private DesktopPane parent;
	private SketchData data;
	public static final List<DefaultRowHeader> _headerList = new ArrayList<DefaultRowHeader>();
	public TimeBarViewer tbv;
	Double seconds;
	Double ppi;
	public TimeBarMarkerImpl marker1;

	public DateTime originalStartTime;
	public DateTime originalEndTime;

	public TimeBarMarkerImpl beginRangeMarker;
	public TimeBarMarkerImpl endRangeMarker;

	public TimeLine(DesktopPane father) {
		parent = father;
		data = parent.data;

		originalStartTime = data.startTime;
		originalEndTime = data.endTime;

		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		setLayout(new MigLayout("", "[grow]", "[grow]"));
		TimeBarModel model = tbModel(parent.tagList.size(), 1);
		seconds = (double) Seconds.secondsBetween(data.startTime, data.endTime).getSeconds();
		ppi = 830 / seconds;
		tbv = new TimeBarViewer(model, true, true);
		tbv.setTimeScaleRenderer(new CustomTimeScaleRenderer());
		tbv.setTimeBarRenderer(new EventRenderer());
		tbv.setMarkerRenderer(new CustomMarkerRenderer());
		tbv.setHeaderRenderer(new CustomHeaderRenderer());
		tbv.setMiscRenderer(new CustomMiscRenderer());
		tbv.setSelectionModel(new CustomTimeBarSelectionModel());
		tbv.getSelectionModel().setMultipleSelectionAllowed(false);
		tbv.setHierarchyRenderer(null);
		tbv.setTimeScalePosition(TimeBarViewerInterface.TIMESCALE_POSITION_BOTTOM);
		tbv.setPixelPerSecond(ppi);
		tbv.setRowHeight(15);
		tbv.setDrawRowGrid(false);
		tbv.setAdjustMinMaxDatesByModel(false);
		tbv.setMinDate(new JaretDate(data.startTime.toDate()));
		tbv.setStartDate(new JaretDate(data.startTime.toDate()));
		tbv.setMaxDate(new JaretDate(data.endTime.toDate()));
		tbv.setMilliAccuracy(false);
		marker1 = new TimeBarMarkerImpl(true, new JaretDate(data.currentTime.toDate()));
		marker1.addTimeBarMarkerListener(new MarkerListener());
		tbv.addMarker(marker1);
		tbv.setMarkerDraggingInDiagramArea(true); //makes marker draggable so can select on timeline itself rather than seek bar

		if (data.timeRange) {
			addRangeMarkers();
		}
		this.add(tbv, "cell 0 0, grow");
		addComponentListener(this);

		for (int i = 0; i < model.getRowCount(); i++) {
			tbv.getSelectionModel().setSelectedRow(model.getRow(i));
		}
	}

	public void setSeekSliderValue() {
		if (Minutes.minutesBetween(data.startTime, data.endTime).getMinutes() > 0) {
			data.seek = Minutes.minutesBetween(data.startTime, data.currentTime).getMinutes();
		} else {
			data.seek = Seconds.secondsBetween(data.startTime, data.currentTime).getSeconds();
		}
		data.seek = data.seek / data.dataInterval;
		parent.controlPanel.seek.setValue(data.seek);
	}

	public void setSeekSliderRange(){
		if (Minutes.minutesBetween(data.startTime, data.endTime).getMinutes() > 0) {
			data.totalTime = Minutes.minutesBetween(data.startTime, data.endTime).getMinutes();
		} else {
			data.totalTime = Seconds.secondsBetween(data.startTime, data.endTime).getSeconds();
		}
		parent.controlPanel.seek.setMaximum((data.totalTime / data.dataInterval));
	}

	public void setLimitedTimeRange(DateTime rangeStart, DateTime rangeEnd) {
		data.startTime = rangeStart;
		data.endTime = rangeEnd;

		setSeekSliderRange();
		setSeekSliderValue();
	}

	public void resetTimeRange() {
		data.startTime = originalStartTime;
		data.endTime = originalEndTime;

		setSeekSliderRange();
		setSeekSliderValue();
	}

	public void setTimeSelectable() {
		data.daysSelectable = Days.daysBetween(data.startTime.toLocalDate(), data.endTime.toLocalDate()).getDays();
	}

	public void setTimeSelectableFromSpinners(int months, int weeks, int days) {
		int numDays = 0;
		numDays+= (int)months*30.437; //avg num days in a month
		numDays+= weeks*7;
		numDays+=days;
		data.daysSelectable = numDays;
		moveEndRangeMarker(numDays);
	}

	public void moveEndRangeMarker(int days) {
		DateTime newEndDate = data.startTime.plusDays(days);
		data.endTime = newEndDate;
		JaretDate markerDate = new JaretDate(newEndDate.toDate());
		if (tbv.getMarkers().size() > 1) {
			endRangeMarker.setDate(markerDate);
		}
	}

	public void addRangeMarkers() {
		DateTime[] selectableRange = getSelectionBounds();
			
		beginRangeMarker = new TimeBarMarkerImpl(true, new JaretDate(selectableRange[0].toDate()));
		endRangeMarker = new TimeBarMarkerImpl(true, new JaretDate(selectableRange[1].toDate()));
		beginRangeMarker.setDescription("rangeMarker");
		beginRangeMarker.addTimeBarMarkerListener(new RangeStartMarkerListener());
		endRangeMarker.setDescription("rangeMarker");
		endRangeMarker.addTimeBarMarkerListener(new RangeEndMarkerListener());
		tbv.remMarker(marker1);
		List<TimeBarMarker> markers = new ArrayList<>();
		markers.add(beginRangeMarker);
		markers.add(endRangeMarker);
		markers.add(marker1);
		tbv.addMarkers(markers);

		setLimitedTimeRange(selectableRange[0], selectableRange[1]);
	}

	public void removeRangeMarkers() { 
		List<TimeBarMarker> markers = tbv.getMarkers();
		while (markers.size() >1) {
			if (markers.get(0).getDescription() == "rangeMarker") {
				tbv.remMarker(markers.get(0));
			}
			markers = tbv.getMarkers();
		}
		resetTimeRange();
	}

	public DateTime[] getSelectionBounds() {
		DateTime newEnd = data.currentTime.plusDays(data.daysSelectable);
		if (newEnd.isAfter(data.endTime)){
			newEnd = data.endTime;
		}

		return new DateTime[] {data.currentTime, newEnd};
	}

	public class RangeStartMarkerListener implements TimeBarMarkerListener {
		@Override
		public void markerMoved(TimeBarMarker marker, JaretDate oldDate, JaretDate currentDate) {
			DateTime startTimeConverted = new DateTime(currentDate.getDate());
			if (startTimeConverted.isAfter(data.endTime)) {
				marker.setDate(new JaretDate(data.endTime.minusDays(1).toDate()));
			}

			DateTime desiredEndTime = startTimeConverted.plusDays(data.daysSelectable);
			if ((desiredEndTime.isBefore(data.endTime) || desiredEndTime.isAfter(data.endTime)) && data.moveRangeTogether) {
				data.endTime = desiredEndTime;
				endRangeMarker.setDate(new JaretDate(data.endTime.toDate()));
			}

			setLimitedTimeRange(startTimeConverted, data.endTime);
			if (marker1.getDate().compareTo(currentDate) <0){
				marker1.setDate(currentDate);
			}
		}

		@Override
		public void markerDescriptionChanged(TimeBarMarker marker, String oldValue, String newValue) {
		}
	}

	public class RangeEndMarkerListener implements TimeBarMarkerListener {
		@Override
		public void markerMoved(TimeBarMarker marker, JaretDate oldDate, JaretDate currentDate) {
			DateTime endTimeConverted = new DateTime(currentDate.getDate());
			DateTime desiredStartTime = endTimeConverted.minusDays(data.daysSelectable);

			if (endTimeConverted.isBefore(data.startTime)) {
				marker.setDate(new JaretDate(data.startTime.plusDays(1).toDate()));
			}
			if ((desiredStartTime.isAfter(data.startTime) || desiredStartTime.isBefore(data.startTime)) && data.moveRangeTogether){
				data.startTime = desiredStartTime;
				beginRangeMarker.setDate(new JaretDate(data.startTime.toDate()));
			}
			setLimitedTimeRange(data.startTime,endTimeConverted);

			if (marker1.getDate().compareTo(currentDate) >0){
				marker1.setDate(currentDate);
			}
		}

		@Override
		public void markerDescriptionChanged(TimeBarMarker marker, String oldValue, String newValue) {
		}
	}

	public class MarkerListener implements TimeBarMarkerListener {

		@Override
		public void markerMoved(TimeBarMarker marker, JaretDate oldDate, JaretDate currentDate) {
			DateTime currentConverted = new DateTime(currentDate.getDate());
			if (currentConverted.isBefore(data.startTime)) {
				marker.setDate(new JaretDate(data.startTime.toDate()));
			}
			else if (currentConverted.isAfter(data.endTime)) { 
				marker.setDate(new JaretDate(data.endTime.toDate()));
			} else {
				DateTime time = new DateTime(currentDate.getDate());
				parent.data.currentTime = time;
			}
			setSeekSliderValue();
		}

		@Override
		public void markerDescriptionChanged(TimeBarMarker marker, String oldValue, String newValue) {
		}

	}

	public class CustomTimeBarSelectionModel extends TimeBarSelectionModelImpl {
		public void setSelectedRow(TimeBarRow row) {
			if (row != null) {
				if (isSelected(row)) {
					_selectedRows.remove(row);
					changeVisibility(row.getRowHeader(), false);
				} else {
					_selectedRows.add(row);
					changeVisibility(row.getRowHeader(), true);
				}
				fireSelectionChanged();
			}
		}

		private void changeVisibility(TimeBarRowHeader rowHeader, boolean b) {
			for (Entry<String, Track> entry : parent.trackList.entrySet()) {
				if (entry.getKey() == rowHeader.toString()) {
					Track track = entry.getValue();
					track.setVisibility(b);
				}
			}

		}

		public void clearSelection() {
			if (!isEmpty()) {
				_selectedIntervals.clear();
				parent.data.brushedTag = null;
				_selectedRows.clear();
				// _selectedRelations.clear();
				fireSelectionChanged();
			}
		}

		public void setSelectedInterval(Interval interval) {
			if (_intervalSelectAllow) {
				boolean hasSelection = hasIntervalSelection();
				_selectedIntervals.clear();
				_selectedIntervals.add(interval);
				parent.data.brushedTag = ((EventInterval) interval).getTitle();
				if (hasSelection) {
					fireSelectionChanged();
				} else {
					fireElementAdded(interval);
				}
			}
		}
	}

	public class CustomMarkerRenderer implements IMarkerRenderer {
		protected Color _draggedColor = new Color(0, 0, 255, 150);
		protected Color _markerColor =  new Color(255, 234, 0, 255);
		protected Color _rangeMarkerColor = new Color(255, 0, 0, 150);
		protected BasicStroke _rangeStroke = new BasicStroke(5f);
		protected BasicStroke _markerStroke = new BasicStroke(2.5f);

		public int getMarkerWidth(TimeBarMarker marker) {
			return 50;
		}

		public void drawRangeHighlight(Graphics gc, JaretDate startDate, JaretDate endDate) {
			int startX = tbv.xForDate(startDate);
			int endX = tbv.xForDate(endDate);

			gc.setColor(new Color(150, 150, 150, 25));
			gc.fillRect(startX, 0, endX-startX, getHeight());
		}

		public void renderMarker(TimeBarViewerDelegate delegate, Graphics graphics, TimeBarMarker marker, int x,
				boolean isDragged) {
			Graphics2D g2 = (Graphics2D) graphics;
			Stroke oldStroke = g2.getStroke();
			Stroke stroke = _markerStroke;

			if (data.timeRange) {
				drawRangeHighlight(g2,beginRangeMarker.getDate(), endRangeMarker.getDate());
			}

			if (marker.getDescription() == "rangeMarker"){
				stroke = _rangeStroke;
			}
			g2.setStroke(stroke);
			Color oldCol = g2.getColor();
			if (isDragged) {
				g2.setColor(_draggedColor);
			} else {
				if (marker.getDescription() == "rangeMarker") {
					g2.setColor(_rangeMarkerColor);
				} else {
					g2.setColor(_markerColor);
				}
			}
			Line2D line = new Line2D.Double(x, 0, x, delegate.getDiagramRect().height + delegate.getXAxisHeight());
			g2.draw(line);
			if (marker.getDescription() == "rangeMarker") {
				g2.setColor(_rangeMarkerColor);
			} else {
				g2.setColor(oldCol);
			}
			g2.setStroke(oldStroke);
		}
	}

	@Override
	public void componentResized(ComponentEvent e) {
		ppi = this.getWidth() / seconds;
		tbv.setPixelPerSecond(ppi);
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {

	}

	@Override
	public void componentMoved(ComponentEvent arg0) {

	}

	@Override
	public void componentShown(ComponentEvent arg0) {

	}

	public void setMarker(DateTime currentTime) {
		marker1.setDate(new JaretDate(currentTime.toDate()));
	}

	public TimeBarModel tbModel(int rows, int countPerRow) {
		DefaultTimeBarModel model = new DefaultTimeBarModel();

		for (String i : parent.tagList) {
			Track track = parent.trackList.get(i);
			String tag = track.getTag();
			if (track.getVisibility()) {
				DefaultRowHeader header = new DefaultRowHeader(tag);
				_headerList.add(header);
				DefaultTimeBarRowModel tbr = new DefaultTimeBarRowModel(header);
				Date dateStart = track.getStartDate().toDate();
				Date dateEnd = track.getEndDate().toDate();
				EventInterval interval = new EventInterval(new JaretDate(dateStart), new JaretDate(dateEnd));
				interval.setTitle(tag);
				tbr.addInterval(interval);
				model.addRow(tbr);
			}
		}
		return model;
	}

	public class CustomHeaderRenderer implements HeaderRenderer {
		/** component used for rendering. */
		private JCheckBox _component = new JCheckBox();

		private final int _width;

		/**
		 * Construct a DefaultHeaderRenderer using the default width (35).
		 */
		public CustomHeaderRenderer() {
			this(35);
		}

		/**
		 * Construct a DefaultHeaderRenderer with a configured width.
		 * 
		 * @param width width to be used
		 */
		public CustomHeaderRenderer(int width) {
			_width = width;
		}

		/**
		 * {@inheritDoc}
		 */
		public JComponent getHeaderRendererComponent(TimeBarViewer tbv, TimeBarRowHeader value, boolean isSelected) {
			_component.setName(value.toString());
			_component.setToolTipText("Toggle Visiblity");
			_component.setSelected(isSelected);
			_component.setEnabled(true);
			return _component;
		}

		/**
		 * {@inheritDoc}
		 */
		public int getWidth() {
			return _width;
		}
	}

	public class CustomMiscRenderer extends DefaultMiscRenderer {

		public void drawRowBackground(Graphics graphics, int x, int y, int width, int height, boolean selected,
				boolean highlighted) {
		}
	}

	public class CustomTimeScaleRenderer extends DefaultTimeScaleRenderer {

		public int getHeight() {
			return 40;
		}

		protected MyTimeScaleRenderer _renderer = new MyTimeScaleRenderer();

		public JComponent getRendererComponent(TimeBarViewer tbv, boolean top) {
			_renderer.setTimeBarViewer(tbv);
			_renderer.setTop(top);
			return _renderer;
		}

		@SuppressWarnings("serial")
		class MyTimeScaleRenderer extends JComponent {
			protected static final int BOXHEIGHT = 20;
			protected static final int ADDITIONALGAP = 5;
			protected static final int GAP = 1;
			protected static final int SETBONUS = 5;
			private double _lastPPS = -1;
			protected DateIterator _midStrip;
			protected DateIterator _upperStrip;
			protected DateIterator _lowerStrip;
			private TimeBarViewer _tbv;
			private TimeBarViewerDelegate _delegate;
			boolean _top;

			public MyTimeScaleRenderer() {
				super();
				initIterators();
			}

			public void setTimeBarViewer(TimeBarViewer tbv) {
				_tbv = tbv;
				_delegate = _tbv.getDelegate();
			}

			public void setTop(boolean top) {
				_top = top;
			}

			private int xForDate(JaretDate date) {
				int x = _tbv.xForDate(date);
				x -= _tbv.getHierarchyWidth() + _tbv.getYAxisWidth();
				return x;
			}

			public void paintComponent(Graphics graphics) {
				// if pps changed check which stripes to draw
				if (_lastPPS != _delegate.getPixelPerSecond()) {
					checkStrips(graphics, _delegate, _delegate.getStartDate());
					_lastPPS = _delegate.getPixelPerSecond();
				}

				// int lineWidth = graphics.getLineWidth();

				// each drawing operation produces new tick dates
				_majorTicks = new ArrayList<JaretDate>();
				_minorTicks = new ArrayList<JaretDate>();

				if (!_delegate.hasVariableXScale()) {
					// plain scale
					// +1 second for millisecond scales since the getSecondsDisplayed method rounds
					// to nearest lower second
					// count
					drawStrips(graphics, _delegate, _top, _delegate.getStartDate().copy(),
							_delegate.getStartDate().copy().advanceSeconds(_delegate.getSecondsDisplayed() + 1));
				} else {
					// check strips for every part with different scale
					JaretDate startDate = _delegate.getStartDate().copy();
					JaretDate endDate = _delegate.getStartDate().copy().advanceSeconds(_delegate.getSecondsDisplayed());
					List<Interval> ppsIntervals = _delegate.getPpsRow().getIntervals(startDate, endDate);
					// shortcut if no ppsintervals are in the area just draw straight
					if (ppsIntervals.size() == 0) {
						drawStrips(graphics, _delegate, _top, _delegate.getStartDate().copy(),
								_delegate.getStartDate().copy().advanceSeconds(_delegate.getSecondsDisplayed() + 1)); // +1
																														// ->
																														// see
																														// above
					} else {
						JaretDate d = startDate.copy();
						while (d.compareTo(endDate) < 0) {
							// calculate the strips for the current date in question
							checkStrips(graphics, _delegate, d);
							PPSInterval ppsInterval = _delegate.getPPSInterval(d);
							JaretDate e;
							if (ppsInterval != null) {
								e = ppsInterval.getEnd();
							} else {
								PPSInterval nextInterval = _delegate.nextPPSInterval(d);
								if (nextInterval != null) {
									e = nextInterval.getBegin();
								} else {
									e = endDate;
								}
							}
							// only draw if the interval is not a break
							if (ppsInterval == null || !ppsInterval.isBreak()) {
								drawStrips(graphics, _delegate, _top, d, e);
							} else {

							}
							d = e;
						}
						_lastPPS = -1; // force check next paint
					}
				}
			}

			private void drawStrips(Graphics gc, TimeBarViewerDelegate delegate, boolean top, JaretDate startDate,
					JaretDate endDate) {

				int basey;
				int minorOff;
				int majorOff;
				int majorLabelOff;
				int dayOff;
				if (!_top) {
					basey = 0;
					minorOff = 5;
					majorOff = 11;
					majorLabelOff = 22;
					dayOff = 34;
				} else {
					basey = getHeight() - 1;
					minorOff = -5;
					majorOff = -11;
					majorLabelOff = -10;
					dayOff = -22;
				}
				int oy = basey;

				// draw top line
				gc.drawLine(0, oy, getWidth(), oy);
				Font f = new Font("Arial", Font.PLAIN, 10);
				gc.setFont(f);

				if (_lowerStrip != null) {
					DateIterator it = _lowerStrip;
					it.reInitialize(startDate, endDate.copy().advanceMillis(it.getApproxStepMilliSeconds()));
					while (it.hasNextDate()) {
						JaretDate d = it.getNextDate();
						_minorTicks.add(d);
						int x = xForDate(d);
						gc.drawLine(x, oy, x, oy + minorOff);
					}
				}
				if (_midStrip != null) {
					DateIterator it = _midStrip;
					it.reInitialize(startDate, endDate.copy().advanceMillis(it.getApproxStepMilliSeconds()));
					while (it.hasNextDate()) {
						JaretDate d = it.getNextDate();

						_majorTicks.add(d);

						int x = xForDate(d);
						gc.drawLine(x, oy, x, oy + majorOff);
						// label every two major ticks
						String label = it.getLabel(d, DateIterator.Format.LONG);
						GraphicsHelper.drawStringCentered(gc, label, x, oy + majorLabelOff);
					}
				}

				// draw upper part
				if (_upperStrip != null) {
					DateIterator it = _upperStrip;
					it.reInitialize(startDate, endDate.copy().advanceMillis(it.getApproxStepMilliSeconds()));
					while (it.hasNextDate()) {
						JaretDate d = it.getNextDate();
						int x = xForDate(d);

						gc.drawLine(x, oy, x, oy + majorOff);
						String label = it.getLabel(d, DateIterator.Format.LONG);
						GraphicsHelper.drawStringCentered(gc, label, x, oy + dayOff);
					}
				}
			}

			private void checkStrips(Graphics gc, TimeBarViewerDelegate delegate, JaretDate startDate) {
				for (int i = 0; i < _iterators.size(); i++) {
					DateIterator it = _iterators.get(i);
					it.reInitialize(startDate, null);
					if (it.previewNextDate() != null) {
						JaretDate current = it.getNextDate();
						JaretDate next = it.getNextDate();
						int width = xForDate(next) - xForDate(current);
						String label = it.getLabel(current, DateIterator.Format.LONG);
						Rectangle2D rect = gc.getFontMetrics().getStringBounds(label, gc);
						int bonus = _midStrip == it && _formats.get(i).equals(DateIterator.Format.LONG) ? SETBONUS : 0;
						if (width > rect.getWidth() + GAP + ADDITIONALGAP - bonus) {
							_midStrip = it;
							_upperStrip = _upperMap.get(_midStrip);
							if (i > 0) {
								_lowerStrip = _iterators.get(i - 1);
							}
							break;
						}
					}
				}
			}
		}
	}

	public class EventInterval extends IntervalImpl {
		private String _title;

		public EventInterval(JaretDate from, JaretDate to) {
			super(from, to);
		}

		public String getTitle() {
			return _title;
		}

		public void setTitle(String title) {
			_title = title;
		}

		@Override
		public String toString() {
			return _title + ":" + super.toString();
		}

	}

	public class EventRenderer implements TimeBarRenderer {

		protected JButton _component = new JButton();
		String tag;

		public EventRenderer() {
			_component = new JButton();
		}

		public JComponent getTimeBarRendererComponent(TimeBarViewer tbv, Interval value, boolean isSelected,
				boolean overlapping) {
			EventInterval t = (EventInterval) value;
			tag = t.getTitle();
			return defaultGetTimeBarRendererComponent(tbv, value, isSelected, overlapping);
		}

		public JComponent defaultGetTimeBarRendererComponent(TimeBarViewer tbv, Interval value, boolean isSelected,
				boolean overlapping) {
			_component.setText(tag);
			_component.setToolTipText(tag);

			for (Entry<String, Track> entry : parent.trackList.entrySet()) {
				if (entry.getKey().equals(tag)) {
					Track track = entry.getValue();
					if (track.getVisibility()) {
						_component.setForeground(Color.BLACK);
						Color color = parent.colors.getTagColor(tag);
						_component.setBackground(color);
						_component.setBorder(new LineBorder(new Color(160, 160, 160, 100), 1));
						_component.setFont(_component.getFont().deriveFont(Font.BOLD));
					} else {
						_component.setForeground(new Color(160, 160, 160));
						_component.setBackground(new Color(240, 240, 240));
						_component.setBorder(new LineBorder(new Color(160, 160, 160, 100), 0));
						_component.setFont(_component.getFont().deriveFont(Font.ITALIC));
					}
				}
			}

			if (isSelected) {
				_component.setBorder(new LineBorder(new Color(0, 255, 255), 2));
			}
			return _component;
		}

		public Rectangle getPreferredDrawingBounds(Rectangle intervalDrawingArea, TimeBarViewerDelegate delegate,
				Interval interval, boolean selected, boolean overlap) {
			return intervalDrawingArea;
		}

	}

}
