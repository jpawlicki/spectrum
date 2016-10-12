package sparks.maingame;
import sparks.shared.MathManager;
import sparks.graphics.ScreenManager;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.Path2D;
import java.util.ArrayList;

public class Geometry{
	public static final int NUM_PARAMS=6;
	public static final int PARAM_COLLIDING=0;
	public static final int PARAM_CRITICAL=1;
	public static final int PARAM_SENSOR=2;
	public static final int PARAM_INVISIBLE=3;
	public static final int PARAM_TEAMCOLOR=4;
	public static final int PARAM_SENSORTRIGGER=5;

	double[][][] canonicalVerticies;
	double internalScale=1;

	public PolyLine[] drawlines;
	public double[][] fillcolors;
	public boolean[][] params; //0: colliding. 1: critical. 2: sensor. 3: invisible. 4: teamcolor

	private double boundingDrawCircleRadius=Double.NEGATIVE_INFINITY;
	private double boundingCollideCircleRadius=Double.NEGATIVE_INFINITY;
	private double boundingSensorCircleRadius=Double.NEGATIVE_INFINITY;
	private double[] cpos=new double[2];
	private double oldrotz;
	private boolean scalechange=false;

	public Entity owner;

	public Geometry(double[][][] canonicalV, int[][] colors, boolean[][] param){
		canonicalVerticies=canonicalV;
		PolyLine[] shapes=new PolyLine[canonicalV.length];
		for(int i=0;i<canonicalV.length;i++){
			double[] x=new double[canonicalV[i].length];
			double[] y=new double[canonicalV[i].length];
			for(int j=0;j<x.length;j++){
				x[j]=canonicalVerticies[i][j][0];
				y[j]=canonicalVerticies[i][j][1];
			}
			shapes[i]=PolyLine.makePolygon(x,y);
		}

		drawlines=shapes;
		fillcolors=new double[colors.length][4];
		for(int i=0;i<shapes.length;i++){
			fillcolors[i][0]=colors[i][0]/255.0;
			fillcolors[i][1]=colors[i][1]/255.0;
			fillcolors[i][2]=colors[i][2]/255.0;
			fillcolors[i][3]=colors[i][3]/255.0;
		}

		params=param;

		computeBoundingCircles();
		nptstemp=new double[drawlines.length][0][0];
	}

	public void setRotPos(double rotz,double[] loc){
		if(!scalechange && rotz==oldrotz && loc[0]==cpos[0] && loc[1]==cpos[1]) return; //don't bother with the matmult if we've done it before.
		for(int i=0;i<canonicalVerticies.length;i++){
			double[][] rotLines=MathManager.rotz(canonicalVerticies[i],rotz);
			PolyLine p=drawlines[i];
			for(int j=0;j<rotLines.length;j++){
				p.x=rotLines[j][0]+loc[0];
				p.y=rotLines[j][1]+loc[1];
				p=p.next;
			}
		}
		cpos[0]=loc[0];
		cpos[1]=loc[1];
		oldrotz=rotz;
		scalechange=false;
	}

	private double[][][] nptstemp;
	public void setNextRot(double rotz){
		double[][] rotmat=MathManager.fetchrot2d(rotz);
		for(int i=0;i<drawlines.length;i++)	nptstemp[i]=MathManager.matmult(canonicalVerticies[i],rotmat);
	}

	public boolean collides(double[] npos, Geometry other){
		for(int i=0;i<drawlines.length;i++){
			if(!params[i][0]) continue; //skip non-colliding lines
			double[][] npts=nptstemp[i];
			PolyLine[] pa=drawlines[i].toArray();
			for(int j=0;j<pa.length;j++){

				Path2D.Double scanpoly=new Path2D.Double(Path2D.WIND_EVEN_ODD);
				scanpoly.moveTo(pa[j].x,pa[j].y);
				scanpoly.lineTo(npts[j][0]+npos[0],npts[j][1]+npos[1]);
				if(pa[j].next!=null) scanpoly.lineTo(npts[(j+1)%npts.length][0]+npos[0],npts[(j+1)%npts.length][1]+npos[1]);
				if(pa[j].next!=null) scanpoly.lineTo(pa[j].next.x,pa[j].next.y);
				scanpoly.closePath();

				for(int k=0;k<other.drawlines.length;k++){
					if(!other.params[k][0]) continue; //skip non-colliding lines
					PolyLine[] z=other.drawlines[k].toArray();
					for(int l=0;l<z.length;l++){
						//so we are considering the vertex that is home to pa[j] and the environment vertex z[l].
						//there is an intersection iff A or B:
						//	A: the polygon of pa[j],pa[j].next,destOf(pa[j].next) destOf(pa[j]) contains an environment vertex.
						//	B: the line of pa[j],destOf(pa[j]) intersects an environment line.
						if(scanpoly.contains(z[l].x,z[l].y)) return true; //case A
						if(z[l].intersectsSingle(pa[j].x,pa[j].y,npts[j][0]+npos[0],npts[j][1]+npos[1])) return true;	//case B
					}
				}
			}
		}
		return false;
	}

	public PolyLine[] collidesCalc(double[] npos, double nfacing, Geometry other){
		for(int i=0;i<drawlines.length;i++){
			if(!params[i][0]) continue; //skip non-colliding lines
			double[][] npts=MathManager.rotz(canonicalVerticies[i],nfacing);
			PolyLine[] pa=drawlines[i].toArray();
			for(int j=0;j<pa.length;j++){

				Path2D.Double scanpoly=new Path2D.Double(Path2D.WIND_EVEN_ODD);
				scanpoly.moveTo(pa[j].x,pa[j].y);
				scanpoly.lineTo(npts[j][0]+npos[0],npts[j][1]+npos[1]);
				if(pa[j].next!=null) scanpoly.lineTo(npts[(j+1)%npts.length][0]+npos[0],npts[(j+1)%npts.length][1]+npos[1]);
				if(pa[j].next!=null) scanpoly.lineTo(pa[j].next.x,pa[j].next.y);
				scanpoly.closePath();

				for(int k=0;k<other.drawlines.length;k++){
					if(!other.params[k][0]) continue; //skip non-colliding lines
					PolyLine[] z=other.drawlines[k].toArray();
					for(int l=0;l<z.length;l++){
						//so we are considering the vertex that is home to pa[j] and the environment vertex z[l].
						//there is an intersection iff A or B:
						//	A: the polygon of pa[j],pa[j].next,destOf(pa[j].next) destOf(pa[j]) contains an environment vertex.
						//	B: the line of pa[j],destOf(pa[j]) intersects an environment line.
						if(z[l].intersectsSingle(pa[j].x,pa[j].y,npts[j][0]+npos[0],npts[j][1]+npos[1])){	//case B){
							return new PolyLine[]{pa[j],z[l]};
						}else if(scanpoly.contains(z[l].x,z[l].y)){ //case A
							return new PolyLine[]{z[l],pa[j]};
						}
					}
				}
			}
		}
		return new PolyLine[]{null,null};
	}

	public void draw(Graphics2D g, double[] cameraCenterWorld, double scale){
		if(drawlines==null || drawlines.length==0) return;
		boolean inX=(cpos[0]<cameraCenterWorld[0]+ScreenManager.display.winwidth/2/scale+boundingDrawCircleRadius) && (cpos[0]>cameraCenterWorld[0]-(ScreenManager.display.winwidth/2/scale+boundingDrawCircleRadius));
		boolean inY=(cpos[1]<cameraCenterWorld[1]+ScreenManager.display.winheight/2/scale+boundingDrawCircleRadius) && (cpos[1]>cameraCenterWorld[1]-(ScreenManager.display.winheight/2/scale+boundingDrawCircleRadius));
		if(inX && inY){ //if not offscreen...
			for(int i=0;i<drawlines.length;i++){
				if(params[i][3]) continue;
				g.setPaint(new Color((int)(255*fillcolors[i][0]),(int)(255*fillcolors[i][1]),(int)(255*fillcolors[i][2]),(int)(255*fillcolors[i][3])));
				Path2D.Double shape=new Path2D.Double();
				PolyLine p=drawlines[i];
				PolyLine first=p;
				shape.moveTo(p.x*scale,p.y*scale);
				p=p.next;
				while(p!=first && p!=null){
					shape.lineTo(p.x*scale,p.y*scale);
					p=p.next;
				}
				shape.closePath();
				g.fill(shape);
			}
		}
	}

	public void setScale(double s){
		double[][][] tempverts=new double[canonicalVerticies.length][0][0];
		for(int i=0;i<canonicalVerticies.length;i++){
			tempverts[i]=new double[canonicalVerticies[i].length][2];
			for(int j=0;j<canonicalVerticies[i].length;j++){
				tempverts[i][j][0]=canonicalVerticies[i][j][0]*s/internalScale;
				tempverts[i][j][1]=canonicalVerticies[i][j][1]*s/internalScale;
			}
		}
		canonicalVerticies=tempverts;
		internalScale=s;
		scalechange=true;
		computeBoundingCircles();
	}

	private void computeBoundingCircles(){
		boundingCollideCircleRadius=Double.NEGATIVE_INFINITY;
		for(int i=0;i<canonicalVerticies.length;i++){
			if(!params[i][0]) continue;
			for(int j=0;j<canonicalVerticies[i].length;j++){
				double dist=Math.sqrt(canonicalVerticies[i][j][0]*canonicalVerticies[i][j][0]+canonicalVerticies[i][j][1]*canonicalVerticies[i][j][1]);
				if(boundingCollideCircleRadius<dist) boundingCollideCircleRadius=dist;
			}
		}
		boundingDrawCircleRadius=Double.NEGATIVE_INFINITY;
		for(int i=0;i<canonicalVerticies.length;i++){
			if(params[i][3]) continue;
			for(int j=0;j<canonicalVerticies[i].length;j++){
				double dist=Math.sqrt(canonicalVerticies[i][j][0]*canonicalVerticies[i][j][0]+canonicalVerticies[i][j][1]*canonicalVerticies[i][j][1]);
				if(boundingDrawCircleRadius<dist) boundingDrawCircleRadius=dist;
			}
		}
		boundingSensorCircleRadius=Double.NEGATIVE_INFINITY;
		for(int i=0;i<canonicalVerticies.length;i++){
			if(!params[i][PARAM_SENSORTRIGGER] && !params[i][PARAM_SENSOR]) continue;
			for(int j=0;j<canonicalVerticies[i].length;j++){
				double dist=Math.sqrt(canonicalVerticies[i][j][0]*canonicalVerticies[i][j][0]+canonicalVerticies[i][j][1]*canonicalVerticies[i][j][1]);
				if(boundingSensorCircleRadius<dist) boundingSensorCircleRadius=dist;
			}
		}
	}

	public boolean sensePoint(double x, double y){
		for(int i=0;i<canonicalVerticies.length;i++){
			if(!params[i][PARAM_SENSOR]) continue;
			Path2D.Double shape=new Path2D.Double();
			PolyLine p=drawlines[i];
			PolyLine first=p;
			shape.moveTo(p.x,p.y);
			p=p.next;
			while(p!=first && p!=null){
				shape.lineTo(p.x,p.y);
				p=p.next;
			}
			shape.closePath();
			if(shape.contains(x,y)) return true;
		}
		return false;
	}

	public boolean sense(Geometry geo){
		if(MathManager.magnitude2d(MathManager.subtract2d(cpos,geo.cpos))>boundingSensorCircleRadius+geo.boundingSensorCircleRadius) return false;
		for(int i=0;i<canonicalVerticies.length;i++){
			if(!params[i][PARAM_SENSOR]) continue;
			Path2D.Double shape=new Path2D.Double();
			PolyLine p=drawlines[i];
			PolyLine first=p;
			shape.moveTo(p.x,p.y);
			p=p.next;
			while(p!=first && p!=null){
				shape.lineTo(p.x,p.y);
				p=p.next;
			}
			shape.closePath();
			for(int j=0;j<geo.drawlines.length;j++){
				if(!geo.params[j][PARAM_SENSORTRIGGER]) continue;
				PolyLine comp=geo.drawlines[j];
				PolyLine first2=comp;
				if(shape.contains(first2.x,first2.y)) return true;
				boolean ff=true;
				Path2D.Double shape2=new Path2D.Double();
				shape2.moveTo(first2.x,first2.y);
				while(comp!=first2 || ff){
					if(!ff) shape2.lineTo(comp.x,comp.y);
					ff=false;
					p=first;
					boolean fff=true;
					while(p!=first || fff){
						fff=false;
						if(p.intersectsSingle(comp.x,comp.y,comp.next.x,comp.next.y)) return true;
						p=p.next;
					}
					comp=comp.next;
				}
				shape2.closePath();
				if(shape2.contains(first.x,first.y)) return true;
			}
		}
		return false;
	}

	public void setColors(double[][] colors){
		fillcolors=colors;
	}

	public void setTeamColor(double[] color){
		for(int i=0;i<params.length;i++){
			if(!params[i][PARAM_TEAMCOLOR]) continue;
			fillcolors[i][0]=color[0];
			fillcolors[i][1]=color[1];
			fillcolors[i][2]=color[2];
			fillcolors[i][3]=color[3];
		}
	}
}
