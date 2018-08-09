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
				if (note.pitch == null)
					continue;
				int pitch = note.pitch.getMIDIPitch(scale);
				System.out.print(note.pitch.function + " ");
				if (pitch < rootPitch) {
					rootPitch = pitch;
					rootPos = note.pitch;
				}
			}
			System.out.println();
		}
		System.out.println(rootPos.function);
		root = new Scale(rootPitch, scale.mode + rootPos.function);
		arpeggio = new ArrayList<Melody>();
		for (int i = 0; i < lines.size(); i++) {
			ArrayList<Note> newLine = scale.convert(lines.get(i).notes, root);
			arpeggio.add(new Melody(newLine, lines.get(i).duration));
		}
		root = scale;
		arpeggio = lines;
	}
	
	public ArrayList<Melody> asMelodyLines(Scale scale) {
		ArrayList<Melody> melodies = new ArrayList<>();
		for (int i = 0 ; i < arpeggio.size(); i++) {
			ArrayList<Note> newLine = root.convert(arpeggio.get(i).notes, scale);
			melodies.add(new Melody(newLine, arpeggio.get(i).duration));
		}
		return melodies;
	}
	
}
