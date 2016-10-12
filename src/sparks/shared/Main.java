package sparks.shared;
import sparks.graphics.*;
import sparks.menus.MainMenu;
import sparks.maingame.MissionState;
import sparks.maingame.FileLoader;
import java.awt.geom.AffineTransform;

public class Main{
	public static GameState state;
	private static ScreenPanel ui;

	private static boolean running=true;
	private static GameState nextState=null;

	public static void main(String args[]){
		ScreenManager smanage=new ScreenManager();
		//FileLoader.loadAssets();
		final MissionState mstate = new MissionState();
		state = mstate;
		final Camera2D camera = new Camera2D(0, 0, 1);
		ui = new MissionMap(
				new Provider<MissionState>(){ @Override public MissionState get() { return mstate; }},
				new Provider<Camera2D>(){ @Override public Camera2D get() { return camera; }});

		long nextframe=System.currentTimeMillis();
		long frcount=nextframe;
		int frame=0;
		while(running){
			long td=System.currentTimeMillis();
			nextframe = td + Options.framerate_msPerFrame;
			if(nextState!=null){
				state=nextState;
				nextState=null;
			}
			state.update();
			ui.render(smanage.getDrawSurface(), smanage.getDimensions(), new AffineTransform());
			smanage.flip();
			frame++;
			nextframe -= System.currentTimeMillis();
			if (nextframe > 0) {
				try {
					Thread.sleep(nextframe);
				} catch (InterruptedException e) {
					// Do nothing.
				}
			}
		}
		ScreenManager.display.shutdown();
		Console.println("Exiting main.");
	}

	public static void shutdown(){
		Console.println("System shutting down.");
		running=false;
	}

	public static void stateSwitch(GameState newstate){
		nextState=newstate;
	}
}
