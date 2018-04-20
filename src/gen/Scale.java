package gen;

import jm.music.data.*;

public class Scale {

	public int[] pattern;
	public int root;
	
	public Scale (int[] p, int r) {
		pattern = p;
		root = r;
	}
	
	public boolean includes(Part part) {
		for (Phrase phrase : part.getPhraseArray())
			for (Note note : phrase.getNoteArray())
				if (note.getPitch() >= 0 && indexOf(note.getPitch()) < 0)
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
	
}
