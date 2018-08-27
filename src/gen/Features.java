package gen;

import music.Composition;

public class Features {

	public static final double[] weights = new double[] { 
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 
	};
	
	public static double[] calculate(Composition piece) {
		double[] features = new double[weights.length];
		
		Composition.Stats stats = piece.getStats();
		
		// ==================================================================================
		// Rhythm
		// ==================================================================================
		
		// Notes per second
		features[0] = stats.notesPerSecond;
		
		// ==================================================================================
		// Melody
		// ==================================================================================
		
		// Note duration
		features[1] = stats.melody.durationMean;
		features[2] = Math.sqrt(stats.melody.durationVariation);
		
		// Attack distance
		features[3] = stats.melody.attackMean;
		features[4] = Math.sqrt(stats.melody.attackVariation);
		
		// Note pitch
		features[5] = stats.melody.pitchMean;
		features[6] = Math.sqrt(stats.melody.pitchVariation);
		
		// Note position
		features[7] = stats.melody.noteMean;
		features[8] = Math.sqrt(stats.melody.noteVariation);
		
		// Note function
		features[9] = stats.melody.functionMean;
		features[10] = Math.sqrt(stats.melody.functionVariation);
		
		// Note accidental
		features[11] = stats.melody.accidentalMean;
		features[12] = Math.sqrt(stats.melody.accidentalVariation);
		
		return features;
	}
	
}
