package gen;

import jm.music.data.Part;

public class Progression {
	
	public final String string;
	public final Chord[] chords;
	public final int root;
	public final int[] scaleType;
	public double chordLen;
	
	public Progression(String sequenceStr, int root, int[] scaleType, double chordLen) {
		this.string = sequenceStr;
		this.root = root;
		this.scaleType = scaleType;
		this.chordLen = chordLen;
		String[] sequence = sequenceStr.split("-");
		chords = new Chord[sequence.length];
		for (int i = 0; i < sequence.length; i++) {
			chords[i] = new Chord(root, sequence[i], scaleType);
		}
	}
	
	public Progression changeScale(Scale scale) {
		return new Progression(this.string, scale.root, scale.pattern, chordLen);
	}
	
	public Part asPart(String name, int inst, int channel) {
		Part part = new Part("Harmony - Piano", inst, channel);
		for (int i = 0; i < chords.length; i++)
			part.addCPhrase(chords[i].asCPhrase(chordLen));
		return part;
	}
	
}
