package sparks.maingame;

public interface Action {
	public long completeAt();
	public Result update(long datum, MissionState m);

	public interface Result {
		public void apply();
	}
}
