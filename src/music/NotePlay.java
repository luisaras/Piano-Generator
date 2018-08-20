package music;

public class NotePlay {
	
	public Note note = null;
	public double time;
	
	public NotePlay(Note p, double t) { 
		note = p; time = t;
	}
	
	public NotePlay clone() {
		return new NotePlay(note == null ? null : note.clone(), time);
	}
	
	public String toString() {
		String n = note == null ? "0" : note.toString();
		return n + "(" + time + ")";
	}
	
}