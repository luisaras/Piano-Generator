package gen;

import java.util.Arrays;
import java.util.Random;

import jm.music.data.*;

public class Melody {
	
	// Seed
	public int noteCountSeed = 0;
	public int notePosSeed = 0;
	public int notePitchSeed = 0;
	
	// Parameters
	public int notesPerCrotchet = 4;
	public double displacementFreq = 0.2; 
	public double restFreq = 0.2;
	
	// ==================================================================================
	// Generation
	// ==================================================================================
	
	public Phrase asPhrase(Progression progression) {
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
					int[] pitches = chord.getPitches();
					if (notePitch.nextDouble() <= restFreq)
						phrase.addRest(new Rest(duration));
					else {
						int displacement = notePitch.nextDouble() < displacementFreq ? 
								notePitch.nextInt(2) * 2 - 1 : 0;
						int pitch = notePitch.nextInt(pitches.length);
						phrase.addNote(pitches[pitch] + displacement * 12, duration);
					}
				}
			}
		}
		return phrase;
	}
	
	public Part asPart(String name, int inst, int channel, Progression prog) {
		Part part = new Part(name);
		part.setTitle(name);
		part.add(asPhrase(prog));
		return part;
	}
	
}
