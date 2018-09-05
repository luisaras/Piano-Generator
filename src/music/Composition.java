package music;

public class Composition {
	
	public String name;
	
	public Scale scale;
	
	public int duration; // measures
	public int numerator; // beats
	public int denominator; // beats / measure
	public double bpm; // beats / second
	
	public Harmony harmony;
	public Melody melody;
	
	public Composition clone() {
		return cut(0, duration);
	}
	
	// ==================================================================================
	// Cross-over
	// ==================================================================================
	
	/** Gets a sub-piece from start to end (in measures).
	 * @param start Initial measure (inclusive).
	 * @param end End measure (exclusive).
	 * @return A sub-piece (deep copy).
	 */
	public Composition cut(int start, int end) {
		Composition composition = new Composition();
		composition.duration = duration;
		composition.numerator = numerator;
		composition.denominator = denominator;
		composition.bpm = bpm;
		composition.scale = scale;
		composition.melody = melody.cut(start * numerator, end * numerator);
		composition.harmony = harmony.cut(start, end);
		return composition;
	}
	
	public Composition concatenate(Composition second) {
		if (melody == null)
			melody = second.melody;
		else
			melody.concatenate(second.melody);
		if (harmony == null)
			harmony = second.harmony;
		else
			harmony.concatenate(second.harmony);
		return this;
	}
	
	// ==================================================================================
	// Statistics
	// ==================================================================================
	
	public static class Stats {
		
		public double seconds = 0;
		public double notesPerSecond = 0;
		
		public Melody.Stats melody;
		public Harmony.Stats harmony;
		
	}
	
	public Stats getStats() {
		Stats s = new Stats();
		
		s.seconds = duration * denominator * 60 / bpm;
		s.melody = melody.getStats(scale);
		s.harmony = harmony.getStats(scale);
		s.notesPerSecond = melody.size() / s.seconds;
		
		return s;
	}
	
}
