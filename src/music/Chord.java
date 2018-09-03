package music;

import java.util.ArrayList;

public class Chord {
	
	private static String[] chordNames = new String[] { "I", "II", "III", "IV", "V", "VI", "VII" };
	
	public Note tonic;
	public ArrayList<Melody> arpeggio;
	
	public Chord(ArrayList<Melody> lines, Scale scale) {
		ArrayList<Note> notes = new ArrayList<>();
		for (Melody line : lines) {
			for (NotePlay np : line) {
				notes.add(np.note);
			}
		}
		tonic = findTonic(notes);
		Scale tonicScale = tonicScale(scale);
		arpeggio = new ArrayList<Melody>();
		for (Melody line : lines) {
			scale.convert(line, tonicScale);
			arpeggio.add(line);
		}
	}
	
	public Chord(Note tonic, ArrayList<Melody> arpeggio) {
		this.tonic = tonic;
		this.arpeggio = arpeggio;
	}
	
	public static Note findTonic(ArrayList<Note> notes) {
		Note[] functions = new Note[7];
		for (Note n : notes) {
			functions[n.function] = n;
		}
		int f = -1;
		for (int i = 6; i >= 0; i--) {
			if (functions[i] != null && 
					functions[(i+4) % 7] != null) {
				f = i;
			}
		}
		if (f >= 0)
			return functions[f];
		Note lowest = notes.get(0);
		int lsteps = lowest.getSteps();
		for (Note n : notes) {
			int steps = n.getSteps();
			if (steps < lsteps) {
				lowest = n;
				lsteps = steps;
			}
		}
		return lowest;
	}
	
	public Chord clone(){
		ArrayList<Melody> arpeggio = new ArrayList<>();
		for (Melody line : this.arpeggio) {
			arpeggio.add(line.clone());
		}
		return new Chord(tonic.clone(), arpeggio);
	}
	
	public Scale tonicScale(Scale pieceScale) {
		int root = tonic.getMIDIPitch(pieceScale);
		int mode = (pieceScale.mode + tonic.function) % 7;
		int sig = Scale.getSignature(root, mode);
		return new Scale(root, mode, sig);
	}
	
	public ArrayList<Melody> asMelodyLines(Scale pieceScale) {
		Scale tonicScale = this.tonicScale(pieceScale);
		ArrayList<Melody> melodies = new ArrayList<>();
		for (Melody line : arpeggio) {
			line = line.clone();
			tonicScale.convert(line, pieceScale);
			melodies.add(line);
		}
		return melodies;
	}
	
	public String toString() {
		return chordNames[tonic.function] + (tonic.octaves);
	}
	
}
