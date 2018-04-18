package gen;

import jm.JMC;
import jm.music.data.Score;
import jm.util.Write;

public class Generator implements JMC {
	
	public Generator(String name) {
		Score score = new Score(name);
		Progression progresson = new Progression("Isus2-vi-IV7-V", d5, MAJOR_SCALE);
		score.add(progresson.asPart("Piano", 0, 0, 4));
		score.setTempo(90);
		Write.midi(score, name + ".mid");
	}
	
}
