package gen;

import java.util.Random;

import jm.JMC;
import jm.music.data.*;
import jm.util.Read;
import jm.util.Write;

public class Generator implements JMC {
	
	// ==================================================================================
	// Generation Types
	// ==================================================================================
	
	// Generate the static hard-coded song.
	public Generator(String name) {
		Scale scale = new Scale(0, c4);
		int chordLen = 4;
		double tempo = 90;
		Progression progression = new Progression("Isus2-vi-IV7-V", scale, chordLen);
		String[] arpeggio = new String[] { "01010101", "02020202" };
		for (int i = 0; i < progression.chords.length; i++)
			progression.chords[i].setArpeggio(arpeggio);
		Write.midi(randomMelody(name, progression, tempo), name + ".mid");
	}
	
	// Generate a semi-random song from a template.
	public Generator(String in, String out, int chordLen) {
		Score score = new Score(in);
		Read.midi(score, in + ".mid");
		double tempo = score.getTempo();
		try {
			Progression progression = Analysis.deduceProgression(score.getPart(0), null, chordLen);
			Write.midi(randomMelody(out, progression, tempo), out + ".mid");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Generate a random song with a given length (in semibreves).
	public Generator(String name, int len) {
		Random rand = new Random(25);
		// Time and rythm.
		double tempo = rand.nextInt(4) * 15 + 60;
		int numChords = rand.nextInt(2) == 1 ? 4 : 8;
		int chordLen = (len * 4) / numChords;
		// Chords.
		String str = Chord.toRomanNumber(rand.nextInt(7));
		for (int i = 0; i < numChords - 1; i++)
			str += "-" + Chord.toRomanNumber(rand.nextInt(7));
		// Scale.
		int pattern = rand.nextInt(Scale.patterns.length);
		int root = rand.nextInt(48) + 48;
		Progression prog = new Progression(str, new Scale(pattern, root), chordLen);
		Write.midi(randomMelody(name, prog, tempo), name + ".mid");
	}
	
	// ==================================================================================
	// Melody
	// ==================================================================================
	
	// Creates a random melody for the given chord progression.
	public Score randomMelody(String name, Progression prog, double tempo) {
		Score score = new Score(name);
		Melody melody = new Melody(prog);
		System.out.println("Created song: " + prog.scale.toString() + " " + prog.toString());
		score.add(prog.asPart("Harmony - Piano", 0, 0));
		score.add(melody.asPart("Melody - Piano", 0, 1));
		score.setTempo(tempo);
		return score;
	}
	
}
