package org.dawnsci.jzy3d.examples.views;

import java.util.List;

import org.eclipse.dawnsci.plotting.api.histogram.IImageService;
import org.eclipse.dawnsci.plotting.api.histogram.ImageServiceBean;
import org.eclipse.dawnsci.plotting.api.histogram.ImageServiceBean.HistoType;
import org.eclipse.dawnsci.plotting.api.trace.IWaterfallTrace;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.FloatDataset;
import org.eclipse.january.dataset.IDataset;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapGrayscale;
import org.jzy3d.plot3d.builder.concrete.WaterfallTessellator;
import org.jzy3d.plot3d.primitives.Shape;

public class WaterfallTraceImpl extends AbstractColorMapTrace implements IWaterfallTrace {


	private Shape shape;
	private IDataset data;
	
	private ColorMapper colorMapper;
	


	@Override
	public void setData(IDataset data, IDataset[] axes) {
		
		if (getImageServiceBean() == null) {
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

		WaterfallTessellator t = new WaterfallTessellator();
		Shape waterfall = t.build(xArray, yArray, z.getData());
		
			
	
		if (colorMapper == null) {
			colorMapper = new ColorMapper(new ColorMapGrayscale(), min, max, new Color(1, 1, 1, .5f));
		}
		waterfall.setColorMapper(colorMapper);
		
		shape = waterfall;
	       
	}
	
	private float[] getRange(int n) {
		float[] array = new float[n];
		fillRange(array);
		return array;
	}
	
	private void fillRange(float[] array) {

		for (int i = 0; i < array.length; i++) {

			array[i] = i;

		}

	}
	
	
	public Shape getShape(){
		return shape;
	}
	

	@Override
	public IDataset getData() {
		return data;
	}


	@Override
	protected void setColorMap(ColorMapper mapper) {
		colorMapper = mapper;
		this.shape.setColorMapper(colorMapper);
		
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

}
