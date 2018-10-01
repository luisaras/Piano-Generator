package gen;

import music.Composition;

public class Individual {
	
	public final Composition piece;
	
	public final double[][] features; 
	public final double distance;
	public final double melodyDistance;
	public final double harmonyDistance;
	
	public Individual (Composition piece) {
		this.piece = piece;
		distance = melodyDistance = harmonyDistance = 0;
		features = Features.calculate(piece);
	}
	
	public Individual (Composition piece, Individual template) {
		this.piece = piece;
		features = Features.calculate(piece);
		// Melody distance
		melodyDistance = getDifferences(template, 0) + getDifferences(template, 2) + 
			getDifferences(template, 4) + getDifferences(template, 7);
		// Harmony distance
		harmonyDistance = getDifferences(template, 1) + getDifferences(template, 3) + 
			getDifferences(template, 5) + getDifferences(template, 8) + getDifferences(template, 9);
		// General distance
		distance = melodyDistance + harmonyDistance + getDifferences(template, 6);
	}
	
	private double getDifferences(Individual template, int i) {
		double distance = 0;
		for (int j = 0; j < features[i].length; j++) {
			double d = template.features[i][j] - features[i][j];
			distance += d * d;
		}
		return distance;
	}
	
	public void printFeatures() {
		for (int i = 0; i < features.length; i++) {
			for(int j = 0; j < features[i].length; j++) {
				System.out.println(i + " " + j + ": " + features[i][j]);
			}
		}
	}
	
	public void printDifferences(Individual template) {
		for (int i = 0; i < features.length; i++) {
			for(int j = 0; j < features[i].length; j++) {
				double v = Math.abs(features[i][j] - template.features[i][j]);
				System.out.println(i + " " + j + ": " + v);
			}
		}
	}
	
}
