package gen;

import java.util.ArrayList;

public class Melody {
	
	public static class Note {
		public Scale.Position pitch = null;
		public double time;
		public Note(Scale.Position p, double t) { 
			pitch = p; time = t;
		}
	}
	
	public ArrayList<Note> notes;
	public double duration;
	
	// ==================================================================================
	// Initialization
	// ==================================================================================
	
	public Melody(ArrayList<Note> notes, double duration) {
		this.notes = notes;
		this.duration = duration;
	}
	
	public String toString() {
		String s = "";
		for (Note n : notes) {
			if (n.pitch != null)
				s += n.pitch.function + " ";
		}
		return s;
	}
	
	// ==================================================================================
	// Duration
	// ==================================================================================
	
	public void setDuration(int duration) {
		if (duration > this.duration) {
			notes.add(new Note(null, this.duration));
			this.duration = duration;
		} else if (duration < this.duration) {
			for (int i = notes.size() - 1; i >= 0; i--) {
				if (notes.get(i).time >= duration) {
					notes.remove(i);
				} else {
					break;
				}
			}
		}
	}
	
	public void displace(int time) {
		if (time > 0) {
			for (Note note : notes) {
				note.time += time;
			}
			duration += time;
		}
	}
	
	// ==================================================================================
	// Cross-over
	// ==================================================================================
	
	public Melody subMelody(double start, double end) {
		ArrayList<Note> subMelody = new ArrayList<>();
		for(Note note : notes) {
			if (note.time >= start) {
				if (note.time >= end) {
					break;
				}
				subMelody.add(new Note(note.pitch, note.time - start));
			}
		}		
		return new Melody(subMelody, end - start);
	}
	
	public Melody concatenate(Melody other) {
		ArrayList<Note> notes = new ArrayList<>();
		for (Note note : this.notes) {
			notes.add(new Note(note.pitch, note.time));
		}
		if (other.notes.size() == 0) {
			notes.add(new Note(null, duration));
		} else {
			Note first = notes.get(0);
			if (first.time > 0) {
				notes.add(new Note(null, duration));
			}
			for (Note note : other.notes) {
				notes.add(new Note(note.pitch, note.time + duration));
			}
		}
		return new Melody(notes, duration + other.duration);
	}

}
