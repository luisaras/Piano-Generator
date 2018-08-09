package gen;

import gen.Melody.Note;

import java.util.ArrayList;

public class Scale {

	private final static int[] C_MAJOR_VALUES = new int[] { 0, 0, 1, 1, 2, 3, 3, 4, 4, 5, 5, 6 };
	private final static int[] C_MAJOR_STEPS = new int[] { 2, 2, 1, 2, 2, 2, 1 };
	private final static int[] MAJOR_ACCIDENTALS = new int[] { 0, 7, 2, -3, 4, -1, 6, 1, -4, 3, -2, 5 };
	private final static int[] MINOR_ACCIDENTALS = new int[] { -3, 4, -1, 6, 1, -4, 3, -2, 5, 0, 7, 2 };
	
	public static class Position {
		public int function = 0;
		public int accidental = 0; // Out of scale
		public int octaves = 0;
		public Position(int f, int a, int o) { function = f; accidental = a; octaves = o; }
		public int getMIDIPitch(Scale scale) {
			return octaves * 12 + scale.steps[function] + accidental;
		}
	}
	
	public final int mode;
	public final int root;
	public final int rootValue;
	public final String name;
	
	public final boolean[] contains = new boolean[12];
	public final int[] steps = new int[7];
	public final int[] accidentals = new int[7];
	public final int[] positions = new int[7];
	
	// ==================================================================================
	// Initialization
	// ==================================================================================
	
	public Scale(int root, int mode) {
		int[] accidentals = mode == 0 ? MAJOR_ACCIDENTALS : MINOR_ACCIDENTALS;
		for (int i = 0; i < 12; i++) {
			if (accidentals[i] == root) {
				root = i;
				break;
			}
		}
		this.mode = mode;
		this.root = root;
		this.rootValue = C_MAJOR_VALUES[root % 12];
		this.name = getName();
		initializeArrays();
	}
	
	public Scale (int root, int rootValue, int mode) {
		this.mode = mode;
		this.root = root;
		this.rootValue = rootValue;
		this.name = getName();
		initializeArrays();
	}
	
	private void initializeArrays() {		
		for (int i = 0; i < 12; i++)
			this.contains[i] = false;
		
		int step = 0;
		for(int i = 0; i < 7; i++) {
			// Mode offset from the root note
			steps[i] = step;
			
			// Notes in the scale
			int pitch = (step + root) % 12;
			contains[pitch] = true;
			
			// Position of each pitch value (C major scale) in this scale
			int pitchValue = (root + i) % 7;
			positions[i] = pitchValue;
			
			// Accidentals of each pitch value in this scale
			accidentals[i] = pitch - C_MAJOR_STEPS[pitchValue];
			
			step += C_MAJOR_STEPS[(mode + i) % 7];
		}
	}
	
	private String getName() {
		String rootName = new jm.music.data.Note(root, 1).getName();
		if (mode == 0) {
			return rootName + " Major";
		} else {
			return rootName.toLowerCase() + " minor";
		}
	}
	
	// ==================================================================================
	// Analysis
	// ==================================================================================
	
	public String toString() {
		return name;
	}
	
	public int getSignature() {
		return (mode == 0 ? MAJOR_ACCIDENTALS : MINOR_ACCIDENTALS)[root % 12];
	}
	
	public int getPitch(int pos) {
		return root + steps[pos % 7] + 12 * (pos / 7);
	}
	
	public Position getPosition(int pitch) {
		int root = this.root % 12;
		int octaves = pitch / 12;
		pitch = pitch % 12;
		if (pitch < root)
			pitch += 12;
		for (int i = 0; i < 7; i++) {
			int ipitch = (steps[i] + root);
			if (ipitch == pitch) {
				return new Position(i, 0, octaves);
			} else if (ipitch > pitch) {
				return new Position(i - 1, 1, octaves);
			}
		}
		return null;
	}
	
	public ArrayList<Note> convert(ArrayList<Note> original, Scale newScale) {
		ArrayList<Note> notes = new ArrayList<Note>();
		for (int n = 0; n < original.size(); n++) {
			Note note = original.get(n);
			if (note.pitch == null) {
				notes.add(note);
			} else {
				int pitch = note.pitch.getMIDIPitch(this);
				notes.add(new Note(newScale.getPosition(pitch), note.time));
			}
		}
		return notes;
	}

}
