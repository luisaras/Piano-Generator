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
	
	private static final double[] pitchWeights = new double[] { 
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1
	};
	
	public static final double[][] weights = new double[][] { 
		rhythmWeights, pitchWeights
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
		
		// ==================================================================================
		// Pitch
		// ==================================================================================
		
		double[] pitch = new double[pitchWeights.length];
		
		{
			int[] pitchCount = mergedTracks.getPitches(piece.scale);
			int[] classCount = mergedTracks.getPitchClasses(piece.scale);
			
			// Most common pitch
			int firstPitch = 0, secondPitch = 1;
			if (pitchCount[1] > pitchCount[0])
				secondPitch = 0; firstPitch = 1;
			for (int i = 0; i < pitchCount.length; i++) {
				if (pitchCount[i] > pitchCount[firstPitch]) {
					secondPitch = firstPitch;
					firstPitch = 0;
				}
			}
			pitch[0] = pitchCount[firstPitch] / mergedTracks.size();
			
			// Most common pitch class
			int firstClass = 0, secondClass = 1;
			if (classCount[1] > classCount[0])
				secondClass = 0; firstClass = 1;
			for (int i = 0; i < classCount.length; i++) {
				if (classCount[i] > classCount[firstPitch]) {
					secondClass = firstClass;
					firstClass = 0;
				}
			}
			pitch[1] = classCount[firstClass] / mergedTracks.size();
			
			// Relative strength of top pitches
			pitch[2] = pitchCount[secondPitch] / pitchCount[firstPitch];
			
			// Relative strength of top pitch classes
			pitch[3] = classCount[secondClass] / classCount[firstClass];
			
			// Interval between top pitches
			pitch[4] = Math.abs(firstPitch - secondPitch);
			
			// Interval between top pitch classes
			pitch[5] = Math.abs(firstClass - secondClass);
			
			// Number of common pitches
			double minimum = mergedTracks.size() * 0.09;
			for (int i = 0; i < pitchCount.length; i++)
				if (pitchCount[i] >= minimum)
					pitch[6]++;
			
			// Pitch variety
			for (int i = 0; i < pitchCount.length; i++)
				if (pitchCount[i] > 0)
					pitch[7]++;
			
			// Pitch class variety
			for (int i = 0; i < classCount.length; i++)
				if (classCount[i] > 0)
					pitch[8]++;
			
			// Range
			int lowest = 0, highest = 127;
			while (pitchCount[lowest] == 0)
				lowest++;
			while (pitchCount[highest] == 0)
				highest--;
			pitch[9] = highest - lowest;
			
			// Primary register
			for (NotePlay np : mergedTracks)
				pitch[10] += np.note.getMIDIPitch(piece.scale);
			pitch[10] /= mergedTracks.size();
			
			// Importance of bass register
			for (double count : pitchCount) {
				if (count <= 54)
					pitch[11]++;
			}
			pitch[11] /= mergedTracks.size();
			
			// Importance of middle register
			for (double count : pitchCount) {
				if (54 <= count && count <= 72)
					pitch[12]++;
			}
			pitch[12] /= mergedTracks.size();
			
			// Importance of high register
			for (double count : pitchCount) {
				if (73 <= count)
					pitch[13]++;
			}
			pitch[13] /= mergedTracks.size();
			
		}
		
		return new double[][] { rhythm, pitch };
	}
	
}
