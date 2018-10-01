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
	
	public int populationSize = 50;
	public int tournamentSize = 15;
	
	public final Individual template;
	private Individual[] population = new Individual[populationSize];
	private Individual[] melodies = new Individual[populationSize];
	private Individual[] harmonies = new Individual[populationSize];
	
	// ==================================================================================
	// Comparators
	// ==================================================================================
	
	private Comparator<Individual> comparator = new Comparator<Individual>() {
		public int compare(Individual o1, Individual o2) {
			return (int) Math.signum(o1.distance - o2.distance);
		}
	};
	
	private Comparator<Individual> melodyComparator = new Comparator<Individual>() {
		public int compare(Individual o1, Individual o2) {
			return (int) Math.signum(o1.melodyDistance - o2.melodyDistance);
		}
	};
	
	private Comparator<Individual> harmonyComparator = new Comparator<Individual>() {
		public int compare(Individual o1, Individual o2) {
			return (int) Math.signum(o1.harmonyDistance - o2.harmonyDistance);
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
			harmonies[i] = melodies[i] = population[i];
		}
		Arrays.sort(population, comparator);
		Arrays.sort(melodies, melodyComparator);
		Arrays.sort(harmonies, harmonyComparator);
	}
	
	public void initializePopulation() {
		for (int i = 0; i < population.length; i++) {
			do {
				Composition piece = generate(template.piece);
				mutate(piece);
				piece.melody.sort();
				population[i] = new Individual(piece, template);
			} while (Double.isNaN(population[i].distance));
			harmonies[i] = melodies[i] = population[i];
		}
		Arrays.sort(population, comparator);
		Arrays.sort(melodies, melodyComparator);
		Arrays.sort(harmonies, harmonyComparator);
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
			harmonies[i] = melodies[i] = population[i];
		}
		Arrays.sort(population, comparator);
		Arrays.sort(melodies, melodyComparator);
		Arrays.sort(harmonies, harmonyComparator);
	}
	
	// ==================================================================================
	// Crossover
	// ==================================================================================
		
	public float newSignatureMutation = 0.05f;
	public float newMelodyMutation = 0.05f;
	public float newHarmonyMutation = 0.05f;
	
	public Composition crossover() {
		Composition child;
		if (rand.nextDouble() < newSignatureMutation)
			child = randomSignature(template.piece);
		else {
			Individual signature = population[rand.nextInt(tournamentSize)];
			child = signature.piece.cloneSignature();
		}
		if (rand.nextDouble() < newMelodyMutation)
			child.melody = randomMelody(template.piece.melody, template.piece.scale);
		else {
			Individual melody = population[rand.nextInt(tournamentSize)];
			child.melody = melody.piece.melody.clone();
		}
		if (rand.nextDouble() < newHarmonyMutation)
			child.harmony = randomHarmony(template.piece.harmony, template.piece.scale);
		else {
			Individual harmony = population[rand.nextInt(tournamentSize)];
			child.harmony = harmony.piece.harmony.clone();
		}
		return child;
	}
	
	// ==================================================================================
	// Mutation
	// ==================================================================================
	
	public float signatureMutation = 1f;
	public float melodyMutation = 1f;
	public float harmonyMutation = 1f;
	
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
			if (pitch < 0)
				pitch *= -1;
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
	
	public float lineMutation = 0.05f;
	public float durationMutation = 0.15f;
	public float attackMutation = 0.15f;
	
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
			NotePlay orig = melody.get(i);
			if (orig.duration > 1 / NotePlay.minSize && rand.nextDouble() < lineMutation) {
				double point = rand.nextInt((int)(orig.duration * NotePlay.minSize - 1)) + 1;
				point /= NotePlay.minSize;
				NotePlay np = new NotePlay(orig.note.clone(), 
						orig.time + point, orig.duration - point);
				melody.add(i + 1, np);
				orig.duration = point;
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
	public float harmonyOctaveMutation = 0.25f;
	
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