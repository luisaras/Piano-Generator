package gen;

import music.Composition;

public class Individual {
	
	public final Composition piece;
	
	public final double[][] features; 
	public final double distance;
	
	public Individual (Composition piece) {
		this.piece = piece;
		this.distance = 0;
		features = Features.calculate(piece);
	}
	
	public Individual (Composition piece, Individual template) {
		this.piece = piece;
		features = Features.calculate(piece);
		double distance = 0;
		for(int i = 0; i < features.length; i++) {
			for(int j = 0; j < features[i].length; j++) {
				double d = features[i][j] - template.features[i][j];
				distance += d * d * Features.weights[i][j];
			}
		}
		this.distance = distance;
	}
	
}
