package org.dawnsci.jzy3d.examples.views;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.chart.Settings;
import org.jzy3d.chart.swt.SWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.io.IGLLoader;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Normal;
import org.jzy3d.maths.PlaneAxis;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.builder.concrete.OrthonormalTessellator;
import org.jzy3d.plot3d.primitives.AbstractComposite;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.CompileableComposite;
import org.jzy3d.plot3d.primitives.CroppableLineStrip;
import org.jzy3d.plot3d.primitives.LineStrip;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.TesselatedPolygon;
import org.jzy3d.plot3d.primitives.textured.DrawableTexture;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.lights.Light;
import org.jzy3d.plot3d.rendering.textures.SharedTexture;
import org.jzy3d.plot3d.rendering.view.modes.CameraMode;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;

import com.jogamp.common.util.JarUtil;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLProfile;

import uk.ac.diamond.scisoft.analysis.io.LoaderFactory;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.analysis.polynomials.PolynomialsUtils;
import org.dawnsci.isosurface.alg.MarchingCubes;
import org.dawnsci.isosurface.alg.MarchingCubesModel;
import org.dawnsci.isosurface.alg.Surface;
import org.dawnsci.isosurface.alg.Triangle;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dawnsci.analysis.api.io.IDataHolder;
import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.ILazyDataset;
import org.eclipse.january.dataset.Random;
import org.eclipse.january.dataset.SliceND;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class JZY3dView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.dawnsci.jzy3d.examples.views.JZY3dView";

	private TableViewer viewer;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;
	
	private Composite parent;
	 

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

/**
	 * The constructor.
	 */
	public JZY3dView() {
	}

	
	private Polygon getTriangle(org.dawnsci.isosurface.alg.Point a, org.dawnsci.isosurface.alg.Point b, org.dawnsci.isosurface.alg.Point c) {
		
		Polygon p = new Polygon();
		p.add(getFromPoint(a));
		p.add(getFromPoint(b));
		p.add(getFromPoint(c));
		p.setFaceDisplayed(true);
		p.setWireframeColor(Color.BLACK);
		
		return p;
		
	}
	
	private Point getFromPoint(org.dawnsci.isosurface.alg.Point p) {
		return new Point(new Coord3d(p.getxCoord(), p.getyCoord(), p.getzCoord()),Color.BLACK);
	}
	
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		this.parent = parent;
		try {
//			IDataHolder dh = LoaderFactory.getData("/home/jacobfilik/Work/data/exampleFPA.nxs");
//			final Dataset d = DatasetUtils.sliceAndConvertLazyDataset(dh.getLazyDataset("/ftir1/map/data"));
			
			
//			AbstractDrawable v = getVBO();
			
//			v.setGeometry(GL.GL_POINTS);
			
//			Shape test = new Shape();
//			
//			for (Triangle t : triangles) {
//				test.add(getTriangle(t.getA(), t.getB(), t.getC()));
//						
//			}
//			
//			CompileableComposite c = new CompileableComposite();
//			c.add(test);
//			
//			dh.toString();
			
//			s.
		
//		Mapper mapper = new Mapper() {
//           @Override
//           public double f(double x, double y) {
//               return d.getDouble((int)x,(int)y);
//           }
//       };
//       
//       int x = 128;
//       int y = 128;
       
       

//       // Define range and precision for the function to plot
//       Range rangex = new Range(0, x-1);
//       int stepsx = x;
//       Range rangey = new Range(0,  y-1);
//       int stepsy = y;
//       
//       float[] x1 = new float[]{0,0,0.5f,0.5f,0.75f,0.75f};
//       float[] x2 = new float[]{0,1,0,1,0,1};
//       float[] x3 = new float[]{0,0,0,1,1,1};
//       // Create the object to represent the function over the given range.
//       
//       final Shape surface = Builder.buildOrthonormal(new OrthonormalGrid(rangex, stepsx, rangey, stepsy), mapper);
//       OrthonormalTessellator t = new OrthonormalTessellator();
////       final AbstractComposite surface = t.build(x1, x2, x3);
//       surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(), surface.getBounds().getZmax()/4, new Color(1, 1, 1, .5f)));
//       surface.setFaceDisplayed(true);
//       surface.setWireframeDisplayed(false);

       // Create a chart
       
		
       
////		List<AbstractDrawable> strip = new ArrayList<AbstractDrawable>();
////		
//		
//		float off = 0f;
//		Color w = new Color(1, 1, 1, 1);
//		for (int i = 0; i < 12; i++) {
//       
//			Polygon poly = new Polygon();
////			LineStrip poly = new LineStrip();
//			poly.add(new Point(new Coord3d(0, i, 0),Color.BLACK));
////			CroppableLineStrip s = new CroppableLineStrip();
//			for (int j = 0; j < 128; j++) {
//				Color c = d.getDouble(i,j)+2 > 5 ? Color.BLUE : Color.BLACK;
//				Coord3d c0 = new Coord3d(j, i, 0+off);
//				Point p0 = new Point(c0, Color.WHITE);
//				Coord3d c1 = new Coord3d(j, i, d.getDouble(i,j)+2-off);
//				Point p1 = new Point(c1, Color.WHITE);
//				Coord3d c2 = new Coord3d(j+1, i, d.getDouble(i,j+1)+2-off);
//				Point p2 = new Point(c2, Color.WHITE);
//				Coord3d c3 = new Coord3d(j+1, i, 0+off);
//				Point p3 = new Point(c3, Color.WHITE);
//				TesselatedPolygon p = new TesselatedPolygon(new Point[]{p0,p1,p2,p3});
//				poly.add(new Point(new Coord3d(j, i, d.getDouble(i,j)+2),c));
//				
////				
////				Coord3d c = new Coord3d(i, j, d.getDouble(i,j)+2);
////				Point p = new Point(c);
////				p.setColor(Color.WHITE);
////				s.add(new Point(c));
////				p.setWireframeColor(Color.WHITE);
////				p.setWireframeWidth(2);
//				List<AbstractDrawable> drawables = p.getDrawables();
//				for (AbstractDrawable dr : drawables) {
//					if (dr instanceof Polygon) {
//						((Polygon)dr).setPolygonOffsetFactor(1);
//						((Polygon)dr).setPolygonOffsetUnit(1);
//						((Polygon)dr).setPolygonOffsetFillEnable(true);
//					}
//				}
//				strip.add(p);
//			}
//			
//			poly.add(new Point(new Coord3d(128, i, d.getDouble(i,128)+2),Color.BLACK));
//			poly.add(new Point(new Coord3d(128, i, 0),Color.BLACK));
//			poly.add(new Point(new Coord3d(0, i, 0),Color.BLACK));
////			poly.setShowPoints(true);
////			poly.setWireframeColor(Color.BLUE);
//			poly.setFaceDisplayed(true);
//			poly.setWireframeColor(Color.BLACK);
//			poly.setPolygonOffsetFactor(-1);
//			poly.setPolygonOffsetUnit(-1);
//			poly.setPolygonOffsetFillEnable(true);
////			surface.setFaceDisplayed(true);
//		       poly.setWireframeDisplayed(true);
//		       poly.setWireframeWidth(2f);
////		       poly.setWidth(2f);
////			 poly.setColorMapper(new ColorMapper(new ColorMapRainbow(), poly.getBounds().getZmin(), poly.getBounds().getZmax()/4, new Color(1, 1, 1, .5f)));
//			strip.add(poly);
////			poly.setWireframeColor(Color.BLACK);
////			s.setFaceDisplayed(true);
////			strip.add(s);
//		}
//		
       Settings.getInstance().setHardwareAccelerated(true);
       Quality q = Quality.Advanced;
//       q.setSmoothPolygon(true);
//       q.setSmoothPoint(true);
//       q.setSmoothEdge(true);
//       q.setDepthActivated(true);
////       q.setAlphaActivated(false);
       
//       Light l = new Light();
//       l.setPosition(new Coord3d(0,0,0));
//       l.setAmbiantColor(Color.MAGENTA);
//       l.setDiffuseColor(Color.WHITE);
//       l.setEnabled(true);
//       l.setRepresentationRadius(1);
       
       
		Chart chart = SWTChartComponentFactory.chart(parent, q);
//		chart.getScene().getGraph().add(test);
////		chart.getView().setSquared(false);
//		
//		chart.getView().setCameraMode(CameraMode.PERSPECTIVE);
//		chart.getScene().add(l);
       chart.getScene().getGraph().add(getTexture());
//       
//       chart.getAxeLayout().setXAxeLabel("cake");
//       chart.getAxeLayout().setYAxeLabel("sweets");
//       chart.getAxeLayout().setZAxeLabel("fruit");
//      
       
       ChartLauncher.openChart(chart);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				JZY3dView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				showMessage("Double-click detected on "+obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"JZY3dView",
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		parent.setFocus();
//		viewer.getControl().setFocus();
	}
	
	private AbstractDrawable getVBO() {
		IDataHolder dh;
		try {
			dh = LoaderFactory.getData("/dls/science/groups/das/ExampleData/fun volumes/miro_test2.nxs02_savu.plugins.astra_recon_cpu_tomo.h5");
		
		final ILazyDataset ld = dh.getLazyDataset("/2-AstraReconCpu/data");
		
		SliceND sl = new SliceND(ld.getShape());
		sl.setSlice(0, 200, 600, 1);
		sl.setSlice(2, 200, 600, 1);
		
		final ILazyDataset fld = ld.getSliceView(sl);
		
		MarchingCubesModel model = new MarchingCubesModel(fld, (List<? extends IDataset> )null, 0.0025, new int[] {4,4,4}, new int[] {0,0,0}, 0, "isosurface_trace");
		
		MarchingCubes cubes = new MarchingCubes(model);
		
		
		
		Surface s = cubes.execute(new IProgressMonitor() {
			
			@Override
			public void worked(int work) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void subTask(String name) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setTaskName(String name) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setCanceled(boolean value) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean isCanceled() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void internalWorked(double work) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void done() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beginTask(String name, int totalWork) {
				// TODO Auto-generated method stub
				
			}
		});
		
		final Set<Triangle> triangles = s.getTriangles();
		
		DrawableVBO v = new DrawableVBO(new IGLLoader<DrawableVBO>() {
			
			@Override
			public void load(GL gl, DrawableVBO drawable) throws Exception {
				
				int n = triangles.size();
				int components = 3; // x, y, z
		        int geomsize = 3; // triangle
		        
		        // Allocate content
		        FloatBuffer vertices = FloatBuffer.allocate(n * (components*2) * geomsize); // *2 normals
		        IntBuffer indices = IntBuffer.allocate(n * geomsize);
		        BoundingBox3d bounds = new BoundingBox3d();
		        
		        // Feed buffers
		        int size = 0;
		        for (Triangle t : triangles) {
		        
		            Coord3d c1 = new Coord3d(t.getA().getxCoord(),t.getA().getyCoord(),t.getA().getzCoord());
		            Coord3d c2 = new Coord3d(t.getB().getxCoord(),t.getB().getyCoord(),t.getB().getzCoord());
		            Coord3d c3 = new Coord3d(t.getC().getxCoord(),t.getC().getyCoord(),t.getC().getzCoord());
		            Coord3d no = Normal.compute(c1, c2, c3);
		            
		            indices.put(size++);
		            vertices.put(c1.x);
		            vertices.put(c1.y);
		            vertices.put(c1.z);
		            vertices.put(no.x);
		            vertices.put(no.y);
		            vertices.put(no.z);            
		            bounds.add(c1);
		            
		            indices.put(size++);
		            vertices.put(c2.x);
		            vertices.put(c2.y);
		            vertices.put(c2.z);
		            vertices.put(no.x);
		            vertices.put(no.y);
		            vertices.put(no.z);
		            bounds.add(c2);

		            indices.put(size++);
		            vertices.put(c3.x);
		            vertices.put(c3.y);
		            vertices.put(c3.z);
		            vertices.put(no.x);
		            vertices.put(no.y);
		            vertices.put(no.z);            
		            bounds.add(c3);
		        }
		        vertices.rewind();
		        indices.rewind();
		        
		        // Store in GPU
		        drawable.setData(gl, indices, vertices, bounds);
				
				
			}
		});
		
		v.setColor(Color.GRAY);
//		v.setGeometry(GL.GL_POINTS);
		
		return v;
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	private AbstractDrawable getTexture() {
		Coord3d position = new Coord3d(2.0, 2.0, 2.0);
        SharedTexture bgMask = new SharedTexture("/scratch/2.png");

        float width = 2.0f;
        
        List<Coord2d> zmapping  = new ArrayList<Coord2d>(4);
        zmapping.add( new Coord2d(position.x-width, position.y-width) );
        zmapping.add( new Coord2d(position.x+width, position.y-width) );
        zmapping.add( new Coord2d(position.x+width, position.y+width) );
        zmapping.add( new Coord2d(position.x-width, position.y+width) );
        
        
        DrawableTexture north = new DrawableTexture(bgMask, PlaneAxis.Z, position.z + width, zmapping);
        north.setAlphaFactor(1f);
        
        return north;
	}
	
}
