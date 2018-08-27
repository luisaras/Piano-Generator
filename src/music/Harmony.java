package music;

import java.util.ArrayList;

public class Harmony extends ArrayList<Chord> {

	private static final long serialVersionUID = 1L;
	
	public ArrayList<Melody> asMelodyLines(Scale scale, int len) {
		ArrayList<Melody> melodies = new ArrayList<>();
		for (int c = 0; c < size(); c++) {
			ArrayList<Melody> chord = get(c).asMelodyLines(scale);
			for (Melody line : chord) {
				line.displace(c * len);
				melodies.add(line);
			}
		}
		return melodies;
	}
	
	public Harmony clone() {
		Harmony harmony = new Harmony();
		for (Chord chord : this) {
			harmony.add(chord.clone());
		}
		return harmony;
	}

	// ==================================================================================
	// Cross-over
	// ==================================================================================
	
	public Harmony cut(int start, int end) {
		Harmony harmony = new Harmony();
		for (int i = start; i < end; i++) {
			harmony.add(get(i));
		}
		return harmony;
	}
	
	public void concatenate(Harmony other) {
		for (Chord chord : other) {
			add(chord);
		}
	}
	
	// ==================================================================================
	// Statistics
	// ==================================================================================
	
	public static class Stats {
		
	}
	
	public Stats getStats(Scale scale) {
		return new Stats();
	}
	
}
