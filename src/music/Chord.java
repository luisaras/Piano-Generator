package music;

import java.util.ArrayList;

public class Chord {
	
	public Note tonic;
	public Scale scale;
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
		int mode = (scale.mode + tonic.function) % 7;
		int sig = Scale.getSignature(rootPitch, mode);
		this.scale = new Scale(rootPitch, mode, sig);
		//System.out.println(root.toString() + " " + (rootPitch %12) + " " + root.mode);
		arpeggio = new ArrayList<Melody>();
		for (Melody line : lines) {
			scale.convert(line, this.scale);
			arpeggio.add(line);
		}
	}
	
	public Chord(Note tonic, Scale scale, ArrayList<Melody> arpeggio) {
		this.tonic = tonic;
		this.scale = scale;
		this.arpeggio = arpeggio;
	}
	
	public Chord clone(){
		ArrayList<Melody> arpeggio = new ArrayList<>();
		for (Melody line : this.arpeggio) {
			arpeggio.add(line.clone());
		}
		return new Chord(tonic.clone(), scale.clone(), arpeggio);
	}
	
	public ArrayList<Melody> asMelodyLines(Scale scale) {
		ArrayList<Melody> melodies = new ArrayList<>();
		for (Melody line : arpeggio) {
			line = line.clone();
			this.scale.convert(line, scale);
			melodies.add(line);
		}
		return melodies;
	}
	
}
