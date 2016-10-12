package sparks.graphics;

public class TouchEvent {
	public final double xStart;
	public final double yStart;
	public final double xEnd;
	public final double yEnd;
	public final boolean finalized;

	private TouchEvent(double xStart, double yStart, double xEnd, double yEnd, boolean finalized) {
		this.xStart = xStart;
		this.yStart = yStart;
		this.xEnd = xEnd;
		this.yEnd = yEnd;
		this.finalized = finalized;
	}

	public TouchEvent end(double x, double y) {
		return new TouchEvent(xStart, yStart, x, y, true);
	}

	public TouchEvent moved(double x, double y) {
		return new TouchEvent(xStart, yStart, x, y, false);
	}

	public static TouchEvent start(double x, double y) {
		return new TouchEvent(x, y, x, y, false);
	}

	public TouchEvent remap(double xoff, double yoff, double xscale, double yscale) {
		return new TouchEvent((xStart - xoff) / xscale, (yStart - yoff) / yscale, (xEnd - xoff) / xscale, (yEnd - yoff) / yscale, finalized);
	}

}
