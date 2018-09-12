package gen;

import music.Composition;
import music.Melody;

public class Features {
	
	// ==================================================================================
	// Weights
	// ==================================================================================
	
	private static final double[] rhythmWeights = new double[] { 
		1
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
	
	// ==================================================================================
	// Stats
	// ==================================================================================
	
	private static void setMelodyFeatures(double[] features, Melody.Stats s) {
		// Note duration
		features[0] = s.durationMean;
		features[1] = Math.sqrt(s.durationVariation);
		
		// Attack distance
		features[2] = s.attackMean;
		features[3] = Math.sqrt(s.attackVariation);
		
		// Note pitch
		features[4] = s.pitchMean;
		features[5] = Math.sqrt(s.pitchVariation);
		
		// Note position
		features[6] = s.noteMean;
		features[7] = Math.sqrt(s.noteVariation);
		
		// Note function
		features[8] = s.functionMean;
		features[9] = Math.sqrt(s.functionVariation);
		
		// Note accidental
		features[10] = s.accidentalMean;
		features[11] = Math.sqrt(s.accidentalVariation);
	}
	
	public static double[][] calculate(Composition piece) {
		
		Composition.Stats stats = piece.getStats();
		
		// ==================================================================================
		// Rhythm
		// ==================================================================================
		
		double[] rhythm = new double[rhythmWeights.length];
		
		// Notes per second
		rhythm[0] = stats.notesPerSecond;
		
		// ==================================================================================
		// Melody
		// ==================================================================================
		
		double[] melody = new double[melodyWeights.length];
		setMelodyFeatures(melody, stats.melody);
		
		// ==================================================================================
		// Chords
		// ==================================================================================
		
		double[] chords = new double[chordsWeights.length];
		setMelodyFeatures(chords, stats.harmony.chords);
		
		// ==================================================================================
		// Arpeggio
		// ==================================================================================
		
		double[] arpeggio = new double[arpeggioWeights.length];
		setMelodyFeatures(arpeggio, stats.harmony.arpeggio);
		
		// Vertical notes
		arpeggio[12] = stats.harmony.arpeggio.verticalNoteMean;
		arpeggio[13] = Math.sqrt(stats.harmony.arpeggio.verticalNoteVariation);
		
		return new double[][] { rhythm, melody, chords, arpeggio };
	}
	
}
