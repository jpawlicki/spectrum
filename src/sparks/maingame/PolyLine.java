package sparks.maingame;
import sparks.shared.*;

public class PolyLine{
	public double x;
	public double y;
	public PolyLine next;
	public PolyLine prev;

	public PolyLine temp;
	public boolean ccwv=true;
	public boolean cwv=true;

	private PolyLine(double x, double y){
		this.x=x;
		this.y=y;
	}

	/* Returns a polygon 
		@param x an array of x-coordinates for the vertexes of the polyline
		@param y an array of y-coordinates for the vertexes of the polyline
		@return a PolyLine representing a closed polygon.
	 */
	public static PolyLine makePolygon(double[] x, double[] y){
		if(x.length==y.length && x.length>1){
			PolyLine first=new PolyLine(x[0],y[0]);
			PolyLine current=first;
			for(int i=1;i<x.length;i++){
				PolyLine nu=new PolyLine(x[i],y[i]);
				nu.prev=current;
				current.next=nu;
				current=nu;
			}
			current.next=first;
			first.prev=current;
			return first;
		}else{
			Console.error("PolyLine:makePolygon ERR 1");
			return null;
		}
	}

	/* Returns an array of the PolyLine segments in this polyline.
		@return an array of PolyLines, corresponding to the segments in this polyline.
	 */

	public PolyLine[] toArray(){
		int cc=1;
		PolyLine t=this;
		while(t.next!=this){
			t=t.next;
			cc++;
		}
		PolyLine[] ret=new PolyLine[cc];
		t=this;
		for(int i=0;i<cc;i++){
			ret[i]=t;
			t=t.next;
		}
		return ret;
	}

	/* Returns a PolyLine exactly like this polyline, but in different sections of memory.
		@return a clone of this PolyLine.
	 */
	public PolyLine clone(){
		PolyLine retfirst=new PolyLine(x,y);
		PolyLine n=next;
		PolyLine n2=retfirst;
		while(n!=this){
			PolyLine nu=new PolyLine(n.x,n.y);
			nu.prev=n2;
			n2.next=nu;
			n2=nu;
			n=n.next;
		}
		retfirst.prev=n2;
		n2.next=retfirst;
		return retfirst;
	}

	/* Tests whether this segment of a PolyLine intersects another line.
		@return true if and only if this line has an intersection.
	 */
	public boolean intersectsSingle(double x1, double y1, double x2, double y2){
		double[] va=new double[]{next.x-x,next.y-y};
		double[] vb=new double[]{x2-x1,y2-y1};
		double[] vab1=new double[]{x1-x,y1-y};
		double[] vab2=new double[]{x2-x,y2-y};
		double[] vba1=new double[]{x-x1,y-y1};
		double[] vba2=new double[]{next.x-x1,next.y-y1};
		//check cross products for intersection:
		if((va[0]*vab1[1]-va[1]*vab1[0])*(va[0]*vab2[1]-va[1]*vab2[0])>=0 || (vb[0]*vba1[1]-vb[1]*vba1[0])*(vb[0]*vba2[1]-vb[1]*vba2[0])>=0) return false;
		return true;
	}

	/* Tests whether this segment of a PolyLine intersects another line, and computes the intersection point.
		@return an length=2 array of doubles, indicating how far along the lines the intersection occurs. The first element ranges from 0 (x1, y1) to 1 (x2, y2), while the second element ranges from 0 (x,y) to 1 (next.x,next.y). If no intersection exists, returns -1 in both dimensions.
	 */
	public double[] intersectsCompute(double x1, double y1, double x2, double y2){
		double[] va=new double[]{next.x-x,next.y-y};
		double[] vb=new double[]{x2-x1,y2-y1};
		double[] vab1=new double[]{x1-x,y1-y};
		double[] vab2=new double[]{x2-x,y2-y};
		double[] vba1=new double[]{x-x1,y-y1};
		double[] vba2=new double[]{next.x-x1,next.y-y1};
		//check cross products for intersection:
		if((va[0]*vab1[1]-va[1]*vab1[0])*(va[0]*vab2[1]-va[1]*vab2[0])>=0 || (vb[0]*vba1[1]-vb[1]*vba1[0])*(vb[0]*vba2[1]-vb[1]*vba2[0])>=0) return new double[]{-1,-1};
		//at this point, known that they intersect, but need to set aint. //FIXME: reuse cross product magnitudes?
		//consider dot product with normal: will give scalar distances.
		double[] ret=new double[2];
		double[] normal=new double[]{va[1],-va[0]};
		double scale=Math.sqrt(normal[1]*normal[1]+normal[0]*normal[0]);
		normal[0]/=scale;
		normal[1]/=scale;
		double db1=vab1[0]*normal[0]+vab1[1]*normal[1];
		double db2=vab2[0]*normal[0]+vab2[1]*normal[1];
		ret[1]=db1/(db1-db2); //use minus because one of them will be negative: if db2, this is positive, if db1, this is positive.

		normal=new double[]{vb[1],-vb[0]};
		scale=Math.sqrt(normal[1]*normal[1]+normal[0]*normal[0]);
		normal[0]/=scale;
		normal[1]/=scale;
		db1=vba1[0]*normal[0]+vba1[1]*normal[1];
		db2=vba2[0]*normal[0]+vba2[1]*normal[1];
		ret[0]=db1/(db1-db2); //use minus because one of them will be negative.
		return ret;
	}

	/* Tests whether this segment of a PolyLine intersects a sourced ray.
		@return true if and only if this line has an intersection.
	 */
	public boolean intersectsRay(double dxr, double dyr, double xr, double yr){
		double[] va=new double[]{next.x-x,next.y-y};
		double[] vb=new double[]{dxr,dyr};
		double[] vba1=new double[]{x-xr,y-yr};
		double[] vba2=new double[]{next.x-xr,next.y-yr};
		//check cross product for intersection:
		if((vb[0]*vba1[1]-vb[1]*vba1[0])*(vb[0]*vba2[1]-vb[1]*vba2[0])>=0) return false;
		//still have to make sure it's on the right side of the source.
		//consider dot product with normal: gives scalar distance, which is what we want aint to be.
		double[] ret=new double[2];
		double[] normal=new double[]{vb[1],-vb[0]};
		double scale=Math.sqrt(normal[1]*normal[1]+normal[0]*normal[0]);
		normal[0]/=scale;
		normal[1]/=scale;
		double db1=vba1[0]*normal[0]+vba1[1]*normal[1];
		double db2=vba2[0]*normal[0]+vba2[1]*normal[1];
		double gint=db1/(db1-db2); //use minus because one of them will be negative.

		double dx=(gint*next.x+(1-gint)*x)-xr;
		double dy=(gint*next.y+(1-gint)*y)-yr;
		if(dx*dxr+dy*dyr<0) return false;
		return true;
	}


	/* Tests whether this segment of a PolyLine intersects a sourced ray, and computes the intersection point.
		@return an length=2 array of doubles, indicating how far along the lines the intersection occurs. The first element ranges from 0 (x1, y1) up, indicating the distance along the ray, while the second element ranges from 0 (x,y) to 1 (next.x,next.y). If no intersection exists, returns -1 in both dimensions.
	 */
	public double[] intersectsRayCompute(double dxr, double dyr, double xr, double yr){
		double[] va=new double[]{next.x-x,next.y-y};
		double[] vb=new double[]{dxr,dyr};
		double[] vba1=new double[]{x-xr,y-yr};
		double[] vba2=new double[]{next.x-xr,next.y-yr};
		//check cross products for intersection:
		if((vb[0]*vba1[1]-vb[1]*vba1[0])*(vb[0]*vba2[1]-vb[1]*vba2[0])>=0) return new double[]{-1,-1};
		//consider dot product with normal: gives scalar distance, which is what we want aint to be.
		double[] ret=new double[2];
		double[] normal=new double[]{vb[1],-vb[0]};
		double scale=Math.sqrt(normal[1]*normal[1]+normal[0]*normal[0]);
		normal[0]/=scale;
		normal[1]/=scale;
		double db1=vba1[0]*normal[0]+vba1[1]*normal[1];
		double db2=vba2[0]*normal[0]+vba2[1]*normal[1];
		ret[1]=db1/(db1-db2); //use minus because one of them will be negative.

		double dx=(ret[1]*next.x+(1-ret[1])*x)-xr;
		double dy=(ret[1]*next.y+(1-ret[1])*y)-yr;
		if(dx*dxr+dy*dyr<0) return new double[]{-1,-1};
		ret[0]=Math.sqrt(dx*dx+dy*dy);
		return ret;
	}

	/* Splits this PolyLine at a point gint along its length.
	 */
	public void split(double gint){
		PolyLine nu=new PolyLine(gint*next.x+(1-gint)*x,gint*next.y+(1-gint)*y);
		nu.next=next;
		next.prev=nu;
		next=nu;
		nu.prev=this;
	}
/*
 public static boolean intersectsAny(double x1, double y1, double x2, double y2, LineTrain[] set,LineTrain ignore,Entity ignore2){
  for(LineTrain t : set){
   if(t.owner==ignore2) continue;
   LineTrain first=null;
   while(t!=null && t.next!=null && t!=first){
    if(first==null) first=t;
	if(t!=ignore){
     double[] vt1t2=new double[]{t.next.x-t.x,t.next.y-t.y}; //t1 to t2
     double[] vi1i2=new double[]{x2-x1,y2-y1}; //input 1 to input 2
     double[] vi1t1=new double[]{t.x-x1,t.y-y1}; //input 1 to t1
     double[] vi1t2=new double[]{t.next.x-x1,t.next.y-y1}; //input 1 to t2
     double[] vt1i1=new double[]{x1-t.x,y1-t.y}; //t1 to input 1
     double[] vt1i2=new double[]{x2-t.x,y2-t.y}; //t1 to input 2 
     if((MathManager.cross2d(vt1t2,vt1i1)*MathManager.cross2d(vt1t2,vt1i2) < 0) && (MathManager.cross2d(vi1i2,vi1t1)*MathManager.cross2d(vi1i2,vi1t2) < 0)) return true;
    }
    t=t.next;
   }
  }
  return false;
 }

 public static boolean intersectsAny(double x1, double y1, double x2, double y2, LineTrain[] set,LineTrain ignore,Entity ignore2, Entity ignore3){
  for(LineTrain t : set){
   if(t.owner==ignore2) continue;
   if(t.owner==ignore3) continue;
   LineTrain first=null;
   while(t!=null && t.next!=null && t!=first){
    if(first==null) first=t;
	if(t!=ignore){
     double[] vt1t2=new double[]{t.next.x-t.x,t.next.y-t.y}; //t1 to t2
     double[] vi1i2=new double[]{x2-x1,y2-y1}; //input 1 to input 2
     double[] vi1t1=new double[]{t.x-x1,t.y-y1}; //input 1 to t1
     double[] vi1t2=new double[]{t.next.x-x1,t.next.y-y1}; //input 1 to t2
     double[] vt1i1=new double[]{x1-t.x,y1-t.y}; //t1 to input 1
     double[] vt1i2=new double[]{x2-t.x,y2-t.y}; //t1 to input 2 
     if((MathManager.cross2d(vt1t2,vt1i1)*MathManager.cross2d(vt1t2,vt1i2) < 0) && (MathManager.cross2d(vi1i2,vi1t1)*MathManager.cross2d(vi1i2,vi1t2) < 0)) return true;
    }
    t=t.next;
   }
  }
  return false;
 }

 public static LineTrain intersectsClosestRay(double x1, double y1, double x2, double y2, LineTrain[] set,LineTrain ignore){
  double closest=Double.MAX_VALUE;
  LineTrain trunc=null;
  for(LineTrain t : set){
   LineTrain first=null;
   while(t.next!=null && t!=first){
    if(first==null) first=t;
	if(t!=ignore){
     if(t.intersectsRay(x1,y1,x2,y2) && t.aint<closest){
      closest=t.aint;
      trunc=t;
     }
    }
    t=t.next;
   }
  }
  return trunc;
 }

 public boolean intersectsArc(double rot, double cx, double cy, double sx, double sy, double r){
//  System.out.println("IARC: "+rot+", "+cx+", "+cy+", "+sx+", "+sy+", "+r);
  if(next==null) return false;
  double[] va=new double[]{next.x-x,next.y-y};
  double[] radius=new double[]{sx-cx,sy-cy,0};
  double[] normal=new double[]{va[1],-va[0],0};
  double scale=Math.sqrt(normal[1]*normal[1]+normal[0]*normal[0]);
  normal[0]/=scale;
  normal[1]/=scale;
  if(normal[0]*(cx-x)+normal[1]*(cy-y)>0){
   normal[0]*=-1; //actually want the "wrong" direction of the normal for this.
   normal[1]*=-1;
  }
  double L=Math.abs(normal[0]*(cx-x)+normal[1]*(cy-y));
  if(L>r) return false;
  double[] Lpoint=new double[]{cx+normal[0]*L,cy+normal[1]*L};
//  System.out.println("Potentialhit: L="+L);
  double theta=MathManager.getTheta(radius,normal);
  boolean thetaleft=MathManager.cross(radius,normal)[2]<0;
  double aintb; //second (further) point of intersection.
  if((rot>0 && !thetaleft) || (rot<0 && thetaleft)){ //rotating into theta.
   aint=Math.abs(theta-Math.acos(L/r));
   if(aint>Math.abs(rot)) return false;
   aintb=aint+2*(theta-aint);
  }else{ //rotating out of theta.
   double phi=Math.abs(theta-Math.acos(L/r));
   aint=2*Math.PI-2*theta+phi;
   if(aint>Math.abs(rot)) return false;
   aintb=aint+2*(theta-phi);
  }

  //check line segment bounds to prevent collisions with infinite lines
  double[] interceptPoint=MathManager.rotz(radius,aint*Math.signum(rot));
  interceptPoint[0]+=cx;
  interceptPoint[1]+=cy;
//  System.out.println("Ipoint: {"+interceptPoint[0]+","+interceptPoint[1]+"}");
  gint=(interceptPoint[0]-x)*va[0]+(interceptPoint[1]-y)*va[1];
  gint/=scale*scale;
  if(gint>=0 && gint<=1) return true;

  //otherwise, check further endpoint of rotation:
  aint=aintb;
  if(aint>Math.abs(rot)) return false;
  interceptPoint=MathManager.rotz(radius,aint*Math.signum(rot));
  interceptPoint[0]+=cx;
  interceptPoint[1]+=cy;
//  System.out.println("Ipoint: {"+interceptPoint[0]+","+interceptPoint[1]+"}");
  gint=(interceptPoint[0]-x)*va[0]+(interceptPoint[1]-y)*va[1];
  gint/=scale*scale;
  if(gint>=0 && gint<=1) return true;
  return false;
 }*/
}
