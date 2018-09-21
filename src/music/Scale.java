package music;

import java.util.ArrayList;

public class Scale {

	// The number of semitones from the step i to the i + 1 in the Ionian mode.
	private final static int[] C_MAJOR_STEPS = new int[] { 
		2, 2, 1, 2, 2, 2, 1 
	};
	
	// The number of sharps (positive) or flats (negative) of each pitch class in Ionian mode.
	private final static int[] MAJOR_SIGNATURES = new int[] { 
		0, -5, 2, -3, 4, -1, 6, 1, -4, 3, -2, 5 
	};
	
	// The names of each mode from 0 to 6.
	private final static String[] MODE_NAMES = new String[] {
		"Ionian (major)",
		"Dorian",
		"Phrygian",
		"Lydian",
		"Mixolydian",
		"Aeolian (minor)",
		"Locrian"
	};
	
	// Greek mode from 0 to 6.
	public int mode;
	
	// Root MIDI pitch.
	public int root;
	
	// True element i means that pitch class i (from 0 to 11) is on this scale.
	public final boolean[] contains = new boolean[12];
	
	// Element i is the number of semitones between the root and i-th note.
	public final int[] steps = new int[7];
	
	// Element i is the position of pitch value (C to B) in this scale.
	public final int[] positions = new int[7];
	
	// Element i is the accidental of the pitch value of the i-th note.
	public final int[] accidentals = new int[7];
	
	// ==================================================================================
	// Initialization
	// ==================================================================================
	
	/** Creates new scale from the given root pitch, mode and signature.
	 * @param root MIDI pitch of the root note.
	 * @param mode Mode from 0 (major) to 6 (Locrian).
	 * @param sig Scale's signature (number of sharps or flats).
	 */
	public Scale(int root, int mode) {
		this.root = root;
		this.mode = mode;
		
		for (int i = 0; i < 12; i++)
			this.contains[i] = false;
		
		if (root < 0)
			root = 12 - (-root) % 12;
		int step = 0;
		for(int i = 0; i < 7; i++) {
			// Mode offset from the root note
			steps[i] = step;
			
			// Notes in the scale
			int pitch = (step + root) % 12;
			contains[pitch] = true;
			
			// Position of each pitch value (C major scale) in this scale
			int pitchValue = getPitchValue(i);
			positions[pitchValue] = i;
			
			// Accidentals of each pitch value in this scale
			accidentals[i] = pitch - C_MAJOR_STEPS[pitchValue];
			
			step += C_MAJOR_STEPS[(mode + i) % 7];
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Scale clone() {
		return new Scale(root, mode);
	}
	
	public void setRoot(int root) {
		this.root = root;
		if (root < 0)
			root = 12 - (-root) % 12;
		int step = 0;
		for(int i = 0; i < 7; i++) {
			int pitch = (step + root) % 12;
			contains[pitch] = true;
			step += C_MAJOR_STEPS[(mode + i) % 7];
		}
	}
	
	// ==================================================================================
	// Find Scales
	// ==================================================================================

	/** Finds the root pitch of a scale with the given signature and mode.
	 * @param sig Scale's signature (number of sharps or flats).
	 * @param mode Mode from 0 (major) to 6 (Locrian).
	 * @return MIDI pitch.
	 */
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
	
	/** Finds the scale's signature given the root pitch and mode.
	 * @param root MIDI pitch of the root note.
	 * @param mode Mode from 0 (major) to 6 (Locrian).
	 * @return Scale's signature (number of sharps or flats).
	 */
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
	// Notes
	// ==================================================================================
	
	/** Gets the MIDI pitch of the note in the given position.
	 * @param pos Note's position relative to the root.
	 * @return MIDI pitch.
	 */
	public int getPitch(int pos) {
		return root + steps[pos % 7] + 12 * (pos / 7);
	}
	
	public int getPitchValue(int pos) {
		int root = this.root % 12;
		if (root < 0)
			root = 12 - (-root) % 12;
		return (root + pos) % 7;
	}
	
	/** Finds the note position of a given MIDI pitch.
	 * @param pitch MIDI pitch.
	 * @return Note's position relative to the root.
	 */
	public Note getPosition(int pitch) {
		int root = this.root % 12;
		if (root < 0)
			root = 12 - (-root) % 12;
		int octaves = pitch / 12 - this.root / 12;
		pitch = pitch % 12;
		if (pitch < root) {
			pitch += 12;
			octaves --;
		}
		for (int i = 0; i < 7; i++) {
			int ipitch = (steps[i] + root);
			if (ipitch == pitch) {
				return new Note(i, 0, octaves);
			} else if (ipitch > pitch) {
				return new Note(i - 1, 1, octaves);
			}
		}
		return new Note(0, pitch, octaves);
	}
	
	/** Changes the reference scale of the given notes to another, updating their positions.
	 * @param notes Notes to be updated.
	 * @param newScale The new reference scale of the notes.
	 */
	public void convert(ArrayList<NotePlay> notes, Scale newScale) {
		for (int n = 0; n < notes.size(); n++) {
			NotePlay note = notes.get(n);
			if (note.note != null) {
				int pitch = note.note.getMIDIPitch(this);
				note.note = newScale.getPosition(pitch);
			}
		}
	}
	
	// ==================================================================================
	// Scale Info
	// ==================================================================================
	
	/** Checks if the scale is major.
	 * @return Either the scale is of major type or not.
	 */
	public boolean isMajor() {
		return mode == 0 || mode == 3 || mode == 4;
	}
	
	/** Checks if the scale is minor.
	 * @return Either the scale is of minor type or not.
	 */
	public boolean isMinor() {
		return !isMajor();
	}
	
	/** Checks if the scale is diminished.
	 * @return Either the scale is of diminished type or not.
	 */
	public boolean isDiminished() {
		return mode == 6;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String rootName = new jm.music.data.Note(root, 1).getName();
		if (isMinor()) {
			rootName = rootName.toLowerCase();
		}
		return rootName + (root / 12) + " " + MODE_NAMES[mode];
	}
	
}
