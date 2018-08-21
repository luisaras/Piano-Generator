package gen;

import music.Composition;

public class Generator extends RandomGenerator {
	
	public int populationSize = 10;
	public int tournamentSize = 3;
	public float noteMutation = 0.2f;
	public float scaleMutation = 0.2f;
	
	private final Individual template;
	
	public Generator(Composition template) {
		this.template = new Individual(template);
	}
	
	public Composition generate() {
		Composition bestPiece = null;
		double minDistance = 0;
		for (int i = 0; i < populationSize; i++) {
			Composition piece = generate(template.piece);
			double distance = getDistance(new Individual(piece));
			System.out.println(distance);
			if (bestPiece == null || minDistance > distance) {
				minDistance = distance;
				bestPiece = piece;
			}
			midi.Writer.write("tests/piece" + i + "(" + distance + ")", piece);
		}
		return bestPiece;
	}
	
	private double getDistance(Individual ind) {
		double f = 0;
		for(int i = 0; i < ind.features.length; i++) {
			double d = (ind.features[i] - template.features[i]) / template.features[i];
			f += d * d * Features.weights[i];
		}
		return Math.sqrt(f);
	}

}