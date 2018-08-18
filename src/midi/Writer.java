package midi;

import gen.Harmony;
import gen.Composition;
import gen.Melody;
import gen.Scale;
import gen.Melody.Note;

import java.util.ArrayList;

import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Rest;
import jm.music.data.Score;
import jm.util.Write;

public class Writer {

	public static void write(String fileName, Composition composition) {
		Score score = new Score();
		score.setTempo(composition.bpm);
		score.setKeySignature(composition.scale.signature);
		score.setKeyQuality(composition.scale.mode == 0 ? 0 : 1);
		score.setTimeSignature(composition.numerator, composition.denominator);
		
		{ // Melody part
			Part part = new Part("Melody", 0, 0);
			part.addPhrase(toPhrase(composition.melody, composition.scale));
			score.addPart(part);
		}
		
		{ // Harmony part
			Part part = new Part("Harmony", 0, 1);
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
		Phrase phrase = new Phrase(0);
		for (int i = 0; i < notes.size(); i++) {
			double end = i < notes.size() - 1 ? notes.get(i + 1).time : duration;
			Note note = notes.get(i);
			if (note.pitch == null) {
				phrase.addRest(new Rest(end - note.time));
			} else {
				phrase.addNote(note.pitch.getMIDIPitch(scale), end - note.time);
				phrase.getNote(i).setDuration(end - note.time - 0.0001);
			}
		}
		return phrase;
	}
	
	// ==================================================================================
	// Harmony Conversion
	// ==================================================================================
	
	private static Phrase[] toPhraseList(Harmony harmony, Composition composition) {
		ArrayList<Phrase> phrases = new ArrayList<>();
		ArrayList<Melody> lines = harmony.asMelodyLines(composition.scale, composition.numerator);
		for (Melody melody : lines) {
			Phrase phrase = toPhrase(melody, composition.scale);
			phrase.setAppend(false);
			phrases.add(phrase);
		}
		Phrase[] phraseArr = new Phrase[phrases.size()];
		return phrases.toArray(phraseArr);
	}
	
}
