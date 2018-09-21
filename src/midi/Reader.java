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
		composition.bpm = score.getTempo();
		composition.length = (int) (score.getEndTime() / score.getNumerator());
		
		int sig = score.getKeySignature();
		int mode = score.getKeyQuality() == 0 ? 0 : 5;
		int root = Scale.getRoot(sig, mode);
		
		composition.scale = new Scale(root, mode);
		composition.length = (int) score.getEndTime() / composition.numerator;

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
		Melody notes = new Melody(composition.length * composition.numerator);
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
		ArrayList<Melody> lines = new ArrayList<>();
		for (Phrase phrase : part.getPhraseArray()) {
			lines.add(toMelody(phrase, composition));
		}
		Harmony harmony = new Harmony();
		for (int i = 0; i < composition.length; i++) {
			ArrayList<Melody> chordLines = new ArrayList<>();
			int start = i * composition.numerator;
			int end = (i + 1) * composition.numerator;
			for (Melody line : lines) {
				chordLines.add(line.cut(start, end));
			}
			Chord chord = new Chord(chordLines, composition.scale);
			harmony.add(chord);
			if (harmony.arpeggio == null)
				harmony.arpeggio = new Arpeggio(chordLines, composition.scale,
						chord, composition.numerator);
		}
		return harmony;
	}
	
}
