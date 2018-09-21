package gen;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import music.Arpeggio;
import music.Chord;
import music.ChordPlay;
import music.Composition;
import music.Harmony;
import music.Melody;
import music.Note;
import music.NotePlay;
import music.Scale;

public class Generator extends RandomGenerator {
	
	public int generationCount = 0;
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
	}
	
	public void initializePopulation(Composition initialPiece) {
		for (int i = 0; i < population.length; i++) {
			do {
				Composition piece = initialPiece.clone();
				mutate(piece);
				piece.melody.sort();
				population[i] = new Individual(piece, template);
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
	// Debug
	// ==================================================================================
	
	public void print(Individual i) {
		System.out.println(i.piece.scale);
		System.out.println(i.piece.harmony);
	}
	
	public void save() {
		for (int i = 0; i < population.length; i++) {
			midi.Writer.write("tests/" + template.piece.name + " " + i + 
					" (" + population[i].distance + ")", population[i].piece);
		}
	}
	
	// ==================================================================================
	// Generation
	// ==================================================================================
	
	public Composition getFittest() {
		return population[0].piece;
	}
	
	public Composition generate() {
		for (int i = 0; i < generationCount; i++) {
			nextGeneration();
		}
		save();
		System.out.println("BEST:");
		population[0].printDifferences(template);
		System.out.println("WORST:");
		population[populationSize - 1].printDifferences(template);
		System.out.println();
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
		Individual parent1 = population[rand.nextInt(tournamentSize)];
		Individual parent2 = population[rand.nextInt(tournamentSize)];
		Individual parent3 = population[rand.nextInt(tournamentSize)];
		int point = rand.nextInt(parent1.piece.length - 1) + 1;
		Composition first = parent1.piece.cut(0, point);
		Composition second = parent2.piece.cut(point, parent2.piece.length);
		if (second.melody.size() == 0)
			System.err.println("aaaaaaaaa " + point);
		return parent3.piece.cloneSignature().concatenate(parent1.piece);
	}
	
	// ==================================================================================
	// Mutation
	// ==================================================================================
	
	public float signatureMutation = 0.5f;
	public float melodyMutation = 0f;
	public float harmonyMutation = 0f;
	
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
	
	public float tempoMutation = 0.1f;
	public float modeMutation = 0.5f;
	public float rootMutation = 0.1f;
	
	public void mutateSignature(Composition piece) {
		if (rand.nextDouble() < modeMutation) {
			int root = piece.scale.root;
			int mode = rand.nextInt(7);
			piece.scale = new Scale(root, mode);
		}
		if (rand.nextDouble() < rootMutation) {
			int root = rand.nextInt(12);
			if (rand.nextDouble() < octaveMutation) {
				root += rand.nextBoolean() ? 12 : -12;
			}
			piece.scale.setRoot(root);
		}
		if (rand.nextDouble() < tempoMutation) {
			double min = piece.bpm * 0.5;
			double max = piece.bpm * 1.5;
			piece.bpm = rand.nextDouble() * (max - min) + min;
		}
	}
	
	// ==================================================================================
	// Mutation - Note
	// ==================================================================================
	
	public float functionMutation = 0.5f;
	public float accidentalMutation = 0.5f;
	public float octaveMutation = 0.05f;
	
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
					note.octaves = Math.max(0, note.octaves-1);
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
	
	// ==================================================================================
	// Mutation - Melody
	// ==================================================================================
	
	public float noteMutation = 0.5f;
	public float durationMutation = 0.5f;
	public float attackMutation = 0.5f;
	
	public void mutateMelody(Melody melody, Scale scale) {
		// Remove notes
		for (int i = 0; i < melody.size(); i++) {
			if (rand.nextDouble() < noteMutation) {
				melody.remove(i);
				i--;
			}
		}
		// Split notes
		for (int i = 0; i < melody.size(); i++) {
			if (rand.nextDouble() < noteMutation) {
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
			mutateNote(np.note, scale);
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
	}
	
	// ==================================================================================
	// Mutation - Harmony
	// ==================================================================================
	
	public float arpeggioMutation = 0f;
	
	public void mutateHarmony(Harmony harmony, Scale scale) {
		// Tonic
		for (Chord chord : harmony) {
			mutateNote(chord.tonic, scale);
		}
		// Arpeggio
		Scale tonicScale = harmony.get(0).tonicScale(scale);
		if (rand.nextDouble() < arpeggioMutation)
			mutateArpeggio(harmony.arpeggio, tonicScale);
	}
	
	public void mutateArpeggio(Arpeggio arpeggio, Scale scale) {
		// Remove notes
		for(int i = 0; i < arpeggio.size(); i++) {
			if (rand.nextDouble() < noteMutation) {
				arpeggio.remove(i);
				i--;
			}
		}
		// Insert notes
		for(int i = 0; i < arpeggio.size(); i++) {
			if (rand.nextDouble() < noteMutation) {
				ChordPlay orig = arpeggio.get(i);
				orig.duration /= 2;
				ChordPlay np = orig.clone();
				np.time += orig.duration;
				arpeggio.add(i + 1, np);
				i++;
			}
		}
		// Change notes
		int next = 0;
		for (ChordPlay cp : arpeggio) {
			next++;
			// Change pitch
			for (Note n : cp) {
				mutateNote(n, scale);
			}
			double end = next < arpeggio.size() ? arpeggio.get(next).time : arpeggio.duration;
			// Change attacks
			if (rand.nextDouble() < attackMutation) {
				double start = next == 1 ? 0 : arpeggio.get(next - 2).getEnd();
				double t = rand.nextDouble() * (end - start - 1 / NotePlay.minSize) + start;
				cp.time = Math.floor(t * NotePlay.minSize) / NotePlay.minSize;
			}
			// Change duration
			if (rand.nextDouble() < durationMutation) {
				double d = rand.nextDouble() * (end - cp.time - 1 / NotePlay.minSize); 
				cp.duration = Math.floor(d * NotePlay.minSize + 1) / NotePlay.minSize;
			}
			cp.setEnd(Math.min(cp.getEnd(), end));
		}
	}

}