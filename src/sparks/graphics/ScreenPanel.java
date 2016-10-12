package sparks.graphics;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.VolatileImage;

public abstract class ScreenPanel {
	public void render(Graphics2D g, Dimension renderResolution, AffineTransform affine) {
		VolatileImage v = g.getDeviceConfiguration().createCompatibleVolatileImage(renderResolution.width, renderResolution.height, Transparency.OPAQUE);
		Graphics2D g2 = v.createGraphics();
		draw(g2, renderResolution);
		g.drawImage(v, affine, null);
	}

	public abstract void handleTouch(TouchEvent e);

	protected abstract void draw(Graphics2D g, Dimension dimension);
}
