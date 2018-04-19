package gen;

import java.util.ArrayList;
import jm.music.data.*;

public final class ProgressionAnalysis {
	
	private static class NotePlay {
		public Note note;
		public double start;
		public NotePlay(Note n, double s) { note = n; start = s; }
	}
	
	public static Progression deduceProgression(Score score) throws Exception {
		Scale scale = Scale.deduce(score);
		if (scale == null)
			throw new Exception("Could not deduce scale.");
		for (Part part : score.getPartArray()) {
			try {
				return deduceProgression(part, scale);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static Progression deduceProgression(Part part, Scale scale) throws Exception {
		// Collect all notes.
		double length = 0;
		ArrayList<NotePlay> allNotes = new ArrayList<>();
		for (Phrase phrase : part.getPhraseArray()) {
			Note[] notes = phrase.getNoteArray();
			for (int n = 0; n < notes.length; n++) {
				allNotes.add(new NotePlay(notes[n], phrase.getNoteStartTime(n)));
			}
			length = Math.max(phrase.getEndTime(), length);
		}
		if (allNotes.size() == 0)
			throw new Exception("Part is empty.");
		
		if (scale == null)
			throw new Exception("Could not deduce scale.");
		
		String chords = "";
		String chord = null;
		for (int start = 0; start < length; start ++) {
			// Collect compass notes
			String roman = deduceChord(allNotes, start, scale);
			if (roman != null)
				chord = roman;
			if (chord != null) {
				chords += "-" + chord;
			}
		}
		System.out.println(chords);
		return new Progression(chords.substring(1), scale.root, scale.pattern);
	}
	
	private static String deduceChord(ArrayList<NotePlay> notes, double start, Scale scale) {
		int pitch = 999;
		for(NotePlay note : notes) {
			if (note.start - start < 1 && note.start >= start) {
				pitch = Math.min(pitch, note.note.getPitch());
			}
		}
		if (pitch == 999)
			return null;
		int i = scale.indexOf(pitch);
		if (i >= 0)
			return Chord.toRomanNumber(i);
		else
			return null;
	}
 	
}
