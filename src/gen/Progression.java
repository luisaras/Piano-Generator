package gen;

import jm.music.data.Part;

public class Progression {
	
	/*
	 * C
	 * C#
	 * D
	 * D#
	 * E
	 * F
	 * F#
	 * G
	 * G#
	 * A
	 * A#
	 * B
	 * 
	 */
	
	public final Chord[] chords;
	
	public Progression(Chord[] chords) {
		this.chords = chords;
	}
	
	public Progression(String sequenceStr, int root, int[] scaleType) {
		String[] sequence = sequenceStr.split("-");
		chords = new Chord[sequence.length];
		for (int i = 0; i < sequence.length; i++) {
			chords[i] = new Chord(root, sequence[i], scaleType);
		}
	}
	
	public Part asPart(String name, int inst, int channel, double rythm) {
		Part part = new Part("Harmony - Piano", inst, channel);
		for (int i = 0; i < chords.length; i++)
			part.addCPhrase(chords[i].asCPhrase(rythm));
		return part;
	}
	
}
