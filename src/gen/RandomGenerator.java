package gen;

import java.util.Arrays;
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

public class RandomGenerator {

	private Random rand = new Random(0);
	
	public double randomDouble(double mean, double var) {
		double v = (rand.nextDouble() - 0.5) * var;
		if (v < 0) {
			return mean - Math.sqrt(-v);
		} else {
			return mean + Math.sqrt(v);
		}
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
		composition.bpm = template.bpm + (rand.nextDouble() - 0.5) * template.bpm / 2;
		composition.scale = template.scale;
		return composition;
	}
	
	// ==================================================================================
	// Melody
	// ==================================================================================
	
	protected double[] randomAttacks(int size, double duration) {
		double[] attacks = new double[size];
		for (int i = 0; i < size; i++) {
			double t = rand.nextDouble() * duration;
			attacks[i] = Math.floor(t * 64) / 64;
		}
		Arrays.sort(attacks);
		return attacks;
	}
	
	public Melody randomMelody(Melody template, Scale scale) {
		Melody notes = new Melody(template.duration);
		
		int noteCount = template.size();
		double[] attacks = randomAttacks(noteCount, template.duration);
		
		Melody.Stats s = template.getStats(scale);
		for (int i = 0; i < noteCount; i++) {
			double fd = randomDouble(s.functionMean, s.functionVariation);
			double od = randomDouble(s.octaveMean, s.octaveVariation);
			double ad = randomDouble(s.accidentalMean, s.accidentalVariation);
			int func = (int) (Math.round(fd) + 7) % 7;
			int oct = (int) Math.max(Math.round(od), 0);
			int acc = (int) Math.round(ad);
			Note note = new Note(func, acc, oct);
			double end = i < noteCount - 1 ? attacks[i + 1] : template.duration;
			double duration = randomDouble(s.durationMean, s.durationVariation);
			duration = Math.min(duration, end - attacks[i]);
			NotePlay np = new NotePlay(note, attacks[i], duration);
			notes.add(np);
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
		for (int i = 0; i < template.size(); i++) {
			int oct = rand.nextInt(max + 1 - min) + min;
			Note tonic = new Note(rand.nextInt(7), 0, oct);
			harmony.add(new Chord(tonic));
		}
		Arpeggio arpeggio = new Arpeggio(template.arpeggio.duration);
		Arpeggio.Stats s = template.arpeggio.getStats(scale);
		double[] attacks = randomAttacks(template.arpeggio.size(), arpeggio.duration);
		for(int i = 0; i < attacks.length; i++) {
			double end = i < attacks.length - 1 ? attacks[i + 1] : arpeggio.duration;
			double duration = randomDouble(s.durationMean, s.durationVariation);
			duration = Math.min(duration, end - attacks[i]);
			ChordPlay cp = new ChordPlay(attacks[i], duration);
			int noteCount = (int) Math.round(randomDouble(s.verticalNoteMean - 1, s.verticalNoteVariation)) + 1;
			for(int n = 0; n < noteCount; n++) {
				int oct = (int) Math.round(randomDouble(s.octaveMean, s.octaveVariation));
				int acc = (int) Math.round(randomDouble(s.accidentalMean, s.accidentalVariation));
				cp.add(new Note(rand.nextInt(7), acc, oct));
			}
			arpeggio.add(cp);
		}
		harmony.arpeggio = arpeggio;
		return harmony;
	}
	
}
