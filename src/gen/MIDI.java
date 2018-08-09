package gen;

import gen.Melody.Note;

import java.util.ArrayList;

import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Rest;
import jm.music.data.Score;
import jm.util.Read;
import jm.util.Write;

public class MIDI {

	// ==================================================================================
	// Read MIDI
	// ==================================================================================
	
	public static Composition read(String fileName) {
		Score score = new Score();
		try {
			Read.midi(score, fileName);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		Composition composition = new Composition();
		composition.numerator = score.getNumerator();
		composition.denominator = score.getDenominator();
		composition.bpm = score.getTempo();
		
		int key = score.getKeySignature();
		int mode = score.getKeyQuality();
		
		composition.scale = new Scale(key, mode == 0 ? 0 : 5);

		{ // Melody notes
			Phrase phrase = score.getPart(0).getPhrase(0);
			composition.melody = toMelody(phrase, composition);
		}
		
		{ // Harmony notes
			Part part = score.getPart(1);
			composition.harmony = toHarmony(part, composition);
		}
		
		return composition;
	}
	
	// ==================================================================================
	// Write MIDI
	// ==================================================================================
	
	public static void write(String fileName, Composition composition) {
		Score score = new Score();
		score.setTempo(composition.bpm);
		score.setKeySignature(composition.scale.getSignature());
		score.setKeyQuality(composition.scale.mode == 0 ? 0 : 1);
		score.setTimeSignature(composition.numerator, composition.denominator);
		
		{ // Melody part
			Part part = new Part();
			part.setTitle("Melody");
			part.add(toPhrase(composition.melody, composition.scale));
			score.addPart(part);
		}
		
		{ // Harmony part
			Part part = new Part();
			part.setTitle("Harmony");
			part.addPhraseList(toPhraseList(composition.harmony, composition));
			score.addPart(part);
		}
		
		Write.midi(score, fileName);
	}
	
	// ==================================================================================
	// Melody Conversion
	// ==================================================================================
	
	private static Phrase toPhrase(Melody melody, Scale scale) {
		ArrayList<Note> notes = melody.notes;
		double duration = melody.duration;
		Phrase phrase = new Phrase();
		phrase.setStartTime(0);
		//phrase.setDuration(duration);
		phrase.setRhythmValue(1);
		for (int i = 0; i < notes.size(); i++) {
			double end = i < notes.size() - 1 ? notes.get(i + 1).time : duration;
			Note note = notes.get(i);
			if (note.pitch == null) {
				phrase.addRest(new Rest(end - note.time));
			} else {
				phrase.addNote(note.pitch.getMIDIPitch(scale), end - note.time);
				phrase.getNote(i).setDuration(end - note.time);
			}
		}
		return phrase;
	}
	
	private static Melody toMelody(Phrase phrase, Composition composition) {
		ArrayList<Note> notes = new ArrayList<>();
		for (int i = 0; i < phrase.size(); i++) {
			jm.music.data.Note note = phrase.getNote(i);
			double time = phrase.getNoteStartTime(i);
			if (note.isRest()) {
				notes.add(new Note(null, time));
			} else {
				Scale.Position pos = composition.scale.getPosition(note.getPitch());
				notes.add(new Note(pos, time));
			}
		}
		double duration = (phrase.getEndTime() - phrase.getStartTime());
		double songDuration = 4 * composition.numerator;
		if (duration < songDuration)
			notes.add(new Note(null, duration));
		return new Melody(notes, songDuration);
	}
	
	// ==================================================================================
	// Harmony Conversion
	// ==================================================================================
	
	private static ArrayList<Chord> toHarmony(Part part, Composition composition) {
		ArrayList<Chord> harmony = new ArrayList<>();
		ArrayList<Melody> lines = new ArrayList<>();
		for (Phrase phrase : part.getPhraseArray()) {
			lines.add(toMelody(phrase, composition));
		}
		for (int i = 0; i < 4; i++) {
			ArrayList<Melody> chordLines = new ArrayList<>();
			int start = i * composition.numerator;
			int end = (i + 1) * composition.numerator;
			for (Melody line : lines) {
				chordLines.add(line.subMelody(start, end));
			}
			harmony.add(new Chord(chordLines, composition.scale));
		}
		return harmony;
	}
	
	private static Phrase[] toPhraseList(ArrayList<Chord> harmony, Composition composition) {
		ArrayList<Melody> melodies = new ArrayList<>();
		for (int c = 0; c < harmony.size(); c++) {
			Chord chord = harmony.get(c);
			ArrayList<Melody> arpeggio = chord.asMelodyLines(composition.scale);
			for (int i = 0; i < arpeggio.size(); i++) {
				Melody melody = arpeggio.get(i);
				if (melodies.size() <= i) {
					melody.displace(c * composition.numerator);
					melodies.add(melody);
				} else {
					Melody melody0 = melodies.get(i);
					melody = melody0.concatenate(melody);
					melodies.set(i, melody);
				}
			}
			for (Melody melody : melodies) {
				melody.setDuration((c + 1) * composition.numerator);
			}
		}
		Phrase[] phrases = new Phrase[melodies.size()];
		for (int i = 0; i < melodies.size(); i++) {
			phrases[i] = toPhrase(melodies.get(i), composition.scale);
		}
		return phrases;
	}
	
}
