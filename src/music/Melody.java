package music;

import java.util.ArrayList;

public class Melody extends ArrayList<NotePlay> {

	private static final long serialVersionUID = 1L;
	
	public double duration;
	
	// ==================================================================================
	// Initialization
	// ==================================================================================
	
	public Melody(double duration) {
		this.duration = duration;
	}
	
	public String toString() {
		String s = "";
		for (NotePlay n : this) {
			if (n.note != null)
				s += n.note.function + " ";
		}
		return s;
	}
	
	public Melody clone() {
		Melody notes = new Melody(this.duration);
		for (NotePlay n : this) {
			notes.add(n.clone());
		}
		return notes;
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
	
	public void displace(int time) {
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
	// Mutation
	// ==================================================================================
	
	public void transpose(int src, int dest) {
		for (NotePlay np : this) {
			if (np.note.function == src)
				np.note.function = dest;
		}
	}
	
	// ==================================================================================
	// Statistics
	// ==================================================================================

	public static class Stats {
		
		public double pitchMean = 0;
		public double pitchVariation = 0;
		
		public double noteMean = 0;
		public double noteVariation = 0;
		
		public double functionMean = 0;
		public double functionVariation = 0;
		
		public double octaveMean = 0;
		public double octaveVariation = 0;
		
		public double accidentalMean = 0;
		public double accidentalVariation = 0;
		
		public double durationMean = 0;
		public double durationVariation = 0;
		
		public double attackMean = 0;
		public double attackVariation = 0;
		
	}
	
	public Stats getStats(Scale scale) {
		return getStats(scale, 0, duration);
	}
	
	public Stats getStats(Scale scale, double start, double end) {
		Stats s = new Stats();
		
		double lastAttack = -1;
		for (NotePlay np : this) {
			if (np.time >= end)
				break;
			if (np.time >= start) {
				s.pitchMean += np.note.getPitch(scale);
				s.noteMean += np.note.getPitch(scale) % 12;
				s.functionMean += np.note.function;
				s.octaveMean += np.note.octaves;
				s.accidentalMean += np.note.accidental;
				s.durationMean += np.duration;
				if (lastAttack >= 0) {
					s.attackMean += np.time - lastAttack;
				}
				lastAttack = np.time;
			}
		}
		s.pitchMean /= size();
		s.noteMean /= size();
		s.functionMean /= size();
		s.octaveMean /= size();
		s.accidentalMean /= size();
		s.durationMean /= size();
		s.attackMean /= size() - 1;
		
		lastAttack = -1;
		for (NotePlay np : this) {
			if (np.time >= end)
				break;
			if (np.time >= start) {
				double p = np.note.getPitch(scale) - s.pitchMean;
				double n = np.note.getPitch(scale) % 12 - s.noteMean;
				double f = np.note.function - s.functionMean;
				double o = np.note.octaves - s.octaveMean;
				double a = np.note.accidental - s.accidentalMean;
				double d = np.duration - s.durationMean;
				s.pitchVariation += p * p;
				s.noteVariation += n * n;
				s.functionVariation += f * f;
				s.octaveVariation += o * o;
				s.accidentalVariation += a * a;
				s.durationVariation += d * d;
				if (lastAttack >= 0) {
					double at = np.time - lastAttack - s.accidentalMean;
					s.attackVariation += at * at;
					lastAttack = np.time;
				}
			}
		}
		s.pitchVariation /= size();
		s.noteVariation /= size();
		s.functionVariation /= size();
		s.octaveVariation /= size();
		s.accidentalVariation /= size();
		s.durationVariation /= size();
		s.attackVariation /= size() - 1;
		
		return s;
	}
	
}
