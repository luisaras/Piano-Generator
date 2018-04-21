package gen;

import java.util.Arrays;
import java.util.Random;

import jm.music.data.*;

public class Melody {
	
	// Parameters
	public int noteCountSeed = 0;
	public int notePosSeed = 0;
	public int notePitchSeed = 0;
	public int notesPerCrotchet = 4;
	public int displacement = 0; 
	public double restFrequence = 0.25;
	
	public Progression progression;
	
	public Melody (Progression progression) {
		this.progression = progression;
	}
	
	public Phrase generate(Progression progression) {
		Random noteCount = new Random(noteCountSeed);
		Random notePos = new Random(notePosSeed);
		Random notePitch = new Random(notePitchSeed);
		
		Phrase phrase = new Phrase();
		int maxNotes = notesPerCrotchet * progression.chordLen;
		for(int i = 0; i < progression.chords.length; i++) {
			// Note distribution
			int[] pos = new int[noteCount.nextInt(maxNotes) + 2];
			for(int j = 1; j < pos.length - 1; j++)
				pos[j] = notePos.nextInt(maxNotes);
			pos[0] = 0;
			pos[pos.length - 1] = maxNotes;
			Arrays.sort(pos);
			// Note pitches
			Chord chord = progression.chords[i];
			for (int j = 0; j < pos.length - 1; j++) {
				double duration = (pos[j + 1] - pos[j]) * 1.0 * progression.chordLen / maxNotes;
				if (duration > 0) {
					if (notePitch.nextDouble() <= restFrequence)
						phrase.addRest(new Rest(duration));
					else {
						int pitch = notePitch.nextInt(chord.pitches.length);
						phrase.addNote(chord.pitches[pitch] + displacement, duration);
					}
				}
			}
		}
		return phrase;
	}
	
	public Part asPart(String name, int inst, int channel) {
		Part part = new Part(name, inst, channel);
		part.add(generate(progression));
		return part;
	}
	
}
