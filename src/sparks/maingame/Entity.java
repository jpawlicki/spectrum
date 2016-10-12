package sparks.maingame;

import sparks.shared.MathManager;
import java.util.Collection;
import java.util.LinkedList;
import java.util.HashSet;

/**
 * An Entity represents a persistent non-wall object on the mission map.
 * Entities are primarily modified by actions.
 */
public class Entity{
	public int visibilityFaction=-1; //-1 means no visibilty, -2 means makes visibility for all players. Non-negative numbers correspond to factions.
	private Geometry geo;
	private double[] position=new double[2];
	private double facing;
	public final int drawLayer = 1; // TODO(waffles): Remove this.

	protected Entity(String geoName) {
		geo=FileLoader.getGeometry(geoName);
		geo.owner=this;
	}

	void setPosition(double[] d){
		position=d;
		if(geo!=null) geo.setRotPos(facing,position);
	}

	public double[] getPosition() {
		return new double[] { position[0], position[1] };
	}

	public double getFacing() {
		return facing;
	}

	public Geometry getGeometry() {
		return geo;
	}
}
