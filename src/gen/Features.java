package gen;

import music.Composition;

public class Features {
	
	private static final double[] rhythmWeights = new double[] { 
		1
	};
	
	private static final double[] melodyWeights = new double[] {
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1
	};
	
	private static final double[] harmonyWeights = new double[] {
		
	};
	
	public static final double[][] weights = new double[][] { 
		rhythmWeights, melodyWeights, harmonyWeights
	};
	
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
		
		// Note duration
		melody[0] = stats.melody.durationMean;
		melody[1] = Math.sqrt(stats.melody.durationVariation);
		
		// Attack distance
		melody[2] = stats.melody.attackMean;
		melody[3] = Math.sqrt(stats.melody.attackVariation);
		
		// Note pitch
		melody[4] = stats.melody.pitchMean;
		melody[5] = Math.sqrt(stats.melody.pitchVariation);
		
		// Note position
		melody[6] = stats.melody.noteMean;
		melody[7] = Math.sqrt(stats.melody.noteVariation);
		
		// Note function
		melody[8] = stats.melody.functionMean;
		melody[9] = Math.sqrt(stats.melody.functionVariation);
		
		// Note accidental
		melody[10] = stats.melody.accidentalMean;
		melody[11] = Math.sqrt(stats.melody.accidentalVariation);
		
		// ==================================================================================
		// Harmony
		// ==================================================================================
		
		double[] harmony = new double[harmonyWeights.length];
		
		return new double[][] { rhythm, melody, harmony };
	}
	
}
