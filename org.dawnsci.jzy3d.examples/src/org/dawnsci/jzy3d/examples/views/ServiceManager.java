package org.dawnsci.jzy3d.examples.views;

import org.eclipse.dawnsci.plotting.api.histogram.IPaletteService;

public class ServiceManager {

	private static IPaletteService paletteService;

	public static IPaletteService getPaletteService() {
		return paletteService;
	}

	public static void setPaletteService(IPaletteService paletteService) {
		ServiceManager.paletteService = paletteService;
	}
	
	
}
