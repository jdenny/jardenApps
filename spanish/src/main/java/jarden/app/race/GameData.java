package jarden.app.race;

public class GameData {
	public final static int RUNNING = 0;
	public final static int CAUGHT = 1;
	public final static int PAUSED = 2;
	public int position = 0; // square within lane, starting from 0
	public int level = 1;
	public int status = RUNNING;
	
	public GameData() {
	}
	public GameData(int level) {
		this.level = level;
	}
	public GameData(int position, int level, int status) {
		this.position = position;
		this.level = level;
		this.status = status;
	}
}
