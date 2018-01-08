package org.dawnsci.jzy3d.examples.views;

import java.util.Arrays;
import java.util.List;

import org.eclipse.dawnsci.plotting.api.histogram.HistogramBound;
import org.eclipse.dawnsci.plotting.api.histogram.IImageService;
import org.eclipse.dawnsci.plotting.api.histogram.IPaletteService;
import org.eclipse.dawnsci.plotting.api.histogram.ImageServiceBean;
import org.eclipse.dawnsci.plotting.api.histogram.ImageServiceBean.HistoType;
import org.eclipse.dawnsci.plotting.api.trace.IPaletteListener;
import org.eclipse.dawnsci.plotting.api.trace.IPaletteTrace;
import org.eclipse.dawnsci.plotting.api.trace.ISurfaceMeshTrace;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.FloatDataset;
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
import org.jzy3d.plot3d.primitives.AbstractComposite;
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
	
	private ColorMapper colorMapper;
	
	private PaletteData paletteData;
	private String paletteName;
	
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
		
		if (bean == null) {
			bean = new ImageServiceBean(data, HistoType.OUTLIER_VALUES);
		}
		
		IImageService imageService = ServiceManager.getImageService();
		double[] fs = imageService.getFastStatistics(bean);
		
		bean.setMin(fs[0]);
		bean.setMax(fs[1]);
		
		float max = bean.getMax().floatValue();
		float min = bean.getMin().floatValue();

		this.data = data;
		int x = data.getShape()[1];
		int y = data.getShape()[0];
		
		FloatDataset z = DatasetUtils.cast(FloatDataset.class, data);
		float[] xArray = null;
		float[] yArray = null;


		xArray = (axes != null && axes[0] != null) ? DatasetUtils.cast(FloatDataset.class, axes[0]).getData() : getRange(x);
		yArray = (axes != null && axes[1] != null) ? DatasetUtils.cast(FloatDataset.class, axes[1]).getData() : getRange(y);

		final Shape surface  = MeshTessellator.buildShape(yArray, xArray, z.getData());
			
	
		if (colorMapper == null) {
			colorMapper = new ColorMapper(new ColorMapRainbow(), min, max, new Color(1, 1, 1, .5f));
		}
		surface.setColorMapper(colorMapper);
		surface.setFaceDisplayed(true);
		surface.setWireframeDisplayed(false);
		
		shape = surface;
	       
	}
	
	private float[] getRange(int n) {
		float[] array = new float[n];
		fillRange(array);
		return array;
	}
	
	private void fillRange(float[] array) {
		
		float count = 0;
		
		for (int i = 0; i < array.length; i++) {
			
			array[i] = i;
		
//			if (i > (array.length/3) && i < 2*(array.length/3)) {
//				count +=0.1;
//			} else {
//				count++;
//			}
//			
//			array[i] = count;
			
		}
		
	}
	
	private void fillInOrder(float[] small, float[] big) {
		
		int remainder = big.length%small.length;
		
		if (remainder != 0) {
			throw new IllegalArgumentException("Size of big not compatible with small");
		}
		
		int n = big.length/small.length;
		
		for (int i = 0; i < n; i++) {
			System.arraycopy(small, 0, big, small.length*i, small.length);
		}	
	}
	
	private void fillWithRange(float[] big, int n) {
		
		int remainder = big.length%n;
		
		if (remainder != 0) {
			throw new IllegalArgumentException("Size of big not compatible with n");
		}
		
		int n2 = big.length/n;
		
		float[] small = new float[n];
		System.arraycopy(small, 0, big, 0, small.length);
		
		for (int i = 1; i < n2; i++) {
			Arrays.fill(small, i);
			System.arraycopy(small, 0, big, small.length*i, small.length);
		}
		
	}
	
	public Shape getShape(){
		return shape;
	}
	
	private Shape buildShape(IDataset data, IDataset[] axes) {
		return null;
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
		return paletteData;
	}

	@Override
	public void setPaletteData(PaletteData paletteData) {
		if (paletteData == null) return;
		this.paletteData = paletteData; 
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
		        
				RGB rgb = paletteData.getRGB((int)(rel_value*255));
				return new Color((float)(rgb.red/255.), (float)(rgb.green/255.), (float)(rgb.blue/255.));
			}
		};
		
		float zmin = shape.getBounds().getZmin();
		float zman = shape.getBounds().getZmax();
		float bmin = bean.getMin().floatValue();
		float bmax = bean.getMax().floatValue();
		
		colorMapper = new ColorMapper(map, bmin, bmax, new Color(1, 1, 1, .5f));
		
		shape.setColorMapper(colorMapper);
		
	}

	@Override
	public String getPaletteName() {
		return paletteName;
	}

	@Override
	public void setPaletteName(String paletteName) {
		this.paletteName = paletteName;
		
	}

	@Override
	public void setPalette(String paletteName) {
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
		return bean.getMin();
	}

	@Override
	public Number getMax() {
		return bean.getMax();
	}

	@Override
	public HistogramBound getNanBound() {
		return bean.getNanBound();
	}

	@Override
	public HistogramBound getMinCut() {
		return bean.getMinimumCutBound();
	}

	@Override
	public HistogramBound getMaxCut() {
		return bean.getMaximumCutBound();
	}

	@Override
	public void setNanBound(HistogramBound bound) {
		bean.setNanBound(bound);
		
	}

	@Override
	public void setMinCut(HistogramBound bound) {
		bean.setMinimumCutBound(bound);
		
	}

	@Override
	public void setMaxCut(HistogramBound bound) {
		bean.setMinimumCutBound(bound);
		
	}

	@Override
	public void setMin(Number min) {
		if (bean==null) return;
		bean.setMax(min);
		
		
	}

	@Override
	public void setMax(Number max) {
		if (bean==null) return;
		bean.setMax(max);
		
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
