package gen;

import music.Composition;
import music.NotePlay;

public class Features {

	public static final double[] weights = new double[] { 1, 1, 1, 1 };
	
	public static double[] calculate(Composition piece) {
		double[] features = new double[weights.length];
		
		double duration = piece.getDuration();
		double beatToSecond = 60 / piece.bpm;
		
		// ==================================================================================
		// Rhythm
		// ==================================================================================
		
		double noteDuration = 0;
		int noteCount = 0;
		int[] mNoteCounts = new int[piece.duration];
		for (int i = 0; i < piece.melody.notes.size(); i++) {
			NotePlay play = piece.melody.notes.get(i);
			if (play.note != null) {
				noteCount ++;
				mNoteCounts[(int)(play.time / piece.numerator)]++;
				double end = i == piece.melody.notes.size() - 1 ?
						piece.duration * piece.numerator :
						piece.melody.notes.get(i + 1).time;
				noteDuration += (end - play.time);
			}
		}
		
		// Notes per second
		features[0] = noteCount / duration;
		
		// Notes per second variation (per measure)
		for (int i = 0; i < mNoteCounts.length; i++) {
			double d = features[0] - mNoteCounts[i] / duration;
			features[1] += d * d;
		}
		
		// Average note duration
		features[2] = noteDuration / noteCount * beatToSecond;
		
		double lastNote = -1;
		int attackCount = 0;
		double attacks = 0;
		for(int i = piece.melody.notes.size() - 1; i >= 0; i--) {
			NotePlay play = piece.melody.notes.get(i);
			if (play.note != null) {
				if (lastNote >= 0) {
					attackCount++;
					attacks += lastNote - play.time;
				}
				lastNote = play.time;
			}
		}
		
		// Average attack distance
		features[3] = attacks / attackCount * beatToSecond;
		
		return features;
	}
	
}
