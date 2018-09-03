package gen;

import java.util.Arrays;
import java.util.Random;

import music.Chord;
import music.Composition;
import music.Harmony;
import music.Melody;
import music.Note;
import music.NotePlay;
import music.Scale;

public class RandomGenerator {

	private Random rand = new Random(0);
	
	public double getDouble(double mean, double var) {
		// TODO
		return mean + (rand.nextDouble() - 0.5) * var;
	}
	
	public Composition generate(Composition template) {
		Composition composition = randomSignature(template);
		composition.melody = randomMelody(template.melody, template.scale);
		composition.harmony = randomHarmony(template.harmony, template.scale);
		return composition;
	}
	
	public Composition randomSignature(Composition template) {
		Composition composition = new Composition();
		composition.duration = template.duration;
		composition.numerator = template.numerator;
		composition.denominator = template.denominator;
		composition.bps = template.bps + (rand.nextDouble() - 0.5) * template.bps / 2;
		composition.scale = template.scale;
		return composition;
	}
	
	// ==================================================================================
	// Melody
	// ==================================================================================
	
	public Melody randomMelody(Melody template, Scale scale) {
		Melody notes = new Melody(template.duration);
		
		int noteCount = template.size();
		double[] attacks = new double[noteCount];
		for (int i = 0; i < noteCount; i++) {
			double t = rand.nextDouble() * template.duration;
			attacks[i] = Math.floor(t * 64) / 64;
		}
		Arrays.sort(attacks);
		
		Melody.Stats s = template.getStats(scale);
		s.functionVariation = Math.sqrt(s.functionVariation);
		s.octaveVariation = Math.sqrt(s.octaveVariation);
		s.accidentalVariation = Math.sqrt(s.accidentalVariation);
		for (int i = 0; i < noteCount; i++) {
			double fd = getDouble(s.functionMean, s.functionVariation);
			double od = getDouble(s.octaveMean, s.octaveVariation);
			double ad = getDouble(s.accidentalMean, s.accidentalVariation);
			int func = (int) (Math.round(fd) + 7) % 7;
			int oct = (int) Math.max(Math.round(od), 0);
			int acc = (int) Math.round(ad);
			Note note = new Note(func, acc, oct);
			double end = i < noteCount - 1 ? attacks[i + 1] : template.duration;
			double duration = getDouble(s.durationMean, s.durationVariation);
			notes.add(new NotePlay(note, attacks[i], Math.min(duration, end - attacks[i])));
		}
		
		return notes;
	}
	
	// ==================================================================================
	// Harmony
	// ==================================================================================
	
	public Harmony randomHarmony(Harmony template, Scale scale) {
		int max = 0, min = 100;
		for (Chord chord : template) {
			max = Math.max(max, chord.tonic.octaves);
			min = Math.min(min, chord.tonic.octaves);
		}
		Harmony harmony = new Harmony();
		for (Chord chord : template) {
			int oct = rand.nextInt(max + 1 - min) + min;
			Note tonic = new Note(rand.nextInt(7), 0, oct);
			harmony.add(new Chord(tonic, chord.arpeggio));
		}
		return harmony;
	}
	
}
