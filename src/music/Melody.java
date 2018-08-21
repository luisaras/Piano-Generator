package music;

import java.util.ArrayList;

public class Melody {
	
	public ArrayList<NotePlay> notes;
	public double duration;
	
	// ==================================================================================
	// Initialization
	// ==================================================================================
	
	public Melody(ArrayList<NotePlay> notes, double duration) {
		this.notes = notes;
		this.duration = duration;
	}
	
	public String toString() {
		String s = "";
		for (NotePlay n : notes) {
			if (n.note != null)
				s += n.note.function + " ";
		}
		return s;
	}
	
	public Melody clone() {
		ArrayList<NotePlay> notes = new ArrayList<>();
		for (NotePlay n : this.notes) {
			notes.add(n.clone());
		}
		return new Melody(notes, this.duration);
	}
	
	// ==================================================================================
	// Duration
	// ==================================================================================
	
	public void setDuration(int duration) {
		if (duration > this.duration) {
			notes.add(new NotePlay(null, this.duration));
			this.duration = duration;
		} else if (duration < this.duration) {
			for (int i = notes.size() - 1; i >= 0; i--) {
				if (notes.get(i).time >= duration) {
					notes.remove(i);
				} else {
					break;
				}
			}
			this.duration = duration;
		}
	}
	
	public void displace(int time) {
		if (time > 0) {
			for (NotePlay note : notes) {
				note.time += time;
			}
			notes.add(0, new NotePlay(null, 0));
			duration += time;
		}
	}
	
	public int noteCount() {
		int c = 0;
		for (NotePlay note : notes) {
			if (note.note != null)
				c++;
		}
		return c;
	}
	
	// ==================================================================================
	// Cross-over
	// ==================================================================================
	
	public Melody cut(double start, double end) {
		ArrayList<NotePlay> subMelody = new ArrayList<>();
		for(NotePlay note : notes) {
			if (note.time >= start - 0.1) {
				if (note.time >= end) {
					break;
				}
				subMelody.add(new NotePlay(note.note, note.time - start));
			}
		}
		return new Melody(subMelody, end - start);
	}
	
	public Melody concatenate(Melody other) {
		ArrayList<NotePlay> notes = new ArrayList<>();
		for (NotePlay note : this.notes) {
			notes.add(new NotePlay(note.note, note.time));
		}
		notes.add(new NotePlay(null, duration));
		for (NotePlay note : other.notes) {
			notes.add(new NotePlay(note.note, note.time + duration));
		}
		return new Melody(notes, duration + other.duration);
	}
	
	// ==================================================================================
	// Statistics
	// ==================================================================================

	public static class Stats {
		
		public double averageFunction = 0;
		public double averageOctave = 0;
		public double averageAccidental = 0;
		
		public double functionVariation = 0;
		public double octaveVariation = 0;
		public double accidentalVariation = 0;
		
	}
	
	public Stats getStats() {
		Stats s = new Stats();
		
		int noteCount = noteCount();
		for (NotePlay np : notes) {
			if (np.note != null) {
				s.averageFunction += np.note.function;
				s.averageOctave += np.note.octaves;
				s.averageAccidental += np.note.accidental;
			}
		}
		s.averageFunction /= noteCount;
		s.averageOctave /= noteCount;
		s.averageAccidental /= noteCount;
		
		for (NotePlay np : notes) {
			if (np.note != null) {
				double f = np.note.function - s.averageFunction;
				double o = np.note.octaves - s.averageOctave;
				double a = np.note.accidental - s.averageAccidental;
				s.functionVariation += f * f;
				s.octaveVariation += o * o;
				s.accidentalVariation += a * a;
			}
		}
		
		return s;
	}
	
}
