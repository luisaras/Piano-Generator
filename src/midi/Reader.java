package midi;

import java.util.ArrayList;

import gen.Chord;
import gen.Composition;
import gen.Melody;
import gen.Scale;
import gen.Melody.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import jm.util.Read;

public class Reader {

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
		
		int sig = score.getKeySignature();
		int mode = score.getKeyQuality() == 0 ? 0 : 5;
		int root = Scale.getRoot(sig, mode);
		
		composition.scale = new Scale(root, mode, sig);

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
		ArrayList<Note> notes = new ArrayList<>();
		double start = phrase.getStartTime();
		if (start > 0)
			notes.add(new Note(null, 0));
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
		double songDuration = 4 * composition.numerator;
		if (phrase.getEndTime() < songDuration)
			notes.add(new Note(null, phrase.getEndTime()));
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
				chordLines.add(line.cut(start, end));
			}
			harmony.add(new Chord(chordLines, composition.scale));
		}
		return harmony;
	}
	
}
