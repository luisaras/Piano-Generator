package gen;

import gen.Melody.Note;

import java.util.ArrayList;

public class Harmony {

	public ArrayList<Scale> chords = new ArrayList<>();
	public ArrayList<Melody> arpeggio = null;
	
	public void addChord(ArrayList<Melody> lines, Scale scale) {
		int rootPitch = 999; // Lowest pitch
		Scale.Position rootPos = null;
		for (Melody line : lines) {
			for (Note note : line.notes) {
				if (note.pitch == null) {
					//System.out.print("0(" + note.time + ") ");
					continue;
				}
				int pitch = note.pitch.getMIDIPitch(scale);
				//System.out.print((note.pitch.function + 1) + "(" + note.time + ") ");
				if (pitch < rootPitch) {
					rootPitch = pitch;
					rootPos = note.pitch;
				}
			}
			//System.out.println();
		}
		int mode = (scale.mode + rootPos.function) % 7;
		int sig = Scale.getSignature(rootPitch, mode);
		Scale root = new Scale(rootPitch, mode, sig);
		//System.out.println(root.toString() + " " + (rootPitch %12) + " " + root.mode);
		if (arpeggio == null) {
			arpeggio = new ArrayList<Melody>();
			for (Melody line : lines) {
				scale.convert(line.notes, root);
				arpeggio.add(line);
			}
		}
		chords.add(root);
	}
	
	public ArrayList<Melody> asMelodyLines(Scale scale, int len) {
		ArrayList<Melody> melodies = new ArrayList<>();
		for (int c = 0; c < chords.size(); c++) {
			for (Melody line : arpeggio) {
				line = line.clone();
				line.displace(c * len);
				chords.get(c).convert(line.notes, scale);
				melodies.add(line);
			}
		}
		return melodies;
	}
	
	
}
