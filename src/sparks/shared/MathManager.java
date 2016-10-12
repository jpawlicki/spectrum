package sparks.shared;

public class MathManager{
 private MathManager() {}
 public static double[] cross3d(double[] a, double[] b){
  double[] result=new double[3];
  result[0]=a[1]*b[2]-a[2]*b[1];
  result[1]=a[2]*b[0]-a[0]*b[2];
  result[2]=a[0]*b[1]-a[1]*b[0];
  return result;
 }
 public static double cross2d(double[] a, double[] b){
  return a[0]*b[1]-a[1]*b[0];
 }
 public static double dot3d(double[] a, double[] b){
  return a[0]*b[0]+a[1]*b[1]+a[2]*b[2];
 }
 public static double dot2d(double[] a, double[] b){
  return a[0]*b[0]+a[1]*b[1];
 }
 public static double[] subtract3d(double[] a, double[] b){
  double[] result=new double[3];
  result[0]=a[0]-b[0];
  result[1]=a[1]-b[1];
  result[2]=a[2]-b[2];
  return result;
 }
 public static double[] subtract2d(double[] a, double[] b){
  double[] result=new double[2];
  result[0]=a[0]-b[0];
  result[1]=a[1]-b[1];
  return result;
 }
 public static double[] add3d(double[] a, double[] b){
  double[] result=new double[3];
  result[0]=a[0]+b[0];
  result[1]=a[1]+b[1];
  result[2]=a[2]+b[2];
  return result;
 }
 public static double[] add2d(double[] a, double[] b){
  double[] result=new double[2];
  result[0]=a[0]+b[0];
  result[1]=a[1]+b[1];
  return result;
 }
 public static double[] scale3d(double[] a, double b){
  double[] result=new double[3];
  result[0]=a[0]*b;
  result[1]=a[1]*b;
  result[2]=a[2]*b;
  return result;
 }
 public static double[] scale2d(double[] a, double b){
  double[] result=new double[2];
  result[0]=a[0]*b;
  result[1]=a[1]*b;
  return result;
 }
 public static double magnitude2d(double[] a){
  return (double)Math.sqrt(a[0]*a[0]+a[1]*a[1]);
 }
 public static double magnitude(double[] a){
  return (double)Math.sqrt(a[0]*a[0]+a[1]*a[1]+a[2]*a[2]);
 }
 public static double dist2d(double[] a, double[] b){
  return magnitude2d(subtract2d(a,b));
 }
 public static double[] matmult(double[][] a, double[] b){
  double[] result=new double[3];
  result[0]=a[0][0]*b[0]+a[0][1]*b[1]+a[0][2]*b[2];
  result[1]=a[1][0]*b[0]+a[1][1]*b[1]+a[1][2]*b[2];
  result[2]=a[2][0]*b[0]+a[2][1]*b[1]+a[2][2]*b[2];
  return result;
 }
 public static double[] matmult2d(double[][] a, double[] b){
  double[] result=new double[2];
  result[0]=a[0][0]*b[0]+a[0][1]*b[1];
  result[1]=a[1][0]*b[0]+a[1][1]*b[1];
  return result;
 }
 public static double[] avg(double[] a, double[] b){
  double[] result=new double[3];
  result[0]=(a[0]+b[0])/2;
  result[1]=(a[1]+b[1])/2;
  result[2]=(a[2]+b[2])/2;
  return result;
 }
 public static double[] avg(double[][] a){
  int x=a[0].length;
  double[] result=new double[x];
  for(int i=0;i<a.length;i++){
   for(int j=0;j<x;j++){
    result[j]+=a[i][j];
   }
  }
  for(int j=0;j<x;j++){
   result[j]=result[j]/a.length;
  }
  return result;
 }
 public static double[][] avg(double[][][] a){
  int x=a[0].length;
  int y=a[0][0].length;
  double[][] result=new double[x][y];
  for(int i=0;i<a.length;i++){
   for(int j=0;j<x;j++){
    for(int k=0;k<y;k++){
     result[j][k]+=a[i][j][k];
    }
   }
  }
  for(int j=0;j<x;j++){
   for(int k=0;k<y;k++){
    result[j][k]=result[j][k]/a.length;
   }
  }
  return result;
 }
 public static double[] normalize2d(double[] a){
  double[] result=new double[2];
  double magnitude=magnitude2d(a);
  result[0]=a[0]/magnitude;
  result[1]=a[1]/magnitude;
  return result;
 }
 public static double[] normalize(double[] a){
  double[] result=new double[3];
  double magnitude=magnitude(a);
  result[0]=a[0]/magnitude;
  result[1]=a[1]/magnitude;
  result[2]=a[2]/magnitude;
  return result;
 }
 public static double[] normalizeToRadius(double[] a, double r){
  double[] result=normalize(a);
  result[0]=result[0]*r;
  result[1]=result[1]*r;
  result[2]=result[2]*r;
  return result;
 }
 public static double[][] matmult3d(double[][] a, double[][] b){
  double[][] result=new double[3][3];
  result[0][0]=a[0][0]*b[0][0]+a[0][1]*b[1][0]+a[0][2]*b[2][0];
  result[0][1]=a[0][0]*b[0][1]+a[0][1]*b[1][1]+a[0][2]*b[2][1];
  result[0][2]=a[0][0]*b[0][2]+a[0][1]*b[1][2]+a[0][2]*b[2][2];

  result[1][0]=a[1][0]*b[0][0]+a[1][1]*b[1][0]+a[1][2]*b[2][0];
  result[1][1]=a[1][0]*b[0][1]+a[1][1]*b[1][1]+a[1][2]*b[2][1];
  result[1][2]=a[1][0]*b[0][2]+a[1][1]*b[1][2]+a[1][2]*b[2][2];

  result[2][0]=a[2][0]*b[0][0]+a[2][1]*b[1][0]+a[2][2]*b[2][0];
  result[2][1]=a[2][0]*b[0][1]+a[2][1]*b[1][1]+a[2][2]*b[2][1];
  result[2][2]=a[2][0]*b[0][2]+a[2][1]*b[1][2]+a[2][2]*b[2][2];

  return result;
 }

 public static double[][] matmult(double[][] a, double[][] b){
  double[][] result=new double[a.length][b[0].length];
  for(int i=0;i<result.length;i++){
   for(int j=0;j<result[0].length;j++){
    for(int k=0;k<b.length;k++){
     result[i][j]+=a[i][k]*b[k][j];
    }
   }
  }
  return result;
 }

/* public static double[][] rotz(double[][] points, double radians){
  double[][] rotmat=new double[2][2];
  rotmat[0][0]=Math.cos(radians);
  rotmat[0][1]=Math.sin(radians);
  rotmat[1][1]=rotmat[0][0];
  rotmat[1][0]=-rotmat[0][1];
  return matmult(points,rotmat);
 }*/

	//optimized
 public static double[][] rotz(double[][] points, double radians){
  double[][] rotmat=new double[2][2];
  rotmat[0][0]=Math.cos(radians);
  rotmat[0][1]=Math.sin(radians);
  rotmat[1][1]=rotmat[0][0];
  rotmat[1][0]=-rotmat[0][1];
  double[][] result=new double[points.length][2];
  for(int i=0;i<result.length;i++){
   result[i][0]=points[i][0]*rotmat[0][0]+points[i][1]*rotmat[1][0];
   result[i][1]=points[i][0]*rotmat[0][1]+points[i][1]*rotmat[1][1];
  }
  return result;
 }

 public static double[] rotz(double[] point, double radians){
  double[][] rotmat=new double[3][3];
  rotmat[0][0]=Math.cos(radians);
  rotmat[0][1]=-Math.sin(radians);
  rotmat[1][1]=rotmat[0][0];
  rotmat[1][0]=-rotmat[0][1];
  rotmat[2][2]=1;
  return matmult(rotmat,point);
 }

 public static double getTheta(double[] a, double[] b){
  return (double)(Math.acos(dot3d(a,b)/(magnitude(a)*magnitude(b))));
 }

 //a is from, b is "to". Positive thetas mean a counterclockwise rotation around (a cross b).
 public static double getSignedTheta(double[] a, double[] b){
  double mag=(double)(Math.acos(dot3d(a,b)/(magnitude(a)*magnitude(b))));
  double[] pole=cross3d(a,b);
  double[] refpole=new double[3];
  refpole[0]=pole[0];
  refpole[1]=pole[1];
  refpole[2]=pole[2];
  if(refpole[0]<0){
   refpole[0]*=-1;
   refpole[1]*=-1;
   refpole[2]*=-1;
  }else if(refpole[0]==0){
   if(refpole[1]<0){
    refpole[0]*=-1;
    refpole[1]*=-1;
    refpole[2]*=-1;
   }else if(refpole[1]==0){
    if(refpole[2]<0){
     refpole[0]*=-1;
     refpole[1]*=-1;
     refpole[2]*=-1;
    }
   }
  }
  if(dot3d(refpole,pole)<0) return -mag;
  else return mag;
 }
 public static double[] rot3d(double[] x, double[] around, double radians){
  double u=around[0];
  double v=around[1];
  double w=around[2];
  double[][] rotmat=new double[3][3];
  double u2=u*u;
  double v2=v*v;
  double w2=w*w;
  double n=u2+v2+w2;
  double m=(double)Math.sqrt(n);
  double sin=(double)Math.sin(radians);
  double cos=(double)Math.cos(radians);
  rotmat[0][0]=(u2+(v2+w2)*cos)/n;
  rotmat[0][1]=(u*v*(1-cos)-w*m*sin)/n;
  rotmat[0][2]=(u*w*(1-cos)+v*m*sin)/n;
  rotmat[1][0]=(u*v*(1-cos)+w*m*sin)/n;
  rotmat[1][1]=(v2+(u2+w2)*cos)/n;
  rotmat[1][2]=(v*w*(1-cos)-u*m*sin)/n;
  rotmat[2][0]=(u*w*(1-cos)-v*m*sin)/n;
  rotmat[2][1]=(v*w*(1-cos)+u*m*sin)/n;
  rotmat[2][2]=(w2+(u2+v2)*cos)/n;

  return matmult(rotmat,x);
 }

 public static double[][] fetchrot3d(double[] around, double radians){
  double u=around[0];
  double v=around[1];
  double w=around[2];
  double[][] rotmat=new double[3][3];
  double u2=u*u;
  double v2=v*v;
  double w2=w*w;
  double n=u2+v2+w2;
  double m=(double)Math.sqrt(n);
  double sin=(double)Math.sin(radians);
  double cos=(double)Math.cos(radians);
  rotmat[0][0]=(u2+(v2+w2)*cos)/n;
  rotmat[0][1]=(u*v*(1-cos)-w*m*sin)/n;
  rotmat[0][2]=(u*w*(1-cos)+v*m*sin)/n;
  rotmat[1][0]=(u*v*(1-cos)+w*m*sin)/n;
  rotmat[1][1]=(v2+(u2+w2)*cos)/n;
  rotmat[1][2]=(v*w*(1-cos)-u*m*sin)/n;
  rotmat[2][0]=(u*w*(1-cos)-v*m*sin)/n;
  rotmat[2][1]=(v*w*(1-cos)+u*m*sin)/n;
  rotmat[2][2]=(w2+(u2+v2)*cos)/n;

  return rotmat;
 }

 public static double[][] fetchrot2d(double radians){
  double[][] rotmat=new double[2][2];
  rotmat[0][0]=Math.cos(radians);
  rotmat[0][1]=Math.sin(radians);
  rotmat[1][1]=rotmat[0][0];
  rotmat[1][0]=-rotmat[0][1];
  return rotmat;
 }
}
