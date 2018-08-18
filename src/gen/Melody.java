package gen;

import java.util.ArrayList;

public class Melody {
	
	public static class Note {
		
		public Scale.Position pitch = null;
		public double time;
		
		public Note(Scale.Position p, double t) { 
			pitch = p; time = t;
		}
		
		public Note clone() {
			return new Note(pitch == null ? null : pitch.clone(), time);
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
	
	public Melody clone() {
		ArrayList<Note> notes = new ArrayList<>();
		for (Note n : this.notes) {
			notes.add(n.clone());
		}
		return new Melody(notes, this.duration);
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
			this.duration = duration;
		}
	}
	
	public void displace(int time) {
		if (time > 0) {
			for (Note note : notes) {
				note.time += time;
			}
			notes.add(0, new Note(null, 0));
			duration += time;
		}
	}
	
	public int noteCount() {
		int c = 0;
		for (Note note : notes) {
			if (note.pitch != null)
				c++;
		}
		return c;
	}
	
	// ==================================================================================
	// Cross-over
	// ==================================================================================
	
	public Melody cut(double start, double end) {
		ArrayList<Note> subMelody = new ArrayList<>();
		for(Note note : notes) {
			if (note.time >= start - 0.1) {
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
		notes.add(new Note(null, duration));
		for (Note note : other.notes) {
			notes.add(new Note(note.pitch, note.time + duration));
		}
		return new Melody(notes, duration + other.duration);
	}

}
