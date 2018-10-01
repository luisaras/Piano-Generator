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
	
	private static void setWeights(double[] features, double weight) {
		for (int i = 0; i < features.length; i++)
			features[i] *= weight;
	}
	
	// ==================================================================================
	// Features
	// ==================================================================================
	
	public static double[][] calculate(Composition piece) {
		
		Melody chords = piece.harmony.asMelody();
		Melody arpeggio = piece.harmony.arpeggio.asMelody(piece.scale, piece.harmony.get(0));
		
		// Rhythm
		double[] melodyr = rhythmFeatures(piece, piece.melody);		
		setWeights(melodyr, 1);
		double[] harmonyr = rhythmFeatures(piece, piece.harmony.arpeggio.getPlays());
		setWeights(harmonyr, 0.5);
		
		// Pitch
		double[] melodyp = pitchFeatures(piece, piece.melody);
		setWeights(melodyp, 0.5);
		double[] harmonyp = pitchFeatures(piece, piece.harmony.asMelody(piece.scale));
		setWeights(harmonyp, 0.5);
		
		// Intervals
		double[] melodyi = intervalFeatures(piece.scale, piece.melody.getIntervals());
		setWeights(melodyi, 5);
		double[] chordsi = intervalFeatures(piece.scale, chords.getIntervals());
		setWeights(chordsi, 2);
		double[] arpeggioi = harmonyIntervals(piece);
		setWeights(arpeggioi, 3);
		
		// Note
		double[] melodyn = noteFeatures(piece.scale, piece.melody);
		setWeights(melodyn, 2);
		double[] chordsn = noteFeatures(piece.scale, chords);
		setWeights(chordsn, 3);
		double[] arpeggion = noteFeatures(piece.scale, arpeggio);
		setWeights(arpeggion, 0.5);

		return new double[][] { melodyr, harmonyr,
				melodyp, harmonyp,
				melodyi, chordsi, arpeggioi,
				melodyn, chordsn, arpeggion };
	}
	
	// ==================================================================================
	// Rhythm
	// ==================================================================================
	
	private static double[] rhythmFeatures(Composition piece, Melody notes) {
		double[] features = new double[10];
		
		double seconds = notes.duration / piece.bpm * 60;
		
		// Note density
		features[0] = notes.size() / seconds * 50;
		
		// Note duration mean, maximum and minimum
		features[2] = 100;
		for (NotePlay np : notes) {
			features[1] += np.duration;
			features[2] = Math.min(np.duration, features[3]);
			features[3] = Math.max(np.duration, features[4]);
		}
		features[1] /= notes.size();
		
		// Note duration variation
		for (NotePlay np : notes) {
			double d = features[1] - np.duration;
			features[4] += d * d;
		}
		features[4] *= 10.0 / notes.size();
		
		// Staccato incidence
		double staccatoLength = 0.1 * piece.bpm / 60;
		for (NotePlay np : notes) {
			if (np.duration < staccatoLength)
				features[5]++;
		}
		features[5] /= notes.size();
		
		double[] attacks = notes.getAttacks();
		if (attacks.length > 0) {
			// Attack mean
			for (double attack : attacks) {
				features[6] += attack;
			}
			features[6] /= attacks.length;
			
			// Attack variation
			for (double attack : attacks) {
				double d = features[6] - attack;
				features[7] += d * d;
			}
			features[7] *= 20.0 / attacks.length;
		} else {
			features[6] = notes.duration;
			features[7] = 0;
		}
		
		// Complete rest
		NotePlay[] rests = notes.getRests();
		for (NotePlay np : rests) {
			features[8] += np.duration;
			features[9] = Math.max(np.duration, features[9]);
		}
		features[8] /= notes.duration;

		return features;
	}
	
	// ==================================================================================
	// Pitch
	// ==================================================================================
	
	private static double[] pitchFeatures(Composition piece, Melody notes) {
		double[] features = new double[14];
		
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
		
		// Difference between top pitches
		features[4] = Math.abs(pitchCount[firstPitch] - pitchCount[secondPitch]) * 10;
		
		// Difference between top pitch classes
		features[5] = Math.abs(classCount[firstClass] - classCount[secondClass]);
		
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
		
		for (NotePlay np : notes) {
			int pitch = np.note.getMIDIPitch(piece.scale);
			features[10] += pitch;
			features[11] += pitch % 12;
			features[12] += (pitch / 12) * 12;
			features[13] += pitch - piece.scale.root;
		}
		
		// Average pitch
		features[10] *= 10.0 / notes.size();
		
		// Average pitch class
		features[11] /= notes.size();
		
		// Average pitch octave
		features[12] *= 10.0 / notes.size();
		
		// Average pitch position relative to scale
		features[13] /= notes.size();
		
		return features;
	}
	
	// ==================================================================================
	// Intervals
	// ==================================================================================
	
	private static double[] intervalFeatures(Scale scale, Note[] intervals) {
		double[] features = new double[15];
		
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
			} else if (interval == 0)
				octaves++;
			else if (intervals[i].accidental == 0 && intervals[i + 1].accidental == 0 &&
					intervals[i].octaves == intervals[i + 1].octaves &&
					Math.abs(intervals[i].function - intervals[i + 1].function) == 1)
				stepwise++;
			else
				dissonants++;
		}
		
		// Longest melodic interval
		features[0] = longestIntervalSize * 10;
		
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
		features[1] *= 10;
		
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
		features[6] = chromatic * 2.0 / intervals.length * 200;
		
		// Stepwise motion
		features[7] = stepwise * 2.0 / intervals.length;
		
		// Melodic thirds
		features[8] = thirds * 2.0 / intervals.length;
		
		// Melodic fifths
		features[9] = fifths * 2.0 / intervals.length * 100;
		
		// Melodic tritones
		features[10] = tritones * 2.0 / intervals.length * 100;
		
		// Melodic octaves
		features[11] = octaves * 2.0 / intervals.length;
		
		// Dissonance ratio
		features[12] = dissonants * 2.0 / intervals.length * 50;
		
		// Rising motion
		features[13] = rising * 2.0 / intervals.length * 10;
		
		// Falling motion
		features[14] = falling * 2.0 / intervals.length * 20;
		
		return features;
	}
	
	// ==================================================================================
	// Note
	// ==================================================================================
	
	private static double[] noteFeatures(Scale scale, Melody melody) {
		double[] features = new double[13];
		
		HashMap<Integer, Boolean> repeat = new HashMap<>();
		HashMap<Integer, Integer> occurrence = new HashMap<>();
		int repetitions = 1;
		int length = 0;
		for (NotePlay np : melody) {
			features[np.note.function]++;
			int pitch = np.note.getMIDIPitch(scale);
			int lastOccurence = occurrence.getOrDefault(pitch, 0);
			if (lastOccurence > 0 && length - lastOccurence <= 16) {
				features[8] += length;
				occurrence.clear();
				repetitions++;
				length = 0;
				if (!repeat.getOrDefault(pitch, false)) {
					features[7]++;
					repeat.put(pitch, true);
				}
			} else {
				length++;
				occurrence.put(pitch, length);
			}
		}
		features[0] *= 10;
		features[1] *= 10;
		features[4] *= 10;
		features[5] *= 10;

		// Repeated notes
		features[7] /= melody.size();
		
		// Melodic pitch variety
		features[8] = (features[8] + length) / repetitions;
		
		int[] functionCount = melody.getFunctions();
		int firstFunction = 0, secondFunction = 0;
		for (int i = 1; i < 7; i++) {
			if (functionCount[i] >= functionCount[firstFunction]) {
				secondFunction = firstFunction;
				firstFunction = i;
			} else if (functionCount[i] > functionCount[secondFunction])
				secondFunction = i;
		}
		
		// Most common function prevalence
		if (melody.size() == 0)
			features[9] = 0;
		else 
			features[9] = functionCount[firstFunction] / melody.size();
		
		// Relative strength between top functions
		if (melody.size() == 0)
			features[10] = 0;
		else 
			features[10] = functionCount[secondFunction] / functionCount[firstFunction];
		
		// Average octave
		for (NotePlay np : melody) {
			features[11] += np.note.octaves;
		}
		features[11] /= melody.size();
		
		// Accidental incidence
		for (NotePlay np : melody) {
			if (np.note.accidental != 0)
				features[12]++;
		}
		features[12] *= 1000.0 / melody.size();
		
		return features;
	}
	
	// ==================================================================================
	// Harmony
	// ==================================================================================

	private static double[] harmonyIntervals(Composition piece) {
		double[] features = new double[15];
		
		for (int m = 0; m < piece.length; m++) {
			Note[] measureIntervals = piece.getMeasureIntervals(m);
			double[] measureFeatures = intervalFeatures(piece.scale, measureIntervals);
			for (int i = 0; i < features.length; i++)
				features[i] += measureFeatures[i] / piece.length 
					* 2 * piece.melody.size() / measureIntervals.length;
		}
		
		return features;
	}
	
}
