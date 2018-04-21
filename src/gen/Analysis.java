package gen;

import java.util.ArrayList;

import jm.music.data.*;

public final class Analysis {
	
	private static class NotePlay {
		public Note note;
		public double start;
		public NotePlay(Note n, double s) { note = n; start = s; }
	}
	
	// ==================================================================================
	// Progression
	// ==================================================================================
	
	public static Progression deduceProgression(Part part, Scale scale, int chordLen) 
			throws Exception {
		// Find scale.
		if (scale == null) {
			scale = deduceScale(part);
			if (scale == null)
				throw new Exception("Could not deduce scale.");
		}
		// Collect all notes.
		double length = 0;
		ArrayList<NotePlay> allNotes = new ArrayList<>();
		for (Phrase phrase : part.getPhraseArray()) {
			Note[] notes = phrase.getNoteArray();
			for (int n = 0; n < notes.length; n++) {
				if (notes[n].getPitch() >= 0)
					allNotes.add(new NotePlay(notes[n], phrase.getNoteStartTime(n)));
			}
			length = Math.max(phrase.getEndTime(), length);
		}
		if (allNotes.size() == 0)
			throw new Exception("Part is empty.");
		// Deduce chords
		String chords = "";
		String chord = "?";
		for (int start = 0; start < length; start += chordLen) {
			String roman = deduceChord(allNotes, start, scale, chordLen);
			if (roman != null)
				chord = roman;
			chords += "-" + chord;
		}
		return new Progression(chords.substring(1), scale, chordLen);
	}
	
	// ==================================================================================
	// Chord
	// ==================================================================================
	
	private static String deduceChord(ArrayList<NotePlay> notes, double start, Scale scale, double chordLen) {
		// Find lowest pitch.
		int pitch = 999;
		for(NotePlay note : notes) {
			if (note.start - start < chordLen && note.start >= start) {
				pitch = Math.min(pitch, note.note.getPitch());
			}
		}
		if (pitch == 999)
			return null;
		// Find chord number.
		int i = scale.indexOf(pitch);
		if (i >= 0)
			return Chord.toRomanNumber(i);
		else
			return null;
	}
	
	// ==================================================================================
	// Scale
	// ==================================================================================
	
	public static Scale deduceScale(Part part) {
		for(int i = 0; i < Scale.patterns.length; i++) {
			Scale s = scaleOf(i, part);
			if (s != null)
				return s;
		}
		return null;
	}
	
	public static Scale scaleOf(int pattern, Part part) {
		int pitch = part.getLowestPitch();
		pitch = pitch - (pitch % 12);
		Scale scale = new Scale(pattern, pitch);
		for (int i = 0; i < 12; i++) {
			scale.setRoot(pitch + i);
			if (scale.includes(part)) {
				return scale;
			}
		}
		return null;
	}
 	
}
