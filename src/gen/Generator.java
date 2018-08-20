package gen;

import music.Composition;

public class Generator {
	
	private final Individual template;
	
	public Generator(Composition template) {
		this.template = new Individual(template);
		getFitness(this.template);
	}
	
	public Composition generate() {
		// TODO
		return template.piece;
	}
	
	private double getFitness(Individual ind) {
		double f = 0;
		for(int i = 0; i < ind.features.length; i++) {
			double d = (ind.features[i] - template.features[i]) / template.features[i];
			//System.out.println(template.features[i]);
			f += d * d * Features.weights[i];
		}
		return Math.sqrt(f);
	}

}