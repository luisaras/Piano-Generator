package gen;

import jm.music.data.CPhrase;

public class Chord {

	// Chord types
	public static int[] fifth = new int[] { 0, 2, 4 };
	public static int[] seventh = new int[] { 0, 2, 4, 6 };
	public static int[] ninth = new int[] { 0, 2, 4, 6, 8 };
	public static int[] sus = new int[] { 0, 4 };
	public static int[] sus2 = new int[] { 0, 1, 4 };
	public static int[] sus4 = new int[] { 0, 3, 4 };
	
	public final int[] pitches;
	public final int root;
	
	public Chord(int scaleRoot, int[] scaleType) {
		this(scaleRoot, 0, scaleType, fifth);
	}
	
	public Chord(int scaleRoot, String chordRoot, int[] scaleType) {
		this(scaleRoot, toNoteNumber(chordRoot), scaleType, toChordType(chordRoot));
	}
	
	public Chord(int scaleRoot, int chordRoot, int[] scaleType, int[] chordType) {
		root = chordRoot;
		pitches = new int[chordType.length];
	 	for(int i = 0; i < pitches.length; i++) {
	 		int note = chordType[i] + chordRoot;
	 		int oct = note >= scaleType.length ? 12 : 0;
	 		pitches[i] = scaleType[note % scaleType.length] + scaleRoot + oct;
	 	}
	}
	
	public CPhrase asCPhrase(double rythm) {
		CPhrase cphrase = new CPhrase();
		cphrase.addChord(pitches, rythm);
		cphrase.setDuration(rythm);
		return cphrase;
	}
	
	public static int[] toChordType(String chordRoot) {
		String n = chordRoot.trim().toUpperCase();
		if (n.contains("sus")) {
			if (n.contains("2"))
				return sus2;
			else if (n.contains("4"))
				return sus4;
			else
				return sus;
		}
		else if (n.contains("7"))
			return seventh;
		else if (n.contains("9"))
			return ninth;
		else
			return fifth;
	}
	
	public static int toNoteNumber(String chordRoot) {
		String n = chordRoot.trim().toUpperCase();
		if (n.contains("VII"))
			return 6;
		else if (n.contains("VI"))
			return 5;
		else if (n.contains("IV"))
			return 3;
		else if (n.contains("V"))
			return 4;
		else if (n.contains("III"))
			return 2;
		else if (n.contains("II"))
			return 1;
		else
			return 0;
	}
	
	public static String toRomanNumber(int i) {
		switch(i) {
			case 0: return "I";
			case 1: return "II";
			case 2: return "III";
			case 3: return "IV";
			case 4: return "V";
			case 5: return "VI";
			case 6: return "VII";
			default:
				return null;
		}
	}
	
}
