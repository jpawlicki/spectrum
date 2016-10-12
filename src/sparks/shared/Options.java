package sparks.shared;

public class Options{
	public static boolean graphics_fullscreen           = false;
	public static int     graphics_resx                 = 768;
	public static int     graphics_resy                 = 768;
	public static int     graphics_bitdepth             = 24;
	public static int     graphics_refreshrate          = 60;
	public static int     graphics_numbuffer            = 3;
	public static int     graphics_flipsleep            = 2;

	public static int     framerate_msPerFrame          = 16;
	public static int     framerate_tolerance           = 1;
	public static boolean framerate_spinlock            = false;

	public static int     network_serverportnum         = 6000;
	public static int     network_clientportnum         = 6001;
	public static int     network_headlessserversleep   = 40; //in milliseconds

	public static String  menu_font                     = "Sans";
	public static int     menu_fontsize                 = 20;
	public static int[]   menu_fontcolor                = new int[]{0,0,0,255};
	public static int[]   menu_fontmouseovercolor       = new int[]{128,128,255,255};
	public static int     menu_backgroundpoints         = 200;
	public static int[]   menu_backgroundcolor          = new int[]{240,240,255,255};

	public static String  console_font                  = "Sans";
	public static int     console_fontsize              = 12;
	public static int[]   console_fontcolor             = new int[]{0,0,0,255};
	public static int[]   console_backgroundcolor       = new int[]{255,255,255,255};
}
