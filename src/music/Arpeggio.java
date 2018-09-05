package music;

import java.util.ArrayList;

public class Arpeggio extends ArrayList<Melody> {

	private static final long serialVersionUID = 1L;

	public Arpeggio() {}
	
	public Arpeggio(ArrayList<Melody> lines, Scale scale, Chord chord) {
		Scale tonic = chord.tonicScale(scale);
		for (Melody line : lines) {
			scale.convert(line, tonic);
			add(line);
		}
	}
	
	public Arpeggio clone() {
		Arpeggio arpeggio = new Arpeggio();
		for (Melody line : this) {
			arpeggio.add(line.clone());
		}
		return arpeggio;
	}
	
	public ArrayList<Melody> getNotes(Scale pieceScale, Chord chord) {
		Scale tonicScale = chord.tonicScale(pieceScale);
		ArrayList<Melody> melodies = new ArrayList<>();
		for (Melody line : this) {
			line = line.clone();
			tonicScale.convert(line, pieceScale);
			melodies.add(line);
		}
		return melodies;
	}
	
}
