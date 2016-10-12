package sparks.graphics;

import sparks.shared.*;
import sparks.maingame.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.awt.event.*;

public class MissionMap extends ScreenPanel {
	private final Provider<MissionState> missionStateProvider;
	private final Provider<Camera2D> missionCameraProvider;

	public MissionMap(Provider<MissionState> missionStateProvider, Provider<Camera2D> missionCameraProvider) {
		this.missionStateProvider = missionStateProvider;
		this.missionCameraProvider = missionCameraProvider;
	}

	@Override
	public void handleTouch(TouchEvent e) {
	}


	@Override
	protected void draw(Graphics2D g, Dimension bounds) {
		PriorityQueue<Integer> pq=new PriorityQueue<Integer>(20);
		MissionState frame = missionStateProvider.get();
		Camera2D camera = missionCameraProvider.get();
		for (Entity e : frame.entities) if (!pq.contains(e.drawLayer)) pq.add(e.drawLayer);
		for (Particle p : frame.particles) if (!pq.contains(p.drawLayer)) pq.add(p.drawLayer);

		AffineTransform affine=new AffineTransform();
		g.setClip(0, 0, bounds.width, bounds.height);
		g.setPaint(new Color(255, 255, 255));
		g.fillRect(0, 0, bounds.width, bounds.height);
		affine.translate(bounds.width/2, bounds.height/2);
		affine.scale(1, -1);
		g.transform(affine);

		//draw grid
		/*if (drawGrid) {
			if (gridResolution*camera.zoom>3) {
				g.setPaint(new Color(220, 220, 220, 255));
				for (double x=gridResolution*((int)((camx-bounds.width/2/camera.zoom)/gridResolution));x<=(camx+bounds.width/2/camera.zoom);x+=gridResolution) {
					for (double y=gridResolution*((int)((camy-bounds.height/2/camera.zoom)/gridResolution));y<=(camy+bounds.height/2/camera.zoom);y+=gridResolution) {
						g.fillRect((int)((x-camx)*camera.zoom-1), (int)((y-camy)*camera.zoom-1), 3, 3);
					}
				}
			}
		}*/

		//draw environment
		affine=new AffineTransform();
		affine.translate(camera.x * camera.zoom, camera.y * camera.zoom);
		g.transform(affine);
		double[] campos=new double[]{camera.x, camera.y};
		Integer i = null;
		while((i = pq.poll()) != null) {
			for (Entity e : frame.entities)
				if (e.drawLayer==i) e.getGeometry().draw(g, campos, camera.zoom);
			for (Particle p : frame.particles)
				if (p.drawLayer==i) p.draw(g, campos, camera.zoom);
		}
		drawWalls(g, camera, frame.walls);
		drawFogOfWar(g, camera, frame.entities, frame.walls, bounds);
	}

	protected void drawWalls(Graphics2D g, Camera2D camera, Collection<PolyLine> walls) {
		g.setPaint(new Color(255, 0, 0, 255));
		g.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
		for (PolyLine p : walls) {
			for (PolyLine i : p.toArray()) {
				g.drawLine((int)(i.x*camera.zoom), (int)(i.y*camera.zoom), (int)(i.next.x*camera.zoom), (int)(i.next.y*camera.zoom));
			}
		}
	}

	protected void drawFogOfWar(Graphics2D g, Camera2D camera, Collection<Entity> entities, Collection<PolyLine> walls, Dimension bounds) {
		g.setPaint(new Color(0, 0, 0, 255));
		//get all visibility areas and merge them.
		Area visarea=new Area();
		LinkedList<Area> visareas=new LinkedList<>();
		for (Entity e : entities) {
			if (e.visibilityFaction==-2 /*|| e.visibilityFaction==players.get(whoami).*/) {
				visarea.add(visibilityFromEntity(e, walls, camera.zoom));
			}
		}

		//add screen area.
		double minx=camera.x*camera.zoom-bounds.width;
		double maxx=camera.x*camera.zoom+bounds.width;
		double miny=camera.y*camera.zoom-bounds.height;
		double maxy=camera.y*camera.zoom+bounds.height;
		Path2D.Double shape=new Path2D.Double();
		shape.moveTo(minx, miny);
		shape.lineTo(minx, maxy);
		shape.lineTo(maxx, maxy);
		shape.lineTo(maxx, miny);
		shape.closePath();
		Area screenarea=new Area(shape);
		screenarea.subtract(visarea);
		g.fill(screenarea);
	}

	public Area visibilityFromEntity(Entity e, Collection<PolyLine> walls, double zoom) {
		double[] position = e.getPosition();
		double camx=position[0];
		double camy=position[1];

		LinkedList<PolyLine> closeWalls=new LinkedList<>();
		for (PolyLine p : walls) {
			p=p.clone();
			PolyLine f=p;
			boolean s1=true;
			while(s1 || f!=p) {
				s1=false;
				closeWalls.add(p);
				p=p.next;
			}
		}
		closeWalls.add(PolyLine.makePolygon(new double[]{-1000000, 1000000, 1000000, -1000000}, new double[]{-1000000, -1000000, 1000000, 1000000}));

		for (PolyLine w : closeWalls) { //for each point, determine if it is visible (not occluded by another wall)
			for (PolyLine p : closeWalls) {
				if (p==w) continue;
				if (p==w.prev) continue;
				if (p.intersectsSingle(camx, camy, w.x, w.y)) {
					w.cwv=false;
					w.ccwv=false;
					break;
				}
				w.cwv=true;
				w.ccwv=true;
			}
		}

		Collection<PolyLine> splits=new LinkedList<PolyLine>();
		for (PolyLine w : closeWalls) splits.add(w);

		for (PolyLine w : closeWalls) { //for every visible point, determine if it is a corner.
			if (!w.cwv) continue; //skip non-visible points

			double[] CtoW=new double[]{w.x-camx, w.y-camy};
			double[] CtoWP=new double[]{w.prev.x-camx, w.prev.y-camy};
			double[] CtoWN=new double[]{w.next.x-camx, w.next.y-camy};

			double crossPrev=MathManager.cross2d(CtoW, CtoWP);
			double crossNext=MathManager.cross2d(CtoW, CtoWN);
			if (crossPrev*crossNext<0) continue; //if not a corner, continue.

			if (crossPrev<0) w.cwv=false;
			else w.ccwv=false;

			//for every corner, project backward to find any other line that it splits and add a segment there.
			double minHit=Double.MAX_VALUE;
			double minHitG=.5;
			PolyLine whoHit=null;

			double[] ray=MathManager.normalize2d(new double[]{w.x-camx, w.y-camy});
			for (PolyLine x : splits) {
				if (x==w) continue;
				if (x==w.prev) continue;
				double[] d=x.intersectsRayCompute(ray[0], ray[1], w.x, w.y);
				if (d[0]!=-1 && d[0]<minHit) {
					minHit=d[0];
					minHitG=d[1];
					whoHit=x;
				}
			}
			if (whoHit!=null) { //this must happen. Split the line.
				whoHit.split(minHitG);
				w.temp=whoHit.next;
				whoHit.next.temp=w;
				w.temp.ccwv=!w.ccwv;
				w.temp.cwv=!w.cwv;
				splits.add(w.temp);
			} else {
				Console.error("Sight-giving object is outside bounding wall ("+camx+", "+camy+").");
				return new Area();
			}
		}

		PolyLine x=null; //start from a known visible vertex
		for (PolyLine w : closeWalls) {
			if (w.cwv && w.ccwv) {
				x=w;
				break;
			}
		}
		boolean f=true;
		PolyLine first=x; //start from a known visible vertex

		Path2D.Double shape=new Path2D.Double();
		shape.moveTo(x.x*zoom, x.y*zoom);

		while(f || first!=x) {
			f=false;
			PolyLine next;
			if (!x.cwv) next=x.temp;
			else if (MathManager.cross2d(new double[]{x.x-camx, x.y-camy}, new double[]{x.next.x-camx, x.next.y-camy})>0) next=x.next;
			else next=x.prev;
			if (next!=first) shape.lineTo(next.x*zoom, next.y*zoom);
			x=next;
		}
		shape.closePath();

		return new Area(shape);
	}
}
