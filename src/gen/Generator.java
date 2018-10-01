package gen;

import java.util.Arrays;
import java.util.Comparator;

import music.Chord;
import music.Composition;
import music.Harmony;
import music.Melody;
import music.Note;
import music.NotePlay;
import music.Scale;

public class Generator extends RandomGenerator {
	
	public int generationCount = 100;
	public int populationSize = 30;
	public int tournamentSize = 15;
	
	public final Individual template;
	private Individual[] population = new Individual[populationSize];
	
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
		Arrays.sort(population, comparator);
	}
	
	public void initializePopulation() {
		for (int i = 0; i < population.length; i++) {
			do {
				Composition piece = generate(template.piece);
				mutate(piece);
				piece.melody.sort();
				population[i] = new Individual(piece, template);
			} while (Double.isNaN(population[i].distance));
		}
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
	public float melodyMutation = 0.5f;
	public float harmonyMutation = 0.5f;
	
	protected void mutate(Composition piece) {
		if (rand.nextDouble() < signatureMutation) {
			mutateSignature(piece);
		}
		if (rand.nextDouble() < melodyMutation) {
			mutateMelody(piece.melody, piece.scale);
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
	// Mutation - Melody
	// ==================================================================================
	
	public float lineMutation = 0f;
	public float durationMutation = 0.05f;
	public float attackMutation = 0.05f;
	
	public float melodyFunctionMutation = 0.25f;
	public float melodyAccidentalMutation = 0.05f;
	public float melodyOctaveMutation = 0.25f;
	
	public void mutateMelody(Melody melody, Scale scale) {
		// Remove notes
		for (int i = 0; i < melody.size(); i++) {
			if (rand.nextDouble() < lineMutation) {
				melody.remove(i);
				i--;
			}
		}
		// Split notes
		for (int i = 0; i < melody.size(); i++) {
			if (rand.nextDouble() < lineMutation) {
				NotePlay orig = melody.get(i);
				orig.duration /= 2;
				NotePlay np = new NotePlay(orig.note.clone(), 
						orig.time + orig.duration, orig.duration);
				melody.add(i + 1, np);
				i++;
			}
		}
		// Change notes
		int next = 0;
		for (NotePlay np : melody) {
			next++;
			// Change pitch
			mutateNote(np.note, scale, 
				melodyFunctionMutation, melodyAccidentalMutation);
			// Change start
			double end = next < melody.size() ? melody.get(next).time : melody.duration;
			if (rand.nextDouble() < attackMutation) {
				double start = next == 1 ? 0 : melody.get(next - 2).getEnd();
				double t = rand.nextDouble() * (end - start - 1 / NotePlay.minSize) + start;
				np.time = Math.floor(t * NotePlay.minSize) / NotePlay.minSize;
			}
			// Change duration
			if (rand.nextDouble() < durationMutation) {
				double d = rand.nextDouble() * (end - np.time - 1 / NotePlay.minSize); 
				np.duration = Math.floor(d * NotePlay.minSize + 1) / NotePlay.minSize;
			}
			np.setEnd(Math.min(np.getEnd(), end));
		}
		// Octaves
		if (rand.nextDouble() < melodyOctaveMutation) {
			int i = rand.nextBoolean() ? 1 : -1;
			for (NotePlay np : melody) {
				np.note.octaves += i;
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