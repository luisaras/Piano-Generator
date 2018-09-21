package music;

public class Composition {
	
	public String name;
	
	public Scale scale;
	
	public int length; // measures
	public int numerator; // beats
	public int denominator; // beats / measure
	public double bpm; // beats / second
	
	public Harmony harmony;
	public Melody melody;
	
	public Composition clone() {
		return cut(0, length);
	}
	
	public Composition cloneSignature() {
		Composition composition = new Composition();
		composition.length = length;
		composition.numerator = numerator;
		composition.denominator = denominator;
		composition.bpm = bpm;
		composition.scale = scale;
		return composition;
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
		Composition composition = cloneSignature();
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
	
	public Melody mergeTracks() {
		Melody tracks = harmony.asMelody(scale);
		tracks.addAll(melody);
		tracks.sort();
		return tracks;
	}
	
	public double getMinutes() {
		return length * denominator / bpm;
	}

	public double getBeats() {
		return length * numerator;
	}
	
}
