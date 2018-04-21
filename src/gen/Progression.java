package gen;

import jm.music.data.Part;

public class Progression {
	
	public final String name;
	public final Chord[] chords;
	public final Scale scale;
	public int chordLen;
	
	public Progression(String sequenceStr, Scale scale, int chordLen) {
		this.name = sequenceStr;
		this.scale = scale;
		this.chordLen = chordLen;
		String[] sequence = sequenceStr.split("-");
		chords = new Chord[sequence.length];
		for (int i = 0; i < sequence.length; i++) {
			chords[i] = new Chord(sequence[i], scale);
		}
	}
	
	public Progression changeScale(Scale scale) {
		return new Progression(this.name, scale, chordLen);
	}
	
	public String toString() {
		return name;
	}
	
	// ==================================================================================
	// Generation
	// ==================================================================================
	
	public Part asPart(String name, int inst, int channel) {
		Part part = new Part(name, inst, channel);
		for (int i = 0; i < chords.length; i++)
			part.addCPhrase(chords[i].asCPhrase(chordLen));
		return part;
	}
	
}
