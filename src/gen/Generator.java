package gen;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import music.Composition;

public class Generator extends RandomGenerator {
	
	public int generationCount = 100;
	public int populationSize = 20;
	public int tournamentSize = 3;
	
	public float noteMutation = 0.2f;
	public float scaleMutation = 0.2f;
	
	private final Individual template;
	private Individual[] population = new Individual[populationSize];
	
	private Random rand = new Random(0);
	private Comparator<Individual> comparator = new Comparator<Individual>() {
		public int compare(Individual o1, Individual o2) {
			return (int) Math.signum(o1.distance - o2.distance);
		}
	};
	
	// ==================================================================================
	// Initialization
	// ==================================================================================
	
	public Generator(Composition templatePiece) {
		template = new Individual(templatePiece);
		for (int i = 0; i < population.length; i++) {
			do {
				Composition piece = generate(template.piece);
				population[i] = new Individual(piece, template);
			} while (Double.isNaN(population[i].distance));
		}
		Arrays.sort(population, comparator);
	}
	
	// ==================================================================================
	// Generation
	// ==================================================================================
	
	public Composition generate() {
		for (int i = 0; i < generationCount; i++) {
			nextGeneration();
		}
		save();
		return population[0].piece;
	}
	
	public void nextGeneration() {
		for (int i = tournamentSize; i < population.length; i++) {
			do {
				Individual parent1 = population[rand.nextInt(tournamentSize)];
				Individual parent2 = population[rand.nextInt(tournamentSize)];
				// Cross-over
				int point = rand.nextInt(template.piece.duration - 1) + 1;
				Composition first = parent1.piece.cut(0, point);
				Composition second = parent2.piece.cut(point, parent2.piece.duration);
				Composition child = first.concatenate(second);
				// Mutation
				if (rand.nextDouble() > noteMutation) {
					
				}
				if (rand.nextDouble() > scaleMutation) {
					
				}
				population[i] = new Individual(child, template);
			} while (Double.isNaN(population[i].distance)); 
		}
		Arrays.sort(population, comparator);
	}
	
	public void save() {
		for (int i = 0; i < population.length; i++) {
			midi.Writer.write("tests/piece" + i + "(" + population[i].distance + ")", 
					population[i].piece);
		}
	}

}