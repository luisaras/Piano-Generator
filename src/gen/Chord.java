package gen;

import gen.Melody.Note;
import java.util.ArrayList;

public class Chord {

	public Scale root;
	public ArrayList<Melody> arpeggio;
	
	public Chord(Scale r, double d, ArrayList<Melody> a) { 
		root = r; arpeggio = a; 
	}
	
	public Chord(ArrayList<Melody> lines, Scale scale) {
		int rootPitch = 999; // Lowest pitch
		Scale.Position rootPos = null;
		for (Melody line : lines) {
			for (Note note : line.notes) {
				if (note.pitch == null) {
					System.out.print("0(" + note.time + ") ");
					continue;
				}
				int pitch = note.pitch.getMIDIPitch(scale);
				System.out.print((note.pitch.function + 1) + "(" + note.time + ") ");
				if (pitch < rootPitch) {
					rootPitch = pitch;
					rootPos = note.pitch;
				}
			}
			System.out.println();
		}
		int mode = (scale.mode + rootPos.function) % 7;
		int sig = Scale.getSignature(rootPitch, mode);
		root = new Scale(rootPitch, mode, sig);
		System.out.println(root.toString() + " " + (rootPitch %12) + " " + root.mode);
		arpeggio = new ArrayList<Melody>();
		for (Melody line : lines) {
			scale.convert(line.notes, root);
			arpeggio.add(line);
		}
	}
	
	public ArrayList<Melody> asMelodyLines(Scale scale) {
		ArrayList<Melody> melodies = new ArrayList<>();
		for (Melody line : arpeggio) {
			root.convert(line.notes, scale);
			melodies.add(line);
		}
		return melodies;
	}
	
}
