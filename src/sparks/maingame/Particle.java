package sparks.maingame;

public class Particle{
	public ParticleType type;
	
	public int drawLayer;

	private double age;
	private double[] location;
	private double[] velocity;
	private double facing;

	private Geometry geo;

	public Particle(ParticleType parent, double[] loc, double[] vel, double fac, int layer, double g){
		drawLayer=layer;
		type=parent;
		location=loc;
		velocity=vel;
		facing=fac;
		geo=FileLoader.getGeometry(parent.geo);
		update(g);
		geo.setRotPos(facing, location);
		//Sparks.runningSparks.addParticle(this);
	}

	public Particle(ParticleType parent, double[] loc, double[] vel, double fac, int layer){
		this(parent,loc,vel,fac,layer,0);
	}

	public void update(double t){
		age+=t;
		if(age>type.lifetime){
			//Sparks.runningSparks.removeParticle(this);
		}else{
			location[0]+=velocity[0]*t;
			location[1]+=velocity[1]*t;
			double interpolate=age/type.lifetime;
			double[][] colors=new double[type.s_color.length][4];
			for(int i=0;i<colors.length;i++){
				colors[i][0]=type.s_color[i][0]*(1-interpolate)+type.e_color[i][0]*interpolate;
				colors[i][1]=type.s_color[i][1]*(1-interpolate)+type.e_color[i][1]*interpolate;
				colors[i][2]=type.s_color[i][2]*(1-interpolate)+type.e_color[i][2]*interpolate;
				colors[i][3]=type.s_color[i][3]*(1-interpolate)+type.e_color[i][3]*interpolate;
			}
			geo.setScale(type.s_scale*(1-interpolate)+type.e_scale*interpolate);
			geo.setColors(colors);
			geo.setRotPos(facing, location);
		}
	}

	public void draw(java.awt.Graphics2D g, double[] campos, double scale){
		geo.draw(g,campos,scale);
	}
}

class ParticleType{
	public double lifetime;
	public double		  s_scale;
	public double[][]	s_color;
	public double		  e_scale;
	public double[][]	e_color;
	public String geo;

	public ParticleType(double life, String g, double ss, double es, double[][] sc, double[][] ec){
		lifetime=life;
		geo=g;
		s_scale=ss;
		e_scale=es;
		s_color=sc;
		e_color=ec;
	}
}

class ParticleEmitter{
	public ParticleType[] ptype;
	public double[] spawnRate;
	public double[] spawnVelocity;
	public double[] spawnAngle;
	public double[] spawnFacing;
	public int[] spawnLayer;

	public double[] spawnRateJitter;
	public double[] spawnVelocityJitter;
	public double[] spawnAngleJitter;
	public double[] spawnFacingJitter;

	protected double[] ratejitterTracker;
	public double[] prevposition; //managed by Entity
	public double[] position; //managed by Entity
	public double facing; //managed by Entity
	public double[] tagCoordinates;
	protected double[] timing;

	public ParticleEmitter(double[] tc, ParticleType[] t, double[] sr, double[] sv, double[] sa, double[] sf, int[] sl, double[] srj, double[] svj, double[] saj, double[] sfj){
		tagCoordinates=tc;
		ptype=t;
		spawnRate=sr;
		spawnVelocity=sv;
		spawnAngle=sa;
		spawnFacing=sf;
		spawnLayer=sl;
		spawnRateJitter=srj;
		spawnVelocityJitter=svj;
		spawnAngleJitter=saj;
		spawnFacingJitter=sfj;
		timing=new double[t.length];
		ratejitterTracker=new double[t.length];
		for(int i=0;i<t.length;i++){
			ratejitterTracker[i]=Math.random()*spawnRateJitter[i];
		}
	}

	public ParticleEmitter(double[] tc, ParticleType[] t, double[] sr, double[] sv, double[] sa, double[] sf, int[] sl){
		this(tc,t,sr,sv,sa,sf,sl,new double[t.length],new double[t.length],new double[t.length],new double[t.length]);
	}


	public void update(double t){
		for(int i=0;i<timing.length;i++){
			timing[i]+=t;
			double prog=0;
			while(timing[i]>spawnRate[i]+ratejitterTracker[i]){
				prog=prog+(spawnRate[i]+ratejitterTracker[i]-timing[i])/t;
				timing[i]-=spawnRate[i]+ratejitterTracker[i];
				ratejitterTracker[i]=Math.random()*spawnRateJitter[i];
				double[] npos=new double[2];
				npos[0]=prog*position[0]+(1-prog)*prevposition[0];
				npos[1]=prog*position[1]+(1-prog)*prevposition[1];
				double sangle=facing+spawnAngle[i]+Math.random()*spawnAngleJitter[i];
				double sfacing=sangle+spawnFacing[i]+Math.random()*spawnFacingJitter[i];
				double velocity=spawnVelocity[i]+Math.random()*spawnVelocityJitter[i];
				new Particle(ptype[i],npos,new double[]{Math.cos(sangle)*velocity,Math.sin(sangle)*velocity},sfacing,spawnLayer[i],t*(1-prog));
			}
		}
	}
}

class SingleEmitter extends ParticleEmitter{
	public SingleEmitter(ParticleType[] t, double[] sr, double[] sv, double[] sa, double[] sf, int[] sl, double[] srj, double[] svj, double[] saj, double[] sfj, double loc[], double face){
		super(new double[]{0,0,0},t,sr,sv,sa,sf,sl,srj,svj,saj,sfj);
		emitSingle(loc,face);
	}

	public SingleEmitter(ParticleType[] t, double[] sr, double[] sv, double[] sa, double[] sf, int[] sl, double[] srj, double[] svj, double[] saj, double[] sfj){
		super(new double[]{0,0,0},t,sr,sv,sa,sf,sl,srj,svj,saj,sfj);
	}

	public void emitSingle(double[] loc, double face){
		position=loc;
		prevposition=loc;
		facing=face;

		for(int i=0;i<timing.length;i++){
			int count=0;
			while(spawnRate[i]+ratejitterTracker[i]>=count+1){
				count++;
				double sangle=facing+spawnAngle[i]+Math.random()*spawnAngleJitter[i];
				double sfacing=sangle+spawnFacing[i]+Math.random()*spawnFacingJitter[i];
				double velocity=spawnVelocity[i]+Math.random()*spawnVelocityJitter[i];
				new Particle(ptype[i],new double[]{position[0],position[1]},new double[]{Math.cos(sangle)*velocity,Math.sin(sangle)*velocity},sfacing,spawnLayer[i]);
			}
		}
	}
}
