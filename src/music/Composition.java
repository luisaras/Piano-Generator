package music;

public class Composition {
	
	public Scale scale;
	
	public int duration = 4; // measures
	public int numerator; // beats
	public int denominator; // beats / measure
	public double bps; // beats / second
	
	public Harmony harmony;
	public Melody melody;
	
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
