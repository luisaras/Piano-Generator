package gen;

import jm.music.data.CPhrase;
import jm.music.data.Phrase;

public class Chord {

	// Chord types
	public static final int[] fifth = new int[] { 0, 2, 4 };
	public static final int[] seventh = new int[] { 0, 2, 4, 6 };
	public static final int[] ninth = new int[] { 0, 2, 4, 6, 8 };
	public static final int[] sus = new int[] { 0, 4 };
	public static final int[] sus2 = new int[] { 0, 1, 4 };
	public static final int[] sus4 = new int[] { 0, 3, 4 };
	
	// Roman numbers
	public static final String[] chordNames = new String[] {
		"I", "II", "III", "IV", "V", "VI", "VII" };
	
	private String name;
	private Scale scale;
	private int root = 0;
	private String[] arpeggio;
	
	public Chord(String chordRoot, Scale scale) {
		this.name = chordRoot;
		this.scale = scale;
	}
	
	// ==================================================================================
	// Get / Set
	// ==================================================================================
	
	public String[] getArpeggio() {
		return arpeggio;
	}
	
	public void setArpeggio(String[] arpeggio) {
		this.arpeggio = arpeggio;
	}
	
	public int getRoot() {
		return root;
	}
	
	public void setRoot(int root) {
		this.root = root;
	}
	
	// ==================================================================================
	// Generation
	// ==================================================================================
	
	public int[] getPitches() {
		// Scale
		int root = this.root + scale.getRoot();
		int[] scaleType = scale.getPattern();
		// Chord
		int chordRoot = toNoteNumber(name);
		int[] chordType = toChordType(name);
		int[] pitches = new int[chordType.length];
	 	for(int i = 0; i < pitches.length; i++) {
	 		int note = chordType[i] + chordRoot;
	 		int oct = note >= scaleType.length ? 12 : 0;
	 		pitches[i] = scaleType[note % scaleType.length] + oct + root;
	 	}
	 	return pitches;
	}
	
	private Phrase getArpeggioPhrase(int[] pitches, String arp, double len) {
		Phrase phrase = new Phrase();
		int count = arp.length();
		int pitch = 0;
		int duration = 0;
		for(int j = 0; j < count; j++) {
			char pos = arp.charAt(j);
			if (pos == '_') {
				duration += 1;
			} else {
				if (duration > 0)
					phrase.addNote(pitches[pitch], duration * len / count);
				pitch = pos - '0';
				duration = 1;
			}
		}
		if (duration > 0) {
			phrase.addNote(pitches[pitch], duration * len / count);
		}
		return phrase;
	}
	
	public CPhrase asCPhrase(double rythm) {
		CPhrase cphrase = new CPhrase();
		int[] pitches = getPitches();
		if (arpeggio == null) {
			cphrase.addChord(pitches, rythm);
			cphrase.setDuration(rythm);
			return cphrase;
		}
		for(int i = 0; i < arpeggio.length; i++) {
			Phrase phrase = getArpeggioPhrase(pitches, arpeggio[i], rythm);
			phrase.setAppend(false);
			cphrase.addPhrase(phrase);
		}
		return cphrase;
	}
	
	// ==================================================================================
	// Conversion
	// ==================================================================================
	
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
		return chordNames[i];
	}
	
}
