package music;

import java.util.ArrayList;

public class Harmony extends ArrayList<Scale> {

	private static final long serialVersionUID = 1L;
	
	public ArrayList<Melody> arpeggio = null;
	
	public void addChord(ArrayList<Melody> lines, Scale scale) {
		int rootPitch = 999; // Lowest pitch
		Note rootPos = null;
		for (Melody line : lines) {
			for (NotePlay note : line) {
				if (note.note == null) {
					continue;
				}
				int pitch = note.note.getMIDIPitch(scale);
				if (pitch < rootPitch) {
					rootPitch = pitch;
					rootPos = note.note;
				}
			}
		}
		int mode = (scale.mode + rootPos.function) % 7;
		int sig = Scale.getSignature(rootPitch, mode);
		Scale root = new Scale(rootPitch, mode, sig);
		//System.out.println(root.toString() + " " + (rootPitch %12) + " " + root.mode);
		if (arpeggio == null) {
			arpeggio = new ArrayList<Melody>();
			for (Melody line : lines) {
				scale.convert(line, root);
				arpeggio.add(line);
			}
		}
		add(root);
	}
	
	public ArrayList<Melody> asMelodyLines(Scale scale, int len) {
		ArrayList<Melody> melodies = new ArrayList<>();
		for (int c = 0; c < size(); c++) {
			for (Melody line : arpeggio) {
				line = line.clone();
				line.displace(c * len);
				get(c).convert(line, scale);
				melodies.add(line);
			}
		}
		return melodies;
	}
	
	public Harmony clone() {
		Harmony harmony = new Harmony();
		harmony.arpeggio = new ArrayList<>();
		
		for (Scale chord : this) {
			harmony.add(chord.clone());
		}
		for (Melody line : arpeggio) {
			harmony.arpeggio.add(line.clone());
		}
		
		return harmony;
	}

	
	// ==================================================================================
	// Statistics
	// ==================================================================================
	
	public static class Stats {
		
	}
	
	public Stats getStats(Scale scale) {
		return new Stats();
	}
	
}
