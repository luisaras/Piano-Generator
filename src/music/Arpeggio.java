package music;

import java.util.ArrayList;

public class Arpeggio extends ArrayList<ChordPlay> {

	private static final long serialVersionUID = 1L;
	
	public Arpeggio() {}
	
	public Arpeggio(ArrayList<Melody> lines, Scale scale, Chord chord) {
		Scale tonic = chord.tonicScale(scale);
		for (Melody line : lines) {
			scale.convert(line, tonic);
			for (NotePlay np : line) {
				insertNote(np);
			}
		}
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
		Arpeggio arpeggio = new Arpeggio();
		for (ChordPlay cp : this) {
			arpeggio.add(cp.clone());
		}
		return arpeggio;
	}
	
	public double getDuration() {
		double end = 0;
		for (ChordPlay cp : this) {
			end = Math.max(end, cp.time + cp.duration);
		}
		return end;
	}
	
	public ArrayList<Melody> getNotes(Scale pieceScale, Chord chord) {
		double duration = getDuration();
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
	
}
