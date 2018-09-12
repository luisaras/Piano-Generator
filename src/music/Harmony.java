package music;

import java.util.ArrayList;

public class Harmony extends ArrayList<Chord> {

	private static final long serialVersionUID = 1L;
	
	public Arpeggio arpeggio;
	
	// ==================================================================================
	// Initialization
	// ==================================================================================
	
	public Harmony() {}
	
	public Harmony(Arpeggio arpeggio) {
		this.arpeggio = arpeggio;
	}
	
	public Harmony clone() {
		Arpeggio arpeggio = this.arpeggio.clone();
		Harmony harmony = new Harmony(arpeggio);
		for (Chord chord : this) {
			harmony.add(chord.clone());
		}
		return harmony;
	}
	
	// ==================================================================================
	// Debug
	// ==================================================================================
	
	public ArrayList<Melody> asMelodyLines(Scale scale, int len) {
		ArrayList<Melody> melodies = new ArrayList<>();
		for (int c = 0; c < size(); c++) {
			ArrayList<Melody> chord = arpeggio.getNotes(scale, get(c));
			for (Melody line : chord) {
				line.displace(c * len);
				melodies.add(line);
			}
		}
		return melodies;
	}
	
	public String toString() {
		String s = get(0).toString();
		for(int i = 1; i < size(); i++) {
			s += "-" + get(i).toString();
		}
		return s;
	}

	// ==================================================================================
	// Cross-over
	// ==================================================================================
	
	/** Gets a sub-list of chord from start to end (in measures), with the same arpeggio.
	 * @param start Initial measure (inclusive).
	 * @param end End measure (exclusive).
	 * @return A sub-list of chords (deep copy).
	 */
	public Harmony cut(int start, int end) {
		Harmony harmony = new Harmony(arpeggio.clone());
		for (int i = start; i < end; i++) {
			harmony.add(get(i).clone());
		}
		return harmony;
	}
	
	/** Inserts all chords of another harmony into this one.
	 * @param other Chords to be inserted at the end of this harmony.
	 */
	public void concatenate(Harmony other) {
		for (Chord chord : other) {
			add(chord);
		}
	}
	
	// ==================================================================================
	// Statistics
	// ==================================================================================
	
	public static class Stats {
		
		Melody.Stats chords;
		Arpeggio.Stats arpeggio;
		
	}
	
	public Stats getStats(Scale scale) {
		Stats s = new Stats();
		s.arpeggio = arpeggio.getStats(scale);
		
		Melody melody = new Melody(arpeggio.duration * size());
		int time = 0;
		for (Chord chord : this) {
			melody.add(new NotePlay(chord.tonic, time++, arpeggio.duration));
		}
		s.chords = melody.getStats(scale);
		
		return s;
	}
	
}
