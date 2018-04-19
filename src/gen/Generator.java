package gen;

import jm.JMC;
import jm.music.data.*;
import jm.util.Read;
import jm.util.Write;

public class Generator implements JMC {
	
	public Generator(String in, String out) {
		Score score = new Score(in);
		Read.midi(score, in + ".mid");
		double tempo = score.getTempo();
		try {
			Progression progression = ProgressionAnalysis.deduceProgression(score);
			//Progression progression = new Progression("Isus2-vi-IV7-V", c5, MAJOR_SCALE);
			score = new Score(out);
			score.add(progression.asPart("Piano", 0, 0, 1));
			score.setTempo(tempo);
			Write.midi(score, out + ".mid");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
