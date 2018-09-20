package gen;

import java.util.HashMap;
import java.util.Map;

import music.Composition;
import music.Melody;
import music.Note;
import music.NotePlay;
import music.Scale;

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
	
	private static final double[] intervalWeights = new double[] {
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1
	};
	
	private static final double[] noteWeights = new double[] { 
		1, 1, 1, 1, 1, 1, 1
	};
	
	public static final double[][] weights = new double[][] { 
		rhythmWeights, pitchWeights, intervalWeights, intervalWeights, intervalWeights,
			noteWeights, noteWeights, noteWeights
	};
	
	// ==================================================================================
	// Features
	// ==================================================================================
	
	public static double[][] calculate(Composition piece) {
		
		Melody mergedTracks = piece.mergeTracks();
		Melody chords = piece.harmony.asMelody();
		Melody arpeggio = piece.harmony.arpeggio.asMelody(piece.scale, piece.harmony.get(0));
		Note[] chordIntervals = piece.harmony.arpeggio.getIntervals(piece.scale);
		
		double[] rhythm = rhythmFeatures(piece, mergedTracks);
		double[] pitch = pitchFeatures(piece, mergedTracks);
		
		double[] melodyi = intervalFeatures(piece.scale, piece.melody.getIntervals());
		double[] harmonyi = intervalFeatures(piece.scale, chords.getIntervals());
		double[] arpeggioi = intervalFeatures(piece.scale, chordIntervals);
		
		double[] melodyn = noteFeatures(piece.scale, piece.melody);
		double[] harmonyn = noteFeatures(piece.scale, chords);
		double[] arpeggion = noteFeatures(piece.scale, arpeggio);

		return new double[][] { rhythm, pitch, 
				melodyi, harmonyi, arpeggioi,
				melodyn, harmonyn, arpeggion };
	}
	
	// ==================================================================================
	// Rhythm
	// ==================================================================================
	
	private static double[] rhythmFeatures(Composition piece, Melody notes) {
		double[] features = new double[rhythmWeights.length];
		
		double seconds = piece.getMinutes() * 60;
		int beats = piece.numerator * piece.length;
		
		Melody[] measures = new Melody[piece.length];
		for (int m = 0; m < piece.length; m++) {
			int start = m * piece.numerator, end = (m + 1) * piece.numerator;
			measures[m] = notes.cut(start, end);
		}
		
		// Note density
		features[0] = notes.size() / seconds;
		
		// Note density variation
		for (int m = 0; m < piece.length; m++) {
			double d = measures[m].size() * piece.length / seconds - features[0];
			features[1] += d * d;
		}
		features[1] /= piece.length;
		
		// Note duration mean, maximum and minimum
		features[3] = 100;
		for (NotePlay np : notes) {
			features[2] += np.duration;
			features[3] = Math.min(np.duration, features[3]);
			features[4] = Math.max(np.duration, features[4]);
		}
		features[2] /= notes.size();
		
		// Note duration variation
		for (NotePlay np : notes) {
			double d = features[2] - np.duration;
			features[5] += d * d;
		}
		features[5] /= notes.size();
		
		// Staccato incidence
		double staccatoLength = 0.1 * piece.bpm / 60;
		for (NotePlay np : notes) {
			if (np.duration < staccatoLength)
				features[6]++;
		}
		features[6] /= notes.size();
		
		// Attack mean
		double[] attacks = notes.getAttacks();
		for (double attack : attacks) {
			features[7] += attack;
		}
		features[7] /= attacks.length;
		
		// Attack variation
		for (double attack : attacks) {
			double d = features[7] - attack;
			features[8] += d * d;
		}
		features[8] /= attacks.length;
		
		// Complete rest
		NotePlay[] rests = notes.getRests();
		for (NotePlay np : rests) {
			features[9] += np.duration;
			features[10] = Math.max(np.duration, features[10]);
		}
		features[9] /= beats;

		return features;
	}
	
	// ==================================================================================
	// Pitch
	// ==================================================================================
	
	private static double[] pitchFeatures(Composition piece, Melody notes) {
		double[] features = new double[pitchWeights.length];
		
		int[] pitchCount = notes.getPitches(piece.scale);
		int[] classCount = notes.getPitchClasses(piece.scale);
		
		// Most common pitch
		int firstPitch = 0, secondPitch = 0;
		for (int i = 0; i < pitchCount.length; i++) {
			if (pitchCount[i] >= pitchCount[firstPitch]) {
				secondPitch = firstPitch;
				firstPitch = i;
			} else if (pitchCount[i] > pitchCount[secondPitch])
				secondPitch = i;
		}
		features[0] = (firstPitch - piece.scale.root) % 12;
		
		// Most common pitch class
		int firstClass = 0, secondClass = 0;
		for (int i = 0; i < classCount.length; i++) {
			if (classCount[i] >= classCount[firstClass]) {
				secondClass = firstClass;
				firstClass = i;
			} else if (classCount[i] > classCount[secondClass])
				secondClass = i;
		}
		features[1] = firstClass - piece.scale.root % 12;
		
		// Relative strength of top pitches
		features[2] = 1.0 * pitchCount[secondPitch] / pitchCount[firstPitch];
		
		// Relative strength of top pitch classes
		features[3] = 1.0 * classCount[secondClass] / classCount[firstClass];
		
		// Interval between top pitches
		features[4] = Math.abs(firstPitch - secondPitch);
		
		// Interval between top pitch classes
		features[5] = Math.abs(firstClass - secondClass);
		
		// Number of common pitches
		double minimum = notes.size() * 0.09;
		for (int i = 0; i < pitchCount.length; i++)
			if (pitchCount[i] >= minimum)
				features[6]++;
		
		// Pitch variety
		for (int i = 0; i < pitchCount.length; i++)
			if (pitchCount[i] > 0)
				features[7]++;
		
		// Pitch class variety
		for (int i = 0; i < classCount.length; i++)
			if (classCount[i] > 0)
				features[8]++;
		
		// Range
		if (notes.size() == 0) {
			features[9] = 0;
		} else {
			int lowest = 0, highest = 127;
			while (pitchCount[lowest] == 0)
				lowest++;
			while (pitchCount[highest] == 0)
				highest--;
			features[9] = highest - lowest;
		}
		
		// Primary register
		for (NotePlay np : notes)
			features[10] += (np.note.getMIDIPitch(piece.scale) - piece.scale.root) % 12;
		features[10] /= notes.size();
		
		// Importance of bass register
		for(int i = 0; i <= 54; i++) {
			if (pitchCount[i] > 0)
				features[11]++;
		}
		features[11] /= notes.size();
		
		// Importance of middle register
		for(int i = 55; i <= 72; i++) {
			if (pitchCount[i] > 0)
				features[12]++;
		}
		features[12] /= notes.size();
		
		// Importance of high register
		for(int i = 73; i <= 127; i++) {
			if (pitchCount[i] > 0)
				features[13]++;
		}
		features[13] /= notes.size();
		
		return features;
	}
	
	// ==================================================================================
	// Intervals
	// ==================================================================================
	
	private static double[] intervalFeatures(Scale scale, Note[] intervals) {
		double[] features = new double[intervalWeights.length];
		
		// ------------------------------------------------------------------------------
		// Intervals
		// ------------------------------------------------------------------------------
		
		HashMap<Integer, Integer> count = new HashMap<>();
		int longestIntervalSize = 0;
		int chromatic = 0, stepwise = 0, thirds = 0, fifths = 0, 
				tritones = 0, octaves = 0, dissonants = 0;
		int falling = 0, rising = 0;
		for (int i = 0; i < intervals.length; i += 2) {
			int pitch1 = intervals[i].getMIDIPitch(scale);
			int pitch2 = intervals[i + 1].getMIDIPitch(scale);
			int interval = Math.abs(pitch2 - pitch1);
			if (interval > longestIntervalSize) {
				longestIntervalSize = interval;
			}
			int currentCount = count.getOrDefault(pitch2 - pitch1, 0) + 1;
			count.put(pitch2 - pitch1, currentCount);
			
			if (pitch2 > pitch1)
				rising++;
			else if (pitch1 > pitch2)
				falling++;
			
			interval = interval % 12;
			if (interval == 1) {
				chromatic++;
				dissonants++;
			} else if (interval == 4 || interval == 3)
				thirds++;
			else if (interval == 7)
				fifths++;
			else if (interval == 6) {
				tritones++;
				dissonants++;
			} else if (interval == 12)
				octaves++;
			else if (intervals[i].accidental == 0 && intervals[i + 1].accidental == 0 &&
					intervals[i].octaves == intervals[i + 1].octaves &&
					Math.abs(intervals[i].function - intervals[i + 1].function) == 1)
				stepwise++;
			else
				dissonants++;
		}
		
		// Longest melodic interval
		features[0] = longestIntervalSize;
		
		// Most common melodic interval
		int firstInterval = 0;
		int secondInterval = 0;
		for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
			if (entry.getValue() >= firstInterval) {
				secondInterval = firstInterval;
				firstInterval = entry.getValue();
				features[1] = entry.getKey();
			} else if (entry.getValue() > secondInterval)
				secondInterval = entry.getValue();
		}
		
		// Distance between most common melodic intervals
		features[2] = Math.abs(firstInterval - secondInterval);
		
		// Most common melodic interval prevalence
		features[3] = firstInterval * 2.0 / intervals.length;
		
		// Relative strength of most common intervals
		features[4] = secondInterval  * 2.0 / intervals.length / features[3];
		
		// Number of common melodic intervals
		double mininum = 0.09 * 2.0 * intervals.length;
		for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
			if (entry.getValue() >= mininum) {
				features[5]++;
			}
		}
		
		// Chromatic motion
		features[6] = chromatic * 2.0 / intervals.length;
		
		// Stepwise motion
		features[7] = stepwise * 2.0 / intervals.length;
		
		// Melodic thirds
		features[8] = thirds * 2.0 / intervals.length;
		
		// Melodic fifths
		features[9] = fifths * 2.0 / intervals.length;
		
		// Melodic tritones
		features[10] = tritones * 2.0 / intervals.length;
		
		// Melodic octaves
		features[11] = octaves * 2.0 / intervals.length;
		
		// Dissonance ratio
		features[12] = dissonants * 2.0 / intervals.length;
		
		// Rising motion
		features[13] = rising * 2.0 / intervals.length;
		
		// Falling motion
		features[14] = falling * 2.0 / intervals.length;
		
		return features;
	}
	
	// ==================================================================================
	// Note
	// ==================================================================================
	
	private static double[] noteFeatures(Scale scale, Melody melody) {
		double[] features = new double[noteWeights.length];
		
		HashMap<Integer, Boolean> repeat = new HashMap<>();
		HashMap<Integer, Integer> occurrence = new HashMap<>();
		int repetitions = 1;
		int length = 0;
		for (NotePlay np : melody) {
			int pitch = np.note.getMIDIPitch(scale);
			int lastOccurence = occurrence.getOrDefault(pitch, 0);
			if (lastOccurence > 0 && length - lastOccurence <= 16) {
				features[1] += length;
				occurrence.clear();
				repetitions++;
				length = 0;
				if (!repeat.getOrDefault(pitch, false)) {
					features[0]++;
					repeat.put(pitch, true);
				}
			} else {
				length++;
				occurrence.put(pitch, length);
			}
		}

		// Repeated notes
		features[0] /= melody.size();
		
		// Melodic pitch variety
		features[1] = (features[1] + length) / repetitions;
		
		int[] functionCount = melody.getFunctions();
		int firstFunction = 0, secondFunction = 0;
		for (int i = 1; i < 7; i++) {
			if (functionCount[i] >= functionCount[firstFunction]) {
				secondFunction = firstFunction;
				firstFunction = i;
			} else if (functionCount[i] > functionCount[secondFunction])
				secondFunction = i;
		}
		
		// Most common function
		features[2] = firstFunction;
		
		// Most common function prevalence
		if (melody.size() == 0)
			features[3] = 0;
		else 
			features[3] = functionCount[firstFunction] / melody.size();
		
		// Relative strength between top functions
		if (melody.size() == 0)
			features[4] = 0;
		else 
			features[4] = functionCount[secondFunction] / functionCount[firstFunction];
		
		// Average octave
		for (NotePlay np : melody) {
			features[5] += np.note.octaves;
		}
		features[5] /= melody.size();
		
		// Accidental incidence
		for (NotePlay np : melody) {
			if (np.note.accidental != 0)
				features[6]++;
		}
		features[6] /= melody.size();
		
		return features;
	}
	
}
