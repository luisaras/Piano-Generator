package gen;

import jm.JMC;
import jm.music.data.*;

public class Scale {

	public final static int[][] patterns = new int[][] { 
		JMC.MAJOR_SCALE, 
		JMC.MINOR_SCALE, 
		JMC.LYDIAN_SCALE, 
		JMC.DORIAN_SCALE };
	
	private final static String[] patternNames = new String[] {
		"Major",
		"Minor",
		"Lydian",
		"Dorian" };
	
	private int patternID;
	private int root;
	
	public Scale (int pattern, int root) {
		this.patternID = pattern;
		this.root = root;
	}
	
	// ==================================================================================
	// Get / Set
	// ==================================================================================
	
	public int[] getPattern() {
		return patterns[patternID];
	}
	
	public void setPattern(int id) {
		patternID = id;
	}
	
	public int getRoot() {
		return root;
	}
	
	public void setRoot(int pitch) {
		root = pitch;
	}
	
	public String toString() {
		Note n = new Note(root, 1);
		return patternNames[patternID] + " " + n.getName();
	}
	
	// ==================================================================================
	// Analysis
	// ==================================================================================
	
	public boolean includes(Part part) {
		for (Phrase phrase : part.getPhraseArray())
			for (Note note : phrase.getNoteArray())
				if (note.getPitch() >= 0 && indexOf(note.getPitch()) < 0)
					return false;
		return true;
	}
	
	public int indexOf(int pitch) {
		pitch = pitch % 12;
		int[] pattern = getPattern();
		for (int i = 0; i < pattern.length; i++)
			if ((root + pattern[i]) % 12 == pitch)
				return i;
		return -1;
	}
	
}
