package music;

import java.util.ArrayList;

public class ChordPlay extends ArrayList<Note> {

	private static final long serialVersionUID = 1L;
	
	public double time;
	public double duration;
	
	public ChordPlay() {}
	
	public ChordPlay(double time, double duration) {
		this.time = time;
		this.duration = duration;
	}
	
	public ChordPlay clone() {
		ChordPlay cp = new ChordPlay(time, duration);
		for (Note n : this) {
			cp.add(n.clone());
		}
		return cp;
	}
	
	public double getEnd() {
		return time + duration;
	}
	
	public void setEnd(double end) {
		duration = end - time;
	}
	
}
