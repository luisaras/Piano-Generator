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
		int sig = Scale.getSignature(composition.scale.root, composition.scale.mode);
		Score score = new Score();
		score.setTempo(composition.bpm);
		score.setKeySignature(composition.scale.mode);
		score.setKeyQuality(sig);
		score.setTimeSignature(composition.numerator, composition.denominator);
		
		System.out.println(composition.scale);
		System.out.println(composition.harmony);
		
		{ // Melody part
			Part part = new Part("Melody", 0, 0);
			part.setDynamic(100);
			part.addPhrase(toPhrase(composition.melody, composition.scale, 100));
			score.addPart(part);
		}
		
		{ // Harmony part
			Part part = new Part("Harmony", 0, 1);
			part.setDynamic(100);
			part.addPhraseList(toPhraseList(composition.harmony, composition));
			score.addPart(part);
		}
		Write.midi(score, fileName + ".mid");
	}
	
	// ==================================================================================
	// Melody Conversion
	// ==================================================================================
	
	private static Phrase toPhrase(Melody melody, Scale scale, int volume) {
		Phrase phrase = new Phrase(0);
		double lastTime = 0;
		for (NotePlay np : melody) {
			if (np.time > lastTime) {
				phrase.addRest(new Rest(np.time - lastTime));
			}
			Note note = new Note(np.note.getMIDIPitch(scale), np.duration);
			note.setDuration(np.duration - 0.0001);
			note.setDynamic(volume);
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
		ArrayList<Melody> lines = harmony.asMelodyLines(composition.scale);
		for (Melody melody : lines) {
			Phrase phrase = toPhrase(melody, composition.scale, 60);
			phrase.setAppend(false);
			phrases.add(phrase);
		}
		Phrase[] phraseArr = new Phrase[phrases.size()];
		return phrases.toArray(phraseArr);
	}
	
}
