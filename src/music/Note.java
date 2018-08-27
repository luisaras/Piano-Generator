package music;

public class Note {
	
	public int function = 0;
	public int accidental = 0; // Out of scale
	public int octaves = 0;
	
	public Note(int func, int acc, int oct) { 
		function = func; 
		accidental = acc; 
		octaves = oct; 
	}
	
	public int getPitch(Scale scale) {
		return getMIDIPitch(scale) - scale.root;
	}
	
	public int getMIDIPitch(Scale scale) {
		return octaves * 12 + accidental + scale.getPitch(function);
	}
	
	public String toString() {
		String acc = accidental < 0 ? "b" : accidental > 0 ? "#" : "";
		return octaves + "oct " + (function + 1) + acc;
	}
	
	public Note clone() {
		return new Note(function, accidental, octaves);
	}
	
}