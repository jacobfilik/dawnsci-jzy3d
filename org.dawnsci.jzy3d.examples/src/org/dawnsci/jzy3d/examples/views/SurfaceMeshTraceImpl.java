package org.dawnsci.jzy3d.examples.views;

import java.util.Arrays;
import java.util.List;

import org.eclipse.dawnsci.plotting.api.histogram.HistogramBound;
import org.eclipse.dawnsci.plotting.api.histogram.IPaletteService;
import org.eclipse.dawnsci.plotting.api.histogram.ImageServiceBean;
import org.eclipse.dawnsci.plotting.api.histogram.ImageServiceBean.HistoType;
import org.eclipse.dawnsci.plotting.api.trace.IPaletteListener;
import org.eclipse.dawnsci.plotting.api.trace.IPaletteTrace;
import org.eclipse.dawnsci.plotting.api.trace.ISurfaceMeshTrace;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.AbstractColorMap;
import org.jzy3d.colors.colormaps.ColorMapGrayscale;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.builder.concrete.OrthonormalTessellator;
import org.jzy3d.plot3d.primitives.Shape;

public class SurfaceMeshTraceImpl implements ISurfaceMeshTrace, IPaletteTrace {

	//TODO Implement colormapping
	//TODO Implement proper axes (including non-linear X and Y)
	//TODO implement proper shaped axes (xy not square with fixed z)
	
	private String name;
	private Shape shape;
	private SurfaceMapper mapper;
	private IDataset data;
	
	private Object userObject;
	
	private ImageServiceBean bean;
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;

	}

	@Override
	public void setData(IDataset data, IDataset[] axes) {
		
		bean = new ImageServiceBean(data, HistoType.OUTLIER_VALUES);
		
		this.data = data;
			int x = data.getShape()[0];
			int y = data.getShape()[1];
			Range rangex = new Range(0, x-1);
	       int stepsx = x;
	       Range rangey = new Range(0,  y-1);
	       int stepsy = y;
	       
	       if (mapper != null && !Arrays.equals(mapper.getDataShape(),data.getShape())) mapper = null;
	       
	       if (mapper == null) {
	    	   mapper = new SurfaceMapper(data);
	    	   final Shape surface = Builder.buildOrthonormal(new OrthonormalGrid(rangex, stepsx, rangey, stepsy), mapper);
//		       OrthonormalTessellator t = new OrthonormalTessellator();
//		       final AbstractComposite surface = t.build(x1, x2, x3);
		       surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(), surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
		       surface.setFaceDisplayed(true);
		       surface.setWireframeDisplayed(false);
		       shape = surface;
	       } else {
	    	   mapper.updateData(data);
	    	   mapper.remap(shape);
	       }
	       
	    	   
	       
	       // Create the object to represent the function over the given range.
	       
	       
	}
	
	public Shape getShape(){
		return shape;
	}
	
	
	@Override
	public String getDataName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDataName(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public IDataset getData() {
		return data;
	}

	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setVisible(boolean isVisible) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isUserTrace() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setUserTrace(boolean isUserTrace) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getUserObject() {
		return userObject;
	}

	@Override
	public void setUserObject(Object userObject) {
		this.userObject = userObject;

	}

	@Override
	public boolean is3DTrace() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getRank() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private class SurfaceMapper extends Mapper {
		
		private IDataset data;
		
		public SurfaceMapper(IDataset data) {
			this.data = data;
		}
		
		public void updateData(IDataset data) {
			this.data = data;
		}
		
		public int[] getDataShape(){
			return data.getShape();
		}

		@Override
		public double f(double x, double y) {
			 return data.getDouble((int)x,(int)y);
		}
		
	}

	@Override
	public List<IDataset> getAxes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> getAxesNames() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
	//palette trace stuff
	
	
	@Override
	public PaletteData getPaletteData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPaletteData(PaletteData paletteData) {
		AbstractColorMap map = new AbstractColorMap() {
			
			@Override
			public Color getColor(double arg0, double arg1, double z, double zMin, double zMax) {
				double rel_value = 0;
		        
		        if( z < zMin )
		            rel_value = 0;
		        else if( z > zMax )
		            rel_value = 1;
		        else
		            rel_value = ( z - zMin ) / ( zMax - zMin );
		        
		        float b = (float) rel_value;
		        float v = (float) rel_value;
		        float r = (float) rel_value;
		        
//		return new Color( r, v, b );
				RGB rgb = paletteData.getRGB((int)(rel_value*255));
				return new Color((float)(rgb.red/255.), (float)(rgb.green/255.), (float)(rgb.blue/255.));
			}
		};
		
		shape.setColorMapper(new ColorMapper(map, shape.getBounds().getZmin(), shape.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
		
	}

	@Override
	public String getPaletteName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPaletteName(String paletteName) {
		paletteName.toString();
		
	}

	@Override
	public void setPalette(String paletteName) {
//		String orig = this.paletteName;
		IPaletteService pservice = ServiceManager.getPaletteService();
		final PaletteData paletteData = pservice.getDirectPaletteData(paletteName);
        setPaletteName(paletteName);
        setPaletteData(paletteData);
		
	}

	@Override
	public ImageServiceBean getImageServiceBean() {
		// TODO Auto-generated method stub
		return bean;
	}

	@Override
	public void addPaletteListener(IPaletteListener pl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removePaletteListener(IPaletteListener pl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Number getMin() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Number getMax() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HistogramBound getNanBound() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HistogramBound getMinCut() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HistogramBound getMaxCut() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setNanBound(HistogramBound bound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMinCut(HistogramBound bound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMaxCut(HistogramBound bound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMin(Number min) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMax(Number max) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRescaleHistogram(boolean rescaleHistogram) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isRescaleHistogram() {
		// TODO Auto-generated method stub
		return false;
	}

}
