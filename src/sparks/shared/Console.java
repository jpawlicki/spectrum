package sparks.shared;

public class Console{
	public static void error(String s){
		System.err.println(s);
	}
	public static void error(String s, Exception e){
		System.err.println(s);
		e.printStackTrace();
	}

	public static void println(String s){
		System.out.println(s);
	}

	public static void keyTyped(java.awt.event.KeyEvent e){
	}

	public static void update(double t){
	}

	public static void draw(java.awt.Graphics2D g){
	}
}
