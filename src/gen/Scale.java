package gen;

import jm.JMC;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

public class Scale {

	public int[] pattern;
	public int root;
	
	public Scale (int[] p, int r) {
		pattern = p;
		root = r;
	}
	
	public boolean includes(Score score) {
		for (Part part : score.getPartArray())
			for (Phrase phrase : part.getPhraseArray())
				for (Note note : phrase.getNoteArray())
					if (indexOf(note.getPitch()) < 0)
						return false;
		return true;
	}
	
	public int indexOf(int pitch) {
		pitch = pitch % 12;
		for (int i = 0; i < pattern.length; i++)
			if ((root + pattern[i]) % 12 == pitch)
				return i;
		return -1;
	}
	
	public static Scale deduce(Score score) {
		Scale s;
		s = scaleOf(JMC.MAJOR_SCALE, score);
		if (s != null) return s;
		s = scaleOf(JMC.MINOR_SCALE, score);
		if (s != null) return s;
		s = scaleOf(JMC.LYDIAN_SCALE, score);
		if (s != null) return s;
		return null;
	}
	
	public static Scale scaleOf(int[] pattern, Score score) {
		for (int i = 0; i < 12; i++) {
			Scale scale = new Scale(pattern, i);
			if (scale.includes(score)) {
				scale.root = score.getLowestPitch();
				return scale;
			}
		}
		return null;
	}
	
}
