package sparks.graphics;
import sparks.shared.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.AffineTransform;
import java.awt.event.*;

public class ScreenManager implements ComponentListener{
	public Frame screen;
	public int winwidth;
	public int winheight;
	public int mouseeventXoffset;
	public int mouseeventYoffset;
	boolean fullscreen=true;

	public static ScreenManager display;

	public ScreenManager(){
		display=this;
		initialize();
	}

	public void initialize(){
		int resx=Options.graphics_resx;
		int resy=Options.graphics_resy;
		boolean fullscreen=Options.graphics_fullscreen;

		screen=new Frame(CommonInfo.gamename+" "+CommonInfo.version);
		if(fullscreen){
			GraphicsDevice graphicsdevice=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			if(!graphicsdevice.isFullScreenSupported()){
				Console.error("The default display device does not support fullscreen capabilities. Continuing in windowed mode.");
				setupWindowedMode();
			}else{
				GraphicsConfiguration gc[]=graphicsdevice.getConfigurations();
				DisplayMode dm[]=graphicsdevice.getDisplayModes();
				int bestmode=-1;
				for(int i=0;i<dm.length;i++){
					if(dm[i].getWidth()==resx && dm[i].getHeight()==resy){
						if(bestmode==-1){ //have we assigned a mode yet?
							bestmode=i;
						}else if(dm[i].getRefreshRate()==Options.graphics_refreshrate && dm[i].getBitDepth()==Options.graphics_bitdepth){ //first satisfy resolution, then bit depth / refresh rate
							bestmode=i;
						}
					}
				}
				if(bestmode==-1){
					Console.error("The display device is not capable of entering a matching display mode. Continuing in windowed mode. Valid display modes are: ");
					for(int i=0;i<dm.length;i++) Console.error("  "+dm[i].getWidth()+"x"+dm[i].getHeight()+"x"+dm[i].getBitDepth()+" @ "+dm[i].getRefreshRate()+" hertz");
					setupWindowedMode();
				}else{
					winwidth=Options.graphics_resx;
					winheight=Options.graphics_resy;
					screen.setUndecorated(true);
					/*InputMultiplexor m=new InputMultiplexor();
					screen.addMouseListener(m);
					screen.addMouseWheelListener(m);
					screen.addMouseMotionListener(m);
					screen.addKeyListener(m);
					screen.addWindowListener(m);*/
					screen.addComponentListener(this);
					screen.setIconImage(CommonInfo.icon);
					graphicsdevice.setFullScreenWindow(screen);
					screen.createBufferStrategy(Options.graphics_numbuffer);
				}
			}
		}else{
			setupWindowedMode();
		}
	}

	private void setupWindowedMode(){
		fullscreen=false;
		screen.setSize(Options.graphics_resx,Options.graphics_resy);
		/*InputMultiplexor m=new InputMultiplexor();
		screen.addMouseListener(m);
		screen.addMouseWheelListener(m);
		screen.addMouseMotionListener(m);
		screen.addKeyListener(m);
		screen.addWindowListener(m);*/
		screen.addComponentListener(this);
		screen.setIconImage(CommonInfo.icon);
		screen.setVisible(true);
		screen.createBufferStrategy(Options.graphics_numbuffer);
		winwidth=Options.graphics_resx-(screen.getInsets().left+screen.getInsets().right);
		winheight=Options.graphics_resy-(screen.getInsets().top+screen.getInsets().bottom);
		mouseeventYoffset=screen.getInsets().top;
		mouseeventXoffset=-screen.getInsets().left;
	}

	public Graphics2D getDrawSurface(){
		Graphics2D g=(Graphics2D)screen.getBufferStrategy().getDrawGraphics();
		if(!fullscreen){
			AffineTransform affine=new AffineTransform();
			affine.translate(screen.getInsets().left,screen.getInsets().top);
			g.transform(affine);
		}
		return g;
	}

	public BufferedImage getBufferedImage(int width, int height, int transparency){
		return screen.getGraphicsConfiguration().createCompatibleImage(width,height,transparency);
	}

	public VolatileImage getVolatileImage(int width, int height, int transparency){
		return screen.getGraphicsConfiguration().createCompatibleVolatileImage(width,height,transparency);
	}

	public ColorModel getColorModel(){
		return screen.getGraphicsConfiguration().getColorModel();
	}

	public GraphicsConfiguration getGraphicsConfiguration(){
		return screen.getGraphicsConfiguration();
	}

	public void flip(){
		screen.getBufferStrategy().show();
		try{
			if(Options.graphics_flipsleep>0) Thread.sleep(Options.graphics_flipsleep);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void shutdown(){
		GraphicsDevice graphicsdevice=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		if(fullscreen) graphicsdevice.setFullScreenWindow(null);
		screen.setVisible(false);
		screen.dispose();
	}

	public Dimension getDimensions() {
		return new Dimension(winwidth, winheight);
	}

	public void componentResized(ComponentEvent e){
		winwidth=screen.getSize().width-(screen.getInsets().left+screen.getInsets().right);
		winheight=screen.getSize().height-(screen.getInsets().top+screen.getInsets().bottom);
	}
	public void componentHidden(ComponentEvent e){}
	public void componentMoved(ComponentEvent e){}
	public void componentShown(ComponentEvent e){}
}
