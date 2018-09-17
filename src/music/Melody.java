package music;

import java.util.ArrayList;
import java.util.Comparator;

public class Melody extends ArrayList<NotePlay> {

	private static final long serialVersionUID = 1L;
	public double duration;
	
	private Comparator<NotePlay> comparator = new Comparator<NotePlay>() {
		public int compare(NotePlay o1, NotePlay o2) {
			return (int) Math.signum(o1.time - o2.time);
		}
	};
	
	// ==================================================================================
	// Initialization
	// ==================================================================================
	
	public Melody(double duration) {
		this.duration = duration;
	}
	
	public Melody clone() {
		Melody notes = new Melody(this.duration);
		for (NotePlay n : this) {
			notes.add(n.clone());
		}
		return notes;
	}
	
	public void sort() {
		sort(comparator);
	}
	
	// ==================================================================================
	// Duration
	// ==================================================================================
	
	public void setDuration(int duration) {
		if (duration < this.duration) {
			for (int i = size() - 1; i >= 0; i--) {
				NotePlay note = get(i);
				if (note.time >= duration) {
					remove(i);
				} else {
					note.duration = Math.min(note.duration, duration - note.time);
				}
			}
		}
		this.duration = duration;
	}
	
	public void displace(double time) {
		if (time > 0) {
			for (NotePlay note : this) {
				note.time += time;
			}
			duration += time;
		}
	}
	
	// ==================================================================================
	// Cross-over
	// ==================================================================================
	
	public Melody cut(double start, double end) {
		Melody subMelody = new Melody(end - start);
		for(NotePlay note : this) {
			if (note.time >= start - 0.1) {
				if (note.time >= end) {
					break;
				}
				double time = note.time - start;
				double duration = Math.min(note.duration, end - time);
				subMelody.add(new NotePlay(note.note.clone(), time, duration));
			}
		}
		return subMelody;
	}
	
	public Melody concatenate(Melody other) {
		Melody notes = new Melody(duration + other.duration);
		for (NotePlay note : this) {
			notes.add(note.clone());
		}
		for (NotePlay note : other) {
			note = note.clone();
			note.time += duration;
			notes.add(note);
		}
		return notes;
	}
	
	// ==================================================================================
	// Debug
	// ==================================================================================
	
	public String toString() {
		String s = "";
		for (NotePlay n : this) {
			if (n.note != null)
				s += n.note.function + " ";
		}
		return s;
	}
	
	// ==================================================================================
	// Statistics
	// ==================================================================================
	
	public double[] getAttacks() {
		ArrayList<Double> attacks = new ArrayList<>();
		double lastTime = get(0).time;
		for (NotePlay np : this) {
			if (np.time > lastTime) {
				attacks.add(np.time - lastTime);
				lastTime = np.time;
			}
		}
		double[] array = new double[attacks.size()];
		for (int i = 0; i < array.length; i++)
			array[i] = attacks.get(i);
		return array;
	}
	
	public NotePlay[] getRests() {
		ArrayList<NotePlay> rests = new ArrayList<>();
		NotePlay first = get(0);
		if (first.time > 0)
			rests.add(new NotePlay(null, 0, first.time));
		double restStart = first.time + first.duration;
		for (NotePlay np : this) {
			double noteEnd = np.time + np.duration;
			if (noteEnd < restStart)
				continue;
			double noteStart = np.time;
			if (noteStart > restStart)
				rests.add(new NotePlay(null, restStart, noteStart - restStart));
			restStart = noteEnd;
		}
		return rests.toArray(new NotePlay[rests.size()]);
	}
	
	public int[] getPitches(Scale scale) {
		int[] pitches = new int[128];
		for(NotePlay np : this) {
			int pitch = np.note.getMIDIPitch(scale);
			if (pitch >= 0 && pitch <= 127)
				pitches[pitch]++;
		}
		return pitches;
	}
	
	public int[] getPitchClasses(Scale scale) {
		int[] pitches = new int[12];
		for(NotePlay np : this) {
			int pitch = np.note.getMIDIPitch(scale) % 12;
			if (pitch >= 0 && pitch <= 11)
				pitches[pitch]++;
		}
		return pitches;
	}
	
}
