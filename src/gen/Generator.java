package gen;

import java.util.Arrays;

import music.Composition;
import music.Melody;
import music.NotePlay;
import music.Scale;

public class Generator extends Modifier {
	
	// ==================================================================================
	// Initialization
	// ==================================================================================
	
	public Generator(Composition templatePiece) {
		super(templatePiece);
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
	// Mutation
	// ==================================================================================
	
	public float melodyMutation = 0.5f;
	
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

}