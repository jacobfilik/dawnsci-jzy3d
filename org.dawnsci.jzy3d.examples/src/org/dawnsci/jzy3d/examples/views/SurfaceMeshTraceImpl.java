package org.dawnsci.jzy3d.examples.views;

import java.util.Arrays;

import org.eclipse.dawnsci.plotting.api.trace.ISurfaceMeshTrace;
import org.eclipse.january.dataset.IDataset;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.builder.concrete.OrthonormalTessellator;
import org.jzy3d.plot3d.primitives.Shape;

public class SurfaceMeshTraceImpl implements ISurfaceMeshTrace {

	private String name;
	private Shape shape;
	private SurfaceMapper mapper;
	private IDataset data;
	
	private Object userObject;
	
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

}
