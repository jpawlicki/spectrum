package sparks.graphics;

import java.awt.*;
import java.awt.geom.*;

public class TextRenderer{


	public static void drawText(Graphics2D g, String s, double x, double y){
		y=-y;
		String[] data=s.split("\n");
		FontMetrics f=g.getFontMetrics();
		int height=f.getAscent()+f.getDescent();
		AffineTransform xform=new AffineTransform();
		xform.scale(1,-1);
		g.transform(xform);
		for(String d : data){
			g.drawString(d,(int)x,(int)y);
			y+=height;
		}
		g.transform(xform);
	}


	public static void drawTextInWidth(Graphics2D g, String s, double x, double y, double width){
		y=-y;
		String[] data=s.split("\n");
		FontMetrics f=g.getFontMetrics();
		int height=f.getAscent()+f.getDescent();
		AffineTransform xform=new AffineTransform();
		xform.scale(1,-1);
		g.transform(xform);
		for(String d : data){
			while(d!=""){
				int ub=d.length();
				int lb=1;
				int guess=ub;
				while(lb<ub-1){
					if(f.getStringBounds(d.substring(0,guess),g).getWidth()>width){
						ub=guess;
						guess=(ub+lb)/2;
					}else{
						lb=guess;
						guess=(ub+lb+1)/2;
					}
				}
				g.drawString(d.substring(0,guess),(int)x,(int)y);
				y+=height;
				if(guess<d.length()){
					d=d.substring(guess,d.length());
				}else{
					d="";
				}
				g.drawString(d,(int)x,(int)y);
			}
		}
		g.transform(xform);
	}

	public static int getFontHeight(Graphics2D g){
		FontMetrics f=g.getFontMetrics();
		return f.getAscent()+f.getDescent();
	}


	public static void drawTextCenteredHorizontally(Graphics2D g, String s, double x, double y){
		y=-y;
		String[] data=s.split("\n");
		FontMetrics f=g.getFontMetrics();
		int height=f.getAscent()+f.getDescent();
		AffineTransform xform=new AffineTransform();
		xform.scale(1,-1);
		g.transform(xform);
		for(String d : data){
			g.drawString(d,(int)(x-(f.getStringBounds(d,g).getWidth())/2),(int)y);
			y+=height;
		}
		g.transform(xform);
	}


	public static void drawTextCentered(Graphics2D g, String s, double x, double y){
		y=-y;
		String[] data=s.split("\n");
		FontMetrics f=g.getFontMetrics();
		int height=f.getAscent()+f.getDescent();
		AffineTransform xform=new AffineTransform();
		xform.scale(1,-1);
		g.transform(xform);
		y-=(height*(data.length-1))/2;
		if(data.length>1) y+=f.getDescent();
		for(String d : data){
			g.drawString(d,(int)(x-(f.getStringBounds(d,g).getWidth())/2),(int)y);
			y+=height;
		}
		g.transform(xform);
	}

}
