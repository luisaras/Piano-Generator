package midi;

import java.util.ArrayList;

import music.Composition;
import music.Harmony;
import music.Melody;
import music.NotePlay;
import music.Scale;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Rest;
import jm.music.data.Score;
import jm.util.Write;

public class Writer {

	public static void write(String fileName, Composition composition) {
		Score score = new Score();
		score.setTempo(composition.bps * 60);
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
		
		Write.midi(score, fileName + ".mid");
	}
	
	// ==================================================================================
	// Melody Conversion
	// ==================================================================================
	
	private static Phrase toPhrase(Melody melody, Scale scale) {
		Phrase phrase = new Phrase(0);
		double lastTime = 0;
		for (NotePlay np : melody) {
			if (np.time > lastTime) {
				phrase.addRest(new Rest(np.time - lastTime));
			}
			Note note = new Note(np.note.getMIDIPitch(scale), np.duration);
			note.setDuration(np.duration - 0.0001);
			phrase.add(note);
			lastTime = np.time + np.duration;
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
