package music;

public class NotePlay {
	
	public Note note = null;
	public double time;
	public double duration;
	
	public NotePlay(Note note, double time, double duration) { 
		this.note = note; 
		this.time = time;
		this.duration = duration;
	}
	
	public NotePlay clone() {
		return new NotePlay(note.clone(), time, duration);
	}
	
	public String toString() {
		return note.toString() + "(" + time + ")";
	}
	
}