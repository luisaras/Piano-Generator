package gen;

import java.util.Arrays;
import java.util.Comparator;

import music.Chord;
import music.Composition;
import music.Harmony;
import music.Note;
import music.Scale;

public class Modifier extends RandomGenerator {

	public int generationCount = 100;
	public int populationSize = 60;
	public int tournamentSize = 15;
	
	public final Individual template;
	public Individual base = null;
	public Individual[] population = new Individual[populationSize];
	
	protected Comparator<Individual> comparator = new Comparator<Individual>() {
		public int compare(Individual o1, Individual o2) {
			return (int) Math.signum(o1.distance - o2.distance);
		}
	};

	// ==================================================================================
	// Initialization
	// ==================================================================================
	
	public Modifier(Composition templatePiece) {
		template = new Individual(templatePiece);
	}
	
	public void initializePopulation(Composition initialPiece) {
		for (int i = 0; i < population.length; i++) {
			int it = 0;
			do {
				if (it >= 20) {
					population[i].printDifferences(template);
					throw new RuntimeException("" + population[i].distance);
				}
				Composition piece = initialPiece.clone();
				mutate(piece);
				piece.melody.sort();
				population[i] = new Individual(piece, template);
				it++;
			} while (Double.isNaN(population[i].distance));
			population[i].piece.melody.sort();
		}
		base = new Individual(initialPiece, template);
		Arrays.sort(population, comparator);
	}
	
	// ==================================================================================
	// Generation
	// ==================================================================================
	
	public Individual getFittest() {
		return population[0];
	}
	
	public Individual getWorst() {
		return population[populationSize - 1];
	}
	
	public Composition generate() {
		return generate(generationCount);
	}
	
	public Composition generate(int generationCount) {
		for (int i = 0; i < generationCount; i++) {
			nextGeneration();
		}
		return population[0].piece;
	}
	
	public void nextGeneration() {
		for (int i = tournamentSize; i < population.length; i++) {
			do {
				Composition child = crossover();
				mutate(child);
				child.melody.sort();
				population[i] = new Individual(child, template);
			} while (Double.isNaN(population[i].distance)); 
		}
		Arrays.sort(population, comparator);
	}
	
	public Composition crossover() {
		Individual signature = population[rand.nextInt(tournamentSize)];
		Individual melody = population[rand.nextInt(tournamentSize)];
		Individual harmony = population[rand.nextInt(tournamentSize)];
		Composition child = signature.piece.cloneSignature();
		child.melody = melody.piece.melody.clone();
		child.harmony = harmony.piece.harmony.clone();
		return child;
	}
	
	// ==================================================================================
	// Mutation
	// ==================================================================================
	
	public float signatureMutation = 0.5f;
	public float harmonyMutation = 0.5f;
	
	protected void mutate(Composition piece) {
		if (rand.nextDouble() < signatureMutation) {
			mutateSignature(piece);
		}
		if (rand.nextDouble() < harmonyMutation) {
			mutateHarmony(piece.harmony, piece.scale);
		}
	}
	
	// ==================================================================================
	// Mutation - Signature
	// ==================================================================================
	
	public float tempoMutation = 0.5f;
	public float modeMutation = 0.5f;
	
	public void mutateSignature(Composition piece) {
		if (rand.nextDouble() < modeMutation) {
			int root = piece.scale.root;
			int mode = rand.nextInt(7);
			piece.scale = new Scale(root, mode);
		}
		if (rand.nextDouble() < tempoMutation) {
			double min = piece.bpm * 0.5;
			double max = piece.bpm * 1.5;
			piece.bpm = rand.nextDouble() * (max - min) + min;
		}
	}
	
	// ==================================================================================
	// Mutation - Pitch
	// ==================================================================================
	
	public void mutateNote(Note note, Scale scale, 
			float functionMutation, float accidentalMutation) {
		// Change accidental
		if (rand.nextDouble() < accidentalMutation) {
			int pitch = note.getMIDIPitch(scale) + 
					(rand.nextBoolean() ? 1 : -1);
			Note note2 = scale.getPosition(pitch);
			note.function = note2.function;
			note.accidental = note2.accidental;
			note.octaves = note2.octaves;
		}
		// Change functions
		if (rand.nextDouble() < functionMutation) {
			for (int i = 0; i < 3; i++)
			if (rand.nextBoolean()) {
				// Increase
				if (note.function == 6) {
					note.function = 0;
					note.octaves++;
				} else {
					note.function++;
				}
			} else {
				// Decrease
				if (note.function == 0) {
					note.function = 6;
					note.octaves = Math.max(0, note.octaves-1);
				} else {
					note.function--;
				}
			}
		}
	}
	
	// ==================================================================================
	// Mutation - Harmony
	// ==================================================================================
	
	public float harmonyFunctionMutation = 0.5f;
	public float harmonyAccidentalMutation = 0.05f;
	public float harmonyOctaveMutation = 0.2f;
	
	public void mutateHarmony(Harmony harmony, Scale scale) {
		// Tonic
		for (Chord chord : harmony) {
			mutateNote(chord.tonic, scale, 
				harmonyFunctionMutation, harmonyAccidentalMutation);
		}
		// Octaves
		if (rand.nextDouble() < harmonyOctaveMutation) {
			int i = rand.nextBoolean() ? 1 : -1;
			for (Chord chord : harmony) {
				chord.tonic.octaves += i;
			}
		}
	}
	
}
