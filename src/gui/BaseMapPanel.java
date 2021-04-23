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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import main.DesktopPane;
import net.miginfocom.swing.MigLayout;

import javax.swing.JRadioButton;

import de.fhpotsdam.unfolding.providers.AbstractMapProvider;
import de.fhpotsdam.unfolding.providers.EsriProvider;
import de.fhpotsdam.unfolding.providers.Google;
// import de.fhpotsdam.unfolding.providers.MapBox;
// import de.fhpotsdam.unfolding.providers.MapQuestProvider;
import de.fhpotsdam.unfolding.providers.Microsoft;
// import de.fhpotsdam.unfolding.providers.Yahoo;

@SuppressWarnings("serial")
public class BaseMapPanel extends JPanel {
	DesktopPane parent;
	public AbstractMapProvider chosenProvider;

	Map<String, AbstractMapProvider> providerList = new TreeMap<String, AbstractMapProvider>() {
		{
			put("Google Maps", new Google.GoogleMapProvider());
			put("Google Terrain", new Google.GoogleTerrainProvider());
			put("Google Maps Simple", new Google.GoogleSimplifiedProvider());
			put("Microsoft Aerial", new Microsoft.AerialProvider());
			// put("MapBox World Light", new MapBox.WorldLightProvider());
			put("ESRI World Terrain", new EsriProvider.WorldTerrain());
			put("ESRI NatGeo", new EsriProvider.NatGeoWorldMap());
			put("ESRI Ocean Basemap", new EsriProvider.OceanBasemap());
			put("ESRI World Gray Canvas", new EsriProvider.WorldGrayCanvas());
			// put("ESRI World Physical", new EsriProvider.WorldPhysical());
			put("ESRI World Shaded Relief", new EsriProvider.WorldShadedRelief());
			put("ESRI World Topo", new EsriProvider.WorldTopoMap());
			// put("Yahoo Aerial", new Yahoo.AerialProvider());
			// put("MapQuest Aerial", new MapQuestProvider.Aerial());
		}
	};

	public BaseMapPanel(DesktopPane father) {
		parent = father;

		setLayout(new MigLayout("wrap 1"));
		ButtonGroup group = new ButtonGroup();

		for (Entry<String, AbstractMapProvider> entry : providerList.entrySet()) {
			String name = entry.getKey();
			final AbstractMapProvider provider = entry.getValue();
			JRadioButton button = new JRadioButton(name);
			add(button);

			group.add(button);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					chosenProvider = provider;
					if (parent.sketch != null) {
						parent.sketch.map.mapDisplay.setProvider(provider);
						if(parent.sketch.leftMap != null) 	parent.sketch.leftMap.mapDisplay.setProvider(provider);
						if(parent.sketch.rightMap != null) 	parent.sketch.rightMap.mapDisplay.setProvider(provider);
					}
				}
			});
			if (name.equals("Microsoft Aerial")) {
				button.doClick();
			}
		}
	}
}
