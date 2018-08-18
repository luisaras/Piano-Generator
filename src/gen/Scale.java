package gen;

import gen.Melody.Note;

import java.util.ArrayList;

public class Scale {

	private final static int[] C_MAJOR_STEPS = new int[] { 2, 2, 1, 2, 2, 2, 1 };
	private final static int[] MAJOR_SIGNATURES = new int[] { 0, -5, 2, -3, 4, -1, 6, 1, -4, 3, -2, 5 };
	private final static String[] MODE_NAMES = new String[] {
		"Ionian (major)",
		"Dorian",
		"Phrygian",
		"Lydian",
		"Mixolydian",
		"Aeolian (minor)",
		"Locrian"
	};
	
	public static class Position {
		public int function = 0;
		public int accidental = 0; // Out of scale
		public int octaves = 0;
		public Position(int f, int a, int o) { function = f; accidental = a; octaves = o; }
		public int getMIDIPitch(Scale scale) {
			return octaves * 12 + scale.steps[function] + accidental + scale.root % 12;
		}
		public String toString() {
			String acc = accidental < 0 ? "b" : accidental > 0 ? "#" : "";
			return octaves + "oct " + function + acc;
		}
	}
	
	public final int mode;
	public final int root;
	public final int signature;
	public final String name;
	
	public final boolean[] contains = new boolean[12];
	public final int[] steps = new int[7];
	public final int[] accidentals = new int[7];
	public final int[] positions = new int[7];
	
	// ==================================================================================
	// Initialization
	// ==================================================================================
	
	public Scale(int root, int mode, int sig) {
		this.root = root;
		this.mode = mode;
		this.signature = sig;
		
		String rootName = new jm.music.data.Note(root, 1).getName();
		if (mode == 5) {
			rootName = rootName.toLowerCase();
		}
		this.name = rootName + " " + MODE_NAMES[mode];
		
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

	public static int getRoot(int sig, int mode) {
		if (sig >= 7) sig = 12 - sig;
		if (sig <= -6) sig = 12 + sig;
		for (int i = 0; i < 12; i++) {
			int isig = getSignature(i, mode);
			if (isig == sig) {
				return i;
			}
		}
		return -1;
	}
	
	public static int getSignature(int root, int mode) {
		int sig = MAJOR_SIGNATURES[root % 12];
		if (mode <= 2)
			sig -= mode * 2;
		else
			sig += 1 - (mode - 3) * 2;
		while (sig >= 7) sig = 12 - sig;
		while (sig <= -6) sig = 12 + sig;
		return sig;
	}
	
	// ==================================================================================
	// Analysis
	// ==================================================================================
	
	public String toString() {
		return name;
	}

	public int getPitch(int pos) {
		return root + steps[pos % 7] + 12 * (pos / 7);
	}
	
	public Position getPosition(int pitch) {
		int root = this.root % 12;
		int octaves = pitch / 12;
		pitch = pitch % 12;
		if (pitch < root) {
			pitch += 12;
			octaves --;
		}
		for (int i = 0; i < 7; i++) {
			int ipitch = (steps[i] + root);
			if (ipitch == pitch) {
				return new Position(i, 0, octaves);
			} else if (ipitch > pitch) {
				System.out.println("ACCIDENTAL " + (pitch + octaves * 12) + " " + this.toString());
				return new Position(i - 1, 1, octaves);
			}
		}
		return new Position(0, pitch, octaves);
	}
	
	public void convert(ArrayList<Note> notes, Scale newScale) {
		for (int n = 0; n < notes.size(); n++) {
			Note note = notes.get(n);
			if (note.pitch != null) {
				//int pitch = note.pitch.getMIDIPitch(this);
				//note.pitch = newScale.getPosition(pitch);
			}
		}
	}

}
