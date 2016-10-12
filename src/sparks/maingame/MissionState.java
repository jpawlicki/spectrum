package sparks.maingame;

import sparks.shared.GameState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class MissionState extends GameState {
	public final ArrayList<Entity> entities = new ArrayList<>();
	public final ArrayList<PolyLine> walls = new ArrayList<>();
	public final ArrayList<Particle> particles = new ArrayList<>();
	private final PriorityQueue<Action> actions = new PriorityQueue<Action>(100, new Comparator<Action>() {
		@Override
		public int compare(Action a, Action b) {
			long c = a.completeAt() - b.completeAt();
			if (c > 0) return 1;
			if (c < 0) return -1;
			return 0;
		}
	});

	@Override
	protected void update(long toTick) {
		long nextTick = 0;
		while (!actions.isEmpty() && ((nextTick = actions.peek().completeAt()) <= toTick)) {
			updateInner(nextTick);
		}
	}

	private void updateInner(long toTick) {
		LinkedList<Action.Result> results = new LinkedList<>();
		for (Action a : actions) {
			Action.Result r = a.update(toTick, this);
			if (r != null) results.add(r);
		}
		for (Action.Result r : results) {
			r.apply();
		}
	}
}
