package gen;

import jm.music.data.Score;
import jm.util.Read;
import jm.util.Write;

public class Generator {
	
	// Generate the static hard-coded song.
	public Generator() {
		Score score = testRandom();
		if (score != null)
			Write.midi(score, score.getTitle() + ".mid");
	}
	
	// ==================================================================================
	// Generation Types
	// ==================================================================================
	
	public Score testRandom() {
		return new Composition(4).asScore("Random Test");
	}
	
	public Score testTemplate() {
		try {
			Score template = new Score();
			Read.midi(template, "Template");
			return new Composition(template, 4).asScore("Template Test");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Score testDefault() {
		return new Composition().asScore("Default Test");
	}
	
}
