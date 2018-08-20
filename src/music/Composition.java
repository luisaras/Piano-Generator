package music;

public class Composition {
	
	public Scale scale;
	
	public int duration = 4; // measures
	public int numerator; // beats
	public int denominator; // beats / measure
	public double bpm; // beats / minute
	
	public Harmony harmony;
	public Melody melody;
	
	/** The duration of the piece in seconds.
	 * @return The duration in seconds.
	 */
	public double getDuration() {
		return duration * denominator / bpm * 60; 
	}
	
}
