package music;

public class Composition {
	
	public Scale scale;
	
	public int duration = 4; // measures
	public int numerator; // beats
	public int denominator; // beats / measure
	public double bps; // beats / second
	
	public Harmony harmony;
	public Melody melody;
	
	// ==================================================================================
	// Cross-over
	// ==================================================================================
	
	public Composition cut(int start, int end) {
		Composition composition = new Composition();
		composition.duration = duration;
		composition.numerator = numerator;
		composition.denominator = denominator;
		composition.bps = bps;
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
		
		s.seconds = duration * denominator / bps;
		s.melody = melody.getStats(scale);
		s.harmony = harmony.getStats(scale);
		
		return s;
	}
	
}
