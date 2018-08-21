package gen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import music.Composition;
import music.Melody;
import music.Note;
import music.NotePlay;

public class RandomGenerator {

	Random rand = new Random(0);
	
	public Composition generate(Composition template) {
		Composition composition = new Composition();
		
		composition.duration = template.duration;
		composition.numerator = template.numerator;
		composition.denominator = template.denominator;
		composition.bpm = template.bpm + (rand.nextDouble() - 0.5) * template.bpm / 2;
		composition.scale = template.scale;
		composition.melody = randomMelody(template.melody);
		
		{ // TODO: randomize harmony
			composition.harmony = template.harmony.clone();
		}
		
		return composition;
	}
	
	public Melody randomMelody(Melody template) {
		ArrayList<NotePlay> notes = new ArrayList<>();
		
		int noteCount = template.noteCount();
		double[] attacks = new double[noteCount];
		for (int i = 0; i < noteCount; i++) {
			attacks[i] = rand.nextDouble() * template.duration;
		}
		Arrays.sort(attacks);
		
		Melody.Stats s = template.getStats();
		s.functionVariation = Math.sqrt(s.functionVariation);
		s.octaveVariation = Math.sqrt(s.octaveVariation);
		s.accidentalVariation = Math.sqrt(s.accidentalVariation);
		for (int i = 0; i < noteCount; i++) {
			double fd = s.averageFunction + (rand.nextDouble() - 0.5) * s.functionVariation;
			double od = s.averageOctave + (rand.nextDouble() - 0.5) * s.octaveVariation;
			double ad = s.averageAccidental + (rand.nextDouble() - 0.5) * s.accidentalVariation;
			int func = (int) (Math.round(fd) + 7) % 7;
			int oct = (int) Math.max(Math.round(od), 0);
			int acc = (int) Math.round(ad);
			Note note = new Note(func, acc, oct);
			notes.add(new NotePlay(note, attacks[i]));
			if (rand.nextBoolean()) {
				double start = attacks[i];
				double maxEnd = i < noteCount - 1 ? attacks[i + 1] : template.duration;
				double end = rand.nextDouble() * (maxEnd - start) + start;
				notes.add(new NotePlay(null, end));
			}
		}
		
		return new Melody(notes, template.duration);
	}
	
}
