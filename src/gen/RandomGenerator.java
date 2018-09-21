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
	
	public Composition generate(Composition template) {
		Composition composition = randomSignature(template);
		composition.melody = randomMelody(template.melody, template.scale);
		composition.harmony = randomHarmony(template.harmony, template.scale);
		return composition;
	}
	
	public Composition randomSignature(Composition template) {
		Composition composition = new Composition();
		composition.length = template.length;
		composition.numerator = template.numerator;
		composition.denominator = template.denominator;
		composition.bpm = template.bpm + (rand.nextDouble() - 0.5) * template.bpm;
		composition.scale = template.scale;
		return composition;
	}
	
	// ==================================================================================
	// Melody
	// ==================================================================================
	
	protected double[] randomAttacks(int size, double duration) {
		double[] attacks = new double[size];
		for (int i = 0; i < size; i++) {
			double t = rand.nextDouble() * (duration - 1 / NotePlay.minSize);
			attacks[i] = Math.floor(t * NotePlay.minSize + 1) / NotePlay.minSize;
		}
		Arrays.sort(attacks);
		return attacks;
	}
	
	public Melody randomMelody(Melody template, Scale scale) {
		Melody notes = new Melody(template.duration);
		int noteCount = template.size();
		int minPitch = 127;
		int maxPitch = 0;
		double minDuration = 100;
		double maxDuration = 0;
		for (NotePlay np : template) {
			int pitch = np.note.getMIDIPitch(scale);
			minPitch = Math.min(pitch, minPitch);
			maxPitch = Math.max(pitch + 1, maxPitch);
			minDuration = Math.min(np.duration, minDuration);
			maxDuration = Math.max(np.duration + 1, maxDuration);
		}
		double[] attacks = randomAttacks(noteCount, template.duration);
		for (int i = 0; i < noteCount; i++) {
			int pitch = rand.nextInt(maxPitch - minPitch) + minPitch;
			Note note = scale.getPosition(pitch);
			double end = i < noteCount - 1 ? attacks[i + 1] : template.duration;
			double duration = rand.nextDouble() * (maxDuration - minDuration) + minDuration;
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
		int minOctave = 100, maxOctave = 0;
		for (Chord chord : template) {
			minOctave = Math.min(minOctave, chord.tonic.octaves);
			maxOctave = Math.max(maxOctave + 1, chord.tonic.octaves);
		}
		Harmony harmony = new Harmony();
		for (int i = 0; i < template.size(); i++) {
			int oct = rand.nextInt(maxOctave - minOctave) + minOctave;
			Note tonic = new Note(rand.nextInt(7), 0, oct);
			harmony.add(new Chord(tonic));
		}
		Arpeggio arpeggio = new Arpeggio(template.arpeggio.duration);
		int minPitch = 127, maxPitch = 0;
		int minNoteCount = 100, maxNoteCount = 0;
		double minDuration = 100, maxDuration = 0;
		for (ChordPlay cp : template.arpeggio) {
			for (Note note : cp) {
				int pitch = note.getMIDIPitch(scale);
				minPitch = Math.min(pitch, minPitch);
				maxPitch = Math.max(pitch + 1, maxPitch);
			}
			minNoteCount = Math.min(cp.size(), minNoteCount);
			maxNoteCount = Math.max(cp.size() + 1, maxNoteCount);
			minDuration = Math.min(cp.duration, minDuration);
			maxDuration = Math.max(cp.duration + 1, maxDuration);
		}
		double[] attacks = randomAttacks(template.arpeggio.size(), arpeggio.duration);
		for(int i = 0; i < attacks.length; i++) {
			double end = i < attacks.length - 1 ? attacks[i + 1] : arpeggio.duration;
			double duration = rand.nextDouble() * (maxDuration - minDuration) + minDuration;
			duration = Math.min(duration, end - attacks[i]);
			ChordPlay cp = new ChordPlay(attacks[i], duration);
			int noteCount = rand.nextInt(maxNoteCount - minNoteCount) + minNoteCount;
			for(int n = 0; n < noteCount; n++) {
				int pitch = rand.nextInt(maxPitch - minPitch) + minPitch;
				cp.add(scale.getPosition(pitch));
			}
			arpeggio.add(cp);
		}
		harmony.arpeggio = arpeggio;
		return harmony;
	}
	
}
