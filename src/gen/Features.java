package gen;

import music.Composition;
import music.Melody;
import music.NotePlay;

public class Features {
	
	// ==================================================================================
	// Weights
	// ==================================================================================
	
	private static final double[] rhythmWeights = new double[] { 
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1
	};
	
	private static final double[] melodyWeights = new double[] {
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1
	};
	
	private static final double[] chordsWeights = new double[] {
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1
	};
	
	private static final double[] arpeggioWeights = new double[] {
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1
	};
	
	public static final double[][] weights = new double[][] { 
		rhythmWeights, melodyWeights, chordsWeights, arpeggioWeights
	};
	
	public static double[][] calculate(Composition piece) {
		
		Melody mergedTracks = piece.mergeTracks();
		
		// ==================================================================================
		// Rhythm
		// ==================================================================================
		
		double[] rhythm = new double[rhythmWeights.length];
		
		{
			double seconds = piece.getMinutes() / 60;
			int beats = piece.numerator * piece.length;
			
			Melody[] measures = new Melody[piece.length];
			for (int m = 0; m < piece.length; m++) {
				int start = m * piece.numerator, end = (m + 1) * piece.numerator;
				measures[m] = mergedTracks.cut(start, end);
			}
			
			// Note density
			rhythm[0] = mergedTracks.size() / seconds;
			
			// Note density variation
			for (int m = 0; m < piece.length; m++) {
				double d = measures[m].size() * piece.length / seconds;
				rhythm[1] += d * d;
			}
			rhythm[1] /= piece.length;
			
			// Note duration mean, maximum and minimum
			rhythm[3] = 100;
			for (NotePlay np : mergedTracks) {
				rhythm[2] += np.duration;
				rhythm[3] = Math.min(np.duration, rhythm[3]);
				rhythm[4] = Math.max(np.duration, rhythm[4]);
				
			}
			rhythm[2] /= mergedTracks.size();
			
			// Note duration variation
			for (NotePlay np : mergedTracks) {
				double d = rhythm[2] - np.duration;
				rhythm[5] += d * d;
			}
			rhythm[5] /= mergedTracks.size();
			
			// Staccato incidence
			double staccatoLength = 0.1 * piece.bpm / 60;
			for (NotePlay np : mergedTracks) {
				if (np.duration < staccatoLength)
					rhythm[6]++;
			}
			rhythm[6] /= mergedTracks.size();
			
			// Attack mean
			double[] attacks = mergedTracks.getAttacks();
			for (double attack : attacks) {
				rhythm[7] += attack;
			}
			rhythm[7] /= attacks.length;
			
			// Attack variation
			for (double attack : attacks) {
				double d = rhythm[7] - attack;
				rhythm[8] += d * d;
			}
			rhythm[8] /= attacks.length;
			
			// Complete rest
			NotePlay[] rests = mergedTracks.getRests();
			for (NotePlay np : rests) {
				rhythm[9] += np.duration;
				rhythm[10] = Math.max(np.duration, rhythm[10]);
			}
			rhythm[9] /= beats;

		}
		
		return new double[][] { rhythm };
	}
	
}
