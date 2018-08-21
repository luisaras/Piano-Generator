package music;

public class NotePlay {
	
	public Note note = null;
	public double time;
	
	public NotePlay(Note note, double time) { 
		this.note = note; 
		this.time = time;
	}
	
	public NotePlay clone() {
		return new NotePlay(note == null ? null : note.clone(), time);
	}
	
	public String toString() {
		String n = note == null ? "0" : note.toString();
		return n + "(" + time + ")";
	}
	
}