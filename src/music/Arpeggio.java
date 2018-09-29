package music;

import java.util.ArrayList;

public class Arpeggio extends ArrayList<ChordPlay> {

	private static final long serialVersionUID = 1L;
	
	public double duration;
	
	// ==================================================================================
	// Initialization
	// ==================================================================================
	
	public Arpeggio(double duration) {
		this.duration = duration;
	}
	
	public Arpeggio(ArrayList<Melody> lines, Scale scale, Chord chord, double duration) {
		Scale tonic = chord.tonicScale(scale);
		for (Melody line : lines) {
			scale.convert(line, tonic);
			for (NotePlay np : line) {
				insertNote(np);
			}
		}
		this.duration = duration;
	}
	
	public void insertNote(NotePlay np) {
		for(ChordPlay cp : this) {
			if (cp.duration == np.duration && cp.time == np.time) {
				cp.add(np.note);
				return;
			}
		}
		ChordPlay cp = new ChordPlay(np.time, np.duration);
		cp.add(np.note);
		for(int i = 0; i < size(); i++) {
			if (np.time < get(i).time) {
				add(i, cp);
				return;
			}
		}
		add(cp);
	}
	
	public Arpeggio clone() {
		Arpeggio arpeggio = new Arpeggio(duration);
		for (ChordPlay cp : this) {
			arpeggio.add(cp.clone());
		}
		return arpeggio;
	}
	
	// ==================================================================================
	// Debug
	// ==================================================================================
	
	public ArrayList<Melody> getNotes(Scale pieceScale, Chord chord) {
		Scale tonicScale = chord.tonicScale(pieceScale);
		ArrayList<Melody> melodies = new ArrayList<>();
		for (ChordPlay cp : this) {
			for(int i = 0; i < cp.size(); i++) {
				if (melodies.size() <= i)
					melodies.add(new Melody(duration));
				melodies.get(i).add(new NotePlay(cp.get(i).clone(), cp.time, cp.duration));
			}
		}
		for (Melody melody : melodies) {
			tonicScale.convert(melody, pieceScale);
		}
		return melodies;
	}
	
	public Melody getPlays() {
		Melody melody = new Melody(duration);
		for (ChordPlay cp : this) {
			melody.add(new NotePlay(null, cp.time, cp.duration));
		}
		return melody;
	}
	
	public Melody asMelody(Scale pieceScale, Chord chord) {
		Melody melody = new Melody(duration);
		for (ChordPlay cp : this) {
			for(Note n : cp) {
				melody.add(new NotePlay(n, cp.time, cp.duration));
			}
		}
		Scale tonicScale = chord.tonicScale(pieceScale);
		tonicScale.convert(melody, pieceScale);
		return melody;
	}
	
}
