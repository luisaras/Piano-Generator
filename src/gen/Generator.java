package gen;

import java.util.Random;

import jm.JMC;
import jm.music.data.*;
import jm.util.Read;
import jm.util.Write;

public class Generator implements JMC {
	
	private final String fileName;
	
	public Generator(String name) {
		fileName = name;
		Score score = staticSong();
		Write.midi(score, name + ".mid");
	}
	
	public Generator(String name, int len) {
		fileName = name;
		Score score = randomSong(len);
		Write.midi(score, name + ".mid");
	}
	
	public Generator(String in, String out) {
		fileName = out;
		try {
			Score score = templateSong(in);
			Write.midi(score, out + ".mid");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// ==================================================================================
	// Score
	// ==================================================================================
	
	public Score templateSong(String in) throws Exception {
		Score score = new Score(in);
		Read.midi(score, in + ".mid");
		int chordLen = 4;
		double tempo = score.getTempo();
		Progression progression = Analysis.deduceProgression(score.getPart(0), null, chordLen);
		return randomMelody(progression, tempo);
	}
	
	public Score staticSong() {
		Scale scale = new Scale(0, c5);
		int chordLen = 4;
		double tempo = 90;
		Progression progression = new Progression("Isus2-vi-IV7-V", scale, chordLen);
		return randomMelody(progression, tempo);
	}
	
	public Score randomSong(int len) {
		Random rand = new Random(31);
		String progressionStr = Chord.toRomanNumber(rand.nextInt(7));
		int numChords = rand.nextInt(2) == 1 ? 4 : 8;
		int chordLen = (len * 4) / numChords;
		for (int i = 0; i < numChords - 1; i++)
			progressionStr += "-" + Chord.toRomanNumber(rand.nextInt(7));
		System.out.println(progressionStr);
		int pattern = rand.nextInt(Scale.patterns.length);
		int root = rand.nextInt(48) + 48;
		Progression progression = new Progression(progressionStr, new Scale(pattern, root), chordLen);
		double tempo = rand.nextInt(4) * 15 + 60;
		return randomMelody(progression, tempo);
	}
	
	// ==================================================================================
	// Melody
	// ==================================================================================
	
	public Score randomMelody(Progression progression, double tempo) {
		Score score = new Score(fileName);
		Melody melody = new Melody(progression);
		score.add(progression.asPart("Harmony - Piano", 0, 0));
		score.add(melody.asPart("Melody - Piano", 0, 1));
		score.setTempo(tempo);
		return score;
	}
}
