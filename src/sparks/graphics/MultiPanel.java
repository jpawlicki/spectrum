package sparks.graphics;

import java.awt.geom.AffineTransform;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class MultiPanel extends ScreenPanel {
	private final ArrayList<Node> panels;

	public MultiPanel() {
		this.panels = new ArrayList<>();
	}

	public MultiPanel(List<Node> panels) {
		this.panels = new ArrayList<>(panels);
	}

	public void addPanel(Node node) {
		panels.add(node);
	}

	@Override
	protected void draw(Graphics2D g, Dimension d) {
		for (Node n : panels) {
			n.panel.render(g, new Dimension(d.width * n.loc.width / 10000, d.height * n.loc.height / 10000), AffineTransform.getTranslateInstance(d.width * n.loc.x / 10000.0, d.height * n.loc.y / 10000.0));
		}
	}

	@Override
	public void handleTouch(TouchEvent e) {
		double x = e.xStart;
		double y = e.yStart;
		ListIterator<Node> it = panels.listIterator(panels.size());
		while (it.hasPrevious()) {
			Node n = it.previous();
			if (n.loc.contains(x, y)) {
				n.panel.handleTouch(e.remap(n.loc.x, n.loc.y, n.loc.width / 10000.0, n.loc.height / 10000.0));
				break;
			}
		}
	}

	public static class Node {
		private final ScreenPanel panel;
		private final Rectangle loc; // This rectangle is in permilli: 0 is top/left, 1000 is bottom/right. The aspect ratio may not be preserved.
		private final boolean hidden;

		public Node(ScreenPanel panel, Rectangle loc, boolean hidden) {
			this.panel = panel;
			this.loc = loc;
			this.hidden = hidden;
		}
	}
}
