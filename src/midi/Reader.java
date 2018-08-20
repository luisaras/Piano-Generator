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
		System.out.println(composition.scale.toString());

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
		ArrayList<NotePlay> notes = new ArrayList<>();
		double start = phrase.getStartTime();
		if (start > 0)
			notes.add(new NotePlay(null, 0));
		for (int i = 0; i < phrase.size(); i++) {
			jm.music.data.Note note = phrase.getNote(i);
			double time = phrase.getNoteStartTime(i);
			if (note.isRest()) {
				notes.add(new NotePlay(null, time));
			} else {
				Note pos = composition.scale.getPosition(note.getPitch());
				notes.add(new NotePlay(pos, time));
			}
		}
		double songDuration = composition.duration * composition.numerator;
		if (phrase.getEndTime() < songDuration)
			notes.add(new NotePlay(null, phrase.getEndTime()));
		return new Melody(notes, songDuration);
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
			harmony.addChord(chordLines, composition.scale);
		}
		return harmony;
	}
	
}
