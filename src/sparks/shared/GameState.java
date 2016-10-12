package sparks.shared;
import sparks.graphics.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.LinkedList;

public abstract class GameState {
	private final long timeDatum = System.nanoTime();
	private final ScreenPanel screen;
	private final Provider<Dimension> panelSizeProvider;
	private final LinkedList<Touch> inputQueue = new LinkedList<>();

	protected GameState(ScreenPanel screen, Provider<Dimension> panelSizeProvider) {
		this.screen = screen;
		this.panelSizeProvider = panelSizeProvider;
	}

	public void update() {
		long tick = (timeDatum - System.nanoTime()) / 1000;
		handleInput(tick);
		// handle network events?
		update(tick);
	}

	public void draw(Graphics2D g) {
		screen.render(g, panelSizeProvider.get(), new AffineTransform());
	}

	private synchronized void handleInput(long tick) {
		for (Touch t : inputQueue) {
			Dimension screenSize = panelSizeProvider.get();
			//screen.handleTouch(TouchEvent((double)t.x / screenSize.width * 10000, (double)t.y / screenSize.height * 10000);
		}
		inputQueue.clear();
	}

	public synchronized void handleTouch(int x, int y) {
		inputQueue.push(new Touch(x, y));
	}

	protected abstract void update(long tick);

	private static final class Touch {
		public final int x;
		public final int y;

		public Touch(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}
