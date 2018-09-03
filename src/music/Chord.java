package music;

import java.util.ArrayList;

public class Chord {
	
	public Note tonic;
	public ArrayList<Melody> arpeggio;
	
	public Chord(ArrayList<Melody> lines, Scale scale) {
		int rootPitch = 999; // Lowest pitch
		for (Melody line : lines) {
			for (NotePlay note : line) {
				if (note.note == null) {
					continue;
				}
				int pitch = note.note.getMIDIPitch(scale);
				if (pitch < rootPitch) {
					rootPitch = pitch;
					tonic = note.note;
				}
			}
		}
		Scale tonicScale = this.tonicScale(scale);
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
		return new Scale(root % 12, mode, sig);
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
	
}
