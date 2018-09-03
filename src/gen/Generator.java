package gen;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import music.Chord;
import music.Composition;
import music.Harmony;
import music.Melody;
import music.Note;
import music.NotePlay;
import music.Scale;

public class Generator extends RandomGenerator {
	
	public int generationCount = 200;
	public int populationSize = 20;
	public int tournamentSize = 5;
	
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
	
	public void save() {
		for (int i = 0; i < population.length; i++) {
			midi.Writer.write("tests/piece" + i + "(" + population[i].distance + ")", 
					population[i].piece);
		}
	}
	
	// ==================================================================================
	// Generation
	// ==================================================================================
	
	public float scaleMutation = 0.2f;
	public float melodyMutation = 0.2f;
	public float harmonyMutation = 0.2f;
	
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
				int point = rand.nextInt(parent1.piece.duration - 1) + 1;
				Composition first = parent1.piece.cut(0, point);
				Composition second = parent2.piece.cut(point, parent2.piece.duration);
				Composition child = randomSignature(template.piece).concatenate(first).concatenate(second);
				// Mutation
				if (rand.nextDouble() < scaleMutation) {
					mutateScale(child);
				}
				if (rand.nextDouble() < melodyMutation) {
					mutateMelody(child.melody, child.scale);
				}
				if (rand.nextDouble() < harmonyMutation) {
					mutateHarmony(child.harmony, child.scale);
				}
				population[i] = new Individual(child, template);
			} while (Double.isNaN(population[i].distance)); 
		}
		Arrays.sort(population, comparator);
	}
	
	// ==================================================================================
	// Mutation
	// ==================================================================================
	
	public float functionMutation = 0.5f;
	public float accidentalMutation = 0.05f;
	public float octaveMutation = 0.1f;
	public float durationMutation = 0.1f;
	public float attackMutation = 0.05f;
	public float arpeggioMutation = 0f;
	
	public void mutateScale(Composition piece) {
		int root = piece.scale.root;
		int mode = rand.nextInt(7);
		int sig = Scale.getSignature(root, mode);
		piece.scale = new Scale(root, mode, sig);
	}
	
	public void mutateNote(Note note, Scale scale) {
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
					note.octaves--;
				} else {
					note.function--;
				}
			}
		}
		// Octaves
		if (rand.nextDouble() < octaveMutation) {
			int oct = note.octaves + (rand.nextBoolean() ? 1 : -1);
			note.octaves = oct < 0 ? oct + 2 : oct;
		}
	}
	
	public void mutateMelody(Melody melody, Scale scale) {
		int next = 0;
		for (NotePlay np : melody) {
			next++;
			mutateNote(np.note, scale);
			double end = next < melody.size() ? melody.get(next).time : melody.duration;
			// Change duration
			if (rand.nextDouble() < durationMutation) {
				double d = rand.nextDouble() * (end - np.time); 
				np.duration = Math.floor(d * 64) / 64;
			}
			// Change start
			if (rand.nextDouble() < attackMutation) {
				double t = rand.nextDouble() * (end - np.time) + np.time;
				np.time = Math.floor(t * 64) / 64;
			}
		}
	}
	
	public void mutateHarmony(Harmony harmony, Scale scale) {
		for (Chord chord : harmony) {
			// Tonic
			mutateNote(chord.tonic, scale);
			// Arpeggio
			if (rand.nextDouble() < arpeggioMutation) {
				Scale tonicScale = chord.tonicScale(scale);
				for (Melody line : chord.arpeggio)
					mutateMelody(line, tonicScale);
			}
		}
	}

}