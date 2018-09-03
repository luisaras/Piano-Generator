package midi;

import java.util.ArrayList;

import music.*;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import jm.util.Read;

public class Reader {

	public static Composition read(String fileName) {
		Score score = new Score();
		try {
			Read.midi(score, "templates/" + fileName + ".mid");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		Composition composition = new Composition();
		composition.name = fileName;
		composition.numerator = score.getNumerator();
		composition.denominator = score.getDenominator();
		composition.bps = score.getTempo() / 60;
		composition.duration = (int) (score.getEndTime() / score.getNumerator());
		
		int sig = score.getKeySignature();
		int mode = score.getKeyQuality() == 0 ? 0 : 5;
		int root = Scale.getRoot(sig, mode);
		
		composition.scale = new Scale(root, mode, sig);
		composition.duration = (int) score.getEndTime() / composition.numerator;

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
	// Melody Conversion
	// ==================================================================================
	
	private static Melody toMelody(Phrase phrase, Composition composition) {
		Melody notes = new Melody(composition.duration * composition.numerator);
		for (int i = 0; i < phrase.size(); i++) {
			jm.music.data.Note note = phrase.getNote(i);
			if (!note.isRest()) {
				Note pos = composition.scale.getPosition(note.getPitch());
				double time = phrase.getNoteStartTime(i);
				notes.add(new NotePlay(pos, time, note.getDuration()));
			}
		}
		return notes;
	}
	
	// ==================================================================================
	// Harmony Conversion
	// ==================================================================================
	
	private static Harmony toHarmony(Part part, Composition composition) {
		Harmony harmony = new Harmony();
		ArrayList<Melody> lines = new ArrayList<>();
		for (Phrase phrase : part.getPhraseArray()) {
			lines.add(toMelody(phrase, composition));
		}
		for (int i = 0; i < composition.duration; i++) {
			ArrayList<Melody> chordLines = new ArrayList<>();
			int start = i * composition.numerator;
			int end = (i + 1) * composition.numerator;
			for (Melody line : lines) {
				chordLines.add(line.cut(start, end));
			}
			harmony.add(new Chord(chordLines, composition.scale));
		}
		return harmony;
	}
	
}
