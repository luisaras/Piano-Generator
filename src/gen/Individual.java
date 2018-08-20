package gen;

import music.Composition;

public class Individual {
	
	public final Composition piece;
	
	public double[] features; 
	
	public Individual (Composition piece) {
		this.piece = piece;
		features = Features.calculate(piece);
	}
	
}
